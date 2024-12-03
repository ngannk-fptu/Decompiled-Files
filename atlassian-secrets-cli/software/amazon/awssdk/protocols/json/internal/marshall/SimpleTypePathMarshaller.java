/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.protocols.core.PathMarshaller;
import software.amazon.awssdk.protocols.core.ValueToStringConverter;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshaller;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshallerContext;

@SdkInternalApi
public final class SimpleTypePathMarshaller {
    public static final JsonMarshaller<String> STRING = new SimplePathMarshaller<String>(ValueToStringConverter.FROM_STRING, PathMarshaller.NON_GREEDY);
    public static final JsonMarshaller<Integer> INTEGER = new SimplePathMarshaller<Integer>(ValueToStringConverter.FROM_INTEGER, PathMarshaller.NON_GREEDY);
    public static final JsonMarshaller<Long> LONG = new SimplePathMarshaller<Long>(ValueToStringConverter.FROM_LONG, PathMarshaller.NON_GREEDY);
    public static final JsonMarshaller<Short> SHORT = new SimplePathMarshaller<Short>(ValueToStringConverter.FROM_SHORT, PathMarshaller.NON_GREEDY);
    public static final JsonMarshaller<String> GREEDY_STRING = new SimplePathMarshaller<String>(ValueToStringConverter.FROM_STRING, PathMarshaller.GREEDY);
    public static final JsonMarshaller<Void> NULL = (val, context, paramName, sdkField) -> {
        throw new IllegalArgumentException(String.format("Parameter '%s' must not be null", paramName));
    };

    private SimpleTypePathMarshaller() {
    }

    private static class SimplePathMarshaller<T>
    implements JsonMarshaller<T> {
        private final ValueToStringConverter.ValueToString<T> converter;
        private final PathMarshaller pathMarshaller;

        private SimplePathMarshaller(ValueToStringConverter.ValueToString<T> converter, PathMarshaller pathMarshaller) {
            this.converter = converter;
            this.pathMarshaller = pathMarshaller;
        }

        @Override
        public void marshall(T val, JsonMarshallerContext context, String paramName, SdkField<T> sdkField) {
            context.request().encodedPath(this.pathMarshaller.marshall(context.request().encodedPath(), paramName, this.converter.convert(val, sdkField)));
        }
    }
}

