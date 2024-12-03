/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.dto.state.GlobalStateDto;
import com.atlassian.migration.agent.dto.state.InitialStateDto;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.service.InitialStateService;
import com.atlassian.migration.agent.service.PlanService;

public class DefaultInitialStateService
implements InitialStateService {
    private final PlanService planService;

    public DefaultInitialStateService(PlanService planService) {
        this.planService = planService;
    }

    @Override
    public InitialStateDto getInitialState() {
        return new InitialStateDto(new GlobalStateDto(this.planService.hasPlans(new ExecutionStatus[0])));
    }
}

