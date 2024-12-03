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
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.content.template;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintId;
import com.atlassian.confluence.api.model.content.template.ContentTemplateId;
import com.atlassian.confluence.api.model.content.template.ContentTemplateType;
import com.atlassian.confluence.api.model.plugin.ModuleCompleteKey;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class ContentTemplate
implements NavigationAware {
    @JsonProperty
    private final ContentTemplateId templateId;
    @JsonProperty
    private final Optional<ModuleCompleteKey> originalTemplate;
    @JsonProperty
    private final Optional<ContentBlueprintId> referencingBlueprint;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String description;
    @JsonProperty
    private final Optional<Space> space;
    @JsonProperty
    private final List<Label> labels;
    @JsonProperty
    private final ContentTemplateType templateType;
    @JsonDeserialize(as=EnrichableMap.class)
    @JsonProperty
    private final Map<ContentRepresentation, ContentBody> body;

    public Map<ContentRepresentation, ContentBody> getBody() {
        return this.body;
    }

    public ContentTemplateId getTemplateId() {
        return this.templateId;
    }

    @Deprecated
    @JsonIgnore
    public Option<ModuleCompleteKey> getOriginalTemplate() {
        return FugueConversionUtil.toComOption(this.originalTemplate);
    }

    @JsonProperty(value="originalTemplate")
    public Optional<ModuleCompleteKey> originalTemplate() {
        return this.originalTemplate;
    }

    @Deprecated
    @JsonIgnore
    public Option<ContentBlueprintId> getReferencingBlueprint() {
        return FugueConversionUtil.toComOption(this.referencingBlueprint);
    }

    @JsonProperty(value="referencingBlueprint")
    public Optional<ContentBlueprintId> referencingBlueprint() {
        return this.referencingBlueprint;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    @Deprecated
    @JsonIgnore
    public Option<Space> getSpace() {
        return FugueConversionUtil.toComOption(this.space);
    }

    @JsonProperty(value="space")
    public Optional<Space> space() {
        return this.space;
    }

    public List<Label> getLabels() {
        return this.labels;
    }

    public ContentTemplateType getTemplateType() {
        return this.templateType;
    }

    @JsonCreator
    private ContentTemplate() {
        this(ContentTemplate.builder());
    }

    private ContentTemplate(ContentTemplateBuilder builder) {
        this.templateId = builder.templateId;
        this.originalTemplate = builder.originalTemplate;
        this.referencingBlueprint = builder.referencingBlueprint;
        this.name = builder.name;
        this.description = builder.description;
        this.space = Optional.ofNullable(builder.space);
        this.body = BuilderUtils.modelMap(builder.bodyMapBuilder);
        this.labels = Collections.unmodifiableList(builder.labels);
        this.templateType = builder.templateType;
    }

    public static ContentTemplateBuilder builder() {
        return new ContentTemplateBuilder();
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return navigationService.createNavigation().experimental().template(this);
    }

    public static class Expansions {
        public static final String BODY = "body";
    }

    public static class ContentTemplateBuilder {
        private ModelMapBuilder<ContentRepresentation, ContentBody> bodyMapBuilder = ModelMapBuilder.newInstance();
        private ContentTemplateId templateId;
        private Optional<ModuleCompleteKey> originalTemplate = Optional.empty();
        private Optional<ContentBlueprintId> referencingBlueprint = Optional.empty();
        private String name = "";
        private String description = "";
        private Space space;
        private final List<Label> labels = new ArrayList<Label>();
        private ContentTemplateType templateType;

        private ContentTemplateBuilder() {
        }

        public ContentTemplateBuilder body(Map<ContentRepresentation, ContentBody> body) {
            this.bodyMapBuilder.copy(body);
            return this;
        }

        public ContentTemplateBuilder body(ContentBody body) {
            this.bodyMapBuilder.put(body.getRepresentation(), body);
            return this;
        }

        public ContentTemplateBuilder templateId(ContentTemplateId templateId) {
            this.templateId = templateId;
            return this;
        }

        public ContentTemplateBuilder originalTemplate(ModuleCompleteKey originalTemplate) {
            this.originalTemplate = Optional.ofNullable(originalTemplate);
            return this;
        }

        public ContentTemplateBuilder referencingBlueprint(ContentBlueprintId referencingBlueprint) {
            this.referencingBlueprint = Optional.ofNullable(referencingBlueprint);
            return this;
        }

        public ContentTemplateBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ContentTemplateBuilder description(String description) {
            this.description = description == null ? "" : description;
            return this;
        }

        public ContentTemplateBuilder space(Space space) {
            this.space = space;
            return this;
        }

        public ContentTemplateBuilder labels(List<Label> labels) {
            this.labels.addAll(labels);
            return this;
        }

        public ContentTemplateBuilder templateType(ContentTemplateType templateType) {
            this.templateType = templateType;
            return this;
        }

        public ContentTemplate build() {
            return new ContentTemplate(this);
        }
    }
}

