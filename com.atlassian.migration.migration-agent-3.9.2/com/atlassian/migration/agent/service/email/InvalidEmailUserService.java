/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.email.EmailData
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.cmpt.check.email.EmailData;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.agent.entity.InvalidEmailUser;
import com.atlassian.migration.agent.store.InvalidEmailUserStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.util.List;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class InvalidEmailUserService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(InvalidEmailUserService.class);
    private final InvalidEmailUserStore invalidEmailUserStore;
    private final PluginTransactionTemplate ptx;
    private final EventPublisher eventPublisher;

    public InvalidEmailUserService(InvalidEmailUserStore invalidEmailUserStore, PluginTransactionTemplate ptx, EventPublisher eventPublisher) {
        this.invalidEmailUserStore = invalidEmailUserStore;
        this.ptx = ptx;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void postConstruct() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public void saveInvalidEmailUsers(List<EmailData> retrieveInvalidEmails) {
        retrieveInvalidEmails.stream().map(emailData -> new InvalidEmailUser(emailData.id, emailData.email)).forEach(this::saveIfNotExists);
    }

    private void saveIfNotExists(InvalidEmailUser invalidEmailUser) {
        this.ptx.write(() -> {
            if (!this.invalidEmailUserStore.findByUserName(invalidEmailUser.getUserName()).isPresent()) {
                this.invalidEmailUserStore.saveInvalidEmailUserOrIgnoreIfExists(invalidEmailUser);
            }
        });
    }

    public Set<String> findAllUserNamesOfInvalidEmailUsers() {
        return this.invalidEmailUserStore.findAllUserNamesOfInvalidEmailUsers();
    }

    @EventListener
    public void handleInvalidEmailUserFeatureDisable(SiteDarkFeatureDisabledEvent event) {
        if (event.getFeatureKey().equals("migration-assistant.handle-invalid-duplicate-email-users")) {
            this.ptx.write(this.invalidEmailUserStore::deleteAll);
            log.info("Invalid email user handling dark feature was disabled.");
        }
    }
}

