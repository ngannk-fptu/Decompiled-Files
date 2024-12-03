/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.LazilyGeneratedCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateModelIterator;

abstract class LazilyGeneratedCollectionModelEx
extends LazilyGeneratedCollectionModel
implements TemplateCollectionModelEx {
    LazilyGeneratedCollectionModelEx(TemplateModelIterator iterator, boolean sequence) {
        super(iterator, sequence);
    }
}

