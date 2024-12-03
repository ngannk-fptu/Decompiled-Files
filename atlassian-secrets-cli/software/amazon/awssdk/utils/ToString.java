/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.util.Arrays;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.BinaryUtils;

@NotThreadSafe
@SdkProtectedApi
public final class ToString {
    private final StringBuilder result;
    private final int startingLength;

    private ToString(String className) {
        this.result = new StringBuilder(className).append("(");
        this.startingLength = this.result.length();
    }

    public static String create(String className) {
        return className + "()";
    }

    public static ToString builder(String className) {
        return new ToString(className);
    }

    public ToString add(String fieldName, Object field) {
        if (field != null) {
            String value = field.getClass().isArray() ? (field instanceof byte[] ? "0x" + BinaryUtils.toHex((byte[])field) : Arrays.toString((Object[])field)) : String.valueOf(field);
            this.result.append(fieldName).append("=").append(value).append(", ");
        }
        return this;
    }

    public String build() {
        if (this.result.length() > this.startingLength) {
            this.result.setLength(this.result.length() - 2);
        }
        return this.result.append(")").toString();
    }
}

