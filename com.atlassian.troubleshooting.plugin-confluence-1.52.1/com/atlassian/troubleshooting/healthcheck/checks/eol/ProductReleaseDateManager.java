/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.healthcheck.checks.eol;

import com.atlassian.troubleshooting.healthcheck.checks.eol.Product;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ProductReleaseDateManager {
    public static final String PRODUCT_RELEASE_DATES_JSON_FILE_NAME = "product-release-dates.json";
    public static final String RELATIVE_PRODUCT_RELEASE_DATES_JSON_FILE_PATH = "common-healthchecks/src/main/resources/product-release-dates.json";

    @Nonnull
    public List<Product> readProducts(InputStream var1);

    public void writeProducts(List<Product> var1, File var2);
}

