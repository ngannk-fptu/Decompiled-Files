/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.troubleshooting.api.healthcheck.JvmMemoryInfo;
import com.atlassian.troubleshooting.stp.mxbean.MXBeanProvider;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultJvmMemoryInfo
implements JvmMemoryInfo {
    private final MXBeanProvider mxBeanProvider;
    private List<MemoryPoolMXBean> codeCacheMemoryPoolBeans;
    private RuntimeMXBean runtimeMXBean;

    @Autowired
    public DefaultJvmMemoryInfo(MXBeanProvider mxBeanProvider) {
        Objects.requireNonNull(mxBeanProvider);
        this.mxBeanProvider = mxBeanProvider;
    }

    private Optional<MemoryManagerMXBean> getCodeCacheMemoryManagerMXBean(MXBeanProvider mxBeanProvider) {
        return mxBeanProvider.getMemoryManagerMXBeans().stream().filter(mpmvb -> mpmvb.getName().equals("CodeCacheManager")).findFirst();
    }

    @Override
    public List<MemoryPoolMXBean> getCodeCacheMemoryPoolMXBeans() {
        if (this.codeCacheMemoryPoolBeans == null) {
            this.codeCacheMemoryPoolBeans = this.findCodeCacheMemoryPoolMXBeans();
        }
        return this.codeCacheMemoryPoolBeans;
    }

    private List<MemoryPoolMXBean> findCodeCacheMemoryPoolMXBeans() {
        Optional<MemoryManagerMXBean> codeCacheMemoryManagerBean = this.getCodeCacheMemoryManagerMXBean(this.mxBeanProvider);
        return codeCacheMemoryManagerBean.map(bean -> {
            ArrayList<String> poolNames = new ArrayList<String>(Arrays.asList(bean.getMemoryPoolNames()));
            return this.mxBeanProvider.getMemoryPoolMXBeans().stream().filter(mpmxb -> poolNames.contains(mpmxb.getName())).collect(Collectors.toList());
        }).orElse(Collections.emptyList());
    }

    @Override
    public RuntimeMXBean getRuntimeMXBean() {
        if (this.runtimeMXBean == null) {
            this.runtimeMXBean = this.mxBeanProvider.getRuntimeMXBean();
        }
        return this.runtimeMXBean;
    }
}

