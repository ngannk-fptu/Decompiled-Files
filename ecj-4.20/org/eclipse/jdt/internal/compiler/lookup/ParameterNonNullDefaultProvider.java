/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

interface ParameterNonNullDefaultProvider {
    public static final ParameterNonNullDefaultProvider FALSE_PROVIDER = new ParameterNonNullDefaultProvider(){

        @Override
        public boolean hasNonNullDefaultForParam(int i) {
            return false;
        }

        @Override
        public boolean hasAnyNonNullDefault() {
            return false;
        }
    };
    public static final ParameterNonNullDefaultProvider TRUE_PROVIDER = new ParameterNonNullDefaultProvider(){

        @Override
        public boolean hasNonNullDefaultForParam(int i) {
            return true;
        }

        @Override
        public boolean hasAnyNonNullDefault() {
            return true;
        }
    };

    public boolean hasAnyNonNullDefault();

    public boolean hasNonNullDefaultForParam(int var1);

    public static class MixedProvider
    implements ParameterNonNullDefaultProvider {
        private final boolean[] result;

        public MixedProvider(boolean[] result) {
            this.result = result;
        }

        @Override
        public boolean hasNonNullDefaultForParam(int i) {
            return this.result[i];
        }

        @Override
        public boolean hasAnyNonNullDefault() {
            return true;
        }
    }
}

