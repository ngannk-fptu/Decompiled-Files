/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.google.common.base.Supplier
 */
package com.atlassian.confluence.license;

import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.LicenseWebFacade;
import com.atlassian.confluence.license.exception.KnownConfluenceLicenseValidationException;
import com.atlassian.confluence.license.exception.LicenseException;
import com.atlassian.confluence.license.exception.handler.LicenseExceptionHandler;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.google.common.base.Supplier;

public abstract class LicenseWebFacadeTemplate
implements LicenseWebFacade {
    private LicenseService delegate;

    public LicenseWebFacadeTemplate(LicenseService delegate) {
        this.delegate = delegate;
    }

    @Override
    @Deprecated
    public Either<String, ConfluenceLicense> retrieve() {
        return this.call(this.delegate::retrieve);
    }

    @Override
    @Deprecated
    public Either<String, Maybe<ProductLicense>> retrieve(Product product) {
        return this.call(() -> this.delegate.retrieve(product));
    }

    @Override
    @Deprecated
    public Either<String, ConfluenceLicense> validate(String licenseString) throws LicenseException, KnownConfluenceLicenseValidationException {
        return this.call(() -> this.delegate.validate(licenseString));
    }

    @Override
    @Deprecated
    public Either<String, ProductLicense> validatePlugin(String licenseString, Product product) throws LicenseException, KnownConfluenceLicenseValidationException {
        return this.call(() -> this.delegate.validate(licenseString, product));
    }

    @Override
    @Deprecated
    public Either<String, ConfluenceLicense> install(String licenseString) throws LicenseException, KnownConfluenceLicenseValidationException {
        return this.call(() -> this.delegate.install(licenseString));
    }

    @Override
    @Deprecated
    public Either<String, Boolean> isLicensedForDataCenter() {
        return this.call(this.delegate::isLicensedForDataCenter);
    }

    private <R> Either<String, R> call(Supplier<R> receiver) {
        try {
            return Either.right((Object)receiver.get());
        }
        catch (Exception exception) {
            return Either.left((Object)this.createExceptionHandler().handle(exception));
        }
    }

    protected abstract LicenseExceptionHandler<Exception> createExceptionHandler();
}

