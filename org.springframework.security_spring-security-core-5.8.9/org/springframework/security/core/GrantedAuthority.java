/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core;

import java.io.Serializable;

public interface GrantedAuthority
extends Serializable {
    public String getAuthority();
}

