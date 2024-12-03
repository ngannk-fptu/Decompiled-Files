/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugins.authentication.impl.web.usercontext.IdentifiableRuntimeException;

public class InvalidLicenseException
extends IdentifiableRuntimeException {
    public InvalidLicenseException(String s) {
        super(s);
    }
}

