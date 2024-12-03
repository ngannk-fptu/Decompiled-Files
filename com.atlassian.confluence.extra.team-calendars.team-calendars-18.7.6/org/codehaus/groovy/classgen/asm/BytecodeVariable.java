/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.Label;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;

public class BytecodeVariable {
    public static final BytecodeVariable THIS_VARIABLE = new BytecodeVariable();
    public static final BytecodeVariable SUPER_VARIABLE = new BytecodeVariable();
    private int index;
    private ClassNode type;
    private String name;
    private final int prevCurrent;
    private boolean holder;
    private Label startLabel = null;
    private Label endLabel = null;
    private boolean dynamicTyped;

    private BytecodeVariable() {
        this.dynamicTyped = true;
        this.index = 0;
        this.holder = false;
        this.prevCurrent = 0;
    }

    public BytecodeVariable(int index, ClassNode type, String name, int prevCurrent) {
        this.index = index;
        this.type = type;
        this.name = name;
        this.prevCurrent = prevCurrent;
    }

    public String getName() {
        return this.name;
    }

    public ClassNode getType() {
        return this.type;
    }

    public int getIndex() {
        return this.index;
    }

    public boolean isHolder() {
        return this.holder;
    }

    public void setHolder(boolean holder) {
        this.holder = holder;
    }

    public Label getStartLabel() {
        return this.startLabel;
    }

    public void setStartLabel(Label startLabel) {
        this.startLabel = startLabel;
    }

    public Label getEndLabel() {
        return this.endLabel;
    }

    public void setEndLabel(Label endLabel) {
        this.endLabel = endLabel;
    }

    public String toString() {
        return this.name + "(index=" + this.index + ",type=" + this.type + ",holder=" + this.holder + ")";
    }

    public void setType(ClassNode type) {
        this.type = type;
        this.dynamicTyped |= type == ClassHelper.DYNAMIC_TYPE;
    }

    public void setDynamicTyped(boolean b) {
        this.dynamicTyped = b;
    }

    public boolean isDynamicTyped() {
        return this.dynamicTyped;
    }

    public int getPrevIndex() {
        return this.prevCurrent;
    }
}

