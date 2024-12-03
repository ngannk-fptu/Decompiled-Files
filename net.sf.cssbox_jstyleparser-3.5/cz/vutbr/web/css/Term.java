/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

public interface Term<T>
extends Cloneable {
    public T getValue();

    public Term<T> setValue(T var1);

    public Operator getOperator();

    public Term<T> setOperator(Operator var1);

    public Term<T> shallowClone();

    public static enum Operator {
        SPACE(" "),
        SLASH("/"),
        COMMA(", ");

        private final String value;

        private Operator(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }
}

