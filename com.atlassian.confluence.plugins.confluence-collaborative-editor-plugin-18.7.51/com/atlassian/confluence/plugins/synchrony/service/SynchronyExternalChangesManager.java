/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.event.events.content.page.synchrony.SynchronyRecoveryEvent
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.service;

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.event.events.content.page.synchrony.SynchronyRecoveryEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyDarkFeatureHelper;
import com.atlassian.confluence.plugins.synchrony.model.SynchronyError;
import com.atlassian.confluence.plugins.synchrony.service.AbstractSynchronyManager;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyInternalDraftManager;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyJsonWebTokenGenerator;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyChangeRequest;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyRequestExecutor;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchrony-external-changes-manager")
@ParametersAreNonnullByDefault
public class SynchronyExternalChangesManager
extends AbstractSynchronyManager {
    protected static final Logger log = LoggerFactory.getLogger(SynchronyExternalChangesManager.class);

    @Autowired
    public SynchronyExternalChangesManager(@ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport FormatConverter formatConverter, @ComponentImport TransactionalExecutorFactory transactionalExecutorFactory, SynchronyInternalDraftManager draftManager, SynchronyRequestExecutor synchronyRequestExecutor, SynchronyDarkFeatureHelper synchronyDarkFeatureHelper, SynchronyJsonWebTokenGenerator synchronyJsonWebTokenGenerator) {
        super(pageManager, eventPublisher, formatConverter, transactionalExecutorFactory, draftManager, synchronyRequestExecutor, synchronyDarkFeatureHelper, synchronyJsonWebTokenGenerator);
    }

    void syncContentOnUpdate(ContentId contentId, ContentStatus contentStatus, String spaceKey, ConfluenceUser user, PageUpdateTrigger updateTrigger) {
        if (this.synchronyEnabled(spaceKey) && ContentStatus.CURRENT.equals((Object)contentStatus) && updateTrigger != PageUpdateTrigger.SPACE_CREATE) {
            this.performExternalChange(user, contentId, updateTrigger);
        }
    }

    Either<SynchronyError, JSONObject> performExternalChange(ConfluenceUser user, ContentId contentId, PageUpdateTrigger pageUpdateTrigger) {
        log.info("Started external change for {}", (Object)contentId);
        Optional<SynchronyChangeRequest> request = this.createExternalChangeRequest(user, contentId, pageUpdateTrigger);
        if (!request.isPresent()) {
            return Either.left((Object)SynchronyError.ERROR_CREATING_REQUEST);
        }
        Either<SynchronyError, JSONObject> result = this.execute(request.get(), contentId);
        this.verifyResultForRecovery(contentId, user, result, pageUpdateTrigger != PageUpdateTrigger.DISCARD_CHANGES);
        return result;
    }

    private Optional<SynchronyChangeRequest> createExternalChangeRequest(ConfluenceUser user, ContentId contentId, PageUpdateTrigger pageUpdateTrigger) {
        return (Optional)this.transactionalExecutorFactory.createReadOnly().execute(connection -> {
            try {
                AbstractPage content = this.pageManager.getAbstractPage(contentId.asLong());
                if (content != null) {
                    SynchronyChangeRequest.Builder requestBuilder = new SynchronyChangeRequest.Builder().url(this.synchronyRequestExecutor.getContentUrlWithStateQuery(contentId.asLong())).token(this.synchronyJsonWebTokenGenerator.create(contentId.asLong(), user)).html(SynchronyChangeRequest.Builder.createEditorDom(content.getTitle(), this.formatConverter.convertToEditorFormatWithResult(content.getBodyAsString(), (RenderContext)content.toPageContext())));
                    if (pageUpdateTrigger == PageUpdateTrigger.REVERT) {
                        requestBuilder.rev(content.getConfluenceRevision()).generateReset(true).merges(user, content.getVersion(), "external", "reset");
                    } else if (pageUpdateTrigger == PageUpdateTrigger.DISCARD_CHANGES) {
                        requestBuilder.generateRev(Boolean.TRUE.toString()).generateReset(true).merges(user, content.getVersion(), "external", "reset");
                    } else {
                        String synchronyRev = content.getSynchronyRevision();
                        if (StringUtils.isEmpty((CharSequence)synchronyRev)) {
                            log.debug("External change request with empty Synchrony Rev with content {}", (Object)contentId.asLong());
                        }
                        String trigger = pageUpdateTrigger == PageUpdateTrigger.EDIT_PAGE ? "publish" : "other";
                        requestBuilder.rev(content.getConfluenceRevision()).ancestor(content.getSynchronyRevision()).merges(user, content.getVersion(), "external", trigger);
                    }
                    return Optional.of(requestBuilder.build());
                }
            }
            catch (Exception e) {
                log.error("Failed to create external change request: {}", (Object)e.getMessage());
                log.debug("Full stack trace", (Throwable)e);
            }
            return Optional.empty();
        });
    }

    private void verifyResultForRecovery(ContentId contentId, ConfluenceUser user, Either<SynchronyError, JSONObject> result, boolean pageModified) {
        if (result.isLeft()) {
            String syncRevSource;
            SynchronyError error = (SynchronyError)result.left().get();
            if (error.getCode() == SynchronyError.Code.INVALID_ANCESTOR) {
                syncRevSource = pageModified ? "synchrony-recovery-with-external-change" : "synchrony-recovery";
                this.setSyncRevStateForPage(contentId, syncRevSource);
                this.eventPublisher.publish((Object)new SynchronyRecoveryEvent(user, contentId, syncRevSource));
            }
            if (error.getCode() == SynchronyError.Code.OUT_OF_ORDER_REVISION && this.isConfluenceOutdated(this.getCurrentConfRev(contentId), error.getConflictingRev())) {
                syncRevSource = pageModified ? "confluence-recovery-with-external-change" : "confluence-recovery";
                this.setSyncRevStateForPage(contentId, syncRevSource);
                this.eventPublisher.publish((Object)new SynchronyRecoveryEvent(user, contentId, syncRevSource));
            }
        }
    }

    private void setSyncRevStateForPage(ContentId contentId, String state) {
        try {
            this.transactionalExecutorFactory.create().execute(transactionStatus -> {
                AbstractPage page = this.pageManager.getAbstractPage(contentId.asLong());
                page.setSynchronyRevisionSource(state);
                return null;
            });
        }
        catch (Exception e) {
            log.error("Failed to set sync rev source for page {}", (Object)contentId.asLong());
        }
    }

    private String getCurrentConfRev(ContentId contentId) {
        try {
            return (String)this.transactionalExecutorFactory.createReadOnly().execute(transactionStatus -> {
                AbstractPage page = this.pageManager.getAbstractPage(contentId.asLong());
                return page.getConfluenceRevision();
            });
        }
        catch (Exception e) {
            log.error("Failed to get conf rev for page {}", (Object)contentId.asLong());
            return null;
        }
    }
}

