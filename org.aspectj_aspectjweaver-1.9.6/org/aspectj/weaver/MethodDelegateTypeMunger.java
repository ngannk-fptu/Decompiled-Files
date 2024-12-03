/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.TypePattern;

public class MethodDelegateTypeMunger
extends ResolvedTypeMunger {
    private final UnresolvedType aspect;
    private UnresolvedType fieldType;
    private final String implClassName;
    private final TypePattern typePattern;
    private String factoryMethodName;
    private String factoryMethodSignature;
    private int bitflags;
    private static final int REPLACING_EXISTING_METHOD = 1;
    private volatile int hashCode = 0;

    public MethodDelegateTypeMunger(ResolvedMember signature, UnresolvedType aspect, String implClassName, TypePattern typePattern) {
        super(MethodDelegate2, signature);
        this.aspect = aspect;
        this.typePattern = typePattern;
        this.implClassName = implClassName;
        this.factoryMethodName = "";
        this.factoryMethodSignature = "";
    }

    public MethodDelegateTypeMunger(ResolvedMember signature, UnresolvedType aspect, String implClassName, TypePattern typePattern, String factoryMethodName, String factoryMethodSignature) {
        super(MethodDelegate2, signature);
        this.aspect = aspect;
        this.typePattern = typePattern;
        this.implClassName = implClassName;
        this.factoryMethodName = factoryMethodName;
        this.factoryMethodSignature = factoryMethodSignature;
    }

    public boolean equals(Object other) {
        if (!(other instanceof MethodDelegateTypeMunger)) {
            return false;
        }
        MethodDelegateTypeMunger o = (MethodDelegateTypeMunger)other;
        return (o.aspect == null ? this.aspect == null : this.aspect.equals(o.aspect)) && (o.typePattern == null ? this.typePattern == null : this.typePattern.equals(o.typePattern)) && (o.implClassName == null ? this.implClassName == null : this.implClassName.equals(o.implClassName)) && (o.fieldType == null ? this.fieldType == null : this.fieldType.equals(o.fieldType)) && (o.factoryMethodName == null ? this.factoryMethodName == null : this.factoryMethodName.equals(o.factoryMethodName)) && (o.factoryMethodSignature == null ? this.factoryMethodSignature == null : this.factoryMethodSignature.equals(o.factoryMethodSignature)) && o.bitflags == this.bitflags;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17;
            result = 37 * result + (this.aspect == null ? 0 : this.aspect.hashCode());
            result = 37 * result + (this.typePattern == null ? 0 : this.typePattern.hashCode());
            result = 37 * result + (this.implClassName == null ? 0 : this.implClassName.hashCode());
            result = 37 * result + (this.fieldType == null ? 0 : this.fieldType.hashCode());
            result = 37 * result + (this.factoryMethodName == null ? 0 : this.factoryMethodName.hashCode());
            result = 37 * result + (this.factoryMethodSignature == null ? 0 : this.factoryMethodSignature.hashCode());
            this.hashCode = result = 37 * result + this.bitflags;
        }
        return this.hashCode;
    }

    public ResolvedMember getDelegate(ResolvedType targetType) {
        return AjcMemberMaker.itdAtDeclareParentsField(targetType, this.fieldType, this.aspect);
    }

    public ResolvedMember getDelegateFactoryMethod(World w) {
        ResolvedType aspectType = w.resolve(this.aspect);
        ResolvedMember[] methods = aspectType.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            ResolvedMember rm = methods[i];
            if (!rm.getName().equals(this.factoryMethodName) || !rm.getSignature().equals(this.factoryMethodSignature)) continue;
            return rm;
        }
        return null;
    }

    public String getImplClassName() {
        return this.implClassName;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        this.kind.write(s);
        this.signature.write(s);
        this.aspect.write(s);
        s.writeUTF(this.implClassName == null ? "" : this.implClassName);
        this.typePattern.write(s);
        this.fieldType.write(s);
        s.writeUTF(this.factoryMethodName);
        s.writeUTF(this.factoryMethodSignature);
        s.writeInt(this.bitflags);
    }

    public static ResolvedTypeMunger readMethod(VersionedDataInputStream s, ISourceContext context, boolean isEnhanced) throws IOException {
        ResolvedMemberImpl signature = ResolvedMemberImpl.readResolvedMember(s, context);
        UnresolvedType aspect = UnresolvedType.read(s);
        String implClassName = s.readUTF();
        if (implClassName.equals("")) {
            implClassName = null;
        }
        TypePattern tp = TypePattern.read(s, context);
        MethodDelegateTypeMunger typeMunger = new MethodDelegateTypeMunger(signature, aspect, implClassName, tp);
        UnresolvedType fieldType = null;
        fieldType = isEnhanced ? UnresolvedType.read(s) : signature.getDeclaringType();
        typeMunger.setFieldType(fieldType);
        if (isEnhanced) {
            typeMunger.factoryMethodName = s.readUTF();
            typeMunger.factoryMethodSignature = s.readUTF();
            typeMunger.bitflags = s.readInt();
        }
        return typeMunger;
    }

    @Override
    public boolean matches(ResolvedType matchType, ResolvedType aspectType) {
        if (matchType.isEnum() || matchType.isInterface() || matchType.isAnnotation()) {
            return false;
        }
        return this.typePattern.matchesStatically(matchType);
    }

    @Override
    public boolean changesPublicSignature() {
        return true;
    }

    public void setFieldType(UnresolvedType fieldType) {
        this.fieldType = fieldType;
    }

    public boolean specifiesDelegateFactoryMethod() {
        return this.factoryMethodName != null && this.factoryMethodName.length() != 0;
    }

    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }

    public String getFactoryMethodSignature() {
        return this.factoryMethodSignature;
    }

    public UnresolvedType getAspect() {
        return this.aspect;
    }

    @Override
    public boolean existsToSupportShadowMunging() {
        return true;
    }

    public void tagAsReplacingExistingMethod() {
        this.bitflags |= 1;
    }

    public boolean isReplacingExistingMethod() {
        return (this.bitflags & 1) != 0;
    }

    public static class FieldHostTypeMunger
    extends ResolvedTypeMunger {
        private final UnresolvedType aspect;
        private final TypePattern typePattern;

        public FieldHostTypeMunger(ResolvedMember field, UnresolvedType aspect, TypePattern typePattern) {
            super(FieldHost, field);
            this.aspect = aspect;
            this.typePattern = typePattern;
        }

        public boolean equals(Object other) {
            if (!(other instanceof FieldHostTypeMunger)) {
                return false;
            }
            FieldHostTypeMunger o = (FieldHostTypeMunger)other;
            return (o.aspect == null ? this.aspect == null : this.aspect.equals(o.aspect)) && (o.typePattern == null ? this.typePattern == null : this.typePattern.equals(o.typePattern));
        }

        public int hashCode() {
            int result = 17;
            result = 37 * result + (this.aspect == null ? 0 : this.aspect.hashCode());
            result = 37 * result + (this.typePattern == null ? 0 : this.typePattern.hashCode());
            return result;
        }

        @Override
        public void write(CompressingDataOutputStream s) throws IOException {
            this.kind.write(s);
            this.signature.write(s);
            this.aspect.write(s);
            this.typePattern.write(s);
        }

        public static ResolvedTypeMunger readFieldHost(VersionedDataInputStream s, ISourceContext context) throws IOException {
            ResolvedMemberImpl signature = ResolvedMemberImpl.readResolvedMember(s, context);
            UnresolvedType aspect = UnresolvedType.read(s);
            TypePattern tp = TypePattern.read(s, context);
            return new FieldHostTypeMunger(signature, aspect, tp);
        }

        @Override
        public boolean matches(ResolvedType matchType, ResolvedType aspectType) {
            if (matchType.isEnum() || matchType.isInterface() || matchType.isAnnotation()) {
                return false;
            }
            return this.typePattern.matchesStatically(matchType);
        }

        @Override
        public boolean changesPublicSignature() {
            return false;
        }

        @Override
        public boolean existsToSupportShadowMunging() {
            return true;
        }
    }
}

