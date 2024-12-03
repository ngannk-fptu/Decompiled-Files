/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.AnnotatedElement;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExactAnnotationTypePattern;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.WildTypePattern;

public class WildAnnotationTypePattern
extends AnnotationTypePattern {
    private TypePattern typePattern;
    private boolean resolved = false;
    Map<String, String> annotationValues;
    private static final byte VERSION = 1;

    public WildAnnotationTypePattern(TypePattern typePattern) {
        this.typePattern = typePattern;
        this.setLocation(typePattern.getSourceContext(), typePattern.start, typePattern.end);
    }

    public WildAnnotationTypePattern(TypePattern typePattern, Map<String, String> annotationValues) {
        this.typePattern = typePattern;
        this.annotationValues = annotationValues;
        this.setLocation(typePattern.getSourceContext(), typePattern.start, typePattern.end);
    }

    public TypePattern getTypePattern() {
        return this.typePattern;
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated) {
        return this.matches(annotated, null);
    }

    /*
     * Could not resolve type clashes
     */
    protected void resolveAnnotationValues(ResolvedType annotationType, IScope scope) {
        if (this.annotationValues == null) {
            return;
        }
        HashMap<String, String> replacementValues = new HashMap<String, String>();
        Set<String> keys = this.annotationValues.keySet();
        ResolvedMember[] ms = annotationType.getDeclaredMethods();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String k;
            String key = k = iterator.next();
            if (k.endsWith("!")) {
                key = key.substring(0, k.length() - 1);
            }
            String v = this.annotationValues.get(k);
            boolean validKey = false;
            for (int i = 0; i < ms.length; ++i) {
                IMessage m;
                ResolvedType rt;
                IMessage m2;
                ResolvedMember resolvedMember = ms[i];
                if (!resolvedMember.getName().equals(key) || !resolvedMember.isAbstract()) continue;
                validKey = true;
                ResolvedType t = resolvedMember.getReturnType().resolve(scope.getWorld());
                if (t.isEnum()) {
                    int pos = v.lastIndexOf(".");
                    if (pos == -1) {
                        m2 = MessageUtil.error(WeaverMessages.format("invalidAnnotationValue", v, "enum"), this.getSourceLocation());
                        scope.getWorld().getMessageHandler().handleMessage(m2);
                        continue;
                    }
                    String typename = v.substring(0, pos);
                    ResolvedType rt2 = scope.lookupType(typename, this).resolve(scope.getWorld());
                    v = rt2.getSignature() + v.substring(pos + 1);
                    replacementValues.put(k, v);
                    break;
                }
                if (t.isPrimitiveType()) {
                    short value;
                    if (t.getSignature().equals("I")) {
                        try {
                            value = Integer.parseInt(v);
                            replacementValues.put(k, Integer.toString(value));
                            break;
                        }
                        catch (NumberFormatException nfe) {
                            m2 = MessageUtil.error(WeaverMessages.format("invalidAnnotationValue", v, "int"), this.getSourceLocation());
                            scope.getWorld().getMessageHandler().handleMessage(m2);
                            continue;
                        }
                    }
                    if (t.getSignature().equals("F")) {
                        try {
                            float value2 = Float.parseFloat(v);
                            replacementValues.put(k, Float.toString(value2));
                            break;
                        }
                        catch (NumberFormatException nfe) {
                            m2 = MessageUtil.error(WeaverMessages.format("invalidAnnotationValue", v, "float"), this.getSourceLocation());
                            scope.getWorld().getMessageHandler().handleMessage(m2);
                            continue;
                        }
                    }
                    if (t.getSignature().equals("Z")) {
                        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) continue;
                        IMessage m3 = MessageUtil.error(WeaverMessages.format("invalidAnnotationValue", v, "boolean"), this.getSourceLocation());
                        scope.getWorld().getMessageHandler().handleMessage(m3);
                        continue;
                    }
                    if (t.getSignature().equals("S")) {
                        try {
                            value = Short.parseShort(v);
                            replacementValues.put(k, Short.toString(value));
                            break;
                        }
                        catch (NumberFormatException nfe) {
                            m2 = MessageUtil.error(WeaverMessages.format("invalidAnnotationValue", v, "short"), this.getSourceLocation());
                            scope.getWorld().getMessageHandler().handleMessage(m2);
                            continue;
                        }
                    }
                    if (t.getSignature().equals("J")) {
                        try {
                            replacementValues.put(k, Long.toString(Long.parseLong(v)));
                            break;
                        }
                        catch (NumberFormatException nfe) {
                            m2 = MessageUtil.error(WeaverMessages.format("invalidAnnotationValue", v, "long"), this.getSourceLocation());
                            scope.getWorld().getMessageHandler().handleMessage(m2);
                            continue;
                        }
                    }
                    if (t.getSignature().equals("D")) {
                        try {
                            replacementValues.put(k, Double.toString(Double.parseDouble(v)));
                            break;
                        }
                        catch (NumberFormatException nfe) {
                            m2 = MessageUtil.error(WeaverMessages.format("invalidAnnotationValue", v, "double"), this.getSourceLocation());
                            scope.getWorld().getMessageHandler().handleMessage(m2);
                            continue;
                        }
                    }
                    if (t.getSignature().equals("B")) {
                        try {
                            replacementValues.put(k, Byte.toString(Byte.parseByte(v)));
                            break;
                        }
                        catch (NumberFormatException nfe) {
                            m2 = MessageUtil.error(WeaverMessages.format("invalidAnnotationValue", v, "byte"), this.getSourceLocation());
                            scope.getWorld().getMessageHandler().handleMessage(m2);
                            continue;
                        }
                    }
                    if (t.getSignature().equals("C")) {
                        if (v.length() != 3) {
                            IMessage m4 = MessageUtil.error(WeaverMessages.format("invalidAnnotationValue", v, "char"), this.getSourceLocation());
                            scope.getWorld().getMessageHandler().handleMessage(m4);
                            continue;
                        }
                        replacementValues.put(k, v.substring(1, 2));
                        break;
                    }
                    throw new RuntimeException("Not implemented for " + t);
                }
                if (t.equals(ResolvedType.JL_STRING)) continue;
                if (t.equals(ResolvedType.JL_CLASS) || t.isParameterizedOrGenericType() && t.getRawType().equals(ResolvedType.JL_CLASS)) {
                    String typename = v.substring(0, v.lastIndexOf(46));
                    rt = scope.lookupType(typename, this).resolve(scope.getWorld());
                    if (rt.isMissing()) {
                        m = MessageUtil.error("Unable to resolve type '" + v + "' specified for value '" + k + "'", this.getSourceLocation());
                        scope.getWorld().getMessageHandler().handleMessage(m);
                    }
                    replacementValues.put(k, rt.getSignature());
                    break;
                }
                if (t.isAnnotation()) {
                    if (v.indexOf("(") != -1) {
                        throw new RuntimeException("Compiler limitation: annotation values can only currently be marker annotations (no values): " + v);
                    }
                    String typename = v.substring(1);
                    rt = scope.lookupType(typename, this).resolve(scope.getWorld());
                    if (rt.isMissing()) {
                        m = MessageUtil.error("Unable to resolve type '" + v + "' specified for value '" + k + "'", this.getSourceLocation());
                        scope.getWorld().getMessageHandler().handleMessage(m);
                    }
                    replacementValues.put(k, rt.getSignature());
                    break;
                }
                scope.message(MessageUtil.error(WeaverMessages.format("unsupportedAnnotationValueType", t), this.getSourceLocation()));
                replacementValues.put(k, "");
            }
            if (validKey) continue;
            IMessage m = MessageUtil.error(WeaverMessages.format("unknownAnnotationValue", annotationType, k), this.getSourceLocation());
            scope.getWorld().getMessageHandler().handleMessage(m);
        }
        this.annotationValues.putAll(replacementValues);
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated, ResolvedType[] parameterAnnotations) {
        block6: {
            block5: {
                if (!this.resolved) {
                    throw new IllegalStateException("Can't match on an unresolved annotation type pattern");
                }
                if (this.annotationValues != null && !this.typePattern.hasFailedResolution()) {
                    throw new IllegalStateException("Cannot use annotationvalues with a wild annotation pattern");
                }
                if (!this.isForParameterAnnotationMatch()) break block5;
                if (parameterAnnotations == null || parameterAnnotations.length == 0) break block6;
                for (int i = 0; i < parameterAnnotations.length; ++i) {
                    if (!this.typePattern.matches(parameterAnnotations[i], TypePattern.STATIC).alwaysTrue()) continue;
                    return FuzzyBoolean.YES;
                }
                break block6;
            }
            ResolvedType[] annTypes = annotated.getAnnotationTypes();
            if (annTypes != null && annTypes.length != 0) {
                for (int i = 0; i < annTypes.length; ++i) {
                    if (!this.typePattern.matches(annTypes[i], TypePattern.STATIC).alwaysTrue()) continue;
                    return FuzzyBoolean.YES;
                }
            }
        }
        return FuzzyBoolean.NO;
    }

    @Override
    public void resolve(World world) {
        if (!this.resolved) {
            ResolvedType resolvedType;
            WildTypePattern wildTypePattern;
            String fullyQualifiedName;
            if (this.typePattern instanceof WildTypePattern && (this.annotationValues == null || this.annotationValues.isEmpty()) && (fullyQualifiedName = (wildTypePattern = (WildTypePattern)this.typePattern).maybeGetCleanName()) != null && fullyQualifiedName.indexOf(".") != -1 && (resolvedType = world.resolve(UnresolvedType.forName(fullyQualifiedName))) != null && !resolvedType.isMissing()) {
                this.typePattern = new ExactTypePattern(resolvedType, false, false);
            }
            this.resolved = true;
        }
    }

    @Override
    public AnnotationTypePattern resolveBindings(IScope scope, Bindings bindings, boolean allowBinding) {
        if (!scope.getWorld().isInJava5Mode()) {
            scope.message(MessageUtil.error(WeaverMessages.format("annotationsRequireJava5"), this.getSourceLocation()));
            return this;
        }
        if (this.resolved) {
            return this;
        }
        this.typePattern = this.typePattern.resolveBindings(scope, bindings, false, false);
        this.resolved = true;
        if (this.typePattern instanceof ExactTypePattern) {
            ExactTypePattern et = (ExactTypePattern)this.typePattern;
            if (!et.getExactType().resolve(scope.getWorld()).isAnnotation()) {
                IMessage m = MessageUtil.error(WeaverMessages.format("referenceToNonAnnotationType", et.getExactType().getName()), this.getSourceLocation());
                scope.getWorld().getMessageHandler().handleMessage(m);
                this.resolved = false;
            }
            ResolvedType annotationType = et.getExactType().resolve(scope.getWorld());
            this.resolveAnnotationValues(annotationType, scope);
            ExactAnnotationTypePattern eatp = new ExactAnnotationTypePattern(annotationType, this.annotationValues);
            eatp.copyLocationFrom(this);
            if (this.isForParameterAnnotationMatch()) {
                eatp.setForParameterAnnotationMatch();
            }
            return eatp;
        }
        return this;
    }

    @Override
    public AnnotationTypePattern parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        WildAnnotationTypePattern ret = new WildAnnotationTypePattern(this.typePattern.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        ret.resolved = this.resolved;
        return ret;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(8);
        s.writeByte(1);
        this.typePattern.write(s);
        this.writeLocation(s);
        s.writeBoolean(this.isForParameterAnnotationMatch());
        if (this.annotationValues == null) {
            s.writeInt(0);
        } else {
            s.writeInt(this.annotationValues.size());
            Set<String> key = this.annotationValues.keySet();
            for (String k : key) {
                s.writeUTF(k);
                s.writeUTF(this.annotationValues.get(k));
            }
        }
    }

    public static AnnotationTypePattern read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        int annotationValueCount;
        byte version = s.readByte();
        if (version > 1) {
            throw new BCException("ExactAnnotationTypePattern was written by a newer version of AspectJ");
        }
        TypePattern t = TypePattern.read(s, context);
        WildAnnotationTypePattern ret = new WildAnnotationTypePattern(t);
        ret.readLocation(context, s);
        if (s.getMajorVersion() >= 4 && s.readBoolean()) {
            ret.setForParameterAnnotationMatch();
        }
        if (s.getMajorVersion() >= 5 && (annotationValueCount = s.readInt()) > 0) {
            HashMap<String, String> aValues = new HashMap<String, String>();
            for (int i = 0; i < annotationValueCount; ++i) {
                String key = s.readUTF();
                String val = s.readUTF();
                aValues.put(key, val);
            }
            ret.annotationValues = aValues;
        }
        return ret;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WildAnnotationTypePattern)) {
            return false;
        }
        WildAnnotationTypePattern other = (WildAnnotationTypePattern)obj;
        return other.typePattern.equals(this.typePattern) && this.isForParameterAnnotationMatch() == other.isForParameterAnnotationMatch() && (this.annotationValues == null ? other.annotationValues == null : this.annotationValues.equals(other.annotationValues));
    }

    public int hashCode() {
        return ((17 + 37 * this.typePattern.hashCode()) * 37 + (this.isForParameterAnnotationMatch() ? 0 : 1)) * 37 + (this.annotationValues == null ? 0 : this.annotationValues.hashCode());
    }

    public String toString() {
        return "@(" + this.typePattern.toString() + ")";
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

