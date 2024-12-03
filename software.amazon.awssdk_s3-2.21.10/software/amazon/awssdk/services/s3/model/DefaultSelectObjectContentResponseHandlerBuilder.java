/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.eventstream.DefaultEventStreamResponseHandlerBuilder
 *  software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandlerFromBuilder
 */
package software.amazon.awssdk.services.s3.model;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.eventstream.DefaultEventStreamResponseHandlerBuilder;
import software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandlerFromBuilder;
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponse;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;

@SdkInternalApi
final class DefaultSelectObjectContentResponseHandlerBuilder
extends DefaultEventStreamResponseHandlerBuilder<SelectObjectContentResponse, SelectObjectContentEventStream, SelectObjectContentResponseHandler.Builder>
implements SelectObjectContentResponseHandler.Builder {
    DefaultSelectObjectContentResponseHandlerBuilder() {
    }

    @Override
    public SelectObjectContentResponseHandler.Builder subscriber(SelectObjectContentResponseHandler.Visitor visitor) {
        this.subscriber(e -> e.accept(visitor));
        return this;
    }

    @Override
    public SelectObjectContentResponseHandler build() {
        return new Impl(this);
    }

    private static final class Impl
    extends EventStreamResponseHandlerFromBuilder<SelectObjectContentResponse, SelectObjectContentEventStream>
    implements SelectObjectContentResponseHandler {
        private Impl(DefaultSelectObjectContentResponseHandlerBuilder builder) {
            super((DefaultEventStreamResponseHandlerBuilder)builder);
        }
    }
}

