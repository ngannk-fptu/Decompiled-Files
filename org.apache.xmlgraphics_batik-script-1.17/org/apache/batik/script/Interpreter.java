/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.i18n.Localizable
 */
package org.apache.batik.script;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.script.InterpreterException;

public interface Interpreter
extends Localizable {
    public String[] getMimeTypes();

    public Object evaluate(Reader var1, String var2) throws InterpreterException, IOException;

    public Object evaluate(Reader var1) throws InterpreterException, IOException;

    public Object evaluate(String var1) throws InterpreterException;

    public void bindObject(String var1, Object var2);

    public void setOut(Writer var1);

    public void dispose();
}

