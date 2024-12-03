/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.UnresolvedTypeVariableReferenceType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;

public class NewFieldTypeMunger
extends ResolvedTypeMunger {
    public static final int VersionOne = 1;
    public static final int VersionTwo = 2;
    public int version = 2;

    public NewFieldTypeMunger(ResolvedMember signature, Set superMethodsCalled, List typeVariableAliases) {
        super(Field, signature);
        this.typeVariableAliases = typeVariableAliases;
        signature.setAnnotatedElsewhere(true);
        this.setSuperMethodsCalled(superMethodsCalled);
    }

    public ResolvedMember getInitMethod(UnresolvedType aspectType) {
        return AjcMemberMaker.interFieldInitializer(this.signature, aspectType);
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        this.kind.write(s);
        this.signature.write(s);
        this.writeSuperMethodsCalled(s);
        this.writeSourceLocation(s);
        this.writeOutTypeAliases(s);
        s.writeInt(this.version);
    }

    public static ResolvedTypeMunger readField(VersionedDataInputStream s, ISourceContext context) throws IOException {
        ISourceLocation sloc = null;
        ResolvedMemberImpl fieldSignature = ResolvedMemberImpl.readResolvedMember(s, context);
        Set<ResolvedMember> superMethodsCalled = NewFieldTypeMunger.readSuperMethodsCalled(s);
        sloc = NewFieldTypeMunger.readSourceLocation(s);
        List<String> aliases = NewFieldTypeMunger.readInTypeAliases(s);
        NewFieldTypeMunger munger = new NewFieldTypeMunger(fieldSignature, superMethodsCalled, aliases);
        if (sloc != null) {
            munger.setSourceLocation(sloc);
        }
        munger.version = s.getMajorVersion() >= 7 ? s.readInt() : 1;
        return munger;
    }

    @Override
    public ResolvedMember getMatchingSyntheticMember(Member member, ResolvedType aspectType) {
        ResolvedMember ret;
        ResolvedType onType = aspectType.getWorld().resolve(this.getSignature().getDeclaringType());
        if (onType.isRawType()) {
            onType = onType.getGenericType();
        }
        if (ResolvedType.matches(ret = AjcMemberMaker.interFieldGetDispatcher(this.getSignature(), aspectType), member)) {
            return this.getSignature();
        }
        ret = AjcMemberMaker.interFieldSetDispatcher(this.getSignature(), aspectType);
        if (ResolvedType.matches(ret, member)) {
            return this.getSignature();
        }
        ret = AjcMemberMaker.interFieldInterfaceGetter(this.getSignature(), onType, aspectType);
        if (ResolvedType.matches(ret, member)) {
            return this.getSignature();
        }
        ret = AjcMemberMaker.interFieldInterfaceSetter(this.getSignature(), onType, aspectType);
        if (ResolvedType.matches(ret, member)) {
            return this.getSignature();
        }
        return super.getMatchingSyntheticMember(member, aspectType);
    }

    @Override
    public ResolvedTypeMunger parameterizedFor(ResolvedType target) {
        ResolvedType genericType = target;
        if (target.isRawType() || target.isParameterizedType()) {
            genericType = genericType.getGenericType();
        }
        ResolvedMemberImpl parameterizedSignature = null;
        if (target.isGenericType()) {
            TypeVariable[] vars = target.getTypeVariables();
            UnresolvedType[] varRefs = new UnresolvedTypeVariableReferenceType[vars.length];
            for (int i = 0; i < vars.length; ++i) {
                varRefs[i] = new UnresolvedTypeVariableReferenceType(vars[i]);
            }
            parameterizedSignature = this.getSignature().parameterizedWith(varRefs, genericType, true, this.typeVariableAliases);
        } else {
            parameterizedSignature = this.getSignature().parameterizedWith(target.getTypeParameters(), genericType, target.isParameterizedType(), this.typeVariableAliases);
        }
        NewFieldTypeMunger nftm = new NewFieldTypeMunger(parameterizedSignature, this.getSuperMethodsCalled(), this.typeVariableAliases);
        nftm.setDeclaredSignature(this.getSignature());
        nftm.setSourceLocation(this.getSourceLocation());
        return nftm;
    }

    @Override
    public ResolvedTypeMunger parameterizeWith(Map<String, UnresolvedType> m, World w) {
        ResolvedMember parameterizedSignature = this.getSignature().parameterizedWith(m, w);
        NewFieldTypeMunger nftm = new NewFieldTypeMunger(parameterizedSignature, this.getSuperMethodsCalled(), this.typeVariableAliases);
        nftm.setDeclaredSignature(this.getSignature());
        nftm.setSourceLocation(this.getSourceLocation());
        return nftm;
    }

    public boolean equals(Object other) {
        if (!(other instanceof NewFieldTypeMunger)) {
            return false;
        }
        NewFieldTypeMunger o = (NewFieldTypeMunger)other;
        return (this.kind == null ? o.kind == null : this.kind.equals(o.kind)) && (this.signature == null ? o.signature == null : this.signature.equals(o.signature)) && (this.declaredSignature == null ? o.declaredSignature == null : this.declaredSignature.equals(o.declaredSignature)) && (this.typeVariableAliases == null ? o.typeVariableAliases == null : this.typeVariableAliases.equals(o.typeVariableAliases));
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.kind.hashCode();
        result = 37 * result + (this.signature == null ? 0 : this.signature.hashCode());
        result = 37 * result + (this.declaredSignature == null ? 0 : this.declaredSignature.hashCode());
        result = 37 * result + (this.typeVariableAliases == null ? 0 : this.typeVariableAliases.hashCode());
        return result;
    }
}

