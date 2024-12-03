/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.metadata;

import java.util.Set;
import javax.validation.metadata.ConstructorDescriptor;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.MethodDescriptor;
import javax.validation.metadata.MethodType;
import javax.validation.metadata.PropertyDescriptor;

public interface BeanDescriptor
extends ElementDescriptor {
    public boolean isBeanConstrained();

    public PropertyDescriptor getConstraintsForProperty(String var1);

    public Set<PropertyDescriptor> getConstrainedProperties();

    public MethodDescriptor getConstraintsForMethod(String var1, Class<?> ... var2);

    public Set<MethodDescriptor> getConstrainedMethods(MethodType var1, MethodType ... var2);

    public ConstructorDescriptor getConstraintsForConstructor(Class<?> ... var1);

    public Set<ConstructorDescriptor> getConstrainedConstructors();
}

