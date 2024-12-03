/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Mutator
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.Index
 *  net.java.ao.schema.Indexes
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.webhooks.internal.dao.ao;

import net.java.ao.Accessor;
import net.java.ao.Mutator;
import net.java.ao.RawEntity;
import net.java.ao.schema.Index;
import net.java.ao.schema.Indexes;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Indexes(value={@Index(name="IDX_WEBHOOK_COUNTS_WH_EV", methodNames={"WEBHOOK_ID", "EVENT_ID"})})
@Table(value="DAILY_COUNTS")
public interface AoDailyInvocationCounts
extends RawEntity<String> {
    public static final String COLUMN_DAYS_SINCE_EPOCH = "DAY_SINCE_EPOCH";
    public static final String COLUMN_ERRORS = "ERRORS";
    public static final String COLUMN_EVENT_ID = "EVENT_ID";
    public static final String COLUMN_FAILURES = "FAILURES";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_SUCCESSES = "SUCCESSES";
    public static final String COLUMN_WEBHOOK_ID = "WEBHOOK_ID";
    public static final String TABLE_NAME = "DAILY_COUNTS";

    @Accessor(value="DAY_SINCE_EPOCH")
    @NotNull
    public long getDaysSinceEpoch();

    @Accessor(value="ERRORS")
    @NotNull
    public int getErrors();

    @Accessor(value="EVENT_ID")
    @NotNull
    @StringLength(value=64)
    public String getEventId();

    @PrimaryKey(value="ID")
    @StringLength(value=88)
    @NotNull
    public String getId();

    @Accessor(value="FAILURES")
    @NotNull
    public int getFailures();

    @Accessor(value="SUCCESSES")
    @NotNull
    public int getSuccesses();

    @Accessor(value="WEBHOOK_ID")
    @NotNull
    public int getWebhookId();

    @Mutator(value="ERRORS")
    public void setErrors(int var1);

    @Mutator(value="FAILURES")
    public void setFailures(int var1);

    @Mutator(value="SUCCESSES")
    public void setSuccesses(int var1);
}

