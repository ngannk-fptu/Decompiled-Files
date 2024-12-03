/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapModel
extends StringModel
implements TemplateMethodModelEx {
    static final ModelFactory FACTORY = new ModelFactory(){

        @Override
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new MapModel((Map)object, (BeansWrapper)wrapper);
        }
    };

    public MapModel(Map map, BeansWrapper wrapper) {
        super(map, wrapper);
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object key = this.unwrap((TemplateModel)arguments.get(0));
        return this.wrap(((Map)this.object).get(key));
    }

    protected TemplateModel invokeGenericGet(Map keyMap, Class clazz, String key) throws TemplateModelException {
        Character charKey;
        Map map = (Map)this.object;
        Object val = map.get(key);
        if (val == null && (key.length() == 1 ? (val = map.get(charKey = Character.valueOf(key.charAt(0)))) == null && !map.containsKey(key) && !map.containsKey(charKey) : !map.containsKey(key))) {
            return UNKNOWN;
        }
        return this.wrap(val);
    }

    @Override
    public boolean isEmpty() {
        return ((Map)this.object).isEmpty() && super.isEmpty();
    }

    @Override
    public int size() {
        return this.keySet().size();
    }

    @Override
    protected Set keySet() {
        Set set = super.keySet();
        set.addAll(((Map)this.object).keySet());
        return set;
    }
}

