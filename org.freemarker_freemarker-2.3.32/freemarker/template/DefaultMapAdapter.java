/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.MapKeyValuePairIterator;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import java.io.Serializable;
import java.util.Map;
import java.util.SortedMap;

public class DefaultMapAdapter
extends WrappingTemplateModel
implements TemplateHashModelEx2,
AdapterTemplateModel,
WrapperTemplateModel,
TemplateModelWithAPISupport,
Serializable {
    private final Map map;

    public static DefaultMapAdapter adapt(Map map, ObjectWrapperWithAPISupport wrapper) {
        return new DefaultMapAdapter(map, wrapper);
    }

    private DefaultMapAdapter(Map map, ObjectWrapper wrapper) {
        super(wrapper);
        this.map = map;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        Object val;
        block11: {
            try {
                val = this.map.get(key);
            }
            catch (ClassCastException e) {
                throw new _TemplateModelException((Throwable)e, "ClassCastException while getting Map entry with String key ", new _DelayedJQuote(key));
            }
            catch (NullPointerException e) {
                throw new _TemplateModelException((Throwable)e, "NullPointerException while getting Map entry with String key ", new _DelayedJQuote(key));
            }
            if (val == null) {
                if (key.length() == 1 && !(this.map instanceof SortedMap)) {
                    Character charKey = Character.valueOf(key.charAt(0));
                    try {
                        val = this.map.get(charKey);
                        if (val == null) {
                            TemplateModel wrappedNull = this.wrap(null);
                            if (wrappedNull == null || !this.map.containsKey(key) && !this.map.containsKey(charKey)) {
                                return null;
                            }
                            return wrappedNull;
                        }
                        break block11;
                    }
                    catch (ClassCastException e) {
                        throw new _TemplateModelException((Throwable)e, "Class casting exception while getting Map entry with Character key ", new _DelayedJQuote(charKey));
                    }
                    catch (NullPointerException e) {
                        throw new _TemplateModelException((Throwable)e, "NullPointerException while getting Map entry with Character key ", new _DelayedJQuote(charKey));
                    }
                }
                TemplateModel wrappedNull = this.wrap(null);
                if (wrappedNull == null || !this.map.containsKey(key)) {
                    return null;
                }
                return wrappedNull;
            }
        }
        return this.wrap(val);
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
        return new SimpleCollection(this.map.keySet(), this.getObjectWrapper());
    }

    @Override
    public TemplateCollectionModel values() {
        return new SimpleCollection(this.map.values(), this.getObjectWrapper());
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
        return ((ObjectWrapperWithAPISupport)this.getObjectWrapper()).wrapAsAPI(this.map);
    }
}

