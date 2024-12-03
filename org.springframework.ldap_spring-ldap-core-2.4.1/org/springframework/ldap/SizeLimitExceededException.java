/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.LimitExceededException;

public class SizeLimitExceededException
extends LimitExceededException {
    public SizeLimitExceededException(javax.naming.SizeLimitExceededException cause) {
        super(cause);
    }
}

