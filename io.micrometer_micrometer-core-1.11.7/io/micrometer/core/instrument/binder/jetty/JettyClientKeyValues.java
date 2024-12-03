/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.StringUtils
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.client.api.Result
 *  org.eclipse.jetty.http.HttpStatus
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.common.KeyValue;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.binder.http.Outcome;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpStatus;

public final class JettyClientKeyValues {
    private static final KeyValue URI_NOT_FOUND = KeyValue.of((String)"uri", (String)"NOT_FOUND");
    private static final KeyValue URI_REDIRECTION = KeyValue.of((String)"uri", (String)"REDIRECTION");
    private static final KeyValue URI_ROOT = KeyValue.of((String)"uri", (String)"root");
    private static final KeyValue EXCEPTION_NONE = KeyValue.of((String)"exception", (String)"None");
    private static final KeyValue EXCEPTION_UNKNOWN = KeyValue.of((String)"exception", (String)"UNKNOWN");
    private static final KeyValue METHOD_UNKNOWN = KeyValue.of((String)"method", (String)"UNKNOWN");
    private static final KeyValue HOST_UNKNOWN = KeyValue.of((String)"host", (String)"UNKNOWN");
    private static final KeyValue STATUS_UNKNOWN = KeyValue.of((String)"status", (String)"UNKNOWN");
    private static final Pattern TRAILING_SLASH_PATTERN = Pattern.compile("/$");
    private static final Pattern MULTIPLE_SLASH_PATTERN = Pattern.compile("//+");
    private static final KeyValue OUTCOME_UNKNOWN = KeyValue.of((String)"outcome", (String)"UNKNOWN");

    private JettyClientKeyValues() {
    }

    public static KeyValue method(Request request) {
        return request != null ? KeyValue.of((String)"method", (String)request.getMethod()) : METHOD_UNKNOWN;
    }

    public static KeyValue host(Request request) {
        return request != null ? KeyValue.of((String)"host", (String)request.getHost()) : HOST_UNKNOWN;
    }

    public static KeyValue status(@Nullable Result result) {
        return result != null ? KeyValue.of((String)"status", (String)Integer.toString(result.getResponse().getStatus())) : STATUS_UNKNOWN;
    }

    public static KeyValue uri(Request request, @Nullable Result result, BiFunction<Request, Result, String> successfulUriPattern) {
        if (result != null && result.getResponse() != null) {
            int status = result.getResponse().getStatus();
            if (HttpStatus.isRedirection((int)status)) {
                return URI_REDIRECTION;
            }
            if (status == 404) {
                return URI_NOT_FOUND;
            }
        }
        String matchingPattern = successfulUriPattern.apply(request, result);
        if ((matchingPattern = MULTIPLE_SLASH_PATTERN.matcher(matchingPattern).replaceAll("/")).equals("/")) {
            return URI_ROOT;
        }
        matchingPattern = TRAILING_SLASH_PATTERN.matcher(matchingPattern).replaceAll("");
        return KeyValue.of((String)"uri", (String)matchingPattern);
    }

    public static KeyValue exception(@Nullable Result result) {
        String simpleName;
        int status;
        if (result == null) {
            return EXCEPTION_UNKNOWN;
        }
        Throwable exception = result.getFailure();
        if (exception == null) {
            return EXCEPTION_NONE;
        }
        if (result.getResponse() != null && ((status = result.getResponse().getStatus()) == 404 || HttpStatus.isRedirection((int)status))) {
            return EXCEPTION_NONE;
        }
        if (exception.getCause() != null) {
            exception = exception.getCause();
        }
        return KeyValue.of((String)"exception", (String)(StringUtils.isNotEmpty((String)(simpleName = exception.getClass().getSimpleName())) ? simpleName : exception.getClass().getName()));
    }

    public static KeyValue outcome(@Nullable Result result) {
        if (result == null) {
            return OUTCOME_UNKNOWN;
        }
        return Outcome.forStatus(result.getResponse().getStatus()).asKeyValue();
    }
}

