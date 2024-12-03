/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.PresortedMap;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

public class TreeMapConverter
extends MapConverter {
    private static final Comparator NULL_MARKER = new NullComparator();
    static /* synthetic */ Class class$java$util$Comparator;

    public TreeMapConverter(Mapper mapper) {
        super(mapper, TreeMap.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        SortedMap sortedMap = (SortedMap)source;
        this.marshalComparator(sortedMap.comparator(), writer, context);
        super.marshal(source, writer, context);
    }

    protected void marshalComparator(Comparator comparator, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (comparator != null) {
            writer.startNode("comparator");
            writer.addAttribute(this.mapper().aliasForSystemAttribute("class"), this.mapper().serializedClass(comparator.getClass()));
            context.convertAnother(comparator);
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        TreeMap result = Reflections.comparatorField != null ? new TreeMap() : null;
        Comparator comparator = this.unmarshalComparator(reader, context, result);
        if (result == null) {
            result = comparator == null || comparator == NULL_MARKER ? new TreeMap() : new TreeMap(comparator);
        }
        this.populateTreeMap(reader, context, result, comparator);
        return result;
    }

    protected Comparator unmarshalComparator(HierarchicalStreamReader reader, UnmarshallingContext context, TreeMap result) {
        Comparator comparator;
        if (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equals("comparator")) {
                Class comparatorClass = HierarchicalStreams.readClassType(reader, this.mapper());
                comparator = (Comparator)context.convertAnother(result, comparatorClass);
            } else if (reader.getNodeName().equals("no-comparator")) {
                comparator = null;
            } else {
                return NULL_MARKER;
            }
            reader.moveUp();
        } else {
            comparator = null;
        }
        return comparator;
    }

    protected void populateTreeMap(HierarchicalStreamReader reader, UnmarshallingContext context, TreeMap result, Comparator comparator) {
        boolean inFirstElement;
        boolean bl = inFirstElement = comparator == NULL_MARKER;
        if (inFirstElement) {
            comparator = null;
        }
        PresortedMap sortedMap = new PresortedMap(comparator != null && JVM.hasOptimizedTreeMapPutAll() ? comparator : null);
        if (inFirstElement) {
            this.putCurrentEntryIntoMap(reader, context, result, sortedMap);
            reader.moveUp();
        }
        this.populateMap(reader, context, result, sortedMap);
        try {
            if (JVM.hasOptimizedTreeMapPutAll()) {
                if (comparator != null && Reflections.comparatorField != null) {
                    Reflections.comparatorField.set(result, comparator);
                }
                result.putAll(sortedMap);
            } else if (Reflections.comparatorField != null) {
                Reflections.comparatorField.set(result, sortedMap.comparator());
                result.putAll(sortedMap);
                Reflections.comparatorField.set(result, comparator);
            } else {
                result.putAll(sortedMap);
            }
        }
        catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot set comparator of TreeMap", e);
        }
    }

    private static class Reflections {
        private static final Field comparatorField = Fields.locate(class$java$util$TreeMap == null ? (class$java$util$TreeMap = TreeMapConverter.class$("java.util.TreeMap")) : class$java$util$TreeMap, class$java$util$Comparator == null ? (class$java$util$Comparator = TreeMapConverter.class$("java.util.Comparator")) : class$java$util$Comparator, false);

        private Reflections() {
        }
    }

    private static final class NullComparator
    extends Mapper.Null
    implements Comparator {
        private NullComparator() {
        }

        public int compare(Object o1, Object o2) {
            Comparable c1 = (Comparable)o1;
            Comparable c2 = (Comparable)o2;
            return c1.compareTo(o2);
        }
    }
}

