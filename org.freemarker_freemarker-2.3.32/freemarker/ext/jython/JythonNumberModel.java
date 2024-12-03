/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.python.core.Py
 *  org.python.core.PyException
 *  org.python.core.PyObject
 */
package freemarker.ext.jython;

import freemarker.ext.jython.JythonModel;
import freemarker.ext.jython.JythonWrapper;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;

public class JythonNumberModel
extends JythonModel
implements TemplateNumberModel {
    static final ModelFactory FACTORY = new ModelFactory(){

        @Override
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new JythonNumberModel((PyObject)object, (JythonWrapper)wrapper);
        }
    };

    public JythonNumberModel(PyObject object, JythonWrapper wrapper) {
        super(object, wrapper);
    }

    @Override
    public Number getAsNumber() throws TemplateModelException {
        try {
            Object value = this.object.__tojava__(Number.class);
            if (value == null || value == Py.NoConversion) {
                return this.object.__float__().getValue();
            }
            return (Number)value;
        }
        catch (PyException e) {
            throw new TemplateModelException((Exception)((Object)e));
        }
    }
}

