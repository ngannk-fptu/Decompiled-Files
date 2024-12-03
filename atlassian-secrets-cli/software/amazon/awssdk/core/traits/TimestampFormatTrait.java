/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.traits;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.traits.Trait;

@SdkProtectedApi
public final class TimestampFormatTrait
implements Trait {
    private final Format format;

    private TimestampFormatTrait(Format timestampFormat) {
        this.format = timestampFormat;
    }

    public Format format() {
        return this.format;
    }

    public static TimestampFormatTrait create(Format timestampFormat) {
        return new TimestampFormatTrait(timestampFormat);
    }

    public static enum Format {
        ISO_8601,
        RFC_822,
        UNIX_TIMESTAMP,
        UNIX_TIMESTAMP_MILLIS;


        public static Format fromString(String strFormat) {
            switch (strFormat) {
                case "iso8601": {
                    return ISO_8601;
                }
                case "rfc822": {
                    return RFC_822;
                }
                case "unixTimestamp": {
                    return UNIX_TIMESTAMP;
                }
            }
            throw new RuntimeException("Unknown timestamp format - " + strFormat);
        }
    }
}

