/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.util.Assertions
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.conditions.AlwaysDisplayCondition
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nullable
 */
package com.atlassian.gadgets.plugins;

import com.atlassian.gadgets.plugins.PluginGadgetSpecBuilder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.conditions.AlwaysDisplayCondition;
import io.atlassian.fugue.Option;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class PluginGadgetSpec {
    private final Condition enabledCondition;
    private final Condition localCondition;
    private final String location;
    private final String moduleKey;
    private final Map<String, String> params;
    private final Plugin plugin;
    private final String publishLocation;

    public PluginGadgetSpec(Plugin plugin, String moduleKey, String location, Map<String, String> params) {
        this(null, null, location, moduleKey, params, plugin, null);
    }

    PluginGadgetSpec(@Nullable Condition enabledCondition, @Nullable Condition localCondition, String location, String moduleKey, Map<String, String> params, Plugin plugin, @Nullable String publishLocation) {
        this.enabledCondition = (Condition)Option.option((Object)enabledCondition).getOrElse((Object)new AlwaysDisplayCondition());
        this.localCondition = (Condition)Option.option((Object)localCondition).getOrElse((Object)new AlwaysDisplayCondition());
        this.location = (String)Assertions.notNull((String)"location", (Object)location);
        this.moduleKey = (String)Assertions.notNull((String)"moduleKey", (Object)moduleKey);
        this.params = this.unmodifiableCopy((Map)Assertions.notNull((String)"params", params));
        this.plugin = (Plugin)Assertions.notNull((String)"plugin", (Object)plugin);
        this.publishLocation = publishLocation;
    }

    public static PluginGadgetSpecBuilder builder() {
        return new PluginGadgetSpecBuilder();
    }

    private Map<String, String> unmodifiableCopy(Map<String, String> map) {
        return Collections.unmodifiableMap(new HashMap<String, String>(map));
    }

    public Condition getEnabledCondition() {
        return this.enabledCondition;
    }

    public Condition getLocalCondition() {
        return this.localCondition;
    }

    public Key getKey() {
        return new Key(this.getPluginKey(), this.getLocation());
    }

    public String getModuleKey() {
        return this.moduleKey;
    }

    public String getPluginKey() {
        return this.plugin.getKey();
    }

    public String getPublishLocation() {
        return this.publishLocation;
    }

    public String getLocation() {
        return this.location;
    }

    public InputStream getInputStream() {
        return this.plugin.getResourceAsStream(this.location);
    }

    public boolean isHostedExternally() {
        return this.location.startsWith("http://") || this.location.startsWith("https://");
    }

    public boolean hasParameter(String name) {
        return this.params.containsKey(name);
    }

    public String getParameter(String name) {
        return this.params.get(name);
    }

    public Date getDateLoaded() {
        return this.plugin.getDateLoaded();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.location == null ? 0 : this.location.hashCode());
        result = 31 * result + (this.plugin == null ? 0 : this.plugin.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PluginGadgetSpec other = (PluginGadgetSpec)obj;
        return this.plugin.equals(other.plugin) && this.moduleKey.equals(other.moduleKey) && this.location.equals(other.location);
    }

    public String toString() {
        return "PluginGadgetSpec{plugin=" + this.plugin + ", moduleKey='" + this.moduleKey + '\'' + ", location='" + this.location + '\'' + '}';
    }

    public static final class Key {
        private final String pluginKey;
        private final String location;

        public Key(String pluginKey, String location) {
            this.pluginKey = (String)Assertions.notNull((String)"pluginKey", (Object)pluginKey);
            this.location = (String)Assertions.notNull((String)"location", (Object)location);
        }

        public String getPluginKey() {
            return this.pluginKey;
        }

        public String getLocation() {
            return this.location;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Key that = (Key)o;
            return this.location.equals(that.location) && this.pluginKey.equals(that.pluginKey);
        }

        public int hashCode() {
            return 31 * this.pluginKey.hashCode() + this.location.hashCode();
        }

        public String toString() {
            return "Key{pluginKey='" + this.pluginKey + '\'' + ", location='" + this.location + '\'' + '}';
        }
    }
}

