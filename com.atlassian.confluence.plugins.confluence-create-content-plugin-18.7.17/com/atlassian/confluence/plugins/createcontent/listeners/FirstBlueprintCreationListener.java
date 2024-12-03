/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.xwork.FlashScope
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.listeners;

import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.plugins.createcontent.extensions.UserBlueprintConfigManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.xwork.FlashScope;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FirstBlueprintCreationListener {
    private final UserBlueprintConfigManager userBlueprintConfigManager;
    private final EventPublisher eventPublisher;

    @Autowired
    public FirstBlueprintCreationListener(UserBlueprintConfigManager userBlueprintConfigManager, @ComponentImport EventPublisher eventPublisher) {
        this.userBlueprintConfigManager = userBlueprintConfigManager;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onBlueprintCreateEvent(BlueprintPageCreateEvent event) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            return;
        }
        UUID id = event.getBlueprint().getId();
        if (this.userBlueprintConfigManager.isFirstBlueprintOfTypeForUser(id, user)) {
            this.userBlueprintConfigManager.setBlueprintCreatedByUser(id, user);
            FlashScope.put((String)"firstBlueprintForUser", (Object)id);
            FlashScope.put((String)"com.atlassian.confluence.plugins.confluence-create-content-plugin.blueprint-index-disabled", (Object)event.getBlueprint().isIndexDisabled());
        }
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }
}

