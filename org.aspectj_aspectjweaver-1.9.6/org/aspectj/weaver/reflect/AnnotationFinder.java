/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import java.lang.reflect.Member;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public interface AnnotationFinder {
    public void setClassLoader(ClassLoader var1);

    public void setWorld(World var1);

    public Object getAnnotation(ResolvedType var1, Object var2);

    public Object getAnnotationFromMember(ResolvedType var1, Member var2);

    public AnnotationAJ getAnnotationOfType(UnresolvedType var1, Member var2);

    public String getAnnotationDefaultValue(Member var1);

    public Object getAnnotationFromClass(ResolvedType var1, Class<?> var2);

    public ResolvedType[] getAnnotations(Member var1, boolean var2);

    public ResolvedType[][] getParameterAnnotationTypes(Member var1);
}

