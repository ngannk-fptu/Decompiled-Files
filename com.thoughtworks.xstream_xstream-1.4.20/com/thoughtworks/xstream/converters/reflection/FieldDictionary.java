/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.reflection.FieldKey;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.FieldUtil14;
import com.thoughtworks.xstream.converters.reflection.ImmutableFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.MissingFieldException;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class FieldDictionary
implements Caching {
    private static final DictionaryEntry OBJECT_DICTIONARY_ENTRY = new DictionaryEntry(Collections.EMPTY_MAP, Collections.EMPTY_MAP);
    private transient Map dictionaryEntries;
    private transient FieldUtil fieldUtil;
    private final FieldKeySorter sorter;
    static /* synthetic */ Class class$java$lang$Object;

    public FieldDictionary() {
        this(new ImmutableFieldKeySorter());
    }

    public FieldDictionary(FieldKeySorter sorter) {
        this.sorter = sorter;
        this.init();
    }

    private void init() {
        this.dictionaryEntries = new HashMap();
        if (JVM.is15()) {
            try {
                this.fieldUtil = (FieldUtil)JVM.loadClassForName("com.thoughtworks.xstream.converters.reflection.FieldUtil15", true).newInstance();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (this.fieldUtil == null) {
            this.fieldUtil = new FieldUtil14();
        }
    }

    public Iterator serializableFieldsFor(Class cls) {
        return this.fieldsFor(cls);
    }

    public Iterator fieldsFor(Class cls) {
        return this.buildMap(cls, true).values().iterator();
    }

    public Field field(Class cls, String name, Class definedIn) {
        Field field = this.fieldOrNull(cls, name, definedIn);
        if (field == null) {
            throw new MissingFieldException(cls.getName(), name);
        }
        return field;
    }

    public Field fieldOrNull(Class cls, String name, Class definedIn) {
        Map fields = this.buildMap(cls, definedIn != null);
        Field field = (Field)fields.get(definedIn != null ? new FieldKey(name, definedIn, -1) : name);
        return field;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map buildMap(Class type, boolean tupleKeyed) {
        Class cls = type;
        DictionaryEntry lastDictionaryEntry = null;
        LinkedList superClasses = new LinkedList();
        while (lastDictionaryEntry == null) {
            lastDictionaryEntry = (class$java$lang$Object == null ? FieldDictionary.class$("java.lang.Object") : class$java$lang$Object).equals(cls) || cls == null ? OBJECT_DICTIONARY_ENTRY : this.getDictionaryEntry(cls);
            if (lastDictionaryEntry != null) continue;
            superClasses.addFirst(cls);
            cls = cls.getSuperclass();
        }
        Iterator iter = superClasses.iterator();
        while (iter.hasNext()) {
            cls = (Class)iter.next();
            DictionaryEntry newDictionaryEntry = this.buildDictionaryEntryForClass(cls, lastDictionaryEntry);
            FieldDictionary fieldDictionary = this;
            synchronized (fieldDictionary) {
                DictionaryEntry concurrentEntry = this.getDictionaryEntry(cls);
                if (concurrentEntry == null) {
                    this.dictionaryEntries.put(cls, newDictionaryEntry);
                } else {
                    newDictionaryEntry = concurrentEntry;
                }
            }
            lastDictionaryEntry = newDictionaryEntry;
        }
        return tupleKeyed ? lastDictionaryEntry.getKeyedByFieldKey() : lastDictionaryEntry.getKeyedByFieldName();
    }

    private DictionaryEntry buildDictionaryEntryForClass(Class cls, DictionaryEntry lastDictionaryEntry) {
        int i;
        HashMap<String, Field> keyedByFieldName = new HashMap<String, Field>(lastDictionaryEntry.getKeyedByFieldName());
        OrderRetainingMap keyedByFieldKey = new OrderRetainingMap(lastDictionaryEntry.getKeyedByFieldKey());
        Field[] fields = cls.getDeclaredFields();
        if (JVM.reverseFieldDefinition()) {
            i = fields.length >> 1;
            while (i-- > 0) {
                int idx = fields.length - i - 1;
                Field field = fields[i];
                fields[i] = fields[idx];
                fields[idx] = field;
            }
        }
        for (i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            if (this.fieldUtil.isSynthetic(field) && field.getName().startsWith("$jacoco")) continue;
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            FieldKey fieldKey = new FieldKey(field.getName(), field.getDeclaringClass(), i);
            Field existent = (Field)keyedByFieldName.get(field.getName());
            if (existent == null || (existent.getModifiers() & 8) != 0 || existent != null && (field.getModifiers() & 8) == 0) {
                keyedByFieldName.put(field.getName(), field);
            }
            keyedByFieldKey.put(fieldKey, field);
        }
        Map sortedFieldKeys = this.sorter.sort(cls, keyedByFieldKey);
        return new DictionaryEntry(keyedByFieldName, sortedFieldKeys);
    }

    private synchronized DictionaryEntry getDictionaryEntry(Class cls) {
        return (DictionaryEntry)this.dictionaryEntries.get(cls);
    }

    public synchronized void flushCache() {
        this.dictionaryEntries.clear();
        if (this.sorter instanceof Caching) {
            ((Caching)((Object)this.sorter)).flushCache();
        }
    }

    protected Object readResolve() {
        this.init();
        return this;
    }

    private static final class DictionaryEntry {
        private final Map keyedByFieldName;
        private final Map keyedByFieldKey;

        public DictionaryEntry(Map keyedByFieldName, Map keyedByFieldKey) {
            this.keyedByFieldName = keyedByFieldName;
            this.keyedByFieldKey = keyedByFieldKey;
        }

        public Map getKeyedByFieldName() {
            return this.keyedByFieldName;
        }

        public Map getKeyedByFieldKey() {
            return this.keyedByFieldKey;
        }
    }

    static interface FieldUtil {
        public boolean isSynthetic(Field var1);
    }
}

