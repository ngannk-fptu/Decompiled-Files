/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckContext
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckContext;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckType;

public interface CheckRegistration<T extends CheckContext> {
    public CheckType getCheckType();

    public Checker<T> getChecker();

    public CheckContextProvider<T> getCheckContextProvider();

    public CheckResultMapper getCheckResultMapper();

    public EventDto getAnalyticsEventModel(CheckResult var1, long var2);

    public String getFailedToExecuteAnalyticsEventName();
}

