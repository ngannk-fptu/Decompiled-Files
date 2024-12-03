/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.script.ImportInfo
 *  org.apache.batik.script.Interpreter
 *  org.apache.batik.script.InterpreterFactory
 */
package org.apache.batik.bridge;

import java.net.URL;
import org.apache.batik.bridge.RhinoInterpreter;
import org.apache.batik.bridge.SVG12RhinoInterpreter;
import org.apache.batik.script.ImportInfo;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterFactory;

public class RhinoInterpreterFactory
implements InterpreterFactory {
    public static final String[] RHINO_MIMETYPES = new String[]{"application/ecmascript", "application/javascript", "text/ecmascript", "text/javascript"};

    public String[] getMimeTypes() {
        return RHINO_MIMETYPES;
    }

    public Interpreter createInterpreter(URL documentURL, boolean svg12) {
        return this.createInterpreter(documentURL, svg12, null);
    }

    public Interpreter createInterpreter(URL documentURL, boolean svg12, ImportInfo imports) {
        if (svg12) {
            return new SVG12RhinoInterpreter(documentURL, imports);
        }
        return new RhinoInterpreter(documentURL, imports);
    }
}

