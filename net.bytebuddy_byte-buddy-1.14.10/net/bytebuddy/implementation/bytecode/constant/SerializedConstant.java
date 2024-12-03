/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.constant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

@HashCodeAndEqualsPlugin.Enhance
public class SerializedConstant
extends StackManipulation.AbstractBase {
    private static final String CHARSET = "ISO-8859-1";
    private final String serialization;

    protected SerializedConstant(String serialization) {
        this.serialization = serialization;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static StackManipulation of(@MaybeNull Serializable value) {
        if (value == null) {
            return NullConstant.INSTANCE;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            try {
                objectOutputStream.writeObject(value);
                Object var4_4 = null;
            }
            catch (Throwable throwable) {
                Object var4_5 = null;
                objectOutputStream.close();
                throw throwable;
            }
            objectOutputStream.close();
            return new SerializedConstant(byteArrayOutputStream.toString(CHARSET));
        }
        catch (IOException exception) {
            throw new IllegalStateException("Cannot serialize " + value, exception);
        }
    }

    public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        try {
            return new StackManipulation.Compound(TypeCreation.of(TypeDescription.ForLoadedType.of(ObjectInputStream.class)), Duplication.SINGLE, TypeCreation.of(TypeDescription.ForLoadedType.of(ByteArrayInputStream.class)), Duplication.SINGLE, new TextConstant(this.serialization), new TextConstant(CHARSET), MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(String.class.getMethod("getBytes", String.class))), MethodInvocation.invoke(new MethodDescription.ForLoadedConstructor(ByteArrayInputStream.class.getConstructor(byte[].class))), MethodInvocation.invoke(new MethodDescription.ForLoadedConstructor(ObjectInputStream.class.getConstructor(InputStream.class))), MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(ObjectInputStream.class.getMethod("readObject", new Class[0])))).apply(methodVisitor, implementationContext);
        }
        catch (NoSuchMethodException exception) {
            throw new IllegalStateException("Could not locate Java API method", exception);
        }
    }

    public boolean equals(@MaybeNull Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        return this.serialization.equals(((SerializedConstant)object).serialization);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.serialization.hashCode();
    }
}

