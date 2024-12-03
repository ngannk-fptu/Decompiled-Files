/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.model.selectobjectcontenteventstream;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.model.ProgressEvent;
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;

@SdkInternalApi
public final class DefaultProgress
extends ProgressEvent {
    private static final long serialVersionUID = 1L;

    DefaultProgress(BuilderImpl builderImpl) {
        super(builderImpl);
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    @Override
    public void accept(SelectObjectContentResponseHandler.Visitor visitor) {
        visitor.visitProgress(this);
    }

    @Override
    public SelectObjectContentEventStream.EventType sdkEventType() {
        return SelectObjectContentEventStream.EventType.PROGRESS;
    }

    private static final class BuilderImpl
    extends ProgressEvent.BuilderImpl
    implements Builder {
        private BuilderImpl() {
        }

        private BuilderImpl(DefaultProgress event) {
            super(event);
        }

        @Override
        public DefaultProgress build() {
            return new DefaultProgress(this);
        }
    }

    public static interface Builder
    extends ProgressEvent.Builder {
        public DefaultProgress build();
    }
}

