/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.util.BundleDelegatingClassLoader
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver
 *  org.springframework.beans.factory.xml.DelegatingEntityResolver
 *  org.springframework.beans.factory.xml.NamespaceHandler
 *  org.springframework.beans.factory.xml.NamespaceHandlerResolver
 */
package org.eclipse.gemini.blueprint.extender.internal.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.internal.support.LazyBundleRegistry;
import org.eclipse.gemini.blueprint.util.BundleDelegatingClassLoader;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.DelegatingEntityResolver;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class NamespacePlugins
implements NamespaceHandlerResolver,
EntityResolver,
DisposableBean {
    private static final Log log = LogFactory.getLog(NamespacePlugins.class);
    final LazyBundleRegistry.Condition condition = new LazyBundleRegistry.Condition(){
        private final String NS_HANDLER_RESOLVER_CLASS_NAME = NamespaceHandlerResolver.class.getName();

        @Override
        public boolean pass(Bundle bundle) {
            try {
                Class type = bundle.loadClass(this.NS_HANDLER_RESOLVER_CLASS_NAME);
                return NamespaceHandlerResolver.class.equals((Object)type);
            }
            catch (Throwable th) {
                log.warn((Object)("Bundle " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle) + " cannot see class [" + this.NS_HANDLER_RESOLVER_CLASS_NAME + "]; ignoring it as a namespace resolver"));
                return false;
            }
        }
    };
    private final LazyBundleRegistry.Activator<Plugin> activation = new LazyBundleRegistry.Activator<Plugin>(){

        @Override
        public Plugin activate(Bundle bundle) {
            return new Plugin(bundle);
        }
    };
    private final LazyBundleRegistry<Plugin> pluginRegistry = new LazyBundleRegistry<Plugin>(this.condition, this.activation, log);

    void addPlugin(Bundle bundle, boolean lazyBundle, boolean applyCondition) {
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("Adding as " + (lazyBundle ? "lazy " : "") + "namespace handler bundle " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle)));
        }
        this.pluginRegistry.add(bundle, lazyBundle, applyCondition);
    }

    boolean isTypeCompatible(Bundle bundle) {
        return this.condition.pass(bundle);
    }

    boolean removePlugin(Bundle bundle) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Removing handler " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle)));
        }
        return this.pluginRegistry.remove(bundle);
    }

    public NamespaceHandler resolve(final String namespaceUri) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(new PrivilegedAction<NamespaceHandler>(){

                @Override
                public NamespaceHandler run() {
                    return NamespacePlugins.this.doResolve(namespaceUri);
                }
            });
        }
        return this.doResolve(namespaceUri);
    }

    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        if (System.getSecurityManager() != null) {
            try {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<InputSource>(){

                    @Override
                    public InputSource run() throws Exception {
                        return NamespacePlugins.this.doResolveEntity(publicId, systemId);
                    }
                });
            }
            catch (PrivilegedActionException pae) {
                Exception cause = pae.getException();
                this.handleInputSourceException(cause);
            }
        } else {
            try {
                return this.doResolveEntity(publicId, systemId);
            }
            catch (Exception ex) {
                this.handleInputSourceException(ex);
            }
        }
        return null;
    }

    private NamespaceHandler doResolve(final String namespaceUri) {
        final boolean debug = log.isDebugEnabled();
        final boolean trace = log.isTraceEnabled();
        if (debug) {
            log.debug((Object)("Trying to resolving namespace handler for " + namespaceUri));
        }
        try {
            return this.pluginRegistry.apply(new LazyBundleRegistry.Operation<Plugin, NamespaceHandler>(){

                @Override
                public NamespaceHandler operate(Plugin plugin) {
                    block5: {
                        try {
                            NamespaceHandler handler = plugin.resolve(namespaceUri);
                            if (handler != null) {
                                if (debug) {
                                    log.debug((Object)("Namespace handler for " + namespaceUri + " found inside bundle " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)plugin.getBundle())));
                                }
                                return handler;
                            }
                            if (trace) {
                                log.trace((Object)("Namespace handler for " + namespaceUri + " not found inside bundle " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)plugin.getBundle())));
                            }
                        }
                        catch (IllegalArgumentException ex) {
                            if (!trace) break block5;
                            log.trace((Object)("Namespace handler for " + namespaceUri + " not found inside bundle " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)plugin.getBundle())));
                        }
                    }
                    return null;
                }
            });
        }
        catch (Exception ex) {
            throw (RuntimeException)ex;
        }
    }

    private InputSource doResolveEntity(final String publicId, final String systemId) throws Exception {
        final boolean debug = log.isDebugEnabled();
        final boolean trace = log.isTraceEnabled();
        if (debug) {
            log.debug((Object)("Trying to resolving entity for " + publicId + "|" + systemId));
        }
        if (systemId != null) {
            return this.pluginRegistry.apply(new LazyBundleRegistry.Operation<Plugin, InputSource>(){

                @Override
                public InputSource operate(Plugin plugin) throws SAXException, IOException {
                    block4: {
                        try {
                            InputSource inputSource = plugin.resolveEntity(publicId, systemId);
                            if (inputSource != null) {
                                if (debug) {
                                    log.debug((Object)("XML schema for " + publicId + "|" + systemId + " found inside bundle " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)plugin.getBundle())));
                                }
                                return inputSource;
                            }
                        }
                        catch (FileNotFoundException ex) {
                            if (!trace) break block4;
                            log.trace((Object)("XML schema for " + publicId + "|" + systemId + " not found inside bundle " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)plugin.getBundle())), (Throwable)ex);
                        }
                    }
                    return null;
                }
            });
        }
        return null;
    }

    private void handleInputSourceException(Exception exception) throws SAXException, IOException {
        if (exception instanceof RuntimeException) {
            throw (RuntimeException)exception;
        }
        if (exception instanceof IOException) {
            throw (IOException)exception;
        }
        throw (SAXException)exception;
    }

    public void destroy() {
        this.pluginRegistry.clear();
    }

    private static class Plugin
    implements NamespaceHandlerResolver,
    EntityResolver {
        private final NamespaceHandlerResolver namespace;
        private final EntityResolver entity;
        private final Bundle bundle;

        private Plugin(Bundle bundle) {
            this.bundle = bundle;
            BundleDelegatingClassLoader loader = BundleDelegatingClassLoader.createBundleClassLoaderFor((Bundle)bundle);
            this.entity = new DelegatingEntityResolver((ClassLoader)loader);
            this.namespace = new DefaultNamespaceHandlerResolver((ClassLoader)loader);
        }

        public NamespaceHandler resolve(String namespaceUri) {
            return this.namespace.resolve(namespaceUri);
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return this.entity.resolveEntity(publicId, systemId);
        }

        public Bundle getBundle() {
            return this.bundle;
        }
    }
}

