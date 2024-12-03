/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import java.util.List;
import org.xhtmlrenderer.util.ArrayUtil;

public class FormFieldState {
    private String _value = "";
    private boolean _checked = false;
    private int[] _selected = null;

    private FormFieldState() {
    }

    public String getValue() {
        return this._value;
    }

    public boolean isChecked() {
        return this._checked;
    }

    public int[] getSelectedIndices() {
        return ArrayUtil.cloneOrEmpty(this._selected);
    }

    public static FormFieldState fromString(String s) {
        FormFieldState stateObject = new FormFieldState();
        stateObject._value = s;
        return stateObject;
    }

    public static FormFieldState fromBoolean(boolean b) {
        FormFieldState stateObject = new FormFieldState();
        stateObject._checked = b;
        return stateObject;
    }

    public static FormFieldState fromList(List list) {
        FormFieldState stateObject = new FormFieldState();
        int[] indices = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            indices[i] = (Integer)list.get(i);
        }
        stateObject._selected = indices;
        return stateObject;
    }
}

