/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.longtasks.LongTaskId
 *  com.atlassian.confluence.api.model.longtasks.LongTaskStatus
 *  com.atlassian.confluence.api.model.longtasks.LongTaskSubmission
 *  com.atlassian.confluence.api.model.longtasks.LongTaskSubmission$LongTaskSubmissionBuilder
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.NavigationService
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.google.common.base.Strings
 */
package com.atlassian.confluence.api.impl.service.longtasks;

import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import com.atlassian.confluence.api.model.longtasks.LongTaskStatus;
import com.atlassian.confluence.api.model.longtasks.LongTaskSubmission;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.google.common.base.Strings;

public class LongTaskFactory {
    private final NavigationService navigationService;

    public LongTaskFactory(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    public LongTaskSubmission buildSubmission(LongTaskId taskId) {
        return this.buildSubmission(taskId, null);
    }

    public LongTaskSubmission buildSubmission(LongTaskId id, Navigation.Builder resultBuilder) {
        LongTaskSubmission.LongTaskSubmissionBuilder builder = LongTaskSubmission.builder().id(id).status(this.navigationService.createNavigation().longTask(id).buildRelative());
        if (resultBuilder != null) {
            builder.result(resultBuilder.buildRelative());
        }
        return builder.build();
    }

    public static LongTaskStatus buildStatus(LongTaskId id, LongRunningTask task) {
        String nameKey = task.getNameKey();
        SimpleMessage name = Strings.isNullOrEmpty((String)nameKey) ? SimpleMessage.withTranslation((String)task.getName()) : SimpleMessage.withKeyAndArgs((String)nameKey, (Object[])new Object[0]);
        SimpleMessage message = SimpleMessage.withTranslation((String)task.getCurrentStatus());
        return LongTaskStatus.builder((LongTaskId)id).name((Message)name).elapsedTime(task.getElapsedTime()).percentageComplete(task.getPercentageComplete()).addMessage((Message)message).successful(task.isSuccessful()).build();
    }
}

