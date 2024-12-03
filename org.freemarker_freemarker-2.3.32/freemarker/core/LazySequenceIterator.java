/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;

class LazySequenceIterator
implements TemplateModelIterator {
    private final TemplateSequenceModel sequence;
    private Integer size;
    private int index = 0;

    LazySequenceIterator(TemplateSequenceModel sequence) throws TemplateModelException {
        this.sequence = sequence;
    }

    @Override
    public TemplateModel next() throws TemplateModelException {
        return this.sequence.get(this.index++);
    }

    @Override
    public boolean hasNext() {
        if (this.size == null) {
            try {
                this.size = this.sequence.size();
            }
            catch (TemplateModelException e) {
                throw new RuntimeException("Error when getting sequence size", e);
            }
        }
        return this.index < this.size;
    }
}

