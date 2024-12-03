/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils.bytecode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.axis.utils.Messages;

public class ClassReader
extends ByteArrayInputStream {
    private static final int CONSTANT_Class = 7;
    private static final int CONSTANT_Fieldref = 9;
    private static final int CONSTANT_Methodref = 10;
    private static final int CONSTANT_InterfaceMethodref = 11;
    private static final int CONSTANT_String = 8;
    private static final int CONSTANT_Integer = 3;
    private static final int CONSTANT_Float = 4;
    private static final int CONSTANT_Long = 5;
    private static final int CONSTANT_Double = 6;
    private static final int CONSTANT_NameAndType = 12;
    private static final int CONSTANT_Utf8 = 1;
    private int[] cpoolIndex;
    private Object[] cpool;
    private Map attrMethods;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static byte[] getBytes(Class c) throws IOException {
        InputStream fin = c.getResourceAsStream('/' + c.getName().replace('.', '/') + ".class");
        if (fin == null) {
            throw new IOException(Messages.getMessage("cantLoadByecode", c.getName()));
        }
        try {
            int actual;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            do {
                if ((actual = fin.read(buf)) <= 0) continue;
                out.write(buf, 0, actual);
            } while (actual > 0);
            byte[] byArray = out.toByteArray();
            return byArray;
        }
        finally {
            fin.close();
        }
    }

    static String classDescriptorToName(String desc) {
        return desc.replace('/', '.');
    }

    protected static Map findAttributeReaders(Class c) {
        HashMap<String, Method> map = new HashMap<String, Method>();
        Method[] methods = c.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            String name = methods[i].getName();
            if (!name.startsWith("read") || methods[i].getReturnType() != Void.TYPE) continue;
            map.put(name.substring(4), methods[i]);
        }
        return map;
    }

    protected static String getSignature(Member method, Class[] paramTypes) {
        StringBuffer b = new StringBuffer(method instanceof Method ? method.getName() : "<init>");
        b.append('(');
        for (int i = 0; i < paramTypes.length; ++i) {
            ClassReader.addDescriptor(b, paramTypes[i]);
        }
        b.append(')');
        if (method instanceof Method) {
            ClassReader.addDescriptor(b, ((Method)method).getReturnType());
        } else if (method instanceof Constructor) {
            ClassReader.addDescriptor(b, Void.TYPE);
        }
        return b.toString();
    }

    private static void addDescriptor(StringBuffer b, Class c) {
        if (c.isPrimitive()) {
            if (c == Void.TYPE) {
                b.append('V');
            } else if (c == Integer.TYPE) {
                b.append('I');
            } else if (c == Boolean.TYPE) {
                b.append('Z');
            } else if (c == Byte.TYPE) {
                b.append('B');
            } else if (c == Short.TYPE) {
                b.append('S');
            } else if (c == Long.TYPE) {
                b.append('J');
            } else if (c == Character.TYPE) {
                b.append('C');
            } else if (c == Float.TYPE) {
                b.append('F');
            } else if (c == Double.TYPE) {
                b.append('D');
            }
        } else if (c.isArray()) {
            b.append('[');
            ClassReader.addDescriptor(b, c.getComponentType());
        } else {
            b.append('L').append(c.getName().replace('.', '/')).append(';');
        }
    }

    protected final int readShort() {
        return this.read() << 8 | this.read();
    }

    protected final int readInt() {
        return this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
    }

    protected void skipFully(int n) throws IOException {
        while (n > 0) {
            int c = (int)this.skip(n);
            if (c <= 0) {
                throw new EOFException(Messages.getMessage("unexpectedEOF00"));
            }
            n -= c;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Member resolveMethod(int index) throws IOException, ClassNotFoundException, NoSuchMethodException {
        int oldPos = this.pos;
        try {
            Executable m = (Constructor<?>)this.cpool[index];
            if (m == null) {
                this.pos = this.cpoolIndex[index];
                Class owner = this.resolveClass(this.readShort());
                NameAndType nt = this.resolveNameAndType(this.readShort());
                String signature = nt.name + nt.type;
                if (nt.name.equals("<init>")) {
                    Constructor<?>[] ctors = owner.getConstructors();
                    for (int i = 0; i < ctors.length; ++i) {
                        String sig = ClassReader.getSignature(ctors[i], ctors[i].getParameterTypes());
                        if (!sig.equals(signature)) continue;
                        this.cpool[index] = m = ctors[i];
                        Executable executable = m;
                        return executable;
                    }
                } else {
                    Method[] methods = owner.getDeclaredMethods();
                    for (int i = 0; i < methods.length; ++i) {
                        String sig = ClassReader.getSignature(methods[i], methods[i].getParameterTypes());
                        if (!sig.equals(signature)) continue;
                        m = methods[i];
                        this.cpool[index] = m;
                        Executable executable = m;
                        return executable;
                    }
                }
                throw new NoSuchMethodException(signature);
            }
            Constructor<?> constructor = m;
            return constructor;
        }
        finally {
            this.pos = oldPos;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Field resolveField(int i) throws IOException, ClassNotFoundException, NoSuchFieldException {
        int oldPos = this.pos;
        try {
            Field f = (Field)this.cpool[i];
            if (f == null) {
                this.pos = this.cpoolIndex[i];
                Class owner = this.resolveClass(this.readShort());
                NameAndType nt = this.resolveNameAndType(this.readShort());
                f = owner.getDeclaredField(nt.name);
                this.cpool[i] = f;
            }
            Field field = f;
            return field;
        }
        finally {
            this.pos = oldPos;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final NameAndType resolveNameAndType(int i) throws IOException {
        int oldPos = this.pos;
        try {
            NameAndType nt = (NameAndType)this.cpool[i];
            if (nt == null) {
                this.pos = this.cpoolIndex[i];
                String name = this.resolveUtf8(this.readShort());
                String type = this.resolveUtf8(this.readShort());
                nt = new NameAndType(name, type);
                this.cpool[i] = nt;
            }
            NameAndType nameAndType = nt;
            return nameAndType;
        }
        finally {
            this.pos = oldPos;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Class resolveClass(int i) throws IOException, ClassNotFoundException {
        int oldPos = this.pos;
        try {
            Class<?> c = (Class<?>)this.cpool[i];
            if (c == null) {
                this.pos = this.cpoolIndex[i];
                String name = this.resolveUtf8(this.readShort());
                this.cpool[i] = c = Class.forName(ClassReader.classDescriptorToName(name));
            }
            Class<?> clazz = c;
            return clazz;
        }
        finally {
            this.pos = oldPos;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final String resolveUtf8(int i) throws IOException {
        int oldPos = this.pos;
        try {
            String s = (String)this.cpool[i];
            if (s == null) {
                this.pos = this.cpoolIndex[i];
                int len = this.readShort();
                this.skipFully(len);
                s = new String(this.buf, this.pos - len, len, "utf-8");
                this.cpool[i] = s;
            }
            String string = s;
            return string;
        }
        finally {
            this.pos = oldPos;
        }
    }

    protected final void readCpool() throws IOException {
        int count = this.readShort();
        this.cpoolIndex = new int[count];
        this.cpool = new Object[count];
        block7: for (int i = 1; i < count; ++i) {
            int c = this.read();
            this.cpoolIndex[i] = this.pos;
            switch (c) {
                case 9: 
                case 10: 
                case 11: 
                case 12: {
                    this.readShort();
                }
                case 7: 
                case 8: {
                    this.readShort();
                    continue block7;
                }
                case 5: 
                case 6: {
                    this.readInt();
                    ++i;
                }
                case 3: 
                case 4: {
                    this.readInt();
                    continue block7;
                }
                case 1: {
                    int len = this.readShort();
                    this.skipFully(len);
                    continue block7;
                }
                default: {
                    throw new IllegalStateException(Messages.getMessage("unexpectedBytes00"));
                }
            }
        }
    }

    protected final void skipAttributes() throws IOException {
        int count = this.readShort();
        for (int i = 0; i < count; ++i) {
            this.readShort();
            this.skipFully(this.readInt());
        }
    }

    protected final void readAttributes() throws IOException {
        int count = this.readShort();
        for (int i = 0; i < count; ++i) {
            int nameIndex = this.readShort();
            int attrLen = this.readInt();
            int curPos = this.pos;
            String attrName = this.resolveUtf8(nameIndex);
            Method m = (Method)this.attrMethods.get(attrName);
            if (m != null) {
                try {
                    m.invoke((Object)this, new Object[0]);
                }
                catch (IllegalAccessException e) {
                    this.pos = curPos;
                    this.skipFully(attrLen);
                }
                catch (InvocationTargetException e) {
                    try {
                        throw e.getTargetException();
                    }
                    catch (Error ex) {
                        throw ex;
                    }
                    catch (RuntimeException ex) {
                        throw ex;
                    }
                    catch (IOException ex) {
                        throw ex;
                    }
                    catch (Throwable ex) {
                        this.pos = curPos;
                        this.skipFully(attrLen);
                    }
                }
                continue;
            }
            this.skipFully(attrLen);
        }
    }

    public void readCode() throws IOException {
        this.readShort();
        this.readShort();
        this.skipFully(this.readInt());
        this.skipFully(8 * this.readShort());
        this.readAttributes();
    }

    protected ClassReader(byte[] buf, Map attrMethods) {
        super(buf);
        this.attrMethods = attrMethods;
    }

    private static class NameAndType {
        String name;
        String type;

        public NameAndType(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}

