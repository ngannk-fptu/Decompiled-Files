/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public abstract class WrappingTemplateModel {
    @Deprecated
    private static ObjectWrapper defaultObjectWrapper = DefaultObjectWrapper.instance;
    private ObjectWrapper objectWrapper;

    @Deprecated
    public static void setDefaultObjectWrapper(ObjectWrapper objectWrapper) {
        defaultObjectWrapper = objectWrapper;
    }

    @Deprecated
    public static ObjectWrapper getDefaultObjectWrapper() {
        return defaultObjectWrapper;
    }

    @Deprecated
    protected WrappingTemplateModel() {
        this(defaultObjectWrapper);
    }

    protected WrappingTemplateModel(ObjectWrapper objectWrapper) {
        ObjectWrapper objectWrapper2 = this.objectWrapper = objectWrapper != null ? objectWrapper : defaultObjectWrapper;
        if (this.objectWrapper == null) {
            this.objectWrapper = defaultObjectWrapper = new DefaultObjectWrapper();
        }
    }

    public ObjectWrapper getObjectWrapper() {
        return this.objectWrapper;
    }

    public void setObjectWrapper(ObjectWrapper objectWrapper) {
        this.objectWrapper = objectWrapper;
    }

    protected final TemplateModel wrap(Object obj) throws TemplateModelException {
        return this.objectWrapper.wrap(obj);
    }
}

