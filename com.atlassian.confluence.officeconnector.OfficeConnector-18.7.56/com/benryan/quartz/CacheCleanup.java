/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.benryan.components.OcSettingsManager
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.benryan.quartz;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.benryan.components.DefaultSlideCacheManager;
import com.benryan.components.OcSettingsManager;
import com.benryan.components.SlideCacheManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="cacheCleanupJob")
public class CacheCleanup
implements JobRunner {
    private final OcSettingsManager settingsManager;
    private final SlideCacheManager slideCacheManager;
    private static final Logger log = LoggerFactory.getLogger(CacheCleanup.class);
    private static final Comparator fileDateComparator = Comparator.comparing(obj -> ((File)obj).lastModified());

    @Autowired
    public CacheCleanup(OcSettingsManager settingsManager, SlideCacheManager slideCacheManager) {
        this.settingsManager = settingsManager;
        this.slideCacheManager = slideCacheManager;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        long maxSize = this.settingsManager.getMaxCacheSize() * 1024 * 1024;
        File tempDir = this.slideCacheManager.getTempDir();
        this.deleteAllTempFiles(tempDir, this.slideCacheManager);
        int cacheType = this.settingsManager.getCacheType();
        switch (cacheType) {
            case 0: 
            case 1: {
                String path = this.settingsManager.getCacheDir();
                this.cullOldFiles(path, maxSize);
                break;
            }
            default: {
                log.error("Unhandled cache type {}", (Object)cacheType);
            }
        }
        return null;
    }

    private void cullOldFiles(String path, long maxSize) {
        long sum = 0L;
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children == null) {
                return;
            }
            ArrayList<File> fileList = new ArrayList<File>();
            for (File aChildren : children) {
                if (!aChildren.isFile()) continue;
                sum += aChildren.length();
                fileList.add(aChildren);
            }
            if (sum > maxSize) {
                fileList.sort(fileDateComparator);
                int x = 0;
                while (x < fileList.size() && sum > maxSize) {
                    File oldestFile = (File)fileList.get(x++);
                    long oldestSize = oldestFile.length();
                    if (oldestFile.delete()) {
                        sum -= oldestSize;
                        continue;
                    }
                    log.warn("Unable to delete cached conversion " + oldestFile.getAbsolutePath());
                }
                if (sum > maxSize) {
                    log.error("Unable to delete enough files to get cache below max size");
                }
            }
        }
    }

    private void deleteAllTempFiles(File tempDir, SlideCacheManager slideManager) {
        if (tempDir.exists() && tempDir.isDirectory()) {
            Set<DefaultSlideCacheManager.QueueData> beingConvertedKeys = slideManager.getBeingConvertedKeys();
            File[] children = tempDir.listFiles();
            if (children == null) {
                return;
            }
            for (File aChildren : children) {
                String key = aChildren.getName();
                String string = key = key.endsWith(".tmp") ? key.substring(0, key.length() - 4) : key;
                if (beingConvertedKeys.contains(key) || aChildren.delete()) continue;
                log.warn("Unable to delete temp file " + aChildren.getAbsolutePath());
            }
        }
    }
}

