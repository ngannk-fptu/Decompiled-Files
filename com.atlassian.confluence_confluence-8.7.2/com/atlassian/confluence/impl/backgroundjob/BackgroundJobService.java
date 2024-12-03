/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.impl.backgroundjob;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.impl.backgroundjob.BackgroundJobProcessor;
import com.atlassian.confluence.impl.backgroundjob.BackgroundJobResponse;
import com.atlassian.confluence.impl.backgroundjob.dao.BackgroundJobDAO;
import com.atlassian.confluence.impl.backgroundjob.domain.ArchivedBackgroundJob;
import com.atlassian.confluence.impl.backgroundjob.domain.BackgroundJob;
import com.atlassian.confluence.impl.backgroundjob.domain.BackgroundJobState;
import com.atlassian.confluence.impl.backgroundjob.exception.BackgroundJobServiceNotFound;
import com.atlassian.confluence.util.Cleanup;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class BackgroundJobService {
    private static final Logger log = LoggerFactory.getLogger(BackgroundJobService.class);
    private static final int MAX_NUMBER_OF_FAILURES = Integer.getInteger("confluence.background-job.max-number-of-failures", 7);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicBoolean singletonesAreInitialised = new AtomicBoolean();
    private final BackgroundJobDAO backgroundJobDAO;
    private final Supplier<TransactionTemplate> transactionTemplateSupplier;
    private static final long RECOMMENDED_TIMEOUT_IN_SEC = Long.getLong("confluence.background-job.recommended-timeout.ms", 60L);
    private final Map<String, BackgroundJobProcessor> processorMap;
    private final Supplier<Instant> timeSupplier;

    public BackgroundJobService(BackgroundJobDAO backgroundJobDAO, PlatformTransactionManager transactionManager, List<BackgroundJobProcessor> processors) {
        this(backgroundJobDAO, processors, Instant::now, () -> {
            DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(3);
            return new TransactionTemplate(transactionManager, (TransactionDefinition)transactionAttribute);
        });
    }

    @VisibleForTesting
    public BackgroundJobService(BackgroundJobDAO backgroundJobDAO, List<BackgroundJobProcessor> processors, Supplier<Instant> timeSupplier, Supplier<TransactionTemplate> transactionTemplateSupplier) {
        this.backgroundJobDAO = backgroundJobDAO;
        this.processorMap = new ConcurrentHashMap<String, BackgroundJobProcessor>(processors.stream().collect(Collectors.toMap(this::getProcessorName, x -> x)));
        this.timeSupplier = timeSupplier;
        this.transactionTemplateSupplier = transactionTemplateSupplier;
    }

    public Long addJob(Class<?> processorClass, Map<String, Object> parameters, String description, Instant runAt) {
        this.checkTheServiceExists(this.getProcessorName(processorClass));
        Instant now = this.timeSupplier.get();
        BackgroundJob job = new BackgroundJob();
        job.setType(this.getProcessorName(processorClass));
        job.setCreationTime(now);
        job.setIterationNumber(0);
        job.setNumberOfFailures(0);
        job.setParameters(this.convertToJson(parameters));
        job.setRunAt(runAt);
        job.setLastTouchTime(now);
        job.setDuration(0L);
        job.setDescription(description);
        this.backgroundJobDAO.save(job);
        return job.getId();
    }

    @Internal
    public int processNextJobs() {
        if (!this.singletonesAreInitialised.get()) {
            this.createSingletonsIfRequired(this.processorMap);
            this.singletonesAreInitialised.set(true);
        }
        List backgroundJobs = (List)this.doInTransaction(tx -> this.backgroundJobDAO.getAllJobsReadyToRunSortedById(Instant.now()));
        for (BackgroundJob backgroundJob : backgroundJobs) {
            BackgroundJobProcessor processor = this.processorMap.get(backgroundJob.getType());
            if (processor == null) {
                String warningMessage = "Processor with type " + backgroundJob.getType() + " and id " + backgroundJob.getId() + " was not found";
                log.warn(warningMessage);
                this.delayNextExecution(backgroundJob.getId(), 0L, warningMessage);
                continue;
            }
            this.runBackgroundProcess(processor, backgroundJob.getId());
        }
        return backgroundJobs.size();
    }

    @VisibleForTesting
    public void registerBackgroundJobProcessor(BackgroundJobProcessor backgroundJobProcessor) {
        this.processorMap.put(this.getProcessorName(backgroundJobProcessor), backgroundJobProcessor);
    }

    @VisibleForTesting
    public void unregisterBackgroundJobProcessor(BackgroundJobProcessor backgroundJobProcessor) {
        this.processorMap.remove(this.getProcessorName(backgroundJobProcessor));
    }

    private void createSingletonsIfRequired(Map<String, BackgroundJobProcessor> processorMap) {
        processorMap.forEach((key, value) -> {
            if (value.isSingleton()) {
                try {
                    this.createSingletonJobIfNeeded((String)key, (BackgroundJobProcessor)value);
                }
                catch (BackgroundJobServiceNotFound e) {
                    log.warn("Unable to initialise singleton job for type " + key + " with error: " + e.getMessage(), (Throwable)e);
                }
            }
        });
    }

    private void createSingletonJobIfNeeded(String type, BackgroundJobProcessor processor) throws BackgroundJobServiceNotFound {
        List backgroundJobs = (List)this.doInTransaction(tx -> this.backgroundJobDAO.findActiveJobsByType(type));
        if (backgroundJobs.size() == 1) {
            return;
        }
        if (backgroundJobs.size() > 1) {
            String warningMessage = "Found more than 1 background job with type " + type + ". All of them will be cancelled and a new one will be created";
            log.warn(warningMessage);
            backgroundJobs.forEach(job -> this.finishJob((BackgroundJob)job, BackgroundJobState.CANCELLED, warningMessage));
        }
        this.doInTransaction(tx -> {
            this.addJob(processor.getClass(), Collections.emptyMap(), "Repetitive task", Instant.now());
            return null;
        });
        log.info("A job with type {} was added", (Object)type);
    }

    private void finishJob(BackgroundJob job, BackgroundJobState state, String errorMessage) {
        this.backgroundJobDAO.remove(job);
        ArchivedBackgroundJob archivedBackgroundJob = this.createdArchivedJob(job, state);
        archivedBackgroundJob.setErrorMessage(errorMessage);
        this.backgroundJobDAO.saveArchived(archivedBackgroundJob);
    }

    private ArchivedBackgroundJob createdArchivedJob(BackgroundJob job, BackgroundJobState state) {
        ArchivedBackgroundJob archived = new ArchivedBackgroundJob();
        archived.setId(job.getId());
        archived.setCompletionTime(this.timeSupplier.get());
        archived.setState(state);
        archived.setCreationTime(job.getCreationTime());
        archived.setDescription(job.getDescription());
        archived.setDuration(job.getDuration());
        archived.setIterationNumber(job.getIterationNumber());
        archived.setNumberOfFailures(job.getNumberOfFailures());
        archived.setOwner(job.getOwner());
        archived.setType(job.getType());
        return archived;
    }

    private void checkTheServiceExists(String name) throws BackgroundJobServiceNotFound {
        if (!this.processorMap.containsKey(name)) {
            throw new BackgroundJobServiceNotFound(name);
        }
    }

    private void runBackgroundProcess(BackgroundJobProcessor processor, long backgroundJobId) {
        long start = System.currentTimeMillis();
        try (Cleanup ignore = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            this.doInTransaction(tx -> {
                log.debug("Starting background job ({}) with id {}", (Object)processor.getClass().getSimpleName(), (Object)backgroundJobId);
                BackgroundJob backgroundJob = this.backgroundJobDAO.getActiveJobById(backgroundJobId);
                BackgroundJobResponse response = processor.process(backgroundJob.getId(), this.convertToHashMap(backgroundJob.getParameters()), RECOMMENDED_TIMEOUT_IN_SEC * 1000L);
                backgroundJob.setRunAt(response.getNextRunAt());
                backgroundJob.setParameters(this.convertToJson(response.getNewParameters()));
                backgroundJob.setLastTouchTime(this.timeSupplier.get());
                backgroundJob.setDuration(backgroundJob.getDuration() + (System.currentTimeMillis() - start));
                backgroundJob.setIterationNumber(backgroundJob.getIterationNumber() + 1);
                this.backgroundJobDAO.save(backgroundJob);
                if (!BackgroundJobState.ACTIVE.equals((Object)response.getNewState())) {
                    this.finishJob(backgroundJob, response.getNewState(), "");
                }
                log.debug("Background job ({}) with id {} has been finished with state {}", new Object[]{processor.getClass().getSimpleName(), backgroundJobId, response.getNewState()});
                return null;
            });
        }
        catch (Exception e) {
            log.warn("Job " + processor.getClass().getSimpleName() + " with id " + backgroundJobId + " failed: " + e.getMessage(), (Throwable)e);
            this.delayNextExecution(backgroundJobId, System.currentTimeMillis() - start, e.getMessage());
        }
    }

    public String convertToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> convertToHashMap(String customerInfoJSON) {
        try {
            return (Map)objectMapper.readValue(customerInfoJSON, Map.class);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Instant calculateDelay(Instant now, int numberOfFailures) {
        switch (numberOfFailures) {
            case 0: {
                return now.plus(1L, ChronoUnit.HOURS);
            }
            case 1: {
                return now.plus(6L, ChronoUnit.HOURS);
            }
        }
        return now.plus(1L, ChronoUnit.DAYS);
    }

    private void delayNextExecution(long backgroundJobId, long duration, String errorMessage) {
        this.doInTransaction(tx -> {
            BackgroundJob backgroundJob = this.backgroundJobDAO.getActiveJobById(backgroundJobId);
            backgroundJob.setRunAt(this.calculateDelay(this.timeSupplier.get(), backgroundJob.getNumberOfFailures()));
            backgroundJob.setNumberOfFailures(backgroundJob.getNumberOfFailures() + 1);
            backgroundJob.setLastTouchTime(this.timeSupplier.get());
            backgroundJob.setDuration(backgroundJob.getDuration() + duration);
            this.backgroundJobDAO.save(backgroundJob);
            if (backgroundJob.getNumberOfFailures() >= MAX_NUMBER_OF_FAILURES) {
                this.finishJob(backgroundJob, BackgroundJobState.FAILED, "Too many failures (>=" + MAX_NUMBER_OF_FAILURES + "). Last error message: " + errorMessage);
            }
            return null;
        });
    }

    private String getProcessorName(BackgroundJobProcessor backgroundJobProcessor) {
        return this.getProcessorName(backgroundJobProcessor.getClass());
    }

    private String getProcessorName(Class<?> processorClass) {
        return processorClass.getSimpleName();
    }

    private <T> T doInTransaction(TransactionCallback<T> callback) {
        return (T)this.transactionTemplateSupplier.get().execute(callback);
    }
}

