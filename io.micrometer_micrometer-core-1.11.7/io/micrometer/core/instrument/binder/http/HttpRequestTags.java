/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.util.StringUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package io.micrometer.core.instrument.binder.http;

import io.micrometer.common.util.StringUtils;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.http.Outcome;
import jakarta.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Incubating(since="1.4.0")
public class HttpRequestTags {
    private static final Tag EXCEPTION_NONE = Tag.of("exception", "None");
    private static final Tag STATUS_UNKNOWN = Tag.of("status", "UNKNOWN");
    private static final Tag METHOD_UNKNOWN = Tag.of("method", "UNKNOWN");

    private HttpRequestTags() {
    }

    public static Tag method(javax.servlet.http.HttpServletRequest request) {
        return request != null ? Tag.of("method", request.getMethod()) : METHOD_UNKNOWN;
    }

    public static Tag method(HttpServletRequest request) {
        return request != null ? Tag.of("method", request.getMethod()) : METHOD_UNKNOWN;
    }

    public static Tag status(HttpServletResponse response) {
        return response != null ? Tag.of("status", Integer.toString(response.getStatus())) : STATUS_UNKNOWN;
    }

    public static Tag status(jakarta.servlet.http.HttpServletResponse response) {
        return response != null ? Tag.of("status", Integer.toString(response.getStatus())) : STATUS_UNKNOWN;
    }

    public static Tag exception(Throwable exception) {
        if (exception != null) {
            String simpleName = exception.getClass().getSimpleName();
            return Tag.of("exception", StringUtils.isNotBlank((String)simpleName) ? simpleName : exception.getClass().getName());
        }
        return EXCEPTION_NONE;
    }

    public static Tag outcome(HttpServletResponse response) {
        Outcome outcome = response != null ? Outcome.forStatus(response.getStatus()) : Outcome.UNKNOWN;
        return outcome.asTag();
    }

    public static Tag outcome(jakarta.servlet.http.HttpServletResponse response) {
        Outcome outcome = response != null ? Outcome.forStatus(response.getStatus()) : Outcome.UNKNOWN;
        return outcome.asTag();
    }
}

