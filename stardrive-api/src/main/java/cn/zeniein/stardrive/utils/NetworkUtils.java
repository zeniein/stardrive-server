package cn.zeniein.stardrive.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtils {
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String UNKNOWN = "unknown";

    public static String getClientIp(HttpServletRequest request) {
        String clientIpAddress = request.getHeader("X-Forwarded-For");
        if((clientIpAddress == null || "".equals(clientIpAddress)) || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("Proxy-Client-IP");
        }

        if((clientIpAddress == null || "".equals(clientIpAddress)) || UNKNOWN.equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if((clientIpAddress == null || "".equals(clientIpAddress)) || UNKNOWN.equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getRemoteAddr();
            if(LOCALHOST_IPV4.equals(clientIpAddress) || LOCALHOST_IPV6.equals(clientIpAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    clientIpAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        if(!(clientIpAddress == null || "".equals(clientIpAddress))
                && clientIpAddress.length() > 15
                && clientIpAddress.indexOf(",") > 0) {
            clientIpAddress = clientIpAddress.substring(0, clientIpAddress.indexOf(","));
        }

        return clientIpAddress;
    }

}
