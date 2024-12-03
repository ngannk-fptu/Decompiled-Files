/*
 * Decompiled with CFR 0.152.
 */
package zipkin2;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Locale;
import zipkin2.internal.HexCodec;
import zipkin2.internal.Nullable;
import zipkin2.internal.RecyclableBuffers;

public final class Endpoint
implements Serializable {
    private static final long serialVersionUID = 0L;
    static final int IPV6_PART_COUNT = 8;
    final String serviceName;
    final String ipv4;
    final String ipv6;
    final byte[] ipv4Bytes;
    final byte[] ipv6Bytes;
    final int port;

    @Nullable
    public String serviceName() {
        return this.serviceName;
    }

    @Nullable
    public String ipv4() {
        return this.ipv4;
    }

    @Nullable
    public byte[] ipv4Bytes() {
        return this.ipv4Bytes;
    }

    @Nullable
    public String ipv6() {
        return this.ipv6;
    }

    @Nullable
    public byte[] ipv6Bytes() {
        return this.ipv6Bytes;
    }

    @Nullable
    public Integer port() {
        return this.port != 0 ? Integer.valueOf(this.port) : null;
    }

    public int portAsInt() {
        return this.port;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    static IpFamily detectFamily(String ipString) {
        char c;
        boolean hasColon = false;
        boolean hasDot = false;
        int length = ipString.length();
        for (int i = 0; i < length; ++i) {
            c = ipString.charAt(i);
            if (c == '.') {
                hasDot = true;
                continue;
            }
            if (c == ':') {
                if (hasDot) {
                    return IpFamily.Unknown;
                }
                hasColon = true;
                continue;
            }
            if (!Endpoint.notHex(c)) continue;
            return IpFamily.Unknown;
        }
        if (hasColon) {
            if (hasDot) {
                int lastColonIndex = ipString.lastIndexOf(58);
                if (!Endpoint.isValidIpV4Address(ipString, lastColonIndex + 1, ipString.length())) {
                    return IpFamily.Unknown;
                }
                if (lastColonIndex == 1 && ipString.charAt(0) == ':') {
                    return IpFamily.IPv4Embedded;
                }
                if (lastColonIndex != 6 || ipString.charAt(0) != ':' || ipString.charAt(1) != ':') {
                    return IpFamily.Unknown;
                }
                for (int i = 2; i < 6; ++i) {
                    c = ipString.charAt(i);
                    if (c == 'f' || c == 'F' || c == '0') continue;
                    return IpFamily.Unknown;
                }
                return IpFamily.IPv4Embedded;
            }
            return IpFamily.IPv6;
        }
        if (hasDot && Endpoint.isValidIpV4Address(ipString, 0, ipString.length())) {
            return IpFamily.IPv4;
        }
        return IpFamily.Unknown;
    }

    static boolean notHex(char c) {
        return !(c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F');
    }

    static String writeIpV6(byte[] ipv6) {
        int i;
        int pos = 0;
        char[] buf = RecyclableBuffers.shortStringBuffer();
        int zeroCompressionIndex = -1;
        int zeroCompressionLength = -1;
        int zeroIndex = -1;
        boolean allZeros = true;
        for (i = 0; i < ipv6.length; i += 2) {
            if (ipv6[i] == 0 && ipv6[i + 1] == 0) {
                if (zeroIndex >= 0) continue;
                zeroIndex = i;
                continue;
            }
            allZeros = false;
            if (zeroIndex < 0) continue;
            int zeroLength = i - zeroIndex;
            if (zeroLength > zeroCompressionLength) {
                zeroCompressionIndex = zeroIndex;
                zeroCompressionLength = zeroLength;
            }
            zeroIndex = -1;
        }
        if (allZeros) {
            return "::";
        }
        if (zeroCompressionIndex == -1 && zeroIndex != -1) {
            zeroCompressionIndex = zeroIndex;
            zeroCompressionLength = 16 - zeroIndex;
        }
        i = 0;
        while (i < ipv6.length) {
            if (i == zeroCompressionIndex) {
                buf[pos++] = 58;
                if ((i += zeroCompressionLength) != ipv6.length) continue;
                buf[pos++] = 58;
                continue;
            }
            if (i != 0) {
                buf[pos++] = 58;
            }
            byte high = ipv6[i++];
            byte low = ipv6[i++];
            char val = HexCodec.HEX_DIGITS[high >> 4 & 0xF];
            boolean leadingZero = val == '0';
            if (!leadingZero) {
                buf[pos++] = val;
            }
            val = HexCodec.HEX_DIGITS[high & 0xF];
            if (!(leadingZero = leadingZero && val == '0')) {
                buf[pos++] = val;
            }
            val = HexCodec.HEX_DIGITS[low >> 4 & 0xF];
            if (!leadingZero || val != '0') {
                buf[pos++] = val;
            }
            buf[pos++] = HexCodec.HEX_DIGITS[low & 0xF];
        }
        return new String(buf, 0, pos);
    }

    @Nullable
    static byte[] textToNumericFormatV6(String ipString) {
        int partsLo;
        int partsHi;
        String[] parts = ipString.split(":", 10);
        if (parts.length < 3 || parts.length > 9) {
            return null;
        }
        int skipIndex = -1;
        for (int i = 1; i < parts.length - 1; ++i) {
            if (parts[i].length() != 0) continue;
            if (skipIndex >= 0) {
                return null;
            }
            skipIndex = i;
        }
        if (skipIndex >= 0) {
            partsHi = skipIndex;
            partsLo = parts.length - skipIndex - 1;
            if (parts[0].length() == 0 && --partsHi != 0) {
                return null;
            }
            if (parts[parts.length - 1].length() == 0 && --partsLo != 0) {
                return null;
            }
        } else {
            partsHi = parts.length;
            partsLo = 0;
        }
        int partsSkipped = 8 - (partsHi + partsLo);
        if (!(skipIndex < 0 ? partsSkipped == 0 : partsSkipped >= 1)) {
            return null;
        }
        ByteBuffer rawBytes = ByteBuffer.allocate(16);
        try {
            int i;
            for (i = 0; i < partsHi; ++i) {
                rawBytes.putShort(Endpoint.parseHextet(parts[i]));
            }
            for (i = 0; i < partsSkipped; ++i) {
                rawBytes.putShort((short)0);
            }
            for (i = partsLo; i > 0; --i) {
                rawBytes.putShort(Endpoint.parseHextet(parts[parts.length - i]));
            }
        }
        catch (NumberFormatException ex) {
            return null;
        }
        return rawBytes.array();
    }

    static short parseHextet(String ipPart) {
        int hextet = Integer.parseInt(ipPart, 16);
        if (hextet > 65535) {
            throw new NumberFormatException();
        }
        return (short)hextet;
    }

    static boolean isValidIpV4Address(String ip, int from, int toExcluded) {
        int i;
        int len = toExcluded - from;
        return len <= 15 && len >= 7 && (i = ip.indexOf(46, from + 1)) > 0 && Endpoint.isValidIpV4Word(ip, from, i) && (i = ip.indexOf(46, from = i + 2)) > 0 && Endpoint.isValidIpV4Word(ip, from - 1, i) && (i = ip.indexOf(46, from = i + 2)) > 0 && Endpoint.isValidIpV4Word(ip, from - 1, i) && Endpoint.isValidIpV4Word(ip, i + 1, toExcluded);
    }

    static boolean isValidIpV4Word(CharSequence word, int from, int toExclusive) {
        char c0;
        int len = toExclusive - from;
        if (len < 1 || len > 3 || (c0 = word.charAt(from)) < '0') {
            return false;
        }
        if (len == 3) {
            char c2;
            char c1 = word.charAt(from + 1);
            return c1 >= '0' && (c2 = word.charAt(from + 2)) >= '0' && (c0 <= '1' && c1 <= '9' && c2 <= '9' || c0 == '2' && c1 <= '5' && (c2 <= '5' || c1 < '5' && c2 <= '9'));
        }
        return c0 <= '9' && (len == 1 || Endpoint.isValidNumericChar(word.charAt(from + 1)));
    }

    static boolean isValidNumericChar(char c) {
        return c >= '0' && c <= '9';
    }

    Endpoint(Builder builder) {
        this.serviceName = builder.serviceName;
        this.ipv4 = builder.ipv4;
        this.ipv4Bytes = builder.ipv4Bytes;
        this.ipv6 = builder.ipv6;
        this.ipv6Bytes = builder.ipv6Bytes;
        this.port = builder.port;
    }

    Endpoint(SerializedForm serializedForm) {
        this.serviceName = serializedForm.serviceName;
        this.ipv4 = serializedForm.ipv4;
        this.ipv4Bytes = serializedForm.ipv4Bytes;
        this.ipv6 = serializedForm.ipv6;
        this.ipv6Bytes = serializedForm.ipv6Bytes;
        this.port = serializedForm.port;
    }

    public String toString() {
        return "Endpoint{serviceName=" + this.serviceName + ", ipv4=" + this.ipv4 + ", ipv6=" + this.ipv6 + ", port=" + this.port + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Endpoint)) {
            return false;
        }
        Endpoint that = (Endpoint)o;
        return (this.serviceName == null ? that.serviceName == null : this.serviceName.equals(that.serviceName)) && (this.ipv4 == null ? that.ipv4 == null : this.ipv4.equals(that.ipv4)) && (this.ipv6 == null ? that.ipv6 == null : this.ipv6.equals(that.ipv6)) && this.port == that.port;
    }

    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.serviceName == null ? 0 : this.serviceName.hashCode();
        h *= 1000003;
        h ^= this.ipv4 == null ? 0 : this.ipv4.hashCode();
        h *= 1000003;
        h ^= this.ipv6 == null ? 0 : this.ipv6.hashCode();
        h *= 1000003;
        return h ^= this.port;
    }

    final Object writeReplace() throws ObjectStreamException {
        return new SerializedForm(this);
    }

    static byte[] getIpv4Bytes(String ipv4) {
        byte[] result = new byte[4];
        int pos = 0;
        int i = 0;
        int len = ipv4.length();
        while (i < len) {
            char ch = ipv4.charAt(i++);
            int octet = ch - 48;
            if (i == len || (ch = ipv4.charAt(i++)) == '.') {
                result[pos++] = (byte)octet;
                continue;
            }
            octet = octet * 10 + (ch - 48);
            if (i == len || (ch = ipv4.charAt(i++)) == '.') {
                result[pos++] = (byte)octet;
                continue;
            }
            octet = octet * 10 + (ch - 48);
            result[pos++] = (byte)octet;
            ++i;
        }
        return result;
    }

    static final class SerializedForm
    implements Serializable {
        static final long serialVersionUID = 0L;
        final String serviceName;
        final String ipv4;
        final String ipv6;
        final byte[] ipv4Bytes;
        final byte[] ipv6Bytes;
        final int port;

        SerializedForm(Endpoint endpoint) {
            this.serviceName = endpoint.serviceName;
            this.ipv4 = endpoint.ipv4;
            this.ipv4Bytes = endpoint.ipv4Bytes;
            this.ipv6 = endpoint.ipv6;
            this.ipv6Bytes = endpoint.ipv6Bytes;
            this.port = endpoint.port;
        }

        Object readResolve() throws ObjectStreamException {
            try {
                return new Endpoint(this);
            }
            catch (IllegalArgumentException e) {
                throw new StreamCorruptedException(e.getMessage());
            }
        }
    }

    static enum IpFamily {
        Unknown,
        IPv4,
        IPv4Embedded,
        IPv6;

    }

    public static final class Builder {
        String serviceName;
        String ipv4;
        String ipv6;
        byte[] ipv4Bytes;
        byte[] ipv6Bytes;
        int port;

        Builder(Endpoint source) {
            this.serviceName = source.serviceName;
            this.ipv4 = source.ipv4;
            this.ipv6 = source.ipv6;
            this.ipv4Bytes = source.ipv4Bytes;
            this.ipv6Bytes = source.ipv6Bytes;
            this.port = source.port;
        }

        Builder merge(Endpoint source) {
            if (this.serviceName == null) {
                this.serviceName = source.serviceName;
            }
            if (this.ipv4 == null) {
                this.ipv4 = source.ipv4;
            }
            if (this.ipv6 == null) {
                this.ipv6 = source.ipv6;
            }
            if (this.ipv4Bytes == null) {
                this.ipv4Bytes = source.ipv4Bytes;
            }
            if (this.ipv6Bytes == null) {
                this.ipv6Bytes = source.ipv6Bytes;
            }
            if (this.port == 0) {
                this.port = source.port;
            }
            return this;
        }

        public Builder serviceName(@Nullable String serviceName) {
            this.serviceName = serviceName == null || serviceName.isEmpty() ? null : serviceName.toLowerCase(Locale.ROOT);
            return this;
        }

        public Builder ip(@Nullable InetAddress addr) {
            this.parseIp(addr);
            return this;
        }

        public final boolean parseIp(@Nullable InetAddress addr) {
            if (addr == null) {
                return false;
            }
            if (addr instanceof Inet4Address) {
                this.ipv4 = addr.getHostAddress();
                this.ipv4Bytes = addr.getAddress();
            } else if (addr instanceof Inet6Address) {
                byte[] addressBytes = addr.getAddress();
                if (!this.parseEmbeddedIPv4(addressBytes)) {
                    this.ipv6 = Endpoint.writeIpV6(addressBytes);
                    this.ipv6Bytes = addressBytes;
                }
            } else {
                return false;
            }
            return true;
        }

        public final boolean parseIp(byte[] ipBytes) {
            if (ipBytes == null) {
                return false;
            }
            if (ipBytes.length == 4) {
                this.ipv4Bytes = ipBytes;
                this.ipv4 = Builder.writeIpV4(ipBytes);
            } else if (ipBytes.length == 16) {
                if (!this.parseEmbeddedIPv4(ipBytes)) {
                    this.ipv6 = Endpoint.writeIpV6(ipBytes);
                    this.ipv6Bytes = ipBytes;
                }
            } else {
                return false;
            }
            return true;
        }

        static String writeIpV4(byte[] ipBytes) {
            char[] buf = RecyclableBuffers.shortStringBuffer();
            int pos = 0;
            pos = Builder.writeBackwards(ipBytes[0] & 0xFF, pos, buf);
            buf[pos++] = 46;
            pos = Builder.writeBackwards(ipBytes[1] & 0xFF, pos, buf);
            buf[pos++] = 46;
            pos = Builder.writeBackwards(ipBytes[2] & 0xFF, pos, buf);
            buf[pos++] = 46;
            pos = Builder.writeBackwards(ipBytes[3] & 0xFF, pos, buf);
            return new String(buf, 0, pos);
        }

        static int writeBackwards(int b, int pos, char[] buf) {
            if (b < 10) {
                buf[pos] = HexCodec.HEX_DIGITS[b];
                return pos + 1;
            }
            int i = pos += b < 100 ? 2 : 3;
            while (b != 0) {
                int digit = b % 10;
                buf[--i] = HexCodec.HEX_DIGITS[digit];
                b /= 10;
            }
            return pos;
        }

        public Builder ip(@Nullable String ipString) {
            this.parseIp(ipString);
            return this;
        }

        public final boolean parseIp(@Nullable String ipString) {
            if (ipString == null || ipString.isEmpty()) {
                return false;
            }
            IpFamily format = Endpoint.detectFamily(ipString);
            if (format == IpFamily.IPv4) {
                this.ipv4 = ipString;
                this.ipv4Bytes = Endpoint.getIpv4Bytes(this.ipv4);
            } else if (format == IpFamily.IPv4Embedded) {
                this.ipv4 = ipString.substring(ipString.lastIndexOf(58) + 1);
                this.ipv4Bytes = Endpoint.getIpv4Bytes(this.ipv4);
            } else if (format == IpFamily.IPv6) {
                byte[] addressBytes = Endpoint.textToNumericFormatV6(ipString);
                if (addressBytes == null) {
                    return false;
                }
                this.ipv6 = Endpoint.writeIpV6(addressBytes);
                this.ipv6Bytes = addressBytes;
            } else {
                return false;
            }
            return true;
        }

        public Builder port(@Nullable Integer port) {
            if (port != null) {
                if (port > 65535) {
                    throw new IllegalArgumentException("invalid port " + port);
                }
                if (port <= 0) {
                    port = 0;
                }
            }
            this.port = port != null ? port : 0;
            return this;
        }

        public Builder port(int port) {
            if (port > 65535) {
                throw new IllegalArgumentException("invalid port " + port);
            }
            if (port < 0) {
                port = 0;
            }
            this.port = port;
            return this;
        }

        public Endpoint build() {
            return new Endpoint(this);
        }

        Builder() {
        }

        boolean parseEmbeddedIPv4(byte[] ipv6) {
            for (int i = 0; i < 10; ++i) {
                if (ipv6[i] == 0) continue;
                return false;
            }
            int flag = (ipv6[10] & 0xFF) << 8 | ipv6[11] & 0xFF;
            if (flag != 0) {
                return false;
            }
            byte o1 = ipv6[12];
            byte o2 = ipv6[13];
            byte o3 = ipv6[14];
            byte o4 = ipv6[15];
            if (o1 == 0 && o2 == 0 && o3 == 0 && o4 == 1) {
                return false;
            }
            this.ipv4 = String.valueOf(o1 & 0xFF) + '.' + (o2 & 0xFF) + '.' + (o3 & 0xFF) + '.' + (o4 & 0xFF);
            this.ipv4Bytes = new byte[]{o1, o2, o3, o4};
            return true;
        }
    }
}

