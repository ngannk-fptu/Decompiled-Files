/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.util;

public class ServerInformationParser {
    public static ServerInformation parse(String serverInformationText) {
        if (!serverInformationText.contains("/")) {
            throw new IllegalArgumentException("Server information is not present: " + serverInformationText);
        }
        int slashLoc = serverInformationText.indexOf("/");
        String name = serverInformationText.substring(0, slashLoc);
        if (serverInformationText.indexOf(" ", slashLoc) == -1) {
            String version = serverInformationText.substring(slashLoc + 1);
            return new ServerInformation(name, version);
        }
        int spaceLoc = serverInformationText.indexOf(" ", slashLoc);
        String version = serverInformationText.substring(slashLoc + 1, spaceLoc);
        String otherInformation = serverInformationText.substring(spaceLoc + 2, serverInformationText.length() - 1);
        return new ServerInformation(name, version, otherInformation);
    }

    public static class ServerInformation {
        private final String name;
        private final String version;
        private final String otherInformation;

        private ServerInformation(String name, String version) {
            this(name, version, (String)null);
        }

        private ServerInformation(String name, String version, String otherInformation) {
            this.name = name;
            this.version = version;
            this.otherInformation = otherInformation;
        }

        public String getName() {
            return this.name;
        }

        public String getVersion() {
            return this.version;
        }

        public String getOtherInformation() {
            return this.otherInformation;
        }

        public boolean isApacheTomcat() {
            return "Apache Tomcat".equals(this.name);
        }
    }
}

