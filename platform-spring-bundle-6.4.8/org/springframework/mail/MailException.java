/*
 * Decompiled with CFR 0.152.
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

