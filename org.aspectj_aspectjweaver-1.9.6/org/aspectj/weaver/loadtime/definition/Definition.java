/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Definition {
    private final StringBuffer weaverOptions = new StringBuffer();
    private final List<String> dumpPatterns = new ArrayList<String>();
    private boolean dumpBefore = false;
    private boolean perClassloaderDumpDir = false;
    private final List<String> includePatterns = new ArrayList<String>();
    private final List<String> excludePatterns = new ArrayList<String>();
    private final List<String> aspectClassNames = new ArrayList<String>();
    private final List<String> aspectExcludePatterns = new ArrayList<String>();
    private final List<String> aspectIncludePatterns = new ArrayList<String>();
    private final List<ConcreteAspect> concreteAspects = new ArrayList<ConcreteAspect>();
    private final Map<String, String> scopedAspects = new HashMap<String, String>();
    private final Map<String, String> requiredTypesForAspects = new HashMap<String, String>();

    public String getWeaverOptions() {
        return this.weaverOptions.toString();
    }

    public List<String> getDumpPatterns() {
        return this.dumpPatterns;
    }

    public void setDumpBefore(boolean b) {
        this.dumpBefore = b;
    }

    public boolean shouldDumpBefore() {
        return this.dumpBefore;
    }

    public void setCreateDumpDirPerClassloader(boolean b) {
        this.perClassloaderDumpDir = b;
    }

    public boolean createDumpDirPerClassloader() {
        return this.perClassloaderDumpDir;
    }

    public List<String> getIncludePatterns() {
        return this.includePatterns;
    }

    public List<String> getExcludePatterns() {
        return this.excludePatterns;
    }

    public List<String> getAspectClassNames() {
        return this.aspectClassNames;
    }

    public List<String> getAspectExcludePatterns() {
        return this.aspectExcludePatterns;
    }

    public List<String> getAspectIncludePatterns() {
        return this.aspectIncludePatterns;
    }

    public List<ConcreteAspect> getConcreteAspects() {
        return this.concreteAspects;
    }

    public void appendWeaverOptions(String option) {
        this.weaverOptions.append(option.trim()).append(' ');
    }

    public void addScopedAspect(String name, String scopePattern) {
        this.scopedAspects.put(name, scopePattern);
    }

    public String getScopeForAspect(String name) {
        return this.scopedAspects.get(name);
    }

    public void setAspectRequires(String name, String requiredType) {
        this.requiredTypesForAspects.put(name, requiredType);
    }

    public String getAspectRequires(String name) {
        return this.requiredTypesForAspects.get(name);
    }

    public static class DeclareErrorOrWarning {
        public final boolean isError;
        public final String pointcut;
        public final String message;

        public DeclareErrorOrWarning(boolean isError, String pointcut, String message) {
            this.isError = isError;
            this.pointcut = pointcut;
            this.message = message;
        }
    }

    public static class PointcutAndAdvice {
        public final AdviceKind adviceKind;
        public final String pointcut;
        public final String adviceClass;
        public final String adviceMethod;

        public PointcutAndAdvice(AdviceKind adviceKind, String pointcut, String adviceClass, String adviceMethod) {
            this.adviceKind = adviceKind;
            this.pointcut = pointcut;
            this.adviceClass = adviceClass;
            this.adviceMethod = adviceMethod;
        }
    }

    public static class DeclareAnnotation {
        public final DeclareAnnotationKind declareAnnotationKind;
        public final String pattern;
        public final String annotation;

        public DeclareAnnotation(DeclareAnnotationKind kind, String pattern, String annotation) {
            this.declareAnnotationKind = kind;
            this.pattern = pattern;
            this.annotation = annotation;
        }
    }

    public static enum DeclareAnnotationKind {
        Method,
        Field,
        Type;

    }

    public static enum AdviceKind {
        Before,
        After,
        AfterReturning,
        AfterThrowing,
        Around;

    }

    public static class Pointcut {
        public final String name;
        public final String expression;

        public Pointcut(String name, String expression) {
            this.name = name;
            this.expression = expression;
        }
    }

    public static class ConcreteAspect {
        public final String name;
        public final String extend;
        public final String precedence;
        public final List<Pointcut> pointcuts;
        public final List<DeclareAnnotation> declareAnnotations;
        public final List<PointcutAndAdvice> pointcutsAndAdvice;
        public final String perclause;
        public List<DeclareErrorOrWarning> deows;

        public ConcreteAspect(String name, String extend) {
            this(name, extend, null, null);
        }

        public ConcreteAspect(String name, String extend, String precedence, String perclause) {
            this.name = name;
            if (extend == null || extend.length() == 0) {
                this.extend = null;
                if (precedence != null && precedence.length() == 0) {
                    // empty if block
                }
            } else {
                this.extend = extend;
            }
            this.precedence = precedence;
            this.pointcuts = new ArrayList<Pointcut>();
            this.declareAnnotations = new ArrayList<DeclareAnnotation>();
            this.pointcutsAndAdvice = new ArrayList<PointcutAndAdvice>();
            this.deows = new ArrayList<DeclareErrorOrWarning>();
            this.perclause = perclause;
        }
    }
}

