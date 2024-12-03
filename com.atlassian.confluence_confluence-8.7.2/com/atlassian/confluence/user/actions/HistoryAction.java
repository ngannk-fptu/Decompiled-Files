/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.user.history.UserHistoryHelper;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistoryAction
extends ConfluenceActionSupport
implements Beanable {
    private static final Logger log = LoggerFactory.getLogger(HistoryAction.class);
    private ContentEntityManager contentEntityManager;
    private List<ContentEntityObject> history = new ArrayList<ContentEntityObject>();
    private String[] types = new String[0];
    private int maxResults = -1;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        UserHistoryHelper userHistoryHelper = new UserHistoryHelper(this.getAuthenticatedUser(), this.contentEntityManager, this.permissionManager);
        this.history = userHistoryHelper.getHistoryContent(this.maxResults, this.requestedTypes());
        return "success";
    }

    @Override
    public Object getBean() {
        return Map.of("history", this.history);
    }

    private ContentTypeEnum[] requestedTypes() {
        ArrayList<ContentTypeEnum> result = new ArrayList<ContentTypeEnum>();
        for (String type : this.types) {
            ContentTypeEnum contentType = ContentTypeEnum.getByRepresentation(type);
            if (contentType == null) {
                log.warn("Ignoring invalid content type: " + type);
                continue;
            }
            result.add(contentType);
        }
        return result.toArray(new ContentTypeEnum[result.size()]);
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }
}

