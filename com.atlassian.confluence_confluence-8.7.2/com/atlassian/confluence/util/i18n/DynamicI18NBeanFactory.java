/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ComponentNotFoundException
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.util.concurrent.LazyReference
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.confluence.util.i18n.DefaultI18NBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

public class DynamicI18NBeanFactory
implements I18NBeanFactory {
    private static final String USER_I18N_BEAN_FACTORY_REF = "userI18NBeanFactory";
    private static final String I18N_BEAN_FACTORY_REF = "i18NBeanFactory";
    private final @NonNull LazyReference<I18NBeanFactory> delegateI18NBeanFactory;
    private final Supplier<Optional<BeanFactory>> setupBeanFactorySupplier;

    @VisibleForTesting
    public DynamicI18NBeanFactory(Supplier<Optional<BeanFactory>> setupBeanFactorySupplier) {
        this.setupBeanFactorySupplier = setupBeanFactorySupplier;
        this.delegateI18NBeanFactory = new LazyReference<I18NBeanFactory>(){

            protected @NonNull I18NBeanFactory create() throws Exception {
                return DynamicI18NBeanFactory.this.getTrappingExceptions(DynamicI18NBeanFactory.USER_I18N_BEAN_FACTORY_REF, I18NBeanFactory.class).orElse(DynamicI18NBeanFactory.this.getTrappingExceptions(DynamicI18NBeanFactory.I18N_BEAN_FACTORY_REF, I18NBeanFactory.class).orElseGet(DefaultI18NBeanFactory::new));
            }
        };
    }

    public DynamicI18NBeanFactory() {
        this(() -> SetupContext.isAvailable() ? Optional.of(SetupContext.get().getBeanFactory()) : Optional.empty());
    }

    private <T> Optional<T> getTrappingExceptions(String beanName, Class<T> beanClass) {
        try {
            if (ContainerManager.isContainerSetup()) {
                return Optional.of(ContainerManager.getComponent((String)beanName, beanClass));
            }
            return this.setupBeanFactorySupplier.get().map(c -> Optional.of(c.getBean(beanName, beanClass))).orElseGet(Optional::empty);
        }
        catch (ComponentNotFoundException | IllegalStateException | NoSuchBeanDefinitionException e) {
            return Optional.empty();
        }
    }

    private @NonNull I18NBeanFactory getI18NBeanFactory() {
        return Objects.requireNonNull((I18NBeanFactory)this.delegateI18NBeanFactory.get());
    }

    @Override
    public @NonNull I18NBean getI18NBean(@NonNull Locale locale) {
        return this.getI18NBeanFactory().getI18NBean(locale);
    }

    @Override
    public @NonNull I18NBean getI18NBean() {
        return this.getI18NBeanFactory().getI18NBean();
    }

    @Override
    public @NonNull String getStateHash() {
        return this.getI18NBeanFactory().getStateHash();
    }
}

