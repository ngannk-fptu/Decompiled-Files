/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.audit.ao.dao.ChangedValuesSerializer;
import com.atlassian.audit.entity.ChangedValue;
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

public class JacksonChangedValuesSerializer
implements ChangedValuesSerializer {
    private final ObjectMapper objectMapper;

    public JacksonChangedValuesSerializer(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Override
    public List<ChangedValue> deserialize(@Nonnull String s) {
        try {
            return Stream.of((Object[])this.objectMapper.readValue(s, ChangedValueData[].class)).map(x -> ChangedValue.fromI18nKeys((String)x.getI18nKey()).withKeyTranslation(x.getKey()).from(x.getFrom()).to(x.getTo()).build()).collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serialize(@Nonnull Iterable<ChangedValue> changedValues) {
        try {
            ChangedValueData[] values = (ChangedValueData[])StreamSupport.stream(changedValues.spliterator(), false).map(x -> new ChangedValueData(x.getI18nKey(), x.getKey(), x.getFrom(), x.getTo())).toArray(ChangedValueData[]::new);
            return this.objectMapper.writeValueAsString((Object)values);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ChangedValueData {
        private final String i18nKey;
        private final String key;
        private final String from;
        private final String to;

        @JsonCreator
        public ChangedValueData(@JsonProperty(value="i18nKey") String i18nKey, @JsonProperty(value="key") String key, @JsonProperty(value="from") String from, @JsonProperty(value="to") String to) {
            this.i18nKey = i18nKey == null ? key : i18nKey;
            this.key = key;
            this.from = from;
            this.to = to;
        }

        public String getKey() {
            return this.key;
        }

        public String getI18nKey() {
            return this.i18nKey;
        }

        public String getFrom() {
            return this.from;
        }

        public String getTo() {
            return this.to;
        }
    }
}

