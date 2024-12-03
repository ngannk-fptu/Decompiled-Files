/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 */
package com.atlassian.confluence.license.validator;

import com.atlassian.confluence.license.exception.KnownConfluenceLicenseValidationException;
import com.atlassian.confluence.license.validator.LicenseValidator;
import com.atlassian.extras.api.confluence.ConfluenceLicense;

public class CompositeLicenseValidator
implements LicenseValidator {
    private LicenseValidator[] validators;

    public CompositeLicenseValidator(LicenseValidator ... validators) {
        this.validators = validators;
    }

    @Override
    public void validate(ConfluenceLicense license) throws KnownConfluenceLicenseValidationException {
        for (LicenseValidator validator : this.validators) {
            validator.validate(license);
        }
    }
}

