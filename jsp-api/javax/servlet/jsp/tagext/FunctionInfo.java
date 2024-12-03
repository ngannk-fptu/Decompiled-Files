/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

public class FunctionInfo {
    private final String name;
    private final String functionClass;
    private final String functionSignature;

    public FunctionInfo(String name, String klass, String signature) {
        this.name = name;
        this.functionClass = klass;
        this.functionSignature = signature;
    }

    public String getName() {
        return this.name;
    }

    public String getFunctionClass() {
        return this.functionClass;
    }

    public String getFunctionSignature() {
        return this.functionSignature;
    }
}

