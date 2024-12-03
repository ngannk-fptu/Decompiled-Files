/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.felix.framework.Logger
 *  org.apache.felix.framework.resolver.ResolveException
 *  org.osgi.framework.BundleException
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 */
package com.atlassian.plugin.osgi.container.felix;

import java.util.Arrays;
import java.util.List;
import org.apache.felix.framework.resolver.ResolveException;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;

public class FelixLoggerBridge
extends org.apache.felix.framework.Logger {
    private final Logger log;
    private static final List<String> messagesToIgnore = Arrays.asList("BeanInfo", "sun.beans.editors.", "add an import for 'org.eclipse.gemini.blueprint.service.", "Class 'org.springframework.util.Assert'", "Class '[Lorg.eclipse.gemini.blueprint.service", "org.springframework.core.InfrastructureProxy", "org.springframework.aop.SpringProxy", "org.springframework.aop.IntroductionInfo", "Class 'org.apache.commons.logging.impl.Log4JLogger'", "org.springframework.util.Assert", "org.eclipse.gemini.blueprint.service.importer.ServiceReferenceProxy", "org.eclipse.gemini.blueprint.service.importer.ImportedOsgiServiceProxy", "org.eclipse.gemini.blueprint.service.importer.support.ImportContextClassLoaderEditor", "[Lorg.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;Editor");

    public FelixLoggerBridge(Logger log) {
        this.log = log;
        if (log.isDebugEnabled()) {
            this.setLogLevel(4);
        } else if (log.isInfoEnabled() || log.isWarnEnabled()) {
            this.setLogLevel(2);
        } else {
            this.setLogLevel(1);
        }
    }

    protected void doLog(ServiceReference serviceReference, int level, String message, Throwable throwable) {
        if (serviceReference != null) {
            message = "Service " + serviceReference + ": " + message;
        }
        switch (level) {
            case 4: {
                if (throwable != null && throwable instanceof ResolveException) {
                    this.log.debug(message, throwable);
                    break;
                }
                this.log.debug(message);
                break;
            }
            case 1: {
                if (throwable != null) {
                    if (throwable instanceof BundleException && ((BundleException)throwable).getNestedException() != null) {
                        throwable = ((BundleException)throwable).getNestedException();
                    }
                    this.log.error(message, throwable);
                    break;
                }
                this.log.error(message);
                break;
            }
            case 3: {
                this.logInfoUnlessLame(message);
                break;
            }
            case 2: {
                if (throwable != null) {
                    if (throwable instanceof ClassNotFoundException && this.isClassNotFoundsWeCareAbout(throwable)) {
                        this.log.debug("Class not found in bundle: {}", (Object)message);
                        break;
                    }
                    if (!(throwable instanceof BundleException)) break;
                    this.log.warn("{}: {}", (Object)message, (Object)throwable.getMessage());
                    break;
                }
                this.logInfoUnlessLame(message);
                break;
            }
            default: {
                this.log.debug("UNKNOWN[{}]: ", (Object)message);
            }
        }
    }

    protected void logInfoUnlessLame(String message) {
        if (message != null) {
            for (String dumbBit : messagesToIgnore) {
                if (!message.contains(dumbBit)) continue;
                return;
            }
        }
        this.log.info(message);
    }

    public boolean isClassNotFoundsWeCareAbout(Throwable t) {
        if (t instanceof ClassNotFoundException) {
            String className = t.getMessage();
            if (className.contains("***") && t.getCause() instanceof ClassNotFoundException) {
                className = t.getCause().getMessage();
            }
            if (!(className.startsWith("org.springframework") || className.endsWith("BeanInfo") || className.endsWith("Editor"))) {
                return true;
            }
        }
        return false;
    }
}

