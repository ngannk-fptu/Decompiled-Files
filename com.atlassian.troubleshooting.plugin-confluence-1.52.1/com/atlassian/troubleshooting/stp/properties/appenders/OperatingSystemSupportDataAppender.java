/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.troubleshooting.api.healthcheck.FileSystemInfo;
import com.atlassian.troubleshooting.api.healthcheck.OperatingSystemInfo;
import com.atlassian.troubleshooting.stp.properties.LinuxDistributionDataProvider;
import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class OperatingSystemSupportDataAppender
extends RootLevelSupportDataAppender {
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("###%");
    private static final String LINUX_OS_NAME = "Linux";
    private final OperatingSystemInfo operatingSystemInfo;
    private final FileSystemInfo fileSystemInfo;
    private final LinuxDistributionDataProvider distributionDataProvider;

    public OperatingSystemSupportDataAppender(OperatingSystemInfo operatingSystemInfo, FileSystemInfo fileSystemInfo, LinuxDistributionDataProvider distributionDataProvider) {
        this.operatingSystemInfo = operatingSystemInfo;
        this.fileSystemInfo = fileSystemInfo;
        this.distributionDataProvider = distributionDataProvider;
    }

    @Override
    protected void addSupportData(SupportDataBuilder builder) {
        builder = builder.addCategory("stp.properties.os");
        this.addString(builder, "name", this.operatingSystemInfo.getName());
        this.addString(builder, "architecture", this.operatingSystemInfo.getArch());
        this.addString(builder, "version", this.operatingSystemInfo.getVersion());
        if (this.isLinuxDistribution(this.operatingSystemInfo.getName())) {
            this.addLinuxDistributionInfo(builder);
        }
        this.addNumber(builder, "available.processors", this.operatingSystemInfo.getAvailableProcessors());
        this.addNumber(builder, "system.load.average", this.operatingSystemInfo.getSystemLoadAverage());
        this.addNumber(builder, "committed.virtual.memory.size", this.operatingSystemInfo.getCommittedVirtualMemorySize());
        this.addNumber(builder, "total.swap.space.size", this.operatingSystemInfo.getTotalSwapSpaceSize());
        this.addNumber(builder, "free.swap.space.size", this.operatingSystemInfo.getFreeSwapSpaceSize());
        this.addNumber(builder, "physical.memory.total", this.operatingSystemInfo.getTotalPhysicalMemorySize());
        this.addNumber(builder, "physical.memory.free", this.operatingSystemInfo.getFreePhysicalMemorySize());
        this.addPercentage(builder, "cpu.load.system", this.operatingSystemInfo.getSystemCpuLoad());
        this.addPercentage(builder, "cpu.load.process", this.operatingSystemInfo.getProcessCpuLoad());
        this.addNumber(builder, "file.descriptors.max", this.operatingSystemInfo.getMaxFileDescriptorCount());
        this.addNumber(builder, "file.descriptors.open", this.operatingSystemInfo.getOpenFileDescriptorCount());
        this.addNumber(builder, "max.user.processes", this.fileSystemInfo.getThreadLimit().map(FileSystemInfo.ThreadLimit::value).orElse(-1).intValue());
    }

    private boolean isLinuxDistribution(String osName) {
        return LINUX_OS_NAME.equalsIgnoreCase(osName);
    }

    private void addLinuxDistributionInfo(SupportDataBuilder builder) {
        SupportDataBuilder distributionBuilder = builder.addCategory("stp.properties.os.distribution");
        Map<String, String> distributionData = this.distributionDataProvider.fetchDistributionData();
        distributionData.forEach(distributionBuilder::addValue);
    }

    private void addString(SupportDataBuilder builder, String suffix, String value) {
        value = value == null ? "Unknown" : value;
        builder.addValue("stp.properties.os." + suffix, value);
    }

    private void addPercentage(SupportDataBuilder builder, String suffix, double value) {
        String valueAsString = value < 0.0 ? null : PERCENTAGE_FORMAT.format(value);
        this.addString(builder, suffix, valueAsString);
    }

    private void addNumber(SupportDataBuilder builder, String suffix, long value) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        String valueAsString = value < 0L ? null : nf.format(value);
        this.addString(builder, suffix, valueAsString);
    }

    private void addNumber(SupportDataBuilder builder, String suffix, double value) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        String valueAsString = value < 0.0 ? null : nf.format(value);
        this.addString(builder, suffix, valueAsString);
    }
}

