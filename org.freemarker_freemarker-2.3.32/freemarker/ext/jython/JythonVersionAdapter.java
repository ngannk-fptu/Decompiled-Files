/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.python.core.PyObject
 */
package freemarker.ext.jython;

import org.python.core.PyObject;

public abstract class JythonVersionAdapter {
    public abstract boolean isPyInstance(Object var1);

    public abstract Object pyInstanceToJava(Object var1);

    public abstract String getPythonClassName(PyObject var1);
}

