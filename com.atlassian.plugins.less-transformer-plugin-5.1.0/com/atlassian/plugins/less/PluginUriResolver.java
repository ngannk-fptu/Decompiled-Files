/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.lesscss.spi.DimensionAwareUriResolver
 *  com.atlassian.lesscss.spi.EncodeStateResult
 *  com.atlassian.lesscss.spi.UriResolverStateChangedEvent
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  com.google.common.base.Strings
 */
package com.atlassian.plugins.less;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.lesscss.spi.DimensionAwareUriResolver;
import com.atlassian.lesscss.spi.EncodeStateResult;
import com.atlassian.lesscss.spi.UriResolverStateChangedEvent;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.Dimensions;
import com.google.common.base.Strings;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class PluginUriResolver
implements DimensionAwareUriResolver,
StateAware {
    private static final String SNAPSHOT_VERSION = "SNAPSHOT";
    private final EventPublisher eventPublisher;
    private final PluginAccessor pluginAccessor;
    private final PluginEventManager pluginEventManager;

    public PluginUriResolver(EventPublisher eventPublisher, PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.eventPublisher = eventPublisher;
        this.pluginAccessor = pluginAccessor;
        this.pluginEventManager = pluginEventManager;
    }

    public Dimensions computeDimensions() {
        return Dimensions.empty();
    }

    public void enabled() {
        this.pluginEventManager.register((Object)this);
    }

    public void disabled() {
        this.pluginEventManager.unregister((Object)this);
    }

    public boolean exists(URI uri) {
        Plugin plugin = this.resolvePlugin(uri);
        String path = this.getResourcePath(uri);
        return plugin != null && plugin.getResource(path) != null;
    }

    public EncodeStateResult encodeState(URI uri, Coordinate coord) {
        return new EncodeStateResult(this.encodeState(uri), Optional.empty());
    }

    public String encodeState(URI uri) {
        Plugin plugin = this.resolvePlugin(uri);
        String version = plugin.getPluginInformation().getVersion();
        if (version.endsWith(SNAPSHOT_VERSION)) {
            return this.encodeFromDateLastModified(uri, plugin);
        }
        return version;
    }

    private String encodeFromDateLastModified(URI uri, Plugin plugin) {
        URL url = plugin.getResource(this.getResourcePath(uri));
        URLConnection connection = null;
        try {
            connection = url.openConnection();
            String string = String.valueOf(connection.getLastModified());
            return string;
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
        finally {
            if (connection != null) {
                try {
                    connection.getInputStream().close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    @PluginEventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        this.eventPublisher.publish((Object)new PluginUriResolvedStateChangedEvent(this, event.getPlugin().getKey()));
    }

    @PluginEventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        this.eventPublisher.publish((Object)new PluginUriResolvedStateChangedEvent(this, event.getPlugin().getKey()));
    }

    public InputStream open(URI uri) throws IOException {
        Plugin plugin = this.resolvePlugin(uri);
        InputStream in = plugin.getResourceAsStream(this.getResourcePath(uri));
        if (in == null) {
            throw new IOException(uri.getPath() + " does not exist in plugin " + plugin.getKey());
        }
        return in;
    }

    public boolean supports(URI uri) {
        return "plugin".equals(uri.getScheme()) && !Strings.isNullOrEmpty((String)uri.getHost());
    }

    private String getResourcePath(URI uri) {
        String path = uri.getPath();
        if (!Strings.isNullOrEmpty((String)path) && path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    private Plugin resolvePlugin(URI uri) {
        return this.pluginAccessor.getPlugin(uri.getHost());
    }

    public static class PluginUriResolvedStateChangedEvent
    extends UriResolverStateChangedEvent {
        private final String pluginKey;

        public PluginUriResolvedStateChangedEvent(Object source, String pluginKey) {
            super(source);
            this.pluginKey = pluginKey;
        }

        public boolean hasChanged(URI uri) {
            return this.pluginKey.equals(uri.getHost());
        }
    }
}

