/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.history.InvocationOutcome
 *  net.java.ao.Accessor
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.Transient
 *  net.java.ao.schema.Index
 *  net.java.ao.schema.Indexes
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.webhooks.internal.dao.ao;

import com.atlassian.webhooks.history.InvocationOutcome;
import net.java.ao.Accessor;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.Transient;
import net.java.ao.schema.Index;
import net.java.ao.schema.Indexes;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Preload(value={"FINISH", "WEBHOOK_ID"})
@Indexes(value={@Index(name="IDX_HIST_INVOKE_WER", methodNames={"WEBHOOK_ID", "EVENT_ID", "OUTCOME"}), @Index(name="IDX_HIST_INVOKE_WR", methodNames={"WEBHOOK_ID", "OUTCOME"}), @Index(name="IDX_HIST_INVOKE_FIN", methodNames={"FINISH"})})
@Table(value="HIST_INVOCATION")
public interface AoHistoricalInvocation
extends RawEntity<String> {
    public static final String COLUMN_ERROR_CONTENT = "ERROR_CONTENT";
    public static final String COLUMN_EVENT_ID = "EVENT_ID";
    public static final String COLUMN_FINISH = "FINISH";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_OUTCOME = "OUTCOME";
    public static final String COLUMN_REQUEST_BODY = "REQUEST_BODY";
    public static final String COLUMN_REQUEST_HEADERS = "REQUEST_HEADERS";
    public static final String COLUMN_REQUEST_ID = "REQUEST_ID";
    public static final String COLUMN_REQUEST_METHOD = "REQUEST_METHOD";
    public static final String COLUMN_REQUEST_URL = "REQUEST_URL";
    public static final String COLUMN_RESULT_DESCRIPTION = "RESULT_DESCRIPTION";
    public static final String COLUMN_RESPONSE_BODY = "RESPONSE_BODY";
    public static final String COLUMN_RESPONSE_HEADERS = "RESPONSE_HEADERS";
    public static final String COLUMN_START = "START";
    public static final String COLUMN_STATUS_CODE = "STATUS_CODE";
    public static final String COLUMN_WEBHOOK_ID = "WEBHOOK_ID";
    public static final String INDEX_WEBHOOK_EVENT_RESULT = "IDX_HIST_INVOKE_WER";
    public static final String INDEX_WEBHOOK_RESULT = "IDX_HIST_INVOKE_WR";
    public static final String INDEX_FINISH = "IDX_HIST_INVOKE_FIN";
    public static final String TABLE_NAME = "HIST_INVOCATION";

    @Accessor(value="EVENT_ID")
    @StringLength(value=64)
    @NotNull
    public String getEventId();

    @PrimaryKey(value="ID")
    @StringLength(value=77)
    @NotNull
    public String getId();

    @Transient
    @Accessor(value="ERROR_CONTENT")
    @StringLength(value=-1)
    public String getErrorContent();

    @Accessor(value="FINISH")
    @NotNull
    public long getFinish();

    @Accessor(value="OUTCOME")
    @NotNull
    public InvocationOutcome getOutcome();

    @Accessor(value="REQUEST_BODY")
    @StringLength(value=-1)
    public String getRequestBody();

    @Accessor(value="REQUEST_HEADERS")
    @StringLength(value=-1)
    public String getRequestHeaders();

    @Accessor(value="REQUEST_ID")
    @StringLength(value=64)
    @NotNull
    public String getRequestId();

    @Accessor(value="REQUEST_METHOD")
    @StringLength(value=16)
    @NotNull
    public String getRequestMethod();

    @Accessor(value="REQUEST_URL")
    @NotNull
    public String getRequestUrl();

    @Transient
    @Accessor(value="RESPONSE_BODY")
    @StringLength(value=-1)
    public String getResponseBody();

    @Accessor(value="RESPONSE_HEADERS")
    @StringLength(value=-1)
    public String getResponseHeaders();

    @Accessor(value="RESULT_DESCRIPTION")
    @NotNull
    public String getResultDescription();

    @Accessor(value="START")
    @NotNull
    public long getStart();

    @Accessor(value="STATUS_CODE")
    public Integer getStatusCode();

    @Accessor(value="WEBHOOK_ID")
    @NotNull
    public int getWebhookId();
}

