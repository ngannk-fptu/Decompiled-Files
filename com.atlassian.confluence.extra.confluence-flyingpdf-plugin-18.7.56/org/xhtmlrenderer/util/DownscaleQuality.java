/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.util.HashMap;
import java.util.Map;

public class DownscaleQuality {
    private static Map constList;
    public static final DownscaleQuality HIGH_QUALITY;
    public static final DownscaleQuality LOW_QUALITY;
    public static final DownscaleQuality FAST;
    public static final DownscaleQuality AREA;
    private final String type;

    private static DownscaleQuality addConstant(String type) {
        DownscaleQuality.init();
        if (constList.containsKey(type)) {
            throw new RuntimeException("Type strings for DownscaleQuality should be unique; " + type + " is declared twice");
        }
        DownscaleQuality q = new DownscaleQuality(type);
        constList.put(type, q);
        return q;
    }

    private static void init() {
        if (constList == null) {
            constList = new HashMap();
        }
    }

    private DownscaleQuality(String type) {
        this.type = type;
    }

    public String asString() {
        return this.type;
    }

    public static DownscaleQuality forString(String type, DownscaleQuality dflt) {
        DownscaleQuality q = (DownscaleQuality)constList.get(type);
        return q == null ? dflt : q;
    }

    static {
        HIGH_QUALITY = DownscaleQuality.addConstant("HIGH");
        LOW_QUALITY = DownscaleQuality.addConstant("MED");
        FAST = DownscaleQuality.addConstant("LOW");
        AREA = DownscaleQuality.addConstant("AREA");
    }
}

