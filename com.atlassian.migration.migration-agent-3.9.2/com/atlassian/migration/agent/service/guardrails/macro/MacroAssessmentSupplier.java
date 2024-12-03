/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.guardrails.macro;

import com.atlassian.migration.agent.service.guardrails.macro.MacroAssessment;
import java.util.Set;

@FunctionalInterface
interface MacroAssessmentSupplier {
    public Set<MacroAssessment> fetch();
}

