/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.protocols.core.PathMarshaller
 *  software.amazon.awssdk.protocols.core.ValueToStringConverter
 *  software.amazon.awssdk.protocols.core.ValueToStringConverter$ValueToString
 */
package software.amazon.awssdk.protocols.xml.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.protocols.core.PathMarshaller;
import software.amazon.awssdk.protocols.core.ValueToStringConverter;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshaller;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshallerContext;

@SdkInternalApi
public final class SimpleTypePathMarshaller {
    public static final XmlMarshaller<String> STRING = new SimplePathMarshaller<String>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_STRING, PathMarshaller.NON_GREEDY);
    public static final XmlMarshaller<Integer> INTEGER = new SimplePathMarshaller<Integer>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_INTEGER, PathMarshaller.NON_GREEDY);
    public static final XmlMarshaller<Long> LONG = new SimplePathMarshaller<Long>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_LONG, PathMarshaller.NON_GREEDY);
    public static final XmlMarshaller<Short> SHORT = new SimplePathMarshaller<Short>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_SHORT, PathMarshaller.NON_GREEDY);
    public static final XmlMarshaller<String> GREEDY_STRING = new SimplePathMarshaller<String>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_STRING, PathMarshaller.GREEDY_WITH_SLASHES);
    public static final XmlMarshaller<Void> NULL = (val, context, paramName, sdkField) -> {
        throw new IllegalArgumentException(String.format("Parameter '%s' must not be null", paramName));
    };

    private SimpleTypePathMarshaller() {
    }

    private static class SimplePathMarshaller<T>
    implements XmlMarshaller<T> {
        private final ValueToStringConverter.ValueToString<T> converter;
        private final PathMarshaller pathMarshaller;

        private SimplePathMarshaller(ValueToStringConverter.ValueToString<T> converter, PathMarshaller pathMarshaller) {
            this.converter = converter;
            this.pathMarshaller = pathMarshaller;
        }

        @Override
        public void marshall(T val, XmlMarshallerContext context, String paramName, SdkField<T> sdkField) {
            context.request().encodedPath(this.pathMarshaller.marshall(context.request().encodedPath(), paramName, this.converter.convert(val, sdkField)));
        }
    }
}

