/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.plugins.whitelist.ImmutableWhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistManager
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.whitelist.core.migration.confluence.gadgets;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugins.whitelist.ImmutableWhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistManager;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.atlassian.plugins.whitelist.core.migration.AbstractWhitelistPluginUpgradeTask;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceGadgetsWhitelistMigration
extends AbstractWhitelistPluginUpgradeTask {
    private static final String BANDANA_KEY = "userconfigured.gadget.whitelist";
    private static final Logger logger = LoggerFactory.getLogger(ConfluenceGadgetsWhitelistMigration.class);
    private final BandanaManager bandanaManager;
    private final WhitelistManager whitelistManager;
    private final TransactionTemplate transactionTemplate;

    public ConfluenceGadgetsWhitelistMigration(BandanaManager bandanaManager, WhitelistManager whitelistManager, TransactionTemplate transactionTemplate) {
        this.bandanaManager = bandanaManager;
        this.whitelistManager = whitelistManager;
        this.transactionTemplate = transactionTemplate;
    }

    public int getBuildNumber() {
        return 3;
    }

    public String getShortDescription() {
        return "Migrate existing Confluence Gadget whitelist data.";
    }

    public Collection<Message> doUpgrade() throws Exception {
        return (Collection)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Collection<Message>>(){

            public Collection<Message> doInTransaction() {
                HashSet<String> existingData = this.readExistingData();
                Collection whitelistRules = Collections2.transform(existingData, this.toWhitelistRuleData());
                logger.debug("Migrating {} whitelist rules ...", (Object)whitelistRules.size());
                ConfluenceGadgetsWhitelistMigration.this.whitelistManager.addAll((Iterable)whitelistRules);
                return Collections.emptyList();
            }

            private HashSet<String> readExistingData() {
                Object data = ConfluenceGadgetsWhitelistMigration.this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), ConfluenceGadgetsWhitelistMigration.BANDANA_KEY);
                if (data instanceof HashSet) {
                    return (HashSet)data;
                }
                return new HashSet<String>();
            }

            private Function<String, WhitelistRule> toWhitelistRuleData() {
                return new Function<String, WhitelistRule>(){

                    public WhitelistRule apply(@Nullable String input) {
                        return input != null ? ImmutableWhitelistRule.builder().expression(input).type(WhitelistType.EXACT_URL).build() : null;
                    }
                };
            }
        });
    }
}

