/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  io.atlassian.fugue.Either
 */
package com.atlassian.confluence.license;

import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import java.util.Optional;

public interface LicenseWebFacade {
    @Deprecated
    public Either<String, ConfluenceLicense> retrieve();

    default public io.atlassian.fugue.Either<String, ConfluenceLicense> retrieveLicense() {
        return FugueConversionUtil.toIoEither(this.retrieve());
    }

    @Deprecated
    public Either<String, Maybe<ProductLicense>> retrieve(Product var1);

    default public io.atlassian.fugue.Either<String, Optional<ProductLicense>> retrieveForProduct(Product product) {
        return FugueConversionUtil.toIoEither(this.retrieve(product)).map(FugueConversionUtil::toOptional);
    }

    @Deprecated
    public Either<String, ConfluenceLicense> validate(String var1);

    default public io.atlassian.fugue.Either<String, ConfluenceLicense> validateLicense(String licenseString) {
        return FugueConversionUtil.toIoEither(this.validate(licenseString));
    }

    @Deprecated
    public Either<String, ProductLicense> validatePlugin(String var1, Product var2);

    default public io.atlassian.fugue.Either<String, ProductLicense> validateLicenseForPlugin(String licenseString, Product product) {
        return FugueConversionUtil.toIoEither(this.validatePlugin(licenseString, product));
    }

    @Deprecated
    public Either<String, ConfluenceLicense> install(String var1);

    default public io.atlassian.fugue.Either<String, ConfluenceLicense> installLicense(String licenseString) {
        return FugueConversionUtil.toIoEither(this.install(licenseString));
    }

    @Deprecated
    public Either<String, Boolean> isLicensedForDataCenter();

    default public io.atlassian.fugue.Either<String, Boolean> licensedForDataCenter() {
        return FugueConversionUtil.toIoEither(this.isLicensedForDataCenter());
    }
}

