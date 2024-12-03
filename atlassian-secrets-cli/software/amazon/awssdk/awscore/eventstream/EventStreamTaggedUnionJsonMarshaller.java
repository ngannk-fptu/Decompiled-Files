/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.eventstream;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.runtime.transform.Marshaller;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkProtectedApi
public final class EventStreamTaggedUnionJsonMarshaller<BaseEventT>
implements Marshaller<BaseEventT> {
    private final Map<Class<? extends BaseEventT>, Marshaller<BaseEventT>> marshallers;
    private final Marshaller<BaseEventT> defaultMarshaller;

    private EventStreamTaggedUnionJsonMarshaller(Builder<BaseEventT> builder) {
        this.marshallers = new HashMap<Class<? extends BaseEventT>, Marshaller<BaseEventT>>(((Builder)builder).marshallers);
        this.defaultMarshaller = ((Builder)builder).defaultMarshaller;
    }

    @Override
    public SdkHttpFullRequest marshall(BaseEventT eventT) {
        return this.marshallers.getOrDefault(eventT.getClass(), this.defaultMarshaller).marshall(eventT);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder<BaseEventT> {
        private final Map<Class<? extends BaseEventT>, Marshaller<BaseEventT>> marshallers = new HashMap<Class<? extends BaseEventT>, Marshaller<BaseEventT>>();
        private Marshaller<BaseEventT> defaultMarshaller;

        private Builder() {
        }

        public Builder putMarshaller(Class<? extends BaseEventT> eventClass, Marshaller<BaseEventT> marshaller) {
            this.marshallers.put(eventClass, marshaller);
            return this;
        }

        public EventStreamTaggedUnionJsonMarshaller<BaseEventT> build() {
            this.defaultMarshaller = e -> {
                String errorMsg = "Event type should be one of the following types: " + this.marshallers.keySet().stream().map(Class::getSimpleName).collect(Collectors.toList());
                throw new IllegalArgumentException(errorMsg);
            };
            return new EventStreamTaggedUnionJsonMarshaller(this);
        }
    }
}

