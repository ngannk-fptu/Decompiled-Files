/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.util.StringUtils
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.client.api.Response
 *  org.eclipse.jetty.client.api.Result
 *  org.eclipse.jetty.http.HttpStatus
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.http.Outcome;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpStatus;

public final class JettyClientTags {
    private static final Tag URI_NOT_FOUND = Tag.of("uri", "NOT_FOUND");
    private static final Tag URI_REDIRECTION = Tag.of("uri", "REDIRECTION");
    private static final Tag URI_ROOT = Tag.of("uri", "root");
    private static final Tag EXCEPTION_NONE = Tag.of("exception", "None");
    private static final Tag METHOD_UNKNOWN = Tag.of("method", "UNKNOWN");
    private static final Tag HOST_UNKNOWN = Tag.of("host", "UNKNOWN");
    private static final Pattern TRAILING_SLASH_PATTERN = Pattern.compile("/$");
    private static final Pattern MULTIPLE_SLASH_PATTERN = Pattern.compile("//+");

    private JettyClientTags() {
    }

    public static Tag method(Request request) {
        return request != null ? Tag.of("method", request.getMethod()) : METHOD_UNKNOWN;
    }

    public static Tag host(Request request) {
        return request != null ? Tag.of("host", request.getHost()) : HOST_UNKNOWN;
    }

    public static Tag status(Result result) {
        return Tag.of("status", Integer.toString(result.getResponse().getStatus()));
    }

    public static Tag uri(Result result, Function<Result, String> successfulUriPattern) {
        Response response = result.getResponse();
        if (response != null) {
            int status = response.getStatus();
            if (HttpStatus.isRedirection((int)status)) {
                return URI_REDIRECTION;
            }
            if (status == 404) {
                return URI_NOT_FOUND;
            }
        }
        String matchingPattern = successfulUriPattern.apply(result);
        if ((matchingPattern = MULTIPLE_SLASH_PATTERN.matcher(matchingPattern).replaceAll("/")).equals("/")) {
            return URI_ROOT;
        }
        matchingPattern = TRAILING_SLASH_PATTERN.matcher(matchingPattern).replaceAll("");
        return Tag.of("uri", matchingPattern);
    }

    public static Tag exception(Result result) {
        String simpleName;
        int status;
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
        return Tag.of("exception", StringUtils.isNotEmpty((String)(simpleName = exception.getClass().getSimpleName())) ? simpleName : exception.getClass().getName());
    }

    public static Tag outcome(Result result) {
        return Outcome.forStatus(result.getResponse().getStatus()).asTag();
    }
}

