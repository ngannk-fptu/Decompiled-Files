/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.license.exception.handler;

import com.atlassian.confluence.license.exception.handler.LicenseExceptionHandler;
import com.atlassian.confluence.util.i18n.I18NBean;

public abstract class AbstractLicenseExceptionHandler<E extends Exception>
implements LicenseExceptionHandler<E> {
    private I18NBean i18n;

    public AbstractLicenseExceptionHandler(I18NBean i18n) {
        this.i18n = i18n;
    }

    protected String lookupMessage(String messageKey, Object ... messageArguments) {
        return this.i18n.getText(messageKey, messageArguments);
    }
}

