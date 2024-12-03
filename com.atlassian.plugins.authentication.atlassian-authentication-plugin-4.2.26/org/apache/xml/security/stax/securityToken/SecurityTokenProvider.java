/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityToken;

import org.apache.xml.security.exceptions.XMLSecurityException;

public interface SecurityTokenProvider<T> {
    public T getSecurityToken() throws XMLSecurityException;

    public String getId();
}

