/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.conversion.convert.image.PdfConversionSupport
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.conversion.convert.image.SlidesConverter
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.benryan.components.OcSettingsManager
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.Maps
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.ListenableFutureTask
 *  com.google.common.util.concurrent.MoreExecutors
 *  com.google.common.util.concurrent.SettableFuture
 *  net.jcip.annotations.GuardedBy
 *  org.apache.commons.io.IOUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.core.io.InputStreamResource
 *  org.springframework.core.io.Resource
 *  org.springframework.stereotype.Component
 */
package com.benryan.components;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.conversion.convert.image.PdfConversionSupport;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.conversion.convert.image.SlidesConverter;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.util.concurrent.ThreadFactories;
import com.benryan.components.AbstractConversionCacheManager;
import com.benryan.components.ConvertedPageResult;
import com.benryan.components.OcSettingsManager;
import com.benryan.components.SlideCacheManager;
import com.benryan.conversion.AbstractSlideConversionTask;
import com.benryan.conversion.AttachmentTempFileSupplier;
import com.benryan.conversion.FilePathAwareConversionStore;
import com.benryan.conversion.LocalFilePathAwareConversionStore;
import com.benryan.conversion.PPtDocumentConversionTask;
import com.benryan.conversion.PdfSlideConversionBatchTask;
import com.benryan.conversion.PdfSlideSandboxConversionTask;
import com.benryan.conversion.PptDocumentSandboxConversionTask;
import com.benryan.conversion.SandboxConversionFeature;
import com.benryan.conversion.SlideConversionDataHolder;
import com.benryan.conversion.SlideDocConversionData;
import com.benryan.conversion.SlidePageConversionData;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.jcip.annotations.GuardedBy;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component(value="slideCacheManager")
public class DefaultSlideCacheManager
extends AbstractConversionCacheManager<QueueData, SlidePageConversionData>
implements SlideCacheManager {
    private static final String BY_PASSING_SANDBOX_DARK_FEATURE_KEY = "officeconnector.slide.sandbox.bypass";
    private static final int KEEP_ALIVE = 120;
    private final int CONVERSION_BATCH_SIZE = Integer.getInteger("officeconnector.slide.conversion.batchsize", 4);
    private ThreadPoolExecutor conversionQueue;
    private FilePathAwareConversionStore conversionStore;
    private File tempDir;
    private final Sandbox sandbox;
    private final SandboxConversionFeature sandboxConversionFeature;
    private final AttachmentTempFileSupplier attachmentTempFileSupplier;
    private final DarkFeatureManager darkFeatureManager;
    @GuardedBy(value="self")
    private final BiMap<QueueData, ConvertedPageResult<?>> beingConverted = Maps.synchronizedBiMap((BiMap)HashBiMap.create(new LinkedHashMap()));

    @Autowired
    public DefaultSlideCacheManager(@ComponentImport PageManager pageManager, @ComponentImport AttachmentManager fileManager, OcSettingsManager ocSettingsManager, @ComponentImport PluginAccessor pluginAccessor, @Qualifier(value="officeConnectorConversionSandbox") Sandbox sandbox, SandboxConversionFeature sandboxConversionFeature, AttachmentTempFileSupplier attachmentTempFileSupplier, @ComponentImport DarkFeatureManager darkFeatureManager) {
        super(pageManager, fileManager, ocSettingsManager, pluginAccessor);
        this.initCache();
        this.sandbox = sandbox;
        this.sandboxConversionFeature = sandboxConversionFeature;
        this.attachmentTempFileSupplier = attachmentTempFileSupplier;
        this.darkFeatureManager = darkFeatureManager;
    }

    @Override
    public Future<SlidePageConversionData> getSlideConversionData(Attachment attachment, int slideNum) {
        ListenableFuture<SlidePageConversionData> future = this.getFuture(attachment, slideNum);
        if (slideNum == 0 || slideNum == 1) {
            return future;
        }
        this.preEmptivePageLoad(future, attachment, slideNum);
        return future;
    }

    private void preEmptivePageLoad(ListenableFuture<SlidePageConversionData> currentBatch, Attachment attachment, int slideNum) {
        if (slideNum % this.CONVERSION_BATCH_SIZE > 0) {
            currentBatch.addListener(() -> {
                try {
                    int premptivePageLoad = (slideNum / this.CONVERSION_BATCH_SIZE + 1) * this.CONVERSION_BATCH_SIZE;
                    int numSlides = ((SlidePageConversionData)currentBatch.get()).getParent().getNumSlides();
                    if (premptivePageLoad < numSlides) {
                        this.getFuture(attachment, premptivePageLoad, numSlides);
                    }
                }
                catch (Exception e) {
                    log.warn("Failed queue up the next batch: " + e.getMessage(), (Throwable)e);
                }
            }, MoreExecutors.directExecutor());
        }
    }

    private ListenableFuture<SlidePageConversionData> getFuture(Attachment attachment, int slideNum) {
        return this.getFuture(attachment, slideNum, Integer.MAX_VALUE);
    }

    private ListenableFuture<SlidePageConversionData> getFuture(Attachment attachment, int slideNum, int numSlides) {
        QueueData key = this.getKey(new SlideDocConversionData(attachment), slideNum);
        ListenableFuture futureTask = (ListenableFuture)this.beingConverted.get((Object)key);
        if (futureTask != null) {
            return futureTask;
        }
        SlidePageConversionData data = (SlidePageConversionData)this.getFromCache(key);
        if (data == null || data.getParent().getQueueDate() == null) {
            return this.queueNewConversion(attachment, slideNum, numSlides);
        }
        Date queueDate = data.getParent().getQueueDate();
        if (queueDate.before(attachment.getLastModificationDate()) || queueDate.before(this.lastUpgrade)) {
            return this.queueNewConversion(attachment, slideNum, numSlides);
        }
        InputStream inputStream = this.conversionStore.readFile(data.id);
        if (inputStream == null) {
            return this.queueNewConversion(attachment, slideNum, numSlides);
        }
        SettableFuture settableFuture = SettableFuture.create();
        settableFuture.set((Object)data);
        return settableFuture;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void finish(ConvertedPageResult<?> task) {
        try {
            BiMap<QueueData, ConvertedPageResult<?>> page;
            if (!task.isDone()) {
                throw new IllegalArgumentException("Should only finish tasks that are done :" + task);
            }
            if (!task.isCancelled() && (page = task.get()) != null) {
                this.putToCache(this.getKey((SlidePageConversionData)page), page);
            }
        }
        catch (ExecutionException ee) {
            log.error("Error converting page, could not create slide", (Throwable)ee);
        }
        catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        finally {
            BiMap<QueueData, ConvertedPageResult<?>> ee = this.beingConverted;
            synchronized (ee) {
                this.beingConverted.inverse().remove(task);
            }
        }
    }

    private QueueData getKey(SlidePageConversionData page) {
        return new QueueData(page.getParent(), page.getSlideNum());
    }

    private QueueData getKey(SlideDocConversionData id, int pageNum) {
        return new QueueData(id, pageNum);
    }

    @Override
    public void initCache() {
        super.initCache();
        int poolSize = this.ocSettingsManager.getMaxQueues();
        if (this.conversionQueue == null) {
            this.conversionQueue = new ThreadPoolExecutor(poolSize, poolSize, 120L, TimeUnit.SECONDS, new LinkedBlockingQueue(), ThreadFactories.namedThreadFactory((String)this.getClass().getSimpleName())){

                @Override
                protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
                    return ListenableFutureTask.create(callable);
                }
            };
        } else {
            this.conversionQueue.setCorePoolSize(poolSize);
            this.conversionQueue.setMaximumPoolSize(poolSize);
        }
        this.tempDir = new File(this.ocSettingsManager.getCacheDir() + File.separator + "temp");
        if (this.tempDir.exists()) {
            File[] files = this.tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.delete()) continue;
                    log.error("Can't delete DefaultSlideCacheManager cache file: " + file.getAbsolutePath());
                }
            }
        } else if (!this.tempDir.mkdirs()) {
            log.error("Can't create temp directory for conversion queue: dir=" + this.tempDir.getAbsolutePath());
        }
        this.conversionStore = new LocalFilePathAwareConversionStore(this.tempDir.getPath());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFromQueue(long attachmentId) {
        BiMap<QueueData, ConvertedPageResult<?>> biMap = this.beingConverted;
        synchronized (biMap) {
            for (QueueData queueData : new HashSet(this.beingConverted.keySet())) {
                Future data;
                if (queueData.getConversionData().getKey() != attachmentId || (data = (Future)this.beingConverted.get((Object)queueData)) == null || !data.isDone() && !data.cancel(true)) continue;
                this.beingConverted.remove((Object)queueData);
            }
        }
    }

    private synchronized ListenableFuture<SlidePageConversionData> queueNewConversion(Attachment file, int slideNum, int numSlides) {
        SlideDocConversionData data = new SlideDocConversionData(file.getId(), file.getContainer().getTitle(), file.getFileName());
        String attachmentName = file.getFileName();
        String lowerCaseName = attachmentName.toLowerCase();
        ConvertedPageResult slideConversionResult = (ConvertedPageResult)((Object)this.beingConverted.get((Object)this.getKey(data, slideNum)));
        if (slideConversionResult != null) {
            return ConvertedPageResult.copySlideConversionResult(slideConversionResult, slideNum);
        }
        if (lowerCaseName.endsWith(".ppt") || lowerCaseName.endsWith(".pptx")) {
            return this.queueNewPptConversion(file, attachmentName, data, slideNum, numSlides);
        }
        if (lowerCaseName.endsWith(".pdf")) {
            return this.queueNewPdfConversion(file, attachmentName, data, slideNum, numSlides);
        }
        throw new IllegalArgumentException("Cannot create conversion task for file extension, supported types are .pdf, .ppt and .pptx, but got : " + attachmentName);
    }

    private boolean shouldUsingSandbox() {
        boolean shouldByPassSandbox = this.darkFeatureManager.isEnabledForAllUsers(BY_PASSING_SANDBOX_DARK_FEATURE_KEY).orElseGet(() -> false);
        return this.sandboxConversionFeature.isEnable() != false && !shouldByPassSandbox;
    }

    private ListenableFuture<SlidePageConversionData> queueNewPptConversion(Attachment file, String attachmentName, SlideDocConversionData data, int slideNum, int numSlides) {
        SlidesConverter slidesConverter = new SlidesConverter();
        int totalPages = 0;
        try {
            totalPages = slidesConverter.getTotalPageNumber(this.convertToInputStreamResource(file).getInputStream());
            data.setNumSlides(totalPages);
        }
        catch (IOException e) {
            log.error("Could not total pages for PPT file, attachment {}", (Object)attachmentName);
        }
        Resource inputStreamSource = this.convertToInputStreamResource(file);
        List<Integer> pages = this.getBatchPages(slideNum, numSlides);
        AbstractSlideConversionTask task = this.shouldUsingSandbox() ? new PptDocumentSandboxConversionTask(this.conversionStore, this.sandbox, file, attachmentName, inputStreamSource, this.attachmentTempFileSupplier, slideNum, data) : new PPtDocumentConversionTask(file, attachmentName, inputStreamSource, this.conversionStore, data, pages);
        return this.queueNewSlideConversion(task, data, slideNum, pages);
    }

    private void cacheSlidePageConversionData(SlideConversionDataHolder holder) {
        for (SlidePageConversionData page : holder) {
            this.putToCache(this.getKey(page), page);
        }
    }

    private void cacheSlide(ListenableFuture<SlideConversionDataHolder> future) {
        future.addListener(() -> {
            try {
                SlideConversionDataHolder holder = (SlideConversionDataHolder)future.get();
                if (holder != null) {
                    this.cacheSlidePageConversionData(holder);
                }
            }
            catch (Exception ex) {
                log.error("Could not convert powerpoint", (Throwable)ex);
            }
        }, MoreExecutors.directExecutor());
    }

    private ListenableFuture<SlidePageConversionData> queueNewPdfConversion(Attachment file, String attachmentName, SlideDocConversionData data, int slideNum, int numSlides) {
        PdfConversionSupport pdfConverterSupport = new PdfConversionSupport();
        int totalPages = 0;
        try {
            totalPages = pdfConverterSupport.getTotalPageNumber(this.convertToInputStreamResource(file).getInputStream());
            data.setNumSlides(totalPages);
        }
        catch (IOException e) {
            log.error("Could not total pages for PDF file, attachment {}", (Object)attachmentName);
        }
        Resource inputStreamSource = this.convertToInputStreamResource(file);
        List<Integer> pages = this.getBatchPages(slideNum, numSlides);
        AbstractSlideConversionTask pdfConversionTask = this.shouldUsingSandbox() ? new PdfSlideSandboxConversionTask(this.conversionStore, this.sandbox, file, attachmentName, inputStreamSource, this.attachmentTempFileSupplier, slideNum, data) : new PdfSlideConversionBatchTask(file, attachmentName, inputStreamSource, data, pages, this.conversionStore);
        return this.queueNewSlideConversion(pdfConversionTask, data, slideNum, pages);
    }

    private ListenableFuture<SlidePageConversionData> queueNewSlideConversion(AbstractSlideConversionTask slideConversionTask, SlideDocConversionData data, int slideNum, List<Integer> batchedPages) {
        ListenableFuture future = (ListenableFuture)this.conversionQueue.submit(slideConversionTask);
        this.cacheSlide((ListenableFuture<SlideConversionDataHolder>)future);
        batchedPages.stream().forEach(convertingSlideNumber -> this.addToBeingConverted(this.getKey(data, (int)convertingSlideNumber), ConvertedPageResult.createSlideConversionResult((ListenableFuture<SlideConversionDataHolder>)future, convertingSlideNumber)));
        return ConvertedPageResult.createSlideConversionResult((ListenableFuture<SlideConversionDataHolder>)future, slideNum);
    }

    private List<Integer> getBatchPages(int slideNum, int numSlides) {
        if (this.shouldUsingSandbox()) {
            return Arrays.asList(slideNum);
        }
        ArrayList<Integer> pages = new ArrayList<Integer>(this.CONVERSION_BATCH_SIZE);
        int minSlideNum = slideNum - slideNum % this.CONVERSION_BATCH_SIZE;
        int maxSlideNum = Math.min(minSlideNum + this.CONVERSION_BATCH_SIZE, numSlides);
        for (int i = minSlideNum; i < maxSlideNum; ++i) {
            pages.add(i);
        }
        if (pages.isEmpty()) {
            throw new IllegalArgumentException("Slid conversion pages is required for this type of conversion task");
        }
        return pages;
    }

    private void addToBeingConverted(QueueData key, ConvertedPageResult<?> conversionResult) {
        this.beingConverted.put((Object)key, conversionResult);
        conversionResult.addListener(() -> this.finish(conversionResult), MoreExecutors.directExecutor());
    }

    private synchronized Resource convertToInputStreamResource(Attachment attachment) {
        return new InputStreamResource(this.fileManager.getAttachmentData(attachment));
    }

    @Override
    public File getTempDir() {
        return this.tempDir;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<QueueData> getBeingConvertedKeys() {
        BiMap<QueueData, ConvertedPageResult<?>> biMap = this.beingConverted;
        synchronized (biMap) {
            return new HashSet<QueueData>(this.beingConverted.keySet());
        }
    }

    @Override
    public void writeSlideToStream(SlidePageConversionData data, OutputStream out) throws IOException {
        try (InputStream inputStream = this.conversionStore.readFile(data.id);){
            IOUtils.copyLarge((InputStream)inputStream, (OutputStream)out);
        }
    }

    public static class QueueData {
        private final SlideDocConversionData conversionData;
        private final int slideNum;

        public QueueData(SlideDocConversionData conversionData, int slideNum) {
            this.slideNum = slideNum;
            this.conversionData = conversionData;
        }

        public SlideDocConversionData getConversionData() {
            return this.conversionData;
        }

        public int getSlideNum() {
            return this.slideNum;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (int)this.conversionData.getKey();
            result = 31 * result + this.slideNum;
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            QueueData other = (QueueData)obj;
            if (this.conversionData.getKey() != other.conversionData.getKey()) {
                return false;
            }
            return this.slideNum == other.slideNum;
        }

        public String toString() {
            return "slide-" + this.conversionData.getKey() + "-" + this.slideNum;
        }
    }
}

