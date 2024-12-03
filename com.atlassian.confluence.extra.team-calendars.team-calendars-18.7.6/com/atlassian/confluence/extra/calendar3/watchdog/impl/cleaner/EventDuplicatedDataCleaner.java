/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner;

import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogStatusReporter;
import com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.EventDuplicatedDataCleanerSpec;
import com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.callables.CallableBuilder;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDuplicatedDataCleaner<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDuplicatedDataCleaner.class);
    private static final int RETRY_TIMES = 2;
    private final int duplicatedEventPage;
    private final int deleteLimit;
    private final TransactionalHostContextAccessor hostContextAccessor;
    private final EventDuplicatedDataCleanerSpec<T> cleanerSpec;
    private final WatchDogStatusReporter reporter;

    public EventDuplicatedDataCleaner(TransactionalHostContextAccessor hostContextAccessor, int duplicatedEventPage, int deleteLimit, EventDuplicatedDataCleanerSpec<T> cleanerSpec, WatchDogStatusReporter reporter) {
        Objects.nonNull(hostContextAccessor);
        Objects.nonNull(cleanerSpec);
        Preconditions.checkArgument((duplicatedEventPage >= 1 ? 1 : 0) != 0);
        Preconditions.checkArgument((deleteLimit >= 1 ? 1 : 0) != 0);
        this.hostContextAccessor = hostContextAccessor;
        this.duplicatedEventPage = duplicatedEventPage;
        this.deleteLimit = deleteLimit;
        this.cleanerSpec = cleanerSpec;
        this.reporter = reporter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cleanData() {
        RetryWithErrorMaker retryErrorMarker = new RetryWithErrorMaker();
        int duplicateEventLassOffset = 0;
        boolean hasDuplicated = true;
        int batchNumber = 1;
        long totalDeletedRow = 0L;
        while (hasDuplicated) {
            Collection<T> listDuplicatedDTO;
            block5: {
                listDuplicatedDTO = this.cleanerSpec.getDuplicatedDTOs(this.duplicatedEventPage, duplicateEventLassOffset);
                if (listDuplicatedDTO != null) break block5;
                this.report("Could not found anymore duplication");
                hasDuplicated = listDuplicatedDTO != null && !listDuplicatedDTO.isEmpty() && !retryErrorMarker.isRetryWithError();
                continue;
            }
            try {
                for (T duplicatedDTO : listDuplicatedDTO) {
                    long numberOfDuplicationPerEvent = this.cleanerSpec.getDuplicatedCountPerDTO(duplicatedDTO);
                    Collection<Long> results = CallableBuilder.builder().withAction(() -> {
                        int actualDeleteLimit = this.deleteLimit;
                        if (numberOfDuplicationPerEvent < (long)this.deleteLimit) {
                            actualDeleteLimit = -1;
                        }
                        return this.cleanerSpec.deleteDuplicatedRow(actualDeleteLimit, duplicatedDTO);
                    }).withTransaction(this.hostContextAccessor).withRetry(2, retryErrorMarker).withBatching(this.deleteLimit, numberOfDuplicationPerEvent).getBatchCallable().apply(null);
                    totalDeletedRow += results != null ? results.stream().flatMap(item -> {
                        if (item == null) {
                            return Stream.of(Long.valueOf(0L));
                        }
                        return Stream.of(item);
                    }).reduce(Long::sum).orElse(0L) : 0L;
                }
                String status = String.format("Processing batch of %d with %d duplicated DTO with total delete items %d", ++batchNumber, listDuplicatedDTO.size(), totalDeletedRow);
                this.report(status);
                hasDuplicated = listDuplicatedDTO != null && !listDuplicatedDTO.isEmpty() && !retryErrorMarker.isRetryWithError();
            }
            catch (Throwable throwable) {
                hasDuplicated = listDuplicatedDTO != null && !listDuplicatedDTO.isEmpty() && !retryErrorMarker.isRetryWithError();
                throw throwable;
            }
        }
    }

    private void report(String status) {
        if (this.reporter != null) {
            this.reporter.report(status);
        }
        LOGGER.debug(status);
    }

    private class RetryWithErrorMaker
    implements Runnable {
        boolean retryWithError;

        private RetryWithErrorMaker() {
        }

        public boolean isRetryWithError() {
            return this.retryWithError;
        }

        @Override
        public void run() {
            this.retryWithError = true;
        }
    }
}

