/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.spi;

import com.atlassian.plugin.Plugin;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PluginInstallResult {
    private final Plugin plugin;
    private final List<Plugin> dependencies;
    private final Iterable<Message> messages;

    public PluginInstallResult(Plugin plugin) {
        this(plugin, Collections.emptyList(), Collections.emptyList());
    }

    public PluginInstallResult(Plugin plugin, List<Plugin> dependencies) {
        this(plugin, dependencies, Collections.emptyList());
    }

    public PluginInstallResult(Plugin plugin, List<Plugin> dependencies, Iterable<Message> messages) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.dependencies = Collections.unmodifiableList(Objects.requireNonNull(dependencies, "dependencies"));
        this.messages = PluginInstallResult.toList(Objects.requireNonNull(messages, "messages"));
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public List<Plugin> getDependencies() {
        return this.dependencies;
    }

    public Iterable<Message> getMessages() {
        return this.messages;
    }

    private static <T> List<T> toList(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    public static class Message {
        private final Level level;
        private final String message;

        public Message(Level level, String message) {
            this.level = Objects.requireNonNull(level, "level");
            this.message = Objects.requireNonNull(message, "message");
        }

        public Level getLevel() {
            return this.level;
        }

        public String getMessage() {
            return this.message;
        }

        public static enum Level {
            INFO,
            WARNING;

        }
    }
}

