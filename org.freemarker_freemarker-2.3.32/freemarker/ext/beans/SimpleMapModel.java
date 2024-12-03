/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.core.CollectionAndSequence;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.util.ModelFactory;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.MapKeyValuePairIterator;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.utility.RichObjectWrapper;
import java.util.List;
import java.util.Map;

public class SimpleMapModel
extends WrappingTemplateModel
implements TemplateHashModelEx2,
TemplateMethodModelEx,
AdapterTemplateModel,
WrapperTemplateModel,
TemplateModelWithAPISupport {
    static final ModelFactory FACTORY = new ModelFactory(){

        @Override
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new SimpleMapModel((Map)object, (BeansWrapper)wrapper);
        }
    };
    private final Map map;

    public SimpleMapModel(Map map, BeansWrapper wrapper) {
        super(wrapper);
        this.map = map;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        Character charKey;
        Object val = this.map.get(key);
        if (val == null && (key.length() == 1 ? (val = this.map.get(charKey = Character.valueOf(key.charAt(0)))) == null && !this.map.containsKey(key) && !this.map.containsKey(charKey) : !this.map.containsKey(key))) {
            return null;
        }
        return this.wrap(val);
    }

    @Override
    public Object exec(List args) throws TemplateModelException {
        Object key = ((BeansWrapper)this.getObjectWrapper()).unwrap((TemplateModel)args.get(0));
        Object value = this.map.get(key);
        if (value == null && !this.map.containsKey(key)) {
            return null;
        }
        return this.wrap(value);
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public TemplateCollectionModel keys() {
        return new CollectionAndSequence(new SimpleSequence(this.map.keySet(), this.getObjectWrapper()));
    }

    @Override
    public TemplateCollectionModel values() {
        return new CollectionAndSequence(new SimpleSequence(this.map.values(), this.getObjectWrapper()));
    }

    @Override
    public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
        return new MapKeyValuePairIterator(this.map, this.getObjectWrapper());
    }

    public Object getAdaptedObject(Class hint) {
        return this.map;
    }

    @Override
    public Object getWrappedObject() {
        return this.map;
    }

    @Override
    public TemplateModel getAPI() throws TemplateModelException {
        return ((RichObjectWrapper)this.getObjectWrapper()).wrapAsAPI(this.map);
    }
}

