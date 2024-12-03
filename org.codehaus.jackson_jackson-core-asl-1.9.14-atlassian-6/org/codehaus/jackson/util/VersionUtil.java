/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import org.codehaus.jackson.Version;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class VersionUtil {
    public static final String VERSION_FILE = "VERSION.txt";
    private static final Pattern VERSION_SEPARATOR = Pattern.compile("[-_./;:]");

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Version versionFor(Class<?> cls) {
        Version version;
        block9: {
            version = null;
            try {
                InputStream in = cls.getResourceAsStream(VERSION_FILE);
                if (in == null) break block9;
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    version = VersionUtil.parseVersion(br.readLine());
                }
                finally {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return version == null ? Version.unknownVersion() : version;
    }

    public static Version parseVersion(String versionStr) {
        if (versionStr == null) {
            return null;
        }
        if ((versionStr = versionStr.trim()).length() == 0) {
            return null;
        }
        String[] parts = VERSION_SEPARATOR.split(versionStr);
        if (parts.length < 2) {
            return null;
        }
        int major = VersionUtil.parseVersionPart(parts[0]);
        int minor = VersionUtil.parseVersionPart(parts[1]);
        int patch = parts.length > 2 ? VersionUtil.parseVersionPart(parts[2]) : 0;
        String snapshot = parts.length > 3 ? parts[3] : null;
        return new Version(major, minor, patch, snapshot);
    }

    protected static int parseVersionPart(String partStr) {
        char c;
        partStr = partStr.toString();
        int len = partStr.length();
        int number = 0;
        for (int i = 0; i < len && (c = partStr.charAt(i)) <= '9' && c >= '0'; ++i) {
            number = number * 10 + (c - 48);
        }
        return number;
    }
}

