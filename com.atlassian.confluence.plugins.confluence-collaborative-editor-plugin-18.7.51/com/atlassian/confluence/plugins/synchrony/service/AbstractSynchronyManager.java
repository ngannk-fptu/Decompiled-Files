/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.google.common.base.Throwables
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.synchrony.service;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyDarkFeatureHelper;
import com.atlassian.confluence.plugins.synchrony.model.SynchronyError;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyInternalDraftManager;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyJsonWebTokenGenerator;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyChangeRequest;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyRequestExecutor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.google.common.base.Throwables;
import java.util.Optional;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractSynchronyManager {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSynchronyManager.class);
    final PageManager pageManager;
    final EventPublisher eventPublisher;
    final FormatConverter formatConverter;
    final TransactionalExecutorFactory transactionalExecutorFactory;
    final SynchronyInternalDraftManager draftManager;
    final SynchronyRequestExecutor synchronyRequestExecutor;
    final SynchronyDarkFeatureHelper synchronyDarkFeatureHelper;
    final SynchronyJsonWebTokenGenerator synchronyJsonWebTokenGenerator;

    AbstractSynchronyManager(PageManager pageManager, EventPublisher eventPublisher, FormatConverter formatConverter, TransactionalExecutorFactory transactionalExecutorFactory, SynchronyInternalDraftManager draftManager, SynchronyRequestExecutor synchronyRequestExecutor, SynchronyDarkFeatureHelper synchronyDarkFeatureHelper, SynchronyJsonWebTokenGenerator synchronyJsonWebTokenGenerator) {
        this.pageManager = pageManager;
        this.eventPublisher = eventPublisher;
        this.formatConverter = formatConverter;
        this.transactionalExecutorFactory = transactionalExecutorFactory;
        this.draftManager = draftManager;
        this.synchronyRequestExecutor = synchronyRequestExecutor;
        this.synchronyDarkFeatureHelper = synchronyDarkFeatureHelper;
        this.synchronyJsonWebTokenGenerator = synchronyJsonWebTokenGenerator;
    }

    boolean synchronyEnabled(String spaceKey) {
        return this.synchronyDarkFeatureHelper.isSynchronyFeatureEnabled(spaceKey);
    }

    Either<SynchronyError, JSONObject> execute(SynchronyChangeRequest request, ContentId contentId) {
        Either<SynchronyError, JSONObject> result = this.synchronyRequestExecutor.execute(request, contentId);
        if (result.isRight()) {
            this.updateContent((JSONObject)result.right().get(), contentId);
        }
        return result;
    }

    boolean isConfluenceOutdated(String currentConfRev, String conflictingRev) {
        Optional<SynchronyRev> optCurrent = SynchronyRev.from(currentConfRev);
        Optional<SynchronyRev> optConflicting = SynchronyRev.from(conflictingRev);
        if (!optCurrent.isPresent() || !optConflicting.isPresent()) {
            return false;
        }
        SynchronyRev current = optCurrent.get();
        SynchronyRev conflicting = optConflicting.get();
        return current.pid.equals(conflicting.pid) && current.seq.compareTo(conflicting.seq) < 0;
    }

    void updateContent(JSONObject result, ContentId contentId) {
        this.transactionalExecutorFactory.create().execute(connection -> {
            AbstractPage contentToUpdate = this.pageManager.getAbstractPage(contentId.asLong());
            if (contentToUpdate != null) {
                AbstractPage draft;
                JSONObject state;
                String responseRev = result.get("rev").toString();
                String contentSyncRev = StringUtils.defaultString((String)contentToUpdate.getSynchronyRevision(), (String)"");
                if (contentSyncRev.equals(responseRev)) {
                    logger.warn("Duplicated Rev has detected by Synchrony");
                }
                contentToUpdate.setSynchronyRevision(responseRev);
                contentToUpdate.setSynchronyRevisionSource("synchrony-ack");
                if (result.containsKey("stateAt") && result.containsKey("state") && "html".equals((state = (JSONObject)result.get("state")).get("format").toString()) && (draft = this.draftManager.findDraftFor(contentToUpdate)) != null) {
                    try {
                        String storage = this.formatConverter.convertToStorageFormat(state.get("value").toString(), (RenderContext)draft.toPageContext());
                        draft.setBodyAsString(storage);
                        draft.setSynchronyRevision(result.get("stateAt").toString());
                        draft.setSynchronyRevisionSource("synchrony-ack");
                    }
                    catch (XhtmlException e) {
                        throw Throwables.propagate((Throwable)e);
                    }
                }
            }
            return null;
        });
    }

    static class SynchronyRev {
        String pid;
        Long seq;

        SynchronyRev(String pid, Long seq) {
            this.pid = pid;
            this.seq = seq;
        }

        static Optional<SynchronyRev> from(String rev) {
            try {
                String[] split = rev.split("\\.");
                int last = split.length - 1;
                return Optional.of(new SynchronyRev(split[last - 1], Long.parseLong(split[last])));
            }
            catch (Exception e) {
                return Optional.empty();
            }
        }
    }
}

