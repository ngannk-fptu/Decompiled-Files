/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mozilla.javascript.Context
 *  org.mozilla.javascript.Scriptable
 *  org.mozilla.javascript.WrapFactory
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.RhinoInterpreter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;
import org.w3c.dom.events.EventTarget;

class BatikWrapFactory
extends WrapFactory {
    private RhinoInterpreter interpreter;

    public BatikWrapFactory(RhinoInterpreter interp) {
        this.interpreter = interp;
        this.setJavaPrimitiveWrap(false);
    }

    public Object wrap(Context ctx, Scriptable scope, Object obj, Class staticType) {
        if (obj instanceof EventTarget) {
            return this.interpreter.buildEventTargetWrapper((EventTarget)obj);
        }
        return super.wrap(ctx, scope, obj, staticType);
    }
}

