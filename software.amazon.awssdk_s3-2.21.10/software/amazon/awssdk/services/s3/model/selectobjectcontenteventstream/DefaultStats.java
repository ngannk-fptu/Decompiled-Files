/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.model.selectobjectcontenteventstream;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;
import software.amazon.awssdk.services.s3.model.StatsEvent;

@SdkInternalApi
public final class DefaultStats
extends StatsEvent {
    private static final long serialVersionUID = 1L;

    DefaultStats(BuilderImpl builderImpl) {
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
        visitor.visitStats(this);
    }

    @Override
    public SelectObjectContentEventStream.EventType sdkEventType() {
        return SelectObjectContentEventStream.EventType.STATS;
    }

    private static final class BuilderImpl
    extends StatsEvent.BuilderImpl
    implements Builder {
        private BuilderImpl() {
        }

        private BuilderImpl(DefaultStats event) {
            super(event);
        }

        @Override
        public DefaultStats build() {
            return new DefaultStats(this);
        }
    }

    public static interface Builder
    extends StatsEvent.Builder {
        public DefaultStats build();
    }
}

