/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.JoinPointSignature;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberImpl;
import org.aspectj.weaver.MemberKind;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.TypeFactory;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableReference;
import org.aspectj.weaver.TypeVariableReferenceType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.UnresolvedTypeVariableReferenceType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;

public class ResolvedMemberImpl
extends MemberImpl
implements IHasPosition,
ResolvedMember {
    private String[] parameterNames = null;
    private boolean isResolved = false;
    protected UnresolvedType[] checkedExceptions = UnresolvedType.NONE;
    protected ResolvedMember backingGenericMember = null;
    protected AnnotationAJ[] annotations = null;
    protected ResolvedType[] annotationTypes = null;
    protected AnnotationAJ[][] parameterAnnotations = null;
    protected ResolvedType[][] parameterAnnotationTypes = null;
    private boolean isAnnotatedElsewhere = false;
    private boolean isAjSynthetic = false;
    protected TypeVariable[] typeVariables;
    protected int start;
    protected int end;
    protected ISourceContext sourceContext = null;
    private String myParameterSignatureWithBoundsRemoved = null;
    private String myParameterSignatureErasure = null;
    public static boolean showParameterNames = true;

    public ResolvedMemberImpl(MemberKind kind, UnresolvedType declaringType, int modifiers, UnresolvedType returnType, String name, UnresolvedType[] parameterTypes) {
        super(kind, declaringType, modifiers, returnType, name, parameterTypes);
    }

    public ResolvedMemberImpl(MemberKind kind, UnresolvedType declaringType, int modifiers, UnresolvedType returnType, String name, UnresolvedType[] parameterTypes, UnresolvedType[] checkedExceptions) {
        super(kind, declaringType, modifiers, returnType, name, parameterTypes);
        this.checkedExceptions = checkedExceptions;
    }

    public ResolvedMemberImpl(MemberKind kind, UnresolvedType declaringType, int modifiers, UnresolvedType returnType, String name, UnresolvedType[] parameterTypes, UnresolvedType[] checkedExceptions, ResolvedMember backingGenericMember) {
        this(kind, declaringType, modifiers, returnType, name, parameterTypes, checkedExceptions);
        this.backingGenericMember = backingGenericMember;
        this.isAjSynthetic = backingGenericMember.isAjSynthetic();
    }

    public ResolvedMemberImpl(MemberKind kind, UnresolvedType declaringType, int modifiers, String name, String signature) {
        super(kind, declaringType, modifiers, name, signature);
    }

    public static JoinPointSignature[] getJoinPointSignatures(Member joinPointSignature, World inAWorld) {
        ResolvedType originalDeclaringType = joinPointSignature.getDeclaringType().resolve(inAWorld);
        ResolvedMemberImpl firstDefiningMember = (ResolvedMemberImpl)joinPointSignature.resolve(inAWorld);
        if (firstDefiningMember == null) {
            return JoinPointSignature.EMPTY_ARRAY;
        }
        ResolvedType firstDefiningType = firstDefiningMember.getDeclaringType().resolve(inAWorld);
        if (firstDefiningType != originalDeclaringType && joinPointSignature.getKind() == Member.CONSTRUCTOR) {
            return JoinPointSignature.EMPTY_ARRAY;
        }
        ArrayList<ResolvedType> declaringTypes = new ArrayList<ResolvedType>();
        ResolvedMemberImpl.accumulateTypesInBetween(originalDeclaringType, firstDefiningType, declaringTypes);
        LinkedHashSet<ResolvedMember> memberSignatures = new LinkedHashSet<ResolvedMember>();
        for (ResolvedType declaringType : declaringTypes) {
            memberSignatures.add(new JoinPointSignature(firstDefiningMember, declaringType));
        }
        if (ResolvedMemberImpl.shouldWalkUpHierarchyFor(firstDefiningMember)) {
            Iterator<ResolvedType> superTypeIterator = firstDefiningType.getDirectSupertypes();
            ArrayList<ResolvedType> typesAlreadyVisited = new ArrayList<ResolvedType>();
            ResolvedMemberImpl.accumulateMembersMatching(firstDefiningMember, superTypeIterator, typesAlreadyVisited, memberSignatures, false);
        }
        JoinPointSignature[] ret = new JoinPointSignature[memberSignatures.size()];
        memberSignatures.toArray(ret);
        return ret;
    }

    private static boolean shouldWalkUpHierarchyFor(Member aMember) {
        if (aMember.getKind() == Member.CONSTRUCTOR) {
            return false;
        }
        if (aMember.getKind() == Member.FIELD) {
            return false;
        }
        return !Modifier.isStatic(aMember.getModifiers());
    }

    private static void accumulateTypesInBetween(ResolvedType subType, ResolvedType superType, List<ResolvedType> types) {
        types.add(subType);
        if (subType == superType) {
            return;
        }
        Iterator<ResolvedType> iter = subType.getDirectSupertypes();
        while (iter.hasNext()) {
            ResolvedType parent = iter.next();
            if (!superType.isAssignableFrom(parent)) continue;
            ResolvedMemberImpl.accumulateTypesInBetween(parent, superType, types);
        }
    }

    private static void accumulateMembersMatching(ResolvedMemberImpl memberToMatch, Iterator<ResolvedType> typesToLookIn, List<ResolvedType> typesAlreadyVisited, Set<ResolvedMember> foundMembers, boolean ignoreGenerics) {
        while (typesToLookIn.hasNext()) {
            ResolvedType toLookIn = typesToLookIn.next();
            if (typesAlreadyVisited.contains(toLookIn)) continue;
            typesAlreadyVisited.add(toLookIn);
            ResolvedMemberImpl foundMember = (ResolvedMemberImpl)toLookIn.lookupResolvedMember(memberToMatch, true, ignoreGenerics);
            if (foundMember == null || !ResolvedMemberImpl.isVisibleTo(memberToMatch, foundMember)) continue;
            ArrayList<ResolvedType> declaringTypes = new ArrayList<ResolvedType>();
            ResolvedType resolvedDeclaringType = foundMember.getDeclaringType().resolve(toLookIn.getWorld());
            ResolvedMemberImpl.accumulateTypesInBetween(toLookIn, resolvedDeclaringType, declaringTypes);
            for (ResolvedType declaringType : declaringTypes) {
                foundMembers.add(new JoinPointSignature(foundMember, declaringType));
            }
            if (!ignoreGenerics && toLookIn.isParameterizedType() && foundMember.backingGenericMember != null) {
                foundMembers.add(new JoinPointSignature(foundMember.backingGenericMember, foundMember.declaringType.resolve(toLookIn.getWorld())));
            }
            ResolvedMemberImpl.accumulateMembersMatching(foundMember, toLookIn.getDirectSupertypes(), typesAlreadyVisited, foundMembers, ignoreGenerics);
        }
    }

    private static boolean isVisibleTo(ResolvedMember childMember, ResolvedMember parentMember) {
        if (childMember.getDeclaringType().equals(parentMember.getDeclaringType())) {
            return true;
        }
        return !Modifier.isPrivate(parentMember.getModifiers());
    }

    @Override
    public final int getModifiers(World world) {
        return this.modifiers;
    }

    @Override
    public final int getModifiers() {
        return this.modifiers;
    }

    @Override
    public final UnresolvedType[] getExceptions(World world) {
        return this.getExceptions();
    }

    @Override
    public UnresolvedType[] getExceptions() {
        return this.checkedExceptions;
    }

    @Override
    public ShadowMunger getAssociatedShadowMunger() {
        return null;
    }

    @Override
    public boolean isAjSynthetic() {
        return this.isAjSynthetic;
    }

    protected void setAjSynthetic(boolean b) {
        this.isAjSynthetic = b;
    }

    public boolean hasAnnotations() {
        return this.annotationTypes != null;
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        if (this.backingGenericMember != null) {
            if (this.annotationTypes != null) {
                throw new BCException("Unexpectedly found a backing generic member and a local set of annotations");
            }
            return this.backingGenericMember.hasAnnotation(ofType);
        }
        if (this.annotationTypes != null) {
            int max = this.annotationTypes.length;
            for (int i = 0; i < max; ++i) {
                if (!this.annotationTypes[i].equals(ofType)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        if (this.backingGenericMember != null) {
            if (this.annotationTypes != null) {
                throw new BCException("Unexpectedly found a backing generic member and a local set of annotations");
            }
            return this.backingGenericMember.getAnnotationTypes();
        }
        return this.annotationTypes;
    }

    @Override
    public String getAnnotationDefaultValue() {
        throw new UnsupportedOperationException("You should resolve this member and call getAnnotationDefaultValue() on the result...");
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        if (this.backingGenericMember != null) {
            return this.backingGenericMember.getAnnotations();
        }
        if (this.annotations != null) {
            return this.annotations;
        }
        return super.getAnnotations();
    }

    @Override
    public AnnotationAJ getAnnotationOfType(UnresolvedType ofType) {
        if (this.annotations != null) {
            for (AnnotationAJ annotation : this.annotations) {
                if (!annotation.getType().equals(ofType)) continue;
                return annotation;
            }
            return null;
        }
        throw new UnsupportedOperationException("You should resolve this member and call getAnnotationOfType() on the result...");
    }

    @Override
    public void setAnnotations(AnnotationAJ[] annotations) {
        this.annotations = annotations;
    }

    @Override
    public void setAnnotationTypes(ResolvedType[] annotationTypes) {
        this.annotationTypes = annotationTypes;
    }

    @Override
    public ResolvedType[][] getParameterAnnotationTypes() {
        return this.parameterAnnotationTypes;
    }

    @Override
    public AnnotationAJ[][] getParameterAnnotations() {
        if (this.backingGenericMember != null) {
            return this.backingGenericMember.getParameterAnnotations();
        }
        throw new BCException("Cannot return parameter annotations for a " + this.getClass().getName() + " member");
    }

    @Override
    public void addAnnotation(AnnotationAJ annotation) {
        if (this.annotationTypes == null) {
            this.annotationTypes = new ResolvedType[1];
            this.annotationTypes[0] = annotation.getType();
            this.annotations = new AnnotationAJ[1];
            this.annotations[0] = annotation;
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
    }

    @Override
    public boolean isBridgeMethod() {
        return (this.modifiers & 0x40) != 0 && this.getKind().equals(METHOD);
    }

    @Override
    public boolean isVarargsMethod() {
        return (this.modifiers & 0x80) != 0;
    }

    public void setVarargsMethod() {
        this.modifiers |= 0x80;
    }

    @Override
    public boolean isSynthetic() {
        return (this.modifiers & 0x1000) != 0;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        this.getKind().write(s);
        s.writeBoolean(s.canCompress());
        if (s.canCompress()) {
            s.writeCompressedSignature(this.getDeclaringType().getSignature());
        } else {
            this.getDeclaringType().write(s);
        }
        s.writeInt(this.modifiers);
        if (s.canCompress()) {
            s.writeCompressedName(this.getName());
            s.writeCompressedSignature(this.getSignature());
        } else {
            s.writeUTF(this.getName());
            s.writeUTF(this.getSignature());
        }
        UnresolvedType.writeArray(this.getExceptions(), s);
        s.writeInt(this.getStart());
        s.writeInt(this.getEnd());
        s.writeBoolean(this.isVarargsMethod());
        if (this.typeVariables == null) {
            s.writeByte(0);
        } else {
            s.writeByte(this.typeVariables.length);
            for (int i = 0; i < this.typeVariables.length; ++i) {
                this.typeVariables[i].write(s);
            }
        }
        String gsig = this.getGenericSignature();
        if (this.getSignature().equals(gsig)) {
            s.writeByte(255);
        } else {
            s.writeByte(this.parameterTypes.length);
            for (int i = 0; i < this.parameterTypes.length; ++i) {
                if (s.canCompress()) {
                    s.writeCompressedSignature(this.parameterTypes[i].getSignature());
                    continue;
                }
                UnresolvedType array_element = this.parameterTypes[i];
                array_element.write(s);
            }
            if (s.canCompress()) {
                s.writeCompressedSignature(this.returnType.getSignature());
            } else {
                this.returnType.write(s);
            }
        }
    }

    public String getSignatureForAttribute() {
        int i;
        StringBuffer sb = new StringBuffer();
        if (this.typeVariables != null) {
            sb.append("<");
            for (i = 0; i < this.typeVariables.length; ++i) {
                sb.append(this.typeVariables[i].getSignatureForAttribute());
            }
            sb.append(">");
        }
        sb.append("(");
        for (i = 0; i < this.parameterTypes.length; ++i) {
            ResolvedType ptype = (ResolvedType)this.parameterTypes[i];
            sb.append(ptype.getSignatureForAttribute());
        }
        sb.append(")");
        sb.append(((ResolvedType)this.returnType).getSignatureForAttribute());
        return sb.toString();
    }

    public String getGenericSignature() {
        int i;
        StringBuffer sb = new StringBuffer();
        if (this.typeVariables != null) {
            sb.append("<");
            for (i = 0; i < this.typeVariables.length; ++i) {
                sb.append(this.typeVariables[i].getSignature());
            }
            sb.append(">");
        }
        sb.append("(");
        for (i = 0; i < this.parameterTypes.length; ++i) {
            UnresolvedType ptype = this.parameterTypes[i];
            sb.append(ptype.getSignature());
        }
        sb.append(")");
        sb.append(this.returnType.getSignature());
        return sb.toString();
    }

    public static void writeArray(ResolvedMember[] members, CompressingDataOutputStream s) throws IOException {
        s.writeInt(members.length);
        int len = members.length;
        for (int i = 0; i < len; ++i) {
            members[i].write(s);
        }
    }

    public static ResolvedMemberImpl readResolvedMember(VersionedDataInputStream s, ISourceContext sourceContext) throws IOException {
        MemberKind mk = MemberKind.read(s);
        boolean compressed = s.isAtLeast169() ? s.readBoolean() : false;
        UnresolvedType declaringType = compressed ? UnresolvedType.forSignature(s.readUtf8(s.readShort())) : UnresolvedType.read(s);
        int modifiers = s.readInt();
        String name = compressed ? s.readUtf8(s.readShort()) : s.readUTF();
        String signature = compressed ? s.readUtf8(s.readShort()) : s.readUTF();
        ResolvedMemberImpl m = new ResolvedMemberImpl(mk, declaringType, modifiers, name, signature);
        m.checkedExceptions = UnresolvedType.readArray(s);
        m.start = s.readInt();
        m.end = s.readInt();
        m.sourceContext = sourceContext;
        if (s.getMajorVersion() >= 2) {
            int tvcount;
            boolean isvarargs;
            if (s.getMajorVersion() >= 3 && (isvarargs = s.readBoolean())) {
                m.setVarargsMethod();
            }
            int n = tvcount = s.isAtLeast169() ? s.readByte() : s.readInt();
            if (tvcount != 0) {
                m.typeVariables = new TypeVariable[tvcount];
                for (int i = 0; i < tvcount; ++i) {
                    m.typeVariables[i] = TypeVariable.read(s);
                    m.typeVariables[i].setDeclaringElement(m);
                    m.typeVariables[i].setRank(i);
                }
            }
            if (s.getMajorVersion() >= 3) {
                int pcount = -1;
                boolean hasAGenericSignature = false;
                hasAGenericSignature = s.isAtLeast169() ? (pcount = (int)s.readByte()) >= 0 && pcount < 255 : s.readBoolean();
                if (hasAGenericSignature) {
                    int ps = s.isAtLeast169() ? pcount : s.readInt();
                    UnresolvedType[] params = new UnresolvedType[ps];
                    for (int i = 0; i < params.length; ++i) {
                        params[i] = compressed ? TypeFactory.createTypeFromSignature(s.readSignature()) : TypeFactory.createTypeFromSignature(s.readUTF());
                    }
                    UnresolvedType rt = compressed ? TypeFactory.createTypeFromSignature(s.readSignature()) : TypeFactory.createTypeFromSignature(s.readUTF());
                    m.parameterTypes = params;
                    m.returnType = rt;
                }
            }
        }
        return m;
    }

    public static ResolvedMember[] readResolvedMemberArray(VersionedDataInputStream s, ISourceContext context) throws IOException {
        int len = s.readInt();
        ResolvedMember[] members = new ResolvedMember[len];
        for (int i = 0; i < len; ++i) {
            members[i] = ResolvedMemberImpl.readResolvedMember(s, context);
        }
        return members;
    }

    @Override
    public ResolvedMember resolve(World world) {
        if (this.isResolved) {
            return this;
        }
        try {
            int i;
            if (this.typeVariables != null && this.typeVariables.length > 0) {
                for (i = 0; i < this.typeVariables.length; ++i) {
                    this.typeVariables[i] = this.typeVariables[i].resolve(world);
                }
            }
            world.setTypeVariableLookupScope(this);
            this.declaringType = this.declaringType.resolve(world);
            if (this.declaringType.isRawType()) {
                this.declaringType = ((ReferenceType)this.declaringType).getGenericType();
            }
            if (this.parameterTypes != null && this.parameterTypes.length > 0) {
                for (i = 0; i < this.parameterTypes.length; ++i) {
                    this.parameterTypes[i] = this.parameterTypes[i].resolve(world);
                }
            }
            this.returnType = this.returnType.resolve(world);
        }
        finally {
            world.setTypeVariableLookupScope(null);
        }
        this.isResolved = true;
        return this;
    }

    @Override
    public ISourceContext getSourceContext(World world) {
        return this.getDeclaringType().resolve(world).getSourceContext();
    }

    @Override
    public String[] getParameterNames() {
        return this.parameterNames;
    }

    @Override
    public final void setParameterNames(String[] pnames) {
        this.parameterNames = pnames;
    }

    @Override
    public final String[] getParameterNames(World world) {
        return this.getParameterNames();
    }

    @Override
    public AjAttribute.EffectiveSignatureAttribute getEffectiveSignature() {
        return null;
    }

    @Override
    public ISourceLocation getSourceLocation() {
        if (this.getSourceContext() == null) {
            return null;
        }
        return this.getSourceContext().makeSourceLocation(this);
    }

    @Override
    public int getEnd() {
        return this.end;
    }

    @Override
    public ISourceContext getSourceContext() {
        return this.sourceContext;
    }

    @Override
    public int getStart() {
        return this.start;
    }

    @Override
    public void setPosition(int sourceStart, int sourceEnd) {
        this.start = sourceStart;
        this.end = sourceEnd;
    }

    public void setDeclaringType(ReferenceType rt) {
        this.declaringType = rt;
    }

    @Override
    public void setSourceContext(ISourceContext sourceContext) {
        this.sourceContext = sourceContext;
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.modifiers);
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(this.modifiers);
    }

    @Override
    public boolean isDefault() {
        int mods = this.getModifiers();
        return !Modifier.isPublic(mods) && !Modifier.isProtected(mods) && !Modifier.isPrivate(mods);
    }

    @Override
    public boolean isVisible(ResolvedType fromType) {
        UnresolvedType declaringType = this.getDeclaringType();
        ResolvedType type = null;
        if (fromType.equals(declaringType)) {
            type = fromType;
        } else {
            World world = fromType.getWorld();
            type = declaringType.resolve(world);
        }
        return ResolvedType.isVisible(this.getModifiers(), type, fromType);
    }

    @Override
    public void setCheckedExceptions(UnresolvedType[] checkedExceptions) {
        this.checkedExceptions = checkedExceptions;
    }

    @Override
    public void setAnnotatedElsewhere(boolean b) {
        this.isAnnotatedElsewhere = b;
    }

    @Override
    public boolean isAnnotatedElsewhere() {
        return this.isAnnotatedElsewhere;
    }

    @Override
    public UnresolvedType getGenericReturnType() {
        return this.getReturnType();
    }

    @Override
    public UnresolvedType[] getGenericParameterTypes() {
        return this.getParameterTypes();
    }

    @Override
    public ResolvedMemberImpl parameterizedWith(UnresolvedType[] typeParameters, ResolvedType newDeclaringType, boolean isParameterized) {
        return this.parameterizedWith(typeParameters, newDeclaringType, isParameterized, null);
    }

    @Override
    public ResolvedMemberImpl parameterizedWith(UnresolvedType[] typeParameters, ResolvedType newDeclaringType, boolean isParameterized, List<String> aliases) {
        boolean typeParametersSupplied;
        if (!this.getDeclaringType().isGenericType() && this.getDeclaringType().getName().indexOf("$") == -1) {
            throw new IllegalStateException("Can't ask to parameterize a member of non-generic type: " + this.getDeclaringType() + "  kind(" + this.getDeclaringType().typeKind + ")");
        }
        TypeVariable[] typeVariables = this.getDeclaringType().getTypeVariables();
        if (isParameterized && typeVariables.length != typeParameters.length) {
            throw new IllegalStateException("Wrong number of type parameters supplied");
        }
        HashMap<String, UnresolvedType> typeMap = new HashMap<String, UnresolvedType>();
        boolean bl = typeParametersSupplied = typeParameters != null && typeParameters.length > 0;
        if (typeVariables != null) {
            for (int i = 0; i < typeVariables.length; ++i) {
                UnresolvedType ut = !typeParametersSupplied ? typeVariables[i].getFirstBound() : typeParameters[i];
                typeMap.put(typeVariables[i].getName(), ut);
            }
        }
        if (aliases != null) {
            int posn = 0;
            for (String typeVariableAlias : aliases) {
                typeMap.put(typeVariableAlias, !typeParametersSupplied ? typeVariables[posn].getFirstBound() : typeParameters[posn]);
                ++posn;
            }
        }
        UnresolvedType parameterizedReturnType = this.parameterize(this.getGenericReturnType(), typeMap, isParameterized, newDeclaringType.getWorld());
        UnresolvedType[] parameterizedParameterTypes = new UnresolvedType[this.getGenericParameterTypes().length];
        UnresolvedType[] genericParameterTypes = this.getGenericParameterTypes();
        for (int i = 0; i < parameterizedParameterTypes.length; ++i) {
            parameterizedParameterTypes[i] = this.parameterize(genericParameterTypes[i], typeMap, isParameterized, newDeclaringType.getWorld());
        }
        ResolvedMemberImpl ret = new ResolvedMemberImpl(this.getKind(), newDeclaringType, this.getModifiers(), parameterizedReturnType, this.getName(), parameterizedParameterTypes, this.getExceptions(), this);
        ret.setTypeVariables(this.getTypeVariables());
        ret.setSourceContext(this.getSourceContext());
        ret.setPosition(this.getStart(), this.getEnd());
        ret.setParameterNames(this.getParameterNames());
        return ret;
    }

    @Override
    public ResolvedMember parameterizedWith(Map<String, UnresolvedType> m, World w) {
        this.declaringType = this.declaringType.resolve(w);
        if (this.declaringType.isRawType()) {
            this.declaringType = ((ResolvedType)this.declaringType).getGenericType();
        }
        UnresolvedType parameterizedReturnType = this.parameterize(this.getGenericReturnType(), m, true, w);
        UnresolvedType[] parameterizedParameterTypes = new UnresolvedType[this.getGenericParameterTypes().length];
        UnresolvedType[] genericParameterTypes = this.getGenericParameterTypes();
        for (int i = 0; i < parameterizedParameterTypes.length; ++i) {
            parameterizedParameterTypes[i] = this.parameterize(genericParameterTypes[i], m, true, w);
        }
        ResolvedMemberImpl ret = new ResolvedMemberImpl(this.getKind(), this.declaringType, this.getModifiers(), parameterizedReturnType, this.getName(), parameterizedParameterTypes, this.getExceptions(), this);
        ret.setTypeVariables(this.getTypeVariables());
        ret.setSourceContext(this.getSourceContext());
        ret.setPosition(this.getStart(), this.getEnd());
        ret.setParameterNames(this.getParameterNames());
        return ret;
    }

    @Override
    public void setTypeVariables(TypeVariable[] tvars) {
        this.typeVariables = tvars;
    }

    @Override
    public TypeVariable[] getTypeVariables() {
        return this.typeVariables;
    }

    protected UnresolvedType parameterize(UnresolvedType aType, Map<String, UnresolvedType> typeVariableMap, boolean inParameterizedType, World w) {
        if (aType instanceof TypeVariableReference) {
            String variableName = ((TypeVariableReference)((Object)aType)).getTypeVariable().getName();
            if (!typeVariableMap.containsKey(variableName)) {
                return aType;
            }
            return typeVariableMap.get(variableName);
        }
        if (aType.isParameterizedType()) {
            if (inParameterizedType) {
                if (w != null) {
                    aType = aType.resolve(w);
                } else {
                    UnresolvedType dType = this.getDeclaringType();
                    aType = aType.resolve(((ResolvedType)dType).getWorld());
                }
                return aType.parameterize(typeVariableMap);
            }
            return aType.getRawType();
        }
        if (aType.isArray()) {
            int dims = 1;
            String sig = aType.getSignature();
            UnresolvedType arrayType = null;
            UnresolvedType componentSig = UnresolvedType.forSignature(sig.substring(dims));
            UnresolvedType parameterizedComponentSig = this.parameterize(componentSig, typeVariableMap, inParameterizedType, w);
            if (parameterizedComponentSig.isTypeVariableReference() && parameterizedComponentSig instanceof UnresolvedTypeVariableReferenceType && typeVariableMap.containsKey(((UnresolvedTypeVariableReferenceType)parameterizedComponentSig).getTypeVariable().getName())) {
                StringBuffer newsig = new StringBuffer();
                newsig.append("[T");
                newsig.append(((UnresolvedTypeVariableReferenceType)parameterizedComponentSig).getTypeVariable().getName());
                newsig.append(";");
                arrayType = UnresolvedType.forSignature(newsig.toString());
            } else {
                arrayType = ResolvedType.makeArray(parameterizedComponentSig, dims);
            }
            return arrayType;
        }
        return aType;
    }

    @Override
    public boolean hasBackingGenericMember() {
        return this.backingGenericMember != null;
    }

    @Override
    public ResolvedMember getBackingGenericMember() {
        return this.backingGenericMember;
    }

    public void resetName(String newName) {
        this.name = newName;
    }

    public void resetKind(MemberKind newKind) {
        this.kind = newKind;
    }

    public void resetModifiers(int newModifiers) {
        this.modifiers = newModifiers;
    }

    public void resetReturnTypeToObjectArray() {
        this.returnType = UnresolvedType.OBJECTARRAY;
    }

    @Override
    public boolean matches(ResolvedMember aCandidateMatch, boolean ignoreGenerics) {
        String candidateParameterSignature;
        UnresolvedType[] candidateParameterTypes;
        ResolvedMemberImpl candidateMatchImpl = (ResolvedMemberImpl)aCandidateMatch;
        if (!this.getName().equals(aCandidateMatch.getName())) {
            return false;
        }
        UnresolvedType[] parameterTypes = this.getGenericParameterTypes();
        if (parameterTypes.length != (candidateParameterTypes = aCandidateMatch.getGenericParameterTypes()).length) {
            return false;
        }
        boolean b = false;
        String myParameterSignature = this.getParameterSigWithBoundsRemoved();
        if (myParameterSignature.equals(candidateParameterSignature = candidateMatchImpl.getParameterSigWithBoundsRemoved())) {
            b = true;
        } else {
            myParameterSignature = this.getParameterSignatureErased();
            candidateParameterSignature = candidateMatchImpl.getParameterSignatureErased();
            b = myParameterSignature.equals(candidateParameterSignature);
        }
        return b;
    }

    private String getParameterSigWithBoundsRemoved() {
        if (this.myParameterSignatureWithBoundsRemoved != null) {
            return this.myParameterSignatureWithBoundsRemoved;
        }
        StringBuffer sig = new StringBuffer();
        UnresolvedType[] myParameterTypes = this.getGenericParameterTypes();
        for (int i = 0; i < myParameterTypes.length; ++i) {
            ResolvedMemberImpl.appendSigWithTypeVarBoundsRemoved(myParameterTypes[i], sig, new HashSet<UnresolvedType>());
        }
        this.myParameterSignatureWithBoundsRemoved = sig.toString();
        return this.myParameterSignatureWithBoundsRemoved;
    }

    @Override
    public String getParameterSignatureErased() {
        if (this.myParameterSignatureErasure == null) {
            StringBuilder sig = new StringBuilder();
            for (UnresolvedType parameter : this.getParameterTypes()) {
                sig.append(parameter.getErasureSignature());
            }
            this.myParameterSignatureErasure = sig.toString();
        }
        return this.myParameterSignatureErasure;
    }

    @Override
    public String getSignatureErased() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append(this.getParameterSignatureErased());
        sb.append(")");
        sb.append(this.getReturnType().getErasureSignature());
        return sb.toString();
    }

    public static void appendSigWithTypeVarBoundsRemoved(UnresolvedType aType, StringBuffer toBuffer, Set<UnresolvedType> alreadyUsedTypeVars) {
        if (aType.isTypeVariableReference()) {
            TypeVariableReferenceType typeVariableRT = (TypeVariableReferenceType)aType;
            if (alreadyUsedTypeVars.contains(aType)) {
                toBuffer.append("...");
            } else {
                alreadyUsedTypeVars.add(aType);
                ResolvedMemberImpl.appendSigWithTypeVarBoundsRemoved(typeVariableRT.getTypeVariable().getFirstBound(), toBuffer, alreadyUsedTypeVars);
            }
        } else if (aType.isParameterizedType()) {
            toBuffer.append(aType.getRawType().getSignature());
            toBuffer.append("<");
            for (int i = 0; i < aType.getTypeParameters().length; ++i) {
                ResolvedMemberImpl.appendSigWithTypeVarBoundsRemoved(aType.getTypeParameters()[i], toBuffer, alreadyUsedTypeVars);
            }
            toBuffer.append(">;");
        } else {
            toBuffer.append(aType.getSignature());
        }
    }

    @Override
    public String toDebugString() {
        String modsStr;
        StringBuffer r = new StringBuffer();
        int mods = this.modifiers;
        if ((mods & 0x1000) > 0) {
            mods -= 4096;
        }
        if ((mods & 0x200) > 0) {
            mods -= 512;
        }
        if ((mods & 0x20000) > 0) {
            mods -= 131072;
        }
        if ((modsStr = Modifier.toString(mods)).length() != 0) {
            r.append(modsStr).append("(" + mods + ")").append(" ");
        }
        if (this.typeVariables != null && this.typeVariables.length > 0) {
            r.append("<");
            for (int i = 0; i < this.typeVariables.length; ++i) {
                if (i > 0) {
                    r.append(",");
                }
                TypeVariable t = this.typeVariables[i];
                r.append(t.toDebugString());
            }
            r.append("> ");
        }
        r.append(this.getGenericReturnType().toDebugString());
        r.append(' ');
        r.append(this.declaringType.getName());
        r.append('.');
        r.append(this.name);
        if (this.kind != FIELD) {
            boolean parameterNamesExist;
            r.append("(");
            UnresolvedType[] params = this.getGenericParameterTypes();
            boolean bl = parameterNamesExist = showParameterNames && this.parameterNames != null && this.parameterNames.length == params.length;
            if (params.length != 0) {
                int len = params.length;
                for (int i = 0; i < len; ++i) {
                    if (i > 0) {
                        r.append(", ");
                    }
                    r.append(params[i].toDebugString());
                    if (!parameterNamesExist) continue;
                    r.append(" ").append(this.parameterNames[i]);
                }
            }
            r.append(")");
        }
        return r.toString();
    }

    @Override
    public String toGenericString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.getGenericReturnType().getSimpleName());
        buf.append(' ');
        buf.append(this.declaringType.getName());
        buf.append('.');
        buf.append(this.name);
        if (this.kind != FIELD) {
            buf.append("(");
            UnresolvedType[] params = this.getGenericParameterTypes();
            if (params.length != 0) {
                buf.append(params[0].getSimpleName());
                int len = params.length;
                for (int i = 1; i < len; ++i) {
                    buf.append(", ");
                    buf.append(params[i].getSimpleName());
                }
            }
            buf.append(")");
        }
        return buf.toString();
    }

    @Override
    public boolean isCompatibleWith(Member am) {
        if (this.kind != METHOD || am.getKind() != METHOD) {
            return true;
        }
        if (!this.name.equals(am.getName())) {
            return true;
        }
        if (!ResolvedMemberImpl.equalTypes(this.getParameterTypes(), am.getParameterTypes())) {
            return true;
        }
        return this.getReturnType().equals(am.getReturnType());
    }

    private static boolean equalTypes(UnresolvedType[] a, UnresolvedType[] b) {
        int len = a.length;
        if (len != b.length) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (a[i].equals(b[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public TypeVariable getTypeVariableNamed(String name) {
        if (this.typeVariables != null) {
            for (int i = 0; i < this.typeVariables.length; ++i) {
                if (!this.typeVariables[i].getName().equals(name)) continue;
                return this.typeVariables[i];
            }
        }
        return this.declaringType.getTypeVariableNamed(name);
    }

    @Override
    public void evictWeavingState() {
    }

    public boolean isEquivalentTo(Object other) {
        return this.equals(other);
    }

    @Override
    public boolean isDefaultConstructor() {
        return false;
    }
}

