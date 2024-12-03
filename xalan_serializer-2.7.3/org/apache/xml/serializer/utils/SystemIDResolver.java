/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer.utils;

import java.io.File;
import javax.xml.transform.TransformerException;
import org.apache.xml.serializer.utils.URI;

public final class SystemIDResolver {
    public static String getAbsoluteURIFromRelative(String localPath) {
        if (localPath == null || localPath.length() == 0) {
            return "";
        }
        String absolutePath = localPath;
        if (!SystemIDResolver.isAbsolutePath(localPath)) {
            try {
                absolutePath = SystemIDResolver.getAbsolutePathFromRelativePath(localPath);
            }
            catch (SecurityException se) {
                return "file:" + localPath;
            }
        }
        String urlString = null != absolutePath ? (absolutePath.startsWith(File.separator) ? "file://" + absolutePath : "file:///" + absolutePath) : "file:" + localPath;
        return SystemIDResolver.replaceChars(urlString);
    }

    private static String getAbsolutePathFromRelativePath(String relativePath) {
        return new File(relativePath).getAbsolutePath();
    }

    public static boolean isAbsoluteURI(String systemId) {
        if (SystemIDResolver.isWindowsAbsolutePath(systemId)) {
            return false;
        }
        int fragmentIndex = systemId.indexOf(35);
        int queryIndex = systemId.indexOf(63);
        int slashIndex = systemId.indexOf(47);
        int colonIndex = systemId.indexOf(58);
        int index = systemId.length() - 1;
        if (fragmentIndex > 0) {
            index = fragmentIndex;
        }
        if (queryIndex > 0 && queryIndex < index) {
            index = queryIndex;
        }
        if (slashIndex > 0 && slashIndex < index) {
            index = slashIndex;
        }
        return colonIndex > 0 && colonIndex < index;
    }

    public static boolean isAbsolutePath(String systemId) {
        if (systemId == null) {
            return false;
        }
        File file = new File(systemId);
        return file.isAbsolute();
    }

    private static boolean isWindowsAbsolutePath(String systemId) {
        if (!SystemIDResolver.isAbsolutePath(systemId)) {
            return false;
        }
        return systemId.length() > 2 && systemId.charAt(1) == ':' && Character.isLetter(systemId.charAt(0)) && (systemId.charAt(2) == '\\' || systemId.charAt(2) == '/');
    }

    private static String replaceChars(String str) {
        StringBuffer buf = new StringBuffer(str);
        int length = buf.length();
        for (int i = 0; i < length; ++i) {
            char currentChar = buf.charAt(i);
            if (currentChar == ' ') {
                buf.setCharAt(i, '%');
                buf.insert(i + 1, "20");
                length += 2;
                i += 2;
                continue;
            }
            if (currentChar != '\\') continue;
            buf.setCharAt(i, '/');
        }
        return buf.toString();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static String getAbsoluteURI(String systemId) {
        int secondColonIndex;
        String absoluteURI = systemId;
        if (!SystemIDResolver.isAbsoluteURI(systemId)) return SystemIDResolver.getAbsoluteURIFromRelative(systemId);
        if (!systemId.startsWith("file:")) return systemId;
        String str = systemId.substring(5);
        if (str == null || !str.startsWith("/")) return SystemIDResolver.getAbsoluteURIFromRelative(systemId.substring(5));
        if (!str.startsWith("///") && str.startsWith("//") || (secondColonIndex = systemId.indexOf(58, 5)) <= 0) return SystemIDResolver.replaceChars(absoluteURI);
        String localPath = systemId.substring(secondColonIndex - 1);
        try {
            if (SystemIDResolver.isAbsolutePath(localPath)) return SystemIDResolver.replaceChars(absoluteURI);
            absoluteURI = systemId.substring(0, secondColonIndex - 1) + SystemIDResolver.getAbsolutePathFromRelativePath(localPath);
            return SystemIDResolver.replaceChars(absoluteURI);
        }
        catch (SecurityException se) {
            return systemId;
        }
    }

    public static String getAbsoluteURI(String urlString, String base) throws TransformerException {
        if (base == null) {
            return SystemIDResolver.getAbsoluteURI(urlString);
        }
        String absoluteBase = SystemIDResolver.getAbsoluteURI(base);
        URI uri = null;
        try {
            URI baseURI = new URI(absoluteBase);
            uri = new URI(baseURI, urlString);
        }
        catch (URI.MalformedURIException mue) {
            throw new TransformerException(mue);
        }
        return SystemIDResolver.replaceChars(uri.toString());
    }
}

