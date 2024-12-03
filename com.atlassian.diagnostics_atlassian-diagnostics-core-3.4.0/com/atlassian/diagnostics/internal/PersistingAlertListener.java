/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertListener
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertListener;
import com.atlassian.diagnostics.internal.dao.AlertEntityDao;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Objects;
import javax.annotation.Nonnull;

public class PersistingAlertListener
implements AlertListener {
    private final AlertEntityDao dao;
    private final TransactionTemplate transactionTemplate;

    public PersistingAlertListener(@Nonnull AlertEntityDao dao, @Nonnull TransactionTemplate transactionTemplate) {
        this.dao = Objects.requireNonNull(dao, "dao");
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate");
    }

    public void onAlert(@Nonnull Alert alert) {
        this.transactionTemplate.execute(() -> {
            this.dao.save(alert);
            return null;
        });
    }
}

