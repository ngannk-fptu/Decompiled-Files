/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.dao.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.mywork.host.dao.UserApplicationLinkDao;
import com.atlassian.mywork.host.dao.ao.AOUser;
import com.atlassian.mywork.host.dao.ao.AOUserApplicationLink;
import com.atlassian.mywork.host.dao.ao.AbstractAODao;
import com.atlassian.mywork.host.model.UserApplicationLink;
import com.atlassian.sal.usercompatibility.UserKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.springframework.stereotype.Component;

@Component
public class AOUserApplicationLinkDao
extends AbstractAODao<AOUserApplicationLink, Long>
implements UserApplicationLinkDao {
    public AOUserApplicationLinkDao(ActiveObjects ao) {
        super(AOUserApplicationLink.class, ao);
    }

    @Override
    public void setPingCompleted(String username, String applicationLinkId) {
        AOUserApplicationLink aoUserApplicationLink;
        AOUserApplicationLink[] aoUserApplicationLinks;
        String userKey = UserCompatibilityHelper.getStringKeyForUsername(username);
        AOUser user = this.findOnly(AOUser.class, Query.select().where("USERNAME = ?", new Object[]{userKey}));
        if (user == null) {
            user = (AOUser)this.ao.create(AOUser.class, new DBParam[]{new DBParam("USERNAME", (Object)userKey)});
            user.setCreated(new Date());
            user.setUpdated(user.getCreated());
            user.save();
        }
        if ((aoUserApplicationLinks = (AOUserApplicationLink[])this.ao.find(AOUserApplicationLink.class, Query.select().where("USER_ID = ? AND APPLICATION_LINK_ID = ?", new Object[]{user.getId(), applicationLinkId}).order("ID"))).length == 0) {
            aoUserApplicationLink = (AOUserApplicationLink)this.ao.create(AOUserApplicationLink.class, new DBParam[0]);
            aoUserApplicationLink.setUser(user);
            aoUserApplicationLink.setCreated(new Date());
            aoUserApplicationLink.setUpdated(aoUserApplicationLink.getCreated());
            aoUserApplicationLink.setApplicationLinkId(applicationLinkId);
        } else {
            aoUserApplicationLink = aoUserApplicationLinks[0];
            aoUserApplicationLink.setUpdated(new Date());
            for (int i = 1; i < aoUserApplicationLinks.length; ++i) {
                this.ao.delete(new RawEntity[]{aoUserApplicationLinks[i]});
            }
        }
        aoUserApplicationLink.setAuthVerified(true);
        aoUserApplicationLink.save();
    }

    @Override
    public Map<String, UserApplicationLink> findAllByApplicationId(UserKey userKey) {
        HashMap<String, UserApplicationLink> result = new HashMap<String, UserApplicationLink>();
        AOUser aoUser = this.findOnly(AOUser.class, Query.select().where("USERNAME = ?", new Object[]{userKey.getStringValue()}));
        if (aoUser != null) {
            long userId = aoUser.getId();
            for (AOUserApplicationLink link : (AOUserApplicationLink[])this.ao.find(AOUserApplicationLink.class, Query.select((String)"ID, APPLICATION_LINK_ID, AUTH_VERIFIED, CREATED, UPDATED").where("USER_ID = ?", new Object[]{userId}).order("ID"))) {
                String applicationLinkId = link.getApplicationLinkId();
                result.put(applicationLinkId, new UserApplicationLink(link.getId(), applicationLinkId, link.isAuthVerified(), link.getCreated().getTime(), link.getUpdated().getTime()));
            }
        }
        return result;
    }

    @Override
    public void clearPingCompleted(String applicationLinkId) {
        AOUserApplicationLink[] userApplicationLinks;
        for (AOUserApplicationLink aoUserApplicationLink : userApplicationLinks = (AOUserApplicationLink[])this.ao.find(AOUserApplicationLink.class, Query.select().where("APPLICATION_LINK_ID = ?", new Object[]{applicationLinkId}))) {
            aoUserApplicationLink.setAuthVerified(false);
            aoUserApplicationLink.setUpdated(new Date());
            aoUserApplicationLink.save();
        }
    }

    @Override
    public boolean delete(long id) {
        AOUserApplicationLink aoUserApplicationLink = (AOUserApplicationLink)this.getAO(id);
        this.ao.delete(new RawEntity[]{aoUserApplicationLink});
        return aoUserApplicationLink != null;
    }
}

