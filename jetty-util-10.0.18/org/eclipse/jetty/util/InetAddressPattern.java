/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.net.InetAddress;
import java.util.function.Predicate;
import org.eclipse.jetty.util.StringUtil;

public abstract class InetAddressPattern
implements Predicate<InetAddress> {
    protected final String _pattern;

    public static InetAddressPattern from(String pattern) {
        if (pattern == null) {
            return null;
        }
        int slash = pattern.lastIndexOf(47);
        int dash = pattern.lastIndexOf(45);
        try {
            if (slash >= 0) {
                return new CidrInetAddressRange(pattern, InetAddress.getByName(pattern.substring(0, slash).trim()), StringUtil.toInt(pattern, slash + 1));
            }
            if (dash >= 0) {
                return new MinMaxInetAddressRange(pattern, InetAddress.getByName(pattern.substring(0, dash).trim()), InetAddress.getByName(pattern.substring(dash + 1).trim()));
            }
            return new SingletonInetAddressRange(pattern, InetAddress.getByName(pattern));
        }
        catch (Exception e) {
            try {
                if (slash < 0 && dash > 0) {
                    return new LegacyInetAddressRange(pattern);
                }
            }
            catch (Exception ex2) {
                e.addSuppressed(ex2);
            }
            throw new IllegalArgumentException("Bad pattern: " + pattern, e);
        }
    }

    public InetAddressPattern(String pattern) {
        this._pattern = pattern;
    }

    public String toString() {
        return this._pattern;
    }

    static class CidrInetAddressRange
    extends InetAddressPattern {
        final byte[] _raw;
        final int _octets;
        final int _mask;
        final int _masked;

        public CidrInetAddressRange(String pattern, InetAddress address, int cidr) {
            super(pattern);
            this._raw = address.getAddress();
            this._octets = cidr / 8;
            this._mask = 0xFF & 255 << 8 - cidr % 8;
            int n = this._masked = this._mask == 0 ? 0 : this._raw[this._octets] & this._mask;
            if (cidr > this._raw.length * 8) {
                throw new IllegalArgumentException("CIDR too large: " + pattern);
            }
            if (this._mask != 0 && (0xFF & this._raw[this._octets]) != this._masked) {
                throw new IllegalArgumentException("CIDR bits non zero: " + pattern);
            }
            for (int o = this._octets + (this._mask == 0 ? 0 : 1); o < this._raw.length; ++o) {
                if (this._raw[o] == 0) continue;
                throw new IllegalArgumentException("CIDR bits non zero: " + pattern);
            }
        }

        @Override
        public boolean test(InetAddress address) {
            byte[] raw = address.getAddress();
            if (raw.length != this._raw.length) {
                return false;
            }
            for (int o = 0; o < this._octets; ++o) {
                if (this._raw[o] == raw[o]) continue;
                return false;
            }
            return this._mask == 0 || (raw[this._octets] & this._mask) == this._masked;
        }
    }

    static class MinMaxInetAddressRange
    extends InetAddressPattern {
        final int[] _min;
        final int[] _max;

        public MinMaxInetAddressRange(String pattern, InetAddress min, InetAddress max) {
            super(pattern);
            int i;
            byte[] rawMin = min.getAddress();
            byte[] rawMax = max.getAddress();
            if (rawMin.length != rawMax.length) {
                throw new IllegalArgumentException("Cannot mix IPv4 and IPv6: " + pattern);
            }
            if (rawMin.length == 4) {
                int count = 0;
                for (char c : pattern.toCharArray()) {
                    if (c != '.') continue;
                    ++count;
                }
                if (count != 6) {
                    throw new IllegalArgumentException("Legacy pattern: " + pattern);
                }
            }
            this._min = new int[rawMin.length];
            this._max = new int[rawMin.length];
            for (i = 0; i < this._min.length; ++i) {
                this._min[i] = 0xFF & rawMin[i];
                this._max[i] = 0xFF & rawMax[i];
            }
            for (i = 0; i < this._min.length; ++i) {
                if (this._min[i] > this._max[i]) {
                    throw new IllegalArgumentException("min is greater than max: " + pattern);
                }
                if (this._min[i] < this._max[i]) break;
            }
        }

        @Override
        public boolean test(InetAddress address) {
            byte[] raw = address.getAddress();
            if (raw.length != this._min.length) {
                return false;
            }
            boolean minOk = false;
            boolean maxOk = false;
            for (int i = 0; i < this._min.length; ++i) {
                int r = 0xFF & raw[i];
                if (!minOk) {
                    if (r < this._min[i]) {
                        return false;
                    }
                    if (r > this._min[i]) {
                        minOk = true;
                    }
                }
                if (!maxOk) {
                    if (r > this._max[i]) {
                        return false;
                    }
                    if (r < this._max[i]) {
                        maxOk = true;
                    }
                }
                if (minOk && maxOk) break;
            }
            return true;
        }
    }

    static class SingletonInetAddressRange
    extends InetAddressPattern {
        final InetAddress _address;

        public SingletonInetAddressRange(String pattern, InetAddress address) {
            super(pattern);
            this._address = address;
        }

        @Override
        public boolean test(InetAddress address) {
            return this._address.equals(address);
        }
    }

    static class LegacyInetAddressRange
    extends InetAddressPattern {
        int[] _min = new int[4];
        int[] _max = new int[4];

        public LegacyInetAddressRange(String pattern) {
            super(pattern);
            String[] parts = pattern.split("\\.");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Bad legacy pattern: " + pattern);
            }
            for (int i = 0; i < 4; ++i) {
                String part = parts[i].trim();
                int dash = part.indexOf(45);
                if (dash < 0) {
                    this._min[i] = this._max[i] = Integer.parseInt(part);
                } else {
                    this._min[i] = dash == 0 ? 0 : StringUtil.toInt(part, 0);
                    int n = this._max[i] = dash == part.length() - 1 ? 255 : StringUtil.toInt(part, dash + 1);
                }
                if (this._min[i] >= 0 && this._min[i] <= this._max[i] && this._max[i] <= 255) continue;
                throw new IllegalArgumentException("Bad legacy pattern: " + pattern);
            }
        }

        @Override
        public boolean test(InetAddress address) {
            byte[] raw = address.getAddress();
            if (raw.length != 4) {
                return false;
            }
            for (int i = 0; i < 4; ++i) {
                if ((0xFF & raw[i]) >= this._min[i] && (0xFF & raw[i]) <= this._max[i]) continue;
                return false;
            }
            return true;
        }
    }
}

