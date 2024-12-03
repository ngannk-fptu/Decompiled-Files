/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.AnnotatedElement;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.BindingPattern;
import org.aspectj.weaver.patterns.ExactAnnotationTypePattern;

public class BindingAnnotationFieldTypePattern
extends ExactAnnotationTypePattern
implements BindingPattern {
    protected int formalIndex;
    UnresolvedType formalType;

    public BindingAnnotationFieldTypePattern(UnresolvedType formalType, int formalIndex, UnresolvedType theAnnotationType) {
        super(theAnnotationType, null);
        this.formalIndex = formalIndex;
        this.formalType = formalType;
    }

    public void resolveBinding(World world) {
        if (this.resolved) {
            return;
        }
        this.resolved = true;
        this.formalType = world.resolve(this.formalType);
        this.annotationType = world.resolve(this.annotationType);
        ResolvedType annoType = (ResolvedType)this.annotationType;
        if (!annoType.isAnnotation()) {
            IMessage m = MessageUtil.error(WeaverMessages.format("referenceToNonAnnotationType", annoType.getName()), this.getSourceLocation());
            world.getMessageHandler().handleMessage(m);
            this.resolved = false;
        }
    }

    public AnnotationTypePattern parameterizeWith(Map typeVariableMap, World w) {
        throw new BCException("Parameterization not implemented for annotation field binding construct (compiler limitation)");
    }

    @Override
    public int getFormalIndex() {
        return this.formalIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BindingAnnotationFieldTypePattern)) {
            return false;
        }
        BindingAnnotationFieldTypePattern btp = (BindingAnnotationFieldTypePattern)obj;
        return btp.formalIndex == this.formalIndex && this.annotationType.equals(btp.annotationType) && this.formalType.equals(btp.formalType);
    }

    @Override
    public int hashCode() {
        return this.annotationType.hashCode() * 37 + this.formalIndex * 37 + this.formalType.hashCode();
    }

    @Override
    public AnnotationTypePattern remapAdviceFormals(IntMap bindings) {
        if (!bindings.hasKey(this.formalIndex)) {
            throw new BCException("Annotation field binding reference must be bound (compiler limitation)");
        }
        int newFormalIndex = bindings.get(this.formalIndex);
        BindingAnnotationFieldTypePattern baftp = new BindingAnnotationFieldTypePattern(this.formalType, newFormalIndex, this.annotationType);
        baftp.formalName = this.formalName;
        return baftp;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(11);
        this.formalType.write(s);
        s.writeShort((short)this.formalIndex);
        this.annotationType.write(s);
        s.writeUTF(this.formalName);
        this.writeLocation(s);
    }

    public static AnnotationTypePattern read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        BindingAnnotationFieldTypePattern ret = new BindingAnnotationFieldTypePattern(UnresolvedType.read(s), s.readShort(), UnresolvedType.read(s));
        ret.readLocation(context, s);
        return ret;
    }

    public static AnnotationTypePattern read2(VersionedDataInputStream s, ISourceContext context) throws IOException {
        BindingAnnotationFieldTypePattern ret = new BindingAnnotationFieldTypePattern(UnresolvedType.read(s), s.readShort(), UnresolvedType.read(s));
        ret.formalName = s.readUTF();
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated, ResolvedType[] parameterAnnotations) {
        if (annotated.hasAnnotation(this.annotationType) && this.annotationType instanceof ReferenceType) {
            ReferenceType rt = (ReferenceType)this.annotationType;
            if (rt.getRetentionPolicy() != null && rt.getRetentionPolicy().equals("SOURCE")) {
                rt.getWorld().getMessageHandler().handleMessage(MessageUtil.warn(WeaverMessages.format("noMatchBecauseSourceRetention", this.annotationType, annotated), this.getSourceLocation()));
                return FuzzyBoolean.NO;
            }
            ResolvedMember[] methods = rt.getDeclaredMethods();
            boolean found = false;
            for (int i = 0; i < methods.length && !found; ++i) {
                if (!methods[i].getReturnType().equals(this.formalType)) continue;
                found = true;
            }
            return found ? FuzzyBoolean.YES : FuzzyBoolean.NO;
        }
        return FuzzyBoolean.NO;
    }

    public UnresolvedType getFormalType() {
        return this.formalType;
    }
}

