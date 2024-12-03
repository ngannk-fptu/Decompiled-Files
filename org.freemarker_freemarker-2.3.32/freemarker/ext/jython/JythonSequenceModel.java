/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.python.core.PyException
 *  org.python.core.PyObject
 */
package freemarker.ext.jython;

import freemarker.ext.jython.JythonModel;
import freemarker.ext.jython.JythonWrapper;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import org.python.core.PyException;
import org.python.core.PyObject;

public class JythonSequenceModel
extends JythonModel
implements TemplateSequenceModel,
TemplateCollectionModel {
    static final ModelFactory FACTORY = new ModelFactory(){

        @Override
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new JythonSequenceModel((PyObject)object, (JythonWrapper)wrapper);
        }
    };

    public JythonSequenceModel(PyObject object, JythonWrapper wrapper) {
        super(object, wrapper);
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        try {
            return this.wrapper.wrap(this.object.__finditem__(index));
        }
        catch (PyException e) {
            throw new TemplateModelException((Exception)((Object)e));
        }
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
    public TemplateModelIterator iterator() {
        return new TemplateModelIterator(){
            int i = 0;

            @Override
            public boolean hasNext() throws TemplateModelException {
                return this.i < JythonSequenceModel.this.size();
            }

            @Override
            public TemplateModel next() throws TemplateModelException {
                return JythonSequenceModel.this.get(this.i++);
            }
        };
    }
}

