/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.persistence.ContentEntityObjectDao
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.user.User
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.ReconciliationHistoryDao;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.SynchronyDao;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.SynchronyRequestsHistoryDao;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity.Reconciliation;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity.SynchronyRequest;
import com.atlassian.confluence.plugins.collaborative.content.feedback.exception.DataFetchException;
import com.atlassian.confluence.plugins.collaborative.content.feedback.rest.model.CollectMetadata;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.FileNameUtils;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.PermissionService;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.user.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataExportService {
    private static final Logger log = LoggerFactory.getLogger(DataExportService.class);
    private static final int BATCH_SIZE = 1000;
    private final PageManager pageManager;
    private final ContentEntityObjectDao contentEntityObjectDao;
    private final I18nResolver i18nResolver;
    private final SettingsManager settingsManager;
    private final PermissionService permissionService;
    private final SynchronyDao synchronyDao;
    private final ExecutorService executorService;
    private final ReconciliationHistoryDao reconciliationHistoryDao;
    private final SynchronyRequestsHistoryDao synchronyRequestsHistoryDao;

    @Autowired
    public DataExportService(@ComponentImport PageManager pageManager, @ComponentImport ContentEntityObjectDao contentEntityObjectDao, @ComponentImport I18nResolver i18nResolver, SettingsManager settingsManager, PermissionService permissionService, SynchronyDao synchronyDao, ReconciliationHistoryDao reconciliationHistoryDao, SynchronyRequestsHistoryDao synchronyRequestsHistoryDao) {
        this.pageManager = pageManager;
        this.contentEntityObjectDao = contentEntityObjectDao;
        this.i18nResolver = i18nResolver;
        this.permissionService = permissionService;
        this.settingsManager = settingsManager;
        this.synchronyDao = synchronyDao;
        this.reconciliationHistoryDao = reconciliationHistoryDao;
        this.synchronyRequestsHistoryDao = synchronyRequestsHistoryDao;
        this.executorService = this.initExecutor();
    }

    private ExecutorService initExecutor() {
        return new ThreadPoolExecutor(1, this.settingsManager.getMaxConcurrentRequests(), 5L, TimeUnit.MINUTES, new SynchronousQueue<Runnable>(), new ThreadFactory(){
            private final AtomicInteger seq = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable runnable) {
                int s = this.seq.getAndIncrement();
                Thread thread = new Thread(runnable);
                thread.setName("collab.feedback.worker-" + s);
                return thread;
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

    public String exportDataFor(long contentId, CollectMetadata meta) {
        this.enforceCreatePermissions(contentId);
        this.validate(contentId);
        try {
            return this.executorService.submit(() -> this.exportDataInternal(contentId, meta)).get(this.settingsManager.getOperationTimeout(), TimeUnit.SECONDS);
        }
        catch (RejectedExecutionException e) {
            throw DataFetchException.tooManyRequests(contentId, e);
        }
        catch (TimeoutException e) {
            throw DataFetchException.timeout(contentId, e);
        }
        catch (ExecutionException e) {
            throw DataFetchException.executionError(contentId, e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw DataFetchException.executionError(contentId, e);
        }
    }

    private void validate(long contentId) throws DataFetchException {
        this.validateContentId(contentId);
        this.validateContentExists(contentId);
        this.validateTotalNumberOfFiles(contentId);
    }

    private void validateContentId(long contentId) {
        if (contentId <= 0L) {
            throw DataFetchException.invalidContentId(contentId);
        }
    }

    private void validateContentExists(long contentId) {
        if (this.pageManager.getAbstractPage(contentId) == null) {
            throw DataFetchException.contentNotFound(contentId);
        }
    }

    private void validateTotalNumberOfFiles(long contentId) {
        long totalNumberOfFiles;
        if (this.settingsManager.getMaxFiles() > 0 && this.settingsManager.getDestinationFolder().listFiles() != null && (totalNumberOfFiles = Arrays.stream(this.settingsManager.getDestinationFolder().listFiles()).filter(file -> FileNameUtils.isValidFileName(file.getName())).count()) >= (long)this.settingsManager.getMaxFiles()) {
            throw DataFetchException.tooManyFiles(contentId);
        }
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private String exportDataInternal(long contentId, CollectMetadata meta) {
        File resultFile;
        long currentTimestamp = new Date().getTime();
        File eventsTmpFile = null;
        File snapshotsTmpFile = null;
        File pageHistoryTmpFile = null;
        File reconciliationHistoryTmpFile = null;
        File synchronyRequestHistoryTmpFile = null;
        File descriptorTmpFile = null;
        try {
            eventsTmpFile = FileNameUtils.eventsTmpFile(contentId, currentTimestamp);
            this.synchronyDao.exportEvents(contentId, eventsTmpFile);
            snapshotsTmpFile = FileNameUtils.snapshotsTmpFile(contentId, currentTimestamp);
            this.synchronyDao.exportSnapshots(contentId, snapshotsTmpFile);
            pageHistoryTmpFile = FileNameUtils.pageHistoryTmpFile(contentId, currentTimestamp);
            this.exportPageHistory(contentId, pageHistoryTmpFile);
            reconciliationHistoryTmpFile = FileNameUtils.reconciliationHistoryTmpFile(contentId, currentTimestamp);
            this.exportReconciliationHistory(contentId, reconciliationHistoryTmpFile);
            synchronyRequestHistoryTmpFile = FileNameUtils.synchronyRequestHistoryTmpFile(contentId, currentTimestamp);
            this.exportSynchronyRequestHistory(contentId, synchronyRequestHistoryTmpFile);
            descriptorTmpFile = FileNameUtils.descriptorTmpFile(contentId, currentTimestamp);
            this.saveAppProperties(this.settingsManager.getAppProperties(), meta, descriptorTmpFile);
            resultFile = new File(this.settingsManager.getDestinationFolder(), FileNameUtils.buildResultFileName(contentId, currentTimestamp));
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(resultFile));){
                Arrays.asList(eventsTmpFile, snapshotsTmpFile, descriptorTmpFile, pageHistoryTmpFile, reconciliationHistoryTmpFile, synchronyRequestHistoryTmpFile).forEach(file -> this.addZipEntry(contentId, zipOutputStream, (File)file));
            }
        }
        catch (FileNotFoundException e) {
            try {
                throw DataFetchException.fileError("Error creating a file", contentId, e);
                catch (IOException e2) {
                    throw DataFetchException.fileError("Error creating a zip file", contentId, e2);
                }
            }
            catch (Throwable throwable) {
                this.deleteSwallowingErrors(eventsTmpFile, snapshotsTmpFile, descriptorTmpFile, pageHistoryTmpFile, reconciliationHistoryTmpFile);
                throw throwable;
            }
        }
        this.deleteSwallowingErrors(eventsTmpFile, snapshotsTmpFile, descriptorTmpFile, pageHistoryTmpFile, reconciliationHistoryTmpFile);
        return resultFile.getName();
    }

    private void exportReconciliationHistory(long contentId, File reconciliationHistoryTmpFile) throws IOException {
        try (OutputStreamWriter outputWriter = new OutputStreamWriter(new FileOutputStream(reconciliationHistoryTmpFile));){
            DataExportService.batched((start, limit) -> this.reconciliationHistoryDao.history(contentId, (int)start, (int)limit), reconciliations -> {
                reconciliations.stream().map(Reconciliation::fromEntity).forEach(reconciliation -> {
                    try {
                        outputWriter.write(reconciliation.toCsvString());
                    }
                    catch (IOException e) {
                        throw DataFetchException.fileError("Error fetching reconciliation history", contentId, e);
                    }
                });
                return null;
            }, 1000);
        }
    }

    private void exportSynchronyRequestHistory(long contentId, File synchronyRequestHistoryTmpFile) throws IOException {
        AbstractPage abstractPage = this.pageManager.getAbstractPage(contentId);
        ContentEntityObject draft = abstractPage.isDraft() ? null : this.contentEntityObjectDao.findDraftFor(abstractPage.getId());
        long draftId = 0L;
        if (draft instanceof AbstractPage) {
            draftId = draft.getId();
        }
        try (OutputStreamWriter outputWriter = new OutputStreamWriter(new FileOutputStream(synchronyRequestHistoryTmpFile));){
            Arrays.asList(draftId, contentId).forEach(contentIdToFetchDataFor -> {
                if (contentIdToFetchDataFor != 0L) {
                    DataExportService.batched((start, limit) -> this.synchronyRequestsHistoryDao.history((long)contentIdToFetchDataFor, (int)start, (int)limit), synchronyRequests -> {
                        synchronyRequests.stream().map(SynchronyRequest::fromEntity).forEach(request -> {
                            try {
                                outputWriter.write(request.toCsvString());
                            }
                            catch (IOException e) {
                                throw DataFetchException.fileError("Error fetching history of requests to Synchrony", contentIdToFetchDataFor, e);
                            }
                        });
                        return null;
                    }, 1000);
                }
            });
        }
    }

    private void exportPageHistory(long contentId, File pageHistoryTmpFile) throws IOException {
        List summaries = this.pageManager.getVersionHistorySummaries(this.pageManager.getById(contentId));
        try (OutputStreamWriter outputWriter = new OutputStreamWriter(new FileOutputStream(pageHistoryTmpFile));){
            summaries.forEach(versionHistorySummary -> {
                AbstractPage page = this.pageManager.getAbstractPage(versionHistorySummary.getId());
                try {
                    outputWriter.write("Version: " + versionHistorySummary.getVersion() + "\n");
                    outputWriter.write("Type: " + page.getBodyContent().getBodyType() + "\n");
                    outputWriter.write("Body:\n" + page.getBodyContent().getBody() + "\n\n\n");
                }
                catch (IOException e) {
                    throw DataFetchException.fileError("Error fetching page history", contentId, e);
                }
            });
        }
    }

    private void addZipEntry(long contentId, ZipOutputStream zipOutputStream, File fileToAdd) {
        byte[] buffer = new byte[1024];
        try (FileInputStream fileInputStream = new FileInputStream(fileToAdd);){
            int length;
            zipOutputStream.putNextEntry(new ZipEntry(fileToAdd.getName()));
            while ((length = fileInputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, length);
            }
            zipOutputStream.closeEntry();
        }
        catch (IOException e) {
            throw DataFetchException.fileError("Error creating a zip file", contentId, e);
        }
    }

    private void deleteSwallowingErrors(File ... files) {
        for (File file : files) {
            try {
                if (file == null || !file.exists() || file.delete()) continue;
                log.debug("Error deleting temporary file {}", (Object)file);
            }
            catch (Exception swallowed) {
                log.debug("Error deleting file {}", (Object)file, (Object)swallowed);
            }
        }
    }

    private void saveAppProperties(Properties appProperties, CollectMetadata meta, File descriptorFile) throws IOException {
        appProperties.setProperty("description", meta.getProblemDescription());
        appProperties.setProperty(this.i18nResolver.getText("collaborative.editing.feedback.page.dialog.checkbox1"), String.valueOf(meta.isCheckbox1()));
        appProperties.setProperty(this.i18nResolver.getText("collaborative.editing.feedback.page.dialog.checkbox2"), String.valueOf(meta.isCheckbox2()));
        try (FileOutputStream stream = new FileOutputStream(descriptorFile);){
            appProperties.store(stream, null);
        }
    }

    private void enforceCreatePermissions(long contentId) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!(this.permissionService.isSysAdmin((User)user) || this.settingsManager.isEditorReportsEnabled() && this.permissionService.canEdit((User)user, contentId) && this.settingsManager.collaborativeEditingEnabled())) {
            throw new AuthorisationException();
        }
    }

    private static <V> void batched(BiFunction<Integer, Integer, List<V>> dataProvider, Function<List<V>, Void> dataProcessor, int batchSize) {
        List<V> data;
        int lastBatchSize;
        int i = 0;
        do {
            data = dataProvider.apply(i, batchSize);
            dataProcessor.apply(data);
            i += batchSize;
        } while ((lastBatchSize = data.size()) == batchSize);
    }

    @PreDestroy
    public void tearDown() {
        if (this.executorService != null) {
            this.executorService.shutdown();
        }
    }
}

