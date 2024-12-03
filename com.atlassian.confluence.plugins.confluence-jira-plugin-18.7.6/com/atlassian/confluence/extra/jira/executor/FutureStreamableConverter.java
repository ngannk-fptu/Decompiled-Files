/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.MoreObjects
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.executor;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.extra.jira.helper.JiraExceptionHelper;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.MoreObjects;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FutureStreamableConverter
implements Streamable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FutureStreamableConverter.class);
    private static final String defaultMsg = "jira.streamable.macro.default.error";
    private Builder builder;

    private FutureStreamableConverter(Builder builder) {
        this.builder = builder;
    }

    public void writeTo(Writer writer) throws IOException {
        try {
            long remainingTimeout = this.builder.context.getTimeout().getTime();
            if (remainingTimeout > 0L) {
                writer.write(this.builder.futureResult.get(remainingTimeout, TimeUnit.MILLISECONDS));
            } else {
                this.logStreamableError(writer, this.getExecutionTimeoutErrorMsg(), new TimeoutException());
            }
        }
        catch (InterruptedException e) {
            this.logStreamableError(writer, this.getInterruptedErrorMsg(), e);
        }
        catch (ExecutionException e) {
            for (Throwable cause = e.getCause(); cause != null; cause = cause.getCause()) {
                if (!(cause instanceof MacroExecutionException)) continue;
                this.logStreamableError(writer, cause.getMessage(), e);
                return;
            }
            this.logStreamableError(writer, this.getExecutionErrorMsg(), e);
        }
        catch (TimeoutException e) {
            this.logStreamableError(writer, this.getConnectionTimeoutErrorMsg(), e);
        }
    }

    private String getConnectionTimeoutErrorMsg() {
        return (String)MoreObjects.firstNonNull((Object)this.builder.connectionTimeoutErrorMsg, (Object)defaultMsg);
    }

    private String getExecutionTimeoutErrorMsg() {
        return (String)MoreObjects.firstNonNull((Object)this.builder.executionTimeoutErrorMsg, (Object)defaultMsg);
    }

    private String getInterruptedErrorMsg() {
        return (String)MoreObjects.firstNonNull((Object)this.builder.interruptedErrorMsg, (Object)defaultMsg);
    }

    private String getExecutionErrorMsg() {
        return (String)MoreObjects.firstNonNull((Object)this.builder.executionErrorMsg, (Object)defaultMsg);
    }

    private void logStreamableError(Writer writer, String exceptionKey, Exception e) throws IOException {
        if (exceptionKey != null) {
            String errorMessage = this.builder.i18nResolver.getText(exceptionKey);
            writer.write(this.builder.jiraExceptionHelper.renderExceptionMessage(errorMessage));
            if (e != null) {
                LOGGER.warn(errorMessage);
                LOGGER.debug(errorMessage, (Throwable)e);
            }
        }
    }

    public static class Builder {
        private final Future<String> futureResult;
        private final ConversionContext context;
        private final I18nResolver i18nResolver;
        private final JiraExceptionHelper jiraExceptionHelper;
        private String executionTimeoutErrorMsg;
        private String connectionTimeoutErrorMsg;
        private String interruptedErrorMsg;
        private String executionErrorMsg;

        public Builder(Future<String> futureResult, ConversionContext context, I18nResolver i18nResolver, JiraExceptionHelper jiraExceptionHelper) {
            this.futureResult = futureResult;
            this.context = context;
            this.i18nResolver = i18nResolver;
            this.jiraExceptionHelper = jiraExceptionHelper;
        }

        public Builder executionTimeoutErrorMsg(String i18nErrorMsg) {
            this.executionTimeoutErrorMsg = i18nErrorMsg;
            return this;
        }

        public Builder connectionTimeoutErrorMsg(String i18nErrorMsg) {
            this.connectionTimeoutErrorMsg = i18nErrorMsg;
            return this;
        }

        public Builder interruptedErrorMsg(String i18nErrorMsg) {
            this.interruptedErrorMsg = i18nErrorMsg;
            return this;
        }

        public Builder executionErrorMsg(String i18nErrorMsg) {
            this.executionErrorMsg = i18nErrorMsg;
            return this;
        }

        public FutureStreamableConverter build() {
            return new FutureStreamableConverter(this);
        }
    }
}

