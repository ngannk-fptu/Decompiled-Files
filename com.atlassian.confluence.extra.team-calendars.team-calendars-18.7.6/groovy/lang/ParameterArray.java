/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

public class ParameterArray {
    private Object parameters;

    public ParameterArray(Object data) {
        this.parameters = ParameterArray.packArray(data);
    }

    private static Object packArray(Object object) {
        if (object instanceof Object[]) {
            return (Object[])object;
        }
        return object;
    }

    public Object get() {
        return this.parameters;
    }

    public String toString() {
        if (this.parameters == null) {
            return "<null parameter>";
        }
        return this.parameters.toString();
    }
}

