/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.thready.manager;

import com.atlassian.plugin.PluginController;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;

public class DisableOldThreadyPlugin
implements LifecycleAware {
    private final PluginController pluginController;

    @Autowired
    public DisableOldThreadyPlugin(PluginController pluginController) {
        this.pluginController = Objects.requireNonNull(pluginController);
    }

    public void onStart() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                this.pluginController.disablePlugin("com.atlassian.ams.shipit.tomcat-filter");
            }
            finally {
                executor.shutdown();
            }
        });
    }

    public void onStop() {
    }
}

