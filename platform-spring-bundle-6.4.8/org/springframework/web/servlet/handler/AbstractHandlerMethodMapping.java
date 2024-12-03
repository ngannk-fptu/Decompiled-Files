/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy;
import org.springframework.web.util.pattern.PathPatternParser;

public abstract class AbstractHandlerMethodMapping<T>
extends AbstractHandlerMapping
implements InitializingBean {
    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";
    private static final HandlerMethod PREFLIGHT_AMBIGUOUS_MATCH = new HandlerMethod(new EmptyHandler(), ClassUtils.getMethod(EmptyHandler.class, "handle", new Class[0]));
    private static final CorsConfiguration ALLOW_CORS_CONFIG = new CorsConfiguration();
    private boolean detectHandlerMethodsInAncestorContexts = false;
    @Nullable
    private HandlerMethodMappingNamingStrategy<T> namingStrategy;
    private final MappingRegistry mappingRegistry = new MappingRegistry();

    @Override
    public void setPatternParser(PathPatternParser patternParser) {
        Assert.state(this.mappingRegistry.getRegistrations().isEmpty(), "PathPatternParser must be set before the initialization of request mappings through InitializingBean#afterPropertiesSet.");
        super.setPatternParser(patternParser);
    }

    public void setDetectHandlerMethodsInAncestorContexts(boolean detectHandlerMethodsInAncestorContexts) {
        this.detectHandlerMethodsInAncestorContexts = detectHandlerMethodsInAncestorContexts;
    }

    public void setHandlerMethodMappingNamingStrategy(HandlerMethodMappingNamingStrategy<T> namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    @Nullable
    public HandlerMethodMappingNamingStrategy<T> getNamingStrategy() {
        return this.namingStrategy;
    }

    public Map<T, HandlerMethod> getHandlerMethods() {
        this.mappingRegistry.acquireReadLock();
        try {
            Map<Object, HandlerMethod> map = Collections.unmodifiableMap(this.mappingRegistry.getRegistrations().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ((MappingRegistration)entry.getValue()).handlerMethod)));
            return map;
        }
        finally {
            this.mappingRegistry.releaseReadLock();
        }
    }

    @Nullable
    public List<HandlerMethod> getHandlerMethodsForMappingName(String mappingName) {
        return this.mappingRegistry.getHandlerMethodsByMappingName(mappingName);
    }

    MappingRegistry getMappingRegistry() {
        return this.mappingRegistry;
    }

    public void registerMapping(T mapping, Object handler, Method method) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Register \"" + mapping + "\" to " + method.toGenericString()));
        }
        this.mappingRegistry.register(mapping, handler, method);
    }

    public void unregisterMapping(T mapping) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Unregister mapping \"" + mapping + "\""));
        }
        this.mappingRegistry.unregister(mapping);
    }

    @Override
    public void afterPropertiesSet() {
        this.initHandlerMethods();
    }

    protected void initHandlerMethods() {
        for (String beanName : this.getCandidateBeanNames()) {
            if (beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) continue;
            this.processCandidateBean(beanName);
        }
        this.handlerMethodsInitialized(this.getHandlerMethods());
    }

    protected String[] getCandidateBeanNames() {
        return this.detectHandlerMethodsInAncestorContexts ? BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)this.obtainApplicationContext(), Object.class) : this.obtainApplicationContext().getBeanNamesForType(Object.class);
    }

    protected void processCandidateBean(String beanName) {
        Class<?> beanType;
        block3: {
            beanType = null;
            try {
                beanType = this.obtainApplicationContext().getType(beanName);
            }
            catch (Throwable ex) {
                if (!this.logger.isTraceEnabled()) break block3;
                this.logger.trace((Object)("Could not resolve type for bean '" + beanName + "'"), ex);
            }
        }
        if (beanType != null && this.isHandler(beanType)) {
            this.detectHandlerMethods(beanName);
        }
    }

    protected void detectHandlerMethods(Object handler) {
        Class<?> handlerType;
        Class<?> clazz = handlerType = handler instanceof String ? this.obtainApplicationContext().getType((String)handler) : handler.getClass();
        if (handlerType != null) {
            Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, Object> methods = MethodIntrospector.selectMethods(userType, method -> {
                try {
                    return this.getMappingForMethod(method, userType);
                }
                catch (Throwable ex) {
                    throw new IllegalStateException("Invalid mapping on handler class [" + userType.getName() + "]: " + method, ex);
                }
            });
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)this.formatMappings(userType, methods));
            } else if (this.mappingsLogger.isDebugEnabled()) {
                this.mappingsLogger.debug((Object)this.formatMappings(userType, methods));
            }
            methods.forEach((method, mapping) -> {
                Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
                this.registerHandlerMethod(handler, invocableMethod, mapping);
            });
        }
    }

    private String formatMappings(Class<?> userType, Map<Method, T> methods) {
        String packageName = ClassUtils.getPackageName(userType);
        String formattedType = StringUtils.hasText(packageName) ? Arrays.stream(packageName.split("\\.")).map(packageSegment -> packageSegment.substring(0, 1)).collect(Collectors.joining(".", "", "." + userType.getSimpleName())) : userType.getSimpleName();
        Function<Method, String> methodFormatter = method -> Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(",", "(", ")"));
        return methods.entrySet().stream().map(e -> {
            Method method = (Method)e.getKey();
            return e.getValue() + ": " + method.getName() + (String)methodFormatter.apply(method);
        }).collect(Collectors.joining("\n\t", "\n\t" + formattedType + ":\n\t", ""));
    }

    protected void registerHandlerMethod(Object handler, Method method, T mapping) {
        this.mappingRegistry.register(mapping, handler, method);
    }

    protected HandlerMethod createHandlerMethod(Object handler, Method method) {
        if (handler instanceof String) {
            return new HandlerMethod((String)handler, this.obtainApplicationContext().getAutowireCapableBeanFactory(), this.obtainApplicationContext(), method);
        }
        return new HandlerMethod(handler, method);
    }

    @Nullable
    protected CorsConfiguration initCorsConfiguration(Object handler, Method method, T mapping) {
        return null;
    }

    protected void handlerMethodsInitialized(Map<T, HandlerMethod> handlerMethods) {
        int total = handlerMethods.size();
        if (this.logger.isTraceEnabled() && total == 0 || this.logger.isDebugEnabled() && total > 0) {
            this.logger.debug((Object)(total + " mappings in " + this.formatMappingName()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = this.initLookupPath(request);
        this.mappingRegistry.acquireReadLock();
        try {
            HandlerMethod handlerMethod = this.lookupHandlerMethod(lookupPath, request);
            HandlerMethod handlerMethod2 = handlerMethod != null ? handlerMethod.createWithResolvedBean() : null;
            return handlerMethod2;
        }
        finally {
            this.mappingRegistry.releaseReadLock();
        }
    }

    @Nullable
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        ArrayList<Match> matches = new ArrayList<Match>();
        List directPathMatches = this.mappingRegistry.getMappingsByDirectPath(lookupPath);
        if (directPathMatches != null) {
            this.addMatchingMappings(directPathMatches, matches, request);
        }
        if (matches.isEmpty()) {
            this.addMatchingMappings(this.mappingRegistry.getRegistrations().keySet(), matches, request);
        }
        if (!matches.isEmpty()) {
            Match bestMatch = (Match)matches.get(0);
            if (matches.size() > 1) {
                MatchComparator comparator = new MatchComparator(this.getMappingComparator(request));
                matches.sort(comparator);
                bestMatch = (Match)matches.get(0);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)(matches.size() + " matching mappings: " + matches));
                }
                if (CorsUtils.isPreFlightRequest(request)) {
                    for (Match match : matches) {
                        if (!match.hasCorsConfig()) continue;
                        return PREFLIGHT_AMBIGUOUS_MATCH;
                    }
                } else {
                    Match secondBestMatch = (Match)matches.get(1);
                    if (comparator.compare(bestMatch, secondBestMatch) == 0) {
                        Method m1 = bestMatch.getHandlerMethod().getMethod();
                        Method m2 = secondBestMatch.getHandlerMethod().getMethod();
                        String uri = request.getRequestURI();
                        throw new IllegalStateException("Ambiguous handler methods mapped for '" + uri + "': {" + m1 + ", " + m2 + "}");
                    }
                }
            }
            request.setAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE, (Object)bestMatch.getHandlerMethod());
            this.handleMatch(bestMatch.mapping, lookupPath, request);
            return bestMatch.getHandlerMethod();
        }
        return this.handleNoMatch(this.mappingRegistry.getRegistrations().keySet(), lookupPath, request);
    }

    private void addMatchingMappings(Collection<T> mappings, List<Match> matches, HttpServletRequest request) {
        for (T mapping : mappings) {
            T match = this.getMatchingMapping(mapping, request);
            if (match == null) continue;
            matches.add(new Match(match, this.mappingRegistry.getRegistrations().get(mapping)));
        }
    }

    protected void handleMatch(T mapping, String lookupPath, HttpServletRequest request) {
        request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, (Object)lookupPath);
    }

    @Nullable
    protected HandlerMethod handleNoMatch(Set<T> mappings, String lookupPath, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    protected boolean hasCorsConfigurationSource(Object handler) {
        return super.hasCorsConfigurationSource(handler) || handler instanceof HandlerMethod && this.mappingRegistry.getCorsConfiguration((HandlerMethod)handler) != null;
    }

    @Override
    protected CorsConfiguration getCorsConfiguration(Object handler, HttpServletRequest request) {
        CorsConfiguration corsConfig = super.getCorsConfiguration(handler, request);
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            if (handlerMethod.equals(PREFLIGHT_AMBIGUOUS_MATCH)) {
                return ALLOW_CORS_CONFIG;
            }
            CorsConfiguration corsConfigFromMethod = this.mappingRegistry.getCorsConfiguration(handlerMethod);
            corsConfig = corsConfig != null ? corsConfig.combine(corsConfigFromMethod) : corsConfigFromMethod;
        }
        return corsConfig;
    }

    protected abstract boolean isHandler(Class<?> var1);

    @Nullable
    protected abstract T getMappingForMethod(Method var1, Class<?> var2);

    @Deprecated
    protected Set<String> getMappingPathPatterns(T mapping) {
        return Collections.emptySet();
    }

    protected Set<String> getDirectPaths(T mapping) {
        HashSet<String> urls = Collections.emptySet();
        for (String path : this.getMappingPathPatterns(mapping)) {
            if (this.getPathMatcher().isPattern(path)) continue;
            urls = urls.isEmpty() ? new HashSet<String>(1) : urls;
            urls.add(path);
        }
        return urls;
    }

    @Nullable
    protected abstract T getMatchingMapping(T var1, HttpServletRequest var2);

    protected abstract Comparator<T> getMappingComparator(HttpServletRequest var1);

    static {
        ALLOW_CORS_CONFIG.addAllowedOriginPattern("*");
        ALLOW_CORS_CONFIG.addAllowedMethod("*");
        ALLOW_CORS_CONFIG.addAllowedHeader("*");
        ALLOW_CORS_CONFIG.setAllowCredentials(true);
    }

    private static class EmptyHandler {
        private EmptyHandler() {
        }

        public void handle() {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    private class MatchComparator
    implements Comparator<Match> {
        private final Comparator<T> comparator;

        public MatchComparator(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(Match match1, Match match2) {
            return this.comparator.compare(match1.mapping, match2.mapping);
        }
    }

    private class Match {
        private final T mapping;
        private final MappingRegistration<T> registration;

        public Match(T mapping, MappingRegistration<T> registration) {
            this.mapping = mapping;
            this.registration = registration;
        }

        public HandlerMethod getHandlerMethod() {
            return this.registration.getHandlerMethod();
        }

        public boolean hasCorsConfig() {
            return this.registration.hasCorsConfig();
        }

        public String toString() {
            return this.mapping.toString();
        }
    }

    static class MappingRegistration<T> {
        private final T mapping;
        private final HandlerMethod handlerMethod;
        private final Set<String> directPaths;
        @Nullable
        private final String mappingName;
        private final boolean corsConfig;

        public MappingRegistration(T mapping, HandlerMethod handlerMethod, @Nullable Set<String> directPaths, @Nullable String mappingName, boolean corsConfig) {
            Assert.notNull(mapping, "Mapping must not be null");
            Assert.notNull((Object)handlerMethod, "HandlerMethod must not be null");
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
            this.directPaths = directPaths != null ? directPaths : Collections.emptySet();
            this.mappingName = mappingName;
            this.corsConfig = corsConfig;
        }

        public T getMapping() {
            return this.mapping;
        }

        public HandlerMethod getHandlerMethod() {
            return this.handlerMethod;
        }

        public Set<String> getDirectPaths() {
            return this.directPaths;
        }

        @Nullable
        public String getMappingName() {
            return this.mappingName;
        }

        public boolean hasCorsConfig() {
            return this.corsConfig;
        }
    }

    class MappingRegistry {
        private final Map<T, MappingRegistration<T>> registry = new HashMap();
        private final MultiValueMap<String, T> pathLookup = new LinkedMultiValueMap();
        private final Map<String, List<HandlerMethod>> nameLookup = new ConcurrentHashMap<String, List<HandlerMethod>>();
        private final Map<HandlerMethod, CorsConfiguration> corsLookup = new ConcurrentHashMap<HandlerMethod, CorsConfiguration>();
        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        MappingRegistry() {
        }

        public Map<T, MappingRegistration<T>> getRegistrations() {
            return this.registry;
        }

        @Nullable
        public List<T> getMappingsByDirectPath(String urlPath) {
            return (List)this.pathLookup.get(urlPath);
        }

        public List<HandlerMethod> getHandlerMethodsByMappingName(String mappingName) {
            return this.nameLookup.get(mappingName);
        }

        @Nullable
        public CorsConfiguration getCorsConfiguration(HandlerMethod handlerMethod) {
            HandlerMethod original = handlerMethod.getResolvedFromHandlerMethod();
            return this.corsLookup.get(original != null ? original : handlerMethod);
        }

        public void acquireReadLock() {
            this.readWriteLock.readLock().lock();
        }

        public void releaseReadLock() {
            this.readWriteLock.readLock().unlock();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void register(T mapping, Object handler, Method method) {
            this.readWriteLock.writeLock().lock();
            try {
                CorsConfiguration corsConfig;
                HandlerMethod handlerMethod = AbstractHandlerMethodMapping.this.createHandlerMethod(handler, method);
                this.validateMethodMapping(handlerMethod, mapping);
                Set<String> directPaths = AbstractHandlerMethodMapping.this.getDirectPaths(mapping);
                for (String path : directPaths) {
                    this.pathLookup.add(path, mapping);
                }
                String name = null;
                if (AbstractHandlerMethodMapping.this.getNamingStrategy() != null) {
                    name = AbstractHandlerMethodMapping.this.getNamingStrategy().getName(handlerMethod, mapping);
                    this.addMappingName(name, handlerMethod);
                }
                if ((corsConfig = AbstractHandlerMethodMapping.this.initCorsConfiguration(handler, method, mapping)) != null) {
                    corsConfig.validateAllowCredentials();
                    this.corsLookup.put(handlerMethod, corsConfig);
                }
                this.registry.put(mapping, new MappingRegistration(mapping, handlerMethod, directPaths, name, corsConfig != null));
            }
            finally {
                this.readWriteLock.writeLock().unlock();
            }
        }

        private void validateMethodMapping(HandlerMethod handlerMethod, T mapping) {
            HandlerMethod existingHandlerMethod;
            MappingRegistration registration = this.registry.get(mapping);
            HandlerMethod handlerMethod2 = existingHandlerMethod = registration != null ? registration.getHandlerMethod() : null;
            if (existingHandlerMethod != null && !existingHandlerMethod.equals(handlerMethod)) {
                throw new IllegalStateException("Ambiguous mapping. Cannot map '" + handlerMethod.getBean() + "' method \n" + handlerMethod + "\nto " + mapping + ": There is already '" + existingHandlerMethod.getBean() + "' bean method\n" + existingHandlerMethod + " mapped.");
            }
        }

        private void addMappingName(String name, HandlerMethod handlerMethod) {
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                oldList = Collections.emptyList();
            }
            for (HandlerMethod current : oldList) {
                if (!handlerMethod.equals(current)) continue;
                return;
            }
            ArrayList<HandlerMethod> newList = new ArrayList<HandlerMethod>(oldList.size() + 1);
            newList.addAll(oldList);
            newList.add(handlerMethod);
            this.nameLookup.put(name, newList);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void unregister(T mapping) {
            this.readWriteLock.writeLock().lock();
            try {
                MappingRegistration registration = this.registry.remove(mapping);
                if (registration == null) {
                    return;
                }
                for (String path : registration.getDirectPaths()) {
                    List mappings = (List)this.pathLookup.get(path);
                    if (mappings == null) continue;
                    mappings.remove(registration.getMapping());
                    if (!mappings.isEmpty()) continue;
                    this.pathLookup.remove(path);
                }
                this.removeMappingName(registration);
                this.corsLookup.remove(registration.getHandlerMethod());
            }
            finally {
                this.readWriteLock.writeLock().unlock();
            }
        }

        private void removeMappingName(MappingRegistration<T> definition) {
            String name = definition.getMappingName();
            if (name == null) {
                return;
            }
            HandlerMethod handlerMethod = definition.getHandlerMethod();
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                return;
            }
            if (oldList.size() <= 1) {
                this.nameLookup.remove(name);
                return;
            }
            ArrayList<HandlerMethod> newList = new ArrayList<HandlerMethod>(oldList.size() - 1);
            for (HandlerMethod current : oldList) {
                if (current.equals(handlerMethod)) continue;
                newList.add(current);
            }
            this.nameLookup.put(name, newList);
        }
    }
}

