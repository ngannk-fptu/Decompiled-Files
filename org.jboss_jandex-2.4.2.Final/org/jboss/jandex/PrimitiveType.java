/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

public final class PrimitiveType
extends Type {
    public static final PrimitiveType BYTE = new PrimitiveType(Primitive.BYTE);
    public static final PrimitiveType CHAR = new PrimitiveType(Primitive.CHAR);
    public static final PrimitiveType DOUBLE = new PrimitiveType(Primitive.DOUBLE);
    public static final PrimitiveType FLOAT = new PrimitiveType(Primitive.FLOAT);
    public static final PrimitiveType INT = new PrimitiveType(Primitive.INT);
    public static final PrimitiveType LONG = new PrimitiveType(Primitive.LONG);
    public static final PrimitiveType SHORT = new PrimitiveType(Primitive.SHORT);
    public static final PrimitiveType BOOLEAN = new PrimitiveType(Primitive.BOOLEAN);
    private static final Map<String, PrimitiveType> reverseMap = new HashMap<String, PrimitiveType>();
    private final Primitive primitive;

    private PrimitiveType(Primitive primitive) {
        this(primitive, null);
    }

    private PrimitiveType(Primitive primitive, AnnotationInstance[] annotations) {
        super(new DotName(null, primitive.name().toLowerCase(Locale.ENGLISH), true, false), annotations);
        this.primitive = primitive;
    }

    @Override
    public Type.Kind kind() {
        return Type.Kind.PRIMITIVE;
    }

    public Primitive primitive() {
        return this.primitive;
    }

    @Override
    public PrimitiveType asPrimitiveType() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrimitiveType)) {
            return false;
        }
        PrimitiveType that = (PrimitiveType)o;
        return super.equals(o) && this.primitive == that.primitive;
    }

    @Override
    Type copyType(AnnotationInstance[] newAnnotations) {
        return new PrimitiveType(this.primitive, newAnnotations);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.primitive.hashCode();
        return result;
    }

    char toCode() {
        Primitive primitive = this.primitive;
        if (primitive == Primitive.BYTE) {
            return 'B';
        }
        if (primitive == Primitive.CHAR) {
            return 'C';
        }
        if (primitive == Primitive.DOUBLE) {
            return 'D';
        }
        if (primitive == Primitive.FLOAT) {
            return 'F';
        }
        if (primitive == Primitive.INT) {
            return 'I';
        }
        if (primitive == Primitive.LONG) {
            return 'J';
        }
        if (primitive == Primitive.SHORT) {
            return 'S';
        }
        return 'Z';
    }

    static PrimitiveType decode(String name) {
        return reverseMap.get(name);
    }

    static PrimitiveType decode(char c) {
        switch (c) {
            case 'B': {
                return BYTE;
            }
            case 'C': {
                return CHAR;
            }
            case 'D': {
                return DOUBLE;
            }
            case 'F': {
                return FLOAT;
            }
            case 'I': {
                return INT;
            }
            case 'J': {
                return LONG;
            }
            case 'S': {
                return SHORT;
            }
            case 'Z': {
                return BOOLEAN;
            }
        }
        return null;
    }

    static PrimitiveType fromOridinal(int ordinal) {
        switch (ordinal) {
            case 0: {
                return BYTE;
            }
            case 1: {
                return CHAR;
            }
            case 2: {
                return DOUBLE;
            }
            case 3: {
                return FLOAT;
            }
            case 4: {
                return INT;
            }
            case 5: {
                return LONG;
            }
            case 6: {
                return SHORT;
            }
            case 7: {
                return BOOLEAN;
            }
        }
        throw new IllegalArgumentException();
    }

    static {
        reverseMap.put("byte", BYTE);
        reverseMap.put("char", CHAR);
        reverseMap.put("double", DOUBLE);
        reverseMap.put("float", FLOAT);
        reverseMap.put("int", INT);
        reverseMap.put("long", LONG);
        reverseMap.put("short", SHORT);
        reverseMap.put("boolean", BOOLEAN);
    }

    public static enum Primitive {
        BYTE,
        CHAR,
        DOUBLE,
        FLOAT,
        INT,
        LONG,
        SHORT,
        BOOLEAN;

    }
}

