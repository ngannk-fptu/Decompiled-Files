/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedRuntimeException
 *  org.springframework.lang.Nullable
 */
package org.springframework.mail;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

public abstract class MailException
extends NestedRuntimeException {
    public MailException(String msg) {
        super(msg);
    }

    public MailException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

