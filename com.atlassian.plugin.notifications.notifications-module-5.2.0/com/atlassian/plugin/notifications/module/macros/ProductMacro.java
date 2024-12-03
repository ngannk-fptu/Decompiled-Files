/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.notifications.module.macros;

import com.atlassian.plugin.notifications.api.macros.Macro;
import com.atlassian.sal.api.ApplicationProperties;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ProductMacro
implements Macro {
    private final String product;

    public ProductMacro(ApplicationProperties properties) {
        if (StringUtils.isBlank((CharSequence)properties.getDisplayName())) {
            throw new IllegalArgumentException("Product name can not be empty!");
        }
        this.product = properties.getDisplayName().toLowerCase();
    }

    @Override
    public String getName() {
        return "product";
    }

    @Override
    public String resolve(Map<String, Object> context) {
        return this.product;
    }
}

