/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description;

import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface TypeVariableSource
extends ModifierReviewable.OfAbstraction {
    @AlwaysNull
    public static final TypeVariableSource UNDEFINED = null;

    public TypeList.Generic getTypeVariables();

    @MaybeNull
    public TypeVariableSource getEnclosingSource();

    public boolean isInferrable();

    @MaybeNull
    public TypeDescription.Generic findVariable(String var1);

    public TypeDescription.Generic findExpectedVariable(String var1);

    public <T> T accept(Visitor<T> var1);

    public boolean isGenerified();

    public static abstract class AbstractBase
    extends ModifierReviewable.AbstractBase
    implements TypeVariableSource {
        @MaybeNull
        public TypeDescription.Generic findVariable(String symbol) {
            TypeList.Generic typeVariables = (TypeList.Generic)this.getTypeVariables().filter(ElementMatchers.named(symbol));
            if (typeVariables.isEmpty()) {
                TypeVariableSource enclosingSource = this.getEnclosingSource();
                return enclosingSource == null ? TypeDescription.Generic.UNDEFINED : enclosingSource.findVariable(symbol);
            }
            return (TypeDescription.Generic)typeVariables.getOnly();
        }

        public TypeDescription.Generic findExpectedVariable(String symbol) {
            TypeDescription.Generic variable = this.findVariable(symbol);
            if (variable == null) {
                throw new IllegalArgumentException("Cannot resolve " + symbol + " from " + this.toSafeString());
            }
            return variable;
        }

        protected abstract String toSafeString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Visitor<T> {
        public T onType(TypeDescription var1);

        public T onMethod(MethodDescription.InDefinedShape var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements Visitor<TypeVariableSource>
        {
            INSTANCE;


            @Override
            public TypeVariableSource onType(TypeDescription typeDescription) {
                return typeDescription;
            }

            @Override
            public TypeVariableSource onMethod(MethodDescription.InDefinedShape methodDescription) {
                return methodDescription;
            }
        }
    }
}

