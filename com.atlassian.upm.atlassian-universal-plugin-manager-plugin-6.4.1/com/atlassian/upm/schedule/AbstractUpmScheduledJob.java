/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableInstant
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.upm.schedule;

import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.upm.schedule.UpmScheduledJob;
import com.atlassian.upm.schedule.UpmScheduler;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractUpmScheduledJob
implements UpmScheduledJob,
InitializingBean {
    private static final Random RANDOM = new Random(new Date().getTime());
    private static final Logger log = LoggerFactory.getLogger(AbstractUpmScheduledJob.class);
    protected final UpmScheduler scheduler;

    protected AbstractUpmScheduledJob(UpmScheduler scheduler) {
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    }

    public void afterPropertiesSet() throws Exception {
        this.scheduler.registerJob(this);
    }

    @Override
    public DateTime getStartTime() {
        return new DateTime();
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        try {
            this.execute(UpmScheduler.RunMode.SCHEDULED);
            return JobRunnerResponse.success();
        }
        catch (Exception e) {
            return JobRunnerResponse.failed((Throwable)e);
        }
    }

    @Override
    public void execute(UpmScheduler.RunMode runMode) {
        if (!this.getInterval().isDefined()) {
            this.scheduler.unregisterJob(this);
        }
        try {
            this.executeInternal(runMode);
            log.debug("Executed job '{}'", (Object)this.getClass().getSimpleName());
        }
        catch (Exception e) {
            log.error("Error executing job '" + this.getClass().getSimpleName() + "'", (Throwable)e);
        }
    }

    protected abstract void executeInternal(UpmScheduler.RunMode var1) throws Exception;

    protected static DateTime getStartTimeAtRandomTimeOfAnyHour() {
        return AbstractUpmScheduledJob.getStartTimeAtRandomTimeOfSpecificHour(RANDOM.nextInt(24));
    }

    public static int chooseOutside(Function<Integer, Integer> f, int before, int after, int max) {
        AbstractUpmScheduledJob.checkPositionIndex(before, after);
        AbstractUpmScheduledJob.checkPositionIndex(after, max);
        int inside = after - before;
        int upperBound = max - inside;
        int choice = f.apply(upperBound) % upperBound;
        return (after + choice) % max;
    }

    private static int checkPositionIndex(int index, int size) {
        if (index >= 0 && index <= size) {
            return index;
        }
        throw new IndexOutOfBoundsException(AbstractUpmScheduledJob.badPositionIndex(index, size));
    }

    private static String badPositionIndex(int index, int size) {
        if (index < 0) {
            return String.format("%s must not be negative", index);
        }
        if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        }
        return String.format("%s must not be greater than size (%s)", index, size);
    }

    protected static DateTime getStartTimeAtRandomTimeOutsideHours(int before, int after) {
        Function<Integer, Integer> nextInt = RANDOM::nextInt;
        int hour = AbstractUpmScheduledJob.chooseOutside(nextInt, before, after, 24);
        return AbstractUpmScheduledJob.getStartTimeAtRandomTimeOfSpecificHour(hour);
    }

    protected static DateTime getStartTimeAtRandomTimeOfSpecificHour(int hour) {
        DateTime now = new DateTime();
        DateTime dt = now.withTime(hour, RANDOM.nextInt(60), RANDOM.nextInt(60), RANDOM.nextInt(1000));
        return dt.isAfter((ReadableInstant)now) ? dt : dt.plusDays(1);
    }
}

