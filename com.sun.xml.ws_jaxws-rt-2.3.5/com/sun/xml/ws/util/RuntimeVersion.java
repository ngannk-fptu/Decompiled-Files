/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util;

import com.sun.xml.ws.util.Version;
import java.io.IOException;
import java.io.InputStream;

public final class RuntimeVersion {
    public static final Version VERSION;

    public String getVersion() {
        return VERSION.toString();
    }

    static {
        Version version = null;
        InputStream in = RuntimeVersion.class.getResourceAsStream("version.properties");
        try {
            version = Version.create(in);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException iOException) {}
            }
        }
        VERSION = version == null ? Version.create(null) : version;
    }
}

