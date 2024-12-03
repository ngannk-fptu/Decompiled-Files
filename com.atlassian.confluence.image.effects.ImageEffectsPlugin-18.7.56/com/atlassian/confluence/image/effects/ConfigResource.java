/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.image.effects.ImageEffectsConfig;
import com.atlassian.confluence.image.effects.ImageEffectsConfigChangedEvent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/config")
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class ConfigResource {
    private static final Logger log = LoggerFactory.getLogger(ConfigResource.class);
    private final ImageEffectsConfig config;
    private final EventPublisher eventPublisher;

    public ConfigResource(ImageEffectsConfig config, @ComponentImport EventPublisher eventPublisher) {
        this.config = Objects.requireNonNull(config);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @GET
    @Produces(value={"application/json"})
    public Map<String, String> getConfig() {
        return this.mapOfConfig();
    }

    @POST
    @Consumes(value={"application/x-www-form-urlencoded"})
    @Produces(value={"application/json"})
    public Map<String, String> postConfig(@FormParam(value="disable_cache") Boolean disableCache, @FormParam(value="transform.timeout_ms") String transformTimeoutMs, @FormParam(value="transform.max_data_size") String transformMaxDataSize) {
        Integer value;
        boolean configChanged = false;
        if (disableCache != null) {
            this.config.setDisableCache(disableCache);
            configChanged = true;
        }
        if ((value = this.parseInteger("transform.timeout_ms", transformTimeoutMs)) != null) {
            this.config.setTransformTimeoutMs(value);
            configChanged = true;
        }
        if ((value = this.parseInteger("transform.max_data_size", transformMaxDataSize)) != null) {
            this.config.setTransformMaxDataSize(value);
            configChanged = true;
        }
        if (configChanged) {
            log.info("Configuration has changed, sending an event");
            this.eventPublisher.publish((Object)new ImageEffectsConfigChangedEvent());
        }
        return this.mapOfConfig();
    }

    @Nullable
    private Integer parseInteger(String name, String strVal) {
        if (strVal == null) {
            return null;
        }
        try {
            return Integer.parseInt(strVal);
        }
        catch (NumberFormatException e) {
            log.info("Unable to parse parameter '{}' with value '{}'", (Object)name, (Object)strVal);
            return null;
        }
    }

    private Map<String, String> mapOfConfig() {
        TreeMap<String, String> result = new TreeMap<String, String>();
        result.put("thread_pool_configuration.core_pool_size", Integer.toString(this.config.getCorePoolSize()));
        result.put("thread_pool_configuration.max_pool_size", Integer.toString(this.config.getMaximumPoolSize()));
        result.put("thread_pool_configuration.queue_size", Integer.toString(this.config.getQueueSize()));
        result.put("thread_pool_configuration.keep_alive_time", Long.toString(this.config.getKeepAliveTime()));
        result.put("thread_pool_configuration.time_unit", this.config.getTimeUnit().name());
        result.put("disable_cache", Boolean.toString(this.config.isDisableCache()));
        result.put("transform.timeout_ms", Integer.toString(this.config.getTransformTimeoutMs()));
        result.put("transform.max_data_size", Integer.toString(this.config.getTransformMaxDataSize()));
        return result;
    }
}

