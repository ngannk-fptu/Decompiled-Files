/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cfg;

import com.mchange.v2.cfg.ConfigUtils;
import com.mchange.v2.cfg.MultiPropertiesConfig;
import java.util.List;

public final class MLogConfigSource {
    public static MultiPropertiesConfig readVmConfig(String[] stringArray, String[] stringArray2, List list) {
        return ConfigUtils.readVmConfig(stringArray, stringArray2, list);
    }

    private MLogConfigSource() {
    }
}

