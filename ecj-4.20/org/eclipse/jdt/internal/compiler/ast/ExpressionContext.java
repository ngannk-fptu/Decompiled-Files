/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

public enum ExpressionContext {
    ASSIGNMENT_CONTEXT{

        public String toString() {
            return "assignment context";
        }

        @Override
        public boolean definesTargetType() {
            return true;
        }
    }
    ,
    INVOCATION_CONTEXT{

        public String toString() {
            return "invocation context";
        }

        @Override
        public boolean definesTargetType() {
            return true;
        }
    }
    ,
    CASTING_CONTEXT{

        public String toString() {
            return "casting context";
        }

        @Override
        public boolean definesTargetType() {
            return false;
        }
    }
    ,
    VANILLA_CONTEXT{

        public String toString() {
            return "vanilla context";
        }

        @Override
        public boolean definesTargetType() {
            return false;
        }
    };


    private ExpressionContext() {
    }

    public abstract boolean definesTargetType();

    /* synthetic */ ExpressionContext(String string, int n, ExpressionContext expressionContext) {
        this();
    }
}

