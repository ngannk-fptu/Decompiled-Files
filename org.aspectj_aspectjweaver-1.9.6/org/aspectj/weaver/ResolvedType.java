/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AnnotatedElement;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.AnnotationTargetKind;
import org.aspectj.weaver.ArrayReferenceType;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.BoundedReferenceType;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.CrosscuttingMembers;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Iterators;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MissingResolvedTypeWithKnownSignature;
import org.aspectj.weaver.NewFieldTypeMunger;
import org.aspectj.weaver.NewMethodTypeMunger;
import org.aspectj.weaver.NewParentTypeMunger;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.TypeFactory;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableReference;
import org.aspectj.weaver.TypeVariableReferenceType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.WeaverStateInfo;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.PerClause;

public abstract class ResolvedType
extends UnresolvedType
implements AnnotatedElement {
    public static final ResolvedType[] EMPTY_RESOLVED_TYPE_ARRAY = new ResolvedType[0];
    public static final String PARAMETERIZED_TYPE_IDENTIFIER = "P";
    public ResolvedType[] temporaryAnnotationTypes;
    private ResolvedType[] resolvedTypeParams;
    private String binaryPath;
    protected World world;
    protected int bits;
    private static int AnnotationBitsInitialized = 1;
    private static int AnnotationMarkedInherited = 2;
    private static int MungersAnalyzed = 4;
    private static int HasParentMunger = 8;
    private static int TypeHierarchyCompleteBit = 16;
    private static int GroovyObjectInitialized = 32;
    private static int IsGroovyObject = 64;
    private static int IsPrivilegedBitInitialized = 128;
    private static int IsPrivilegedAspect = 256;
    protected static Set<String> validBoxing = new HashSet<String>();
    private static final MethodGetter MethodGetterInstance;
    private static final MethodGetterIncludingItds MethodGetterWithItdsInstance;
    private static final PointcutGetter PointcutGetterInstance;
    private static final FieldGetter FieldGetterInstance;
    public CrosscuttingMembers crosscuttingMembers;
    public static final ResolvedType[] NONE;
    public static final ResolvedType[] EMPTY_ARRAY;
    public static final Missing MISSING;
    protected List<ConcreteTypeMunger> interTypeMungers = new ArrayList<ConcreteTypeMunger>();
    private FuzzyBoolean parameterizedWithTypeVariable = FuzzyBoolean.MAYBE;

    protected ResolvedType(String signature, World world) {
        super(signature);
        this.world = world;
    }

    protected ResolvedType(String signature, String signatureErasure, World world) {
        super(signature, signatureErasure);
        this.world = world;
    }

    @Override
    public int getSize() {
        return 1;
    }

    public final Iterator<ResolvedType> getDirectSupertypes() {
        Iterator<ResolvedType> interfacesIterator = Iterators.array(this.getDeclaredInterfaces());
        ResolvedType superclass = this.getSuperclass();
        if (superclass == null) {
            return interfacesIterator;
        }
        return Iterators.snoc(interfacesIterator, superclass);
    }

    public abstract ResolvedMember[] getDeclaredFields();

    public abstract ResolvedMember[] getDeclaredMethods();

    public abstract ResolvedType[] getDeclaredInterfaces();

    public abstract ResolvedMember[] getDeclaredPointcuts();

    public boolean isCacheable() {
        return true;
    }

    public abstract ResolvedType getSuperclass();

    public abstract int getModifiers();

    public boolean canBeSeenBy(ResolvedType from) {
        int targetMods = this.getModifiers();
        if (Modifier.isPublic(targetMods)) {
            return true;
        }
        if (Modifier.isPrivate(targetMods)) {
            return false;
        }
        return this.getPackageName().equals(from.getPackageName());
    }

    public boolean isMissing() {
        return false;
    }

    public static boolean isMissing(UnresolvedType unresolved) {
        if (unresolved instanceof ResolvedType) {
            ResolvedType resolved = (ResolvedType)unresolved;
            return resolved.isMissing();
        }
        return unresolved == MISSING;
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        return EMPTY_RESOLVED_TYPE_ARRAY;
    }

    @Override
    public AnnotationAJ getAnnotationOfType(UnresolvedType ofType) {
        return null;
    }

    public ResolvedType getResolvedComponentType() {
        return null;
    }

    public World getWorld() {
        return this.world;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ResolvedType) {
            return this == other;
        }
        return super.equals(other);
    }

    public Iterator<ResolvedMember> getFields() {
        final Iterators.Filter dupFilter = Iterators.dupFilter();
        Iterators.Getter<ResolvedType, ResolvedType> typeGetter = new Iterators.Getter<ResolvedType, ResolvedType>(){

            @Override
            public Iterator<ResolvedType> get(ResolvedType o) {
                return dupFilter.filter(o.getDirectSupertypes());
            }
        };
        return Iterators.mapOver(Iterators.recur(this, typeGetter), FieldGetterInstance);
    }

    public Iterator<ResolvedMember> getMethods(boolean wantGenerics, boolean wantDeclaredParents) {
        return Iterators.mapOver(this.getHierarchy(wantGenerics, wantDeclaredParents), MethodGetterInstance);
    }

    public Iterator<ResolvedMember> getMethodsIncludingIntertypeDeclarations(boolean wantGenerics, boolean wantDeclaredParents) {
        return Iterators.mapOver(this.getHierarchy(wantGenerics, wantDeclaredParents), MethodGetterWithItdsInstance);
    }

    public Iterator<ResolvedType> getHierarchy() {
        return this.getHierarchy(false, false);
    }

    public Iterator<ResolvedType> getHierarchy(final boolean wantGenerics, final boolean wantDeclaredParents) {
        Iterators.Getter<ResolvedType, ResolvedType> interfaceGetter = new Iterators.Getter<ResolvedType, ResolvedType>(){
            List<String> alreadySeen = new ArrayList<String>();

            @Override
            public Iterator<ResolvedType> get(ResolvedType type) {
                ResolvedType[] interfaces = type.getDeclaredInterfaces();
                if (!wantDeclaredParents && type.hasNewParentMungers()) {
                    ArrayList<Integer> forRemoval = new ArrayList<Integer>();
                    for (ConcreteTypeMunger munger : type.interTypeMungers) {
                        ResolvedTypeMunger m;
                        if (munger.getMunger() == null || (m = munger.getMunger()).getKind() != ResolvedTypeMunger.Parent) continue;
                        ResolvedType newType = ((NewParentTypeMunger)m).getNewParent();
                        if (!wantGenerics && newType.isParameterizedOrGenericType()) {
                            newType = newType.getRawType();
                        }
                        for (int ii = 0; ii < interfaces.length; ++ii) {
                            ResolvedType iface = interfaces[ii];
                            if (!wantGenerics && iface.isParameterizedOrGenericType()) {
                                iface = iface.getRawType();
                            }
                            if (!newType.getSignature().equals(iface.getSignature())) continue;
                            forRemoval.add(ii);
                        }
                    }
                    if (forRemoval.size() > 0) {
                        ResolvedType[] interfaces2 = new ResolvedType[interfaces.length - forRemoval.size()];
                        int p = 0;
                        for (int ii = 0; ii < interfaces.length; ++ii) {
                            if (forRemoval.contains(ii)) continue;
                            interfaces2[p++] = interfaces[ii];
                        }
                        interfaces = interfaces2;
                    }
                }
                return new Iterators.ResolvedTypeArrayIterator(interfaces, this.alreadySeen, wantGenerics);
            }
        };
        if (this.isInterface()) {
            return new SuperInterfaceWalker(interfaceGetter, this);
        }
        SuperInterfaceWalker superInterfaceWalker = new SuperInterfaceWalker(interfaceGetter);
        SuperClassWalker superClassesIterator = new SuperClassWalker(this, superInterfaceWalker, wantGenerics);
        return Iterators.append1(superClassesIterator, superInterfaceWalker);
    }

    public List<ResolvedMember> getMethodsWithoutIterator(boolean includeITDs, boolean allowMissing, boolean genericsAware) {
        ArrayList<ResolvedMember> methods = new ArrayList<ResolvedMember>();
        HashSet<String> knowninterfaces = new HashSet<String>();
        this.addAndRecurse(knowninterfaces, methods, this, includeITDs, allowMissing, genericsAware);
        return methods;
    }

    public List<ResolvedType> getHierarchyWithoutIterator(boolean includeITDs, boolean allowMissing, boolean genericsAware) {
        ArrayList<ResolvedType> types = new ArrayList<ResolvedType>();
        HashSet<String> visited = new HashSet<String>();
        this.recurseHierarchy(visited, types, this, includeITDs, allowMissing, genericsAware);
        return types;
    }

    private void addAndRecurse(Set<String> knowninterfaces, List<ResolvedMember> collector, ResolvedType resolvedType, boolean includeITDs, boolean allowMissing, boolean genericsAware) {
        ResolvedType superType;
        collector.addAll(Arrays.asList(resolvedType.getDeclaredMethods()));
        if (includeITDs && resolvedType.interTypeMungers != null) {
            for (ConcreteTypeMunger typeTransformer : this.interTypeMungers) {
                ResolvedMember rm = typeTransformer.getSignature();
                if (rm == null) continue;
                collector.add(typeTransformer.getSignature());
            }
        }
        if (!(resolvedType.isInterface() || resolvedType.equals(OBJECT) || (superType = resolvedType.getSuperclass()) == null || superType.isMissing())) {
            if (!genericsAware && superType.isParameterizedOrGenericType()) {
                superType = superType.getRawType();
            }
            this.addAndRecurse(knowninterfaces, collector, superType, includeITDs, allowMissing, genericsAware);
        }
        ResolvedType[] interfaces = resolvedType.getDeclaredInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            ResolvedType iface = interfaces[i];
            if (!genericsAware && iface.isParameterizedOrGenericType()) {
                iface = iface.getRawType();
            }
            boolean shouldSkip = false;
            for (int j = 0; j < resolvedType.interTypeMungers.size(); ++j) {
                ConcreteTypeMunger munger = resolvedType.interTypeMungers.get(j);
                if (munger.getMunger() == null || munger.getMunger().getKind() != ResolvedTypeMunger.Parent || !((NewParentTypeMunger)munger.getMunger()).getNewParent().equals(iface)) continue;
                shouldSkip = true;
                break;
            }
            if (shouldSkip || knowninterfaces.contains(iface.getSignature())) continue;
            knowninterfaces.add(iface.getSignature());
            if (allowMissing && iface.isMissing()) {
                if (!(iface instanceof MissingResolvedTypeWithKnownSignature)) continue;
                ((MissingResolvedTypeWithKnownSignature)iface).raiseWarningOnMissingInterfaceWhilstFindingMethods();
                continue;
            }
            this.addAndRecurse(knowninterfaces, collector, iface, includeITDs, allowMissing, genericsAware);
        }
    }

    private void recurseHierarchy(Set<String> knowninterfaces, List<ResolvedType> collector, ResolvedType resolvedType, boolean includeITDs, boolean allowMissing, boolean genericsAware) {
        ResolvedType superType;
        collector.add(resolvedType);
        if (!(resolvedType.isInterface() || resolvedType.equals(OBJECT) || (superType = resolvedType.getSuperclass()) == null || superType.isMissing())) {
            if (!genericsAware && (superType.isParameterizedType() || superType.isGenericType())) {
                superType = superType.getRawType();
            }
            this.recurseHierarchy(knowninterfaces, collector, superType, includeITDs, allowMissing, genericsAware);
        }
        ResolvedType[] interfaces = resolvedType.getDeclaredInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            ResolvedType iface = interfaces[i];
            if (!genericsAware && (iface.isParameterizedType() || iface.isGenericType())) {
                iface = iface.getRawType();
            }
            boolean shouldSkip = false;
            for (int j = 0; j < resolvedType.interTypeMungers.size(); ++j) {
                ConcreteTypeMunger munger = resolvedType.interTypeMungers.get(j);
                if (munger.getMunger() == null || munger.getMunger().getKind() != ResolvedTypeMunger.Parent || !((NewParentTypeMunger)munger.getMunger()).getNewParent().equals(iface)) continue;
                shouldSkip = true;
                break;
            }
            if (shouldSkip || knowninterfaces.contains(iface.getSignature())) continue;
            knowninterfaces.add(iface.getSignature());
            if (allowMissing && iface.isMissing()) {
                if (!(iface instanceof MissingResolvedTypeWithKnownSignature)) continue;
                ((MissingResolvedTypeWithKnownSignature)iface).raiseWarningOnMissingInterfaceWhilstFindingMethods();
                continue;
            }
            this.recurseHierarchy(knowninterfaces, collector, iface, includeITDs, allowMissing, genericsAware);
        }
    }

    public ResolvedType[] getResolvedTypeParameters() {
        if (this.resolvedTypeParams == null) {
            this.resolvedTypeParams = this.world.resolve(this.typeParameters);
        }
        return this.resolvedTypeParams;
    }

    public ResolvedMember lookupField(Member field) {
        Iterator<ResolvedMember> i = this.getFields();
        while (i.hasNext()) {
            ResolvedMember resolvedMember = i.next();
            if (ResolvedType.matches(resolvedMember, field)) {
                return resolvedMember;
            }
            if (!resolvedMember.hasBackingGenericMember() || !field.getName().equals(resolvedMember.getName()) || !ResolvedType.matches(resolvedMember.getBackingGenericMember(), field)) continue;
            return resolvedMember;
        }
        return null;
    }

    public ResolvedMember lookupMethod(Member m) {
        ArrayList<ResolvedType> typesTolookat = new ArrayList<ResolvedType>();
        typesTolookat.add(this);
        int pos = 0;
        while (pos < typesTolookat.size()) {
            ResolvedType[] superinterfaces;
            ResolvedType superclass;
            ResolvedMember[] methods;
            ResolvedType type;
            if (!(type = (ResolvedType)typesTolookat.get(pos++)).isMissing() && (methods = type.getDeclaredMethods()) != null) {
                for (int i = 0; i < methods.length; ++i) {
                    ResolvedMember method = methods[i];
                    if (ResolvedType.matches(method, m)) {
                        return method;
                    }
                    if (!method.hasBackingGenericMember() || !m.getName().equals(method.getName()) || !ResolvedType.matches(method.getBackingGenericMember(), m)) continue;
                    return method;
                }
            }
            if ((superclass = type.getSuperclass()) != null) {
                typesTolookat.add(superclass);
            }
            if ((superinterfaces = type.getDeclaredInterfaces()) == null) continue;
            for (int i = 0; i < superinterfaces.length; ++i) {
                ResolvedType interf = superinterfaces[i];
                if (typesTolookat.contains(interf)) continue;
                typesTolookat.add(interf);
            }
        }
        return null;
    }

    public ResolvedMember lookupMethodInITDs(Member member) {
        for (ConcreteTypeMunger typeTransformer : this.interTypeMungers) {
            if (!ResolvedType.matches(typeTransformer.getSignature(), member)) continue;
            return typeTransformer.getSignature();
        }
        return null;
    }

    private ResolvedMember lookupMember(Member m, ResolvedMember[] a) {
        for (int i = 0; i < a.length; ++i) {
            ResolvedMember f = a[i];
            if (!ResolvedType.matches(f, m)) continue;
            return f;
        }
        return null;
    }

    public ResolvedMember lookupResolvedMember(ResolvedMember aMember, boolean allowMissing, boolean eraseGenerics) {
        Iterator<ResolvedMember> toSearch = null;
        ResolvedMember found = null;
        if (aMember.getKind() == Member.METHOD || aMember.getKind() == Member.CONSTRUCTOR) {
            toSearch = this.getMethodsIncludingIntertypeDeclarations(!eraseGenerics, true);
        } else {
            if (aMember.getKind() == Member.ADVICE) {
                return null;
            }
            assert (aMember.getKind() == Member.FIELD);
            toSearch = this.getFields();
        }
        while (toSearch.hasNext()) {
            ResolvedMember candidate = toSearch.next();
            if (eraseGenerics && candidate.hasBackingGenericMember()) {
                candidate = candidate.getBackingGenericMember();
            }
            if (!candidate.matches(aMember, eraseGenerics)) continue;
            found = candidate;
            break;
        }
        return found;
    }

    public static boolean matches(Member m1, Member m2) {
        if (m1 == null) {
            return m2 == null;
        }
        if (m2 == null) {
            return false;
        }
        boolean equalNames = m1.getName().equals(m2.getName());
        if (!equalNames) {
            return false;
        }
        boolean equalSignatures = m1.getSignature().equals(m2.getSignature());
        if (equalSignatures) {
            return true;
        }
        boolean equalCovariantSignatures = m1.getParameterSignature().equals(m2.getParameterSignature());
        return equalCovariantSignatures;
    }

    public static boolean conflictingSignature(Member m1, Member m2) {
        return ResolvedType.conflictingSignature(m1, m2, true);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean conflictingSignature(Member m1, Member m2, boolean v2itds) {
        int n;
        if (m1 == null || m2 == null) {
            return false;
        }
        if (!m1.getName().equals(m2.getName())) {
            return false;
        }
        if (m1.getKind() != m2.getKind()) {
            return false;
        }
        if (m1.getKind() == Member.FIELD) {
            if (!v2itds) return m1.getDeclaringType().equals(m2.getDeclaringType());
            if (m1.getDeclaringType().equals(m2.getDeclaringType())) {
                return true;
            }
        } else if (m1.getKind() == Member.POINTCUT) {
            return true;
        }
        UnresolvedType[] p1 = m1.getGenericParameterTypes();
        UnresolvedType[] p2 = m2.getGenericParameterTypes();
        if (p1 == null) {
            p1 = m1.getParameterTypes();
        }
        if (p2 == null) {
            p2 = m2.getParameterTypes();
        }
        if ((n = p1.length) != p2.length) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            if (p1[i].equals(p2[i])) continue;
            return false;
        }
        return true;
    }

    public Iterator<ResolvedMember> getPointcuts() {
        final Iterators.Filter dupFilter = Iterators.dupFilter();
        Iterators.Getter<ResolvedType, ResolvedType> typeGetter = new Iterators.Getter<ResolvedType, ResolvedType>(){

            @Override
            public Iterator<ResolvedType> get(ResolvedType o) {
                return dupFilter.filter(o.getDirectSupertypes());
            }
        };
        return Iterators.mapOver(Iterators.recur(this, typeGetter), PointcutGetterInstance);
    }

    public ResolvedPointcutDefinition findPointcut(String name) {
        Iterator<ResolvedMember> i = this.getPointcuts();
        while (i.hasNext()) {
            ResolvedPointcutDefinition f = (ResolvedPointcutDefinition)i.next();
            if (f == null || !name.equals(f.getName())) continue;
            return f;
        }
        if (!this.getOutermostType().equals(this)) {
            ResolvedType outerType = this.getOutermostType().resolve(this.world);
            ResolvedPointcutDefinition rpd = outerType.findPointcut(name);
            return rpd;
        }
        return null;
    }

    public CrosscuttingMembers collectCrosscuttingMembers(boolean shouldConcretizeIfNeeded) {
        this.crosscuttingMembers = new CrosscuttingMembers(this, shouldConcretizeIfNeeded);
        if (this.getPerClause() == null) {
            return this.crosscuttingMembers;
        }
        this.crosscuttingMembers.setPerClause(this.getPerClause());
        this.crosscuttingMembers.addShadowMungers(this.collectShadowMungers());
        this.crosscuttingMembers.addTypeMungers(this.getTypeMungers());
        this.crosscuttingMembers.addDeclares(this.collectDeclares(!this.doesNotExposeShadowMungers()));
        this.crosscuttingMembers.addPrivilegedAccesses(this.getPrivilegedAccesses());
        return this.crosscuttingMembers;
    }

    public final List<Declare> collectDeclares(boolean includeAdviceLike) {
        if (!this.isAspect()) {
            return Collections.emptyList();
        }
        ArrayList<Declare> ret = new ArrayList<Declare>();
        if (!this.isAbstract()) {
            final Iterators.Filter dupFilter = Iterators.dupFilter();
            Iterators.Getter<ResolvedType, ResolvedType> typeGetter = new Iterators.Getter<ResolvedType, ResolvedType>(){

                @Override
                public Iterator<ResolvedType> get(ResolvedType o) {
                    return dupFilter.filter(o.getDirectSupertypes());
                }
            };
            Iterator<ResolvedType> typeIterator = Iterators.recur(this, typeGetter);
            while (typeIterator.hasNext()) {
                ResolvedType ty = typeIterator.next();
                for (Declare dec : ty.getDeclares()) {
                    if (dec.isAdviceLike()) {
                        if (!includeAdviceLike) continue;
                        ret.add(dec);
                        continue;
                    }
                    ret.add(dec);
                }
            }
        }
        return ret;
    }

    private final List<ShadowMunger> collectShadowMungers() {
        if (!this.isAspect() || this.isAbstract() || this.doesNotExposeShadowMungers()) {
            return Collections.emptyList();
        }
        ArrayList<ShadowMunger> acc = new ArrayList<ShadowMunger>();
        final Iterators.Filter dupFilter = Iterators.dupFilter();
        Iterators.Getter<ResolvedType, ResolvedType> typeGetter = new Iterators.Getter<ResolvedType, ResolvedType>(){

            @Override
            public Iterator<ResolvedType> get(ResolvedType o) {
                return dupFilter.filter(o.getDirectSupertypes());
            }
        };
        Iterator<ResolvedType> typeIterator = Iterators.recur(this, typeGetter);
        while (typeIterator.hasNext()) {
            ResolvedType ty = typeIterator.next();
            acc.addAll(ty.getDeclaredShadowMungers());
        }
        return acc;
    }

    public void addParent(ResolvedType newParent) {
    }

    protected boolean doesNotExposeShadowMungers() {
        return false;
    }

    public PerClause getPerClause() {
        return null;
    }

    public Collection<Declare> getDeclares() {
        return Collections.emptyList();
    }

    public Collection<ConcreteTypeMunger> getTypeMungers() {
        return Collections.emptyList();
    }

    public Collection<ResolvedMember> getPrivilegedAccesses() {
        return Collections.emptyList();
    }

    public final boolean isInterface() {
        return Modifier.isInterface(this.getModifiers());
    }

    public final boolean isAbstract() {
        return Modifier.isAbstract(this.getModifiers());
    }

    public boolean isClass() {
        return false;
    }

    public boolean isAspect() {
        return false;
    }

    public boolean isAnnotationStyleAspect() {
        return false;
    }

    public boolean isEnum() {
        return false;
    }

    public boolean isAnnotation() {
        return false;
    }

    public boolean isAnonymous() {
        return false;
    }

    public boolean isNested() {
        return false;
    }

    public ResolvedType getOuterClass() {
        return null;
    }

    public void addAnnotation(AnnotationAJ annotationX) {
        throw new RuntimeException("ResolvedType.addAnnotation() should never be called");
    }

    public AnnotationAJ[] getAnnotations() {
        throw new RuntimeException("ResolvedType.getAnnotations() should never be called");
    }

    public boolean hasAnnotations() {
        throw new RuntimeException("ResolvedType.getAnnotations() should never be called");
    }

    public boolean canAnnotationTargetType() {
        return false;
    }

    public AnnotationTargetKind[] getAnnotationTargetKinds() {
        return null;
    }

    public boolean isAnnotationWithRuntimeRetention() {
        return false;
    }

    public boolean isSynthetic() {
        return this.signature.indexOf("$ajc") != -1;
    }

    public final boolean isFinal() {
        return Modifier.isFinal(this.getModifiers());
    }

    protected Map<String, UnresolvedType> getMemberParameterizationMap() {
        if (!this.isParameterizedType()) {
            return Collections.emptyMap();
        }
        TypeVariable[] tvs = this.getGenericType().getTypeVariables();
        HashMap<String, UnresolvedType> parameterizationMap = new HashMap<String, UnresolvedType>();
        if (tvs.length != this.typeParameters.length) {
            this.world.getMessageHandler().handleMessage(new Message("Mismatch when building parameterization map. For type '" + this.signature + "' expecting " + tvs.length + ":[" + this.toString(tvs) + "] type parameters but found " + this.typeParameters.length + ":[" + this.toString(this.typeParameters) + "]", "", IMessage.ERROR, this.getSourceLocation(), null, new ISourceLocation[]{this.getSourceLocation()}));
        } else {
            for (int i = 0; i < tvs.length; ++i) {
                parameterizationMap.put(tvs[i].getName(), this.typeParameters[i]);
            }
        }
        return parameterizationMap;
    }

    private String toString(UnresolvedType[] typeParameters) {
        StringBuilder s = new StringBuilder();
        for (UnresolvedType tv : typeParameters) {
            s.append(tv.getSignature()).append(" ");
        }
        return s.toString().trim();
    }

    private String toString(TypeVariable[] tvs) {
        StringBuilder s = new StringBuilder();
        for (TypeVariable tv : tvs) {
            s.append(tv.getName()).append(" ");
        }
        return s.toString().trim();
    }

    public List<ShadowMunger> getDeclaredAdvice() {
        ArrayList<ShadowMunger> l = new ArrayList<ShadowMunger>();
        ResolvedMember[] methods = this.getDeclaredMethods();
        if (this.isParameterizedType()) {
            methods = this.getGenericType().getDeclaredMethods();
        }
        Map<String, UnresolvedType> typeVariableMap = this.getAjMemberParameterizationMap();
        int len = methods.length;
        for (int i = 0; i < len; ++i) {
            ShadowMunger munger = methods[i].getAssociatedShadowMunger();
            if (munger == null) continue;
            if (this.ajMembersNeedParameterization() && (munger = munger.parameterizeWith(this, typeVariableMap)) instanceof Advice) {
                Advice advice = (Advice)munger;
                UnresolvedType[] ptypes = methods[i].getGenericParameterTypes();
                UnresolvedType[] newPTypes = new UnresolvedType[ptypes.length];
                for (int j = 0; j < ptypes.length; ++j) {
                    if (ptypes[j] instanceof TypeVariableReferenceType) {
                        TypeVariableReferenceType tvrt = (TypeVariableReferenceType)ptypes[j];
                        if (typeVariableMap.containsKey(tvrt.getTypeVariable().getName())) {
                            newPTypes[j] = typeVariableMap.get(tvrt.getTypeVariable().getName());
                            continue;
                        }
                        newPTypes[j] = ptypes[j];
                        continue;
                    }
                    newPTypes[j] = ptypes[j];
                }
                advice.setBindingParameterTypes(newPTypes);
            }
            munger.setDeclaringType(this);
            l.add(munger);
        }
        return l;
    }

    public List<ShadowMunger> getDeclaredShadowMungers() {
        return this.getDeclaredAdvice();
    }

    public ResolvedMember[] getDeclaredJavaFields() {
        return this.filterInJavaVisible(this.getDeclaredFields());
    }

    public ResolvedMember[] getDeclaredJavaMethods() {
        return this.filterInJavaVisible(this.getDeclaredMethods());
    }

    private ResolvedMember[] filterInJavaVisible(ResolvedMember[] ms) {
        ArrayList<ResolvedMember> l = new ArrayList<ResolvedMember>();
        int len = ms.length;
        for (int i = 0; i < len; ++i) {
            if (ms[i].isAjSynthetic() || ms[i].getAssociatedShadowMunger() != null) continue;
            l.add(ms[i]);
        }
        return l.toArray(new ResolvedMember[l.size()]);
    }

    public abstract ISourceContext getSourceContext();

    public static ResolvedType makeArray(ResolvedType type, int dim) {
        if (dim == 0) {
            return type;
        }
        ArrayReferenceType array = new ArrayReferenceType("[" + type.getSignature(), "[" + type.getErasureSignature(), type.getWorld(), type);
        return ResolvedType.makeArray(array, dim - 1);
    }

    public ResolvedMember lookupMemberNoSupers(Member member) {
        ResolvedMember ret = this.lookupDirectlyDeclaredMemberNoSupers(member);
        if (ret == null && this.interTypeMungers != null) {
            for (ConcreteTypeMunger tm : this.interTypeMungers) {
                if (!ResolvedType.matches(tm.getSignature(), member)) continue;
                return tm.getSignature();
            }
        }
        return ret;
    }

    public ResolvedMember lookupMemberWithSupersAndITDs(Member member) {
        ResolvedMember ret = this.lookupMemberNoSupers(member);
        if (ret != null) {
            return ret;
        }
        ResolvedType supert = this.getSuperclass();
        while (ret == null && supert != null) {
            ret = supert.lookupMemberNoSupers(member);
            if (ret != null) continue;
            supert = supert.getSuperclass();
        }
        return ret;
    }

    public ResolvedMember lookupDirectlyDeclaredMemberNoSupers(Member member) {
        ResolvedMember ret = member.getKind() == Member.FIELD ? this.lookupMember(member, this.getDeclaredFields()) : this.lookupMember(member, this.getDeclaredMethods());
        return ret;
    }

    public ResolvedMember lookupMemberIncludingITDsOnInterfaces(Member member) {
        return this.lookupMemberIncludingITDsOnInterfaces(member, this);
    }

    private ResolvedMember lookupMemberIncludingITDsOnInterfaces(Member member, ResolvedType onType) {
        ResolvedMember ret = onType.lookupMemberNoSupers(member);
        if (ret != null) {
            return ret;
        }
        ResolvedType superType = onType.getSuperclass();
        if (superType != null) {
            ret = this.lookupMemberIncludingITDsOnInterfaces(member, superType);
        }
        if (ret == null) {
            ResolvedType[] superInterfaces = onType.getDeclaredInterfaces();
            for (int i = 0; i < superInterfaces.length; ++i) {
                ret = superInterfaces[i].lookupMethodInITDs(member);
                if (ret == null) continue;
                return ret;
            }
        }
        return ret;
    }

    public List<ConcreteTypeMunger> getInterTypeMungers() {
        return this.interTypeMungers;
    }

    public List<ConcreteTypeMunger> getInterTypeParentMungers() {
        ArrayList<ConcreteTypeMunger> l = new ArrayList<ConcreteTypeMunger>();
        for (ConcreteTypeMunger element : this.interTypeMungers) {
            if (!(element.getMunger() instanceof NewParentTypeMunger)) continue;
            l.add(element);
        }
        return l;
    }

    public List<ConcreteTypeMunger> getInterTypeMungersIncludingSupers() {
        ArrayList<ConcreteTypeMunger> ret = new ArrayList<ConcreteTypeMunger>();
        this.collectInterTypeMungers(ret);
        return ret;
    }

    public List<ConcreteTypeMunger> getInterTypeParentMungersIncludingSupers() {
        ArrayList<ConcreteTypeMunger> ret = new ArrayList<ConcreteTypeMunger>();
        this.collectInterTypeParentMungers(ret);
        return ret;
    }

    private void collectInterTypeParentMungers(List<ConcreteTypeMunger> collector) {
        Iterator<ResolvedType> iter = this.getDirectSupertypes();
        while (iter.hasNext()) {
            ResolvedType superType = iter.next();
            superType.collectInterTypeParentMungers(collector);
        }
        collector.addAll(this.getInterTypeParentMungers());
    }

    protected void collectInterTypeMungers(List<ConcreteTypeMunger> collector) {
        Iterator<ResolvedType> iter = this.getDirectSupertypes();
        while (iter.hasNext()) {
            ResolvedType superType = iter.next();
            if (superType == null) {
                throw new BCException("UnexpectedProblem: a supertype in the hierarchy for " + this.getName() + " is null");
            }
            superType.collectInterTypeMungers(collector);
        }
        Iterator<ConcreteTypeMunger> iter1 = collector.iterator();
        block1: while (iter1.hasNext()) {
            ConcreteTypeMunger superMunger = iter1.next();
            if (superMunger.getSignature() == null || !superMunger.getSignature().isAbstract()) continue;
            for (ConcreteTypeMunger myMunger : this.getInterTypeMungers()) {
                if (!ResolvedType.conflictingSignature(myMunger.getSignature(), superMunger.getSignature())) continue;
                iter1.remove();
                continue block1;
            }
            if (!superMunger.getSignature().isPublic()) continue;
            Iterator<ResolvedMember> iter2 = this.getMethods(true, true);
            while (iter2.hasNext()) {
                ResolvedMember method = iter2.next();
                if (!ResolvedType.conflictingSignature(method, superMunger.getSignature())) continue;
                iter1.remove();
                continue block1;
            }
        }
        collector.addAll(this.getInterTypeMungers());
    }

    public void checkInterTypeMungers() {
        if (this.isAbstract()) {
            return;
        }
        boolean itdProblem = false;
        for (ConcreteTypeMunger munger : this.getInterTypeMungersIncludingSupers()) {
            itdProblem = this.checkAbstractDeclaration(munger) || itdProblem;
        }
        if (itdProblem) {
            return;
        }
        for (ConcreteTypeMunger munger : this.getInterTypeMungersIncludingSupers()) {
            if (munger.getSignature() == null || !munger.getSignature().isAbstract() || munger.getMunger().getKind() == ResolvedTypeMunger.PrivilegedAccess || munger.getMunger().getKind() == ResolvedTypeMunger.MethodDelegate2) continue;
            this.world.getMessageHandler().handleMessage(new Message("must implement abstract inter-type declaration: " + munger.getSignature(), "", IMessage.ERROR, this.getSourceLocation(), null, new ISourceLocation[]{this.getMungerLocation(munger)}));
        }
    }

    private boolean checkAbstractDeclaration(ConcreteTypeMunger munger) {
        ResolvedMember itdMember;
        ResolvedType onType;
        if (munger.getMunger() != null && munger.getMunger() instanceof NewMethodTypeMunger && (onType = (itdMember = munger.getSignature()).getDeclaringType().resolve(this.world)).isInterface() && itdMember.isAbstract() && !itdMember.isPublic()) {
            this.world.getMessageHandler().handleMessage(new Message(WeaverMessages.format("itdAbstractMustBePublicOnInterface", munger.getSignature(), onType), "", Message.ERROR, this.getSourceLocation(), null, new ISourceLocation[]{this.getMungerLocation(munger)}));
            return true;
        }
        return false;
    }

    private ISourceLocation getMungerLocation(ConcreteTypeMunger munger) {
        ISourceLocation sloc = munger.getSourceLocation();
        if (sloc == null) {
            sloc = munger.getAspectType().getSourceLocation();
        }
        return sloc;
    }

    public ResolvedType getDeclaringType() {
        if (this.isArray()) {
            return null;
        }
        if (this.isNested() || this.isAnonymous()) {
            return this.getOuterClass();
        }
        return null;
    }

    public static boolean isVisible(int modifiers, ResolvedType targetType, ResolvedType fromType) {
        if (Modifier.isPublic(modifiers)) {
            return true;
        }
        if (Modifier.isPrivate(modifiers)) {
            return targetType.getOutermostType().equals(fromType.getOutermostType());
        }
        if (Modifier.isProtected(modifiers)) {
            return ResolvedType.samePackage(targetType, fromType) || targetType.isAssignableFrom(fromType);
        }
        return ResolvedType.samePackage(targetType, fromType);
    }

    private static boolean samePackage(ResolvedType targetType, ResolvedType fromType) {
        String p1 = targetType.getPackageName();
        String p2 = fromType.getPackageName();
        if (p1 == null) {
            return p2 == null;
        }
        if (p2 == null) {
            return false;
        }
        return p1.equals(p2);
    }

    private boolean genericTypeEquals(ResolvedType other) {
        ResolvedType rt = other;
        if (rt.isParameterizedType() || rt.isRawType()) {
            rt.getGenericType();
        }
        return (this.isParameterizedType() || this.isRawType()) && this.getGenericType().equals(rt) || this.equals(other);
    }

    public ResolvedType discoverActualOccurrenceOfTypeInHierarchy(ResolvedType lookingFor) {
        if (!lookingFor.isGenericType()) {
            throw new BCException("assertion failed: method should only be called with generic type, but " + lookingFor + " is " + lookingFor.typeKind);
        }
        if (this.equals(OBJECT)) {
            return null;
        }
        if (this.genericTypeEquals(lookingFor)) {
            return this;
        }
        ResolvedType superT = this.getSuperclass();
        if (superT.genericTypeEquals(lookingFor)) {
            return superT;
        }
        ResolvedType[] superIs = this.getDeclaredInterfaces();
        for (int i = 0; i < superIs.length; ++i) {
            ResolvedType superI = superIs[i];
            if (superI.genericTypeEquals(lookingFor)) {
                return superI;
            }
            ResolvedType checkTheSuperI = superI.discoverActualOccurrenceOfTypeInHierarchy(lookingFor);
            if (checkTheSuperI == null) continue;
            return checkTheSuperI;
        }
        return superT.discoverActualOccurrenceOfTypeInHierarchy(lookingFor);
    }

    public ConcreteTypeMunger fillInAnyTypeParameters(ConcreteTypeMunger munger) {
        boolean debug = false;
        ResolvedMember member = munger.getSignature();
        if (munger.isTargetTypeParameterized()) {
            ResolvedType actualTarget;
            ResolvedType onTypeResolved;
            ReferenceType onType;
            if (debug) {
                System.err.println("Processing attempted parameterization of " + munger + " targetting type " + this);
            }
            if (debug) {
                System.err.println("  This type is " + this + "  (" + this.typeKind + ")");
            }
            if (debug) {
                System.err.println("  Signature that needs parameterizing: " + member);
            }
            if ((onType = (onTypeResolved = this.world.resolve(member.getDeclaringType())).getGenericType()) == null) {
                this.getWorld().getMessageHandler().handleMessage(MessageUtil.error("The target type for the intertype declaration is not generic", munger.getSourceLocation()));
                return munger;
            }
            member.resolve(this.world);
            if (debug) {
                System.err.println("  Actual target ontype: " + onType + "  (" + onType.typeKind + ")");
            }
            if ((actualTarget = this.discoverActualOccurrenceOfTypeInHierarchy(onType)) == null) {
                throw new BCException("assertion failed: asked " + this + " for occurrence of " + onType + " in its hierarchy??");
            }
            if (!actualTarget.isGenericType() && debug) {
                System.err.println("Occurrence in " + this + " is actually " + actualTarget + "  (" + actualTarget.typeKind + ")");
            }
            munger = munger.parameterizedFor(actualTarget);
            if (debug) {
                System.err.println("New sig: " + munger.getSignature());
            }
            if (debug) {
                System.err.println("=====================================");
            }
        }
        return munger;
    }

    public void addInterTypeMunger(ConcreteTypeMunger munger, boolean isDuringCompilation) {
        ResolvedMember sig = munger.getSignature();
        this.bits &= ~MungersAnalyzed;
        if (sig == null || munger.getMunger() == null || munger.getMunger().getKind() == ResolvedTypeMunger.PrivilegedAccess) {
            this.interTypeMungers.add(munger);
            return;
        }
        sig = (munger = this.fillInAnyTypeParameters(munger)).getSignature();
        if (sig.getKind() == Member.METHOD) {
            if (this.clashesWithExistingMember(munger, this.getMethods(true, false))) {
                return;
            }
            if (this.isInterface() && this.clashesWithExistingMember(munger, Arrays.asList(this.world.getCoreType(OBJECT).getDeclaredMethods()).iterator())) {
                return;
            }
        } else if (sig.getKind() == Member.FIELD) {
            ResolvedTypeMunger thisRealMunger;
            if (this.clashesWithExistingMember(munger, Arrays.asList(this.getDeclaredFields()).iterator())) {
                return;
            }
            if (!isDuringCompilation && (thisRealMunger = munger.getMunger()) instanceof NewFieldTypeMunger) {
                NewFieldTypeMunger newFieldTypeMunger = (NewFieldTypeMunger)thisRealMunger;
                if (newFieldTypeMunger.version == 2) {
                    String thisRealMungerSignatureName = newFieldTypeMunger.getSignature().getName();
                    for (ConcreteTypeMunger typeMunger : this.interTypeMungers) {
                        NewFieldTypeMunger existing;
                        if (!(typeMunger.getMunger() instanceof NewFieldTypeMunger) || typeMunger.getSignature().getKind() != Member.FIELD || !(existing = (NewFieldTypeMunger)typeMunger.getMunger()).getSignature().getName().equals(thisRealMungerSignatureName) || existing.version != 2 || !existing.getSignature().getDeclaringType().equals(newFieldTypeMunger.getSignature().getDeclaringType())) continue;
                        StringBuffer sb = new StringBuffer();
                        sb.append("Cannot handle two aspects both attempting to use new style ITDs for the same named field ");
                        sb.append("on the same target type.  Please recompile at least one aspect with '-Xset:itdVersion=1'.");
                        sb.append(" Aspects involved: " + munger.getAspectType().getName() + " and " + typeMunger.getAspectType().getName() + ".");
                        sb.append(" Field is named '" + existing.getSignature().getName() + "'");
                        this.getWorld().getMessageHandler().handleMessage(new Message(sb.toString(), this.getSourceLocation(), true));
                        return;
                    }
                }
            }
        } else if (this.clashesWithExistingMember(munger, Arrays.asList(this.getDeclaredMethods()).iterator())) {
            return;
        }
        boolean needsAdding = true;
        boolean needsToBeAddedEarlier = false;
        Iterator<ConcreteTypeMunger> i = this.interTypeMungers.iterator();
        while (i.hasNext()) {
            boolean v2itds;
            ConcreteTypeMunger existingMunger = i.next();
            boolean bl = v2itds = munger.getSignature().getKind() == Member.FIELD && munger.getMunger() instanceof NewFieldTypeMunger && ((NewFieldTypeMunger)munger.getMunger()).version == 2;
            if (!ResolvedType.conflictingSignature(existingMunger.getSignature(), munger.getSignature(), v2itds) || !ResolvedType.isVisible(munger.getSignature().getModifiers(), munger.getAspectType(), existingMunger.getAspectType())) continue;
            int c = this.compareMemberPrecedence(sig, existingMunger.getSignature());
            if (c == 0) {
                c = this.getWorld().compareByPrecedenceAndHierarchy(munger.getAspectType(), existingMunger.getAspectType());
            }
            if (c < 0) {
                this.checkLegalOverride(munger.getSignature(), existingMunger.getSignature(), 17, null);
                needsAdding = false;
                if (munger.getSignature().getKind() != Member.FIELD || !munger.getSignature().getDeclaringType().resolve(this.world).isInterface() || ((NewFieldTypeMunger)munger.getMunger()).version != 2) break;
                needsAdding = true;
                break;
            }
            if (c > 0) {
                this.checkLegalOverride(existingMunger.getSignature(), munger.getSignature(), 17, null);
                if (existingMunger.getSignature().getKind() == Member.FIELD && existingMunger.getSignature().getDeclaringType().resolve(this.world).isInterface() && ((NewFieldTypeMunger)existingMunger.getMunger()).version == 2) {
                    needsToBeAddedEarlier = true;
                    break;
                }
                i.remove();
                break;
            }
            this.interTypeConflictError(munger, existingMunger);
            this.interTypeConflictError(existingMunger, munger);
            return;
        }
        if (needsAdding) {
            if (!needsToBeAddedEarlier) {
                this.interTypeMungers.add(munger);
            } else {
                this.interTypeMungers.add(0, munger);
            }
        }
    }

    private boolean clashesWithExistingMember(ConcreteTypeMunger typeTransformer, Iterator<ResolvedMember> existingMembers) {
        ResolvedMember typeTransformerSignature = typeTransformer.getSignature();
        ResolvedTypeMunger rtm = typeTransformer.getMunger();
        boolean v2itds = true;
        if (rtm instanceof NewFieldTypeMunger && ((NewFieldTypeMunger)rtm).version == 1) {
            v2itds = false;
        }
        while (existingMembers.hasNext()) {
            ResolvedMember existingMember = existingMembers.next();
            if (existingMember.isBridgeMethod() || !ResolvedType.conflictingSignature(existingMember, typeTransformerSignature, v2itds)) continue;
            if (ResolvedType.isVisible(existingMember.getModifiers(), this, typeTransformer.getAspectType())) {
                List<ConcreteTypeMunger> mungersAffectingThisType;
                int c = this.compareMemberPrecedence(typeTransformerSignature, existingMember);
                if (c < 0) {
                    ResolvedType typeTransformerTargetType = typeTransformerSignature.getDeclaringType().resolve(this.world);
                    if (typeTransformerTargetType.isInterface()) {
                        ResolvedType existingMemberType = existingMember.getDeclaringType().resolve(this.world);
                        if (rtm instanceof NewMethodTypeMunger && !typeTransformerTargetType.equals(existingMemberType) && Modifier.isPrivate(typeTransformerSignature.getModifiers()) && Modifier.isPublic(existingMember.getModifiers())) {
                            this.world.getMessageHandler().handleMessage(new Message("private intertype declaration '" + typeTransformerSignature.toString() + "' clashes with public member '" + existingMember.toString() + "'", existingMember.getSourceLocation(), true));
                        }
                    }
                    this.checkLegalOverride(typeTransformerSignature, existingMember, 16, typeTransformer.getAspectType());
                    return true;
                }
                if (c > 0) {
                    this.checkLegalOverride(existingMember, typeTransformerSignature, 1, typeTransformer.getAspectType());
                    continue;
                }
                boolean sameReturnTypes = existingMember.getReturnType().equals(typeTransformerSignature.getReturnType());
                if (!sameReturnTypes) continue;
                boolean isDuplicateOfPreviousITD = false;
                ResolvedType declaringRt = existingMember.getDeclaringType().resolve(this.world);
                WeaverStateInfo wsi = declaringRt.getWeaverState();
                if (wsi != null && (mungersAffectingThisType = wsi.getTypeMungers(declaringRt)) != null) {
                    Iterator<ConcreteTypeMunger> iterator = mungersAffectingThisType.iterator();
                    while (iterator.hasNext() && !isDuplicateOfPreviousITD) {
                        ConcreteTypeMunger ctMunger = iterator.next();
                        if (!ctMunger.getSignature().equals(existingMember) || !ctMunger.aspectType.equals(typeTransformer.getAspectType())) continue;
                        isDuplicateOfPreviousITD = true;
                    }
                }
                if (isDuplicateOfPreviousITD || typeTransformerSignature.getName().equals("<init>") && existingMember.isDefaultConstructor()) continue;
                String aspectName = typeTransformer.getAspectType().getName();
                ISourceLocation typeTransformerLocation = typeTransformer.getSourceLocation();
                ISourceLocation existingMemberLocation = existingMember.getSourceLocation();
                String msg = WeaverMessages.format("itdMemberConflict", aspectName, existingMember);
                this.getWorld().getMessageHandler().handleMessage(new Message(msg, typeTransformerLocation, true));
                if (existingMemberLocation != null) {
                    this.getWorld().getMessageHandler().handleMessage(new Message(msg, existingMemberLocation, true));
                }
                return true;
            }
            if (!this.isDuplicateMemberWithinTargetType(existingMember, this, typeTransformerSignature)) continue;
            this.getWorld().getMessageHandler().handleMessage(MessageUtil.error(WeaverMessages.format("itdMemberConflict", typeTransformer.getAspectType().getName(), existingMember), typeTransformer.getSourceLocation()));
            return true;
        }
        return false;
    }

    private boolean isDuplicateMemberWithinTargetType(ResolvedMember existingMember, ResolvedType targetType, ResolvedMember itdMember) {
        if (existingMember.isAbstract() || itdMember.isAbstract()) {
            return false;
        }
        UnresolvedType declaringType = existingMember.getDeclaringType();
        if (!targetType.equals(declaringType)) {
            return false;
        }
        if (Modifier.isPrivate(itdMember.getModifiers())) {
            return false;
        }
        if (itdMember.isPublic()) {
            return true;
        }
        return targetType.getPackageName().equals(itdMember.getDeclaringType().getPackageName());
    }

    public boolean checkLegalOverride(ResolvedMember parent, ResolvedMember child, int transformerPosition, ResolvedType aspectType) {
        Object[] cTypes;
        Object[] pTypes;
        ResolvedType rtChildReturnType;
        ResolvedType rtParentReturnType;
        if (Modifier.isFinal(parent.getModifiers())) {
            List<ConcreteTypeMunger> transformersOnThisType;
            ResolvedType nonItdDeclaringType;
            WeaverStateInfo wsi;
            if (transformerPosition == 16 && aspectType != null && (wsi = (nonItdDeclaringType = child.getDeclaringType().resolve(this.world)).getWeaverState()) != null && (transformersOnThisType = wsi.getTypeMungers(nonItdDeclaringType)) != null) {
                for (ConcreteTypeMunger transformer : transformersOnThisType) {
                    if (!transformer.aspectType.equals(aspectType) || !parent.equalsApartFromDeclaringType(transformer.getSignature())) continue;
                    return true;
                }
            }
            this.world.showMessage(Message.ERROR, WeaverMessages.format("cantOverrideFinalMember", parent), child.getSourceLocation(), null);
            return false;
        }
        boolean incompatibleReturnTypes = false;
        if (this.world.isInJava5Mode() && parent.getKind() == Member.METHOD) {
            rtParentReturnType = parent.resolve(this.world).getGenericReturnType().resolve(this.world);
            incompatibleReturnTypes = !rtParentReturnType.isAssignableFrom(rtChildReturnType = child.resolve(this.world).getGenericReturnType().resolve(this.world));
        } else {
            rtParentReturnType = parent.resolve(this.world).getGenericReturnType().resolve(this.world);
            boolean bl = incompatibleReturnTypes = !rtParentReturnType.equals(rtChildReturnType = child.resolve(this.world).getGenericReturnType().resolve(this.world));
        }
        if (incompatibleReturnTypes) {
            this.world.showMessage(IMessage.ERROR, WeaverMessages.format("returnTypeMismatch", parent, child), child.getSourceLocation(), parent.getSourceLocation());
            return false;
        }
        if (parent.getKind() == Member.POINTCUT && !Arrays.equals(pTypes = parent.getParameterTypes(), cTypes = child.getParameterTypes())) {
            this.world.showMessage(IMessage.ERROR, WeaverMessages.format("paramTypeMismatch", parent, child), child.getSourceLocation(), parent.getSourceLocation());
            return false;
        }
        if (ResolvedType.isMoreVisible(parent.getModifiers(), child.getModifiers())) {
            this.world.showMessage(IMessage.ERROR, WeaverMessages.format("visibilityReduction", parent, child), child.getSourceLocation(), parent.getSourceLocation());
            return false;
        }
        ResolvedType[] childExceptions = this.world.resolve(child.getExceptions());
        ResolvedType[] parentExceptions = this.world.resolve(parent.getExceptions());
        ResolvedType runtimeException = this.world.resolve("java.lang.RuntimeException");
        ResolvedType error = this.world.resolve("java.lang.Error");
        int leni = childExceptions.length;
        block1: for (int i = 0; i < leni; ++i) {
            if (runtimeException.isAssignableFrom(childExceptions[i]) || error.isAssignableFrom(childExceptions[i])) continue;
            int lenj = parentExceptions.length;
            for (int j = 0; j < lenj; ++j) {
                if (parentExceptions[j].isAssignableFrom(childExceptions[i])) continue block1;
            }
            return false;
        }
        boolean parentStatic = Modifier.isStatic(parent.getModifiers());
        boolean childStatic = Modifier.isStatic(child.getModifiers());
        if (parentStatic && !childStatic) {
            this.world.showMessage(IMessage.ERROR, WeaverMessages.format("overriddenStatic", child, parent), child.getSourceLocation(), null);
            return false;
        }
        if (childStatic && !parentStatic) {
            this.world.showMessage(IMessage.ERROR, WeaverMessages.format("overridingStatic", child, parent), child.getSourceLocation(), null);
            return false;
        }
        return true;
    }

    private int compareMemberPrecedence(ResolvedMember m1, ResolvedMember m2) {
        ResolvedType t2;
        UnresolvedType declaring;
        if (Modifier.isProtected(m2.getModifiers()) && m2.getName().charAt(0) == 'c' && (declaring = m2.getDeclaringType()) != null && declaring.getName().equals("java.lang.Object") && m2.getName().equals("clone")) {
            return 1;
        }
        if (Modifier.isAbstract(m1.getModifiers())) {
            return -1;
        }
        if (Modifier.isAbstract(m2.getModifiers())) {
            return 1;
        }
        if (m1.getDeclaringType().equals(m2.getDeclaringType())) {
            return 0;
        }
        ResolvedType t1 = m1.getDeclaringType().resolve(this.world);
        if (t1.isAssignableFrom(t2 = m2.getDeclaringType().resolve(this.world))) {
            return -1;
        }
        if (t2.isAssignableFrom(t1)) {
            return 1;
        }
        return 0;
    }

    public static boolean isMoreVisible(int m1, int m2) {
        if (Modifier.isPrivate(m1)) {
            return false;
        }
        if (ResolvedType.isPackage(m1)) {
            return Modifier.isPrivate(m2);
        }
        if (Modifier.isProtected(m1)) {
            return Modifier.isPrivate(m2) || ResolvedType.isPackage(m2);
        }
        if (Modifier.isPublic(m1)) {
            return !Modifier.isPublic(m2);
        }
        throw new RuntimeException("bad modifier: " + m1);
    }

    private static boolean isPackage(int i) {
        return 0 == (i & 7);
    }

    private void interTypeConflictError(ConcreteTypeMunger m1, ConcreteTypeMunger m2) {
        this.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("itdConflict", m1.getAspectType().getName(), m2.getSignature(), m2.getAspectType().getName()), m2.getSourceLocation(), this.getSourceLocation());
    }

    public ResolvedMember lookupSyntheticMember(Member member) {
        for (ConcreteTypeMunger m : this.interTypeMungers) {
            ResolvedMember ret = m.getMatchingSyntheticMember(member);
            if (ret == null) continue;
            return ret;
        }
        if (this.world.isJoinpointArrayConstructionEnabled() && this.isArray() && member.getKind() == Member.CONSTRUCTOR) {
            ResolvedMemberImpl ret = new ResolvedMemberImpl(Member.CONSTRUCTOR, this, 1, UnresolvedType.VOID, "<init>", this.world.resolve(member.getParameterTypes()));
            int count = ret.getParameterTypes().length;
            String[] paramNames = new String[count];
            for (int i = 0; i < count; ++i) {
                paramNames[i] = new StringBuffer("dim").append(i).toString();
            }
            ret.setParameterNames(paramNames);
            return ret;
        }
        return null;
    }

    public void clearInterTypeMungers() {
        if (this.isRawType()) {
            ReferenceType genericType = this.getGenericType();
            if (genericType.isRawType()) {
                System.err.println("DebugFor341926: Type " + this.getName() + " has an incorrect generic form");
            } else {
                genericType.clearInterTypeMungers();
            }
        }
        this.interTypeMungers = new ArrayList<ConcreteTypeMunger>();
    }

    public boolean isTopmostImplementor(ResolvedType interfaceType) {
        boolean b = true;
        if (this.isInterface()) {
            b = false;
        } else if (!interfaceType.isAssignableFrom(this, true)) {
            b = false;
        } else {
            ResolvedType superclass = this.getSuperclass();
            if (superclass.isMissing()) {
                b = true;
            } else if (interfaceType.isAssignableFrom(superclass, true)) {
                b = false;
            }
        }
        return b;
    }

    public ResolvedType getTopmostImplementor(ResolvedType interfaceType) {
        if (this.isInterface()) {
            return null;
        }
        if (!interfaceType.isAssignableFrom(this)) {
            return null;
        }
        ResolvedType higherType = this.getSuperclass().getTopmostImplementor(interfaceType);
        if (higherType != null) {
            return higherType;
        }
        return this;
    }

    public List<ResolvedMember> getExposedPointcuts() {
        ArrayList<ResolvedMember> ret = new ArrayList<ResolvedMember>();
        if (this.getSuperclass() != null) {
            ret.addAll(this.getSuperclass().getExposedPointcuts());
        }
        for (ResolvedType type : this.getDeclaredInterfaces()) {
            this.addPointcutsResolvingConflicts(ret, Arrays.asList(type.getDeclaredPointcuts()), false);
        }
        this.addPointcutsResolvingConflicts(ret, Arrays.asList(this.getDeclaredPointcuts()), true);
        for (ResolvedMember member : ret) {
            ResolvedPointcutDefinition inherited = (ResolvedPointcutDefinition)member;
            if (inherited == null || !inherited.isAbstract() || this.isAbstract()) continue;
            this.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("abstractPointcutNotMadeConcrete", inherited, this.getName()), inherited.getSourceLocation(), this.getSourceLocation());
        }
        return ret;
    }

    private void addPointcutsResolvingConflicts(List<ResolvedMember> acc, List<ResolvedMember> added, boolean isOverriding) {
        for (ResolvedPointcutDefinition resolvedPointcutDefinition : added) {
            Iterator<ResolvedMember> j = acc.iterator();
            while (j.hasNext()) {
                ResolvedPointcutDefinition existing = (ResolvedPointcutDefinition)j.next();
                if (resolvedPointcutDefinition == null || existing == null || existing == resolvedPointcutDefinition) continue;
                UnresolvedType pointcutDeclaringTypeUT = existing.getDeclaringType();
                if (pointcutDeclaringTypeUT != null) {
                    ResolvedType pointcutDeclaringType = pointcutDeclaringTypeUT.resolve(this.getWorld());
                    if (!ResolvedType.isVisible(existing.getModifiers(), pointcutDeclaringType, this)) {
                        if (!existing.isAbstract() || !ResolvedType.conflictingSignature(existing, resolvedPointcutDefinition)) continue;
                        this.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("pointcutNotVisible", existing.getDeclaringType().getName() + "." + existing.getName() + "()", this.getName()), resolvedPointcutDefinition.getSourceLocation(), null);
                        j.remove();
                        continue;
                    }
                }
                if (!ResolvedType.conflictingSignature(existing, resolvedPointcutDefinition)) continue;
                if (isOverriding) {
                    this.checkLegalOverride(existing, resolvedPointcutDefinition, 0, null);
                    j.remove();
                    continue;
                }
                this.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("conflictingInheritedPointcuts", this.getName() + resolvedPointcutDefinition.getSignature()), existing.getSourceLocation(), resolvedPointcutDefinition.getSourceLocation());
                j.remove();
            }
            acc.add(resolvedPointcutDefinition);
        }
    }

    public ISourceLocation getSourceLocation() {
        return null;
    }

    public boolean isExposedToWeaver() {
        return false;
    }

    public WeaverStateInfo getWeaverState() {
        return null;
    }

    public ReferenceType getGenericType() {
        return null;
    }

    @Override
    public ResolvedType getRawType() {
        return super.getRawType().resolve(this.world);
    }

    public ResolvedType parameterizedWith(UnresolvedType[] typeParameters) {
        if (!this.isGenericType() && !this.isParameterizedType()) {
            return this;
        }
        return TypeFactory.createParameterizedType(this.getGenericType(), typeParameters, this.getWorld());
    }

    @Override
    public UnresolvedType parameterize(Map<String, UnresolvedType> typeBindings) {
        if (!this.isParameterizedType()) {
            return this;
        }
        boolean workToDo = false;
        for (int i = 0; i < this.typeParameters.length; ++i) {
            if (!this.typeParameters[i].isTypeVariableReference() && !(this.typeParameters[i] instanceof BoundedReferenceType) && !this.typeParameters[i].isParameterizedType()) continue;
            workToDo = true;
        }
        if (!workToDo) {
            return this;
        }
        UnresolvedType[] newTypeParams = new UnresolvedType[this.typeParameters.length];
        for (int i = 0; i < newTypeParams.length; ++i) {
            newTypeParams[i] = this.typeParameters[i];
            if (newTypeParams[i].isTypeVariableReference()) {
                TypeVariableReferenceType tvrt = (TypeVariableReferenceType)newTypeParams[i];
                UnresolvedType binding = typeBindings.get(tvrt.getTypeVariable().getName());
                if (binding == null) continue;
                newTypeParams[i] = binding;
                continue;
            }
            if (newTypeParams[i] instanceof BoundedReferenceType) {
                BoundedReferenceType brType = (BoundedReferenceType)newTypeParams[i];
                newTypeParams[i] = brType.parameterize(typeBindings);
                continue;
            }
            if (!newTypeParams[i].isParameterizedType()) continue;
            newTypeParams[i] = newTypeParams[i].parameterize(typeBindings);
        }
        return TypeFactory.createParameterizedType(this.getGenericType(), newTypeParams, this.getWorld());
    }

    public boolean isException() {
        return this.world.getCoreType(UnresolvedType.JL_EXCEPTION).isAssignableFrom(this);
    }

    public boolean isCheckedException() {
        if (!this.isException()) {
            return false;
        }
        return !this.world.getCoreType(UnresolvedType.RUNTIME_EXCEPTION).isAssignableFrom(this);
    }

    public final boolean isConvertableFrom(ResolvedType other) {
        if (this.equals(OBJECT)) {
            return true;
        }
        if (this.world.isInJava5Mode() && this.isPrimitiveType() ^ other.isPrimitiveType() && validBoxing.contains(this.getSignature() + other.getSignature())) {
            return true;
        }
        if (this.isPrimitiveType() || other.isPrimitiveType()) {
            return this.isAssignableFrom(other);
        }
        return this.isCoerceableFrom(other);
    }

    public abstract boolean isAssignableFrom(ResolvedType var1);

    public abstract boolean isAssignableFrom(ResolvedType var1, boolean var2);

    public abstract boolean isCoerceableFrom(ResolvedType var1);

    public boolean needsNoConversionFrom(ResolvedType o) {
        return this.isAssignableFrom(o);
    }

    public String getSignatureForAttribute() {
        return this.signature;
    }

    public boolean isParameterizedWithTypeVariable() {
        if (this.parameterizedWithTypeVariable == FuzzyBoolean.MAYBE) {
            if (this.typeParameters == null || this.typeParameters.length == 0) {
                this.parameterizedWithTypeVariable = FuzzyBoolean.NO;
                return false;
            }
            for (int i = 0; i < this.typeParameters.length; ++i) {
                boolean b;
                boolean b2;
                ResolvedType aType = (ResolvedType)this.typeParameters[i];
                if (aType.isTypeVariableReference()) {
                    this.parameterizedWithTypeVariable = FuzzyBoolean.YES;
                    return true;
                }
                if (aType.isParameterizedType() && (b2 = aType.isParameterizedWithTypeVariable())) {
                    this.parameterizedWithTypeVariable = FuzzyBoolean.YES;
                    return true;
                }
                if (!aType.isGenericWildcard()) continue;
                BoundedReferenceType boundedRT = (BoundedReferenceType)aType;
                if (boundedRT.isExtends()) {
                    b = false;
                    UnresolvedType upperBound = boundedRT.getUpperBound();
                    if (upperBound.isParameterizedType()) {
                        b = ((ResolvedType)upperBound).isParameterizedWithTypeVariable();
                    } else if (upperBound.isTypeVariableReference() && ((TypeVariableReference)((Object)upperBound)).getTypeVariable().getDeclaringElementKind() == 1) {
                        b = true;
                    }
                    if (b) {
                        this.parameterizedWithTypeVariable = FuzzyBoolean.YES;
                        return true;
                    }
                }
                if (!boundedRT.isSuper()) continue;
                b = false;
                UnresolvedType lowerBound = boundedRT.getLowerBound();
                if (lowerBound.isParameterizedType()) {
                    b = ((ResolvedType)lowerBound).isParameterizedWithTypeVariable();
                } else if (lowerBound.isTypeVariableReference() && ((TypeVariableReference)((Object)lowerBound)).getTypeVariable().getDeclaringElementKind() == 1) {
                    b = true;
                }
                if (!b) continue;
                this.parameterizedWithTypeVariable = FuzzyBoolean.YES;
                return true;
            }
            this.parameterizedWithTypeVariable = FuzzyBoolean.NO;
        }
        return this.parameterizedWithTypeVariable.alwaysTrue();
    }

    protected boolean ajMembersNeedParameterization() {
        if (this.isParameterizedType()) {
            return true;
        }
        ResolvedType superclass = this.getSuperclass();
        if (superclass != null && !superclass.isMissing()) {
            return superclass.ajMembersNeedParameterization();
        }
        return false;
    }

    protected Map<String, UnresolvedType> getAjMemberParameterizationMap() {
        Map<String, UnresolvedType> myMap = this.getMemberParameterizationMap();
        if (myMap.isEmpty() && this.getSuperclass() != null) {
            return this.getSuperclass().getAjMemberParameterizationMap();
        }
        return myMap;
    }

    public void setBinaryPath(String binaryPath) {
        this.binaryPath = binaryPath;
    }

    public String getBinaryPath() {
        return this.binaryPath;
    }

    public void ensureConsistent() {
    }

    public boolean isInheritedAnnotation() {
        this.ensureAnnotationBitsInitialized();
        return (this.bits & AnnotationMarkedInherited) != 0;
    }

    private void ensureAnnotationBitsInitialized() {
        if ((this.bits & AnnotationBitsInitialized) == 0) {
            this.bits |= AnnotationBitsInitialized;
            if (this.hasAnnotation(UnresolvedType.AT_INHERITED)) {
                this.bits |= AnnotationMarkedInherited;
            }
        }
    }

    private boolean hasNewParentMungers() {
        if ((this.bits & MungersAnalyzed) == 0) {
            this.bits |= MungersAnalyzed;
            for (ConcreteTypeMunger munger : this.interTypeMungers) {
                ResolvedTypeMunger resolvedTypeMunger = munger.getMunger();
                if (resolvedTypeMunger == null || resolvedTypeMunger.getKind() != ResolvedTypeMunger.Parent) continue;
                this.bits |= HasParentMunger;
            }
        }
        return (this.bits & HasParentMunger) != 0;
    }

    public void tagAsTypeHierarchyComplete() {
        if (this.isParameterizedOrRawType()) {
            ReferenceType genericType = this.getGenericType();
            genericType.tagAsTypeHierarchyComplete();
            return;
        }
        this.bits |= TypeHierarchyCompleteBit;
    }

    public boolean isTypeHierarchyComplete() {
        if (this.isParameterizedOrRawType()) {
            return this.getGenericType().isTypeHierarchyComplete();
        }
        return (this.bits & TypeHierarchyCompleteBit) != 0;
    }

    public int getCompilerVersion() {
        return AjAttribute.WeaverVersionInfo.getCurrentWeaverMajorVersion();
    }

    public boolean isPrimitiveArray() {
        return false;
    }

    public boolean isGroovyObject() {
        if ((this.bits & GroovyObjectInitialized) == 0) {
            ResolvedType[] intfaces = this.getDeclaredInterfaces();
            boolean done = false;
            if (intfaces != null) {
                for (ResolvedType intface : intfaces) {
                    if (!intface.getName().equals("groovy.lang.GroovyObject")) continue;
                    this.bits |= IsGroovyObject;
                    done = true;
                    break;
                }
            }
            if (!done && this.getSuperclass().getName().equals("groovy.lang.GroovyObjectSupport")) {
                this.bits |= IsGroovyObject;
            }
            this.bits |= GroovyObjectInitialized;
        }
        return (this.bits & IsGroovyObject) != 0;
    }

    public boolean isPrivilegedAspect() {
        if ((this.bits & IsPrivilegedBitInitialized) == 0) {
            AnnotationAJ privilegedAnnotation = this.getAnnotationOfType(UnresolvedType.AJC_PRIVILEGED);
            if (privilegedAnnotation != null) {
                this.bits |= IsPrivilegedAspect;
            }
            this.bits |= IsPrivilegedBitInitialized;
        }
        return (this.bits & IsPrivilegedAspect) != 0;
    }

    static {
        validBoxing.add("Ljava/lang/Byte;B");
        validBoxing.add("Ljava/lang/Character;C");
        validBoxing.add("Ljava/lang/Double;D");
        validBoxing.add("Ljava/lang/Float;F");
        validBoxing.add("Ljava/lang/Integer;I");
        validBoxing.add("Ljava/lang/Long;J");
        validBoxing.add("Ljava/lang/Short;S");
        validBoxing.add("Ljava/lang/Boolean;Z");
        validBoxing.add("BLjava/lang/Byte;");
        validBoxing.add("CLjava/lang/Character;");
        validBoxing.add("DLjava/lang/Double;");
        validBoxing.add("FLjava/lang/Float;");
        validBoxing.add("ILjava/lang/Integer;");
        validBoxing.add("JLjava/lang/Long;");
        validBoxing.add("SLjava/lang/Short;");
        validBoxing.add("ZLjava/lang/Boolean;");
        MethodGetterInstance = new MethodGetter();
        MethodGetterWithItdsInstance = new MethodGetterIncludingItds();
        PointcutGetterInstance = new PointcutGetter();
        FieldGetterInstance = new FieldGetter();
        NONE = new ResolvedType[0];
        EMPTY_ARRAY = NONE;
        MISSING = new Missing();
    }

    static class SuperInterfaceWalker
    implements Iterator<ResolvedType> {
        private Iterators.Getter<ResolvedType, ResolvedType> ifaceGetter;
        Iterator<ResolvedType> delegate = null;
        public Queue<ResolvedType> toPersue = new LinkedList<ResolvedType>();
        public Set<ResolvedType> visited = new HashSet<ResolvedType>();

        SuperInterfaceWalker(Iterators.Getter<ResolvedType, ResolvedType> ifaceGetter) {
            this.ifaceGetter = ifaceGetter;
        }

        SuperInterfaceWalker(Iterators.Getter<ResolvedType, ResolvedType> ifaceGetter, ResolvedType interfaceType) {
            this.ifaceGetter = ifaceGetter;
            this.delegate = Iterators.one(interfaceType);
        }

        @Override
        public boolean hasNext() {
            if (this.delegate == null || !this.delegate.hasNext()) {
                if (this.toPersue.isEmpty()) {
                    return false;
                }
                do {
                    ResolvedType next = this.toPersue.remove();
                    this.visited.add(next);
                    this.delegate = this.ifaceGetter.get(next);
                } while (!this.delegate.hasNext() && !this.toPersue.isEmpty());
            }
            return this.delegate.hasNext();
        }

        public void push(ResolvedType ret) {
            this.toPersue.add(ret);
        }

        @Override
        public ResolvedType next() {
            ResolvedType next = this.delegate.next();
            if (this.visited.add(next)) {
                this.toPersue.add(next);
            }
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static class SuperClassWalker
    implements Iterator<ResolvedType> {
        private ResolvedType curr;
        private SuperInterfaceWalker iwalker;
        private boolean wantGenerics;

        public SuperClassWalker(ResolvedType type, SuperInterfaceWalker iwalker, boolean genericsAware) {
            this.curr = type;
            this.iwalker = iwalker;
            this.wantGenerics = genericsAware;
        }

        @Override
        public boolean hasNext() {
            return this.curr != null;
        }

        @Override
        public ResolvedType next() {
            ResolvedType ret = this.curr;
            if (!this.wantGenerics && ret.isParameterizedOrGenericType()) {
                ret = ret.getRawType();
            }
            this.iwalker.push(ret);
            this.curr = this.curr.getSuperclass();
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static class Missing
    extends ResolvedType {
        Missing() {
            super("@missing@", (World)null);
        }

        @Override
        public final String getName() {
            return "@missing@";
        }

        @Override
        public final boolean isMissing() {
            return true;
        }

        @Override
        public boolean hasAnnotation(UnresolvedType ofType) {
            return false;
        }

        @Override
        public final ResolvedMember[] getDeclaredFields() {
            return ResolvedMember.NONE;
        }

        @Override
        public final ResolvedMember[] getDeclaredMethods() {
            return ResolvedMember.NONE;
        }

        @Override
        public final ResolvedType[] getDeclaredInterfaces() {
            return NONE;
        }

        @Override
        public final ResolvedMember[] getDeclaredPointcuts() {
            return ResolvedMember.NONE;
        }

        @Override
        public final ResolvedType getSuperclass() {
            return null;
        }

        @Override
        public final int getModifiers() {
            return 0;
        }

        @Override
        public final boolean isAssignableFrom(ResolvedType other) {
            return false;
        }

        @Override
        public final boolean isAssignableFrom(ResolvedType other, boolean allowMissing) {
            return false;
        }

        @Override
        public final boolean isCoerceableFrom(ResolvedType other) {
            return false;
        }

        @Override
        public boolean needsNoConversionFrom(ResolvedType other) {
            return false;
        }

        @Override
        public ISourceContext getSourceContext() {
            return null;
        }
    }

    static class Primitive
    extends ResolvedType {
        private final int size;
        private final int index;
        private static final boolean[][] assignTable = new boolean[][]{{true, true, true, true, true, true, true, false, false}, {false, true, true, true, true, true, false, false, false}, {false, false, true, false, false, false, false, false, false}, {false, false, true, true, false, false, false, false, false}, {false, false, true, true, true, true, false, false, false}, {false, false, true, true, false, true, false, false, false}, {false, false, true, true, true, true, true, false, false}, {false, false, false, false, false, false, false, true, false}, {false, false, false, false, false, false, false, false, true}};
        private static final boolean[][] noConvertTable = new boolean[][]{{true, true, false, false, true, false, true, false, false}, {false, true, false, false, true, false, false, false, false}, {false, false, true, false, false, false, false, false, false}, {false, false, false, true, false, false, false, false, false}, {false, false, false, false, true, false, false, false, false}, {false, false, false, false, false, true, false, false, false}, {false, false, false, false, true, false, true, false, false}, {false, false, false, false, false, false, false, true, false}, {false, false, false, false, false, false, false, false, true}};

        Primitive(String signature, int size, int index) {
            super(signature, (World)null);
            this.size = size;
            this.index = index;
            this.typeKind = UnresolvedType.TypeKind.PRIMITIVE;
        }

        @Override
        public final int getSize() {
            return this.size;
        }

        @Override
        public final int getModifiers() {
            return 17;
        }

        @Override
        public final boolean isPrimitiveType() {
            return true;
        }

        @Override
        public boolean hasAnnotation(UnresolvedType ofType) {
            return false;
        }

        @Override
        public final boolean isAssignableFrom(ResolvedType other) {
            if (!other.isPrimitiveType()) {
                if (!this.world.isInJava5Mode()) {
                    return false;
                }
                return validBoxing.contains(this.getSignature() + other.getSignature());
            }
            return assignTable[((Primitive)other).index][this.index];
        }

        @Override
        public final boolean isAssignableFrom(ResolvedType other, boolean allowMissing) {
            return this.isAssignableFrom(other);
        }

        @Override
        public final boolean isCoerceableFrom(ResolvedType other) {
            if (this == other) {
                return true;
            }
            if (!other.isPrimitiveType()) {
                return false;
            }
            return this.index <= 6 && ((Primitive)other).index <= 6;
        }

        @Override
        public ResolvedType resolve(World world) {
            if (this.world != world) {
                throw new IllegalStateException();
            }
            this.world = world;
            return super.resolve(world);
        }

        @Override
        public final boolean needsNoConversionFrom(ResolvedType other) {
            if (!other.isPrimitiveType()) {
                return false;
            }
            return noConvertTable[((Primitive)other).index][this.index];
        }

        @Override
        public final ResolvedMember[] getDeclaredFields() {
            return ResolvedMember.NONE;
        }

        @Override
        public final ResolvedMember[] getDeclaredMethods() {
            return ResolvedMember.NONE;
        }

        @Override
        public final ResolvedType[] getDeclaredInterfaces() {
            return NONE;
        }

        @Override
        public final ResolvedMember[] getDeclaredPointcuts() {
            return ResolvedMember.NONE;
        }

        @Override
        public final ResolvedType getSuperclass() {
            return null;
        }

        @Override
        public ISourceContext getSourceContext() {
            return null;
        }
    }

    private static class FieldGetter
    implements Iterators.Getter<ResolvedType, ResolvedMember> {
        private FieldGetter() {
        }

        @Override
        public Iterator<ResolvedMember> get(ResolvedType type) {
            return Iterators.array(type.getDeclaredFields());
        }
    }

    private static class MethodGetterIncludingItds
    implements Iterators.Getter<ResolvedType, ResolvedMember> {
        private MethodGetterIncludingItds() {
        }

        @Override
        public Iterator<ResolvedMember> get(ResolvedType type) {
            ResolvedMember[] methods = type.getDeclaredMethods();
            if (type.interTypeMungers != null) {
                int additional = 0;
                for (ConcreteTypeMunger typeTransformer : type.interTypeMungers) {
                    ResolvedMember rm = typeTransformer.getSignature();
                    if (rm == null) continue;
                    ++additional;
                }
                if (additional > 0) {
                    ResolvedMember[] methods2 = new ResolvedMember[methods.length + additional];
                    System.arraycopy(methods, 0, methods2, 0, methods.length);
                    additional = methods.length;
                    for (ConcreteTypeMunger typeTransformer : type.interTypeMungers) {
                        ResolvedMember rm = typeTransformer.getSignature();
                        if (rm == null) continue;
                        methods2[additional++] = typeTransformer.getSignature();
                    }
                    methods = methods2;
                }
            }
            return Iterators.array(methods);
        }
    }

    private static class PointcutGetter
    implements Iterators.Getter<ResolvedType, ResolvedMember> {
        private PointcutGetter() {
        }

        @Override
        public Iterator<ResolvedMember> get(ResolvedType o) {
            return Iterators.array(o.getDeclaredPointcuts());
        }
    }

    private static class MethodGetter
    implements Iterators.Getter<ResolvedType, ResolvedMember> {
        private MethodGetter() {
        }

        @Override
        public Iterator<ResolvedMember> get(ResolvedType type) {
            return Iterators.array(type.getDeclaredMethods());
        }
    }
}

