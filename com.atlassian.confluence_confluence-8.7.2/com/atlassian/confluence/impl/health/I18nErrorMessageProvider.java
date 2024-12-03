/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.impl.health;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.health.ErrorMessageProvider;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.spring.container.ContainerManager;
import java.util.Objects;
import java.util.function.Supplier;

public class I18nErrorMessageProvider
implements ErrorMessageProvider {
    private static final Supplier<I18NBean> I18N_BEAN_SUPPLIER = () -> (I18NBean)ContainerManager.getComponent((String)"i18NBean", I18NBean.class);
    private final Supplier<I18NBean> i18NBeanSupplier;

    public I18nErrorMessageProvider() {
        this(I18N_BEAN_SUPPLIER);
    }

    @VisibleForTesting
    I18nErrorMessageProvider(Supplier<I18NBean> i18NBeanSupplier) {
        this.i18NBeanSupplier = Objects.requireNonNull(i18NBeanSupplier);
    }

    @Override
    public String getErrorMessage(String key, Object ... args) {
        return this.i18NBeanSupplier.get().getText(key, args);
    }
}

