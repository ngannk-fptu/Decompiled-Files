/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

public class TagVariableInfo {
    private final String nameGiven;
    private final String nameFromAttribute;
    private final String className;
    private final boolean declare;
    private final int scope;

    public TagVariableInfo(String nameGiven, String nameFromAttribute, String className, boolean declare, int scope) {
        this.nameGiven = nameGiven;
        this.nameFromAttribute = nameFromAttribute;
        this.className = className;
        this.declare = declare;
        this.scope = scope;
    }

    public String getNameGiven() {
        return this.nameGiven;
    }

    public String getNameFromAttribute() {
        return this.nameFromAttribute;
    }

    public String getClassName() {
        return this.className;
    }

    public boolean getDeclare() {
        return this.declare;
    }

    public int getScope() {
        return this.scope;
    }
}

