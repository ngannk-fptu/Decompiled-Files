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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class RetentionRule {
    @JsonProperty
    private Integer maxAge;
    @JsonProperty
    private AgeUnit ageUnit;
    @JsonProperty
    private Integer maxNumberOfVersions;
    @JsonProperty
    private boolean keepAll = true;

    public Integer getMaxAge() {
        return this.maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public AgeUnit getAgeUnit() {
        return this.ageUnit;
    }

    public void setAgeUnit(AgeUnit ageUnit) {
        this.ageUnit = ageUnit;
    }

    public Integer getMaxNumberOfVersions() {
        return this.maxNumberOfVersions;
    }

    public void setMaxNumberOfVersions(Integer maxNumberOfVersions) {
        this.maxNumberOfVersions = maxNumberOfVersions;
    }

    public boolean getKeepAll() {
        return this.keepAll;
    }

    public void setKeepAll(boolean keepAll) {
        this.keepAll = keepAll;
    }

    public boolean hasVersionLimit() {
        return this.maxNumberOfVersions != null;
    }

    public boolean hasAgeLimit() {
        return this.maxAge != null && this.ageUnit != null;
    }

    public List<String> validate() {
        ArrayList<String> validations = new ArrayList<String>();
        if (!RetentionRuleValidator.isValidMaxAge(this.maxAge).booleanValue()) {
            validations.add(String.format("maxAge should be between than %s and %s", RetentionRuleValidator.MAX_AGE_LOWER_LIMIT, RetentionRuleValidator.MAX_AGE_UPPER_LIMIT));
        }
        if (!RetentionRuleValidator.isValidMaxNumber(this.maxNumberOfVersions).booleanValue()) {
            validations.add(String.format("maxNumberOfVersions should be between %s and %s", RetentionRuleValidator.MAX_NUMBER_LOWER_LIMIT, RetentionRuleValidator.MAX_NUMBER_UPPER_LIMIT));
        }
        if (!RetentionRuleValidator.isValidAgeUnit(this.ageUnit, this.maxAge).booleanValue()) {
            validations.add("ageUnit of DAY, MONTH or YEAR should be provided when maxAge specified");
        }
        if (this.getKeepAll() && (this.getMaxAge() != null || this.getMaxNumberOfVersions() != null)) {
            validations.add("maxAge and maxNumberOfVersions should be null when keepAll is true");
        }
        return validations;
    }

    public LocalDate calculateMaxDate(LocalDate now) {
        return now.minus(this.maxAge.intValue(), ChronoUnit.valueOf(this.ageUnit.name()));
    }

    public boolean equals(Object other) {
        if (!(other instanceof RetentionRule)) {
            return false;
        }
        RetentionRule retentionRule = (RetentionRule)other;
        return Objects.equals(retentionRule.maxAge, this.maxAge) && Objects.equals(retentionRule.ageUnit, this.ageUnit) && Objects.equals(retentionRule.maxNumberOfVersions, this.maxNumberOfVersions) && Objects.equals(retentionRule.getKeepAll(), this.keepAll);
    }

    public int hashCode() {
        return Objects.hash(this.maxAge, this.ageUnit, this.maxNumberOfVersions, this.keepAll);
    }

    public String toString() {
        return "RetentionRule{maxAge=" + this.maxAge + ", ageUnit=" + this.ageUnit + ", maxNumberOfVersions=" + this.maxNumberOfVersions + ", keepAll=" + this.keepAll + '}';
    }
}

