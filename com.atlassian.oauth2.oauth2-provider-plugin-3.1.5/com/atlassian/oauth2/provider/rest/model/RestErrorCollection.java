/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.oauth2.provider.rest.model;

import com.atlassian.oauth2.common.rest.validator.ErrorCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class RestErrorCollection {
    @JsonProperty
    private List<String> errors;
    @JsonProperty
    private Map<String, List<String>> fieldErrors;

    public RestErrorCollection(@Nonnull ErrorCollection errorCollection) {
        this(errorCollection.getErrors(), errorCollection.getFieldErrors());
    }

    public RestErrorCollection(@Nonnull List<String> errors, @Nonnull Map<String, List<String>> fieldErrors) {
        this.errors = ImmutableList.copyOf(errors);
        this.fieldErrors = ImmutableMap.copyOf((Map)Maps.transformValues(fieldErrors, ImmutableList::copyOf));
    }

    public List<String> getErrors() {
        return this.errors;
    }

    public Map<String, List<String>> getFieldErrors() {
        return this.fieldErrors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void setFieldErrors(Map<String, List<String>> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestErrorCollection)) {
            return false;
        }
        RestErrorCollection other = (RestErrorCollection)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<String> this$errors = this.getErrors();
        List<String> other$errors = other.getErrors();
        if (this$errors == null ? other$errors != null : !((Object)this$errors).equals(other$errors)) {
            return false;
        }
        Map<String, List<String>> this$fieldErrors = this.getFieldErrors();
        Map<String, List<String>> other$fieldErrors = other.getFieldErrors();
        return !(this$fieldErrors == null ? other$fieldErrors != null : !((Object)this$fieldErrors).equals(other$fieldErrors));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestErrorCollection;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<String> $errors = this.getErrors();
        result = result * 59 + ($errors == null ? 43 : ((Object)$errors).hashCode());
        Map<String, List<String>> $fieldErrors = this.getFieldErrors();
        result = result * 59 + ($fieldErrors == null ? 43 : ((Object)$fieldErrors).hashCode());
        return result;
    }

    public String toString() {
        return "RestErrorCollection(errors=" + this.getErrors() + ", fieldErrors=" + this.getFieldErrors() + ")";
    }

    public RestErrorCollection() {
    }
}

