/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.Part
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.server.PathContainer
 *  org.springframework.http.server.RequestPath
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.cors.CorsUtils
 *  org.springframework.web.util.UriBuilder
 *  org.springframework.web.util.UriUtils
 *  org.springframework.web.util.pattern.PathPattern
 *  org.springframework.web.util.pattern.PathPattern$PathMatchInfo
 *  org.springframework.web.util.pattern.PathPattern$PathRemainingMatchInfo
 *  org.springframework.web.util.pattern.PathPatternParser
 */
package org.springframework.web.servlet.function;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.function.ChangePathPatternParserVisitor;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public abstract class RequestPredicates {
    private static final Log logger = LogFactory.getLog(RequestPredicates.class);

    public static RequestPredicate all() {
        return request -> true;
    }

    public static RequestPredicate method(HttpMethod httpMethod) {
        return new HttpMethodPredicate(httpMethod);
    }

    public static RequestPredicate methods(HttpMethod ... httpMethods) {
        return new HttpMethodPredicate(httpMethods);
    }

    public static RequestPredicate path(String pattern) {
        Assert.notNull((Object)pattern, (String)"'pattern' must not be null");
        PathPatternParser parser = PathPatternParser.defaultInstance;
        pattern = parser.initFullPathPattern(pattern);
        return RequestPredicates.pathPredicates(parser).apply(pattern);
    }

    public static Function<String, RequestPredicate> pathPredicates(PathPatternParser patternParser) {
        Assert.notNull((Object)patternParser, (String)"PathPatternParser must not be null");
        return pattern -> new PathPatternPredicate(patternParser.parse(pattern));
    }

    public static RequestPredicate headers(Predicate<ServerRequest.Headers> headersPredicate) {
        return new HeadersPredicate(headersPredicate);
    }

    public static RequestPredicate contentType(MediaType ... mediaTypes) {
        Assert.notEmpty((Object[])mediaTypes, (String)"'mediaTypes' must not be empty");
        return new ContentTypePredicate(mediaTypes);
    }

    public static RequestPredicate accept(MediaType ... mediaTypes) {
        Assert.notEmpty((Object[])mediaTypes, (String)"'mediaTypes' must not be empty");
        return new AcceptPredicate(mediaTypes);
    }

    public static RequestPredicate GET(String pattern) {
        return RequestPredicates.method(HttpMethod.GET).and(RequestPredicates.path(pattern));
    }

    public static RequestPredicate HEAD(String pattern) {
        return RequestPredicates.method(HttpMethod.HEAD).and(RequestPredicates.path(pattern));
    }

    public static RequestPredicate POST(String pattern) {
        return RequestPredicates.method(HttpMethod.POST).and(RequestPredicates.path(pattern));
    }

    public static RequestPredicate PUT(String pattern) {
        return RequestPredicates.method(HttpMethod.PUT).and(RequestPredicates.path(pattern));
    }

    public static RequestPredicate PATCH(String pattern) {
        return RequestPredicates.method(HttpMethod.PATCH).and(RequestPredicates.path(pattern));
    }

    public static RequestPredicate DELETE(String pattern) {
        return RequestPredicates.method(HttpMethod.DELETE).and(RequestPredicates.path(pattern));
    }

    public static RequestPredicate OPTIONS(String pattern) {
        return RequestPredicates.method(HttpMethod.OPTIONS).and(RequestPredicates.path(pattern));
    }

    public static RequestPredicate pathExtension(String extension) {
        Assert.notNull((Object)extension, (String)"'extension' must not be null");
        return new PathExtensionPredicate(extension);
    }

    public static RequestPredicate pathExtension(Predicate<String> extensionPredicate) {
        return new PathExtensionPredicate(extensionPredicate);
    }

    public static RequestPredicate param(String name, String value) {
        return new ParamPredicate(name, value);
    }

    public static RequestPredicate param(String name, Predicate<String> predicate) {
        return new ParamPredicate(name, predicate);
    }

    private static void traceMatch(String prefix, Object desired, @Nullable Object actual, boolean match) {
        if (logger.isTraceEnabled()) {
            logger.trace((Object)String.format("%s \"%s\" %s against value \"%s\"", prefix, desired, match ? "matches" : "does not match", actual));
        }
    }

    private static void restoreAttributes(ServerRequest request, Map<String, Object> attributes) {
        request.attributes().clear();
        request.attributes().putAll(attributes);
    }

    private static Map<String, String> mergePathVariables(Map<String, String> oldVariables, Map<String, String> newVariables) {
        if (!newVariables.isEmpty()) {
            LinkedHashMap<String, String> mergedVariables = new LinkedHashMap<String, String>(oldVariables);
            mergedVariables.putAll(newVariables);
            return mergedVariables;
        }
        return oldVariables;
    }

    private static PathPattern mergePatterns(@Nullable PathPattern oldPattern, PathPattern newPattern) {
        if (oldPattern != null) {
            return oldPattern.combine(newPattern);
        }
        return newPattern;
    }

    private static class SubPathServerRequestWrapper
    implements ServerRequest {
        private final ServerRequest request;
        private RequestPath requestPath;
        private final Map<String, Object> attributes;

        public SubPathServerRequestWrapper(ServerRequest request, PathPattern.PathRemainingMatchInfo info, PathPattern pattern) {
            this.request = request;
            this.requestPath = SubPathServerRequestWrapper.requestPath(request.requestPath(), info);
            this.attributes = SubPathServerRequestWrapper.mergeAttributes(request, info.getUriVariables(), pattern);
        }

        private static RequestPath requestPath(RequestPath original, PathPattern.PathRemainingMatchInfo info) {
            StringBuilder contextPath = new StringBuilder(original.contextPath().value());
            contextPath.append(info.getPathMatched().value());
            int length = contextPath.length();
            if (length > 0 && contextPath.charAt(length - 1) == '/') {
                contextPath.setLength(length - 1);
            }
            return original.modifyContextPath(contextPath.toString());
        }

        private static Map<String, Object> mergeAttributes(ServerRequest request, Map<String, String> pathVariables, PathPattern pattern) {
            ConcurrentHashMap<String, Object> result = new ConcurrentHashMap<String, Object>(request.attributes());
            result.put(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestPredicates.mergePathVariables(request.pathVariables(), pathVariables));
            pattern = RequestPredicates.mergePatterns((PathPattern)request.attributes().get(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE), pattern);
            result.put(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE, pattern);
            return result;
        }

        @Override
        public HttpMethod method() {
            return this.request.method();
        }

        @Override
        public String methodName() {
            return this.request.methodName();
        }

        @Override
        public URI uri() {
            return this.request.uri();
        }

        @Override
        public UriBuilder uriBuilder() {
            return this.request.uriBuilder();
        }

        @Override
        public RequestPath requestPath() {
            return this.requestPath;
        }

        @Override
        public ServerRequest.Headers headers() {
            return this.request.headers();
        }

        @Override
        public MultiValueMap<String, Cookie> cookies() {
            return this.request.cookies();
        }

        @Override
        public Optional<InetSocketAddress> remoteAddress() {
            return this.request.remoteAddress();
        }

        @Override
        public List<HttpMessageConverter<?>> messageConverters() {
            return this.request.messageConverters();
        }

        @Override
        public <T> T body(Class<T> bodyType) throws ServletException, IOException {
            return this.request.body(bodyType);
        }

        @Override
        public <T> T body(ParameterizedTypeReference<T> bodyType) throws ServletException, IOException {
            return this.request.body(bodyType);
        }

        @Override
        public Optional<Object> attribute(String name) {
            return this.request.attribute(name);
        }

        @Override
        public Map<String, Object> attributes() {
            return this.attributes;
        }

        @Override
        public Optional<String> param(String name) {
            return this.request.param(name);
        }

        @Override
        public MultiValueMap<String, String> params() {
            return this.request.params();
        }

        @Override
        public MultiValueMap<String, Part> multipartData() throws IOException, ServletException {
            return this.request.multipartData();
        }

        @Override
        public Map<String, String> pathVariables() {
            return this.attributes.getOrDefault(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Collections.emptyMap());
        }

        @Override
        public HttpSession session() {
            return this.request.session();
        }

        @Override
        public Optional<Principal> principal() {
            return this.request.principal();
        }

        @Override
        public HttpServletRequest servletRequest() {
            return this.request.servletRequest();
        }

        @Override
        public Optional<ServerResponse> checkNotModified(Instant lastModified) {
            return this.request.checkNotModified(lastModified);
        }

        @Override
        public Optional<ServerResponse> checkNotModified(String etag) {
            return this.request.checkNotModified(etag);
        }

        @Override
        public Optional<ServerResponse> checkNotModified(Instant lastModified, String etag) {
            return this.request.checkNotModified(lastModified, etag);
        }

        public String toString() {
            return this.method() + " " + this.path();
        }
    }

    static class OrRequestPredicate
    implements RequestPredicate,
    ChangePathPatternParserVisitor.Target {
        private final RequestPredicate left;
        private final RequestPredicate right;

        public OrRequestPredicate(RequestPredicate left, RequestPredicate right) {
            Assert.notNull((Object)left, (String)"Left RequestPredicate must not be null");
            Assert.notNull((Object)right, (String)"Right RequestPredicate must not be null");
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean test(ServerRequest request) {
            HashMap<String, Object> oldAttributes = new HashMap<String, Object>(request.attributes());
            if (this.left.test(request)) {
                return true;
            }
            RequestPredicates.restoreAttributes(request, oldAttributes);
            if (this.right.test(request)) {
                return true;
            }
            RequestPredicates.restoreAttributes(request, oldAttributes);
            return false;
        }

        @Override
        public Optional<ServerRequest> nest(ServerRequest request) {
            Optional<ServerRequest> leftResult = this.left.nest(request);
            if (leftResult.isPresent()) {
                return leftResult;
            }
            return this.right.nest(request);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.startOr();
            this.left.accept(visitor);
            visitor.or();
            this.right.accept(visitor);
            visitor.endOr();
        }

        @Override
        public void changeParser(PathPatternParser parser) {
            if (this.left instanceof ChangePathPatternParserVisitor.Target) {
                ((ChangePathPatternParserVisitor.Target)((Object)this.left)).changeParser(parser);
            }
            if (this.right instanceof ChangePathPatternParserVisitor.Target) {
                ((ChangePathPatternParserVisitor.Target)((Object)this.right)).changeParser(parser);
            }
        }

        public String toString() {
            return String.format("(%s || %s)", this.left, this.right);
        }
    }

    static class NegateRequestPredicate
    implements RequestPredicate,
    ChangePathPatternParserVisitor.Target {
        private final RequestPredicate delegate;

        public NegateRequestPredicate(RequestPredicate delegate) {
            Assert.notNull((Object)delegate, (String)"Delegate must not be null");
            this.delegate = delegate;
        }

        @Override
        public boolean test(ServerRequest request) {
            boolean result;
            HashMap<String, Object> oldAttributes = new HashMap<String, Object>(request.attributes());
            boolean bl = result = !this.delegate.test(request);
            if (!result) {
                RequestPredicates.restoreAttributes(request, oldAttributes);
            }
            return result;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.startNegate();
            this.delegate.accept(visitor);
            visitor.endNegate();
        }

        @Override
        public void changeParser(PathPatternParser parser) {
            if (this.delegate instanceof ChangePathPatternParserVisitor.Target) {
                ((ChangePathPatternParserVisitor.Target)((Object)this.delegate)).changeParser(parser);
            }
        }

        public String toString() {
            return "!" + this.delegate.toString();
        }
    }

    static class AndRequestPredicate
    implements RequestPredicate,
    ChangePathPatternParserVisitor.Target {
        private final RequestPredicate left;
        private final RequestPredicate right;

        public AndRequestPredicate(RequestPredicate left, RequestPredicate right) {
            Assert.notNull((Object)left, (String)"Left RequestPredicate must not be null");
            Assert.notNull((Object)right, (String)"Right RequestPredicate must not be null");
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean test(ServerRequest request) {
            HashMap<String, Object> oldAttributes = new HashMap<String, Object>(request.attributes());
            if (this.left.test(request) && this.right.test(request)) {
                return true;
            }
            RequestPredicates.restoreAttributes(request, oldAttributes);
            return false;
        }

        @Override
        public Optional<ServerRequest> nest(ServerRequest request) {
            return this.left.nest(request).flatMap(this.right::nest);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.startAnd();
            this.left.accept(visitor);
            visitor.and();
            this.right.accept(visitor);
            visitor.endAnd();
        }

        @Override
        public void changeParser(PathPatternParser parser) {
            if (this.left instanceof ChangePathPatternParserVisitor.Target) {
                ((ChangePathPatternParserVisitor.Target)((Object)this.left)).changeParser(parser);
            }
            if (this.right instanceof ChangePathPatternParserVisitor.Target) {
                ((ChangePathPatternParserVisitor.Target)((Object)this.right)).changeParser(parser);
            }
        }

        public String toString() {
            return String.format("(%s && %s)", this.left, this.right);
        }
    }

    private static class ParamPredicate
    implements RequestPredicate {
        private final String name;
        private final Predicate<String> valuePredicate;
        @Nullable
        private final String value;

        public ParamPredicate(String name, Predicate<String> valuePredicate) {
            Assert.notNull((Object)name, (String)"Name must not be null");
            Assert.notNull(valuePredicate, (String)"Predicate must not be null");
            this.name = name;
            this.valuePredicate = valuePredicate;
            this.value = null;
        }

        public ParamPredicate(String name, String value) {
            Assert.notNull((Object)name, (String)"Name must not be null");
            Assert.notNull((Object)value, (String)"Value must not be null");
            this.name = name;
            this.valuePredicate = value::equals;
            this.value = value;
        }

        @Override
        public boolean test(ServerRequest request) {
            Optional<String> s = request.param(this.name);
            return s.filter(this.valuePredicate).isPresent();
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.param(this.name, this.value != null ? this.value : this.valuePredicate.toString());
        }

        public String toString() {
            return String.format("?%s %s", this.name, this.value != null ? this.value : this.valuePredicate);
        }
    }

    private static class PathExtensionPredicate
    implements RequestPredicate {
        private final Predicate<String> extensionPredicate;
        @Nullable
        private final String extension;

        public PathExtensionPredicate(Predicate<String> extensionPredicate) {
            Assert.notNull(extensionPredicate, (String)"Predicate must not be null");
            this.extensionPredicate = extensionPredicate;
            this.extension = null;
        }

        public PathExtensionPredicate(String extension) {
            Assert.notNull((Object)extension, (String)"Extension must not be null");
            this.extensionPredicate = s -> {
                boolean match = extension.equalsIgnoreCase((String)s);
                RequestPredicates.traceMatch("Extension", extension, s, match);
                return match;
            };
            this.extension = extension;
        }

        @Override
        public boolean test(ServerRequest request) {
            String pathExtension = UriUtils.extractFileExtension((String)request.path());
            return this.extensionPredicate.test(pathExtension);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.pathExtension(this.extension != null ? this.extension : this.extensionPredicate.toString());
        }

        public String toString() {
            return String.format("*.%s", this.extension != null ? this.extension : this.extensionPredicate);
        }
    }

    private static class AcceptPredicate
    extends HeadersPredicate {
        private final Set<MediaType> mediaTypes;

        public AcceptPredicate(MediaType ... mediaTypes) {
            this(new HashSet<MediaType>(Arrays.asList(mediaTypes)));
        }

        private AcceptPredicate(Set<MediaType> mediaTypes) {
            super((ServerRequest.Headers headers) -> {
                List<MediaType> acceptedMediaTypes = AcceptPredicate.acceptedMediaTypes(headers);
                boolean match = acceptedMediaTypes.stream().anyMatch(acceptedMediaType -> mediaTypes.stream().anyMatch(arg_0 -> ((MediaType)acceptedMediaType).isCompatibleWith(arg_0)));
                RequestPredicates.traceMatch("Accept", mediaTypes, acceptedMediaTypes, match);
                return match;
            });
            this.mediaTypes = mediaTypes;
        }

        @NonNull
        private static List<MediaType> acceptedMediaTypes(ServerRequest.Headers headers) {
            List<MediaType> acceptedMediaTypes = headers.accept();
            if (acceptedMediaTypes.isEmpty()) {
                acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
            } else {
                MediaType.sortBySpecificityAndQuality(acceptedMediaTypes);
            }
            return acceptedMediaTypes;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.header("Accept", this.mediaTypes.size() == 1 ? this.mediaTypes.iterator().next().toString() : this.mediaTypes.toString());
        }

        @Override
        public String toString() {
            return String.format("Accept: %s", this.mediaTypes.size() == 1 ? this.mediaTypes.iterator().next().toString() : this.mediaTypes.toString());
        }
    }

    private static class ContentTypePredicate
    extends HeadersPredicate {
        private final Set<MediaType> mediaTypes;

        public ContentTypePredicate(MediaType ... mediaTypes) {
            this(new HashSet<MediaType>(Arrays.asList(mediaTypes)));
        }

        private ContentTypePredicate(Set<MediaType> mediaTypes) {
            super((ServerRequest.Headers headers) -> {
                MediaType contentType = headers.contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
                boolean match = mediaTypes.stream().anyMatch(mediaType -> mediaType.includes(contentType));
                RequestPredicates.traceMatch("Content-Type", mediaTypes, contentType, match);
                return match;
            });
            this.mediaTypes = mediaTypes;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.header("Content-Type", this.mediaTypes.size() == 1 ? this.mediaTypes.iterator().next().toString() : this.mediaTypes.toString());
        }

        @Override
        public String toString() {
            return String.format("Content-Type: %s", this.mediaTypes.size() == 1 ? this.mediaTypes.iterator().next().toString() : this.mediaTypes.toString());
        }
    }

    private static class HeadersPredicate
    implements RequestPredicate {
        private final Predicate<ServerRequest.Headers> headersPredicate;

        public HeadersPredicate(Predicate<ServerRequest.Headers> headersPredicate) {
            Assert.notNull(headersPredicate, (String)"Predicate must not be null");
            this.headersPredicate = headersPredicate;
        }

        @Override
        public boolean test(ServerRequest request) {
            if (CorsUtils.isPreFlightRequest((HttpServletRequest)request.servletRequest())) {
                return true;
            }
            return this.headersPredicate.test(request.headers());
        }

        public String toString() {
            return this.headersPredicate.toString();
        }
    }

    private static class PathPatternPredicate
    implements RequestPredicate,
    ChangePathPatternParserVisitor.Target {
        private PathPattern pattern;

        public PathPatternPredicate(PathPattern pattern) {
            Assert.notNull((Object)pattern, (String)"'pattern' must not be null");
            this.pattern = pattern;
        }

        @Override
        public boolean test(ServerRequest request) {
            PathContainer pathContainer = request.requestPath().pathWithinApplication();
            PathPattern.PathMatchInfo info = this.pattern.matchAndExtract(pathContainer);
            RequestPredicates.traceMatch("Pattern", this.pattern.getPatternString(), request.path(), info != null);
            if (info != null) {
                PathPatternPredicate.mergeAttributes(request, info.getUriVariables(), this.pattern);
                return true;
            }
            return false;
        }

        private static void mergeAttributes(ServerRequest request, Map<String, String> variables, PathPattern pattern) {
            Map pathVariables = RequestPredicates.mergePathVariables(request.pathVariables(), variables);
            request.attributes().put(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Collections.unmodifiableMap(pathVariables));
            pattern = RequestPredicates.mergePatterns((PathPattern)request.attributes().get(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE), pattern);
            request.attributes().put(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE, pattern);
        }

        @Override
        public Optional<ServerRequest> nest(ServerRequest request) {
            return Optional.ofNullable(this.pattern.matchStartOfPath(request.requestPath().pathWithinApplication())).map(info -> new SubPathServerRequestWrapper(request, (PathPattern.PathRemainingMatchInfo)info, this.pattern));
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.path(this.pattern.getPatternString());
        }

        @Override
        public void changeParser(PathPatternParser parser) {
            String patternString = this.pattern.getPatternString();
            this.pattern = parser.parse(patternString);
        }

        public String toString() {
            return this.pattern.getPatternString();
        }
    }

    private static class HttpMethodPredicate
    implements RequestPredicate {
        private final Set<HttpMethod> httpMethods;

        public HttpMethodPredicate(HttpMethod httpMethod) {
            Assert.notNull((Object)httpMethod, (String)"HttpMethod must not be null");
            this.httpMethods = EnumSet.of(httpMethod);
        }

        public HttpMethodPredicate(HttpMethod ... httpMethods) {
            Assert.notEmpty((Object[])httpMethods, (String)"HttpMethods must not be empty");
            this.httpMethods = EnumSet.copyOf(Arrays.asList(httpMethods));
        }

        @Override
        public boolean test(ServerRequest request) {
            HttpMethod method = HttpMethodPredicate.method(request);
            boolean match = this.httpMethods.contains(method);
            RequestPredicates.traceMatch("Method", this.httpMethods, method, match);
            return match;
        }

        @Nullable
        private static HttpMethod method(ServerRequest request) {
            if (CorsUtils.isPreFlightRequest((HttpServletRequest)request.servletRequest())) {
                String accessControlRequestMethod = request.headers().firstHeader("Access-Control-Request-Method");
                return HttpMethod.resolve((String)accessControlRequestMethod);
            }
            return request.method();
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.method(Collections.unmodifiableSet(this.httpMethods));
        }

        public String toString() {
            if (this.httpMethods.size() == 1) {
                return this.httpMethods.iterator().next().toString();
            }
            return this.httpMethods.toString();
        }
    }

    public static interface Visitor {
        public void method(Set<HttpMethod> var1);

        public void path(String var1);

        public void pathExtension(String var1);

        public void header(String var1, String var2);

        public void param(String var1, String var2);

        public void startAnd();

        public void and();

        public void endAnd();

        public void startOr();

        public void or();

        public void endOr();

        public void startNegate();

        public void endNegate();

        public void unknown(RequestPredicate var1);
    }
}

