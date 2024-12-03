/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.python.core.Py
 *  org.python.core.PyDictionary
 *  org.python.core.PyFloat
 *  org.python.core.PyInteger
 *  org.python.core.PyLong
 *  org.python.core.PyNone
 *  org.python.core.PyObject
 *  org.python.core.PySequence
 *  org.python.core.PyStringMap
 */
package freemarker.ext.jython;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.DateModel;
import freemarker.ext.jython.JythonHashModel;
import freemarker.ext.jython.JythonModel;
import freemarker.ext.jython.JythonNumberModel;
import freemarker.ext.jython.JythonSequenceModel;
import freemarker.ext.jython.JythonVersionAdapter;
import freemarker.ext.jython.JythonVersionAdapterHolder;
import freemarker.ext.jython.JythonWrapper;
import freemarker.ext.util.ModelCache;
import freemarker.template.TemplateModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyNone;
import org.python.core.PyObject;
import org.python.core.PySequence;
import org.python.core.PyStringMap;

class JythonModelCache
extends ModelCache {
    private final JythonWrapper wrapper;

    JythonModelCache(JythonWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    protected boolean isCacheable(Object object) {
        return true;
    }

    @Override
    protected TemplateModel create(Object obj) {
        boolean asHash = false;
        boolean asSequence = false;
        JythonVersionAdapter versionAdapter = JythonVersionAdapterHolder.INSTANCE;
        if (versionAdapter.isPyInstance(obj)) {
            Object jobj = versionAdapter.pyInstanceToJava(obj);
            if (jobj instanceof TemplateModel) {
                return (TemplateModel)jobj;
            }
            if (jobj instanceof Map) {
                asHash = true;
            }
            if (jobj instanceof Date) {
                return new DateModel((Date)jobj, BeansWrapper.getDefaultInstance());
            }
            if (jobj instanceof Collection) {
                asSequence = true;
                if (!(jobj instanceof List)) {
                    obj = new ArrayList((Collection)jobj);
                }
            }
        }
        if (!(obj instanceof PyObject)) {
            obj = Py.java2py((Object)obj);
        }
        if (asHash || obj instanceof PyDictionary || obj instanceof PyStringMap) {
            return JythonHashModel.FACTORY.create(obj, this.wrapper);
        }
        if (asSequence || obj instanceof PySequence) {
            return JythonSequenceModel.FACTORY.create(obj, this.wrapper);
        }
        if (obj instanceof PyInteger || obj instanceof PyLong || obj instanceof PyFloat) {
            return JythonNumberModel.FACTORY.create(obj, this.wrapper);
        }
        if (obj instanceof PyNone) {
            return null;
        }
        return JythonModel.FACTORY.create(obj, this.wrapper);
    }
}

