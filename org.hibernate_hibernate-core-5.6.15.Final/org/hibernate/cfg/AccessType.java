/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 */
package org.hibernate.cfg;

public enum AccessType {
    DEFAULT("property"),
    PROPERTY("property"),
    FIELD("field");

    private final String accessType;

    private AccessType(String type) {
        this.accessType = type;
    }

    public String getType() {
        return this.accessType;
    }

    public static AccessType getAccessStrategy(String externalName) {
        if (externalName == null) {
            return DEFAULT;
        }
        if (FIELD.getType().equals(externalName)) {
            return FIELD;
        }
        if (PROPERTY.getType().equals(externalName)) {
            return PROPERTY;
        }
        return DEFAULT;
    }

    public static AccessType getAccessStrategy(javax.persistence.AccessType type) {
        if (javax.persistence.AccessType.PROPERTY.equals((Object)type)) {
            return PROPERTY;
        }
        if (javax.persistence.AccessType.FIELD.equals((Object)type)) {
            return FIELD;
        }
        return DEFAULT;
    }
}

