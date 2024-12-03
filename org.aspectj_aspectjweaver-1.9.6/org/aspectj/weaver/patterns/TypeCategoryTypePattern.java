/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;

public class TypeCategoryTypePattern
extends TypePattern {
    public static final int CLASS = 1;
    public static final int INTERFACE = 2;
    public static final int ASPECT = 3;
    public static final int INNER = 4;
    public static final int ANONYMOUS = 5;
    public static final int ENUM = 6;
    public static final int ANNOTATION = 7;
    public static final int FINAL = 8;
    public static final int ABSTRACT = 9;
    private int category;
    private int VERSION = 1;

    public TypeCategoryTypePattern(int category) {
        super(false);
        this.category = category;
    }

    public int getTypeCategory() {
        return this.category;
    }

    @Override
    protected boolean matchesExactly(ResolvedType type) {
        return this.isRightCategory(type);
    }

    @Override
    protected boolean matchesExactly(ResolvedType type, ResolvedType annotatedType) {
        return this.isRightCategory(type);
    }

    @Override
    public FuzzyBoolean matchesInstanceof(ResolvedType type) {
        return FuzzyBoolean.fromBoolean(this.isRightCategory(type));
    }

    @Override
    public TypePattern parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        return this;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean equals(Object other) {
        if (!(other instanceof TypeCategoryTypePattern)) {
            return false;
        }
        TypeCategoryTypePattern o = (TypeCategoryTypePattern)other;
        return o.category == this.category;
    }

    public int hashCode() {
        return this.category * 37;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(12);
        s.writeInt(this.VERSION);
        s.writeInt(this.category);
        this.writeLocation(s);
    }

    public static TypePattern read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        int version = s.readInt();
        int category = s.readInt();
        TypeCategoryTypePattern tp = new TypeCategoryTypePattern(category);
        tp.readLocation(context, s);
        return tp;
    }

    private boolean isRightCategory(ResolvedType type) {
        switch (this.category) {
            case 1: {
                return type.isClass();
            }
            case 2: {
                return type.isInterface();
            }
            case 3: {
                return type.isAspect();
            }
            case 5: {
                return type.isAnonymous();
            }
            case 4: {
                return type.isNested();
            }
            case 6: {
                return type.isEnum();
            }
            case 7: {
                return type.isAnnotation();
            }
            case 8: {
                return Modifier.isFinal(type.getModifiers());
            }
            case 9: {
                return Modifier.isAbstract(type.getModifiers());
            }
        }
        return false;
    }
}

