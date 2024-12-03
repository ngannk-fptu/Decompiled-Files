/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.salext.bundle;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import com.atlassian.troubleshooting.api.supportzip.FileSupportZipArtifact;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.bundle.AbstractSupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.BundleManifest;
import com.atlassian.troubleshooting.stp.salext.bundle.threaddump.LegacyThreadDumpGenerator;
import com.atlassian.troubleshooting.stp.salext.bundle.threaddump.TDACompatibleThreadDumpGenerator;
import com.atlassian.troubleshooting.stp.salext.bundle.threaddump.ThreadDumpGenerator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ThreadDumpBundle
extends AbstractSupportZipBundle {
    static final String PROPERTY_THREAD_DUMP_COUNT = "stp.threaddump.count";
    static final String PROPERTY_THREAD_DUMP_INTERVAL = "stp.threaddump.interval";
    static final int DEFAULT_THREAD_DUMP_COUNT = 3;
    static final int DEFAULT_THREAD_DUMP_INTERVAL = 5;
    private static final Logger LOG = LoggerFactory.getLogger(ThreadDumpBundle.class);
    private static final DateFormat FILE_NAME_TS_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssS");
    private static final List<Class<? extends ThreadDumpGenerator>> THREAD_DUMP_GENERATORS = ImmutableList.of(TDACompatibleThreadDumpGenerator.class, LegacyThreadDumpGenerator.class);
    private final SupportApplicationInfo applicationInfo;
    private final int threadDumpCount;
    private final long threadDumpIntervalMillis;

    @Autowired
    public ThreadDumpBundle(SupportApplicationInfo applicationInfo, I18nResolver i18nResolver) {
        this(applicationInfo, Integer.getInteger(PROPERTY_THREAD_DUMP_COUNT, 3), TimeUnit.SECONDS.toMillis(Integer.getInteger(PROPERTY_THREAD_DUMP_INTERVAL, 5).intValue()), i18nResolver);
    }

    @VisibleForTesting
    protected ThreadDumpBundle(SupportApplicationInfo applicationInfo, int threadDumpCount, long threadDumpIntervalMillis, I18nResolver i18nResolver) {
        super(i18nResolver, BundleManifest.THREAD_DUMP, "stp.zip.include.threadDump", "stp.zip.include.threadDump.description");
        this.applicationInfo = applicationInfo;
        this.threadDumpCount = threadDumpCount;
        this.threadDumpIntervalMillis = threadDumpIntervalMillis;
    }

    @Override
    public BundleCategory getCategory() {
        return BundleCategory.OTHER;
    }

    public List<SupportZipBundle.Artifact> getArtifacts() {
        try {
            return Collections.singletonList(new FileSupportZipArtifact(this.generateThreadDump()));
        }
        catch (IOException e) {
            LOG.error("Failed to generate a thread dump.", (Throwable)e);
        }
        catch (InterruptedException e) {
            LOG.error("Failed to generate a thread dump.", (Throwable)e);
        }
        return Collections.emptyList();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public File generateThreadDump() throws IOException, InterruptedException {
        File logDirectory = new File(this.applicationInfo.getApplicationHome(), "logs/support");
        FileUtils.forceMkdir((File)logDirectory);
        int count = 0;
        String nameFormat = "threaddump_" + FILE_NAME_TS_FORMAT.format(new Date()) + "-%d.tdump";
        while (new File(logDirectory, String.format(nameFormat, count)).exists()) {
            ++count;
        }
        File threadDump = new File(logDirectory, String.format(nameFormat, count));
        if (!threadDump.createNewFile()) {
            throw new IOException("Failed to create file " + threadDump.getAbsolutePath());
        }
        FileOutputStream out = new FileOutputStream(threadDump);
        try {
            ThreadDumpGenerator generator = this.getFirstAvailableThreadDumpGenerator();
            if (generator != null) {
                for (int i = 1; i <= this.threadDumpCount; ++i) {
                    LOG.info("Generating thread dump {} of {}", (Object)i, (Object)this.threadDumpCount);
                    generator.generateThreadDump(out, this.applicationInfo);
                    if (i == this.threadDumpCount) continue;
                    LOG.info("Waiting {} seconds to take another thread dump", (Object)TimeUnit.MILLISECONDS.toSeconds(this.threadDumpIntervalMillis));
                    Thread.sleep(this.threadDumpIntervalMillis);
                }
            } else {
                LOG.error("Failed to generate thread dump: none of the generators can be initialized");
            }
        }
        finally {
            IOUtils.closeQuietly((OutputStream)out);
        }
        return threadDump;
    }

    private ThreadDumpGenerator getFirstAvailableThreadDumpGenerator() {
        for (Class<? extends ThreadDumpGenerator> clazz : THREAD_DUMP_GENERATORS) {
            try {
                LOG.info("Attempting to instantiate strategy {}", (Object)clazz.getName());
                return clazz.newInstance();
            }
            catch (Exception e) {
                LOG.info("Unable to instantiate strategy {}", (Object)clazz.getName(), (Object)e);
            }
        }
        return null;
    }
}

