/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.guardrails.macro;

import java.util.Collection;
import java.util.Set;

public interface MacroInformationSupplier {
    public Set<String> filterMacrosWithMigrationIssue(Collection<String> var1);

    public Set<String> filterDeprecatedMacro(Collection<String> var1);

    public Set<String> filterMacrosNotWorkingOnFabric(Collection<String> var1);
}

