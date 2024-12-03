/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.annotation.Nullable
 */
package com.atlassian.migration.agent.mapi.executor;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.agent.dto.PlanDto;
import java.util.Optional;
import javax.annotation.Nullable;

public interface CloudExecutorService {
    public PlanDto createPlan(String var1, Optional<String> var2);

    public void executePreflightChecks(String var1, String var2, String var3);

    public void executeMigration(String var1, String var2, String var3);

    public void sendCreatePlanAnalyticsEvents(PlanDto var1, ConfluenceUser var2, Optional<String> var3);

    public void sendMapiJobAnalyticsEvents(String var1, @Nullable PlanDto var2, int var3, @Nullable String var4, long var5, String var7, String var8);
}

