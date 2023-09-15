package cn.zeniein.stardrive.support.jwt;


import cn.zeniein.stardrive.common.ResponseData;
import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.utils.NetworkUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;


/**
 * 验证JWT
 * <p>
 * 验证通过将信息放入上下文中
 * <p>
 * 验证不通过返回401
 */
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_AUTHORIZATION = "Bearer ";

    private static String[] excluded;

    public JwtFilter(String... excludedUris) {
        excluded = excludedUris;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientIp = NetworkUtils.getClientIp(request);
        log.info("request:{} {} from {}", request.getMethod(), request.getRequestURI(), clientIp);
        for (String excludedUri : excluded) {
            excludedUri = excludedUri.replace("/*", "");
            if (request.getRequestURI().startsWith(excludedUri)) {
                doFilter(request, response, filterChain);
                return;
            }
        }
        // 对options方法放行
        String method = request.getMethod();
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(method)) {

            doFilter(request, response, filterChain);
            return;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith(BEARER_AUTHORIZATION)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ResponseData<Object> responseData = ResponseData.error(ResponseEnum.ERROR.getStatus(), "用户未授权");
            String jsonResponse = new ObjectMapper().writeValueAsString(responseData);
            response.getWriter().println(jsonResponse);
            log.info("unauthorized:{} {}, from {}", method, request.getRequestURI(), clientIp);
            return;
        }
        String token = authorization.substring(7);
        try {
            JWTClaimsSet jwtClaimsSet = JwtUtils.verify(token);
            String userId = jwtClaimsSet.getStringClaim("uid");
            String role = jwtClaimsSet.getStringClaim("role");
            SecurityContextHolder.setContext(new Authentication(userId, role));
        } catch (ParseException | JOSEException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ResponseData<Object> responseData = ResponseData.error(ResponseEnum.ERROR.getStatus(), "登录过期！");
            String jsonResponse = new ObjectMapper().writeValueAsString(responseData);
            response.getWriter().println(jsonResponse);
            return;
        }

        doFilter(request, response, filterChain);
    }

}
