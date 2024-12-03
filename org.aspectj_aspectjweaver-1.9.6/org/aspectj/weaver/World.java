/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.bridge.context.PinpointingMessageHandler;
import org.aspectj.util.IStructureModel;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.ArrayReferenceType;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.BoundedReferenceType;
import org.aspectj.weaver.Checker;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.CrosscuttingMembersSet;
import org.aspectj.weaver.Dump;
import org.aspectj.weaver.ICrossReferenceHandler;
import org.aspectj.weaver.IHasSourceLocation;
import org.aspectj.weaver.IWeavingSupport;
import org.aspectj.weaver.Lint;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MissingResolvedTypeWithKnownSignature;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.RuntimeVersion;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.TypeFactory;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableDeclaringElement;
import org.aspectj.weaver.TypeVariableReferenceType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.WildcardedUnresolvedType;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.DeclareAnnotation;
import org.aspectj.weaver.patterns.DeclareParents;
import org.aspectj.weaver.patterns.DeclarePrecedence;
import org.aspectj.weaver.patterns.DeclareSoft;
import org.aspectj.weaver.patterns.DeclareTypeErrorOrWarning;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.tools.PointcutDesignatorHandler;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;

public abstract class World
implements Dump.INode {
    private IMessageHandler messageHandler = IMessageHandler.SYSTEM_ERR;
    private ICrossReferenceHandler xrefHandler = null;
    private TypeVariableDeclaringElement typeVariableLookupScope;
    protected TypeMap typeMap = new TypeMap(this);
    private Set<PointcutDesignatorHandler> pointcutDesignators;
    public static boolean createInjarHierarchy = true;
    private final AspectPrecedenceCalculator precedenceCalculator;
    private final CrosscuttingMembersSet crosscuttingMembersSet = new CrosscuttingMembersSet(this);
    private IStructureModel model = null;
    private Lint lint = new Lint(this);
    private boolean XnoInline;
    private boolean XlazyTjp;
    private boolean XhasMember = false;
    private boolean Xpinpoint = false;
    private boolean behaveInJava5Way = false;
    private boolean timing = false;
    private boolean timingPeriodically = true;
    private boolean incrementalCompileCouldFollow = false;
    public static final RuntimeVersion RUNTIME_LEVEL_DEFAULT = RuntimeVersion.V1_5;
    private RuntimeVersion targetAspectjRuntimeLevel = RUNTIME_LEVEL_DEFAULT;
    private boolean optionalJoinpoint_ArrayConstruction = false;
    private boolean optionalJoinpoint_Synchronization = false;
    private boolean addSerialVerUID = false;
    private Properties extraConfiguration = null;
    private boolean checkedAdvancedConfiguration = false;
    private boolean synchronizationPointcutsInUse = false;
    private boolean runMinimalMemory = false;
    private boolean transientTjpFields = false;
    private boolean runMinimalMemorySet = false;
    private boolean shouldPipelineCompilation = true;
    private boolean shouldGenerateStackMaps = false;
    protected boolean bcelRepositoryCaching = "true".equalsIgnoreCase("true");
    private boolean fastMethodPacking = false;
    private int itdVersion = 2;
    private boolean minimalModel = true;
    private boolean useFinal = true;
    private boolean targettingRuntime1_6_10 = false;
    private boolean completeBinaryTypes = false;
    private boolean overWeaving = false;
    private static boolean systemPropertyOverWeaving = false;
    public boolean forDEBUG_structuralChangesCode = false;
    public boolean forDEBUG_bridgingCode = false;
    public boolean optimizedMatching = true;
    public boolean generateNewLvts = true;
    protected long timersPerJoinpoint = 25000L;
    protected long timersPerType = 250L;
    public int infoMessagesEnabled = 0;
    private static Trace trace = TraceFactory.getTraceFactory().getTrace(World.class);
    private boolean errorThreshold;
    private boolean warningThreshold;
    private List<RuntimeException> dumpState_cantFindTypeExceptions = null;
    public final ResolvedType.Primitive BYTE = new ResolvedType.Primitive("B", 1, 0);
    public final ResolvedType.Primitive CHAR = new ResolvedType.Primitive("C", 1, 1);
    public final ResolvedType.Primitive DOUBLE = new ResolvedType.Primitive("D", 2, 2);
    public final ResolvedType.Primitive FLOAT = new ResolvedType.Primitive("F", 1, 3);
    public final ResolvedType.Primitive INT = new ResolvedType.Primitive("I", 1, 4);
    public final ResolvedType.Primitive LONG = new ResolvedType.Primitive("J", 2, 5);
    public final ResolvedType.Primitive SHORT = new ResolvedType.Primitive("S", 1, 6);
    public final ResolvedType.Primitive BOOLEAN = new ResolvedType.Primitive("Z", 1, 7);
    public final ResolvedType.Primitive VOID = new ResolvedType.Primitive("V", 0, 8);
    private Object buildingTypeLock = new Object();
    private BoundedReferenceType wildcard;
    private boolean allLintIgnored = false;
    public static final String xsetAVOID_FINAL = "avoidFinal";
    public static final String xsetWEAVE_JAVA_PACKAGES = "weaveJavaPackages";
    public static final String xsetWEAVE_JAVAX_PACKAGES = "weaveJavaxPackages";
    public static final String xsetCAPTURE_ALL_CONTEXT = "captureAllContext";
    public static final String xsetRUN_MINIMAL_MEMORY = "runMinimalMemory";
    public static final String xsetDEBUG_STRUCTURAL_CHANGES_CODE = "debugStructuralChangesCode";
    public static final String xsetDEBUG_BRIDGING = "debugBridging";
    public static final String xsetTRANSIENT_TJP_FIELDS = "makeTjpFieldsTransient";
    public static final String xsetBCEL_REPOSITORY_CACHING = "bcelRepositoryCaching";
    public static final String xsetPIPELINE_COMPILATION = "pipelineCompilation";
    public static final String xsetGENERATE_STACKMAPS = "generateStackMaps";
    public static final String xsetPIPELINE_COMPILATION_DEFAULT = "true";
    public static final String xsetCOMPLETE_BINARY_TYPES = "completeBinaryTypes";
    public static final String xsetCOMPLETE_BINARY_TYPES_DEFAULT = "false";
    public static final String xsetTYPE_DEMOTION = "typeDemotion";
    public static final String xsetTYPE_DEMOTION_DEBUG = "typeDemotionDebug";
    public static final String xsetTYPE_REFS = "useWeakTypeRefs";
    public static final String xsetBCEL_REPOSITORY_CACHING_DEFAULT = "true";
    public static final String xsetFAST_PACK_METHODS = "fastPackMethods";
    public static final String xsetOVERWEAVING = "overWeaving";
    public static final String xsetOPTIMIZED_MATCHING = "optimizedMatching";
    public static final String xsetTIMERS_PER_JOINPOINT = "timersPerJoinpoint";
    public static final String xsetTIMERS_PER_FASTMATCH_CALL = "timersPerFastMatchCall";
    public static final String xsetITD_VERSION = "itdVersion";
    public static final String xsetITD_VERSION_ORIGINAL = "1";
    public static final String xsetITD_VERSION_2NDGEN = "2";
    public static final String xsetITD_VERSION_DEFAULT = "2";
    public static final String xsetMINIMAL_MODEL = "minimalModel";
    public static final String xsetTARGETING_RUNTIME_1610 = "targetRuntime1_6_10";
    public static final String xsetGENERATE_NEW_LVTS = "generateNewLocalVariableTables";
    private final Map<Class<?>, TypeVariable[]> workInProgress1 = new HashMap();
    private Map<ResolvedType, Set<ResolvedType>> exclusionMap = new HashMap<ResolvedType, Set<ResolvedType>>();
    private TimeCollector timeCollector = null;

    protected World() {
        this.typeMap.put("B", this.BYTE);
        this.typeMap.put("S", this.SHORT);
        this.typeMap.put("I", this.INT);
        this.typeMap.put("J", this.LONG);
        this.typeMap.put("F", this.FLOAT);
        this.typeMap.put("D", this.DOUBLE);
        this.typeMap.put("C", this.CHAR);
        this.typeMap.put("Z", this.BOOLEAN);
        this.typeMap.put("V", this.VOID);
        this.precedenceCalculator = new AspectPrecedenceCalculator(this);
    }

    @Override
    public void accept(Dump.IVisitor visitor) {
        visitor.visitObject("Shadow mungers:");
        visitor.visitList(this.crosscuttingMembersSet.getShadowMungers());
        visitor.visitObject("Type mungers:");
        visitor.visitList(this.crosscuttingMembersSet.getTypeMungers());
        visitor.visitObject("Late Type mungers:");
        visitor.visitList(this.crosscuttingMembersSet.getLateTypeMungers());
        if (this.dumpState_cantFindTypeExceptions != null) {
            visitor.visitObject("Cant find type problems:");
            visitor.visitList(this.dumpState_cantFindTypeExceptions);
            this.dumpState_cantFindTypeExceptions = null;
        }
    }

    public ResolvedType resolve(UnresolvedType ty) {
        return this.resolve(ty, false);
    }

    public ResolvedType resolve(UnresolvedType ty, ISourceLocation isl) {
        ResolvedType ret = this.resolve(ty, true);
        if (ResolvedType.isMissing(ty)) {
            this.getLint().cantFindType.signal(WeaverMessages.format("cantFindType", ty.getName()), isl);
        }
        return ret;
    }

    public ResolvedType[] resolve(UnresolvedType[] types) {
        if (types == null) {
            return ResolvedType.NONE;
        }
        ResolvedType[] ret = new ResolvedType[types.length];
        for (int i = 0; i < types.length; ++i) {
            ret[i] = this.resolve(types[i]);
        }
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ResolvedType resolve(UnresolvedType ty, boolean allowMissing) {
        if (ty instanceof ResolvedType) {
            ResolvedType rty = (ResolvedType)ty;
            if (!(rty = this.resolve(rty)).isTypeVariableReference() || ((TypeVariableReferenceType)rty).isTypeVariableResolved()) {
                return rty;
            }
        }
        if (ty.isTypeVariableReference()) {
            return ty.resolve(this);
        }
        String signature = ty.getSignature();
        ResolvedType ret = this.typeMap.get(signature);
        if (ret != null) {
            ret.world = this;
            return ret;
        }
        if (signature.equals("?") || signature.equals("*")) {
            BoundedReferenceType something = this.getWildcard();
            this.typeMap.put("?", something);
            return something;
        }
        Object something = this.buildingTypeLock;
        synchronized (something) {
            if (ty.isArray()) {
                ResolvedType componentType = this.resolve(ty.getComponentType(), allowMissing);
                ret = new ArrayReferenceType(signature, "[" + componentType.getErasureSignature(), this, componentType);
            } else {
                ret = this.resolveToReferenceType(ty, allowMissing);
                if (!allowMissing && ret.isMissing()) {
                    ret = this.handleRequiredMissingTypeDuringResolution(ty);
                }
                if (this.completeBinaryTypes) {
                    this.completeBinaryType(ret);
                }
            }
        }
        ResolvedType result = this.typeMap.get(signature);
        if (result == null && !ret.isMissing()) {
            ret = this.ensureRawTypeIfNecessary(ret);
            this.typeMap.put(signature, ret);
            return ret;
        }
        if (result == null) {
            return ret;
        }
        return result;
    }

    private BoundedReferenceType getWildcard() {
        if (this.wildcard == null) {
            this.wildcard = new BoundedReferenceType(this);
        }
        return this.wildcard;
    }

    protected void completeBinaryType(ResolvedType ret) {
    }

    public boolean isLocallyDefined(String classname) {
        return false;
    }

    private ResolvedType handleRequiredMissingTypeDuringResolution(UnresolvedType ty) {
        if (this.dumpState_cantFindTypeExceptions == null) {
            this.dumpState_cantFindTypeExceptions = new ArrayList<RuntimeException>();
        }
        if (this.dumpState_cantFindTypeExceptions.size() < 100) {
            this.dumpState_cantFindTypeExceptions.add(new RuntimeException("Can't find type " + ty.getName()));
        }
        return new MissingResolvedTypeWithKnownSignature(ty.getSignature(), this);
    }

    public ResolvedType resolve(ResolvedType ty) {
        if (ty.isTypeVariableReference()) {
            return ty;
        }
        ResolvedType resolved = this.typeMap.get(ty.getSignature());
        if (resolved == null) {
            resolved = this.ensureRawTypeIfNecessary(ty);
            this.typeMap.put(ty.getSignature(), resolved);
            resolved = ty;
        }
        resolved.world = this;
        return resolved;
    }

    private ResolvedType ensureRawTypeIfNecessary(ResolvedType type) {
        if (!this.isInJava5Mode() || type.isRawType()) {
            return type;
        }
        if (type instanceof ReferenceType && ((ReferenceType)type).getDelegate() != null && type.isGenericType()) {
            ReferenceType rawType = new ReferenceType(type.getSignature(), this);
            rawType.typeKind = UnresolvedType.TypeKind.RAW;
            ReferenceTypeDelegate delegate = ((ReferenceType)type).getDelegate();
            rawType.setDelegate(delegate);
            rawType.setGenericType((ReferenceType)type);
            return rawType;
        }
        return type;
    }

    public ResolvedType resolve(String name) {
        ResolvedType ret = this.resolve(UnresolvedType.forName(name));
        return ret;
    }

    public ReferenceType resolveToReferenceType(String name) {
        return (ReferenceType)this.resolve(name);
    }

    public ResolvedType resolve(String name, boolean allowMissing) {
        return this.resolve(UnresolvedType.forName(name), allowMissing);
    }

    private final ResolvedType resolveToReferenceType(UnresolvedType ty, boolean allowMissing) {
        ReferenceTypeDelegate delegate;
        if (ty.isParameterizedType()) {
            ResolvedType rt = this.resolveGenericTypeFor(ty, allowMissing);
            if (rt.isMissing()) {
                return rt;
            }
            ReferenceType genericType = (ReferenceType)rt;
            ReferenceType parameterizedType = TypeFactory.createParameterizedType(genericType, ty.typeParameters, this);
            return parameterizedType;
        }
        if (ty.isGenericType()) {
            ResolvedType rt = this.resolveGenericTypeFor(ty, false);
            if (rt.isMissing()) {
                return rt;
            }
            ReferenceType genericType = (ReferenceType)rt;
            if (rt.isMissing()) {
                return rt;
            }
            return genericType;
        }
        if (ty.isGenericWildcard()) {
            return this.resolveGenericWildcardFor((WildcardedUnresolvedType)ty);
        }
        String erasedSignature = ty.getErasureSignature();
        ReferenceType simpleOrRawType = new ReferenceType(erasedSignature, this);
        if (ty.needsModifiableDelegate()) {
            simpleOrRawType.setNeedsModifiableDelegate(true);
        }
        if ((delegate = this.resolveDelegate(simpleOrRawType)) == null) {
            return new MissingResolvedTypeWithKnownSignature(ty.getSignature(), erasedSignature, this);
        }
        if (delegate.isGeneric() && this.behaveInJava5Way) {
            simpleOrRawType.typeKind = UnresolvedType.TypeKind.RAW;
            if (simpleOrRawType.hasNewInterfaces()) {
                throw new IllegalStateException("Simple type promoted forced to raw, but it had new interfaces/superclass.  Type is " + simpleOrRawType.getName());
            }
            ReferenceType genericType = this.makeGenericTypeFrom(delegate, simpleOrRawType);
            simpleOrRawType.setDelegate(delegate);
            genericType.setDelegate(delegate);
            simpleOrRawType.setGenericType(genericType);
            return simpleOrRawType;
        }
        simpleOrRawType.setDelegate(delegate);
        return simpleOrRawType;
    }

    public ResolvedType resolveGenericTypeFor(UnresolvedType anUnresolvedType, boolean allowMissing) {
        String rawSignature = anUnresolvedType.getRawType().getSignature();
        ResolvedType rawType = this.typeMap.get(rawSignature);
        if (rawType == null) {
            rawType = this.resolve(UnresolvedType.forSignature(rawSignature), allowMissing);
            this.typeMap.put(rawSignature, rawType);
        }
        if (rawType.isMissing()) {
            return rawType;
        }
        ReferenceType genericType = rawType.getGenericType();
        if (rawType.isSimpleType() && (anUnresolvedType.typeParameters == null || anUnresolvedType.typeParameters.length == 0)) {
            rawType.world = this;
            return rawType;
        }
        if (genericType != null) {
            genericType.world = this;
            return genericType;
        }
        ReferenceTypeDelegate delegate = this.resolveDelegate((ReferenceType)rawType);
        ReferenceType genericRefType = this.makeGenericTypeFrom(delegate, (ReferenceType)rawType);
        ((ReferenceType)rawType).setGenericType(genericRefType);
        genericRefType.setDelegate(delegate);
        ((ReferenceType)rawType).setDelegate(delegate);
        return genericRefType;
    }

    private ReferenceType makeGenericTypeFrom(ReferenceTypeDelegate delegate, ReferenceType rawType) {
        String genericSig = delegate.getDeclaredGenericSignature();
        if (genericSig != null) {
            return new ReferenceType(UnresolvedType.forGenericTypeSignature(rawType.getSignature(), delegate.getDeclaredGenericSignature()), this);
        }
        return new ReferenceType(UnresolvedType.forGenericTypeVariables(rawType.getSignature(), delegate.getTypeVariables()), this);
    }

    private ReferenceType resolveGenericWildcardFor(WildcardedUnresolvedType aType) {
        BoundedReferenceType ret = null;
        if (aType.isExtends()) {
            ResolvedType resolvedUpperBound = this.resolve(aType.getUpperBound());
            if (resolvedUpperBound.isMissing()) {
                return this.getWildcard();
            }
            ret = new BoundedReferenceType((ReferenceType)resolvedUpperBound, true, this);
        } else if (aType.isSuper()) {
            ResolvedType resolvedLowerBound = this.resolve(aType.getLowerBound());
            if (resolvedLowerBound.isMissing()) {
                return this.getWildcard();
            }
            ret = new BoundedReferenceType((ReferenceType)resolvedLowerBound, false, this);
        } else {
            ret = this.getWildcard();
        }
        return ret;
    }

    protected abstract ReferenceTypeDelegate resolveDelegate(ReferenceType var1);

    public ResolvedType getCoreType(UnresolvedType tx) {
        ResolvedType coreTy = this.resolve(tx, true);
        if (coreTy.isMissing()) {
            MessageUtil.error(this.messageHandler, WeaverMessages.format("cantFindCoreType", tx.getName()));
        }
        return coreTy;
    }

    public ReferenceType lookupOrCreateName(UnresolvedType ty) {
        String signature = ty.getSignature();
        ReferenceType ret = this.lookupBySignature(signature);
        if (ret == null) {
            ret = ReferenceType.fromTypeX(ty, this);
            this.typeMap.put(signature, ret);
        }
        return ret;
    }

    public ReferenceType lookupBySignature(String signature) {
        return (ReferenceType)this.typeMap.get(signature);
    }

    public ResolvedMember resolve(Member member) {
        ResolvedMember ret;
        ResolvedType declaring = member.getDeclaringType().resolve(this);
        if (declaring.isRawType()) {
            declaring = declaring.getGenericType();
        }
        if ((ret = member.getKind() == Member.FIELD ? declaring.lookupField(member) : declaring.lookupMethod(member)) != null) {
            return ret;
        }
        return declaring.lookupSyntheticMember(member);
    }

    public void setAllLintIgnored() {
        this.allLintIgnored = true;
    }

    public boolean areAllLintIgnored() {
        return this.allLintIgnored;
    }

    public abstract IWeavingSupport getWeavingSupport();

    public final Advice createAdviceMunger(AdviceKind kind, Pointcut p, Member signature, int extraParameterFlags, IHasSourceLocation loc, ResolvedType declaringAspect) {
        AjAttribute.AdviceAttribute attribute = new AjAttribute.AdviceAttribute(kind, p, extraParameterFlags, loc.getStart(), loc.getEnd(), loc.getSourceContext());
        return this.getWeavingSupport().createAdviceMunger(attribute, p, signature, declaringAspect);
    }

    public int compareByPrecedence(ResolvedType aspect1, ResolvedType aspect2) {
        return this.precedenceCalculator.compareByPrecedence(aspect1, aspect2);
    }

    public Integer getPrecedenceIfAny(ResolvedType aspect1, ResolvedType aspect2) {
        return this.precedenceCalculator.getPrecedenceIfAny(aspect1, aspect2);
    }

    public int compareByPrecedenceAndHierarchy(ResolvedType aspect1, ResolvedType aspect2) {
        return this.precedenceCalculator.compareByPrecedenceAndHierarchy(aspect1, aspect2);
    }

    public IMessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    public void setMessageHandler(IMessageHandler messageHandler) {
        this.messageHandler = this.isInPinpointMode() ? new PinpointingMessageHandler(messageHandler) : messageHandler;
    }

    public void showMessage(IMessage.Kind kind, String message, ISourceLocation loc1, ISourceLocation loc2) {
        if (loc1 != null) {
            this.messageHandler.handleMessage(new Message(message, kind, null, loc1));
            if (loc2 != null) {
                this.messageHandler.handleMessage(new Message(message, kind, null, loc2));
            }
        } else {
            this.messageHandler.handleMessage(new Message(message, kind, null, loc2));
        }
    }

    public void setCrossReferenceHandler(ICrossReferenceHandler xrefHandler) {
        this.xrefHandler = xrefHandler;
    }

    public ICrossReferenceHandler getCrossReferenceHandler() {
        return this.xrefHandler;
    }

    public void setTypeVariableLookupScope(TypeVariableDeclaringElement scope) {
        this.typeVariableLookupScope = scope;
    }

    public TypeVariableDeclaringElement getTypeVariableLookupScope() {
        return this.typeVariableLookupScope;
    }

    public List<DeclareParents> getDeclareParents() {
        return this.crosscuttingMembersSet.getDeclareParents();
    }

    public List<DeclareAnnotation> getDeclareAnnotationOnTypes() {
        return this.crosscuttingMembersSet.getDeclareAnnotationOnTypes();
    }

    public List<DeclareAnnotation> getDeclareAnnotationOnFields() {
        return this.crosscuttingMembersSet.getDeclareAnnotationOnFields();
    }

    public List<DeclareAnnotation> getDeclareAnnotationOnMethods() {
        return this.crosscuttingMembersSet.getDeclareAnnotationOnMethods();
    }

    public List<DeclareTypeErrorOrWarning> getDeclareTypeEows() {
        return this.crosscuttingMembersSet.getDeclareTypeEows();
    }

    public List<DeclareSoft> getDeclareSoft() {
        return this.crosscuttingMembersSet.getDeclareSofts();
    }

    public CrosscuttingMembersSet getCrosscuttingMembersSet() {
        return this.crosscuttingMembersSet;
    }

    public IStructureModel getModel() {
        return this.model;
    }

    public void setModel(IStructureModel model) {
        this.model = model;
    }

    public Lint getLint() {
        return this.lint;
    }

    public void setLint(Lint lint) {
        this.lint = lint;
    }

    public boolean isXnoInline() {
        return this.XnoInline;
    }

    public void setXnoInline(boolean xnoInline) {
        this.XnoInline = xnoInline;
    }

    public boolean isXlazyTjp() {
        return this.XlazyTjp;
    }

    public void setXlazyTjp(boolean b) {
        this.XlazyTjp = b;
    }

    public boolean isHasMemberSupportEnabled() {
        return this.XhasMember;
    }

    public void setXHasMemberSupportEnabled(boolean b) {
        this.XhasMember = b;
    }

    public boolean isInPinpointMode() {
        return this.Xpinpoint;
    }

    public void setPinpointMode(boolean b) {
        this.Xpinpoint = b;
    }

    public boolean useFinal() {
        return this.useFinal;
    }

    public boolean isMinimalModel() {
        this.ensureAdvancedConfigurationProcessed();
        return this.minimalModel;
    }

    public boolean isTargettingRuntime1_6_10() {
        this.ensureAdvancedConfigurationProcessed();
        return this.targettingRuntime1_6_10;
    }

    public void setBehaveInJava5Way(boolean b) {
        this.behaveInJava5Way = b;
    }

    public void setTiming(boolean timersOn, boolean reportPeriodically) {
        this.timing = timersOn;
        this.timingPeriodically = reportPeriodically;
    }

    public void setErrorAndWarningThreshold(boolean errorThreshold, boolean warningThreshold) {
        this.errorThreshold = errorThreshold;
        this.warningThreshold = warningThreshold;
    }

    public boolean isIgnoringUnusedDeclaredThrownException() {
        return this.errorThreshold || this.warningThreshold;
    }

    public void performExtraConfiguration(String config) {
        int pos2;
        if (config == null) {
            return;
        }
        this.extraConfiguration = new Properties();
        int pos = -1;
        while ((pos = config.indexOf(",")) != -1) {
            String nvpair = config.substring(0, pos);
            int pos22 = nvpair.indexOf("=");
            if (pos22 != -1) {
                String n = nvpair.substring(0, pos22);
                String v = nvpair.substring(pos22 + 1);
                this.extraConfiguration.setProperty(n, v);
            }
            config = config.substring(pos + 1);
        }
        if (config.length() > 0 && (pos2 = config.indexOf("=")) != -1) {
            String n = config.substring(0, pos2);
            String v = config.substring(pos2 + 1);
            this.extraConfiguration.setProperty(n, v);
        }
        this.ensureAdvancedConfigurationProcessed();
    }

    public boolean areInfoMessagesEnabled() {
        if (this.infoMessagesEnabled == 0) {
            this.infoMessagesEnabled = this.messageHandler.isIgnoring(IMessage.INFO) ? 1 : 2;
        }
        return this.infoMessagesEnabled == 2;
    }

    public Properties getExtraConfiguration() {
        return this.extraConfiguration;
    }

    public boolean isInJava5Mode() {
        return this.behaveInJava5Way;
    }

    public boolean isTimingEnabled() {
        return this.timing;
    }

    public void setTargetAspectjRuntimeLevel(String s) {
        this.targetAspectjRuntimeLevel = RuntimeVersion.getVersionFor(s);
    }

    public void setOptionalJoinpoints(String jps) {
        if (jps == null) {
            return;
        }
        if (jps.indexOf("arrayconstruction") != -1) {
            this.optionalJoinpoint_ArrayConstruction = true;
        }
        if (jps.indexOf("synchronization") != -1) {
            this.optionalJoinpoint_Synchronization = true;
        }
    }

    public boolean isJoinpointArrayConstructionEnabled() {
        return this.optionalJoinpoint_ArrayConstruction;
    }

    public boolean isJoinpointSynchronizationEnabled() {
        return this.optionalJoinpoint_Synchronization;
    }

    public RuntimeVersion getTargetAspectjRuntimeLevel() {
        return this.targetAspectjRuntimeLevel;
    }

    public boolean isTargettingAspectJRuntime12() {
        boolean b = false;
        b = !this.isInJava5Mode() ? true : this.getTargetAspectjRuntimeLevel() == RuntimeVersion.V1_2;
        return b;
    }

    public void validateType(UnresolvedType type) {
    }

    public boolean isDemotionActive() {
        return true;
    }

    public TypeVariable[] getTypeVariablesCurrentlyBeingProcessed(Class<?> baseClass) {
        return this.workInProgress1.get(baseClass);
    }

    public void recordTypeVariablesCurrentlyBeingProcessed(Class<?> baseClass, TypeVariable[] typeVariables) {
        this.workInProgress1.put(baseClass, typeVariables);
    }

    public void forgetTypeVariablesCurrentlyBeingProcessed(Class<?> baseClass) {
        this.workInProgress1.remove(baseClass);
    }

    public void setAddSerialVerUID(boolean b) {
        this.addSerialVerUID = b;
    }

    public boolean isAddSerialVerUID() {
        return this.addSerialVerUID;
    }

    public void flush() {
        this.typeMap.expendableMap.clear();
    }

    public void ensureAdvancedConfigurationProcessed() {
        if (!this.checkedAdvancedConfiguration) {
            Properties p = this.getExtraConfiguration();
            if (p != null) {
                String s = p.getProperty(xsetBCEL_REPOSITORY_CACHING, "true");
                this.bcelRepositoryCaching = s.equalsIgnoreCase("true");
                if (!this.bcelRepositoryCaching) {
                    this.getMessageHandler().handleMessage(MessageUtil.info("[bcelRepositoryCaching=false] AspectJ will not use a bcel cache for class information"));
                }
                if ((s = p.getProperty(xsetITD_VERSION, "2")).equals(xsetITD_VERSION_ORIGINAL)) {
                    this.itdVersion = 1;
                }
                if ((s = p.getProperty(xsetAVOID_FINAL, xsetCOMPLETE_BINARY_TYPES_DEFAULT)).equalsIgnoreCase("true")) {
                    this.useFinal = false;
                }
                if ((s = p.getProperty(xsetMINIMAL_MODEL, "true")).equalsIgnoreCase(xsetCOMPLETE_BINARY_TYPES_DEFAULT)) {
                    this.minimalModel = false;
                }
                if ((s = p.getProperty(xsetTARGETING_RUNTIME_1610, xsetCOMPLETE_BINARY_TYPES_DEFAULT)).equalsIgnoreCase("true")) {
                    this.targettingRuntime1_6_10 = true;
                }
                s = p.getProperty(xsetFAST_PACK_METHODS, "true");
                this.fastMethodPacking = s.equalsIgnoreCase("true");
                s = p.getProperty(xsetPIPELINE_COMPILATION, "true");
                this.shouldPipelineCompilation = s.equalsIgnoreCase("true");
                s = p.getProperty(xsetGENERATE_STACKMAPS, xsetCOMPLETE_BINARY_TYPES_DEFAULT);
                this.shouldGenerateStackMaps = s.equalsIgnoreCase("true");
                s = p.getProperty(xsetCOMPLETE_BINARY_TYPES, xsetCOMPLETE_BINARY_TYPES_DEFAULT);
                this.completeBinaryTypes = s.equalsIgnoreCase("true");
                if (this.completeBinaryTypes) {
                    this.getMessageHandler().handleMessage(MessageUtil.info("[completeBinaryTypes=true] Completion of binary types activated"));
                }
                if ((s = p.getProperty(xsetTYPE_DEMOTION)) != null) {
                    boolean b = this.typeMap.demotionSystemActive;
                    if (b && s.equalsIgnoreCase(xsetCOMPLETE_BINARY_TYPES_DEFAULT)) {
                        System.out.println("typeDemotion=false: type demotion switched OFF");
                        this.typeMap.demotionSystemActive = false;
                    } else if (!b && s.equalsIgnoreCase("true")) {
                        System.out.println("typeDemotion=true: type demotion switched ON");
                        this.typeMap.demotionSystemActive = true;
                    }
                }
                if ((s = p.getProperty(xsetOVERWEAVING, xsetCOMPLETE_BINARY_TYPES_DEFAULT)).equalsIgnoreCase("true")) {
                    this.overWeaving = true;
                }
                if ((s = p.getProperty(xsetTYPE_DEMOTION_DEBUG, xsetCOMPLETE_BINARY_TYPES_DEFAULT)).equalsIgnoreCase("true")) {
                    this.typeMap.debugDemotion = true;
                }
                if ((s = p.getProperty(xsetTYPE_REFS, "true")).equalsIgnoreCase(xsetCOMPLETE_BINARY_TYPES_DEFAULT)) {
                    this.typeMap.policy = 2;
                }
                this.runMinimalMemorySet = p.getProperty(xsetRUN_MINIMAL_MEMORY) != null;
                s = p.getProperty(xsetRUN_MINIMAL_MEMORY, xsetCOMPLETE_BINARY_TYPES_DEFAULT);
                this.runMinimalMemory = s.equalsIgnoreCase("true");
                s = p.getProperty(xsetDEBUG_STRUCTURAL_CHANGES_CODE, xsetCOMPLETE_BINARY_TYPES_DEFAULT);
                this.forDEBUG_structuralChangesCode = s.equalsIgnoreCase("true");
                s = p.getProperty(xsetTRANSIENT_TJP_FIELDS, xsetCOMPLETE_BINARY_TYPES_DEFAULT);
                this.transientTjpFields = s.equalsIgnoreCase("true");
                s = p.getProperty(xsetDEBUG_BRIDGING, xsetCOMPLETE_BINARY_TYPES_DEFAULT);
                this.forDEBUG_bridgingCode = s.equalsIgnoreCase("true");
                s = p.getProperty(xsetGENERATE_NEW_LVTS, "true");
                this.generateNewLvts = s.equalsIgnoreCase("true");
                if (!this.generateNewLvts) {
                    this.getMessageHandler().handleMessage(MessageUtil.info("[generateNewLvts=false] for methods without an incoming local variable table, do not generate one"));
                }
                s = p.getProperty(xsetOPTIMIZED_MATCHING, "true");
                this.optimizedMatching = s.equalsIgnoreCase("true");
                if (!this.optimizedMatching) {
                    this.getMessageHandler().handleMessage(MessageUtil.info("[optimizedMatching=false] optimized matching turned off"));
                }
                s = p.getProperty(xsetTIMERS_PER_JOINPOINT, "25000");
                try {
                    this.timersPerJoinpoint = Integer.parseInt(s);
                }
                catch (Exception e) {
                    this.getMessageHandler().handleMessage(MessageUtil.error("unable to process timersPerJoinpoint value of " + s));
                    this.timersPerJoinpoint = 25000L;
                }
                s = p.getProperty(xsetTIMERS_PER_FASTMATCH_CALL, "250");
                try {
                    this.timersPerType = Integer.parseInt(s);
                }
                catch (Exception e) {
                    this.getMessageHandler().handleMessage(MessageUtil.error("unable to process timersPerType value of " + s));
                    this.timersPerType = 250L;
                }
            }
            try {
                if (systemPropertyOverWeaving) {
                    this.overWeaving = true;
                }
                String value = null;
                value = System.getProperty("aspectj.typeDemotion", xsetCOMPLETE_BINARY_TYPES_DEFAULT);
                if (value.equalsIgnoreCase("true")) {
                    System.out.println("ASPECTJ: aspectj.typeDemotion=true: type demotion switched ON");
                    this.typeMap.demotionSystemActive = true;
                }
                if ((value = System.getProperty("aspectj.minimalModel", xsetCOMPLETE_BINARY_TYPES_DEFAULT)).equalsIgnoreCase("true")) {
                    System.out.println("ASPECTJ: aspectj.minimalModel=true: minimal model switched ON");
                    this.minimalModel = true;
                }
            }
            catch (Throwable t) {
                System.err.println("ASPECTJ: Unable to read system properties");
                t.printStackTrace();
            }
            this.checkedAdvancedConfiguration = true;
        }
    }

    public boolean isRunMinimalMemory() {
        this.ensureAdvancedConfigurationProcessed();
        return this.runMinimalMemory;
    }

    public boolean isTransientTjpFields() {
        this.ensureAdvancedConfigurationProcessed();
        return this.transientTjpFields;
    }

    public boolean isRunMinimalMemorySet() {
        this.ensureAdvancedConfigurationProcessed();
        return this.runMinimalMemorySet;
    }

    public boolean shouldFastPackMethods() {
        this.ensureAdvancedConfigurationProcessed();
        return this.fastMethodPacking;
    }

    public boolean shouldPipelineCompilation() {
        this.ensureAdvancedConfigurationProcessed();
        return this.shouldPipelineCompilation;
    }

    public boolean shouldGenerateStackMaps() {
        this.ensureAdvancedConfigurationProcessed();
        return this.shouldGenerateStackMaps;
    }

    public void setIncrementalCompileCouldFollow(boolean b) {
        this.incrementalCompileCouldFollow = b;
    }

    public boolean couldIncrementalCompileFollow() {
        return this.incrementalCompileCouldFollow;
    }

    public void setSynchronizationPointcutsInUse() {
        if (trace.isTraceEnabled()) {
            trace.enter("setSynchronizationPointcutsInUse", this);
        }
        this.synchronizationPointcutsInUse = true;
        if (trace.isTraceEnabled()) {
            trace.exit("setSynchronizationPointcutsInUse");
        }
    }

    public boolean areSynchronizationPointcutsInUse() {
        return this.synchronizationPointcutsInUse;
    }

    public void registerPointcutHandler(PointcutDesignatorHandler designatorHandler) {
        if (this.pointcutDesignators == null) {
            this.pointcutDesignators = new HashSet<PointcutDesignatorHandler>();
        }
        this.pointcutDesignators.add(designatorHandler);
    }

    public Set<PointcutDesignatorHandler> getRegisteredPointcutHandlers() {
        if (this.pointcutDesignators == null) {
            return Collections.emptySet();
        }
        return this.pointcutDesignators;
    }

    public void reportMatch(ShadowMunger munger, Shadow shadow) {
    }

    public boolean isOverWeaving() {
        return this.overWeaving;
    }

    public void reportCheckerMatch(Checker checker, Shadow shadow) {
    }

    public boolean isXmlConfigured() {
        return false;
    }

    public boolean isAspectIncluded(ResolvedType aspectType) {
        return true;
    }

    public boolean hasUnsatisfiedDependency(ResolvedType aspectType) {
        return false;
    }

    public TypePattern getAspectScope(ResolvedType declaringType) {
        return null;
    }

    public Map<String, ResolvedType> getFixed() {
        return this.typeMap.tMap;
    }

    public Map<String, Reference<ResolvedType>> getExpendable() {
        return this.typeMap.expendableMap;
    }

    public void demote() {
        this.typeMap.demote();
    }

    protected boolean isExpendable(ResolvedType type) {
        return !type.equals(UnresolvedType.OBJECT) && !type.isExposedToWeaver() && !type.isPrimitiveType() && !type.isPrimitiveArray();
    }

    public Map<ResolvedType, Set<ResolvedType>> getExclusionMap() {
        return this.exclusionMap;
    }

    public void record(Pointcut pointcut, long timetaken) {
        if (this.timeCollector == null) {
            this.ensureAdvancedConfigurationProcessed();
            this.timeCollector = new TimeCollector(this);
        }
        this.timeCollector.record(pointcut, timetaken);
    }

    public void recordFastMatch(Pointcut pointcut, long timetaken) {
        if (this.timeCollector == null) {
            this.ensureAdvancedConfigurationProcessed();
            this.timeCollector = new TimeCollector(this);
        }
        this.timeCollector.recordFastMatch(pointcut, timetaken);
    }

    public void reportTimers() {
        if (this.timeCollector != null && !this.timingPeriodically) {
            this.timeCollector.report();
            this.timeCollector = new TimeCollector(this);
        }
    }

    public TypeMap getTypeMap() {
        return this.typeMap;
    }

    public static void reset() {
    }

    public int getItdVersion() {
        return this.itdVersion;
    }

    public abstract boolean isLoadtimeWeaving();

    public void classWriteEvent(char[][] compoundName) {
    }

    static {
        try {
            String value = System.getProperty("aspectj.overweaving", xsetCOMPLETE_BINARY_TYPES_DEFAULT);
            if (value.equalsIgnoreCase("true")) {
                System.out.println("ASPECTJ: aspectj.overweaving=true: overweaving switched ON");
                systemPropertyOverWeaving = true;
            }
        }
        catch (Throwable t) {
            System.err.println("ASPECTJ: Unable to read system properties");
            t.printStackTrace();
        }
    }

    private static class TimeCollector {
        private World world;
        long joinpointCount;
        long typeCount;
        long perJoinpointCount;
        long perTypes;
        Map<String, Long> joinpointsPerPointcut = new HashMap<String, Long>();
        Map<String, Long> timePerPointcut = new HashMap<String, Long>();
        Map<String, Long> fastMatchTimesPerPointcut = new HashMap<String, Long>();
        Map<String, Long> fastMatchTypesPerPointcut = new HashMap<String, Long>();

        TimeCollector(World world) {
            this.perJoinpointCount = world.timersPerJoinpoint;
            this.perTypes = world.timersPerType;
            this.world = world;
            this.joinpointCount = 0L;
            this.typeCount = 0L;
            this.joinpointsPerPointcut = new HashMap<String, Long>();
            this.timePerPointcut = new HashMap<String, Long>();
        }

        public void report() {
            StringBuffer sb;
            long totalTime = 0L;
            for (String p : this.joinpointsPerPointcut.keySet()) {
                totalTime += this.timePerPointcut.get(p).longValue();
            }
            this.world.getMessageHandler().handleMessage(MessageUtil.info("Pointcut matching cost (total=" + totalTime / 1000000L + "ms for " + this.joinpointCount + " joinpoint match calls):"));
            for (String p : this.joinpointsPerPointcut.keySet()) {
                sb = new StringBuffer();
                sb.append("Time:" + this.timePerPointcut.get(p) / 1000000L + "ms (jps:#" + this.joinpointsPerPointcut.get(p) + ") matching against " + p);
                this.world.getMessageHandler().handleMessage(MessageUtil.info(sb.toString()));
            }
            this.world.getMessageHandler().handleMessage(MessageUtil.info("---"));
            totalTime = 0L;
            for (String p : this.fastMatchTimesPerPointcut.keySet()) {
                totalTime += this.fastMatchTimesPerPointcut.get(p).longValue();
            }
            this.world.getMessageHandler().handleMessage(MessageUtil.info("Pointcut fast matching cost (total=" + totalTime / 1000000L + "ms for " + this.typeCount + " fast match calls):"));
            for (String p : this.fastMatchTimesPerPointcut.keySet()) {
                sb = new StringBuffer();
                sb.append("Time:" + this.fastMatchTimesPerPointcut.get(p) / 1000000L + "ms (types:#" + this.fastMatchTypesPerPointcut.get(p) + ") fast matching against " + p);
                this.world.getMessageHandler().handleMessage(MessageUtil.info(sb.toString()));
            }
            this.world.getMessageHandler().handleMessage(MessageUtil.info("---"));
        }

        void record(Pointcut pointcut, long timetakenInNs) {
            ++this.joinpointCount;
            String pointcutText = pointcut.toString();
            Long jpcounter = this.joinpointsPerPointcut.get(pointcutText);
            if (jpcounter == null) {
                jpcounter = 1L;
            } else {
                Long l = jpcounter;
                Long l2 = jpcounter = Long.valueOf(jpcounter + 1L);
            }
            this.joinpointsPerPointcut.put(pointcutText, jpcounter);
            Long time = this.timePerPointcut.get(pointcutText);
            time = time == null ? Long.valueOf(timetakenInNs) : Long.valueOf(time + timetakenInNs);
            this.timePerPointcut.put(pointcutText, time);
            if (this.world.timingPeriodically && this.joinpointCount % this.perJoinpointCount == 0L) {
                long totalTime = 0L;
                for (String p : this.joinpointsPerPointcut.keySet()) {
                    totalTime += this.timePerPointcut.get(p).longValue();
                }
                this.world.getMessageHandler().handleMessage(MessageUtil.info("Pointcut matching cost (total=" + totalTime / 1000000L + "ms for " + this.joinpointCount + " joinpoint match calls):"));
                for (String p : this.joinpointsPerPointcut.keySet()) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Time:" + this.timePerPointcut.get(p) / 1000000L + "ms (jps:#" + this.joinpointsPerPointcut.get(p) + ") matching against " + p);
                    this.world.getMessageHandler().handleMessage(MessageUtil.info(sb.toString()));
                }
                this.world.getMessageHandler().handleMessage(MessageUtil.info("---"));
            }
        }

        void recordFastMatch(Pointcut pointcut, long timetakenInNs) {
            ++this.typeCount;
            String pointcutText = pointcut.toString();
            Long typecounter = this.fastMatchTypesPerPointcut.get(pointcutText);
            if (typecounter == null) {
                typecounter = 1L;
            } else {
                Long l = typecounter;
                Long l2 = typecounter = Long.valueOf(typecounter + 1L);
            }
            this.fastMatchTypesPerPointcut.put(pointcutText, typecounter);
            Long time = this.fastMatchTimesPerPointcut.get(pointcutText);
            time = time == null ? Long.valueOf(timetakenInNs) : Long.valueOf(time + timetakenInNs);
            this.fastMatchTimesPerPointcut.put(pointcutText, time);
            if (this.world.timingPeriodically && this.typeCount % this.perTypes == 0L) {
                long totalTime = 0L;
                for (String p : this.fastMatchTimesPerPointcut.keySet()) {
                    totalTime += this.fastMatchTimesPerPointcut.get(p).longValue();
                }
                this.world.getMessageHandler().handleMessage(MessageUtil.info("Pointcut fast matching cost (total=" + totalTime / 1000000L + "ms for " + this.typeCount + " fast match calls):"));
                for (String p : this.fastMatchTimesPerPointcut.keySet()) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Time:" + this.fastMatchTimesPerPointcut.get(p) / 1000000L + "ms (types:#" + this.fastMatchTypesPerPointcut.get(p) + ") fast matching against " + p);
                    this.world.getMessageHandler().handleMessage(MessageUtil.info(sb.toString()));
                }
                this.world.getMessageHandler().handleMessage(MessageUtil.info("---"));
            }
        }
    }

    private static class AspectPrecedenceCalculator {
        private final World world;
        private final Map<PrecedenceCacheKey, Integer> cachedResults;

        public AspectPrecedenceCalculator(World forSomeWorld) {
            this.world = forSomeWorld;
            this.cachedResults = new HashMap<PrecedenceCacheKey, Integer>();
        }

        public int compareByPrecedence(ResolvedType firstAspect, ResolvedType secondAspect) {
            PrecedenceCacheKey key = new PrecedenceCacheKey(firstAspect, secondAspect);
            if (this.cachedResults.containsKey(key)) {
                return this.cachedResults.get(key);
            }
            int order = 0;
            DeclarePrecedence orderer = null;
            for (Declare declare : this.world.getCrosscuttingMembersSet().getDeclareDominates()) {
                DeclarePrecedence d = (DeclarePrecedence)declare;
                int thisOrder = d.compare(firstAspect, secondAspect);
                if (thisOrder == 0) continue;
                if (orderer == null) {
                    orderer = d;
                }
                if (order != 0 && order != thisOrder) {
                    ISourceLocation[] isls = new ISourceLocation[]{orderer.getSourceLocation(), d.getSourceLocation()};
                    Message m = new Message("conflicting declare precedence orderings for aspects: " + firstAspect.getName() + " and " + secondAspect.getName(), null, true, isls);
                    this.world.getMessageHandler().handleMessage(m);
                    continue;
                }
                order = thisOrder;
            }
            this.cachedResults.put(key, new Integer(order));
            return order;
        }

        public Integer getPrecedenceIfAny(ResolvedType aspect1, ResolvedType aspect2) {
            return this.cachedResults.get(new PrecedenceCacheKey(aspect1, aspect2));
        }

        public int compareByPrecedenceAndHierarchy(ResolvedType firstAspect, ResolvedType secondAspect) {
            if (firstAspect.equals(secondAspect)) {
                return 0;
            }
            int ret = this.compareByPrecedence(firstAspect, secondAspect);
            if (ret != 0) {
                return ret;
            }
            if (firstAspect.isAssignableFrom(secondAspect)) {
                return -1;
            }
            if (secondAspect.isAssignableFrom(firstAspect)) {
                return 1;
            }
            return 0;
        }

        private static class PrecedenceCacheKey {
            public ResolvedType aspect1;
            public ResolvedType aspect2;

            public PrecedenceCacheKey(ResolvedType a1, ResolvedType a2) {
                this.aspect1 = a1;
                this.aspect2 = a2;
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof PrecedenceCacheKey)) {
                    return false;
                }
                PrecedenceCacheKey other = (PrecedenceCacheKey)obj;
                return this.aspect1 == other.aspect1 && this.aspect2 == other.aspect2;
            }

            public int hashCode() {
                return this.aspect1.hashCode() + this.aspect2.hashCode();
            }
        }
    }

    public static class TypeMap {
        public static final int DONT_USE_REFS = 0;
        public static final int USE_WEAK_REFS = 1;
        public static final int USE_SOFT_REFS = 2;
        public List<String> addedSinceLastDemote;
        public List<String> writtenClasses;
        private static boolean debug = false;
        public static boolean useExpendableMap = true;
        private boolean demotionSystemActive;
        private boolean debugDemotion = false;
        public int policy = 1;
        final Map<String, ResolvedType> tMap = new HashMap<String, ResolvedType>();
        final Map<String, Reference<ResolvedType>> expendableMap = Collections.synchronizedMap(new WeakHashMap());
        private final World w;
        private boolean memoryProfiling = false;
        private int maxExpendableMapSize = -1;
        private int collectedTypes = 0;
        private final ReferenceQueue<ResolvedType> rq = new ReferenceQueue();

        TypeMap(World w) {
            this.demotionSystemActive = w.isDemotionActive() && (w.isLoadtimeWeaving() || w.couldIncrementalCompileFollow());
            this.addedSinceLastDemote = new ArrayList<String>();
            this.writtenClasses = new ArrayList<String>();
            this.w = w;
            this.memoryProfiling = false;
        }

        public Map<String, Reference<ResolvedType>> getExpendableMap() {
            return this.expendableMap;
        }

        public Map<String, ResolvedType> getMainMap() {
            return this.tMap;
        }

        public int demote() {
            return this.demote(false);
        }

        public int demote(boolean atEndOfCompile) {
            if (!this.demotionSystemActive) {
                return 0;
            }
            if (this.debugDemotion) {
                System.out.println("Demotion running " + this.addedSinceLastDemote);
            }
            boolean isLtw = this.w.isLoadtimeWeaving();
            int demotionCounter = 0;
            if (isLtw) {
                for (String key : this.addedSinceLastDemote) {
                    List<ConcreteTypeMunger> typeMungers;
                    ResolvedType type = this.tMap.get(key);
                    if (type == null || type.isAspect() || type.equals(UnresolvedType.OBJECT) || type.isPrimitiveType() || (typeMungers = type.getInterTypeMungers()) != null && typeMungers.size() != 0) continue;
                    this.tMap.remove(key);
                    this.insertInExpendableMap(key, type);
                    ++demotionCounter;
                }
                this.addedSinceLastDemote.clear();
            } else {
                ArrayList<String> forRemoval = new ArrayList<String>();
                for (String key : this.addedSinceLastDemote) {
                    ResolvedType type = this.tMap.get(key);
                    if (type == null) {
                        forRemoval.add(key);
                        continue;
                    }
                    if (!this.writtenClasses.contains(type.getName())) continue;
                    if (!(type == null || type.isAspect() || type.equals(UnresolvedType.OBJECT) || type.isPrimitiveType())) {
                        List<ConcreteTypeMunger> typeMungers = type.getInterTypeMungers();
                        if (typeMungers == null || typeMungers.size() == 0) {
                            boolean hasBeenWoven;
                            ReferenceTypeDelegate delegate = ((ReferenceType)type).getDelegate();
                            boolean isWeavable = delegate == null ? false : delegate.isExposedToWeaver();
                            boolean bl = hasBeenWoven = delegate == null ? false : delegate.hasBeenWoven();
                            if (isWeavable && !hasBeenWoven) continue;
                            if (this.debugDemotion) {
                                System.out.println("Demoting " + key);
                            }
                            forRemoval.add(key);
                            this.tMap.remove(key);
                            this.insertInExpendableMap(key, type);
                            ++demotionCounter;
                            continue;
                        }
                        this.writtenClasses.remove(type.getName());
                        forRemoval.add(key);
                        continue;
                    }
                    this.writtenClasses.remove(type.getName());
                    forRemoval.add(key);
                }
                this.addedSinceLastDemote.removeAll(forRemoval);
            }
            if (this.debugDemotion) {
                System.out.println("Demoted " + demotionCounter + " types.  Types remaining in fixed set #" + this.tMap.keySet().size() + ".  addedSinceLastDemote size is " + this.addedSinceLastDemote.size());
                System.out.println("writtenClasses.size() = " + this.writtenClasses.size() + ": " + this.writtenClasses);
            }
            if (atEndOfCompile) {
                if (this.debugDemotion) {
                    System.out.println("Clearing writtenClasses");
                }
                this.writtenClasses.clear();
            }
            return demotionCounter;
        }

        private void insertInExpendableMap(String key, ResolvedType type) {
            Reference<ResolvedType> existingReference;
            if (useExpendableMap && ((existingReference = this.expendableMap.get(key)) == null || existingReference.get() == null)) {
                this.expendableMap.remove(key);
                if (this.policy == 2) {
                    this.expendableMap.put(key, new SoftReference<ResolvedType>(type));
                } else {
                    this.expendableMap.put(key, new WeakReference<ResolvedType>(type));
                }
            }
        }

        public ResolvedType put(String key, ResolvedType type) {
            if (!type.isCacheable()) {
                return type;
            }
            if (type.isParameterizedType() && type.isParameterizedWithTypeVariable()) {
                if (debug) {
                    System.err.println("Not putting a parameterized type that utilises member declared type variables into the typemap: key=" + key + " type=" + type);
                }
                return type;
            }
            if (type.isTypeVariableReference()) {
                if (debug) {
                    System.err.println("Not putting a type variable reference type into the typemap: key=" + key + " type=" + type);
                }
                return type;
            }
            if (type instanceof BoundedReferenceType) {
                if (debug) {
                    System.err.println("Not putting a bounded reference type into the typemap: key=" + key + " type=" + type);
                }
                return type;
            }
            if (type instanceof MissingResolvedTypeWithKnownSignature) {
                if (debug) {
                    System.err.println("Not putting a missing type into the typemap: key=" + key + " type=" + type);
                }
                return type;
            }
            if (type instanceof ReferenceType && ((ReferenceType)type).getDelegate() == null && this.w.isExpendable(type)) {
                if (debug) {
                    System.err.println("Not putting expendable ref type with null delegate into typemap: key=" + key + " type=" + type);
                }
                return type;
            }
            if (type instanceof ReferenceType && type.getWorld().isInJava5Mode() && ((ReferenceType)type).getDelegate() != null && type.isGenericType()) {
                throw new BCException("Attempt to add generic type to typemap " + type.toString() + " (should be raw)");
            }
            if (this.w.isExpendable(type)) {
                if (useExpendableMap) {
                    this.expendableMap.remove(key);
                    if (this.policy == 1) {
                        if (this.memoryProfiling) {
                            this.expendableMap.put(key, new WeakReference<ResolvedType>(type, this.rq));
                        } else {
                            this.expendableMap.put(key, new WeakReference<ResolvedType>(type));
                        }
                    } else if (this.policy == 2) {
                        if (this.memoryProfiling) {
                            this.expendableMap.put(key, new SoftReference<ResolvedType>(type, this.rq));
                        } else {
                            this.expendableMap.put(key, new SoftReference<ResolvedType>(type));
                        }
                    }
                }
                if (this.memoryProfiling && this.expendableMap.size() > this.maxExpendableMapSize) {
                    this.maxExpendableMapSize = this.expendableMap.size();
                }
                return type;
            }
            if (this.demotionSystemActive) {
                this.addedSinceLastDemote.add(key);
            }
            return this.tMap.put(key, type);
        }

        public void report() {
            if (!this.memoryProfiling) {
                return;
            }
            this.checkq();
            this.w.getMessageHandler().handleMessage(MessageUtil.info("MEMORY: world expendable type map reached maximum size of #" + this.maxExpendableMapSize + " entries"));
            this.w.getMessageHandler().handleMessage(MessageUtil.info("MEMORY: types collected through garbage collection #" + this.collectedTypes + " entries"));
        }

        public void checkq() {
            if (!this.memoryProfiling) {
                return;
            }
            Reference<ResolvedType> r = null;
            while ((r = this.rq.poll()) != null) {
                ++this.collectedTypes;
            }
        }

        public ResolvedType get(String key) {
            this.checkq();
            ResolvedType ret = this.tMap.get(key);
            if (ret == null) {
                SoftReference ref;
                if (this.policy == 1) {
                    WeakReference ref2 = (WeakReference)this.expendableMap.get(key);
                    if (ref2 != null) {
                        ret = (ResolvedType)ref2.get();
                    }
                } else if (this.policy == 2 && (ref = (SoftReference)this.expendableMap.get(key)) != null) {
                    ret = (ResolvedType)ref.get();
                }
            }
            return ret;
        }

        public ResolvedType remove(String key) {
            ResolvedType ret = this.tMap.remove(key);
            if (ret == null) {
                SoftReference wref;
                if (this.policy == 1) {
                    WeakReference wref2 = (WeakReference)this.expendableMap.remove(key);
                    if (wref2 != null) {
                        ret = (ResolvedType)wref2.get();
                    }
                } else if (this.policy == 2 && (wref = (SoftReference)this.expendableMap.remove(key)) != null) {
                    ret = (ResolvedType)wref.get();
                }
            }
            return ret;
        }

        public void classWriteEvent(String classname) {
            if (this.demotionSystemActive) {
                this.writtenClasses.add(classname);
            }
            if (this.debugDemotion) {
                System.out.println("Class write event for " + classname);
            }
        }

        public void demote(ResolvedType type) {
            String key = type.getSignature();
            if (this.debugDemotion) {
                this.addedSinceLastDemote.remove(key);
            }
            this.tMap.remove(key);
            this.insertInExpendableMap(key, type);
        }
    }
}

