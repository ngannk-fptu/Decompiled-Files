/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.model;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.model.ContinuationEvent;
import software.amazon.awssdk.services.s3.model.EndEvent;
import software.amazon.awssdk.services.s3.model.ProgressEvent;
import software.amazon.awssdk.services.s3.model.RecordsEvent;
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;
import software.amazon.awssdk.services.s3.model.StatsEvent;

@SdkInternalApi
final class DefaultSelectObjectContentVisitorBuilder
implements SelectObjectContentResponseHandler.Visitor.Builder {
    private Consumer<SelectObjectContentEventStream> onDefault;
    private Consumer<RecordsEvent> onRecords;
    private Consumer<StatsEvent> onStats;
    private Consumer<ProgressEvent> onProgress;
    private Consumer<ContinuationEvent> onCont;
    private Consumer<EndEvent> onEnd;

    DefaultSelectObjectContentVisitorBuilder() {
    }

    @Override
    public SelectObjectContentResponseHandler.Visitor.Builder onDefault(Consumer<SelectObjectContentEventStream> c) {
        this.onDefault = c;
        return this;
    }

    @Override
    public SelectObjectContentResponseHandler.Visitor build() {
        return new VisitorFromBuilder(this);
    }

    @Override
    public SelectObjectContentResponseHandler.Visitor.Builder onRecords(Consumer<RecordsEvent> c) {
        this.onRecords = c;
        return this;
    }

    @Override
    public SelectObjectContentResponseHandler.Visitor.Builder onStats(Consumer<StatsEvent> c) {
        this.onStats = c;
        return this;
    }

    @Override
    public SelectObjectContentResponseHandler.Visitor.Builder onProgress(Consumer<ProgressEvent> c) {
        this.onProgress = c;
        return this;
    }

    @Override
    public SelectObjectContentResponseHandler.Visitor.Builder onCont(Consumer<ContinuationEvent> c) {
        this.onCont = c;
        return this;
    }

    @Override
    public SelectObjectContentResponseHandler.Visitor.Builder onEnd(Consumer<EndEvent> c) {
        this.onEnd = c;
        return this;
    }

    static class VisitorFromBuilder
    implements SelectObjectContentResponseHandler.Visitor {
        private final Consumer<SelectObjectContentEventStream> onDefault;
        private final Consumer<RecordsEvent> onRecords;
        private final Consumer<StatsEvent> onStats;
        private final Consumer<ProgressEvent> onProgress;
        private final Consumer<ContinuationEvent> onCont;
        private final Consumer<EndEvent> onEnd;

        VisitorFromBuilder(DefaultSelectObjectContentVisitorBuilder builder) {
            this.onDefault = builder.onDefault != null ? builder.onDefault : x$0 -> SelectObjectContentResponseHandler.Visitor.super.visitDefault((SelectObjectContentEventStream)x$0);
            this.onRecords = builder.onRecords != null ? builder.onRecords : x$0 -> SelectObjectContentResponseHandler.Visitor.super.visitRecords((RecordsEvent)x$0);
            this.onStats = builder.onStats != null ? builder.onStats : x$0 -> SelectObjectContentResponseHandler.Visitor.super.visitStats((StatsEvent)x$0);
            this.onProgress = builder.onProgress != null ? builder.onProgress : x$0 -> SelectObjectContentResponseHandler.Visitor.super.visitProgress((ProgressEvent)x$0);
            this.onCont = builder.onCont != null ? builder.onCont : x$0 -> SelectObjectContentResponseHandler.Visitor.super.visitCont((ContinuationEvent)x$0);
            this.onEnd = builder.onEnd != null ? builder.onEnd : x$0 -> SelectObjectContentResponseHandler.Visitor.super.visitEnd((EndEvent)x$0);
        }

        @Override
        public void visitDefault(SelectObjectContentEventStream event) {
            this.onDefault.accept(event);
        }

        @Override
        public void visitRecords(RecordsEvent event) {
            this.onRecords.accept(event);
        }

        @Override
        public void visitStats(StatsEvent event) {
            this.onStats.accept(event);
        }

        @Override
        public void visitProgress(ProgressEvent event) {
            this.onProgress.accept(event);
        }

        @Override
        public void visitCont(ContinuationEvent event) {
            this.onCont.accept(event);
        }

        @Override
        public void visitEnd(EndEvent event) {
            this.onEnd.accept(event);
        }
    }
}

