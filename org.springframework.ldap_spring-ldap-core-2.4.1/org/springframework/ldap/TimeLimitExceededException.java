/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.LimitExceededException;

public class TimeLimitExceededException
extends LimitExceededException {
    public TimeLimitExceededException(javax.naming.TimeLimitExceededException cause) {
        super(cause);
    }
}

