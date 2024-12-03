/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.component.annotations;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ConfigurationPolicy {
    OPTIONAL("optional"),
    REQUIRE("require"),
    IGNORE("ignore");

    private final String value;

    private ConfigurationPolicy(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}

