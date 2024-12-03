/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandler
 *  software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandler$Builder
 */
package software.amazon.awssdk.services.s3.model;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandler;
import software.amazon.awssdk.services.s3.model.ContinuationEvent;
import software.amazon.awssdk.services.s3.model.DefaultSelectObjectContentResponseHandlerBuilder;
import software.amazon.awssdk.services.s3.model.DefaultSelectObjectContentVisitorBuilder;
import software.amazon.awssdk.services.s3.model.EndEvent;
import software.amazon.awssdk.services.s3.model.ProgressEvent;
import software.amazon.awssdk.services.s3.model.RecordsEvent;
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponse;
import software.amazon.awssdk.services.s3.model.StatsEvent;

@SdkPublicApi
public interface SelectObjectContentResponseHandler
extends EventStreamResponseHandler<SelectObjectContentResponse, SelectObjectContentEventStream> {
    public static Builder builder() {
        return new DefaultSelectObjectContentResponseHandlerBuilder();
    }

    public static interface Visitor {
        public static Builder builder() {
            return new DefaultSelectObjectContentVisitorBuilder();
        }

        default public void visitDefault(SelectObjectContentEventStream event) {
        }

        default public void visitRecords(RecordsEvent event) {
            this.visitDefault(event);
        }

        default public void visitStats(StatsEvent event) {
            this.visitDefault(event);
        }

        default public void visitProgress(ProgressEvent event) {
            this.visitDefault(event);
        }

        default public void visitCont(ContinuationEvent event) {
            this.visitDefault(event);
        }

        default public void visitEnd(EndEvent event) {
            this.visitDefault(event);
        }

        public static interface Builder {
            public Builder onDefault(Consumer<SelectObjectContentEventStream> var1);

            public Visitor build();

            public Builder onRecords(Consumer<RecordsEvent> var1);

            public Builder onStats(Consumer<StatsEvent> var1);

            public Builder onProgress(Consumer<ProgressEvent> var1);

            public Builder onCont(Consumer<ContinuationEvent> var1);

            public Builder onEnd(Consumer<EndEvent> var1);
        }
    }

    public static interface Builder
    extends EventStreamResponseHandler.Builder<SelectObjectContentResponse, SelectObjectContentEventStream, Builder> {
        public Builder subscriber(Visitor var1);

        public SelectObjectContentResponseHandler build();
    }
}

