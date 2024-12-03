/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.iptc;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcType;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcTypes;

public final class IptcTypeLookup {
    private static final Map<Integer, IptcType> IPTC_TYPE_MAP = new HashMap<Integer, IptcType>();

    private IptcTypeLookup() {
    }

    public static IptcType getIptcType(int type) {
        if (!IPTC_TYPE_MAP.containsKey(type)) {
            return IptcTypes.getUnknown(type);
        }
        return IPTC_TYPE_MAP.get(type);
    }

    static {
        for (IptcTypes iptcType : IptcTypes.values()) {
            IPTC_TYPE_MAP.put(iptcType.getType(), iptcType);
        }
    }
}

