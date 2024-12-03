/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl;

import java.io.InputStream;
import java.util.Properties;

public final class BuildId {
    private static String buildId = BuildId._initiateBuildId();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String _initiateBuildId() {
        String id = "Jersey";
        InputStream in = BuildId.getIntputStream();
        if (in != null) {
            try {
                Properties p = new Properties();
                p.load(in);
                String _id = p.getProperty("Build-Id");
                if (_id != null) {
                    id = id + ": " + _id;
                }
            }
            catch (Exception exception) {
            }
            finally {
                BuildId.close(in);
            }
        }
        return id;
    }

    private static void close(InputStream in) {
        try {
            in.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static InputStream getIntputStream() {
        try {
            return BuildId.class.getResourceAsStream("build.properties");
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static final String getBuildId() {
        return buildId;
    }
}

