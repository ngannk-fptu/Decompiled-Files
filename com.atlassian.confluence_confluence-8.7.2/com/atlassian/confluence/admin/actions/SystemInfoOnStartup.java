/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.confluence.status.SystemErrorInformationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemInfoOnStartup
implements LifecycleItem {
    private static final Logger log = LoggerFactory.getLogger(SystemInfoOnStartup.class);

    public void shutdown(LifecycleContext context) throws Exception {
    }

    public void startup(LifecycleContext context) throws Exception {
        if (log.isInfoEnabled()) {
            try {
                log.info(new SystemErrorInformationLogger(null, context.getServletContext(), null).toString(true));
            }
            catch (Exception e) {
                log.error("Unable to log system info on startup", (Throwable)e);
            }
        }
    }
}

