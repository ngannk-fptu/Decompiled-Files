/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.launch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.StringCharacterIterator;
import java.util.Locale;
import java.util.stream.Stream;

public final class Locator {
    private static final int NIBBLE = 4;
    private static final int NIBBLE_MASK = 15;
    private static final int ASCII_SIZE = 128;
    private static final int BYTE_SIZE = 256;
    private static final int WORD = 16;
    private static final int SPACE = 32;
    private static final int DEL = 127;
    private static boolean[] gNeedEscaping;
    private static char[] gAfterEscaping1;
    private static char[] gAfterEscaping2;
    private static char[] gHexChs;
    public static final String ERROR_NOT_FILE_URI = "Can only handle valid file: URIs, not ";

    public static File getClassSource(Class<?> c) {
        String classResource = c.getName().replace('.', '/') + ".class";
        return Locator.getResourceSource(c.getClassLoader(), classResource);
    }

    public static File getResourceSource(ClassLoader c, String resource) {
        URL url;
        if (c == null) {
            c = Locator.class.getClassLoader();
        }
        if ((url = c == null ? ClassLoader.getSystemResource(resource) : c.getResource(resource)) != null) {
            String u = url.toString();
            try {
                if (u.startsWith("jar:file:")) {
                    return new File(Locator.fromJarURI(u));
                }
                if (u.startsWith("file:")) {
                    int tail = u.indexOf(resource);
                    String dirName = u.substring(0, tail);
                    return new File(Locator.fromURI(dirName));
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return null;
    }

    public static String fromURI(String uri) {
        String file;
        int queryPos;
        URL url = null;
        try {
            url = new URL(uri);
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
        if (url == null || !"file".equals(url.getProtocol())) {
            throw new IllegalArgumentException(ERROR_NOT_FILE_URI + uri);
        }
        StringBuilder buf = new StringBuilder(url.getHost());
        if (buf.length() > 0) {
            buf.insert(0, File.separatorChar).insert(0, File.separatorChar);
        }
        buf.append((queryPos = (file = url.getFile()).indexOf(63)) < 0 ? file : file.substring(0, queryPos));
        uri = buf.toString().replace('/', File.separatorChar);
        if (File.pathSeparatorChar == ';' && uri.startsWith("\\") && uri.length() > 2 && Character.isLetter(uri.charAt(1)) && uri.lastIndexOf(58) > -1) {
            uri = uri.substring(1);
        }
        String path = null;
        try {
            path = Locator.decodeUri(uri);
            String cwd = System.getProperty("user.dir");
            int posi = cwd.indexOf(58);
            boolean pathStartsWithFileSeparator = path.startsWith(File.separator);
            boolean pathStartsWithUNC = path.startsWith("" + File.separator + File.separator);
            if (posi > 0 && pathStartsWithFileSeparator && !pathStartsWithUNC) {
                path = cwd.substring(0, posi + 1) + path;
            }
        }
        catch (UnsupportedEncodingException exc) {
            throw new IllegalStateException("Could not convert URI " + uri + " to path: " + exc.getMessage());
        }
        return path;
    }

    public static String fromJarURI(String uri) {
        int pling = uri.indexOf("!/");
        String jarName = uri.substring("jar:".length(), pling);
        return Locator.fromURI(jarName);
    }

    public static String decodeUri(String uri) throws UnsupportedEncodingException {
        if (!uri.contains("%")) {
            return uri;
        }
        ByteArrayOutputStream sb = new ByteArrayOutputStream(uri.length());
        StringCharacterIterator iter = new StringCharacterIterator(uri);
        char c = iter.first();
        while (c != '\uffff') {
            if (c == '%') {
                char c1 = iter.next();
                if (c1 != '\uffff') {
                    int i1 = Character.digit(c1, 16);
                    char c2 = iter.next();
                    if (c2 != '\uffff') {
                        int i2 = Character.digit(c2, 16);
                        sb.write((char)((i1 << 4) + i2));
                    }
                }
            } else if (c >= '\u0000' && c < '\u0080') {
                sb.write(c);
            } else {
                byte[] bytes = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
                sb.write(bytes, 0, bytes.length);
            }
            c = iter.next();
        }
        return sb.toString(StandardCharsets.UTF_8.name());
    }

    public static String encodeURI(String path) {
        int i;
        int len = path.length();
        int ch = 0;
        StringBuilder sb = null;
        for (i = 0; i < len && (ch = path.charAt(i)) < 128; ++i) {
            if (gNeedEscaping[ch]) {
                if (sb == null) {
                    sb = new StringBuilder(path.substring(0, i));
                }
                sb.append('%');
                sb.append(gAfterEscaping1[ch]);
                sb.append(gAfterEscaping2[ch]);
                continue;
            }
            if (sb == null) continue;
            sb.append((char)ch);
        }
        if (i < len) {
            if (sb == null) {
                sb = new StringBuilder(path.substring(0, i));
            }
            for (byte b : path.substring(i).getBytes(StandardCharsets.UTF_8)) {
                if (b < 0) {
                    ch = b + 256;
                    sb.append('%');
                    sb.append(gHexChs[ch >> 4]);
                    sb.append(gHexChs[ch & 0xF]);
                    continue;
                }
                if (gNeedEscaping[b]) {
                    sb.append('%');
                    sb.append(gAfterEscaping1[b]);
                    sb.append(gAfterEscaping2[b]);
                    continue;
                }
                sb.append((char)b);
            }
        }
        return sb == null ? path : sb.toString();
    }

    @Deprecated
    public static URL fileToURL(File file) throws MalformedURLException {
        return new URL(file.toURI().toASCIIString());
    }

    public static File getToolsJar() {
        boolean toolsJarAvailable = false;
        try {
            Class.forName("com.sun.tools.javac.Main");
            toolsJarAvailable = true;
        }
        catch (Exception e) {
            try {
                Class.forName("sun.tools.javac.Main");
                toolsJarAvailable = true;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (toolsJarAvailable) {
            return null;
        }
        String libToolsJar = File.separator + "lib" + File.separator + "tools.jar";
        String javaHome = System.getProperty("java.home");
        File toolsJar = new File(javaHome + libToolsJar);
        if (toolsJar.exists()) {
            return toolsJar;
        }
        if (javaHome.toLowerCase(Locale.ENGLISH).endsWith(File.separator + "jre")) {
            javaHome = javaHome.substring(0, javaHome.length() - "/jre".length());
            toolsJar = new File(javaHome + libToolsJar);
        }
        if (!toolsJar.exists()) {
            return null;
        }
        return toolsJar;
    }

    public static URL[] getLocationURLs(File location) throws MalformedURLException {
        return Locator.getLocationURLs(location, ".jar");
    }

    public static URL[] getLocationURLs(File location, String ... extensions) throws MalformedURLException {
        URL[] urls = new URL[]{};
        if (!location.exists()) {
            return urls;
        }
        if (!location.isDirectory()) {
            urls = new URL[1];
            String path = location.getPath();
            String littlePath = path.toLowerCase(Locale.ENGLISH);
            for (String extension : extensions) {
                if (!littlePath.endsWith(extension)) continue;
                urls[0] = Locator.fileToURL(location);
                break;
            }
            return urls;
        }
        File[] matches = location.listFiles((dir, name) -> {
            String littleName = name.toLowerCase(Locale.ENGLISH);
            return Stream.of(extensions).anyMatch(littleName::endsWith);
        });
        urls = new URL[matches.length];
        for (int i = 0; i < matches.length; ++i) {
            urls[i] = Locator.fileToURL(matches[i]);
        }
        return urls;
    }

    private Locator() {
    }

    static {
        char[] escChs;
        gNeedEscaping = new boolean[128];
        gAfterEscaping1 = new char[128];
        gAfterEscaping2 = new char[128];
        gHexChs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0; i < 32; ++i) {
            Locator.gNeedEscaping[i] = true;
            Locator.gAfterEscaping1[i] = gHexChs[i >> 4];
            Locator.gAfterEscaping2[i] = gHexChs[i & 0xF];
        }
        Locator.gNeedEscaping[127] = true;
        Locator.gAfterEscaping1[127] = 55;
        Locator.gAfterEscaping2[127] = 70;
        for (char ch : escChs = new char[]{' ', '<', '>', '#', '%', '\"', '{', '}', '|', '\\', '^', '~', '[', ']', '`'}) {
            Locator.gNeedEscaping[ch] = true;
            Locator.gAfterEscaping1[ch] = gHexChs[ch >> 4];
            Locator.gAfterEscaping2[ch] = gHexChs[ch & 0xF];
        }
    }
}

