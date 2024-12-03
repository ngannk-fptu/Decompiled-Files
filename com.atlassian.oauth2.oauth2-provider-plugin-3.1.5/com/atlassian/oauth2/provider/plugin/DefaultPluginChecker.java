/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.event.events.PluginEvent
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.oauth2.provider.plugin;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.provider.core.plugin.PluginChecker;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.event.events.PluginEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class DefaultPluginChecker
implements PluginChecker,
InitializingBean,
DisposableBean {
    static final String OAUTH2_PROVIDER_PLUGIN_KEY = "com.atlassian.oauth2.oauth2-provider-plugin";
    static final String OAUTH2_SCOPES_PLUGIN_KEY = "com.atlassian.oauth2.oauth2-scopes-plugin";
    private volatile boolean isOAuth2ProviderPluginEnabled = false;
    private volatile boolean isOAuth2ScopesPluginEnabled = false;
    private final EventPublisher eventPublisher;
    private final PluginAccessor pluginAccessor;

    public DefaultPluginChecker(EventPublisher eventPublisher, PluginAccessor pluginAccessor) {
        this.eventPublisher = eventPublisher;
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public boolean isOAuth2ProviderPluginEnabled() {
        if (!this.isOAuth2ProviderPluginEnabled && this.pluginAccessor.isPluginEnabled(OAUTH2_PROVIDER_PLUGIN_KEY)) {
            this.isOAuth2ProviderPluginEnabled = true;
        }
        return this.isOAuth2ProviderPluginEnabled;
    }

    @Override
    public boolean isOAuth2ScopesPluginEnabled() {
        if (!this.isOAuth2ScopesPluginEnabled && this.pluginAccessor.isPluginEnabled(OAUTH2_SCOPES_PLUGIN_KEY)) {
            this.isOAuth2ScopesPluginEnabled = true;
        }
        return this.isOAuth2ScopesPluginEnabled;
    }

    @EventListener
    public synchronized void onPluginEvent(PluginEvent pluginEvent) {
        PluginState pluginState;
        if (this.isOAuth2ProviderPlugin(pluginEvent) && (PluginState.ENABLED.equals((Object)(pluginState = pluginEvent.getPlugin().getPluginState())) || PluginState.DISABLED.equals((Object)pluginState))) {
            this.isOAuth2ProviderPluginEnabled = PluginState.ENABLED.equals((Object)pluginState);
        }
        if (this.isOAuth2ScopesPlugin(pluginEvent) && (PluginState.ENABLED.equals((Object)(pluginState = pluginEvent.getPlugin().getPluginState())) || PluginState.DISABLED.equals((Object)pluginState))) {
            this.isOAuth2ScopesPluginEnabled = PluginState.ENABLED.equals((Object)pluginState);
        }
    }

    private boolean isOAuth2ProviderPlugin(PluginEvent event) {
        return event.getPlugin().getKey().equals(OAUTH2_PROVIDER_PLUGIN_KEY);
    }

    private boolean isOAuth2ScopesPlugin(PluginEvent event) {
        return event.getPlugin().getKey().equals(OAUTH2_SCOPES_PLUGIN_KEY);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

