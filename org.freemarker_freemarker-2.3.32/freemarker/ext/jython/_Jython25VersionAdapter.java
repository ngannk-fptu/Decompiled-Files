/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.python.core.PyInstance
 *  org.python.core.PyObject
 */
package freemarker.ext.jython;

import freemarker.ext.jython.JythonVersionAdapter;
import org.python.core.PyInstance;
import org.python.core.PyObject;

public class _Jython25VersionAdapter
extends JythonVersionAdapter {
    @Override
    public boolean isPyInstance(Object obj) {
        return obj instanceof PyInstance;
    }

    @Override
    public Object pyInstanceToJava(Object pyInstance) {
        return ((PyInstance)pyInstance).__tojava__(Object.class);
    }

    @Override
    public String getPythonClassName(PyObject pyObject) {
        return pyObject.getType().getName();
    }
}

