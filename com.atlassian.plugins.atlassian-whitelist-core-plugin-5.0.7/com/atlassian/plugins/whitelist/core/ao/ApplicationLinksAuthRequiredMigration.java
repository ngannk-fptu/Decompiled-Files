/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictions
 *  com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictiveness
 *  net.java.ao.RawEntity
 */
package com.atlassian.plugins.whitelist.core.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictions;
import com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictiveness;
import com.atlassian.plugins.whitelist.core.ao.AoWhitelistRule;
import java.util.Arrays;
import java.util.Objects;
import net.java.ao.RawEntity;

public class ApplicationLinksAuthRequiredMigration
implements ActiveObjectsUpgradeTask {
    private final ApplicationLinkRestrictions applicationLinkRestrictions;

    public ApplicationLinksAuthRequiredMigration(ApplicationLinkRestrictions applicationLinkRestrictions) {
        this.applicationLinkRestrictions = Objects.requireNonNull(applicationLinkRestrictions);
    }

    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf((String)"1");
    }

    public void upgrade(ModelVersion currentVersion, ActiveObjects ao) {
        ao.migrate(new Class[]{AoWhitelistRule.class});
        ApplicationLinkRestrictiveness restrictiveness = this.applicationLinkRestrictions.getRestrictiveness();
        Arrays.stream(ao.find(AoWhitelistRule.class)).filter(rule -> WhitelistType.APPLICATION_LINK.equals((Object)rule.getType())).forEach(rule -> {
            if (restrictiveness.createApplinkRules()) {
                rule.setAuthenticationRequired(!restrictiveness.allowAnonymous());
                rule.save();
            } else {
                ao.delete(new RawEntity[]{rule});
            }
        });
    }
}

