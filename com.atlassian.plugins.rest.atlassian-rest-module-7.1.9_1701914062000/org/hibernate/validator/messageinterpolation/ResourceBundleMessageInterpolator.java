/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELManager
 *  javax.el.ExpressionFactory
 *  javax.validation.MessageInterpolator$Context
 */
package org.hibernate.validator.messageinterpolation;

import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import javax.el.ELManager;
import javax.el.ExpressionFactory;
import javax.validation.MessageInterpolator;
import org.hibernate.validator.internal.engine.messageinterpolation.InterpolationTerm;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.SetContextClassLoader;
import org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

public class ResourceBundleMessageInterpolator
extends AbstractMessageInterpolator {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ExpressionFactory expressionFactory;

    public ResourceBundleMessageInterpolator() {
        this.expressionFactory = ResourceBundleMessageInterpolator.buildExpressionFactory();
    }

    public ResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator) {
        super(userResourceBundleLocator);
        this.expressionFactory = ResourceBundleMessageInterpolator.buildExpressionFactory();
    }

    public ResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator, ResourceBundleLocator contributorResourceBundleLocator) {
        super(userResourceBundleLocator, contributorResourceBundleLocator);
        this.expressionFactory = ResourceBundleMessageInterpolator.buildExpressionFactory();
    }

    public ResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator, ResourceBundleLocator contributorResourceBundleLocator, boolean cachingEnabled) {
        super(userResourceBundleLocator, contributorResourceBundleLocator, cachingEnabled);
        this.expressionFactory = ResourceBundleMessageInterpolator.buildExpressionFactory();
    }

    public ResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator, boolean cachingEnabled) {
        super(userResourceBundleLocator, null, cachingEnabled);
        this.expressionFactory = ResourceBundleMessageInterpolator.buildExpressionFactory();
    }

    public ResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator, boolean cachingEnabled, ExpressionFactory expressionFactory) {
        super(userResourceBundleLocator, null, cachingEnabled);
        this.expressionFactory = expressionFactory;
    }

    @Override
    public String interpolate(MessageInterpolator.Context context, Locale locale, String term) {
        InterpolationTerm expression = new InterpolationTerm(term, locale, this.expressionFactory);
        return expression.interpolate(context);
    }

    private static ExpressionFactory buildExpressionFactory() {
        if (ResourceBundleMessageInterpolator.canLoadExpressionFactory()) {
            ExpressionFactory expressionFactory = ELManager.getExpressionFactory();
            LOG.debug("Loaded expression factory via original TCCL");
            return expressionFactory;
        }
        ClassLoader originalContextClassLoader = ResourceBundleMessageInterpolator.run(GetClassLoader.fromContext());
        try {
            ResourceBundleMessageInterpolator.run(SetContextClassLoader.action(ResourceBundleMessageInterpolator.class.getClassLoader()));
            if (ResourceBundleMessageInterpolator.canLoadExpressionFactory()) {
                ExpressionFactory expressionFactory = ELManager.getExpressionFactory();
                LOG.debug("Loaded expression factory via HV classloader");
                ExpressionFactory expressionFactory2 = expressionFactory;
                return expressionFactory2;
            }
            ResourceBundleMessageInterpolator.run(SetContextClassLoader.action(ELManager.class.getClassLoader()));
            if (ResourceBundleMessageInterpolator.canLoadExpressionFactory()) {
                ExpressionFactory expressionFactory = ELManager.getExpressionFactory();
                LOG.debug("Loaded expression factory via EL classloader");
                ExpressionFactory expressionFactory3 = expressionFactory;
                return expressionFactory3;
            }
        }
        catch (Throwable e) {
            throw LOG.getUnableToInitializeELExpressionFactoryException(e);
        }
        finally {
            ResourceBundleMessageInterpolator.run(SetContextClassLoader.action(originalContextClassLoader));
        }
        throw LOG.getUnableToInitializeELExpressionFactoryException(null);
    }

    private static boolean canLoadExpressionFactory() {
        try {
            ExpressionFactory.newInstance();
            return true;
        }
        catch (Throwable e) {
            return false;
        }
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

