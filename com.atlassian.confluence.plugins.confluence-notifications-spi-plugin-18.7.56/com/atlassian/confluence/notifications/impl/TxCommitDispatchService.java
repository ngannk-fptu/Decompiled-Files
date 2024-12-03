/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.SynchronizationManager
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.notifications.DispatchService;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;

final class TxCommitDispatchService
implements DispatchService {
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forClass(TxCommitDispatchService.class);
    private final SynchronizationManager synchronizationManager;
    private final DispatchService delegate;

    public TxCommitDispatchService(DispatchService delegate, SynchronizationManager synchronizationManager) {
        this.synchronizationManager = synchronizationManager;
        this.delegate = delegate;
    }

    @Override
    public void dispatch(Notification notification) {
        this.afterTxSubmit(notification, () -> this.delegate.dispatch(notification));
    }

    @Override
    public void dispatchWithAdditionalRecipients(Notification notification, Iterable<RoleRecipient> additionalRecipients) {
        this.afterTxSubmit(notification, () -> this.delegate.dispatchWithAdditionalRecipients(notification, additionalRecipients));
    }

    @Override
    public void dispatchForExclusiveRecipients(Notification notification, Iterable<RoleRecipient> exclusiveRecipients) {
        this.afterTxSubmit(notification, () -> this.delegate.dispatchForExclusiveRecipients(notification, exclusiveRecipients));
    }

    private void afterTxSubmit(Notification<?> notification, Runnable afterCommitHook) {
        if (this.synchronizationManager.isTransactionActive()) {
            log.onlyTrace("Registering transaction post-commit hook for dispatch of notification [%s]", notification);
            this.synchronizationManager.runOnSuccessfulCommit(afterCommitHook);
        } else {
            log.onlyTrace("No transaction active - directly dispatching notification [%s]", notification);
            afterCommitHook.run();
        }
    }
}

