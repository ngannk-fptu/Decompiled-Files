/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.NodeStatus
 *  com.atlassian.confluence.cluster.NodeStatusImpl
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.spring.container.ContainerContext
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.confluence.cluster.NodeStatus;
import com.atlassian.confluence.cluster.NodeStatusImpl;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectNodeStatus
implements Callable<NodeStatus>,
Serializable {
    private static final long serialVersionUID = 7953447355233473859L;
    private static final Logger log = LoggerFactory.getLogger(CollectNodeStatus.class);
    private final String requestingMember;

    public CollectNodeStatus(String requestingMember) {
        this.requestingMember = (String)Preconditions.checkNotNull((Object)requestingMember);
    }

    @Override
    public NodeStatus call() {
        log.debug("Collecting NodeStatus for {}", (Object)this.requestingMember);
        Map<Object, Object> jvmStats = new HashMap();
        Map<Object, Object> props = new HashMap();
        Map<Object, Object> buildStats = new HashMap();
        if (ContainerManager.isContainerSetup()) {
            ContainerContext containerContext = ContainerManager.getInstance().getContainerContext();
            SystemInformationService sysInfo = (SystemInformationService)containerContext.getComponent((Object)"systemInformationService");
            jvmStats = this.tryConvertBeanToMap(() -> ((SystemInformationService)sysInfo).getMemoryInfo());
            props = this.tryConvertBeanToMap(() -> ((SystemInformationService)sysInfo).getSystemProperties());
            buildStats = this.tryConvertBeanToMap(() -> ((SystemInformationService)sysInfo).getConfluenceInfo());
        } else {
            log.info("Unable to obtain node status as container context is not yet set up");
        }
        return new NodeStatusImpl(jvmStats, props, buildStats);
    }

    private Map<String, String> tryConvertBeanToMap(Supplier<Object> beanToMapSupplier) {
        try {
            return GeneralUtil.convertBeanToMap((Object)beanToMapSupplier.get());
        }
        catch (Exception ex) {
            log.warn("failed to convert bean to map", (Throwable)ex);
            return new HashMap<String, String>();
        }
    }
}

