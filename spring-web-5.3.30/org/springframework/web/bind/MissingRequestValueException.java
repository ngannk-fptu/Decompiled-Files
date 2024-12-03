/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind;

import org.springframework.web.bind.ServletRequestBindingException;

public class MissingRequestValueException
extends ServletRequestBindingException {
    private final boolean missingAfterConversion;

    public MissingRequestValueException(String msg) {
        this(msg, false);
    }

    public MissingRequestValueException(String msg, boolean missingAfterConversion) {
        super(msg);
        this.missingAfterConversion = missingAfterConversion;
    }

    public boolean isMissingAfterConversion() {
        return this.missingAfterConversion;
    }
}

