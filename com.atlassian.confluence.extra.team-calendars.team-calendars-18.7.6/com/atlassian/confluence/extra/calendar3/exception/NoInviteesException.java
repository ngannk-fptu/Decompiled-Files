/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.exception;

import com.atlassian.confluence.extra.calendar3.exception.CalendarException;

public class NoInviteesException
extends CalendarException {
    public NoInviteesException(String message) {
        super(message);
    }

    public NoInviteesException(String errorMessageKey, Object ... errorMessageSubstitutions) {
        super(errorMessageKey, errorMessageSubstitutions);
    }
}

