/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.retention;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.retention.AgeUnit;
import com.atlassian.confluence.api.model.retention.RetentionRuleValidator;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class TrashRetentionRule {
    @JsonProperty
    private Integer maxDeletedAge;
    @JsonProperty
    private AgeUnit deletedAgeUnit;
    @JsonProperty
    private boolean keepAll = true;

    public TrashRetentionRule() {
    }

    public TrashRetentionRule(int maxDeletedAge, AgeUnit ageUnit) {
        this.keepAll = false;
        this.maxDeletedAge = maxDeletedAge;
        this.deletedAgeUnit = ageUnit;
    }

    public Integer getMaxDeletedAge() {
        return this.maxDeletedAge;
    }

    public void setMaxDeletedAge(Integer maxDeletedAge) {
        this.maxDeletedAge = maxDeletedAge;
    }

    public AgeUnit getDeletedAgeUnit() {
        return this.deletedAgeUnit;
    }

    public void setDeletedAgeUnit(AgeUnit deletedAgeUnit) {
        this.deletedAgeUnit = deletedAgeUnit;
    }

    public boolean getKeepAll() {
        return this.keepAll;
    }

    public void setKeepAll(boolean keepAll) {
        this.keepAll = keepAll;
    }

    public boolean hasDeletedAgeLimit() {
        return this.maxDeletedAge != null && this.deletedAgeUnit != null;
    }

    public List<String> validate() {
        ArrayList<String> validations = new ArrayList<String>();
        if (!RetentionRuleValidator.isValidMaxAge(this.maxDeletedAge).booleanValue()) {
            validations.add(String.format("maxDeletedAge should be between than %s and %s", RetentionRuleValidator.MAX_AGE_LOWER_LIMIT, RetentionRuleValidator.MAX_AGE_UPPER_LIMIT));
        }
        if (!RetentionRuleValidator.isValidAgeUnit(this.deletedAgeUnit, this.maxDeletedAge).booleanValue()) {
            validations.add("deletedAgeUnit of DAY, MONTH or YEAR should be provided when maxDeletedAge specified");
        }
        if (this.getKeepAll() && this.getMaxDeletedAge() != null) {
            validations.add("maxDeletedAge should be null when keepAll is true");
        }
        return validations;
    }

    public OffsetDateTime calculateMaxDate(OffsetDateTime now) {
        return now.minus(this.maxDeletedAge.intValue(), ChronoUnit.valueOf(this.deletedAgeUnit.name()));
    }

    public boolean equals(Object other) {
        if (!(other instanceof TrashRetentionRule)) {
            return false;
        }
        TrashRetentionRule TrashRetentionRule2 = (TrashRetentionRule)other;
        return Objects.equals(TrashRetentionRule2.maxDeletedAge, this.maxDeletedAge) && Objects.equals(TrashRetentionRule2.deletedAgeUnit, this.deletedAgeUnit) && Objects.equals(TrashRetentionRule2.getKeepAll(), this.keepAll);
    }

    public int hashCode() {
        return Objects.hash(this.maxDeletedAge, this.deletedAgeUnit, this.keepAll);
    }

    public String toString() {
        return "TrashRetentionRule{maxDeletedAge=" + this.maxDeletedAge + ", deletedAgeUnit=" + this.deletedAgeUnit + ", keepAll=" + this.keepAll + '}';
    }
}

