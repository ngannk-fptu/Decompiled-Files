/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.model.selectobjectcontenteventstream;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.model.ContinuationEvent;
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;

@SdkInternalApi
public final class DefaultCont
extends ContinuationEvent {
    private static final long serialVersionUID = 1L;

    DefaultCont(BuilderImpl builderImpl) {
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
        visitor.visitCont(this);
    }

    @Override
    public SelectObjectContentEventStream.EventType sdkEventType() {
        return SelectObjectContentEventStream.EventType.CONT;
    }

    private static final class BuilderImpl
    extends ContinuationEvent.BuilderImpl
    implements Builder {
        private BuilderImpl() {
        }

        private BuilderImpl(DefaultCont event) {
            super(event);
        }

        @Override
        public DefaultCont build() {
            return new DefaultCont(this);
        }
    }

    public static interface Builder
    extends ContinuationEvent.Builder {
        public DefaultCont build();
    }
}

