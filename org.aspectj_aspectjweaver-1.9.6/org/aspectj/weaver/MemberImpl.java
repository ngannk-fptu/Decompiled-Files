/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.JoinPointSignatureIterator;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberKind;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;

public class MemberImpl
implements Member {
    protected MemberKind kind;
    protected int modifiers;
    protected String name;
    protected UnresolvedType declaringType;
    protected UnresolvedType returnType;
    protected UnresolvedType[] parameterTypes;
    private final String erasedSignature;
    private String paramSignature;
    private boolean reportedCantFindDeclaringType = false;
    private boolean reportedUnresolvableMember = false;
    private JoinPointSignatureIterator joinPointSignatures = null;
    private volatile int hashCode = 0;

    public MemberImpl(MemberKind kind, UnresolvedType declaringType, int modifiers, String name, String erasedSignature) {
        this.kind = kind;
        this.declaringType = declaringType;
        this.modifiers = modifiers;
        this.name = name;
        this.erasedSignature = erasedSignature;
        if (kind == FIELD) {
            this.returnType = UnresolvedType.forSignature(erasedSignature);
            this.parameterTypes = UnresolvedType.NONE;
        } else {
            Object[] returnAndParams = MemberImpl.signatureToTypes(erasedSignature);
            this.returnType = (UnresolvedType)returnAndParams[0];
            this.parameterTypes = (UnresolvedType[])returnAndParams[1];
        }
    }

    public MemberImpl(MemberKind kind, UnresolvedType declaringType, int modifiers, UnresolvedType returnType, String name, UnresolvedType[] parameterTypes) {
        this.kind = kind;
        this.declaringType = declaringType;
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.erasedSignature = kind == FIELD ? returnType.getErasureSignature() : MemberImpl.typesToSignature(returnType, parameterTypes, true);
    }

    @Override
    public ResolvedMember resolve(World world) {
        return world.resolve(this);
    }

    public static String typesToSignature(UnresolvedType returnType, UnresolvedType[] paramTypes, boolean eraseGenerics) {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        for (UnresolvedType paramType : paramTypes) {
            if (eraseGenerics) {
                buf.append(paramType.getErasureSignature());
                continue;
            }
            buf.append(paramType.getSignature());
        }
        buf.append(")");
        if (eraseGenerics) {
            buf.append(returnType.getErasureSignature());
        } else {
            buf.append(returnType.getSignature());
        }
        return buf.toString();
    }

    public static String typesToSignature(UnresolvedType[] paramTypes) {
        StringBuffer buf = new StringBuffer();
        buf.append("(");
        for (int i = 0; i < paramTypes.length; ++i) {
            buf.append(paramTypes[i].getSignature());
        }
        buf.append(")");
        return buf.toString();
    }

    private static Object[] signatureToTypes(String sig) {
        boolean hasParameters;
        boolean bl = hasParameters = sig.charAt(1) != ')';
        if (hasParameters) {
            char c;
            boolean hasAnyAnglies;
            ArrayList<UnresolvedType> l = new ArrayList<UnresolvedType>();
            int i = 1;
            boolean bl2 = hasAnyAnglies = sig.indexOf(60) != -1;
            while ((c = sig.charAt(i)) != ')') {
                int nextSemicolon;
                int start = i;
                while (c == '[') {
                    c = sig.charAt(++i);
                }
                if (c == 'L' || c == 'P') {
                    int firstAngly;
                    nextSemicolon = sig.indexOf(59, start);
                    int n = firstAngly = hasAnyAnglies ? sig.indexOf(60, start) : -1;
                    if (!hasAnyAnglies || firstAngly == -1 || firstAngly > nextSemicolon) {
                        i = nextSemicolon + 1;
                        l.add(UnresolvedType.forSignature(sig.substring(start, i)));
                        continue;
                    }
                    boolean endOfSigReached = false;
                    int posn = firstAngly;
                    int genericDepth = 0;
                    while (!endOfSigReached) {
                        switch (sig.charAt(posn)) {
                            case '<': {
                                ++genericDepth;
                                break;
                            }
                            case '>': {
                                --genericDepth;
                                break;
                            }
                            case ';': {
                                if (genericDepth != 0) break;
                                endOfSigReached = true;
                                break;
                            }
                        }
                        ++posn;
                    }
                    i = posn;
                    l.add(UnresolvedType.forSignature(sig.substring(start, i)));
                    continue;
                }
                if (c == 'T') {
                    nextSemicolon = sig.indexOf(59, start);
                    String nextbit = sig.substring(start, nextSemicolon + 1);
                    l.add(UnresolvedType.forSignature(nextbit));
                    i = nextSemicolon + 1;
                    continue;
                }
                l.add(UnresolvedType.forSignature(sig.substring(start, ++i)));
            }
            UnresolvedType[] paramTypes = l.toArray(new UnresolvedType[l.size()]);
            UnresolvedType returnType = UnresolvedType.forSignature(sig.substring(i + 1, sig.length()));
            return new Object[]{returnType, paramTypes};
        }
        UnresolvedType returnType = UnresolvedType.forSignature(sig.substring(2));
        return new Object[]{returnType, UnresolvedType.NONE};
    }

    public static MemberImpl field(String declaring, int mods, String name, String signature) {
        return MemberImpl.field(declaring, mods, UnresolvedType.forSignature(signature), name);
    }

    public static MemberImpl method(UnresolvedType declaring, int mods, String name, String signature) {
        Object[] pair = MemberImpl.signatureToTypes(signature);
        return MemberImpl.method(declaring, mods, (UnresolvedType)pair[0], name, (UnresolvedType[])pair[1]);
    }

    public static MemberImpl monitorEnter() {
        return new MemberImpl(MONITORENTER, UnresolvedType.OBJECT, 8, UnresolvedType.VOID, "<lock>", UnresolvedType.ARRAY_WITH_JUST_OBJECT);
    }

    public static MemberImpl monitorExit() {
        return new MemberImpl(MONITOREXIT, UnresolvedType.OBJECT, 8, UnresolvedType.VOID, "<unlock>", UnresolvedType.ARRAY_WITH_JUST_OBJECT);
    }

    public static Member pointcut(UnresolvedType declaring, String name, String signature) {
        Object[] pair = MemberImpl.signatureToTypes(signature);
        return MemberImpl.pointcut(declaring, 0, (UnresolvedType)pair[0], name, (UnresolvedType[])pair[1]);
    }

    private static MemberImpl field(String declaring, int mods, UnresolvedType ty, String name) {
        return new MemberImpl(FIELD, UnresolvedType.forName(declaring), mods, ty, name, UnresolvedType.NONE);
    }

    public static MemberImpl method(UnresolvedType declTy, int mods, UnresolvedType rTy, String name, UnresolvedType[] paramTys) {
        return new MemberImpl(name.equals("<init>") ? CONSTRUCTOR : METHOD, declTy, mods, rTy, name, paramTys);
    }

    private static Member pointcut(UnresolvedType declTy, int mods, UnresolvedType rTy, String name, UnresolvedType[] paramTys) {
        return new MemberImpl(POINTCUT, declTy, mods, rTy, name, paramTys);
    }

    public static ResolvedMemberImpl makeExceptionHandlerSignature(UnresolvedType inType, UnresolvedType catchType) {
        return new ResolvedMemberImpl(HANDLER, inType, 8, "<catch>", "(" + catchType.getSignature() + ")V");
    }

    public final boolean equals(Object other) {
        if (!(other instanceof Member)) {
            return false;
        }
        Member o = (Member)other;
        return this.getKind() == o.getKind() && this.getName().equals(o.getName()) && this.getSignature().equals(o.getSignature()) && this.getDeclaringType().equals(o.getDeclaringType());
    }

    public final boolean equalsApartFromDeclaringType(Object other) {
        if (!(other instanceof Member)) {
            return false;
        }
        Member o = (Member)other;
        return this.getKind() == o.getKind() && this.getName().equals(o.getName()) && this.getSignature().equals(o.getSignature());
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17;
            result = 37 * result + this.getKind().hashCode();
            result = 37 * result + this.getName().hashCode();
            result = 37 * result + this.getSignature().hashCode();
            this.hashCode = result = 37 * result + this.getDeclaringType().hashCode();
        }
        return this.hashCode;
    }

    @Override
    public int compareTo(Member other) {
        Member o = other;
        int i = this.getName().compareTo(o.getName());
        if (i != 0) {
            return i;
        }
        return this.getSignature().compareTo(o.getSignature());
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.returnType.getName());
        buf.append(' ');
        if (this.declaringType == null) {
            buf.append("<NULL>");
        } else {
            buf.append(this.declaringType.getName());
        }
        buf.append('.');
        buf.append(this.name);
        if (this.kind != FIELD) {
            buf.append("(");
            if (this.parameterTypes.length != 0) {
                buf.append(this.parameterTypes[0]);
                int len = this.parameterTypes.length;
                for (int i = 1; i < len; ++i) {
                    buf.append(", ");
                    buf.append(this.parameterTypes[i].getName());
                }
            }
            buf.append(")");
        }
        return buf.toString();
    }

    @Override
    public MemberKind getKind() {
        return this.kind;
    }

    @Override
    public UnresolvedType getDeclaringType() {
        return this.declaringType;
    }

    @Override
    public UnresolvedType getReturnType() {
        return this.returnType;
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
    public final UnresolvedType getType() {
        return this.returnType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UnresolvedType[] getParameterTypes() {
        return this.parameterTypes;
    }

    @Override
    public String getSignature() {
        return this.erasedSignature;
    }

    @Override
    public int getArity() {
        return this.parameterTypes.length;
    }

    @Override
    public String getParameterSignature() {
        if (this.paramSignature == null) {
            StringBuilder sb = new StringBuilder("(");
            for (UnresolvedType parameterType : this.parameterTypes) {
                sb.append(parameterType.getSignature());
            }
            this.paramSignature = sb.append(")").toString();
        }
        return this.paramSignature;
    }

    @Override
    public int getModifiers(World world) {
        ResolvedMember resolved = this.resolve(world);
        if (resolved == null) {
            this.reportDidntFindMember(world);
            return 0;
        }
        return resolved.getModifiers();
    }

    @Override
    public UnresolvedType[] getExceptions(World world) {
        ResolvedMember resolved = this.resolve(world);
        if (resolved == null) {
            this.reportDidntFindMember(world);
            return UnresolvedType.NONE;
        }
        return resolved.getExceptions();
    }

    public final boolean isStatic() {
        return Modifier.isStatic(this.modifiers);
    }

    public final boolean isInterface() {
        return Modifier.isInterface(this.modifiers);
    }

    public final boolean isPrivate() {
        return Modifier.isPrivate(this.modifiers);
    }

    @Override
    public boolean canBeParameterized() {
        return false;
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        throw new UnsupportedOperationException("You should resolve this member '" + this + "' and call getAnnotations() on the result...");
    }

    @Override
    public Collection<ResolvedType> getDeclaringTypes(World world) {
        ResolvedType myType = this.getDeclaringType().resolve(world);
        HashSet<ResolvedType> ret = new HashSet<ResolvedType>();
        if (this.kind == CONSTRUCTOR) {
            ret.add(myType);
        } else if (Modifier.isStatic(this.modifiers) || this.kind == FIELD) {
            this.walkUpStatic(ret, myType);
        } else {
            this.walkUp(ret, myType);
        }
        return ret;
    }

    private boolean walkUp(Collection<ResolvedType> acc, ResolvedType curr) {
        if (acc.contains(curr)) {
            return true;
        }
        boolean b = false;
        Iterator<ResolvedType> i = curr.getDirectSupertypes();
        while (i.hasNext()) {
            b |= this.walkUp(acc, i.next());
        }
        if (!b && curr.isParameterizedType()) {
            b = this.walkUp(acc, curr.getGenericType());
        }
        if (!b) {
            boolean bl = b = curr.lookupMemberNoSupers(this) != null;
        }
        if (b) {
            acc.add(curr);
        }
        return b;
    }

    private boolean walkUpStatic(Collection<ResolvedType> acc, ResolvedType curr) {
        if (curr.lookupMemberNoSupers(this) != null) {
            acc.add(curr);
            return true;
        }
        boolean b = false;
        Iterator<ResolvedType> i = curr.getDirectSupertypes();
        while (i.hasNext()) {
            b |= this.walkUpStatic(acc, i.next());
        }
        if (!b && curr.isParameterizedType()) {
            b = this.walkUpStatic(acc, curr.getGenericType());
        }
        if (b) {
            acc.add(curr);
        }
        return b;
    }

    @Override
    public String[] getParameterNames(World world) {
        ResolvedMember resolved = this.resolve(world);
        if (resolved == null) {
            this.reportDidntFindMember(world);
            return null;
        }
        return resolved.getParameterNames();
    }

    @Override
    public JoinPointSignatureIterator getJoinPointSignatures(World inAWorld) {
        if (this.joinPointSignatures == null) {
            this.joinPointSignatures = new JoinPointSignatureIterator(this, inAWorld);
        }
        this.joinPointSignatures.reset();
        return this.joinPointSignatures;
    }

    private void reportDidntFindMember(World world) {
        if (this.reportedCantFindDeclaringType || this.reportedUnresolvableMember) {
            return;
        }
        ResolvedType rType = this.getDeclaringType().resolve(world);
        if (rType.isMissing()) {
            world.getLint().cantFindType.signal(WeaverMessages.format("cantFindType", rType.getName()), null);
            this.reportedCantFindDeclaringType = true;
        } else {
            world.getLint().unresolvableMember.signal(this.getName(), null);
            this.reportedUnresolvableMember = true;
        }
    }

    public void wipeJoinpointSignatures() {
        this.joinPointSignatures = null;
    }
}

