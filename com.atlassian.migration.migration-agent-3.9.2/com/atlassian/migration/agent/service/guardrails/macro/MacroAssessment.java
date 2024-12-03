/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.guardrails.macro;

import java.util.Set;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class MacroAssessment {
    @JsonProperty
    private final String macro;
    @JsonProperty
    private final int numberOfContents;
    @JsonProperty
    private final Set<Long> contentIds;

    public MacroAssessment(String macro, Set<Long> contentIds) {
        this.macro = macro;
        this.numberOfContents = contentIds.size();
        this.contentIds = contentIds;
    }

    @Generated
    public String getMacro() {
        return this.macro;
    }

    @Generated
    public int getNumberOfContents() {
        return this.numberOfContents;
    }

    @Generated
    public Set<Long> getContentIds() {
        return this.contentIds;
    }

    @Generated
    public String toString() {
        return "MacroAssessment(macro=" + this.getMacro() + ", numberOfContents=" + this.getNumberOfContents() + ", contentIds=" + this.getContentIds() + ")";
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MacroAssessment)) {
            return false;
        }
        MacroAssessment other = (MacroAssessment)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$macro = this.getMacro();
        String other$macro = other.getMacro();
        if (this$macro == null ? other$macro != null : !this$macro.equals(other$macro)) {
            return false;
        }
        if (this.getNumberOfContents() != other.getNumberOfContents()) {
            return false;
        }
        Set<Long> this$contentIds = this.getContentIds();
        Set<Long> other$contentIds = other.getContentIds();
        return !(this$contentIds == null ? other$contentIds != null : !((Object)this$contentIds).equals(other$contentIds));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MacroAssessment;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $macro = this.getMacro();
        result = result * 59 + ($macro == null ? 43 : $macro.hashCode());
        result = result * 59 + this.getNumberOfContents();
        Set<Long> $contentIds = this.getContentIds();
        result = result * 59 + ($contentIds == null ? 43 : ((Object)$contentIds).hashCode());
        return result;
    }
}

