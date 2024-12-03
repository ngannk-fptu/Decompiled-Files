/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.build;

import net.bytebuddy.description.type.TypeDescription;

public interface AndroidDescriptor {
    public TypeScope getTypeScope(TypeDescription var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Trivial implements AndroidDescriptor
    {
        LOCAL(TypeScope.LOCAL),
        EXTERNAL(TypeScope.EXTERNAL);

        private final TypeScope typeScope;

        private Trivial(TypeScope typeScope) {
            this.typeScope = typeScope;
        }

        @Override
        public TypeScope getTypeScope(TypeDescription typeDescription) {
            return this.typeScope;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeScope {
        LOCAL,
        EXTERNAL;

    }
}

