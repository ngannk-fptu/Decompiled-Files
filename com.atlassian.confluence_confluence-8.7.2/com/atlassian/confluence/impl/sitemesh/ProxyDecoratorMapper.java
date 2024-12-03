/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.spring.container.ContainerManager
 *  com.opensymphony.module.sitemesh.Config
 *  com.opensymphony.module.sitemesh.Decorator
 *  com.opensymphony.module.sitemesh.DecoratorMapper
 *  com.opensymphony.module.sitemesh.Page
 *  com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.impl.sitemesh;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;

public final class ProxyDecoratorMapper
extends AbstractDecoratorMapper {
    private Supplier<DecoratorMapper> delegateRef;
    private final Function<String, DecoratorMapper> delegateLookup;
    private final BooleanSupplier containerReadyCheck;

    public ProxyDecoratorMapper() {
        this(beanName -> (DecoratorMapper)ContainerManager.getComponent((String)beanName, DecoratorMapper.class), ContainerManager::isContainerSetup);
    }

    @VisibleForTesting
    ProxyDecoratorMapper(Function<String, DecoratorMapper> delegateLookup, BooleanSupplier containerReadyCheck) {
        this.delegateLookup = delegateLookup;
        this.containerReadyCheck = containerReadyCheck;
    }

    public void init(final Config config, final Properties properties, final DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        this.delegateRef = new LazyReference<DecoratorMapper>(){

            protected DecoratorMapper create() throws Exception {
                String beanName = properties.getProperty("beanName");
                DecoratorMapper delegate = ProxyDecoratorMapper.this.delegateLookup.apply(beanName);
                delegate.init(config, properties, parent);
                return delegate;
            }
        };
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        return this.getDecorator(dm -> dm.getDecorator(request, page));
    }

    public Decorator getNamedDecorator(HttpServletRequest request, String name) {
        return this.getDecorator(dm -> dm.getNamedDecorator(request, name));
    }

    private Decorator getDecorator(Function<DecoratorMapper, Decorator> f) {
        return this.getDelegate().map(f).orElseGet(() -> (Decorator)f.apply(this.parent));
    }

    private Optional<DecoratorMapper> getDelegate() {
        if (this.containerReadyCheck.getAsBoolean()) {
            return Optional.ofNullable(this.delegateRef.get());
        }
        return Optional.empty();
    }
}

