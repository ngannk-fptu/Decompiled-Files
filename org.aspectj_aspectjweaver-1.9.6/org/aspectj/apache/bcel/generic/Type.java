/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.apache.bcel.ConstantsInitializer;
import org.aspectj.apache.bcel.classfile.ClassFormatException;
import org.aspectj.apache.bcel.classfile.Utility;
import org.aspectj.apache.bcel.generic.ArrayType;
import org.aspectj.apache.bcel.generic.BasicType;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.ReferenceType;

public abstract class Type {
    protected byte type;
    protected String signature;
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
    public static final ObjectType STRING = new ObjectType("java.lang.String");
    public static final ArrayType OBJECT_ARRAY = new ArrayType("java.lang.Object", 1);
    public static final ArrayType STRING_ARRAY = new ArrayType("java.lang.String", 1);
    public static final ArrayType CLASS_ARRAY = new ArrayType("java.lang.Class", 1);
    public static final ObjectType STRINGBUFFER = new ObjectType("java.lang.StringBuffer");
    public static final ObjectType STRINGBUILDER = new ObjectType("java.lang.StringBuilder");
    public static final ObjectType THROWABLE = new ObjectType("java.lang.Throwable");
    public static final ObjectType CLASS = new ObjectType("java.lang.Class");
    public static final ObjectType INTEGER = new ObjectType("java.lang.Integer");
    public static final ObjectType EXCEPTION = new ObjectType("java.lang.Exception");
    public static final ObjectType LIST = new ObjectType("java.util.List");
    public static final ObjectType ITERATOR = new ObjectType("java.util.Iterator");
    public static final Type[] NO_ARGS = new Type[0];
    public static final ReferenceType NULL = new ReferenceType(){};
    public static final Type UNKNOWN = new Type(15, "<unknown object>"){};
    public static final Type[] STRINGARRAY1 = new Type[]{STRING};
    public static final Type[] STRINGARRAY2 = new Type[]{STRING, STRING};
    public static final Type[] STRINGARRAY3 = new Type[]{STRING, STRING, STRING};
    public static final Type[] STRINGARRAY4 = new Type[]{STRING, STRING, STRING, STRING};
    public static final Type[] STRINGARRAY5 = new Type[]{STRING, STRING, STRING, STRING, STRING};
    public static final Type[] STRINGARRAY6 = new Type[]{STRING, STRING, STRING, STRING, STRING, STRING};
    public static final Type[] STRINGARRAY7 = new Type[]{STRING, STRING, STRING, STRING, STRING, STRING, STRING};
    private static Map<String, Type> commonTypes = new HashMap<String, Type>();

    protected Type(byte t, String s) {
        this.type = t;
        this.signature = s;
    }

    public String getSignature() {
        return this.signature;
    }

    public byte getType() {
        return this.type;
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

    public String toString() {
        return this.equals(NULL) || this.type >= 15 ? this.signature : Utility.signatureToString(this.signature, false);
    }

    public static final Type getType(String signature) {
        Type t = commonTypes.get(signature);
        if (t != null) {
            return t;
        }
        byte type = Utility.typeOfSignature(signature);
        if (type <= 12) {
            return BasicType.getType(type);
        }
        if (type == 13) {
            int dim = 0;
            while (signature.charAt(++dim) == '[') {
            }
            Type componentType = Type.getType(signature.substring(dim));
            return new ArrayType(componentType, dim);
        }
        int nextAngly = signature.indexOf(60);
        int index = signature.indexOf(59);
        String typeString = null;
        if (nextAngly == -1 || nextAngly > index) {
            typeString = signature.substring(1, index).replace('/', '.');
        } else {
            boolean endOfSigReached = false;
            int posn = nextAngly;
            int genericDepth = 0;
            block6: while (!endOfSigReached) {
                switch (signature.charAt(posn++)) {
                    case '<': {
                        ++genericDepth;
                        continue block6;
                    }
                    case '>': {
                        --genericDepth;
                        continue block6;
                    }
                    case ';': {
                        if (genericDepth != 0) continue block6;
                        endOfSigReached = true;
                        continue block6;
                    }
                }
            }
            index = posn - 1;
            typeString = signature.substring(1, nextAngly).replace('/', '.');
        }
        return new ObjectType(typeString);
    }

    public static final TypeHolder getTypeInternal(String signature) throws StringIndexOutOfBoundsException {
        byte type = Utility.typeOfSignature(signature);
        if (type <= 12) {
            return new TypeHolder(BasicType.getType(type), 1);
        }
        if (type == 13) {
            int dim = 0;
            while (signature.charAt(++dim) == '[') {
            }
            TypeHolder th = Type.getTypeInternal(signature.substring(dim));
            return new TypeHolder(new ArrayType(th.getType(), dim), dim + th.getConsumed());
        }
        int index = signature.indexOf(59);
        if (index < 0) {
            throw new ClassFormatException("Invalid signature: " + signature);
        }
        int nextAngly = signature.indexOf(60);
        String typeString = null;
        if (nextAngly == -1 || nextAngly > index) {
            typeString = signature.substring(1, index).replace('/', '.');
        } else {
            boolean endOfSigReached = false;
            int posn = nextAngly;
            int genericDepth = 0;
            block6: while (!endOfSigReached) {
                switch (signature.charAt(posn++)) {
                    case '<': {
                        ++genericDepth;
                        continue block6;
                    }
                    case '>': {
                        --genericDepth;
                        continue block6;
                    }
                    case ';': {
                        if (genericDepth != 0) continue block6;
                        endOfSigReached = true;
                        continue block6;
                    }
                }
            }
            index = posn - 1;
            typeString = signature.substring(1, nextAngly).replace('/', '.');
        }
        return new TypeHolder(new ObjectType(typeString), index + 1);
    }

    public static Type getReturnType(String signature) {
        try {
            int index = signature.lastIndexOf(41) + 1;
            return Type.getType(signature.substring(index));
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
    }

    public static Type[] getArgumentTypes(String signature) {
        ArrayList<Type> argumentTypes = new ArrayList<Type>();
        try {
            if (signature.charAt(0) != '(') {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            int index = 1;
            while (signature.charAt(index) != ')') {
                TypeHolder th = Type.getTypeInternal(signature.substring(index));
                argumentTypes.add(th.getType());
                index += th.getConsumed();
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
        Type[] types = new Type[argumentTypes.size()];
        argumentTypes.toArray(types);
        return types;
    }

    public static int getArgumentSizes(String signature) {
        int size = 0;
        if (signature.charAt(0) != '(') {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
        int index = 1;
        try {
            while (signature.charAt(index) != ')') {
                byte type = Utility.typeOfSignature(signature.charAt(index));
                if (type <= 12) {
                    size += BasicType.getType(type).getSize();
                    ++index;
                    continue;
                }
                if (type == 13) {
                    int dim = 0;
                    while (signature.charAt(++dim + index) == '[') {
                    }
                    TypeHolder th = Type.getTypeInternal(signature.substring(dim + index));
                    ++size;
                    index += dim + th.getConsumed();
                    continue;
                }
                int index2 = signature.indexOf(59, index);
                int nextAngly = signature.indexOf(60, index);
                if (nextAngly != -1 && nextAngly <= index2) {
                    boolean endOfSigReached = false;
                    int posn = nextAngly;
                    int genericDepth = 0;
                    block9: while (!endOfSigReached) {
                        switch (signature.charAt(posn++)) {
                            case '<': {
                                ++genericDepth;
                                continue block9;
                            }
                            case '>': {
                                --genericDepth;
                                continue block9;
                            }
                            case ';': {
                                if (genericDepth != 0) continue block9;
                                endOfSigReached = true;
                                continue block9;
                            }
                        }
                    }
                    index2 = posn - 1;
                }
                ++size;
                index = index2 + 1;
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
        return size;
    }

    public static int getTypeSize(String signature) {
        byte type = Utility.typeOfSignature(signature.charAt(0));
        if (type <= 12) {
            return BasicType.getType(type).getSize();
        }
        if (type == 13) {
            return 1;
        }
        return 1;
    }

    public static Type getType(Class cl) {
        if (cl == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if (cl.isArray()) {
            return Type.getType(cl.getName());
        }
        if (cl.isPrimitive()) {
            if (cl == Integer.TYPE) {
                return INT;
            }
            if (cl == Void.TYPE) {
                return VOID;
            }
            if (cl == Double.TYPE) {
                return DOUBLE;
            }
            if (cl == Float.TYPE) {
                return FLOAT;
            }
            if (cl == Boolean.TYPE) {
                return BOOLEAN;
            }
            if (cl == Byte.TYPE) {
                return BYTE;
            }
            if (cl == Short.TYPE) {
                return SHORT;
            }
            if (cl == Byte.TYPE) {
                return BYTE;
            }
            if (cl == Long.TYPE) {
                return LONG;
            }
            if (cl == Character.TYPE) {
                return CHAR;
            }
            throw new IllegalStateException("Ooops, what primitive type is " + cl);
        }
        return new ObjectType(cl.getName());
    }

    public static String getSignature(Method meth) {
        StringBuffer sb = new StringBuffer("(");
        Class<?>[] params = meth.getParameterTypes();
        for (int j = 0; j < params.length; ++j) {
            sb.append(Type.getType(params[j]).getSignature());
        }
        sb.append(")");
        sb.append(Type.getType(meth.getReturnType()).getSignature());
        return sb.toString();
    }

    public static String getSignature(Constructor<?> cons) {
        StringBuffer sb = new StringBuffer("(");
        Class<?>[] params = cons.getParameterTypes();
        for (int j = 0; j < params.length; ++j) {
            sb.append(Type.getType(params[j]).getSignature());
        }
        sb.append(")V");
        return sb.toString();
    }

    static {
        commonTypes.put(STRING.getSignature(), STRING);
        commonTypes.put(THROWABLE.getSignature(), THROWABLE);
        commonTypes.put(VOID.getSignature(), VOID);
        commonTypes.put(BOOLEAN.getSignature(), BOOLEAN);
        commonTypes.put(BYTE.getSignature(), BYTE);
        commonTypes.put(SHORT.getSignature(), SHORT);
        commonTypes.put(CHAR.getSignature(), CHAR);
        commonTypes.put(INT.getSignature(), INT);
        commonTypes.put(LONG.getSignature(), LONG);
        commonTypes.put(DOUBLE.getSignature(), DOUBLE);
        commonTypes.put(FLOAT.getSignature(), FLOAT);
        commonTypes.put(CLASS.getSignature(), CLASS);
        commonTypes.put(OBJECT.getSignature(), OBJECT);
        commonTypes.put(STRING_ARRAY.getSignature(), STRING_ARRAY);
        commonTypes.put(CLASS_ARRAY.getSignature(), CLASS_ARRAY);
        commonTypes.put(OBJECT_ARRAY.getSignature(), OBJECT_ARRAY);
        commonTypes.put(INTEGER.getSignature(), INTEGER);
        commonTypes.put(EXCEPTION.getSignature(), EXCEPTION);
        commonTypes.put(STRINGBUFFER.getSignature(), STRINGBUFFER);
        commonTypes.put(STRINGBUILDER.getSignature(), STRINGBUILDER);
        commonTypes.put(LIST.getSignature(), LIST);
        commonTypes.put(ITERATOR.getSignature(), ITERATOR);
        ConstantsInitializer.initialize();
    }

    public static class TypeHolder {
        private Type t;
        private int consumed;

        public Type getType() {
            return this.t;
        }

        public int getConsumed() {
            return this.consumed;
        }

        public TypeHolder(Type t, int i) {
            this.t = t;
            this.consumed = i;
        }
    }
}

