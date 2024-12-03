/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.processors;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor;

public class QueryNodeProcessorPipeline
implements QueryNodeProcessor,
List<QueryNodeProcessor> {
    private LinkedList<QueryNodeProcessor> processors = new LinkedList();
    private QueryConfigHandler queryConfig;

    public QueryNodeProcessorPipeline() {
    }

    public QueryNodeProcessorPipeline(QueryConfigHandler queryConfigHandler) {
        this.queryConfig = queryConfigHandler;
    }

    @Override
    public QueryConfigHandler getQueryConfigHandler() {
        return this.queryConfig;
    }

    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        for (QueryNodeProcessor processor : this.processors) {
            queryTree = processor.process(queryTree);
        }
        return queryTree;
    }

    @Override
    public void setQueryConfigHandler(QueryConfigHandler queryConfigHandler) {
        this.queryConfig = queryConfigHandler;
        for (QueryNodeProcessor processor : this.processors) {
            processor.setQueryConfigHandler(this.queryConfig);
        }
    }

    @Override
    public boolean add(QueryNodeProcessor processor) {
        boolean added = this.processors.add(processor);
        if (added) {
            processor.setQueryConfigHandler(this.queryConfig);
        }
        return added;
    }

    @Override
    public void add(int index, QueryNodeProcessor processor) {
        this.processors.add(index, processor);
        processor.setQueryConfigHandler(this.queryConfig);
    }

    @Override
    public boolean addAll(Collection<? extends QueryNodeProcessor> c) {
        boolean anyAdded = this.processors.addAll(c);
        for (QueryNodeProcessor queryNodeProcessor : c) {
            queryNodeProcessor.setQueryConfigHandler(this.queryConfig);
        }
        return anyAdded;
    }

    @Override
    public boolean addAll(int index, Collection<? extends QueryNodeProcessor> c) {
        boolean anyAdded = this.processors.addAll(index, c);
        for (QueryNodeProcessor queryNodeProcessor : c) {
            queryNodeProcessor.setQueryConfigHandler(this.queryConfig);
        }
        return anyAdded;
    }

    @Override
    public void clear() {
        this.processors.clear();
    }

    @Override
    public boolean contains(Object o) {
        return this.processors.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.processors.containsAll(c);
    }

    @Override
    public QueryNodeProcessor get(int index) {
        return this.processors.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.processors.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return this.processors.isEmpty();
    }

    @Override
    public Iterator<QueryNodeProcessor> iterator() {
        return this.processors.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.processors.lastIndexOf(o);
    }

    @Override
    public ListIterator<QueryNodeProcessor> listIterator() {
        return this.processors.listIterator();
    }

    @Override
    public ListIterator<QueryNodeProcessor> listIterator(int index) {
        return this.processors.listIterator(index);
    }

    @Override
    public boolean remove(Object o) {
        return this.processors.remove(o);
    }

    @Override
    public QueryNodeProcessor remove(int index) {
        return this.processors.remove(index);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.processors.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.processors.retainAll(c);
    }

    @Override
    public QueryNodeProcessor set(int index, QueryNodeProcessor processor) {
        QueryNodeProcessor oldProcessor = this.processors.set(index, processor);
        if (oldProcessor != processor) {
            processor.setQueryConfigHandler(this.queryConfig);
        }
        return oldProcessor;
    }

    @Override
    public int size() {
        return this.processors.size();
    }

    @Override
    public List<QueryNodeProcessor> subList(int fromIndex, int toIndex) {
        return this.processors.subList(fromIndex, toIndex);
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return this.processors.toArray(array);
    }

    @Override
    public Object[] toArray() {
        return this.processors.toArray();
    }
}

