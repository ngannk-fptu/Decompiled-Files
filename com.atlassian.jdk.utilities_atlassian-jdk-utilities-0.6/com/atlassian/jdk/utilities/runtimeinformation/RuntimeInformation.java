/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jdk.utilities.runtimeinformation;

import com.atlassian.jdk.utilities.runtimeinformation.MemoryInformation;
import java.util.List;

public interface RuntimeInformation {
    public long getTotalHeapMemory();

    public long getTotalHeapMemoryUsed();

    public String getJvmInputArguments();

    public List<MemoryInformation> getMemoryPoolInformation();

    public long getTotalPermGenMemory();

    public long getTotalPermGenMemoryUsed();

    public long getTotalNonHeapMemory();

    public long getTotalNonHeapMemoryUsed();

    public long getXmx();

    public long getXms();
}

