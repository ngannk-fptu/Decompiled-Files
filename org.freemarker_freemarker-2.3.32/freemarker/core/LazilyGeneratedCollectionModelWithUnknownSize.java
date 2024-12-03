/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.LazilyGeneratedCollectionModel;
import freemarker.template.TemplateModelIterator;

final class LazilyGeneratedCollectionModelWithUnknownSize
extends LazilyGeneratedCollectionModel {
    public LazilyGeneratedCollectionModelWithUnknownSize(TemplateModelIterator iterator, boolean sequence) {
        super(iterator, sequence);
    }

    @Override
    protected LazilyGeneratedCollectionModelWithUnknownSize withIsSequenceFromFalseToTrue() {
        return new LazilyGeneratedCollectionModelWithUnknownSize(this.getIterator(), true);
    }
}

