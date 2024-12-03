/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.http.FastHttpDateFormat
 *  org.apache.tomcat.util.net.SSLSupport
 *  org.apache.tomcat.util.net.jsse.PEMFile
 *  org.apache.tomcat.util.net.openssl.ciphers.Cipher
 *  org.apache.tomcat.util.net.openssl.ciphers.EncryptionLevel
 *  org.apache.tomcat.util.net.openssl.ciphers.OpenSSLCipherConfigurationParser
 */
package org.apache.catalina.valves.rewrite;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Request;
import org.apache.catalina.valves.rewrite.Resolver;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.jsse.PEMFile;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.net.openssl.ciphers.EncryptionLevel;
import org.apache.tomcat.util.net.openssl.ciphers.OpenSSLCipherConfigurationParser;

public class ResolverImpl
extends Resolver {
    protected Request request = null;

    public ResolverImpl(Request request) {
        this.request = request;
    }

    @Override
    public String resolve(String key) {
        if (key.equals("HTTP_USER_AGENT")) {
            return this.request.getHeader("user-agent");
        }
        if (key.equals("HTTP_REFERER")) {
            return this.request.getHeader("referer");
        }
        if (key.equals("HTTP_COOKIE")) {
            return this.request.getHeader("cookie");
        }
        if (key.equals("HTTP_FORWARDED")) {
            return this.request.getHeader("forwarded");
        }
        if (key.equals("HTTP_HOST")) {
            return this.request.getServerName();
        }
        if (key.equals("HTTP_PROXY_CONNECTION")) {
            return this.request.getHeader("proxy-connection");
        }
        if (key.equals("HTTP_ACCEPT")) {
            return this.request.getHeader("accept");
        }
        if (key.equals("REMOTE_ADDR")) {
            return this.request.getRemoteAddr();
        }
        if (key.equals("REMOTE_HOST")) {
            return this.request.getRemoteHost();
        }
        if (key.equals("REMOTE_PORT")) {
            return String.valueOf(this.request.getRemotePort());
        }
        if (key.equals("REMOTE_USER")) {
            return this.request.getRemoteUser();
        }
        if (key.equals("REMOTE_IDENT")) {
            return this.request.getRemoteUser();
        }
        if (key.equals("REQUEST_METHOD")) {
            return this.request.getMethod();
        }
        if (key.equals("SCRIPT_FILENAME")) {
            return this.request.getServletContext().getRealPath(this.request.getServletPath());
        }
        if (key.equals("REQUEST_PATH")) {
            return this.request.getRequestPathMB().toString();
        }
        if (key.equals("CONTEXT_PATH")) {
            return this.request.getContextPath();
        }
        if (key.equals("SERVLET_PATH")) {
            return ResolverImpl.emptyStringIfNull(this.request.getServletPath());
        }
        if (key.equals("PATH_INFO")) {
            return ResolverImpl.emptyStringIfNull(this.request.getPathInfo());
        }
        if (key.equals("QUERY_STRING")) {
            return ResolverImpl.emptyStringIfNull(this.request.getQueryString());
        }
        if (key.equals("AUTH_TYPE")) {
            return this.request.getAuthType();
        }
        if (key.equals("DOCUMENT_ROOT")) {
            return this.request.getServletContext().getRealPath("/");
        }
        if (key.equals("SERVER_NAME")) {
            return this.request.getLocalName();
        }
        if (key.equals("SERVER_ADDR")) {
            return this.request.getLocalAddr();
        }
        if (key.equals("SERVER_PORT")) {
            return String.valueOf(this.request.getLocalPort());
        }
        if (key.equals("SERVER_PROTOCOL")) {
            return this.request.getProtocol();
        }
        if (key.equals("SERVER_SOFTWARE")) {
            return "tomcat";
        }
        if (key.equals("THE_REQUEST")) {
            return this.request.getMethod() + " " + this.request.getRequestURI() + " " + this.request.getProtocol();
        }
        if (key.equals("REQUEST_URI")) {
            return this.request.getRequestURI();
        }
        if (key.equals("REQUEST_FILENAME")) {
            return this.request.getPathTranslated();
        }
        if (key.equals("HTTPS")) {
            return this.request.isSecure() ? "on" : "off";
        }
        if (key.equals("TIME_YEAR")) {
            return String.valueOf(Calendar.getInstance().get(1));
        }
        if (key.equals("TIME_MON")) {
            return String.valueOf(Calendar.getInstance().get(2));
        }
        if (key.equals("TIME_DAY")) {
            return String.valueOf(Calendar.getInstance().get(5));
        }
        if (key.equals("TIME_HOUR")) {
            return String.valueOf(Calendar.getInstance().get(11));
        }
        if (key.equals("TIME_MIN")) {
            return String.valueOf(Calendar.getInstance().get(12));
        }
        if (key.equals("TIME_SEC")) {
            return String.valueOf(Calendar.getInstance().get(13));
        }
        if (key.equals("TIME_WDAY")) {
            return String.valueOf(Calendar.getInstance().get(7));
        }
        if (key.equals("TIME")) {
            return FastHttpDateFormat.getCurrentDate();
        }
        return null;
    }

    @Override
    public String resolveEnv(String key) {
        Object result = this.request.getAttribute(key);
        return result != null ? result.toString() : System.getProperty(key);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String resolveSsl(String key) {
        SSLSupport sslSupport = (SSLSupport)this.request.getAttribute("javax.servlet.request.ssl_session_mgr");
        try {
            if (key.equals("HTTPS")) {
                boolean bl;
                if (sslSupport != null) {
                    bl = true;
                    return String.valueOf(bl);
                }
                bl = false;
                return String.valueOf(bl);
            }
            if (key.equals("SSL_PROTOCOL")) {
                return sslSupport.getProtocol();
            }
            if (key.equals("SSL_SESSION_ID")) {
                return sslSupport.getSessionId();
            }
            if (key.equals("SSL_SESSION_RESUMED")) {
                return null;
            }
            if (key.equals("SSL_SECURE_RENEG")) {
                return null;
            }
            if (key.equals("SSL_COMPRESS_METHOD")) {
                return null;
            }
            if (key.equals("SSL_TLS_SNI")) {
                return null;
            }
            if (key.equals("SSL_CIPHER")) {
                return sslSupport.getCipherSuite();
            }
            if (key.equals("SSL_CIPHER_EXPORT")) {
                String cipherSuite = sslSupport.getCipherSuite();
                if (cipherSuite == null) return null;
                LinkedHashSet cipherList = OpenSSLCipherConfigurationParser.parse((String)cipherSuite);
                if (cipherList.size() != 1) return null;
                Cipher cipher = (Cipher)cipherList.iterator().next();
                if (cipher.getLevel().equals((Object)EncryptionLevel.EXP40)) return "true";
                if (!cipher.getLevel().equals((Object)EncryptionLevel.EXP56)) return "false";
                return "true";
            }
            if (key.equals("SSL_CIPHER_ALGKEYSIZE")) {
                String cipherSuite = sslSupport.getCipherSuite();
                if (cipherSuite == null) return null;
                LinkedHashSet cipherList = OpenSSLCipherConfigurationParser.parse((String)cipherSuite);
                if (cipherList.size() != 1) return null;
                Cipher cipher = (Cipher)cipherList.iterator().next();
                return String.valueOf(cipher.getAlg_bits());
            }
            if (key.equals("SSL_CIPHER_USEKEYSIZE")) {
                Integer keySize = sslSupport.getKeySize();
                if (keySize == null) {
                    return null;
                }
                String string = sslSupport.getKeySize().toString();
                return string;
            }
            if (key.startsWith("SSL_CLIENT_")) {
                X509Certificate[] certificates = sslSupport.getPeerCertificateChain();
                if (certificates == null) return null;
                if (certificates.length <= 0) return null;
                String result = this.resolveSslCertificates(key = key.substring("SSL_CLIENT_".length()), certificates);
                if (result != null) {
                    return result;
                }
                if (key.startsWith("SAN_OTHER_msUPN_")) {
                    key = key.substring("SAN_OTHER_msUPN_".length());
                    return null;
                }
                if (key.equals("CERT_RFC4523_CEA")) {
                    return null;
                }
            } else {
                if (!key.startsWith("SSL_SERVER_")) return null;
                X509Certificate[] certificates = sslSupport.getLocalCertificateChain();
                if (certificates == null) return null;
                if (certificates.length <= 0) return null;
                String result = this.resolveSslCertificates(key = key.substring("SSL_SERVER_".length()), certificates);
                if (result != null) {
                    return result;
                }
                if (!key.startsWith("SAN_OTHER_dnsSRV_")) return null;
                key = key.substring("SAN_OTHER_dnsSRV_".length());
                return null;
            }
            if (!key.equals("VERIFY")) return null;
            return null;
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    private String resolveSslCertificates(String key, X509Certificate[] certificates) {
        if (key.equals("M_VERSION")) {
            return String.valueOf(certificates[0].getVersion());
        }
        if (key.equals("M_SERIAL")) {
            return certificates[0].getSerialNumber().toString();
        }
        if (key.equals("S_DN")) {
            return certificates[0].getSubjectX500Principal().toString();
        }
        if (key.startsWith("S_DN_")) {
            key = key.substring("S_DN_".length());
            return this.resolveComponent(certificates[0].getSubjectX500Principal().getName(), key);
        }
        if (key.startsWith("SAN_Email_")) {
            key = key.substring("SAN_Email_".length());
            return this.resolveAlternateName(certificates[0], 1, Integer.parseInt(key));
        }
        if (key.startsWith("SAN_DNS_")) {
            key = key.substring("SAN_DNS_".length());
            return this.resolveAlternateName(certificates[0], 2, Integer.parseInt(key));
        }
        if (key.equals("I_DN")) {
            return certificates[0].getIssuerX500Principal().getName();
        }
        if (key.startsWith("I_DN_")) {
            key = key.substring("I_DN_".length());
            return this.resolveComponent(certificates[0].getIssuerX500Principal().toString(), key);
        }
        if (key.equals("V_START")) {
            return String.valueOf(certificates[0].getNotBefore().getTime());
        }
        if (key.equals("V_END")) {
            return String.valueOf(certificates[0].getNotAfter().getTime());
        }
        if (key.equals("V_REMAIN")) {
            long remain = certificates[0].getNotAfter().getTime() - System.currentTimeMillis();
            if (remain < 0L) {
                remain = 0L;
            }
            return String.valueOf(TimeUnit.MILLISECONDS.toDays(remain));
        }
        if (key.equals("A_SIG")) {
            return certificates[0].getSigAlgName();
        }
        if (key.equals("A_KEY")) {
            return certificates[0].getPublicKey().getAlgorithm();
        }
        if (key.equals("CERT")) {
            try {
                return PEMFile.toPEM((X509Certificate)certificates[0]);
            }
            catch (CertificateEncodingException certificateEncodingException) {
            }
        } else if (key.startsWith("CERT_CHAIN_")) {
            key = key.substring("CERT_CHAIN_".length());
            try {
                return PEMFile.toPEM((X509Certificate)certificates[Integer.parseInt(key)]);
            }
            catch (ArrayIndexOutOfBoundsException | NumberFormatException | CertificateEncodingException exception) {
                // empty catch block
            }
        }
        return null;
    }

    private String resolveComponent(String fullDN, String component) {
        HashMap<String, String> components = new HashMap<String, String>();
        StringTokenizer tokenizer = new StringTokenizer(fullDN, ",");
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken().trim();
            int pos = token.indexOf(61);
            if (pos <= 0 || pos + 1 >= token.length()) continue;
            components.put(token.substring(0, pos), token.substring(pos + 1));
        }
        return (String)components.get(component);
    }

    private String resolveAlternateName(X509Certificate certificate, int type, int n) {
        try {
            Collection<List<?>> alternateNames = certificate.getSubjectAlternativeNames();
            if (alternateNames != null) {
                ArrayList<String> elements = new ArrayList<String>();
                for (List<?> alternateName : alternateNames) {
                    Integer alternateNameType = (Integer)alternateName.get(0);
                    if (alternateNameType != type) continue;
                    elements.add(String.valueOf(alternateName.get(1)));
                }
                if (elements.size() > n) {
                    return (String)elements.get(n);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException | CertificateParsingException exception) {
            // empty catch block
        }
        return null;
    }

    @Override
    public String resolveHttp(String key) {
        String header = this.request.getHeader(key);
        if (header == null) {
            return "";
        }
        return header;
    }

    @Override
    public boolean resolveResource(int type, String name) {
        WebResourceRoot resources = this.request.getContext().getResources();
        WebResource resource = resources.getResource(name);
        if (!resource.exists()) {
            return false;
        }
        switch (type) {
            case 0: {
                return resource.isDirectory();
            }
            case 1: {
                return resource.isFile();
            }
            case 2: {
                return resource.isFile() && resource.getContentLength() > 0L;
            }
        }
        return false;
    }

    private static String emptyStringIfNull(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override
    public Charset getUriCharset() {
        return this.request.getConnector().getURICharset();
    }
}

