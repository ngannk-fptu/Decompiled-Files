/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AnnotatedElementUtils {
    @Nullable
    private static final Boolean CONTINUE = null;
    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
    private static final Processor<Boolean> alwaysTrueAnnotationProcessor = new AlwaysTrueBooleanAnnotationProcessor();

    public static AnnotatedElement forAnnotations(final Annotation ... annotations) {
        return new AnnotatedElement(){

            @Override
            @Nullable
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                for (Annotation ann : annotations) {
                    if (ann.annotationType() != annotationClass) continue;
                    return (T)ann;
                }
                return null;
            }

            @Override
            public Annotation[] getAnnotations() {
                return annotations;
            }

            @Override
            public Annotation[] getDeclaredAnnotations() {
                return annotations;
            }
        };
    }

    public static Set<String> getMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return AnnotatedElementUtils.getMetaAnnotationTypes(element, element.getAnnotation(annotationType));
    }

    public static Set<String> getMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
        return AnnotatedElementUtils.getMetaAnnotationTypes(element, AnnotationUtils.getAnnotation(element, annotationName));
    }

    private static Set<String> getMetaAnnotationTypes(AnnotatedElement element, @Nullable Annotation composed) {
        if (composed == null) {
            return Collections.emptySet();
        }
        try {
            final LinkedHashSet<String> types = new LinkedHashSet<String>();
            AnnotatedElementUtils.searchWithGetSemantics(composed.annotationType(), null, null, null, new SimpleAnnotationProcessor<Object>(true){

                @Override
                @Nullable
                public Object process(@Nullable AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
                    types.add(annotation.annotationType().getName());
                    return CONTINUE;
                }
            }, new HashSet<AnnotatedElement>(), 1);
            return types;
        }
        catch (Throwable ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex);
            throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
        }
    }

    public static boolean hasMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return AnnotatedElementUtils.hasMetaAnnotationTypes(element, annotationType, null);
    }

    public static boolean hasMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
        return AnnotatedElementUtils.hasMetaAnnotationTypes(element, null, annotationName);
    }

    private static boolean hasMetaAnnotationTypes(AnnotatedElement element, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName) {
        return Boolean.TRUE.equals(AnnotatedElementUtils.searchWithGetSemantics(element, annotationType, annotationName, new SimpleAnnotationProcessor<Boolean>(){

            @Override
            @Nullable
            public Boolean process(@Nullable AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
                return metaDepth > 0 ? Boolean.TRUE : CONTINUE;
            }
        }));
    }

    public static boolean isAnnotated(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        if (element.isAnnotationPresent(annotationType)) {
            return true;
        }
        return Boolean.TRUE.equals(AnnotatedElementUtils.searchWithGetSemantics(element, annotationType, null, alwaysTrueAnnotationProcessor));
    }

    public static boolean isAnnotated(AnnotatedElement element, String annotationName) {
        return Boolean.TRUE.equals(AnnotatedElementUtils.searchWithGetSemantics(element, null, annotationName, alwaysTrueAnnotationProcessor));
    }

    @Nullable
    public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        AnnotationAttributes attributes = AnnotatedElementUtils.searchWithGetSemantics(element, annotationType, null, new MergedAnnotationAttributesProcessor());
        AnnotationUtils.postProcessAnnotationAttributes(element, attributes, false, false);
        return attributes;
    }

    @Nullable
    public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, String annotationName) {
        return AnnotatedElementUtils.getMergedAnnotationAttributes(element, annotationName, false, false);
    }

    @Nullable
    public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        AnnotationAttributes attributes = AnnotatedElementUtils.searchWithGetSemantics(element, null, annotationName, new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
        AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString, nestedAnnotationsAsMap);
        return attributes;
    }

    @Nullable
    public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
        A annotation = element.getDeclaredAnnotation(annotationType);
        if (annotation != null) {
            return AnnotationUtils.synthesizeAnnotation(annotation, element);
        }
        AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(element, annotationType);
        return attributes != null ? (A)AnnotationUtils.synthesizeAnnotation(attributes, annotationType, element) : null;
    }

    public static <A extends Annotation> Set<A> getAllMergedAnnotations(AnnotatedElement element, Class<A> annotationType) {
        MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
        AnnotatedElementUtils.searchWithGetSemantics(element, annotationType, null, processor);
        return AnnotatedElementUtils.postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
    }

    public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType) {
        return AnnotatedElementUtils.getMergedRepeatableAnnotations(element, annotationType, null);
    }

    public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType, @Nullable Class<? extends Annotation> containerType) {
        if (containerType == null) {
            containerType = AnnotatedElementUtils.resolveContainerType(annotationType);
        } else {
            AnnotatedElementUtils.validateContainerType(annotationType, containerType);
        }
        MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
        AnnotatedElementUtils.searchWithGetSemantics(element, annotationType, null, containerType, processor);
        return AnnotatedElementUtils.postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
    }

    @Nullable
    public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element, String annotationName) {
        return AnnotatedElementUtils.getAllAnnotationAttributes(element, annotationName, false, false);
    }

    @Nullable
    public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element, String annotationName, final boolean classValuesAsString, final boolean nestedAnnotationsAsMap) {
        final LinkedMultiValueMap<String, Object> attributesMap = new LinkedMultiValueMap<String, Object>();
        AnnotatedElementUtils.searchWithGetSemantics(element, null, annotationName, new SimpleAnnotationProcessor<Object>(){

            @Override
            @Nullable
            public Object process(@Nullable AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
                AnnotationAttributes annotationAttributes = AnnotationUtils.getAnnotationAttributes(annotation, classValuesAsString, nestedAnnotationsAsMap);
                annotationAttributes.forEach(attributesMap::add);
                return CONTINUE;
            }
        });
        return !attributesMap.isEmpty() ? attributesMap : null;
    }

    public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        if (element.isAnnotationPresent(annotationType)) {
            return true;
        }
        return Boolean.TRUE.equals(AnnotatedElementUtils.searchWithFindSemantics(element, annotationType, null, alwaysTrueAnnotationProcessor));
    }

    @Nullable
    public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element, Class<? extends Annotation> annotationType, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        AnnotationAttributes attributes = AnnotatedElementUtils.searchWithFindSemantics(element, annotationType, null, new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
        AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString, nestedAnnotationsAsMap);
        return attributes;
    }

    @Nullable
    public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element, String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        AnnotationAttributes attributes = AnnotatedElementUtils.searchWithFindSemantics(element, null, annotationName, new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
        AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString, nestedAnnotationsAsMap);
        return attributes;
    }

    @Nullable
    public static <A extends Annotation> A findMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
        A annotation = element.getDeclaredAnnotation(annotationType);
        if (annotation != null) {
            return AnnotationUtils.synthesizeAnnotation(annotation, element);
        }
        AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(element, annotationType, false, false);
        return attributes != null ? (A)AnnotationUtils.synthesizeAnnotation(attributes, annotationType, element) : null;
    }

    public static <A extends Annotation> Set<A> findAllMergedAnnotations(AnnotatedElement element, Class<A> annotationType) {
        MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
        AnnotatedElementUtils.searchWithFindSemantics(element, annotationType, null, processor);
        return AnnotatedElementUtils.postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
    }

    public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType) {
        return AnnotatedElementUtils.findMergedRepeatableAnnotations(element, annotationType, null);
    }

    public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType, @Nullable Class<? extends Annotation> containerType) {
        if (containerType == null) {
            containerType = AnnotatedElementUtils.resolveContainerType(annotationType);
        } else {
            AnnotatedElementUtils.validateContainerType(annotationType, containerType);
        }
        MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
        AnnotatedElementUtils.searchWithFindSemantics(element, annotationType, null, containerType, processor);
        return AnnotatedElementUtils.postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
    }

    @Nullable
    private static <T> T searchWithGetSemantics(AnnotatedElement element, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName, Processor<T> processor) {
        return AnnotatedElementUtils.searchWithGetSemantics(element, annotationType, annotationName, null, processor);
    }

    @Nullable
    private static <T> T searchWithGetSemantics(AnnotatedElement element, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor) {
        try {
            return AnnotatedElementUtils.searchWithGetSemantics(element, annotationType, annotationName, containerType, processor, new HashSet<AnnotatedElement>(), 0);
        }
        catch (Throwable ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex);
            throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
        }
    }

    @Nullable
    private static <T> T searchWithGetSemantics(AnnotatedElement element, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor, Set<AnnotatedElement> visited, int metaDepth) {
        if (visited.add(element)) {
            try {
                Class superclass;
                List<Annotation> declaredAnnotations = Arrays.asList(element.getDeclaredAnnotations());
                T result = AnnotatedElementUtils.searchWithGetSemanticsInAnnotations(element, declaredAnnotations, annotationType, annotationName, containerType, processor, visited, metaDepth);
                if (result != null) {
                    return result;
                }
                if (element instanceof Class && (superclass = ((Class)element).getSuperclass()) != null && superclass != Object.class) {
                    LinkedList<Annotation> inheritedAnnotations = new LinkedList<Annotation>();
                    for (Annotation annotation : element.getAnnotations()) {
                        if (declaredAnnotations.contains(annotation)) continue;
                        inheritedAnnotations.add(annotation);
                    }
                    result = AnnotatedElementUtils.searchWithGetSemanticsInAnnotations(element, inheritedAnnotations, annotationType, annotationName, containerType, processor, visited, metaDepth);
                    if (result != null) {
                        return result;
                    }
                }
            }
            catch (Throwable ex) {
                AnnotationUtils.handleIntrospectionFailure(element, ex);
            }
        }
        return null;
    }

    @Nullable
    private static <T> T searchWithGetSemanticsInAnnotations(@Nullable AnnotatedElement element, List<Annotation> annotations, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor, Set<AnnotatedElement> visited, int metaDepth) {
        Object result;
        Class<? extends Annotation> currentAnnotationType;
        for (Annotation annotation : annotations) {
            currentAnnotationType = annotation.annotationType();
            if (AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) continue;
            if (currentAnnotationType == annotationType || currentAnnotationType.getName().equals(annotationName) || processor.alwaysProcesses()) {
                result = processor.process(element, annotation, metaDepth);
                if (result == null) continue;
                if (processor.aggregates() && metaDepth == 0) {
                    processor.getAggregatedResults().add(result);
                    continue;
                }
                return result;
            }
            if (currentAnnotationType != containerType) continue;
            result = AnnotatedElementUtils.getRawAnnotationsFromContainer((AnnotatedElement)element, (Annotation)annotation);
            int n = ((T)result).length;
            for (int i = 0; i < n; ++i) {
                T contained = result[i];
                T result2 = processor.process(element, (Annotation)contained, metaDepth);
                if (result2 == null) continue;
                processor.getAggregatedResults().add(result2);
            }
        }
        for (Annotation annotation : annotations) {
            currentAnnotationType = annotation.annotationType();
            if (!AnnotatedElementUtils.hasSearchableMetaAnnotations(currentAnnotationType, annotationType, annotationName) || (result = AnnotatedElementUtils.searchWithGetSemantics(currentAnnotationType, annotationType, annotationName, containerType, processor, visited, metaDepth + 1)) == null) continue;
            processor.postProcess(element, annotation, result);
            if (processor.aggregates() && metaDepth == 0) {
                processor.getAggregatedResults().add(result);
                continue;
            }
            return result;
        }
        return null;
    }

    @Nullable
    private static <T> T searchWithFindSemantics(AnnotatedElement element, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName, Processor<T> processor) {
        return AnnotatedElementUtils.searchWithFindSemantics(element, annotationType, annotationName, null, processor);
    }

    @Nullable
    private static <T> T searchWithFindSemantics(AnnotatedElement element, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor) {
        if (containerType != null && !processor.aggregates()) {
            throw new IllegalArgumentException("Searches for repeatable annotations must supply an aggregating Processor");
        }
        try {
            return AnnotatedElementUtils.searchWithFindSemantics(element, annotationType, annotationName, containerType, processor, new HashSet<AnnotatedElement>(), 0);
        }
        catch (Throwable ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex);
            throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
        }
    }

    @Nullable
    private static <T> T searchWithFindSemantics(AnnotatedElement element, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor, Set<AnnotatedElement> visited, int metaDepth) {
        if (visited.add(element)) {
            try {
                Class clazz;
                Annotation[] annotations = element.getDeclaredAnnotations();
                if (annotations.length > 0) {
                    Object result;
                    Class<? extends Annotation> currentAnnotationType;
                    ArrayList<T> aggregatedResults = processor.aggregates() ? new ArrayList<T>() : null;
                    for (Annotation annotation : annotations) {
                        currentAnnotationType = annotation.annotationType();
                        if (AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) continue;
                        if (currentAnnotationType == annotationType || currentAnnotationType.getName().equals(annotationName) || processor.alwaysProcesses()) {
                            result = processor.process(element, annotation, metaDepth);
                            if (result == null) continue;
                            if (aggregatedResults != null && metaDepth == 0) {
                                aggregatedResults.add(result);
                                continue;
                            }
                            return result;
                        }
                        if (currentAnnotationType != containerType) continue;
                        result = AnnotatedElementUtils.getRawAnnotationsFromContainer((AnnotatedElement)element, (Annotation)annotation);
                        int n = ((T)result).length;
                        for (int i = 0; i < n; ++i) {
                            T contained = result[i];
                            T result2 = processor.process(element, (Annotation)contained, metaDepth);
                            if (aggregatedResults == null || result2 == null) continue;
                            aggregatedResults.add(result2);
                        }
                    }
                    Annotation[] annotationArray = annotations;
                    int n = annotationArray.length;
                    for (int i = 0; i < n; ++i) {
                        Annotation annotation;
                        annotation = annotationArray[i];
                        currentAnnotationType = annotation.annotationType();
                        if (!AnnotatedElementUtils.hasSearchableMetaAnnotations(currentAnnotationType, annotationType, annotationName) || (result = AnnotatedElementUtils.searchWithFindSemantics(currentAnnotationType, annotationType, annotationName, containerType, processor, visited, metaDepth + 1)) == null) continue;
                        processor.postProcess(currentAnnotationType, annotation, result);
                        if (aggregatedResults != null && metaDepth == 0) {
                            aggregatedResults.add(result);
                            continue;
                        }
                        return result;
                    }
                    if (!CollectionUtils.isEmpty(aggregatedResults)) {
                        processor.getAggregatedResults().addAll(0, aggregatedResults);
                    }
                }
                if (element instanceof Method) {
                    T t;
                    T t2;
                    Method method = (Method)element;
                    Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
                    if (resolvedMethod != method && (t2 = AnnotatedElementUtils.searchWithFindSemantics(resolvedMethod, annotationType, annotationName, containerType, processor, visited, metaDepth)) != null) {
                        return t2;
                    }
                    Class<?>[] ifcs = method.getDeclaringClass().getInterfaces();
                    if (ifcs.length > 0 && (t = AnnotatedElementUtils.searchOnInterfaces(method, annotationType, annotationName, containerType, processor, visited, metaDepth, ifcs)) != null) {
                        return t;
                    }
                    Class<?> clazz2 = method.getDeclaringClass();
                    while ((clazz2 = clazz2.getSuperclass()) != null && clazz2 != Object.class) {
                        T t3;
                        Set<Method> annotatedMethods = AnnotationUtils.getAnnotatedMethodsInBaseType(clazz2);
                        if (!annotatedMethods.isEmpty()) {
                            for (Method annotatedMethod : annotatedMethods) {
                                Method resolvedSuperMethod;
                                T t4;
                                if (!AnnotationUtils.isOverride(method, annotatedMethod) || (t4 = AnnotatedElementUtils.searchWithFindSemantics(resolvedSuperMethod = BridgeMethodResolver.findBridgedMethod(annotatedMethod), annotationType, annotationName, containerType, processor, visited, metaDepth)) == null) continue;
                                return t4;
                            }
                        }
                        if ((t3 = AnnotatedElementUtils.searchOnInterfaces(method, annotationType, annotationName, containerType, processor, visited, metaDepth, clazz2.getInterfaces())) == null) continue;
                        return t3;
                    }
                } else if (element instanceof Class && !Annotation.class.isAssignableFrom(clazz = (Class)element)) {
                    T result;
                    for (Class<?> ifc : clazz.getInterfaces()) {
                        T result3 = AnnotatedElementUtils.searchWithFindSemantics(ifc, annotationType, annotationName, containerType, processor, visited, metaDepth);
                        if (result3 == null) continue;
                        return result3;
                    }
                    Class clazz2 = clazz.getSuperclass();
                    if (clazz2 != null && clazz2 != Object.class && (result = AnnotatedElementUtils.searchWithFindSemantics(clazz2, annotationType, annotationName, containerType, processor, visited, metaDepth)) != null) {
                        return result;
                    }
                }
            }
            catch (Throwable ex) {
                AnnotationUtils.handleIntrospectionFailure(element, ex);
            }
        }
        return null;
    }

    @Nullable
    private static <T> T searchOnInterfaces(Method method, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor, Set<AnnotatedElement> visited, int metaDepth, Class<?>[] ifcs) {
        for (Class<?> ifc : ifcs) {
            Set<Method> annotatedMethods = AnnotationUtils.getAnnotatedMethodsInBaseType(ifc);
            if (annotatedMethods.isEmpty()) continue;
            for (Method annotatedMethod : annotatedMethods) {
                T result;
                if (!AnnotationUtils.isOverride(method, annotatedMethod) || (result = AnnotatedElementUtils.searchWithFindSemantics(annotatedMethod, annotationType, annotationName, containerType, processor, visited, metaDepth)) == null) continue;
                return result;
            }
        }
        return null;
    }

    private static boolean hasSearchableMetaAnnotations(Class<? extends Annotation> currentAnnotationType, @Nullable Class<?> annotationType, @Nullable String annotationName) {
        if (AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) {
            return false;
        }
        if (currentAnnotationType == Nullable.class || currentAnnotationType.getName().startsWith("java")) {
            return annotationType != null && annotationType.getName().startsWith("java") || annotationName != null && annotationName.startsWith("java");
        }
        return true;
    }

    private static <A extends Annotation> A[] getRawAnnotationsFromContainer(@Nullable AnnotatedElement element, Annotation container) {
        try {
            Annotation[] value = (Annotation[])AnnotationUtils.getValue(container);
            if (value != null) {
                return value;
            }
        }
        catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(element, ex);
        }
        return EMPTY_ANNOTATION_ARRAY;
    }

    private static Class<? extends Annotation> resolveContainerType(Class<? extends Annotation> annotationType) {
        Class<? extends Annotation> containerType = AnnotationUtils.resolveContainerAnnotationType(annotationType);
        if (containerType == null) {
            throw new IllegalArgumentException("Annotation type must be a repeatable annotation: failed to resolve container type for " + annotationType.getName());
        }
        return containerType;
    }

    private static void validateContainerType(Class<? extends Annotation> annotationType, Class<? extends Annotation> containerType) {
        try {
            Method method = containerType.getDeclaredMethod("value", new Class[0]);
            Class<?> returnType = method.getReturnType();
            if (!returnType.isArray() || returnType.getComponentType() != annotationType) {
                String msg = String.format("Container type [%s] must declare a 'value' attribute for an array of type [%s]", containerType.getName(), annotationType.getName());
                throw new AnnotationConfigurationException(msg);
            }
        }
        catch (Throwable ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex);
            String msg = String.format("Invalid declaration of container type [%s] for repeatable annotation [%s]", containerType.getName(), annotationType.getName());
            throw new AnnotationConfigurationException(msg, ex);
        }
    }

    private static <A extends Annotation> Set<A> postProcessAndSynthesizeAggregatedResults(AnnotatedElement element, Class<A> annotationType, List<AnnotationAttributes> aggregatedResults) {
        LinkedHashSet<A> annotations = new LinkedHashSet<A>();
        for (AnnotationAttributes attributes : aggregatedResults) {
            AnnotationUtils.postProcessAnnotationAttributes(element, attributes, false, false);
            annotations.add(AnnotationUtils.synthesizeAnnotation(attributes, annotationType, element));
        }
        return annotations;
    }

    private static class MergedAnnotationAttributesProcessor
    implements Processor<AnnotationAttributes> {
        private final boolean classValuesAsString;
        private final boolean nestedAnnotationsAsMap;
        private final boolean aggregates;
        private final List<AnnotationAttributes> aggregatedResults;

        MergedAnnotationAttributesProcessor() {
            this(false, false, false);
        }

        MergedAnnotationAttributesProcessor(boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
            this(classValuesAsString, nestedAnnotationsAsMap, false);
        }

        MergedAnnotationAttributesProcessor(boolean classValuesAsString, boolean nestedAnnotationsAsMap, boolean aggregates) {
            this.classValuesAsString = classValuesAsString;
            this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
            this.aggregates = aggregates;
            this.aggregatedResults = aggregates ? new ArrayList() : Collections.emptyList();
        }

        @Override
        public boolean alwaysProcesses() {
            return false;
        }

        @Override
        public boolean aggregates() {
            return this.aggregates;
        }

        @Override
        public List<AnnotationAttributes> getAggregatedResults() {
            return this.aggregatedResults;
        }

        @Override
        @Nullable
        public AnnotationAttributes process(@Nullable AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
            return AnnotationUtils.retrieveAnnotationAttributes(annotatedElement, annotation, this.classValuesAsString, this.nestedAnnotationsAsMap);
        }

        @Override
        public void postProcess(@Nullable AnnotatedElement element, Annotation annotation, AnnotationAttributes attributes) {
            annotation = AnnotationUtils.synthesizeAnnotation(annotation, element);
            Class<? extends Annotation> targetAnnotationType = attributes.annotationType();
            HashSet<String> valuesAlreadyReplaced = new HashSet<String>();
            for (Method attributeMethod : AnnotationUtils.getAttributeMethods(annotation.annotationType())) {
                String attributeName = attributeMethod.getName();
                String attributeOverrideName = AnnotationUtils.getAttributeOverrideName(attributeMethod, targetAnnotationType);
                if (attributeOverrideName != null) {
                    if (valuesAlreadyReplaced.contains(attributeOverrideName)) continue;
                    ArrayList<String> targetAttributeNames = new ArrayList<String>();
                    targetAttributeNames.add(attributeOverrideName);
                    valuesAlreadyReplaced.add(attributeOverrideName);
                    List<String> aliases = AnnotationUtils.getAttributeAliasMap(targetAnnotationType).get(attributeOverrideName);
                    if (aliases != null) {
                        for (String alias : aliases) {
                            if (valuesAlreadyReplaced.contains(alias)) continue;
                            targetAttributeNames.add(alias);
                            valuesAlreadyReplaced.add(alias);
                        }
                    }
                    this.overrideAttributes(element, annotation, attributes, attributeName, targetAttributeNames);
                    continue;
                }
                if ("value".equals(attributeName) || !attributes.containsKey(attributeName)) continue;
                this.overrideAttribute(element, annotation, attributes, attributeName, attributeName);
            }
        }

        private void overrideAttributes(@Nullable AnnotatedElement element, Annotation annotation, AnnotationAttributes attributes, String sourceAttributeName, List<String> targetAttributeNames) {
            Object adaptedValue = this.getAdaptedValue(element, annotation, sourceAttributeName);
            for (String targetAttributeName : targetAttributeNames) {
                attributes.put(targetAttributeName, adaptedValue);
            }
        }

        private void overrideAttribute(@Nullable AnnotatedElement element, Annotation annotation, AnnotationAttributes attributes, String sourceAttributeName, String targetAttributeName) {
            attributes.put(targetAttributeName, this.getAdaptedValue(element, annotation, sourceAttributeName));
        }

        @Nullable
        private Object getAdaptedValue(@Nullable AnnotatedElement element, Annotation annotation, String sourceAttributeName) {
            Object value = AnnotationUtils.getValue(annotation, sourceAttributeName);
            return AnnotationUtils.adaptValue(element, value, this.classValuesAsString, this.nestedAnnotationsAsMap);
        }
    }

    static class AlwaysTrueBooleanAnnotationProcessor
    extends SimpleAnnotationProcessor<Boolean> {
        AlwaysTrueBooleanAnnotationProcessor() {
        }

        @Override
        public final Boolean process(@Nullable AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
            return Boolean.TRUE;
        }
    }

    private static abstract class SimpleAnnotationProcessor<T>
    implements Processor<T> {
        private final boolean alwaysProcesses;

        public SimpleAnnotationProcessor() {
            this(false);
        }

        public SimpleAnnotationProcessor(boolean alwaysProcesses) {
            this.alwaysProcesses = alwaysProcesses;
        }

        @Override
        public final boolean alwaysProcesses() {
            return this.alwaysProcesses;
        }

        @Override
        public final void postProcess(@Nullable AnnotatedElement annotatedElement, Annotation annotation, T result) {
        }

        @Override
        public final boolean aggregates() {
            return false;
        }

        @Override
        public final List<T> getAggregatedResults() {
            throw new UnsupportedOperationException("SimpleAnnotationProcessor does not support aggregated results");
        }
    }

    private static interface Processor<T> {
        @Nullable
        public T process(@Nullable AnnotatedElement var1, Annotation var2, int var3);

        public void postProcess(@Nullable AnnotatedElement var1, Annotation var2, T var3);

        public boolean alwaysProcesses();

        public boolean aggregates();

        public List<T> getAggregatedResults();
    }
}

