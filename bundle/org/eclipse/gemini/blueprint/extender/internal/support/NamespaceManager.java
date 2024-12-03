/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.util.OsgiBundleUtils
 *  org.eclipse.gemini.blueprint.util.OsgiServiceUtils
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.xml.NamespaceHandlerResolver
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.extender.internal.support;

import java.io.IOException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.internal.support.NamespacePlugins;
import org.eclipse.gemini.blueprint.extender.internal.util.BundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiServiceUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.util.Assert;
import org.xml.sax.EntityResolver;

public class NamespaceManager
implements InitializingBean,
DisposableBean {
    private static final Log log = LogFactory.getLog(NamespaceManager.class);
    private NamespacePlugins namespacePlugins;
    private ServiceRegistration nsResolverRegistration;
    private ServiceRegistration enResolverRegistration = null;
    private final BundleContext context;
    private final String extenderInfo;
    private static final String META_INF = "META-INF/";
    private static final String SPRING_HANDLERS = "spring.handlers";
    private static final String SPRING_SCHEMAS = "spring.schemas";

    public NamespaceManager(BundleContext context) {
        this.context = context;
        this.extenderInfo = context.getBundle().getSymbolicName() + "|" + OsgiBundleUtils.getBundleVersion((Bundle)context.getBundle());
        this.namespacePlugins = new NamespacePlugins();
    }

    public void maybeAddNamespaceHandlerFor(Bundle bundle, boolean isLazyBundle) {
        if (OsgiBundleUtils.isSystemBundle((Bundle)bundle)) {
            return;
        }
        if ("org.eclipse.gemini.blueprint.core".equals(bundle.getSymbolicName()) && !bundle.equals(BundleUtils.getDMCoreBundle(this.context))) {
            return;
        }
        boolean debug = log.isDebugEnabled();
        boolean trace = log.isTraceEnabled();
        boolean hasHandlers = false;
        boolean hasSchemas = false;
        if (trace) {
            log.trace((Object)("Inspecting bundle " + bundle + " for Spring namespaces"));
        }
        if (this.context.getBundle().equals(bundle)) {
            try {
                Enumeration handlers = bundle.getResources("META-INF/spring.handlers");
                Enumeration schemas = bundle.getResources("META-INF/spring.schemas");
                hasHandlers = handlers != null;
                boolean bl = hasSchemas = schemas != null;
                if (hasHandlers && debug) {
                    log.debug((Object)("Found namespace handlers: " + Collections.list(schemas)));
                }
            }
            catch (IOException ioe) {
                log.warn((Object)"Cannot discover own namespaces", (Throwable)ioe);
            }
        } else {
            hasHandlers = bundle.findEntries(META_INF, SPRING_HANDLERS, false) != null;
            boolean bl = hasSchemas = bundle.findEntries(META_INF, SPRING_SCHEMAS, false) != null;
        }
        if (hasHandlers) {
            if (trace) {
                log.trace((Object)("Bundle " + bundle + " provides Spring namespace handlers..."));
            }
            if (isLazyBundle) {
                this.namespacePlugins.addPlugin(bundle, isLazyBundle, true);
            } else if (this.hasCompatibleNamespaceType(bundle)) {
                this.namespacePlugins.addPlugin(bundle, isLazyBundle, false);
            } else if (debug) {
                log.debug((Object)("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle) + "] declares namespace handlers but is not compatible with extender [" + this.extenderInfo + "]; ignoring..."));
            }
        } else if (hasSchemas) {
            this.namespacePlugins.addPlugin(bundle, isLazyBundle, false);
            if (trace) {
                log.trace((Object)("Bundle " + bundle + " provides Spring schemas..."));
            }
        }
    }

    private boolean hasCompatibleNamespaceType(Bundle bundle) {
        return this.namespacePlugins.isTypeCompatible(bundle);
    }

    public void maybeRemoveNameSpaceHandlerFor(Bundle bundle) {
        Assert.notNull((Object)bundle);
        boolean removed = this.namespacePlugins.removePlugin(bundle);
        if (removed && log.isDebugEnabled()) {
            log.debug((Object)("Removed namespace handler resolver for " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle)));
        }
    }

    private void registerResolverServices() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Registering Spring NamespaceHandlerResolver and EntityResolver...");
        }
        Bundle bnd = BundleUtils.getDMCoreBundle(this.context);
        Hashtable<String, Long> props = null;
        if (bnd != null) {
            props = new Hashtable<String, Long>();
            ((Dictionary)props).put("spring.osgi.core.bundle.id", bnd.getBundleId());
            ((Dictionary)props).put("spring.osgi.core.bundle.timestamp", bnd.getLastModified());
        }
        this.nsResolverRegistration = this.context.registerService(new String[]{NamespaceHandlerResolver.class.getName()}, (Object)this.namespacePlugins, props);
        this.enResolverRegistration = this.context.registerService(new String[]{EntityResolver.class.getName()}, (Object)this.namespacePlugins, props);
    }

    private void unregisterResolverService() {
        boolean result = OsgiServiceUtils.unregisterService((ServiceRegistration)this.nsResolverRegistration);
        boolean bl = result = result || OsgiServiceUtils.unregisterService((ServiceRegistration)this.enResolverRegistration);
        if (result && log.isDebugEnabled()) {
            log.debug((Object)"Unregistering Spring NamespaceHandler and EntityResolver service");
        }
        this.nsResolverRegistration = null;
        this.enResolverRegistration = null;
    }

    public NamespacePlugins getNamespacePlugins() {
        return this.namespacePlugins;
    }

    public void afterPropertiesSet() {
        this.registerResolverServices();
    }

    public void destroy() {
        this.unregisterResolverService();
        this.namespacePlugins.destroy();
        this.namespacePlugins = null;
    }
}

