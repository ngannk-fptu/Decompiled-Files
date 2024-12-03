/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import java.util.HashMap;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.patterns.Pointcut;

public class ResolvedPointcutDefinition
extends ResolvedMemberImpl {
    private Pointcut pointcut;
    public static final ResolvedPointcutDefinition DUMMY = new ResolvedPointcutDefinition(UnresolvedType.OBJECT, 0, "missing", UnresolvedType.NONE, Pointcut.makeMatchesNothing(Pointcut.RESOLVED));
    public static final ResolvedPointcutDefinition[] NO_POINTCUTS = new ResolvedPointcutDefinition[0];

    public ResolvedPointcutDefinition(UnresolvedType declaringType, int modifiers, String name, UnresolvedType[] parameterTypes, Pointcut pointcut) {
        this(declaringType, modifiers, name, parameterTypes, UnresolvedType.VOID, pointcut);
    }

    public ResolvedPointcutDefinition(UnresolvedType declaringType, int modifiers, String name, UnresolvedType[] parameterTypes, UnresolvedType returnType, Pointcut pointcut) {
        super(POINTCUT, declaringType, modifiers, returnType, name, parameterTypes);
        this.pointcut = pointcut;
        this.checkedExceptions = UnresolvedType.NONE;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        this.getDeclaringType().write(s);
        s.writeInt(this.getModifiers());
        s.writeUTF(this.getName());
        UnresolvedType.writeArray(this.getParameterTypes(), s);
        this.pointcut.write(s);
    }

    public static ResolvedPointcutDefinition read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        ResolvedPointcutDefinition rpd = new ResolvedPointcutDefinition(UnresolvedType.read(s), s.readInt(), s.readUTF(), UnresolvedType.readArray(s), Pointcut.read(s, context));
        rpd.setSourceContext(context);
        return rpd;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("pointcut ");
        buf.append(this.getDeclaringType() == null ? "<nullDeclaringType>" : this.getDeclaringType().getName());
        buf.append(".");
        buf.append(this.getName());
        buf.append("(");
        for (int i = 0; i < this.getParameterTypes().length; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(this.getParameterTypes()[i].toString());
        }
        buf.append(")");
        return buf.toString();
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public boolean isAjSynthetic() {
        return true;
    }

    @Override
    public ResolvedMemberImpl parameterizedWith(UnresolvedType[] typeParameters, ResolvedType newDeclaringType, boolean isParameterized) {
        boolean typeParametersSupplied;
        TypeVariable[] typeVariables = this.getDeclaringType().resolve(newDeclaringType.getWorld()).getTypeVariables();
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
        UnresolvedType parameterizedReturnType = this.parameterize(this.getGenericReturnType(), typeMap, isParameterized, newDeclaringType.getWorld());
        UnresolvedType[] parameterizedParameterTypes = new UnresolvedType[this.getGenericParameterTypes().length];
        for (int i = 0; i < parameterizedParameterTypes.length; ++i) {
            parameterizedParameterTypes[i] = this.parameterize(this.getGenericParameterTypes()[i], typeMap, isParameterized, newDeclaringType.getWorld());
        }
        ResolvedPointcutDefinition ret = new ResolvedPointcutDefinition(newDeclaringType, this.getModifiers(), this.getName(), parameterizedParameterTypes, parameterizedReturnType, this.pointcut.parameterizeWith(typeMap, newDeclaringType.getWorld()));
        ret.setTypeVariables(this.getTypeVariables());
        ret.setSourceContext(this.getSourceContext());
        ret.setPosition(this.getStart(), this.getEnd());
        ret.setParameterNames(this.getParameterNames());
        return ret;
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }
}

