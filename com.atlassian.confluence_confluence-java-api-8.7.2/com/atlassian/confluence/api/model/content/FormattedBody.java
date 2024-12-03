/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public class FormattedBody {
    @JsonProperty
    private final String value;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=WebResourceDependencies.class)
    @JsonProperty
    private final Reference<WebResourceDependencies> webresource;
    @JsonProperty
    private final ContentRepresentation representation;

    @JsonCreator
    public FormattedBody(@JsonProperty(value="representation") ContentRepresentation representation, @JsonProperty(value="value") String value) {
        this(((FormattedBodyBuilder)new FormattedBodyBuilder().representation(representation)).value(value));
    }

    FormattedBody(BaseFormattedBodyBuilder<? extends BaseFormattedBodyBuilder> builder) {
        this.representation = builder.representation;
        this.value = builder.value;
        this.webresource = Reference.orEmpty(builder.webresource, WebResourceDependencies.class);
    }

    public static FormattedBodyBuilder builder() {
        return new FormattedBodyBuilder();
    }

    public ContentRepresentation getRepresentation() {
        return this.representation;
    }

    public String getValue() {
        return this.value;
    }

    public WebResourceDependencies getWebresource() {
        if (this.webresource.isExpanded()) {
            return this.webresource.get();
        }
        return WebResourceDependencies.builder().build();
    }

    @ExperimentalApi
    protected static abstract class BaseFormattedBodyBuilder<T extends BaseFormattedBodyBuilder<T>> {
        protected ContentRepresentation representation;
        protected String value;
        protected Reference<WebResourceDependencies> webresource = Reference.empty(WebResourceDependencies.class);

        protected BaseFormattedBodyBuilder() {
        }

        public T representation(ContentRepresentation representation) {
            this.representation = representation;
            return (T)this;
        }

        public T value(String value) {
            this.value = value;
            return (T)this;
        }

        public T webresource(WebResourceDependencies webresource) {
            this.webresource = Reference.to(webresource);
            return (T)this;
        }

        public T webresource(@NonNull Reference<WebResourceDependencies> webresource) {
            this.webresource = webresource;
            return (T)this;
        }

        public abstract FormattedBody build();
    }

    public static final class FormattedBodyBuilder
    extends BaseFormattedBodyBuilder<FormattedBodyBuilder> {
        FormattedBodyBuilder() {
        }

        @Override
        public FormattedBody build() {
            return new FormattedBody(this);
        }
    }
}

