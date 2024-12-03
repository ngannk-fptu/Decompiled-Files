/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.license.exception.handler;

import com.atlassian.confluence.license.exception.InvalidLicenseException;
import com.atlassian.confluence.license.exception.handler.AbstractLicenseExceptionHandler;
import com.atlassian.confluence.util.i18n.I18NBean;

public class InvalidLicenseExceptionHandler
extends AbstractLicenseExceptionHandler<InvalidLicenseException> {
    public InvalidLicenseExceptionHandler(I18NBean i18n) {
        super(i18n);
    }

    @Override
    public String handle(InvalidLicenseException exception) {
        return this.lookupMessage("error.license.not.valid", new Object[0]);
    }
}

