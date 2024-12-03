/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.server.impl.model.parameter.multivalued.AbstractStringReaderExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.spi.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.ws.rs.core.MultivaluedMap;

abstract class CollectionStringReaderExtractor<V extends Collection>
extends AbstractStringReaderExtractor {
    protected CollectionStringReaderExtractor(StringReader sr, String parameter, String defaultStringValue) {
        super(sr, parameter, defaultStringValue);
    }

    @Override
    public Object extract(MultivaluedMap<String, String> parameters) {
        List stringList = (List)parameters.get(this.parameter);
        if (stringList != null) {
            V valueList = this.getInstance();
            for (String v : stringList) {
                valueList.add(this.sr.fromString(v));
            }
            return valueList;
        }
        if (this.defaultStringValue != null) {
            V valueList = this.getInstance();
            valueList.add(this.sr.fromString(this.defaultStringValue));
            return valueList;
        }
        return this.getInstance();
    }

    protected abstract V getInstance();

    static MultivaluedParameterExtractor getInstance(Class c, StringReader sr, String parameter, String defaultValueString) {
        if (List.class == c) {
            return new ListValueOf(sr, parameter, defaultValueString);
        }
        if (Set.class == c) {
            return new SetValueOf(sr, parameter, defaultValueString);
        }
        if (SortedSet.class == c) {
            return new SortedSetValueOf(sr, parameter, defaultValueString);
        }
        throw new RuntimeException();
    }

    private static final class SortedSetValueOf
    extends CollectionStringReaderExtractor<SortedSet> {
        SortedSetValueOf(StringReader sr, String parameter, String defaultValueString) {
            super(sr, parameter, defaultValueString);
        }

        @Override
        protected SortedSet getInstance() {
            return new TreeSet();
        }
    }

    private static final class SetValueOf
    extends CollectionStringReaderExtractor<Set> {
        SetValueOf(StringReader sr, String parameter, String defaultValueString) {
            super(sr, parameter, defaultValueString);
        }

        @Override
        protected Set getInstance() {
            return new HashSet();
        }
    }

    private static final class ListValueOf
    extends CollectionStringReaderExtractor<List> {
        ListValueOf(StringReader sr, String parameter, String defaultValueString) {
            super(sr, parameter, defaultValueString);
        }

        @Override
        protected List getInstance() {
            return new ArrayList();
        }
    }
}

