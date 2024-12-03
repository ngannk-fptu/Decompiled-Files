/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.history.UserHistory
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.ContentEntityList;
import com.atlassian.confluence.plugins.rest.entities.UserHistoryList;
import com.atlassian.confluence.plugins.rest.entities.UserSessionEntity;
import com.atlassian.confluence.plugins.rest.manager.RestContentManager;
import com.atlassian.confluence.plugins.rest.manager.RestUserSessionManager;
import com.atlassian.confluence.user.history.UserHistory;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRestUserSessionManager
implements RestUserSessionManager {
    private final RestContentManager restContentManager;
    private final HttpContext httpContext;
    private static final Logger log = LoggerFactory.getLogger(DefaultRestUserSessionManager.class);

    public DefaultRestUserSessionManager(HttpContext httpContext, RestContentManager restContentManager) {
        this.httpContext = httpContext;
        this.restContentManager = restContentManager;
    }

    @Override
    public UserSessionEntity getUserSession() {
        List historyList = this.getUserHistoryObject().getContent();
        ContentEntityList history = new ContentEntityList(historyList.size(), (ListWrapperCallback<ContentEntity>)((ListWrapperCallback)indexes -> {
            int size = historyList.size();
            if (size == 0) {
                return Collections.emptyList();
            }
            int startIndex = Math.max(0, indexes.getMinIndex(size));
            int endIndex = Math.max(0, indexes.getMaxIndex(size));
            ArrayList<ContentEntity> result = new ArrayList<ContentEntity>();
            for (int i = startIndex; i <= endIndex; ++i) {
                ContentEntity entity = this.restContentManager.getContentEntity((Long)historyList.get(i), false);
                if (entity == null) continue;
                result.add(entity);
            }
            return result;
        }));
        return new UserSessionEntity(history);
    }

    private UserHistory getUserHistoryObject() {
        HttpSession session = this.httpContext.getSession(false);
        if (log.isDebugEnabled()) {
            if (session == null) {
                log.debug("No established session found when trying to retrieve user history");
            } else {
                log.debug("Retrieving user history from session: " + session.getId());
            }
        }
        if (session == null || session.getAttribute("confluence.user.history") == null) {
            return new UserHistory(0);
        }
        return (UserHistory)session.getAttribute("confluence.user.history");
    }

    @Override
    public UserHistoryList getUserHistory(Integer startIndex, Integer maxResults) {
        List userHistoryContentIds = this.getUserHistoryObject().getContent();
        UserHistoryList result = new UserHistoryList();
        int start = startIndex == null ? 0 : startIndex;
        start = Math.max(0, start);
        int end = maxResults == null ? userHistoryContentIds.size() : start + maxResults;
        for (int i = start; i < end && i < userHistoryContentIds.size(); ++i) {
            ContentEntity entity = this.restContentManager.getContentEntity((Long)userHistoryContentIds.get(i), false);
            if (entity == null) continue;
            result.getContents().add(entity);
        }
        return result;
    }
}

