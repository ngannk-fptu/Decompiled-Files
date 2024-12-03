/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.ProductLicense
 */
package com.atlassian.confluence.license.exception.handler;

import com.atlassian.confluence.license.exception.MissingConfluenceLicenseValidationException;
import com.atlassian.confluence.license.exception.handler.AbstractLicenseExceptionHandler;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.extras.api.ProductLicense;

public class MissingConfluenceLicenseValidationExceptionHandler
extends AbstractLicenseExceptionHandler<MissingConfluenceLicenseValidationException> {
    public MissingConfluenceLicenseValidationExceptionHandler(I18NBean i18n) {
        super(i18n);
    }

    @Override
    public String handle(MissingConfluenceLicenseValidationException exception) {
        StringBuilder sb = new StringBuilder();
        for (ProductLicense license : exception.getOtherProductLicenses()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(license.getProduct().getName());
        }
        return this.lookupMessage("error.license.no.confluence", sb.toString());
    }
}

