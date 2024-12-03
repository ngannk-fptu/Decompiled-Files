/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.plugins.whitelist.WhitelistManager
 *  com.atlassian.plugins.whitelist.WhitelistOnOffSwitch
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.whitelist.core.migration.confluence.macros;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.plugins.whitelist.WhitelistManager;
import com.atlassian.plugins.whitelist.WhitelistOnOffSwitch;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.core.migration.AbstractWhitelistPluginUpgradeTask;
import com.atlassian.plugins.whitelist.core.migration.confluence.CustomConfluenceBandanaContext;
import com.atlassian.plugins.whitelist.core.migration.confluence.macros.BandanaMacroWhitelistXmlData;
import com.atlassian.plugins.whitelist.core.migration.confluence.macros.BandanaMacroWhitelistXmlParser;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceMacroWhitelistMigration
extends AbstractWhitelistPluginUpgradeTask
implements PluginUpgradeTask {
    private static final String MACRO_WHITELIST_BANDANA_KEY = "com.atlassian.allowed.domains";
    private static final Logger logger = LoggerFactory.getLogger(ConfluenceMacroWhitelistMigration.class);
    private final BandanaManager bandanaManager;
    private final WhitelistOnOffSwitch whitelistOnOffSwitch;
    private final WhitelistManager whitelistManager;
    private final TransactionTemplate transactionTemplate;
    private final BandanaMacroWhitelistXmlParser parser;

    public ConfluenceMacroWhitelistMigration(BandanaManager bandanaManager, WhitelistOnOffSwitch whitelistOnOffSwitch, WhitelistManager whitelistManager, TransactionTemplate transactionTemplate) {
        this.bandanaManager = bandanaManager;
        this.whitelistOnOffSwitch = whitelistOnOffSwitch;
        this.whitelistManager = whitelistManager;
        this.transactionTemplate = transactionTemplate;
        this.parser = new BandanaMacroWhitelistXmlParser();
    }

    public int getBuildNumber() {
        return 2;
    }

    public String getShortDescription() {
        return "Migrate existing whitelist entries used by the RSS and HTML include macro.";
    }

    public Collection<Message> doUpgrade() throws Exception {
        return (Collection)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Collection<Message>>(){

            public Collection<Message> doInTransaction() {
                Object value = ConfluenceMacroWhitelistMigration.this.bandanaManager.getValue((BandanaContext)new CustomConfluenceBandanaContext(), ConfluenceMacroWhitelistMigration.MACRO_WHITELIST_BANDANA_KEY);
                if (value instanceof String) {
                    ConfluenceMacroWhitelistMigration.this.migrateExistingData((String)value);
                }
                return Collections.emptyList();
            }
        });
    }

    private void migrateExistingData(String bandanaContent) {
        BandanaMacroWhitelistXmlData data = this.parser.parseData(bandanaContent);
        this.migrateWhitelistRules(data);
        this.migrateOnOffState(data);
    }

    private void migrateWhitelistRules(BandanaMacroWhitelistXmlData data) {
        Collection<WhitelistRule> whitelistRules = data.getRules();
        logger.debug("Migrating {} whitelist rules ...", (Object)whitelistRules.size());
        this.whitelistManager.addAll(whitelistRules);
    }

    private void migrateOnOffState(BandanaMacroWhitelistXmlData data) {
        if (data.isAllAllowed()) {
            logger.debug("Confluence macro whitelist was disabled");
            this.whitelistOnOffSwitch.disable();
        } else {
            logger.debug("Confluence macro whitelist was enabled");
            this.whitelistOnOffSwitch.enable();
        }
    }
}

