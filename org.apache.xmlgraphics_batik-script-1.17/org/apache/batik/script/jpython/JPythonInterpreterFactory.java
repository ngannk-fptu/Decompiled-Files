/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.script.jpython;

import java.net.URL;
import org.apache.batik.script.ImportInfo;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterFactory;
import org.apache.batik.script.jpython.JPythonInterpreter;

public class JPythonInterpreterFactory
implements InterpreterFactory {
    public static final String[] JPYTHON_MIMETYPES = new String[]{"text/python"};

    @Override
    public String[] getMimeTypes() {
        return JPYTHON_MIMETYPES;
    }

    @Override
    public Interpreter createInterpreter(URL documentURL, boolean svg12) {
        return new JPythonInterpreter();
    }

    @Override
    public Interpreter createInterpreter(URL documentURL, boolean svg12, ImportInfo imports) {
        return new JPythonInterpreter();
    }
}

