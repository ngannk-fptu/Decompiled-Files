/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Undefined;

public class ScriptRuntimeES6 {
    public static Object requireObjectCoercible(Context cx, Object val, IdFunctionObject idFuncObj) {
        if (val == null || Undefined.isUndefined(val)) {
            throw ScriptRuntime.typeErrorById("msg.called.null.or.undefined", idFuncObj.getTag(), idFuncObj.getFunctionName());
        }
        return val;
    }
}

