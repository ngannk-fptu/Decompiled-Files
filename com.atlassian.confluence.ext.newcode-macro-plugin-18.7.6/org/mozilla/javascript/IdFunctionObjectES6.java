/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.IdFunctionCall;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;

public class IdFunctionObjectES6
extends IdFunctionObject {
    private static final long serialVersionUID = -8023088662589035261L;
    private static final int Id_length = 1;
    private static final int Id_name = 3;
    private boolean myLength = true;
    private boolean myName = true;

    public IdFunctionObjectES6(IdFunctionCall idcall, Object tag, int id, String name, int arity, Scriptable scope) {
        super(idcall, tag, id, name, arity, scope);
    }

    @Override
    protected int findInstanceIdInfo(String s) {
        if (s.equals("length")) {
            return IdFunctionObjectES6.instanceIdInfo(3, 1);
        }
        if (s.equals("name")) {
            return IdFunctionObjectES6.instanceIdInfo(3, 3);
        }
        return super.findInstanceIdInfo(s);
    }

    @Override
    protected Object getInstanceIdValue(int id) {
        if (id == 1 && !this.myLength) {
            return NOT_FOUND;
        }
        if (id == 3 && !this.myName) {
            return NOT_FOUND;
        }
        return super.getInstanceIdValue(id);
    }

    @Override
    protected void setInstanceIdValue(int id, Object value) {
        if (id == 1 && value == NOT_FOUND) {
            this.myLength = false;
            return;
        }
        if (id == 3 && value == NOT_FOUND) {
            this.myName = false;
            return;
        }
        super.setInstanceIdValue(id, value);
    }
}

