/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.content.service.experimental;

import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.service.experimental.PageUpdateService;
import com.atlassian.confluence.content.service.experimental.PreparedAbstractPage;
import com.atlassian.confluence.core.Modification;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.internal.content.collab.ContentReconciliationManager;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultPageUpdateService
implements PageUpdateService {
    private final PageManagerInternal pageManager;
    private final TransactionTemplate transactionTemplate;
    private final ContentReconciliationManager contentReconciliationManager;

    public DefaultPageUpdateService(PageManagerInternal pageManager, TransactionTemplate transactionTemplate, ContentReconciliationManager contentReconciliationManager) {
        this.pageManager = pageManager;
        this.transactionTemplate = transactionTemplate;
        this.contentReconciliationManager = contentReconciliationManager;
    }

    @Override
    public PreparedAbstractPage prepare(long id, Modification<AbstractPage> modification, @Nullable SaveContext saveContext) {
        try {
            this.transactionTemplate.execute(() -> {
                Page pageOrDraft = this.pageManager.getPage(id);
                if (pageOrDraft == null) {
                    throw new NotFoundException("Page or blogpost with given id " + id + " is not found");
                }
                this.contentReconciliationManager.reconcileIfNeeded(pageOrDraft, saveContext);
                return null;
            });
            return new PreparedAbstractPage(id, modification, saveContext);
        }
        catch (ServiceException se) {
            throw se;
        }
        catch (Exception e) {
            throw new ServiceException((Throwable)e);
        }
    }

    @Override
    public void update(PreparedAbstractPage preparedAbstractPage) {
        try {
            this.transactionTemplate.execute(() -> {
                long contentId = preparedAbstractPage.getContentId();
                Page current = this.pageManager.getPage(contentId);
                if (current == null) {
                    throw new NotFoundException("Page or blogpost with given id " + contentId + " is not found");
                }
                this.pageManager.saveNewVersion(current, preparedAbstractPage.getModification(), preparedAbstractPage.getSaveContext());
                return null;
            });
        }
        catch (ServiceException se) {
            throw se;
        }
        catch (Exception e) {
            throw new ServiceException((Throwable)e);
        }
    }
}

