/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management;

import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.internal.management.dto.ConnectionManagerDTO;
import com.hazelcast.internal.management.dto.EventServiceDTO;
import com.hazelcast.internal.management.dto.MXBeansDTO;
import com.hazelcast.internal.management.dto.ManagedExecutorDTO;
import com.hazelcast.internal.management.dto.OperationServiceDTO;
import com.hazelcast.internal.management.dto.PartitionServiceBeanDTO;
import com.hazelcast.internal.management.dto.ProxyServiceDTO;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.util.RuntimeAvailableProcessors;
import com.hazelcast.monitor.impl.MemberStateImpl;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.spi.impl.eventservice.InternalEventService;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.proxyservice.InternalProxyService;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.OperatingSystemMXBeanSupport;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.Map;

final class TimedMemberStateFactoryHelper {
    private static final int PERCENT_MULTIPLIER = 100;

    private TimedMemberStateFactoryHelper() {
    }

    static void registerJMXBeans(HazelcastInstanceImpl instance, MemberStateImpl memberState) {
        InternalEventService es = instance.node.nodeEngine.getEventService();
        InternalOperationService os = instance.node.nodeEngine.getOperationService();
        NetworkingService cm = instance.node.networkingService;
        InternalPartitionServiceImpl ps = instance.node.partitionService;
        InternalProxyService proxyService = instance.node.nodeEngine.getProxyService();
        InternalExecutionService executionService = instance.node.nodeEngine.getExecutionService();
        MXBeansDTO beans = new MXBeansDTO();
        EventServiceDTO esBean = new EventServiceDTO(es);
        beans.setEventServiceBean(esBean);
        OperationServiceDTO osBean = new OperationServiceDTO(os);
        beans.setOperationServiceBean(osBean);
        ConnectionManagerDTO cmBean = new ConnectionManagerDTO(cm);
        beans.setConnectionManagerBean(cmBean);
        PartitionServiceBeanDTO psBean = new PartitionServiceBeanDTO(ps, instance);
        beans.setPartitionServiceBean(psBean);
        ProxyServiceDTO proxyServiceBean = new ProxyServiceDTO(proxyService);
        beans.setProxyServiceBean(proxyServiceBean);
        ManagedExecutorService systemExecutor = executionService.getExecutor("hz:system");
        ManagedExecutorService asyncExecutor = executionService.getExecutor("hz:async");
        ManagedExecutorService scheduledExecutor = executionService.getExecutor("hz:scheduled");
        ManagedExecutorService clientExecutor = executionService.getExecutor("hz:client");
        ManagedExecutorService queryExecutor = executionService.getExecutor("hz:query");
        ManagedExecutorService ioExecutor = executionService.getExecutor("hz:io");
        ManagedExecutorService offloadableExecutor = executionService.getExecutor("hz:offloadable");
        ManagedExecutorDTO systemExecutorDTO = new ManagedExecutorDTO(systemExecutor);
        ManagedExecutorDTO asyncExecutorDTO = new ManagedExecutorDTO(asyncExecutor);
        ManagedExecutorDTO scheduledExecutorDTO = new ManagedExecutorDTO(scheduledExecutor);
        ManagedExecutorDTO clientExecutorDTO = new ManagedExecutorDTO(clientExecutor);
        ManagedExecutorDTO queryExecutorDTO = new ManagedExecutorDTO(queryExecutor);
        ManagedExecutorDTO ioExecutorDTO = new ManagedExecutorDTO(ioExecutor);
        ManagedExecutorDTO offloadableExecutorDTO = new ManagedExecutorDTO(offloadableExecutor);
        beans.putManagedExecutor("hz:system", systemExecutorDTO);
        beans.putManagedExecutor("hz:async", asyncExecutorDTO);
        beans.putManagedExecutor("hz:scheduled", scheduledExecutorDTO);
        beans.putManagedExecutor("hz:client", clientExecutorDTO);
        beans.putManagedExecutor("hz:query", queryExecutorDTO);
        beans.putManagedExecutor("hz:io", ioExecutorDTO);
        beans.putManagedExecutor("hz:offloadable", offloadableExecutorDTO);
        memberState.setBeans(beans);
    }

    static void createRuntimeProps(MemberStateImpl memberState) {
        Runtime runtime = Runtime.getRuntime();
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        ClassLoadingMXBean clMxBean = ManagementFactory.getClassLoadingMXBean();
        MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemory = memoryMxBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemory = memoryMxBean.getNonHeapMemoryUsage();
        int propertyCount = 29;
        Map<String, Long> map = MapUtil.createHashMap(29);
        map.put("runtime.availableProcessors", Long.valueOf(RuntimeAvailableProcessors.get()));
        map.put("date.startTime", runtimeMxBean.getStartTime());
        map.put("seconds.upTime", runtimeMxBean.getUptime());
        map.put("memory.maxMemory", runtime.maxMemory());
        map.put("memory.freeMemory", runtime.freeMemory());
        map.put("memory.totalMemory", runtime.totalMemory());
        map.put("memory.heapMemoryMax", heapMemory.getMax());
        map.put("memory.heapMemoryUsed", heapMemory.getUsed());
        map.put("memory.nonHeapMemoryMax", nonHeapMemory.getMax());
        map.put("memory.nonHeapMemoryUsed", nonHeapMemory.getUsed());
        map.put("runtime.totalLoadedClassCount", clMxBean.getTotalLoadedClassCount());
        map.put("runtime.loadedClassCount", Long.valueOf(clMxBean.getLoadedClassCount()));
        map.put("runtime.unloadedClassCount", clMxBean.getUnloadedClassCount());
        map.put("runtime.totalStartedThreadCount", threadMxBean.getTotalStartedThreadCount());
        map.put("runtime.threadCount", Long.valueOf(threadMxBean.getThreadCount()));
        map.put("runtime.peakThreadCount", Long.valueOf(threadMxBean.getPeakThreadCount()));
        map.put("runtime.daemonThreadCount", Long.valueOf(threadMxBean.getDaemonThreadCount()));
        OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        map.put("osMemory.freePhysicalMemory", TimedMemberStateFactoryHelper.get(osMxBean, "getFreePhysicalMemorySize", 0L));
        map.put("osMemory.committedVirtualMemory", TimedMemberStateFactoryHelper.get(osMxBean, "getCommittedVirtualMemorySize", 0L));
        map.put("osMemory.totalPhysicalMemory", TimedMemberStateFactoryHelper.get(osMxBean, "getTotalPhysicalMemorySize", 0L));
        map.put("osSwap.freeSwapSpace", TimedMemberStateFactoryHelper.get(osMxBean, "getFreeSwapSpaceSize", 0L));
        map.put("osSwap.totalSwapSpace", TimedMemberStateFactoryHelper.get(osMxBean, "getTotalSwapSpaceSize", 0L));
        map.put("os.maxFileDescriptorCount", TimedMemberStateFactoryHelper.get(osMxBean, "getMaxFileDescriptorCount", 0L));
        map.put("os.openFileDescriptorCount", TimedMemberStateFactoryHelper.get(osMxBean, "getOpenFileDescriptorCount", 0L));
        map.put("os.processCpuLoad", TimedMemberStateFactoryHelper.get(osMxBean, "getProcessCpuLoad", -1L));
        map.put("os.systemLoadAverage", TimedMemberStateFactoryHelper.get(osMxBean, "getSystemLoadAverage", -1L));
        map.put("os.systemCpuLoad", TimedMemberStateFactoryHelper.get(osMxBean, "getSystemCpuLoad", -1L));
        map.put("os.processCpuTime", TimedMemberStateFactoryHelper.get(osMxBean, "getProcessCpuTime", 0L));
        map.put("os.availableProcessors", TimedMemberStateFactoryHelper.get(osMxBean, "getAvailableProcessors", 0L));
        memberState.setRuntimeProps(map);
    }

    private static Long get(OperatingSystemMXBean mbean, String methodName, Long defaultValue) {
        if (OperatingSystemMXBeanSupport.GET_FREE_PHYSICAL_MEMORY_SIZE_DISABLED && methodName.equals("getFreePhysicalMemorySize")) {
            return defaultValue;
        }
        try {
            Method method = mbean.getClass().getMethod(methodName, new Class[0]);
            method.setAccessible(true);
            Object value = method.invoke((Object)mbean, new Object[0]);
            if (value instanceof Integer) {
                return (long)((Integer)value);
            }
            if (value instanceof Double) {
                double v = (Double)value;
                return Math.round(v * 100.0);
            }
            if (value instanceof Long) {
                return (Long)value;
            }
            return defaultValue;
        }
        catch (RuntimeException e) {
            return defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
}

