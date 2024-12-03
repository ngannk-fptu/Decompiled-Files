/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Collection;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.JoinPointSignatureIterator;
import org.aspectj.weaver.MemberKind;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public interface Member
extends Comparable<Member> {
    public static final Member[] NONE = new Member[0];
    public static final MemberKind METHOD = new MemberKind("METHOD", 1);
    public static final MemberKind FIELD = new MemberKind("FIELD", 2);
    public static final MemberKind CONSTRUCTOR = new MemberKind("CONSTRUCTOR", 3);
    public static final MemberKind STATIC_INITIALIZATION = new MemberKind("STATIC_INITIALIZATION", 4);
    public static final MemberKind POINTCUT = new MemberKind("POINTCUT", 5);
    public static final MemberKind ADVICE = new MemberKind("ADVICE", 6);
    public static final MemberKind HANDLER = new MemberKind("HANDLER", 7);
    public static final MemberKind MONITORENTER = new MemberKind("MONITORENTER", 8);
    public static final MemberKind MONITOREXIT = new MemberKind("MONITOREXIT", 9);
    public static final AnnotationAJ[][] NO_PARAMETER_ANNOTATIONXS = new AnnotationAJ[0][];
    public static final ResolvedType[][] NO_PARAMETER_ANNOTATION_TYPES = new ResolvedType[0][];

    public MemberKind getKind();

    public String getName();

    public UnresolvedType getDeclaringType();

    public UnresolvedType[] getParameterTypes();

    public UnresolvedType[] getGenericParameterTypes();

    public UnresolvedType getType();

    public UnresolvedType getReturnType();

    public UnresolvedType getGenericReturnType();

    public String getSignature();

    public JoinPointSignatureIterator getJoinPointSignatures(World var1);

    public int getArity();

    public String getParameterSignature();

    public int getModifiers(World var1);

    public int getModifiers();

    public boolean canBeParameterized();

    public AnnotationAJ[] getAnnotations();

    public Collection<ResolvedType> getDeclaringTypes(World var1);

    public String[] getParameterNames(World var1);

    public UnresolvedType[] getExceptions(World var1);

    public ResolvedMember resolve(World var1);

    @Override
    public int compareTo(Member var1);
}

