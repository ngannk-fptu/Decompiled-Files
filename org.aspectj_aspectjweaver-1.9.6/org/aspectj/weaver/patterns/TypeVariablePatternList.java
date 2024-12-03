/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.patterns.PatternNode;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypeVariablePattern;

public class TypeVariablePatternList
extends PatternNode {
    public static final TypeVariablePatternList EMPTY = new TypeVariablePatternList(new TypeVariablePattern[0]);
    private TypeVariablePattern[] patterns;

    public TypeVariablePatternList(TypeVariablePattern[] typeVars) {
        this.patterns = typeVars;
    }

    public TypeVariablePattern[] getTypeVariablePatterns() {
        return this.patterns;
    }

    public TypeVariablePattern lookupTypeVariable(String name) {
        for (int i = 0; i < this.patterns.length; ++i) {
            if (!this.patterns[i].getName().equals(name)) continue;
            return this.patterns[i];
        }
        return null;
    }

    public boolean isEmpty() {
        return this.patterns == null || this.patterns.length == 0;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeInt(this.patterns.length);
        for (int i = 0; i < this.patterns.length; ++i) {
            this.patterns[i].write(s);
        }
        this.writeLocation(s);
    }

    public static TypeVariablePatternList read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        TypeVariablePatternList ret = EMPTY;
        int length = s.readInt();
        if (length > 0) {
            TypeVariablePattern[] patterns = new TypeVariablePattern[length];
            for (int i = 0; i < patterns.length; ++i) {
                patterns[i] = TypeVariablePattern.read(s, context);
            }
            ret = new TypeVariablePatternList(patterns);
        }
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object traverse(PatternNodeVisitor visitor, Object data) {
        Object ret = this.accept(visitor, data);
        for (int i = 0; i < this.patterns.length; ++i) {
            this.patterns[i].traverse(visitor, ret);
        }
        return ret;
    }
}

