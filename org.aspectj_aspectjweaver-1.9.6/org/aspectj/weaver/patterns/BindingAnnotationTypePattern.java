/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariableReference;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.BindingPattern;
import org.aspectj.weaver.patterns.ExactAnnotationTypePattern;
import org.aspectj.weaver.patterns.FormalBinding;

public class BindingAnnotationTypePattern
extends ExactAnnotationTypePattern
implements BindingPattern {
    protected int formalIndex;
    private static final byte VERSION = 1;

    public BindingAnnotationTypePattern(UnresolvedType annotationType, int index) {
        super(annotationType, null);
        this.formalIndex = index;
    }

    public BindingAnnotationTypePattern(FormalBinding binding) {
        this(binding.getType(), binding.getIndex());
    }

    public void resolveBinding(World world) {
        if (this.resolved) {
            return;
        }
        this.resolved = true;
        this.annotationType = this.annotationType.resolve(world);
        ResolvedType resolvedAnnotationType = (ResolvedType)this.annotationType;
        if (!resolvedAnnotationType.isAnnotation()) {
            IMessage m = MessageUtil.error(WeaverMessages.format("referenceToNonAnnotationType", this.annotationType.getName()), this.getSourceLocation());
            world.getMessageHandler().handleMessage(m);
            this.resolved = false;
        }
        if (this.annotationType.isTypeVariableReference()) {
            return;
        }
        this.verifyRuntimeRetention(world, resolvedAnnotationType);
    }

    private void verifyRuntimeRetention(World world, ResolvedType resolvedAnnotationType) {
        if (!resolvedAnnotationType.isAnnotationWithRuntimeRetention()) {
            IMessage m = MessageUtil.error(WeaverMessages.format("bindingNonRuntimeRetentionAnnotation", this.annotationType.getName()), this.getSourceLocation());
            world.getMessageHandler().handleMessage(m);
            this.resolved = false;
        }
    }

    public AnnotationTypePattern parameterizeWith(Map typeVariableMap, World w) {
        UnresolvedType newAnnotationType = this.annotationType;
        if (this.annotationType.isTypeVariableReference()) {
            TypeVariableReference t = (TypeVariableReference)((Object)this.annotationType);
            String key = t.getTypeVariable().getName();
            if (typeVariableMap.containsKey(key)) {
                newAnnotationType = (UnresolvedType)typeVariableMap.get(key);
            }
        } else if (this.annotationType.isParameterizedType()) {
            newAnnotationType = this.annotationType.parameterize(typeVariableMap);
        }
        BindingAnnotationTypePattern ret = new BindingAnnotationTypePattern(newAnnotationType, this.formalIndex);
        if (newAnnotationType instanceof ResolvedType) {
            ResolvedType rat = (ResolvedType)newAnnotationType;
            this.verifyRuntimeRetention(rat.getWorld(), rat);
        }
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public int getFormalIndex() {
        return this.formalIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BindingAnnotationTypePattern)) {
            return false;
        }
        BindingAnnotationTypePattern btp = (BindingAnnotationTypePattern)obj;
        return super.equals(btp) && btp.formalIndex == this.formalIndex;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 37 + this.formalIndex;
    }

    @Override
    public AnnotationTypePattern remapAdviceFormals(IntMap bindings) {
        if (!bindings.hasKey(this.formalIndex)) {
            return new ExactAnnotationTypePattern(this.annotationType, null);
        }
        int newFormalIndex = bindings.get(this.formalIndex);
        return new BindingAnnotationTypePattern(this.annotationType, newFormalIndex);
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(2);
        s.writeByte(1);
        this.annotationType.write(s);
        s.writeShort((short)this.formalIndex);
        this.writeLocation(s);
    }

    public static AnnotationTypePattern read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        byte version = s.readByte();
        if (version > 1) {
            throw new BCException("BindingAnnotationTypePattern was written by a more recent version of AspectJ");
        }
        BindingAnnotationTypePattern ret = new BindingAnnotationTypePattern(UnresolvedType.read(s), s.readShort());
        ret.readLocation(context, s);
        return ret;
    }
}

