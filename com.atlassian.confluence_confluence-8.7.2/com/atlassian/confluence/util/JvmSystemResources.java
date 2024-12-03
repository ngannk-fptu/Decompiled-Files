/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.unit.DataSize
 */
package com.atlassian.confluence.util;

import org.springframework.util.unit.DataSize;

public interface JvmSystemResources {
    public int getAvailableProcessors();

    public DataSize getFreeMemory();

    public static JvmSystemResources getRuntime() {
        return new JvmSystemResources(){

            @Override
            public int getAvailableProcessors() {
                return Runtime.getRuntime().availableProcessors();
            }

            @Override
            public DataSize getFreeMemory() {
                return DataSize.ofBytes((long)Runtime.getRuntime().freeMemory());
            }
        };
    }
}

