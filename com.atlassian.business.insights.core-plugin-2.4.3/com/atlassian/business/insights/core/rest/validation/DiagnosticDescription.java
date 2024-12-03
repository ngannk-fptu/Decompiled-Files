/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.validation;

import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class DiagnosticDescription
implements Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    private String message;

    public DiagnosticDescription() {
    }

    @JsonCreator
    public DiagnosticDescription(@Nonnull @JsonProperty(value="key") String key, @Nonnull @JsonProperty(value="message") String message) {
        this.key = Objects.requireNonNull(key, "key");
        this.message = Objects.requireNonNull(message, "message");
    }

    @JsonProperty(value="key")
    @Nonnull
    public String getKey() {
        return this.key;
    }

    @JsonProperty(value="message")
    @Nonnull
    public String getMessage() {
        return this.message;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DiagnosticDescription that = (DiagnosticDescription)o;
        return Objects.equals(this.key, that.key) && Objects.equals(this.message, that.message);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.message);
    }
}

