/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationListener
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.BootstrapManager;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public abstract class AbstractBootstrapApplicationListener<E extends ApplicationEvent>
implements ApplicationListener<E> {
    protected BootstrapManager getBootstrapManager() {
        AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
        return (BootstrapManager)bootstrapManager;
    }
}

