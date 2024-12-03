/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.service.ActionResult
 *  com.atlassian.mywork.service.ActionService
 *  com.atlassian.mywork.service.LocalNotificationService
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.JsonNode
 */
package com.atlassian.mywork.host.provider;

import com.atlassian.mywork.host.dao.UserApplicationLinkDao;
import com.atlassian.mywork.host.provider.MyWorkRegistrationProvider;
import com.atlassian.mywork.service.ActionResult;
import com.atlassian.mywork.service.ActionService;
import com.atlassian.mywork.service.LocalNotificationService;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.codehaus.jackson.JsonNode;

public class MyWorkActionService
implements ActionService {
    private final LocalNotificationService notificationService;
    private final UserApplicationLinkDao userApplicationLinkDao;
    private final ActionExecutor dismissAuth = new ActionExecutor(){

        @Override
        public ActionResult execute(String username, JsonNode action) {
            JsonNode appId = action.path("metadata").path("appId");
            if (appId.isTextual()) {
                MyWorkActionService.this.userApplicationLinkDao.setPingCompleted(username, appId.getTextValue());
            }
            String globalId = action.get("globalId").getTextValue();
            MyWorkActionService.this.notificationService.deleteByGlobalId(username, globalId);
            return ActionResult.SUCCESS;
        }
    };
    private final Map<String, ActionExecutor> actionExecutors = ImmutableMap.builder().put((Object)"com.atlassian.mywork.host.provider.authentication.dismissAuth", (Object)this.dismissAuth).build();

    public MyWorkActionService(LocalNotificationService notificationService, UserApplicationLinkDao userApplicationLinkDao) {
        this.notificationService = notificationService;
        this.userApplicationLinkDao = userApplicationLinkDao;
    }

    public String getApplication() {
        return new MyWorkRegistrationProvider().getApplication();
    }

    public ActionResult execute(String username, JsonNode action) {
        String qualifiedAction = action.path("qualifiedAction").getTextValue();
        ActionExecutor executor = this.actionExecutors.get(qualifiedAction);
        return executor.execute(username, action);
    }

    private static interface ActionExecutor {
        public ActionResult execute(String var1, JsonNode var2);
    }
}

