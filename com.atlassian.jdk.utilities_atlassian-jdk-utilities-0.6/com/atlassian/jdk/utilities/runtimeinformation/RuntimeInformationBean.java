/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jdk.utilities.runtimeinformation;

import com.atlassian.jdk.utilities.runtimeinformation.MemoryInformation;
import com.atlassian.jdk.utilities.runtimeinformation.MemoryInformationBean;
import com.atlassian.jdk.utilities.runtimeinformation.MemorySizeUtils;
import com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformation;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuntimeInformationBean
implements RuntimeInformation {
    private final MemoryMXBean memoryBean;
    private final RuntimeMXBean runtimeBean;

    RuntimeInformationBean(MemoryMXBean memoryBean, RuntimeMXBean runtimeBean) {
        this.memoryBean = memoryBean;
        this.runtimeBean = runtimeBean;
    }

    RuntimeInformationBean() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.runtimeBean = ManagementFactory.getRuntimeMXBean();
    }

    @Override
    public long getTotalHeapMemory() {
        return this.memoryBean.getHeapMemoryUsage().getMax();
    }

    @Override
    public long getTotalHeapMemoryUsed() {
        return this.memoryBean.getHeapMemoryUsage().getUsed();
    }

    @Override
    public List<MemoryInformation> getMemoryPoolInformation() {
        List<MemoryPoolMXBean> mxBeans = ManagementFactory.getMemoryPoolMXBeans();
        ArrayList<MemoryInformationBean> result = new ArrayList<MemoryInformationBean>(mxBeans.size());
        for (MemoryPoolMXBean mxBean : mxBeans) {
            result.add(new MemoryInformationBean(mxBean));
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public long getTotalPermGenMemory() {
        return this.getPermGen().getTotal();
    }

    @Override
    public long getTotalPermGenMemoryUsed() {
        return this.getPermGen().getUsed();
    }

    @Override
    public long getTotalNonHeapMemory() {
        return this.memoryBean.getNonHeapMemoryUsage().getMax();
    }

    @Override
    public long getTotalNonHeapMemoryUsed() {
        return this.memoryBean.getNonHeapMemoryUsage().getUsed();
    }

    @Override
    public long getXmx() {
        return this.getMemoryArgumentValue("Xmx");
    }

    @Override
    public long getXms() {
        return this.getMemoryArgumentValue("Xms");
    }

    @Override
    public String getJvmInputArguments() {
        StringBuilder sb = new StringBuilder();
        for (String argument : this.runtimeBean.getInputArguments()) {
            sb.append(argument).append(" ");
        }
        return sb.toString();
    }

    private MemoryInformation getPermGen() {
        for (MemoryInformation info : this.getMemoryPoolInformation()) {
            String name = info.getName().toLowerCase();
            if (!name.contains("perm gen")) continue;
            return info;
        }
        return new MemoryInformation(){

            @Override
            public String getName() {
                return "";
            }

            @Override
            public long getTotal() {
                return -1L;
            }

            @Override
            public long getUsed() {
                return -1L;
            }

            @Override
            public long getFree() {
                return -1L;
            }
        };
    }

    private long getMemoryArgumentValue(String argument) {
        String prefixArgument = "-" + argument;
        return this.runtimeBean.getInputArguments().stream().filter(n -> n.startsWith(prefixArgument)).map(n -> n.replace(prefixArgument, "")).reduce((first, second) -> second).flatMap(MemorySizeUtils::displaySizeToBytes).orElse(-1L);
    }
}

