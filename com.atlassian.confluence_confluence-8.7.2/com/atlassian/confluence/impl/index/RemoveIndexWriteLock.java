/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.index;

import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import java.io.File;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveIndexWriteLock
implements LifecycleItem {
    private static final Logger log = LoggerFactory.getLogger(RemoveIndexWriteLock.class);

    public void startup(LifecycleContext lifecycleContext) {
        File indexPath = ConfluenceDirectories.getLegacyLuceneIndexDirectory();
        File edgeIndexPath = new File(indexPath, "edge");
        File changeIndexPath = new File(indexPath, "change");
        Stream.of(indexPath, edgeIndexPath, changeIndexPath).map(path -> new File((File)path, "write.lock")).filter(File::exists).forEach(lockFile -> {
            boolean result = lockFile.delete();
            log.info("Deleted lock found on start up '{}'. Successful='{}'", lockFile, (Object)result);
        });
    }

    public void shutdown(LifecycleContext lifecycleContext) throws Exception {
    }
}

