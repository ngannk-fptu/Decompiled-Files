/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.core;

import java.time.Instant;
import java.util.Map;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.traits.TimestampFormatTrait;
import software.amazon.awssdk.protocols.core.StringToValueConverter;
import software.amazon.awssdk.utils.DateUtils;

@SdkProtectedApi
public final class StringToInstant
implements StringToValueConverter.StringToValue<Instant> {
    private final Map<MarshallLocation, TimestampFormatTrait.Format> defaultFormats;

    private StringToInstant(Map<MarshallLocation, TimestampFormatTrait.Format> defaultFormats) {
        this.defaultFormats = defaultFormats;
    }

    @Override
    public Instant convert(String value, SdkField<Instant> field) {
        if (value == null) {
            return null;
        }
        TimestampFormatTrait.Format format = this.resolveTimestampFormat(field);
        switch (format) {
            case ISO_8601: {
                return DateUtils.parseIso8601Date(value);
            }
            case UNIX_TIMESTAMP: {
                return this.safeParseDate(DateUtils::parseUnixTimestampInstant).apply(value);
            }
            case UNIX_TIMESTAMP_MILLIS: {
                return this.safeParseDate(DateUtils::parseUnixTimestampMillisInstant).apply(value);
            }
            case RFC_822: {
                return DateUtils.parseRfc822Date(value);
            }
        }
        throw SdkClientException.create("Unrecognized timestamp format - " + (Object)((Object)format));
    }

    private Function<String, Instant> safeParseDate(Function<String, Instant> dateUnmarshaller) {
        return value -> {
            try {
                return (Instant)dateUnmarshaller.apply((String)value);
            }
            catch (NumberFormatException e) {
                throw SdkClientException.builder().message("Unable to parse date : " + value).cause(e).build();
            }
        };
    }

    private TimestampFormatTrait.Format resolveTimestampFormat(SdkField<Instant> field) {
        TimestampFormatTrait trait = field.getTrait(TimestampFormatTrait.class);
        if (trait == null) {
            TimestampFormatTrait.Format format = this.defaultFormats.get((Object)field.location());
            if (format == null) {
                throw SdkClientException.create(String.format("Timestamps are not supported for this location (%s)", new Object[]{field.location()}));
            }
            return format;
        }
        return trait.format();
    }

    public static StringToInstant create(Map<MarshallLocation, TimestampFormatTrait.Format> defaultFormats) {
        return new StringToInstant(defaultFormats);
    }
}

