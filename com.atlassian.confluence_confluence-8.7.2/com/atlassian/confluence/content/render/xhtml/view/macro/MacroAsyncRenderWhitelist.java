/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.content.render.xhtml.view.macro;

import com.atlassian.confluence.macro.AsyncRenderSafe;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;

public class MacroAsyncRenderWhitelist {
    private static final Logger log = LoggerFactory.getLogger(MacroAsyncRenderWhitelist.class);
    private final Map<String, Boolean> macroRenderSafetyFlags;

    @VisibleForTesting
    MacroAsyncRenderWhitelist(Map<String, Boolean> macroRenderSafetyFlags) {
        this.macroRenderSafetyFlags = macroRenderSafetyFlags;
    }

    public static MacroAsyncRenderWhitelist emptyWhitelist() {
        return new MacroAsyncRenderWhitelist(Collections.emptyMap());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MacroAsyncRenderWhitelist fromPropertiesResource(Resource resource) {
        MacroAsyncRenderWhitelist macroAsyncRenderWhitelist;
        block7: {
            if (resource == null) {
                log.debug("Whitelist resource is null; using empty whitelist");
                return MacroAsyncRenderWhitelist.emptyWhitelist();
            }
            InputStream inputStream = null;
            try {
                log.debug("Loading whitelist from {}", (Object)resource.getDescription());
                Properties properties = new Properties();
                inputStream = resource.getInputStream();
                properties.load(inputStream);
                macroAsyncRenderWhitelist = MacroAsyncRenderWhitelist.fromProperties(properties);
                if (inputStream == null) break block7;
            }
            catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    throw throwable;
                }
                catch (IOException ex) {
                    log.error("Failed to read macro async-render-safe whitelist", (Throwable)ex);
                    return MacroAsyncRenderWhitelist.emptyWhitelist();
                }
            }
            inputStream.close();
        }
        return macroAsyncRenderWhitelist;
    }

    public static MacroAsyncRenderWhitelist fromProperties(Properties properties) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            builder.put((Object)((String)entry.getKey()), (Object)Boolean.valueOf((String)entry.getValue()));
        }
        return new MacroAsyncRenderWhitelist((Map<String, Boolean>)builder.build());
    }

    public boolean isAsyncRenderSafe(String macroId, Class<? extends Macro> macroClass) {
        if (this.macroRenderSafetyFlags.containsKey(macroId)) {
            Boolean asyncRenderSafe = this.macroRenderSafetyFlags.get(macroId);
            log.debug("Macro '{}' is listed in whitelist as async-render-safe={}", (Object)macroId, (Object)asyncRenderSafe);
            return asyncRenderSafe;
        }
        AsyncRenderSafe annotation = (AsyncRenderSafe)AnnotationUtils.findAnnotation(macroClass, AsyncRenderSafe.class);
        if (annotation != null) {
            boolean asyncRenderSafe = annotation.value();
            log.debug("Macro '{}' is specified by {} as async-render-safe={}", new Object[]{macroId, AsyncRenderSafe.class.getSimpleName(), asyncRenderSafe});
            return asyncRenderSafe;
        }
        log.debug("Neither whitelist nor annotation lists async-render-safety of macro '{}', defaulting to false", (Object)macroId);
        return false;
    }

    public boolean isAsyncRenderSafe(MacroDefinition macroDefinition, Class<? extends Macro> macroClass) {
        return this.isAsyncRenderSafe(macroDefinition.getName(), macroClass);
    }
}

