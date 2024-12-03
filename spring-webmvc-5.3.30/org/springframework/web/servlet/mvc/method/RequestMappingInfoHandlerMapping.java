/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.InvalidMediaTypeException
 *  org.springframework.http.MediaType
 *  org.springframework.http.server.PathContainer
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 *  org.springframework.web.HttpMediaTypeNotAcceptableException
 *  org.springframework.web.HttpMediaTypeNotSupportedException
 *  org.springframework.web.HttpRequestMethodNotSupportedException
 *  org.springframework.web.bind.UnsatisfiedServletRequestParameterException
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.method.HandlerMethod
 *  org.springframework.web.util.ServletRequestPathUtils
 *  org.springframework.web.util.WebUtils
 *  org.springframework.web.util.pattern.PathPattern
 *  org.springframework.web.util.pattern.PathPattern$PathMatchInfo
 */
package org.springframework.web.servlet.mvc.method;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMethodMappingNamingStrategy;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.WebUtils;
import org.springframework.web.util.pattern.PathPattern;

public abstract class RequestMappingInfoHandlerMapping
extends AbstractHandlerMethodMapping<RequestMappingInfo> {
    private static final Method HTTP_OPTIONS_HANDLE_METHOD;

    protected RequestMappingInfoHandlerMapping() {
        this.setHandlerMethodMappingNamingStrategy(new RequestMappingInfoHandlerMethodMappingNamingStrategy());
    }

    @Override
    protected Set<String> getMappingPathPatterns(RequestMappingInfo info) {
        return info.getPatternValues();
    }

    @Override
    protected Set<String> getDirectPaths(RequestMappingInfo info) {
        return info.getDirectPaths();
    }

    @Override
    protected RequestMappingInfo getMatchingMapping(RequestMappingInfo info, HttpServletRequest request) {
        return info.getMatchingCondition(request);
    }

    @Override
    protected Comparator<RequestMappingInfo> getMappingComparator(HttpServletRequest request) {
        return (info1, info2) -> info1.compareTo((RequestMappingInfo)info2, request);
    }

    @Override
    @Nullable
    protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        request.removeAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        try {
            HandlerMethod handlerMethod = super.getHandlerInternal(request);
            return handlerMethod;
        }
        finally {
            ProducesRequestCondition.clearMediaTypesAttribute(request);
        }
    }

    @Override
    protected void handleMatch(RequestMappingInfo info, String lookupPath, HttpServletRequest request) {
        Set<MediaType> mediaTypes;
        super.handleMatch(info, lookupPath, request);
        RequestCondition condition = info.getActivePatternsCondition();
        if (condition instanceof PathPatternsRequestCondition) {
            this.extractMatchDetails((PathPatternsRequestCondition)condition, lookupPath, request);
        } else {
            this.extractMatchDetails((PatternsRequestCondition)condition, lookupPath, request);
        }
        ProducesRequestCondition producesCondition = info.getProducesCondition();
        if (!producesCondition.isEmpty() && !(mediaTypes = producesCondition.getProducibleMediaTypes()).isEmpty()) {
            request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, mediaTypes);
        }
    }

    private void extractMatchDetails(PathPatternsRequestCondition condition, String lookupPath, HttpServletRequest request) {
        Map uriVariables;
        PathPattern bestPattern;
        if (condition.isEmptyPathMapping()) {
            bestPattern = condition.getFirstPattern();
            uriVariables = Collections.emptyMap();
        } else {
            PathContainer path = ServletRequestPathUtils.getParsedRequestPath((ServletRequest)request).pathWithinApplication();
            bestPattern = condition.getFirstPattern();
            PathPattern.PathMatchInfo result = bestPattern.matchAndExtract(path);
            Assert.notNull((Object)result, () -> "Expected bestPattern: " + bestPattern + " to match lookupPath " + path);
            uriVariables = result.getUriVariables();
            request.setAttribute(MATRIX_VARIABLES_ATTRIBUTE, (Object)result.getMatrixVariables());
        }
        request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, (Object)bestPattern.getPatternString());
        request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriVariables);
    }

    private void extractMatchDetails(PatternsRequestCondition condition, String lookupPath, HttpServletRequest request) {
        Map uriVariables;
        String bestPattern;
        if (condition.isEmptyPathMapping()) {
            bestPattern = lookupPath;
            uriVariables = Collections.emptyMap();
        } else {
            bestPattern = condition.getPatterns().iterator().next();
            uriVariables = this.getPathMatcher().extractUriTemplateVariables(bestPattern, lookupPath);
            if (!this.getUrlPathHelper().shouldRemoveSemicolonContent()) {
                request.setAttribute(MATRIX_VARIABLES_ATTRIBUTE, this.extractMatrixVariables(request, uriVariables));
            }
            uriVariables = this.getUrlPathHelper().decodePathVariables(request, uriVariables);
        }
        request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, (Object)bestPattern);
        request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, (Object)uriVariables);
    }

    private Map<String, MultiValueMap<String, String>> extractMatrixVariables(HttpServletRequest request, Map<String, String> uriVariables) {
        LinkedHashMap<String, MultiValueMap<String, String>> result = new LinkedHashMap<String, MultiValueMap<String, String>>();
        uriVariables.forEach((uriVarKey, uriVarValue) -> {
            int equalsIndex = uriVarValue.indexOf(61);
            if (equalsIndex == -1) {
                return;
            }
            int semicolonIndex = uriVarValue.indexOf(59);
            if (semicolonIndex != -1 && semicolonIndex != 0) {
                uriVariables.put((String)uriVarKey, uriVarValue.substring(0, semicolonIndex));
            }
            String matrixVariables = semicolonIndex == -1 || semicolonIndex == 0 || equalsIndex < semicolonIndex ? uriVarValue : uriVarValue.substring(semicolonIndex + 1);
            MultiValueMap vars = WebUtils.parseMatrixVariables((String)matrixVariables);
            result.put((String)uriVarKey, (MultiValueMap<String, String>)this.getUrlPathHelper().decodeMatrixVariables(request, vars));
        });
        return result;
    }

    @Override
    protected HandlerMethod handleNoMatch(Set<RequestMappingInfo> infos, String lookupPath, HttpServletRequest request) throws ServletException {
        if (CollectionUtils.isEmpty(infos)) {
            return null;
        }
        PartialMatchHelper helper = new PartialMatchHelper(infos, request);
        if (helper.isEmpty()) {
            return null;
        }
        if (helper.hasMethodsMismatch()) {
            Set<String> methods = helper.getAllowedMethods();
            if (HttpMethod.OPTIONS.matches(request.getMethod())) {
                Set<MediaType> mediaTypes = helper.getConsumablePatchMediaTypes();
                HttpOptionsHandler handler = new HttpOptionsHandler(methods, mediaTypes);
                return new HandlerMethod((Object)handler, HTTP_OPTIONS_HANDLE_METHOD);
            }
            throw new HttpRequestMethodNotSupportedException(request.getMethod(), methods);
        }
        if (helper.hasConsumesMismatch()) {
            Set<MediaType> mediaTypes = helper.getConsumableMediaTypes();
            MediaType contentType = null;
            if (StringUtils.hasLength((String)request.getContentType())) {
                try {
                    contentType = MediaType.parseMediaType((String)request.getContentType());
                }
                catch (InvalidMediaTypeException ex) {
                    throw new HttpMediaTypeNotSupportedException(ex.getMessage());
                }
            }
            throw new HttpMediaTypeNotSupportedException(contentType, new ArrayList<MediaType>(mediaTypes));
        }
        if (helper.hasProducesMismatch()) {
            Set<MediaType> mediaTypes = helper.getProducibleMediaTypes();
            throw new HttpMediaTypeNotAcceptableException(new ArrayList<MediaType>(mediaTypes));
        }
        if (helper.hasParamsMismatch()) {
            List<String[]> conditions = helper.getParamConditions();
            throw new UnsatisfiedServletRequestParameterException(conditions, request.getParameterMap());
        }
        return null;
    }

    static {
        try {
            HTTP_OPTIONS_HANDLE_METHOD = HttpOptionsHandler.class.getMethod("handle", new Class[0]);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Failed to retrieve internal handler method for HTTP OPTIONS", ex);
        }
    }

    private static class HttpOptionsHandler {
        private final HttpHeaders headers = new HttpHeaders();

        public HttpOptionsHandler(Set<String> declaredMethods, Set<MediaType> acceptPatch) {
            this.headers.setAllow(HttpOptionsHandler.initAllowedHttpMethods(declaredMethods));
            this.headers.setAcceptPatch(new ArrayList<MediaType>(acceptPatch));
        }

        private static Set<HttpMethod> initAllowedHttpMethods(Set<String> declaredMethods) {
            LinkedHashSet<HttpMethod> result = new LinkedHashSet<HttpMethod>(declaredMethods.size());
            if (declaredMethods.isEmpty()) {
                for (HttpMethod method : HttpMethod.values()) {
                    if (method == HttpMethod.TRACE) continue;
                    result.add(method);
                }
            } else {
                for (String method : declaredMethods) {
                    HttpMethod httpMethod = HttpMethod.valueOf((String)method);
                    result.add(httpMethod);
                    if (httpMethod != HttpMethod.GET) continue;
                    result.add(HttpMethod.HEAD);
                }
                result.add(HttpMethod.OPTIONS);
            }
            return result;
        }

        public HttpHeaders handle() {
            return this.headers;
        }
    }

    private static class PartialMatchHelper {
        private final List<PartialMatch> partialMatches = new ArrayList<PartialMatch>();

        PartialMatchHelper(Set<RequestMappingInfo> infos, HttpServletRequest request) {
            for (RequestMappingInfo info : infos) {
                if (info.getActivePatternsCondition().getMatchingCondition(request) == null) continue;
                this.partialMatches.add(new PartialMatch(info, request));
            }
        }

        public boolean isEmpty() {
            return this.partialMatches.isEmpty();
        }

        public boolean hasMethodsMismatch() {
            for (PartialMatch match : this.partialMatches) {
                if (!match.hasMethodsMatch()) continue;
                return false;
            }
            return true;
        }

        public boolean hasConsumesMismatch() {
            for (PartialMatch match : this.partialMatches) {
                if (!match.hasConsumesMatch()) continue;
                return false;
            }
            return true;
        }

        public boolean hasProducesMismatch() {
            for (PartialMatch match : this.partialMatches) {
                if (!match.hasProducesMatch()) continue;
                return false;
            }
            return true;
        }

        public boolean hasParamsMismatch() {
            for (PartialMatch match : this.partialMatches) {
                if (!match.hasParamsMatch()) continue;
                return false;
            }
            return true;
        }

        public Set<String> getAllowedMethods() {
            LinkedHashSet<String> result = new LinkedHashSet<String>();
            for (PartialMatch match : this.partialMatches) {
                for (RequestMethod method : match.getInfo().getMethodsCondition().getMethods()) {
                    result.add(method.name());
                }
            }
            return result;
        }

        public Set<MediaType> getConsumableMediaTypes() {
            LinkedHashSet<MediaType> result = new LinkedHashSet<MediaType>();
            for (PartialMatch match : this.partialMatches) {
                if (!match.hasMethodsMatch()) continue;
                result.addAll(match.getInfo().getConsumesCondition().getConsumableMediaTypes());
            }
            return result;
        }

        public Set<MediaType> getProducibleMediaTypes() {
            LinkedHashSet<MediaType> result = new LinkedHashSet<MediaType>();
            for (PartialMatch match : this.partialMatches) {
                if (!match.hasConsumesMatch()) continue;
                result.addAll(match.getInfo().getProducesCondition().getProducibleMediaTypes());
            }
            return result;
        }

        public List<String[]> getParamConditions() {
            ArrayList<String[]> result = new ArrayList<String[]>();
            for (PartialMatch match : this.partialMatches) {
                Set<NameValueExpression<String>> set;
                if (!match.hasProducesMatch() || CollectionUtils.isEmpty(set = match.getInfo().getParamsCondition().getExpressions())) continue;
                int i2 = 0;
                String[] array = new String[set.size()];
                for (NameValueExpression<String> expression : set) {
                    array[i2++] = expression.toString();
                }
                result.add(array);
            }
            return result;
        }

        public Set<MediaType> getConsumablePatchMediaTypes() {
            LinkedHashSet<MediaType> result = new LinkedHashSet<MediaType>();
            for (PartialMatch match : this.partialMatches) {
                Set<RequestMethod> methods = match.getInfo().getMethodsCondition().getMethods();
                if (!methods.isEmpty() && !methods.contains(RequestMethod.PATCH)) continue;
                result.addAll(match.getInfo().getConsumesCondition().getConsumableMediaTypes());
            }
            return result;
        }

        private static class PartialMatch {
            private final RequestMappingInfo info;
            private final boolean methodsMatch;
            private final boolean consumesMatch;
            private final boolean producesMatch;
            private final boolean paramsMatch;

            public PartialMatch(RequestMappingInfo info, HttpServletRequest request) {
                this.info = info;
                this.methodsMatch = info.getMethodsCondition().getMatchingCondition(request) != null;
                this.consumesMatch = info.getConsumesCondition().getMatchingCondition(request) != null;
                this.producesMatch = info.getProducesCondition().getMatchingCondition(request) != null;
                this.paramsMatch = info.getParamsCondition().getMatchingCondition(request) != null;
            }

            public RequestMappingInfo getInfo() {
                return this.info;
            }

            public boolean hasMethodsMatch() {
                return this.methodsMatch;
            }

            public boolean hasConsumesMatch() {
                return this.hasMethodsMatch() && this.consumesMatch;
            }

            public boolean hasProducesMatch() {
                return this.hasConsumesMatch() && this.producesMatch;
            }

            public boolean hasParamsMatch() {
                return this.hasProducesMatch() && this.paramsMatch;
            }

            public String toString() {
                return this.info.toString();
            }
        }
    }
}

