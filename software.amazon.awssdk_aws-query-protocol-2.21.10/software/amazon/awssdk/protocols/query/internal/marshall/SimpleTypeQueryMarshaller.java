/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait$Format
 *  software.amazon.awssdk.protocols.core.InstantToString
 *  software.amazon.awssdk.protocols.core.ValueToStringConverter
 *  software.amazon.awssdk.protocols.core.ValueToStringConverter$ValueToString
 */
package software.amazon.awssdk.protocols.query.internal.marshall;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.traits.TimestampFormatTrait;
import software.amazon.awssdk.protocols.core.InstantToString;
import software.amazon.awssdk.protocols.core.ValueToStringConverter;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshaller;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshallerContext;

@SdkInternalApi
public final class SimpleTypeQueryMarshaller<T>
implements QueryMarshaller<T> {
    public static final QueryMarshaller<String> STRING = new SimpleTypeQueryMarshaller<String>((ValueToStringConverter.ValueToString<String>)ValueToStringConverter.FROM_STRING);
    public static final QueryMarshaller<Integer> INTEGER = new SimpleTypeQueryMarshaller<Integer>((ValueToStringConverter.ValueToString<Integer>)ValueToStringConverter.FROM_INTEGER);
    public static final QueryMarshaller<Float> FLOAT = new SimpleTypeQueryMarshaller<Float>((ValueToStringConverter.ValueToString<Float>)ValueToStringConverter.FROM_FLOAT);
    public static final QueryMarshaller<Boolean> BOOLEAN = new SimpleTypeQueryMarshaller<Boolean>((ValueToStringConverter.ValueToString<Boolean>)ValueToStringConverter.FROM_BOOLEAN);
    public static final QueryMarshaller<Double> DOUBLE = new SimpleTypeQueryMarshaller<Double>((ValueToStringConverter.ValueToString<Double>)ValueToStringConverter.FROM_DOUBLE);
    public static final QueryMarshaller<Long> LONG = new SimpleTypeQueryMarshaller<Long>((ValueToStringConverter.ValueToString<Long>)ValueToStringConverter.FROM_LONG);
    public static final QueryMarshaller<Short> SHORT = new SimpleTypeQueryMarshaller<Short>((ValueToStringConverter.ValueToString<Short>)ValueToStringConverter.FROM_SHORT);
    public static final QueryMarshaller<Instant> INSTANT = new SimpleTypeQueryMarshaller<Instant>((ValueToStringConverter.ValueToString<Instant>)InstantToString.create(SimpleTypeQueryMarshaller.defaultTimestampFormats()));
    public static final QueryMarshaller<SdkBytes> SDK_BYTES = new SimpleTypeQueryMarshaller<SdkBytes>((ValueToStringConverter.ValueToString<SdkBytes>)ValueToStringConverter.FROM_SDK_BYTES);
    public static final QueryMarshaller<Void> NULL = (request, path, val, sdkField) -> {};
    private final ValueToStringConverter.ValueToString<T> valueToString;

    private SimpleTypeQueryMarshaller(ValueToStringConverter.ValueToString<T> valueToString) {
        this.valueToString = valueToString;
    }

    @Override
    public void marshall(QueryMarshallerContext context, String path, T val, SdkField<T> sdkField) {
        context.request().putRawQueryParameter(path, this.valueToString.convert(val, sdkField));
    }

    public static Map<MarshallLocation, TimestampFormatTrait.Format> defaultTimestampFormats() {
        EnumMap<MarshallLocation, TimestampFormatTrait.Format> formats = new EnumMap<MarshallLocation, TimestampFormatTrait.Format>(MarshallLocation.class);
        formats.put(MarshallLocation.PAYLOAD, TimestampFormatTrait.Format.ISO_8601);
        return Collections.unmodifiableMap(formats);
    }
}

