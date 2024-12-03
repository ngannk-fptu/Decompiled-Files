/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.python.core.PyException
 *  org.python.core.PyObject
 */
package freemarker.ext.jython;

import freemarker.ext.jython.JythonModel;
import freemarker.ext.jython.JythonVersionAdapterHolder;
import freemarker.ext.jython.JythonWrapper;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.python.core.PyException;
import org.python.core.PyObject;

public class JythonHashModel
extends JythonModel
implements TemplateHashModelEx {
    private static final String KEYS = "keys";
    private static final String KEYSET = "keySet";
    private static final String VALUES = "values";
    static final ModelFactory FACTORY = new ModelFactory(){

        @Override
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new JythonHashModel((PyObject)object, (JythonWrapper)wrapper);
        }
    };

    public JythonHashModel(PyObject object, JythonWrapper wrapper) {
        super(object, wrapper);
    }

    @Override
    public int size() throws TemplateModelException {
        try {
            return this.object.__len__();
        }
        catch (PyException e) {
            throw new TemplateModelException((Exception)((Object)e));
        }
    }

    @Override
    public TemplateCollectionModel keys() throws TemplateModelException {
        try {
            PyObject method = this.object.__findattr__(KEYS);
            if (method == null) {
                method = this.object.__findattr__(KEYSET);
            }
            if (method != null) {
                return (TemplateCollectionModel)this.wrapper.wrap(method.__call__());
            }
        }
        catch (PyException e) {
            throw new TemplateModelException((Exception)((Object)e));
        }
        throw new TemplateModelException("'?keys' is not supported as there is no 'keys' nor 'keySet' attribute on an instance of " + JythonVersionAdapterHolder.INSTANCE.getPythonClassName(this.object));
    }

    @Override
    public TemplateCollectionModel values() throws TemplateModelException {
        try {
            PyObject method = this.object.__findattr__(VALUES);
            if (method != null) {
                return (TemplateCollectionModel)this.wrapper.wrap(method.__call__());
            }
        }
        catch (PyException e) {
            throw new TemplateModelException((Exception)((Object)e));
        }
        throw new TemplateModelException("'?values' is not supported as there is no 'values' attribute on an instance of " + JythonVersionAdapterHolder.INSTANCE.getPythonClassName(this.object));
    }
}

