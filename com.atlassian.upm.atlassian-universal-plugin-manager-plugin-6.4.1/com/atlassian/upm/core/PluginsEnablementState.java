/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core;

import com.atlassian.upm.Iterables;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRetriever;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class PluginsEnablementState {
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String comment;
    @JsonProperty
    private final Date saveDate;
    @JsonProperty
    private final Collection<PluginState> plugins;

    @JsonCreator
    public PluginsEnablementState(@JsonProperty(value="title") String title, @JsonProperty(value="comment") String comment, @JsonProperty(value="saveDate") Date saveDate, @JsonProperty(value="plugins") Collection<PluginState> plugins) {
        this.title = title;
        this.comment = comment;
        this.saveDate = saveDate;
        this.plugins = Collections.unmodifiableCollection(plugins);
    }

    private PluginsEnablementState(Builder builder) {
        this.title = builder.title;
        this.comment = builder.comment;
        this.saveDate = builder.saveDate;
        this.plugins = builder.plugins;
    }

    public String getTitle() {
        return this.title;
    }

    public String getComment() {
        return this.comment;
    }

    public Date getSaveDate() {
        return this.saveDate;
    }

    public Iterable<PluginState> getPlugins() {
        return this.plugins;
    }

    public static Predicate<PluginState> pluginStateEnabled(boolean enabled) {
        return input -> input.isEnabled() == enabled;
    }

    public static Function<PluginState, String> pluginStateKey() {
        return PluginState::getKey;
    }

    public static Predicate<PluginState> isUserInstalled(PluginRetriever retriever, PluginMetadataAccessor metadata) {
        return ps -> {
            Iterator<Plugin> iterator = retriever.getPlugin(ps.getKey()).iterator();
            if (iterator.hasNext()) {
                Plugin p = iterator.next();
                return metadata.isUserInstalled(p);
            }
            return false;
        };
    }

    public static Predicate<ModuleState> moduleStateEnabled(boolean enabled) {
        return input -> input.isEnabled() == enabled;
    }

    public static final class ModuleState {
        @JsonProperty
        private final String completeKey;
        @JsonProperty
        private final boolean enabled;

        @JsonCreator
        public ModuleState(@JsonProperty(value="completeKey") String completeKey, @JsonProperty(value="enabled") boolean enabled) {
            this.completeKey = Objects.requireNonNull(completeKey, "completeKey");
            this.enabled = enabled;
        }

        private ModuleState(Builder builder) {
            this(builder.completeKey, builder.enabled);
        }

        public String getCompleteKey() {
            return this.completeKey;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public static final class Builder {
            private String completeKey;
            private boolean enabled;

            public Builder(Plugin.Module module, UpmPluginAccessor pluginAccessor) {
                Objects.requireNonNull(module, "moduleDescriptor");
                this.completeKey = module.getCompleteKey();
                this.enabled = pluginAccessor.isPluginModuleEnabled(module.getCompleteKey());
            }

            public Builder(String completeKey, boolean enabled, String name) {
                this.completeKey = Objects.requireNonNull(completeKey, "completeKey");
                this.enabled = enabled;
            }

            public Builder enabled(boolean enabled) {
                this.enabled = enabled;
                return this;
            }

            public ModuleState build() {
                return new ModuleState(this);
            }
        }
    }

    public static final class PluginState {
        @JsonProperty
        private final String key;
        @JsonProperty
        private final boolean enabled;
        @JsonProperty
        private final Collection<ModuleState> modules;

        @JsonCreator
        public PluginState(@JsonProperty(value="key") String key, @JsonProperty(value="enabled") boolean enabled, @JsonProperty(value="modules") Collection<ModuleState> modules) {
            this.key = Objects.requireNonNull(key, "key");
            this.enabled = enabled;
            this.modules = Collections.unmodifiableCollection(Objects.requireNonNull(modules, "modules"));
        }

        private PluginState(Builder builder) {
            this(builder.key, builder.enabled, builder.modules);
        }

        public String getKey() {
            return this.key;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public Iterable<ModuleState> getModules() {
            return this.modules;
        }

        public boolean isModuleEnabled(String completeKey) {
            for (ModuleState mc : this.modules) {
                if (!completeKey.equals(mc.getCompleteKey())) continue;
                return mc.isEnabled();
            }
            return true;
        }

        public static final class Builder {
            private String key;
            private boolean enabled;
            private Collection<ModuleState> modules;

            public Builder(Plugin plugin, UpmPluginAccessor pluginAccessor) {
                this(plugin.getKey(), pluginAccessor.isPluginEnabled(plugin.getKey()), Builder.getModuleConfigurations(plugin, pluginAccessor));
            }

            public Builder(String key, boolean enabled, Iterable<ModuleState> modules) {
                this.key = Objects.requireNonNull(key, "key");
                this.enabled = enabled;
                this.modules = Iterables.toStream(modules).filter(PluginsEnablementState.moduleStateEnabled(false)).collect(Collectors.toList());
            }

            public Builder enabled(boolean enabled) {
                this.enabled = enabled;
                return this;
            }

            public Builder modules(Collection<ModuleState> modules) {
                this.modules = modules;
                return this;
            }

            public PluginState build() {
                return new PluginState(this);
            }

            private static Iterable<ModuleState> getModuleConfigurations(Plugin plugin, UpmPluginAccessor pluginAccessor) {
                return StreamSupport.stream(plugin.getModules().spliterator(), false).map(module -> new ModuleState.Builder((Plugin.Module)module, pluginAccessor).build()).collect(Collectors.toList());
            }
        }
    }

    public static final class Builder {
        private String title;
        private String comment;
        private Date saveDate = new Date();
        private final Collection<PluginState> plugins;

        public Builder(Collection<PluginState> plugins, PluginRetriever retriever, PluginMetadataAccessor metadata) {
            this.plugins = Collections.unmodifiableList(plugins.stream().filter(PluginsEnablementState.isUserInstalled(retriever, metadata)).collect(Collectors.toList()));
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public PluginsEnablementState build() {
            return new PluginsEnablementState(this);
        }
    }
}

