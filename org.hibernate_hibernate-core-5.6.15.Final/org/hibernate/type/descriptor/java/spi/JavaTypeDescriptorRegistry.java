/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.type.descriptor.java.spi;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.spi.RegistryHelper;
import org.hibernate.type.spi.TypeConfiguration;
import org.jboss.logging.Logger;

public class JavaTypeDescriptorRegistry
implements Serializable {
    private static final Logger log = Logger.getLogger(JavaTypeDescriptorRegistry.class);
    private ConcurrentHashMap<Class, JavaTypeDescriptor> descriptorsByClass = new ConcurrentHashMap();

    public JavaTypeDescriptorRegistry(TypeConfiguration typeConfiguration) {
    }

    public <T> JavaTypeDescriptor<T> getDescriptor(Class<T> javaType) {
        return RegistryHelper.INSTANCE.resolveDescriptor(this.descriptorsByClass, javaType, () -> {
            log.debugf("Could not find matching scoped JavaTypeDescriptor for requested Java class [%s]; falling back to static registry", (Object)javaType.getName());
            return org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry.INSTANCE.getDescriptor(javaType);
        });
    }

    public void addDescriptor(JavaTypeDescriptor descriptor) {
        JavaTypeDescriptor old = this.descriptorsByClass.put(descriptor.getJavaType(), descriptor);
        if (old != null) {
            log.debugf("JavaTypeDescriptorRegistry entry replaced : %s -> %s (was %s)", descriptor.getJavaType(), (Object)descriptor, (Object)old);
        }
    }
}

