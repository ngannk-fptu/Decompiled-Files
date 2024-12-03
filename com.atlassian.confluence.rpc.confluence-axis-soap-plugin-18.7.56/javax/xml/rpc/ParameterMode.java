/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc;

public class ParameterMode {
    private final String mode;
    public static final ParameterMode IN = new ParameterMode("IN");
    public static final ParameterMode INOUT = new ParameterMode("INOUT");
    public static final ParameterMode OUT = new ParameterMode("OUT");

    private ParameterMode(String mode) {
        this.mode = mode;
    }

    public String toString() {
        return this.mode;
    }
}

