/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import zipkin2.Endpoint;
import zipkin2.internal.ReadBuffer;
import zipkin2.internal.ThriftCodec;
import zipkin2.internal.ThriftField;
import zipkin2.internal.WriteBuffer;

final class ThriftEndpointCodec {
    static final byte[] INT_ZERO = new byte[]{0, 0, 0, 0};
    static final ThriftField IPV4 = new ThriftField(8, 1);
    static final ThriftField PORT = new ThriftField(6, 2);
    static final ThriftField SERVICE_NAME = new ThriftField(11, 3);
    static final ThriftField IPV6 = new ThriftField(11, 4);

    ThriftEndpointCodec() {
    }

    static Endpoint read(ReadBuffer buffer) {
        Endpoint.Builder result = Endpoint.newBuilder();
        while (true) {
            ThriftField thriftField = ThriftField.read(buffer);
            if (thriftField.type == 0) break;
            if (thriftField.isEqualTo(IPV4)) {
                int ipv4 = buffer.readInt();
                if (ipv4 == 0) continue;
                result.parseIp(new byte[]{(byte)(ipv4 >> 24 & 0xFF), (byte)(ipv4 >> 16 & 0xFF), (byte)(ipv4 >> 8 & 0xFF), (byte)(ipv4 & 0xFF)});
                continue;
            }
            if (thriftField.isEqualTo(PORT)) {
                result.port(buffer.readShort() & 0xFFFF);
                continue;
            }
            if (thriftField.isEqualTo(SERVICE_NAME)) {
                result.serviceName(buffer.readUtf8(buffer.readInt()));
                continue;
            }
            if (thriftField.isEqualTo(IPV6)) {
                result.parseIp(buffer.readBytes(buffer.readInt()));
                continue;
            }
            ThriftCodec.skip(buffer, thriftField.type);
        }
        return result.build();
    }

    static int sizeInBytes(Endpoint value) {
        String serviceName = value.serviceName();
        int sizeInBytes = 0;
        sizeInBytes += 7;
        sizeInBytes += 5;
        sizeInBytes += 7 + (serviceName != null ? WriteBuffer.utf8SizeInBytes(serviceName) : 0);
        if (value.ipv6() != null) {
            sizeInBytes += 23;
        }
        return ++sizeInBytes;
    }

    static void write(Endpoint value, WriteBuffer buffer) {
        IPV4.write(buffer);
        buffer.write(value.ipv4Bytes() != null ? value.ipv4Bytes() : INT_ZERO);
        PORT.write(buffer);
        int port = value.portAsInt();
        buffer.writeByte(port >>> 8 & 0xFF);
        buffer.writeByte(port & 0xFF);
        SERVICE_NAME.write(buffer);
        ThriftCodec.writeLengthPrefixed(buffer, value.serviceName() != null ? value.serviceName() : "");
        byte[] ipv6 = value.ipv6Bytes();
        if (ipv6 != null) {
            IPV6.write(buffer);
            ThriftCodec.writeInt(buffer, 16);
            buffer.write(ipv6);
        }
        buffer.writeByte(0);
    }
}

