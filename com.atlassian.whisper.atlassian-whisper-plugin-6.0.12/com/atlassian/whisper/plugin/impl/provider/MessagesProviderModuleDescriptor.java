/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.ozymandias.SafePluginPointAccess
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.whisper.plugin.api.Message
 *  com.atlassian.whisper.plugin.api.MessagesProvider
 *  io.atlassian.fugue.Option
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.inject.Inject
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.whisper.plugin.impl.provider;

import com.atlassian.ozymandias.SafePluginPointAccess;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.whisper.plugin.api.Message;
import com.atlassian.whisper.plugin.api.MessagesProvider;
import io.atlassian.fugue.Option;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class MessagesProviderModuleDescriptor
extends AbstractModuleDescriptor<MessagesProvider>
implements MessagesProvider {
    private final ResettableLazyReference<Option<MessagesProvider>> moduleRef = new ResettableLazyReference<Option<MessagesProvider>>(){

        protected Option<MessagesProvider> create() throws Exception {
            return SafePluginPointAccess.call(() -> {
                if (StringUtils.isEmpty((CharSequence)MessagesProviderModuleDescriptor.this.moduleClassName)) {
                    return null;
                }
                return (MessagesProvider)MessagesProviderModuleDescriptor.this.moduleFactory.createModule(MessagesProviderModuleDescriptor.this.moduleClassName, (ModuleDescriptor)MessagesProviderModuleDescriptor.this);
            });
        }
    };

    @Inject
    public MessagesProviderModuleDescriptor(@ComponentImport ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public MessagesProvider getModule() {
        return (MessagesProvider)((Option)this.moduleRef.get()).getOrNull();
    }

    public Set<Message> getMessagesForUser(UserProfile user) {
        return this.withProvider(provider -> provider.getMessagesForUser(user), Collections.emptySet());
    }

    public Set<Message> getMessages() {
        return this.withProvider(MessagesProvider::getMessages, Collections.emptySet());
    }

    public boolean hasMessages() {
        return this.withProvider(MessagesProvider::hasMessages, false);
    }

    public boolean hasMessages(UserProfile user) {
        return this.withProvider(provider -> provider.hasMessages(user), false);
    }

    private <T> T withProvider(Function<MessagesProvider, T> fn, T empty) {
        return (T)SafePluginPointAccess.call(() -> Option.option((Object)this.getModule()).map(provider -> Option.option(fn.apply((MessagesProvider)provider)).getOrElse(empty)).getOrElse(empty)).getOrElse(empty);
    }
}

