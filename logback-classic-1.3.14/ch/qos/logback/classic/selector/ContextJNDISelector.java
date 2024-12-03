/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Context
 *  ch.qos.logback.core.joran.spi.JoranException
 *  ch.qos.logback.core.status.InfoStatus
 *  ch.qos.logback.core.status.Status
 *  ch.qos.logback.core.status.StatusManager
 *  ch.qos.logback.core.status.StatusUtil
 *  ch.qos.logback.core.status.WarnStatus
 *  ch.qos.logback.core.util.JNDIUtil
 *  ch.qos.logback.core.util.Loader
 *  ch.qos.logback.core.util.StatusPrinter
 */
package ch.qos.logback.classic.selector;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.JNDIUtil;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.StatusPrinter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;

public class ContextJNDISelector
implements ContextSelector {
    private final Map<String, LoggerContext> synchronizedContextMap = Collections.synchronizedMap(new HashMap());
    private final LoggerContext defaultContext;
    private static final ThreadLocal<LoggerContext> threadLocal = new ThreadLocal();

    public ContextJNDISelector(LoggerContext context) {
        this.defaultContext = context;
    }

    @Override
    public LoggerContext getDefaultLoggerContext() {
        return this.defaultContext;
    }

    @Override
    public LoggerContext detachLoggerContext(String loggerContextName) {
        return this.synchronizedContextMap.remove(loggerContextName);
    }

    @Override
    public LoggerContext getLoggerContext() {
        String contextName = null;
        javax.naming.Context ctx = null;
        LoggerContext lc = threadLocal.get();
        if (lc != null) {
            return lc;
        }
        try {
            ctx = JNDIUtil.getInitialContext();
            contextName = JNDIUtil.lookupString((javax.naming.Context)ctx, (String)"java:comp/env/logback/context-name");
        }
        catch (NamingException namingException) {
            // empty catch block
        }
        if (contextName == null) {
            return this.defaultContext;
        }
        LoggerContext loggerContext = this.synchronizedContextMap.get(contextName);
        if (loggerContext == null) {
            loggerContext = new LoggerContext();
            loggerContext.setName(contextName);
            this.synchronizedContextMap.put(contextName, loggerContext);
            URL url = this.findConfigFileURL(ctx, loggerContext);
            if (url != null) {
                this.configureLoggerContextByURL(loggerContext, url);
            } else {
                try {
                    new ContextInitializer(loggerContext).autoConfig();
                }
                catch (JoranException joranException) {
                    // empty catch block
                }
            }
            if (!StatusUtil.contextHasStatusListener((Context)loggerContext)) {
                StatusPrinter.printInCaseOfErrorsOrWarnings((Context)loggerContext);
            }
        }
        return loggerContext;
    }

    private String conventionalConfigFileName(String contextName) {
        return "logback-" + contextName + ".xml";
    }

    private URL findConfigFileURL(javax.naming.Context ctx, LoggerContext loggerContext) {
        StatusManager sm = loggerContext.getStatusManager();
        String jndiEntryForConfigResource = null;
        try {
            jndiEntryForConfigResource = JNDIUtil.lookupString((javax.naming.Context)ctx, (String)"java:comp/env/logback/configuration-resource");
        }
        catch (NamingException e) {
            sm.add((Status)new WarnStatus("JNDI lookup failed", (Object)this, (Throwable)e));
        }
        if (jndiEntryForConfigResource != null) {
            sm.add((Status)new InfoStatus("Searching for [" + jndiEntryForConfigResource + "]", (Object)this));
            URL url = this.urlByResourceName(sm, jndiEntryForConfigResource);
            if (url == null) {
                String msg = "The jndi resource [" + jndiEntryForConfigResource + "] for context [" + loggerContext.getName() + "] does not lead to a valid file";
                sm.add((Status)new WarnStatus(msg, (Object)this));
            }
            return url;
        }
        String resourceByConvention = this.conventionalConfigFileName(loggerContext.getName());
        return this.urlByResourceName(sm, resourceByConvention);
    }

    private URL urlByResourceName(StatusManager sm, String resourceName) {
        sm.add((Status)new InfoStatus("Searching for [" + resourceName + "]", (Object)this));
        URL url = Loader.getResource((String)resourceName, (ClassLoader)Loader.getTCL());
        if (url != null) {
            return url;
        }
        return Loader.getResourceBySelfClassLoader((String)resourceName);
    }

    private void configureLoggerContextByURL(LoggerContext context, URL url) {
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            context.reset();
            configurator.setContext((Context)context);
            configurator.doConfigure(url);
        }
        catch (JoranException joranException) {
            // empty catch block
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings((Context)context);
    }

    @Override
    public List<String> getContextNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(this.synchronizedContextMap.keySet());
        return list;
    }

    @Override
    public LoggerContext getLoggerContext(String name) {
        return this.synchronizedContextMap.get(name);
    }

    public int getCount() {
        return this.synchronizedContextMap.size();
    }

    public void setLocalContext(LoggerContext context) {
        threadLocal.set(context);
    }

    public void removeLocalContext() {
        threadLocal.remove();
    }
}

