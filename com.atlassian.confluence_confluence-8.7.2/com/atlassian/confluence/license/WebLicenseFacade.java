/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.fugue.Either
 *  io.atlassian.fugue.Either
 */
package com.atlassian.confluence.license;

import com.atlassian.confluence.license.exception.LicenseException;
import com.atlassian.confluence.license.exception.LicenseValidationException;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import io.atlassian.fugue.Either;

public interface WebLicenseFacade {
    @Deprecated
    public com.atlassian.fugue.Either<String, ConfluenceLicense> retrieve();

    public Either<String, ConfluenceLicense> retrieveLicense();

    @Deprecated
    public com.atlassian.fugue.Either<String, ConfluenceLicense> validate(String var1) throws LicenseException, LicenseValidationException;

    public Either<String, ConfluenceLicense> validateLicense(String var1) throws LicenseException, LicenseValidationException;

    @Deprecated
    public com.atlassian.fugue.Either<String, ConfluenceLicense> install(String var1) throws LicenseException, LicenseValidationException;

    public Either<String, ConfluenceLicense> installLicense(String var1) throws LicenseException, LicenseValidationException;
}

