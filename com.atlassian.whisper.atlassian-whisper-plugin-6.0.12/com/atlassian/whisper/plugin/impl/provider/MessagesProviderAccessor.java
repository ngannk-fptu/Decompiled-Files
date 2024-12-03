/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.ozymandias.SafePluginPointAccess
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.whisper.plugin.api.Message
 *  com.atlassian.whisper.plugin.api.MessagesProvider
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.whisper.plugin.impl.provider;

import com.atlassian.ozymandias.SafePluginPointAccess;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.whisper.plugin.api.Message;
import com.atlassian.whisper.plugin.api.MessagesProvider;
import com.atlassian.whisper.plugin.impl.provider.MessagesProviderModuleDescriptor;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ExportAsService
public class MessagesProviderAccessor
implements MessagesProvider {
    private final PluginAccessor pluginAccessor;

    @Inject
    public MessagesProviderAccessor(@ComponentImport PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public Set<Message> getMessages() {
        return this.withDescriptor(MessagesProviderModuleDescriptor::getMessages).stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public Set<Message> getMessagesForUser(UserProfile user) {
        return this.withDescriptor(descriptor -> descriptor.getMessagesForUser(user)).stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public boolean hasMessages() {
        return this.withDescriptor(MessagesProviderModuleDescriptor::hasMessages).stream().anyMatch(Boolean.TRUE::equals);
    }

    public boolean hasMessages(UserProfile user) {
        return this.withDescriptor(descriptor -> descriptor.hasMessages(user)).stream().anyMatch(Boolean.TRUE::equals);
    }

    private <T> List<T> withDescriptor(Function<MessagesProviderModuleDescriptor, T> fn) {
        return SafePluginPointAccess.to((PluginAccessor)this.pluginAccessor).forType(MessagesProviderModuleDescriptor.class, (moduleDescriptor, provider) -> fn.apply((MessagesProviderModuleDescriptor)moduleDescriptor));
    }
}

