/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.python.core.PyJavaInstance
 *  org.python.core.PyObject
 */
package freemarker.ext.jython;

import freemarker.ext.jython.JythonVersionAdapter;
import org.python.core.PyJavaInstance;
import org.python.core.PyObject;

public class _Jython22VersionAdapter
extends JythonVersionAdapter {
    @Override
    public boolean isPyInstance(Object obj) {
        return obj instanceof PyJavaInstance;
    }

    @Override
    public Object pyInstanceToJava(Object pyInstance) {
        return ((PyJavaInstance)pyInstance).__tojava__(Object.class);
    }

    @Override
    public String getPythonClassName(PyObject pyObject) {
        return pyObject.getType().getFullName();
    }
}

