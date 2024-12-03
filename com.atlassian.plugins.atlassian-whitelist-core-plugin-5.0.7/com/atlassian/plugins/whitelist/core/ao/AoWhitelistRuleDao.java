/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.google.common.base.Preconditions
 *  net.java.ao.DBParam
 *  net.java.ao.RawEntity
 */
package com.atlassian.plugins.whitelist.core.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.core.ao.AoWhitelistRule;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import net.java.ao.DBParam;
import net.java.ao.RawEntity;

public class AoWhitelistRuleDao {
    private final ActiveObjects activeObjects;

    public AoWhitelistRuleDao(ActiveObjects activeObjects) {
        this.activeObjects = (ActiveObjects)Preconditions.checkNotNull((Object)activeObjects);
    }

    public List<AoWhitelistRule> getAll() {
        return Arrays.asList(this.activeObjects.find(AoWhitelistRule.class));
    }

    public AoWhitelistRule get(int id) {
        return (AoWhitelistRule)this.activeObjects.get(AoWhitelistRule.class, (Object)id);
    }

    public AoWhitelistRule add(WhitelistRule data) {
        return (AoWhitelistRule)this.activeObjects.create(AoWhitelistRule.class, new DBParam[]{new DBParam("EXPRESSION", (Object)data.getExpression()), new DBParam("TYPE", (Object)data.getType()), new DBParam("ALLOWINBOUND", (Object)data.isAllowInbound()), new DBParam("AUTHENTICATIONREQUIRED", (Object)data.isAuthenticationRequired())});
    }

    public void remove(int id) {
        AoWhitelistRule model = (AoWhitelistRule)this.activeObjects.get(AoWhitelistRule.class, (Object)id);
        if (model != null) {
            this.activeObjects.delete(new RawEntity[]{model});
        }
    }
}

