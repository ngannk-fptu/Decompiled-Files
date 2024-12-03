/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 *  com.atlassian.fugue.Option
 *  com.atlassian.soy.renderer.CustomSoyDataMapper
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.api.model.content.template;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.plugin.ModuleCompleteKey;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import com.atlassian.soy.renderer.CustomSoyDataMapper;
import java.util.Objects;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonValue;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class ContentBlueprintId {
    public static final String KEY_SPACE_DELIMITER = "@";

    protected ContentBlueprintId() {
    }

    @JsonCreator
    public static ContentBlueprintId fromString(String value) {
        if (ContentBlueprintIdWithKeys.isKeys(value)) {
            return new ContentBlueprintIdWithKeys(value);
        }
        return new ContentBlueprintIdWithId(value);
    }

    public static ContentBlueprintId fromKeyAndSpaceString(String moduleCompleteKey, String space) {
        return new ContentBlueprintIdWithKeys(moduleCompleteKey, space);
    }

    @JsonValue
    public abstract String serialise();

    @ExperimentalApi
    @JsonIgnoreProperties(ignoreUnknown=true)
    @CustomSoyDataMapper(value="jackson2soy")
    @Internal
    public static class ContentBlueprintIdWithKeys
    extends ContentBlueprintId {
        private final ModuleCompleteKey moduleCompleteKey;
        private final Optional<String> spaceKey;

        private ContentBlueprintIdWithKeys(String moduleKey, String spaceKey) {
            this.moduleCompleteKey = new ModuleCompleteKey(moduleKey);
            this.spaceKey = Optional.ofNullable(spaceKey);
        }

        private ContentBlueprintIdWithKeys(String moduleCompleteKeyAndSpace) {
            String[] values = new String[]{moduleCompleteKeyAndSpace};
            if (moduleCompleteKeyAndSpace.contains(ContentBlueprintId.KEY_SPACE_DELIMITER)) {
                values = moduleCompleteKeyAndSpace.split(ContentBlueprintId.KEY_SPACE_DELIMITER);
                if (values.length != 2) {
                    throw new BadRequestException("Cannot parse contentTemplateId: " + moduleCompleteKeyAndSpace);
                }
                this.moduleCompleteKey = new ModuleCompleteKey(values[0]);
                this.spaceKey = Optional.of(values[1]);
            } else {
                this.moduleCompleteKey = new ModuleCompleteKey(values[0]);
                this.spaceKey = Optional.empty();
            }
        }

        public String getModuleCompleteKey() {
            return this.moduleCompleteKey.getCompleteKey();
        }

        @Deprecated
        public Option<String> getSpaceKey() {
            return FugueConversionUtil.toComOption(this.spaceKey);
        }

        public Optional<String> spaceKey() {
            return this.spaceKey;
        }

        @Override
        public String serialise() {
            return this.moduleCompleteKey + this.spaceKey.map(k -> ContentBlueprintId.KEY_SPACE_DELIMITER + k).orElse("");
        }

        public boolean equals(Object o) {
            if (o instanceof ContentBlueprintIdWithKeys) {
                ContentBlueprintIdWithKeys other = (ContentBlueprintIdWithKeys)o;
                return Objects.equals(this.moduleCompleteKey, other.moduleCompleteKey) && Objects.equals(this.spaceKey, other.spaceKey);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.moduleCompleteKey, this.spaceKey);
        }

        public static boolean isKeys(String value) {
            return value.contains(ContentBlueprintId.KEY_SPACE_DELIMITER) || value.contains(":");
        }
    }

    @ExperimentalApi
    @JsonIgnoreProperties(ignoreUnknown=true)
    @CustomSoyDataMapper(value="jackson2soy")
    @Internal
    public static final class ContentBlueprintIdWithId
    extends ContentBlueprintId {
        private final String contentBlueprintId;

        private ContentBlueprintIdWithId(String id) {
            this.contentBlueprintId = id;
        }

        @JsonIgnore
        public String getId() {
            return this.contentBlueprintId;
        }

        @Override
        public String serialise() {
            return this.contentBlueprintId;
        }
    }
}

