/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.http;

import java.util.Locale;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public enum SdkHttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    PATCH,
    OPTIONS;


    public static SdkHttpMethod fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String upperCaseValue = value.toUpperCase(Locale.ENGLISH);
        return Stream.of(SdkHttpMethod.values()).filter(h -> h.name().equals(upperCaseValue)).findFirst().orElseThrow(() -> new IllegalArgumentException("Unsupported HTTP method name " + value));
    }
}

