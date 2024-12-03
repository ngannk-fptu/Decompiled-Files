/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.util.TypeSafeEnum;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MethodDelegateTypeMunger;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.aspectj.weaver.NewFieldTypeMunger;
import org.aspectj.weaver.NewMemberClassTypeMunger;
import org.aspectj.weaver.NewMethodTypeMunger;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;

public abstract class ResolvedTypeMunger {
    protected Kind kind;
    protected ResolvedMember signature;
    protected ResolvedMember declaredSignature;
    protected List<String> typeVariableAliases;
    private Set<ResolvedMember> superMethodsCalled = Collections.emptySet();
    private ISourceLocation location;
    private ResolvedType onType = null;
    public static final Kind Field = new Kind("Field", 1);
    public static final Kind Method = new Kind("Method", 2);
    public static final Kind Constructor = new Kind("Constructor", 5);
    public static final Kind PerObjectInterface = new Kind("PerObjectInterface", 3);
    public static final Kind PrivilegedAccess = new Kind("PrivilegedAccess", 4);
    public static final Kind Parent = new Kind("Parent", 6);
    public static final Kind PerTypeWithinInterface = new Kind("PerTypeWithinInterface", 7);
    public static final Kind AnnotationOnType = new Kind("AnnotationOnType", 8);
    public static final Kind MethodDelegate = new Kind("MethodDelegate", 9);
    public static final Kind FieldHost = new Kind("FieldHost", 10);
    public static final Kind MethodDelegate2 = new Kind("MethodDelegate2", 11);
    public static final Kind InnerClass = new Kind("InnerClass", 12);
    public static final String SUPER_DISPATCH_NAME = "superDispatch";

    public ResolvedTypeMunger(Kind kind, ResolvedMember signature) {
        UnresolvedType declaringType;
        this.kind = kind;
        this.signature = signature;
        UnresolvedType unresolvedType = declaringType = signature != null ? signature.getDeclaringType() : null;
        if (declaringType != null) {
            if (declaringType.isRawType()) {
                throw new IllegalStateException("Use generic type, not raw type");
            }
            if (declaringType.isParameterizedType()) {
                throw new IllegalStateException("Use generic type, not parameterized type");
            }
        }
    }

    public void setSourceLocation(ISourceLocation isl) {
        this.location = isl;
    }

    public ISourceLocation getSourceLocation() {
        return this.location;
    }

    public boolean matches(ResolvedType matchType, ResolvedType aspectType) {
        if (this.onType == null) {
            this.onType = matchType.getWorld().resolve(this.getDeclaringType());
            if (this.onType.isRawType()) {
                this.onType = this.onType.getGenericType();
            }
        }
        if (matchType.equals(this.onType)) {
            if (!this.onType.isExposedToWeaver()) {
                boolean ok;
                boolean bl = ok = this.onType.isInterface() && this.onType.lookupMemberWithSupersAndITDs(this.getSignature()) != null;
                if (!ok && this.onType.getWeaverState() == null && matchType.getWorld().getLint().typeNotExposedToWeaver.isEnabled()) {
                    matchType.getWorld().getLint().typeNotExposedToWeaver.signal(matchType.getName(), this.signature.getSourceLocation());
                }
            }
            return true;
        }
        if (this.onType.isInterface()) {
            return matchType.isTopmostImplementor(this.onType);
        }
        return false;
    }

    public String toString() {
        return "ResolvedTypeMunger(" + this.getKind() + ", " + this.getSignature() + ")";
    }

    public static ResolvedTypeMunger read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        Kind kind = Kind.read(s);
        if (kind == Field) {
            return NewFieldTypeMunger.readField(s, context);
        }
        if (kind == Method) {
            return NewMethodTypeMunger.readMethod(s, context);
        }
        if (kind == Constructor) {
            return NewConstructorTypeMunger.readConstructor(s, context);
        }
        if (kind == MethodDelegate) {
            return MethodDelegateTypeMunger.readMethod(s, context, false);
        }
        if (kind == FieldHost) {
            return MethodDelegateTypeMunger.FieldHostTypeMunger.readFieldHost(s, context);
        }
        if (kind == MethodDelegate2) {
            return MethodDelegateTypeMunger.readMethod(s, context, true);
        }
        if (kind == InnerClass) {
            return NewMemberClassTypeMunger.readInnerClass(s, context);
        }
        throw new RuntimeException("unimplemented");
    }

    protected static Set<ResolvedMember> readSuperMethodsCalled(VersionedDataInputStream s) throws IOException {
        HashSet<ResolvedMember> ret = new HashSet<ResolvedMember>();
        int n = -1;
        n = s.isAtLeast169() ? (int)s.readByte() : s.readInt();
        if (n < 0) {
            throw new BCException("Problem deserializing type munger");
        }
        for (int i = 0; i < n; ++i) {
            ret.add(ResolvedMemberImpl.readResolvedMember(s, null));
        }
        return ret;
    }

    protected final void writeSuperMethodsCalled(CompressingDataOutputStream s) throws IOException {
        if (this.superMethodsCalled == null || this.superMethodsCalled.size() == 0) {
            s.writeByte(0);
            return;
        }
        ArrayList<ResolvedMember> ret = new ArrayList<ResolvedMember>(this.superMethodsCalled);
        Collections.sort(ret);
        int n = ret.size();
        s.writeByte(n);
        for (ResolvedMember m : ret) {
            m.write(s);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static ISourceLocation readSourceLocation(VersionedDataInputStream s) throws IOException {
        if (s.getMajorVersion() < 2) {
            return null;
        }
        SourceLocation ret = null;
        try (ObjectInputStream ois = null;){
            byte b = 0;
            if (!s.isAtLeast169() || (b = s.readByte()) == 0) {
                ois = new ObjectInputStream(s);
                boolean validLocation = (Boolean)ois.readObject();
                if (validLocation) {
                    File f = (File)ois.readObject();
                    Integer ii = (Integer)ois.readObject();
                    Integer offset = (Integer)ois.readObject();
                    ret = new SourceLocation(f, ii);
                    ret.setOffset(offset);
                }
            } else {
                boolean validLocation;
                boolean bl = validLocation = b == 2;
                if (validLocation) {
                    String path = s.readUtf8(s.readShort());
                    File f = new File(path);
                    ret = new SourceLocation(f, s.readInt());
                    int offset = s.readInt();
                    ret.setOffset(offset);
                }
            }
        }
        return ret;
    }

    protected final void writeSourceLocation(CompressingDataOutputStream s) throws IOException {
        if (s.canCompress()) {
            s.writeByte(1 + (this.location == null ? 0 : 1));
            if (this.location != null) {
                s.writeCompressedPath(this.location.getSourceFile().getPath());
                s.writeInt(this.location.getLine());
                s.writeInt(this.location.getOffset());
            }
        } else {
            s.writeByte(0);
            ObjectOutputStream oos = new ObjectOutputStream(s);
            oos.writeObject(new Boolean(this.location != null));
            if (this.location != null) {
                oos.writeObject(this.location.getSourceFile());
                oos.writeObject(new Integer(this.location.getLine()));
                oos.writeObject(new Integer(this.location.getOffset()));
            }
            oos.flush();
            oos.close();
        }
    }

    public abstract void write(CompressingDataOutputStream var1) throws IOException;

    public Kind getKind() {
        return this.kind;
    }

    public void setSuperMethodsCalled(Set<ResolvedMember> c) {
        this.superMethodsCalled = c;
    }

    public Set<ResolvedMember> getSuperMethodsCalled() {
        return this.superMethodsCalled;
    }

    public ResolvedMember getSignature() {
        return this.signature;
    }

    public ResolvedMember getMatchingSyntheticMember(Member member, ResolvedType aspectType) {
        if (this.getSignature() != null && this.getSignature().isPublic() && member.equals(this.getSignature())) {
            return this.getSignature();
        }
        return null;
    }

    public boolean changesPublicSignature() {
        return this.kind == Field || this.kind == Method || this.kind == Constructor;
    }

    public boolean needsAccessToTopmostImplementor() {
        if (this.kind == Field) {
            return true;
        }
        if (this.kind == Method) {
            return !this.signature.isAbstract();
        }
        return false;
    }

    protected static List<String> readInTypeAliases(VersionedDataInputStream s) throws IOException {
        if (s.getMajorVersion() >= 2) {
            int count = -1;
            count = s.isAtLeast169() ? (int)s.readByte() : s.readInt();
            if (count != 0) {
                ArrayList<String> aliases = new ArrayList<String>();
                for (int i = 0; i < count; ++i) {
                    aliases.add(s.readUTF());
                }
                return aliases;
            }
        }
        return null;
    }

    protected final void writeOutTypeAliases(DataOutputStream s) throws IOException {
        if (this.typeVariableAliases == null || this.typeVariableAliases.size() == 0) {
            s.writeByte(0);
        } else {
            s.writeByte(this.typeVariableAliases.size());
            for (String element : this.typeVariableAliases) {
                s.writeUTF(element);
            }
        }
    }

    public List<String> getTypeVariableAliases() {
        return this.typeVariableAliases;
    }

    protected void setTypeVariableAliases(List<String> typeVariableAliases) {
        this.typeVariableAliases = typeVariableAliases;
    }

    public boolean hasTypeVariableAliases() {
        return this.typeVariableAliases != null && this.typeVariableAliases.size() > 0;
    }

    public boolean sharesTypeVariablesWithGenericType() {
        return this.typeVariableAliases != null && this.typeVariableAliases.size() > 0;
    }

    public ResolvedTypeMunger parameterizedFor(ResolvedType target) {
        throw new BCException("Dont call parameterizedFor on a type munger of this kind: " + this.getClass());
    }

    public void setDeclaredSignature(ResolvedMember rm) {
        this.declaredSignature = rm;
    }

    public ResolvedMember getDeclaredSignature() {
        return this.declaredSignature;
    }

    public boolean isLateMunger() {
        return false;
    }

    public boolean existsToSupportShadowMunging() {
        return false;
    }

    public ResolvedTypeMunger parameterizeWith(Map<String, UnresolvedType> m, World w) {
        throw new BCException("Dont call parameterizeWith() on a type munger of this kind: " + this.getClass());
    }

    public UnresolvedType getDeclaringType() {
        return this.getSignature().getDeclaringType();
    }

    public static class Kind
    extends TypeSafeEnum {
        Kind(String name, int key) {
            super(name, key);
        }

        public static Kind read(DataInputStream s) throws IOException {
            byte key = s.readByte();
            switch (key) {
                case 1: {
                    return Field;
                }
                case 2: {
                    return Method;
                }
                case 5: {
                    return Constructor;
                }
                case 9: {
                    return MethodDelegate;
                }
                case 10: {
                    return FieldHost;
                }
                case 11: {
                    return MethodDelegate2;
                }
                case 12: {
                    return InnerClass;
                }
            }
            throw new BCException("bad kind: " + key);
        }

        @Override
        public String toString() {
            if (this.getName().startsWith(MethodDelegate.getName())) {
                return Method.toString();
            }
            return super.toString();
        }
    }
}

