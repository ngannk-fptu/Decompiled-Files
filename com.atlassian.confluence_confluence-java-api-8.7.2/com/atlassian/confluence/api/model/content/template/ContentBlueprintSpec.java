/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Option
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.content.template;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintId;
import com.atlassian.confluence.api.model.content.template.ContentTemplateId;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class ContentBlueprintSpec {
    public static final String PAGE_TITLE = "title";
    public static final String LABELS = "labelsString";
    public static final String VIEW_PERMISSION_USERS = "viewPermissionUsers";
    @JsonProperty
    private final ContentBlueprintId blueprintId;
    @JsonProperty
    private final Optional<ContentTemplateId> contentTemplateId;
    @JsonProperty
    private final Map<String, Object> context;

    @JsonCreator
    private ContentBlueprintSpec() {
        this(ContentBlueprintSpec.builder());
    }

    private ContentBlueprintSpec(ContentBlueprintSpecBuilder builder) {
        this.blueprintId = builder.id;
        this.context = Collections.unmodifiableMap(builder.context);
        this.contentTemplateId = Optional.ofNullable(builder.contentTemplateId);
    }

    @Deprecated
    @JsonIgnore
    public Option<String> getViewPermissionUsersString() {
        return FugueConversionUtil.toComOption(this.viewPermissionUsersString());
    }

    @JsonIgnore
    public Optional<String> viewPermissionUsersString() {
        return Optional.ofNullable((String)this.context.get(VIEW_PERMISSION_USERS));
    }

    @Deprecated
    @JsonIgnore
    public Option<String> getLabelsString() {
        return FugueConversionUtil.toComOption(this.labelsString());
    }

    @JsonIgnore
    public Optional<String> labelsString() {
        return Optional.ofNullable((String)this.context.get(LABELS));
    }

    public static ContentBlueprintSpecBuilder builder() {
        return new ContentBlueprintSpecBuilder();
    }

    public static ContentBlueprintSpecBuilder builder(ContentBlueprintSpec spec) {
        return ContentBlueprintSpec.builder().id(spec.blueprintId).contentTemplateId(spec.contentTemplateId.get()).context(spec.getContext());
    }

    public ContentBlueprintId getBlueprintId() {
        return this.blueprintId;
    }

    @Deprecated
    @JsonIgnore
    public Option<ContentTemplateId> getContentTemplateId() {
        return FugueConversionUtil.toComOption(this.contentTemplateId);
    }

    @JsonProperty(value="contentTemplateId")
    public Optional<ContentTemplateId> contentTemplateId() {
        return this.contentTemplateId;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }

    public static final class ContentBlueprintSpecBuilder {
        private ContentBlueprintId id;
        private final Map<String, Object> context = new HashMap<String, Object>();
        private ContentTemplateId contentTemplateId;

        private ContentBlueprintSpecBuilder() {
        }

        public ContentBlueprintSpecBuilder id(ContentBlueprintId id) {
            this.id = id;
            return this;
        }

        public ContentBlueprintSpecBuilder putContextEntry(String key, Object value) {
            this.context.put(key, value);
            return this;
        }

        public ContentBlueprintSpecBuilder context(Map<String, Object> context) {
            this.context.putAll(context);
            return this;
        }

        public ContentBlueprintSpecBuilder contentTemplateId(ContentTemplateId contentTemplateId) {
            this.contentTemplateId = contentTemplateId;
            return this;
        }

        public ContentBlueprintSpec build() {
            return new ContentBlueprintSpec(this);
        }
    }
}

