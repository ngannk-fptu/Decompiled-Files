/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.validator.ValidationError
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.exception.entity;

import com.atlassian.crowd.validator.ValidationError;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonProperty;

public class ValidationErrorsEntity {
    @JsonProperty(value="errors")
    private final List<ErrorMessage> errors = new ArrayList<ErrorMessage>();

    public ValidationErrorsEntity(List<ValidationError> validationErrors) {
        Map fieldMessageMap = validationErrors.stream().collect(Collectors.toMap(ValidationError::getFieldName, v -> Collections.singletonList(v.getErrorMessage()), (firstList, secondList) -> ImmutableList.builder().addAll((Iterable)firstList).addAll((Iterable)secondList).build(), LinkedHashMap::new));
        fieldMessageMap.forEach((key, values) -> this.errors.add(new ErrorMessage((String)key, (List<String>)values)));
    }

    public List<ErrorMessage> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    public static class ErrorMessage {
        @JsonProperty(value="field")
        private String field;
        @JsonProperty(value="messages")
        private List<String> messages;

        public ErrorMessage(String field, List<String> messages) {
            this.field = field;
            this.messages = messages;
        }

        public String getField() {
            return this.field;
        }

        public List<String> getMessages() {
            return this.messages;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ErrorMessage that = (ErrorMessage)o;
            return Objects.equals(this.field, that.field) && Objects.equals(this.messages, that.messages);
        }

        public int hashCode() {
            return Objects.hash(this.field, this.messages);
        }
    }
}

