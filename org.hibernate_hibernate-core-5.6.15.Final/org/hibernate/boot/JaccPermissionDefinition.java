/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot;

public class JaccPermissionDefinition {
    public final String contextId;
    public final String role;
    public final String clazz;
    public final String actions;

    public JaccPermissionDefinition(String contextId, String role, String clazz, String actions) {
        this.contextId = contextId;
        this.role = role;
        this.clazz = clazz;
        this.actions = actions;
    }
}

