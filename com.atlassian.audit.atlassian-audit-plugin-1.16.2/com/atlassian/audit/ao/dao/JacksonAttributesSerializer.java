/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAttribute
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.audit.ao.dao.AttributesSerializer;
import com.atlassian.audit.entity.AuditAttribute;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

public class JacksonAttributesSerializer
implements AttributesSerializer {
    private final ObjectMapper objectMapper;

    public JacksonAttributesSerializer(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Override
    public List<AuditAttribute> deserialize(@Nonnull String s) {
        try {
            return Stream.of((Object[])this.objectMapper.readValue(s, AuditAttributeData[].class)).map(x -> AuditAttribute.fromI18nKeys((String)x.getNameI18nKey(), (String)x.getValue()).withNameTranslation(x.getName()).build()).collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serialize(@Nonnull Iterable<AuditAttribute> changedValues) {
        AuditAttributeData[] values = (AuditAttributeData[])StreamSupport.stream(changedValues.spliterator(), false).map(x -> new AuditAttributeData(x.getNameI18nKey(), x.getName(), x.getValue())).toArray(AuditAttributeData[]::new);
        try {
            return this.objectMapper.writeValueAsString((Object)values);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class AuditAttributeData {
        private final String name;
        private final String nameI18nKey;
        private final String value;

        @JsonCreator
        public AuditAttributeData(@JsonProperty(value="nameI18nKey") String nameI18nKey, @JsonProperty(value="name") String name, @JsonProperty(value="value") String value) {
            this.nameI18nKey = nameI18nKey == null ? name : nameI18nKey;
            this.name = name == null ? nameI18nKey : name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getNameI18nKey() {
            return this.nameI18nKey;
        }

        public String getValue() {
            return this.value;
        }
    }
}

