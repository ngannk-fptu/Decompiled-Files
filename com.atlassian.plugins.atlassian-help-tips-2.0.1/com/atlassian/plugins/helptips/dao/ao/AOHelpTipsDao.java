/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 *  net.java.ao.EntityStreamCallback
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 */
package com.atlassian.plugins.helptips.dao.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugins.helptips.dao.HelpTipsDao;
import com.atlassian.plugins.helptips.dao.ao.AOHelpTip;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;
import net.java.ao.RawEntity;

public class AOHelpTipsDao
implements HelpTipsDao {
    private final ActiveObjects ao;

    public AOHelpTipsDao(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public Set<String> findDismissedTips(String userKey) {
        final HashSet tips = Sets.newHashSet();
        this.ao.stream(AOHelpTip.class, Query.select((String)"ID, DISMISSED_HELP_TIP").where("USER_KEY = ?", new Object[]{userKey}), (EntityStreamCallback)new EntityStreamCallback<AOHelpTip, Integer>(){

            public void onRowRead(AOHelpTip aoHelpTip) {
                tips.add(aoHelpTip.getDismissedHelpTip());
            }
        });
        return tips;
    }

    @Override
    public void saveDismissedTip(String userKey, String tip) {
        AOHelpTip[] aoHelpTip = this.findTip(userKey, tip);
        if (aoHelpTip.length == 0) {
            this.ao.create(AOHelpTip.class, (Map)ImmutableMap.of((Object)"USER_KEY", (Object)userKey, (Object)"DISMISSED_HELP_TIP", (Object)tip));
        }
    }

    @Override
    public void deleteDismissedTip(String userKey, String tip) {
        AOHelpTip[] aoHelpTip = this.findTip(userKey, tip);
        if (aoHelpTip.length >= 1) {
            this.ao.delete(new RawEntity[]{aoHelpTip[0]});
        }
    }

    private AOHelpTip[] findTip(String userKey, String tip) {
        return (AOHelpTip[])this.ao.find(AOHelpTip.class, Query.select().where("USER_KEY = ? AND DISMISSED_HELP_TIP = ?", new Object[]{userKey, tip}));
    }
}

