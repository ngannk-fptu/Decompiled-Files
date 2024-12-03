/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public interface Substitution {
    public TypeBinding substitute(TypeVariableBinding var1);

    public LookupEnvironment environment();

    public boolean isRawSubstitution();

    public static class NullSubstitution
    implements Substitution {
        LookupEnvironment environment;

        public NullSubstitution(LookupEnvironment environment) {
            this.environment = environment;
        }

        @Override
        public TypeBinding substitute(TypeVariableBinding typeVariable) {
            return typeVariable;
        }

        @Override
        public boolean isRawSubstitution() {
            return false;
        }

        @Override
        public LookupEnvironment environment() {
            return this.environment;
        }
    }
}

