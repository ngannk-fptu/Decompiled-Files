/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.version;

import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.version.MemberVersion;
import java.io.Serializable;
import java.util.Comparator;

@SerializableByConvention
class MajorMinorVersionComparator
implements Comparator<MemberVersion>,
Serializable {
    private static final long serialVersionUID = 364570099633468810L;

    MajorMinorVersionComparator() {
    }

    @Override
    public int compare(MemberVersion o1, MemberVersion o2) {
        int thatVersion;
        int thisVersion = o1.getMajor() << 8 & 0xFF00 | o1.getMinor() & 0xFF;
        if (thisVersion > (thatVersion = o2.getMajor() << 8 & 0xFF00 | o2.getMinor() & 0xFF)) {
            return 1;
        }
        return thisVersion == thatVersion ? 0 : -1;
    }
}

