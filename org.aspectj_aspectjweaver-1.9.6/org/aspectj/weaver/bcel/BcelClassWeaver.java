/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Method;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.generic.FieldGen;
import org.aspectj.apache.bcel.generic.FieldInstruction;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionCP;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionLV;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.InstructionSelect;
import org.aspectj.apache.bcel.generic.InstructionTargeter;
import org.aspectj.apache.bcel.generic.InvokeInstruction;
import org.aspectj.apache.bcel.generic.LineNumberTag;
import org.aspectj.apache.bcel.generic.LocalVariableTag;
import org.aspectj.apache.bcel.generic.MULTIANEWARRAY;
import org.aspectj.apache.bcel.generic.MethodGen;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.RET;
import org.aspectj.apache.bcel.generic.Tag;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.asm.AsmManager;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.bridge.WeaveMessage;
import org.aspectj.bridge.context.CompilationAndWeavingContext;
import org.aspectj.bridge.context.ContextToken;
import org.aspectj.util.PartialOrder;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.IClassWeaver;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MissingResolvedTypeWithKnownSignature;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.aspectj.weaver.NewFieldTypeMunger;
import org.aspectj.weaver.NewMethodTypeMunger;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.UnresolvedTypeVariableReferenceType;
import org.aspectj.weaver.WeaverStateInfo;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelAdvice;
import org.aspectj.weaver.bcel.BcelAnnotation;
import org.aspectj.weaver.bcel.BcelField;
import org.aspectj.weaver.bcel.BcelMethod;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelShadow;
import org.aspectj.weaver.bcel.BcelTypeMunger;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.ExceptionRange;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.LazyMethodGen;
import org.aspectj.weaver.bcel.Range;
import org.aspectj.weaver.bcel.ShadowRange;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.model.AsmRelationshipProvider;
import org.aspectj.weaver.patterns.DeclareAnnotation;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.PatternNode;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;

class BcelClassWeaver
implements IClassWeaver {
    private static Trace trace = TraceFactory.getTraceFactory().getTrace(BcelClassWeaver.class);
    private static final String SWITCH_TABLE_SYNTHETIC_METHOD_PREFIX = "$SWITCH_TABLE$";
    private final LazyClassGen clazz;
    private final List<ShadowMunger> shadowMungers;
    private final List<ConcreteTypeMunger> typeMungers;
    private final List<ConcreteTypeMunger> lateTypeMungers;
    private List<ShadowMunger>[] indexedShadowMungers;
    private boolean canMatchBodyShadows = false;
    private final BcelObjectType ty;
    private final BcelWorld world;
    private final ConstantPool cpg;
    private final InstructionFactory fact;
    private final List<LazyMethodGen> addedLazyMethodGens = new ArrayList<LazyMethodGen>();
    private final Set<ResolvedMember> addedDispatchTargets = new HashSet<ResolvedMember>();
    private boolean inReweavableMode = false;
    private List<IfaceInitList> addedSuperInitializersAsList = null;
    private final Map<ResolvedType, IfaceInitList> addedSuperInitializers = new HashMap<ResolvedType, IfaceInitList>();
    private final List<ConcreteTypeMunger> addedThisInitializers = new ArrayList<ConcreteTypeMunger>();
    private final List<ConcreteTypeMunger> addedClassInitializers = new ArrayList<ConcreteTypeMunger>();
    private final Map<ResolvedMember, ResolvedMember> mapToAnnotationHolder = new HashMap<ResolvedMember, ResolvedMember>();
    private final List<BcelShadow> initializationShadows = new ArrayList<BcelShadow>();
    private static boolean checkedXsetForLowLevelContextCapturing = false;
    private static boolean captureLowLevelContext = false;

    public static boolean weave(BcelWorld world, LazyClassGen clazz, List<ShadowMunger> shadowMungers, List<ConcreteTypeMunger> typeMungers, List<ConcreteTypeMunger> lateTypeMungers, boolean inReweavableMode) {
        BcelClassWeaver classWeaver = new BcelClassWeaver(world, clazz, shadowMungers, typeMungers, lateTypeMungers);
        classWeaver.setReweavableMode(inReweavableMode);
        boolean b = classWeaver.weave();
        return b;
    }

    private BcelClassWeaver(BcelWorld world, LazyClassGen clazz, List<ShadowMunger> shadowMungers, List<ConcreteTypeMunger> typeMungers, List<ConcreteTypeMunger> lateTypeMungers) {
        this.world = world;
        this.clazz = clazz;
        this.shadowMungers = shadowMungers;
        this.typeMungers = typeMungers;
        this.lateTypeMungers = lateTypeMungers;
        this.ty = clazz.getBcelObjectType();
        this.cpg = clazz.getConstantPool();
        this.fact = clazz.getFactory();
        this.indexShadowMungers();
        this.initializeSuperInitializerMap(this.ty.getResolvedTypeX());
        if (!checkedXsetForLowLevelContextCapturing) {
            String s;
            Properties p = world.getExtraConfiguration();
            if (p != null && (captureLowLevelContext = (s = p.getProperty("captureAllContext", "false")).equalsIgnoreCase("true"))) {
                world.getMessageHandler().handleMessage(MessageUtil.info("[captureAllContext=true] Enabling collection of low level context for debug/crash messages"));
            }
            checkedXsetForLowLevelContextCapturing = true;
        }
    }

    private boolean canMatch(Shadow.Kind kind) {
        return this.indexedShadowMungers[kind.getKey()] != null;
    }

    private void initializeSuperInitializerMap(ResolvedType child) {
        ResolvedType[] superInterfaces = child.getDeclaredInterfaces();
        int len = superInterfaces.length;
        for (int i = 0; i < len; ++i) {
            if (!this.ty.getResolvedTypeX().isTopmostImplementor(superInterfaces[i]) || !this.addSuperInitializer(superInterfaces[i])) continue;
            this.initializeSuperInitializerMap(superInterfaces[i]);
        }
    }

    private void indexShadowMungers() {
        this.indexedShadowMungers = new List[14];
        for (ShadowMunger shadowMunger : this.shadowMungers) {
            int couldMatchKinds = shadowMunger.getPointcut().couldMatchKinds();
            for (Shadow.Kind kind : Shadow.SHADOW_KINDS) {
                if (!kind.isSet(couldMatchKinds)) continue;
                byte k = kind.getKey();
                if (this.indexedShadowMungers[k] == null) {
                    this.indexedShadowMungers[k] = new ArrayList<ShadowMunger>();
                    if (!kind.isEnclosingKind()) {
                        this.canMatchBodyShadows = true;
                    }
                }
                this.indexedShadowMungers[k].add(shadowMunger);
            }
        }
    }

    private boolean addSuperInitializer(ResolvedType onType) {
        IfaceInitList l;
        if (onType.isRawType() || onType.isParameterizedType()) {
            onType = onType.getGenericType();
        }
        if ((l = this.addedSuperInitializers.get(onType)) != null) {
            return false;
        }
        l = new IfaceInitList(onType);
        this.addedSuperInitializers.put(onType, l);
        return true;
    }

    public void addInitializer(ConcreteTypeMunger cm) {
        NewFieldTypeMunger m = (NewFieldTypeMunger)cm.getMunger();
        ResolvedType onType = m.getSignature().getDeclaringType().resolve(this.world);
        if (onType.isRawType()) {
            onType = onType.getGenericType();
        }
        if (Modifier.isStatic(m.getSignature().getModifiers())) {
            this.addedClassInitializers.add(cm);
        } else if (onType == this.ty.getResolvedTypeX()) {
            this.addedThisInitializers.add(cm);
        } else {
            IfaceInitList l = this.addedSuperInitializers.get(onType);
            l.list.add(cm);
        }
    }

    public boolean addDispatchTarget(ResolvedMember m) {
        return this.addedDispatchTargets.add(m);
    }

    public void addLazyMethodGen(LazyMethodGen gen) {
        this.addedLazyMethodGens.add(gen);
    }

    public void addOrReplaceLazyMethodGen(LazyMethodGen mg) {
        if (this.alreadyDefined(this.clazz, mg)) {
            return;
        }
        Iterator<LazyMethodGen> i = this.addedLazyMethodGens.iterator();
        while (i.hasNext()) {
            LazyMethodGen existing = i.next();
            if (!this.signaturesMatch(mg, existing)) continue;
            if (existing.definingType == null) {
                return;
            }
            if (mg.definingType.isAssignableFrom(existing.definingType)) {
                return;
            }
            if (existing.definingType.isAssignableFrom(mg.definingType)) {
                i.remove();
                this.addedLazyMethodGens.add(mg);
                return;
            }
            throw new BCException("conflict between: " + mg + " and " + existing);
        }
        this.addedLazyMethodGens.add(mg);
    }

    private boolean alreadyDefined(LazyClassGen clazz, LazyMethodGen mg) {
        Iterator<LazyMethodGen> i = clazz.getMethodGens().iterator();
        while (i.hasNext()) {
            LazyMethodGen existing = i.next();
            if (!this.signaturesMatch(mg, existing)) continue;
            if (!mg.isAbstract() && existing.isAbstract()) {
                i.remove();
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean signaturesMatch(LazyMethodGen mg, LazyMethodGen existing) {
        return mg.getName().equals(existing.getName()) && mg.getSignature().equals(existing.getSignature());
    }

    protected static LazyMethodGen makeBridgeMethod(LazyClassGen gen, ResolvedMember member) {
        int mods = member.getModifiers();
        if (Modifier.isAbstract(mods)) {
            mods -= 1024;
        }
        LazyMethodGen ret = new LazyMethodGen(mods, BcelWorld.makeBcelType(member.getReturnType()), member.getName(), BcelWorld.makeBcelTypes(member.getParameterTypes()), UnresolvedType.getNames(member.getExceptions()), gen);
        return ret;
    }

    private static void createBridgeMethod(BcelWorld world, LazyMethodGen whatToBridgeToMethodGen, LazyClassGen clazz, ResolvedMember theBridgeMethod) {
        LazyMethodGen bridgeMethod;
        int newflags;
        int pos = 0;
        ResolvedMemberImpl whatToBridgeTo = whatToBridgeToMethodGen.getMemberView();
        if (whatToBridgeTo == null) {
            whatToBridgeTo = new ResolvedMemberImpl(Member.METHOD, whatToBridgeToMethodGen.getEnclosingClass().getType(), whatToBridgeToMethodGen.getAccessFlags(), whatToBridgeToMethodGen.getName(), whatToBridgeToMethodGen.getSignature());
        }
        if (((newflags = (bridgeMethod = BcelClassWeaver.makeBridgeMethod(clazz, theBridgeMethod)).getAccessFlags() | 0x40 | 0x1000) & 0x100) != 0) {
            newflags -= 256;
        }
        bridgeMethod.setAccessFlags(newflags);
        Type returnType = BcelWorld.makeBcelType(theBridgeMethod.getReturnType());
        Type[] paramTypes = BcelWorld.makeBcelTypes(theBridgeMethod.getParameterTypes());
        Type[] newParamTypes = whatToBridgeToMethodGen.getArgumentTypes();
        InstructionList body = bridgeMethod.getBody();
        InstructionFactory fact = clazz.getFactory();
        if (!whatToBridgeToMethodGen.isStatic()) {
            body.append(InstructionFactory.createThis());
            ++pos;
        }
        int len = paramTypes.length;
        for (int i = 0; i < len; ++i) {
            Type paramType = paramTypes[i];
            body.append(InstructionFactory.createLoad(paramType, pos));
            if (!newParamTypes[i].equals(paramTypes[i])) {
                if (world.forDEBUG_bridgingCode) {
                    System.err.println("Bridging: Cast " + newParamTypes[i] + " from " + paramTypes[i]);
                }
                body.append(fact.createCast(paramTypes[i], newParamTypes[i]));
            }
            pos += paramType.getSize();
        }
        body.append(Utility.createInvoke(fact, world, (Member)whatToBridgeTo));
        body.append(InstructionFactory.createReturn(returnType));
        clazz.addMethodGen(bridgeMethod);
    }

    @Override
    public boolean weave() {
        if (this.clazz.isWoven() && !this.clazz.isReweavable()) {
            if (this.world.getLint().nonReweavableTypeEncountered.isEnabled()) {
                this.world.getLint().nonReweavableTypeEncountered.signal(this.clazz.getType().getName(), this.ty.getSourceLocation());
            }
            return false;
        }
        HashSet<String> aspectsAffectingType = null;
        if (this.inReweavableMode || this.clazz.getType().isAspect()) {
            aspectsAffectingType = new HashSet<String>();
        }
        boolean isChanged = false;
        if (this.clazz.getType().isAspect()) {
            isChanged = true;
        }
        WeaverStateInfo typeWeaverState = this.world.isOverWeaving() ? this.getLazyClassGen().getType().getWeaverState() : null;
        for (ConcreteTypeMunger o : this.typeMungers) {
            boolean bl;
            if (!(o instanceof BcelTypeMunger)) continue;
            Iterator munger = (BcelTypeMunger)o;
            if (typeWeaverState != null && typeWeaverState.isAspectAlreadyApplied(((ConcreteTypeMunger)((Object)munger)).getAspectType()) || !(bl = ((BcelTypeMunger)((Object)munger)).munge(this))) continue;
            isChanged = true;
            if (!this.inReweavableMode && !this.clazz.getType().isAspect()) continue;
            aspectsAffectingType.add(((ConcreteTypeMunger)((Object)munger)).getAspectType().getSignature());
        }
        isChanged = this.weaveDeclareAtMethodCtor(this.clazz) || isChanged;
        isChanged = this.weaveDeclareAtField(this.clazz) || isChanged;
        this.addedSuperInitializersAsList = new ArrayList<IfaceInitList>(this.addedSuperInitializers.values());
        this.addedSuperInitializersAsList = PartialOrder.sort(this.addedSuperInitializersAsList);
        if (this.addedSuperInitializersAsList == null) {
            throw new BCException("circularity in inter-types");
        }
        LazyMethodGen staticInit = this.clazz.getStaticInitializer();
        staticInit.getBody().insert(this.genInitInstructions(this.addedClassInitializers, true));
        ArrayList<LazyMethodGen> methodGens = new ArrayList<LazyMethodGen>(this.clazz.getMethodGens());
        for (LazyMethodGen lazyMethodGen : methodGens) {
            boolean shadowMungerMatched;
            if (!lazyMethodGen.hasBody()) continue;
            if (this.world.isJoinpointSynchronizationEnabled() && this.world.areSynchronizationPointcutsInUse() && lazyMethodGen.getMethod().isSynchronized()) {
                BcelClassWeaver.transformSynchronizedMethod(lazyMethodGen);
            }
            if (!(shadowMungerMatched = this.match(lazyMethodGen))) continue;
            if (this.inReweavableMode || this.clazz.getType().isAspect()) {
                aspectsAffectingType.addAll(this.findAspectsForMungers(lazyMethodGen));
            }
            isChanged = true;
        }
        for (LazyMethodGen lazyMethodGen : methodGens) {
            if (!lazyMethodGen.hasBody()) continue;
            this.implement(lazyMethodGen);
        }
        if (!this.initializationShadows.isEmpty()) {
            ArrayList<LazyMethodGen> recursiveCtors = new ArrayList<LazyMethodGen>();
            while (this.inlineSelfConstructors(methodGens, recursiveCtors)) {
            }
            this.positionAndImplement(this.initializationShadows);
        }
        if (this.lateTypeMungers != null) {
            for (BcelTypeMunger bcelTypeMunger : this.lateTypeMungers) {
                boolean typeMungerAffectedType;
                if (!bcelTypeMunger.matches(this.clazz.getType()) || !(typeMungerAffectedType = bcelTypeMunger.munge(this))) continue;
                isChanged = true;
                if (!this.inReweavableMode && !this.clazz.getType().isAspect()) continue;
                aspectsAffectingType.add(bcelTypeMunger.getAspectType().getSignature());
            }
        }
        if (isChanged) {
            this.clazz.getOrCreateWeaverStateInfo(this.inReweavableMode);
            this.weaveInAddedMethods();
        }
        if (this.inReweavableMode) {
            WeaverStateInfo wsi = this.clazz.getOrCreateWeaverStateInfo(true);
            wsi.addAspectsAffectingType(aspectsAffectingType);
            if (!this.world.isOverWeaving()) {
                wsi.setUnwovenClassFileData(this.ty.getJavaClass().getBytes());
                wsi.setReweavable(true);
            } else {
                wsi.markOverweavingInUse();
            }
        } else {
            this.clazz.getOrCreateWeaverStateInfo(false).setReweavable(false);
        }
        for (LazyMethodGen lazyMethodGen : methodGens) {
            BcelMethod method = lazyMethodGen.getMemberView();
            if (method == null) continue;
            method.wipeJoinpointSignatures();
        }
        return isChanged;
    }

    private static ResolvedMember isOverriding(ResolvedType typeToCheck, ResolvedMember methodThatMightBeGettingOverridden, String mname, String mrettype, int mmods, boolean inSamePackage, UnresolvedType[] methodParamsArray) {
        if (Modifier.isStatic(methodThatMightBeGettingOverridden.getModifiers())) {
            return null;
        }
        if (Modifier.isPrivate(methodThatMightBeGettingOverridden.getModifiers())) {
            return null;
        }
        if (!methodThatMightBeGettingOverridden.getName().equals(mname)) {
            return null;
        }
        if (methodThatMightBeGettingOverridden.getParameterTypes().length != methodParamsArray.length) {
            return null;
        }
        if (!BcelClassWeaver.isVisibilityOverride(mmods, methodThatMightBeGettingOverridden, inSamePackage)) {
            return null;
        }
        if (typeToCheck.getWorld().forDEBUG_bridgingCode) {
            System.err.println("  Bridging:seriously considering this might be getting overridden '" + methodThatMightBeGettingOverridden + "'");
        }
        World w = typeToCheck.getWorld();
        boolean sameParams = true;
        int max = methodThatMightBeGettingOverridden.getParameterTypes().length;
        for (int p = 0; p < max; ++p) {
            UnresolvedType mtmbgoParameter = methodThatMightBeGettingOverridden.getParameterTypes()[p];
            UnresolvedType ptype = methodParamsArray[p];
            if (mtmbgoParameter.isTypeVariableReference()) {
                if (mtmbgoParameter.resolve(w).isAssignableFrom(ptype.resolve(w))) continue;
                sameParams = false;
                continue;
            }
            boolean b = !methodThatMightBeGettingOverridden.getParameterTypes()[p].getErasureSignature().equals(methodParamsArray[p].getErasureSignature());
            UnresolvedType parameterType = methodThatMightBeGettingOverridden.getParameterTypes()[p];
            if (parameterType instanceof UnresolvedTypeVariableReferenceType) {
                parameterType = ((UnresolvedTypeVariableReferenceType)parameterType).getTypeVariable().getFirstBound();
            }
            if (!b) continue;
            sameParams = false;
        }
        if (sameParams) {
            if (typeToCheck.isParameterizedType()) {
                return methodThatMightBeGettingOverridden.getBackingGenericMember();
            }
            if (!methodThatMightBeGettingOverridden.getReturnType().getErasureSignature().equals(mrettype)) {
                ResolvedType subReturn;
                ResolvedType superReturn = typeToCheck.getWorld().resolve(UnresolvedType.forSignature(methodThatMightBeGettingOverridden.getReturnType().getErasureSignature()));
                if (superReturn.isAssignableFrom(subReturn = typeToCheck.getWorld().resolve(UnresolvedType.forSignature(mrettype)))) {
                    return methodThatMightBeGettingOverridden;
                }
            } else {
                return methodThatMightBeGettingOverridden;
            }
        }
        return null;
    }

    static boolean isVisibilityOverride(int methodMods, ResolvedMember inheritedMethod, boolean inSamePackage) {
        boolean isPackageVisible;
        int inheritedModifiers = inheritedMethod.getModifiers();
        if (Modifier.isStatic(inheritedModifiers)) {
            return false;
        }
        if (methodMods == inheritedModifiers) {
            return true;
        }
        if (Modifier.isPrivate(inheritedModifiers)) {
            return false;
        }
        boolean bl = isPackageVisible = !Modifier.isPrivate(inheritedModifiers) && !Modifier.isProtected(inheritedModifiers) && !Modifier.isPublic(inheritedModifiers);
        return !isPackageVisible || inSamePackage;
    }

    public static void checkForOverride(ResolvedType typeToCheck, String mname, String mparams, String mrettype, int mmods, String mpkg, UnresolvedType[] methodParamsArray, List<ResolvedMember> overriddenMethodsCollector) {
        String packageName;
        if (typeToCheck == null) {
            return;
        }
        if (typeToCheck instanceof MissingResolvedTypeWithKnownSignature) {
            return;
        }
        if (typeToCheck.getWorld().forDEBUG_bridgingCode) {
            System.err.println("  Bridging:checking for override of " + mname + " in " + typeToCheck);
        }
        if ((packageName = typeToCheck.getPackageName()) == null) {
            packageName = "";
        }
        boolean inSamePackage = packageName.equals(mpkg);
        ResolvedMember[] methods = typeToCheck.getDeclaredMethods();
        for (int ii = 0; ii < methods.length; ++ii) {
            ResolvedMember methodThatMightBeGettingOverridden = methods[ii];
            ResolvedMember isOverriding = BcelClassWeaver.isOverriding(typeToCheck, methodThatMightBeGettingOverridden, mname, mrettype, mmods, inSamePackage, methodParamsArray);
            if (isOverriding == null) continue;
            overriddenMethodsCollector.add(isOverriding);
        }
        List<ConcreteTypeMunger> l = typeToCheck.isRawType() ? typeToCheck.getGenericType().getInterTypeMungers() : typeToCheck.getInterTypeMungers();
        for (ConcreteTypeMunger o : l) {
            ResolvedMember aMethod;
            ResolvedMember isOverriding;
            BcelTypeMunger element;
            if (!(o instanceof BcelTypeMunger) || !((element = (BcelTypeMunger)o).getMunger() instanceof NewMethodTypeMunger)) continue;
            if (typeToCheck.getWorld().forDEBUG_bridgingCode) {
                System.err.println("Possible ITD candidate " + element);
            }
            if ((isOverriding = BcelClassWeaver.isOverriding(typeToCheck, aMethod = element.getSignature(), mname, mrettype, mmods, inSamePackage, methodParamsArray)) == null) continue;
            overriddenMethodsCollector.add(isOverriding);
        }
        if (typeToCheck.equals(UnresolvedType.OBJECT)) {
            return;
        }
        ResolvedType superclass = typeToCheck.getSuperclass();
        BcelClassWeaver.checkForOverride(superclass, mname, mparams, mrettype, mmods, mpkg, methodParamsArray, overriddenMethodsCollector);
        ResolvedType[] interfaces = typeToCheck.getDeclaredInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            ResolvedType anInterface = interfaces[i];
            BcelClassWeaver.checkForOverride(anInterface, mname, mparams, mrettype, mmods, mpkg, methodParamsArray, overriddenMethodsCollector);
        }
    }

    public static boolean calculateAnyRequiredBridgeMethods(BcelWorld world, LazyClassGen clazz) {
        int i;
        world.ensureAdvancedConfigurationProcessed();
        if (!world.isInJava5Mode()) {
            return false;
        }
        if (clazz.isInterface()) {
            return false;
        }
        boolean didSomething = false;
        List<LazyMethodGen> methods = clazz.getMethodGens();
        HashSet<String> methodsSet = new HashSet<String>();
        for (i = 0; i < methods.size(); ++i) {
            LazyMethodGen aMethod = methods.get(i);
            StringBuilder sb = new StringBuilder(aMethod.getName());
            sb.append(aMethod.getSignature());
            methodsSet.add(sb.toString());
        }
        for (i = 0; i < methods.size(); ++i) {
            LazyMethodGen bridgeToCandidate = methods.get(i);
            if (bridgeToCandidate.isBridgeMethod()) continue;
            String name = bridgeToCandidate.getName();
            String psig = bridgeToCandidate.getParameterSignature();
            String rsig = bridgeToCandidate.getReturnType().getSignature();
            if (bridgeToCandidate.isStatic() || name.endsWith("init>")) continue;
            if (world.forDEBUG_bridgingCode) {
                System.err.println("Bridging: Determining if we have to bridge to " + clazz.getName() + "." + name + "" + bridgeToCandidate.getSignature());
            }
            ResolvedType theSuperclass = clazz.getSuperClass();
            if (world.forDEBUG_bridgingCode) {
                System.err.println("Bridging: Checking supertype " + theSuperclass);
            }
            String pkgName = clazz.getPackageName();
            UnresolvedType[] bm = BcelWorld.fromBcel(bridgeToCandidate.getArgumentTypes());
            ArrayList<ResolvedMember> overriddenMethodsCollector = new ArrayList<ResolvedMember>();
            BcelClassWeaver.checkForOverride(theSuperclass, name, psig, rsig, bridgeToCandidate.getAccessFlags(), pkgName, bm, overriddenMethodsCollector);
            if (overriddenMethodsCollector.size() != 0) {
                for (ResolvedMember overriddenMethod : overriddenMethodsCollector) {
                    String key = overriddenMethod.getName() + overriddenMethod.getSignatureErased();
                    boolean alreadyHaveABridgeMethod = methodsSet.contains(key);
                    if (alreadyHaveABridgeMethod) continue;
                    if (world.forDEBUG_bridgingCode) {
                        System.err.println("Bridging:bridging to '" + overriddenMethod + "'");
                    }
                    BcelClassWeaver.createBridgeMethod(world, bridgeToCandidate, clazz, overriddenMethod);
                    methodsSet.add(key);
                    didSomething = true;
                }
            }
            String[] interfaces = clazz.getInterfaceNames();
            for (int j = 0; j < interfaces.length; ++j) {
                if (world.forDEBUG_bridgingCode) {
                    System.err.println("Bridging:checking superinterface " + interfaces[j]);
                }
                ResolvedType interfaceType = world.resolve(interfaces[j]);
                overriddenMethodsCollector.clear();
                BcelClassWeaver.checkForOverride(interfaceType, name, psig, rsig, bridgeToCandidate.getAccessFlags(), clazz.getPackageName(), bm, overriddenMethodsCollector);
                for (ResolvedMember overriddenMethod : overriddenMethodsCollector) {
                    String key = new StringBuffer().append(overriddenMethod.getName()).append(overriddenMethod.getSignatureErased()).toString();
                    boolean alreadyHaveABridgeMethod = methodsSet.contains(key);
                    if (alreadyHaveABridgeMethod) continue;
                    BcelClassWeaver.createBridgeMethod(world, bridgeToCandidate, clazz, overriddenMethod);
                    methodsSet.add(key);
                    didSomething = true;
                    if (!world.forDEBUG_bridgingCode) continue;
                    System.err.println("Bridging:bridging to " + overriddenMethod);
                }
            }
        }
        return didSomething;
    }

    private boolean weaveDeclareAtMethodCtor(LazyClassGen clazz) {
        List<LazyMethodGen> members;
        AnnotationGen a;
        boolean modificationOccured;
        ArrayList<DeclareAnnotation> worthRetrying;
        List<DeclareAnnotation> decaMs;
        ArrayList<Integer> reportedProblems = new ArrayList<Integer>();
        List<DeclareAnnotation> allDecams = this.world.getDeclareAnnotationOnMethods();
        if (allDecams.isEmpty()) {
            return false;
        }
        boolean isChanged = false;
        List<ConcreteTypeMunger> itdMethodsCtors = this.getITDSubset(clazz, ResolvedTypeMunger.Method);
        itdMethodsCtors.addAll(this.getITDSubset(clazz, ResolvedTypeMunger.Constructor));
        if (!itdMethodsCtors.isEmpty()) {
            isChanged = this.weaveAtMethodOnITDSRepeatedly(allDecams, itdMethodsCtors, reportedProblems);
        }
        if ((decaMs = this.getMatchingSubset(allDecams, clazz.getType())).isEmpty()) {
            return false;
        }
        HashSet<DeclareAnnotation> unusedDecams = new HashSet<DeclareAnnotation>();
        unusedDecams.addAll(decaMs);
        if (this.addedLazyMethodGens != null) {
            for (LazyMethodGen method : this.addedLazyMethodGens) {
                ResolvedMemberImpl resolvedmember = new ResolvedMemberImpl(ResolvedMember.METHOD, method.getEnclosingClass().getType(), method.getAccessFlags(), BcelWorld.fromBcel(method.getReturnType()), method.getName(), BcelWorld.fromBcel(method.getArgumentTypes()), UnresolvedType.forNames(method.getDeclaredExceptions()));
                resolvedmember.setAnnotationTypes(method.getAnnotationTypes());
                resolvedmember.setAnnotations(method.getAnnotations());
                worthRetrying = new ArrayList();
                modificationOccured = false;
                for (DeclareAnnotation declareAnnotation : decaMs) {
                    if (declareAnnotation.matches(resolvedmember, this.world)) {
                        if (this.doesAlreadyHaveAnnotation(resolvedmember, declareAnnotation, reportedProblems, false)) {
                            unusedDecams.remove(declareAnnotation);
                            continue;
                        }
                        AnnotationGen annotationGen = ((BcelAnnotation)declareAnnotation.getAnnotation()).getBcelAnnotation();
                        BcelAnnotation aj = new BcelAnnotation(new AnnotationGen(annotationGen, clazz.getConstantPool(), true), this.world);
                        method.addAnnotation(aj);
                        resolvedmember.addAnnotation(declareAnnotation.getAnnotation());
                        AsmRelationshipProvider.addDeclareAnnotationMethodRelationship(declareAnnotation.getSourceLocation(), clazz.getName(), resolvedmember, this.world.getModelAsAsmManager());
                        this.reportMethodCtorWeavingMessage(clazz, resolvedmember, declareAnnotation, method.getDeclarationLineNumber());
                        isChanged = true;
                        modificationOccured = true;
                        unusedDecams.remove(declareAnnotation);
                        continue;
                    }
                    if (declareAnnotation.isStarredAnnotationPattern()) continue;
                    worthRetrying.add(declareAnnotation);
                }
                while (!worthRetrying.isEmpty() && modificationOccured) {
                    modificationOccured = false;
                    ArrayList<DeclareAnnotation> forRemoval = new ArrayList<DeclareAnnotation>();
                    for (DeclareAnnotation declareAnnotation : worthRetrying) {
                        if (!declareAnnotation.matches(resolvedmember, this.world)) continue;
                        if (this.doesAlreadyHaveAnnotation(resolvedmember, declareAnnotation, reportedProblems, false)) {
                            unusedDecams.remove(declareAnnotation);
                            continue;
                        }
                        a = ((BcelAnnotation)declareAnnotation.getAnnotation()).getBcelAnnotation();
                        BcelAnnotation aj = new BcelAnnotation(new AnnotationGen(a, clazz.getConstantPool(), true), this.world);
                        method.addAnnotation(aj);
                        resolvedmember.addAnnotation(declareAnnotation.getAnnotation());
                        AsmRelationshipProvider.addDeclareAnnotationMethodRelationship(declareAnnotation.getSourceLocation(), clazz.getName(), resolvedmember, this.world.getModelAsAsmManager());
                        isChanged = true;
                        modificationOccured = true;
                        forRemoval.add(declareAnnotation);
                        unusedDecams.remove(declareAnnotation);
                    }
                    worthRetrying.removeAll(forRemoval);
                }
            }
        }
        if (!(members = clazz.getMethodGens()).isEmpty()) {
            for (int memberCounter = 0; memberCounter < members.size(); ++memberCounter) {
                Object decaM22;
                LazyMethodGen mg = members.get(memberCounter);
                if (mg.getName().startsWith("ajc$")) continue;
                worthRetrying = new ArrayList<DeclareAnnotation>();
                modificationOccured = false;
                ArrayList<AnnotationGen> annotationsToAdd = null;
                for (DeclareAnnotation declareAnnotation : decaMs) {
                    if (declareAnnotation.matches(mg.getMemberView(), this.world)) {
                        if (this.doesAlreadyHaveAnnotation(mg.getMemberView(), declareAnnotation, reportedProblems, true)) {
                            unusedDecams.remove(declareAnnotation);
                            continue;
                        }
                        if (annotationsToAdd == null) {
                            annotationsToAdd = new ArrayList<AnnotationGen>();
                        }
                        a = ((BcelAnnotation)declareAnnotation.getAnnotation()).getBcelAnnotation();
                        AnnotationGen ag = new AnnotationGen(a, clazz.getConstantPool(), true);
                        annotationsToAdd.add(ag);
                        mg.addAnnotation(declareAnnotation.getAnnotation());
                        AsmRelationshipProvider.addDeclareAnnotationMethodRelationship(declareAnnotation.getSourceLocation(), clazz.getName(), mg.getMemberView(), this.world.getModelAsAsmManager());
                        this.reportMethodCtorWeavingMessage(clazz, mg.getMemberView(), declareAnnotation, mg.getDeclarationLineNumber());
                        isChanged = true;
                        modificationOccured = true;
                        unusedDecams.remove(declareAnnotation);
                        continue;
                    }
                    if (declareAnnotation.isStarredAnnotationPattern()) continue;
                    worthRetrying.add(declareAnnotation);
                }
                while (!worthRetrying.isEmpty() && modificationOccured) {
                    modificationOccured = false;
                    ArrayList<Object> arrayList = new ArrayList<Object>();
                    for (Object decaM22 : worthRetrying) {
                        if (!((DeclareAnnotation)decaM22).matches(mg.getMemberView(), this.world)) continue;
                        if (this.doesAlreadyHaveAnnotation(mg.getMemberView(), (DeclareAnnotation)decaM22, reportedProblems, true)) {
                            unusedDecams.remove(decaM22);
                            continue;
                        }
                        if (annotationsToAdd == null) {
                            annotationsToAdd = new ArrayList();
                        }
                        AnnotationGen a2 = ((BcelAnnotation)((DeclareAnnotation)decaM22).getAnnotation()).getBcelAnnotation();
                        AnnotationGen ag = new AnnotationGen(a2, clazz.getConstantPool(), true);
                        annotationsToAdd.add(ag);
                        mg.addAnnotation(((DeclareAnnotation)decaM22).getAnnotation());
                        AsmRelationshipProvider.addDeclareAnnotationMethodRelationship(((PatternNode)decaM22).getSourceLocation(), clazz.getName(), mg.getMemberView(), this.world.getModelAsAsmManager());
                        isChanged = true;
                        modificationOccured = true;
                        arrayList.add(decaM22);
                        unusedDecams.remove(decaM22);
                    }
                    worthRetrying.removeAll(arrayList);
                }
                if (annotationsToAdd == null) continue;
                Method method = mg.getMethod();
                MethodGen methodGen = new MethodGen(method, clazz.getClassName(), clazz.getConstantPool(), false);
                decaM22 = annotationsToAdd.iterator();
                while (decaM22.hasNext()) {
                    AnnotationGen a3 = (AnnotationGen)decaM22.next();
                    methodGen.addAnnotation(a3);
                }
                Method newMethod = methodGen.getMethod();
                members.set(memberCounter, new LazyMethodGen(newMethod, clazz));
            }
            this.checkUnusedDeclareAts(unusedDecams, false);
        }
        return isChanged;
    }

    private void reportMethodCtorWeavingMessage(LazyClassGen clazz, ResolvedMember member, DeclareAnnotation decaM, int memberLineNumber) {
        if (!this.getWorld().getMessageHandler().isIgnoring(IMessage.WEAVEINFO)) {
            StringBuffer parmString = new StringBuffer("(");
            UnresolvedType[] paramTypes = member.getParameterTypes();
            for (int i = 0; i < paramTypes.length; ++i) {
                UnresolvedType type = paramTypes[i];
                String s = org.aspectj.apache.bcel.classfile.Utility.signatureToString(type.getSignature());
                if (s.lastIndexOf(46) != -1) {
                    s = s.substring(s.lastIndexOf(46) + 1);
                }
                parmString.append(s);
                if (i + 1 >= paramTypes.length) continue;
                parmString.append(",");
            }
            parmString.append(")");
            String methodName = member.getName();
            StringBuffer sig = new StringBuffer();
            sig.append(org.aspectj.apache.bcel.classfile.Utility.accessToString(member.getModifiers()));
            sig.append(" ");
            sig.append(member.getReturnType().toString());
            sig.append(" ");
            sig.append(member.getDeclaringType().toString());
            sig.append(".");
            sig.append(methodName.equals("<init>") ? "new" : methodName);
            sig.append(parmString);
            StringBuffer loc = new StringBuffer();
            if (clazz.getFileName() == null) {
                loc.append("no debug info available");
            } else {
                loc.append(clazz.getFileName());
                if (memberLineNumber != -1) {
                    loc.append(":" + memberLineNumber);
                }
            }
            this.getWorld().getMessageHandler().handleMessage(WeaveMessage.constructWeavingMessage(WeaveMessage.WEAVEMESSAGE_ANNOTATES, new String[]{sig.toString(), loc.toString(), decaM.getAnnotationString(), methodName.startsWith("<init>") ? "constructor" : "method", decaM.getAspect().toString(), Utility.beautifyLocation(decaM.getSourceLocation())}));
        }
    }

    private List<DeclareAnnotation> getMatchingSubset(List<DeclareAnnotation> declareAnnotations, ResolvedType type) {
        ArrayList<DeclareAnnotation> subset = new ArrayList<DeclareAnnotation>();
        for (DeclareAnnotation da : declareAnnotations) {
            if (!da.couldEverMatch(type)) continue;
            subset.add(da);
        }
        return subset;
    }

    private List<ConcreteTypeMunger> getITDSubset(LazyClassGen clazz, ResolvedTypeMunger.Kind wantedKind) {
        ArrayList<ConcreteTypeMunger> subset = new ArrayList<ConcreteTypeMunger>();
        for (ConcreteTypeMunger typeMunger : clazz.getBcelObjectType().getTypeMungers()) {
            if (typeMunger.getMunger().getKind() != wantedKind) continue;
            subset.add(typeMunger);
        }
        return subset;
    }

    public LazyMethodGen locateAnnotationHolderForFieldMunger(LazyClassGen clazz, ConcreteTypeMunger fieldMunger) {
        NewFieldTypeMunger newFieldMunger = (NewFieldTypeMunger)fieldMunger.getMunger();
        ResolvedMember lookingFor = AjcMemberMaker.interFieldInitializer(newFieldMunger.getSignature(), clazz.getType());
        for (LazyMethodGen method : clazz.getMethodGens()) {
            if (!method.getName().equals(lookingFor.getName())) continue;
            return method;
        }
        return null;
    }

    public LazyMethodGen locateAnnotationHolderForMethodCtorMunger(LazyClassGen clazz, ConcreteTypeMunger methodCtorMunger) {
        ResolvedTypeMunger nftm;
        ResolvedTypeMunger rtMunger = methodCtorMunger.getMunger();
        ResolvedMember lookingFor = null;
        if (rtMunger instanceof NewMethodTypeMunger) {
            nftm = (NewMethodTypeMunger)rtMunger;
            lookingFor = AjcMemberMaker.interMethodDispatcher(nftm.getSignature(), methodCtorMunger.getAspectType());
        } else if (rtMunger instanceof NewConstructorTypeMunger) {
            nftm = (NewConstructorTypeMunger)rtMunger;
            lookingFor = AjcMemberMaker.postIntroducedConstructor(methodCtorMunger.getAspectType(), nftm.getSignature().getDeclaringType(), nftm.getSignature().getParameterTypes());
        } else {
            throw new BCException("Not sure what this is: " + methodCtorMunger);
        }
        String name = lookingFor.getName();
        String paramSignature = lookingFor.getParameterSignature();
        for (LazyMethodGen member : clazz.getMethodGens()) {
            if (!member.getName().equals(name) || !member.getParameterSignature().equals(paramSignature)) continue;
            return member;
        }
        return null;
    }

    private boolean weaveAtFieldRepeatedly(List<DeclareAnnotation> decaFs, List<ConcreteTypeMunger> itdFields, List<Integer> reportedErrors) {
        boolean isChanged = false;
        for (BcelTypeMunger bcelTypeMunger : itdFields) {
            ResolvedMember itdIsActually = bcelTypeMunger.getSignature();
            LinkedHashSet<DeclareAnnotation> worthRetrying = new LinkedHashSet<DeclareAnnotation>();
            boolean modificationOccured = false;
            for (DeclareAnnotation decaF : decaFs) {
                if (decaF.matches(itdIsActually, this.world)) {
                    LazyMethodGen annotationHolder;
                    if (decaF.isRemover()) {
                        annotationHolder = this.locateAnnotationHolderForFieldMunger(this.clazz, bcelTypeMunger);
                        if (annotationHolder.hasAnnotation(decaF.getAnnotationType())) {
                            isChanged = true;
                            annotationHolder.removeAnnotation(decaF.getAnnotationType());
                            AsmRelationshipProvider.addDeclareAnnotationRelationship(this.world.getModelAsAsmManager(), decaF.getSourceLocation(), itdIsActually.getSourceLocation(), true);
                            continue;
                        }
                        worthRetrying.add(decaF);
                        continue;
                    }
                    annotationHolder = this.locateAnnotationHolderForFieldMunger(this.clazz, bcelTypeMunger);
                    if (this.doesAlreadyHaveAnnotation(annotationHolder, itdIsActually, decaF, reportedErrors)) continue;
                    annotationHolder.addAnnotation(decaF.getAnnotation());
                    AsmRelationshipProvider.addDeclareAnnotationRelationship(this.world.getModelAsAsmManager(), decaF.getSourceLocation(), itdIsActually.getSourceLocation(), false);
                    isChanged = true;
                    modificationOccured = true;
                    continue;
                }
                if (decaF.isStarredAnnotationPattern()) continue;
                worthRetrying.add(decaF);
            }
            while (!worthRetrying.isEmpty() && modificationOccured) {
                modificationOccured = false;
                ArrayList<DeclareAnnotation> forRemoval = new ArrayList<DeclareAnnotation>();
                for (DeclareAnnotation decaF : worthRetrying) {
                    LazyMethodGen annotationHolder;
                    if (!decaF.matches(itdIsActually, this.world)) continue;
                    if (decaF.isRemover()) {
                        annotationHolder = this.locateAnnotationHolderForFieldMunger(this.clazz, bcelTypeMunger);
                        if (!annotationHolder.hasAnnotation(decaF.getAnnotationType())) continue;
                        isChanged = true;
                        annotationHolder.removeAnnotation(decaF.getAnnotationType());
                        AsmRelationshipProvider.addDeclareAnnotationRelationship(this.world.getModelAsAsmManager(), decaF.getSourceLocation(), itdIsActually.getSourceLocation(), true);
                        forRemoval.add(decaF);
                        continue;
                    }
                    annotationHolder = this.locateAnnotationHolderForFieldMunger(this.clazz, bcelTypeMunger);
                    if (this.doesAlreadyHaveAnnotation(annotationHolder, itdIsActually, decaF, reportedErrors)) continue;
                    annotationHolder.addAnnotation(decaF.getAnnotation());
                    AsmRelationshipProvider.addDeclareAnnotationRelationship(this.world.getModelAsAsmManager(), decaF.getSourceLocation(), itdIsActually.getSourceLocation(), false);
                    isChanged = true;
                    modificationOccured = true;
                    forRemoval.add(decaF);
                }
                worthRetrying.removeAll(forRemoval);
            }
        }
        return isChanged;
    }

    private boolean weaveAtMethodOnITDSRepeatedly(List<DeclareAnnotation> decaMCs, List<ConcreteTypeMunger> itdsForMethodAndConstructor, List<Integer> reportedErrors) {
        boolean isChanged = false;
        AsmManager asmManager = this.world.getModelAsAsmManager();
        for (ConcreteTypeMunger methodctorMunger : itdsForMethodAndConstructor) {
            ResolvedMember unMangledInterMethod = methodctorMunger.getSignature();
            ArrayList<DeclareAnnotation> worthRetrying = new ArrayList<DeclareAnnotation>();
            boolean modificationOccured = false;
            for (DeclareAnnotation decaMC : decaMCs) {
                if (decaMC.matches(unMangledInterMethod, this.world)) {
                    LazyMethodGen annotationHolder = this.locateAnnotationHolderForMethodCtorMunger(this.clazz, methodctorMunger);
                    if (annotationHolder == null || this.doesAlreadyHaveAnnotation(annotationHolder, unMangledInterMethod, decaMC, reportedErrors)) continue;
                    annotationHolder.addAnnotation(decaMC.getAnnotation());
                    isChanged = true;
                    AsmRelationshipProvider.addDeclareAnnotationRelationship(asmManager, decaMC.getSourceLocation(), unMangledInterMethod.getSourceLocation(), false);
                    this.reportMethodCtorWeavingMessage(this.clazz, unMangledInterMethod, decaMC, -1);
                    modificationOccured = true;
                    continue;
                }
                if (decaMC.isStarredAnnotationPattern()) continue;
                worthRetrying.add(decaMC);
            }
            while (!worthRetrying.isEmpty() && modificationOccured) {
                modificationOccured = false;
                ArrayList<DeclareAnnotation> forRemoval = new ArrayList<DeclareAnnotation>();
                for (DeclareAnnotation decaMC : worthRetrying) {
                    if (decaMC.matches(unMangledInterMethod, this.world)) {
                        LazyMethodGen annotationHolder = this.locateAnnotationHolderForFieldMunger(this.clazz, methodctorMunger);
                        if (this.doesAlreadyHaveAnnotation(annotationHolder, unMangledInterMethod, decaMC, reportedErrors)) continue;
                        annotationHolder.addAnnotation(decaMC.getAnnotation());
                        unMangledInterMethod.addAnnotation(decaMC.getAnnotation());
                        AsmRelationshipProvider.addDeclareAnnotationRelationship(asmManager, decaMC.getSourceLocation(), unMangledInterMethod.getSourceLocation(), false);
                        isChanged = true;
                        modificationOccured = true;
                        forRemoval.add(decaMC);
                    }
                    worthRetrying.removeAll(forRemoval);
                }
            }
        }
        return isChanged;
    }

    private boolean dontAddTwice(DeclareAnnotation decaF, AnnotationAJ[] dontAddMeTwice) {
        for (AnnotationAJ ann : dontAddMeTwice) {
            if (ann == null || !decaF.getAnnotation().getTypeName().equals(ann.getTypeName())) continue;
            return true;
        }
        return false;
    }

    private AnnotationAJ[] removeFromAnnotationsArray(AnnotationAJ[] annotations, AnnotationAJ annotation) {
        for (int i = 0; i < annotations.length; ++i) {
            if (annotations[i] == null || !annotation.getTypeName().equals(annotations[i].getTypeName())) continue;
            AnnotationAJ[] newArray = new AnnotationAJ[annotations.length - 1];
            int index = 0;
            for (int j = 0; j < annotations.length; ++j) {
                if (j == i) continue;
                newArray[index++] = annotations[j];
            }
            return newArray;
        }
        return annotations;
    }

    private boolean weaveDeclareAtField(LazyClassGen clazz) {
        List<DeclareAnnotation> decafs;
        ArrayList<Integer> reportedProblems = new ArrayList<Integer>();
        List<DeclareAnnotation> allDecafs = this.world.getDeclareAnnotationOnFields();
        if (allDecafs.isEmpty()) {
            return false;
        }
        boolean typeIsChanged = false;
        List<ConcreteTypeMunger> relevantItdFields = this.getITDSubset(clazz, ResolvedTypeMunger.Field);
        if (relevantItdFields != null) {
            typeIsChanged = this.weaveAtFieldRepeatedly(allDecafs, relevantItdFields, reportedProblems);
        }
        if ((decafs = this.getMatchingSubset(allDecafs, clazz.getType())).isEmpty()) {
            return typeIsChanged;
        }
        List<BcelField> fields = clazz.getFieldGens();
        if (fields != null) {
            HashSet<DeclareAnnotation> unusedDecafs = new HashSet<DeclareAnnotation>();
            unusedDecafs.addAll(decafs);
            for (BcelField field : fields) {
                if (field.getName().startsWith("ajc$")) continue;
                LinkedHashSet<DeclareAnnotation> worthRetrying = new LinkedHashSet<DeclareAnnotation>();
                boolean modificationOccured = false;
                AnnotationAJ[] dontAddMeTwice = field.getAnnotations();
                for (DeclareAnnotation decaf : decafs) {
                    if (decaf.getAnnotation() == null) {
                        return false;
                    }
                    if (decaf.matches(field, this.world)) {
                        if (decaf.isRemover()) {
                            AnnotationAJ annotation = decaf.getAnnotation();
                            if (field.hasAnnotation(annotation.getType())) {
                                typeIsChanged = true;
                                field.removeAnnotation(annotation);
                                AsmRelationshipProvider.addDeclareAnnotationFieldRelationship(this.world.getModelAsAsmManager(), decaf.getSourceLocation(), clazz.getName(), field, true);
                                this.reportFieldAnnotationWeavingMessage(clazz, field, decaf, true);
                                dontAddMeTwice = this.removeFromAnnotationsArray(dontAddMeTwice, annotation);
                            } else {
                                worthRetrying.add(decaf);
                            }
                            unusedDecafs.remove(decaf);
                            continue;
                        }
                        if (!this.dontAddTwice(decaf, dontAddMeTwice)) {
                            if (this.doesAlreadyHaveAnnotation(field, decaf, reportedProblems, true)) {
                                unusedDecafs.remove(decaf);
                                continue;
                            }
                            field.addAnnotation(decaf.getAnnotation());
                        }
                        AsmRelationshipProvider.addDeclareAnnotationFieldRelationship(this.world.getModelAsAsmManager(), decaf.getSourceLocation(), clazz.getName(), field, false);
                        this.reportFieldAnnotationWeavingMessage(clazz, field, decaf, false);
                        typeIsChanged = true;
                        modificationOccured = true;
                        unusedDecafs.remove(decaf);
                        continue;
                    }
                    if (decaf.isStarredAnnotationPattern() && !decaf.isRemover()) continue;
                    worthRetrying.add(decaf);
                }
                while (!worthRetrying.isEmpty() && modificationOccured) {
                    modificationOccured = false;
                    ArrayList<DeclareAnnotation> forRemoval = new ArrayList<DeclareAnnotation>();
                    for (DeclareAnnotation decaF : worthRetrying) {
                        if (!decaF.matches(field, this.world)) continue;
                        if (decaF.isRemover()) {
                            AnnotationAJ annotation = decaF.getAnnotation();
                            if (!field.hasAnnotation(annotation.getType())) continue;
                            modificationOccured = true;
                            typeIsChanged = true;
                            forRemoval.add(decaF);
                            field.removeAnnotation(annotation);
                            AsmRelationshipProvider.addDeclareAnnotationFieldRelationship(this.world.getModelAsAsmManager(), decaF.getSourceLocation(), clazz.getName(), field, true);
                            this.reportFieldAnnotationWeavingMessage(clazz, field, decaF, true);
                            continue;
                        }
                        unusedDecafs.remove(decaF);
                        if (this.doesAlreadyHaveAnnotation(field, decaF, reportedProblems, true)) continue;
                        field.addAnnotation(decaF.getAnnotation());
                        AsmRelationshipProvider.addDeclareAnnotationFieldRelationship(this.world.getModelAsAsmManager(), decaF.getSourceLocation(), clazz.getName(), field, false);
                        modificationOccured = true;
                        typeIsChanged = true;
                        forRemoval.add(decaF);
                    }
                    worthRetrying.removeAll(forRemoval);
                }
            }
            this.checkUnusedDeclareAts(unusedDecafs, true);
        }
        return typeIsChanged;
    }

    private void checkUnusedDeclareAts(Set<DeclareAnnotation> unusedDecaTs, boolean isDeclareAtField) {
        for (DeclareAnnotation declA : unusedDecaTs) {
            boolean shouldCheck;
            boolean bl = shouldCheck = declA.isExactPattern() || declA.getSignaturePattern().getExactDeclaringTypes().size() != 0;
            if (shouldCheck && declA.getKind() != DeclareAnnotation.AT_CONSTRUCTOR) {
                if (declA.getSignaturePattern().isMatchOnAnyName()) {
                    shouldCheck = false;
                } else {
                    List<ExactTypePattern> declaringTypePatterns = declA.getSignaturePattern().getExactDeclaringTypes();
                    if (declaringTypePatterns.size() == 0) {
                        shouldCheck = false;
                    } else {
                        for (ExactTypePattern exactTypePattern : declaringTypePatterns) {
                            if (!exactTypePattern.isIncludeSubtypes()) continue;
                            shouldCheck = false;
                            break;
                        }
                    }
                }
            }
            if (!shouldCheck) continue;
            boolean itdMatch = false;
            List<ConcreteTypeMunger> lst = this.clazz.getType().getInterTypeMungers();
            Iterator<ConcreteTypeMunger> iterator = lst.iterator();
            while (iterator.hasNext() && !itdMatch) {
                ConcreteTypeMunger element = iterator.next();
                if (element.getMunger() instanceof NewFieldTypeMunger) {
                    NewFieldTypeMunger nftm = (NewFieldTypeMunger)element.getMunger();
                    itdMatch = declA.matches(nftm.getSignature(), this.world);
                    continue;
                }
                if (element.getMunger() instanceof NewMethodTypeMunger) {
                    NewMethodTypeMunger nmtm = (NewMethodTypeMunger)element.getMunger();
                    itdMatch = declA.matches(nmtm.getSignature(), this.world);
                    continue;
                }
                if (!(element.getMunger() instanceof NewConstructorTypeMunger)) continue;
                NewConstructorTypeMunger nctm = (NewConstructorTypeMunger)element.getMunger();
                itdMatch = declA.matches(nctm.getSignature(), this.world);
            }
            if (itdMatch) continue;
            Message message = null;
            message = isDeclareAtField ? new Message("The field '" + declA.getSignaturePattern().toString() + "' does not exist", declA.getSourceLocation(), true) : new Message("The method '" + declA.getSignaturePattern().toString() + "' does not exist", declA.getSourceLocation(), true);
            this.world.getMessageHandler().handleMessage(message);
        }
    }

    private void reportFieldAnnotationWeavingMessage(LazyClassGen clazz, BcelField theField, DeclareAnnotation decaf, boolean isRemove) {
        if (!this.getWorld().getMessageHandler().isIgnoring(IMessage.WEAVEINFO)) {
            this.world.getMessageHandler().handleMessage(WeaveMessage.constructWeavingMessage(isRemove ? WeaveMessage.WEAVEMESSAGE_REMOVES_ANNOTATION : WeaveMessage.WEAVEMESSAGE_ANNOTATES, new String[]{theField.getFieldAsIs().toString() + "' of type '" + clazz.getName(), clazz.getFileName(), decaf.getAnnotationString(), "field", decaf.getAspect().toString(), Utility.beautifyLocation(decaf.getSourceLocation())}));
        }
    }

    private boolean doesAlreadyHaveAnnotation(ResolvedMember rm, DeclareAnnotation deca, List<Integer> reportedProblems, boolean reportError) {
        if (rm.hasAnnotation(deca.getAnnotationType())) {
            Integer uniqueID;
            if (reportError && this.world.getLint().elementAlreadyAnnotated.isEnabled() && !reportedProblems.contains(uniqueID = new Integer(rm.hashCode() * deca.hashCode()))) {
                reportedProblems.add(uniqueID);
                this.world.getLint().elementAlreadyAnnotated.signal(new String[]{rm.toString(), deca.getAnnotationType().toString()}, rm.getSourceLocation(), new ISourceLocation[]{deca.getSourceLocation()});
            }
            return true;
        }
        return false;
    }

    private boolean doesAlreadyHaveAnnotation(LazyMethodGen rm, ResolvedMember itdfieldsig, DeclareAnnotation deca, List<Integer> reportedProblems) {
        if (rm != null && rm.hasAnnotation(deca.getAnnotationType())) {
            Integer uniqueID;
            if (this.world.getLint().elementAlreadyAnnotated.isEnabled() && !reportedProblems.contains(uniqueID = new Integer(rm.hashCode() * deca.hashCode()))) {
                reportedProblems.add(uniqueID);
                reportedProblems.add(new Integer(itdfieldsig.hashCode() * deca.hashCode()));
                this.world.getLint().elementAlreadyAnnotated.signal(new String[]{itdfieldsig.toString(), deca.getAnnotationType().toString()}, rm.getSourceLocation(), new ISourceLocation[]{deca.getSourceLocation()});
            }
            return true;
        }
        return false;
    }

    private Set<String> findAspectsForMungers(LazyMethodGen mg) {
        HashSet<String> aspectsAffectingType = new HashSet<String>();
        for (BcelShadow shadow : mg.matchedShadows) {
            for (ShadowMunger munger : shadow.getMungers()) {
                BcelAdvice bcelAdvice;
                if (!(munger instanceof BcelAdvice) || (bcelAdvice = (BcelAdvice)munger).getConcreteAspect() == null) continue;
                aspectsAffectingType.add(bcelAdvice.getConcreteAspect().getSignature());
            }
        }
        return aspectsAffectingType;
    }

    private boolean inlineSelfConstructors(List<LazyMethodGen> methodGens, List<LazyMethodGen> recursiveCtors) {
        boolean inlinedSomething = false;
        ArrayList<LazyMethodGen> newRecursiveCtors = new ArrayList<LazyMethodGen>();
        for (LazyMethodGen methodGen : methodGens) {
            InstructionHandle ih;
            if (!methodGen.getName().equals("<init>") || (ih = this.findSuperOrThisCall(methodGen)) == null || !this.isThisCall(ih)) continue;
            LazyMethodGen donor = this.getCalledMethod(ih);
            if (donor.equals(methodGen)) {
                newRecursiveCtors.add(donor);
                continue;
            }
            if (recursiveCtors.contains(donor)) continue;
            BcelClassWeaver.inlineMethod(donor, methodGen, ih);
            inlinedSomething = true;
        }
        recursiveCtors.addAll(newRecursiveCtors);
        return inlinedSomething;
    }

    private void positionAndImplement(List<BcelShadow> initializationShadows) {
        for (BcelShadow s : initializationShadows) {
            this.positionInitializationShadow(s);
            s.implement();
        }
    }

    private void positionInitializationShadow(BcelShadow s) {
        LazyMethodGen mg = s.getEnclosingMethod();
        InstructionHandle call = this.findSuperOrThisCall(mg);
        InstructionList body = mg.getBody();
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        if (s.getKind() == Shadow.PreInitialization) {
            r.associateWithTargets(Range.genStart(body, body.getStart().getNext()), Range.genEnd(body, call.getPrev()));
        } else {
            r.associateWithTargets(Range.genStart(body, call.getNext()), Range.genEnd(body));
        }
    }

    private boolean isThisCall(InstructionHandle ih) {
        InvokeInstruction inst = (InvokeInstruction)ih.getInstruction();
        return inst.getClassName(this.cpg).equals(this.clazz.getName());
    }

    public static void inlineMethod(LazyMethodGen donor, LazyMethodGen recipient, InstructionHandle call) {
        InstructionFactory fact = recipient.getEnclosingClass().getFactory();
        IntMap frameEnv = new IntMap();
        InstructionList argumentStores = BcelClassWeaver.genArgumentStores(donor, recipient, frameEnv, fact);
        InstructionList inlineInstructions = BcelClassWeaver.genInlineInstructions(donor, recipient, frameEnv, fact, false);
        inlineInstructions.insert(argumentStores);
        recipient.getBody().append(call, inlineInstructions);
        Utility.deleteInstruction(call, recipient);
    }

    public static void transformSynchronizedMethod(LazyMethodGen synchronizedMethod) {
        if (trace.isTraceEnabled()) {
            trace.enter("transformSynchronizedMethod", synchronizedMethod);
        }
        InstructionFactory fact = synchronizedMethod.getEnclosingClass().getFactory();
        InstructionList body = synchronizedMethod.getBody();
        InstructionList prepend = new InstructionList();
        Type enclosingClassType = BcelWorld.makeBcelType(synchronizedMethod.getEnclosingClass().getType());
        if (synchronizedMethod.isStatic()) {
            if (synchronizedMethod.getEnclosingClass().isAtLeastJava5()) {
                int slotForLockObject = synchronizedMethod.allocateLocal(enclosingClassType);
                prepend.append(fact.createConstant(enclosingClassType));
                prepend.append(InstructionFactory.createDup(1));
                prepend.append(InstructionFactory.createStore(enclosingClassType, slotForLockObject));
                prepend.append(InstructionFactory.MONITORENTER);
                InstructionList finallyBlock = new InstructionList();
                finallyBlock.append(InstructionFactory.createLoad(Type.getType(Class.class), slotForLockObject));
                finallyBlock.append(InstructionConstants.MONITOREXIT);
                finallyBlock.append(InstructionConstants.ATHROW);
                ArrayList<InstructionHandle> rets = new ArrayList<InstructionHandle>();
                for (InstructionHandle walker = body.getStart(); walker != null; walker = walker.getNext()) {
                    if (!walker.getInstruction().isReturnInstruction()) continue;
                    rets.add(walker);
                }
                if (!rets.isEmpty()) {
                    for (InstructionHandle element : rets) {
                        InstructionList monitorExitBlock = new InstructionList();
                        monitorExitBlock.append(InstructionFactory.createLoad(enclosingClassType, slotForLockObject));
                        monitorExitBlock.append(InstructionConstants.MONITOREXIT);
                        InstructionHandle monitorExitBlockStart = body.insert(element, monitorExitBlock);
                        for (InstructionTargeter targeter : element.getTargetersCopy()) {
                            if (targeter instanceof LocalVariableTag || targeter instanceof LineNumberTag) continue;
                            if (targeter instanceof InstructionBranch) {
                                targeter.updateTarget(element, monitorExitBlockStart);
                                continue;
                            }
                            throw new BCException("Unexpected targeter encountered during transform: " + targeter);
                        }
                    }
                }
                InstructionHandle finallyStart = finallyBlock.getStart();
                InstructionHandle tryPosition = body.getStart();
                InstructionHandle catchPosition = body.getEnd();
                body.insert(body.getStart(), prepend);
                synchronizedMethod.getBody().append(finallyBlock);
                synchronizedMethod.addExceptionHandler(tryPosition, catchPosition, finallyStart, null, false);
                synchronizedMethod.addExceptionHandler(finallyStart, finallyStart.getNext(), finallyStart, null, false);
            } else {
                Type classType = BcelWorld.makeBcelType(synchronizedMethod.getEnclosingClass().getType());
                Type clazzType = Type.getType(Class.class);
                InstructionList parttwo = new InstructionList();
                parttwo.append(InstructionFactory.createDup(1));
                int slotForThis = synchronizedMethod.allocateLocal(classType);
                parttwo.append(InstructionFactory.createStore(clazzType, slotForThis));
                parttwo.append(InstructionFactory.MONITORENTER);
                String fieldname = synchronizedMethod.getEnclosingClass().allocateField("class$");
                FieldGen f = new FieldGen(10, Type.getType(Class.class), fieldname, synchronizedMethod.getEnclosingClass().getConstantPool());
                synchronizedMethod.getEnclosingClass().addField(f, null);
                String name = synchronizedMethod.getEnclosingClass().getName();
                prepend.append(fact.createGetStatic(name, fieldname, Type.getType(Class.class)));
                prepend.append(InstructionFactory.createDup(1));
                prepend.append(InstructionFactory.createBranchInstruction((short)199, parttwo.getStart()));
                prepend.append(InstructionFactory.POP);
                prepend.append(fact.createConstant(name));
                InstructionHandle tryInstruction = prepend.getEnd();
                prepend.append(fact.createInvoke("java.lang.Class", "forName", clazzType, new Type[]{Type.getType(String.class)}, (short)184));
                InstructionHandle catchInstruction = prepend.getEnd();
                prepend.append(InstructionFactory.createDup(1));
                prepend.append(fact.createPutStatic(synchronizedMethod.getEnclosingClass().getType().getName(), fieldname, Type.getType(Class.class)));
                prepend.append(InstructionFactory.createBranchInstruction((short)167, parttwo.getStart()));
                InstructionList catchBlockForLiteralLoadingFail = new InstructionList();
                catchBlockForLiteralLoadingFail.append(fact.createNew((ObjectType)Type.getType(NoClassDefFoundError.class)));
                catchBlockForLiteralLoadingFail.append(InstructionFactory.createDup_1(1));
                catchBlockForLiteralLoadingFail.append(InstructionFactory.SWAP);
                catchBlockForLiteralLoadingFail.append(fact.createInvoke("java.lang.Throwable", "getMessage", Type.getType(String.class), new Type[0], (short)182));
                catchBlockForLiteralLoadingFail.append(fact.createInvoke("java.lang.NoClassDefFoundError", "<init>", Type.VOID, new Type[]{Type.getType(String.class)}, (short)183));
                catchBlockForLiteralLoadingFail.append(InstructionFactory.ATHROW);
                InstructionHandle catchBlockStart = catchBlockForLiteralLoadingFail.getStart();
                prepend.append(catchBlockForLiteralLoadingFail);
                prepend.append(parttwo);
                InstructionList finallyBlock = new InstructionList();
                finallyBlock.append(InstructionFactory.createLoad(Type.getType(Class.class), slotForThis));
                finallyBlock.append(InstructionConstants.MONITOREXIT);
                finallyBlock.append(InstructionConstants.ATHROW);
                ArrayList<InstructionHandle> rets = new ArrayList<InstructionHandle>();
                for (InstructionHandle walker = body.getStart(); walker != null; walker = walker.getNext()) {
                    if (!walker.getInstruction().isReturnInstruction()) continue;
                    rets.add(walker);
                }
                if (rets.size() > 0) {
                    for (InstructionHandle ret : rets) {
                        InstructionList monitorExitBlock = new InstructionList();
                        monitorExitBlock.append(InstructionFactory.createLoad(classType, slotForThis));
                        monitorExitBlock.append(InstructionConstants.MONITOREXIT);
                        InstructionHandle monitorExitBlockStart = body.insert(ret, monitorExitBlock);
                        for (InstructionTargeter targeter : ret.getTargetersCopy()) {
                            if (targeter instanceof LocalVariableTag || targeter instanceof LineNumberTag) continue;
                            if (targeter instanceof InstructionBranch) {
                                targeter.updateTarget(ret, monitorExitBlockStart);
                                continue;
                            }
                            throw new BCException("Unexpected targeter encountered during transform: " + targeter);
                        }
                    }
                }
                InstructionHandle finallyStart = finallyBlock.getStart();
                InstructionHandle tryPosition = body.getStart();
                InstructionHandle catchPosition = body.getEnd();
                body.insert(body.getStart(), prepend);
                synchronizedMethod.getBody().append(finallyBlock);
                synchronizedMethod.addExceptionHandler(tryPosition, catchPosition, finallyStart, null, false);
                synchronizedMethod.addExceptionHandler(tryInstruction, catchInstruction, catchBlockStart, (ObjectType)Type.getType(ClassNotFoundException.class), true);
                synchronizedMethod.addExceptionHandler(finallyStart, finallyStart.getNext(), finallyStart, null, false);
            }
        } else {
            Type classType = BcelWorld.makeBcelType(synchronizedMethod.getEnclosingClass().getType());
            prepend.append(InstructionFactory.createLoad(classType, 0));
            prepend.append(InstructionFactory.createDup(1));
            int slotForThis = synchronizedMethod.allocateLocal(classType);
            prepend.append(InstructionFactory.createStore(classType, slotForThis));
            prepend.append(InstructionFactory.MONITORENTER);
            InstructionList finallyBlock = new InstructionList();
            finallyBlock.append(InstructionFactory.createLoad(classType, slotForThis));
            finallyBlock.append(InstructionConstants.MONITOREXIT);
            finallyBlock.append(InstructionConstants.ATHROW);
            ArrayList<InstructionHandle> rets = new ArrayList<InstructionHandle>();
            for (InstructionHandle walker = body.getStart(); walker != null; walker = walker.getNext()) {
                if (!walker.getInstruction().isReturnInstruction()) continue;
                rets.add(walker);
            }
            if (!rets.isEmpty()) {
                for (InstructionHandle element : rets) {
                    InstructionList monitorExitBlock = new InstructionList();
                    monitorExitBlock.append(InstructionFactory.createLoad(classType, slotForThis));
                    monitorExitBlock.append(InstructionConstants.MONITOREXIT);
                    InstructionHandle monitorExitBlockStart = body.insert(element, monitorExitBlock);
                    for (InstructionTargeter targeter : element.getTargetersCopy()) {
                        if (targeter instanceof LocalVariableTag || targeter instanceof LineNumberTag) continue;
                        if (targeter instanceof InstructionBranch) {
                            targeter.updateTarget(element, monitorExitBlockStart);
                            continue;
                        }
                        throw new BCException("Unexpected targeter encountered during transform: " + targeter);
                    }
                }
            }
            InstructionHandle finallyStart = finallyBlock.getStart();
            InstructionHandle tryPosition = body.getStart();
            InstructionHandle catchPosition = body.getEnd();
            body.insert(body.getStart(), prepend);
            synchronizedMethod.getBody().append(finallyBlock);
            synchronizedMethod.addExceptionHandler(tryPosition, catchPosition, finallyStart, null, false);
            synchronizedMethod.addExceptionHandler(finallyStart, finallyStart.getNext(), finallyStart, null, false);
        }
        if (trace.isTraceEnabled()) {
            trace.exit("transformSynchronizedMethod");
        }
    }

    static InstructionList genInlineInstructions(LazyMethodGen donor, LazyMethodGen recipient, IntMap frameEnv, InstructionFactory fact, boolean keepReturns) {
        InstructionHandle dest;
        ConstantPool recipientCpg;
        InstructionList footer = new InstructionList();
        InstructionHandle end = footer.append(InstructionConstants.NOP);
        InstructionList ret = new InstructionList();
        InstructionList sourceList = donor.getBody();
        HashMap<InstructionHandle, InstructionHandle> srcToDest = new HashMap<InstructionHandle, InstructionHandle>();
        ConstantPool donorCpg = donor.getEnclosingClass().getConstantPool();
        boolean isAcrossClass = donorCpg != (recipientCpg = recipient.getEnclosingClass().getConstantPool());
        Object bootstrapMethods = null;
        for (InstructionHandle src = sourceList.getStart(); src != null; src = src.getNext()) {
            Instruction fresh = Utility.copyInstruction(src.getInstruction());
            if (fresh.isConstantPoolInstruction() && isAcrossClass) {
                InstructionCP cpi = (InstructionCP)fresh;
                cpi.setIndex(recipientCpg.addConstant(donorCpg.getConstant(cpi.getIndex()), donorCpg));
            }
            if (src.getInstruction() == Range.RANGEINSTRUCTION) {
                dest = ret.append(Range.RANGEINSTRUCTION);
            } else if (fresh.isReturnInstruction()) {
                dest = keepReturns ? ret.append(fresh) : ret.append(InstructionFactory.createBranchInstruction((short)167, end));
            } else if (fresh instanceof InstructionBranch) {
                dest = ret.append((InstructionBranch)fresh);
            } else if (fresh.isLocalVariableInstruction() || fresh instanceof RET) {
                int freshIndex;
                int oldIndex = fresh.getIndex();
                if (!frameEnv.hasKey(oldIndex)) {
                    freshIndex = recipient.allocateLocal(2);
                    frameEnv.put(oldIndex, freshIndex);
                } else {
                    freshIndex = frameEnv.get(oldIndex);
                }
                if (fresh instanceof RET) {
                    fresh.setIndex(freshIndex);
                } else {
                    fresh = ((InstructionLV)fresh).setIndexAndCopyIfNecessary(freshIndex);
                }
                dest = ret.append(fresh);
            } else {
                dest = ret.append(fresh);
            }
            srcToDest.put(src, dest);
        }
        HashMap<Tag, Tag> tagMap = new HashMap<Tag, Tag>();
        HashMap<BcelShadow, BcelShadow> shadowMap = new HashMap<BcelShadow, BcelShadow>();
        dest = ret.getStart();
        InstructionHandle src = sourceList.getStart();
        while (dest != null) {
            InstructionBranch branch;
            InstructionHandle oldTarget;
            InstructionHandle newTarget;
            Instruction inst = dest.getInstruction();
            if (inst instanceof InstructionBranch && (newTarget = (InstructionHandle)srcToDest.get(oldTarget = (branch = (InstructionBranch)inst).getTarget())) != null) {
                branch.setTarget(newTarget);
                if (branch instanceof InstructionSelect) {
                    InstructionSelect select = (InstructionSelect)branch;
                    InstructionHandle[] oldTargets = select.getTargets();
                    for (int k = oldTargets.length - 1; k >= 0; --k) {
                        select.setTarget(k, (InstructionHandle)srcToDest.get(oldTargets[k]));
                    }
                }
            }
            for (InstructionTargeter old : src.getTargeters()) {
                ShadowRange oldRange;
                if (old instanceof Tag) {
                    Tag oldTag = (Tag)old;
                    Tag fresh = (Tag)tagMap.get(oldTag);
                    if (fresh == null) {
                        fresh = oldTag.copy();
                        if (old instanceof LocalVariableTag) {
                            LocalVariableTag lvTag = (LocalVariableTag)old;
                            LocalVariableTag lvTagFresh = (LocalVariableTag)fresh;
                            if (lvTag.getSlot() == 0) {
                                fresh = new LocalVariableTag(lvTag.getRealType().getSignature(), "ajc$aspectInstance", frameEnv.get(lvTag.getSlot()), 0);
                            } else {
                                lvTagFresh.updateSlot(frameEnv.get(lvTag.getSlot()));
                            }
                        }
                        tagMap.put(oldTag, fresh);
                    }
                    dest.addTargeter(fresh);
                    continue;
                }
                if (old instanceof ExceptionRange) {
                    ExceptionRange er = (ExceptionRange)old;
                    if (er.getStart() != src) continue;
                    ExceptionRange freshEr = new ExceptionRange(recipient.getBody(), er.getCatchType(), er.getPriority());
                    freshEr.associateWithTargets(dest, (InstructionHandle)srcToDest.get(er.getEnd()), (InstructionHandle)srcToDest.get(er.getHandler()));
                    continue;
                }
                if (!(old instanceof ShadowRange) || (oldRange = (ShadowRange)old).getStart() != src) continue;
                BcelShadow oldShadow = oldRange.getShadow();
                BcelShadow freshEnclosing = oldShadow.getEnclosingShadow() == null ? null : (BcelShadow)shadowMap.get(oldShadow.getEnclosingShadow());
                BcelShadow freshShadow = oldShadow.copyInto(recipient, freshEnclosing);
                ShadowRange freshRange = new ShadowRange(recipient.getBody());
                freshRange.associateWithShadow(freshShadow);
                freshRange.associateWithTargets(dest, (InstructionHandle)srcToDest.get(oldRange.getEnd()));
                shadowMap.put(oldShadow, freshShadow);
            }
            dest = dest.getNext();
            src = src.getNext();
        }
        if (!keepReturns) {
            ret.append(footer);
        }
        return ret;
    }

    private static InstructionList genArgumentStores(LazyMethodGen donor, LazyMethodGen recipient, IntMap frameEnv, InstructionFactory fact) {
        InstructionList ret = new InstructionList();
        int donorFramePos = 0;
        if (!donor.isStatic()) {
            int targetSlot = recipient.allocateLocal(Type.OBJECT);
            ret.insert(InstructionFactory.createStore(Type.OBJECT, targetSlot));
            frameEnv.put(donorFramePos, targetSlot);
            ++donorFramePos;
        }
        for (Type argType : donor.getArgumentTypes()) {
            int argSlot = recipient.allocateLocal(argType);
            ret.insert(InstructionFactory.createStore(argType, argSlot));
            frameEnv.put(donorFramePos, argSlot);
            donorFramePos += argType.getSize();
        }
        return ret;
    }

    private LazyMethodGen getCalledMethod(InstructionHandle ih) {
        InvokeInstruction inst = (InvokeInstruction)ih.getInstruction();
        String methodName = inst.getName(this.cpg);
        String signature = inst.getSignature(this.cpg);
        return this.clazz.getLazyMethodGen(methodName, signature);
    }

    private void weaveInAddedMethods() {
        Collections.sort(this.addedLazyMethodGens, new Comparator<LazyMethodGen>(){

            @Override
            public int compare(LazyMethodGen aa, LazyMethodGen bb) {
                int i = aa.getName().compareTo(bb.getName());
                if (i != 0) {
                    return i;
                }
                return aa.getSignature().compareTo(bb.getSignature());
            }
        });
        for (LazyMethodGen addedMember : this.addedLazyMethodGens) {
            this.clazz.addMethodGen(addedMember);
        }
    }

    private InstructionHandle findSuperOrThisCall(LazyMethodGen mg) {
        int depth = 1;
        InstructionHandle start = mg.getBody().getStart();
        while (start != null) {
            Instruction inst = start.getInstruction();
            if (inst.opcode == 183 && ((InvokeInstruction)inst).getName(this.cpg).equals("<init>")) {
                if (--depth == 0) {
                    return start;
                }
            } else if (inst.opcode == 187) {
                ++depth;
            }
            start = start.getNext();
        }
        return null;
    }

    private boolean match(LazyMethodGen mg) {
        BcelShadow enclosingShadow;
        boolean startsAngly;
        ArrayList<BcelShadow> shadowAccumulator = new ArrayList<BcelShadow>();
        boolean isOverweaving = this.world.isOverWeaving();
        boolean bl = startsAngly = mg.getName().charAt(0) == '<';
        if (startsAngly && mg.getName().equals("<init>")) {
            return this.matchInit(mg, shadowAccumulator);
        }
        if (!this.shouldWeaveBody(mg)) {
            return false;
        }
        if (startsAngly && mg.getName().equals("<clinit>")) {
            enclosingShadow = BcelShadow.makeStaticInitialization(this.world, mg);
        } else if (mg.isAdviceMethod()) {
            enclosingShadow = BcelShadow.makeAdviceExecution(this.world, mg);
        } else {
            AjAttribute.EffectiveSignatureAttribute effective = mg.getEffectiveSignature();
            if (effective == null) {
                if (isOverweaving && mg.getName().startsWith("ajc$")) {
                    return false;
                }
                if (mg.getName().startsWith(SWITCH_TABLE_SYNTHETIC_METHOD_PREFIX) && Objects.equals(mg.getReturnType().getSignature(), "[I")) {
                    return false;
                }
                enclosingShadow = BcelShadow.makeMethodExecution(this.world, mg, !this.canMatchBodyShadows);
            } else if (effective.isWeaveBody()) {
                ResolvedMember rm = effective.getEffectiveSignature();
                this.fixParameterNamesForResolvedMember(rm, mg.getMemberView());
                this.fixAnnotationsForResolvedMember(rm, mg.getMemberView());
                enclosingShadow = BcelShadow.makeShadowForMethod(this.world, mg, effective.getShadowKind(), rm);
            } else {
                return false;
            }
        }
        if (this.canMatchBodyShadows) {
            for (InstructionHandle h = mg.getBody().getStart(); h != null; h = h.getNext()) {
                this.match(mg, h, enclosingShadow, shadowAccumulator);
            }
        }
        if (this.canMatch(enclosingShadow.getKind()) && (mg.getName().charAt(0) != 'a' || !mg.getName().startsWith("ajc$interFieldInit")) && this.match(enclosingShadow, shadowAccumulator)) {
            enclosingShadow.init();
        }
        mg.matchedShadows = shadowAccumulator;
        return !shadowAccumulator.isEmpty();
    }

    private boolean matchInit(LazyMethodGen mg, List<BcelShadow> shadowAccumulator) {
        InstructionHandle superOrThisCall = this.findSuperOrThisCall(mg);
        if (superOrThisCall == null) {
            return false;
        }
        BcelShadow enclosingShadow = BcelShadow.makeConstructorExecution(this.world, mg, superOrThisCall);
        if (mg.getEffectiveSignature() != null) {
            enclosingShadow.setMatchingSignature(mg.getEffectiveSignature().getEffectiveSignature());
        }
        boolean beforeSuperOrThisCall = true;
        if (this.shouldWeaveBody(mg)) {
            if (this.canMatchBodyShadows) {
                for (InstructionHandle h = mg.getBody().getStart(); h != null; h = h.getNext()) {
                    if (h == superOrThisCall) {
                        beforeSuperOrThisCall = false;
                        continue;
                    }
                    this.match(mg, h, beforeSuperOrThisCall ? null : enclosingShadow, shadowAccumulator);
                }
            }
            if (this.canMatch(Shadow.ConstructorExecution)) {
                this.match(enclosingShadow, shadowAccumulator);
            }
        }
        if (!this.isThisCall(superOrThisCall)) {
            InstructionHandle curr = enclosingShadow.getRange().getStart();
            for (IfaceInitList l : this.addedSuperInitializersAsList) {
                Member ifaceInitSig = AjcMemberMaker.interfaceConstructor(l.onType);
                BcelShadow initShadow = BcelShadow.makeIfaceInitialization(this.world, mg, ifaceInitSig);
                InstructionList inits = this.genInitInstructions(l.list, false);
                if (!this.match(initShadow, shadowAccumulator) && inits.isEmpty()) continue;
                initShadow.initIfaceInitializer(curr);
                initShadow.getRange().insert(inits, Range.OutsideBefore);
            }
            InstructionList inits = this.genInitInstructions(this.addedThisInitializers, false);
            enclosingShadow.getRange().insert(inits, Range.OutsideBefore);
        }
        boolean addedInitialization = this.match(BcelShadow.makeUnfinishedInitialization(this.world, mg), this.initializationShadows);
        mg.matchedShadows = shadowAccumulator;
        return (addedInitialization |= this.match(BcelShadow.makeUnfinishedPreinitialization(this.world, mg), this.initializationShadows)) || !shadowAccumulator.isEmpty();
    }

    private boolean shouldWeaveBody(LazyMethodGen mg) {
        if (mg.isBridgeMethod()) {
            return false;
        }
        if (mg.isAjSynthetic()) {
            return mg.getName().equals("<clinit>");
        }
        AjAttribute.EffectiveSignatureAttribute a = mg.getEffectiveSignature();
        if (a != null) {
            return a.isWeaveBody();
        }
        return true;
    }

    private InstructionList genInitInstructions(List<ConcreteTypeMunger> list, boolean isStatic) {
        if ((list = PartialOrder.sort(list)) == null) {
            throw new BCException("circularity in inter-types");
        }
        InstructionList ret = new InstructionList();
        for (ConcreteTypeMunger cmunger : list) {
            NewFieldTypeMunger munger = (NewFieldTypeMunger)cmunger.getMunger();
            ResolvedMember initMethod = munger.getInitMethod(cmunger.getAspectType());
            if (!isStatic) {
                ret.append(InstructionConstants.ALOAD_0);
            }
            ret.append(Utility.createInvoke(this.fact, this.world, (Member)initMethod));
        }
        return ret;
    }

    private void match(LazyMethodGen mg, InstructionHandle ih, BcelShadow enclosingShadow, List<BcelShadow> shadowAccumulator) {
        Instruction i = ih.getInstruction();
        if (this.canMatch(Shadow.ExceptionHandler) && !Range.isRangeHandle(ih)) {
            Set<InstructionTargeter> targeters = ih.getTargetersCopy();
            for (InstructionTargeter t : targeters) {
                ExceptionRange er;
                if (!(t instanceof ExceptionRange) || (er = (ExceptionRange)t).getCatchType() == null) continue;
                if (this.isInitFailureHandler(ih)) {
                    return;
                }
                if (!ih.getInstruction().isStoreInstruction() && ih.getInstruction().getOpcode() != 0) {
                    mg.getBody().insert(ih, InstructionConstants.NOP);
                    InstructionHandle newNOP = ih.getPrev();
                    er.updateTarget(ih, newNOP, mg.getBody());
                    for (InstructionTargeter t2 : targeters) {
                        newNOP.addTargeter(t2);
                    }
                    ih.removeAllTargeters();
                    this.match(BcelShadow.makeExceptionHandler(this.world, er, mg, newNOP, enclosingShadow), shadowAccumulator);
                    continue;
                }
                this.match(BcelShadow.makeExceptionHandler(this.world, er, mg, ih, enclosingShadow), shadowAccumulator);
            }
        }
        if (i instanceof FieldInstruction && (this.canMatch(Shadow.FieldGet) || this.canMatch(Shadow.FieldSet))) {
            FieldInstruction fi = (FieldInstruction)i;
            if (fi.opcode == 181 || fi.opcode == 179) {
                InstructionHandle prevHandle = ih.getPrev();
                Instruction prevI = prevHandle.getInstruction();
                if (Utility.isConstantPushInstruction(prevI)) {
                    Member field = BcelWorld.makeFieldJoinPointSignature(this.clazz, (FieldInstruction)i);
                    ResolvedMember resolvedField = field.resolve(this.world);
                    if (resolvedField != null && !Modifier.isFinal(resolvedField.getModifiers()) && this.canMatch(Shadow.FieldSet)) {
                        this.matchSetInstruction(mg, ih, enclosingShadow, shadowAccumulator);
                    }
                } else if (this.canMatch(Shadow.FieldSet)) {
                    this.matchSetInstruction(mg, ih, enclosingShadow, shadowAccumulator);
                }
            } else if (this.canMatch(Shadow.FieldGet)) {
                this.matchGetInstruction(mg, ih, enclosingShadow, shadowAccumulator);
            }
        } else if (i instanceof InvokeInstruction) {
            InvokeInstruction ii = (InvokeInstruction)i;
            if (ii.getMethodName(this.clazz.getConstantPool()).equals("<init>")) {
                if (this.canMatch(Shadow.ConstructorCall)) {
                    this.match(BcelShadow.makeConstructorCall(this.world, mg, ih, enclosingShadow), shadowAccumulator);
                }
            } else if (ii.opcode == 183) {
                String onTypeName = ii.getClassName(this.cpg);
                if (onTypeName.equals(mg.getEnclosingClass().getName())) {
                    this.matchInvokeInstruction(mg, ih, ii, enclosingShadow, shadowAccumulator);
                }
            } else if (ii.getOpcode() != 186) {
                this.matchInvokeInstruction(mg, ih, ii, enclosingShadow, shadowAccumulator);
            }
        } else if (this.world.isJoinpointArrayConstructionEnabled() && i.isArrayCreationInstruction()) {
            if (this.canMatch(Shadow.ConstructorCall)) {
                BcelShadow ctorCallShadow;
                if (i.opcode == 189) {
                    ctorCallShadow = BcelShadow.makeArrayConstructorCall(this.world, mg, ih, enclosingShadow);
                    this.match(ctorCallShadow, shadowAccumulator);
                } else if (i.opcode == 188) {
                    ctorCallShadow = BcelShadow.makeArrayConstructorCall(this.world, mg, ih, enclosingShadow);
                    this.match(ctorCallShadow, shadowAccumulator);
                } else if (i instanceof MULTIANEWARRAY) {
                    ctorCallShadow = BcelShadow.makeArrayConstructorCall(this.world, mg, ih, enclosingShadow);
                    this.match(ctorCallShadow, shadowAccumulator);
                }
            }
        } else if (this.world.isJoinpointSynchronizationEnabled() && (i.getOpcode() == 194 || i.getOpcode() == 195)) {
            if (i.getOpcode() == 194) {
                BcelShadow monitorEntryShadow = BcelShadow.makeMonitorEnter(this.world, mg, ih, enclosingShadow);
                this.match(monitorEntryShadow, shadowAccumulator);
            } else {
                BcelShadow monitorExitShadow = BcelShadow.makeMonitorExit(this.world, mg, ih, enclosingShadow);
                this.match(monitorExitShadow, shadowAccumulator);
            }
        }
    }

    private boolean isInitFailureHandler(InstructionHandle ih) {
        String name;
        InstructionHandle twoInstructionsAway = ih.getNext().getNext();
        return twoInstructionsAway.getInstruction().opcode == 179 && (name = ((FieldInstruction)twoInstructionsAway.getInstruction()).getFieldName(this.cpg)).equals("ajc$initFailureCause");
    }

    private void matchSetInstruction(LazyMethodGen mg, InstructionHandle ih, BcelShadow enclosingShadow, List<BcelShadow> shadowAccumulator) {
        FieldInstruction fi = (FieldInstruction)ih.getInstruction();
        Member field = BcelWorld.makeFieldJoinPointSignature(this.clazz, fi);
        if (field.getName().startsWith("ajc$")) {
            return;
        }
        ResolvedMember resolvedField = field.resolve(this.world);
        if (resolvedField == null) {
            return;
        }
        if (Modifier.isFinal(resolvedField.getModifiers()) && Utility.isConstantPushInstruction(ih.getPrev().getInstruction())) {
            return;
        }
        if (resolvedField.isSynthetic()) {
            return;
        }
        BcelShadow bs = BcelShadow.makeFieldSet(this.world, resolvedField, mg, ih, enclosingShadow);
        String cname = fi.getClassName(this.cpg);
        if (!resolvedField.getDeclaringType().getName().equals(cname)) {
            bs.setActualTargetType(cname);
        }
        this.match(bs, shadowAccumulator);
    }

    private void matchGetInstruction(LazyMethodGen mg, InstructionHandle ih, BcelShadow enclosingShadow, List<BcelShadow> shadowAccumulator) {
        FieldInstruction fi = (FieldInstruction)ih.getInstruction();
        Member field = BcelWorld.makeFieldJoinPointSignature(this.clazz, fi);
        if (field.getName().startsWith("ajc$")) {
            return;
        }
        ResolvedMember resolvedField = field.resolve(this.world);
        if (resolvedField == null) {
            return;
        }
        if (resolvedField.isSynthetic()) {
            return;
        }
        BcelShadow bs = BcelShadow.makeFieldGet(this.world, resolvedField, mg, ih, enclosingShadow);
        String cname = fi.getClassName(this.cpg);
        if (!resolvedField.getDeclaringType().getName().equals(cname)) {
            bs.setActualTargetType(cname);
        }
        this.match(bs, shadowAccumulator);
    }

    private ResolvedMember findResolvedMemberNamed(ResolvedType type, String methodName) {
        ResolvedMember[] allMethods = type.getDeclaredMethods();
        for (int i = 0; i < allMethods.length; ++i) {
            ResolvedMember member = allMethods[i];
            if (!member.getName().equals(methodName)) continue;
            return member;
        }
        return null;
    }

    private ResolvedMember findResolvedMemberNamed(ResolvedType type, String methodName, UnresolvedType[] params) {
        ResolvedMember[] allMethods = type.getDeclaredMethods();
        ArrayList<ResolvedMember> candidates = new ArrayList<ResolvedMember>();
        for (int i = 0; i < allMethods.length; ++i) {
            ResolvedMember candidate = allMethods[i];
            if (!candidate.getName().equals(methodName) || candidate.getArity() != params.length) continue;
            candidates.add(candidate);
        }
        if (candidates.size() == 0) {
            return null;
        }
        if (candidates.size() == 1) {
            return (ResolvedMember)candidates.get(0);
        }
        for (ResolvedMember candidate : candidates) {
            boolean allOK = true;
            UnresolvedType[] candidateParams = candidate.getParameterTypes();
            for (int p = 0; p < candidateParams.length; ++p) {
                if (candidateParams[p].getErasureSignature().equals(params[p].getErasureSignature())) continue;
                allOK = false;
                break;
            }
            if (!allOK) continue;
            return candidate;
        }
        return null;
    }

    private void fixParameterNamesForResolvedMember(ResolvedMember rm, ResolvedMember declaredSig) {
        UnresolvedType memberHostType = declaredSig.getDeclaringType();
        String methodName = declaredSig.getName();
        String[] pnames = null;
        if (rm.getKind() == Member.METHOD && !rm.isAbstract()) {
            if (methodName.startsWith("ajc$inlineAccessMethod") || methodName.startsWith("ajc$superDispatch")) {
                ResolvedMember resolvedDooberry = this.world.resolve(declaredSig);
                pnames = resolvedDooberry.getParameterNames();
            } else {
                ResolvedMember realthing = AjcMemberMaker.interMethodDispatcher(rm.resolve(this.world), memberHostType).resolve(this.world);
                ResolvedMember theRealMember = this.findResolvedMemberNamed(memberHostType.resolve(this.world), realthing.getName());
                if (theRealMember != null && (pnames = theRealMember.getParameterNames()).length > 0 && pnames[0].equals("ajc$this_")) {
                    String[] pnames2 = new String[pnames.length - 1];
                    System.arraycopy(pnames, 1, pnames2, 0, pnames2.length);
                    pnames = pnames2;
                }
            }
        }
        rm.setParameterNames(pnames);
    }

    private void fixAnnotationsForResolvedMember(ResolvedMember rm, ResolvedMember declaredSig) {
        try {
            AnnotationAJ[] annotations;
            ResolvedType[] annotationTypes;
            UnresolvedType memberHostType = declaredSig.getDeclaringType();
            boolean containsKey = this.mapToAnnotationHolder.containsKey(rm);
            ResolvedMember realAnnotationHolder = this.mapToAnnotationHolder.get(rm);
            String methodName = declaredSig.getName();
            if (!containsKey) {
                ResolvedMember realThing;
                ResolvedMember realthing;
                if (rm.getKind() == Member.FIELD) {
                    if (methodName.startsWith("ajc$inlineAccessField")) {
                        realAnnotationHolder = this.world.resolve(rm);
                    } else {
                        realthing = AjcMemberMaker.interFieldInitializer(rm, memberHostType);
                        realAnnotationHolder = this.world.resolve(realthing);
                    }
                } else if (rm.getKind() == Member.METHOD && !rm.isAbstract()) {
                    if (methodName.startsWith("ajc$inlineAccessMethod") || methodName.startsWith("ajc$superDispatch")) {
                        realAnnotationHolder = this.world.resolve(declaredSig);
                    } else {
                        realthing = AjcMemberMaker.interMethodDispatcher(rm.resolve(this.world), memberHostType).resolve(this.world);
                        realAnnotationHolder = this.findResolvedMemberNamed(memberHostType.resolve(this.world), realthing.getName(), realthing.getParameterTypes());
                        if (realAnnotationHolder == null) {
                            throw new UnsupportedOperationException("Known limitation in M4 - can't find ITD members when type variable is used as an argument and has upper bound specified");
                        }
                    }
                } else if (rm.getKind() == Member.CONSTRUCTOR && (realAnnotationHolder = this.world.resolve(realThing = AjcMemberMaker.postIntroducedConstructor(memberHostType.resolve(this.world), rm.getDeclaringType(), rm.getParameterTypes()))) == null) {
                    throw new UnsupportedOperationException("Known limitation in M4 - can't find ITD members when type variable is used as an argument and has upper bound specified");
                }
                this.mapToAnnotationHolder.put(rm, realAnnotationHolder);
            }
            if (realAnnotationHolder != null) {
                annotationTypes = realAnnotationHolder.getAnnotationTypes();
                annotations = realAnnotationHolder.getAnnotations();
                if (annotationTypes == null) {
                    annotationTypes = ResolvedType.EMPTY_ARRAY;
                }
                if (annotations == null) {
                    annotations = AnnotationAJ.EMPTY_ARRAY;
                }
            } else {
                annotations = AnnotationAJ.EMPTY_ARRAY;
                annotationTypes = ResolvedType.EMPTY_ARRAY;
            }
            rm.setAnnotations(annotations);
            rm.setAnnotationTypes(annotationTypes);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw new BCException("Unexpectedly went bang when searching for annotations on " + rm, t);
        }
    }

    private void matchInvokeInstruction(LazyMethodGen mg, InstructionHandle ih, InvokeInstruction invoke, BcelShadow enclosingShadow, List<BcelShadow> shadowAccumulator) {
        String methodName = invoke.getName(this.cpg);
        if (methodName.startsWith("ajc$")) {
            Member jpSig = this.world.makeJoinPointSignatureForMethodInvocation(this.clazz, invoke);
            ResolvedMember declaredSig = jpSig.resolve(this.world);
            if (declaredSig == null) {
                return;
            }
            if (declaredSig.getKind() == Member.FIELD) {
                Shadow.Kind kind = jpSig.getReturnType().equals(UnresolvedType.VOID) ? Shadow.FieldSet : Shadow.FieldGet;
                if (this.canMatch(Shadow.FieldGet) || this.canMatch(Shadow.FieldSet)) {
                    this.match(BcelShadow.makeShadowForMethodCall(this.world, mg, ih, enclosingShadow, kind, declaredSig), shadowAccumulator);
                }
            } else if (!declaredSig.getName().startsWith("ajc$")) {
                if (this.canMatch(Shadow.MethodCall)) {
                    this.match(BcelShadow.makeShadowForMethodCall(this.world, mg, ih, enclosingShadow, Shadow.MethodCall, declaredSig), shadowAccumulator);
                }
            } else {
                AjAttribute.EffectiveSignatureAttribute effectiveSig = declaredSig.getEffectiveSignature();
                if (effectiveSig == null) {
                    return;
                }
                if (effectiveSig.isWeaveBody()) {
                    return;
                }
                ResolvedMember rm = effectiveSig.getEffectiveSignature();
                this.fixParameterNamesForResolvedMember(rm, declaredSig);
                this.fixAnnotationsForResolvedMember(rm, declaredSig);
                if (this.canMatch(effectiveSig.getShadowKind())) {
                    this.match(BcelShadow.makeShadowForMethodCall(this.world, mg, ih, enclosingShadow, effectiveSig.getShadowKind(), rm), shadowAccumulator);
                }
            }
        } else if (this.canMatch(Shadow.MethodCall)) {
            boolean proceed = true;
            if (this.world.isOverWeaving()) {
                String s = invoke.getClassName(mg.getConstantPool());
                if (s.length() > 4 && s.charAt(4) == 'a' && (s.equals("org.aspectj.runtime.internal.CFlowCounter") || s.equals("org.aspectj.runtime.internal.CFlowStack") || s.equals("org.aspectj.runtime.reflect.Factory"))) {
                    proceed = false;
                } else if (methodName.equals("aspectOf")) {
                    proceed = false;
                }
            }
            if (methodName.startsWith(SWITCH_TABLE_SYNTHETIC_METHOD_PREFIX)) {
                proceed = false;
            }
            if (proceed) {
                this.match(BcelShadow.makeMethodCall(this.world, mg, ih, enclosingShadow), shadowAccumulator);
            }
        }
    }

    private boolean match(BcelShadow shadow, List<BcelShadow> shadowAccumulator) {
        if (captureLowLevelContext) {
            ContextToken shadowMatchToken = CompilationAndWeavingContext.enteringPhase(28, shadow);
            boolean isMatched = false;
            Shadow.Kind shadowKind = shadow.getKind();
            List<ShadowMunger> candidateMungers = this.indexedShadowMungers[shadowKind.getKey()];
            if (candidateMungers != null) {
                for (ShadowMunger munger : candidateMungers) {
                    ContextToken mungerMatchToken = CompilationAndWeavingContext.enteringPhase(30, munger.getPointcut());
                    if (munger.match(shadow, this.world)) {
                        shadow.addMunger(munger);
                        isMatched = true;
                        if (shadow.getKind() == Shadow.StaticInitialization) {
                            this.clazz.warnOnAddedStaticInitializer(shadow, munger.getSourceLocation());
                        }
                    }
                    CompilationAndWeavingContext.leavingPhase(mungerMatchToken);
                }
                if (isMatched) {
                    shadowAccumulator.add(shadow);
                }
            }
            CompilationAndWeavingContext.leavingPhase(shadowMatchToken);
            return isMatched;
        }
        boolean isMatched = false;
        Shadow.Kind shadowKind = shadow.getKind();
        List<ShadowMunger> candidateMungers = this.indexedShadowMungers[shadowKind.getKey()];
        if (candidateMungers != null) {
            for (ShadowMunger munger : candidateMungers) {
                if (!munger.match(shadow, this.world)) continue;
                shadow.addMunger(munger);
                isMatched = true;
                if (shadow.getKind() != Shadow.StaticInitialization) continue;
                this.clazz.warnOnAddedStaticInitializer(shadow, munger.getSourceLocation());
            }
            if (isMatched) {
                shadowAccumulator.add(shadow);
            }
        }
        return isMatched;
    }

    private void implement(LazyMethodGen mg) {
        List<BcelShadow> shadows = mg.matchedShadows;
        if (shadows == null) {
            return;
        }
        for (BcelShadow shadow : shadows) {
            ContextToken tok = CompilationAndWeavingContext.enteringPhase(29, shadow);
            shadow.implement();
            CompilationAndWeavingContext.leavingPhase(tok);
        }
        mg.getMaxLocals();
        mg.matchedShadows = null;
    }

    public LazyClassGen getLazyClassGen() {
        return this.clazz;
    }

    public BcelWorld getWorld() {
        return this.world;
    }

    public void setReweavableMode(boolean mode) {
        this.inReweavableMode = mode;
    }

    public boolean getReweavableMode() {
        return this.inReweavableMode;
    }

    public String toString() {
        return "BcelClassWeaver instance for : " + this.clazz;
    }

    private static class IfaceInitList
    implements PartialOrder.PartialComparable {
        final ResolvedType onType;
        List<ConcreteTypeMunger> list = new ArrayList<ConcreteTypeMunger>();

        IfaceInitList(ResolvedType onType) {
            this.onType = onType;
        }

        @Override
        public int compareTo(Object other) {
            IfaceInitList o = (IfaceInitList)other;
            if (this.onType.isAssignableFrom(o.onType)) {
                return 1;
            }
            if (o.onType.isAssignableFrom(this.onType)) {
                return -1;
            }
            return 0;
        }

        @Override
        public int fallbackCompareTo(Object other) {
            return 0;
        }
    }
}

