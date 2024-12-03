/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.AttributeUtils;
import org.aspectj.apache.bcel.classfile.ConstantClass;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.EnclosingMethod;
import org.aspectj.apache.bcel.classfile.Field;
import org.aspectj.apache.bcel.classfile.InnerClass;
import org.aspectj.apache.bcel.classfile.InnerClasses;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.classfile.Method;
import org.aspectj.apache.bcel.classfile.Signature;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.EnumElementValue;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.aspectj.asm.AsmManager;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.GenericSignature;
import org.aspectj.weaver.AbstractReferenceTypeDelegate;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.AnnotationTargetKind;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.BindingScope;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.SourceContextImpl;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverStateInfo;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.AtAjAttributes;
import org.aspectj.weaver.bcel.BcelAnnotation;
import org.aspectj.weaver.bcel.BcelConstantPoolReader;
import org.aspectj.weaver.bcel.BcelField;
import org.aspectj.weaver.bcel.BcelGenericSignatureToTypeXConverter;
import org.aspectj.weaver.bcel.BcelMethod;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.DeclareErrorOrWarning;
import org.aspectj.weaver.patterns.DeclarePrecedence;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.PerClause;

public class BcelObjectType
extends AbstractReferenceTypeDelegate {
    public JavaClass javaClass;
    private boolean artificial;
    private LazyClassGen lazyClassGen = null;
    private int modifiers;
    private String className;
    private String superclassSignature;
    private String superclassName;
    private String[] interfaceSignatures;
    private ResolvedMember[] fields = null;
    private ResolvedMember[] methods = null;
    private ResolvedType[] annotationTypes = null;
    private AnnotationAJ[] annotations = null;
    private TypeVariable[] typeVars = null;
    private String retentionPolicy;
    private AnnotationTargetKind[] annotationTargetKinds;
    private AjAttribute.WeaverVersionInfo wvInfo = AjAttribute.WeaverVersionInfo.UNKNOWN;
    private ResolvedPointcutDefinition[] pointcuts = null;
    private ResolvedMember[] privilegedAccess = null;
    private WeaverStateInfo weaverState = null;
    private PerClause perClause = null;
    private List<ConcreteTypeMunger> typeMungers = Collections.emptyList();
    private List<Declare> declares = Collections.emptyList();
    private GenericSignature.FormalTypeParameter[] formalsForResolution = null;
    private String declaredSignature = null;
    private boolean hasBeenWoven = false;
    private boolean isGenericType = false;
    private boolean isInterface;
    private boolean isEnum;
    private boolean isAnnotation;
    private boolean isAnonymous;
    private boolean isNested;
    private boolean isObject = false;
    private boolean isAnnotationStyleAspect = false;
    private boolean isCodeStyleAspect = false;
    private WeakReference<ResolvedType> superTypeReference = new WeakReference<Object>(null);
    private WeakReference<ResolvedType[]> superInterfaceReferences = new WeakReference<Object>(null);
    private int bitflag = 0;
    private static final int DISCOVERED_ANNOTATION_RETENTION_POLICY = 1;
    private static final int UNPACKED_GENERIC_SIGNATURE = 2;
    private static final int UNPACKED_AJATTRIBUTES = 4;
    private static final int DISCOVERED_ANNOTATION_TARGET_KINDS = 8;
    private static final int DISCOVERED_DECLARED_SIGNATURE = 16;
    private static final int DISCOVERED_WHETHER_ANNOTATION_STYLE = 32;
    private static final int ANNOTATION_UNPACK_IN_PROGRESS = 256;
    private static final String[] NO_INTERFACE_SIGS = new String[0];

    BcelObjectType(ReferenceType resolvedTypeX, JavaClass javaClass, boolean artificial, boolean exposedToWeaver) {
        super(resolvedTypeX, exposedToWeaver);
        this.javaClass = javaClass;
        this.artificial = artificial;
        this.initializeFromJavaclass();
        resolvedTypeX.setDelegate(this);
        ISourceContext sourceContext = resolvedTypeX.getSourceContext();
        if (sourceContext == SourceContextImpl.UNKNOWN_SOURCE_CONTEXT) {
            sourceContext = new SourceContextImpl(this);
            this.setSourceContext(sourceContext);
        }
        this.isObject = javaClass.getSuperclassNameIndex() == 0;
        this.ensureAspectJAttributesUnpacked();
        this.setSourcefilename(javaClass.getSourceFileName());
    }

    public void setJavaClass(JavaClass newclass, boolean artificial) {
        this.javaClass = newclass;
        this.artificial = artificial;
        this.resetState();
        this.initializeFromJavaclass();
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    private void initializeFromJavaclass() {
        this.isInterface = this.javaClass.isInterface();
        this.isEnum = this.javaClass.isEnum();
        this.isAnnotation = this.javaClass.isAnnotation();
        this.isAnonymous = this.javaClass.isAnonymous();
        this.isNested = this.javaClass.isNested();
        this.modifiers = this.javaClass.getModifiers();
        this.superclassName = this.javaClass.getSuperclassName();
        this.className = this.javaClass.getClassName();
        this.cachedGenericClassTypeSignature = null;
    }

    @Override
    public boolean isInterface() {
        return this.isInterface;
    }

    @Override
    public boolean isEnum() {
        return this.isEnum;
    }

    @Override
    public boolean isAnnotation() {
        return this.isAnnotation;
    }

    @Override
    public boolean isAnonymous() {
        return this.isAnonymous;
    }

    @Override
    public boolean isNested() {
        return this.isNested;
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    @Override
    public ResolvedType getSuperclass() {
        if (this.isObject) {
            return null;
        }
        ResolvedType supertype = (ResolvedType)this.superTypeReference.get();
        if (supertype == null) {
            this.ensureGenericSignatureUnpacked();
            if (this.superclassSignature == null) {
                if (this.superclassName == null) {
                    this.superclassName = this.javaClass.getSuperclassName();
                }
                this.superclassSignature = this.getResolvedTypeX().getWorld().resolve(UnresolvedType.forName(this.superclassName)).getSignature();
            }
            World world = this.getResolvedTypeX().getWorld();
            supertype = world.resolve(UnresolvedType.forSignature(this.superclassSignature));
            this.superTypeReference = new WeakReference<ResolvedType>(supertype);
        }
        return supertype;
    }

    public World getWorld() {
        return this.getResolvedTypeX().getWorld();
    }

    @Override
    public ResolvedType[] getDeclaredInterfaces() {
        ResolvedType[] cachedInterfaceTypes = (ResolvedType[])this.superInterfaceReferences.get();
        if (cachedInterfaceTypes == null) {
            this.ensureGenericSignatureUnpacked();
            ResolvedType[] interfaceTypes = null;
            if (this.interfaceSignatures == null) {
                String[] names = this.javaClass.getInterfaceNames();
                if (names.length == 0) {
                    this.interfaceSignatures = NO_INTERFACE_SIGS;
                    interfaceTypes = ResolvedType.NONE;
                } else {
                    this.interfaceSignatures = new String[names.length];
                    interfaceTypes = new ResolvedType[names.length];
                    int len = names.length;
                    for (int i = 0; i < len; ++i) {
                        interfaceTypes[i] = this.getResolvedTypeX().getWorld().resolve(UnresolvedType.forName(names[i]));
                        this.interfaceSignatures[i] = interfaceTypes[i].getSignature();
                    }
                }
            } else {
                interfaceTypes = new ResolvedType[this.interfaceSignatures.length];
                int len = this.interfaceSignatures.length;
                for (int i = 0; i < len; ++i) {
                    interfaceTypes[i] = this.getResolvedTypeX().getWorld().resolve(UnresolvedType.forSignature(this.interfaceSignatures[i]));
                }
            }
            this.superInterfaceReferences = new WeakReference<ResolvedType[]>(interfaceTypes);
            return interfaceTypes;
        }
        return cachedInterfaceTypes;
    }

    @Override
    public ResolvedMember[] getDeclaredMethods() {
        this.ensureGenericSignatureUnpacked();
        if (this.methods == null) {
            Method[] ms = this.javaClass.getMethods();
            ResolvedMember[] newMethods = new ResolvedMember[ms.length];
            for (int i = ms.length - 1; i >= 0; --i) {
                newMethods[i] = new BcelMethod(this, ms[i]);
            }
            this.methods = newMethods;
        }
        return this.methods;
    }

    @Override
    public ResolvedMember[] getDeclaredFields() {
        this.ensureGenericSignatureUnpacked();
        if (this.fields == null) {
            Field[] fs = this.javaClass.getFields();
            ResolvedMember[] newfields = new ResolvedMember[fs.length];
            int len = fs.length;
            for (int i = 0; i < len; ++i) {
                newfields[i] = new BcelField(this, fs[i]);
            }
            this.fields = newfields;
        }
        return this.fields;
    }

    @Override
    public TypeVariable[] getTypeVariables() {
        if (!this.isGeneric()) {
            return TypeVariable.NONE;
        }
        if (this.typeVars == null) {
            GenericSignature.ClassSignature classSig = this.getGenericClassTypeSignature();
            this.typeVars = new TypeVariable[classSig.formalTypeParameters.length];
            for (int i = 0; i < this.typeVars.length; ++i) {
                GenericSignature.FormalTypeParameter ftp = classSig.formalTypeParameters[i];
                try {
                    this.typeVars[i] = BcelGenericSignatureToTypeXConverter.formalTypeParameter2TypeVariable(ftp, classSig.formalTypeParameters, this.getResolvedTypeX().getWorld());
                    continue;
                }
                catch (BcelGenericSignatureToTypeXConverter.GenericSignatureFormatException e) {
                    throw new IllegalStateException("While getting the type variables for type " + this.toString() + " with generic signature " + classSig + " the following error condition was detected: " + e.getMessage());
                }
            }
        }
        return this.typeVars;
    }

    @Override
    public Collection<ConcreteTypeMunger> getTypeMungers() {
        return this.typeMungers;
    }

    @Override
    public Collection<Declare> getDeclares() {
        return this.declares;
    }

    @Override
    public Collection<ResolvedMember> getPrivilegedAccesses() {
        if (this.privilegedAccess == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(this.privilegedAccess);
    }

    @Override
    public ResolvedMember[] getDeclaredPointcuts() {
        return this.pointcuts;
    }

    @Override
    public boolean isAspect() {
        return this.perClause != null;
    }

    @Override
    public boolean isAnnotationStyleAspect() {
        if ((this.bitflag & 0x20) == 0) {
            this.bitflag |= 0x20;
            this.isAnnotationStyleAspect = !this.isCodeStyleAspect && this.hasAnnotation(AjcMemberMaker.ASPECT_ANNOTATION);
        }
        return this.isAnnotationStyleAspect;
    }

    private void ensureAspectJAttributesUnpacked() {
        if ((this.bitflag & 4) != 0) {
            return;
        }
        this.bitflag |= 4;
        IMessageHandler msgHandler = this.getResolvedTypeX().getWorld().getMessageHandler();
        List<AjAttribute> l = null;
        try {
            l = Utility.readAjAttributes(this.className, this.javaClass.getAttributes(), this.getResolvedTypeX().getSourceContext(), this.getResolvedTypeX().getWorld(), AjAttribute.WeaverVersionInfo.UNKNOWN, new BcelConstantPoolReader(this.javaClass.getConstantPool()));
        }
        catch (RuntimeException re) {
            throw new RuntimeException("Problem processing attributes in " + this.javaClass.getFileName(), re);
        }
        ArrayList<ResolvedPointcutDefinition> pointcuts = new ArrayList<ResolvedPointcutDefinition>();
        this.typeMungers = new ArrayList<ConcreteTypeMunger>();
        this.declares = new ArrayList<Declare>();
        this.processAttributes(l, pointcuts, false);
        ReferenceType type = this.getResolvedTypeX();
        AsmManager asmManager = ((BcelWorld)type.getWorld()).getModelAsAsmManager();
        l = AtAjAttributes.readAj5ClassAttributes(asmManager, this.javaClass, type, type.getSourceContext(), msgHandler, this.isCodeStyleAspect);
        AjAttribute.Aspect deferredAspectAttribute = this.processAttributes(l, pointcuts, true);
        this.pointcuts = pointcuts.size() == 0 ? ResolvedPointcutDefinition.NO_POINTCUTS : pointcuts.toArray(new ResolvedPointcutDefinition[pointcuts.size()]);
        this.resolveAnnotationDeclares(l);
        if (deferredAspectAttribute != null) {
            this.perClause = deferredAspectAttribute.reifyFromAtAspectJ(this.getResolvedTypeX());
        }
        if (this.isAspect() && !Modifier.isAbstract(this.getModifiers()) && this.isGeneric()) {
            msgHandler.handleMessage(MessageUtil.error("The generic aspect '" + this.getResolvedTypeX().getName() + "' must be declared abstract", this.getResolvedTypeX().getSourceLocation()));
        }
    }

    private AjAttribute.Aspect processAttributes(List<AjAttribute> attributeList, List<ResolvedPointcutDefinition> pointcuts, boolean fromAnnotations) {
        AjAttribute.Aspect deferredAspectAttribute = null;
        for (AjAttribute a : attributeList) {
            if (a instanceof AjAttribute.Aspect) {
                if (fromAnnotations) {
                    deferredAspectAttribute = (AjAttribute.Aspect)a;
                    continue;
                }
                this.perClause = ((AjAttribute.Aspect)a).reify(this.getResolvedTypeX());
                this.isCodeStyleAspect = true;
                continue;
            }
            if (a instanceof AjAttribute.PointcutDeclarationAttribute) {
                pointcuts.add(((AjAttribute.PointcutDeclarationAttribute)a).reify());
                continue;
            }
            if (a instanceof AjAttribute.WeaverState) {
                this.weaverState = ((AjAttribute.WeaverState)a).reify();
                continue;
            }
            if (a instanceof AjAttribute.TypeMunger) {
                this.typeMungers.add(((AjAttribute.TypeMunger)a).reify(this.getResolvedTypeX().getWorld(), this.getResolvedTypeX()));
                continue;
            }
            if (a instanceof AjAttribute.DeclareAttribute) {
                this.declares.add(((AjAttribute.DeclareAttribute)a).getDeclare());
                continue;
            }
            if (a instanceof AjAttribute.PrivilegedAttribute) {
                AjAttribute.PrivilegedAttribute privAttribute = (AjAttribute.PrivilegedAttribute)a;
                this.privilegedAccess = privAttribute.getAccessedMembers();
                continue;
            }
            if (a instanceof AjAttribute.SourceContextAttribute) {
                if (!(this.getResolvedTypeX().getSourceContext() instanceof SourceContextImpl)) continue;
                AjAttribute.SourceContextAttribute sca = (AjAttribute.SourceContextAttribute)a;
                ((SourceContextImpl)this.getResolvedTypeX().getSourceContext()).configureFromAttribute(sca.getSourceFileName(), sca.getLineBreaks());
                this.setSourcefilename(sca.getSourceFileName());
                continue;
            }
            if (a instanceof AjAttribute.WeaverVersionInfo) {
                this.wvInfo = (AjAttribute.WeaverVersionInfo)a;
                continue;
            }
            throw new BCException("bad attribute " + a);
        }
        return deferredAspectAttribute;
    }

    private void resolveAnnotationDeclares(List<AjAttribute> attributeList) {
        FormalBinding[] bindings = new FormalBinding[]{};
        BindingScope bindingScope = new BindingScope(this.getResolvedTypeX(), this.getResolvedTypeX().getSourceContext(), bindings);
        for (AjAttribute a : attributeList) {
            if (!(a instanceof AjAttribute.DeclareAttribute)) continue;
            Declare decl = ((AjAttribute.DeclareAttribute)a).getDeclare();
            if (decl instanceof DeclareErrorOrWarning) {
                decl.resolve(bindingScope);
                continue;
            }
            if (!(decl instanceof DeclarePrecedence)) continue;
            ((DeclarePrecedence)decl).setScopeForResolution(bindingScope);
        }
    }

    @Override
    public PerClause getPerClause() {
        this.ensureAspectJAttributesUnpacked();
        return this.perClause;
    }

    public JavaClass getJavaClass() {
        return this.javaClass;
    }

    public boolean isArtificial() {
        return this.artificial;
    }

    public void resetState() {
        if (this.javaClass == null) {
            throw new BCException("can't weave evicted type");
        }
        this.bitflag = 0;
        this.annotationTypes = null;
        this.annotations = null;
        this.interfaceSignatures = null;
        this.superclassSignature = null;
        this.superclassName = null;
        this.fields = null;
        this.methods = null;
        this.pointcuts = null;
        this.perClause = null;
        this.weaverState = null;
        this.lazyClassGen = null;
        this.hasBeenWoven = false;
        this.isObject = this.javaClass.getSuperclassNameIndex() == 0;
        this.isAnnotationStyleAspect = false;
        this.ensureAspectJAttributesUnpacked();
    }

    public void finishedWith() {
    }

    @Override
    public WeaverStateInfo getWeaverState() {
        return this.weaverState;
    }

    void setWeaverState(WeaverStateInfo weaverState) {
        this.weaverState = weaverState;
    }

    public void printWackyStuff(PrintStream out) {
        if (this.typeMungers.size() > 0) {
            out.println("  TypeMungers: " + this.typeMungers);
        }
        if (this.declares.size() > 0) {
            out.println("     declares: " + this.declares);
        }
    }

    public LazyClassGen getLazyClassGen() {
        LazyClassGen ret = this.lazyClassGen;
        if (ret == null) {
            ret = new LazyClassGen(this);
            if (this.isAspect()) {
                this.lazyClassGen = ret;
            }
        }
        return ret;
    }

    public boolean isSynthetic() {
        return this.getResolvedTypeX().isSynthetic();
    }

    public AjAttribute.WeaverVersionInfo getWeaverVersionAttribute() {
        return this.wvInfo;
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        this.ensureAnnotationsUnpacked();
        return this.annotationTypes;
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        this.ensureAnnotationsUnpacked();
        return this.annotations;
    }

    @Override
    public boolean hasAnnotations() {
        this.ensureAnnotationsUnpacked();
        return this.annotations.length != 0;
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        if (this.isUnpackingAnnotations()) {
            AnnotationGen[] annos = this.javaClass.getAnnotations();
            if (annos == null || annos.length == 0) {
                return false;
            }
            String lookingForSignature = ofType.getSignature();
            for (int a = 0; a < annos.length; ++a) {
                AnnotationGen annotation = annos[a];
                if (!lookingForSignature.equals(annotation.getTypeSignature())) continue;
                return true;
            }
            return false;
        }
        this.ensureAnnotationsUnpacked();
        int max = this.annotationTypes.length;
        for (int i = 0; i < max; ++i) {
            ResolvedType ax = this.annotationTypes[i];
            if (ax == null) {
                throw new RuntimeException("Annotation entry " + i + " on type " + this.getResolvedTypeX().getName() + " is null!");
            }
            if (!((UnresolvedType)ax).equals(ofType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isAnnotationWithRuntimeRetention() {
        return this.getRetentionPolicy() == null ? false : this.getRetentionPolicy().equals("RUNTIME");
    }

    @Override
    public String getRetentionPolicy() {
        if ((this.bitflag & 1) == 0) {
            this.bitflag |= 1;
            this.retentionPolicy = null;
            if (this.isAnnotation()) {
                this.ensureAnnotationsUnpacked();
                for (int i = this.annotations.length - 1; i >= 0; --i) {
                    List<NameValuePair> values;
                    Iterator<NameValuePair> it;
                    AnnotationAJ ax = this.annotations[i];
                    if (!ax.getTypeName().equals(UnresolvedType.AT_RETENTION.getName()) || !(it = (values = ((BcelAnnotation)ax).getBcelAnnotation().getValues()).iterator()).hasNext()) continue;
                    NameValuePair element = it.next();
                    EnumElementValue v = (EnumElementValue)element.getValue();
                    this.retentionPolicy = v.getEnumValueString();
                    return this.retentionPolicy;
                }
            }
        }
        return this.retentionPolicy;
    }

    @Override
    public boolean canAnnotationTargetType() {
        AnnotationTargetKind[] targetKinds = this.getAnnotationTargetKinds();
        if (targetKinds == null) {
            return true;
        }
        for (int i = 0; i < targetKinds.length; ++i) {
            if (!targetKinds[i].equals(AnnotationTargetKind.TYPE)) continue;
            return true;
        }
        return false;
    }

    @Override
    public AnnotationTargetKind[] getAnnotationTargetKinds() {
        if ((this.bitflag & 8) != 0) {
            return this.annotationTargetKinds;
        }
        this.bitflag |= 8;
        this.annotationTargetKinds = null;
        ArrayList<AnnotationTargetKind> targetKinds = new ArrayList<AnnotationTargetKind>();
        if (this.isAnnotation()) {
            AnnotationAJ[] annotationsOnThisType = this.getAnnotations();
            for (int i = 0; i < annotationsOnThisType.length; ++i) {
                Set<String> targets;
                AnnotationAJ a = annotationsOnThisType[i];
                if (!a.getTypeName().equals(UnresolvedType.AT_TARGET.getName()) || (targets = a.getTargets()) == null) continue;
                for (String targetKind : targets) {
                    if (targetKind.equals("ANNOTATION_TYPE")) {
                        targetKinds.add(AnnotationTargetKind.ANNOTATION_TYPE);
                        continue;
                    }
                    if (targetKind.equals("CONSTRUCTOR")) {
                        targetKinds.add(AnnotationTargetKind.CONSTRUCTOR);
                        continue;
                    }
                    if (targetKind.equals("FIELD")) {
                        targetKinds.add(AnnotationTargetKind.FIELD);
                        continue;
                    }
                    if (targetKind.equals("LOCAL_VARIABLE")) {
                        targetKinds.add(AnnotationTargetKind.LOCAL_VARIABLE);
                        continue;
                    }
                    if (targetKind.equals("METHOD")) {
                        targetKinds.add(AnnotationTargetKind.METHOD);
                        continue;
                    }
                    if (targetKind.equals("PACKAGE")) {
                        targetKinds.add(AnnotationTargetKind.PACKAGE);
                        continue;
                    }
                    if (targetKind.equals("PARAMETER")) {
                        targetKinds.add(AnnotationTargetKind.PARAMETER);
                        continue;
                    }
                    if (!targetKind.equals("TYPE")) continue;
                    targetKinds.add(AnnotationTargetKind.TYPE);
                }
            }
            if (!targetKinds.isEmpty()) {
                this.annotationTargetKinds = new AnnotationTargetKind[targetKinds.size()];
                return targetKinds.toArray(this.annotationTargetKinds);
            }
        }
        return this.annotationTargetKinds;
    }

    private boolean isUnpackingAnnotations() {
        return (this.bitflag & 0x100) != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void ensureAnnotationsUnpacked() {
        if (this.isUnpackingAnnotations()) {
            throw new BCException("Re-entered weaver instance whilst unpacking annotations on " + this.className);
        }
        if (this.annotationTypes == null) {
            try {
                this.bitflag |= 0x100;
                AnnotationGen[] annos = this.javaClass.getAnnotations();
                if (annos == null || annos.length == 0) {
                    this.annotationTypes = ResolvedType.NONE;
                    this.annotations = AnnotationAJ.EMPTY_ARRAY;
                } else {
                    World w = this.getResolvedTypeX().getWorld();
                    this.annotationTypes = new ResolvedType[annos.length];
                    this.annotations = new AnnotationAJ[annos.length];
                    for (int i = 0; i < annos.length; ++i) {
                        AnnotationGen annotation = annos[i];
                        String typeSignature = annotation.getTypeSignature();
                        ResolvedType rType = w.resolve(UnresolvedType.forSignature(typeSignature));
                        if (rType == null) {
                            throw new RuntimeException("Whilst unpacking annotations on '" + this.getResolvedTypeX().getName() + "', failed to resolve type '" + typeSignature + "'");
                        }
                        this.annotationTypes[i] = rType;
                        this.annotations[i] = new BcelAnnotation(annotation, rType);
                    }
                }
            }
            finally {
                this.bitflag &= 0xFFFFFEFF;
            }
        }
    }

    @Override
    public String getDeclaredGenericSignature() {
        this.ensureGenericInfoProcessed();
        return this.declaredSignature;
    }

    private void ensureGenericSignatureUnpacked() {
        ReferenceType genericType;
        if ((this.bitflag & 2) != 0) {
            return;
        }
        this.bitflag |= 2;
        if (!this.getResolvedTypeX().getWorld().isInJava5Mode()) {
            return;
        }
        GenericSignature.ClassSignature cSig = this.getGenericClassTypeSignature();
        if (cSig != null) {
            GenericSignature.FormalTypeParameter[] extraFormals;
            this.formalsForResolution = cSig.formalTypeParameters;
            if (this.isNested() && (extraFormals = this.getFormalTypeParametersFromOuterClass()).length > 0) {
                int i;
                ArrayList<GenericSignature.FormalTypeParameter> allFormals = new ArrayList<GenericSignature.FormalTypeParameter>();
                for (i = 0; i < this.formalsForResolution.length; ++i) {
                    allFormals.add(this.formalsForResolution[i]);
                }
                for (i = 0; i < extraFormals.length; ++i) {
                    allFormals.add(extraFormals[i]);
                }
                this.formalsForResolution = new GenericSignature.FormalTypeParameter[allFormals.size()];
                allFormals.toArray(this.formalsForResolution);
            }
            GenericSignature.ClassTypeSignature superSig = cSig.superclassSignature;
            try {
                ResolvedType rt = BcelGenericSignatureToTypeXConverter.classTypeSignature2TypeX(superSig, this.formalsForResolution, this.getResolvedTypeX().getWorld());
                this.superclassSignature = rt.getSignature();
                this.superclassName = rt.getName();
            }
            catch (BcelGenericSignatureToTypeXConverter.GenericSignatureFormatException e) {
                throw new IllegalStateException("While determining the generic superclass of " + this.className + " with generic signature " + this.getDeclaredGenericSignature() + " the following error was detected: " + e.getMessage());
            }
            if (cSig.superInterfaceSignatures.length == 0) {
                this.interfaceSignatures = NO_INTERFACE_SIGS;
            } else {
                this.interfaceSignatures = new String[cSig.superInterfaceSignatures.length];
                for (int i = 0; i < cSig.superInterfaceSignatures.length; ++i) {
                    try {
                        this.interfaceSignatures[i] = BcelGenericSignatureToTypeXConverter.classTypeSignature2TypeX(cSig.superInterfaceSignatures[i], this.formalsForResolution, this.getResolvedTypeX().getWorld()).getSignature();
                        continue;
                    }
                    catch (BcelGenericSignatureToTypeXConverter.GenericSignatureFormatException e) {
                        throw new IllegalStateException("While determing the generic superinterfaces of " + this.className + " with generic signature " + this.getDeclaredGenericSignature() + " the following error was detected: " + e.getMessage());
                    }
                }
            }
        }
        if (this.isGeneric() && (genericType = this.resolvedTypeX.getGenericType()) != null) {
            genericType.setStartPos(this.resolvedTypeX.getStartPos());
            this.resolvedTypeX = genericType;
        }
    }

    public GenericSignature.FormalTypeParameter[] getAllFormals() {
        this.ensureGenericSignatureUnpacked();
        if (this.formalsForResolution == null) {
            return new GenericSignature.FormalTypeParameter[0];
        }
        return this.formalsForResolution;
    }

    @Override
    public ResolvedType getOuterClass() {
        if (!this.isNested()) {
            throw new IllegalStateException("Can't get the outer class of non-nested type: " + this.className);
        }
        for (Attribute attr : this.javaClass.getAttributes()) {
            if (!(attr instanceof InnerClasses)) continue;
            InnerClass[] innerClss = ((InnerClasses)attr).getInnerClasses();
            ConstantPool cpool = this.javaClass.getConstantPool();
            for (InnerClass innerCls : innerClss) {
                ConstantClass innerClsInfo;
                String innerClsName;
                if (innerCls.getInnerClassIndex() == 0 || innerCls.getOuterClassIndex() == 0 || (innerClsName = cpool.getConstantUtf8((innerClsInfo = (ConstantClass)cpool.getConstant(innerCls.getInnerClassIndex())).getNameIndex()).getValue().replace('/', '.')).compareTo(this.className) != 0) continue;
                ConstantClass outerClsInfo = (ConstantClass)cpool.getConstant(innerCls.getOuterClassIndex());
                String outerClsName = cpool.getConstantUtf8(outerClsInfo.getNameIndex()).getValue().replace('/', '.');
                UnresolvedType outer = UnresolvedType.forName(outerClsName);
                return outer.resolve(this.getResolvedTypeX().getWorld());
            }
        }
        for (Attribute attr : this.javaClass.getAttributes()) {
            EnclosingMethod enclosingMethodAttribute;
            ConstantPool cpool = this.javaClass.getConstantPool();
            if (!(attr instanceof EnclosingMethod) || (enclosingMethodAttribute = (EnclosingMethod)attr).getEnclosingClassIndex() == 0) continue;
            ConstantClass outerClassInfo = enclosingMethodAttribute.getEnclosingClass();
            String outerClassName = cpool.getConstantUtf8(outerClassInfo.getNameIndex()).getValue().replace('/', '.');
            UnresolvedType outer = UnresolvedType.forName(outerClassName);
            return outer.resolve(this.getResolvedTypeX().getWorld());
        }
        int lastDollar = this.className.lastIndexOf(36);
        if (lastDollar == -1) {
            return null;
        }
        String superClassName = this.className.substring(0, lastDollar);
        UnresolvedType outer = UnresolvedType.forName(superClassName);
        return outer.resolve(this.getResolvedTypeX().getWorld());
    }

    private void ensureGenericInfoProcessed() {
        if ((this.bitflag & 0x10) != 0) {
            return;
        }
        this.bitflag |= 0x10;
        Signature sigAttr = AttributeUtils.getSignatureAttribute(this.javaClass.getAttributes());
        String string = this.declaredSignature = sigAttr == null ? null : sigAttr.getSignature();
        if (this.declaredSignature != null) {
            this.isGenericType = this.declaredSignature.charAt(0) == '<';
        }
    }

    @Override
    public boolean isGeneric() {
        this.ensureGenericInfoProcessed();
        return this.isGenericType;
    }

    public String toString() {
        return this.javaClass == null ? "BcelObjectType" : "BcelObjectTypeFor:" + this.className;
    }

    public void evictWeavingState() {
        if (this.getResolvedTypeX().getWorld().couldIncrementalCompileFollow()) {
            return;
        }
        if (this.javaClass != null) {
            int i;
            this.ensureAnnotationsUnpacked();
            this.ensureGenericInfoProcessed();
            this.getDeclaredInterfaces();
            this.getDeclaredFields();
            this.getDeclaredMethods();
            if (this.getResolvedTypeX().getWorld().isXnoInline()) {
                this.lazyClassGen = null;
            }
            if (this.weaverState != null) {
                this.weaverState.setReweavable(false);
                this.weaverState.setUnwovenClassFileData(null);
            }
            for (i = this.methods.length - 1; i >= 0; --i) {
                this.methods[i].evictWeavingState();
            }
            for (i = this.fields.length - 1; i >= 0; --i) {
                this.fields[i].evictWeavingState();
            }
            this.javaClass = null;
            this.artificial = true;
        }
    }

    public void weavingCompleted() {
        this.hasBeenWoven = true;
        if (this.getResolvedTypeX().getWorld().isRunMinimalMemory()) {
            this.evictWeavingState();
        }
        if (this.getSourceContext() != null && !this.getResolvedTypeX().isAspect()) {
            this.getSourceContext().tidy();
        }
    }

    @Override
    public boolean hasBeenWoven() {
        return this.hasBeenWoven;
    }

    @Override
    public boolean copySourceContext() {
        return false;
    }

    public void setExposedToWeaver(boolean b) {
        this.exposedToWeaver = b;
    }

    @Override
    public int getCompilerVersion() {
        return this.wvInfo.getMajorVersion();
    }

    @Override
    public void ensureConsistent() {
        this.superTypeReference.clear();
        this.superInterfaceReferences.clear();
    }

    @Override
    public boolean isWeavable() {
        return true;
    }
}

