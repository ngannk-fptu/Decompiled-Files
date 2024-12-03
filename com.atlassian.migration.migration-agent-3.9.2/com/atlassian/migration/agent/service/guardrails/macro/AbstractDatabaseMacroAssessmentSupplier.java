/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  javax.persistence.Tuple
 *  lombok.Generated
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.migration.agent.service.guardrails.macro;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.service.guardrails.macro.MacroAssessment;
import com.atlassian.migration.agent.service.guardrails.macro.MacroAssessmentSupplier;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import lombok.Generated;
import org.apache.commons.lang3.tuple.Pair;

abstract class AbstractDatabaseMacroAssessmentSupplier
implements MacroAssessmentSupplier {
    protected final EntityManagerTemplate tmpl;
    protected final BootstrapManager bootstrapManager;

    abstract String getQuery();

    abstract Set<Pair<String, Long>> map(Tuple var1);

    @Override
    public Set<MacroAssessment> fetch() {
        Map<String, Set<Long>> macroOccurrences = this.collectMacroOccurrences();
        return AbstractDatabaseMacroAssessmentSupplier.mapToMacroAssessments(macroOccurrences);
    }

    private Map<String, Set<Long>> collectMacroOccurrences() {
        return this.tmpl.query(Tuple.class, this.getQuery()).list().stream().flatMap(tuple -> this.map((Tuple)tuple).stream()).collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toSet())));
    }

    private static Set<MacroAssessment> mapToMacroAssessments(Map<String, Set<Long>> macroOccurrences) {
        return macroOccurrences.entrySet().stream().map((? super T entry) -> new MacroAssessment((String)entry.getKey(), (Set)entry.getValue())).collect(Collectors.toSet());
    }

    @Generated
    public AbstractDatabaseMacroAssessmentSupplier(EntityManagerTemplate tmpl, BootstrapManager bootstrapManager) {
        this.tmpl = tmpl;
        this.bootstrapManager = bootstrapManager;
    }
}

