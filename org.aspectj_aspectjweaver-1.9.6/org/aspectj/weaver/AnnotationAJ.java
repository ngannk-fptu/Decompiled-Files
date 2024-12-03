/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Set;
import org.aspectj.weaver.ResolvedType;

public interface AnnotationAJ {
    public static final AnnotationAJ[] EMPTY_ARRAY = new AnnotationAJ[0];

    public String getTypeSignature();

    public String getTypeName();

    public ResolvedType getType();

    public boolean allowedOnAnnotationType();

    public boolean allowedOnField();

    public boolean allowedOnRegularType();

    public Set<String> getTargets();

    public boolean hasNamedValue(String var1);

    public boolean hasNameValuePair(String var1, String var2);

    public String getValidTargets();

    public String stringify();

    public boolean specifiesTarget();

    public boolean isRuntimeVisible();

    public String getStringFormOfValue(String var1);
}

