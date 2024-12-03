/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.service;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyDarkFeatureHelper;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyInternalDraftManager;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyJsonWebTokenGenerator;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyRecoveryManager;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyUUIDManager;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyRequestExecutor;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="confluence-recovery-manager")
public class ConfluenceRecoveryManager
extends SynchronyRecoveryManager {
    private final SynchronyUUIDManager synchronyUuidManager;

    @Autowired
    public ConfluenceRecoveryManager(@ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport FormatConverter formatConverter, @ComponentImport TransactionalExecutorFactory transactionalExecutorFactory, SynchronyRequestExecutor synchronyRequestExecutor, SynchronyDarkFeatureHelper synchronyDarkFeatureHelper, SynchronyJsonWebTokenGenerator synchronyJsonWebTokenGenerator, SynchronyInternalDraftManager draftManager, SynchronyUUIDManager synchronyUuidManager) {
        super(pageManager, eventPublisher, formatConverter, transactionalExecutorFactory, synchronyRequestExecutor, synchronyDarkFeatureHelper, synchronyJsonWebTokenGenerator, draftManager);
        this.synchronyUuidManager = synchronyUuidManager;
    }

    @Override
    boolean reconcile(long contentId, ConfluenceUser user) {
        AbstractPage content = this.pageManager.getAbstractPage(contentId);
        return this.reconcile(content, AuthenticatedUserThreadLocal.get());
    }

    boolean reconcile(long contentId, ConfluenceUser user, String conflictingRev) {
        AbstractPage content = this.pageManager.getAbstractPage(contentId);
        if (this.isConfluenceOutdated(content.getConfluenceRevision(), conflictingRev)) {
            return this.reconcile(content, user);
        }
        return true;
    }

    private boolean reconcile(AbstractPage content, ConfluenceUser user) {
        AbstractPage page;
        AbstractPage abstractPage = page = content.isDraft() ? content.getLatestVersion() : content;
        if (this.synchronyUuidManager.getGlobalUuid().equals(page.getCollaborativeEditingUuid())) {
            return true;
        }
        super.reconcile(page.getId(), user);
        page.setCollaborativeEditingUuid(this.synchronyUuidManager.getGlobalUuid());
        return true;
    }
}

