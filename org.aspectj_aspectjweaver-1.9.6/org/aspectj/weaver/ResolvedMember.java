/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AnnotatedElement;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableDeclaringElement;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public interface ResolvedMember
extends Member,
AnnotatedElement,
TypeVariableDeclaringElement {
    public static final ResolvedMember[] NONE = new ResolvedMember[0];

    @Override
    public int getModifiers(World var1);

    @Override
    public int getModifiers();

    @Override
    public UnresolvedType[] getExceptions(World var1);

    public UnresolvedType[] getExceptions();

    public ShadowMunger getAssociatedShadowMunger();

    public boolean isAjSynthetic();

    public boolean isCompatibleWith(Member var1);

    @Override
    public boolean hasAnnotation(UnresolvedType var1);

    @Override
    public AnnotationAJ[] getAnnotations();

    @Override
    public ResolvedType[] getAnnotationTypes();

    public void setAnnotationTypes(ResolvedType[] var1);

    public void addAnnotation(AnnotationAJ var1);

    public boolean isBridgeMethod();

    public boolean isVarargsMethod();

    public boolean isSynthetic();

    public void write(CompressingDataOutputStream var1) throws IOException;

    public ISourceContext getSourceContext(World var1);

    public String[] getParameterNames();

    public void setParameterNames(String[] var1);

    public AnnotationAJ[][] getParameterAnnotations();

    public ResolvedType[][] getParameterAnnotationTypes();

    public String getAnnotationDefaultValue();

    public String getParameterSignatureErased();

    public String getSignatureErased();

    @Override
    public String[] getParameterNames(World var1);

    public AjAttribute.EffectiveSignatureAttribute getEffectiveSignature();

    public ISourceLocation getSourceLocation();

    public int getStart();

    public int getEnd();

    public ISourceContext getSourceContext();

    public void setPosition(int var1, int var2);

    public void setSourceContext(ISourceContext var1);

    public boolean isAbstract();

    public boolean isPublic();

    public boolean isDefault();

    public boolean isVisible(ResolvedType var1);

    public void setCheckedExceptions(UnresolvedType[] var1);

    public void setAnnotatedElsewhere(boolean var1);

    public boolean isAnnotatedElsewhere();

    public String toGenericString();

    public String toDebugString();

    public boolean hasBackingGenericMember();

    public ResolvedMember getBackingGenericMember();

    @Override
    public UnresolvedType getGenericReturnType();

    @Override
    public UnresolvedType[] getGenericParameterTypes();

    public boolean equalsApartFromDeclaringType(Object var1);

    public ResolvedMemberImpl parameterizedWith(UnresolvedType[] var1, ResolvedType var2, boolean var3);

    public ResolvedMemberImpl parameterizedWith(UnresolvedType[] var1, ResolvedType var2, boolean var3, List<String> var4);

    public void setTypeVariables(TypeVariable[] var1);

    public TypeVariable[] getTypeVariables();

    public boolean matches(ResolvedMember var1, boolean var2);

    public void evictWeavingState();

    public ResolvedMember parameterizedWith(Map<String, UnresolvedType> var1, World var2);

    public boolean isDefaultConstructor();

    public void setAnnotations(AnnotationAJ[] var1);
}

