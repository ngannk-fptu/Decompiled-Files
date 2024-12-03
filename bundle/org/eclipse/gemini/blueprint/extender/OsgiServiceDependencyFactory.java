/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleException
 *  org.osgi.framework.InvalidSyntaxException
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 */
package org.eclipse.gemini.blueprint.extender;

import java.util.Collection;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public interface OsgiServiceDependencyFactory {
    public Collection<OsgiServiceDependency> getServiceDependencies(BundleContext var1, ConfigurableListableBeanFactory var2) throws BeansException, InvalidSyntaxException, BundleException;
}

