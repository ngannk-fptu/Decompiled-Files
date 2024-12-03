/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.python.core.PySystemState
 */
package freemarker.ext.jython;

import freemarker.ext.jython.JythonVersionAdapter;
import freemarker.template.utility.StringUtil;
import org.python.core.PySystemState;

class JythonVersionAdapterHolder {
    static final JythonVersionAdapter INSTANCE;

    JythonVersionAdapterHolder() {
    }

    private static RuntimeException adapterCreationException(Exception e) {
        return new RuntimeException("Unexpected exception when creating JythonVersionAdapter", e);
    }

    static {
        int version;
        try {
            version = StringUtil.versionStringToInt(PySystemState.class.getField("version").get(null).toString());
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to get Jython version: " + e);
        }
        ClassLoader cl = JythonVersionAdapter.class.getClassLoader();
        try {
            INSTANCE = version >= 2005000 ? (JythonVersionAdapter)cl.loadClass("freemarker.ext.jython._Jython25VersionAdapter").newInstance() : (version >= 2002000 ? (JythonVersionAdapter)cl.loadClass("freemarker.ext.jython._Jython22VersionAdapter").newInstance() : (JythonVersionAdapter)cl.loadClass("freemarker.ext.jython._Jython20And21VersionAdapter").newInstance());
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw JythonVersionAdapterHolder.adapterCreationException(e);
        }
    }
}

