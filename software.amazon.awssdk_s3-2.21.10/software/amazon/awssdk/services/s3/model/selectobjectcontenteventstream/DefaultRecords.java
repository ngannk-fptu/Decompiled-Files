/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.model.selectobjectcontenteventstream;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.model.RecordsEvent;
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;

@SdkInternalApi
public final class DefaultRecords
extends RecordsEvent {
    private static final long serialVersionUID = 1L;

    DefaultRecords(BuilderImpl builderImpl) {
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
        visitor.visitRecords(this);
    }

    @Override
    public SelectObjectContentEventStream.EventType sdkEventType() {
        return SelectObjectContentEventStream.EventType.RECORDS;
    }

    private static final class BuilderImpl
    extends RecordsEvent.BuilderImpl
    implements Builder {
        private BuilderImpl() {
        }

        private BuilderImpl(DefaultRecords event) {
            super(event);
        }

        @Override
        public DefaultRecords build() {
            return new DefaultRecords(this);
        }
    }

    public static interface Builder
    extends RecordsEvent.Builder {
        public DefaultRecords build();
    }
}

