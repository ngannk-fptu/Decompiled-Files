/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.spi.StreamsKeyProvider$StreamsKey
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.streams.internal.rest.representations;

import com.atlassian.streams.spi.StreamsKeyProvider;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class StreamsKeysRepresentation {
    @JsonProperty
    private final Collection<StreamsKeyEntry> keys;
    private static final Function<StreamsKeyProvider.StreamsKey, StreamsKeyEntry> toValueMapEnties = new Function<StreamsKeyProvider.StreamsKey, StreamsKeyEntry>(){

        public StreamsKeyEntry apply(StreamsKeyProvider.StreamsKey streamsKey) {
            return new StreamsKeyEntry(streamsKey.getKey(), streamsKey.getLabel());
        }
    };

    @JsonCreator
    public StreamsKeysRepresentation(@JsonProperty(value="keys") Collection<StreamsKeyEntry> keys) {
        this.keys = ImmutableList.copyOf(keys);
    }

    public StreamsKeysRepresentation(Iterable<StreamsKeyProvider.StreamsKey> streamsKeys) {
        this.keys = Collections2.transform((Collection)ImmutableList.copyOf(streamsKeys), toValueMapEnties);
    }

    public Collection<StreamsKeyEntry> getKeys() {
        return this.keys;
    }

    public static class StreamsKeyEntry {
        @JsonProperty
        private final String key;
        @JsonProperty
        private final String label;

        @JsonCreator
        public StreamsKeyEntry(@JsonProperty(value="key") String key, @JsonProperty(value="label") String label) {
            this.key = (String)Preconditions.checkNotNull((Object)key, (Object)("Streams key entry is null for label: " + label));
            this.label = (String)Preconditions.checkNotNull((Object)label, (Object)("Label is null for key: " + key));
        }

        public String getKey() {
            return this.key;
        }

        public String getLabel() {
            return this.label;
        }
    }
}

