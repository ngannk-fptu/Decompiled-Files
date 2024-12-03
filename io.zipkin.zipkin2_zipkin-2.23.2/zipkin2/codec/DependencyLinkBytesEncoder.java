/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.codec;

import java.util.List;
import zipkin2.DependencyLink;
import zipkin2.codec.BytesEncoder;
import zipkin2.codec.Encoding;
import zipkin2.internal.JsonCodec;
import zipkin2.internal.JsonEscaper;
import zipkin2.internal.WriteBuffer;

public enum DependencyLinkBytesEncoder implements BytesEncoder<DependencyLink>
{
    JSON_V1{

        @Override
        public Encoding encoding() {
            return Encoding.JSON;
        }

        @Override
        public int sizeInBytes(DependencyLink input) {
            return WRITER.sizeInBytes(input);
        }

        @Override
        public byte[] encode(DependencyLink link) {
            return JsonCodec.write(WRITER, link);
        }

        @Override
        public byte[] encodeList(List<DependencyLink> links) {
            return JsonCodec.writeList(WRITER, links);
        }
    };

    static final WriteBuffer.Writer<DependencyLink> WRITER;

    static {
        WRITER = new WriteBuffer.Writer<DependencyLink>(){

            @Override
            public int sizeInBytes(DependencyLink value) {
                int sizeInBytes = 37;
                sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(value.parent());
                sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(value.child());
                sizeInBytes += WriteBuffer.asciiSizeInBytes(value.callCount());
                if (value.errorCount() > 0L) {
                    sizeInBytes += 14;
                    sizeInBytes += WriteBuffer.asciiSizeInBytes(value.errorCount());
                }
                return sizeInBytes;
            }

            @Override
            public void write(DependencyLink value, WriteBuffer b) {
                b.writeAscii("{\"parent\":\"");
                b.writeUtf8(JsonEscaper.jsonEscape(value.parent()));
                b.writeAscii("\",\"child\":\"");
                b.writeUtf8(JsonEscaper.jsonEscape(value.child()));
                b.writeAscii("\",\"callCount\":");
                b.writeAscii(value.callCount());
                if (value.errorCount() > 0L) {
                    b.writeAscii(",\"errorCount\":");
                    b.writeAscii(value.errorCount());
                }
                b.writeByte(125);
            }

            public String toString() {
                return "DependencyLink";
            }
        };
    }
}

