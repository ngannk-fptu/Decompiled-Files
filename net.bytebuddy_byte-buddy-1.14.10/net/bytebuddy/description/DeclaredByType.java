/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.bytebuddy.description;

import javax.annotation.Nonnull;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface DeclaredByType {
    @MaybeNull
    public TypeDefinition getDeclaringType();

    public static interface WithMandatoryDeclaration
    extends DeclaredByType {
        @Nonnull
        public TypeDefinition getDeclaringType();
    }
}

