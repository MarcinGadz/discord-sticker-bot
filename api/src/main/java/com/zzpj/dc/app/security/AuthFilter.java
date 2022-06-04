package com.zzpj.dc.app.security;

import com.zzpj.dc.app.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class AuthFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthFilter.class.getName());
    private static final String API_KEY_HEADER = "x-api-key";
    private static final String TRACE_HEADER = "x-trace-id";

    @Autowired
    private AppService appService;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var requestId = UUID.randomUUID().toString();

        LOGGER.info("processing request: " + requestId);
        var req = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;
        String apiKey = req.getHeader(API_KEY_HEADER);
        if (apiKey == null || apiKey.isBlank()) {
            LOGGER.info(requestId + " Got invalid headers");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        LOGGER.info(requestId + " Headers are present");
        if (appService.verifyKey(apiKey)) {
            LOGGER.info((requestId + " Authenticated successfully"));
            req.setAttribute(TRACE_HEADER, requestId);
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            LOGGER.info((requestId + " Wrong api key"));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
