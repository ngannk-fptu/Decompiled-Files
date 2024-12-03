/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.bridge.IMessage;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.AnnotatedElement;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.BindingAnnotationFieldTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExactAnnotationTypePattern;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;

public class ExactAnnotationFieldTypePattern
extends ExactAnnotationTypePattern {
    UnresolvedType annotationType;
    private ResolvedMember field;

    public ExactAnnotationFieldTypePattern(ExactAnnotationTypePattern p, String formalName) {
        super(formalName);
        this.annotationType = p.annotationType;
        this.copyLocationFrom(p);
    }

    public ExactAnnotationFieldTypePattern(UnresolvedType annotationType, String formalName) {
        super(formalName);
        this.annotationType = annotationType;
    }

    @Override
    public AnnotationTypePattern resolveBindings(IScope scope, Bindings bindings, boolean allowBinding) {
        if (this.resolved) {
            return this;
        }
        this.resolved = true;
        FormalBinding formalBinding = scope.lookupFormal(this.formalName);
        if (formalBinding == null) {
            scope.message(IMessage.ERROR, this, "When using @annotation(<annotationType>(<annotationField>)), <annotationField> must be bound");
            return this;
        }
        this.annotationType = scope.getWorld().resolve(this.annotationType, true);
        if (ResolvedType.isMissing(this.annotationType)) {
            int lastDot;
            String cleanname = this.annotationType.getName();
            UnresolvedType type = null;
            while (ResolvedType.isMissing(type = scope.lookupType(cleanname, this)) && (lastDot = cleanname.lastIndexOf(46)) != -1) {
                cleanname = cleanname.substring(0, lastDot) + "$" + cleanname.substring(lastDot + 1);
            }
            this.annotationType = scope.getWorld().resolve(type, true);
            if (ResolvedType.isMissing(this.annotationType)) {
                return this;
            }
        }
        this.verifyIsAnnotationType((ResolvedType)this.annotationType, scope);
        ResolvedType formalBindingType = formalBinding.getType().resolve(scope.getWorld());
        String bindingTypeSignature = formalBindingType.getSignature();
        if (!(formalBindingType.isEnum() || bindingTypeSignature.equals("Ljava/lang/String;") || bindingTypeSignature.equals("I"))) {
            scope.message(IMessage.ERROR, this, "The field within the annotation must be an enum, string or int. '" + formalBinding.getType() + "' is not (compiler limitation)");
        }
        this.bindingPattern = true;
        ReferenceType theAnnotationType = (ReferenceType)this.annotationType;
        ResolvedMember[] annotationFields = theAnnotationType.getDeclaredMethods();
        this.field = null;
        boolean looksAmbiguous = false;
        for (int i = 0; i < annotationFields.length; ++i) {
            ResolvedMember resolvedMember = annotationFields[i];
            if (!resolvedMember.getReturnType().equals(formalBinding.getType())) continue;
            if (this.field != null) {
                boolean haveProblem = true;
                if (this.field.getName().equals(this.formalName)) {
                    haveProblem = false;
                } else if (resolvedMember.getName().equals(this.formalName)) {
                    this.field = resolvedMember;
                    haveProblem = false;
                }
                if (!haveProblem) continue;
                looksAmbiguous = true;
                continue;
            }
            this.field = resolvedMember;
        }
        if (looksAmbiguous && (this.field == null || !this.field.getName().equals(this.formalName))) {
            scope.message(IMessage.ERROR, this, "The field type '" + formalBinding.getType() + "' is ambiguous for annotation type '" + theAnnotationType.getName() + "'");
        }
        if (this.field == null) {
            scope.message(IMessage.ERROR, this, "No field of type '" + formalBinding.getType() + "' exists on annotation type '" + theAnnotationType.getName() + "'");
        }
        BindingAnnotationFieldTypePattern binding = new BindingAnnotationFieldTypePattern(formalBinding.getType(), formalBinding.getIndex(), theAnnotationType);
        binding.copyLocationFrom(this);
        binding.formalName = this.formalName;
        bindings.register(binding, scope);
        binding.resolveBinding(scope.getWorld());
        return binding;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(9);
        s.writeUTF(this.formalName);
        this.annotationType.write(s);
        this.writeLocation(s);
    }

    public static AnnotationTypePattern read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        String formalName = s.readUTF();
        UnresolvedType annotationType = UnresolvedType.read(s);
        ExactAnnotationFieldTypePattern ret = new ExactAnnotationFieldTypePattern(annotationType, formalName);
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExactAnnotationFieldTypePattern)) {
            return false;
        }
        ExactAnnotationFieldTypePattern other = (ExactAnnotationFieldTypePattern)obj;
        return other.annotationType.equals(this.annotationType) && other.field.equals(this.field) && other.formalName.equals(this.formalName);
    }

    @Override
    public int hashCode() {
        int hashcode = this.annotationType.hashCode();
        hashcode = hashcode * 37 + this.field.hashCode();
        hashcode = hashcode * 37 + this.formalName.hashCode();
        return hashcode;
    }

    @Override
    public FuzzyBoolean fastMatches(AnnotatedElement annotated) {
        throw new BCException("unimplemented");
    }

    @Override
    public UnresolvedType getAnnotationType() {
        throw new BCException("unimplemented");
    }

    public Map getAnnotationValues() {
        throw new BCException("unimplemented");
    }

    @Override
    public ResolvedType getResolvedAnnotationType() {
        throw new BCException("unimplemented");
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated, ResolvedType[] parameterAnnotations) {
        throw new BCException("unimplemented");
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated) {
        throw new BCException("unimplemented");
    }

    @Override
    public FuzzyBoolean matchesRuntimeType(AnnotatedElement annotated) {
        throw new BCException("unimplemented");
    }

    public AnnotationTypePattern parameterizeWith(Map typeVariableMap, World w) {
        throw new BCException("unimplemented");
    }

    @Override
    public void resolve(World world) {
        throw new BCException("unimplemented");
    }

    @Override
    public String toString() {
        if (!this.resolved && this.formalName != null) {
            return this.formalName;
        }
        StringBuffer ret = new StringBuffer();
        ret.append("@").append(this.annotationType.toString());
        ret.append("(").append(this.formalName).append(")");
        return ret.toString();
    }
}

