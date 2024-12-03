/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import com.atlassian.crowd.plugin.rest.entity.admin.FailedEntity;
import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class BatchResultWithFailureReasonsRestDto<T> {
    @JsonProperty(value="successes")
    private final Collection<T> successes;
    @JsonProperty(value="failures")
    private final Collection<FailedEntity<T>> failures;

    public BatchResultWithFailureReasonsRestDto() {
        this.successes = new ArrayList<T>();
        this.failures = new ArrayList<FailedEntity<T>>();
    }

    @JsonCreator
    public BatchResultWithFailureReasonsRestDto(@JsonProperty(value="successes") Collection<T> successes, @JsonProperty(value="failures") Collection<FailedEntity<T>> failures) {
        this.successes = successes;
        this.failures = failures;
    }

    public void addSuccess(T entityName) {
        this.successes.add(entityName);
    }

    public void addAllSuccesses(Collection<T> successes) {
        this.successes.addAll(successes);
    }

    public void addFailedEntity(T entityName, String reason) {
        this.failures.add(new FailedEntity<T>(entityName, reason));
    }

    public void addAllFailedEntities(Collection<FailedEntity<T>> failures) {
        this.failures.addAll(failures);
    }

    public Collection<FailedEntity<T>> getFailures() {
        return this.failures;
    }

    public Collection<T> getSuccesses() {
        return this.successes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BatchResultWithFailureReasonsRestDto that = (BatchResultWithFailureReasonsRestDto)o;
        return Objects.equals(this.successes, that.successes) && Objects.equals(this.failures, that.failures);
    }

    public int hashCode() {
        return Objects.hash(this.successes, this.failures);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("successes", this.successes).add("failures", this.failures).toString();
    }
}

