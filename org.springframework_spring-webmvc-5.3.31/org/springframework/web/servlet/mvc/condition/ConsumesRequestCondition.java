/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.http.InvalidMediaTypeException
 *  org.springframework.http.MediaType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.cors.CorsUtils
 */
package org.springframework.web.servlet.mvc.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.AbstractMediaTypeExpression;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.MediaTypeExpression;

public final class ConsumesRequestCondition
extends AbstractRequestCondition<ConsumesRequestCondition> {
    private static final ConsumesRequestCondition EMPTY_CONDITION = new ConsumesRequestCondition(new String[0]);
    private final List<ConsumeMediaTypeExpression> expressions;
    private boolean bodyRequired = true;

    public ConsumesRequestCondition(String ... consumes) {
        this(consumes, (String[])null);
    }

    public ConsumesRequestCondition(String[] consumes, @Nullable String[] headers) {
        this.expressions = ConsumesRequestCondition.parseExpressions(consumes, headers);
        if (this.expressions.size() > 1) {
            Collections.sort(this.expressions);
        }
    }

    private static List<ConsumeMediaTypeExpression> parseExpressions(String[] consumes, @Nullable String[] headers) {
        LinkedHashSet<ConsumeMediaTypeExpression> result = null;
        if (!ObjectUtils.isEmpty((Object[])headers)) {
            for (String header : headers) {
                HeadersRequestCondition.HeaderExpression expr = new HeadersRequestCondition.HeaderExpression(header);
                if (!"Content-Type".equalsIgnoreCase(expr.name) || expr.value == null) continue;
                result = result != null ? result : new LinkedHashSet<ConsumeMediaTypeExpression>();
                for (MediaType mediaType : MediaType.parseMediaTypes((String)((String)expr.value))) {
                    result.add(new ConsumeMediaTypeExpression(mediaType, expr.isNegated));
                }
            }
        }
        if (!ObjectUtils.isEmpty((Object[])consumes)) {
            result = result != null ? result : new LinkedHashSet<ConsumeMediaTypeExpression>();
            for (String consume : consumes) {
                result.add(new ConsumeMediaTypeExpression(consume));
            }
        }
        return result != null ? new ArrayList(result) : Collections.emptyList();
    }

    private ConsumesRequestCondition(List<ConsumeMediaTypeExpression> expressions) {
        this.expressions = expressions;
    }

    public Set<MediaTypeExpression> getExpressions() {
        return new LinkedHashSet<MediaTypeExpression>(this.expressions);
    }

    public Set<MediaType> getConsumableMediaTypes() {
        LinkedHashSet<MediaType> result = new LinkedHashSet<MediaType>();
        for (ConsumeMediaTypeExpression expression : this.expressions) {
            if (expression.isNegated()) continue;
            result.add(expression.getMediaType());
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return this.expressions.isEmpty();
    }

    @Override
    protected Collection<ConsumeMediaTypeExpression> getContent() {
        return this.expressions;
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    public void setBodyRequired(boolean bodyRequired) {
        this.bodyRequired = bodyRequired;
    }

    public boolean isBodyRequired() {
        return this.bodyRequired;
    }

    @Override
    public ConsumesRequestCondition combine(ConsumesRequestCondition other) {
        return !other.expressions.isEmpty() ? other : this;
    }

    @Override
    @Nullable
    public ConsumesRequestCondition getMatchingCondition(HttpServletRequest request) {
        MediaType contentType;
        if (CorsUtils.isPreFlightRequest((HttpServletRequest)request)) {
            return EMPTY_CONDITION;
        }
        if (this.isEmpty()) {
            return this;
        }
        if (!this.hasBody(request) && !this.bodyRequired) {
            return EMPTY_CONDITION;
        }
        try {
            contentType = StringUtils.hasLength((String)request.getContentType()) ? MediaType.parseMediaType((String)request.getContentType()) : MediaType.APPLICATION_OCTET_STREAM;
        }
        catch (InvalidMediaTypeException ex) {
            return null;
        }
        List<ConsumeMediaTypeExpression> result = this.getMatchingExpressions(contentType);
        return !CollectionUtils.isEmpty(result) ? new ConsumesRequestCondition(result) : null;
    }

    private boolean hasBody(HttpServletRequest request) {
        String contentLength = request.getHeader("Content-Length");
        String transferEncoding = request.getHeader("Transfer-Encoding");
        return StringUtils.hasText((String)transferEncoding) || StringUtils.hasText((String)contentLength) && !contentLength.trim().equals("0");
    }

    @Nullable
    private List<ConsumeMediaTypeExpression> getMatchingExpressions(MediaType contentType) {
        ArrayList<ConsumeMediaTypeExpression> result = null;
        for (ConsumeMediaTypeExpression expression : this.expressions) {
            if (!expression.match(contentType)) continue;
            result = result != null ? result : new ArrayList<ConsumeMediaTypeExpression>();
            result.add(expression);
        }
        return result;
    }

    @Override
    public int compareTo(ConsumesRequestCondition other, HttpServletRequest request) {
        if (this.expressions.isEmpty() && other.expressions.isEmpty()) {
            return 0;
        }
        if (this.expressions.isEmpty()) {
            return 1;
        }
        if (other.expressions.isEmpty()) {
            return -1;
        }
        return this.expressions.get(0).compareTo(other.expressions.get(0));
    }

    static class ConsumeMediaTypeExpression
    extends AbstractMediaTypeExpression {
        ConsumeMediaTypeExpression(String expression) {
            super(expression);
        }

        ConsumeMediaTypeExpression(MediaType mediaType, boolean negated) {
            super(mediaType, negated);
        }

        public final boolean match(MediaType contentType) {
            boolean match = this.getMediaType().includes(contentType);
            return !this.isNegated() == match;
        }
    }
}

