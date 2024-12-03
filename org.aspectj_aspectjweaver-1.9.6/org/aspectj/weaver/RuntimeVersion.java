/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

public enum RuntimeVersion {
    V1_2("1.2"),
    V1_5("1.5"),
    V1_6_10("1.6.10"),
    V1_9("1.9");

    private String[] aliases = null;

    private RuntimeVersion(String ... aliases) {
        this.aliases = aliases;
    }

    public static RuntimeVersion getVersionFor(String version) {
        for (RuntimeVersion candidateVersion : RuntimeVersion.values()) {
            if (candidateVersion.name().equals(version)) {
                return candidateVersion;
            }
            if (candidateVersion.aliases == null) continue;
            for (String alias : candidateVersion.aliases) {
                if (!alias.equals(version)) continue;
                return candidateVersion;
            }
        }
        return null;
    }

    public boolean isThisVersionOrLater(RuntimeVersion version) {
        return this.compareTo(version) >= 0;
    }
}

