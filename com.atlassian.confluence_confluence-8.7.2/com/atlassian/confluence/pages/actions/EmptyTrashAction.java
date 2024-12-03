/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  org.springframework.orm.ObjectOptimisticLockingFailureException
 *  org.springframework.transaction.UnexpectedRollbackException
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.event.events.space.SpaceTrashPurgeAllContentEvent;
import com.atlassian.confluence.pages.actions.ViewTrashAction;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.UnexpectedRollbackException;

public class EmptyTrashAction
extends ViewTrashAction {
    @Override
    public String doDefault() throws Exception {
        return "input";
    }

    public String execute() throws Exception {
        try {
            int numberOfContent = this.getTrashManager().getNumberOfItemsInTrash(this.getSpace());
            this.getTrashManager().emptyTrash(this.getSpace());
            this.publishEvent(numberOfContent);
        }
        catch (ConflictException | ObjectOptimisticLockingFailureException | UnexpectedRollbackException e) {
            this.addActionError(this.getText("empty.trash.concurrent.removal.error"));
            return "error";
        }
        return "success";
    }

    private void publishEvent(int numberOfContent) {
        this.eventPublisher.publish((Object)new SpaceTrashPurgeAllContentEvent(this, this.getSpace(), numberOfContent));
    }
}

