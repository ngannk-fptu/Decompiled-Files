/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.AnnotatedElement;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariableReference;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.BindingAnnotationTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;

public class ExactAnnotationTypePattern
extends AnnotationTypePattern {
    protected UnresolvedType annotationType;
    protected String formalName;
    protected boolean resolved = false;
    protected boolean bindingPattern = false;
    private Map<String, String> annotationValues;
    private static byte VERSION = 1;

    public ExactAnnotationTypePattern(UnresolvedType annotationType, Map<String, String> annotationValues) {
        this.annotationType = annotationType;
        this.annotationValues = annotationValues;
        this.resolved = annotationType instanceof ResolvedType;
    }

    private ExactAnnotationTypePattern(UnresolvedType annotationType) {
        this.annotationType = annotationType;
        this.resolved = annotationType instanceof ResolvedType;
    }

    protected ExactAnnotationTypePattern(String formalName) {
        this.formalName = formalName;
        this.resolved = false;
        this.bindingPattern = true;
    }

    public ResolvedType getResolvedAnnotationType() {
        if (!this.resolved) {
            throw new IllegalStateException("I need to be resolved first!");
        }
        return (ResolvedType)this.annotationType;
    }

    public UnresolvedType getAnnotationType() {
        return this.annotationType;
    }

    public Map<String, String> getAnnotationValues() {
        return this.annotationValues;
    }

    @Override
    public FuzzyBoolean fastMatches(AnnotatedElement annotated) {
        if (annotated.hasAnnotation(this.annotationType) && this.annotationValues == null) {
            return FuzzyBoolean.YES;
        }
        return FuzzyBoolean.MAYBE;
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated) {
        return this.matches(annotated, null);
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated, ResolvedType[] parameterAnnotations) {
        block17: {
            block16: {
                if (this.isForParameterAnnotationMatch()) break block16;
                boolean checkSupers = false;
                if (this.getResolvedAnnotationType().isInheritedAnnotation() && annotated instanceof ResolvedType) {
                    checkSupers = true;
                }
                if (annotated.hasAnnotation(this.annotationType)) {
                    ReferenceType rt;
                    if (this.annotationType instanceof ReferenceType && (rt = (ReferenceType)this.annotationType).getRetentionPolicy() != null && rt.getRetentionPolicy().equals("SOURCE")) {
                        rt.getWorld().getMessageHandler().handleMessage(MessageUtil.warn(WeaverMessages.format("noMatchBecauseSourceRetention", this.annotationType, annotated), this.getSourceLocation()));
                        return FuzzyBoolean.NO;
                    }
                    if (this.annotationValues != null) {
                        AnnotationAJ theAnnotation = annotated.getAnnotationOfType(this.annotationType);
                        Set<String> keys = this.annotationValues.keySet();
                        for (String k : keys) {
                            boolean notEqual = false;
                            String v = this.annotationValues.get(k);
                            if (k.endsWith("!")) {
                                notEqual = true;
                                k = k.substring(0, k.length() - 1);
                            }
                            if (theAnnotation.hasNamedValue(k)) {
                                if (!(notEqual ? theAnnotation.hasNameValuePair(k, v) : !theAnnotation.hasNameValuePair(k, v))) continue;
                                return FuzzyBoolean.NO;
                            }
                            ResolvedMember[] ms = ((ResolvedType)this.annotationType).getDeclaredMethods();
                            boolean foundMatch = false;
                            for (int i = 0; i < ms.length && !foundMatch; ++i) {
                                String s;
                                if (!ms[i].isAbstract() || ms[i].getParameterTypes().length != 0 || !ms[i].getName().equals(k) || (s = ms[i].getAnnotationDefaultValue()) == null || !s.equals(v)) continue;
                                foundMatch = true;
                            }
                            if (!(notEqual ? foundMatch : !foundMatch)) continue;
                            return FuzzyBoolean.NO;
                        }
                    }
                    return FuzzyBoolean.YES;
                }
                if (!checkSupers) break block17;
                for (ResolvedType toMatchAgainst = ((ResolvedType)annotated).getSuperclass(); toMatchAgainst != null; toMatchAgainst = toMatchAgainst.getSuperclass()) {
                    if (!toMatchAgainst.hasAnnotation(this.annotationType)) continue;
                    if (this.annotationValues != null) {
                        AnnotationAJ theAnnotation = toMatchAgainst.getAnnotationOfType(this.annotationType);
                        Set<String> keys = this.annotationValues.keySet();
                        for (String k : keys) {
                            String v = this.annotationValues.get(k);
                            if (theAnnotation.hasNamedValue(k)) {
                                if (theAnnotation.hasNameValuePair(k, v)) continue;
                                return FuzzyBoolean.NO;
                            }
                            ResolvedMember[] ms = ((ResolvedType)this.annotationType).getDeclaredMethods();
                            boolean foundMatch = false;
                            for (int i = 0; i < ms.length && !foundMatch; ++i) {
                                String s;
                                if (!ms[i].isAbstract() || ms[i].getParameterTypes().length != 0 || !ms[i].getName().equals(k) || (s = ms[i].getAnnotationDefaultValue()) == null || !s.equals(v)) continue;
                                foundMatch = true;
                            }
                            if (foundMatch) continue;
                            return FuzzyBoolean.NO;
                        }
                    }
                    return FuzzyBoolean.YES;
                }
                break block17;
            }
            if (parameterAnnotations == null) {
                return FuzzyBoolean.NO;
            }
            for (int i = 0; i < parameterAnnotations.length; ++i) {
                if (!this.annotationType.equals(parameterAnnotations[i])) continue;
                if (this.annotationValues != null) {
                    parameterAnnotations[i].getWorld().getMessageHandler().handleMessage(MessageUtil.error("Compiler limitation: annotation value matching for parameter annotations not yet supported"));
                    return FuzzyBoolean.NO;
                }
                return FuzzyBoolean.YES;
            }
        }
        return FuzzyBoolean.NO;
    }

    public FuzzyBoolean matchesRuntimeType(AnnotatedElement annotated) {
        if (this.getResolvedAnnotationType().isInheritedAnnotation() && this.matches(annotated).alwaysTrue()) {
            return FuzzyBoolean.YES;
        }
        return FuzzyBoolean.MAYBE;
    }

    @Override
    public void resolve(World world) {
        if (!this.resolved) {
            this.annotationType = this.annotationType.resolve(world);
            this.resolved = true;
        }
    }

    @Override
    public AnnotationTypePattern resolveBindings(IScope scope, Bindings bindings, boolean allowBinding) {
        FormalBinding formalBinding;
        if (this.resolved) {
            return this;
        }
        this.resolved = true;
        String simpleName = this.maybeGetSimpleName();
        if (simpleName != null && (formalBinding = scope.lookupFormal(simpleName)) != null) {
            if (bindings == null) {
                scope.message(IMessage.ERROR, this, "negation doesn't allow binding");
                return this;
            }
            if (!allowBinding) {
                scope.message(IMessage.ERROR, this, "name binding only allowed in @pcds, args, this, and target");
                return this;
            }
            this.formalName = simpleName;
            this.bindingPattern = true;
            this.verifyIsAnnotationType(formalBinding.getType().resolve(scope.getWorld()), scope);
            BindingAnnotationTypePattern binding = new BindingAnnotationTypePattern(formalBinding);
            binding.copyLocationFrom(this);
            bindings.register(binding, scope);
            binding.resolveBinding(scope.getWorld());
            if (this.isForParameterAnnotationMatch()) {
                binding.setForParameterAnnotationMatch();
            }
            return binding;
        }
        String cleanname = this.annotationType.getName();
        this.annotationType = scope.getWorld().resolve(this.annotationType, true);
        if (ResolvedType.isMissing(this.annotationType)) {
            int lastDot;
            UnresolvedType type = null;
            while (ResolvedType.isMissing(type = scope.lookupType(cleanname, this)) && (lastDot = cleanname.lastIndexOf(46)) != -1) {
                cleanname = cleanname.substring(0, lastDot) + "$" + cleanname.substring(lastDot + 1);
            }
            this.annotationType = scope.getWorld().resolve(type, true);
        }
        this.verifyIsAnnotationType((ResolvedType)this.annotationType, scope);
        return this;
    }

    @Override
    public AnnotationTypePattern parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        UnresolvedType newAnnotationType = this.annotationType;
        if (this.annotationType.isTypeVariableReference()) {
            TypeVariableReference t = (TypeVariableReference)((Object)this.annotationType);
            String key = t.getTypeVariable().getName();
            if (typeVariableMap.containsKey(key)) {
                newAnnotationType = typeVariableMap.get(key);
            }
        } else if (this.annotationType.isParameterizedType()) {
            newAnnotationType = this.annotationType.parameterize(typeVariableMap);
        }
        ExactAnnotationTypePattern ret = new ExactAnnotationTypePattern(newAnnotationType, this.annotationValues);
        ret.formalName = this.formalName;
        ret.bindingPattern = this.bindingPattern;
        ret.copyLocationFrom(this);
        if (this.isForParameterAnnotationMatch()) {
            ret.setForParameterAnnotationMatch();
        }
        return ret;
    }

    protected String maybeGetSimpleName() {
        if (this.formalName != null) {
            return this.formalName;
        }
        String ret = this.annotationType.getName();
        return ret.indexOf(46) == -1 ? ret : null;
    }

    protected void verifyIsAnnotationType(ResolvedType type, IScope scope) {
        if (!type.isAnnotation()) {
            IMessage m = MessageUtil.error(WeaverMessages.format("referenceToNonAnnotationType", type.getName()), this.getSourceLocation());
            scope.getWorld().getMessageHandler().handleMessage(m);
            this.resolved = false;
        }
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(1);
        s.writeByte(VERSION);
        s.writeBoolean(this.bindingPattern);
        if (this.bindingPattern) {
            s.writeUTF(this.formalName);
        } else {
            this.annotationType.write(s);
        }
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
        if (version > VERSION) {
            throw new BCException("ExactAnnotationTypePattern was written by a newer version of AspectJ");
        }
        boolean isBindingPattern = s.readBoolean();
        ExactAnnotationTypePattern ret = isBindingPattern ? new ExactAnnotationTypePattern(s.readUTF()) : new ExactAnnotationTypePattern(UnresolvedType.read(s));
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
        if (!(obj instanceof ExactAnnotationTypePattern)) {
            return false;
        }
        ExactAnnotationTypePattern other = (ExactAnnotationTypePattern)obj;
        return other.annotationType.equals(this.annotationType) && this.isForParameterAnnotationMatch() == other.isForParameterAnnotationMatch() && (this.annotationValues == null ? other.annotationValues == null : this.annotationValues.equals(other.annotationValues));
    }

    public int hashCode() {
        return (this.annotationType.hashCode() * 37 + (this.isForParameterAnnotationMatch() ? 0 : 1)) * 37 + (this.annotationValues == null ? 0 : this.annotationValues.hashCode());
    }

    public String toString() {
        if (!this.resolved && this.formalName != null) {
            return this.formalName;
        }
        String ret = "@" + this.annotationType.toString();
        if (this.formalName != null) {
            ret = ret + " " + this.formalName;
        }
        return ret;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

