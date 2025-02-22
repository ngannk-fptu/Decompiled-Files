/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.objenesis.instantiator.sun;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;
import org.springframework.objenesis.instantiator.util.ClassDefinitionUtils;

@Instantiator(value=Typology.STANDARD)
public class MagicInstantiator<T>
implements ObjectInstantiator<T> {
    private static final String MAGIC_ACCESSOR = MagicInstantiator.getMagicClass();
    private static final int INDEX_CLASS_THIS = 1;
    private static final int INDEX_CLASS_SUPERCLASS = 2;
    private static final int INDEX_UTF8_CONSTRUCTOR_NAME = 3;
    private static final int INDEX_UTF8_CONSTRUCTOR_DESC = 4;
    private static final int INDEX_UTF8_CODE_ATTRIBUTE = 5;
    private static final int INDEX_UTF8_INSTANTIATOR_CLASS = 7;
    private static final int INDEX_UTF8_SUPERCLASS = 8;
    private static final int INDEX_CLASS_INTERFACE = 9;
    private static final int INDEX_UTF8_INTERFACE = 10;
    private static final int INDEX_UTF8_NEWINSTANCE_NAME = 11;
    private static final int INDEX_UTF8_NEWINSTANCE_DESC = 12;
    private static final int INDEX_METHODREF_OBJECT_CONSTRUCTOR = 13;
    private static final int INDEX_CLASS_OBJECT = 14;
    private static final int INDEX_UTF8_OBJECT = 15;
    private static final int INDEX_NAMEANDTYPE_DEFAULT_CONSTRUCTOR = 16;
    private static final int INDEX_CLASS_TYPE = 17;
    private static final int INDEX_UTF8_TYPE = 18;
    private static int CONSTANT_POOL_COUNT = 19;
    private static final byte[] CONSTRUCTOR_CODE = new byte[]{42, -73, 0, 13, -79};
    private static final int CONSTRUCTOR_CODE_ATTRIBUTE_LENGTH = 12 + CONSTRUCTOR_CODE.length;
    private static final byte[] NEWINSTANCE_CODE = new byte[]{-69, 0, 17, 89, -73, 0, 13, -80};
    private static final int NEWINSTANCE_CODE_ATTRIBUTE_LENGTH = 12 + NEWINSTANCE_CODE.length;
    private static final String CONSTRUCTOR_NAME = "<init>";
    private static final String CONSTRUCTOR_DESC = "()V";
    private ObjectInstantiator<T> instantiator;

    public MagicInstantiator(Class<T> type) {
        this.instantiator = this.newInstantiatorOf(type);
    }

    public ObjectInstantiator<T> getInstantiator() {
        return this.instantiator;
    }

    private <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        String suffix = type.getSimpleName();
        String className = this.getClass().getName() + "$$$" + suffix;
        Class clazz = ClassDefinitionUtils.getExistingClass(this.getClass().getClassLoader(), className);
        if (clazz == null) {
            byte[] classBytes = this.writeExtendingClass(type, className);
            try {
                clazz = ClassDefinitionUtils.defineClass(className, classBytes, this.getClass().getClassLoader());
            }
            catch (Exception e) {
                throw new ObjenesisException(e);
            }
        }
        try {
            return (ObjectInstantiator)clazz.newInstance();
        }
        catch (InstantiationException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
    }

    private byte[] writeExtendingClass(Class<?> type, String className) {
        String clazz = ClassDefinitionUtils.classNameToInternalClassName(className);
        DataOutputStream in = null;
        ByteArrayOutputStream bIn = new ByteArrayOutputStream(1000);
        try {
            in = new DataOutputStream(bIn);
            in.write(ClassDefinitionUtils.MAGIC);
            in.write(ClassDefinitionUtils.VERSION);
            in.writeShort(CONSTANT_POOL_COUNT);
            in.writeByte(7);
            in.writeShort(7);
            in.writeByte(7);
            in.writeShort(8);
            in.writeByte(1);
            in.writeUTF(CONSTRUCTOR_NAME);
            in.writeByte(1);
            in.writeUTF(CONSTRUCTOR_DESC);
            in.writeByte(1);
            in.writeUTF("Code");
            in.writeByte(1);
            in.writeUTF("L" + clazz + ";");
            in.writeByte(1);
            in.writeUTF(clazz);
            in.writeByte(1);
            in.writeUTF(MAGIC_ACCESSOR);
            in.writeByte(7);
            in.writeShort(10);
            in.writeByte(1);
            in.writeUTF(ObjectInstantiator.class.getName().replace('.', '/'));
            in.writeByte(1);
            in.writeUTF("newInstance");
            in.writeByte(1);
            in.writeUTF("()Ljava/lang/Object;");
            in.writeByte(10);
            in.writeShort(14);
            in.writeShort(16);
            in.writeByte(7);
            in.writeShort(15);
            in.writeByte(1);
            in.writeUTF("java/lang/Object");
            in.writeByte(12);
            in.writeShort(3);
            in.writeShort(4);
            in.writeByte(7);
            in.writeShort(18);
            in.writeByte(1);
            in.writeUTF(ClassDefinitionUtils.classNameToInternalClassName(type.getName()));
            in.writeShort(49);
            in.writeShort(1);
            in.writeShort(2);
            in.writeShort(1);
            in.writeShort(9);
            in.writeShort(0);
            in.writeShort(2);
            in.writeShort(1);
            in.writeShort(3);
            in.writeShort(4);
            in.writeShort(1);
            in.writeShort(5);
            in.writeInt(CONSTRUCTOR_CODE_ATTRIBUTE_LENGTH);
            in.writeShort(0);
            in.writeShort(1);
            in.writeInt(CONSTRUCTOR_CODE.length);
            in.write(CONSTRUCTOR_CODE);
            in.writeShort(0);
            in.writeShort(0);
            in.writeShort(1);
            in.writeShort(11);
            in.writeShort(12);
            in.writeShort(1);
            in.writeShort(5);
            in.writeInt(NEWINSTANCE_CODE_ATTRIBUTE_LENGTH);
            in.writeShort(2);
            in.writeShort(1);
            in.writeInt(NEWINSTANCE_CODE.length);
            in.write(NEWINSTANCE_CODE);
            in.writeShort(0);
            in.writeShort(0);
            in.writeShort(0);
        }
        catch (IOException e) {
            throw new ObjenesisException(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    throw new ObjenesisException(e);
                }
            }
        }
        return bIn.toByteArray();
    }

    @Override
    public T newInstance() {
        return this.instantiator.newInstance();
    }

    private static String getMagicClass() {
        try {
            Class.forName("sun.reflect.MagicAccessorImpl", false, MagicInstantiator.class.getClassLoader());
            return "sun/reflect/MagicAccessorImpl";
        }
        catch (ClassNotFoundException e) {
            return "jdk/internal/reflect/MagicAccessorImpl";
        }
    }
}

