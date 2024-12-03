/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.MapKeyValuePairIterator;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.WrappingTemplateModel;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class SimpleHash
extends WrappingTemplateModel
implements TemplateHashModelEx2,
Serializable {
    private final Map map;
    private boolean putFailed;
    private Map unwrappedMap;

    @Deprecated
    public SimpleHash() {
        this((ObjectWrapper)null);
    }

    @Deprecated
    public SimpleHash(Map map) {
        this(map, null);
    }

    public SimpleHash(ObjectWrapper wrapper) {
        super(wrapper);
        this.map = new HashMap();
    }

    public SimpleHash(Map<String, Object> directMap, ObjectWrapper wrapper, int overloadDistinction) {
        super(wrapper);
        this.map = directMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SimpleHash(Map map, ObjectWrapper wrapper) {
        super(wrapper);
        Map mapCopy;
        try {
            mapCopy = this.copyMap(map);
        }
        catch (ConcurrentModificationException cme) {
            try {
                Thread.sleep(5L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            Map map2 = map;
            synchronized (map2) {
                mapCopy = this.copyMap(map);
            }
        }
        this.map = mapCopy;
    }

    protected Map copyMap(Map map) {
        if (map instanceof HashMap) {
            return (Map)((HashMap)map).clone();
        }
        if (map instanceof SortedMap) {
            if (map instanceof TreeMap) {
                return (Map)((TreeMap)map).clone();
            }
            return new TreeMap((SortedMap)map);
        }
        return new HashMap(map);
    }

    public void put(String key, Object value) {
        this.map.put(key, value);
        this.unwrappedMap = null;
    }

    public void put(String key, boolean b) {
        this.put(key, b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE);
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        Object result;
        try {
            result = this.map.get(key);
        }
        catch (ClassCastException e) {
            throw new _TemplateModelException((Throwable)e, "ClassCastException while getting Map entry with String key ", new _DelayedJQuote(key));
        }
        catch (NullPointerException e) {
            throw new _TemplateModelException((Throwable)e, "NullPointerException while getting Map entry with String key ", new _DelayedJQuote(key));
        }
        Object putKey = null;
        if (result == null) {
            if (key.length() == 1 && !(this.map instanceof SortedMap)) {
                Character charKey = Character.valueOf(key.charAt(0));
                try {
                    result = this.map.get(charKey);
                    if (result != null || this.map.containsKey(charKey)) {
                        putKey = charKey;
                    }
                }
                catch (ClassCastException e) {
                    throw new _TemplateModelException((Throwable)e, "ClassCastException while getting Map entry with Character key ", new _DelayedJQuote(key));
                }
                catch (NullPointerException e) {
                    throw new _TemplateModelException((Throwable)e, "NullPointerException while getting Map entry with Character key ", new _DelayedJQuote(key));
                }
            }
            if (putKey == null) {
                if (!this.map.containsKey(key)) {
                    return null;
                }
                putKey = key;
            }
        } else {
            putKey = key;
        }
        if (result instanceof TemplateModel) {
            return (TemplateModel)result;
        }
        TemplateModel tm = this.wrap(result);
        if (!this.putFailed) {
            try {
                this.map.put(putKey, tm);
            }
            catch (Exception e) {
                this.putFailed = true;
            }
        }
        return tm;
    }

    public boolean containsKey(String key) {
        return this.map.containsKey(key);
    }

    public void remove(String key) {
        this.map.remove(key);
    }

    public void putAll(Map m) {
        for (Map.Entry entry : m.entrySet()) {
            this.put((String)entry.getKey(), entry.getValue());
        }
    }

    public Map toMap() throws TemplateModelException {
        if (this.unwrappedMap == null) {
            Class<?> mapClass = this.map.getClass();
            Map m = null;
            try {
                m = (Map)mapClass.newInstance();
            }
            catch (Exception e) {
                throw new TemplateModelException("Error instantiating map of type " + mapClass.getName() + "\n" + e.getMessage());
            }
            BeansWrapper bw = BeansWrapper.getDefaultInstance();
            for (Map.Entry entry : this.map.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof TemplateModel) {
                    value = bw.unwrap((TemplateModel)value);
                }
                m.put(key, value);
            }
            this.unwrappedMap = m;
        }
        return this.unwrappedMap;
    }

    public String toString() {
        return this.map.toString();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map == null || this.map.isEmpty();
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

    public SimpleHash synchronizedWrapper() {
        return new SynchronizedHash();
    }

    private class SynchronizedHash
    extends SimpleHash {
        private SynchronizedHash() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            SimpleHash simpleHash = SimpleHash.this;
            synchronized (simpleHash) {
                return SimpleHash.this.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void put(String key, Object obj) {
            SimpleHash simpleHash = SimpleHash.this;
            synchronized (simpleHash) {
                SimpleHash.this.put(key, obj);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            SimpleHash simpleHash = SimpleHash.this;
            synchronized (simpleHash) {
                return SimpleHash.this.get(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void remove(String key) {
            SimpleHash simpleHash = SimpleHash.this;
            synchronized (simpleHash) {
                SimpleHash.this.remove(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            SimpleHash simpleHash = SimpleHash.this;
            synchronized (simpleHash) {
                return SimpleHash.this.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TemplateCollectionModel keys() {
            SimpleHash simpleHash = SimpleHash.this;
            synchronized (simpleHash) {
                return SimpleHash.this.keys();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TemplateCollectionModel values() {
            SimpleHash simpleHash = SimpleHash.this;
            synchronized (simpleHash) {
                return SimpleHash.this.values();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
            SimpleHash simpleHash = SimpleHash.this;
            synchronized (simpleHash) {
                return SimpleHash.this.keyValuePairIterator();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Map toMap() throws TemplateModelException {
            SimpleHash simpleHash = SimpleHash.this;
            synchronized (simpleHash) {
                return SimpleHash.this.toMap();
            }
        }
    }
}

