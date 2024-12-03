/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.opensymphony.xwork2.ActionContext
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.confluence.user.history;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.history.UserHistory;
import com.atlassian.user.User;
import com.opensymphony.xwork2.ActionContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class UserHistoryHelper {
    private ContentEntityManager contentEntityManager;
    private User remoteUser;
    private PermissionManager permissionManager;

    public UserHistoryHelper(User remoteUser, ContentEntityManager contentEntityManager, PermissionManager permissionManager) {
        this.remoteUser = remoteUser;
        this.contentEntityManager = contentEntityManager;
        this.permissionManager = permissionManager;
    }

    public List<ContentEntityObject> getHistoryContent(int maxResults, ContentTypeEnum ... requestedTypes) {
        UserHistory history = (UserHistory)ActionContext.getContext().getSession().get("confluence.user.history");
        if (history == null) {
            return Collections.emptyList();
        }
        ArrayList<ContentEntityObject> historyPages = new ArrayList<ContentEntityObject>();
        for (Long pageId : history.getContent()) {
            ContentEntityObject content = this.contentEntityManager.getById(pageId);
            if (content == null || !UserHistoryHelper.isRequestedType(content, requestedTypes) || !this.permissionManager.hasPermission(this.remoteUser, Permission.VIEW, content)) continue;
            historyPages.add(content);
            if (historyPages.size() != maxResults) continue;
            break;
        }
        return historyPages;
    }

    public List<ContentEntityObject> getHistoryContent(ContentTypeEnum ... requestedTypes) {
        return this.getHistoryContent(-1, requestedTypes);
    }

    private static boolean isRequestedType(ContentEntityObject content, ContentTypeEnum[] requestedTypes) {
        if (ArrayUtils.isEmpty((Object[])requestedTypes)) {
            return true;
        }
        for (ContentTypeEnum type : requestedTypes) {
            if (!type.getType().isInstance(content)) continue;
            return true;
        }
        return false;
    }
}

