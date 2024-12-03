/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.http.MediaType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MimeType
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.HttpMediaTypeException
 *  org.springframework.web.HttpMediaTypeNotAcceptableException
 *  org.springframework.web.accept.ContentNegotiationManager
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.context.request.ServletWebRequest
 *  org.springframework.web.cors.CorsUtils
 */
package org.springframework.web.servlet.mvc.condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.AbstractMediaTypeExpression;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.MediaTypeExpression;

public final class ProducesRequestCondition
extends AbstractRequestCondition<ProducesRequestCondition> {
    private static final ContentNegotiationManager DEFAULT_CONTENT_NEGOTIATION_MANAGER = new ContentNegotiationManager();
    private static final ProducesRequestCondition EMPTY_CONDITION = new ProducesRequestCondition(new String[0]);
    private static final List<ProduceMediaTypeExpression> MEDIA_TYPE_ALL_LIST = Collections.singletonList(new ProduceMediaTypeExpression("*/*"));
    private static final String MEDIA_TYPES_ATTRIBUTE = ProducesRequestCondition.class.getName() + ".MEDIA_TYPES";
    private final List<ProduceMediaTypeExpression> expressions;
    private final ContentNegotiationManager contentNegotiationManager;

    public ProducesRequestCondition(String ... produces) {
        this(produces, (String[])null, (ContentNegotiationManager)null);
    }

    public ProducesRequestCondition(String[] produces, @Nullable String[] headers) {
        this(produces, headers, (ContentNegotiationManager)null);
    }

    public ProducesRequestCondition(String[] produces, @Nullable String[] headers, @Nullable ContentNegotiationManager manager) {
        this.expressions = this.parseExpressions(produces, headers);
        if (this.expressions.size() > 1) {
            Collections.sort(this.expressions);
        }
        this.contentNegotiationManager = manager != null ? manager : DEFAULT_CONTENT_NEGOTIATION_MANAGER;
    }

    private List<ProduceMediaTypeExpression> parseExpressions(String[] produces, @Nullable String[] headers) {
        LinkedHashSet<ProduceMediaTypeExpression> result = null;
        if (!ObjectUtils.isEmpty((Object[])headers)) {
            for (String header : headers) {
                HeadersRequestCondition.HeaderExpression expr = new HeadersRequestCondition.HeaderExpression(header);
                if (!"Accept".equalsIgnoreCase(expr.name) || expr.value == null) continue;
                for (MediaType mediaType : MediaType.parseMediaTypes((String)((String)expr.value))) {
                    result = result != null ? result : new LinkedHashSet<ProduceMediaTypeExpression>();
                    result.add(new ProduceMediaTypeExpression(mediaType, expr.isNegated));
                }
            }
        }
        if (!ObjectUtils.isEmpty((Object[])produces)) {
            for (String produce : produces) {
                result = result != null ? result : new LinkedHashSet<ProduceMediaTypeExpression>();
                result.add(new ProduceMediaTypeExpression(produce));
            }
        }
        return result != null ? new ArrayList(result) : Collections.emptyList();
    }

    private ProducesRequestCondition(List<ProduceMediaTypeExpression> expressions, ProducesRequestCondition other) {
        this.expressions = expressions;
        this.contentNegotiationManager = other.contentNegotiationManager;
    }

    public Set<MediaTypeExpression> getExpressions() {
        return new LinkedHashSet<MediaTypeExpression>(this.expressions);
    }

    public Set<MediaType> getProducibleMediaTypes() {
        LinkedHashSet<MediaType> result = new LinkedHashSet<MediaType>();
        for (ProduceMediaTypeExpression expression : this.expressions) {
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
    protected List<ProduceMediaTypeExpression> getContent() {
        return this.expressions;
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    @Override
    public ProducesRequestCondition combine(ProducesRequestCondition other) {
        return !other.expressions.isEmpty() ? other : this;
    }

    @Override
    @Nullable
    public ProducesRequestCondition getMatchingCondition(HttpServletRequest request) {
        List<MediaType> acceptedMediaTypes;
        if (CorsUtils.isPreFlightRequest((HttpServletRequest)request)) {
            return EMPTY_CONDITION;
        }
        if (this.isEmpty()) {
            return this;
        }
        try {
            acceptedMediaTypes = this.getAcceptedMediaTypes(request);
        }
        catch (HttpMediaTypeException ex) {
            return null;
        }
        List<ProduceMediaTypeExpression> result = this.getMatchingExpressions(acceptedMediaTypes);
        if (!CollectionUtils.isEmpty(result)) {
            return new ProducesRequestCondition(result, this);
        }
        if (MediaType.ALL.isPresentIn(acceptedMediaTypes)) {
            return EMPTY_CONDITION;
        }
        return null;
    }

    @Nullable
    private List<ProduceMediaTypeExpression> getMatchingExpressions(List<MediaType> acceptedMediaTypes) {
        ArrayList<ProduceMediaTypeExpression> result = null;
        for (ProduceMediaTypeExpression expression : this.expressions) {
            if (!expression.match(acceptedMediaTypes)) continue;
            result = result != null ? result : new ArrayList<ProduceMediaTypeExpression>();
            result.add(expression);
        }
        return result;
    }

    @Override
    public int compareTo(ProducesRequestCondition other, HttpServletRequest request) {
        if (this.expressions.isEmpty() && other.expressions.isEmpty()) {
            return 0;
        }
        try {
            List<MediaType> acceptedMediaTypes = this.getAcceptedMediaTypes(request);
            for (MediaType acceptedMediaType : acceptedMediaTypes) {
                int otherIndex;
                int thisIndex = this.indexOfEqualMediaType(acceptedMediaType);
                int result = this.compareMatchingMediaTypes(this, thisIndex, other, otherIndex = other.indexOfEqualMediaType(acceptedMediaType));
                if (result != 0) {
                    return result;
                }
                thisIndex = this.indexOfIncludedMediaType(acceptedMediaType);
                result = this.compareMatchingMediaTypes(this, thisIndex, other, otherIndex = other.indexOfIncludedMediaType(acceptedMediaType));
                if (result == 0) continue;
                return result;
            }
            return 0;
        }
        catch (HttpMediaTypeNotAcceptableException ex) {
            throw new IllegalStateException("Cannot compare without having any requested media types", ex);
        }
    }

    private List<MediaType> getAcceptedMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
        List result = (List)request.getAttribute(MEDIA_TYPES_ATTRIBUTE);
        if (result == null) {
            result = this.contentNegotiationManager.resolveMediaTypes((NativeWebRequest)new ServletWebRequest(request));
            request.setAttribute(MEDIA_TYPES_ATTRIBUTE, (Object)result);
        }
        return result;
    }

    private int indexOfEqualMediaType(MediaType mediaType) {
        for (int i2 = 0; i2 < this.getExpressionsToCompare().size(); ++i2) {
            MediaType currentMediaType = this.getExpressionsToCompare().get(i2).getMediaType();
            if (!mediaType.getType().equalsIgnoreCase(currentMediaType.getType()) || !mediaType.getSubtype().equalsIgnoreCase(currentMediaType.getSubtype())) continue;
            return i2;
        }
        return -1;
    }

    private int indexOfIncludedMediaType(MediaType mediaType) {
        for (int i2 = 0; i2 < this.getExpressionsToCompare().size(); ++i2) {
            if (!mediaType.includes(this.getExpressionsToCompare().get(i2).getMediaType())) continue;
            return i2;
        }
        return -1;
    }

    private int compareMatchingMediaTypes(ProducesRequestCondition condition1, int index1, ProducesRequestCondition condition2, int index2) {
        int result = 0;
        if (index1 != index2) {
            result = index2 - index1;
        } else if (index1 != -1) {
            ProduceMediaTypeExpression expr2;
            ProduceMediaTypeExpression expr1 = condition1.getExpressionsToCompare().get(index1);
            result = expr1.compareTo(expr2 = condition2.getExpressionsToCompare().get(index2));
            result = result != 0 ? result : expr1.getMediaType().compareTo((MimeType)expr2.getMediaType());
        }
        return result;
    }

    private List<ProduceMediaTypeExpression> getExpressionsToCompare() {
        return this.expressions.isEmpty() ? MEDIA_TYPE_ALL_LIST : this.expressions;
    }

    public static void clearMediaTypesAttribute(HttpServletRequest request) {
        request.removeAttribute(MEDIA_TYPES_ATTRIBUTE);
    }

    static class ProduceMediaTypeExpression
    extends AbstractMediaTypeExpression {
        ProduceMediaTypeExpression(MediaType mediaType, boolean negated) {
            super(mediaType, negated);
        }

        ProduceMediaTypeExpression(String expression) {
            super(expression);
        }

        public final boolean match(List<MediaType> acceptedMediaTypes) {
            boolean match = this.matchMediaType(acceptedMediaTypes);
            return !this.isNegated() == match;
        }

        private boolean matchMediaType(List<MediaType> acceptedMediaTypes) {
            for (MediaType acceptedMediaType : acceptedMediaTypes) {
                if (!this.getMediaType().isCompatibleWith(acceptedMediaType) || !this.matchParameters(acceptedMediaType)) continue;
                return true;
            }
            return false;
        }

        private boolean matchParameters(MediaType acceptedMediaType) {
            for (String name : this.getMediaType().getParameters().keySet()) {
                String s1 = this.getMediaType().getParameter(name);
                String s2 = acceptedMediaType.getParameter(name);
                if (!StringUtils.hasText((String)s1) || !StringUtils.hasText((String)s2) || s1.equalsIgnoreCase(s2)) continue;
                return false;
            }
            return true;
        }
    }
}

