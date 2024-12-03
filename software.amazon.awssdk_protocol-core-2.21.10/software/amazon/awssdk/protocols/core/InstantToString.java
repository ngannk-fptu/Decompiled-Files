/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait$Format
 *  software.amazon.awssdk.utils.DateUtils
 */
package software.amazon.awssdk.protocols.core;

import java.time.Instant;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.traits.TimestampFormatTrait;
import software.amazon.awssdk.protocols.core.ValueToStringConverter;
import software.amazon.awssdk.utils.DateUtils;

@SdkProtectedApi
public final class InstantToString
implements ValueToStringConverter.ValueToString<Instant> {
    private final Map<MarshallLocation, TimestampFormatTrait.Format> defaultFormats;

    private InstantToString(Map<MarshallLocation, TimestampFormatTrait.Format> defaultFormats) {
        this.defaultFormats = defaultFormats;
    }

    @Override
    public String convert(Instant val, SdkField<Instant> sdkField) {
        if (val == null) {
            return null;
        }
        TimestampFormatTrait.Format format = sdkField.getOptionalTrait(TimestampFormatTrait.class).map(TimestampFormatTrait::format).orElseGet(() -> this.getDefaultTimestampFormat(sdkField.location(), this.defaultFormats));
        switch (format) {
            case ISO_8601: {
                return DateUtils.formatIso8601Date((Instant)val);
            }
            case RFC_822: {
                return DateUtils.formatRfc822Date((Instant)val);
            }
            case UNIX_TIMESTAMP: {
                return DateUtils.formatUnixTimestampInstant((Instant)val);
            }
        }
        throw SdkClientException.create((String)("Unsupported timestamp format - " + format));
    }

    private TimestampFormatTrait.Format getDefaultTimestampFormat(MarshallLocation location, Map<MarshallLocation, TimestampFormatTrait.Format> defaultFormats) {
        TimestampFormatTrait.Format format = defaultFormats.get(location);
        if (format == null) {
            throw SdkClientException.create((String)("No default timestamp marshaller found for location - " + location));
        }
        return format;
    }

    public static InstantToString create(Map<MarshallLocation, TimestampFormatTrait.Format> defaultFormats) {
        return new InstantToString(defaultFormats);
    }
}

