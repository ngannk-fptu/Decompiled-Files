/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.async;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.async.AsyncTaskErrorInfo;
import com.atlassian.upm.core.async.AsyncTaskStage;
import com.atlassian.upm.core.async.TaskSubitemFailure;
import com.atlassian.upm.core.async.TaskSubitemSuccess;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class AsyncTaskStatus {
    @JsonProperty
    private final boolean done;
    @JsonProperty
    private final AsyncTaskErrorInfo error;
    @JsonProperty
    private final String description;
    @JsonProperty
    private final AsyncTaskStage stage;
    @JsonProperty
    private final String resourceName;
    @JsonProperty
    private final String resourceVersion;
    @JsonProperty
    private final Integer itemsDone;
    @JsonProperty
    private final Integer itemsTotal;
    @JsonProperty
    private final Float progress;
    @JsonProperty
    private final Collection<TaskSubitemSuccess> successes;
    @JsonProperty
    private final Collection<TaskSubitemFailure> failures;
    @JsonProperty
    private final URI nextStepPostUri;
    @JsonProperty
    private final URI resultUri;
    @JsonProperty
    private final int pollDelay;

    @JsonCreator
    private AsyncTaskStatus(@JsonProperty(value="done") boolean done, @JsonProperty(value="error") AsyncTaskErrorInfo error, @JsonProperty(value="description") String description, @JsonProperty(value="stage") AsyncTaskStage stage, @JsonProperty(value="resourceName") String resourceName, @JsonProperty(value="resourceVersion") String resourceVersion, @JsonProperty(value="itemsDone") Integer itemsDone, @JsonProperty(value="itemsTotal") Integer itemsTotal, @JsonProperty(value="progress") Float progress, @JsonProperty(value="successes") Collection<TaskSubitemSuccess> successes, @JsonProperty(value="failures") Collection<TaskSubitemFailure> failures, @JsonProperty(value="nextStepPostUri") URI nextStepPostUri, @JsonProperty(value="resultUri") URI resultUri, @JsonProperty(value="pollDelay") int pollDelay) {
        this.done = done;
        this.error = error;
        this.description = description;
        this.stage = stage;
        this.resourceName = resourceName;
        this.resourceVersion = resourceVersion;
        this.itemsDone = itemsDone;
        this.itemsTotal = itemsTotal;
        this.progress = progress;
        this.successes = successes == null ? null : ImmutableList.copyOf(successes);
        this.failures = failures == null ? null : ImmutableList.copyOf(failures);
        this.nextStepPostUri = nextStepPostUri;
        this.resultUri = resultUri;
        this.pollDelay = pollDelay;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AsyncTaskStatus fromStatus) {
        return new Builder(fromStatus);
    }

    public static AsyncTaskStatus empty() {
        return AsyncTaskStatus.builder().build();
    }

    public final boolean isDone() {
        return this.done;
    }

    @JsonIgnore
    public final Option<AsyncTaskErrorInfo> getError() {
        return Option.option(this.error);
    }

    @JsonIgnore
    public final Option<String> getDescription() {
        return Option.option(this.description);
    }

    @JsonIgnore
    public final Option<AsyncTaskStage> getStage() {
        return Option.option(this.stage);
    }

    @JsonIgnore
    public final Option<String> getResourceName() {
        return Option.option(this.resourceName);
    }

    @JsonIgnore
    public final Option<String> getResourceVersion() {
        return Option.option(this.resourceVersion);
    }

    @JsonIgnore
    public final Option<Integer> getItemsDone() {
        return Option.option(this.itemsDone);
    }

    @JsonIgnore
    public final Option<Integer> getItemsTotal() {
        return Option.option(this.itemsTotal);
    }

    @JsonIgnore
    public final Option<Float> getProgress() {
        return Option.option(this.progress);
    }

    @JsonIgnore
    public final Collection<TaskSubitemSuccess> getSuccesses() {
        return this.successes == null ? ImmutableList.of() : this.successes;
    }

    @JsonIgnore
    public final Collection<TaskSubitemFailure> getFailures() {
        return this.failures == null ? ImmutableList.of() : this.failures;
    }

    @JsonIgnore
    public final Option<URI> getNextStepPostUri() {
        return Option.option(this.nextStepPostUri);
    }

    @JsonIgnore
    public final Option<URI> getResultUri() {
        return Option.option(this.resultUri);
    }

    public final int getPollDelay() {
        return this.pollDelay;
    }

    public String toString() {
        return "AsyncTaskStatus(" + this.done + ", " + this.error + ", " + this.description + ", " + (Object)((Object)this.stage) + ", " + this.resourceName + ", " + this.resourceVersion + ", " + this.itemsDone + ", " + this.itemsTotal + ", " + this.progress + ", " + this.successes + ", " + this.failures + ", " + this.nextStepPostUri + ", " + this.resultUri + ", " + this.pollDelay + ")";
    }

    public static class Builder {
        private boolean done;
        private Option<AsyncTaskErrorInfo> error = Option.none();
        private Option<String> description = Option.none();
        private Option<AsyncTaskStage> stage = Option.none();
        private Option<String> resourceName = Option.none();
        private Option<String> resourceVersion = Option.none();
        private Option<Integer> itemsDone = Option.none();
        private Option<Integer> itemsTotal = Option.none();
        private Option<Float> progress = Option.none();
        private Option<Collection<TaskSubitemSuccess>> successes = Option.none();
        private Option<Collection<TaskSubitemFailure>> failures = Option.none();
        private Option<URI> nextStepPostUri = Option.none();
        private Option<URI> resultUri = Option.none();
        private int pollDelay = 100;

        public Builder() {
        }

        public Builder(AsyncTaskStatus fromStatus) {
            this.done = fromStatus.done;
            this.error = Option.option(fromStatus.error);
            this.description = Option.option(fromStatus.description);
            this.stage = Option.option(fromStatus.stage);
            this.resourceName = Option.option(fromStatus.resourceName);
            this.resourceVersion = Option.option(fromStatus.resourceVersion);
            this.itemsDone = Option.option(fromStatus.itemsDone);
            this.itemsTotal = Option.option(fromStatus.itemsTotal);
            this.progress = Option.option(fromStatus.progress);
            this.successes = Option.option(fromStatus.successes);
            this.failures = Option.option(fromStatus.failures);
            this.nextStepPostUri = Option.option(fromStatus.nextStepPostUri);
            this.resultUri = Option.option(fromStatus.resultUri);
            this.pollDelay = fromStatus.pollDelay;
        }

        public AsyncTaskStatus build() {
            return new AsyncTaskStatus(this.done, this.error.getOrElse((AsyncTaskErrorInfo)null), this.description.getOrElse((String)null), this.stage.getOrElse((AsyncTaskStage)null), this.resourceName.getOrElse((String)null), this.resourceVersion.getOrElse((String)null), this.itemsDone.getOrElse((Integer)null), this.itemsTotal.getOrElse((Integer)null), this.progress.getOrElse((Float)null), this.successes.getOrElse((Collection)null), this.failures.getOrElse((Collection)null), this.nextStepPostUri.getOrElse((URI)null), this.resultUri.getOrElse((URI)null), this.pollDelay);
        }

        public Builder done(boolean done) {
            this.done = done;
            return this;
        }

        public Builder error(Option<AsyncTaskErrorInfo> error) {
            this.error = error;
            return this;
        }

        public Builder errorByCode(String code) {
            this.error = Option.some(new AsyncTaskErrorInfo(code, null));
            return this;
        }

        public Builder errorByMessage(String message) {
            this.error = Option.some(new AsyncTaskErrorInfo(null, message));
            return this;
        }

        public Builder description(Option<String> description) {
            this.description = description;
            return this;
        }

        public Builder stage(Option<AsyncTaskStage> stage) {
            this.stage = stage;
            return this;
        }

        public Builder resourceName(Option<String> resourceName) {
            this.resourceName = resourceName;
            return this;
        }

        public Builder resourceVersion(Option<String> resourceVersion) {
            this.resourceVersion = resourceVersion;
            return this;
        }

        public Builder itemsDone(Option<Integer> itemsDone) {
            this.itemsDone = itemsDone;
            return this;
        }

        public Builder itemsTotal(Option<Integer> itemsTotal) {
            this.itemsTotal = itemsTotal;
            return this;
        }

        public Builder progress(Option<Float> progress) {
            this.progress = progress;
            return this;
        }

        public Builder completedProgress() {
            return this.progress(Option.some(Float.valueOf(1.0f)));
        }

        public Builder successes(Option<? extends Collection<TaskSubitemSuccess>> successes) {
            this.successes = successes;
            return this;
        }

        public Builder failures(Option<? extends Collection<TaskSubitemFailure>> failures) {
            this.failures = failures;
            return this;
        }

        public Builder progressForDownload(Option<PluginDownloadService.Progress> progress, float startingProgress, float endingProgress) {
            Iterator<PluginDownloadService.Progress> iterator = progress.iterator();
            if (iterator.hasNext()) {
                PluginDownloadService.Progress p = iterator.next();
                if (p.getTotalSize() == null || p.getTotalSize() == 0L) {
                    return this.progress(Option.none(Float.class));
                }
                float value = (float)p.getAmountDownloaded() * (endingProgress - startingProgress) / (float)p.getTotalSize().longValue() + startingProgress;
                return this.progress(Option.some(Float.valueOf(value)));
            }
            return this.progress(Option.none(Float.class));
        }

        public Builder nextStepPostUri(Option<URI> nextStepPostUri) {
            this.nextStepPostUri = nextStepPostUri;
            return this;
        }

        public Builder resultUri(Option<URI> resultUri) {
            this.resultUri = resultUri;
            return this;
        }

        public Builder pollDelay(int pollDelay) {
            this.pollDelay = pollDelay;
            return this;
        }
    }
}

