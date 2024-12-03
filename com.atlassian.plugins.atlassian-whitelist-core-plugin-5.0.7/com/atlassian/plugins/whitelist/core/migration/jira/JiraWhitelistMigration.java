/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.config.properties.ApplicationProperties
 *  com.atlassian.plugins.whitelist.LegacyWhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistManager
 *  com.atlassian.plugins.whitelist.WhitelistOnOffSwitch
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.whitelist.core.migration.jira;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.plugins.whitelist.LegacyWhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistManager;
import com.atlassian.plugins.whitelist.WhitelistOnOffSwitch;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.core.migration.AbstractWhitelistPluginUpgradeTask;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraWhitelistMigration
extends AbstractWhitelistPluginUpgradeTask {
    private static final Logger logger = LoggerFactory.getLogger(JiraWhitelistMigration.class);
    private final ApplicationProperties applicationProperties;
    private final WhitelistManager whitelistManager;
    private final TransactionTemplate transactionTemplate;
    private final WhitelistOnOffSwitch whitelistOnOffSwitch;

    public JiraWhitelistMigration(ApplicationProperties applicationProperties, WhitelistManager whitelistManager, TransactionTemplate transactionTemplate, WhitelistOnOffSwitch whitelistOnOffSwitch) {
        this.applicationProperties = applicationProperties;
        this.whitelistManager = whitelistManager;
        this.transactionTemplate = transactionTemplate;
        this.whitelistOnOffSwitch = whitelistOnOffSwitch;
    }

    public int getBuildNumber() {
        return 4;
    }

    public String getShortDescription() {
        return "Migrate existing JIRA whitelist information.";
    }

    public Collection<Message> doUpgrade() throws Exception {
        return (Collection)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Collection<Message>>(){

            public Collection<Message> doInTransaction() {
                this.migrateWhitelistState();
                this.migrateRules();
                return Collections.emptyList();
            }

            private void migrateWhitelistState() {
                boolean isDisabled = this.getLegacyWhitelistStateOrFalse();
                if (isDisabled) {
                    JiraWhitelistMigration.this.whitelistOnOffSwitch.disable();
                } else {
                    JiraWhitelistMigration.this.whitelistOnOffSwitch.enable();
                }
            }

            private void migrateRules() {
                List<String> rules = this.readExistingData();
                Collection existingWhitelistRule = Collections2.transform(rules, this.toWhitelistRuleData());
                logger.debug("Migrating {} whitelist rules ...", (Object)existingWhitelistRule.size());
                JiraWhitelistMigration.this.whitelistManager.addAll((Iterable)existingWhitelistRule);
            }

            private List<String> readExistingData() {
                String rulesString = this.getLegacyWhitelistRuleOrNull();
                String[] split = StringUtils.split((String)rulesString, null);
                ArrayList<String> ret = new ArrayList<String>();
                if (split != null) {
                    ret.addAll(Arrays.asList(split));
                }
                return Collections.unmodifiableList(ret);
            }

            private Function<String, WhitelistRule> toWhitelistRuleData() {
                return new Function<String, WhitelistRule>(){

                    public WhitelistRule apply(@Nullable String input) {
                        return input != null ? new LegacyWhitelistRule(input) : null;
                    }
                };
            }

            private boolean getLegacyWhitelistStateOrFalse() {
                return JiraWhitelistMigration.this.applicationProperties.getOption("jira.whitelist.disabled");
            }

            @Nullable
            private String getLegacyWhitelistRuleOrNull() {
                return JiraWhitelistMigration.this.applicationProperties.getText("jira.whitelist.rules");
            }
        });
    }
}

