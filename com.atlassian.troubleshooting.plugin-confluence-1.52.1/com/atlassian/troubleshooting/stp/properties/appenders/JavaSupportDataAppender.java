/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.ServletContextFactory
 *  javax.servlet.ServletContext
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.apache.commons.lang3.StringUtils;

public class JavaSupportDataAppender
extends RootLevelSupportDataAppender {
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("###%");
    private final RuntimeMXBean runtimeMXBean;
    private final MemoryPoolMXBean permGenMXBean;
    private final ServletContextFactory servletContextFactory;

    public JavaSupportDataAppender(ServletContextFactory servletContextFactory) {
        this.servletContextFactory = servletContextFactory;
        this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        MemoryPoolMXBean permGenMXBean = null;
        for (MemoryPoolMXBean bean : ManagementFactory.getMemoryPoolMXBeans()) {
            if (!bean.getName().contains("Perm Gen")) continue;
            permGenMXBean = bean;
            break;
        }
        this.permGenMXBean = permGenMXBean;
    }

    @Override
    protected void addSupportData(SupportDataBuilder builder) {
        builder = builder.addCategory("stp.properties.java");
        Properties systemProps = System.getProperties();
        for (Map.Entry<Object, Object> entry : systemProps.entrySet()) {
            builder.addValue(entry.getKey().toString(), entry.getValue().toString());
        }
        builder.addValue("stp.properties.java.vm.arguments", this.getJVMInputArguments());
        builder.addValue("stp.properties.application.server", this.getAppServer());
        builder.addValue("stp.properties.java.heap.used", this.getTotalHeap());
        builder.addValue("stp.properties.java.heap.available", this.getFreeHeap());
        builder.addValue("stp.properties.java.heap.percent.used", this.getPercentageHeapUsed());
        builder.addValue("stp.properties.java.heap.max", this.getMaxHeap());
        builder.addValue("stp.properties.java.permgen.used", this.getPermgenUsed());
        builder.addValue("stp.properties.java.permgen.max", this.getMaxPermgen());
    }

    private String getFormattedNum(long num) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        return nf.format(num);
    }

    private String getJVMInputArguments() {
        if (this.runtimeMXBean != null) {
            return StringUtils.join(this.runtimeMXBean.getInputArguments(), (String)" ");
        }
        return "Unknown";
    }

    public String getAppServer() {
        ServletContext context = this.servletContextFactory.getServletContext();
        return context != null ? context.getServerInfo() : "Unknown";
    }

    private String getMaxHeap() {
        return this.getFormattedNum(Runtime.getRuntime().maxMemory());
    }

    private String getTotalHeap() {
        return this.getFormattedNum(Runtime.getRuntime().totalMemory());
    }

    private String getFreeHeap() {
        return this.getFormattedNum(Runtime.getRuntime().freeMemory());
    }

    private String getPercentageHeapUsed() {
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        double percent = total != 0L ? (double)(total - free) / (double)total : 1.0;
        return PERCENTAGE_FORMAT.format(percent);
    }

    private String getPermgenUsed() {
        if (this.permGenMXBean != null) {
            return this.getFormattedNum(this.permGenMXBean.getUsage().getUsed());
        }
        return "Unknown";
    }

    private String getMaxPermgen() {
        if (this.permGenMXBean != null) {
            return this.getFormattedNum(this.permGenMXBean.getUsage().getMax());
        }
        return "Unknown";
    }
}

