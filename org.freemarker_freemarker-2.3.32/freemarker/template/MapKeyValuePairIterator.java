/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.Iterator;
import java.util.Map;

public class MapKeyValuePairIterator
implements TemplateHashModelEx2.KeyValuePairIterator {
    private final Iterator<Map.Entry<?, ?>> entrySetIterator;
    private final ObjectWrapper objectWrapper;

    public <K, V> MapKeyValuePairIterator(Map<?, ?> map, ObjectWrapper objectWrapper) {
        this.entrySetIterator = map.entrySet().iterator();
        this.objectWrapper = objectWrapper;
    }

    @Override
    public boolean hasNext() {
        return this.entrySetIterator.hasNext();
    }

    @Override
    public TemplateHashModelEx2.KeyValuePair next() {
        final Map.Entry<?, ?> entry = this.entrySetIterator.next();
        return new TemplateHashModelEx2.KeyValuePair(){

            @Override
            public TemplateModel getKey() throws TemplateModelException {
                return MapKeyValuePairIterator.this.wrap(entry.getKey());
            }

            @Override
            public TemplateModel getValue() throws TemplateModelException {
                return MapKeyValuePairIterator.this.wrap(entry.getValue());
            }
        };
    }

    private TemplateModel wrap(Object obj) throws TemplateModelException {
        return obj instanceof TemplateModel ? (TemplateModel)obj : this.objectWrapper.wrap(obj);
    }
}

