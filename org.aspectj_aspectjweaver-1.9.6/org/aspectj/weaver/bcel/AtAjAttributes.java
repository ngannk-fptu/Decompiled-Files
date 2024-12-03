/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.Constant;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.Field;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.classfile.LocalVariable;
import org.aspectj.apache.bcel.classfile.LocalVariableTable;
import org.aspectj.apache.bcel.classfile.Method;
import org.aspectj.apache.bcel.classfile.Unknown;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.ArrayElementValue;
import org.aspectj.apache.bcel.classfile.annotation.ClassElementValue;
import org.aspectj.apache.bcel.classfile.annotation.ElementValue;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeVisAnnos;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.asm.AsmManager;
import org.aspectj.asm.IHierarchy;
import org.aspectj.asm.IProgramElement;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.BindingScope;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.MethodDelegateTypeMunger;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelField;
import org.aspectj.weaver.bcel.BcelMethod;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.IfFinder;
import org.aspectj.weaver.patterns.DeclareErrorOrWarning;
import org.aspectj.weaver.patterns.DeclareParents;
import org.aspectj.weaver.patterns.DeclareParentsMixin;
import org.aspectj.weaver.patterns.DeclarePrecedence;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.ParserException;
import org.aspectj.weaver.patterns.PatternParser;
import org.aspectj.weaver.patterns.PerCflow;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.PerFromSuper;
import org.aspectj.weaver.patterns.PerObject;
import org.aspectj.weaver.patterns.PerSingleton;
import org.aspectj.weaver.patterns.PerTypeWithin;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.TypePattern;

public class AtAjAttributes {
    private static final List<AjAttribute> NO_ATTRIBUTES = Collections.emptyList();
    private static final String[] EMPTY_STRINGS = new String[0];
    private static final String VALUE = "value";
    private static final String ARGNAMES = "argNames";
    private static final String POINTCUT = "pointcut";
    private static final String THROWING = "throwing";
    private static final String RETURNING = "returning";
    private static final String STRING_DESC = "Ljava/lang/String;";
    private static final String ASPECTJ_ANNOTATION_PACKAGE = "org.aspectj.lang.annotation";
    private static final char PACKAGE_INITIAL_CHAR = "org.aspectj.lang.annotation".charAt(0);

    public static boolean acceptAttribute(Attribute attribute) {
        return attribute instanceof RuntimeVisAnnos;
    }

    public static List<AjAttribute> readAj5ClassAttributes(AsmManager model, JavaClass javaClass, ReferenceType type, ISourceContext context, IMessageHandler msgHandler, boolean isCodeStyleAspect) {
        int j;
        Attribute attribute;
        int i;
        boolean ignoreThisClass;
        boolean bl = ignoreThisClass = javaClass.getClassName().charAt(0) == PACKAGE_INITIAL_CHAR && javaClass.getClassName().startsWith(ASPECTJ_ANNOTATION_PACKAGE);
        if (ignoreThisClass) {
            return NO_ATTRIBUTES;
        }
        boolean containsPointcut = false;
        boolean containsAnnotationClassReference = false;
        Constant[] cpool = javaClass.getConstantPool().getConstantPool();
        for (int i2 = 0; i2 < cpool.length; ++i2) {
            String constantValue;
            Constant constant = cpool[i2];
            if (constant == null || constant.getTag() != 1 || (constantValue = ((ConstantUtf8)constant).getValue()).length() <= 28 || constantValue.charAt(1) != PACKAGE_INITIAL_CHAR || !constantValue.startsWith("Lorg/aspectj/lang/annotation")) continue;
            containsAnnotationClassReference = true;
            if ("Lorg/aspectj/lang/annotation/DeclareAnnotation;".equals(constantValue)) {
                msgHandler.handleMessage(new Message("Found @DeclareAnnotation while current release does not support it (see '" + type.getName() + "')", IMessage.WARNING, null, type.getSourceLocation()));
            }
            if (!"Lorg/aspectj/lang/annotation/Pointcut;".equals(constantValue)) continue;
            containsPointcut = true;
        }
        if (!containsAnnotationClassReference) {
            return NO_ATTRIBUTES;
        }
        AjAttributeStruct struct = new AjAttributeStruct(type, context, msgHandler);
        Attribute[] attributes = javaClass.getAttributes();
        boolean hasAtAspectAnnotation = false;
        boolean hasAtPrecedenceAnnotation = false;
        AjAttribute.WeaverVersionInfo wvinfo = null;
        for (i = 0; i < attributes.length; ++i) {
            attribute = attributes[i];
            if (!AtAjAttributes.acceptAttribute(attribute)) continue;
            RuntimeAnnos rvs = (RuntimeAnnos)attribute;
            if (isCodeStyleAspect || javaClass.isInterface()) break;
            hasAtAspectAnnotation = AtAjAttributes.handleAspectAnnotation(rvs, struct);
            hasAtPrecedenceAnnotation = AtAjAttributes.handlePrecedenceAnnotation(rvs, struct);
            break;
        }
        for (i = attributes.length - 1; i >= 0; --i) {
            attribute = attributes[i];
            if (!attribute.getName().equals("org.aspectj.weaver.WeaverVersion")) continue;
            try {
                VersionedDataInputStream s = new VersionedDataInputStream(new ByteArrayInputStream(((Unknown)attribute).getBytes()), null);
                wvinfo = AjAttribute.WeaverVersionInfo.read(s);
                struct.ajAttributes.add(0, wvinfo);
                continue;
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if (wvinfo == null) {
            ReferenceTypeDelegate delegate = type.getDelegate();
            if (delegate instanceof BcelObjectType && (wvinfo = ((BcelObjectType)delegate).getWeaverVersionAttribute()) != null) {
                if (wvinfo.getMajorVersion() != 0) {
                    struct.ajAttributes.add(0, wvinfo);
                } else {
                    wvinfo = null;
                }
            }
            if (wvinfo == null) {
                wvinfo = new AjAttribute.WeaverVersionInfo();
                struct.ajAttributes.add(0, wvinfo);
            }
        }
        if (hasAtPrecedenceAnnotation && !hasAtAspectAnnotation) {
            msgHandler.handleMessage(new Message("Found @DeclarePrecedence on a non @Aspect type '" + type.getName() + "'", IMessage.WARNING, null, type.getSourceLocation()));
            return NO_ATTRIBUTES;
        }
        if (!(hasAtAspectAnnotation || isCodeStyleAspect || containsPointcut)) {
            return NO_ATTRIBUTES;
        }
        for (int i3 = 0; i3 < javaClass.getMethods().length; ++i3) {
            Method method = javaClass.getMethods()[i3];
            if (method.getName().startsWith("ajc$")) continue;
            AjAttributeMethodStruct mstruct = null;
            boolean processedPointcut = false;
            Attribute[] mattributes = method.getAttributes();
            for (j = 0; j < mattributes.length; ++j) {
                Attribute mattribute = mattributes[j];
                if (!AtAjAttributes.acceptAttribute(mattribute)) continue;
                mstruct = new AjAttributeMethodStruct(method, null, type, context, msgHandler);
                processedPointcut = AtAjAttributes.handlePointcutAnnotation((RuntimeAnnos)mattribute, mstruct);
                if (processedPointcut) break;
                processedPointcut = AtAjAttributes.handleDeclareMixinAnnotation((RuntimeAnnos)mattribute, mstruct);
                break;
            }
            if (!processedPointcut) continue;
            struct.ajAttributes.addAll(mstruct.ajAttributes);
        }
        Field[] fs = javaClass.getFields();
        for (int i4 = 0; i4 < fs.length; ++i4) {
            Field field = fs[i4];
            if (field.getName().startsWith("ajc$")) continue;
            AjAttributeFieldStruct fstruct = new AjAttributeFieldStruct(field, null, type, context, msgHandler);
            Attribute[] fattributes = field.getAttributes();
            for (j = 0; j < fattributes.length; ++j) {
                Attribute fattribute = fattributes[j];
                if (!AtAjAttributes.acceptAttribute(fattribute)) continue;
                RuntimeAnnos frvs = (RuntimeAnnos)fattribute;
                if (!AtAjAttributes.handleDeclareErrorOrWarningAnnotation(model, frvs, fstruct) && !AtAjAttributes.handleDeclareParentsAnnotation(frvs, fstruct) || type.isAnnotationStyleAspect() || isCodeStyleAspect) break;
                msgHandler.handleMessage(new Message("Found @AspectJ annotations in a non @Aspect type '" + type.getName() + "'", IMessage.WARNING, null, type.getSourceLocation()));
                break;
            }
            struct.ajAttributes.addAll(fstruct.ajAttributes);
        }
        return struct.ajAttributes;
    }

    public static List<AjAttribute> readAj5MethodAttributes(Method method, BcelMethod bMethod, ResolvedType type, ResolvedPointcutDefinition preResolvedPointcut, ISourceContext context, IMessageHandler msgHandler) {
        if (method.getName().startsWith("ajc$")) {
            return Collections.emptyList();
        }
        AjAttributeMethodStruct struct = new AjAttributeMethodStruct(method, bMethod, type, context, msgHandler);
        Attribute[] attributes = method.getAttributes();
        boolean hasAtAspectJAnnotation = false;
        boolean hasAtAspectJAnnotationMustReturnVoid = false;
        for (int i = 0; i < attributes.length; ++i) {
            Attribute attribute = attributes[i];
            try {
                if (!AtAjAttributes.acceptAttribute(attribute)) continue;
                RuntimeAnnos rvs = (RuntimeAnnos)attribute;
                hasAtAspectJAnnotationMustReturnVoid = hasAtAspectJAnnotationMustReturnVoid || AtAjAttributes.handleBeforeAnnotation(rvs, struct, preResolvedPointcut);
                hasAtAspectJAnnotationMustReturnVoid = hasAtAspectJAnnotationMustReturnVoid || AtAjAttributes.handleAfterAnnotation(rvs, struct, preResolvedPointcut);
                hasAtAspectJAnnotationMustReturnVoid = hasAtAspectJAnnotationMustReturnVoid || AtAjAttributes.handleAfterReturningAnnotation(rvs, struct, preResolvedPointcut, bMethod);
                hasAtAspectJAnnotationMustReturnVoid = hasAtAspectJAnnotationMustReturnVoid || AtAjAttributes.handleAfterThrowingAnnotation(rvs, struct, preResolvedPointcut, bMethod);
                hasAtAspectJAnnotation = hasAtAspectJAnnotation || AtAjAttributes.handleAroundAnnotation(rvs, struct, preResolvedPointcut);
                break;
            }
            catch (ReturningFormalNotDeclaredInAdviceSignatureException e) {
                msgHandler.handleMessage(new Message(WeaverMessages.format("returningFormalNotDeclaredInAdvice", e.getFormalName()), IMessage.ERROR, null, bMethod.getSourceLocation()));
                continue;
            }
            catch (ThrownFormalNotDeclaredInAdviceSignatureException e) {
                msgHandler.handleMessage(new Message(WeaverMessages.format("thrownFormalNotDeclaredInAdvice", e.getFormalName()), IMessage.ERROR, null, bMethod.getSourceLocation()));
            }
        }
        boolean bl = hasAtAspectJAnnotation = hasAtAspectJAnnotation || hasAtAspectJAnnotationMustReturnVoid;
        if (hasAtAspectJAnnotation && !type.isAspect()) {
            msgHandler.handleMessage(new Message("Found @AspectJ annotations in a non @Aspect type '" + type.getName() + "'", IMessage.WARNING, null, type.getSourceLocation()));
        }
        if (hasAtAspectJAnnotation && !struct.method.isPublic()) {
            msgHandler.handleMessage(new Message("Found @AspectJ annotation on a non public advice '" + AtAjAttributes.methodToString(struct.method) + "'", IMessage.ERROR, null, type.getSourceLocation()));
        }
        if (hasAtAspectJAnnotation && struct.method.isStatic()) {
            msgHandler.handleMessage(MessageUtil.error("Advice cannot be declared static '" + AtAjAttributes.methodToString(struct.method) + "'", type.getSourceLocation()));
        }
        if (hasAtAspectJAnnotationMustReturnVoid && !Type.VOID.equals(struct.method.getReturnType())) {
            msgHandler.handleMessage(new Message("Found @AspectJ annotation on a non around advice not returning void '" + AtAjAttributes.methodToString(struct.method) + "'", IMessage.ERROR, null, type.getSourceLocation()));
        }
        return struct.ajAttributes;
    }

    public static List<AjAttribute> readAj5FieldAttributes(Field field, BcelField bField, ResolvedType type, ISourceContext context, IMessageHandler msgHandler) {
        return Collections.emptyList();
    }

    private static boolean handleAspectAnnotation(RuntimeAnnos runtimeAnnotations, AjAttributeStruct struct) {
        AnnotationGen aspect = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.ASPECT_ANNOTATION);
        if (aspect != null) {
            String perX;
            NameValuePair aspectPerClause;
            boolean extendsAspect = false;
            if (!"java.lang.Object".equals(struct.enclosingType.getSuperclass().getName())) {
                if (!struct.enclosingType.getSuperclass().isAbstract() && struct.enclosingType.getSuperclass().isAspect()) {
                    AtAjAttributes.reportError("cannot extend a concrete aspect", struct);
                    return false;
                }
                extendsAspect = struct.enclosingType.getSuperclass().isAspect();
            }
            PerClause perClause = (aspectPerClause = AtAjAttributes.getAnnotationElement(aspect, VALUE)) == null ? (!extendsAspect ? new PerSingleton() : new PerFromSuper(struct.enclosingType.getSuperclass().getPerClause().getKind())) : ((perX = aspectPerClause.getValue().stringifyValue()) == null || perX.length() <= 0 ? new PerSingleton() : AtAjAttributes.parsePerClausePointcut(perX, struct));
            if (perClause == null) {
                return false;
            }
            perClause.setLocation(struct.context, -1, -1);
            AjAttribute.Aspect aspectAttribute = new AjAttribute.Aspect(perClause);
            struct.ajAttributes.add(aspectAttribute);
            FormalBinding[] bindings = new FormalBinding[]{};
            BindingScope binding = new BindingScope(struct.enclosingType, struct.context, bindings);
            aspectAttribute.setResolutionScope(binding);
            return true;
        }
        return false;
    }

    private static PerClause parsePerClausePointcut(String perClauseString, AjAttributeStruct struct) {
        PerClause perClause;
        Pointcut pointcut = null;
        TypePattern typePattern = null;
        if (perClauseString.startsWith(PerClause.KindAnnotationPrefix.PERCFLOW.getName())) {
            String pointcutString = PerClause.KindAnnotationPrefix.PERCFLOW.extractPointcut(perClauseString);
            pointcut = AtAjAttributes.parsePointcut(pointcutString, struct, false);
            perClause = new PerCflow(pointcut, false);
        } else if (perClauseString.startsWith(PerClause.KindAnnotationPrefix.PERCFLOWBELOW.getName())) {
            String pointcutString = PerClause.KindAnnotationPrefix.PERCFLOWBELOW.extractPointcut(perClauseString);
            pointcut = AtAjAttributes.parsePointcut(pointcutString, struct, false);
            perClause = new PerCflow(pointcut, true);
        } else if (perClauseString.startsWith(PerClause.KindAnnotationPrefix.PERTARGET.getName())) {
            String pointcutString = PerClause.KindAnnotationPrefix.PERTARGET.extractPointcut(perClauseString);
            pointcut = AtAjAttributes.parsePointcut(pointcutString, struct, false);
            perClause = new PerObject(pointcut, false);
        } else if (perClauseString.startsWith(PerClause.KindAnnotationPrefix.PERTHIS.getName())) {
            String pointcutString = PerClause.KindAnnotationPrefix.PERTHIS.extractPointcut(perClauseString);
            pointcut = AtAjAttributes.parsePointcut(pointcutString, struct, false);
            perClause = new PerObject(pointcut, true);
        } else if (perClauseString.startsWith(PerClause.KindAnnotationPrefix.PERTYPEWITHIN.getName())) {
            String pointcutString = PerClause.KindAnnotationPrefix.PERTYPEWITHIN.extractPointcut(perClauseString);
            typePattern = AtAjAttributes.parseTypePattern(pointcutString, struct);
            perClause = new PerTypeWithin(typePattern);
        } else if (perClauseString.equalsIgnoreCase(PerClause.SINGLETON.getName() + "()")) {
            perClause = new PerSingleton();
        } else {
            AtAjAttributes.reportError("@Aspect per clause cannot be read '" + perClauseString + "'", struct);
            return null;
        }
        if (!PerClause.SINGLETON.equals(((PerClause)perClause).getKind()) && !PerClause.PERTYPEWITHIN.equals(((PerClause)perClause).getKind()) && pointcut == null) {
            return null;
        }
        if (PerClause.PERTYPEWITHIN.equals(((PerClause)perClause).getKind()) && typePattern == null) {
            return null;
        }
        return perClause;
    }

    private static boolean handlePrecedenceAnnotation(RuntimeAnnos runtimeAnnotations, AjAttributeStruct struct) {
        NameValuePair precedence;
        AnnotationGen aspect = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.DECLAREPRECEDENCE_ANNOTATION);
        if (aspect != null && (precedence = AtAjAttributes.getAnnotationElement(aspect, VALUE)) != null) {
            String precedencePattern = precedence.getValue().stringifyValue();
            PatternParser parser = new PatternParser(precedencePattern);
            DeclarePrecedence ajPrecedence = parser.parseDominates();
            struct.ajAttributes.add(new AjAttribute.DeclareAttribute(ajPrecedence));
            return true;
        }
        return false;
    }

    private static boolean handleDeclareParentsAnnotation(RuntimeAnnos runtimeAnnotations, AjAttributeFieldStruct struct) {
        AnnotationGen decpAnno = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.DECLAREPARENTS_ANNOTATION);
        if (decpAnno != null) {
            NameValuePair decpPatternNameValuePair = AtAjAttributes.getAnnotationElement(decpAnno, VALUE);
            String decpPattern = decpPatternNameValuePair.getValue().stringifyValue();
            System.out.println("decpPatterNVP = " + decpPattern);
            if (decpPattern != null) {
                TypePattern typePattern = AtAjAttributes.parseTypePattern(decpPattern, struct);
                ResolvedType fieldType = UnresolvedType.forSignature(struct.field.getSignature()).resolve(struct.enclosingType.getWorld());
                if (fieldType.isParameterizedOrRawType()) {
                    fieldType = fieldType.getGenericType();
                }
                if (fieldType.isInterface()) {
                    TypePattern parent = AtAjAttributes.parseTypePattern(fieldType.getName(), struct);
                    FormalBinding[] bindings = new FormalBinding[]{};
                    BindingScope binding = new BindingScope(struct.enclosingType, struct.context, bindings);
                    ArrayList<TypePattern> parents = new ArrayList<TypePattern>(1);
                    parents.add(parent);
                    DeclareParents dp = new DeclareParents(typePattern, parents, false);
                    dp.resolve(binding);
                    typePattern = dp.getChild();
                    dp.setLocation(struct.context, -1, -1);
                    struct.ajAttributes.add(new AjAttribute.DeclareAttribute(dp));
                    String defaultImplClassName = null;
                    NameValuePair defaultImplNVP = AtAjAttributes.getAnnotationElement(decpAnno, "defaultImpl");
                    if (defaultImplNVP != null) {
                        ClassElementValue defaultImpl = (ClassElementValue)defaultImplNVP.getValue();
                        defaultImplClassName = UnresolvedType.forSignature(defaultImpl.getClassString()).getName();
                        if (defaultImplClassName.equals("org.aspectj.lang.annotation.DeclareParents")) {
                            defaultImplClassName = null;
                        } else {
                            ResolvedType impl = struct.enclosingType.getWorld().resolve(defaultImplClassName, false);
                            ResolvedMember[] mm = impl.getDeclaredMethods();
                            int implModifiers = impl.getModifiers();
                            boolean defaultVisibilityImpl = !Modifier.isPrivate(implModifiers) && !Modifier.isProtected(implModifiers) && !Modifier.isPublic(implModifiers);
                            boolean hasNoCtorOrANoArgOne = true;
                            ResolvedMember foundOneOfIncorrectVisibility = null;
                            for (int i = 0; i < mm.length; ++i) {
                                ResolvedMember resolvedMember = mm[i];
                                if (resolvedMember.getName().equals("<init>")) {
                                    hasNoCtorOrANoArgOne = false;
                                    if (resolvedMember.getParameterTypes().length == 0) {
                                        if (defaultVisibilityImpl) {
                                            if (resolvedMember.isPublic() || resolvedMember.isDefault()) {
                                                hasNoCtorOrANoArgOne = true;
                                            } else {
                                                foundOneOfIncorrectVisibility = resolvedMember;
                                            }
                                        } else if (Modifier.isPublic(implModifiers)) {
                                            if (resolvedMember.isPublic()) {
                                                hasNoCtorOrANoArgOne = true;
                                            } else {
                                                foundOneOfIncorrectVisibility = resolvedMember;
                                            }
                                        }
                                    }
                                }
                                if (hasNoCtorOrANoArgOne) break;
                            }
                            if (!hasNoCtorOrANoArgOne) {
                                if (foundOneOfIncorrectVisibility != null) {
                                    AtAjAttributes.reportError("@DeclareParents: defaultImpl=\"" + defaultImplClassName + "\" has a no argument constructor, but it is of incorrect visibility.  It must be at least as visible as the type.", struct);
                                } else {
                                    AtAjAttributes.reportError("@DeclareParents: defaultImpl=\"" + defaultImplClassName + "\" has no public no-arg constructor", struct);
                                }
                            }
                            if (!fieldType.isAssignableFrom(impl)) {
                                AtAjAttributes.reportError("@DeclareParents: defaultImpl=\"" + defaultImplClassName + "\" does not implement the interface '" + fieldType.toString() + "'", struct);
                            }
                        }
                    }
                    boolean hasAtLeastOneMethod = false;
                    Iterator<ResolvedMember> methodIterator = fieldType.getMethodsIncludingIntertypeDeclarations(false, true);
                    while (methodIterator.hasNext()) {
                        ResolvedMember method = methodIterator.next();
                        if (!method.isAbstract()) continue;
                        hasAtLeastOneMethod = true;
                        MethodDelegateTypeMunger mdtm = new MethodDelegateTypeMunger(method, struct.enclosingType, defaultImplClassName, typePattern);
                        mdtm.setFieldType(fieldType);
                        mdtm.setSourceLocation(struct.enclosingType.getSourceLocation());
                        struct.ajAttributes.add(new AjAttribute.TypeMunger(mdtm));
                    }
                    if (hasAtLeastOneMethod && defaultImplClassName != null) {
                        ResolvedMember fieldHost = AjcMemberMaker.itdAtDeclareParentsField(null, fieldType, struct.enclosingType);
                        struct.ajAttributes.add(new AjAttribute.TypeMunger(new MethodDelegateTypeMunger.FieldHostTypeMunger(fieldHost, struct.enclosingType, typePattern)));
                    }
                    return true;
                }
                AtAjAttributes.reportError("@DeclareParents: can only be used on a field whose type is an interface", struct);
                return false;
            }
        }
        return false;
    }

    public static String getMethodForMessage(AjAttributeMethodStruct methodstructure) {
        StringBuffer sb = new StringBuffer();
        sb.append("Method '");
        sb.append(methodstructure.method.getReturnType().toString());
        sb.append(" ").append(methodstructure.enclosingType).append(".").append(methodstructure.method.getName());
        sb.append("(");
        Type[] args = methodstructure.method.getArgumentTypes();
        if (args != null) {
            for (int t = 0; t < args.length; ++t) {
                if (t > 0) {
                    sb.append(",");
                }
                sb.append(args[t].toString());
            }
        }
        sb.append(")'");
        return sb.toString();
    }

    private static boolean handleDeclareMixinAnnotation(RuntimeAnnos runtimeAnnotations, AjAttributeMethodStruct struct) {
        AnnotationGen declareMixinAnnotation = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.DECLAREMIXIN_ANNOTATION);
        if (declareMixinAnnotation == null) {
            return false;
        }
        Method annotatedMethod = struct.method;
        World world = struct.enclosingType.getWorld();
        NameValuePair declareMixinPatternNameValuePair = AtAjAttributes.getAnnotationElement(declareMixinAnnotation, VALUE);
        String declareMixinPattern = declareMixinPatternNameValuePair.getValue().stringifyValue();
        TypePattern targetTypePattern = AtAjAttributes.parseTypePattern(declareMixinPattern, struct);
        ResolvedType methodReturnType = UnresolvedType.forSignature(annotatedMethod.getReturnType().getSignature()).resolve(world);
        if (methodReturnType.isParameterizedOrRawType()) {
            methodReturnType = methodReturnType.getGenericType();
        }
        if (methodReturnType.isPrimitiveType()) {
            AtAjAttributes.reportError(AtAjAttributes.getMethodForMessage(struct) + ":  factory methods for a mixin cannot return void or a primitive type", struct);
            return false;
        }
        if (annotatedMethod.getArgumentTypes().length > 1) {
            AtAjAttributes.reportError(AtAjAttributes.getMethodForMessage(struct) + ": factory methods for a mixin can take a maximum of one parameter", struct);
            return false;
        }
        NameValuePair interfaceListSpecified = AtAjAttributes.getAnnotationElement(declareMixinAnnotation, "interfaces");
        ArrayList<TypePattern> newParents = new ArrayList<TypePattern>(1);
        ArrayList<ResolvedType> newInterfaceTypes = new ArrayList<ResolvedType>(1);
        if (interfaceListSpecified != null) {
            ArrayElementValue arrayOfInterfaceTypes = (ArrayElementValue)interfaceListSpecified.getValue();
            int numberOfTypes = arrayOfInterfaceTypes.getElementValuesArraySize();
            ElementValue[] theTypes = arrayOfInterfaceTypes.getElementValuesArray();
            for (int i = 0; i < numberOfTypes; ++i) {
                ClassElementValue interfaceType = (ClassElementValue)theTypes[i];
                ResolvedType ajInterfaceType = UnresolvedType.forSignature(interfaceType.getClassString().replace("/", ".")).resolve(world);
                if (ajInterfaceType.isMissing() || !ajInterfaceType.isInterface()) {
                    AtAjAttributes.reportError("Types listed in the 'interfaces' DeclareMixin annotation value must be valid interfaces. This is invalid: " + ajInterfaceType.getName(), struct);
                    return false;
                }
                if (!ajInterfaceType.isAssignableFrom(methodReturnType)) {
                    AtAjAttributes.reportError(AtAjAttributes.getMethodForMessage(struct) + ": factory method does not return something that implements '" + ajInterfaceType.getName() + "'", struct);
                    return false;
                }
                newInterfaceTypes.add(ajInterfaceType);
                TypePattern newParent = AtAjAttributes.parseTypePattern(ajInterfaceType.getName(), struct);
                newParents.add(newParent);
            }
        } else {
            if (methodReturnType.isClass()) {
                AtAjAttributes.reportError(AtAjAttributes.getMethodForMessage(struct) + ": factory methods for a mixin must either return an interface type or specify interfaces in the annotation and return a class", struct);
                return false;
            }
            TypePattern newParent = AtAjAttributes.parseTypePattern(methodReturnType.getName(), struct);
            newInterfaceTypes.add(methodReturnType);
            newParents.add(newParent);
        }
        if (newParents.size() == 0) {
            return false;
        }
        FormalBinding[] bindings = new FormalBinding[]{};
        BindingScope binding = new BindingScope(struct.enclosingType, struct.context, bindings);
        DeclareParentsMixin dp = new DeclareParentsMixin(targetTypePattern, newParents);
        dp.resolve(binding);
        targetTypePattern = dp.getChild();
        dp.setLocation(struct.context, -1, -1);
        struct.ajAttributes.add(new AjAttribute.DeclareAttribute(dp));
        boolean hasAtLeastOneMethod = false;
        for (ResolvedType typeForDelegation : newInterfaceTypes) {
            ResolvedMember[] methods = typeForDelegation.getMethodsWithoutIterator(true, false, false).toArray(new ResolvedMember[0]);
            for (int i = 0; i < methods.length; ++i) {
                ResolvedMember method = methods[i];
                if (!method.isAbstract()) continue;
                hasAtLeastOneMethod = true;
                if (method.hasBackingGenericMember()) {
                    method = method.getBackingGenericMember();
                }
                MethodDelegateTypeMunger mdtm = new MethodDelegateTypeMunger(method, struct.enclosingType, "", targetTypePattern, struct.method.getName(), struct.method.getSignature());
                mdtm.setFieldType(methodReturnType);
                mdtm.setSourceLocation(struct.enclosingType.getSourceLocation());
                struct.ajAttributes.add(new AjAttribute.TypeMunger(mdtm));
            }
        }
        if (hasAtLeastOneMethod) {
            ResolvedMember fieldHost = AjcMemberMaker.itdAtDeclareParentsField(null, methodReturnType, struct.enclosingType);
            struct.ajAttributes.add(new AjAttribute.TypeMunger(new MethodDelegateTypeMunger.FieldHostTypeMunger(fieldHost, struct.enclosingType, targetTypePattern)));
        }
        return true;
    }

    private static boolean handleBeforeAnnotation(RuntimeAnnos runtimeAnnotations, AjAttributeMethodStruct struct, ResolvedPointcutDefinition preResolvedPointcut) {
        NameValuePair beforeAdvice;
        AnnotationGen before = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.BEFORE_ANNOTATION);
        if (before != null && (beforeAdvice = AtAjAttributes.getAnnotationElement(before, VALUE)) != null) {
            String argumentNames = AtAjAttributes.getArgNamesValue(before);
            if (argumentNames != null) {
                struct.unparsedArgumentNames = argumentNames;
            }
            FormalBinding[] bindings = new FormalBinding[]{};
            try {
                bindings = AtAjAttributes.extractBindings(struct);
            }
            catch (UnreadableDebugInfoException unreadableDebugInfoException) {
                return false;
            }
            BindingScope binding = new BindingScope(struct.enclosingType, struct.context, bindings);
            int extraArgument = AtAjAttributes.extractExtraArgument(struct.method);
            Pointcut pc = null;
            if (preResolvedPointcut != null) {
                pc = preResolvedPointcut.getPointcut();
            } else {
                pc = AtAjAttributes.parsePointcut(beforeAdvice.getValue().stringifyValue(), struct, false);
                if (pc == null) {
                    return false;
                }
                pc = pc.resolve(binding);
            }
            AtAjAttributes.setIgnoreUnboundBindingNames(pc, bindings);
            ISourceLocation sl = struct.context.makeSourceLocation(struct.bMethod.getDeclarationLineNumber(), struct.bMethod.getDeclarationOffset());
            struct.ajAttributes.add(new AjAttribute.AdviceAttribute(AdviceKind.Before, pc, extraArgument, sl.getOffset(), sl.getOffset() + 1, struct.context));
            return true;
        }
        return false;
    }

    private static boolean handleAfterAnnotation(RuntimeAnnos runtimeAnnotations, AjAttributeMethodStruct struct, ResolvedPointcutDefinition preResolvedPointcut) {
        NameValuePair afterAdvice;
        AnnotationGen after = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.AFTER_ANNOTATION);
        if (after != null && (afterAdvice = AtAjAttributes.getAnnotationElement(after, VALUE)) != null) {
            FormalBinding[] bindings = new FormalBinding[]{};
            String argumentNames = AtAjAttributes.getArgNamesValue(after);
            if (argumentNames != null) {
                struct.unparsedArgumentNames = argumentNames;
            }
            try {
                bindings = AtAjAttributes.extractBindings(struct);
            }
            catch (UnreadableDebugInfoException unreadableDebugInfoException) {
                return false;
            }
            BindingScope binding = new BindingScope(struct.enclosingType, struct.context, bindings);
            int extraArgument = AtAjAttributes.extractExtraArgument(struct.method);
            Pointcut pc = null;
            if (preResolvedPointcut != null) {
                pc = preResolvedPointcut.getPointcut();
            } else {
                pc = AtAjAttributes.parsePointcut(afterAdvice.getValue().stringifyValue(), struct, false);
                if (pc == null) {
                    return false;
                }
                pc.resolve(binding);
            }
            AtAjAttributes.setIgnoreUnboundBindingNames(pc, bindings);
            ISourceLocation sl = struct.context.makeSourceLocation(struct.bMethod.getDeclarationLineNumber(), struct.bMethod.getDeclarationOffset());
            struct.ajAttributes.add(new AjAttribute.AdviceAttribute(AdviceKind.After, pc, extraArgument, sl.getOffset(), sl.getOffset() + 1, struct.context));
            return true;
        }
        return false;
    }

    private static boolean handleAfterReturningAnnotation(RuntimeAnnos runtimeAnnotations, AjAttributeMethodStruct struct, ResolvedPointcutDefinition preResolvedPointcut, BcelMethod owningMethod) throws ReturningFormalNotDeclaredInAdviceSignatureException {
        AnnotationGen after = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.AFTERRETURNING_ANNOTATION);
        if (after != null) {
            String argumentNames;
            NameValuePair annValue = AtAjAttributes.getAnnotationElement(after, VALUE);
            NameValuePair annPointcut = AtAjAttributes.getAnnotationElement(after, POINTCUT);
            NameValuePair annReturned = AtAjAttributes.getAnnotationElement(after, RETURNING);
            String pointcut = null;
            String returned = null;
            if (annValue != null && annPointcut != null || annValue == null && annPointcut == null) {
                AtAjAttributes.reportError("@AfterReturning: either 'value' or 'poincut' must be provided, not both", struct);
                return false;
            }
            pointcut = annValue != null ? annValue.getValue().stringifyValue() : annPointcut.getValue().stringifyValue();
            if (AtAjAttributes.isNullOrEmpty(pointcut)) {
                AtAjAttributes.reportError("@AfterReturning: either 'value' or 'poincut' must be provided, not both", struct);
                return false;
            }
            if (annReturned != null) {
                returned = annReturned.getValue().stringifyValue();
                if (AtAjAttributes.isNullOrEmpty(returned)) {
                    returned = null;
                } else {
                    String[] pNames = owningMethod.getParameterNames();
                    if (pNames == null || pNames.length == 0 || !Arrays.asList(pNames).contains(returned)) {
                        throw new ReturningFormalNotDeclaredInAdviceSignatureException(returned);
                    }
                }
            }
            if ((argumentNames = AtAjAttributes.getArgNamesValue(after)) != null) {
                struct.unparsedArgumentNames = argumentNames;
            }
            FormalBinding[] bindings = new FormalBinding[]{};
            try {
                bindings = returned == null ? AtAjAttributes.extractBindings(struct) : AtAjAttributes.extractBindings(struct, returned);
            }
            catch (UnreadableDebugInfoException unreadableDebugInfoException) {
                return false;
            }
            BindingScope binding = new BindingScope(struct.enclosingType, struct.context, bindings);
            int extraArgument = AtAjAttributes.extractExtraArgument(struct.method);
            if (returned != null) {
                extraArgument |= 1;
            }
            Pointcut pc = null;
            if (preResolvedPointcut != null) {
                pc = preResolvedPointcut.getPointcut();
            } else {
                pc = AtAjAttributes.parsePointcut(pointcut, struct, false);
                if (pc == null) {
                    return false;
                }
                pc.resolve(binding);
            }
            AtAjAttributes.setIgnoreUnboundBindingNames(pc, bindings);
            ISourceLocation sl = struct.context.makeSourceLocation(struct.bMethod.getDeclarationLineNumber(), struct.bMethod.getDeclarationOffset());
            struct.ajAttributes.add(new AjAttribute.AdviceAttribute(AdviceKind.AfterReturning, pc, extraArgument, sl.getOffset(), sl.getOffset() + 1, struct.context));
            return true;
        }
        return false;
    }

    private static boolean handleAfterThrowingAnnotation(RuntimeAnnos runtimeAnnotations, AjAttributeMethodStruct struct, ResolvedPointcutDefinition preResolvedPointcut, BcelMethod owningMethod) throws ThrownFormalNotDeclaredInAdviceSignatureException {
        AnnotationGen after = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.AFTERTHROWING_ANNOTATION);
        if (after != null) {
            String argumentNames;
            NameValuePair annValue = AtAjAttributes.getAnnotationElement(after, VALUE);
            NameValuePair annPointcut = AtAjAttributes.getAnnotationElement(after, POINTCUT);
            NameValuePair annThrown = AtAjAttributes.getAnnotationElement(after, THROWING);
            String pointcut = null;
            String thrownFormal = null;
            if (annValue != null && annPointcut != null || annValue == null && annPointcut == null) {
                AtAjAttributes.reportError("@AfterThrowing: either 'value' or 'poincut' must be provided, not both", struct);
                return false;
            }
            pointcut = annValue != null ? annValue.getValue().stringifyValue() : annPointcut.getValue().stringifyValue();
            if (AtAjAttributes.isNullOrEmpty(pointcut)) {
                AtAjAttributes.reportError("@AfterThrowing: either 'value' or 'poincut' must be provided, not both", struct);
                return false;
            }
            if (annThrown != null) {
                thrownFormal = annThrown.getValue().stringifyValue();
                if (AtAjAttributes.isNullOrEmpty(thrownFormal)) {
                    thrownFormal = null;
                } else {
                    String[] pNames = owningMethod.getParameterNames();
                    if (pNames == null || pNames.length == 0 || !Arrays.asList(pNames).contains(thrownFormal)) {
                        throw new ThrownFormalNotDeclaredInAdviceSignatureException(thrownFormal);
                    }
                }
            }
            if ((argumentNames = AtAjAttributes.getArgNamesValue(after)) != null) {
                struct.unparsedArgumentNames = argumentNames;
            }
            FormalBinding[] bindings = new FormalBinding[]{};
            try {
                bindings = thrownFormal == null ? AtAjAttributes.extractBindings(struct) : AtAjAttributes.extractBindings(struct, thrownFormal);
            }
            catch (UnreadableDebugInfoException unreadableDebugInfoException) {
                return false;
            }
            BindingScope binding = new BindingScope(struct.enclosingType, struct.context, bindings);
            int extraArgument = AtAjAttributes.extractExtraArgument(struct.method);
            if (thrownFormal != null) {
                extraArgument |= 1;
            }
            Pointcut pc = null;
            if (preResolvedPointcut != null) {
                pc = preResolvedPointcut.getPointcut();
            } else {
                pc = AtAjAttributes.parsePointcut(pointcut, struct, false);
                if (pc == null) {
                    return false;
                }
                pc.resolve(binding);
            }
            AtAjAttributes.setIgnoreUnboundBindingNames(pc, bindings);
            ISourceLocation sl = struct.context.makeSourceLocation(struct.bMethod.getDeclarationLineNumber(), struct.bMethod.getDeclarationOffset());
            struct.ajAttributes.add(new AjAttribute.AdviceAttribute(AdviceKind.AfterThrowing, pc, extraArgument, sl.getOffset(), sl.getOffset() + 1, struct.context));
            return true;
        }
        return false;
    }

    private static boolean handleAroundAnnotation(RuntimeAnnos runtimeAnnotations, AjAttributeMethodStruct struct, ResolvedPointcutDefinition preResolvedPointcut) {
        NameValuePair aroundAdvice;
        AnnotationGen around = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.AROUND_ANNOTATION);
        if (around != null && (aroundAdvice = AtAjAttributes.getAnnotationElement(around, VALUE)) != null) {
            String argumentNames = AtAjAttributes.getArgNamesValue(around);
            if (argumentNames != null) {
                struct.unparsedArgumentNames = argumentNames;
            }
            FormalBinding[] bindings = new FormalBinding[]{};
            try {
                bindings = AtAjAttributes.extractBindings(struct);
            }
            catch (UnreadableDebugInfoException unreadableDebugInfoException) {
                return false;
            }
            BindingScope binding = new BindingScope(struct.enclosingType, struct.context, bindings);
            int extraArgument = AtAjAttributes.extractExtraArgument(struct.method);
            Pointcut pc = null;
            if (preResolvedPointcut != null) {
                pc = preResolvedPointcut.getPointcut();
            } else {
                pc = AtAjAttributes.parsePointcut(aroundAdvice.getValue().stringifyValue(), struct, false);
                if (pc == null) {
                    return false;
                }
                pc.resolve(binding);
            }
            AtAjAttributes.setIgnoreUnboundBindingNames(pc, bindings);
            ISourceLocation sl = struct.context.makeSourceLocation(struct.bMethod.getDeclarationLineNumber(), struct.bMethod.getDeclarationOffset());
            struct.ajAttributes.add(new AjAttribute.AdviceAttribute(AdviceKind.Around, pc, extraArgument, sl.getOffset(), sl.getOffset() + 1, struct.context));
            return true;
        }
        return false;
    }

    private static boolean handlePointcutAnnotation(RuntimeAnnos runtimeAnnotations, AjAttributeMethodStruct struct) {
        BindingScope binding;
        String argumentNames;
        AnnotationGen pointcut = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.POINTCUT_ANNOTATION);
        if (pointcut == null) {
            return false;
        }
        NameValuePair pointcutExpr = AtAjAttributes.getAnnotationElement(pointcut, VALUE);
        if (!(Type.VOID.equals(struct.method.getReturnType()) || Type.BOOLEAN.equals(struct.method.getReturnType()) && struct.method.isStatic() && struct.method.isPublic())) {
            AtAjAttributes.reportWarning("Found @Pointcut on a method not returning 'void' or not 'public static boolean'", struct);
        }
        if (struct.method.getExceptionTable() != null) {
            AtAjAttributes.reportWarning("Found @Pointcut on a method throwing exception", struct);
        }
        if ((argumentNames = AtAjAttributes.getArgNamesValue(pointcut)) != null) {
            struct.unparsedArgumentNames = argumentNames;
        }
        try {
            binding = struct.method.isAbstract() ? null : new BindingScope(struct.enclosingType, struct.context, AtAjAttributes.extractBindings(struct));
        }
        catch (UnreadableDebugInfoException e) {
            return false;
        }
        UnresolvedType[] argumentTypes = new UnresolvedType[struct.method.getArgumentTypes().length];
        for (int i = 0; i < argumentTypes.length; ++i) {
            argumentTypes[i] = UnresolvedType.forSignature(struct.method.getArgumentTypes()[i].getSignature());
        }
        Pointcut pc = null;
        if (struct.method.isAbstract()) {
            if (!(pointcutExpr != null && AtAjAttributes.isNullOrEmpty(pointcutExpr.getValue().stringifyValue()) || pointcutExpr == null)) {
                AtAjAttributes.reportError("Found defined @Pointcut on an abstract method", struct);
                return false;
            }
        } else if (pointcutExpr != null && !AtAjAttributes.isNullOrEmpty(pointcutExpr.getValue().stringifyValue())) {
            pc = AtAjAttributes.parsePointcut(pointcutExpr.getValue().stringifyValue(), struct, true);
            if (pc == null) {
                return false;
            }
            pc.setLocation(struct.context, -1, -1);
        }
        struct.ajAttributes.add(new AjAttribute.PointcutDeclarationAttribute(new LazyResolvedPointcutDefinition(struct.enclosingType, struct.method.getModifiers(), struct.method.getName(), argumentTypes, UnresolvedType.forSignature(struct.method.getReturnType().getSignature()), pc, binding)));
        return true;
    }

    private static boolean handleDeclareErrorOrWarningAnnotation(AsmManager model, RuntimeAnnos runtimeAnnotations, AjAttributeFieldStruct struct) {
        NameValuePair declareWarning;
        NameValuePair declareError;
        AnnotationGen error = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.DECLAREERROR_ANNOTATION);
        boolean hasError = false;
        if (error != null && (declareError = AtAjAttributes.getAnnotationElement(error, VALUE)) != null) {
            if (!STRING_DESC.equals(struct.field.getSignature()) || struct.field.getConstantValue() == null) {
                AtAjAttributes.reportError("@DeclareError used on a non String constant field", struct);
                return false;
            }
            Pointcut pc = AtAjAttributes.parsePointcut(declareError.getValue().stringifyValue(), struct, false);
            if (pc == null) {
                hasError = false;
            } else {
                DeclareErrorOrWarning deow = new DeclareErrorOrWarning(true, pc, struct.field.getConstantValue().toString());
                AtAjAttributes.setDeclareErrorOrWarningLocation(model, deow, struct);
                struct.ajAttributes.add(new AjAttribute.DeclareAttribute(deow));
                hasError = true;
            }
        }
        AnnotationGen warning = AtAjAttributes.getAnnotation(runtimeAnnotations, AjcMemberMaker.DECLAREWARNING_ANNOTATION);
        boolean hasWarning = false;
        if (warning != null && (declareWarning = AtAjAttributes.getAnnotationElement(warning, VALUE)) != null) {
            if (!STRING_DESC.equals(struct.field.getSignature()) || struct.field.getConstantValue() == null) {
                AtAjAttributes.reportError("@DeclareWarning used on a non String constant field", struct);
                return false;
            }
            Pointcut pc = AtAjAttributes.parsePointcut(declareWarning.getValue().stringifyValue(), struct, false);
            if (pc == null) {
                hasWarning = false;
            } else {
                DeclareErrorOrWarning deow = new DeclareErrorOrWarning(false, pc, struct.field.getConstantValue().toString());
                AtAjAttributes.setDeclareErrorOrWarningLocation(model, deow, struct);
                struct.ajAttributes.add(new AjAttribute.DeclareAttribute(deow));
                hasWarning = true;
                return true;
            }
        }
        return hasError || hasWarning;
    }

    private static void setDeclareErrorOrWarningLocation(AsmManager model, DeclareErrorOrWarning deow, AjAttributeFieldStruct struct) {
        IProgramElement ipe;
        IHierarchy top;
        IHierarchy iHierarchy = top = model == null ? null : model.getHierarchy();
        if (top != null && top.getRoot() != null && (ipe = top.findElementForLabel(top.getRoot(), IProgramElement.Kind.FIELD, struct.field.getName())) != null && ipe.getSourceLocation() != null) {
            ISourceLocation sourceLocation = ipe.getSourceLocation();
            int start = sourceLocation.getOffset();
            int end = start + struct.field.getName().length();
            deow.setLocation(struct.context, start, end);
            return;
        }
        deow.setLocation(struct.context, -1, -1);
    }

    private static String methodToString(Method method) {
        StringBuffer sb = new StringBuffer();
        sb.append(method.getName());
        sb.append(method.getSignature());
        return sb.toString();
    }

    private static FormalBinding[] extractBindings(AjAttributeMethodStruct struct) throws UnreadableDebugInfoException {
        Method method = struct.method;
        String[] argumentNames = struct.getArgumentNames();
        if (argumentNames.length != method.getArgumentTypes().length) {
            AtAjAttributes.reportError("Cannot read debug info for @Aspect to handle formal binding in pointcuts (please compile with 'javac -g' or '<javac debug='true'.../>' in Ant)", struct);
            throw new UnreadableDebugInfoException();
        }
        ArrayList<FormalBinding> bindings = new ArrayList<FormalBinding>();
        for (int i = 0; i < argumentNames.length; ++i) {
            String argumentName = argumentNames[i];
            UnresolvedType argumentType = UnresolvedType.forSignature(method.getArgumentTypes()[i].getSignature());
            if (AjcMemberMaker.TYPEX_JOINPOINT.equals(argumentType) || AjcMemberMaker.TYPEX_PROCEEDINGJOINPOINT.equals(argumentType) || AjcMemberMaker.TYPEX_STATICJOINPOINT.equals(argumentType) || AjcMemberMaker.TYPEX_ENCLOSINGSTATICJOINPOINT.equals(argumentType) || AjcMemberMaker.AROUND_CLOSURE_TYPE.equals(argumentType)) {
                bindings.add(new FormalBinding.ImplicitFormalBinding(argumentType, argumentName, i));
                continue;
            }
            bindings.add(new FormalBinding(argumentType, argumentName, i));
        }
        return bindings.toArray(new FormalBinding[0]);
    }

    private static FormalBinding[] extractBindings(AjAttributeMethodStruct struct, String excludeFormal) throws UnreadableDebugInfoException {
        FormalBinding[] bindings = AtAjAttributes.extractBindings(struct);
        for (int i = 0; i < bindings.length; ++i) {
            FormalBinding binding = bindings[i];
            if (!binding.getName().equals(excludeFormal)) continue;
            bindings[i] = new FormalBinding.ImplicitFormalBinding(binding.getType(), binding.getName(), binding.getIndex());
            break;
        }
        return bindings;
    }

    private static int extractExtraArgument(Method method) {
        Type[] methodArgs = method.getArgumentTypes();
        String[] sigs = new String[methodArgs.length];
        for (int i = 0; i < methodArgs.length; ++i) {
            sigs[i] = methodArgs[i].getSignature();
        }
        return AtAjAttributes.extractExtraArgument(sigs);
    }

    public static int extractExtraArgument(String[] argumentSignatures) {
        int extraArgument = 0;
        for (int i = 0; i < argumentSignatures.length; ++i) {
            if (AjcMemberMaker.TYPEX_JOINPOINT.getSignature().equals(argumentSignatures[i])) {
                extraArgument |= 2;
                continue;
            }
            if (AjcMemberMaker.TYPEX_PROCEEDINGJOINPOINT.getSignature().equals(argumentSignatures[i])) {
                extraArgument |= 2;
                continue;
            }
            if (AjcMemberMaker.TYPEX_STATICJOINPOINT.getSignature().equals(argumentSignatures[i])) {
                extraArgument |= 4;
                continue;
            }
            if (!AjcMemberMaker.TYPEX_ENCLOSINGSTATICJOINPOINT.getSignature().equals(argumentSignatures[i])) continue;
            extraArgument |= 8;
        }
        return extraArgument;
    }

    private static AnnotationGen getAnnotation(RuntimeAnnos rvs, UnresolvedType annotationType) {
        String annotationTypeName = annotationType.getName();
        for (AnnotationGen rv : rvs.getAnnotations()) {
            if (!annotationTypeName.equals(rv.getTypeName())) continue;
            return rv;
        }
        return null;
    }

    private static NameValuePair getAnnotationElement(AnnotationGen annotation, String elementName) {
        for (NameValuePair element : annotation.getValues()) {
            if (!elementName.equals(element.getNameString())) continue;
            return element;
        }
        return null;
    }

    private static String getArgNamesValue(AnnotationGen anno) {
        List<NameValuePair> elements = anno.getValues();
        for (NameValuePair element : elements) {
            if (!ARGNAMES.equals(element.getNameString())) continue;
            return element.getValue().stringifyValue();
        }
        return null;
    }

    private static String lastbit(String fqname) {
        int i = fqname.lastIndexOf(".");
        if (i == -1) {
            return fqname;
        }
        return fqname.substring(i + 1);
    }

    private static String[] getMethodArgumentNames(Method method, String argNamesFromAnnotation, AjAttributeMethodStruct methodStruct) {
        String[] argNames;
        if (method.getArgumentTypes().length == 0) {
            return EMPTY_STRINGS;
        }
        int startAtStackIndex = method.isStatic() ? 0 : 1;
        ArrayList<MethodArgument> arguments = new ArrayList<MethodArgument>();
        LocalVariableTable lt = method.getLocalVariableTable();
        if (lt != null) {
            LocalVariable[] lvt = lt.getLocalVariableTable();
            for (int j = 0; j < lvt.length; ++j) {
                LocalVariable localVariable = lvt[j];
                if (localVariable != null) {
                    if (localVariable.getStartPC() != 0 || localVariable.getIndex() < startAtStackIndex) continue;
                    arguments.add(new MethodArgument(localVariable.getName(), localVariable.getIndex()));
                    continue;
                }
                String typename = methodStruct.enclosingType != null ? methodStruct.enclosingType.getName() : "";
                System.err.println("AspectJ: 348488 debug: unusual local variable table for method " + typename + "." + method.getName());
            }
            if (arguments.size() == 0) {
                String[] argNames2;
                if (argNamesFromAnnotation != null && (argNames2 = AtAjAttributes.extractArgNamesFromAnnotationValue(method, argNamesFromAnnotation, methodStruct)).length != 0) {
                    return argNames2;
                }
                LocalVariable localVariable = lvt[0];
                if (localVariable != null && localVariable.getStartPC() != 0) {
                    for (int j = 0; j < lvt.length && arguments.size() < method.getArgumentTypes().length; ++j) {
                        localVariable = lvt[j];
                        if (localVariable.getIndex() < startAtStackIndex) continue;
                        arguments.add(new MethodArgument(localVariable.getName(), localVariable.getIndex()));
                    }
                }
            }
        } else if (argNamesFromAnnotation != null && (argNames = AtAjAttributes.extractArgNamesFromAnnotationValue(method, argNamesFromAnnotation, methodStruct)) != null) {
            return argNames;
        }
        if (arguments.size() != method.getArgumentTypes().length) {
            return EMPTY_STRINGS;
        }
        Collections.sort(arguments, new Comparator<MethodArgument>(){

            @Override
            public int compare(MethodArgument mo, MethodArgument mo1) {
                if (mo.indexOnStack == mo1.indexOnStack) {
                    return 0;
                }
                if (mo.indexOnStack > mo1.indexOnStack) {
                    return 1;
                }
                return -1;
            }
        });
        String[] argumentNames = new String[arguments.size()];
        int i = 0;
        for (MethodArgument methodArgument : arguments) {
            argumentNames[i++] = methodArgument.name;
        }
        return argumentNames;
    }

    private static String[] extractArgNamesFromAnnotationValue(Method method, String argNamesFromAnnotation, AjAttributeMethodStruct methodStruct) {
        StringTokenizer st = new StringTokenizer(argNamesFromAnnotation, " ,");
        ArrayList<String> args = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            args.add(st.nextToken());
        }
        if (args.size() != method.getArgumentTypes().length) {
            StringBuffer shortString = new StringBuffer().append(AtAjAttributes.lastbit(method.getReturnType().toString())).append(" ").append(method.getName());
            if (method.getArgumentTypes().length > 0) {
                shortString.append("(");
                for (int i = 0; i < method.getArgumentTypes().length; ++i) {
                    shortString.append(AtAjAttributes.lastbit(method.getArgumentTypes()[i].toString()));
                    if (i + 1 >= method.getArgumentTypes().length) continue;
                    shortString.append(",");
                }
                shortString.append(")");
            }
            AtAjAttributes.reportError("argNames annotation value does not specify the right number of argument names for the method '" + shortString.toString() + "'", methodStruct);
            return EMPTY_STRINGS;
        }
        return args.toArray(new String[0]);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.length() <= 0;
    }

    private static void setIgnoreUnboundBindingNames(Pointcut pointcut, FormalBinding[] bindings) {
        ArrayList<String> ignores = new ArrayList<String>();
        for (int i = 0; i < bindings.length; ++i) {
            FormalBinding formalBinding = bindings[i];
            if (!(formalBinding instanceof FormalBinding.ImplicitFormalBinding)) continue;
            ignores.add(formalBinding.getName());
        }
        pointcut.m_ignoreUnboundBindingForNames = ignores.toArray(new String[ignores.size()]);
    }

    private static void reportError(String message, AjAttributeStruct location) {
        if (!location.handler.isIgnoring(IMessage.ERROR)) {
            location.handler.handleMessage(new Message(message, location.enclosingType.getSourceLocation(), true));
        }
    }

    private static void reportWarning(String message, AjAttributeStruct location) {
        if (!location.handler.isIgnoring(IMessage.WARNING)) {
            location.handler.handleMessage(new Message(message, location.enclosingType.getSourceLocation(), false));
        }
    }

    private static Pointcut parsePointcut(String pointcutString, AjAttributeStruct struct, boolean allowIf) {
        try {
            PatternParser parser = new PatternParser(pointcutString, struct.context);
            Pointcut pointcut = parser.parsePointcut();
            parser.checkEof();
            pointcut.check(null, struct.enclosingType.getWorld());
            if (!allowIf && pointcutString.indexOf("if()") >= 0 && AtAjAttributes.hasIf(pointcut)) {
                AtAjAttributes.reportError("if() pointcut is not allowed at this pointcut location '" + pointcutString + "'", struct);
                return null;
            }
            pointcut.setLocation(struct.context, -1, -1);
            return pointcut;
        }
        catch (ParserException e) {
            AtAjAttributes.reportError("Invalid pointcut '" + pointcutString + "': " + e.toString() + (e.getLocation() == null ? "" : " at position " + e.getLocation().getStart()), struct);
            return null;
        }
    }

    private static boolean hasIf(Pointcut pointcut) {
        IfFinder visitor = new IfFinder();
        pointcut.accept(visitor, null);
        return visitor.hasIf;
    }

    private static TypePattern parseTypePattern(String patternString, AjAttributeStruct location) {
        try {
            TypePattern typePattern = new PatternParser(patternString).parseTypePattern();
            typePattern.setLocation(location.context, -1, -1);
            return typePattern;
        }
        catch (ParserException e) {
            AtAjAttributes.reportError("Invalid type pattern'" + patternString + "' : " + e.getLocation(), location);
            return null;
        }
    }

    static class ReturningFormalNotDeclaredInAdviceSignatureException
    extends Exception {
        private final String formalName;

        public ReturningFormalNotDeclaredInAdviceSignatureException(String formalName) {
            this.formalName = formalName;
        }

        public String getFormalName() {
            return this.formalName;
        }
    }

    static class ThrownFormalNotDeclaredInAdviceSignatureException
    extends Exception {
        private final String formalName;

        public ThrownFormalNotDeclaredInAdviceSignatureException(String formalName) {
            this.formalName = formalName;
        }

        public String getFormalName() {
            return this.formalName;
        }
    }

    private static class UnreadableDebugInfoException
    extends Exception {
        private UnreadableDebugInfoException() {
        }
    }

    public static class LazyResolvedPointcutDefinition
    extends ResolvedPointcutDefinition {
        private final Pointcut m_pointcutUnresolved;
        private final IScope m_binding;
        private Pointcut m_lazyPointcut = null;

        public LazyResolvedPointcutDefinition(UnresolvedType declaringType, int modifiers, String name, UnresolvedType[] parameterTypes, UnresolvedType returnType, Pointcut pointcut, IScope binding) {
            super(declaringType, modifiers, name, parameterTypes, returnType, Pointcut.makeMatchesNothing(Pointcut.RESOLVED));
            this.m_pointcutUnresolved = pointcut;
            this.m_binding = binding;
        }

        @Override
        public Pointcut getPointcut() {
            if (this.m_lazyPointcut == null && this.m_pointcutUnresolved == null) {
                this.m_lazyPointcut = Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
            }
            if (this.m_lazyPointcut == null && this.m_pointcutUnresolved != null) {
                this.m_lazyPointcut = this.m_pointcutUnresolved.resolve(this.m_binding);
                this.m_lazyPointcut.copyLocationFrom(this.m_pointcutUnresolved);
            }
            return this.m_lazyPointcut;
        }
    }

    private static class MethodArgument {
        String name;
        int indexOnStack;

        public MethodArgument(String name, int indexOnStack) {
            this.name = name;
            this.indexOnStack = indexOnStack;
        }
    }

    private static class AjAttributeFieldStruct
    extends AjAttributeStruct {
        final Field field;

        public AjAttributeFieldStruct(Field field, BcelField bField, ResolvedType type, ISourceContext sourceContext, IMessageHandler messageHandler) {
            super(type, sourceContext, messageHandler);
            this.field = field;
        }
    }

    private static class AjAttributeMethodStruct
    extends AjAttributeStruct {
        private String[] m_argumentNamesLazy = null;
        public String unparsedArgumentNames = null;
        final Method method;
        final BcelMethod bMethod;

        public AjAttributeMethodStruct(Method method, BcelMethod bMethod, ResolvedType type, ISourceContext sourceContext, IMessageHandler messageHandler) {
            super(type, sourceContext, messageHandler);
            this.method = method;
            this.bMethod = bMethod;
        }

        public String[] getArgumentNames() {
            if (this.m_argumentNamesLazy == null) {
                this.m_argumentNamesLazy = AtAjAttributes.getMethodArgumentNames(this.method, this.unparsedArgumentNames, this);
            }
            return this.m_argumentNamesLazy;
        }
    }

    private static class AjAttributeStruct {
        List<AjAttribute> ajAttributes = new ArrayList<AjAttribute>();
        final ResolvedType enclosingType;
        final ISourceContext context;
        final IMessageHandler handler;

        public AjAttributeStruct(ResolvedType type, ISourceContext sourceContext, IMessageHandler messageHandler) {
            this.enclosingType = type;
            this.context = sourceContext;
            this.handler = messageHandler;
        }
    }
}

