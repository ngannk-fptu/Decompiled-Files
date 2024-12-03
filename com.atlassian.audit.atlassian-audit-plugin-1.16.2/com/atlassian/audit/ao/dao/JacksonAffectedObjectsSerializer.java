/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditResource
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.audit.ao.dao.AffectedObjectsSerializer;
import com.atlassian.audit.entity.AuditResource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

public class JacksonAffectedObjectsSerializer
implements AffectedObjectsSerializer {
    private final ObjectMapper objectMapper;

    public JacksonAffectedObjectsSerializer(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Override
    public List<AuditResource> deserialize(@Nonnull String s) {
        try {
            return Stream.of((Object[])this.objectMapper.readValue(s, AuditResourceData[].class)).map(x -> AuditResource.builder((String)Optional.ofNullable(x.getName()).orElse(Optional.ofNullable(x.getId()).orElse("Unknown")), (String)x.getType()).uri(x.getUri()).id(x.getId()).build()).collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serialize(@Nonnull List<AuditResource> affectedObjects) {
        AuditResourceData[] values = (AuditResourceData[])affectedObjects.stream().map(x -> new AuditResourceData(x.getName(), x.getType(), x.getUri(), x.getId())).toArray(AuditResourceData[]::new);
        try {
            return this.objectMapper.writeValueAsString((Object)values);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class AuditResourceData {
        private final String name;
        private final String type;
        private final String uri;
        private final String id;

        @JsonCreator
        public AuditResourceData(@JsonProperty(value="name") String name, @JsonProperty(value="type") String type, @JsonProperty(value="uri") String uri, @JsonProperty(value="id") String id) {
            this.name = name;
            this.type = type;
            this.uri = uri;
            this.id = id;
        }

        public String getName() {
            return this.name;
        }

        public String getType() {
            return this.type;
        }

        public String getUri() {
            return this.uri;
        }

        public String getId() {
            return this.id;
        }
    }
}

