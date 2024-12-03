/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.python.core.PyException
 *  org.python.util.PythonInterpreter
 */
package org.apache.batik.script.jpython;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Locale;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.script.jpython.JPythonInterpreterFactory;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

public class JPythonInterpreter
implements Interpreter {
    private PythonInterpreter interpreter = new PythonInterpreter();

    @Override
    public String[] getMimeTypes() {
        return JPythonInterpreterFactory.JPYTHON_MIMETYPES;
    }

    @Override
    public Object evaluate(Reader scriptreader) throws IOException {
        return this.evaluate(scriptreader, "");
    }

    @Override
    public Object evaluate(Reader scriptreader, String description) throws IOException {
        StringBuffer sbuffer = new StringBuffer();
        char[] buffer = new char[1024];
        int val = 0;
        while ((val = scriptreader.read(buffer)) != -1) {
            sbuffer.append(buffer, 0, val);
        }
        String str = sbuffer.toString();
        return this.evaluate(str);
    }

    @Override
    public Object evaluate(String script) {
        try {
            this.interpreter.exec(script);
        }
        catch (PyException e) {
            throw new InterpreterException((Exception)((Object)e), e.getMessage(), -1, -1);
        }
        catch (RuntimeException re) {
            throw new InterpreterException(re, re.getMessage(), -1, -1);
        }
        return null;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void bindObject(String name, Object object) {
        this.interpreter.set(name, object);
    }

    @Override
    public void setOut(Writer out) {
        this.interpreter.setOut(out);
    }

    public Locale getLocale() {
        return null;
    }

    public void setLocale(Locale locale) {
    }

    public String formatMessage(String key, Object[] args) {
        return null;
    }
}

