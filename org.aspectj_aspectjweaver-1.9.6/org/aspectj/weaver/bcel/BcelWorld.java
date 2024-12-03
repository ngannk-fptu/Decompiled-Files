/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.aspectj.apache.bcel.classfile.ClassParser;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.generic.FieldInstruction;
import org.aspectj.apache.bcel.generic.INVOKEINTERFACE;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InvokeInstruction;
import org.aspectj.apache.bcel.generic.MULTIANEWARRAY;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.apache.bcel.util.ClassLoaderReference;
import org.aspectj.apache.bcel.util.ClassLoaderRepository;
import org.aspectj.apache.bcel.util.ClassPath;
import org.aspectj.apache.bcel.util.NonCachingClassLoaderRepository;
import org.aspectj.apache.bcel.util.Repository;
import org.aspectj.asm.AsmManager;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.internal.CharOperation;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.bridge.WeaveMessage;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.AnnotationOnTypeMunger;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.Checker;
import org.aspectj.weaver.ICrossReferenceHandler;
import org.aspectj.weaver.IWeavingSupport;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberImpl;
import org.aspectj.weaver.MemberKind;
import org.aspectj.weaver.NewParentTypeMunger;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelMethod;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelTypeMunger;
import org.aspectj.weaver.bcel.BcelWeakClassLoaderReference;
import org.aspectj.weaver.bcel.BcelWeavingSupport;
import org.aspectj.weaver.bcel.ClassPathManager;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.LazyMethodGen;
import org.aspectj.weaver.bcel.TypeDelegateResolver;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.aspectj.weaver.loadtime.definition.DocumentParser;
import org.aspectj.weaver.model.AsmRelationshipProvider;
import org.aspectj.weaver.patterns.DeclareAnnotation;
import org.aspectj.weaver.patterns.DeclareParents;
import org.aspectj.weaver.patterns.ParserException;
import org.aspectj.weaver.patterns.PatternParser;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;

public class BcelWorld
extends World
implements Repository {
    private final ClassPathManager classPath;
    protected Repository delegate;
    private BcelWeakClassLoaderReference loaderRef;
    private final BcelWeavingSupport bcelWeavingSupport = new BcelWeavingSupport();
    private boolean isXmlConfiguredWorld = false;
    private WeavingXmlConfig xmlConfiguration;
    private List<TypeDelegateResolver> typeDelegateResolvers;
    private static Trace trace = TraceFactory.getTraceFactory().getTrace(BcelWorld.class);
    private List<String> aspectRequiredTypesProcessed = new ArrayList<String>();
    private Map<String, String> aspectRequiredTypes = null;

    public BcelWorld() {
        this("");
    }

    public BcelWorld(String cp) {
        this(BcelWorld.makeDefaultClasspath(cp), IMessageHandler.THROW, null);
    }

    public IRelationship.Kind determineRelKind(ShadowMunger munger) {
        AdviceKind ak = ((Advice)munger).getKind();
        if (ak.getKey() == AdviceKind.Before.getKey()) {
            return IRelationship.Kind.ADVICE_BEFORE;
        }
        if (ak.getKey() == AdviceKind.After.getKey()) {
            return IRelationship.Kind.ADVICE_AFTER;
        }
        if (ak.getKey() == AdviceKind.AfterThrowing.getKey()) {
            return IRelationship.Kind.ADVICE_AFTERTHROWING;
        }
        if (ak.getKey() == AdviceKind.AfterReturning.getKey()) {
            return IRelationship.Kind.ADVICE_AFTERRETURNING;
        }
        if (ak.getKey() == AdviceKind.Around.getKey()) {
            return IRelationship.Kind.ADVICE_AROUND;
        }
        if (ak.getKey() == AdviceKind.CflowEntry.getKey() || ak.getKey() == AdviceKind.CflowBelowEntry.getKey() || ak.getKey() == AdviceKind.InterInitializer.getKey() || ak.getKey() == AdviceKind.PerCflowEntry.getKey() || ak.getKey() == AdviceKind.PerCflowBelowEntry.getKey() || ak.getKey() == AdviceKind.PerThisEntry.getKey() || ak.getKey() == AdviceKind.PerTargetEntry.getKey() || ak.getKey() == AdviceKind.Softener.getKey() || ak.getKey() == AdviceKind.PerTypeWithinEntry.getKey()) {
            return null;
        }
        throw new RuntimeException("Shadow.determineRelKind: What the hell is it? " + ak);
    }

    @Override
    public void reportMatch(ShadowMunger munger, Shadow shadow) {
        if (this.getCrossReferenceHandler() != null) {
            this.getCrossReferenceHandler().addCrossReference(munger.getSourceLocation(), shadow.getSourceLocation(), this.determineRelKind(munger).getName(), ((Advice)munger).hasDynamicTests());
        }
        if (!this.getMessageHandler().isIgnoring(IMessage.WEAVEINFO)) {
            this.reportWeavingMessage(munger, shadow);
        }
        if (this.getModel() != null) {
            AsmRelationshipProvider.addAdvisedRelationship(this.getModelAsAsmManager(), shadow, munger);
        }
    }

    private void reportWeavingMessage(ShadowMunger munger, Shadow shadow) {
        Advice advice = (Advice)munger;
        AdviceKind aKind = advice.getKind();
        if (aKind == null || advice.getConcreteAspect() == null) {
            return;
        }
        if (!(aKind.equals(AdviceKind.Before) || aKind.equals(AdviceKind.After) || aKind.equals(AdviceKind.AfterReturning) || aKind.equals(AdviceKind.AfterThrowing) || aKind.equals(AdviceKind.Around) || aKind.equals(AdviceKind.Softener))) {
            return;
        }
        if (shadow.getKind() == Shadow.SynchronizationUnlock) {
            if (advice.lastReportedMonitorExitJoinpointLocation == null) {
                advice.lastReportedMonitorExitJoinpointLocation = shadow.getSourceLocation();
            } else {
                if (this.areTheSame(shadow.getSourceLocation(), advice.lastReportedMonitorExitJoinpointLocation)) {
                    advice.lastReportedMonitorExitJoinpointLocation = null;
                    return;
                }
                advice.lastReportedMonitorExitJoinpointLocation = shadow.getSourceLocation();
            }
        }
        String description = advice.getKind().toString();
        String advisedType = shadow.getEnclosingType().getName();
        String advisingType = advice.getConcreteAspect().getName();
        WeaveMessage msg = null;
        if (advice.getKind().equals(AdviceKind.Softener)) {
            msg = WeaveMessage.constructWeavingMessage(WeaveMessage.WEAVEMESSAGE_SOFTENS, new String[]{advisedType, this.beautifyLocation(shadow.getSourceLocation()), advisingType, this.beautifyLocation(munger.getSourceLocation())}, advisedType, advisingType);
        } else {
            boolean runtimeTest = advice.hasDynamicTests();
            String joinPointDescription = shadow.toString();
            msg = WeaveMessage.constructWeavingMessage(WeaveMessage.WEAVEMESSAGE_ADVISES, new String[]{joinPointDescription, advisedType, this.beautifyLocation(shadow.getSourceLocation()), description, advisingType, this.beautifyLocation(munger.getSourceLocation()), runtimeTest ? " [with runtime test]" : ""}, advisedType, advisingType);
        }
        this.getMessageHandler().handleMessage(msg);
    }

    private boolean areTheSame(ISourceLocation locA, ISourceLocation locB) {
        if (locA == null) {
            return locB == null;
        }
        if (locB == null) {
            return false;
        }
        if (locA.getLine() != locB.getLine()) {
            return false;
        }
        File fA = locA.getSourceFile();
        File fB = locA.getSourceFile();
        if (fA == null) {
            return fB == null;
        }
        if (fB == null) {
            return false;
        }
        return fA.getName().equals(fB.getName());
    }

    private String beautifyLocation(ISourceLocation isl) {
        StringBuffer nice = new StringBuffer();
        if (isl == null || isl.getSourceFile() == null || isl.getSourceFile().getName().indexOf("no debug info available") != -1) {
            nice.append("no debug info available");
        } else {
            String pathToBinaryLoc;
            int binary;
            int takeFrom = isl.getSourceFile().getPath().lastIndexOf(47);
            if (takeFrom == -1) {
                takeFrom = isl.getSourceFile().getPath().lastIndexOf(92);
            }
            if ((binary = isl.getSourceFile().getPath().lastIndexOf(33)) != -1 && binary < takeFrom && (pathToBinaryLoc = isl.getSourceFile().getPath().substring(0, binary + 1)).indexOf(".jar") != -1) {
                int lastSlash = pathToBinaryLoc.lastIndexOf(47);
                if (lastSlash == -1) {
                    lastSlash = pathToBinaryLoc.lastIndexOf(92);
                }
                nice.append(pathToBinaryLoc.substring(lastSlash + 1));
            }
            nice.append(isl.getSourceFile().getPath().substring(takeFrom + 1));
            if (isl.getLine() != 0) {
                nice.append(":").append(isl.getLine());
            }
            if (isl.getSourceFileName() != null) {
                nice.append("(from " + isl.getSourceFileName() + ")");
            }
        }
        return nice.toString();
    }

    private static List<String> makeDefaultClasspath(String cp) {
        ArrayList<String> classPath = new ArrayList<String>();
        classPath.addAll(BcelWorld.getPathEntries(cp));
        classPath.addAll(BcelWorld.getPathEntries(ClassPath.getClassPath()));
        return classPath;
    }

    private static List<String> getPathEntries(String s) {
        ArrayList<String> ret = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(s, File.pathSeparator);
        while (tok.hasMoreTokens()) {
            ret.add(tok.nextToken());
        }
        return ret;
    }

    public BcelWorld(List classPath, IMessageHandler handler, ICrossReferenceHandler xrefHandler) {
        this.classPath = new ClassPathManager(classPath, handler);
        this.setMessageHandler(handler);
        this.setCrossReferenceHandler(xrefHandler);
        this.delegate = this;
    }

    public BcelWorld(ClassPathManager cpm, IMessageHandler handler, ICrossReferenceHandler xrefHandler) {
        this.classPath = cpm;
        this.setMessageHandler(handler);
        this.setCrossReferenceHandler(xrefHandler);
        this.delegate = this;
    }

    public BcelWorld(ClassLoader loader, IMessageHandler handler, ICrossReferenceHandler xrefHandler) {
        this.classPath = null;
        this.loaderRef = new BcelWeakClassLoaderReference(loader);
        this.setMessageHandler(handler);
        this.setCrossReferenceHandler(xrefHandler);
    }

    public void ensureRepositorySetup() {
        if (this.delegate == null) {
            this.delegate = this.getClassLoaderRepositoryFor(this.loaderRef);
        }
    }

    public Repository getClassLoaderRepositoryFor(ClassLoaderReference loader) {
        if (this.bcelRepositoryCaching) {
            return new ClassLoaderRepository(loader);
        }
        return new NonCachingClassLoaderRepository(loader);
    }

    public void addPath(String name) {
        this.classPath.addPath(name, this.getMessageHandler());
    }

    public static Type makeBcelType(UnresolvedType type) {
        return Type.getType(type.getErasureSignature());
    }

    static Type[] makeBcelTypes(UnresolvedType[] types) {
        Type[] ret = new Type[types.length];
        int len = types.length;
        for (int i = 0; i < len; ++i) {
            ret[i] = BcelWorld.makeBcelType(types[i]);
        }
        return ret;
    }

    public static Type[] makeBcelTypes(String[] types) {
        if (types == null || types.length == 0) {
            return null;
        }
        Type[] ret = new Type[types.length];
        int len = types.length;
        for (int i = 0; i < len; ++i) {
            ret[i] = BcelWorld.makeBcelType(types[i]);
        }
        return ret;
    }

    public static Type makeBcelType(String type) {
        return Type.getType(type);
    }

    static String[] makeBcelTypesAsClassNames(UnresolvedType[] types) {
        String[] ret = new String[types.length];
        int len = types.length;
        for (int i = 0; i < len; ++i) {
            ret[i] = types[i].getName();
        }
        return ret;
    }

    public static UnresolvedType fromBcel(Type t) {
        return UnresolvedType.forSignature(t.getSignature());
    }

    static UnresolvedType[] fromBcel(Type[] ts) {
        UnresolvedType[] ret = new UnresolvedType[ts.length];
        int len = ts.length;
        for (int i = 0; i < len; ++i) {
            ret[i] = BcelWorld.fromBcel(ts[i]);
        }
        return ret;
    }

    public ResolvedType resolve(Type t) {
        return this.resolve(BcelWorld.fromBcel(t));
    }

    @Override
    protected ReferenceTypeDelegate resolveDelegate(ReferenceType ty) {
        String name = ty.getName();
        this.ensureAdvancedConfigurationProcessed();
        JavaClass jc = this.lookupJavaClass(this.classPath, name);
        if (jc == null) {
            if (this.typeDelegateResolvers != null) {
                for (TypeDelegateResolver tdr : this.typeDelegateResolvers) {
                    ReferenceTypeDelegate delegate = tdr.getDelegate(ty);
                    if (delegate == null) continue;
                    return delegate;
                }
            }
            return null;
        }
        return this.buildBcelDelegate(ty, jc, false, false);
    }

    public BcelObjectType buildBcelDelegate(ReferenceType type, JavaClass jc, boolean artificial, boolean exposedToWeaver) {
        BcelObjectType ret = new BcelObjectType(type, jc, artificial, exposedToWeaver);
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JavaClass lookupJavaClass(ClassPathManager classPath, String name) {
        if (classPath == null) {
            try {
                this.ensureRepositorySetup();
                JavaClass jc = this.delegate.loadClass(name);
                if (trace.isTraceEnabled()) {
                    trace.event("lookupJavaClass", (Object)this, new Object[]{name, jc});
                }
                return jc;
            }
            catch (ClassNotFoundException e) {
                if (trace.isTraceEnabled()) {
                    trace.error("Unable to find class '" + name + "' in repository", e);
                }
                return null;
            }
        }
        try (ClassPathManager.ClassFile file = null;){
            JavaClass jc;
            file = classPath.find(UnresolvedType.forName(name));
            if (file == null) {
                JavaClass javaClass = null;
                return javaClass;
            }
            ClassParser parser = new ClassParser(file.getInputStream(), file.getPath());
            JavaClass javaClass = jc = parser.parse();
            return javaClass;
        }
    }

    public BcelObjectType addSourceObjectType(JavaClass jc, boolean artificial) {
        return this.addSourceObjectType(jc.getClassName(), jc, artificial);
    }

    public BcelObjectType addSourceObjectType(String classname, JavaClass jc, boolean artificial) {
        BcelObjectType ret = null;
        if (!jc.getClassName().equals(classname)) {
            throw new RuntimeException(jc.getClassName() + "!=" + classname);
        }
        String signature = UnresolvedType.forName(jc.getClassName()).getSignature();
        ResolvedType resolvedTypeFromTypeMap = this.typeMap.get(signature);
        if (resolvedTypeFromTypeMap != null && !(resolvedTypeFromTypeMap instanceof ReferenceType)) {
            StringBuffer exceptionText = new StringBuffer();
            exceptionText.append("Found invalid (not a ReferenceType) entry in the type map. ");
            exceptionText.append("Signature=[" + signature + "] Found=[" + resolvedTypeFromTypeMap + "] Class=[" + resolvedTypeFromTypeMap.getClass() + "]");
            throw new BCException(exceptionText.toString());
        }
        ReferenceType referenceTypeFromTypeMap = (ReferenceType)resolvedTypeFromTypeMap;
        if (referenceTypeFromTypeMap == null) {
            if (jc.isGeneric() && this.isInJava5Mode()) {
                ReferenceType rawType = ReferenceType.fromTypeX(UnresolvedType.forRawTypeName(jc.getClassName()), this);
                ret = this.buildBcelDelegate(rawType, jc, artificial, true);
                ReferenceType genericRefType = new ReferenceType(UnresolvedType.forGenericTypeSignature(signature, ret.getDeclaredGenericSignature()), (World)this);
                rawType.setDelegate(ret);
                genericRefType.setDelegate(ret);
                rawType.setGenericType(genericRefType);
                this.typeMap.put(signature, rawType);
            } else {
                referenceTypeFromTypeMap = new ReferenceType(signature, (World)this);
                ret = this.buildBcelDelegate(referenceTypeFromTypeMap, jc, artificial, true);
                this.typeMap.put(signature, referenceTypeFromTypeMap);
            }
        } else {
            ret = this.buildBcelDelegate(referenceTypeFromTypeMap, jc, artificial, true);
        }
        return ret;
    }

    public BcelObjectType addSourceObjectType(String classname, byte[] bytes, boolean artificial) {
        BcelObjectType retval = null;
        String signature = UnresolvedType.forName(classname).getSignature();
        ResolvedType resolvedTypeFromTypeMap = this.typeMap.get(signature);
        if (resolvedTypeFromTypeMap != null && !(resolvedTypeFromTypeMap instanceof ReferenceType)) {
            StringBuffer exceptionText = new StringBuffer();
            exceptionText.append("Found invalid (not a ReferenceType) entry in the type map. ");
            exceptionText.append("Signature=[" + signature + "] Found=[" + resolvedTypeFromTypeMap + "] Class=[" + resolvedTypeFromTypeMap.getClass() + "]");
            throw new BCException(exceptionText.toString());
        }
        ReferenceType referenceTypeFromTypeMap = (ReferenceType)resolvedTypeFromTypeMap;
        if (referenceTypeFromTypeMap == null) {
            JavaClass jc = Utility.makeJavaClass(classname, bytes);
            if (jc.isGeneric() && this.isInJava5Mode()) {
                referenceTypeFromTypeMap = ReferenceType.fromTypeX(UnresolvedType.forRawTypeName(jc.getClassName()), this);
                retval = this.buildBcelDelegate(referenceTypeFromTypeMap, jc, artificial, true);
                ReferenceType genericRefType = new ReferenceType(UnresolvedType.forGenericTypeSignature(signature, retval.getDeclaredGenericSignature()), (World)this);
                referenceTypeFromTypeMap.setDelegate(retval);
                genericRefType.setDelegate(retval);
                referenceTypeFromTypeMap.setGenericType(genericRefType);
                this.typeMap.put(signature, referenceTypeFromTypeMap);
            } else {
                referenceTypeFromTypeMap = new ReferenceType(signature, (World)this);
                retval = this.buildBcelDelegate(referenceTypeFromTypeMap, jc, artificial, true);
                this.typeMap.put(signature, referenceTypeFromTypeMap);
            }
        } else {
            ReferenceTypeDelegate existingDelegate = referenceTypeFromTypeMap.getDelegate();
            if (!(existingDelegate instanceof BcelObjectType)) {
                throw new IllegalStateException("For " + classname + " should be BcelObjectType, but is " + existingDelegate.getClass());
            }
            retval = (BcelObjectType)existingDelegate;
            retval = this.buildBcelDelegate(referenceTypeFromTypeMap, Utility.makeJavaClass(classname, bytes), artificial, true);
        }
        return retval;
    }

    void deleteSourceObjectType(UnresolvedType ty) {
        this.typeMap.remove(ty.getSignature());
    }

    public static Member makeFieldJoinPointSignature(LazyClassGen cg, FieldInstruction fi) {
        ConstantPool cpg = cg.getConstantPool();
        return MemberImpl.field(fi.getClassName(cpg), fi.opcode == 178 || fi.opcode == 179 ? 8 : 0, fi.getName(cpg), fi.getSignature(cpg));
    }

    public Member makeJoinPointSignatureFromMethod(LazyMethodGen mg, MemberKind kind) {
        BcelMethod ret = mg.getMemberView();
        if (ret == null) {
            int mods = mg.getAccessFlags();
            if (mg.getEnclosingClass().isInterface()) {
                mods |= 0x200;
            }
            return new ResolvedMemberImpl(kind, UnresolvedType.forName(mg.getClassName()), mods, BcelWorld.fromBcel(mg.getReturnType()), mg.getName(), BcelWorld.fromBcel(mg.getArgumentTypes()));
        }
        return ret;
    }

    public Member makeJoinPointSignatureForMonitorEnter(LazyClassGen cg, InstructionHandle h) {
        return MemberImpl.monitorEnter();
    }

    public Member makeJoinPointSignatureForMonitorExit(LazyClassGen cg, InstructionHandle h) {
        return MemberImpl.monitorExit();
    }

    public Member makeJoinPointSignatureForArrayConstruction(LazyClassGen cg, InstructionHandle handle) {
        Instruction i = handle.getInstruction();
        ConstantPool cpg = cg.getConstantPool();
        MemberImpl retval = null;
        if (i.opcode == 189) {
            Type ot = i.getType(cpg);
            UnresolvedType ut = BcelWorld.fromBcel(ot);
            ut = UnresolvedType.makeArray(ut, 1);
            retval = MemberImpl.method(ut, 1, UnresolvedType.VOID, "<init>", new ResolvedType[]{this.INT});
        } else if (i instanceof MULTIANEWARRAY) {
            MULTIANEWARRAY arrayInstruction = (MULTIANEWARRAY)i;
            UnresolvedType ut = null;
            int dimensions = arrayInstruction.getDimensions();
            ObjectType ot = arrayInstruction.getLoadClassType(cpg);
            if (ot != null) {
                ut = BcelWorld.fromBcel(ot);
                ut = UnresolvedType.makeArray(ut, dimensions);
            } else {
                Type t = arrayInstruction.getType(cpg);
                ut = BcelWorld.fromBcel(t);
            }
            UnresolvedType[] parms = new ResolvedType[dimensions];
            for (int ii = 0; ii < dimensions; ++ii) {
                parms[ii] = this.INT;
            }
            retval = MemberImpl.method(ut, 1, UnresolvedType.VOID, "<init>", parms);
        } else if (i.opcode == 188) {
            Type ot = i.getType();
            UnresolvedType ut = BcelWorld.fromBcel(ot);
            retval = MemberImpl.method(ut, 1, UnresolvedType.VOID, "<init>", new ResolvedType[]{this.INT});
        } else {
            throw new BCException("Cannot create array construction signature for this non-array instruction:" + i);
        }
        return retval;
    }

    public Member makeJoinPointSignatureForMethodInvocation(LazyClassGen cg, InvokeInstruction ii) {
        int modifier;
        ConstantPool cpg = cg.getConstantPool();
        String name = ii.getName(cpg);
        String declaring = ii.getClassName(cpg);
        UnresolvedType declaringType = null;
        String signature = ii.getSignature(cpg);
        if (name.startsWith("ajc$privMethod$")) {
            try {
                declaringType = UnresolvedType.forName(declaring);
                String typeNameAsFoundInAccessorName = declaringType.getName().replace('.', '_');
                int indexInAccessorName = name.lastIndexOf(typeNameAsFoundInAccessorName);
                if (indexInAccessorName != -1) {
                    ResolvedMember[] methods;
                    String methodName = name.substring(indexInAccessorName + typeNameAsFoundInAccessorName.length() + 1);
                    ResolvedType resolvedDeclaringType = declaringType.resolve(this);
                    for (ResolvedMember method : methods = resolvedDeclaringType.getDeclaredMethods()) {
                        if (!method.getName().equals(methodName) || !method.getSignature().equals(signature) || !Modifier.isPrivate(method.getModifiers())) continue;
                        return method;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        int n = ii instanceof INVOKEINTERFACE ? 512 : (ii.opcode == 184 ? 8 : (modifier = ii.opcode == 183 && !name.equals("<init>") ? 2 : 0));
        if (ii.opcode == 184) {
            ResolvedType appearsDeclaredBy = this.resolve(declaring);
            Iterator<ResolvedMember> iterator = appearsDeclaredBy.getMethods(true, true);
            while (iterator.hasNext()) {
                ResolvedMember method = iterator.next();
                if (!Modifier.isStatic(method.getModifiers()) || !name.equals(method.getName()) || !signature.equals(method.getSignature())) continue;
                declaringType = method.getDeclaringType();
                break;
            }
        }
        if (declaringType == null) {
            declaringType = declaring.charAt(0) == '[' ? UnresolvedType.forSignature(declaring) : UnresolvedType.forName(declaring);
        }
        return MemberImpl.method(declaringType, modifier, name, signature);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("BcelWorld(");
        buf.append(")");
        return buf.toString();
    }

    public static BcelObjectType getBcelObjectType(ResolvedType concreteAspect) {
        if (concreteAspect == null) {
            return null;
        }
        if (!(concreteAspect instanceof ReferenceType)) {
            return null;
        }
        ReferenceTypeDelegate rtDelegate = ((ReferenceType)concreteAspect).getDelegate();
        if (rtDelegate instanceof BcelObjectType) {
            return (BcelObjectType)rtDelegate;
        }
        return null;
    }

    public void tidyUp() {
        this.classPath.closeArchives();
        this.typeMap.report();
        this.typeMap.demote(true);
    }

    @Override
    public JavaClass findClass(String className) {
        return this.lookupJavaClass(this.classPath, className);
    }

    @Override
    public JavaClass loadClass(String className) throws ClassNotFoundException {
        return this.lookupJavaClass(this.classPath, className);
    }

    @Override
    public void storeClass(JavaClass clazz) {
    }

    @Override
    public void removeClass(JavaClass clazz) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public void validateType(UnresolvedType type) {
        ResolvedType result = this.typeMap.get(type.getSignature());
        if (result == null) {
            return;
        }
        if (!result.isExposedToWeaver()) {
            return;
        }
        result.ensureConsistent();
    }

    private boolean applyDeclareParents(DeclareParents p, ResolvedType onType) {
        boolean didSomething = false;
        List<ResolvedType> newParents = p.findMatchingNewParents(onType, true);
        if (!newParents.isEmpty()) {
            didSomething = true;
            BcelObjectType classType = BcelWorld.getBcelObjectType(onType);
            for (ResolvedType newParent : newParents) {
                onType.addParent(newParent);
                NewParentTypeMunger newParentMunger = new NewParentTypeMunger(newParent, p.getDeclaringType());
                newParentMunger.setSourceLocation(p.getSourceLocation());
                onType.addInterTypeMunger(new BcelTypeMunger(newParentMunger, this.getCrosscuttingMembersSet().findAspectDeclaringParents(p)), false);
            }
        }
        return didSomething;
    }

    private boolean applyDeclareAtType(DeclareAnnotation decA, ResolvedType onType, boolean reportProblems) {
        boolean didSomething = false;
        if (decA.matches(onType)) {
            if (onType.hasAnnotation(decA.getAnnotation().getType())) {
                return false;
            }
            AnnotationAJ annoX = decA.getAnnotation();
            boolean isOK = this.checkTargetOK(decA, onType, annoX);
            if (isOK) {
                didSomething = true;
                AnnotationOnTypeMunger newAnnotationTM = new AnnotationOnTypeMunger(annoX);
                newAnnotationTM.setSourceLocation(decA.getSourceLocation());
                onType.addInterTypeMunger(new BcelTypeMunger(newAnnotationTM, decA.getAspect().resolve(this)), false);
                decA.copyAnnotationTo(onType);
            }
        }
        return didSomething;
    }

    private boolean applyDeclareAtField(DeclareAnnotation deca, ResolvedType type) {
        ResolvedMember[] fields;
        boolean changedType = false;
        for (ResolvedMember field : fields = type.getDeclaredFields()) {
            AnnotationAJ anno;
            if (!deca.matches(field, this) || field.hasAnnotation((anno = deca.getAnnotation()).getType())) continue;
            field.addAnnotation(anno);
            changedType = true;
        }
        return changedType;
    }

    private boolean checkTargetOK(DeclareAnnotation decA, ResolvedType onType, AnnotationAJ annoX) {
        return !annoX.specifiesTarget() || (!onType.isAnnotation() || annoX.allowedOnAnnotationType()) && annoX.allowedOnRegularType();
    }

    protected void weaveInterTypeDeclarations(ResolvedType onType) {
        boolean typeChanged;
        List<DeclareParents> declareParentsList = this.getCrosscuttingMembersSet().getDeclareParents();
        if (onType.isRawType()) {
            onType = onType.getGenericType();
        }
        onType.clearInterTypeMungers();
        ArrayList<DeclareParents> decpToRepeat = new ArrayList<DeclareParents>();
        boolean aParentChangeOccurred = false;
        boolean anAnnotationChangeOccurred = false;
        for (DeclareParents decp : declareParentsList) {
            typeChanged = this.applyDeclareParents(decp, onType);
            if (typeChanged) {
                aParentChangeOccurred = true;
                continue;
            }
            if (decp.getChild().isStarAnnotation()) continue;
            decpToRepeat.add(decp);
        }
        for (DeclareAnnotation decA : this.getCrosscuttingMembersSet().getDeclareAnnotationOnTypes()) {
            typeChanged = this.applyDeclareAtType(decA, onType, true);
            if (!typeChanged) continue;
            anAnnotationChangeOccurred = true;
        }
        for (DeclareAnnotation deca : this.getCrosscuttingMembersSet().getDeclareAnnotationOnFields()) {
            if (!this.applyDeclareAtField(deca, onType)) continue;
            anAnnotationChangeOccurred = true;
        }
        while ((aParentChangeOccurred || anAnnotationChangeOccurred) && !decpToRepeat.isEmpty()) {
            aParentChangeOccurred = false;
            anAnnotationChangeOccurred = false;
            ArrayList<DeclareParents> decpToRepeatNextTime = new ArrayList<DeclareParents>();
            for (DeclareParents decp : decpToRepeat) {
                if (this.applyDeclareParents(decp, onType)) {
                    aParentChangeOccurred = true;
                    continue;
                }
                decpToRepeatNextTime.add(decp);
            }
            for (DeclareAnnotation deca : this.getCrosscuttingMembersSet().getDeclareAnnotationOnTypes()) {
                if (!this.applyDeclareAtType(deca, onType, false)) continue;
                anAnnotationChangeOccurred = true;
            }
            for (DeclareAnnotation deca : this.getCrosscuttingMembersSet().getDeclareAnnotationOnFields()) {
                if (!this.applyDeclareAtField(deca, onType)) continue;
                anAnnotationChangeOccurred = true;
            }
            decpToRepeat = decpToRepeatNextTime;
        }
    }

    @Override
    public IWeavingSupport getWeavingSupport() {
        return this.bcelWeavingSupport;
    }

    @Override
    public void reportCheckerMatch(Checker checker, Shadow shadow) {
        Message iMessage = new Message(checker.getMessage(shadow), shadow.toString(), checker.isError() ? IMessage.ERROR : IMessage.WARNING, shadow.getSourceLocation(), null, new ISourceLocation[]{checker.getSourceLocation()}, true, 0, -1, -1);
        this.getMessageHandler().handleMessage(iMessage);
        if (this.getCrossReferenceHandler() != null) {
            this.getCrossReferenceHandler().addCrossReference(checker.getSourceLocation(), shadow.getSourceLocation(), checker.isError() ? IRelationship.Kind.DECLARE_ERROR.getName() : IRelationship.Kind.DECLARE_WARNING.getName(), false);
        }
        if (this.getModel() != null) {
            AsmRelationshipProvider.addDeclareErrorOrWarningRelationship(this.getModelAsAsmManager(), shadow, checker);
        }
    }

    public AsmManager getModelAsAsmManager() {
        return (AsmManager)this.getModel();
    }

    void raiseError(String message) {
        this.getMessageHandler().handleMessage(MessageUtil.error(message));
    }

    public void setXmlFiles(List<File> xmlFiles) {
        if (!this.isXmlConfiguredWorld && !xmlFiles.isEmpty()) {
            this.raiseError("xml configuration files only supported by the compiler when -xmlConfigured option specified");
            return;
        }
        if (!xmlFiles.isEmpty()) {
            this.xmlConfiguration = new WeavingXmlConfig(this, 1);
        }
        for (File xmlfile : xmlFiles) {
            try {
                Definition d = DocumentParser.parse(xmlfile.toURI().toURL());
                this.xmlConfiguration.add(d);
            }
            catch (MalformedURLException e) {
                this.raiseError("Unexpected problem processing XML config file '" + xmlfile.getName() + "' :" + e.getMessage());
            }
            catch (Exception e) {
                this.raiseError("Unexpected problem processing XML config file '" + xmlfile.getName() + "' :" + e.getMessage());
            }
        }
    }

    public void addScopedAspect(String name, String scope) {
        this.isXmlConfiguredWorld = true;
        if (this.xmlConfiguration == null) {
            this.xmlConfiguration = new WeavingXmlConfig(this, 2);
        }
        this.xmlConfiguration.addScopedAspect(name, scope);
    }

    public void setXmlConfigured(boolean b) {
        this.isXmlConfiguredWorld = b;
    }

    @Override
    public boolean isXmlConfigured() {
        return this.isXmlConfiguredWorld && this.xmlConfiguration != null;
    }

    public WeavingXmlConfig getXmlConfiguration() {
        return this.xmlConfiguration;
    }

    @Override
    public boolean isAspectIncluded(ResolvedType aspectType) {
        if (!this.isXmlConfigured()) {
            return true;
        }
        return this.xmlConfiguration.specifiesInclusionOfAspect(aspectType.getName());
    }

    @Override
    public TypePattern getAspectScope(ResolvedType declaringType) {
        return this.xmlConfiguration.getScopeFor(declaringType.getName());
    }

    @Override
    public boolean hasUnsatisfiedDependency(ResolvedType aspectType) {
        String aspectName = aspectType.getName();
        if (aspectType.hasAnnotations()) {
            AnnotationAJ[] annos;
            for (AnnotationAJ anno : annos = aspectType.getAnnotations()) {
                if (!anno.getTypeName().equals("org.aspectj.lang.annotation.RequiredTypes")) continue;
                String values = anno.getStringFormOfValue("value");
                if (values != null && values.length() > 2) {
                    values = values.substring(1, values.length() - 1);
                    StringTokenizer tokenizer = new StringTokenizer(values, ",");
                    boolean anythingMissing = false;
                    while (tokenizer.hasMoreElements()) {
                        String requiredTypeName = tokenizer.nextToken();
                        ResolvedType rt = this.resolve(UnresolvedType.forName(requiredTypeName));
                        if (!rt.isMissing()) continue;
                        if (!this.getMessageHandler().isIgnoring(IMessage.INFO)) {
                            this.getMessageHandler().handleMessage(MessageUtil.info("deactivating aspect '" + aspectName + "' as it requires type '" + requiredTypeName + "' which cannot be found on the classpath"));
                        }
                        anythingMissing = true;
                        if (this.aspectRequiredTypes == null) {
                            this.aspectRequiredTypes = new HashMap<String, String>();
                        }
                        this.aspectRequiredTypes.put(aspectName, requiredTypeName);
                    }
                    return anythingMissing;
                }
                return false;
            }
        }
        if (this.aspectRequiredTypes == null) {
            return false;
        }
        if (!this.aspectRequiredTypesProcessed.contains(aspectName)) {
            String requiredTypeName = this.aspectRequiredTypes.get(aspectName);
            if (requiredTypeName == null) {
                this.aspectRequiredTypesProcessed.add(aspectName);
                return false;
            }
            ResolvedType rt = this.resolve(UnresolvedType.forName(requiredTypeName));
            if (!rt.isMissing()) {
                this.aspectRequiredTypesProcessed.add(aspectName);
                this.aspectRequiredTypes.remove(aspectName);
                return false;
            }
            if (!this.getMessageHandler().isIgnoring(IMessage.INFO)) {
                this.getMessageHandler().handleMessage(MessageUtil.info("deactivating aspect '" + aspectName + "' as it requires type '" + requiredTypeName + "' which cannot be found on the classpath"));
            }
            this.aspectRequiredTypesProcessed.add(aspectName);
            return true;
        }
        return this.aspectRequiredTypes.containsKey(aspectName);
    }

    public void addAspectRequires(String aspectClassName, String requiredType) {
        if (this.aspectRequiredTypes == null) {
            this.aspectRequiredTypes = new HashMap<String, String>();
        }
        this.aspectRequiredTypes.put(aspectClassName, requiredType);
    }

    @Override
    public World.TypeMap getTypeMap() {
        return this.typeMap;
    }

    @Override
    public boolean isLoadtimeWeaving() {
        return false;
    }

    public void addTypeDelegateResolver(TypeDelegateResolver typeDelegateResolver) {
        if (this.typeDelegateResolvers == null) {
            this.typeDelegateResolvers = new ArrayList<TypeDelegateResolver>();
        }
        this.typeDelegateResolvers.add(typeDelegateResolver);
    }

    @Override
    public void classWriteEvent(char[][] compoundName) {
        this.typeMap.classWriteEvent(new String(CharOperation.concatWith(compoundName, '.')));
    }

    public void demote(ResolvedType type) {
        this.typeMap.demote(type);
    }

    static class WeavingXmlConfig {
        static final int MODE_COMPILE = 1;
        static final int MODE_LTW = 2;
        private int mode;
        private boolean initialized = false;
        private List<Definition> definitions = new ArrayList<Definition>();
        private List<String> resolvedIncludedAspects = new ArrayList<String>();
        private Map<String, TypePattern> scopes = new HashMap<String, TypePattern>();
        private List<String> includedFastMatchPatterns = Collections.emptyList();
        private List<TypePattern> includedPatterns = Collections.emptyList();
        private List<String> excludedFastMatchPatterns = Collections.emptyList();
        private List<TypePattern> excludedPatterns = Collections.emptyList();
        private BcelWorld world;

        public WeavingXmlConfig(BcelWorld bcelWorld, int mode) {
            this.world = bcelWorld;
            this.mode = mode;
        }

        public void add(Definition d) {
            this.definitions.add(d);
        }

        public void addScopedAspect(String aspectName, String scope) {
            this.ensureInitialized();
            this.resolvedIncludedAspects.add(aspectName);
            try {
                TypePattern scopePattern = new PatternParser(scope).parseTypePattern();
                scopePattern.resolve(this.world);
                this.scopes.put(aspectName, scopePattern);
                if (!this.world.getMessageHandler().isIgnoring(IMessage.INFO)) {
                    this.world.getMessageHandler().handleMessage(MessageUtil.info("Aspect '" + aspectName + "' is scoped to apply against types matching pattern '" + scopePattern.toString() + "'"));
                }
            }
            catch (Exception e) {
                this.world.getMessageHandler().handleMessage(MessageUtil.error("Unable to parse scope as type pattern.  Scope was '" + scope + "': " + e.getMessage()));
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void ensureInitialized() {
            if (!this.initialized) {
                try {
                    this.resolvedIncludedAspects = new ArrayList<String>();
                    for (Definition definition : this.definitions) {
                        List<String> aspectNames = definition.getAspectClassNames();
                        for (String string : aspectNames) {
                            this.resolvedIncludedAspects.add(string);
                            String scope = definition.getScopeForAspect(string);
                            if (scope == null) continue;
                            try {
                                TypePattern scopePattern = new PatternParser(scope).parseTypePattern();
                                scopePattern.resolve(this.world);
                                this.scopes.put(string, scopePattern);
                                if (this.world.getMessageHandler().isIgnoring(IMessage.INFO)) continue;
                                this.world.getMessageHandler().handleMessage(MessageUtil.info("Aspect '" + string + "' is scoped to apply against types matching pattern '" + scopePattern.toString() + "'"));
                            }
                            catch (Exception e) {
                                this.world.getMessageHandler().handleMessage(MessageUtil.error("Unable to parse scope as type pattern.  Scope was '" + scope + "': " + e.getMessage()));
                            }
                        }
                        try {
                            List<String> includePatterns = definition.getIncludePatterns();
                            if (includePatterns.size() > 0) {
                                this.includedPatterns = new ArrayList<TypePattern>();
                                this.includedFastMatchPatterns = new ArrayList<String>();
                            }
                            for (String includePattern : includePatterns) {
                                if (includePattern.endsWith("..*")) {
                                    this.includedFastMatchPatterns.add(includePattern.substring(0, includePattern.length() - 2));
                                    continue;
                                }
                                TypePattern includedPattern = new PatternParser(includePattern).parseTypePattern();
                                this.includedPatterns.add(includedPattern);
                            }
                            List<String> list = definition.getExcludePatterns();
                            if (list.size() > 0) {
                                this.excludedPatterns = new ArrayList<TypePattern>();
                                this.excludedFastMatchPatterns = new ArrayList<String>();
                            }
                            for (String excludePattern : list) {
                                if (excludePattern.endsWith("..*")) {
                                    this.excludedFastMatchPatterns.add(excludePattern.substring(0, excludePattern.length() - 2));
                                    continue;
                                }
                                TypePattern excludedPattern = new PatternParser(excludePattern).parseTypePattern();
                                this.excludedPatterns.add(excludedPattern);
                            }
                        }
                        catch (ParserException pe) {
                            this.world.getMessageHandler().handleMessage(MessageUtil.error("Unable to parse type pattern: " + pe.getMessage()));
                        }
                    }
                }
                finally {
                    this.initialized = true;
                }
            }
        }

        public boolean specifiesInclusionOfAspect(String name) {
            this.ensureInitialized();
            return this.resolvedIncludedAspects.contains(name);
        }

        public TypePattern getScopeFor(String name) {
            return this.scopes.get(name);
        }

        public boolean excludesType(ResolvedType type) {
            if (this.mode == 2) {
                return false;
            }
            String typename = type.getName();
            boolean excluded = false;
            for (String string : this.excludedFastMatchPatterns) {
                if (!typename.startsWith(string)) continue;
                excluded = true;
                break;
            }
            if (!excluded) {
                for (TypePattern typePattern : this.excludedPatterns) {
                    if (!typePattern.matchesStatically(type)) continue;
                    excluded = true;
                    break;
                }
            }
            return excluded;
        }
    }
}

