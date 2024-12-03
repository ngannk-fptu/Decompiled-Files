/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.collections.TreeMapConverter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.core.util.PresortedSet;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import java.lang.reflect.Field;
import java.util.AbstractList;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeSetConverter
extends CollectionConverter {
    private transient TreeMapConverter treeMapConverter;
    static /* synthetic */ Class class$java$util$SortedMap;
    static /* synthetic */ Class class$java$lang$Object;

    public TreeSetConverter(Mapper mapper) {
        super(mapper, TreeSet.class);
        this.readResolve();
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        SortedSet sortedSet = (SortedSet)source;
        this.treeMapConverter.marshalComparator(sortedSet.comparator(), writer, context);
        super.marshal(source, writer, context);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        TreeMap treeMap;
        Comparator comparator;
        TreeSet result = null;
        Comparator unmarshalledComparator = this.treeMapConverter.unmarshalComparator(reader, context, null);
        boolean inFirstElement = unmarshalledComparator instanceof Mapper.Null;
        Comparator comparator2 = comparator = inFirstElement ? null : unmarshalledComparator;
        if (Reflections.sortedMapField != null) {
            TreeSet possibleResult = comparator == null ? new TreeSet() : new TreeSet(comparator);
            Object backingMap = null;
            try {
                backingMap = Reflections.sortedMapField.get(possibleResult);
            }
            catch (IllegalAccessException e) {
                throw new ObjectAccessException("Cannot get backing map of TreeSet", e);
            }
            if (backingMap instanceof TreeMap) {
                treeMap = (TreeMap)backingMap;
                result = possibleResult;
            } else {
                treeMap = null;
            }
        } else {
            treeMap = null;
        }
        if (treeMap == null) {
            PresortedSet set = new PresortedSet(comparator);
            TreeSet treeSet = result = comparator == null ? new TreeSet() : new TreeSet(comparator);
            if (inFirstElement) {
                this.addCurrentElementToCollection(reader, context, result, set);
                reader.moveUp();
            }
            this.populateCollection(reader, context, result, set);
            if (set.size() > 0) {
                result.addAll(set);
            }
        } else {
            this.treeMapConverter.populateTreeMap(reader, context, treeMap, unmarshalledComparator);
        }
        return result;
    }

    private Object readResolve() {
        this.treeMapConverter = new TreeMapConverter(this.mapper()){

            protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map, final Map target) {
                TreeSetConverter.this.populateCollection(reader, context, new AbstractList(){

                    public boolean add(Object object) {
                        return target.put(object, Reflections.constantValue != null ? Reflections.constantValue : object) != null;
                    }

                    public Object get(int location) {
                        return null;
                    }

                    public int size() {
                        return target.size();
                    }
                });
            }

            protected void putCurrentEntryIntoMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map, Map target) {
                Object key = this.readItem(reader, context, map);
                target.put(key, key);
            }
        };
        return this;
    }

    private static class Reflections {
        private static final Field sortedMapField;
        private static final Object constantValue;

        private Reflections() {
        }

        static {
            Object value = null;
            Field field = JVM.hasOptimizedTreeSetAddAll() ? Fields.locate(class$java$util$TreeSet == null ? (class$java$util$TreeSet = TreeSetConverter.class$("java.util.TreeSet")) : class$java$util$TreeSet, class$java$util$SortedMap == null ? (class$java$util$SortedMap = TreeSetConverter.class$("java.util.SortedMap")) : class$java$util$SortedMap, false) : (sortedMapField = null);
            if (sortedMapField != null) {
                Object[] values;
                TreeSet<String> set = new TreeSet<String>();
                set.add("1");
                set.add("2");
                Map backingMap = null;
                try {
                    backingMap = (Map)sortedMapField.get(set);
                }
                catch (IllegalAccessException illegalAccessException) {
                    // empty catch block
                }
                if (backingMap != null && (values = backingMap.values().toArray())[0] == values[1]) {
                    value = values[0];
                }
            } else {
                Field valueField = Fields.locate(class$java$util$TreeSet == null ? (class$java$util$TreeSet = TreeSetConverter.class$("java.util.TreeSet")) : class$java$util$TreeSet, class$java$lang$Object == null ? (class$java$lang$Object = TreeSetConverter.class$("java.lang.Object")) : class$java$lang$Object, true);
                if (valueField != null) {
                    try {
                        value = valueField.get(null);
                    }
                    catch (IllegalAccessException illegalAccessException) {
                        // empty catch block
                    }
                }
            }
            constantValue = value;
        }
    }
}

