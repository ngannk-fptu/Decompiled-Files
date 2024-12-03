/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.util.StringUtils
 *  org.glassfish.jersey.server.ContainerRequest
 *  org.glassfish.jersey.server.ContainerResponse
 *  org.glassfish.jersey.server.ExtendedUriInfo
 *  org.glassfish.jersey.server.monitoring.RequestEvent
 *  org.glassfish.jersey.uri.UriTemplate
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.http.Outcome;
import java.util.List;
import java.util.regex.Pattern;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.uri.UriTemplate;

public final class JerseyTags {
    private static final Tag URI_NOT_FOUND = Tag.of("uri", "NOT_FOUND");
    private static final Tag URI_REDIRECTION = Tag.of("uri", "REDIRECTION");
    private static final Tag URI_ROOT = Tag.of("uri", "root");
    private static final Tag EXCEPTION_NONE = Tag.of("exception", "None");
    private static final Tag STATUS_SERVER_ERROR = Tag.of("status", "500");
    private static final Tag METHOD_UNKNOWN = Tag.of("method", "UNKNOWN");
    static final Pattern TRAILING_SLASH_PATTERN = Pattern.compile("/$");
    static final Pattern MULTIPLE_SLASH_PATTERN = Pattern.compile("//+");

    private JerseyTags() {
    }

    public static Tag method(ContainerRequest request) {
        return request != null ? Tag.of("method", request.getMethod()) : METHOD_UNKNOWN;
    }

    public static Tag status(ContainerResponse response) {
        return response != null ? Tag.of("status", Integer.toString(response.getStatus())) : STATUS_SERVER_ERROR;
    }

    public static Tag uri(RequestEvent event) {
        String matchingPattern;
        ContainerResponse response = event.getContainerResponse();
        if (response != null) {
            int status = response.getStatus();
            if (JerseyTags.isRedirection(status) && event.getUriInfo().getMatchedResourceMethod() == null) {
                return URI_REDIRECTION;
            }
            if (status == 404 && event.getUriInfo().getMatchedResourceMethod() == null) {
                return URI_NOT_FOUND;
            }
        }
        if ((matchingPattern = JerseyTags.getMatchingPattern(event)).equals("/")) {
            return URI_ROOT;
        }
        return Tag.of("uri", matchingPattern);
    }

    static boolean isRedirection(int status) {
        return 300 <= status && status < 400;
    }

    static String getMatchingPattern(RequestEvent event) {
        ExtendedUriInfo uriInfo = event.getUriInfo();
        List templates = uriInfo.getMatchedTemplates();
        StringBuilder sb = new StringBuilder();
        sb.append(uriInfo.getBaseUri().getPath());
        for (int i = templates.size() - 1; i >= 0; --i) {
            sb.append(((UriTemplate)templates.get(i)).getTemplate());
        }
        String multipleSlashCleaned = MULTIPLE_SLASH_PATTERN.matcher(sb.toString()).replaceAll("/");
        if (multipleSlashCleaned.equals("/")) {
            return multipleSlashCleaned;
        }
        return TRAILING_SLASH_PATTERN.matcher(multipleSlashCleaned).replaceAll("");
    }

    public static Tag exception(RequestEvent event) {
        String simpleName;
        int status;
        Throwable exception = event.getException();
        if (exception == null) {
            return EXCEPTION_NONE;
        }
        ContainerResponse response = event.getContainerResponse();
        if (response != null && ((status = response.getStatus()) == 404 || JerseyTags.isRedirection(status))) {
            return EXCEPTION_NONE;
        }
        if (exception.getCause() != null) {
            exception = exception.getCause();
        }
        return Tag.of("exception", StringUtils.isNotEmpty((String)(simpleName = exception.getClass().getSimpleName())) ? simpleName : exception.getClass().getName());
    }

    public static Tag outcome(ContainerResponse response) {
        if (response != null) {
            return Outcome.forStatus(response.getStatus()).asTag();
        }
        return Outcome.SERVER_ERROR.asTag();
    }
}

