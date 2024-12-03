/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.metricsets;

import com.hazelcast.cache.CacheStatistics;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.logging.ILogger;
import com.hazelcast.monitor.LocalIndexStats;
import com.hazelcast.monitor.LocalInstanceStats;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.monitor.impl.LocalMapStatsImpl;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.servicemanager.ServiceManager;
import com.hazelcast.util.StringUtil;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class StatisticsAwareMetricsSet {
    private static final int SCAN_PERIOD_SECONDS = 10;
    private final ServiceManager serviceManager;
    private final ILogger logger;

    public StatisticsAwareMetricsSet(ServiceManager serviceManager, NodeEngineImpl nodeEngine) {
        this.serviceManager = serviceManager;
        this.logger = nodeEngine.getLogger(this.getClass());
    }

    public void register(MetricsRegistry metricsRegistry) {
        metricsRegistry.scheduleAtFixedRate(new Task(metricsRegistry), 10L, TimeUnit.SECONDS, ProbeLevel.INFO);
    }

    private final class Task
    implements Runnable {
        private final MetricsRegistry metricsRegistry;
        private Set<LocalInstanceStats> previousStats = new HashSet<LocalInstanceStats>();
        private Set<LocalInstanceStats> currentStats = new HashSet<LocalInstanceStats>();

        private Task(MetricsRegistry metricsRegistry) {
            this.metricsRegistry = metricsRegistry;
        }

        @Override
        public void run() {
            try {
                this.registerAliveStats();
                this.purgeDeadStats();
                Set<LocalInstanceStats> tmp = this.previousStats;
                this.previousStats = this.currentStats;
                this.currentStats = tmp;
                this.currentStats.clear();
            }
            catch (Exception e) {
                StatisticsAwareMetricsSet.this.logger.finest("Error occurred while scanning for statistics aware metrics", e);
            }
        }

        private void registerAliveStats() {
            for (StatisticsAwareService statisticsAwareService : StatisticsAwareMetricsSet.this.serviceManager.getServices(StatisticsAwareService.class)) {
                Map stats = statisticsAwareService.getStats();
                if (stats == null) continue;
                for (Map.Entry entry : stats.entrySet()) {
                    LocalInstanceStats localInstanceStats = (LocalInstanceStats)entry.getValue();
                    this.currentStats.add(localInstanceStats);
                    if (this.previousStats.contains(localInstanceStats)) continue;
                    String name = entry.getKey();
                    NearCacheStats nearCacheStats = this.getNearCacheStats(localInstanceStats);
                    String baseName = localInstanceStats.getClass().getSimpleName().replace("Stats", "").replace("Local", "").replace("Impl", "");
                    baseName = StringUtil.lowerCaseFirstChar(baseName);
                    if (nearCacheStats != null) {
                        this.metricsRegistry.scanAndRegister(nearCacheStats, baseName + "[" + name + "].nearcache");
                    }
                    if (localInstanceStats instanceof LocalMapStatsImpl) {
                        Map<String, LocalIndexStats> indexStats = ((LocalMapStatsImpl)localInstanceStats).getIndexStats();
                        for (Map.Entry<String, LocalIndexStats> indexEntry : indexStats.entrySet()) {
                            this.metricsRegistry.scanAndRegister(indexEntry.getValue(), baseName + "[" + name + "].index[" + indexEntry.getKey() + "]");
                        }
                    }
                    this.metricsRegistry.scanAndRegister(localInstanceStats, baseName + "[" + name + "]");
                }
            }
        }

        private NearCacheStats getNearCacheStats(LocalInstanceStats localInstanceStats) {
            if (localInstanceStats instanceof LocalMapStatsImpl) {
                LocalMapStats localMapStats = (LocalMapStats)localInstanceStats;
                return localMapStats.getNearCacheStats();
            }
            if (localInstanceStats instanceof CacheStatistics) {
                CacheStatistics localMapStats = (CacheStatistics)((Object)localInstanceStats);
                return localMapStats.getNearCacheStatistics();
            }
            return null;
        }

        private void purgeDeadStats() {
            for (LocalInstanceStats localInstanceStats : this.previousStats) {
                if (this.currentStats.contains(localInstanceStats)) continue;
                this.metricsRegistry.deregister(localInstanceStats);
                NearCacheStats nearCacheStats = this.getNearCacheStats(localInstanceStats);
                if (nearCacheStats == null) continue;
                this.metricsRegistry.deregister(nearCacheStats);
            }
        }
    }
}

