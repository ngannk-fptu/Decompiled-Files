/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.data.themes;

import com.atlassian.oauth2.provider.data.themes.ProductCustomTheme;
import com.atlassian.oauth2.provider.data.themes.ProductCustomThemeFactory;

public class BitbucketCustomThemeFactory
extends ProductCustomThemeFactory {
    @Override
    public ProductCustomTheme get() {
        return new ProductCustomTheme("#0747a6", "");
    }
}

