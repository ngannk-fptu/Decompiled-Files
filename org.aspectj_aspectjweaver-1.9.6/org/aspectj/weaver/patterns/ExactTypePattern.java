/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariableReference;
import org.aspectj.weaver.TypeVariableReferenceType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.BindingTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.TypePatternList;
import org.aspectj.weaver.patterns.WildTypePattern;

public class ExactTypePattern
extends TypePattern {
    protected UnresolvedType type;
    protected transient ResolvedType resolvedType;
    public boolean checked = false;
    public boolean isVoid = false;
    public static final Map<String, Class<?>> primitiveTypesMap = new HashMap();
    public static final Map<String, Class<?>> boxedPrimitivesMap;
    private static final Map<String, Class<?>> boxedTypesMap;
    private static final byte EXACT_VERSION = 1;

    @Override
    protected boolean matchesSubtypes(ResolvedType type) {
        boolean match = super.matchesSubtypes(type);
        if (match) {
            return match;
        }
        if (type.isArray() && this.type.isArray()) {
            ResolvedType componentType = type.getComponentType().resolve(type.getWorld());
            UnresolvedType newPatternType = this.type.getComponentType();
            ExactTypePattern etp = new ExactTypePattern(newPatternType, this.includeSubtypes, false);
            return etp.matchesSubtypes(componentType, type);
        }
        return match;
    }

    public ExactTypePattern(UnresolvedType type, boolean includeSubtypes, boolean isVarArgs) {
        super(includeSubtypes, isVarArgs);
        this.type = type;
    }

    @Override
    public boolean isArray() {
        return this.type.isArray();
    }

    @Override
    protected boolean couldEverMatchSameTypesAs(TypePattern other) {
        WildTypePattern owtp;
        String yourSimpleNamePrefix;
        if (super.couldEverMatchSameTypesAs(other)) {
            return true;
        }
        UnresolvedType otherType = other.getExactType();
        if (!ResolvedType.isMissing(otherType)) {
            return this.type.equals(otherType);
        }
        if (other instanceof WildTypePattern && (yourSimpleNamePrefix = (owtp = (WildTypePattern)other).getNamePatterns()[0].maybeGetSimpleName()) != null) {
            return this.type.getName().startsWith(yourSimpleNamePrefix);
        }
        return true;
    }

    @Override
    protected boolean matchesExactly(ResolvedType matchType) {
        boolean typeMatch = this.type.equals(matchType);
        if (!typeMatch && (matchType.isParameterizedType() || matchType.isGenericType())) {
            typeMatch = this.type.equals(matchType.getRawType());
        }
        if (!typeMatch && matchType.isTypeVariableReference()) {
            typeMatch = this.matchesTypeVariable((TypeVariableReferenceType)matchType);
        }
        if (!typeMatch) {
            return false;
        }
        this.annotationPattern.resolve(matchType.getWorld());
        boolean annMatch = false;
        annMatch = matchType.temporaryAnnotationTypes != null ? this.annotationPattern.matches(matchType, matchType.temporaryAnnotationTypes).alwaysTrue() : this.annotationPattern.matches(matchType).alwaysTrue();
        return typeMatch && annMatch;
    }

    private boolean matchesTypeVariable(TypeVariableReferenceType matchType) {
        return this.type.equals(matchType.getTypeVariable().getFirstBound());
    }

    @Override
    protected boolean matchesExactly(ResolvedType matchType, ResolvedType annotatedType) {
        boolean typeMatch = this.type.equals(matchType);
        if (!typeMatch && (matchType.isParameterizedType() || matchType.isGenericType())) {
            typeMatch = this.type.equals(matchType.getRawType());
        }
        if (!typeMatch && matchType.isTypeVariableReference()) {
            typeMatch = this.matchesTypeVariable((TypeVariableReferenceType)matchType);
        }
        this.annotationPattern.resolve(matchType.getWorld());
        boolean annMatch = false;
        annMatch = annotatedType.temporaryAnnotationTypes != null ? this.annotationPattern.matches(annotatedType, annotatedType.temporaryAnnotationTypes).alwaysTrue() : this.annotationPattern.matches(annotatedType).alwaysTrue();
        return typeMatch && annMatch;
    }

    public UnresolvedType getType() {
        return this.type;
    }

    public ResolvedType getResolvedExactType(World world) {
        if (this.resolvedType == null) {
            this.resolvedType = world.resolve(this.type);
        }
        return this.resolvedType;
    }

    @Override
    public boolean isVoid() {
        if (!this.checked) {
            this.isVoid = this.type.getSignature().equals("V");
            this.checked = true;
        }
        return this.isVoid;
    }

    @Override
    public FuzzyBoolean matchesInstanceof(ResolvedType matchType) {
        this.annotationPattern.resolve(matchType.getWorld());
        if (this.type.equals(ResolvedType.OBJECT)) {
            return FuzzyBoolean.YES.and(this.annotationPattern.matches(matchType));
        }
        ResolvedType resolvedType = this.type.resolve(matchType.getWorld());
        if (resolvedType.isAssignableFrom(matchType)) {
            return FuzzyBoolean.YES.and(this.annotationPattern.matches(matchType));
        }
        if (this.type.isPrimitiveType()) {
            return FuzzyBoolean.NO;
        }
        return matchType.isCoerceableFrom(this.type.resolve(matchType.getWorld())) ? FuzzyBoolean.MAYBE : FuzzyBoolean.NO;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ExactTypePattern)) {
            return false;
        }
        if (other instanceof BindingTypePattern) {
            return false;
        }
        ExactTypePattern o = (ExactTypePattern)other;
        if (this.includeSubtypes != o.includeSubtypes) {
            return false;
        }
        if (this.isVarArgs != o.isVarArgs) {
            return false;
        }
        if (!this.typeParameters.equals(o.typeParameters)) {
            return false;
        }
        return o.type.equals(this.type) && o.annotationPattern.equals(this.annotationPattern);
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.type.hashCode();
        result = 37 * result + new Boolean(this.includeSubtypes).hashCode();
        result = 37 * result + new Boolean(this.isVarArgs).hashCode();
        result = 37 * result + this.typeParameters.hashCode();
        result = 37 * result + this.annotationPattern.hashCode();
        return result;
    }

    @Override
    public void write(CompressingDataOutputStream out) throws IOException {
        out.writeByte(2);
        out.writeByte(1);
        out.writeCompressedSignature(this.type.getSignature());
        out.writeBoolean(this.includeSubtypes);
        out.writeBoolean(this.isVarArgs);
        this.annotationPattern.write(out);
        this.typeParameters.write(out);
        this.writeLocation(out);
    }

    public static TypePattern read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        if (s.getMajorVersion() >= 2) {
            return ExactTypePattern.readTypePattern150(s, context);
        }
        return ExactTypePattern.readTypePatternOldStyle(s, context);
    }

    public static TypePattern readTypePattern150(VersionedDataInputStream s, ISourceContext context) throws IOException {
        byte version = s.readByte();
        if (version > 1) {
            throw new BCException("ExactTypePattern was written by a more recent version of AspectJ");
        }
        ExactTypePattern ret = new ExactTypePattern(s.isAtLeast169() ? s.readSignatureAsUnresolvedType() : UnresolvedType.read(s), s.readBoolean(), s.readBoolean());
        ret.setAnnotationTypePattern(AnnotationTypePattern.read(s, context));
        ret.setTypeParameters(TypePatternList.read(s, context));
        ret.readLocation(context, s);
        return ret;
    }

    public static TypePattern readTypePatternOldStyle(DataInputStream s, ISourceContext context) throws IOException {
        ExactTypePattern ret = new ExactTypePattern(UnresolvedType.read(s), s.readBoolean(), false);
        ret.readLocation(context, s);
        return ret;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();
        if (this.annotationPattern != AnnotationTypePattern.ANY) {
            buff.append('(');
            buff.append(this.annotationPattern.toString());
            buff.append(' ');
        }
        String typeString = this.type.toString();
        if (this.isVarArgs) {
            typeString = typeString.substring(0, typeString.lastIndexOf(91));
        }
        buff.append(typeString);
        if (this.includeSubtypes) {
            buff.append('+');
        }
        if (this.isVarArgs) {
            buff.append("...");
        }
        if (this.annotationPattern != AnnotationTypePattern.ANY) {
            buff.append(')');
        }
        return buff.toString();
    }

    @Override
    public TypePattern resolveBindings(IScope scope, Bindings bindings, boolean allowBinding, boolean requireExactType) {
        throw new BCException("trying to re-resolve");
    }

    @Override
    public TypePattern parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        UnresolvedType newType = this.type;
        if (this.type.isTypeVariableReference()) {
            TypeVariableReference t = (TypeVariableReference)((Object)this.type);
            String key = t.getTypeVariable().getName();
            if (typeVariableMap.containsKey(key)) {
                newType = typeVariableMap.get(key);
            }
        } else if (this.type.isParameterizedType()) {
            newType = w.resolve(this.type).parameterize(typeVariableMap);
        }
        ExactTypePattern ret = new ExactTypePattern(newType, this.includeSubtypes, this.isVarArgs);
        ret.annotationPattern = this.annotationPattern.parameterizeWith(typeVariableMap, w);
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    static {
        primitiveTypesMap.put("int", Integer.TYPE);
        primitiveTypesMap.put("short", Short.TYPE);
        primitiveTypesMap.put("long", Long.TYPE);
        primitiveTypesMap.put("byte", Byte.TYPE);
        primitiveTypesMap.put("char", Character.TYPE);
        primitiveTypesMap.put("float", Float.TYPE);
        primitiveTypesMap.put("double", Double.TYPE);
        boxedPrimitivesMap = new HashMap();
        boxedPrimitivesMap.put("java.lang.Integer", Integer.class);
        boxedPrimitivesMap.put("java.lang.Short", Short.class);
        boxedPrimitivesMap.put("java.lang.Long", Long.class);
        boxedPrimitivesMap.put("java.lang.Byte", Byte.class);
        boxedPrimitivesMap.put("java.lang.Character", Character.class);
        boxedPrimitivesMap.put("java.lang.Float", Float.class);
        boxedPrimitivesMap.put("java.lang.Double", Double.class);
        boxedTypesMap = new HashMap();
        boxedTypesMap.put("int", Integer.class);
        boxedTypesMap.put("short", Short.class);
        boxedTypesMap.put("long", Long.class);
        boxedTypesMap.put("byte", Byte.class);
        boxedTypesMap.put("char", Character.class);
        boxedTypesMap.put("float", Float.class);
        boxedTypesMap.put("double", Double.class);
    }
}

