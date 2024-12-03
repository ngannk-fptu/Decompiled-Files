/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.ehcache.sizeof.filters;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.ehcache.sizeof.annotations.AnnotationProxyFactory;
import org.ehcache.sizeof.annotations.IgnoreSizeOf;
import org.ehcache.sizeof.filters.SizeOfFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AnnotationSizeOfFilter
implements SizeOfFilter {
    private static final String IGNORE_SIZE_OF_VM_ARGUMENT;
    private static final Logger LOG;
    private static final String IGNORE_SIZE_OF_DEFAULT_REGEXP = "^.*cache\\..*IgnoreSizeOf$";
    private static final Pattern IGNORE_SIZE_OF_PATTERN;

    @Override
    public Collection<Field> filterFields(Class<?> klazz, Collection<Field> fields) {
        Iterator<Field> it = fields.iterator();
        while (it.hasNext()) {
            Field field = it.next();
            IgnoreSizeOf annotationOnField = this.getAnnotationOn(field, IgnoreSizeOf.class, IGNORE_SIZE_OF_PATTERN);
            if (annotationOnField == null) continue;
            it.remove();
        }
        return fields;
    }

    @Override
    public boolean filterClass(Class<?> klazz) {
        boolean classAnnotated = this.isAnnotationPresentOrInherited(klazz);
        Package pack = klazz.getPackage();
        IgnoreSizeOf annotationOnPackage = pack == null ? null : this.getAnnotationOn(pack, IgnoreSizeOf.class, IGNORE_SIZE_OF_PATTERN);
        boolean packageAnnotated = annotationOnPackage != null;
        return !classAnnotated && !packageAnnotated;
    }

    private boolean isAnnotationPresentOrInherited(Class<?> instanceKlazz) {
        for (Class<?> klazz = instanceKlazz; klazz != null; klazz = klazz.getSuperclass()) {
            IgnoreSizeOf annotationOnClass = this.getAnnotationOn(klazz, IgnoreSizeOf.class, IGNORE_SIZE_OF_PATTERN);
            if (annotationOnClass == null || klazz != instanceKlazz && !annotationOnClass.inherited()) continue;
            return true;
        }
        return false;
    }

    private boolean validateCustomAnnotationPattern(String canonicalName, Pattern matchingAnnotationPattern) {
        Matcher matcher = matchingAnnotationPattern.matcher(canonicalName);
        boolean found = matcher.matches();
        if (found) {
            LOG.debug(canonicalName + " matched IgnoreSizeOf annotation pattern " + IGNORE_SIZE_OF_PATTERN);
        }
        return found;
    }

    private <T extends Annotation> T getAnnotationOn(AnnotatedElement element, Class<T> referenceAnnotation, Pattern matchingAnnotationPattern) {
        Annotation[] annotations;
        T matchingAnnotation = null;
        for (Annotation annotation : annotations = element.getAnnotations()) {
            if (!this.validateCustomAnnotationPattern(annotation.annotationType().getName(), matchingAnnotationPattern)) continue;
            if (matchingAnnotation != null) {
                throw new IllegalStateException("You are not allowed to use more than one @" + referenceAnnotation.getName() + " annotations for the same element : " + element.toString());
            }
            matchingAnnotation = AnnotationProxyFactory.getAnnotationProxy(annotation, referenceAnnotation);
        }
        return matchingAnnotation;
    }

    static {
        Pattern localPattern;
        IGNORE_SIZE_OF_VM_ARGUMENT = AnnotationSizeOfFilter.class.getName() + ".pattern";
        LOG = LoggerFactory.getLogger((String)AnnotationSizeOfFilter.class.getName());
        String ignoreSizeOfRegexpVMArg = System.getProperty(IGNORE_SIZE_OF_VM_ARGUMENT);
        String ignoreSizeOfRegexp = ignoreSizeOfRegexpVMArg != null ? ignoreSizeOfRegexpVMArg : IGNORE_SIZE_OF_DEFAULT_REGEXP;
        try {
            localPattern = Pattern.compile(ignoreSizeOfRegexp);
            LOG.info("Using regular expression provided through VM argument " + IGNORE_SIZE_OF_VM_ARGUMENT + " for IgnoreSizeOf annotation : " + ignoreSizeOfRegexp);
        }
        catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regular expression provided through VM argument " + IGNORE_SIZE_OF_VM_ARGUMENT + " : \n" + e.getMessage() + "\n using default regular expression for IgnoreSizeOf annotation : " + IGNORE_SIZE_OF_DEFAULT_REGEXP);
        }
        IGNORE_SIZE_OF_PATTERN = localPattern;
    }
}

