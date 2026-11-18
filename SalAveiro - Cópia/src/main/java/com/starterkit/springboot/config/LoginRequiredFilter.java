package com.starterkit.springboot.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class LoginRequiredFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        // A API SalAveiro usa a autenticação por chave do Spring Security.
        if (httpRequest.getServletPath().startsWith("/api/salaveiro/")) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpSession session = httpRequest.getSession(false);
        Boolean authenticated = (session != null) ? (Boolean) session.getAttribute("authenticated") : null;
        
        // Rotas públicas que não requerem autenticação
        boolean isPublicRoute = requestURI.equals("/login") 
                || requestURI.equals("/logout")
                || requestURI.equals("/")
                || requestURI.equals("/presencas")
                || requestURI.equals("/api/presencas-api")
                || requestURI.equals("/api/presencas")
                || requestURI.equals("/api/check-in")
                || requestURI.startsWith("/css")
                || requestURI.startsWith("/js")
                || requestURI.startsWith("/img")
                || requestURI.startsWith("/uploads");
        
        // Se não está autenticado e não é rota pública, redirecionar para login
        if ((authenticated == null || !authenticated) && !isPublicRoute) {
            // Permitir acesso à página de login
            if (requestURI.equals("/login")) {
                chain.doFilter(request, response);
                return;
            }
            httpResponse.sendRedirect("/login");
            return;
        }
        
        String path = httpRequest.getServletPath();
        if ((path.equals("/utilizadores") || path.startsWith("/utilizadores/"))
                && (session == null || !"ADMIN".equals(session.getAttribute("role")))) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Verificar se é uma rota de phones (requer ADMIN)
        if (requestURI.startsWith("/phones")) {
            String role = (session != null) ? (String) session.getAttribute("role") : null;
            
            // Se não é ADMIN, redirecionar para login
            if (!"ADMIN".equals(role)) {
                httpResponse.sendRedirect("/login");
                return;
            }
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
