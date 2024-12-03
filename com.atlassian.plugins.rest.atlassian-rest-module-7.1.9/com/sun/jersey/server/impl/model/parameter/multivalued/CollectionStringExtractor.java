/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.ws.rs.core.MultivaluedMap;

abstract class CollectionStringExtractor<V extends Collection<String>>
implements MultivaluedParameterExtractor {
    final String parameter;
    final String defaultValue;

    protected CollectionStringExtractor(String parameter, String defaultValue) {
        this.parameter = parameter;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
        return this.parameter;
    }

    @Override
    public String getDefaultStringValue() {
        return this.defaultValue;
    }

    @Override
    public Object extract(MultivaluedMap<String, String> parameters) {
        List stringList = (List)parameters.get(this.parameter);
        if (stringList != null) {
            V copy = this.getInstance();
            copy.addAll(stringList);
            return copy;
        }
        if (this.defaultValue != null) {
            V l = this.getInstance();
            l.add((String)this.defaultValue);
            return l;
        }
        return this.getInstance();
    }

    protected abstract V getInstance();

    static MultivaluedParameterExtractor getInstance(Class c, String parameter, String defaultValue) {
        if (List.class == c) {
            return new ListString(parameter, defaultValue);
        }
        if (Set.class == c) {
            return new SetString(parameter, defaultValue);
        }
        if (SortedSet.class == c) {
            return new SortedSetString(parameter, defaultValue);
        }
        throw new RuntimeException();
    }

    private static final class SortedSetString
    extends CollectionStringExtractor<SortedSet<String>> {
        public SortedSetString(String parameter, String defaultValue) {
            super(parameter, defaultValue);
        }

        @Override
        protected SortedSet<String> getInstance() {
            return new TreeSet<String>();
        }
    }

    private static final class SetString
    extends CollectionStringExtractor<Set<String>> {
        public SetString(String parameter, String defaultValue) {
            super(parameter, defaultValue);
        }

        @Override
        protected Set<String> getInstance() {
            return new HashSet<String>();
        }
    }

    private static final class ListString
    extends CollectionStringExtractor<List<String>> {
        public ListString(String parameter, String defaultValue) {
            super(parameter, defaultValue);
        }

        @Override
        protected List<String> getInstance() {
            return new ArrayList<String>();
        }
    }
}

