package com.starterkit.springboot.config;

import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class NtpTimeService {

    private static NtpTimeService instance;
    private Date cachedDate;
    private long cacheTime = 0;
    private static final long CACHE_DURATION = TimeUnit.HOURS.toMillis(1);

    public NtpTimeService() {
        instance = this;
    }

    private static final String[] NTP_SERVERS = {
        "pool.ntp.org",
        "time.google.com",
        "time.windows.com"
    };

    public Date getCurrentDate() {
        long now = System.currentTimeMillis();
        if (cachedDate != null && (now - cacheTime) < CACHE_DURATION) {
            return cachedDate;
        }
        
        try {
            for (String server : NTP_SERVERS) {
                try {
                    InetAddress addr = InetAddress.getByName(server);
                    java.net.DatagramSocket socket = new java.net.DatagramSocket();
                    socket.setSoTimeout(3000);
                    
                    byte[] buffer = new byte[48];
                    buffer[0] = 0x1B; // LI, Version, Mode
                    
                    java.net.DatagramPacket packet = new java.net.DatagramPacket(buffer, buffer.length, addr, 123);
                    socket.send(packet);
                    socket.receive(packet);
                    socket.close();
                    
                    long transmitTimestamp = decodeNtpTime(buffer, 40);
                    long serverTime = (transmitTimestamp - 2208988800000L);
                    cachedDate = new Date(serverTime);
                    cacheTime = System.currentTimeMillis();
                    return cachedDate;
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            // Fallback to system time
        }
        if (cachedDate == null) {
            cachedDate = new Date();
            cacheTime = System.currentTimeMillis();
        }
        return cachedDate;
    }

    public static Date getCurrentDateStatic() {
        if (instance != null) {
            return instance.getCurrentDate();
        }
        return new Date();
    }

    private long decodeNtpTime(byte[] buffer, int offset) {
        long integerPart = ((buffer[offset] & 0xFF) << 24) |
                          ((buffer[offset + 1] & 0xFF) << 16) |
                          ((buffer[offset + 2] & 0xFF) << 8) |
                          (buffer[offset + 3] & 0xFF);
        long fractionalPart = ((buffer[offset + 4] & 0xFF) << 24) |
                              ((buffer[offset + 5] & 0xFF) << 16) |
                              ((buffer[offset + 6] & 0xFF) << 8) |
                              (buffer[offset + 7] & 0xFF);
        return (integerPart * 1000) + ((fractionalPart * 1000) / 0x100000000L);
    }
}