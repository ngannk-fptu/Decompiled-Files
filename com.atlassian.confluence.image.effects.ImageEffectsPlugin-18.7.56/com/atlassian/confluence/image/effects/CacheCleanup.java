/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.image.effects.ImageCache;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(value="cacheCleanup")
public class CacheCleanup
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(CacheCleanup.class);
    private final ImageCache imageCache;

    @Inject
    public CacheCleanup(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        try {
            File cachePath = this.imageCache.cacheDir();
            this.cullOldFiles(cachePath, 500);
            return JobRunnerResponse.success();
        }
        catch (Exception e) {
            return JobRunnerResponse.failed((Throwable)e);
        }
    }

    private void cullOldFiles(File dir, int maxSize) {
        File[] children;
        if (dir.exists() && dir.isDirectory() && (children = dir.listFiles()) != null && children.length > maxSize) {
            Arrays.sort(children, Comparator.comparingLong(File::lastModified));
            int endIndex = children.length - maxSize;
            for (int x = 0; x < endIndex && endIndex < children.length; ++x) {
                File oldestFile = children[x];
                if (oldestFile.getName().startsWith("preview")) {
                    ++endIndex;
                    continue;
                }
                if (oldestFile.delete()) continue;
                log.warn("Unable to delete cached conversion " + oldestFile.getAbsolutePath());
            }
        }
    }
}

