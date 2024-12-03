/*
 * Decompiled with CFR 0.152.
 */
package groovy.text.markup;

enum IncludeType {
    template("includeGroovy"),
    escaped("includeEscaped"),
    unescaped("includeUnescaped");

    private final String methodName;

    private IncludeType(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }
}

