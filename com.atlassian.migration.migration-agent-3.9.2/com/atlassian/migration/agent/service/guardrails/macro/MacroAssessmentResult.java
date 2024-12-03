/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.guardrails.macro;

import com.atlassian.migration.agent.service.guardrails.macro.MacroAssessment;
import com.atlassian.migration.agent.service.guardrails.macro.MacroInformationSupplier;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class MacroAssessmentResult {
    @JsonProperty
    private final long totalNumberOfUniquePages;
    @JsonProperty
    private final long totalNumberOfUniqueMacros;
    @JsonProperty
    private final Set<String> migrationIssues;
    @JsonProperty
    private final Set<String> deprecated;
    @JsonProperty
    private final Set<String> fabricEditorNotSupported;
    @JsonProperty
    private final Set<MacroAssessment> assessments;

    public MacroAssessmentResult(Set<MacroAssessment> assessments, MacroInformationSupplier macroInformationSupplier) {
        this.assessments = assessments;
        this.totalNumberOfUniquePages = MacroAssessmentResult.countPages(assessments);
        this.totalNumberOfUniqueMacros = assessments.size();
        Set<String> macros = assessments.stream().map(MacroAssessment::getMacro).collect(Collectors.toSet());
        this.migrationIssues = macroInformationSupplier.filterMacrosWithMigrationIssue(macros);
        this.deprecated = macroInformationSupplier.filterDeprecatedMacro(macros);
        this.fabricEditorNotSupported = macroInformationSupplier.filterMacrosNotWorkingOnFabric(macros);
    }

    private static long countPages(Set<MacroAssessment> assessments) {
        return assessments.stream().flatMap(assessment -> assessment.getContentIds().stream()).distinct().count();
    }

    @Generated
    public long getTotalNumberOfUniquePages() {
        return this.totalNumberOfUniquePages;
    }

    @Generated
    public long getTotalNumberOfUniqueMacros() {
        return this.totalNumberOfUniqueMacros;
    }

    @Generated
    public Set<String> getMigrationIssues() {
        return this.migrationIssues;
    }

    @Generated
    public Set<String> getDeprecated() {
        return this.deprecated;
    }

    @Generated
    public Set<String> getFabricEditorNotSupported() {
        return this.fabricEditorNotSupported;
    }

    @Generated
    public Set<MacroAssessment> getAssessments() {
        return this.assessments;
    }

    @Generated
    public String toString() {
        return "MacroAssessmentResult(totalNumberOfUniquePages=" + this.getTotalNumberOfUniquePages() + ", totalNumberOfUniqueMacros=" + this.getTotalNumberOfUniqueMacros() + ", migrationIssues=" + this.getMigrationIssues() + ", deprecated=" + this.getDeprecated() + ", fabricEditorNotSupported=" + this.getFabricEditorNotSupported() + ", assessments=" + this.getAssessments() + ")";
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MacroAssessmentResult)) {
            return false;
        }
        MacroAssessmentResult other = (MacroAssessmentResult)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getTotalNumberOfUniquePages() != other.getTotalNumberOfUniquePages()) {
            return false;
        }
        if (this.getTotalNumberOfUniqueMacros() != other.getTotalNumberOfUniqueMacros()) {
            return false;
        }
        Set<String> this$migrationIssues = this.getMigrationIssues();
        Set<String> other$migrationIssues = other.getMigrationIssues();
        if (this$migrationIssues == null ? other$migrationIssues != null : !((Object)this$migrationIssues).equals(other$migrationIssues)) {
            return false;
        }
        Set<String> this$deprecated = this.getDeprecated();
        Set<String> other$deprecated = other.getDeprecated();
        if (this$deprecated == null ? other$deprecated != null : !((Object)this$deprecated).equals(other$deprecated)) {
            return false;
        }
        Set<String> this$fabricEditorNotSupported = this.getFabricEditorNotSupported();
        Set<String> other$fabricEditorNotSupported = other.getFabricEditorNotSupported();
        if (this$fabricEditorNotSupported == null ? other$fabricEditorNotSupported != null : !((Object)this$fabricEditorNotSupported).equals(other$fabricEditorNotSupported)) {
            return false;
        }
        Set<MacroAssessment> this$assessments = this.getAssessments();
        Set<MacroAssessment> other$assessments = other.getAssessments();
        return !(this$assessments == null ? other$assessments != null : !((Object)this$assessments).equals(other$assessments));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MacroAssessmentResult;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $totalNumberOfUniquePages = this.getTotalNumberOfUniquePages();
        result = result * 59 + (int)($totalNumberOfUniquePages >>> 32 ^ $totalNumberOfUniquePages);
        long $totalNumberOfUniqueMacros = this.getTotalNumberOfUniqueMacros();
        result = result * 59 + (int)($totalNumberOfUniqueMacros >>> 32 ^ $totalNumberOfUniqueMacros);
        Set<String> $migrationIssues = this.getMigrationIssues();
        result = result * 59 + ($migrationIssues == null ? 43 : ((Object)$migrationIssues).hashCode());
        Set<String> $deprecated = this.getDeprecated();
        result = result * 59 + ($deprecated == null ? 43 : ((Object)$deprecated).hashCode());
        Set<String> $fabricEditorNotSupported = this.getFabricEditorNotSupported();
        result = result * 59 + ($fabricEditorNotSupported == null ? 43 : ((Object)$fabricEditorNotSupported).hashCode());
        Set<MacroAssessment> $assessments = this.getAssessments();
        result = result * 59 + ($assessments == null ? 43 : ((Object)$assessments).hashCode());
        return result;
    }
}

