/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.license.exception.handler;

import com.atlassian.confluence.license.exception.EmptyLicenseValidationException;
import com.atlassian.confluence.license.exception.handler.AbstractLicenseExceptionHandler;
import com.atlassian.confluence.util.i18n.I18NBean;

public class EmptyLicenseValidationExceptionHandler
extends AbstractLicenseExceptionHandler<EmptyLicenseValidationException> {
    public EmptyLicenseValidationExceptionHandler(I18NBean i18n) {
        super(i18n);
    }

    @Override
    public String handle(EmptyLicenseValidationException exception) {
        return this.lookupMessage("error.license.required", new Object[0]);
    }
}

