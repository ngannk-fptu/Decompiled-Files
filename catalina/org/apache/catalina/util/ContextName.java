/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.util;

import java.util.Locale;

public final class ContextName {
    public static final String ROOT_NAME = "ROOT";
    private static final String VERSION_MARKER = "##";
    private static final char FWD_SLASH_REPLACEMENT = '#';
    private final String baseName;
    private final String path;
    private final String version;
    private final String name;

    public ContextName(String name, boolean stripFileExtension) {
        String tmp2;
        String tmp1 = name;
        if (tmp1.startsWith("/")) {
            tmp1 = tmp1.substring(1);
        }
        if ((tmp1 = tmp1.replace('/', '#')).startsWith(VERSION_MARKER) || tmp1.isEmpty()) {
            tmp1 = ROOT_NAME + tmp1;
        }
        if (stripFileExtension && (tmp1.toLowerCase(Locale.ENGLISH).endsWith(".war") || tmp1.toLowerCase(Locale.ENGLISH).endsWith(".xml"))) {
            tmp1 = tmp1.substring(0, tmp1.length() - 4);
        }
        this.baseName = tmp1;
        int versionIndex = this.baseName.indexOf(VERSION_MARKER);
        if (versionIndex > -1) {
            this.version = this.baseName.substring(versionIndex + 2);
            tmp2 = this.baseName.substring(0, versionIndex);
        } else {
            this.version = "";
            tmp2 = this.baseName;
        }
        this.path = ROOT_NAME.equals(tmp2) ? "" : "/" + tmp2.replace('#', '/');
        this.name = versionIndex > -1 ? this.path + VERSION_MARKER + this.version : this.path;
    }

    public ContextName(String path, String version) {
        this.path = path == null || "/".equals(path) || "/ROOT".equals(path) ? "" : path;
        this.version = version == null ? "" : version;
        this.name = this.version.isEmpty() ? this.path : this.path + VERSION_MARKER + this.version;
        StringBuilder tmp = new StringBuilder();
        if (this.path.isEmpty()) {
            tmp.append(ROOT_NAME);
        } else {
            tmp.append(this.path.substring(1).replace('/', '#'));
        }
        if (!this.version.isEmpty()) {
            tmp.append(VERSION_MARKER);
            tmp.append(this.version);
        }
        this.baseName = tmp.toString();
    }

    public String getBaseName() {
        return this.baseName;
    }

    public String getPath() {
        return this.path;
    }

    public String getVersion() {
        return this.version;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        StringBuilder tmp = new StringBuilder();
        if ("".equals(this.path)) {
            tmp.append('/');
        } else {
            tmp.append(this.path);
        }
        if (!this.version.isEmpty()) {
            tmp.append(VERSION_MARKER);
            tmp.append(this.version);
        }
        return tmp.toString();
    }

    public String toString() {
        return this.getDisplayName();
    }

    public static ContextName extractFromPath(String path) {
        path = path.replace("\\", "/");
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        int lastSegment = path.lastIndexOf(47);
        if (lastSegment > 0) {
            path = path.substring(lastSegment + 1);
        }
        return new ContextName(path, true);
    }
}

