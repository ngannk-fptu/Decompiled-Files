/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;

public final class JAXWSUtils {
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getFileOrURLName(String fileOrURL) {
        try {
            try {
                return JAXWSUtils.escapeSpace(new URL(fileOrURL).toExternalForm());
            }
            catch (MalformedURLException e) {
                return new File(fileOrURL).getCanonicalFile().toURL().toExternalForm();
            }
        }
        catch (Exception e) {
            return fileOrURL;
        }
    }

    public static URL getFileOrURL(String fileOrURL) throws IOException {
        try {
            URL url = new URL(fileOrURL);
            String scheme = String.valueOf(url.getProtocol()).toLowerCase();
            if (scheme.equals("http") || scheme.equals("https")) {
                return new URL(url.toURI().toASCIIString());
            }
            return url;
        }
        catch (URISyntaxException e) {
            return new File(fileOrURL).toURL();
        }
        catch (MalformedURLException e) {
            return new File(fileOrURL).toURL();
        }
    }

    public static URL getEncodedURL(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        String scheme = String.valueOf(url.getProtocol()).toLowerCase();
        if (scheme.equals("http") || scheme.equals("https")) {
            try {
                return new URL(url.toURI().toASCIIString());
            }
            catch (URISyntaxException e) {
                MalformedURLException malformedURLException = new MalformedURLException(e.getMessage());
                malformedURLException.initCause(e);
                throw malformedURLException;
            }
        }
        return url;
    }

    private static String escapeSpace(String url) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < url.length(); ++i) {
            if (url.charAt(i) == ' ') {
                buf.append("%20");
                continue;
            }
            buf.append(url.charAt(i));
        }
        return buf.toString();
    }

    public static String absolutize(String name) {
        try {
            URL baseURL = new File(".").getCanonicalFile().toURL();
            return new URL(baseURL, name).toExternalForm();
        }
        catch (IOException iOException) {
            return name;
        }
    }

    public static void checkAbsoluteness(String systemId) {
        try {
            new URL(systemId);
        }
        catch (MalformedURLException mue) {
            try {
                new URI(systemId);
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException("system ID '" + systemId + "' isn't absolute", e);
            }
        }
    }

    public static boolean matchQNames(QName target, QName pattern) {
        if (target == null || pattern == null) {
            return false;
        }
        if (pattern.getNamespaceURI().equals(target.getNamespaceURI())) {
            String regex = pattern.getLocalPart().replaceAll("\\*", ".*");
            return Pattern.matches(regex, target.getLocalPart());
        }
        return false;
    }
}

