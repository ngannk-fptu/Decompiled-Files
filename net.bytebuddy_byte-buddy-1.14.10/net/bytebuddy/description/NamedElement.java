/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description;

import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface NamedElement {
    @AlwaysNull
    public static final String NO_NAME = null;
    public static final String EMPTY_NAME = "";

    public String getActualName();

    public static interface WithDescriptor
    extends NamedElement {
        @AlwaysNull
        public static final String NON_GENERIC_SIGNATURE = null;

        public String getDescriptor();

        @MaybeNull
        public String getGenericSignature();
    }

    public static interface WithGenericName
    extends WithRuntimeName {
        public String toGenericString();
    }

    public static interface WithOptionalName
    extends NamedElement {
        public boolean isNamed();
    }

    public static interface WithRuntimeName
    extends NamedElement {
        public String getName();

        public String getInternalName();
    }
}

