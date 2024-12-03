/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.profiling.TimerSnapshot
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.monitoring.rest;

import com.atlassian.confluence.util.profiling.TimerSnapshot;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="timer")
public class TimerSnapshotModel {
    @XmlElement(name="0", nillable=false)
    private String name;
    @XmlElement(name="1", nillable=false)
    private Long counter;
    @XmlElement(name="2", nillable=true)
    private Double wallMean;
    @XmlElement(name="3", nillable=true)
    private Long wallMin;
    @XmlElement(name="4", nillable=true)
    private Long wallMax;
    @XmlElement(name="5", nillable=true)
    private Long wallTotal;
    @XmlElement(name="6", nillable=true)
    private Double cpuMean;
    @XmlElement(name="7", nillable=true)
    private Long cpuMin;
    @XmlElement(name="8", nillable=true)
    private Long cpuMax;
    @XmlElement(name="9", nillable=true)
    private Long cpuTotal;

    public TimerSnapshotModel() {
    }

    public TimerSnapshotModel(TimerSnapshot sample) {
        this.name = sample.getName();
        this.counter = sample.getInvocationCount();
        this.wallMean = sample.getInvocationCount() == 0L ? 0.0 : (double)sample.getElapsedTotalTime(TimeUnit.NANOSECONDS) / (double)sample.getInvocationCount();
        this.wallMin = sample.getElapsedMinTime(TimeUnit.NANOSECONDS);
        this.wallMax = sample.getElapsedMaxTime(TimeUnit.NANOSECONDS);
        this.wallTotal = sample.getElapsedTotalTime(TimeUnit.NANOSECONDS);
        this.cpuMean = sample.getInvocationCount() == 0L ? 0.0 : (double)sample.getCpuTotalTime(TimeUnit.NANOSECONDS) / (double)sample.getInvocationCount();
        this.cpuMin = sample.getCpuMinTime(TimeUnit.NANOSECONDS);
        this.cpuMax = sample.getCpuMaxTime(TimeUnit.NANOSECONDS);
        this.cpuTotal = sample.getCpuTotalTime(TimeUnit.NANOSECONDS);
    }
}

