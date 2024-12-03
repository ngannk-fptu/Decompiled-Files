/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.plugins.mobile.dto.AbstractPageDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.CurrentUserMetadataDto;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpaceDto {
    public static final String ADD_PAGE_PERMISSION_NAME = "addPage";
    @JsonProperty
    private long id;
    @JsonProperty
    private String key;
    @JsonProperty
    private String name;
    @JsonProperty
    private String type;
    @JsonProperty
    private String logoPath;
    @JsonProperty
    private AbstractPageDto homePage;
    @JsonProperty
    private String resultType;
    @JsonProperty
    private CurrentUserMetadataDto currentUser;
    @JsonProperty
    private Map<String, Boolean> permissions;

    public long getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public String getResultType() {
        return this.resultType;
    }

    public CurrentUserMetadataDto getCurrentUser() {
        return this.currentUser;
    }

    @JsonCreator
    private SpaceDto() {
        this(SpaceDto.builder());
    }

    private SpaceDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.key = builder.key;
        this.logoPath = builder.logoPath;
        this.homePage = builder.homePage;
        this.type = builder.type;
        this.resultType = builder.resultType;
        this.currentUser = builder.currentUser;
        this.permissions = builder.permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getLogoPath() {
        return this.logoPath;
    }

    public AbstractPageDto getHomePage() {
        return this.homePage;
    }

    public Map<String, Boolean> getPermissions() {
        return this.permissions;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpaceDto that = (SpaceDto)o;
        return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getLogoPath(), that.getLogoPath());
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return 31 * result + (this.logoPath != null ? this.getLogoPath().hashCode() : 0);
    }

    public static final class Builder {
        private long id;
        private String key;
        private String name;
        private String logoPath;
        private String type;
        private AbstractPageDto homePage;
        private String resultType;
        private CurrentUserMetadataDto currentUser;
        private Map<String, Boolean> permissions = new HashMap<String, Boolean>();

        private Builder() {
        }

        public SpaceDto build() {
            return new SpaceDto(this);
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder addPermission(String permissionName, Supplier<Boolean> permissionFunction) {
            this.permissions.put(permissionName, permissionFunction.get());
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder logoPath(String logoPath) {
            this.logoPath = logoPath;
            return this;
        }

        public Builder homePage(AbstractPageDto homePage) {
            this.homePage = homePage;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder resultType(String resultType) {
            this.resultType = resultType;
            return this;
        }

        public Builder currentUser(CurrentUserMetadataDto currentUser) {
            this.currentUser = currentUser;
            return this;
        }
    }

    public static enum ResultType {
        FAVOURITE("favourite"),
        RECENT("recent"),
        OTHER("other");

        private String value;

        private ResultType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
}

