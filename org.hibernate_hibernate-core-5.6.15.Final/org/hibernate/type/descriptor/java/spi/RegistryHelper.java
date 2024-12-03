/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.type.descriptor.java.spi;

import java.util.Map;
import java.util.function.Supplier;
import org.hibernate.type.descriptor.java.EnumJavaTypeDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.jboss.logging.Logger;

public class RegistryHelper {
    private static final Logger log = Logger.getLogger(RegistryHelper.class);
    public static final RegistryHelper INSTANCE = new RegistryHelper();

    private RegistryHelper() {
    }

    public <J> JavaTypeDescriptor<J> resolveDescriptor(Map<Class, JavaTypeDescriptor> descriptorsByClass, Class<J> cls, Supplier<JavaTypeDescriptor<J>> defaultValueSupplier) {
        if (cls == null) {
            throw new IllegalArgumentException("Class passed to locate JavaTypeDescriptor cannot be null");
        }
        EnumJavaTypeDescriptor<J> descriptor = descriptorsByClass.get(cls);
        if (descriptor != null) {
            return descriptor;
        }
        if (cls.isEnum()) {
            descriptor = new EnumJavaTypeDescriptor<J>(cls);
            descriptorsByClass.put(cls, descriptor);
            return descriptor;
        }
        for (Map.Entry<Class, JavaTypeDescriptor> entry : descriptorsByClass.entrySet()) {
            if (!entry.getKey().isAssignableFrom(cls)) continue;
            log.debugf("Using  cached JavaTypeDescriptor instance for Java class [%s]", (Object)cls.getName());
            return entry.getValue();
        }
        return defaultValueSupplier.get();
    }
}

