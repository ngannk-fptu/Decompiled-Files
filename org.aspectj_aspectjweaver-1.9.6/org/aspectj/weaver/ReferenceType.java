/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.AbstractReferenceTypeDelegate;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.AnnotationTargetKind;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.BoundedReferenceType;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Position;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.SourceContextImpl;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableReference;
import org.aspectj.weaver.TypeVariableReferenceType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverStateInfo;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.PerClause;

public class ReferenceType
extends ResolvedType {
    public static final ReferenceType[] EMPTY_ARRAY = new ReferenceType[0];
    private final List<WeakReference<ReferenceType>> derivativeTypes = new ArrayList<WeakReference<ReferenceType>>();
    ReferenceType genericType = null;
    ReferenceType rawType = null;
    ReferenceTypeDelegate delegate = null;
    int startPos = 0;
    int endPos = 0;
    ResolvedMember[] parameterizedMethods = null;
    ResolvedMember[] parameterizedFields = null;
    ResolvedMember[] parameterizedPointcuts = null;
    WeakReference<ResolvedType[]> parameterizedInterfaces = new WeakReference<Object>(null);
    Collection<Declare> parameterizedDeclares = null;
    private ResolvedType[] annotationTypes = null;
    private AnnotationAJ[] annotations = null;
    private ResolvedType newSuperclass;
    private ResolvedType[] newInterfaces;
    WeakReference<ResolvedType> superclassReference = new WeakReference<Object>(null);

    public ReferenceType(String signature, World world) {
        super(signature, world);
    }

    public ReferenceType(String signature, String signatureErasure, World world) {
        super(signature, signatureErasure, world);
    }

    public static ReferenceType fromTypeX(UnresolvedType tx, World world) {
        ReferenceType rt = new ReferenceType(tx.getErasureSignature(), world);
        rt.typeKind = tx.typeKind;
        return rt;
    }

    public ReferenceType(ResolvedType theGenericType, ResolvedType[] theParameters, World aWorld) {
        super(ReferenceType.makeParameterizedSignature(theGenericType, theParameters), theGenericType.signatureErasure, aWorld);
        ReferenceType genericReferenceType = (ReferenceType)theGenericType;
        this.typeParameters = theParameters;
        this.genericType = genericReferenceType;
        this.typeKind = UnresolvedType.TypeKind.PARAMETERIZED;
        this.delegate = genericReferenceType.getDelegate();
        genericReferenceType.addDependentType(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void addDependentType(ReferenceType dependent) {
        List<WeakReference<ReferenceType>> list = this.derivativeTypes;
        synchronized (list) {
            this.derivativeTypes.add(new WeakReference<ReferenceType>(dependent));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void checkDuplicates(ReferenceType newRt) {
        List<WeakReference<ReferenceType>> list = this.derivativeTypes;
        synchronized (list) {
            ArrayList<WeakReference<ReferenceType>> forRemoval = new ArrayList<WeakReference<ReferenceType>>();
            for (WeakReference<ReferenceType> derivativeTypeReference : this.derivativeTypes) {
                ReferenceType derivativeType = (ReferenceType)derivativeTypeReference.get();
                if (derivativeType == null) {
                    forRemoval.add(derivativeTypeReference);
                    continue;
                }
                if (derivativeType.getTypekind() != newRt.getTypekind() || !this.equal2(newRt.getTypeParameters(), derivativeType.getTypeParameters()) || !World.TypeMap.useExpendableMap) continue;
                throw new IllegalStateException();
            }
            this.derivativeTypes.removeAll(forRemoval);
        }
    }

    private boolean equal2(UnresolvedType[] typeParameters, UnresolvedType[] resolvedParameters) {
        if (typeParameters.length != resolvedParameters.length) {
            return false;
        }
        int len = typeParameters.length;
        for (int p = 0; p < len; ++p) {
            if (typeParameters[p].equals(resolvedParameters[p])) continue;
            return false;
        }
        return true;
    }

    @Override
    public String getSignatureForAttribute() {
        if (this.genericType == null || this.typeParameters == null) {
            return this.getSignature();
        }
        return ReferenceType.makeDeclaredSignature(this.genericType, this.typeParameters);
    }

    public ReferenceType(UnresolvedType genericType, World world) {
        super(genericType.getSignature(), world);
        this.typeKind = UnresolvedType.TypeKind.GENERIC;
        this.typeVariables = genericType.typeVariables;
    }

    @Override
    public boolean isClass() {
        return this.getDelegate().isClass();
    }

    @Override
    public int getCompilerVersion() {
        return this.getDelegate().getCompilerVersion();
    }

    @Override
    public boolean isGenericType() {
        return !this.isParameterizedType() && !this.isRawType() && this.getDelegate().isGeneric();
    }

    public String getGenericSignature() {
        String sig = this.getDelegate().getDeclaredGenericSignature();
        return sig == null ? "" : sig;
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        return this.getDelegate().getAnnotations();
    }

    @Override
    public boolean hasAnnotations() {
        return this.getDelegate().hasAnnotations();
    }

    @Override
    public void addAnnotation(AnnotationAJ annotationX) {
        if (this.annotations == null) {
            this.annotations = new AnnotationAJ[]{annotationX};
        } else {
            AnnotationAJ[] newAnnotations = new AnnotationAJ[this.annotations.length + 1];
            System.arraycopy(this.annotations, 0, newAnnotations, 1, this.annotations.length);
            newAnnotations[0] = annotationX;
            this.annotations = newAnnotations;
        }
        this.addAnnotationType(annotationX.getType());
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        boolean onDelegate = this.getDelegate().hasAnnotation(ofType);
        if (onDelegate) {
            return true;
        }
        if (this.annotationTypes != null) {
            for (int i = 0; i < this.annotationTypes.length; ++i) {
                if (!this.annotationTypes[i].equals(ofType)) continue;
                return true;
            }
        }
        return false;
    }

    private void addAnnotationType(ResolvedType ofType) {
        if (this.annotationTypes == null) {
            this.annotationTypes = new ResolvedType[1];
            this.annotationTypes[0] = ofType;
        } else {
            ResolvedType[] newAnnotationTypes = new ResolvedType[this.annotationTypes.length + 1];
            System.arraycopy(this.annotationTypes, 0, newAnnotationTypes, 1, this.annotationTypes.length);
            newAnnotationTypes[0] = ofType;
            this.annotationTypes = newAnnotationTypes;
        }
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        if (this.getDelegate() == null) {
            throw new BCException("Unexpected null delegate for type " + this.getName());
        }
        if (this.annotationTypes == null) {
            return this.getDelegate().getAnnotationTypes();
        }
        ResolvedType[] delegateAnnotationTypes = this.getDelegate().getAnnotationTypes();
        ResolvedType[] result = new ResolvedType[this.annotationTypes.length + delegateAnnotationTypes.length];
        System.arraycopy(delegateAnnotationTypes, 0, result, 0, delegateAnnotationTypes.length);
        System.arraycopy(this.annotationTypes, 0, result, delegateAnnotationTypes.length, this.annotationTypes.length);
        return result;
    }

    @Override
    public String getNameAsIdentifier() {
        return this.getRawName().replace('.', '_');
    }

    @Override
    public AnnotationAJ getAnnotationOfType(UnresolvedType ofType) {
        AnnotationAJ[] axs = this.getDelegate().getAnnotations();
        if (axs != null) {
            for (int i = 0; i < axs.length; ++i) {
                if (!axs[i].getTypeSignature().equals(ofType.getSignature())) continue;
                return axs[i];
            }
        }
        if (this.annotations != null) {
            String searchSig = ofType.getSignature();
            for (int i = 0; i < this.annotations.length; ++i) {
                if (!this.annotations[i].getTypeSignature().equals(searchSig)) continue;
                return this.annotations[i];
            }
        }
        return null;
    }

    @Override
    public boolean isAspect() {
        return this.getDelegate().isAspect();
    }

    @Override
    public boolean isAnnotationStyleAspect() {
        return this.getDelegate().isAnnotationStyleAspect();
    }

    @Override
    public boolean isEnum() {
        return this.getDelegate().isEnum();
    }

    @Override
    public boolean isAnnotation() {
        return this.getDelegate().isAnnotation();
    }

    @Override
    public boolean isAnonymous() {
        return this.getDelegate().isAnonymous();
    }

    @Override
    public boolean isNested() {
        return this.getDelegate().isNested();
    }

    @Override
    public ResolvedType getOuterClass() {
        return this.getDelegate().getOuterClass();
    }

    public String getRetentionPolicy() {
        return this.getDelegate().getRetentionPolicy();
    }

    @Override
    public boolean isAnnotationWithRuntimeRetention() {
        return this.getDelegate().isAnnotationWithRuntimeRetention();
    }

    @Override
    public boolean canAnnotationTargetType() {
        return this.getDelegate().canAnnotationTargetType();
    }

    @Override
    public AnnotationTargetKind[] getAnnotationTargetKinds() {
        return this.getDelegate().getAnnotationTargetKinds();
    }

    @Override
    public boolean isCoerceableFrom(ResolvedType o) {
        ResolvedType other = o.resolve(this.world);
        if (this.isAssignableFrom(other) || other.isAssignableFrom(this)) {
            return true;
        }
        if (this.isParameterizedType() && other.isParameterizedType()) {
            return this.isCoerceableFromParameterizedType(other);
        }
        if (this.isParameterizedType() && other.isRawType()) {
            return ((ReferenceType)this.getRawType()).isCoerceableFrom(other.getGenericType());
        }
        if (this.isRawType() && other.isParameterizedType()) {
            return this.getGenericType().isCoerceableFrom(other.getRawType());
        }
        if (!this.isInterface() && !other.isInterface()) {
            return false;
        }
        if (this.isFinal() || other.isFinal()) {
            return false;
        }
        ResolvedMember[] a = this.getDeclaredMethods();
        ResolvedMember[] b = other.getDeclaredMethods();
        int alen = a.length;
        for (int ai = 0; ai < alen; ++ai) {
            int blen = b.length;
            for (int bi = 0; bi < blen; ++bi) {
                if (b[bi].isCompatibleWith(a[ai])) continue;
                return false;
            }
        }
        return true;
    }

    private final boolean isCoerceableFromParameterizedType(ResolvedType other) {
        ResolvedType theirRawType;
        if (!other.isParameterizedType()) {
            return false;
        }
        ResolvedType myRawType = this.getRawType();
        if ((myRawType == (theirRawType = other.getRawType()) || myRawType.isCoerceableFrom(theirRawType)) && this.getTypeParameters().length == other.getTypeParameters().length) {
            ResolvedType[] myTypeParameters = this.getResolvedTypeParameters();
            ResolvedType[] theirTypeParameters = other.getResolvedTypeParameters();
            for (int i = 0; i < myTypeParameters.length; ++i) {
                TypeVariable tv;
                TypeVariableReferenceType tvrt;
                BoundedReferenceType wildcard;
                if (myTypeParameters[i] == theirTypeParameters[i]) continue;
                if (myTypeParameters[i].isGenericWildcard()) {
                    wildcard = (BoundedReferenceType)myTypeParameters[i];
                    if (wildcard.canBeCoercedTo(theirTypeParameters[i])) continue;
                    return false;
                }
                if (myTypeParameters[i].isTypeVariableReference()) {
                    tvrt = (TypeVariableReferenceType)myTypeParameters[i];
                    tv = tvrt.getTypeVariable();
                    tv.resolve(this.world);
                    if (tv.canBeBoundTo(theirTypeParameters[i])) continue;
                    return false;
                }
                if (theirTypeParameters[i].isTypeVariableReference()) {
                    tvrt = (TypeVariableReferenceType)theirTypeParameters[i];
                    tv = tvrt.getTypeVariable();
                    tv.resolve(this.world);
                    if (tv.canBeBoundTo(myTypeParameters[i])) continue;
                    return false;
                }
                if (theirTypeParameters[i].isGenericWildcard()) {
                    wildcard = (BoundedReferenceType)theirTypeParameters[i];
                    if (wildcard.canBeCoercedTo(myTypeParameters[i])) continue;
                    return false;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isAssignableFrom(ResolvedType other) {
        return this.isAssignableFrom(other, false);
    }

    @Override
    public boolean isAssignableFrom(ResolvedType other, boolean allowMissing) {
        boolean b;
        if (other.isPrimitiveType()) {
            if (!this.world.isInJava5Mode()) {
                return false;
            }
            if (ResolvedType.validBoxing.contains(this.getSignature() + other.getSignature())) {
                return true;
            }
        }
        if (this == other) {
            return true;
        }
        if (this.getSignature().equals("Ljava/lang/Object;")) {
            return true;
        }
        if (!this.isTypeVariableReference() && other.getSignature().equals("Ljava/lang/Object;")) {
            return false;
        }
        boolean thisRaw = this.isRawType();
        if (thisRaw && other.isParameterizedOrGenericType()) {
            return this.isAssignableFrom(other.getRawType());
        }
        boolean thisGeneric = this.isGenericType();
        if (thisGeneric && other.isParameterizedOrRawType()) {
            return this.isAssignableFrom(other.getGenericType());
        }
        if (this.isParameterizedType() && ((ReferenceType)this.getRawType()).isAssignableFrom(other)) {
            boolean wildcardsAllTheWay = true;
            ResolvedType[] myParameters = this.getResolvedTypeParameters();
            for (int i = 0; i < myParameters.length; ++i) {
                if (!myParameters[i].isGenericWildcard()) {
                    wildcardsAllTheWay = false;
                    continue;
                }
                BoundedReferenceType boundedRT = (BoundedReferenceType)myParameters[i];
                if (!boundedRT.isExtends() && !boundedRT.isSuper()) continue;
                wildcardsAllTheWay = false;
            }
            if (wildcardsAllTheWay && !other.isParameterizedType()) {
                return true;
            }
            ResolvedType[] theirParameters = other.getResolvedTypeParameters();
            boolean parametersAssignable = true;
            if (myParameters.length == theirParameters.length) {
                block1: for (int i = 0; i < myParameters.length && parametersAssignable; ++i) {
                    if (myParameters[i] == theirParameters[i]) continue;
                    ResolvedType mp = myParameters[i];
                    ResolvedType tp = theirParameters[i];
                    if (mp.isParameterizedType() && tp.isParameterizedType()) {
                        if (mp.getGenericType().equals(tp.getGenericType())) {
                            UnresolvedType[] mtps = mp.getTypeParameters();
                            UnresolvedType[] ttps = tp.getTypeParameters();
                            for (int ii = 0; ii < mtps.length; ++ii) {
                                if (mtps[ii].isTypeVariableReference() && ttps[ii].isTypeVariableReference()) {
                                    TypeVariable mtv = ((TypeVariableReferenceType)mtps[ii]).getTypeVariable();
                                    boolean b2 = mtv.canBeBoundTo((ResolvedType)ttps[ii]);
                                    if (b2) continue;
                                    parametersAssignable = false;
                                    continue block1;
                                }
                                parametersAssignable = false;
                                continue block1;
                            }
                            continue;
                        }
                        parametersAssignable = false;
                        break;
                    }
                    if (myParameters[i].isTypeVariableReference() && theirParameters[i].isTypeVariableReference()) {
                        TypeVariable myTV = ((TypeVariableReferenceType)myParameters[i]).getTypeVariable();
                        boolean b3 = myTV.canBeBoundTo(theirParameters[i]);
                        if (b3) continue;
                        parametersAssignable = false;
                    } else if (!myParameters[i].isGenericWildcard()) {
                        parametersAssignable = false;
                    } else {
                        BoundedReferenceType wildcardType = (BoundedReferenceType)myParameters[i];
                        if (wildcardType.alwaysMatches(theirParameters[i])) continue;
                        parametersAssignable = false;
                    }
                    break;
                }
            } else {
                parametersAssignable = false;
            }
            if (parametersAssignable) {
                return true;
            }
        }
        if (this.isTypeVariableReference() && !other.isTypeVariableReference()) {
            TypeVariable aVar = ((TypeVariableReference)((Object)this)).getTypeVariable();
            return aVar.resolve(this.world).canBeBoundTo(other);
        }
        if (other.isTypeVariableReference()) {
            TypeVariableReferenceType otherType = (TypeVariableReferenceType)other;
            if (this instanceof TypeVariableReference) {
                return ((TypeVariableReference)((Object)this)).getTypeVariable().resolve(this.world).canBeBoundTo(otherType.getTypeVariable().getFirstBound().resolve(this.world));
            }
            return this.isAssignableFrom(otherType.getTypeVariable().getFirstBound().resolve(this.world));
        }
        if (allowMissing && other.isMissing()) {
            return false;
        }
        ResolvedType[] interfaces = other.getDeclaredInterfaces();
        for (ResolvedType intface : interfaces) {
            boolean b4 = thisRaw && intface.isParameterizedOrGenericType() ? this.isAssignableFrom(intface.getRawType(), allowMissing) : this.isAssignableFrom(intface, allowMissing);
            if (!b4) continue;
            return true;
        }
        ResolvedType superclass = other.getSuperclass();
        return superclass != null && (b = thisRaw && superclass.isParameterizedOrGenericType() ? this.isAssignableFrom(superclass.getRawType(), allowMissing) : this.isAssignableFrom(superclass, allowMissing));
    }

    @Override
    public ISourceContext getSourceContext() {
        return this.getDelegate().getSourceContext();
    }

    @Override
    public ISourceLocation getSourceLocation() {
        ISourceContext isc = this.getDelegate().getSourceContext();
        return isc.makeSourceLocation(new Position(this.startPos, this.endPos));
    }

    @Override
    public boolean isExposedToWeaver() {
        return this.getDelegate() == null || this.delegate.isExposedToWeaver();
    }

    @Override
    public WeaverStateInfo getWeaverState() {
        return this.getDelegate().getWeaverState();
    }

    @Override
    public ResolvedMember[] getDeclaredFields() {
        if (this.parameterizedFields != null) {
            return this.parameterizedFields;
        }
        if (this.isParameterizedType() || this.isRawType()) {
            ResolvedMember[] delegateFields = this.getDelegate().getDeclaredFields();
            this.parameterizedFields = new ResolvedMember[delegateFields.length];
            for (int i = 0; i < delegateFields.length; ++i) {
                this.parameterizedFields[i] = delegateFields[i].parameterizedWith(this.getTypesForMemberParameterization(), this, this.isParameterizedType());
            }
            return this.parameterizedFields;
        }
        return this.getDelegate().getDeclaredFields();
    }

    @Override
    public ResolvedType[] getDeclaredInterfaces() {
        ResolvedType[] interfaces = (ResolvedType[])this.parameterizedInterfaces.get();
        if (interfaces != null) {
            return interfaces;
        }
        ResolvedType[] delegateInterfaces = this.getDelegate().getDeclaredInterfaces();
        if (this.isRawType()) {
            if (this.newInterfaces != null) {
                throw new IllegalStateException("The raw type should never be accumulating new interfaces, they should be on the generic type.  Type is " + this.getName());
            }
            ResolvedType[] newInterfacesFromGenericType = this.genericType.newInterfaces;
            if (newInterfacesFromGenericType != null) {
                ResolvedType[] extraInterfaces = new ResolvedType[delegateInterfaces.length + newInterfacesFromGenericType.length];
                System.arraycopy(delegateInterfaces, 0, extraInterfaces, 0, delegateInterfaces.length);
                System.arraycopy(newInterfacesFromGenericType, 0, extraInterfaces, delegateInterfaces.length, newInterfacesFromGenericType.length);
                delegateInterfaces = extraInterfaces;
            }
        } else if (this.newInterfaces != null) {
            ResolvedType[] extraInterfaces = new ResolvedType[delegateInterfaces.length + this.newInterfaces.length];
            System.arraycopy(delegateInterfaces, 0, extraInterfaces, 0, delegateInterfaces.length);
            System.arraycopy(this.newInterfaces, 0, extraInterfaces, delegateInterfaces.length, this.newInterfaces.length);
            delegateInterfaces = extraInterfaces;
        }
        if (this.isParameterizedType()) {
            interfaces = new ResolvedType[delegateInterfaces.length];
            for (int i = 0; i < delegateInterfaces.length; ++i) {
                interfaces[i] = delegateInterfaces[i].isParameterizedType() ? delegateInterfaces[i].parameterize(this.getMemberParameterizationMap()).resolve(this.world) : delegateInterfaces[i];
            }
            this.parameterizedInterfaces = new WeakReference<ResolvedType[]>(interfaces);
            return interfaces;
        }
        if (this.isRawType()) {
            UnresolvedType[] paramTypes = this.getTypesForMemberParameterization();
            interfaces = new ResolvedType[delegateInterfaces.length];
            int max = interfaces.length;
            for (int i = 0; i < max; ++i) {
                interfaces[i] = delegateInterfaces[i];
                if (interfaces[i].isGenericType()) {
                    interfaces[i] = interfaces[i].getRawType().resolve(this.getWorld());
                    continue;
                }
                if (!interfaces[i].isParameterizedType()) continue;
                UnresolvedType[] toUseForParameterization = this.determineThoseTypesToUse(interfaces[i], paramTypes);
                interfaces[i] = interfaces[i].parameterizedWith(toUseForParameterization);
            }
            this.parameterizedInterfaces = new WeakReference<ResolvedType[]>(interfaces);
            return interfaces;
        }
        if (this.getDelegate().isCacheable()) {
            this.parameterizedInterfaces = new WeakReference<ResolvedType[]>(delegateInterfaces);
        }
        return delegateInterfaces;
    }

    private UnresolvedType[] determineThoseTypesToUse(ResolvedType parameterizedInterface, UnresolvedType[] paramTypes) {
        UnresolvedType[] tParms = parameterizedInterface.getTypeParameters();
        UnresolvedType[] retVal = new UnresolvedType[tParms.length];
        for (int i = 0; i < tParms.length; ++i) {
            UnresolvedType tParm = tParms[i];
            if (tParm.isTypeVariableReference()) {
                TypeVariableReference tvrt = (TypeVariableReference)((Object)tParm);
                TypeVariable tv = tvrt.getTypeVariable();
                int rank = this.getRank(tv.getName());
                if (rank != -1) {
                    retVal[i] = paramTypes[rank];
                    continue;
                }
                retVal[i] = tParms[i];
                continue;
            }
            retVal[i] = tParms[i];
        }
        return retVal;
    }

    private int getRank(String tvname) {
        TypeVariable[] thisTypesTVars = this.getGenericType().getTypeVariables();
        for (int i = 0; i < thisTypesTVars.length; ++i) {
            TypeVariable tv = thisTypesTVars[i];
            if (!tv.getName().equals(tvname)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public ResolvedMember[] getDeclaredMethods() {
        if (this.parameterizedMethods != null) {
            return this.parameterizedMethods;
        }
        if (this.isParameterizedType() || this.isRawType()) {
            ResolvedMember[] delegateMethods = this.getDelegate().getDeclaredMethods();
            UnresolvedType[] parameters = this.getTypesForMemberParameterization();
            this.parameterizedMethods = new ResolvedMember[delegateMethods.length];
            for (int i = 0; i < delegateMethods.length; ++i) {
                this.parameterizedMethods[i] = delegateMethods[i].parameterizedWith(parameters, this, this.isParameterizedType());
            }
            return this.parameterizedMethods;
        }
        return this.getDelegate().getDeclaredMethods();
    }

    @Override
    public ResolvedMember[] getDeclaredPointcuts() {
        if (this.parameterizedPointcuts != null) {
            return this.parameterizedPointcuts;
        }
        if (this.isParameterizedType()) {
            ResolvedMember[] delegatePointcuts = this.getDelegate().getDeclaredPointcuts();
            this.parameterizedPointcuts = new ResolvedMember[delegatePointcuts.length];
            for (int i = 0; i < delegatePointcuts.length; ++i) {
                this.parameterizedPointcuts[i] = delegatePointcuts[i].parameterizedWith(this.getTypesForMemberParameterization(), this, this.isParameterizedType());
            }
            return this.parameterizedPointcuts;
        }
        return this.getDelegate().getDeclaredPointcuts();
    }

    private UnresolvedType[] getTypesForMemberParameterization() {
        UnresolvedType[] parameters = null;
        if (this.isParameterizedType()) {
            parameters = this.getTypeParameters();
        } else if (this.isRawType()) {
            TypeVariable[] tvs = this.getGenericType().getTypeVariables();
            parameters = new UnresolvedType[tvs.length];
            for (int i = 0; i < tvs.length; ++i) {
                parameters[i] = tvs[i].getFirstBound();
            }
        }
        return parameters;
    }

    @Override
    public TypeVariable[] getTypeVariables() {
        if (this.typeVariables == null) {
            this.typeVariables = this.getDelegate().getTypeVariables();
            for (int i = 0; i < this.typeVariables.length; ++i) {
                this.typeVariables[i].resolve(this.world);
            }
        }
        return this.typeVariables;
    }

    @Override
    public PerClause getPerClause() {
        PerClause pclause = this.getDelegate().getPerClause();
        if (pclause != null && this.isParameterizedType()) {
            Map<String, UnresolvedType> parameterizationMap = this.getAjMemberParameterizationMap();
            pclause = (PerClause)pclause.parameterizeWith(parameterizationMap, this.world);
        }
        return pclause;
    }

    @Override
    public Collection<Declare> getDeclares() {
        if (this.parameterizedDeclares != null) {
            return this.parameterizedDeclares;
        }
        Collection<Declare> declares = null;
        if (this.ajMembersNeedParameterization()) {
            Collection<Declare> genericDeclares = this.getDelegate().getDeclares();
            this.parameterizedDeclares = new ArrayList<Declare>();
            Map<String, UnresolvedType> parameterizationMap = this.getAjMemberParameterizationMap();
            for (Declare declareStatement : genericDeclares) {
                this.parameterizedDeclares.add(declareStatement.parameterizeWith(parameterizationMap, this.world));
            }
            declares = this.parameterizedDeclares;
        } else {
            declares = this.getDelegate().getDeclares();
        }
        for (Declare d : declares) {
            d.setDeclaringType(this);
        }
        return declares;
    }

    @Override
    public Collection<ConcreteTypeMunger> getTypeMungers() {
        return this.getDelegate().getTypeMungers();
    }

    @Override
    public Collection<ResolvedMember> getPrivilegedAccesses() {
        return this.getDelegate().getPrivilegedAccesses();
    }

    @Override
    public int getModifiers() {
        return this.getDelegate().getModifiers();
    }

    @Override
    public ResolvedType getSuperclass() {
        ResolvedType ret = null;
        if (this.newSuperclass != null) {
            if (this.isParameterizedType() && this.newSuperclass.isParameterizedType()) {
                return this.newSuperclass.parameterize(this.getMemberParameterizationMap()).resolve(this.getWorld());
            }
            if (this.getDelegate().isCacheable()) {
                this.superclassReference = new WeakReference<ResolvedType>(ret);
            }
            return this.newSuperclass;
        }
        try {
            this.world.setTypeVariableLookupScope(this);
            ret = this.getDelegate().getSuperclass();
        }
        finally {
            this.world.setTypeVariableLookupScope(null);
        }
        if (this.isParameterizedType() && ret.isParameterizedType()) {
            ret = ret.parameterize(this.getMemberParameterizationMap()).resolve(this.getWorld());
        }
        if (this.getDelegate().isCacheable()) {
            this.superclassReference = new WeakReference<ResolvedType>(ret);
        }
        return ret;
    }

    public ReferenceTypeDelegate getDelegate() {
        return this.delegate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDelegate(ReferenceTypeDelegate delegate) {
        ReferenceType genType;
        if (this.delegate != null && this.delegate.copySourceContext() && this.delegate.getSourceContext() != SourceContextImpl.UNKNOWN_SOURCE_CONTEXT) {
            ((AbstractReferenceTypeDelegate)delegate).setSourceContext(this.delegate.getSourceContext());
        }
        this.delegate = delegate;
        List<WeakReference<ReferenceType>> list = this.derivativeTypes;
        synchronized (list) {
            ArrayList<WeakReference<ReferenceType>> forRemoval = new ArrayList<WeakReference<ReferenceType>>();
            for (WeakReference<ReferenceType> derivativeRef : this.derivativeTypes) {
                ReferenceType derivative = (ReferenceType)derivativeRef.get();
                if (derivative != null) {
                    derivative.setDelegate(delegate);
                    continue;
                }
                forRemoval.add(derivativeRef);
            }
            this.derivativeTypes.removeAll(forRemoval);
        }
        if (this.isRawType() && this.getGenericType() != null && (genType = this.getGenericType()).getDelegate() != delegate) {
            genType.setDelegate(delegate);
        }
        this.clearParameterizationCaches();
        this.ensureConsistent();
    }

    private void clearParameterizationCaches() {
        this.parameterizedFields = null;
        this.parameterizedInterfaces.clear();
        this.parameterizedMethods = null;
        this.parameterizedPointcuts = null;
        this.superclassReference = new WeakReference<Object>(null);
    }

    public int getEndPos() {
        return this.endPos;
    }

    public int getStartPos() {
        return this.startPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    @Override
    public boolean doesNotExposeShadowMungers() {
        return this.getDelegate().doesNotExposeShadowMungers();
    }

    public String getDeclaredGenericSignature() {
        return this.getDelegate().getDeclaredGenericSignature();
    }

    public void setGenericType(ReferenceType rt) {
        this.genericType = rt;
        if (this.typeKind == UnresolvedType.TypeKind.SIMPLE) {
            this.typeKind = UnresolvedType.TypeKind.RAW;
            this.signatureErasure = this.signature;
            if (this.newInterfaces != null) {
                throw new IllegalStateException("Simple type promoted to raw, but simple type had new interfaces/superclass.  Type is " + this.getName());
            }
        }
        if (this.typeKind == UnresolvedType.TypeKind.RAW) {
            this.genericType.addDependentType(this);
        }
        if (this.isRawType()) {
            this.genericType.rawType = this;
        }
        if (this.isRawType() && rt.isRawType()) {
            new RuntimeException("PR341926 diagnostics: Incorrect setup for a generic type, raw type should not point to raw: " + this.getName()).printStackTrace();
        }
    }

    public void demoteToSimpleType() {
        this.genericType = null;
        this.typeKind = UnresolvedType.TypeKind.SIMPLE;
        this.signatureErasure = null;
    }

    @Override
    public ReferenceType getGenericType() {
        if (this.isGenericType()) {
            return this;
        }
        return this.genericType;
    }

    private static String makeParameterizedSignature(ResolvedType aGenericType, ResolvedType[] someParameters) {
        String rawSignature = aGenericType.getErasureSignature();
        StringBuffer ret = new StringBuffer();
        ret.append("P");
        ret.append(rawSignature.substring(1, rawSignature.length() - 1));
        ret.append("<");
        for (int i = 0; i < someParameters.length; ++i) {
            ret.append(someParameters[i].getSignature());
        }
        ret.append(">;");
        return ret.toString();
    }

    private static String makeDeclaredSignature(ResolvedType aGenericType, UnresolvedType[] someParameters) {
        StringBuffer ret = new StringBuffer();
        String rawSig = aGenericType.getErasureSignature();
        ret.append(rawSig.substring(0, rawSig.length() - 1));
        ret.append("<");
        for (int i = 0; i < someParameters.length; ++i) {
            if (someParameters[i] instanceof ReferenceType) {
                ret.append(((ReferenceType)someParameters[i]).getSignatureForAttribute());
                continue;
            }
            if (someParameters[i] instanceof ResolvedType.Primitive) {
                ret.append(((ResolvedType.Primitive)someParameters[i]).getSignatureForAttribute());
                continue;
            }
            throw new IllegalStateException("DebugFor325731: expected a ReferenceType or Primitive but was " + someParameters[i] + " of type " + someParameters[i].getClass().getName());
        }
        ret.append(">;");
        return ret.toString();
    }

    @Override
    public void ensureConsistent() {
        ReferenceType genericType;
        this.annotations = null;
        this.annotationTypes = null;
        this.newSuperclass = null;
        this.bits = 0;
        this.newInterfaces = null;
        this.typeVariables = null;
        this.parameterizedInterfaces.clear();
        this.superclassReference = new WeakReference<Object>(null);
        if (this.getDelegate() != null) {
            this.delegate.ensureConsistent();
        }
        if (this.isParameterizedOrRawType() && (genericType = this.getGenericType()) != null) {
            genericType.ensureConsistent();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addParent(ResolvedType newParent) {
        if (this.isRawType()) {
            throw new IllegalStateException("The raw type should never be accumulating new interfaces, they should be on the generic type.  Type is " + this.getName());
        }
        if (newParent.isClass()) {
            this.newSuperclass = newParent;
            this.superclassReference = new WeakReference<Object>(null);
        } else {
            if (this.newInterfaces == null) {
                this.newInterfaces = new ResolvedType[1];
                this.newInterfaces[0] = newParent;
            } else {
                ResolvedType[] existing = this.getDelegate().getDeclaredInterfaces();
                if (existing != null) {
                    for (int i = 0; i < existing.length; ++i) {
                        if (!existing[i].equals(newParent)) continue;
                        return;
                    }
                }
                ResolvedType[] newNewInterfaces = new ResolvedType[this.newInterfaces.length + 1];
                System.arraycopy(this.newInterfaces, 0, newNewInterfaces, 1, this.newInterfaces.length);
                newNewInterfaces[0] = newParent;
                this.newInterfaces = newNewInterfaces;
            }
            if (this.isGenericType()) {
                List<WeakReference<ReferenceType>> list = this.derivativeTypes;
                synchronized (list) {
                    for (WeakReference<ReferenceType> derivativeTypeRef : this.derivativeTypes) {
                        ReferenceType derivativeType = (ReferenceType)derivativeTypeRef.get();
                        if (derivativeType == null) continue;
                        derivativeType.parameterizedInterfaces.clear();
                    }
                }
            }
            this.parameterizedInterfaces.clear();
        }
    }

    private boolean equal(UnresolvedType[] typeParameters, ResolvedType[] resolvedParameters) {
        if (typeParameters.length != resolvedParameters.length) {
            return false;
        }
        int len = typeParameters.length;
        for (int p = 0; p < len; ++p) {
            if (typeParameters[p].equals(resolvedParameters[p])) continue;
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ReferenceType findDerivativeType(ResolvedType[] typeParameters) {
        List<WeakReference<ReferenceType>> list = this.derivativeTypes;
        synchronized (list) {
            ArrayList<WeakReference<ReferenceType>> forRemoval = new ArrayList<WeakReference<ReferenceType>>();
            for (WeakReference<ReferenceType> derivativeTypeRef : this.derivativeTypes) {
                ReferenceType derivativeType = (ReferenceType)derivativeTypeRef.get();
                if (derivativeType == null) {
                    forRemoval.add(derivativeTypeRef);
                    continue;
                }
                if (derivativeType.isRawType() || !this.equal(derivativeType.typeParameters, typeParameters)) continue;
                return derivativeType;
            }
            this.derivativeTypes.removeAll(forRemoval);
        }
        return null;
    }

    public boolean hasNewInterfaces() {
        return this.newInterfaces != null;
    }
}

