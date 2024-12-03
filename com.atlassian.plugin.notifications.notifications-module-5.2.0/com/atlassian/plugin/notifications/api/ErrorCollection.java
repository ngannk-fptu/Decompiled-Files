/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang.builder.ToStringBuilder
 *  org.apache.commons.lang.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.api;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class ErrorCollection {
    @JsonProperty
    private final List<String> errorMessages = Lists.newArrayList();
    @JsonProperty
    private final Map<String, String> errors = Maps.newHashMap();
    @JsonIgnore
    private final Set<Reason> reasons = Sets.newHashSet();

    public void addErrorMessage(String message) {
        this.errorMessages.add(message);
    }

    public void addError(String key, String message) {
        this.errors.put(key, message);
    }

    public List<String> getErrorMessages() {
        return this.errorMessages;
    }

    public void addErrorMessage(String msg, Reason reason) {
        this.errorMessages.add(msg);
        this.reasons.add(reason);
    }

    public Map<String, String> getErrors() {
        return this.errors;
    }

    public boolean hasAnyErrors() {
        return !this.errors.isEmpty() || !this.errorMessages.isEmpty();
    }

    public void addErrorCollection(ErrorCollection errorCollection) {
        Iterables.addAll(this.errorMessages, errorCollection.getErrorMessages());
        this.errors.putAll(errorCollection.getErrors());
        this.reasons.addAll(errorCollection.getReasons());
    }

    public void addReason(Reason reason) {
        this.reasons.add(reason);
    }

    public Set<Reason> getReasons() {
        return this.reasons;
    }

    public void addErrorMessages(Set<String> errorMessages) {
        this.errorMessages.addAll(errorMessages);
    }

    public String toString() {
        return new ToStringBuilder((Object)this, ToStringStyle.SHORT_PREFIX_STYLE).append("errorMessages", this.errorMessages).append("errors", this.errors).append("reasons", this.reasons).toString();
    }

    public static enum Reason {
        NOT_FOUND(404),
        NOT_LOGGED_IN(401),
        FORBIDDEN(403),
        VALIDATION_FAILED(400),
        SERVER_ERROR(500);

        private final int httpStatusCode;

        private Reason(int httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
        }

        public int getHttpStatusCode() {
            return this.httpStatusCode;
        }

        public static Reason getWorstReason(Collection<Reason> reasons) {
            if (reasons.contains((Object)NOT_LOGGED_IN)) {
                return NOT_LOGGED_IN;
            }
            if (reasons.contains((Object)FORBIDDEN)) {
                return FORBIDDEN;
            }
            if (reasons.contains((Object)NOT_FOUND)) {
                return NOT_FOUND;
            }
            if (reasons.contains((Object)SERVER_ERROR)) {
                return SERVER_ERROR;
            }
            if (reasons.contains((Object)VALIDATION_FAILED)) {
                return VALIDATION_FAILED;
            }
            return null;
        }
    }
}

