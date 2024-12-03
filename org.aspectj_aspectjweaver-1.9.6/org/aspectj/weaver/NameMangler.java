/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.lang.reflect.Modifier;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.CrosscuttingMembers;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberKind;
import org.aspectj.weaver.UnresolvedType;

public class NameMangler {
    public static final String PREFIX = "ajc$";
    public static final char[] PREFIX_CHARS = "ajc$".toCharArray();
    public static final String ITD_PREFIX = "ajc$interType$";
    public static final String CFLOW_STACK_TYPE = "org.aspectj.runtime.internal.CFlowStack";
    public static final String CFLOW_COUNTER_TYPE = "org.aspectj.runtime.internal.CFlowCounter";
    public static final UnresolvedType CFLOW_STACK_UNRESOLVEDTYPE = UnresolvedType.forSignature("Lorg/aspectj/runtime/internal/CFlowStack;");
    public static final UnresolvedType CFLOW_COUNTER_UNRESOLVEDTYPE = UnresolvedType.forSignature("Lorg/aspectj/runtime/internal/CFlowCounter;");
    public static final String SOFT_EXCEPTION_TYPE = "org.aspectj.lang.SoftException";
    public static final String PERSINGLETON_FIELD_NAME = "ajc$perSingletonInstance";
    public static final String PERCFLOW_FIELD_NAME = "ajc$perCflowStack";
    public static final String PERCFLOW_PUSH_METHOD = "ajc$perCflowPush";
    public static final String PEROBJECT_BIND_METHOD = "ajc$perObjectBind";
    public static final String PERTYPEWITHIN_GETINSTANCE_METHOD = "ajc$getInstance";
    public static final String PERTYPEWITHIN_CREATEASPECTINSTANCE_METHOD = "ajc$createAspectInstance";
    public static final String PERTYPEWITHIN_WITHINTYPEFIELD = "ajc$withinType";
    public static final String PERTYPEWITHIN_GETWITHINTYPENAME_METHOD = "getWithinTypeName";
    public static final String AJC_PRE_CLINIT_NAME = "ajc$preClinit";
    public static final String AJC_POST_CLINIT_NAME = "ajc$postClinit";
    public static final String INITFAILURECAUSE_FIELD_NAME = "ajc$initFailureCause";
    public static final String ANNOTATION_CACHE_FIELD_NAME = "ajc$anno$";

    public static boolean isSyntheticMethod(String methodName, boolean declaredInAspect) {
        if (methodName.startsWith(PREFIX)) {
            if (methodName.startsWith("ajc$before") || methodName.startsWith("ajc$after")) {
                return false;
            }
            if (methodName.startsWith("ajc$around")) {
                return methodName.endsWith("proceed");
            }
            return !methodName.startsWith("ajc$interMethod$");
        }
        return methodName.indexOf("_aroundBody") != -1;
    }

    public static String perObjectInterfaceGet(UnresolvedType aspectType) {
        return NameMangler.makeName(aspectType.getNameAsIdentifier(), "perObjectGet");
    }

    public static String perObjectInterfaceSet(UnresolvedType aspectType) {
        return NameMangler.makeName(aspectType.getNameAsIdentifier(), "perObjectSet");
    }

    public static String perObjectInterfaceField(UnresolvedType aspectType) {
        return NameMangler.makeName(aspectType.getNameAsIdentifier(), "perObjectField");
    }

    public static String perTypeWithinFieldForTarget(UnresolvedType aspectType) {
        return NameMangler.makeName(aspectType.getNameAsIdentifier(), "ptwAspectInstance");
    }

    public static String perTypeWithinLocalAspectOf(UnresolvedType aspectType) {
        return NameMangler.makeName(aspectType.getNameAsIdentifier(), "localAspectOf");
    }

    public static String itdAtDeclareParentsField(UnresolvedType aspectType, UnresolvedType itdType) {
        return NameMangler.makeName("instance", aspectType.getNameAsIdentifier(), itdType.getNameAsIdentifier());
    }

    public static String privilegedAccessMethodForMethod(String name, UnresolvedType objectType, UnresolvedType aspectType) {
        return NameMangler.makeName("privMethod", aspectType.getNameAsIdentifier(), objectType.getNameAsIdentifier(), name);
    }

    public static String privilegedAccessMethodForFieldGet(String name, UnresolvedType objectType, UnresolvedType aspectType) {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(NameMangler.makeName("privFieldGet", aspectType.getNameAsIdentifier(), objectType.getNameAsIdentifier(), name));
        return nameBuilder.toString();
    }

    public static String privilegedAccessMethodForFieldSet(String name, UnresolvedType objectType, UnresolvedType aspectType) {
        return NameMangler.makeName("privFieldSet", aspectType.getNameAsIdentifier(), objectType.getNameAsIdentifier(), name);
    }

    public static String inlineAccessMethodForMethod(String name, UnresolvedType objectType, UnresolvedType aspectType) {
        return NameMangler.makeName("inlineAccessMethod", aspectType.getNameAsIdentifier(), objectType.getNameAsIdentifier(), name);
    }

    public static String inlineAccessMethodForFieldGet(String name, UnresolvedType objectType, UnresolvedType aspectType) {
        return NameMangler.makeName("inlineAccessFieldGet", aspectType.getNameAsIdentifier(), objectType.getNameAsIdentifier(), name);
    }

    public static String inlineAccessMethodForFieldSet(String name, UnresolvedType objectType, UnresolvedType aspectType) {
        return NameMangler.makeName("inlineAccessFieldSet", aspectType.getNameAsIdentifier(), objectType.getNameAsIdentifier(), name);
    }

    public static String adviceName(String nameAsIdentifier, AdviceKind kind, int adviceSeqNumber, int pcdHash) {
        String newname = NameMangler.makeName(kind.getName(), nameAsIdentifier, Integer.toString(adviceSeqNumber), Integer.toHexString(pcdHash));
        return newname;
    }

    public static String interFieldInterfaceField(UnresolvedType aspectType, UnresolvedType interfaceType, String name) {
        return NameMangler.makeName("interField", aspectType.getNameAsIdentifier(), interfaceType.getNameAsIdentifier(), name);
    }

    public static String interFieldInterfaceSetter(UnresolvedType aspectType, UnresolvedType interfaceType, String name) {
        return NameMangler.makeName("interFieldSet", aspectType.getNameAsIdentifier(), interfaceType.getNameAsIdentifier(), name);
    }

    public static String interFieldInterfaceGetter(UnresolvedType aspectType, UnresolvedType interfaceType, String name) {
        return NameMangler.makeName("interFieldGet", aspectType.getNameAsIdentifier(), interfaceType.getNameAsIdentifier(), name);
    }

    public static String interFieldSetDispatcher(UnresolvedType aspectType, UnresolvedType onType, String name) {
        return NameMangler.makeName("interFieldSetDispatch", aspectType.getNameAsIdentifier(), onType.getNameAsIdentifier(), name);
    }

    public static String interFieldGetDispatcher(UnresolvedType aspectType, UnresolvedType onType, String name) {
        return NameMangler.makeName("interFieldGetDispatch", aspectType.getNameAsIdentifier(), onType.getNameAsIdentifier(), name);
    }

    public static String interFieldClassField(int modifiers, UnresolvedType aspectType, UnresolvedType classType, String name) {
        if (Modifier.isPublic(modifiers)) {
            return name;
        }
        return NameMangler.makeName("interField", NameMangler.makeVisibilityName(modifiers, aspectType), name);
    }

    public static String interFieldInitializer(UnresolvedType aspectType, UnresolvedType classType, String name) {
        return NameMangler.makeName("interFieldInit", aspectType.getNameAsIdentifier(), classType.getNameAsIdentifier(), name);
    }

    public static String interMethod(int modifiers, UnresolvedType aspectType, UnresolvedType classType, String name) {
        if (Modifier.isPublic(modifiers)) {
            return name;
        }
        return NameMangler.makeName("interMethodDispatch2", NameMangler.makeVisibilityName(modifiers, aspectType), name);
    }

    public static String interMethodDispatcher(UnresolvedType aspectType, UnresolvedType classType, String name) {
        return NameMangler.makeName("interMethodDispatch1", aspectType.getNameAsIdentifier(), classType.getNameAsIdentifier(), name);
    }

    public static String interMethodBody(UnresolvedType aspectType, UnresolvedType classType, String name) {
        return NameMangler.makeName("interMethod", aspectType.getNameAsIdentifier(), classType.getNameAsIdentifier(), name);
    }

    public static String preIntroducedConstructor(UnresolvedType aspectType, UnresolvedType targetType) {
        return NameMangler.makeName("preInterConstructor", aspectType.getNameAsIdentifier(), targetType.getNameAsIdentifier());
    }

    public static String postIntroducedConstructor(UnresolvedType aspectType, UnresolvedType targetType) {
        return NameMangler.makeName("postInterConstructor", aspectType.getNameAsIdentifier(), targetType.getNameAsIdentifier());
    }

    public static String superDispatchMethod(UnresolvedType classType, String name) {
        return NameMangler.makeName("superDispatch", classType.getNameAsIdentifier(), name);
    }

    public static String protectedDispatchMethod(UnresolvedType classType, String name) {
        return NameMangler.makeName("protectedDispatch", classType.getNameAsIdentifier(), name);
    }

    private static String makeVisibilityName(int modifiers, UnresolvedType aspectType) {
        if (Modifier.isPrivate(modifiers)) {
            return aspectType.getOutermostType().getNameAsIdentifier();
        }
        if (Modifier.isProtected(modifiers)) {
            throw new RuntimeException("protected inter-types not allowed");
        }
        if (Modifier.isPublic(modifiers)) {
            return "";
        }
        return aspectType.getPackageNameAsIdentifier();
    }

    private static String makeName(String s1, String s2) {
        return PREFIX + s1 + "$" + s2;
    }

    public static String makeName(String s1, String s2, String s3) {
        return PREFIX + s1 + "$" + s2 + "$" + s3;
    }

    public static String makeName(String s1, String s2, String s3, String s4) {
        return PREFIX + s1 + "$" + s2 + "$" + s3 + "$" + s4;
    }

    public static String cflowStack(CrosscuttingMembers xcut) {
        return NameMangler.makeName("cflowStack", Integer.toHexString(xcut.getCflowEntries().size()));
    }

    public static String cflowCounter(CrosscuttingMembers xcut) {
        return NameMangler.makeName("cflowCounter", Integer.toHexString(xcut.getCflowEntries().size()));
    }

    public static String makeClosureClassName(UnresolvedType enclosingType, String suffix) {
        return enclosingType.getName() + "$AjcClosure" + suffix;
    }

    public static String aroundShadowMethodName(Member shadowSig, String suffixTag) {
        StringBuffer ret = new StringBuffer();
        ret.append(NameMangler.getExtractableName(shadowSig)).append("_aroundBody").append(suffixTag);
        return ret.toString();
    }

    public static String aroundAdviceMethodName(Member shadowSig, String suffixTag) {
        StringBuffer ret = new StringBuffer();
        ret.append(NameMangler.getExtractableName(shadowSig)).append("_aroundBody").append(suffixTag).append("$advice");
        return ret.toString();
    }

    public static String getExtractableName(Member shadowSignature) {
        String name = shadowSignature.getName();
        MemberKind kind = shadowSignature.getKind();
        if (kind == Member.CONSTRUCTOR) {
            return "init$";
        }
        if (kind == Member.STATIC_INITIALIZATION) {
            return "clinit$";
        }
        return name;
    }

    public static String proceedMethodName(String adviceMethodName) {
        return adviceMethodName + "proceed";
    }
}

