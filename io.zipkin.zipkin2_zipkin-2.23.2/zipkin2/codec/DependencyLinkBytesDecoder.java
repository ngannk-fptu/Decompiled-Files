/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.codec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import zipkin2.DependencyLink;
import zipkin2.codec.BytesDecoder;
import zipkin2.codec.Encoding;
import zipkin2.internal.JsonCodec;
import zipkin2.internal.Nullable;
import zipkin2.internal.ReadBuffer;

public enum DependencyLinkBytesDecoder implements BytesDecoder<DependencyLink>
{
    JSON_V1{

        @Override
        public Encoding encoding() {
            return Encoding.JSON;
        }

        @Override
        public boolean decode(byte[] link, Collection<DependencyLink> out) {
            return JsonCodec.read(READER, ReadBuffer.wrap(link), out);
        }

        @Override
        @Nullable
        public DependencyLink decodeOne(byte[] link) {
            return (DependencyLink)JsonCodec.readOne(READER, ReadBuffer.wrap(link));
        }

        @Override
        public boolean decodeList(byte[] links, Collection<DependencyLink> out) {
            return JsonCodec.readList(READER, ReadBuffer.wrap(links), out);
        }

        @Override
        public List<DependencyLink> decodeList(byte[] links) {
            ArrayList<DependencyLink> out = new ArrayList<DependencyLink>();
            this.decodeList(links, (Collection<DependencyLink>)out);
            return out;
        }
    };

    static final JsonCodec.JsonReaderAdapter<DependencyLink> READER;

    static {
        READER = new JsonCodec.JsonReaderAdapter<DependencyLink>(){

            @Override
            public DependencyLink fromJson(JsonCodec.JsonReader reader) throws IOException {
                DependencyLink.Builder result = DependencyLink.newBuilder();
                reader.beginObject();
                while (reader.hasNext()) {
                    String nextName = reader.nextName();
                    if (nextName.equals("parent")) {
                        result.parent(reader.nextString());
                        continue;
                    }
                    if (nextName.equals("child")) {
                        result.child(reader.nextString());
                        continue;
                    }
                    if (nextName.equals("callCount")) {
                        result.callCount(reader.nextLong());
                        continue;
                    }
                    if (nextName.equals("errorCount")) {
                        result.errorCount(reader.nextLong());
                        continue;
                    }
                    reader.skipValue();
                }
                reader.endObject();
                return result.build();
            }

            public String toString() {
                return "DependencyLink";
            }
        };
    }
}

