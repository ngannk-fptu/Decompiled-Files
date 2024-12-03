/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.LazilyGeneratedCollectionModel;
import freemarker.core.LazilyGeneratedCollectionModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

final class LazilyGeneratedCollectionModelWithAlreadyKnownSize
extends LazilyGeneratedCollectionModelEx {
    private final int size;

    LazilyGeneratedCollectionModelWithAlreadyKnownSize(TemplateModelIterator iterator, int size, boolean sequence) {
        super(iterator, sequence);
        this.size = size;
    }

    @Override
    public int size() throws TemplateModelException {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    protected LazilyGeneratedCollectionModel withIsSequenceFromFalseToTrue() {
        return new LazilyGeneratedCollectionModelWithAlreadyKnownSize(this.getIterator(), this.size, true);
    }
}

