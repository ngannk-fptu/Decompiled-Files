/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;

public abstract class AbstractAnnotationAJ
implements AnnotationAJ {
    protected final ResolvedType type;
    private Set<String> supportedTargets = null;

    public AbstractAnnotationAJ(ResolvedType type) {
        this.type = type;
    }

    @Override
    public final ResolvedType getType() {
        return this.type;
    }

    @Override
    public final String getTypeSignature() {
        return this.type.getSignature();
    }

    @Override
    public final String getTypeName() {
        return this.type.getName();
    }

    @Override
    public final boolean allowedOnAnnotationType() {
        this.ensureAtTargetInitialized();
        if (this.supportedTargets.isEmpty()) {
            return true;
        }
        return this.supportedTargets.contains("ANNOTATION_TYPE");
    }

    @Override
    public final boolean allowedOnField() {
        this.ensureAtTargetInitialized();
        if (this.supportedTargets.isEmpty()) {
            return true;
        }
        return this.supportedTargets.contains("FIELD");
    }

    @Override
    public final boolean allowedOnRegularType() {
        this.ensureAtTargetInitialized();
        if (this.supportedTargets.isEmpty()) {
            return true;
        }
        return this.supportedTargets.contains("TYPE");
    }

    public final void ensureAtTargetInitialized() {
        if (this.supportedTargets == null) {
            AnnotationAJ atTargetAnnotation = this.retrieveAnnotationOnAnnotation(UnresolvedType.AT_TARGET);
            this.supportedTargets = atTargetAnnotation == null ? Collections.emptySet() : atTargetAnnotation.getTargets();
        }
    }

    @Override
    public final String getValidTargets() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        Iterator<String> iter = this.supportedTargets.iterator();
        while (iter.hasNext()) {
            String evalue = iter.next();
            sb.append(evalue);
            if (!iter.hasNext()) continue;
            sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public final boolean specifiesTarget() {
        this.ensureAtTargetInitialized();
        return !this.supportedTargets.isEmpty();
    }

    private final AnnotationAJ retrieveAnnotationOnAnnotation(UnresolvedType requiredAnnotationSignature) {
        AnnotationAJ[] annos = this.type.getAnnotations();
        for (int i = 0; i < annos.length; ++i) {
            AnnotationAJ a = annos[i];
            if (!a.getTypeSignature().equals(requiredAnnotationSignature.getSignature())) continue;
            return annos[i];
        }
        return null;
    }

    @Override
    public abstract boolean isRuntimeVisible();

    @Override
    public abstract Set<String> getTargets();

    @Override
    public abstract boolean hasNameValuePair(String var1, String var2);

    @Override
    public abstract boolean hasNamedValue(String var1);

    @Override
    public abstract String stringify();
}

