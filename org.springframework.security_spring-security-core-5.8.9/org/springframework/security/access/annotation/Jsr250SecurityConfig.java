/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.security.DenyAll
 *  javax.annotation.security.PermitAll
 */
package org.springframework.security.access.annotation;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import org.springframework.security.access.SecurityConfig;

@Deprecated
public class Jsr250SecurityConfig
extends SecurityConfig {
    public static final Jsr250SecurityConfig PERMIT_ALL_ATTRIBUTE = new Jsr250SecurityConfig(PermitAll.class.getName());
    public static final Jsr250SecurityConfig DENY_ALL_ATTRIBUTE = new Jsr250SecurityConfig(DenyAll.class.getName());

    public Jsr250SecurityConfig(String role) {
        super(role);
    }
}

