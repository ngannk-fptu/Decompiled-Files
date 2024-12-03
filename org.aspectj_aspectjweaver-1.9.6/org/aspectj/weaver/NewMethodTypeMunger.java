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

public class NewMethodTypeMunger
extends ResolvedTypeMunger {
    public NewMethodTypeMunger(ResolvedMember signature, Set superMethodsCalled, List typeVariableAliases) {
        super(Method, signature);
        this.typeVariableAliases = typeVariableAliases;
        this.setSuperMethodsCalled(superMethodsCalled);
    }

    public ResolvedMember getInterMethodBody(UnresolvedType aspectType) {
        return AjcMemberMaker.interMethodBody(this.signature, aspectType);
    }

    public ResolvedMember getDeclaredInterMethodBody(UnresolvedType aspectType, World w) {
        if (this.declaredSignature != null) {
            ResolvedMemberImpl rm = this.declaredSignature.parameterizedWith(null, this.signature.getDeclaringType().resolve(w), false, this.getTypeVariableAliases());
            return AjcMemberMaker.interMethodBody(rm, aspectType);
        }
        return AjcMemberMaker.interMethodBody(this.signature, aspectType);
    }

    public ResolvedMember getDeclaredInterMethodDispatcher(UnresolvedType aspectType, World w) {
        if (this.declaredSignature != null) {
            ResolvedMemberImpl rm = this.declaredSignature.parameterizedWith(null, this.signature.getDeclaringType().resolve(w), false, this.getTypeVariableAliases());
            return AjcMemberMaker.interMethodDispatcher(rm, aspectType);
        }
        return AjcMemberMaker.interMethodDispatcher(this.signature, aspectType);
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        this.kind.write(s);
        this.signature.write(s);
        this.writeSuperMethodsCalled(s);
        this.writeSourceLocation(s);
        this.writeOutTypeAliases(s);
    }

    public static ResolvedTypeMunger readMethod(VersionedDataInputStream s, ISourceContext context) throws IOException {
        ISourceLocation sloc = null;
        ResolvedMemberImpl rmImpl = ResolvedMemberImpl.readResolvedMember(s, context);
        Set<ResolvedMember> superMethodsCalled = NewMethodTypeMunger.readSuperMethodsCalled(s);
        sloc = NewMethodTypeMunger.readSourceLocation(s);
        List<String> typeVarAliases = NewMethodTypeMunger.readInTypeAliases(s);
        NewMethodTypeMunger munger = new NewMethodTypeMunger(rmImpl, superMethodsCalled, typeVarAliases);
        if (sloc != null) {
            munger.setSourceLocation(sloc);
        }
        return munger;
    }

    @Override
    public ResolvedMember getMatchingSyntheticMember(Member member, ResolvedType aspectType) {
        ResolvedMember ret = AjcMemberMaker.interMethodDispatcher(this.getSignature(), aspectType);
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
        NewMethodTypeMunger nmtm = new NewMethodTypeMunger(parameterizedSignature, this.getSuperMethodsCalled(), this.typeVariableAliases);
        nmtm.setDeclaredSignature(this.getSignature());
        nmtm.setSourceLocation(this.getSourceLocation());
        return nmtm;
    }

    public boolean equals(Object other) {
        if (!(other instanceof NewMethodTypeMunger)) {
            return false;
        }
        NewMethodTypeMunger o = (NewMethodTypeMunger)other;
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

    @Override
    public ResolvedTypeMunger parameterizeWith(Map<String, UnresolvedType> m, World w) {
        ResolvedMember parameterizedSignature = this.getSignature().parameterizedWith(m, w);
        NewMethodTypeMunger nmtm = new NewMethodTypeMunger(parameterizedSignature, this.getSuperMethodsCalled(), this.typeVariableAliases);
        nmtm.setDeclaredSignature(this.getSignature());
        nmtm.setSourceLocation(this.getSourceLocation());
        return nmtm;
    }
}

