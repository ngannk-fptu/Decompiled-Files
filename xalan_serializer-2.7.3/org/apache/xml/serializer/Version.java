/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

public final class Version {
    public static String getVersion() {
        return Version.getProduct() + " " + Version.getImplementationLanguage() + " " + Version.getMajorVersionNum() + "." + Version.getReleaseVersionNum() + "." + (Version.getDevelopmentVersionNum() > 0 ? "D" + Version.getDevelopmentVersionNum() : "" + Version.getMaintenanceVersionNum());
    }

    public static void main(String[] argv) {
        System.out.println(Version.getVersion());
    }

    public static String getProduct() {
        return "Serializer";
    }

    public static String getImplementationLanguage() {
        return "Java";
    }

    public static int getMajorVersionNum() {
        return 2;
    }

    public static int getReleaseVersionNum() {
        return 7;
    }

    public static int getMaintenanceVersionNum() {
        return 3;
    }

    public static int getDevelopmentVersionNum() {
        try {
            if (new String("").length() == 0) {
                return 0;
            }
            return Integer.parseInt("");
        }
        catch (NumberFormatException nfe) {
            return 0;
        }
    }
}

