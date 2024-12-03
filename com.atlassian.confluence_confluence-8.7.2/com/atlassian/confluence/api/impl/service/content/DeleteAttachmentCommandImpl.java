/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class DeleteAttachmentCommandImpl
implements ServiceCommand {
    private final AttachmentManager attachmentManager;
    private final Attachment attachment;
    private final Supplier<Boolean> isAuthorizedSupplier;
    private final Supplier<List<ValidationError>> validationErrorsSupplier;
    private boolean executed = false;

    public DeleteAttachmentCommandImpl(AttachmentManager attachmentManager, PermissionManager permissionManager, Attachment attachment) {
        this.attachmentManager = attachmentManager;
        this.attachment = attachment;
        this.isAuthorizedSupplier = Suppliers.memoize(() -> attachment == null || permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.REMOVE, attachment));
        this.validationErrorsSupplier = Suppliers.memoize(() -> {
            if (attachment == null) {
                return Lists.newArrayList((Object[])new ValidationError[]{new ValidationError("attachment.doesnt.exist", new Object[0])});
            }
            return Collections.emptyList();
        });
    }

    @Override
    public boolean isValid() {
        if (!this.isAuthorized()) {
            throw new NotAuthorizedException("Not authorized to trash attachments");
        }
        return this.getValidationErrors().isEmpty();
    }

    @Override
    public Collection<ValidationError> getValidationErrors() {
        return (Collection)this.validationErrorsSupplier.get();
    }

    @Override
    public boolean isAuthorized() {
        return (Boolean)this.isAuthorizedSupplier.get();
    }

    @Override
    public void execute() {
        if (!this.isValid()) {
            throw new NotValidException(DeleteAttachmentCommandImpl.class.getSimpleName() + " cannot be executed, it is invalid");
        }
        if (this.executed) {
            throw new IllegalStateException(DeleteAttachmentCommandImpl.class.getSimpleName() + " has been executed");
        }
        this.executed = true;
        this.attachmentManager.trash(this.attachment);
    }
}

