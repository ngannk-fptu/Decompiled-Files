/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.setup.velocity.VelocityContextItemProvider;
import com.atlassian.spring.container.ContainerManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class DynamicContextItemProvider
implements VelocityContextItemProvider {
    @Override
    public @NonNull Map<String, Object> getContextMap() {
        HashMap<String, Object> contextMap = new HashMap<String, Object>();
        for (ContextItems item : ContextItems.values()) {
            contextMap.put(item.getKey(), item.getItem());
        }
        return Collections.unmodifiableMap(contextMap);
    }

    public static enum ContextItems {
        GLOBAL_SETTINGS("globalSettings", () -> ((SettingsManager)ContainerManager.getComponent((String)"settingsManager", SettingsManager.class)).getGlobalSettings()),
        GLOBAL_DESCRIPTION("globalDescription", () -> ((SettingsManager)ContainerManager.getComponent((String)"settingsManager", SettingsManager.class)).getGlobalDescription());

        private final String key;
        private final transient Supplier<Object> itemRef;

        private ContextItems(String key, Supplier<Object> itemRef) {
            this.key = key;
            this.itemRef = itemRef;
        }

        public String getKey() {
            return this.key;
        }

        Object getItem() {
            return this.itemRef.get();
        }
    }
}

