/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.description.type.TypeDescription
 */
package org.hibernate.bytecode.enhance.internal.bytebuddy;

import java.lang.annotation.Annotation;
import net.bytebuddy.description.type.TypeDescription;
import org.hibernate.bytecode.enhance.spi.UnloadedClass;

class UnloadedTypeDescription
implements UnloadedClass {
    private final TypeDescription typeDescription;

    UnloadedTypeDescription(TypeDescription typeDescription) {
        this.typeDescription = typeDescription;
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return this.typeDescription.getDeclaredAnnotations().isAnnotationPresent(annotationType);
    }

    @Override
    public String getName() {
        return this.typeDescription.getName();
    }
}

