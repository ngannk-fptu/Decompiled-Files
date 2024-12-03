/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.token;

public interface Token {
    public String getKey();

    public long getKeyCreationTime();

    public String getExtendedInformation();
}

