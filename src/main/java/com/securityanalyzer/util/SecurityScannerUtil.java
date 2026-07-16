package com.securityanalyzer.util;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

public class SecurityScannerUtil {

    private SecurityScannerUtil() {
        // Utility class private constructor
    }

    /**
     * Sanitizes and validates a target URL.
     * Prevents Server-Side Request Forgery (SSRF) by blocking internal loopback and private site-local IP scopes.
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Strip leading/trailing whitespaces and illegal control chars
            String sanitized = url.trim().replaceAll("[\\r\\n]", "");
            URI uri = new URI(sanitized);
            String scheme = uri.getScheme();
            
            // Only allow standard http / https schemes
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                return false;
            }
            
            String host = uri.getHost();
            if (host == null || host.trim().isEmpty()) {
                return false;
            }

            // SSRF Scope Resolution Checks
            try {
                InetAddress addr = InetAddress.getByName(host);
                if (addr.isLoopbackAddress() || 
                    addr.isAnyLocalAddress() || 
                    addr.isLinkLocalAddress() || 
                    addr.isSiteLocalAddress() || 
                    addr.isMulticastAddress()) {
                    return false;
                }
            } catch (UnknownHostException e) {
                // If offline / DNS lookup fails, statically block common loopback and private IPv4 prefixes
                String hostLower = host.toLowerCase().trim();
                if (hostLower.equals("localhost") || 
                    hostLower.equals("127.0.0.1") || 
                    hostLower.equals("[::1]") || 
                    hostLower.startsWith("192.168.") || 
                    hostLower.startsWith("10.") || 
                    hostLower.startsWith("172.16.") || 
                    hostLower.startsWith("172.17.") || 
                    hostLower.startsWith("172.18.") || 
                    hostLower.startsWith("172.19.") || 
                    hostLower.startsWith("172.20.") || 
                    hostLower.startsWith("172.21.") || 
                    hostLower.startsWith("172.22.") || 
                    hostLower.startsWith("172.23.") || 
                    hostLower.startsWith("172.24.") || 
                    hostLower.startsWith("172.25.") || 
                    hostLower.startsWith("172.26.") || 
                    hostLower.startsWith("172.27.") || 
                    hostLower.startsWith("172.28.") || 
                    hostLower.startsWith("172.29.") || 
                    hostLower.startsWith("172.30.") || 
                    hostLower.startsWith("172.31.")) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
