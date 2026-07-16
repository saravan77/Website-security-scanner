package com.securityanalyzer.service.scanner;

import com.securityanalyzer.entity.SSLInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class SslInspector {

    private static final Logger log = LoggerFactory.getLogger(SslInspector.class);

    public SSLInfo inspect(String host, int port, int timeoutMs) {
        log.info("Inspecting SSL certificate for host: {}, port: {}", host, port);
        try {
            // Configure a trust-all trust manager so handshakes succeed even with invalid/expired certs.
            // This allows us to inspect the cert details.
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory factory = sslContext.getSocketFactory();

            try (SSLSocket socket = (SSLSocket) factory.createSocket()) {
                socket.connect(new InetSocketAddress(host, port), timeoutMs);
                socket.setSoTimeout(timeoutMs);
                socket.startHandshake();

                SSLSession session = socket.getSession();
                java.security.cert.Certificate[] certs = session.getPeerCertificates();

                if (certs.length > 0 && certs[0] instanceof X509Certificate) {
                    X509Certificate cert = (X509Certificate) certs[0];
                    
                    String protocol = session.getProtocol();
                    String cipherSuite = session.getCipherSuite();
                    String issuer = cert.getIssuerX500Principal().getName();
                    
                    LocalDateTime validFrom = convertToLocalDateTime(cert.getNotBefore());
                    LocalDateTime validTo = convertToLocalDateTime(cert.getNotAfter());
                    boolean isExpired = new Date().after(cert.getNotAfter());

                    log.debug("SSL details successfully inspected: Protocol={}, Cipher={}, Issuer={}", 
                            protocol, cipherSuite, issuer);

                    return SSLInfo.builder()
                            .isSslEnabled(true)
                            .protocol(protocol)
                            .cipherSuite(cipherSuite)
                            .issuer(issuer)
                            .validFrom(validFrom)
                            .validTo(validTo)
                            .isExpired(isExpired)
                            .build();
                }
            }
        } catch (Exception e) {
            log.warn("SSL certificate inspection failed for {}:{}. Error: {}", host, port, e.getMessage());
        }

        return SSLInfo.builder()
                .isSslEnabled(false)
                .build();
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
