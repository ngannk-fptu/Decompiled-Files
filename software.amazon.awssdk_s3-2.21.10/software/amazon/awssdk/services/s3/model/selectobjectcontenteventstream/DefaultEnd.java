/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.model.selectobjectcontenteventstream;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.model.EndEvent;
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;

@SdkInternalApi
public final class DefaultEnd
extends EndEvent {
    private static final long serialVersionUID = 1L;

    DefaultEnd(BuilderImpl builderImpl) {
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
        visitor.visitEnd(this);
    }

    @Override
    public SelectObjectContentEventStream.EventType sdkEventType() {
        return SelectObjectContentEventStream.EventType.END;
    }

    private static final class BuilderImpl
    extends EndEvent.BuilderImpl
    implements Builder {
        private BuilderImpl() {
        }

        private BuilderImpl(DefaultEnd event) {
            super(event);
        }

        @Override
        public DefaultEnd build() {
            return new DefaultEnd(this);
        }
    }

    public static interface Builder
    extends EndEvent.Builder {
        public DefaultEnd build();
    }
}

