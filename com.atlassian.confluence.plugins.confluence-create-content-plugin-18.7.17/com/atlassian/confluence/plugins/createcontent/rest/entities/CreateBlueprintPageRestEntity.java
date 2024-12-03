/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.createcontent.rest.entities;

import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CreateBlueprintPageRestEntity
implements CreateBlueprintPageEntity {
    @JsonProperty
    private final String moduleCompleteKey;
    @JsonProperty
    private final String spaceKey;
    @JsonProperty
    private final String contentBlueprintId;
    @JsonProperty
    private final String contentTemplateId;
    @JsonProperty
    private final String contentTemplateKey;
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String viewPermissionsUsers;
    @JsonProperty
    private final long parentPageId;
    @JsonProperty
    private final Map<String, Object> context;
    @JsonProperty
    private final long spaceId;

    @Deprecated
    public CreateBlueprintPageRestEntity(String spaceKey, String contentBlueprintId, String contentTemplateId, String contentTemplateKey, String title, String viewPermissionsUsers, long parentPageId, String moduleCompleteKey, Map<String, Object> context) {
        this(spaceKey, contentBlueprintId, contentTemplateId, contentTemplateKey, title, viewPermissionsUsers, parentPageId, moduleCompleteKey, context, 0L);
    }

    @JsonCreator
    public CreateBlueprintPageRestEntity(@JsonProperty(value="spaceKey") String spaceKey, @JsonProperty(value="contentBlueprintId") String contentBlueprintId, @JsonProperty(value="contentTemplateId") String contentTemplateId, @JsonProperty(value="contentTemplateKey") String contentTemplateKey, @JsonProperty(value="title") String title, @JsonProperty(value="viewPermissionsUsers") String viewPermissionsUsers, @JsonProperty(value="parentPageId") long parentPageId, @JsonProperty(value="moduleCompleteKey") String moduleCompleteKey, @JsonProperty(value="context") Map<String, Object> context, @JsonProperty(value="spaceId") long spaceId) {
        this.spaceKey = spaceKey;
        this.contentBlueprintId = contentBlueprintId;
        this.contentTemplateId = contentTemplateId;
        this.contentTemplateKey = contentTemplateKey;
        this.title = title;
        this.viewPermissionsUsers = viewPermissionsUsers;
        this.parentPageId = parentPageId;
        this.moduleCompleteKey = moduleCompleteKey;
        this.context = context;
        this.spaceId = spaceId;
    }

    @Override
    public long getSpaceId() {
        return this.spaceId;
    }

    @Override
    public String getSpaceKey() {
        return this.spaceKey;
    }

    @Override
    public long getParentPageId() {
        return this.parentPageId;
    }

    @Override
    public String getModuleCompleteKey() {
        return this.moduleCompleteKey;
    }

    @Override
    public Map<String, Object> getContext() {
        return this.context;
    }

    @Override
    public String getContentBlueprintId() {
        return this.contentBlueprintId;
    }

    @Override
    public String getContentTemplateId() {
        return this.contentTemplateId;
    }

    @Override
    public String getContentTemplateKey() {
        return this.contentTemplateKey;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getViewPermissionsUsers() {
        return this.viewPermissionsUsers;
    }

    public boolean equals(Object entity) {
        if (entity instanceof CreateBlueprintPageEntity) {
            CreateBlueprintPageEntity e = (CreateBlueprintPageEntity)entity;
            return this.title.equals(e.getTitle());
        }
        return false;
    }

    public int hashCode() {
        return this.title.hashCode();
    }

    public static class Builder {
        private String moduleCompleteKey;
        private String spaceKey;
        private String contentBlueprintId;
        private String contentTemplateId;
        private String contentTemplateKey;
        private String title;
        private String viewPermissionsUsers;
        private long parentPageId;
        private Map<String, Object> context;
        private long spaceId;

        public Builder() {
        }

        public Builder(CreateBlueprintPageEntity entity) {
            this.spaceKey = entity.getSpaceKey();
            this.moduleCompleteKey = entity.getModuleCompleteKey();
            this.contentBlueprintId = entity.getContentBlueprintId();
            this.contentTemplateId = entity.getContentTemplateId();
            this.contentTemplateKey = entity.getContentTemplateKey();
            this.title = entity.getTitle();
            this.viewPermissionsUsers = entity.getViewPermissionsUsers();
            this.parentPageId = entity.getParentPageId();
            this.context = entity.getContext();
            this.spaceId = entity.getSpaceId();
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder spaceId(long spaceId) {
            this.spaceId = spaceId;
            return this;
        }

        public Builder spaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        public Builder viewPermissionsUsers(String viewPermissionsUsers) {
            this.viewPermissionsUsers = viewPermissionsUsers;
            return this;
        }

        public Builder context(Map<String, Object> context) {
            this.context = context;
            return this;
        }

        public CreateBlueprintPageRestEntity build() {
            return new CreateBlueprintPageRestEntity(this.spaceKey, this.contentBlueprintId, this.contentTemplateId, this.contentTemplateKey, this.title, this.viewPermissionsUsers, this.parentPageId, this.moduleCompleteKey, this.context, this.spaceId);
        }
    }
}

