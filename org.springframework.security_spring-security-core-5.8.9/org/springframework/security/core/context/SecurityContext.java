/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.context;

import java.io.Serializable;
import org.springframework.security.core.Authentication;

public interface SecurityContext
extends Serializable {
    public Authentication getAuthentication();

    public void setAuthentication(Authentication var1);
}

