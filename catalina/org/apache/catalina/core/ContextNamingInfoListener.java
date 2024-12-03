/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.descriptor.web.ContextEnvironment
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.res.StringManager;

public class ContextNamingInfoListener
implements LifecycleListener {
    private static final String PATH_ENTRY_NAME = "context/path";
    private static final String ENCODED_PATH_ENTRY_NAME = "context/encodedPath";
    private static final String WEBAPP_VERSION_ENTRY_NAME = "context/webappVersion";
    private static final String NAME_ENTRY_NAME = "context/name";
    private static final String BASE_NAME_ENTRY_NAME = "context/baseName";
    private static final String DISPLAY_NAME_ENTRY_NAME = "context/displayName";
    private static final Log log = LogFactory.getLog(ContextNamingInfoListener.class);
    private static final StringManager sm = StringManager.getManager(ContextNamingInfoListener.class);
    private boolean emptyOnRoot = true;

    public void setEmptyOnRoot(boolean emptyOnRoot) {
        this.emptyOnRoot = emptyOnRoot;
    }

    public boolean isEmptyOnRoot() {
        return this.emptyOnRoot;
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if (event.getType().equals("configure_start")) {
            if (!(event.getLifecycle() instanceof Context)) {
                log.warn((Object)sm.getString("listener.notContext", new Object[]{event.getLifecycle().getClass().getSimpleName()}));
                return;
            }
            Context context = (Context)event.getLifecycle();
            String path = context.getPath();
            String encodedPath = context.getEncodedPath();
            String name = context.getName();
            if (!this.emptyOnRoot && path.isEmpty()) {
                encodedPath = "/";
                path = "/";
                name = "ROOT" + name;
            }
            this.addEnvEntry(context, PATH_ENTRY_NAME, path);
            this.addEnvEntry(context, ENCODED_PATH_ENTRY_NAME, encodedPath);
            this.addEnvEntry(context, WEBAPP_VERSION_ENTRY_NAME, context.getWebappVersion());
            this.addEnvEntry(context, NAME_ENTRY_NAME, name);
            this.addEnvEntry(context, BASE_NAME_ENTRY_NAME, context.getBaseName());
            this.addEnvEntry(context, DISPLAY_NAME_ENTRY_NAME, context.getDisplayName());
        }
    }

    private void addEnvEntry(Context context, String name, String value) {
        ContextEnvironment ce = new ContextEnvironment();
        ce.setName(name);
        ce.setOverride(true);
        ce.setType("java.lang.String");
        ce.setValue(value);
        if (log.isDebugEnabled()) {
            log.info((Object)sm.getString("contextNamingInfoListener.envEntry", new Object[]{name, value}));
        }
        context.getNamingResources().addEnvironment(ce);
    }
}

