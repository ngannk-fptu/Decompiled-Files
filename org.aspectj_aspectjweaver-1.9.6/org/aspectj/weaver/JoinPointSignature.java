/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.JoinPointSignatureIterator;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberKind;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public class JoinPointSignature
implements ResolvedMember {
    public static final JoinPointSignature[] EMPTY_ARRAY = new JoinPointSignature[0];
    private ResolvedMember realMember;
    private ResolvedType substituteDeclaringType;

    public JoinPointSignature(ResolvedMember backing, ResolvedType aType) {
        this.realMember = backing;
        this.substituteDeclaringType = aType;
    }

    @Override
    public UnresolvedType getDeclaringType() {
        return this.substituteDeclaringType;
    }

    @Override
    public int getModifiers(World world) {
        return this.realMember.getModifiers(world);
    }

    @Override
    public int getModifiers() {
        return this.realMember.getModifiers();
    }

    @Override
    public UnresolvedType[] getExceptions(World world) {
        return this.realMember.getExceptions(world);
    }

    @Override
    public UnresolvedType[] getExceptions() {
        return this.realMember.getExceptions();
    }

    @Override
    public ShadowMunger getAssociatedShadowMunger() {
        return this.realMember.getAssociatedShadowMunger();
    }

    @Override
    public boolean isAjSynthetic() {
        return this.realMember.isAjSynthetic();
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        return this.realMember.hasAnnotation(ofType);
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        return this.realMember.getAnnotationTypes();
    }

    @Override
    public AnnotationAJ getAnnotationOfType(UnresolvedType ofType) {
        return this.realMember.getAnnotationOfType(ofType);
    }

    @Override
    public void setAnnotationTypes(ResolvedType[] annotationtypes) {
        this.realMember.setAnnotationTypes(annotationtypes);
    }

    @Override
    public void setAnnotations(AnnotationAJ[] annotations) {
        this.realMember.setAnnotations(annotations);
    }

    @Override
    public void addAnnotation(AnnotationAJ annotation) {
        this.realMember.addAnnotation(annotation);
    }

    @Override
    public boolean isBridgeMethod() {
        return this.realMember.isBridgeMethod();
    }

    @Override
    public boolean isVarargsMethod() {
        return this.realMember.isVarargsMethod();
    }

    @Override
    public boolean isSynthetic() {
        return this.realMember.isSynthetic();
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        this.realMember.write(s);
    }

    @Override
    public ISourceContext getSourceContext(World world) {
        return this.realMember.getSourceContext(world);
    }

    @Override
    public String[] getParameterNames() {
        return this.realMember.getParameterNames();
    }

    @Override
    public void setParameterNames(String[] names) {
        this.realMember.setParameterNames(names);
    }

    @Override
    public String[] getParameterNames(World world) {
        return this.realMember.getParameterNames(world);
    }

    @Override
    public AjAttribute.EffectiveSignatureAttribute getEffectiveSignature() {
        return this.realMember.getEffectiveSignature();
    }

    @Override
    public ISourceLocation getSourceLocation() {
        return this.realMember.getSourceLocation();
    }

    @Override
    public int getEnd() {
        return this.realMember.getEnd();
    }

    @Override
    public ISourceContext getSourceContext() {
        return this.realMember.getSourceContext();
    }

    @Override
    public int getStart() {
        return this.realMember.getStart();
    }

    @Override
    public void setPosition(int sourceStart, int sourceEnd) {
        this.realMember.setPosition(sourceStart, sourceEnd);
    }

    @Override
    public void setSourceContext(ISourceContext sourceContext) {
        this.realMember.setSourceContext(sourceContext);
    }

    @Override
    public boolean isAbstract() {
        return this.realMember.isAbstract();
    }

    @Override
    public boolean isPublic() {
        return this.realMember.isPublic();
    }

    @Override
    public boolean isDefault() {
        return this.realMember.isDefault();
    }

    @Override
    public boolean isVisible(ResolvedType fromType) {
        return this.realMember.isVisible(fromType);
    }

    @Override
    public void setCheckedExceptions(UnresolvedType[] checkedExceptions) {
        this.realMember.setCheckedExceptions(checkedExceptions);
    }

    @Override
    public void setAnnotatedElsewhere(boolean b) {
        this.realMember.setAnnotatedElsewhere(b);
    }

    @Override
    public boolean isAnnotatedElsewhere() {
        return this.realMember.isAnnotatedElsewhere();
    }

    @Override
    public UnresolvedType getGenericReturnType() {
        return this.realMember.getGenericReturnType();
    }

    @Override
    public UnresolvedType[] getGenericParameterTypes() {
        return this.realMember.getGenericParameterTypes();
    }

    @Override
    public ResolvedMemberImpl parameterizedWith(UnresolvedType[] typeParameters, ResolvedType newDeclaringType, boolean isParameterized) {
        return this.realMember.parameterizedWith(typeParameters, newDeclaringType, isParameterized);
    }

    @Override
    public ResolvedMemberImpl parameterizedWith(UnresolvedType[] typeParameters, ResolvedType newDeclaringType, boolean isParameterized, List<String> aliases) {
        return this.realMember.parameterizedWith(typeParameters, newDeclaringType, isParameterized, aliases);
    }

    @Override
    public void setTypeVariables(TypeVariable[] types) {
        this.realMember.setTypeVariables(types);
    }

    @Override
    public TypeVariable[] getTypeVariables() {
        return this.realMember.getTypeVariables();
    }

    @Override
    public TypeVariable getTypeVariableNamed(String name) {
        return this.realMember.getTypeVariableNamed(name);
    }

    @Override
    public boolean matches(ResolvedMember aCandidateMatch, boolean ignoreGenerics) {
        return this.realMember.matches(aCandidateMatch, ignoreGenerics);
    }

    @Override
    public ResolvedMember resolve(World world) {
        return this.realMember.resolve(world);
    }

    @Override
    public int compareTo(Member other) {
        return this.realMember.compareTo(other);
    }

    @Override
    public MemberKind getKind() {
        return this.realMember.getKind();
    }

    @Override
    public UnresolvedType getReturnType() {
        return this.realMember.getReturnType();
    }

    @Override
    public UnresolvedType getType() {
        return this.realMember.getType();
    }

    @Override
    public String getName() {
        return this.realMember.getName();
    }

    @Override
    public UnresolvedType[] getParameterTypes() {
        return this.realMember.getParameterTypes();
    }

    @Override
    public AnnotationAJ[][] getParameterAnnotations() {
        return this.realMember.getParameterAnnotations();
    }

    @Override
    public ResolvedType[][] getParameterAnnotationTypes() {
        return this.realMember.getParameterAnnotationTypes();
    }

    @Override
    public String getSignature() {
        return this.realMember.getSignature();
    }

    @Override
    public int getArity() {
        return this.realMember.getArity();
    }

    @Override
    public String getParameterSignature() {
        return this.realMember.getParameterSignature();
    }

    @Override
    public boolean isCompatibleWith(Member am) {
        return this.realMember.isCompatibleWith(am);
    }

    @Override
    public boolean canBeParameterized() {
        return this.realMember.canBeParameterized();
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        return this.realMember.getAnnotations();
    }

    @Override
    public Collection<ResolvedType> getDeclaringTypes(World world) {
        throw new UnsupportedOperationException("Adrian doesn't think you should be calling this...");
    }

    @Override
    public JoinPointSignatureIterator getJoinPointSignatures(World world) {
        return this.realMember.getJoinPointSignatures(world);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.getReturnType().getName());
        buf.append(' ');
        buf.append(this.getDeclaringType().getName());
        buf.append('.');
        buf.append(this.getName());
        if (this.getKind() != FIELD) {
            buf.append("(");
            UnresolvedType[] parameterTypes = this.getParameterTypes();
            if (parameterTypes.length != 0) {
                buf.append(parameterTypes[0]);
                int len = parameterTypes.length;
                for (int i = 1; i < len; ++i) {
                    buf.append(", ");
                    buf.append(parameterTypes[i].getName());
                }
            }
            buf.append(")");
        }
        return buf.toString();
    }

    @Override
    public String toGenericString() {
        return this.realMember.toGenericString();
    }

    @Override
    public String toDebugString() {
        return this.realMember.toDebugString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof JoinPointSignature)) {
            return false;
        }
        JoinPointSignature other = (JoinPointSignature)obj;
        if (!this.realMember.equals(other.realMember)) {
            return false;
        }
        return this.substituteDeclaringType.equals(other.substituteDeclaringType);
    }

    public int hashCode() {
        return 17 + 37 * this.realMember.hashCode() + 37 * this.substituteDeclaringType.hashCode();
    }

    @Override
    public boolean hasBackingGenericMember() {
        return this.realMember.hasBackingGenericMember();
    }

    @Override
    public ResolvedMember getBackingGenericMember() {
        return this.realMember.getBackingGenericMember();
    }

    @Override
    public void evictWeavingState() {
        this.realMember.evictWeavingState();
    }

    public ResolvedMember parameterizedWith(Map m, World w) {
        return this.realMember.parameterizedWith(m, w);
    }

    @Override
    public String getAnnotationDefaultValue() {
        return this.realMember.getAnnotationDefaultValue();
    }

    @Override
    public String getParameterSignatureErased() {
        return this.realMember.getParameterSignatureErased();
    }

    @Override
    public String getSignatureErased() {
        return this.realMember.getSignatureErased();
    }

    @Override
    public boolean isDefaultConstructor() {
        return this.realMember.isDefaultConstructor();
    }

    @Override
    public boolean equalsApartFromDeclaringType(Object other) {
        return this.realMember.equalsApartFromDeclaringType(other);
    }
}

