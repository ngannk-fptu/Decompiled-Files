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
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonValue;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class ContentTemplateId {
    private ContentTemplateId() {
    }

    @JsonCreator
    public static ContentTemplateId fromString(String value) {
        if (ContentTemplateIdWithKeys.isKeys(value)) {
            return new ContentTemplateIdWithKeys(value);
        }
        if (ContentTemplateIdWithUUID.isUUID(value)) {
            return new ContentTemplateIdWithUUID(value);
        }
        return new ContentTemplateIdWithId(value);
    }

    public static ContentTemplateId fromLong(long templateId) {
        return new ContentTemplateIdWithId(templateId);
    }

    @JsonValue
    public abstract String serialise();

    public String toString() {
        return this.serialise();
    }

    public boolean equals(Object contentTemplateId) {
        return contentTemplateId instanceof ContentTemplateId && this.serialise().equals(((ContentTemplateId)contentTemplateId).serialise());
    }

    public int hashCode() {
        return this.serialise().hashCode();
    }

    @ExperimentalApi
    @JsonIgnoreProperties(ignoreUnknown=true)
    @CustomSoyDataMapper(value="jackson2soy")
    @Internal
    public static final class ContentTemplateIdWithKeys
    extends ContentTemplateId {
        public static final String KEY_SPACE_DELIMITER = "@";
        private final ModuleCompleteKey moduleCompleteKey;
        private final Optional<String> spaceKey;

        private ContentTemplateIdWithKeys(String moduleCompleteKeyAndSpace) {
            String[] values = new String[]{moduleCompleteKeyAndSpace};
            if (moduleCompleteKeyAndSpace.contains(KEY_SPACE_DELIMITER)) {
                values = moduleCompleteKeyAndSpace.split(KEY_SPACE_DELIMITER);
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

        @Deprecated
        @JsonIgnore
        public Option<String> getSpaceKey() {
            return FugueConversionUtil.toComOption(this.spaceKey);
        }

        @JsonIgnore
        public Optional<String> spaceKey() {
            return this.spaceKey;
        }

        @JsonIgnore
        public String getModuleCompleteKey() {
            return this.moduleCompleteKey.getCompleteKey();
        }

        @JsonIgnore
        public ModuleCompleteKey getKey() {
            return this.moduleCompleteKey;
        }

        @Override
        public String serialise() {
            return this.moduleCompleteKey + this.spaceKey.map(k -> KEY_SPACE_DELIMITER + k).orElse("");
        }

        public static boolean isKeys(String value) {
            return value.contains(KEY_SPACE_DELIMITER) || value.contains(":");
        }
    }

    @ExperimentalApi
    @JsonIgnoreProperties(ignoreUnknown=true)
    @CustomSoyDataMapper(value="jackson2soy")
    @Internal
    public static final class ContentTemplateIdWithId
    extends ContentTemplateId {
        private final long id;

        private ContentTemplateIdWithId(String value) {
            try {
                this.id = Long.parseLong(value);
            }
            catch (NumberFormatException ex) {
                throw new BadRequestException("Cannot parse contentTemplateId: " + value, ex);
            }
        }

        private ContentTemplateIdWithId(long templateId) {
            this.id = templateId;
        }

        @JsonIgnore
        public long getId() {
            return this.id;
        }

        @Override
        public String serialise() {
            return Long.toString(this.id);
        }
    }

    @ExperimentalApi
    @JsonIgnoreProperties(ignoreUnknown=true)
    @CustomSoyDataMapper(value="jackson2soy")
    @Internal
    public static final class ContentTemplateIdWithUUID
    extends ContentTemplateId {
        private static final Pattern PATTERN_UUID = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        private final UUID uuid;

        private ContentTemplateIdWithUUID(String id) {
            try {
                this.uuid = UUID.fromString(id);
            }
            catch (IllegalArgumentException ex) {
                throw new BadRequestException("Cannot parse contentTemplateId: " + id, ex);
            }
        }

        @JsonIgnore
        public UUID getUuid() {
            return this.uuid;
        }

        @Override
        public String serialise() {
            return this.uuid.toString();
        }

        public static boolean isUUID(String value) {
            return PATTERN_UUID.matcher(value).matches();
        }
    }
}

