/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.sampled;

import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanNotificationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.hibernate.management.impl.BaseEmitterBean;
import net.sf.ehcache.management.sampled.CacheManagerSampler;
import net.sf.ehcache.management.sampled.CacheManagerSamplerImpl;
import net.sf.ehcache.management.sampled.SampledCacheManagerMBean;

public class SampledCacheManager
extends BaseEmitterBean
implements SampledCacheManagerMBean {
    private static final MBeanNotificationInfo[] NOTIFICATION_INFO;
    private final CacheManagerSampler sampledCacheManagerDelegate;
    private String mbeanRegisteredName;
    private volatile boolean mbeanRegisteredNameSet;

    public SampledCacheManager(CacheManager cacheManager) throws NotCompliantMBeanException {
        super(SampledCacheManagerMBean.class);
        this.sampledCacheManagerDelegate = new CacheManagerSamplerImpl(cacheManager);
    }

    @Override
    protected void doDispose() {
    }

    void setMBeanRegisteredName(String name) {
        if (this.mbeanRegisteredNameSet) {
            throw new IllegalStateException("Name used for registering this mbean is already set");
        }
        this.mbeanRegisteredNameSet = true;
        this.mbeanRegisteredName = name;
    }

    @Override
    public void clearAll() {
        this.sampledCacheManagerDelegate.clearAll();
        this.sendNotification("CachesCleared");
    }

    @Override
    public String[] getCacheNames() throws IllegalStateException {
        return this.sampledCacheManagerDelegate.getCacheNames();
    }

    @Override
    public String getStatus() {
        return this.sampledCacheManagerDelegate.getStatus();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public Map<String, long[]> getCacheMetrics() {
        return this.sampledCacheManagerDelegate.getCacheMetrics();
    }

    @Override
    public long getCacheHitRate() {
        return this.sampledCacheManagerDelegate.getCacheHitRate();
    }

    @Override
    public long getCacheInMemoryHitRate() {
        return this.sampledCacheManagerDelegate.getCacheInMemoryHitRate();
    }

    @Override
    public long getCacheOffHeapHitRate() {
        return this.sampledCacheManagerDelegate.getCacheOffHeapHitRate();
    }

    @Override
    public long getCacheOnDiskHitRate() {
        return this.sampledCacheManagerDelegate.getCacheOnDiskHitRate();
    }

    @Override
    public long getCacheMissRate() {
        return this.sampledCacheManagerDelegate.getCacheMissRate();
    }

    @Override
    public long getCacheInMemoryMissRate() {
        return this.sampledCacheManagerDelegate.getCacheInMemoryMissRate();
    }

    @Override
    public long getCacheOffHeapMissRate() {
        return this.sampledCacheManagerDelegate.getCacheOffHeapMissRate();
    }

    @Override
    public long getCacheOnDiskMissRate() {
        return this.sampledCacheManagerDelegate.getCacheOnDiskMissRate();
    }

    @Override
    public long getCachePutRate() {
        return this.sampledCacheManagerDelegate.getCachePutRate();
    }

    @Override
    public long getCacheUpdateRate() {
        return this.sampledCacheManagerDelegate.getCacheUpdateRate();
    }

    @Override
    public long getCacheRemoveRate() {
        return this.sampledCacheManagerDelegate.getCacheRemoveRate();
    }

    @Override
    public long getCacheEvictionRate() {
        return this.sampledCacheManagerDelegate.getCacheEvictionRate();
    }

    @Override
    public long getCacheExpirationRate() {
        return this.sampledCacheManagerDelegate.getCacheExpirationRate();
    }

    @Override
    public float getCacheAverageGetTime() {
        return this.sampledCacheManagerDelegate.getCacheAverageGetTime();
    }

    @Override
    public long getCacheSearchRate() {
        return this.sampledCacheManagerDelegate.getCacheSearchRate();
    }

    @Override
    public long getCacheAverageSearchTime() {
        return this.sampledCacheManagerDelegate.getCacheAverageSearchTime();
    }

    @Override
    public boolean getHasWriteBehindWriter() {
        return this.sampledCacheManagerDelegate.getHasWriteBehindWriter();
    }

    @Override
    public long getWriterQueueLength() {
        return this.sampledCacheManagerDelegate.getWriterQueueLength();
    }

    @Override
    public int getWriterMaxQueueSize() {
        return this.sampledCacheManagerDelegate.getWriterMaxQueueSize();
    }

    @Override
    public long getMaxBytesLocalDisk() {
        return this.sampledCacheManagerDelegate.getMaxBytesLocalDisk();
    }

    @Override
    public String getMaxBytesLocalDiskAsString() {
        return this.sampledCacheManagerDelegate.getMaxBytesLocalDiskAsString();
    }

    @Override
    public void setMaxBytesLocalDisk(long maxBytes) {
        this.sampledCacheManagerDelegate.setMaxBytesLocalDisk(maxBytes);
        this.sendNotification("CacheManagerChanged", this.getCacheManagerAttributes(), this.getName());
    }

    @Override
    public void setMaxBytesLocalDiskAsString(String maxBytes) {
        this.sampledCacheManagerDelegate.setMaxBytesLocalDiskAsString(maxBytes);
        this.sendNotification("CacheManagerChanged", this.getCacheManagerAttributes(), this.getName());
    }

    @Override
    public long getMaxBytesLocalHeap() {
        return this.sampledCacheManagerDelegate.getMaxBytesLocalHeap();
    }

    @Override
    public String getMaxBytesLocalHeapAsString() {
        return this.sampledCacheManagerDelegate.getMaxBytesLocalHeapAsString();
    }

    @Override
    public void setMaxBytesLocalHeap(long maxBytes) {
        this.sampledCacheManagerDelegate.setMaxBytesLocalHeap(maxBytes);
        this.sendNotification("CacheManagerChanged", this.getCacheManagerAttributes(), this.getName());
    }

    @Override
    public void setMaxBytesLocalHeapAsString(String maxBytes) {
        this.sampledCacheManagerDelegate.setMaxBytesLocalHeapAsString(maxBytes);
        this.sendNotification("CacheManagerChanged", this.getCacheManagerAttributes(), this.getName());
    }

    @Override
    public long getMaxBytesLocalOffHeap() {
        return this.sampledCacheManagerDelegate.getMaxBytesLocalOffHeap();
    }

    @Override
    public String getMaxBytesLocalOffHeapAsString() {
        return this.sampledCacheManagerDelegate.getMaxBytesLocalOffHeapAsString();
    }

    @Override
    public String getName() {
        return this.sampledCacheManagerDelegate.getName();
    }

    @Override
    public String getClusterUUID() {
        return this.sampledCacheManagerDelegate.getClusterUUID();
    }

    @Override
    public String getMBeanRegisteredName() {
        return this.mbeanRegisteredName;
    }

    @Override
    public String generateActiveConfigDeclaration() {
        return this.sampledCacheManagerDelegate.generateActiveConfigDeclaration();
    }

    @Override
    public String generateActiveConfigDeclaration(String cacheName) {
        return this.sampledCacheManagerDelegate.generateActiveConfigDeclaration(cacheName);
    }

    @Override
    public boolean getTransactional() {
        return this.sampledCacheManagerDelegate.getTransactional();
    }

    @Override
    public boolean getSearchable() {
        return this.sampledCacheManagerDelegate.getSearchable();
    }

    @Override
    public long getTransactionCommittedCount() {
        return this.sampledCacheManagerDelegate.getTransactionCommittedCount();
    }

    @Override
    public long getTransactionCommitRate() {
        return this.sampledCacheManagerDelegate.getTransactionCommitRate();
    }

    @Override
    public long getTransactionRolledBackCount() {
        return this.sampledCacheManagerDelegate.getTransactionRolledBackCount();
    }

    @Override
    public long getTransactionRollbackRate() {
        return this.sampledCacheManagerDelegate.getTransactionRollbackRate();
    }

    @Override
    public long getTransactionTimedOutCount() {
        return this.sampledCacheManagerDelegate.getTransactionTimedOutCount();
    }

    @Override
    public boolean isEnabled() throws CacheException {
        return this.sampledCacheManagerDelegate.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.sampledCacheManagerDelegate.setEnabled(enabled);
        this.sendNotification("CachesEnabled", enabled);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return NOTIFICATION_INFO;
    }

    private Map<String, Object> getCacheManagerAttributes() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("MaxBytesLocalHeapAsString", this.getMaxBytesLocalHeapAsString());
        result.put("MaxBytesLocalOffHeapAsString", this.getMaxBytesLocalOffHeapAsString());
        result.put("MaxBytesLocalDiskAsString", this.getMaxBytesLocalDiskAsString());
        result.put("MaxBytesLocalHeap", this.getMaxBytesLocalHeap());
        result.put("MaxBytesLocalOffHeap", this.getMaxBytesLocalOffHeap());
        result.put("MaxBytesLocalDisk", this.getMaxBytesLocalDisk());
        return result;
    }

    @Override
    public Object[][] executeQuery(String queryString) {
        return this.sampledCacheManagerDelegate.executeQuery(queryString);
    }

    static {
        String[] notifTypes = new String[]{"CachesEnabled", "CachesCleared", "StatisticsEnabled", "StatisticsReset"};
        String name = Notification.class.getName();
        String description = "Ehcache SampledCacheManager Event";
        NOTIFICATION_INFO = new MBeanNotificationInfo[]{new MBeanNotificationInfo(notifTypes, name, "Ehcache SampledCacheManager Event")};
    }
}

