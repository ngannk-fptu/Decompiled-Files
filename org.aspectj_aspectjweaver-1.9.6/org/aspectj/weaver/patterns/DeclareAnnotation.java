/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AbstractSignaturePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.ISignaturePattern;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.SignaturePattern;
import org.aspectj.weaver.patterns.TypePattern;

public class DeclareAnnotation
extends Declare {
    public static final Kind AT_TYPE = new Kind(1, "type");
    public static final Kind AT_FIELD = new Kind(2, "field");
    public static final Kind AT_METHOD = new Kind(3, "method");
    public static final Kind AT_CONSTRUCTOR = new Kind(4, "constructor");
    public static final Kind AT_REMOVE_FROM_FIELD = new Kind(5, "removeFromField");
    private Kind kind;
    private TypePattern typePattern;
    private ISignaturePattern signaturePattern;
    private ResolvedType containingAspect;
    private List<String> annotationMethods;
    private List<String> annotationStrings;
    private AnnotationAJ annotation;
    private ResolvedType annotationType;
    private int annotationStart;
    private int annotationEnd;
    boolean isRemover = false;

    public DeclareAnnotation(Kind kind, TypePattern typePattern) {
        this.typePattern = typePattern;
        this.kind = kind;
        this.init();
    }

    public DeclareAnnotation(Kind kind, ISignaturePattern sigPattern) {
        this.signaturePattern = sigPattern;
        this.kind = kind;
        this.init();
    }

    private void init() {
        this.annotationMethods = new ArrayList<String>();
        this.annotationMethods.add("unknown");
        this.annotationStrings = new ArrayList<String>();
        this.annotationStrings.add("@<annotation>");
    }

    public String getAnnotationString() {
        return this.annotationStrings.get(0);
    }

    public boolean isExactPattern() {
        return this.typePattern instanceof ExactTypePattern;
    }

    public String getAnnotationMethod() {
        return this.annotationMethods.get(0);
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("declare @");
        ret.append(this.kind);
        ret.append(" : ");
        ret.append(this.typePattern != null ? this.typePattern.toString() : this.signaturePattern.toString());
        ret.append(" : ");
        ret.append(this.annotationStrings.get(0));
        return ret.toString();
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public void resolve(IScope scope) {
        if (!scope.getWorld().isInJava5Mode()) {
            String msg = null;
            if (this.kind == AT_TYPE) {
                msg = "declareAtTypeNeedsJava5";
            } else if (this.kind == AT_METHOD) {
                msg = "declareAtMethodNeedsJava5";
            } else if (this.kind == AT_FIELD) {
                msg = "declareAtFieldNeedsJava5";
            } else if (this.kind == AT_CONSTRUCTOR) {
                msg = "declareAtConsNeedsJava5";
            }
            scope.message(MessageUtil.error(WeaverMessages.format(msg), this.getSourceLocation()));
            return;
        }
        if (this.typePattern != null) {
            this.typePattern = this.typePattern.resolveBindings(scope, Bindings.NONE, false, false);
        }
        if (this.signaturePattern != null) {
            this.signaturePattern = this.signaturePattern.resolveBindings(scope, Bindings.NONE);
        }
        this.containingAspect = scope.getEnclosingType();
    }

    @Override
    public Declare parameterizeWith(Map<String, UnresolvedType> typeVariableBindingMap, World w) {
        DeclareAnnotation ret = this.kind == AT_TYPE ? new DeclareAnnotation(this.kind, this.typePattern.parameterizeWith(typeVariableBindingMap, w)) : new DeclareAnnotation(this.kind, this.signaturePattern.parameterizeWith(typeVariableBindingMap, w));
        ret.annotationMethods = this.annotationMethods;
        ret.annotationStrings = this.annotationStrings;
        ret.annotation = this.annotation;
        ret.containingAspect = this.containingAspect;
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public boolean isAdviceLike() {
        return false;
    }

    public void setAnnotationString(String annotationString) {
        this.annotationStrings.set(0, annotationString);
    }

    public void setAnnotationLocation(int start, int end) {
        this.annotationStart = start;
        this.annotationEnd = end;
    }

    public int getAnnotationSourceStart() {
        return this.annotationStart;
    }

    public int getAnnotationSourceEnd() {
        return this.annotationEnd;
    }

    public void setAnnotationMethod(String methodName) {
        this.annotationMethods.set(0, methodName);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DeclareAnnotation)) {
            return false;
        }
        DeclareAnnotation other = (DeclareAnnotation)obj;
        if (!this.kind.equals(other.kind)) {
            return false;
        }
        if (!this.annotationStrings.get(0).equals(other.annotationStrings.get(0))) {
            return false;
        }
        if (!this.annotationMethods.get(0).equals(other.annotationMethods.get(0))) {
            return false;
        }
        if (this.typePattern != null && !this.typePattern.equals(other.typePattern)) {
            return false;
        }
        return this.signaturePattern == null || this.signaturePattern.equals(other.signaturePattern);
    }

    public int hashCode() {
        int result = 19;
        result = 37 * result + this.kind.hashCode();
        result = 37 * result + this.annotationStrings.get(0).hashCode();
        result = 37 * result + this.annotationMethods.get(0).hashCode();
        if (this.typePattern != null) {
            result = 37 * result + this.typePattern.hashCode();
        }
        if (this.signaturePattern != null) {
            result = 37 * result + this.signaturePattern.hashCode();
        }
        return result;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        int i;
        s.writeByte(5);
        if (this.kind.id == AT_FIELD.id && this.isRemover) {
            s.writeInt(AT_REMOVE_FROM_FIELD.id);
        } else {
            s.writeInt(this.kind.id);
        }
        int max = 0;
        max = this.annotationStrings.size();
        s.writeByte(max);
        for (i = 0; i < max; ++i) {
            s.writeUTF(this.annotationStrings.get(i));
        }
        max = this.annotationMethods.size();
        s.writeByte(max);
        for (i = 0; i < max; ++i) {
            s.writeUTF(this.annotationMethods.get(i));
        }
        if (this.typePattern != null) {
            this.typePattern.write(s);
        }
        if (this.signaturePattern != null) {
            AbstractSignaturePattern.writeCompoundSignaturePattern(s, this.signaturePattern);
        }
        this.writeLocation(s);
    }

    public static Declare read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        DeclareAnnotation ret = null;
        boolean isRemover = false;
        int kind = s.readInt();
        if (kind == AT_REMOVE_FROM_FIELD.id) {
            kind = AT_FIELD.id;
            isRemover = true;
        }
        if (s.getMajorVersion() >= 7) {
            s.readByte();
        }
        String annotationString = s.readUTF();
        if (s.getMajorVersion() >= 7) {
            s.readByte();
        }
        String annotationMethod = s.readUTF();
        TypePattern tp = null;
        SignaturePattern sp = null;
        switch (kind) {
            case 1: {
                tp = TypePattern.read(s, context);
                ret = new DeclareAnnotation(AT_TYPE, tp);
                break;
            }
            case 2: {
                if (s.getMajorVersion() >= 7) {
                    ret = new DeclareAnnotation(AT_FIELD, AbstractSignaturePattern.readCompoundSignaturePattern(s, context));
                } else {
                    sp = SignaturePattern.read(s, context);
                    ret = new DeclareAnnotation(AT_FIELD, sp);
                }
                if (!isRemover) break;
                ret.setRemover(true);
                break;
            }
            case 3: {
                if (s.getMajorVersion() >= 7) {
                    ret = new DeclareAnnotation(AT_METHOD, AbstractSignaturePattern.readCompoundSignaturePattern(s, context));
                    break;
                }
                sp = SignaturePattern.read(s, context);
                ret = new DeclareAnnotation(AT_METHOD, sp);
                break;
            }
            case 4: {
                if (s.getMajorVersion() >= 7) {
                    ret = new DeclareAnnotation(AT_CONSTRUCTOR, AbstractSignaturePattern.readCompoundSignaturePattern(s, context));
                    break;
                }
                sp = SignaturePattern.read(s, context);
                ret = new DeclareAnnotation(AT_CONSTRUCTOR, sp);
            }
        }
        ret.setAnnotationString(annotationString);
        ret.setAnnotationMethod(annotationMethod);
        ret.readLocation(context, s);
        return ret;
    }

    public boolean matches(ResolvedMember resolvedmember, World world) {
        if ((this.kind == AT_METHOD || this.kind == AT_CONSTRUCTOR) && resolvedmember != null && resolvedmember.getName().charAt(0) == '<' && this.kind == AT_METHOD) {
            return false;
        }
        return this.signaturePattern.matches(resolvedmember, world, false);
    }

    public boolean matches(ResolvedType type) {
        if (!this.typePattern.matchesStatically(type)) {
            return false;
        }
        if (type.getWorld().getLint().typeNotExposedToWeaver.isEnabled() && !type.isExposedToWeaver()) {
            type.getWorld().getLint().typeNotExposedToWeaver.signal(type.getName(), this.getSourceLocation());
        }
        return true;
    }

    public void setAspect(ResolvedType typeX) {
        this.containingAspect = typeX;
    }

    public UnresolvedType getAspect() {
        return this.containingAspect;
    }

    public void copyAnnotationTo(ResolvedType onType) {
        this.ensureAnnotationDiscovered();
        if (!onType.hasAnnotation(this.annotation.getType())) {
            onType.addAnnotation(this.annotation);
        }
    }

    public AnnotationAJ getAnnotation() {
        this.ensureAnnotationDiscovered();
        return this.annotation;
    }

    private void ensureAnnotationDiscovered() {
        if (this.annotation != null) {
            return;
        }
        String annotationMethod = this.annotationMethods.get(0);
        Iterator<ResolvedMember> iter = this.containingAspect.getMethods(true, true);
        while (iter.hasNext()) {
            ResolvedMember member = iter.next();
            if (!member.getName().equals(annotationMethod)) continue;
            AnnotationAJ[] annos = member.getAnnotations();
            if (annos == null) {
                return;
            }
            int idx = 0;
            if (annos.length > 0 && annos[0].getType().getSignature().equals("Lorg/aspectj/internal/lang/annotation/ajcDeclareAnnotation;")) {
                idx = 1;
            }
            this.annotation = annos[idx];
            break;
        }
    }

    public TypePattern getTypePattern() {
        return this.typePattern;
    }

    public ISignaturePattern getSignaturePattern() {
        return this.signaturePattern;
    }

    public boolean isStarredAnnotationPattern() {
        if (this.typePattern != null) {
            return this.typePattern.isStarAnnotation();
        }
        return this.signaturePattern.isStarAnnotation();
    }

    public Kind getKind() {
        return this.kind;
    }

    public boolean isDeclareAtConstuctor() {
        return this.kind.equals(AT_CONSTRUCTOR);
    }

    public boolean isDeclareAtMethod() {
        return this.kind.equals(AT_METHOD);
    }

    public boolean isDeclareAtType() {
        return this.kind.equals(AT_TYPE);
    }

    public boolean isDeclareAtField() {
        return this.kind.equals(AT_FIELD);
    }

    public ResolvedType getAnnotationType() {
        if (this.annotationType == null) {
            String annotationMethod = this.annotationMethods.get(0);
            Iterator<ResolvedMember> iter = this.containingAspect.getMethods(true, true);
            while (iter.hasNext()) {
                ResolvedMember member = iter.next();
                if (!member.getName().equals(annotationMethod)) continue;
                ResolvedType[] annoTypes = member.getAnnotationTypes();
                if (annoTypes == null) {
                    return null;
                }
                int idx = 0;
                if (annoTypes[0].getSignature().equals("Lorg/aspectj/internal/lang/annotation/ajcDeclareAnnotation;")) {
                    idx = 1;
                }
                this.annotationType = annoTypes[idx];
                break;
            }
        }
        return this.annotationType;
    }

    public boolean isAnnotationAllowedOnField() {
        this.ensureAnnotationDiscovered();
        return this.annotation.allowedOnField();
    }

    public String getPatternAsString() {
        if (this.signaturePattern != null) {
            return this.signaturePattern.toString();
        }
        if (this.typePattern != null) {
            return this.typePattern.toString();
        }
        return "DONT KNOW";
    }

    public boolean couldEverMatch(ResolvedType type) {
        if (this.signaturePattern != null) {
            return this.signaturePattern.couldEverMatch(type);
        }
        return true;
    }

    @Override
    public String getNameSuffix() {
        return this.getKind().toString();
    }

    public void setRemover(boolean b) {
        this.isRemover = b;
    }

    public boolean isRemover() {
        return this.isRemover;
    }

    public static class Kind {
        private final int id;
        private String s;

        private Kind(int n, String name) {
            this.id = n;
            this.s = name;
        }

        public int hashCode() {
            return 19 + 37 * this.id;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Kind)) {
                return false;
            }
            Kind other = (Kind)obj;
            return other.id == this.id;
        }

        public String toString() {
            return "at_" + this.s;
        }
    }
}

