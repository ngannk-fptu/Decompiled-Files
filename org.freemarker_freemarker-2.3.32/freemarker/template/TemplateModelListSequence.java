/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateSequenceModel;
import java.util.List;

public class TemplateModelListSequence
implements TemplateSequenceModel {
    private List list;

    public TemplateModelListSequence(List list) {
        this.list = list;
    }

    @Override
    public TemplateModel get(int index) {
        return (TemplateModel)this.list.get(index);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    public Object getWrappedObject() {
        return this.list;
    }
}

