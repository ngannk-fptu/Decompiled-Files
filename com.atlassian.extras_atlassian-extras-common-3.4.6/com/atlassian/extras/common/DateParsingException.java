/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.common;

import com.atlassian.extras.common.LicenseException;

public class DateParsingException
extends LicenseException {
    private final String dateString;

    public DateParsingException(String dateString) {
        this.dateString = dateString;
    }

    public DateParsingException(String dateString, Throwable throwable) {
        super(throwable);
        this.dateString = dateString;
    }

    public String getMessage() {
        return "Could NOT parse <" + this.dateString + "> into a 'license' date";
    }
}

