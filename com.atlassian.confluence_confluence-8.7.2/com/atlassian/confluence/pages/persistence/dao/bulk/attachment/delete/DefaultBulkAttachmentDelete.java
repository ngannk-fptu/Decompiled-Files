/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.impl.hibernate.bulk.BulkTransaction
 *  com.atlassian.confluence.impl.hibernate.bulk.HibernateBulkTransaction
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.LockMode
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete;

import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.impl.hibernate.bulk.BulkAction;
import com.atlassian.confluence.impl.hibernate.bulk.BulkActionReportAware;
import com.atlassian.confluence.impl.hibernate.bulk.BulkTransaction;
import com.atlassian.confluence.impl.hibernate.bulk.HibernateBulkTransaction;
import com.atlassian.confluence.impl.hibernate.bulk.RecursiveHibernateBulkAction;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentDeleteOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.BulkAttachmentDelete;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.BulkAttachmentDeleteContext;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.ContainerAttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.IdListAttachmentDeleteOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers.AllLatestVersionAttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers.AttachmentRefIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers.DefaultAttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers.IdListAttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers.LatestVersionWithNameMineTypeAttachmentIdentifier;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.LogProgressMeterWrapper;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBulkAttachmentDelete
implements BulkAttachmentDelete {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBulkAttachmentDelete.class);
    private static final int PERCENTAGE_COMPLETE = 100;
    private final PageManager pageManager;
    private final AttachmentManager attachmentManager;
    private final SessionFactory sessionFactory;
    private final PermissionManager permissionManager;

    public DefaultBulkAttachmentDelete(PageManager pageManager, AttachmentManager attachmentManager, SessionFactory sessionFactory, PermissionManager permissionManager) {
        this.pageManager = pageManager;
        this.attachmentManager = attachmentManager;
        this.sessionFactory = sessionFactory;
        this.permissionManager = permissionManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deepDelete(AttachmentDeleteOptions attachmentDeleteOptions) {
        Objects.requireNonNull(attachmentDeleteOptions, "AttachmentDeleteOptions should not be null");
        ConfluenceUser user = attachmentDeleteOptions.getUser();
        ConfluenceUser lastLoggedInUser = AuthenticatedUserThreadLocal.get();
        ProgressMeter progressMeter = attachmentDeleteOptions.getProgressMeter();
        LogProgressMeterWrapper logProgressMeter = new LogProgressMeterWrapper(progressMeter);
        try {
            AuthenticatedUserThreadLocal.set(user);
            AttachmentDeleteOptions deleteOptions = this.validatePermissions(attachmentDeleteOptions);
            if (deleteOptions.getAttachmentRefs().isEmpty() && !deleteOptions.getAttachmentRefs().isEmpty()) {
                logger.warn("Validation for bulk delete attachment is failed");
                return;
            }
            AttachmentIdentifier attachmentIdentifier = this.getAttachmentIdentifier(attachmentDeleteOptions, deleteOptions);
            HibernateBulkTransaction bulkTransaction = new HibernateBulkTransaction(this.sessionFactory);
            BulkAttachmentDeleteContext context = new BulkAttachmentDeleteContext();
            RecursiveHibernateBulkAction<BulkAttachmentDeleteContext, AttachmentIdentifier> bulkAction = new RecursiveHibernateBulkAction<BulkAttachmentDeleteContext, AttachmentIdentifier>((BulkTransaction)bulkTransaction, logProgressMeter, deleteOptions.getBatchSize(), deleteOptions.getMaxProcessedEntries());
            bulkAction.execute(context, attachmentIdentifier, new BulkAttachmentDeleteAction());
            logProgressMeter.setPercentage(100);
        }
        finally {
            AuthenticatedUserThreadLocal.set(lastLoggedInUser);
        }
    }

    private AttachmentIdentifier getAttachmentIdentifier(AttachmentDeleteOptions attachmentDeleteOptions, AttachmentDeleteOptions deleteOptions) {
        ContainerAttachmentIdentifier attachmentIdentifier;
        if (deleteOptions instanceof IdListAttachmentDeleteOptions) {
            attachmentIdentifier = new IdListAttachmentIdentifier(((IdListAttachmentDeleteOptions)deleteOptions).getIds(), this.attachmentManager);
        } else {
            ContainerAttachmentIdentifier containerAttachmentIdentifier = attachmentIdentifier = deleteOptions.isLatestVersion() ? new LatestVersionWithNameMineTypeAttachmentIdentifier(this.pageManager, this.attachmentManager, deleteOptions.getAttachmentContainerId(), new ArrayList<AttachmentDeleteOptions.AttachmentRef>(attachmentDeleteOptions.getAttachmentRefs())) : new AttachmentRefIdentifier(deleteOptions.getAttachmentContainerId(), this.pageManager, this.attachmentManager, new ArrayList<AttachmentDeleteOptions.AttachmentRef>(deleteOptions.getAttachmentRefs())){};
            if (deleteOptions.isLatestVersion() && deleteOptions.getAttachmentRefs().isEmpty()) {
                attachmentIdentifier = new AllLatestVersionAttachmentIdentifier(this.pageManager, this.attachmentManager, deleteOptions.getAttachmentContainerId());
            }
        }
        return attachmentIdentifier;
    }

    private AttachmentDeleteOptions validatePermissions(AttachmentDeleteOptions attachmentDeleteOptions) {
        if (attachmentDeleteOptions instanceof IdListAttachmentDeleteOptions) {
            return attachmentDeleteOptions;
        }
        AbstractPage container = this.pageManager.getAbstractPage(attachmentDeleteOptions.getAttachmentContainerId());
        if (container == null) {
            throw new PermissionException("Missing container for bulk deleting attachments");
        }
        ConfluenceUser confluenceUser = attachmentDeleteOptions.getUser();
        if (!this.permissionManager.hasPermission((User)confluenceUser, Permission.EDIT, container)) {
            throw new PermissionException(String.format("Could not execute bulk delete attachment because the user %s doesn't have EDIT permission on original page", confluenceUser == null ? "Anonymous" : confluenceUser.getName()));
        }
        List filteredAttachmentRefs = attachmentDeleteOptions.getAttachmentRefs().stream().filter(attachmentRef -> {
            Attachment attachment = this.attachmentManager.getAttachment(container, attachmentRef.getAttachmentName(), attachmentRef.getAttachmentVersion() == -1 ? 0 : attachmentRef.getAttachmentVersion());
            return this.permissionManager.hasPermission((User)confluenceUser, Permission.REMOVE, attachment);
        }).collect(Collectors.toList());
        AttachmentDeleteOptions.AttachmentDeleteOptionsBuilder validatedOptions = AttachmentDeleteOptions.get().withContainerId(attachmentDeleteOptions.getAttachmentContainerId());
        validatedOptions.withDefaultOptions(attachmentDeleteOptions);
        filteredAttachmentRefs.stream().forEach(attachmentRef -> validatedOptions.withAttachmentBy(attachmentRef.getAttachmentName(), attachmentRef.getAttachmentVersion(), attachmentRef.getMimeType()));
        return validatedOptions.build();
    }

    public class BulkAttachmentDeleteAction
    implements BulkAction<BulkAttachmentDeleteContext, AttachmentIdentifier>,
    BulkActionReportAware {
        private AttachmentIdentifier attachmentIdentifier;
        private int totalDeletedItem;

        @Override
        public @NonNull BulkAction.Result<BulkAttachmentDeleteContext, AttachmentIdentifier> process(BulkAttachmentDeleteContext context, AttachmentIdentifier attachmentIdentifier) {
            this.attachmentIdentifier = attachmentIdentifier;
            if (attachmentIdentifier instanceof ContainerAttachmentIdentifier) {
                ContainerAttachmentIdentifier containerAttachmentIdentified = (ContainerAttachmentIdentifier)attachmentIdentifier;
                List<AttachmentIdentifier> attachmentIdentifiers = containerAttachmentIdentified.getAttachmentIdentifiedList();
                return new BulkAction.Result<BulkAttachmentDeleteContext, AttachmentIdentifier>(context, attachmentIdentifiers, true);
            }
            DefaultAttachmentIdentifier defaultAttachmentIdentified = (DefaultAttachmentIdentifier)attachmentIdentifier;
            Attachment attachment = defaultAttachmentIdentified.getAttachment();
            DefaultBulkAttachmentDelete.this.sessionFactory.getCurrentSession().refresh((Object)attachment, LockMode.NONE);
            DefaultBulkAttachmentDelete.this.attachmentManager.trash(attachment);
            return new BulkAction.Result<BulkAttachmentDeleteContext, AttachmentIdentifier>(context, Collections.emptyList(), false);
        }

        @Override
        public void report(ProgressMeter progressMeter, int processedEntities, int actionedEntities, int maxProcessedDepthLevel) {
            if (this.attachmentIdentifier == null) {
                return;
            }
            if (this.attachmentIdentifier instanceof ContainerAttachmentIdentifier) {
                int totalItemCount = ((ContainerAttachmentIdentifier)this.attachmentIdentifier).getTotalCountLatestAttachment();
                progressMeter.setTotalObjects(totalItemCount);
                return;
            }
            progressMeter.setCurrentCount(++this.totalDeletedItem);
        }
    }
}

