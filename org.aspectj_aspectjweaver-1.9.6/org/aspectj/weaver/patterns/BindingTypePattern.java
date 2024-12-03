/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.BindingPattern;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.TypePattern;

public class BindingTypePattern
extends ExactTypePattern
implements BindingPattern {
    private int formalIndex;
    private String bindingName;

    public BindingTypePattern(UnresolvedType type, int index, boolean isVarArgs) {
        super(type, false, isVarArgs);
        this.formalIndex = index;
    }

    public BindingTypePattern(FormalBinding binding, boolean isVarArgs) {
        this(binding.getType(), binding.getIndex(), isVarArgs);
        this.bindingName = binding.getName();
    }

    @Override
    public int getFormalIndex() {
        return this.formalIndex;
    }

    public String getBindingName() {
        return this.bindingName;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BindingTypePattern)) {
            return false;
        }
        BindingTypePattern o = (BindingTypePattern)other;
        if (this.includeSubtypes != o.includeSubtypes) {
            return false;
        }
        if (this.isVarArgs != o.isVarArgs) {
            return false;
        }
        return o.type.equals(this.type) && o.formalIndex == this.formalIndex;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + super.hashCode();
        result = 37 * result + this.formalIndex;
        return result;
    }

    @Override
    public void write(CompressingDataOutputStream out) throws IOException {
        out.writeByte(3);
        this.type.write(out);
        out.writeShort((short)this.formalIndex);
        out.writeBoolean(this.isVarArgs);
        this.writeLocation(out);
    }

    public static TypePattern read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        UnresolvedType type = UnresolvedType.read(s);
        short index = s.readShort();
        boolean isVarargs = false;
        if (s.getMajorVersion() >= 2) {
            isVarargs = s.readBoolean();
        }
        BindingTypePattern ret = new BindingTypePattern(type, index, isVarargs);
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public TypePattern remapAdviceFormals(IntMap bindings) {
        if (!bindings.hasKey(this.formalIndex)) {
            return new ExactTypePattern(this.type, false, this.isVarArgs);
        }
        int newFormalIndex = bindings.get(this.formalIndex);
        return new BindingTypePattern(this.type, newFormalIndex, this.isVarArgs);
    }

    @Override
    public TypePattern parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        ExactTypePattern superParameterized = (ExactTypePattern)super.parameterizeWith(typeVariableMap, w);
        BindingTypePattern ret = new BindingTypePattern(superParameterized.getExactType(), this.formalIndex, this.isVarArgs);
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public String toString() {
        return "BindingTypePattern(" + super.toString() + ", " + this.formalIndex + ")";
    }
}

