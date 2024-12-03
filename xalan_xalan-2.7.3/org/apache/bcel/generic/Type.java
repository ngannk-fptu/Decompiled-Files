/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.ReferenceType;

public abstract class Type {
    public static final BasicType VOID = new BasicType(12);
    public static final BasicType BOOLEAN = new BasicType(4);
    public static final BasicType INT = new BasicType(10);
    public static final BasicType SHORT = new BasicType(9);
    public static final BasicType BYTE = new BasicType(8);
    public static final BasicType LONG = new BasicType(11);
    public static final BasicType DOUBLE = new BasicType(7);
    public static final BasicType FLOAT = new BasicType(6);
    public static final BasicType CHAR = new BasicType(5);
    public static final ObjectType OBJECT = new ObjectType("java.lang.Object");
    public static final ObjectType CLASS = new ObjectType("java.lang.Class");
    public static final ObjectType STRING = new ObjectType("java.lang.String");
    public static final ObjectType STRINGBUFFER = new ObjectType("java.lang.StringBuffer");
    public static final ObjectType THROWABLE = new ObjectType("java.lang.Throwable");
    public static final Type[] NO_ARGS = new Type[0];
    public static final ReferenceType NULL = new ReferenceType(){};
    public static final Type UNKNOWN = new Type(15, "<unknown object>"){};
    private static final ThreadLocal<Integer> CONSUMED_CHARS = ThreadLocal.withInitial(() -> 0);
    @Deprecated
    protected byte type;
    @Deprecated
    protected String signature;

    static int consumed(int coded) {
        return coded >> 2;
    }

    static int encode(int size, int consumed) {
        return consumed << 2 | size;
    }

    public static Type[] getArgumentTypes(String signature) {
        ArrayList<Type> vec = new ArrayList<Type>();
        try {
            int index = signature.indexOf(40) + 1;
            if (index <= 0) {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            while (signature.charAt(index) != ')') {
                vec.add(Type.getType(signature.substring(index)));
                index += Type.unwrap(CONSUMED_CHARS);
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature, e);
        }
        Type[] types = new Type[vec.size()];
        vec.toArray(types);
        return types;
    }

    static int getArgumentTypesSize(String signature) {
        int res = 0;
        try {
            int index = signature.indexOf(40) + 1;
            if (index <= 0) {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            while (signature.charAt(index) != ')') {
                int coded = Type.getTypeSize(signature.substring(index));
                res += Type.size(coded);
                index += Type.consumed(coded);
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature, e);
        }
        return res;
    }

    public static String getMethodSignature(Type returnType, Type[] argTypes) {
        StringBuilder buf = new StringBuilder("(");
        if (argTypes != null) {
            for (Type argType : argTypes) {
                buf.append(argType.getSignature());
            }
        }
        buf.append(')');
        buf.append(returnType.getSignature());
        return buf.toString();
    }

    public static Type getReturnType(String signature) {
        try {
            int index = signature.lastIndexOf(41) + 1;
            return Type.getType(signature.substring(index));
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature, e);
        }
    }

    static int getReturnTypeSize(String signature) {
        int index = signature.lastIndexOf(41) + 1;
        return Type.size(Type.getTypeSize(signature.substring(index)));
    }

    public static String getSignature(Method meth) {
        Class<?>[] params;
        StringBuilder sb = new StringBuilder("(");
        for (Class<?> param : params = meth.getParameterTypes()) {
            sb.append(Type.getType(param).getSignature());
        }
        sb.append(")");
        sb.append(Type.getType(meth.getReturnType()).getSignature());
        return sb.toString();
    }

    public static Type getType(Class<?> cls) {
        Objects.requireNonNull(cls, "cls");
        if (cls.isArray()) {
            return Type.getType(cls.getName());
        }
        if (!cls.isPrimitive()) {
            return ObjectType.getInstance(cls.getName());
        }
        if (cls == Integer.TYPE) {
            return INT;
        }
        if (cls == Void.TYPE) {
            return VOID;
        }
        if (cls == Double.TYPE) {
            return DOUBLE;
        }
        if (cls == Float.TYPE) {
            return FLOAT;
        }
        if (cls == Boolean.TYPE) {
            return BOOLEAN;
        }
        if (cls == Byte.TYPE) {
            return BYTE;
        }
        if (cls == Short.TYPE) {
            return SHORT;
        }
        if (cls == Long.TYPE) {
            return LONG;
        }
        if (cls == Character.TYPE) {
            return CHAR;
        }
        throw new IllegalStateException("Unknown primitive type " + cls);
    }

    public static Type getType(String signature) throws StringIndexOutOfBoundsException {
        byte type = Utility.typeOfSignature(signature);
        if (type <= 12) {
            Type.wrap(CONSUMED_CHARS, 1);
            return BasicType.getType(type);
        }
        if (type != 13) {
            String parsedSignature = Utility.typeSignatureToString(signature, false);
            Type.wrap(CONSUMED_CHARS, parsedSignature.length() + 2);
            return ObjectType.getInstance(Utility.pathToPackage(parsedSignature));
        }
        int dim = 0;
        while (signature.charAt(++dim) == '[') {
        }
        Type t = Type.getType(signature.substring(dim));
        int temp = Type.unwrap(CONSUMED_CHARS) + dim;
        Type.wrap(CONSUMED_CHARS, temp);
        return new ArrayType(t, dim);
    }

    public static Type[] getTypes(Class<?>[] classes) {
        Type[] ret = new Type[classes.length];
        Arrays.setAll(ret, i -> Type.getType(classes[i]));
        return ret;
    }

    static int getTypeSize(String signature) throws StringIndexOutOfBoundsException {
        byte type = Utility.typeOfSignature(signature);
        if (type <= 12) {
            return Type.encode(BasicType.getType(type).getSize(), 1);
        }
        if (type == 13) {
            int dim = 0;
            while (signature.charAt(++dim) == '[') {
            }
            int consumed = Type.consumed(Type.getTypeSize(signature.substring(dim)));
            return Type.encode(1, dim + consumed);
        }
        int index = signature.indexOf(59);
        if (index < 0) {
            throw new ClassFormatException("Invalid signature: " + signature);
        }
        return Type.encode(1, index + 1);
    }

    static int size(int coded) {
        return coded & 3;
    }

    private static int unwrap(ThreadLocal<Integer> tl) {
        return tl.get();
    }

    private static void wrap(ThreadLocal<Integer> tl, int value) {
        tl.set(value);
    }

    protected Type(byte type, String signature) {
        this.type = type;
        this.signature = signature;
    }

    public boolean equals(Object o) {
        if (o instanceof Type) {
            Type t = (Type)o;
            return this.type == t.type && this.signature.equals(t.signature);
        }
        return false;
    }

    public String getClassName() {
        return this.toString();
    }

    public String getSignature() {
        return this.signature;
    }

    public int getSize() {
        switch (this.type) {
            case 7: 
            case 11: {
                return 2;
            }
            case 12: {
                return 0;
            }
        }
        return 1;
    }

    public byte getType() {
        return this.type;
    }

    public int hashCode() {
        return this.type ^ this.signature.hashCode();
    }

    public Type normalizeForStackOrLocal() {
        if (this == BOOLEAN || this == BYTE || this == SHORT || this == CHAR) {
            return INT;
        }
        return this;
    }

    void setSignature(String signature) {
        this.signature = signature;
    }

    public String toString() {
        return this.equals(NULL) || this.type >= 15 ? this.signature : Utility.signatureToString(this.signature, false);
    }
}

