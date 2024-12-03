/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 */
package com.atlassian.confluence.license.validator;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.license.exception.KnownConfluenceLicenseValidationException;
import com.atlassian.extras.api.confluence.ConfluenceLicense;

@Internal
public interface LicenseValidator {
    public void validate(ConfluenceLicense var1) throws KnownConfluenceLicenseValidationException;
}

