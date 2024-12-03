/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.iptc;

import java.util.Comparator;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcType;

public class IptcRecord {
    public final IptcType iptcType;
    private final String value;
    public static final Comparator<IptcRecord> COMPARATOR = (e1, e2) -> e1.iptcType.getType() - e2.iptcType.getType();

    public IptcRecord(IptcType iptcType, String value) {
        this.iptcType = iptcType;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String getIptcTypeName() {
        return this.iptcType.getName();
    }
}

