/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  javax.annotation.Nonnull
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.jwt.internal.writer;

import com.atlassian.jwt.core.writer.JsonSmartJwtJsonBuilderFactory;
import com.atlassian.jwt.writer.JwtClaimWriter;
import com.atlassian.jwt.writer.JwtJsonBuilder;
import com.atlassian.jwt.writer.JwtJsonBuilderFactory;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.annotation.Nonnull;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class PluggableJwtJsonBuilderFactory
implements LifecycleAware,
JwtJsonBuilderFactory {
    private final ServiceTracker tracker;
    private JwtJsonBuilderFactory delegate = new JsonSmartJwtJsonBuilderFactory();

    public PluggableJwtJsonBuilderFactory(BundleContext bundleContext) {
        this.tracker = new ServiceTracker(bundleContext, JwtClaimWriter.class.getName(), null);
    }

    @Override
    @Nonnull
    public JwtJsonBuilder jsonBuilder() {
        return this.decorate(this.delegate.jsonBuilder());
    }

    public void onStart() {
        this.tracker.open();
    }

    public void onStop() {
        this.tracker.close();
    }

    public void setDelegate(JwtJsonBuilderFactory delegate) {
        this.delegate = delegate;
    }

    private JwtJsonBuilder decorate(final JwtJsonBuilder builder) {
        return (JwtJsonBuilder)Proxy.newProxyInstance(JwtJsonBuilder.class.getClassLoader(), new Class[]{JwtJsonBuilder.class}, new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result;
                if ("build".equals(method.getName())) {
                    PluggableJwtJsonBuilderFactory.this.writeClaims(builder);
                }
                return (result = method.invoke((Object)builder, args)) == builder ? proxy : result;
            }
        });
    }

    private void writeClaims(JwtJsonBuilder builder) {
        Object[] services = this.tracker.getServices();
        if (services != null) {
            for (Object service : services) {
                if (!JwtClaimWriter.class.isAssignableFrom(service.getClass())) continue;
                ((JwtClaimWriter)JwtClaimWriter.class.cast(service)).write(builder);
            }
        }
    }
}

