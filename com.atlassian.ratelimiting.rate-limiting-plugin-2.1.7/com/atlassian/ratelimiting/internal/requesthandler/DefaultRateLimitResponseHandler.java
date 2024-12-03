/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Strings
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.ratelimiting.internal.requesthandler;

import com.atlassian.ratelimiting.bucket.TokenBucket;
import com.atlassian.ratelimiting.internal.user.AnonymousUserProfile;
import com.atlassian.ratelimiting.requesthandler.RateLimitResponseHandler;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Strings;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.ObjectMapper;

public class DefaultRateLimitResponseHandler
implements RateLimitResponseHandler {
    static final String REST_ERROR_RATE_LIMITED_MESSAGE_KEY = "ratelimit.rest.error.ratelimited.message";
    static final String HEADER_RATE_LIMITING_LIMIT = "X-RateLimit-Limit";
    static final String HEADER_RATE_LIMITING_REMAINING = "X-RateLimit-Remaining";
    static final String HEADER_RATE_LIMITING_FILL_RATE = "X-RateLimit-FillRate";
    static final String HEADER_RATE_LIMITING_INTERVAL_SECONDS = "X-RateLimit-Interval-Seconds";
    static final String HEADER_ACCEPT = "Accept";
    static final String HEADER_RETRY_AFTER = "Retry-After";
    static final int TOO_MANY_REQUESTS_STATUS = 429;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final I18nResolver i18nResolver;
    private final RateLimitResponseHandler.RateLimitHeaderOption rateLimitHeaderOption;

    public DefaultRateLimitResponseHandler(@Nonnull I18nResolver i18nResolver, @Nonnull RateLimitResponseHandler.RateLimitHeaderOption rateLimitHeaderOption) {
        this.i18nResolver = i18nResolver;
        this.rateLimitHeaderOption = rateLimitHeaderOption;
    }

    @Override
    public void applyRateLimitingInfo(HttpServletResponse response, HttpServletRequest request, UserKey userKey, Supplier<Optional<TokenBucket>> optionalBucket) throws IOException {
        this.addRateLimitingHeaders(response, userKey, optionalBucket);
        this.setUpResponse(request, response);
    }

    @Override
    public void addRateLimitingHeaders(HttpServletResponse response, UserKey userKey, Supplier<Optional<TokenBucket>> optionalBucket) {
        switch (this.rateLimitHeaderOption) {
            case ENABLED: {
                this.addRateLimitingHeaders(response, optionalBucket);
                break;
            }
            case AUTHENTICATED_REQUEST_ONLY: {
                if (AnonymousUserProfile.isAnonymousRepresentativeUser(userKey)) break;
                this.addRateLimitingHeaders(response, optionalBucket);
                break;
            }
        }
    }

    private void setUpResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String message = this.i18nResolver.getText(REST_ERROR_RATE_LIMITED_MESSAGE_KEY);
        String acceptHeaderValue = request.getHeader(HEADER_ACCEPT);
        if (!Strings.isNullOrEmpty((String)acceptHeaderValue) && acceptHeaderValue.contains("application/json")) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
            RestRateLimitedResponse rateLimited = new RestRateLimitedResponse(message);
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString((Object)rateLimited));
        } else {
            response.sendError(429, message);
        }
    }

    private void addRateLimitingHeaders(HttpServletResponse response, Supplier<Optional<TokenBucket>> optionalBucket) {
        optionalBucket.get().ifPresent(bucket -> {
            response.addHeader(HEADER_RATE_LIMITING_LIMIT, Integer.toString(bucket.getSettings().getCapacity()));
            response.addHeader(HEADER_RATE_LIMITING_REMAINING, Long.toString(bucket.getAvailableTokens()));
            response.addHeader(HEADER_RATE_LIMITING_FILL_RATE, Integer.toString(bucket.getSettings().getFillRate()));
            response.addHeader(HEADER_RATE_LIMITING_INTERVAL_SECONDS, Long.toString(bucket.getSettings().getIntervalDuration().getSeconds()));
            response.addHeader(HEADER_RETRY_AFTER, Long.toString(bucket.getSecondsUntilTokenAvailable()));
        });
    }

    @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
    static class RestRateLimitedResponse {
        private String message;

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof RestRateLimitedResponse)) {
                return false;
            }
            RestRateLimitedResponse other = (RestRateLimitedResponse)o;
            if (!other.canEqual(this)) {
                return false;
            }
            String this$message = this.getMessage();
            String other$message = other.getMessage();
            return !(this$message == null ? other$message != null : !this$message.equals(other$message));
        }

        protected boolean canEqual(Object other) {
            return other instanceof RestRateLimitedResponse;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $message = this.getMessage();
            result = result * 59 + ($message == null ? 43 : $message.hashCode());
            return result;
        }

        public String toString() {
            return "DefaultRateLimitResponseHandler.RestRateLimitedResponse(message=" + this.getMessage() + ")";
        }

        public RestRateLimitedResponse() {
        }

        public RestRateLimitedResponse(String message) {
            this.message = message;
        }
    }
}

