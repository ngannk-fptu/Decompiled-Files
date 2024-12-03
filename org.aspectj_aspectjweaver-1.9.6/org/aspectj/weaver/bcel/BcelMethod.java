/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.aspectj.apache.bcel.classfile.AnnotationDefault;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ExceptionTable;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.classfile.LineNumber;
import org.aspectj.apache.bcel.classfile.LineNumberTable;
import org.aspectj.apache.bcel.classfile.LocalVariable;
import org.aspectj.apache.bcel.classfile.LocalVariableTable;
import org.aspectj.apache.bcel.classfile.Method;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.util.GenericSignature;
import org.aspectj.util.GenericSignatureParser;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.MemberKind;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.AtAjAttributes;
import org.aspectj.weaver.bcel.BcelAnnotation;
import org.aspectj.weaver.bcel.BcelConstantPoolReader;
import org.aspectj.weaver.bcel.BcelGenericSignatureToTypeXConverter;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.Utility;

class BcelMethod
extends ResolvedMemberImpl {
    private static final String ASPECTJ_ANNOTATION_PACKAGE = "org.aspectj.lang.annotation";
    private static final char PACKAGE_INITIAL_CHAR = "org.aspectj.lang.annotation".charAt(0);
    private Method method;
    private ShadowMunger associatedShadowMunger;
    private ResolvedPointcutDefinition preResolvedPointcut;
    private AjAttribute.EffectiveSignatureAttribute effectiveSignature;
    private AjAttribute.MethodDeclarationLineNumberAttribute declarationLineNumber;
    private final BcelObjectType bcelObjectType;
    private int bitflags;
    private static final int KNOW_IF_SYNTHETIC = 1;
    private static final int PARAMETER_NAMES_INITIALIZED = 2;
    private static final int CAN_BE_PARAMETERIZED = 4;
    private static final int UNPACKED_GENERIC_SIGNATURE = 8;
    private static final int IS_AJ_SYNTHETIC = 64;
    private static final int IS_SYNTHETIC = 128;
    private static final int IS_SYNTHETIC_INVERSE = 32639;
    private static final int HAS_ANNOTATIONS = 1024;
    private static final int HAVE_DETERMINED_ANNOTATIONS = 2048;
    private UnresolvedType genericReturnType = null;
    private UnresolvedType[] genericParameterTypes = null;
    public static final AnnotationAJ[] NO_PARAMETER_ANNOTATIONS = new AnnotationAJ[0];

    BcelMethod(BcelObjectType declaringType, Method method) {
        super(method.getName().equals("<init>") ? CONSTRUCTOR : (method.getName().equals("<clinit>") ? STATIC_INITIALIZATION : METHOD), declaringType.getResolvedTypeX(), method.getModifiers(), method.getName(), method.getSignature());
        this.method = method;
        this.sourceContext = declaringType.getResolvedTypeX().getSourceContext();
        this.bcelObjectType = declaringType;
        this.unpackJavaAttributes();
        this.unpackAjAttributes(this.bcelObjectType.getWorld());
    }

    BcelMethod(BcelObjectType declaringType, Method method, List<AjAttribute> attributes) {
        super(method.getName().equals("<init>") ? CONSTRUCTOR : (method.getName().equals("<clinit>") ? STATIC_INITIALIZATION : METHOD), declaringType.getResolvedTypeX(), method.getModifiers(), method.getName(), method.getSignature());
        this.method = method;
        this.sourceContext = declaringType.getResolvedTypeX().getSourceContext();
        this.bcelObjectType = declaringType;
        this.unpackJavaAttributes();
        this.processAttributes(this.bcelObjectType.getWorld(), attributes);
    }

    private void unpackJavaAttributes() {
        ExceptionTable exnTable = this.method.getExceptionTable();
        this.checkedExceptions = exnTable == null ? UnresolvedType.NONE : UnresolvedType.forNames(exnTable.getExceptionNames());
    }

    @Override
    public String[] getParameterNames() {
        this.determineParameterNames();
        return super.getParameterNames();
    }

    public int getLineNumberOfFirstInstruction() {
        LineNumberTable lnt = this.method.getLineNumberTable();
        if (lnt == null) {
            return -1;
        }
        LineNumber[] lns = lnt.getLineNumberTable();
        if (lns == null || lns.length == 0) {
            return -1;
        }
        return lns[0].getLineNumber();
    }

    public void determineParameterNames() {
        if ((this.bitflags & 2) != 0) {
            return;
        }
        this.bitflags |= 2;
        LocalVariableTable varTable = this.method.getLocalVariableTable();
        int len = this.getArity();
        if (varTable == null) {
            AnnotationAJ[] annos = this.getAnnotations();
            if (annos != null && annos.length != 0) {
                AnnotationAJ[] axs = this.getAnnotations();
                for (int i = 0; i < axs.length; ++i) {
                    AnnotationGen a;
                    AnnotationAJ annotationX = axs[i];
                    String typename = annotationX.getTypeName();
                    if (typename.charAt(0) != PACKAGE_INITIAL_CHAR || !typename.equals("org.aspectj.lang.annotation.Pointcut") && !typename.equals("org.aspectj.lang.annotation.Before") && !typename.equals("org.aspectj.lang.annotation.Around") && !typename.startsWith("org.aspectj.lang.annotation.After") || (a = ((BcelAnnotation)annotationX).getBcelAnnotation()) == null) continue;
                    List<NameValuePair> values = a.getValues();
                    for (NameValuePair nvPair : values) {
                        if (!nvPair.getNameString().equals("argNames")) continue;
                        String argNames = nvPair.getValue().stringifyValue();
                        StringTokenizer argNameTokenizer = new StringTokenizer(argNames, " ,");
                        ArrayList<String> argsList = new ArrayList<String>();
                        while (argNameTokenizer.hasMoreTokens()) {
                            argsList.add(argNameTokenizer.nextToken());
                        }
                        int requiredCount = this.getParameterTypes().length;
                        while (argsList.size() < requiredCount) {
                            argsList.add("arg" + argsList.size());
                        }
                        this.setParameterNames(argsList.toArray(new String[0]));
                        return;
                    }
                }
            }
            this.setParameterNames(Utility.makeArgNames(len));
        } else {
            UnresolvedType[] paramTypes = this.getParameterTypes();
            String[] paramNames = new String[len];
            int index = Modifier.isStatic(this.modifiers) ? 0 : 1;
            for (int i = 0; i < len; ++i) {
                LocalVariable lv = varTable.getLocalVariable(index);
                paramNames[i] = lv == null ? "arg" + i : lv.getName();
                index += paramTypes[i].getSize();
            }
            this.setParameterNames(paramNames);
        }
    }

    private void unpackAjAttributes(World world) {
        this.associatedShadowMunger = null;
        ResolvedType resolvedDeclaringType = this.getDeclaringType().resolve(world);
        AjAttribute.WeaverVersionInfo wvinfo = this.bcelObjectType.getWeaverVersionAttribute();
        List<AjAttribute> as = Utility.readAjAttributes(resolvedDeclaringType.getClassName(), this.method.getAttributes(), resolvedDeclaringType.getSourceContext(), world, wvinfo, new BcelConstantPoolReader(this.method.getConstantPool()));
        this.processAttributes(world, as);
        as = AtAjAttributes.readAj5MethodAttributes(this.method, this, resolvedDeclaringType, this.preResolvedPointcut, resolvedDeclaringType.getSourceContext(), world.getMessageHandler());
        this.processAttributes(world, as);
    }

    private void processAttributes(World world, List<AjAttribute> as) {
        for (AjAttribute attr : as) {
            if (attr instanceof AjAttribute.MethodDeclarationLineNumberAttribute) {
                this.declarationLineNumber = (AjAttribute.MethodDeclarationLineNumberAttribute)attr;
                continue;
            }
            if (attr instanceof AjAttribute.AdviceAttribute) {
                this.associatedShadowMunger = ((AjAttribute.AdviceAttribute)attr).reify(this, world, (ResolvedType)this.getDeclaringType());
                continue;
            }
            if (attr instanceof AjAttribute.AjSynthetic) {
                this.bitflags |= 0x40;
                continue;
            }
            if (attr instanceof AjAttribute.EffectiveSignatureAttribute) {
                this.effectiveSignature = (AjAttribute.EffectiveSignatureAttribute)attr;
                continue;
            }
            if (attr instanceof AjAttribute.PointcutDeclarationAttribute) {
                this.preResolvedPointcut = ((AjAttribute.PointcutDeclarationAttribute)attr).reify();
                continue;
            }
            throw new BCException("weird method attribute " + attr);
        }
    }

    @Override
    public String getAnnotationDefaultValue() {
        Attribute[] attrs = this.method.getAttributes();
        for (int i = 0; i < attrs.length; ++i) {
            Attribute attribute = attrs[i];
            if (!attribute.getName().equals("AnnotationDefault")) continue;
            AnnotationDefault def = (AnnotationDefault)attribute;
            return def.getElementValue().stringifyValue();
        }
        return null;
    }

    public String[] getAttributeNames(boolean onlyIncludeAjOnes) {
        Attribute[] as = this.method.getAttributes();
        ArrayList<String> names = new ArrayList<String>();
        for (int j = 0; j < as.length; ++j) {
            if (onlyIncludeAjOnes && !as[j].getName().startsWith("org.aspectj.weaver")) continue;
            names.add(as[j].getName());
        }
        return names.toArray(new String[0]);
    }

    @Override
    public boolean isAjSynthetic() {
        return (this.bitflags & 0x40) != 0;
    }

    @Override
    public ShadowMunger getAssociatedShadowMunger() {
        return this.associatedShadowMunger;
    }

    @Override
    public AjAttribute.EffectiveSignatureAttribute getEffectiveSignature() {
        return this.effectiveSignature;
    }

    public boolean hasDeclarationLineNumberInfo() {
        return this.declarationLineNumber != null;
    }

    public int getDeclarationLineNumber() {
        if (this.declarationLineNumber != null) {
            return this.declarationLineNumber.getLineNumber();
        }
        return -1;
    }

    public int getDeclarationOffset() {
        if (this.declarationLineNumber != null) {
            return this.declarationLineNumber.getOffset();
        }
        return -1;
    }

    @Override
    public ISourceLocation getSourceLocation() {
        ISourceLocation ret = super.getSourceLocation();
        if ((ret == null || ret.getLine() == 0) && this.hasDeclarationLineNumberInfo()) {
            ISourceContext isc = this.getSourceContext();
            ret = isc != null ? isc.makeSourceLocation(this.getDeclarationLineNumber(), this.getDeclarationOffset()) : new SourceLocation(null, this.getDeclarationLineNumber());
        }
        return ret;
    }

    @Override
    public MemberKind getKind() {
        if (this.associatedShadowMunger != null) {
            return ADVICE;
        }
        return super.getKind();
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        this.ensureAnnotationsRetrieved();
        for (ResolvedType aType : this.annotationTypes) {
            if (!aType.equals(ofType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        this.ensureAnnotationsRetrieved();
        if ((this.bitflags & 0x400) != 0) {
            return this.annotations;
        }
        return AnnotationAJ.EMPTY_ARRAY;
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        this.ensureAnnotationsRetrieved();
        return this.annotationTypes;
    }

    @Override
    public AnnotationAJ getAnnotationOfType(UnresolvedType ofType) {
        this.ensureAnnotationsRetrieved();
        if ((this.bitflags & 0x400) == 0) {
            return null;
        }
        for (int i = 0; i < this.annotations.length; ++i) {
            if (!this.annotations[i].getTypeName().equals(ofType.getName())) continue;
            return this.annotations[i];
        }
        return null;
    }

    @Override
    public void addAnnotation(AnnotationAJ annotation) {
        this.ensureAnnotationsRetrieved();
        if ((this.bitflags & 0x400) == 0) {
            this.annotations = new AnnotationAJ[1];
            this.annotations[0] = annotation;
            this.annotationTypes = new ResolvedType[1];
            this.annotationTypes[0] = annotation.getType();
        } else {
            int len = this.annotations.length;
            AnnotationAJ[] ret = new AnnotationAJ[len + 1];
            System.arraycopy(this.annotations, 0, ret, 0, len);
            ret[len] = annotation;
            this.annotations = ret;
            ResolvedType[] newAnnotationTypes = new ResolvedType[len + 1];
            System.arraycopy(this.annotationTypes, 0, newAnnotationTypes, 0, len);
            newAnnotationTypes[len] = annotation.getType();
            this.annotationTypes = newAnnotationTypes;
        }
        this.bitflags |= 0x400;
    }

    public void removeAnnotation(ResolvedType annotationType) {
        this.ensureAnnotationsRetrieved();
        if ((this.bitflags & 0x400) != 0) {
            int len = this.annotations.length;
            if (len == 1) {
                this.bitflags &= 0xFFFFFBFF;
                this.annotations = null;
                this.annotationTypes = null;
                return;
            }
            AnnotationAJ[] ret = new AnnotationAJ[len - 1];
            int p = 0;
            for (AnnotationAJ annotation : this.annotations) {
                if (annotation.getType().equals(annotationType)) continue;
                ret[p++] = annotation;
            }
            this.annotations = ret;
            ResolvedType[] newAnnotationTypes = new ResolvedType[len - 1];
            p = 0;
            for (AnnotationAJ annotation : this.annotations) {
                if (annotation.getType().equals(annotationType)) continue;
                newAnnotationTypes[p++] = annotationType;
            }
            this.annotationTypes = newAnnotationTypes;
        }
        this.bitflags |= 0x400;
    }

    public void addParameterAnnotation(int param, AnnotationAJ anno) {
        int existingCount;
        this.ensureParameterAnnotationsRetrieved();
        if (this.parameterAnnotations == NO_PARAMETER_ANNOTATIONXS) {
            this.parameterAnnotations = new AnnotationAJ[this.getArity()][];
            for (int i = 0; i < this.getArity(); ++i) {
                this.parameterAnnotations[i] = NO_PARAMETER_ANNOTATIONS;
            }
        }
        if ((existingCount = this.parameterAnnotations[param].length) == 0) {
            AnnotationAJ[] annoArray = new AnnotationAJ[]{anno};
            this.parameterAnnotations[param] = annoArray;
        } else {
            AnnotationAJ[] newAnnoArray = new AnnotationAJ[existingCount + 1];
            System.arraycopy(this.parameterAnnotations[param], 0, newAnnoArray, 0, existingCount);
            newAnnoArray[existingCount] = anno;
            this.parameterAnnotations[param] = newAnnoArray;
        }
    }

    private void ensureAnnotationsRetrieved() {
        if (this.method == null) {
            return;
        }
        if ((this.bitflags & 0x800) != 0) {
            return;
        }
        this.bitflags |= 0x800;
        AnnotationGen[] annos = this.method.getAnnotations();
        if (annos.length == 0) {
            this.annotationTypes = ResolvedType.NONE;
            this.annotations = AnnotationAJ.EMPTY_ARRAY;
        } else {
            int annoCount = annos.length;
            this.annotationTypes = new ResolvedType[annoCount];
            this.annotations = new AnnotationAJ[annoCount];
            for (int i = 0; i < annoCount; ++i) {
                AnnotationGen annotation = annos[i];
                this.annotations[i] = new BcelAnnotation(annotation, this.bcelObjectType.getWorld());
                this.annotationTypes[i] = this.annotations[i].getType();
            }
            this.bitflags |= 0x400;
        }
    }

    private void ensureParameterAnnotationsRetrieved() {
        if (this.method == null) {
            return;
        }
        AnnotationGen[][] pAnns = this.method.getParameterAnnotations();
        if (this.parameterAnnotationTypes == null || pAnns.length != this.parameterAnnotationTypes.length) {
            if (pAnns == Method.NO_PARAMETER_ANNOTATIONS) {
                this.parameterAnnotationTypes = NO_PARAMETER_ANNOTATION_TYPES;
                this.parameterAnnotations = NO_PARAMETER_ANNOTATIONXS;
            } else {
                AnnotationGen[][] annos = this.method.getParameterAnnotations();
                this.parameterAnnotations = new AnnotationAJ[annos.length][];
                this.parameterAnnotationTypes = new ResolvedType[annos.length][];
                for (int i = 0; i < annos.length; ++i) {
                    AnnotationGen[] annosOnThisParam = annos[i];
                    if (annos[i].length == 0) {
                        this.parameterAnnotations[i] = AnnotationAJ.EMPTY_ARRAY;
                        this.parameterAnnotationTypes[i] = ResolvedType.NONE;
                        continue;
                    }
                    this.parameterAnnotations[i] = new AnnotationAJ[annosOnThisParam.length];
                    this.parameterAnnotationTypes[i] = new ResolvedType[annosOnThisParam.length];
                    for (int j = 0; j < annosOnThisParam.length; ++j) {
                        this.parameterAnnotations[i][j] = new BcelAnnotation(annosOnThisParam[j], this.bcelObjectType.getWorld());
                        this.parameterAnnotationTypes[i][j] = this.bcelObjectType.getWorld().resolve(UnresolvedType.forSignature(annosOnThisParam[j].getTypeSignature()));
                    }
                }
            }
        }
    }

    @Override
    public AnnotationAJ[][] getParameterAnnotations() {
        this.ensureParameterAnnotationsRetrieved();
        return this.parameterAnnotations;
    }

    @Override
    public ResolvedType[][] getParameterAnnotationTypes() {
        this.ensureParameterAnnotationsRetrieved();
        return this.parameterAnnotationTypes;
    }

    @Override
    public boolean canBeParameterized() {
        this.unpackGenericSignature();
        return (this.bitflags & 4) != 0;
    }

    @Override
    public UnresolvedType[] getGenericParameterTypes() {
        this.unpackGenericSignature();
        return this.genericParameterTypes;
    }

    @Override
    public UnresolvedType getGenericReturnType() {
        this.unpackGenericSignature();
        return this.genericReturnType;
    }

    public Method getMethod() {
        return this.method;
    }

    private void unpackGenericSignature() {
        if ((this.bitflags & 8) != 0) {
            return;
        }
        this.bitflags |= 8;
        if (!this.bcelObjectType.getWorld().isInJava5Mode()) {
            this.genericReturnType = this.getReturnType();
            this.genericParameterTypes = this.getParameterTypes();
            return;
        }
        String gSig = this.method.getGenericSignature();
        if (gSig != null) {
            GenericSignature.MethodTypeSignature mSig = new GenericSignatureParser().parseAsMethodSignature(gSig);
            if (mSig.formalTypeParameters.length > 0) {
                this.bitflags |= 4;
            }
            this.typeVariables = new TypeVariable[mSig.formalTypeParameters.length];
            for (int i = 0; i < this.typeVariables.length; ++i) {
                GenericSignature.FormalTypeParameter methodFtp = mSig.formalTypeParameters[i];
                try {
                    this.typeVariables[i] = BcelGenericSignatureToTypeXConverter.formalTypeParameter2TypeVariable(methodFtp, mSig.formalTypeParameters, this.bcelObjectType.getWorld());
                    continue;
                }
                catch (BcelGenericSignatureToTypeXConverter.GenericSignatureFormatException e) {
                    throw new IllegalStateException("While getting the type variables for method " + this.toString() + " with generic signature " + mSig + " the following error condition was detected: " + e.getMessage());
                }
            }
            GenericSignature.FormalTypeParameter[] parentFormals = this.bcelObjectType.getAllFormals();
            GenericSignature.FormalTypeParameter[] formals = new GenericSignature.FormalTypeParameter[parentFormals.length + mSig.formalTypeParameters.length];
            System.arraycopy(mSig.formalTypeParameters, 0, formals, 0, mSig.formalTypeParameters.length);
            System.arraycopy(parentFormals, 0, formals, mSig.formalTypeParameters.length, parentFormals.length);
            GenericSignature.TypeSignature returnTypeSignature = mSig.returnType;
            try {
                this.genericReturnType = BcelGenericSignatureToTypeXConverter.typeSignature2TypeX(returnTypeSignature, formals, this.bcelObjectType.getWorld());
            }
            catch (BcelGenericSignatureToTypeXConverter.GenericSignatureFormatException e) {
                throw new IllegalStateException("While determing the generic return type of " + this.toString() + " with generic signature " + gSig + " the following error was detected: " + e.getMessage());
            }
            GenericSignature.TypeSignature[] paramTypeSigs = mSig.parameters;
            this.genericParameterTypes = paramTypeSigs.length == 0 ? UnresolvedType.NONE : new UnresolvedType[paramTypeSigs.length];
            for (int i = 0; i < paramTypeSigs.length; ++i) {
                try {
                    this.genericParameterTypes[i] = BcelGenericSignatureToTypeXConverter.typeSignature2TypeX(paramTypeSigs[i], formals, this.bcelObjectType.getWorld());
                }
                catch (BcelGenericSignatureToTypeXConverter.GenericSignatureFormatException e) {
                    throw new IllegalStateException("While determining the generic parameter types of " + this.toString() + " with generic signature " + gSig + " the following error was detected: " + e.getMessage());
                }
                if (!(paramTypeSigs[i] instanceof GenericSignature.TypeVariableSignature)) continue;
                this.bitflags |= 4;
            }
        } else {
            this.genericReturnType = this.getReturnType();
            this.genericParameterTypes = this.getParameterTypes();
        }
    }

    @Override
    public void evictWeavingState() {
        if (this.method != null) {
            this.unpackGenericSignature();
            this.unpackJavaAttributes();
            this.ensureAnnotationsRetrieved();
            this.ensureParameterAnnotationsRetrieved();
            this.determineParameterNames();
            this.method = null;
        }
    }

    @Override
    public boolean isSynthetic() {
        if ((this.bitflags & 1) == 0) {
            this.workOutIfSynthetic();
        }
        return (this.bitflags & 0x80) != 0;
    }

    private void workOutIfSynthetic() {
        if ((this.bitflags & 1) != 0) {
            return;
        }
        this.bitflags |= 1;
        JavaClass jc = this.bcelObjectType.getJavaClass();
        this.bitflags &= 0x7F7F;
        if (jc == null) {
            return;
        }
        if (jc.getMajor() < 49) {
            String[] synthetics = this.getAttributeNames(false);
            if (synthetics != null) {
                for (int i = 0; i < synthetics.length; ++i) {
                    if (!synthetics[i].equals("Synthetic")) continue;
                    this.bitflags |= 0x80;
                    break;
                }
            }
        } else if ((this.modifiers & 0x1000) != 0) {
            this.bitflags |= 0x80;
        }
    }

    @Override
    public boolean isEquivalentTo(Object other) {
        if (!(other instanceof BcelMethod)) {
            return false;
        }
        BcelMethod o = (BcelMethod)other;
        return this.getMethod().getCode().getCodeString().equals(o.getMethod().getCode().getCodeString());
    }

    @Override
    public boolean isDefaultConstructor() {
        boolean mightBe;
        boolean bl = mightBe = !this.hasDeclarationLineNumberInfo() && this.name.equals("<init>") && this.parameterTypes.length == 0;
        return mightBe;
    }
}

