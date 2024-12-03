/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.metadata;

import java.lang.annotation.ElementType;
import java.util.Set;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.Scope;

public interface ElementDescriptor {
    public boolean hasConstraints();

    public Class<?> getElementClass();

    public Set<ConstraintDescriptor<?>> getConstraintDescriptors();

    public ConstraintFinder findConstraints();

    public static interface ConstraintFinder {
        public ConstraintFinder unorderedAndMatchingGroups(Class<?> ... var1);

        public ConstraintFinder lookingAt(Scope var1);

        public ConstraintFinder declaredOn(ElementType ... var1);

        public Set<ConstraintDescriptor<?>> getConstraintDescriptors();

        public boolean hasConstraints();
    }
}

