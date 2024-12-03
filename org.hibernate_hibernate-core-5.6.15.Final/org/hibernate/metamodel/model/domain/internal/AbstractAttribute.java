/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute$PersistentAttributeType
 */
package org.hibernate.metamodel.model.domain.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import javax.persistence.metamodel.Attribute;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;

public abstract class AbstractAttribute<D, J>
implements PersistentAttributeDescriptor<D, J>,
Serializable {
    private final ManagedTypeDescriptor<D> declaringType;
    private final String name;
    private final Attribute.PersistentAttributeType attributeNature;
    private final SimpleTypeDescriptor<?> valueType;
    private transient Member member;

    protected AbstractAttribute(ManagedTypeDescriptor<D> declaringType, String name, Attribute.PersistentAttributeType attributeNature, SimpleTypeDescriptor<?> valueType, Member member) {
        this.declaringType = declaringType;
        this.name = name;
        this.attributeNature = attributeNature;
        this.valueType = valueType;
        this.member = member;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public ManagedTypeDescriptor<D> getDeclaringType() {
        return this.declaringType;
    }

    public Member getJavaMember() {
        return this.member;
    }

    public Attribute.PersistentAttributeType getPersistentAttributeType() {
        return this.attributeNature;
    }

    @Override
    public SimpleTypeDescriptor<?> getValueGraphType() {
        return this.valueType;
    }

    public String toString() {
        return this.declaringType.getName() + '#' + this.name + '(' + this.attributeNature + ')';
    }

    protected void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        String memberDeclaringClassName = (String)ois.readObject();
        String memberName = (String)ois.readObject();
        String memberType = (String)ois.readObject();
        Class<?> memberDeclaringClass = Class.forName(memberDeclaringClassName, false, this.declaringType.getJavaType().getClassLoader());
        try {
            this.member = "method".equals(memberType) ? memberDeclaringClass.getMethod(memberName, ReflectHelper.NO_PARAM_SIGNATURE) : memberDeclaringClass.getField(memberName);
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to locate member [" + memberDeclaringClassName + "#" + memberName + "]");
        }
    }

    protected void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(this.getJavaMember().getDeclaringClass().getName());
        oos.writeObject(this.getJavaMember().getName());
        oos.writeObject(Method.class.isInstance(this.getJavaMember()) ? "method" : "field");
    }
}

