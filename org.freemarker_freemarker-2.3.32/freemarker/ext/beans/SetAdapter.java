/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.CollectionAdapter;
import freemarker.template.TemplateCollectionModel;
import java.util.Set;

class SetAdapter
extends CollectionAdapter
implements Set {
    SetAdapter(TemplateCollectionModel model, BeansWrapper wrapper) {
        super(model, wrapper);
    }
}

