/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public enum HttpStatusFamily {
    INFORMATIONAL,
    SUCCESSFUL,
    REDIRECTION,
    CLIENT_ERROR,
    SERVER_ERROR,
    OTHER;


    public static HttpStatusFamily of(int httpStatusCode) {
        switch (httpStatusCode / 100) {
            case 1: {
                return INFORMATIONAL;
            }
            case 2: {
                return SUCCESSFUL;
            }
            case 3: {
                return REDIRECTION;
            }
            case 4: {
                return CLIENT_ERROR;
            }
            case 5: {
                return SERVER_ERROR;
            }
        }
        return OTHER;
    }

    public boolean isOneOf(HttpStatusFamily ... families) {
        return families != null && Stream.of(families).anyMatch(family -> family == this);
    }
}

