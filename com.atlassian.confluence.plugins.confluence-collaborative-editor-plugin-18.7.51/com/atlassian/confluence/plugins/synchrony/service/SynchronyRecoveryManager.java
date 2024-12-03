/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.service;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyDarkFeatureHelper;
import com.atlassian.confluence.plugins.synchrony.service.AbstractSynchronyManager;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyInternalDraftManager;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyJsonWebTokenGenerator;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyChangeRequest;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyRequestExecutor;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import java.util.Optional;
import java.util.function.Supplier;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchrony-recovery-manager")
public class SynchronyRecoveryManager
extends AbstractSynchronyManager {
    private static final Logger log = LoggerFactory.getLogger(SynchronyRecoveryManager.class);

    @Autowired
    public SynchronyRecoveryManager(@ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport FormatConverter formatConverter, @ComponentImport TransactionalExecutorFactory transactionalExecutorFactory, SynchronyRequestExecutor synchronyRequestExecutor, SynchronyDarkFeatureHelper synchronyDarkFeatureHelper, SynchronyJsonWebTokenGenerator synchronyJsonWebTokenGenerator, SynchronyInternalDraftManager draftManager) {
        super(pageManager, eventPublisher, formatConverter, transactionalExecutorFactory, draftManager, synchronyRequestExecutor, synchronyDarkFeatureHelper, synchronyJsonWebTokenGenerator);
    }

    boolean reconcile(long contentId, ConfluenceUser user) {
        return this.reconcile(contentId, user, false);
    }

    boolean reconcile(long contentId, ConfluenceUser user, boolean fetchPageContentInSeparateTransaction) {
        AbstractPage currentPage = this.getAbstractPage(contentId);
        ContentId currentPageId = currentPage.getContentId();
        AbstractPage draft = this.draftManager.findDraftFor(currentPage);
        Optional resetToCurrentRequest = fetchPageContentInSeparateTransaction ? (Optional)this.transactionalExecutorFactory.createReadOnly().newTransaction().execute(connection -> this.createSynchronyResetRequest(currentPage.getId(), user, () -> this.getAbstractPage(contentId))) : this.createSynchronyResetRequest(currentPage.getId(), user, () -> currentPage);
        this.synchronyRequestExecutor.execute((SynchronyChangeRequest)resetToCurrentRequest.orElseThrow(() -> new RuntimeException("Reconciliation error: cannot fetch page with id: " + contentId)), currentPageId).fold(error -> {
            throw new RuntimeException("Synchrony error: cannot reset page " + currentPageId);
        }, jsonResult -> {
            this.updateContent((JSONObject)jsonResult, currentPageId);
            return null;
        });
        if (draft != null) {
            Optional reconcileDraftRequest = fetchPageContentInSeparateTransaction ? (Optional)this.transactionalExecutorFactory.createReadOnly().newTransaction().execute(connection -> this.createReconciliationRequest(currentPage.getId(), user, () -> this.draftManager.findDraftFor(this.getAbstractPage(contentId)), currentPage.getSynchronyRevision())) : this.createReconciliationRequest(currentPage.getId(), user, () -> draft, currentPage.getSynchronyRevision());
            reconcileDraftRequest.ifPresent(request -> this.synchronyRequestExecutor.execute((SynchronyChangeRequest)request, draft.getContentId()).fold(error -> {
                throw new RuntimeException("Synchrony error: cannot reconcile draft " + draft.getContentId() + " for page " + currentPageId);
            }, jsonResult -> {
                this.updateContent((JSONObject)jsonResult, draft.getContentId());
                return null;
            }));
        }
        return true;
    }

    private AbstractPage getAbstractPage(long contentId) {
        AbstractPage currentPage = this.pageManager.getAbstractPage(contentId);
        if (currentPage.isDraft()) {
            currentPage = currentPage.getLatestVersion();
        }
        return currentPage;
    }

    private Optional<SynchronyChangeRequest> createSynchronyResetRequest(long contentId, ConfluenceUser user, Supplier<AbstractPage> currentPageSupplier) {
        try {
            AbstractPage currentPage = currentPageSupplier.get();
            return Optional.of(new SynchronyChangeRequest.Builder().url(this.synchronyRequestExecutor.getContentUrl(contentId)).token(this.synchronyJsonWebTokenGenerator.create(contentId, user)).html(SynchronyChangeRequest.Builder.createEditorDom(currentPage.getTitle(), this.formatConverter.convertToEditorFormatWithResult(currentPage.getBodyAsString(), (RenderContext)currentPage.toPageContext()))).generateRev(Boolean.TRUE.toString()).generateReset(true).merges(user, currentPage.getVersion(), "external", currentPage.getSynchronyRevisionSource()).build());
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<SynchronyChangeRequest> createReconciliationRequest(long contentId, ConfluenceUser user, Supplier<AbstractPage> contentSupplier, String ancestorSyncRev) {
        try {
            AbstractPage content = contentSupplier.get();
            return Optional.of(new SynchronyChangeRequest.Builder().url(this.synchronyRequestExecutor.getContentUrl(contentId)).token(this.synchronyJsonWebTokenGenerator.create(contentId, user)).html(SynchronyChangeRequest.Builder.createEditorDom(content.getTitle(), this.formatConverter.convertToEditorFormatWithResult(content.getBodyAsString(), (RenderContext)content.toPageContext()))).generateRev(Boolean.TRUE.toString()).ancestor(ancestorSyncRev).merges(user, content.getVersion(), "external", "server-reconciliation").build());
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }
}

