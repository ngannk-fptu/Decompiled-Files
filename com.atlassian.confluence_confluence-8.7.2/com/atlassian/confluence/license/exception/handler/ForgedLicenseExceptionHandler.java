/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.license.exception.handler;

import com.atlassian.confluence.license.exception.ForgedLicenseException;
import com.atlassian.confluence.license.exception.handler.AbstractLicenseExceptionHandler;
import com.atlassian.confluence.util.i18n.I18NBean;

public class ForgedLicenseExceptionHandler
extends AbstractLicenseExceptionHandler<ForgedLicenseException> {
    public ForgedLicenseExceptionHandler(I18NBean i18n) {
        super(i18n);
    }

    @Override
    public String handle(ForgedLicenseException exception) {
        return this.lookupMessage("error.license.forged", new Object[0]);
    }
}

