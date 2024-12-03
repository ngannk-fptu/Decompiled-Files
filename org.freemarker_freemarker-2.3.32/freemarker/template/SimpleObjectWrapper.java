/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;

public class SimpleObjectWrapper
extends DefaultObjectWrapper {
    static final SimpleObjectWrapper instance = new SimpleObjectWrapper();

    @Deprecated
    public SimpleObjectWrapper() {
    }

    public SimpleObjectWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    @Override
    protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
        throw new TemplateModelException(this.getClass().getName() + " deliberately won't wrap this type: " + obj.getClass().getName());
    }

    @Override
    public TemplateHashModel wrapAsAPI(Object obj) throws TemplateModelException {
        throw new TemplateModelException(this.getClass().getName() + " deliberately doesn't allow ?api.");
    }
}

