/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.velocity;

import com.atlassian.confluence.api.impl.service.people.ReadOnlyPersonService;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebInterfaceManager;
import com.atlassian.confluence.impl.settings.ReadOnlySpaceSettingsManager;
import com.atlassian.confluence.impl.setup.BootstrapStatusProviderImpl;
import com.atlassian.confluence.internal.user.UserAccessorInternal;
import com.atlassian.confluence.setup.settings.SpaceSettingsManager;
import com.atlassian.confluence.setup.velocity.VelocityContextItemProvider;
import com.atlassian.confluence.user.ReadOnlyUserAccessor;
import com.atlassian.plugin.web.WebInterfaceManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ReadOnlyBeanContextItemProvider
implements VelocityContextItemProvider {
    private final Map<String, Object> contextMap;
    private final SpaceSettingsManager spaceSettingsManager;
    private final UserAccessorInternal userAccessor;
    private final PersonService apiPersonService;
    private final WebInterfaceManager webInterfaceManager;

    public ReadOnlyBeanContextItemProvider(SpaceSettingsManager spaceSettingsManager, UserAccessorInternal userAccessor, PersonService personService, WebInterfaceManager webInterfaceManager) {
        this.spaceSettingsManager = spaceSettingsManager;
        this.userAccessor = userAccessor;
        this.apiPersonService = personService;
        this.webInterfaceManager = webInterfaceManager;
        this.contextMap = this.constructContextMap();
    }

    private Map<String, Object> constructContextMap() {
        HashMap<String, Object> newContextMap = new HashMap<String, Object>();
        for (ContextItems item : ContextItems.values()) {
            newContextMap.put(item.getKey(), item.getItem(this));
        }
        return Collections.unmodifiableMap(newContextMap);
    }

    @Override
    public @NonNull Map<String, Object> getContextMap() {
        return this.contextMap;
    }

    public static enum ContextItems {
        BOOTSTRAP_STATUS("bootstrap", provider -> BootstrapStatusProviderImpl.getInstance()),
        CONFLUENCE_SETUP("setup", provider -> BootstrapStatusProviderImpl.getInstance()),
        APPLICATION_CONFIG("applicationConfig", provider -> BootstrapStatusProviderImpl.getInstance().getApplicationConfig()),
        SETUP_PERSISTER("setupPersister", provider -> BootstrapStatusProviderImpl.getInstance().getSetupPersister()),
        SPACE_SETTINGS("spaceSettingsManager", provider -> new ReadOnlySpaceSettingsManager(provider.spaceSettingsManager)),
        USER_ACCESSOR("userAccessor", provider -> new ReadOnlyUserAccessor(provider.userAccessor)),
        PERSON_SERVICE("personService", provider -> new ReadOnlyPersonService(provider.apiPersonService)),
        WEB_INTERFACE_MANAGER("webInterfaceManager", provider -> new ReadOnlyWebInterfaceManager(provider.webInterfaceManager));

        private final String key;
        private final transient Function<ReadOnlyBeanContextItemProvider, Object> itemRef;

        private ContextItems(String key, Function<ReadOnlyBeanContextItemProvider, Object> itemRef) {
            this.key = key;
            this.itemRef = itemRef;
        }

        public String getKey() {
            return this.key;
        }

        Object getItem(ReadOnlyBeanContextItemProvider provider) {
            return this.itemRef.apply(provider);
        }
    }
}

