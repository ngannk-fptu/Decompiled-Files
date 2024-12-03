/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.script;

import java.net.URL;
import org.apache.batik.script.ImportInfo;
import org.apache.batik.script.Interpreter;

public interface InterpreterFactory {
    public String[] getMimeTypes();

    public Interpreter createInterpreter(URL var1, boolean var2, ImportInfo var3);

    public Interpreter createInterpreter(URL var1, boolean var2);
}

