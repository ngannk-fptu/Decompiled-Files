/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.script.ImportInfo
 *  org.mozilla.javascript.Context
 *  org.mozilla.javascript.Scriptable
 *  org.mozilla.javascript.ScriptableObject
 */
package org.apache.batik.bridge;

import java.net.URL;
import org.apache.batik.bridge.GlobalWrapper;
import org.apache.batik.bridge.RhinoInterpreter;
import org.apache.batik.script.ImportInfo;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class SVG12RhinoInterpreter
extends RhinoInterpreter {
    public SVG12RhinoInterpreter(URL documentURL) {
        super(documentURL);
    }

    public SVG12RhinoInterpreter(URL documentURL, ImportInfo imports) {
        super(documentURL, imports);
    }

    @Override
    protected void defineGlobalWrapperClass(Scriptable global) {
        try {
            ScriptableObject.defineClass((Scriptable)global, GlobalWrapper.class);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    protected ScriptableObject createGlobalObject(Context ctx) {
        return new GlobalWrapper(ctx);
    }
}

