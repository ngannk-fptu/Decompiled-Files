/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlcommons;

public class Version {
    public static String getVersion() {
        return Version.getProduct() + " " + Version.getVersionNum();
    }

    public static String getProduct() {
        return "XmlCommonsExternal";
    }

    public static String getVersionNum() {
        return "1.4.01";
    }

    public static void main(String[] stringArray) {
        System.out.println(Version.getVersion());
    }
}

