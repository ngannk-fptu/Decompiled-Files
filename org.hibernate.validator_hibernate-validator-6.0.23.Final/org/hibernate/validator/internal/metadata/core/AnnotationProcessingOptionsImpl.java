/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.core;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Member;
import java.util.Map;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class AnnotationProcessingOptionsImpl
implements AnnotationProcessingOptions {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Map<Class<?>, Boolean> ignoreAnnotationDefaults = CollectionHelper.newHashMap();
    private final Map<Class<?>, Boolean> annotationIgnoresForClasses = CollectionHelper.newHashMap();
    private final Map<Member, Boolean> annotationIgnoredForMembers = CollectionHelper.newHashMap();
    private final Map<Member, Boolean> annotationIgnoresForReturnValues = CollectionHelper.newHashMap();
    private final Map<Member, Boolean> annotationIgnoresForCrossParameter = CollectionHelper.newHashMap();
    private final Map<ExecutableParameterKey, Boolean> annotationIgnoresForMethodParameter = CollectionHelper.newHashMap();

    @Override
    public boolean areMemberConstraintsIgnoredFor(Member member) {
        Class<?> clazz = member.getDeclaringClass();
        if (this.annotationIgnoredForMembers.containsKey(member)) {
            return this.annotationIgnoredForMembers.get(member);
        }
        return this.areAllConstraintAnnotationsIgnoredFor(clazz);
    }

    @Override
    public boolean areReturnValueConstraintsIgnoredFor(Member member) {
        if (this.annotationIgnoresForReturnValues.containsKey(member)) {
            return this.annotationIgnoresForReturnValues.get(member);
        }
        return this.areMemberConstraintsIgnoredFor(member);
    }

    @Override
    public boolean areCrossParameterConstraintsIgnoredFor(Member member) {
        if (this.annotationIgnoresForCrossParameter.containsKey(member)) {
            return this.annotationIgnoresForCrossParameter.get(member);
        }
        return this.areMemberConstraintsIgnoredFor(member);
    }

    @Override
    public boolean areParameterConstraintsIgnoredFor(Member member, int index) {
        ExecutableParameterKey key = new ExecutableParameterKey(member, index);
        if (this.annotationIgnoresForMethodParameter.containsKey(key)) {
            return this.annotationIgnoresForMethodParameter.get(key);
        }
        return this.areMemberConstraintsIgnoredFor(member);
    }

    @Override
    public boolean areClassLevelConstraintsIgnoredFor(Class<?> clazz) {
        boolean ignoreAnnotation = this.annotationIgnoresForClasses.containsKey(clazz) ? this.annotationIgnoresForClasses.get(clazz).booleanValue() : this.areAllConstraintAnnotationsIgnoredFor(clazz);
        if (LOG.isDebugEnabled() && ignoreAnnotation) {
            LOG.debugf("Class level annotation are getting ignored for %s.", clazz.getName());
        }
        return ignoreAnnotation;
    }

    @Override
    public void merge(AnnotationProcessingOptions annotationProcessingOptions) {
        AnnotationProcessingOptionsImpl annotationProcessingOptionsImpl = (AnnotationProcessingOptionsImpl)annotationProcessingOptions;
        this.ignoreAnnotationDefaults.putAll(annotationProcessingOptionsImpl.ignoreAnnotationDefaults);
        this.annotationIgnoresForClasses.putAll(annotationProcessingOptionsImpl.annotationIgnoresForClasses);
        this.annotationIgnoredForMembers.putAll(annotationProcessingOptionsImpl.annotationIgnoredForMembers);
        this.annotationIgnoresForReturnValues.putAll(annotationProcessingOptionsImpl.annotationIgnoresForReturnValues);
        this.annotationIgnoresForCrossParameter.putAll(annotationProcessingOptionsImpl.annotationIgnoresForCrossParameter);
        this.annotationIgnoresForMethodParameter.putAll(annotationProcessingOptionsImpl.annotationIgnoresForMethodParameter);
    }

    public void ignoreAnnotationConstraintForClass(Class<?> clazz, Boolean b) {
        if (b == null) {
            this.ignoreAnnotationDefaults.put(clazz, Boolean.TRUE);
        } else {
            this.ignoreAnnotationDefaults.put(clazz, b);
        }
    }

    public void ignoreConstraintAnnotationsOnMember(Member member, Boolean b) {
        this.annotationIgnoredForMembers.put(member, b);
    }

    public void ignoreConstraintAnnotationsForReturnValue(Member member, Boolean b) {
        this.annotationIgnoresForReturnValues.put(member, b);
    }

    public void ignoreConstraintAnnotationsForCrossParameterConstraint(Member member, Boolean b) {
        this.annotationIgnoresForCrossParameter.put(member, b);
    }

    public void ignoreConstraintAnnotationsOnParameter(Member member, int index, Boolean b) {
        ExecutableParameterKey key = new ExecutableParameterKey(member, index);
        this.annotationIgnoresForMethodParameter.put(key, b);
    }

    public void ignoreClassLevelConstraintAnnotations(Class<?> clazz, boolean b) {
        this.annotationIgnoresForClasses.put(clazz, b);
    }

    private boolean areAllConstraintAnnotationsIgnoredFor(Class<?> clazz) {
        return this.ignoreAnnotationDefaults.containsKey(clazz) && this.ignoreAnnotationDefaults.get(clazz) != false;
    }

    public class ExecutableParameterKey {
        private final Member member;
        private final int index;

        public ExecutableParameterKey(Member member, int index) {
            this.member = member;
            this.index = index;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ExecutableParameterKey that = (ExecutableParameterKey)o;
            if (this.index != that.index) {
                return false;
            }
            return !(this.member != null ? !this.member.equals(that.member) : that.member != null);
        }

        public int hashCode() {
            int result = this.member != null ? this.member.hashCode() : 0;
            result = 31 * result + this.index;
            return result;
        }
    }
}

