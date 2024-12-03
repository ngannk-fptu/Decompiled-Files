/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aspectj.apache.bcel.classfile.ClassFormatException;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Signature;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.generic.FieldGen;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.InvokeInstruction;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.asm.AsmManager;
import org.aspectj.asm.IProgramElement;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.bridge.WeaveMessage;
import org.aspectj.bridge.context.CompilationAndWeavingContext;
import org.aspectj.bridge.context.ContextToken;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.AnnotationOnTypeMunger;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberUtils;
import org.aspectj.weaver.MethodDelegateTypeMunger;
import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.aspectj.weaver.NewFieldTypeMunger;
import org.aspectj.weaver.NewMemberClassTypeMunger;
import org.aspectj.weaver.NewMethodTypeMunger;
import org.aspectj.weaver.NewParentTypeMunger;
import org.aspectj.weaver.PerObjectInterfaceTypeMunger;
import org.aspectj.weaver.PrivilegedAccessMunger;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.TypeVariableReference;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.WeaverStateInfo;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelAnnotation;
import org.aspectj.weaver.bcel.BcelClassWeaver;
import org.aspectj.weaver.bcel.BcelField;
import org.aspectj.weaver.bcel.BcelMethod;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.LazyMethodGen;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.model.AsmRelationshipProvider;
import org.aspectj.weaver.patterns.DeclareAnnotation;
import org.aspectj.weaver.patterns.Pointcut;

public class BcelTypeMunger
extends ConcreteTypeMunger {
    private volatile int hashCode = 0;

    public BcelTypeMunger(ResolvedTypeMunger munger, ResolvedType aspectType) {
        super(munger, aspectType);
    }

    public String toString() {
        return "(BcelTypeMunger " + this.getMunger() + ")";
    }

    @Override
    public boolean shouldOverwrite() {
        return false;
    }

    public boolean munge(BcelClassWeaver weaver) {
        WeaverStateInfo typeWeaverState;
        ContextToken tok = CompilationAndWeavingContext.enteringPhase(31, this);
        boolean changed = false;
        boolean worthReporting = true;
        if (weaver.getWorld().isOverWeaving() && (typeWeaverState = weaver.getLazyClassGen().getType().getWeaverState()) != null && typeWeaverState.isAspectAlreadyApplied(this.getAspectType())) {
            return false;
        }
        if (this.munger.getKind() == ResolvedTypeMunger.Field) {
            changed = this.mungeNewField(weaver, (NewFieldTypeMunger)this.munger);
        } else if (this.munger.getKind() == ResolvedTypeMunger.Method) {
            changed = this.mungeNewMethod(weaver, (NewMethodTypeMunger)this.munger);
        } else if (this.munger.getKind() == ResolvedTypeMunger.InnerClass) {
            changed = this.mungeNewMemberType(weaver, (NewMemberClassTypeMunger)this.munger);
        } else if (this.munger.getKind() == ResolvedTypeMunger.MethodDelegate2) {
            changed = this.mungeMethodDelegate(weaver, (MethodDelegateTypeMunger)this.munger);
        } else if (this.munger.getKind() == ResolvedTypeMunger.FieldHost) {
            changed = this.mungeFieldHost(weaver, (MethodDelegateTypeMunger.FieldHostTypeMunger)this.munger);
        } else if (this.munger.getKind() == ResolvedTypeMunger.PerObjectInterface) {
            changed = this.mungePerObjectInterface(weaver, (PerObjectInterfaceTypeMunger)this.munger);
            worthReporting = false;
        } else if (this.munger.getKind() == ResolvedTypeMunger.PerTypeWithinInterface) {
            changed = this.mungePerTypeWithinTransformer(weaver);
            worthReporting = false;
        } else if (this.munger.getKind() == ResolvedTypeMunger.PrivilegedAccess) {
            changed = this.mungePrivilegedAccess(weaver, (PrivilegedAccessMunger)this.munger);
            worthReporting = false;
        } else if (this.munger.getKind() == ResolvedTypeMunger.Constructor) {
            changed = this.mungeNewConstructor(weaver, (NewConstructorTypeMunger)this.munger);
        } else if (this.munger.getKind() == ResolvedTypeMunger.Parent) {
            changed = this.mungeNewParent(weaver, (NewParentTypeMunger)this.munger);
        } else if (this.munger.getKind() == ResolvedTypeMunger.AnnotationOnType) {
            changed = this.mungeNewAnnotationOnType(weaver, (AnnotationOnTypeMunger)this.munger);
            worthReporting = false;
        } else {
            throw new RuntimeException("unimplemented");
        }
        if (changed && this.munger.changesPublicSignature()) {
            WeaverStateInfo info = weaver.getLazyClassGen().getOrCreateWeaverStateInfo(weaver.getReweavableMode());
            info.addConcreteMunger(this);
        }
        if (changed && worthReporting) {
            ResolvedType declaringAspect = null;
            AsmManager model = ((BcelWorld)this.getWorld()).getModelAsAsmManager();
            if (model != null) {
                if (this.munger instanceof NewParentTypeMunger) {
                    NewParentTypeMunger nptMunger = (NewParentTypeMunger)this.munger;
                    declaringAspect = nptMunger.getDeclaringType();
                    if (declaringAspect.isParameterizedOrGenericType()) {
                        declaringAspect = declaringAspect.getRawType();
                    }
                    ResolvedType thisAspect = this.getAspectType();
                    AsmRelationshipProvider.addRelationship(model, weaver.getLazyClassGen().getType(), this.munger, thisAspect);
                    if (!thisAspect.equals(declaringAspect)) {
                        ResolvedType target = weaver.getLazyClassGen().getType();
                        ResolvedType newParent = nptMunger.getNewParent();
                        IProgramElement thisAspectNode = model.getHierarchy().findElementForType(thisAspect.getPackageName(), thisAspect.getClassName());
                        Map<String, List<String>> declareParentsMap = thisAspectNode.getDeclareParentsMap();
                        if (declareParentsMap == null) {
                            declareParentsMap = new HashMap<String, List<String>>();
                            thisAspectNode.setDeclareParentsMap(declareParentsMap);
                        }
                        String tname = target.getName();
                        String pname = newParent.getName();
                        List<String> newparents = declareParentsMap.get(tname);
                        if (newparents == null) {
                            newparents = new ArrayList<String>();
                            declareParentsMap.put(tname, newparents);
                        }
                        newparents.add(pname);
                        AsmRelationshipProvider.addRelationship(model, weaver.getLazyClassGen().getType(), this.munger, declaringAspect);
                    }
                } else {
                    declaringAspect = this.getAspectType();
                    AsmRelationshipProvider.addRelationship(model, weaver.getLazyClassGen().getType(), this.munger, declaringAspect);
                }
            }
        }
        if (changed && worthReporting && this.munger != null && !weaver.getWorld().getMessageHandler().isIgnoring(IMessage.WEAVEINFO)) {
            String tName = weaver.getLazyClassGen().getType().getSourceLocation().getSourceFile().getName();
            tName = tName.indexOf("no debug info available") != -1 ? "no debug info available" : this.getShortname(weaver.getLazyClassGen().getType().getSourceLocation().getSourceFile().getPath());
            String fName = this.getShortname(this.getAspectType().getSourceLocation().getSourceFile().getPath());
            if (this.munger.getKind().equals(ResolvedTypeMunger.Parent)) {
                NewParentTypeMunger parentTM = (NewParentTypeMunger)this.munger;
                if (parentTM.isMixin()) {
                    weaver.getWorld().getMessageHandler().handleMessage(WeaveMessage.constructWeavingMessage(WeaveMessage.WEAVEMESSAGE_MIXIN, new String[]{parentTM.getNewParent().getName(), fName, weaver.getLazyClassGen().getType().getName(), tName}, weaver.getLazyClassGen().getClassName(), this.getAspectType().getName()));
                } else if (parentTM.getNewParent().isInterface()) {
                    weaver.getWorld().getMessageHandler().handleMessage(WeaveMessage.constructWeavingMessage(WeaveMessage.WEAVEMESSAGE_DECLAREPARENTSIMPLEMENTS, new String[]{weaver.getLazyClassGen().getType().getName(), tName, parentTM.getNewParent().getName(), fName}, weaver.getLazyClassGen().getClassName(), this.getAspectType().getName()));
                } else {
                    weaver.getWorld().getMessageHandler().handleMessage(WeaveMessage.constructWeavingMessage(WeaveMessage.WEAVEMESSAGE_DECLAREPARENTSEXTENDS, new String[]{weaver.getLazyClassGen().getType().getName(), tName, parentTM.getNewParent().getName(), fName}));
                }
            } else if (!this.munger.getKind().equals(ResolvedTypeMunger.FieldHost)) {
                ResolvedMember declaredSig = this.munger.getSignature();
                String fromString = fName + ":'" + declaredSig + "'";
                String kindString = this.munger.getKind().toString().toLowerCase();
                if (kindString.equals("innerclass")) {
                    kindString = "member class";
                    fromString = fName;
                }
                weaver.getWorld().getMessageHandler().handleMessage(WeaveMessage.constructWeavingMessage(WeaveMessage.WEAVEMESSAGE_ITD, new String[]{weaver.getLazyClassGen().getType().getName(), tName, kindString, this.getAspectType().getName(), fromString}, weaver.getLazyClassGen().getClassName(), this.getAspectType().getName()));
            }
        }
        CompilationAndWeavingContext.leavingPhase(tok);
        return changed;
    }

    private String getShortname(String path) {
        int takefrom = path.lastIndexOf(47);
        if (takefrom == -1) {
            takefrom = path.lastIndexOf(92);
        }
        return path.substring(takefrom + 1);
    }

    private boolean mungeNewAnnotationOnType(BcelClassWeaver weaver, AnnotationOnTypeMunger munger) {
        try {
            BcelAnnotation anno = (BcelAnnotation)munger.getNewAnnotation();
            weaver.getLazyClassGen().addAnnotation(anno.getBcelAnnotation());
        }
        catch (ClassCastException cce) {
            throw new IllegalStateException("DiagnosticsFor318237: The typemunger " + munger + " contains an annotation of type " + munger.getNewAnnotation().getClass().getName() + " when it should be a BcelAnnotation", cce);
        }
        return true;
    }

    private boolean mungeNewParent(BcelClassWeaver weaver, NewParentTypeMunger typeTransformer) {
        LazyClassGen newParentTarget = weaver.getLazyClassGen();
        ResolvedType newParent = typeTransformer.getNewParent();
        boolean performChange = true;
        performChange = this.enforceDecpRule1_abstractMethodsImplemented(weaver, typeTransformer.getSourceLocation(), newParentTarget, newParent);
        performChange = this.enforceDecpRule2_cantExtendFinalClass(weaver, typeTransformer.getSourceLocation(), newParentTarget, newParent) && performChange;
        List<ResolvedMember> methods = newParent.getMethodsWithoutIterator(false, true, false);
        for (ResolvedMember method : methods) {
            LazyMethodGen subMethod;
            if (method.getName().equals("<init>") || (subMethod = this.findMatchingMethod(newParentTarget, method)) == null || subMethod.isBridgeMethod() || subMethod.isSynthetic() && method.isSynthetic() || subMethod.isStatic() && subMethod.getName().startsWith("access$")) continue;
            performChange = this.enforceDecpRule3_visibilityChanges(weaver, newParent, method, subMethod) && performChange;
            performChange = this.enforceDecpRule4_compatibleReturnTypes(weaver, method, subMethod) && performChange;
            performChange = this.enforceDecpRule5_cantChangeFromStaticToNonstatic(weaver, typeTransformer.getSourceLocation(), method, subMethod) && performChange;
        }
        if (!performChange) {
            return false;
        }
        if (newParent.isClass()) {
            if (!this.attemptToModifySuperCalls(weaver, newParentTarget, newParent)) {
                return false;
            }
            newParentTarget.setSuperClass(newParent);
        } else {
            newParentTarget.addInterface(newParent, this.getSourceLocation());
        }
        return true;
    }

    private boolean enforceDecpRule1_abstractMethodsImplemented(BcelClassWeaver weaver, ISourceLocation mungerLoc, LazyClassGen newParentTarget, ResolvedType newParent) {
        if (newParentTarget.isAbstract() || newParentTarget.isInterface()) {
            return true;
        }
        boolean ruleCheckingSucceeded = true;
        List<ResolvedMember> newParentMethods = newParent.getMethodsWithoutIterator(false, true, false);
        for (ResolvedMember newParentMethod : newParentMethods) {
            String newParentMethodName = newParentMethod.getName();
            if (!newParentMethod.isAbstract() || newParentMethodName.startsWith("ajc$interField")) continue;
            ResolvedMember discoveredImpl = null;
            List<ResolvedMember> targetMethods = newParentTarget.getType().getMethodsWithoutIterator(false, true, false);
            for (ResolvedMember targetMethod : targetMethods) {
                if (targetMethod.isAbstract() || !targetMethod.getName().equals(newParentMethodName)) continue;
                String newParentMethodSig = newParentMethod.getParameterSignature();
                String targetMethodSignature = targetMethod.getParameterSignature();
                if (targetMethodSignature.equals(newParentMethodSig)) {
                    discoveredImpl = targetMethod;
                } else if (targetMethod.hasBackingGenericMember() && targetMethod.getBackingGenericMember().getParameterSignature().equals(newParentMethodSig)) {
                    discoveredImpl = targetMethod;
                } else if (newParentMethod.hasBackingGenericMember()) {
                    UnresolvedType[] targetMethodGenericParameterTypes;
                    if (newParentMethod.getBackingGenericMember().getParameterSignature().equals(targetMethodSignature)) {
                        discoveredImpl = targetMethod;
                    } else if (targetMethod instanceof BcelMethod && (targetMethodGenericParameterTypes = targetMethod.getGenericParameterTypes()) != null) {
                        StringBuilder b = new StringBuilder("(");
                        for (UnresolvedType p : targetMethodGenericParameterTypes) {
                            b.append(p.getSignature());
                        }
                        b.append(')');
                        if (b.toString().equals(newParentMethodSig)) {
                            discoveredImpl = targetMethod;
                        }
                    }
                }
                if (discoveredImpl == null) continue;
                break;
            }
            if (discoveredImpl != null) continue;
            boolean satisfiedByITD = false;
            for (ConcreteTypeMunger m : newParentTarget.getType().getInterTypeMungersIncludingSupers()) {
                if (m.getMunger() != null && m.getMunger().getKind() == ResolvedTypeMunger.Method) {
                    ResolvedMember sig = m.getSignature();
                    if (Modifier.isAbstract(sig.getModifiers())) continue;
                    if (m.isTargetTypeParameterized()) {
                        ReferenceType genericOnType = this.getWorld().resolve(sig.getDeclaringType()).getGenericType();
                        ResolvedType actualOccurrence = newParent.discoverActualOccurrenceOfTypeInHierarchy(genericOnType);
                        if (actualOccurrence == null) {
                            actualOccurrence = newParentTarget.getType().discoverActualOccurrenceOfTypeInHierarchy(genericOnType);
                        }
                        m = m.parameterizedFor(actualOccurrence);
                        sig = m.getSignature();
                    }
                    if (!ResolvedType.matches(AjcMemberMaker.interMethod(sig, m.getAspectType(), sig.getDeclaringType().resolve(weaver.getWorld()).isInterface()), newParentMethod)) continue;
                    satisfiedByITD = true;
                    continue;
                }
                if (m.getMunger() == null || m.getMunger().getKind() != ResolvedTypeMunger.MethodDelegate2) continue;
                satisfiedByITD = true;
            }
            if (satisfiedByITD) continue;
            this.error(weaver, "The type " + newParentTarget.getName() + " must implement the inherited abstract method " + newParentMethod.getDeclaringType() + "." + newParentMethodName + newParentMethod.getParameterSignature(), newParentTarget.getType().getSourceLocation(), new ISourceLocation[]{newParentMethod.getSourceLocation(), mungerLoc});
            ruleCheckingSucceeded = false;
        }
        return ruleCheckingSucceeded;
    }

    private boolean enforceDecpRule2_cantExtendFinalClass(BcelClassWeaver weaver, ISourceLocation transformerLoc, LazyClassGen targetType, ResolvedType newParent) {
        if (newParent.isFinal()) {
            this.error(weaver, "Cannot make type " + targetType.getName() + " extend final class " + newParent.getName(), targetType.getType().getSourceLocation(), new ISourceLocation[]{transformerLoc});
            return false;
        }
        return true;
    }

    private boolean enforceDecpRule3_visibilityChanges(BcelClassWeaver weaver, ResolvedType newParent, ResolvedMember superMethod, LazyMethodGen subMethod) {
        boolean cont = true;
        if (Modifier.isPublic(superMethod.getModifiers())) {
            if (subMethod.isProtected() || subMethod.isDefault() || subMethod.isPrivate()) {
                weaver.getWorld().getMessageHandler().handleMessage(MessageUtil.error("Cannot reduce the visibility of the inherited method '" + superMethod + "' from " + newParent.getName(), superMethod.getSourceLocation()));
                cont = false;
            }
        } else if (Modifier.isProtected(superMethod.getModifiers())) {
            if (subMethod.isDefault() || subMethod.isPrivate()) {
                weaver.getWorld().getMessageHandler().handleMessage(MessageUtil.error("Cannot reduce the visibility of the inherited method '" + superMethod + "' from " + newParent.getName(), superMethod.getSourceLocation()));
                cont = false;
            }
        } else if (superMethod.isDefault() && subMethod.isPrivate()) {
            weaver.getWorld().getMessageHandler().handleMessage(MessageUtil.error("Cannot reduce the visibility of the inherited method '" + superMethod + "' from " + newParent.getName(), superMethod.getSourceLocation()));
            cont = false;
        }
        return cont;
    }

    private boolean enforceDecpRule4_compatibleReturnTypes(BcelClassWeaver weaver, ResolvedMember superMethod, LazyMethodGen subMethod) {
        boolean cont = true;
        String superReturnTypeSig = superMethod.getGenericReturnType().getSignature();
        String subReturnTypeSig = subMethod.getGenericReturnTypeSignature();
        if (!(superReturnTypeSig = superReturnTypeSig.replace('.', '/')).equals(subReturnTypeSig = subReturnTypeSig.replace('.', '/'))) {
            ResolvedType subType = weaver.getWorld().resolve(subMethod.getReturnType());
            ResolvedType superType = weaver.getWorld().resolve(superMethod.getReturnType());
            if (!superType.isAssignableFrom(subType)) {
                weaver.getWorld().getMessageHandler().handleMessage(MessageUtil.error("The return type is incompatible with " + superMethod.getDeclaringType() + "." + superMethod.getName() + superMethod.getParameterSignature(), subMethod.getSourceLocation()));
                cont = false;
            }
        }
        return cont;
    }

    private boolean enforceDecpRule5_cantChangeFromStaticToNonstatic(BcelClassWeaver weaver, ISourceLocation mungerLoc, ResolvedMember superMethod, LazyMethodGen subMethod) {
        boolean superMethodStatic = Modifier.isStatic(superMethod.getModifiers());
        if (superMethodStatic && !subMethod.isStatic()) {
            this.error(weaver, "This instance method " + subMethod.getName() + subMethod.getParameterSignature() + " cannot override the static method from " + superMethod.getDeclaringType().getName(), subMethod.getSourceLocation(), new ISourceLocation[]{mungerLoc});
            return false;
        }
        if (!superMethodStatic && subMethod.isStatic()) {
            this.error(weaver, "The static method " + subMethod.getName() + subMethod.getParameterSignature() + " cannot hide the instance method from " + superMethod.getDeclaringType().getName(), subMethod.getSourceLocation(), new ISourceLocation[]{mungerLoc});
            return false;
        }
        return true;
    }

    public void error(BcelClassWeaver weaver, String text, ISourceLocation primaryLoc, ISourceLocation[] extraLocs) {
        Message msg = new Message(text, primaryLoc, true, extraLocs);
        weaver.getWorld().getMessageHandler().handleMessage(msg);
    }

    private LazyMethodGen findMatchingMethod(LazyClassGen type, ResolvedMember searchMethod) {
        String searchName = searchMethod.getName();
        String searchSig = searchMethod.getParameterSignature();
        for (LazyMethodGen method : type.getMethodGens()) {
            if (!method.getName().equals(searchName) || !method.getParameterSignature().equals(searchSig)) continue;
            return method;
        }
        return null;
    }

    public boolean attemptToModifySuperCalls(BcelClassWeaver weaver, LazyClassGen newParentTarget, ResolvedType newParent) {
        ResolvedType currentParentType = newParentTarget.getSuperClass();
        if (currentParentType.getGenericType() != null) {
            currentParentType = currentParentType.getGenericType();
        }
        String currentParent = currentParentType.getName();
        if (newParent.getGenericType() != null) {
            newParent = newParent.getGenericType();
        }
        List<LazyMethodGen> mgs = newParentTarget.getMethodGens();
        for (LazyMethodGen aMethod : mgs) {
            if (!LazyMethodGen.isConstructor(aMethod)) continue;
            InstructionList insList = aMethod.getBody();
            for (InstructionHandle handle = insList.getStart(); handle != null; handle = handle.getNext()) {
                if (handle.getInstruction().opcode != 183) continue;
                ConstantPool cpg = newParentTarget.getConstantPool();
                InvokeInstruction invokeSpecial = (InvokeInstruction)handle.getInstruction();
                if (!invokeSpecial.getClassName(cpg).equals(currentParent) || !invokeSpecial.getMethodName(cpg).equals("<init>")) continue;
                ResolvedMember newCtor = this.getConstructorWithSignature(newParent, invokeSpecial.getSignature(cpg));
                if (newCtor == null) {
                    boolean satisfiedByITDC = false;
                    Iterator<ConcreteTypeMunger> ii = newParentTarget.getType().getInterTypeMungersIncludingSupers().iterator();
                    while (ii.hasNext() && !satisfiedByITDC) {
                        ConcreteTypeMunger m = ii.next();
                        if (!(m.getMunger() instanceof NewConstructorTypeMunger) || !m.getSignature().getSignature().equals(invokeSpecial.getSignature(cpg))) continue;
                        satisfiedByITDC = true;
                    }
                    if (!satisfiedByITDC) {
                        String csig = this.createReadableCtorSig(newParent, cpg, invokeSpecial);
                        weaver.getWorld().getMessageHandler().handleMessage(MessageUtil.error("Unable to modify hierarchy for " + newParentTarget.getClassName() + " - the constructor " + csig + " is missing", this.getSourceLocation()));
                        return false;
                    }
                }
                int idx = cpg.addMethodref(newParent.getName(), invokeSpecial.getMethodName(cpg), invokeSpecial.getSignature(cpg));
                invokeSpecial.setIndex(idx);
            }
        }
        return true;
    }

    private String createReadableCtorSig(ResolvedType newParent, ConstantPool cpg, InvokeInstruction invokeSpecial) {
        StringBuffer sb = new StringBuffer();
        Type[] ctorArgs = invokeSpecial.getArgumentTypes(cpg);
        sb.append(newParent.getClassName());
        sb.append("(");
        for (int i = 0; i < ctorArgs.length; ++i) {
            String argtype = ctorArgs[i].toString();
            if (argtype.lastIndexOf(".") != -1) {
                sb.append(argtype.substring(argtype.lastIndexOf(".") + 1));
            } else {
                sb.append(argtype);
            }
            if (i + 1 >= ctorArgs.length) continue;
            sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    private ResolvedMember getConstructorWithSignature(ResolvedType type, String searchSig) {
        for (ResolvedMember method : type.getDeclaredJavaMethods()) {
            if (!MemberUtils.isConstructor(method) || !method.getSignature().equals(searchSig)) continue;
            return method;
        }
        return null;
    }

    private boolean mungePrivilegedAccess(BcelClassWeaver weaver, PrivilegedAccessMunger munger) {
        LazyClassGen gen = weaver.getLazyClassGen();
        ResolvedMember member = munger.getMember();
        ResolvedType onType = weaver.getWorld().resolve(member.getDeclaringType(), munger.getSourceLocation());
        if (onType.isRawType()) {
            onType = onType.getGenericType();
        }
        if (onType.equals(gen.getType())) {
            if (member.getKind() == Member.FIELD) {
                this.addFieldGetter(gen, member, AjcMemberMaker.privilegedAccessMethodForFieldGet(this.aspectType, member, munger.shortSyntax));
                this.addFieldSetter(gen, member, AjcMemberMaker.privilegedAccessMethodForFieldSet(this.aspectType, member, munger.shortSyntax));
                return true;
            }
            if (member.getKind() == Member.METHOD) {
                this.addMethodDispatch(gen, member, AjcMemberMaker.privilegedAccessMethodForMethod(this.aspectType, member));
                return true;
            }
            if (member.getKind() == Member.CONSTRUCTOR) {
                for (LazyMethodGen m : gen.getMethodGens()) {
                    if (m.getMemberView() == null || m.getMemberView().getKind() != Member.CONSTRUCTOR) continue;
                    m.forcePublic();
                }
                return true;
            }
            if (member.getKind() == Member.STATIC_INITIALIZATION) {
                gen.forcePublic();
                return true;
            }
            throw new RuntimeException("unimplemented");
        }
        return false;
    }

    private void addFieldGetter(LazyClassGen gen, ResolvedMember field, ResolvedMember accessMethod) {
        LazyMethodGen mg = this.makeMethodGen(gen, accessMethod);
        InstructionList il = new InstructionList();
        InstructionFactory fact = gen.getFactory();
        if (Modifier.isStatic(field.getModifiers())) {
            il.append(fact.createFieldAccess(gen.getClassName(), field.getName(), BcelWorld.makeBcelType(field.getType()), (short)178));
        } else {
            il.append(InstructionConstants.ALOAD_0);
            il.append(fact.createFieldAccess(gen.getClassName(), field.getName(), BcelWorld.makeBcelType(field.getType()), (short)180));
        }
        il.append(InstructionFactory.createReturn(BcelWorld.makeBcelType(field.getType())));
        mg.getBody().insert(il);
        gen.addMethodGen(mg, this.getSignature().getSourceLocation());
    }

    private void addFieldSetter(LazyClassGen gen, ResolvedMember field, ResolvedMember accessMethod) {
        LazyMethodGen mg = this.makeMethodGen(gen, accessMethod);
        InstructionList il = new InstructionList();
        InstructionFactory fact = gen.getFactory();
        Type fieldType = BcelWorld.makeBcelType(field.getType());
        if (Modifier.isStatic(field.getModifiers())) {
            il.append(InstructionFactory.createLoad(fieldType, 0));
            il.append(fact.createFieldAccess(gen.getClassName(), field.getName(), fieldType, (short)179));
        } else {
            il.append(InstructionConstants.ALOAD_0);
            il.append(InstructionFactory.createLoad(fieldType, 1));
            il.append(fact.createFieldAccess(gen.getClassName(), field.getName(), fieldType, (short)181));
        }
        il.append(InstructionFactory.createReturn(Type.VOID));
        mg.getBody().insert(il);
        gen.addMethodGen(mg, this.getSignature().getSourceLocation());
    }

    private void addMethodDispatch(LazyClassGen gen, ResolvedMember method, ResolvedMember accessMethod) {
        LazyMethodGen mg = this.makeMethodGen(gen, accessMethod);
        InstructionList il = new InstructionList();
        InstructionFactory fact = gen.getFactory();
        Type[] paramTypes = BcelWorld.makeBcelTypes(method.getParameterTypes());
        int pos = 0;
        if (!Modifier.isStatic(method.getModifiers())) {
            il.append(InstructionConstants.ALOAD_0);
            ++pos;
        }
        for (Type paramType : paramTypes) {
            il.append(InstructionFactory.createLoad(paramType, pos));
            pos += paramType.getSize();
        }
        il.append(Utility.createInvoke(fact, (BcelWorld)this.aspectType.getWorld(), (Member)method));
        il.append(InstructionFactory.createReturn(BcelWorld.makeBcelType(method.getReturnType())));
        mg.getBody().insert(il);
        gen.addMethodGen(mg);
    }

    protected LazyMethodGen makeMethodGen(LazyClassGen gen, ResolvedMember member) {
        try {
            Type returnType = BcelWorld.makeBcelType(member.getReturnType());
            Type[] parameterTypes = BcelWorld.makeBcelTypes(member.getParameterTypes());
            LazyMethodGen ret = new LazyMethodGen(member.getModifiers(), returnType, member.getName(), parameterTypes, UnresolvedType.getNames(member.getExceptions()), gen);
            return ret;
        }
        catch (ClassFormatException cfe) {
            throw new RuntimeException("Problem with makeMethodGen for method " + member.getName() + " in type " + gen.getName() + "  ret=" + member.getReturnType(), cfe);
        }
    }

    protected FieldGen makeFieldGen(LazyClassGen gen, ResolvedMember member) {
        return new FieldGen(member.getModifiers(), BcelWorld.makeBcelType(member.getReturnType()), member.getName(), gen.getConstantPool());
    }

    private boolean mungePerObjectInterface(BcelClassWeaver weaver, PerObjectInterfaceTypeMunger munger) {
        LazyClassGen gen = weaver.getLazyClassGen();
        if (this.couldMatch(gen.getBcelObjectType(), munger.getTestPointcut())) {
            FieldGen fg = this.makeFieldGen(gen, AjcMemberMaker.perObjectField(gen.getType(), this.aspectType));
            gen.addField(fg, this.getSourceLocation());
            Type fieldType = BcelWorld.makeBcelType(this.aspectType);
            LazyMethodGen mg = new LazyMethodGen(1, fieldType, NameMangler.perObjectInterfaceGet(this.aspectType), new Type[0], new String[0], gen);
            InstructionList il = new InstructionList();
            InstructionFactory fact = gen.getFactory();
            il.append(InstructionConstants.ALOAD_0);
            il.append(fact.createFieldAccess(gen.getClassName(), fg.getName(), fieldType, (short)180));
            il.append(InstructionFactory.createReturn(fieldType));
            mg.getBody().insert(il);
            gen.addMethodGen(mg);
            LazyMethodGen mg1 = new LazyMethodGen(1, Type.VOID, NameMangler.perObjectInterfaceSet(this.aspectType), new Type[]{fieldType}, new String[0], gen);
            InstructionList il1 = new InstructionList();
            il1.append(InstructionConstants.ALOAD_0);
            il1.append(InstructionFactory.createLoad(fieldType, 1));
            il1.append(fact.createFieldAccess(gen.getClassName(), fg.getName(), fieldType, (short)181));
            il1.append(InstructionFactory.createReturn(Type.VOID));
            mg1.getBody().insert(il1);
            gen.addMethodGen(mg1);
            gen.addInterface(munger.getInterfaceType().resolve(weaver.getWorld()), this.getSourceLocation());
            return true;
        }
        return false;
    }

    private boolean mungePerTypeWithinTransformer(BcelClassWeaver weaver) {
        LazyClassGen gen = weaver.getLazyClassGen();
        FieldGen fg = this.makeFieldGen(gen, AjcMemberMaker.perTypeWithinField(gen.getType(), this.aspectType));
        gen.addField(fg, this.getSourceLocation());
        if (!gen.getType().canBeSeenBy(this.aspectType) && this.aspectType.isPrivilegedAspect()) {
            gen.forcePublic();
        }
        Type fieldType = BcelWorld.makeBcelType(this.aspectType);
        LazyMethodGen mg = new LazyMethodGen(9, fieldType, NameMangler.perTypeWithinLocalAspectOf(this.aspectType), new Type[0], new String[0], gen);
        InstructionList il = new InstructionList();
        InstructionFactory fact = gen.getFactory();
        il.append(fact.createFieldAccess(gen.getClassName(), fg.getName(), fieldType, (short)178));
        il.append(InstructionFactory.createReturn(fieldType));
        mg.getBody().insert(il);
        gen.addMethodGen(mg);
        return true;
    }

    private boolean couldMatch(BcelObjectType bcelObjectType, Pointcut pointcut) {
        return !bcelObjectType.isInterface();
    }

    private boolean mungeNewMemberType(BcelClassWeaver classWeaver, NewMemberClassTypeMunger munger) {
        BcelWorld world = classWeaver.getWorld();
        ResolvedType onType = world.resolve(munger.getTargetType());
        if (onType.isRawType()) {
            onType = onType.getGenericType();
        }
        return onType.equals(classWeaver.getLazyClassGen().getType());
    }

    private boolean mungeNewMethod(BcelClassWeaver classWeaver, NewMethodTypeMunger munger) {
        ResolvedMember interMethodDispatcher;
        BcelWorld world = classWeaver.getWorld();
        ResolvedMember unMangledInterMethod = munger.getSignature().resolve(world);
        ResolvedMember interMethodBody = munger.getDeclaredInterMethodBody(this.aspectType, world);
        ResolvedMember memberHoldingAnyAnnotations = interMethodDispatcher = munger.getDeclaredInterMethodDispatcher(this.aspectType, world);
        LazyClassGen classGen = classWeaver.getLazyClassGen();
        ResolvedType onType = world.resolve(unMangledInterMethod.getDeclaringType(), munger.getSourceLocation());
        if (onType.isRawType()) {
            onType = onType.getGenericType();
        }
        if (onType.isAnnotation()) {
            this.signalError("itdmOnAnnotationNotAllowed", classWeaver, onType);
            return false;
        }
        if (onType.isEnum()) {
            this.signalError("itdmOnEnumNotAllowed", classWeaver, onType);
            return false;
        }
        boolean mungingInterface = classGen.isInterface();
        boolean onInterface = onType.isInterface();
        if (onInterface && classGen.getLazyMethodGen(unMangledInterMethod.getName(), unMangledInterMethod.getSignature(), true) != null) {
            return false;
        }
        if (onType.equals(classGen.getType())) {
            String genericSignature;
            String basicSignature;
            ResolvedMember mangledInterMethod = AjcMemberMaker.interMethod(unMangledInterMethod, this.aspectType, onInterface);
            LazyMethodGen newMethod = this.makeMethodGen(classGen, mangledInterMethod);
            if (mungingInterface) {
                newMethod.setAccessFlags(1025);
            }
            if (classWeaver.getWorld().isInJava5Mode()) {
                ResolvedMember realMember;
                AnnotationAJ[] annotationsOnRealMember = null;
                ResolvedType toLookOn = this.aspectType;
                if (this.aspectType.isRawType()) {
                    toLookOn = this.aspectType.getGenericType();
                }
                if ((realMember = this.getRealMemberForITDFromAspect(toLookOn, memberHoldingAnyAnnotations, false)) != null) {
                    annotationsOnRealMember = realMember.getAnnotations();
                }
                HashSet<ResolvedType> addedAnnotations = new HashSet<ResolvedType>();
                if (annotationsOnRealMember != null) {
                    for (AnnotationAJ anno : annotationsOnRealMember) {
                        AnnotationGen a = ((BcelAnnotation)anno).getBcelAnnotation();
                        AnnotationGen ag = new AnnotationGen(a, classGen.getConstantPool(), true);
                        newMethod.addAnnotation(new BcelAnnotation(ag, classWeaver.getWorld()));
                        addedAnnotations.add(anno.getType());
                    }
                }
                if (realMember != null) {
                    this.copyOverParameterAnnotations(newMethod, realMember);
                }
                List<DeclareAnnotation> allDecams = world.getDeclareAnnotationOnMethods();
                for (DeclareAnnotation declareAnnotationMC : allDecams) {
                    AnnotationAJ annotation;
                    if (!declareAnnotationMC.matches(unMangledInterMethod, world) || addedAnnotations.contains((annotation = declareAnnotationMC.getAnnotation()).getType())) continue;
                    newMethod.addAnnotation(annotation);
                }
            }
            if (!onInterface && !Modifier.isAbstract(mangledInterMethod.getModifiers())) {
                InstructionList body = newMethod.getBody();
                InstructionFactory fact = classGen.getFactory();
                int pos = 0;
                if (!Modifier.isStatic(unMangledInterMethod.getModifiers())) {
                    body.append(InstructionFactory.createThis());
                    ++pos;
                }
                Type[] paramTypes = BcelWorld.makeBcelTypes(mangledInterMethod.getParameterTypes());
                int len = paramTypes.length;
                for (int i = 0; i < len; ++i) {
                    Type paramType = paramTypes[i];
                    body.append(InstructionFactory.createLoad(paramType, pos));
                    pos += paramType.getSize();
                }
                body.append(Utility.createInvoke(fact, classWeaver.getWorld(), (Member)interMethodBody));
                body.append(InstructionFactory.createReturn(BcelWorld.makeBcelType(mangledInterMethod.getReturnType())));
                if (classWeaver.getWorld().isInJava5Mode()) {
                    this.createAnyBridgeMethodsForCovariance(classWeaver, munger, unMangledInterMethod, onType, classGen, paramTypes);
                }
            }
            if (world.isInJava5Mode() && !(basicSignature = mangledInterMethod.getSignature()).equals(genericSignature = ((ResolvedMemberImpl)mangledInterMethod).getSignatureForAttribute())) {
                newMethod.addAttribute(this.createSignatureAttribute(classGen.getConstantPool(), genericSignature));
            }
            classWeaver.addLazyMethodGen(newMethod);
            classWeaver.getLazyClassGen().warnOnAddedMethod(newMethod.getMethod(), this.getSignature().getSourceLocation());
            this.addNeededSuperCallMethods(classWeaver, onType, munger.getSuperMethodsCalled());
            return true;
        }
        if (onInterface && !Modifier.isAbstract(unMangledInterMethod.getModifiers())) {
            String genericSignature;
            String basicSignature;
            if (!classGen.getType().isTopmostImplementor(onType)) {
                ResolvedType rtx = classGen.getType().getTopmostImplementor(onType);
                if (rtx == null) {
                    ISourceLocation sloc;
                    ResolvedType rt = classGen.getType();
                    if (rt.isInterface()) {
                        sloc = munger.getSourceLocation();
                        classWeaver.getWorld().getMessageHandler().handleMessage(MessageUtil.error("ITD target " + rt.getName() + " is an interface but has been incorrectly determined to be the topmost implementor of " + onType.getName() + ". ITD is " + this.getSignature(), sloc));
                    }
                    if (!onType.isAssignableFrom(rt)) {
                        sloc = munger.getSourceLocation();
                        classWeaver.getWorld().getMessageHandler().handleMessage(MessageUtil.error("ITD target " + rt.getName() + " doesn't appear to implement " + onType.getName() + " why did we consider it the top most implementor? ITD is " + this.getSignature(), sloc));
                    }
                } else if (!rtx.isExposedToWeaver()) {
                    ISourceLocation sLoc = munger.getSourceLocation();
                    classWeaver.getWorld().getMessageHandler().handleMessage(MessageUtil.error(WeaverMessages.format("itdNonExposedImplementor", rtx, this.getAspectType().getName()), sLoc == null ? this.getAspectType().getSourceLocation() : sLoc));
                }
                return false;
            }
            ResolvedMember mangledInterMethod = AjcMemberMaker.interMethod(unMangledInterMethod, this.aspectType, false);
            LazyMethodGen mg = this.makeMethodGen(classGen, mangledInterMethod);
            if (classWeaver.getWorld().isInJava5Mode()) {
                ResolvedMember realMember;
                AnnotationAJ[] annotationsOnRealMember = null;
                ResolvedType toLookOn = this.aspectType;
                if (this.aspectType.isRawType()) {
                    toLookOn = this.aspectType.getGenericType();
                }
                if ((realMember = this.getRealMemberForITDFromAspect(toLookOn, memberHoldingAnyAnnotations, false)) == null) {
                    throw new BCException("Couldn't find ITD holder member '" + memberHoldingAnyAnnotations + "' on aspect " + this.aspectType);
                }
                annotationsOnRealMember = realMember.getAnnotations();
                if (annotationsOnRealMember != null) {
                    for (AnnotationAJ annotationX : annotationsOnRealMember) {
                        AnnotationGen a = ((BcelAnnotation)annotationX).getBcelAnnotation();
                        AnnotationGen ag = new AnnotationGen(a, classWeaver.getLazyClassGen().getConstantPool(), true);
                        mg.addAnnotation(new BcelAnnotation(ag, classWeaver.getWorld()));
                    }
                }
                this.copyOverParameterAnnotations(mg, realMember);
            }
            if (mungingInterface) {
                mg.setAccessFlags(1025);
            }
            Type[] paramTypes = BcelWorld.makeBcelTypes(mangledInterMethod.getParameterTypes());
            Type returnType = BcelWorld.makeBcelType(mangledInterMethod.getReturnType());
            InstructionList body = mg.getBody();
            InstructionFactory fact = classGen.getFactory();
            int pos = 0;
            if (!Modifier.isStatic(mangledInterMethod.getModifiers())) {
                body.append(InstructionFactory.createThis());
                ++pos;
            }
            for (Type paramType : paramTypes) {
                body.append(InstructionFactory.createLoad(paramType, pos));
                pos += paramType.getSize();
            }
            body.append(Utility.createInvoke(fact, classWeaver.getWorld(), (Member)interMethodBody));
            Type t = BcelWorld.makeBcelType(interMethodBody.getReturnType());
            if (!t.equals(returnType)) {
                body.append(fact.createCast(t, returnType));
            }
            body.append(InstructionFactory.createReturn(returnType));
            mg.definingType = onType;
            if (world.isInJava5Mode() && !(basicSignature = mangledInterMethod.getSignature()).equals(genericSignature = ((ResolvedMemberImpl)mangledInterMethod).getSignatureForAttribute())) {
                mg.addAttribute(this.createSignatureAttribute(classGen.getConstantPool(), genericSignature));
            }
            classWeaver.addOrReplaceLazyMethodGen(mg);
            this.addNeededSuperCallMethods(classWeaver, onType, munger.getSuperMethodsCalled());
            this.createBridgeIfNecessary(classWeaver, munger, unMangledInterMethod, classGen);
            return true;
        }
        return false;
    }

    private void createBridgeIfNecessary(BcelClassWeaver classWeaver, NewMethodTypeMunger munger, ResolvedMember unMangledInterMethod, LazyClassGen classGen) {
        if (munger.getDeclaredSignature() != null) {
            boolean needsbridging = false;
            ResolvedMember mungerSignature = munger.getSignature();
            ResolvedMemberImpl toBridgeTo = munger.getDeclaredSignature().parameterizedWith(null, mungerSignature.getDeclaringType().resolve(this.getWorld()), false, munger.getTypeVariableAliases());
            if (!toBridgeTo.getReturnType().getErasureSignature().equals(mungerSignature.getReturnType().getErasureSignature())) {
                needsbridging = true;
            }
            UnresolvedType[] originalParams = toBridgeTo.getParameterTypes();
            UnresolvedType[] newParams = mungerSignature.getParameterTypes();
            for (int ii = 0; ii < originalParams.length; ++ii) {
                if (originalParams[ii].getErasureSignature().equals(newParams[ii].getErasureSignature())) continue;
                needsbridging = true;
            }
            if (needsbridging) {
                this.createBridge(classWeaver, unMangledInterMethod, classGen, toBridgeTo);
            }
        }
    }

    private void copyOverParameterAnnotations(LazyMethodGen receiverMethod, ResolvedMember donorMethod) {
        AnnotationAJ[][] pAnnos = donorMethod.getParameterAnnotations();
        if (pAnnos != null) {
            int offset = receiverMethod.isStatic() ? 0 : 1;
            int param = 0;
            for (int i = offset; i < pAnnos.length; ++i) {
                AnnotationAJ[] annosOnParam = pAnnos[i];
                if (annosOnParam != null) {
                    for (AnnotationAJ anno : annosOnParam) {
                        receiverMethod.addParameterAnnotation(param, anno);
                    }
                }
                ++param;
            }
        }
    }

    private void createBridge(BcelClassWeaver weaver, ResolvedMember unMangledInterMethod, LazyClassGen classGen, ResolvedMember toBridgeTo) {
        ResolvedMember bridgerMethod = AjcMemberMaker.bridgerToInterMethod(unMangledInterMethod, classGen.getType());
        ResolvedMember bridgingSetter = AjcMemberMaker.interMethodBridger(toBridgeTo, this.aspectType, false);
        LazyMethodGen bridgeMethod = this.makeMethodGen(classGen, bridgingSetter);
        Type[] paramTypes = BcelWorld.makeBcelTypes(bridgingSetter.getParameterTypes());
        Type[] bridgingToParms = BcelWorld.makeBcelTypes(unMangledInterMethod.getParameterTypes());
        Type returnType = BcelWorld.makeBcelType(bridgingSetter.getReturnType());
        InstructionList body = bridgeMethod.getBody();
        InstructionFactory fact = classGen.getFactory();
        int pos = 0;
        if (!Modifier.isStatic(bridgingSetter.getModifiers())) {
            body.append(InstructionFactory.createThis());
            ++pos;
        }
        int len = paramTypes.length;
        for (int i = 0; i < len; ++i) {
            Type paramType = paramTypes[i];
            body.append(InstructionFactory.createLoad(paramType, pos));
            if (!bridgingSetter.getParameterTypes()[i].getErasureSignature().equals(unMangledInterMethod.getParameterTypes()[i].getErasureSignature())) {
                body.append(fact.createCast(paramType, bridgingToParms[i]));
            }
            pos += paramType.getSize();
        }
        body.append(Utility.createInvoke(fact, weaver.getWorld(), (Member)bridgerMethod));
        body.append(InstructionFactory.createReturn(returnType));
        classGen.addMethodGen(bridgeMethod);
    }

    private Signature createSignatureAttribute(ConstantPool cp, String signature) {
        int nameIndex = cp.addUtf8("Signature");
        int sigIndex = cp.addUtf8(signature);
        return new Signature(nameIndex, 2, sigIndex, cp);
    }

    private void createAnyBridgeMethodsForCovariance(BcelClassWeaver weaver, NewMethodTypeMunger munger, ResolvedMember unMangledInterMethod, ResolvedType onType, LazyClassGen gen, Type[] paramTypes) {
        ResolvedType supertype;
        boolean quitRightNow = false;
        String localMethodName = unMangledInterMethod.getName();
        String erasedSig = unMangledInterMethod.getSignatureErased();
        String localParameterSig = erasedSig.substring(0, erasedSig.lastIndexOf(41) + 1);
        String localReturnTypeESig = unMangledInterMethod.getReturnType().getErasureSignature();
        boolean alreadyDone = false;
        ResolvedMember[] localMethods = onType.getDeclaredMethods();
        for (int i = 0; i < localMethods.length; ++i) {
            ResolvedMember member = localMethods[i];
            if (!member.getName().equals(localMethodName) || !member.getParameterSignature().equals(localParameterSig)) continue;
            alreadyDone = true;
        }
        if (!alreadyDone && (supertype = onType.getSuperclass()) != null) {
            Iterator<ResolvedMember> iter = supertype.getMethods(true, true);
            while (iter.hasNext() && !quitRightNow) {
                ResolvedMember aMethod = iter.next();
                if (!aMethod.getName().equals(localMethodName) || !aMethod.getParameterSignature().equals(localParameterSig) || aMethod.getReturnType().getErasureSignature().equals(localReturnTypeESig) || Modifier.isPrivate(aMethod.getModifiers())) continue;
                this.createBridgeMethod(weaver.getWorld(), munger, unMangledInterMethod, gen, paramTypes, aMethod);
                quitRightNow = true;
            }
        }
    }

    private void createBridgeMethod(BcelWorld world, NewMethodTypeMunger munger, ResolvedMember unMangledInterMethod, LazyClassGen clazz, Type[] paramTypes, ResolvedMember theBridgeMethod) {
        int pos = 0;
        LazyMethodGen bridgeMethod = this.makeMethodGen(clazz, theBridgeMethod);
        bridgeMethod.setAccessFlags(bridgeMethod.getAccessFlags() | 0x40);
        Type returnType = BcelWorld.makeBcelType(theBridgeMethod.getReturnType());
        InstructionList body = bridgeMethod.getBody();
        InstructionFactory fact = clazz.getFactory();
        if (!Modifier.isStatic(unMangledInterMethod.getModifiers())) {
            body.append(InstructionFactory.createThis());
            ++pos;
        }
        for (Type paramType : paramTypes) {
            body.append(InstructionFactory.createLoad(paramType, pos));
            pos += paramType.getSize();
        }
        body.append(Utility.createInvoke(fact, world, (Member)unMangledInterMethod));
        body.append(InstructionFactory.createReturn(returnType));
        clazz.addMethodGen(bridgeMethod);
    }

    private String stringifyMember(ResolvedMember member) {
        StringBuffer buf = new StringBuffer();
        buf.append(member.getReturnType().getName());
        buf.append(' ');
        buf.append(member.getName());
        if (member.getKind() != Member.FIELD) {
            buf.append("(");
            UnresolvedType[] params = member.getParameterTypes();
            if (params.length != 0) {
                buf.append(params[0]);
                int len = params.length;
                for (int i = 1; i < len; ++i) {
                    buf.append(", ");
                    buf.append(params[i].getName());
                }
            }
            buf.append(")");
        }
        return buf.toString();
    }

    /*
     * WARNING - void declaration
     */
    private boolean mungeMethodDelegate(BcelClassWeaver weaver, MethodDelegateTypeMunger munger) {
        boolean shouldApply;
        BcelWorld world = weaver.getWorld();
        LazyClassGen gen = weaver.getLazyClassGen();
        if (gen.getType().isAnnotation() || gen.getType().isEnum()) {
            return false;
        }
        ResolvedMember introduced = munger.getSignature();
        ResolvedType fromType = world.resolve(introduced.getDeclaringType(), munger.getSourceLocation());
        if (fromType.isRawType()) {
            fromType = fromType.getGenericType();
        }
        if (shouldApply = munger.matches(weaver.getLazyClassGen().getType(), this.aspectType)) {
            void var16_28;
            Type bcelReturnType = BcelWorld.makeBcelType(introduced.getReturnType());
            if (munger.getImplClassName() == null && !munger.specifiesDelegateFactoryMethod()) {
                boolean isOK = false;
                List<LazyMethodGen> existingMethods = gen.getMethodGens();
                for (LazyMethodGen m : existingMethods) {
                    if (!m.getName().equals(introduced.getName()) || !m.getParameterSignature().equals(introduced.getParameterSignature()) || !m.getReturnType().equals(bcelReturnType)) continue;
                    isOK = true;
                }
                if (!isOK) {
                    Message msg = new Message("@DeclareParents: No defaultImpl was specified but the type '" + gen.getName() + "' does not implement the method '" + this.stringifyMember(introduced) + "' defined on the interface '" + introduced.getDeclaringType() + "'", weaver.getLazyClassGen().getType().getSourceLocation(), true, new ISourceLocation[]{munger.getSourceLocation()});
                    weaver.getWorld().getMessageHandler().handleMessage(msg);
                    return false;
                }
                return true;
            }
            LazyMethodGen mg = new LazyMethodGen(introduced.getModifiers() - 1024, bcelReturnType, introduced.getName(), BcelWorld.makeBcelTypes(introduced.getParameterTypes()), BcelWorld.makeBcelTypesAsClassNames(introduced.getExceptions()), gen);
            if (weaver.getWorld().isInJava5Mode()) {
                ResolvedMember[] ms;
                AnnotationAJ[] annotationsOnRealMember = null;
                ReferenceType toLookOn = weaver.getWorld().lookupOrCreateName(introduced.getDeclaringType());
                if (fromType.isRawType()) {
                    toLookOn = fromType.getGenericType();
                }
                for (ResolvedMember resolvedMember : ms = toLookOn.getDeclaredJavaMethods()) {
                    if (!introduced.getName().equals(resolvedMember.getName()) || !introduced.getSignature().equals(resolvedMember.getSignature())) continue;
                    annotationsOnRealMember = resolvedMember.getAnnotations();
                    break;
                }
                if (annotationsOnRealMember != null) {
                    for (ResolvedMember resolvedMember : annotationsOnRealMember) {
                        AnnotationGen a = ((BcelAnnotation)((Object)resolvedMember)).getBcelAnnotation();
                        AnnotationGen ag = new AnnotationGen(a, weaver.getLazyClassGen().getConstantPool(), true);
                        mg.addAnnotation(new BcelAnnotation(ag, weaver.getWorld()));
                    }
                }
            }
            InstructionList body = new InstructionList();
            InstructionFactory fact = gen.getFactory();
            body.append(InstructionConstants.ALOAD_0);
            body.append(Utility.createGet(fact, munger.getDelegate(weaver.getLazyClassGen().getType())));
            InstructionBranch ifNonNull = InstructionFactory.createBranchInstruction((short)199, null);
            body.append(ifNonNull);
            body.append(InstructionConstants.ALOAD_0);
            if (munger.specifiesDelegateFactoryMethod()) {
                ResolvedType parameterType;
                ResolvedMember rm = munger.getDelegateFactoryMethod(weaver.getWorld());
                if (rm.getArity() != 0 && !(parameterType = rm.getParameterTypes()[0].resolve(weaver.getWorld())).isAssignableFrom(weaver.getLazyClassGen().getType())) {
                    this.signalError("For mixin factory method '" + rm + "': Instance type '" + weaver.getLazyClassGen().getType() + "' is not compatible with factory parameter type '" + parameterType + "'", weaver);
                    return false;
                }
                if (Modifier.isStatic(rm.getModifiers())) {
                    if (rm.getArity() != 0) {
                        body.append(InstructionConstants.ALOAD_0);
                    }
                    body.append(fact.createInvoke(rm.getDeclaringType().getName(), rm.getName(), rm.getSignature(), (short)184));
                    body.append(Utility.createSet(fact, munger.getDelegate(weaver.getLazyClassGen().getType())));
                } else {
                    UnresolvedType theAspect = munger.getAspect();
                    body.append(fact.createInvoke(theAspect.getName(), "aspectOf", "()" + theAspect.getSignature(), (short)184));
                    if (rm.getArity() != 0) {
                        body.append(InstructionConstants.ALOAD_0);
                    }
                    body.append(fact.createInvoke(rm.getDeclaringType().getName(), rm.getName(), rm.getSignature(), (short)182));
                    body.append(Utility.createSet(fact, munger.getDelegate(weaver.getLazyClassGen().getType())));
                }
            } else {
                body.append(fact.createNew(munger.getImplClassName()));
                body.append(InstructionConstants.DUP);
                body.append(fact.createInvoke(munger.getImplClassName(), "<init>", Type.VOID, Type.NO_ARGS, (short)183));
                body.append(Utility.createSet(fact, munger.getDelegate(weaver.getLazyClassGen().getType())));
            }
            InstructionHandle ifNonNullElse = body.append(InstructionConstants.ALOAD_0);
            ifNonNull.setTarget(ifNonNullElse);
            body.append(Utility.createGet(fact, munger.getDelegate(weaver.getLazyClassGen().getType())));
            int pos = 0;
            if (!Modifier.isStatic(introduced.getModifiers())) {
                ++pos;
            }
            Type[] paramTypes = BcelWorld.makeBcelTypes(introduced.getParameterTypes());
            boolean bl = false;
            int len = paramTypes.length;
            while (var16_28 < len) {
                Type paramType = paramTypes[var16_28];
                body.append(InstructionFactory.createLoad(paramType, pos));
                pos += paramType.getSize();
                ++var16_28;
            }
            body.append(Utility.createInvoke(fact, (short)185, (Member)introduced));
            body.append(InstructionFactory.createReturn(bcelReturnType));
            mg.getBody().append(body);
            weaver.addLazyMethodGen(mg);
            weaver.getLazyClassGen().warnOnAddedMethod(mg.getMethod(), this.getSignature().getSourceLocation());
            return true;
        }
        return false;
    }

    private boolean mungeFieldHost(BcelClassWeaver weaver, MethodDelegateTypeMunger.FieldHostTypeMunger munger) {
        LazyClassGen gen = weaver.getLazyClassGen();
        if (gen.getType().isAnnotation() || gen.getType().isEnum()) {
            return false;
        }
        munger.matches(weaver.getLazyClassGen().getType(), this.aspectType);
        ResolvedMember host = AjcMemberMaker.itdAtDeclareParentsField(weaver.getLazyClassGen().getType(), munger.getSignature().getType(), this.aspectType);
        FieldGen field = this.makeFieldGen(weaver.getLazyClassGen(), host);
        field.setModifiers(field.getModifiers() | BcelField.AccSynthetic);
        weaver.getLazyClassGen().addField(field, null);
        return true;
    }

    private ResolvedMember getRealMemberForITDFromAspect(ResolvedType aspectType, ResolvedMember lookingFor, boolean isCtorRelated) {
        World world = aspectType.getWorld();
        boolean debug = false;
        if (debug) {
            System.err.println("Searching for a member on type: " + aspectType);
            System.err.println("Member we are looking for: " + lookingFor);
        }
        ResolvedMember[] aspectMethods = aspectType.getDeclaredMethods();
        UnresolvedType[] lookingForParams = lookingFor.getParameterTypes();
        ResolvedMember realMember = null;
        for (int i = 0; realMember == null && i < aspectMethods.length; ++i) {
            UnresolvedType[] memberParams;
            ResolvedMember member = aspectMethods[i];
            if (!member.getName().equals(lookingFor.getName()) || (memberParams = member.getGenericParameterTypes()).length != lookingForParams.length) continue;
            if (debug) {
                System.err.println("Reviewing potential candidates: " + member);
            }
            boolean matchOK = true;
            if (isCtorRelated) {
                for (int j = 0; j < memberParams.length && matchOK; ++j) {
                    ResolvedType pMember = memberParams[j].resolve(world);
                    ResolvedType pLookingFor = lookingForParams[j].resolve(world);
                    if (pMember.isTypeVariableReference()) {
                        pMember = ((TypeVariableReference)((Object)pMember)).getTypeVariable().getFirstBound().resolve(world);
                    }
                    if (pMember.isParameterizedType() || pMember.isGenericType()) {
                        pMember = pMember.getRawType().resolve(aspectType.getWorld());
                    }
                    if (pLookingFor.isTypeVariableReference()) {
                        pLookingFor = ((TypeVariableReference)((Object)pLookingFor)).getTypeVariable().getFirstBound().resolve(world);
                    }
                    if (pLookingFor.isParameterizedType() || pLookingFor.isGenericType()) {
                        pLookingFor = pLookingFor.getRawType().resolve(world);
                    }
                    if (debug) {
                        System.err.println("Comparing parameter " + j + "   member=" + pMember + "   lookingFor=" + pLookingFor);
                    }
                    if (pMember.equals(pLookingFor)) continue;
                    matchOK = false;
                }
            }
            if (!matchOK) continue;
            realMember = member;
        }
        if (debug && realMember == null) {
            System.err.println("Didn't find a match");
        }
        return realMember;
    }

    private void addNeededSuperCallMethods(BcelClassWeaver weaver, ResolvedType onType, Set<ResolvedMember> neededSuperCalls) {
        LazyClassGen gen = weaver.getLazyClassGen();
        for (ResolvedMember superMethod : neededSuperCalls) {
            if (!weaver.addDispatchTarget(superMethod)) continue;
            boolean isSuper = !superMethod.getDeclaringType().equals(gen.getType());
            String dispatchName = isSuper ? NameMangler.superDispatchMethod(onType, superMethod.getName()) : NameMangler.protectedDispatchMethod(onType, superMethod.getName());
            superMethod = superMethod.resolve(weaver.getWorld());
            LazyMethodGen dispatcher = BcelTypeMunger.makeDispatcher(gen, dispatchName, superMethod, weaver.getWorld(), isSuper);
            weaver.addLazyMethodGen(dispatcher);
        }
    }

    private void signalError(String msgid, BcelClassWeaver weaver, UnresolvedType onType) {
        IMessage msg = MessageUtil.error(WeaverMessages.format(msgid, onType.getName()), this.getSourceLocation());
        weaver.getWorld().getMessageHandler().handleMessage(msg);
    }

    private void signalError(String msgString, BcelClassWeaver weaver) {
        IMessage msg = MessageUtil.error(msgString, this.getSourceLocation());
        weaver.getWorld().getMessageHandler().handleMessage(msg);
    }

    private boolean mungeNewConstructor(BcelClassWeaver weaver, NewConstructorTypeMunger newConstructorTypeMunger) {
        LazyClassGen currentClass = weaver.getLazyClassGen();
        InstructionFactory fact = currentClass.getFactory();
        ResolvedMember newConstructorMember = newConstructorTypeMunger.getSyntheticConstructor();
        ResolvedType onType = newConstructorMember.getDeclaringType().resolve(weaver.getWorld());
        if (onType.isRawType()) {
            onType = onType.getGenericType();
        }
        if (onType.isAnnotation()) {
            this.signalError("itdcOnAnnotationNotAllowed", weaver, onType);
            return false;
        }
        if (onType.isEnum()) {
            this.signalError("itdcOnEnumNotAllowed", weaver, onType);
            return false;
        }
        if (!onType.equals(currentClass.getType())) {
            return false;
        }
        ResolvedMember explicitConstructor = newConstructorTypeMunger.getExplicitConstructor();
        LazyMethodGen mg = this.makeMethodGen(currentClass, newConstructorMember);
        mg.setEffectiveSignature(newConstructorTypeMunger.getSignature(), Shadow.ConstructorExecution, true);
        if (weaver.getWorld().isInJava5Mode()) {
            ResolvedMember interMethodDispatcher = AjcMemberMaker.postIntroducedConstructor(this.aspectType, onType, newConstructorTypeMunger.getSignature().getParameterTypes());
            AnnotationAJ[] annotationsOnRealMember = null;
            ResolvedMember realMember = this.getRealMemberForITDFromAspect(this.aspectType, interMethodDispatcher, true);
            if (realMember != null) {
                annotationsOnRealMember = realMember.getAnnotations();
            }
            if (annotationsOnRealMember != null) {
                for (int i = 0; i < annotationsOnRealMember.length; ++i) {
                    AnnotationAJ annotationX = annotationsOnRealMember[i];
                    AnnotationGen a = ((BcelAnnotation)annotationX).getBcelAnnotation();
                    AnnotationGen ag = new AnnotationGen(a, weaver.getLazyClassGen().getConstantPool(), true);
                    mg.addAnnotation(new BcelAnnotation(ag, weaver.getWorld()));
                }
            }
            List<DeclareAnnotation> allDecams = weaver.getWorld().getDeclareAnnotationOnMethods();
            for (DeclareAnnotation decaMC : allDecams) {
                if (!decaMC.matches(explicitConstructor, weaver.getWorld()) || mg.getEnclosingClass().getType() != this.aspectType) continue;
                mg.addAnnotation(decaMC.getAnnotation());
            }
        }
        if (mg.getArgumentTypes().length == 0) {
            LazyMethodGen toRemove = null;
            for (LazyMethodGen object : currentClass.getMethodGens()) {
                if (!object.getName().equals("<init>") || object.getArgumentTypes().length != 0) continue;
                toRemove = object;
            }
            if (toRemove != null) {
                currentClass.removeMethodGen(toRemove);
            }
        }
        currentClass.addMethodGen(mg);
        InstructionList body = mg.getBody();
        UnresolvedType[] declaredParams = newConstructorTypeMunger.getSignature().getParameterTypes();
        Type[] paramTypes = mg.getArgumentTypes();
        int frameIndex = 1;
        int len = declaredParams.length;
        for (int i = 0; i < len; ++i) {
            body.append(InstructionFactory.createLoad(paramTypes[i], frameIndex));
            frameIndex += paramTypes[i].getSize();
        }
        ResolvedMember preMethod = AjcMemberMaker.preIntroducedConstructor(this.aspectType, onType, declaredParams);
        body.append(Utility.createInvoke(fact, null, (Member)preMethod));
        int arraySlot = mg.allocateLocal(1);
        body.append(InstructionFactory.createStore(Type.OBJECT, arraySlot));
        body.append(InstructionConstants.ALOAD_0);
        UnresolvedType[] superParamTypes = explicitConstructor.getParameterTypes();
        int len2 = superParamTypes.length;
        for (int i = 0; i < len2; ++i) {
            body.append(InstructionFactory.createLoad(Type.OBJECT, arraySlot));
            body.append(Utility.createConstant(fact, i));
            body.append(InstructionFactory.createArrayLoad(Type.OBJECT));
            body.append(Utility.createConversion(fact, Type.OBJECT, BcelWorld.makeBcelType(superParamTypes[i])));
        }
        body.append(Utility.createInvoke(fact, null, (Member)explicitConstructor));
        body.append(InstructionConstants.ALOAD_0);
        ResolvedMember postMethod = AjcMemberMaker.postIntroducedConstructor(this.aspectType, onType, declaredParams);
        UnresolvedType[] postParamTypes = postMethod.getParameterTypes();
        int len3 = postParamTypes.length;
        for (int i = 1; i < len3; ++i) {
            body.append(InstructionFactory.createLoad(Type.OBJECT, arraySlot));
            body.append(Utility.createConstant(fact, superParamTypes.length + i - 1));
            body.append(InstructionFactory.createArrayLoad(Type.OBJECT));
            body.append(Utility.createConversion(fact, Type.OBJECT, BcelWorld.makeBcelType(postParamTypes[i])));
        }
        body.append(Utility.createInvoke(fact, null, (Member)postMethod));
        body.append(InstructionConstants.RETURN);
        this.addNeededSuperCallMethods(weaver, onType, this.munger.getSuperMethodsCalled());
        return true;
    }

    private static LazyMethodGen makeDispatcher(LazyClassGen onGen, String dispatchName, ResolvedMember superMethod, BcelWorld world, boolean isSuper) {
        Type[] paramTypes = BcelWorld.makeBcelTypes(superMethod.getParameterTypes());
        Type returnType = BcelWorld.makeBcelType(superMethod.getReturnType());
        int modifiers = 1;
        if (onGen.isInterface()) {
            modifiers |= 0x400;
        }
        LazyMethodGen mg = new LazyMethodGen(modifiers, returnType, dispatchName, paramTypes, UnresolvedType.getNames(superMethod.getExceptions()), onGen);
        InstructionList body = mg.getBody();
        if (onGen.isInterface()) {
            return mg;
        }
        InstructionFactory fact = onGen.getFactory();
        int pos = 0;
        body.append(InstructionFactory.createThis());
        ++pos;
        for (Type paramType : paramTypes) {
            body.append(InstructionFactory.createLoad(paramType, pos));
            pos += paramType.getSize();
        }
        if (isSuper) {
            body.append(Utility.createSuperInvoke(fact, world, superMethod));
        } else {
            body.append(Utility.createInvoke(fact, world, (Member)superMethod));
        }
        body.append(InstructionFactory.createReturn(returnType));
        return mg;
    }

    private boolean mungeNewField(BcelClassWeaver weaver, NewFieldTypeMunger munger) {
        munger.getInitMethod(this.aspectType);
        LazyClassGen gen = weaver.getLazyClassGen();
        ResolvedMember field = munger.getSignature();
        ResolvedType onType = weaver.getWorld().resolve(field.getDeclaringType(), munger.getSourceLocation());
        if (onType.isRawType()) {
            onType = onType.getGenericType();
        }
        boolean onInterface = onType.isInterface();
        if (onType.isAnnotation()) {
            this.signalError("itdfOnAnnotationNotAllowed", weaver, onType);
            return false;
        }
        if (onType.isEnum()) {
            this.signalError("itdfOnEnumNotAllowed", weaver, onType);
            return false;
        }
        ResolvedMember interMethodBody = munger.getInitMethod(this.aspectType);
        AnnotationAJ[] annotationsOnRealMember = null;
        if (weaver.getWorld().isInJava5Mode()) {
            ResolvedMember realMember;
            ResolvedType toLookOn = this.aspectType;
            if (this.aspectType.isRawType()) {
                toLookOn = this.aspectType.getGenericType();
            }
            if ((realMember = this.getRealMemberForITDFromAspect(toLookOn, interMethodBody, false)) != null) {
                annotationsOnRealMember = realMember.getAnnotations();
            }
        }
        if (onType.equals(gen.getType())) {
            if (onInterface) {
                ResolvedMember itdfieldGetter = AjcMemberMaker.interFieldInterfaceGetter(field, onType, this.aspectType);
                LazyMethodGen mg = this.makeMethodGen(gen, itdfieldGetter);
                gen.addMethodGen(mg);
                LazyMethodGen mg1 = this.makeMethodGen(gen, AjcMemberMaker.interFieldInterfaceSetter(field, onType, this.aspectType));
                gen.addMethodGen(mg1);
            } else {
                String genericSignature;
                String basicSignature;
                weaver.addInitializer(this);
                ResolvedMember newField = AjcMemberMaker.interFieldClassField(field, this.aspectType, munger.version == 2);
                FieldGen fg = this.makeFieldGen(gen, newField);
                if (annotationsOnRealMember != null) {
                    for (int i = 0; i < annotationsOnRealMember.length; ++i) {
                        AnnotationAJ annotationX = annotationsOnRealMember[i];
                        AnnotationGen a = ((BcelAnnotation)annotationX).getBcelAnnotation();
                        AnnotationGen ag = new AnnotationGen(a, weaver.getLazyClassGen().getConstantPool(), true);
                        fg.addAnnotation(ag);
                    }
                }
                if (weaver.getWorld().isInJava5Mode() && !(basicSignature = field.getSignature()).equals(genericSignature = field.getReturnType().resolve(weaver.getWorld()).getSignatureForAttribute())) {
                    fg.addAttribute(this.createSignatureAttribute(gen.getConstantPool(), genericSignature));
                }
                gen.addField(fg, this.getSourceLocation());
            }
            return true;
        }
        if (onInterface && gen.getType().isTopmostImplementor(onType)) {
            if (Modifier.isStatic(field.getModifiers())) {
                throw new RuntimeException("unimplemented");
            }
            boolean alreadyExists = false;
            if (munger.version == 2) {
                for (BcelField fieldgen : gen.getFieldGens()) {
                    if (!fieldgen.getName().equals(field.getName())) continue;
                    alreadyExists = true;
                    break;
                }
            }
            ResolvedMember newField = AjcMemberMaker.interFieldInterfaceField(field, onType, this.aspectType, munger.version == 2);
            String fieldName = newField.getName();
            Type fieldType = BcelWorld.makeBcelType(field.getType());
            if (!alreadyExists) {
                String genericSignature;
                String basicSignature;
                weaver.addInitializer(this);
                FieldGen fg = this.makeFieldGen(gen, newField);
                if (annotationsOnRealMember != null) {
                    for (int i = 0; i < annotationsOnRealMember.length; ++i) {
                        AnnotationAJ annotationX = annotationsOnRealMember[i];
                        AnnotationGen a = ((BcelAnnotation)annotationX).getBcelAnnotation();
                        AnnotationGen ag = new AnnotationGen(a, weaver.getLazyClassGen().getConstantPool(), true);
                        fg.addAnnotation(ag);
                    }
                }
                if (weaver.getWorld().isInJava5Mode() && !(basicSignature = field.getSignature()).equals(genericSignature = field.getReturnType().resolve(weaver.getWorld()).getSignatureForAttribute())) {
                    fg.addAttribute(this.createSignatureAttribute(gen.getConstantPool(), genericSignature));
                }
                gen.addField(fg, this.getSourceLocation());
            }
            ResolvedMember itdfieldGetter = AjcMemberMaker.interFieldInterfaceGetter(field, gen.getType(), this.aspectType);
            LazyMethodGen mg = this.makeMethodGen(gen, itdfieldGetter);
            InstructionList il = new InstructionList();
            InstructionFactory fact = gen.getFactory();
            if (Modifier.isStatic(field.getModifiers())) {
                il.append(fact.createFieldAccess(gen.getClassName(), fieldName, fieldType, (short)178));
            } else {
                il.append(InstructionConstants.ALOAD_0);
                il.append(fact.createFieldAccess(gen.getClassName(), fieldName, fieldType, (short)180));
            }
            il.append(InstructionFactory.createReturn(fieldType));
            mg.getBody().insert(il);
            gen.addMethodGen(mg);
            if (munger.getDeclaredSignature() != null) {
                ResolvedMemberImpl toBridgeTo = munger.getDeclaredSignature().parameterizedWith(null, munger.getSignature().getDeclaringType().resolve(this.getWorld()), false, munger.getTypeVariableAliases());
                boolean needsbridging = false;
                if (!toBridgeTo.getReturnType().getErasureSignature().equals(munger.getSignature().getReturnType().getErasureSignature())) {
                    needsbridging = true;
                }
                if (needsbridging) {
                    ResolvedMember bridgingGetter = AjcMemberMaker.interFieldInterfaceGetter(toBridgeTo, gen.getType(), this.aspectType);
                    this.createBridgeMethodForITDF(weaver, gen, itdfieldGetter, bridgingGetter);
                }
            }
            ResolvedMember itdfieldSetter = AjcMemberMaker.interFieldInterfaceSetter(field, gen.getType(), this.aspectType);
            LazyMethodGen mg1 = this.makeMethodGen(gen, itdfieldSetter);
            InstructionList il1 = new InstructionList();
            if (Modifier.isStatic(field.getModifiers())) {
                il1.append(InstructionFactory.createLoad(fieldType, 0));
                il1.append(fact.createFieldAccess(gen.getClassName(), fieldName, fieldType, (short)179));
            } else {
                il1.append(InstructionConstants.ALOAD_0);
                il1.append(InstructionFactory.createLoad(fieldType, 1));
                il1.append(fact.createFieldAccess(gen.getClassName(), fieldName, fieldType, (short)181));
            }
            il1.append(InstructionFactory.createReturn(Type.VOID));
            mg1.getBody().insert(il1);
            gen.addMethodGen(mg1);
            if (munger.getDeclaredSignature() != null) {
                ResolvedMemberImpl toBridgeTo = munger.getDeclaredSignature().parameterizedWith(null, munger.getSignature().getDeclaringType().resolve(this.getWorld()), false, munger.getTypeVariableAliases());
                boolean needsbridging = false;
                if (!toBridgeTo.getReturnType().getErasureSignature().equals(munger.getSignature().getReturnType().getErasureSignature())) {
                    needsbridging = true;
                }
                if (needsbridging) {
                    ResolvedMember bridgingSetter = AjcMemberMaker.interFieldInterfaceSetter(toBridgeTo, gen.getType(), this.aspectType);
                    this.createBridgeMethodForITDF(weaver, gen, itdfieldSetter, bridgingSetter);
                }
            }
            return true;
        }
        return false;
    }

    private void createBridgeMethodForITDF(BcelClassWeaver weaver, LazyClassGen gen, ResolvedMember itdfieldSetter, ResolvedMember bridgingSetter) {
        LazyMethodGen bridgeMethod = this.makeMethodGen(gen, bridgingSetter);
        bridgeMethod.setAccessFlags(bridgeMethod.getAccessFlags() | 0x40);
        Type[] paramTypes = BcelWorld.makeBcelTypes(bridgingSetter.getParameterTypes());
        Type[] bridgingToParms = BcelWorld.makeBcelTypes(itdfieldSetter.getParameterTypes());
        Type returnType = BcelWorld.makeBcelType(bridgingSetter.getReturnType());
        InstructionList body = bridgeMethod.getBody();
        InstructionFactory fact = gen.getFactory();
        int pos = 0;
        if (!Modifier.isStatic(bridgingSetter.getModifiers())) {
            body.append(InstructionFactory.createThis());
            ++pos;
        }
        int len = paramTypes.length;
        for (int i = 0; i < len; ++i) {
            Type paramType = paramTypes[i];
            body.append(InstructionFactory.createLoad(paramType, pos));
            if (!bridgingSetter.getParameterTypes()[i].getErasureSignature().equals(itdfieldSetter.getParameterTypes()[i].getErasureSignature())) {
                body.append(fact.createCast(paramType, bridgingToParms[i]));
            }
            pos += paramType.getSize();
        }
        body.append(Utility.createInvoke(fact, weaver.getWorld(), (Member)itdfieldSetter));
        body.append(InstructionFactory.createReturn(returnType));
        gen.addMethodGen(bridgeMethod);
    }

    @Override
    public ConcreteTypeMunger parameterizedFor(ResolvedType target) {
        return new BcelTypeMunger(this.munger.parameterizedFor(target), this.aspectType);
    }

    @Override
    public ConcreteTypeMunger parameterizeWith(Map<String, UnresolvedType> m, World w) {
        return new BcelTypeMunger(this.munger.parameterizeWith(m, w), this.aspectType);
    }

    public List<String> getTypeVariableAliases() {
        return this.munger.getTypeVariableAliases();
    }

    public boolean equals(Object other) {
        if (!(other instanceof BcelTypeMunger)) {
            return false;
        }
        BcelTypeMunger o = (BcelTypeMunger)other;
        return (o.getMunger() == null ? this.getMunger() == null : o.getMunger().equals(this.getMunger())) && (o.getAspectType() == null ? this.getAspectType() == null : o.getAspectType().equals(this.getAspectType()));
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17;
            result = 37 * result + (this.getMunger() == null ? 0 : this.getMunger().hashCode());
            this.hashCode = result = 37 * result + (this.getAspectType() == null ? 0 : this.getAspectType().hashCode());
        }
        return this.hashCode;
    }
}

