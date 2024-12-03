/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi;

public class Version {
    private static final String VERSION_STRING = "5.2.3";
    private static final String RELEASE_DATE = "20220909";

    public static String getVersion() {
        return VERSION_STRING;
    }

    public static String getReleaseDate() {
        return RELEASE_DATE;
    }

    public static String getProduct() {
        return "POI";
    }

    public static String getImplementationLanguage() {
        return "Java";
    }

    public static void main(String[] args) {
        System.out.println("Apache " + Version.getProduct() + " " + Version.getVersion() + " (" + Version.getReleaseDate() + ")");
    }
}

