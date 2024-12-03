/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.plugins.whitelist.WhitelistManager
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 */
package com.atlassian.plugins.whitelist.core.migration;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.plugins.whitelist.WhitelistManager;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.core.applinks.ApplicationLinkWhitelistRule;
import com.atlassian.plugins.whitelist.core.migration.AbstractWhitelistPluginUpgradeTask;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Collections;

public class ApplicationLinksMigration
extends AbstractWhitelistPluginUpgradeTask {
    private final ReadOnlyApplicationLinkService applicationLinkService;
    private final WhitelistManager whitelistManager;
    private final TransactionTemplate transactionTemplate;

    public ApplicationLinksMigration(ReadOnlyApplicationLinkService applicationLinkService, WhitelistManager whitelistManager, TransactionTemplate transactionTemplate) {
        this.applicationLinkService = applicationLinkService;
        this.whitelistManager = whitelistManager;
        this.transactionTemplate = transactionTemplate;
    }

    public int getBuildNumber() {
        return 1;
    }

    public String getShortDescription() {
        return "Migrate existing application links to the whitelist.";
    }

    public Collection<Message> doUpgrade() throws Exception {
        return (Collection)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Collection<Message>>(){

            public Collection<Message> doInTransaction() {
                Iterable applicationLinks = ApplicationLinksMigration.this.applicationLinkService.getApplicationLinks();
                ApplicationLinksMigration.this.whitelistManager.addAll(Iterables.transform((Iterable)applicationLinks, (Function)new Function<ReadOnlyApplicationLink, WhitelistRule>(){

                    public WhitelistRule apply(ReadOnlyApplicationLink applicationLink) {
                        return new ApplicationLinkWhitelistRule(applicationLink, false);
                    }
                }));
                return Collections.emptyList();
            }
        });
    }
}

