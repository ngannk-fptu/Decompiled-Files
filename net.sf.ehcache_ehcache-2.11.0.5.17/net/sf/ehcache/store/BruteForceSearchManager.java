/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.ConfigurationHelper;
import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.aggregator.AggregatorInstance;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.AttributeExtractorException;
import net.sf.ehcache.search.attribute.AttributeType;
import net.sf.ehcache.search.attribute.DynamicAttributesExtractor;
import net.sf.ehcache.search.expression.BaseCriteria;
import net.sf.ehcache.search.expression.Criteria;
import net.sf.ehcache.search.impl.AggregateOnlyResult;
import net.sf.ehcache.search.impl.BaseResult;
import net.sf.ehcache.search.impl.DynamicSearchChecker;
import net.sf.ehcache.search.impl.GroupedResultImpl;
import net.sf.ehcache.search.impl.OrderComparator;
import net.sf.ehcache.search.impl.ResultImpl;
import net.sf.ehcache.search.impl.ResultsImpl;
import net.sf.ehcache.search.impl.SearchManager;
import net.sf.ehcache.store.BruteForceSource;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.transaction.SoftLockID;

public class BruteForceSearchManager
implements SearchManager {
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private final Set<Attribute> searchAttributes = new CopyOnWriteArraySet<Attribute>();
    private final Ehcache cache;
    private BruteForceSource bruteForceSource;

    public BruteForceSearchManager(Ehcache cache) {
        this.cache = cache;
    }

    @Override
    public Results executeQuery(StoreQuery query, Map<String, AttributeExtractor> extractors, DynamicAttributesExtractor dynIndexer) {
        List<BaseResult> output;
        Criteria c = query.getCriteria();
        List aggregators = query.getAggregatorInstances();
        Set<Attribute<?>> groupByAttributes = query.groupByAttributes();
        boolean isGroupBy = !groupByAttributes.isEmpty();
        boolean includeResults = query.requestsKeys() || query.requestsValues() || !query.requestedAttributes().isEmpty() || isGroupBy;
        boolean hasOrder = !query.getOrdering().isEmpty();
        HashMap<HashSet<Object>, ResultHolder> groupByResults = new HashMap<HashSet<Object>, ResultHolder>();
        HashMap groupByAggregators = new HashMap();
        LinkedList<Element> matches = new LinkedList<Element>();
        HashMap<Object, Map<String, AttributeExtractor>> eltExtractors = new HashMap<Object, Map<String, AttributeExtractor>>();
        for (Element element : this.bruteForceSource.elements()) {
            Map<String, AttributeExtractor> extractorSuperset = this.getCombinedExtractors(extractors, dynIndexer, element);
            eltExtractors.put(element.getObjectKey(), extractorSuperset);
            if (!c.execute(element, extractorSuperset)) continue;
            if (!isGroupBy && !hasOrder && query.maxResults() >= 0 && matches.size() == query.maxResults()) break;
            matches.add(element);
        }
        List<ResultHolder> results = isGroupBy ? groupByResults.values() : new ArrayList();
        boolean anyMatches = !matches.isEmpty();
        OrderComparator<BaseResult> comp = new OrderComparator<BaseResult>(query.getOrdering());
        for (Element element : matches) {
            Object aggr2;
            Map extractorSuperset = (Map)eltExtractors.get(element.getObjectKey());
            ArrayList<Object> resultAggs = new ArrayList<Object>(aggregators.size());
            for (AggregatorInstance<?> agg : aggregators) {
                Attribute<?> aggrAttr = agg.getAttribute();
                Object val = aggrAttr != null ? BaseCriteria.getExtractor(aggrAttr.getAttributeName(), extractorSuperset).attributeFor(element, aggrAttr.getAttributeName()) : null;
                resultAggs.add(val);
            }
            Map<String, Object> attributes = this.getAttributeValues(query.requestedAttributes(), extractorSuperset, element);
            Object[] sortAttributes = this.getSortAttributes(query, extractorSuperset, element);
            if (!isGroupBy) {
                results.add(new ResultHolder(new ResultImpl(element.getObjectKey(), element.getObjectValue(), query, attributes, sortAttributes), resultAggs, comp));
                continue;
            }
            Map<String, Object> groupByValues = this.getAttributeValues(groupByAttributes, extractorSuperset, element);
            HashSet<Object> groupId = new HashSet<Object>(groupByValues.values());
            ArrayList groupAggrs = (ArrayList)groupByAggregators.get(groupId);
            if (groupAggrs == null) {
                groupAggrs = new ArrayList(aggregators.size());
                for (Object aggr2 : aggregators) {
                    groupAggrs.add(aggr2.createClone());
                }
                groupByAggregators.put(groupId, groupAggrs);
            }
            int i = 0;
            aggr2 = groupAggrs.iterator();
            while (aggr2.hasNext()) {
                AggregatorInstance inst = (AggregatorInstance)aggr2.next();
                inst.accept(resultAggs.get(i++));
            }
            ResultHolder group = (ResultHolder)groupByResults.get(groupId);
            if (group != null) continue;
            group = new ResultHolder(new GroupedResultImpl(query, attributes, sortAttributes, Collections.emptyList(), groupByValues), Collections.emptyList(), comp);
            groupByResults.put(groupId, group);
        }
        if (hasOrder || isGroupBy) {
            int max;
            if (isGroupBy) {
                results = new ArrayList(results);
            }
            if (hasOrder) {
                Collections.sort((List)results);
            }
            if ((max = query.maxResults()) >= 0 && results.size() > max) {
                results = ((List)results).subList(0, max);
            }
        }
        if (!aggregators.isEmpty()) {
            for (ResultHolder rh : results) {
                if (isGroupBy) {
                    GroupedResultImpl group = (GroupedResultImpl)rh.result;
                    HashSet<Object> groupId = new HashSet<Object>(group.getGroupByValues().values());
                    aggregators = (List)groupByAggregators.get(groupId);
                    this.setResultAggregators(aggregators, group);
                    continue;
                }
                int i = 0;
                for (Object val : rh.aggregatorInputs) {
                    ((AggregatorInstance)aggregators.get(i++)).accept(val);
                }
            }
            if (includeResults && !isGroupBy) {
                for (ResultHolder rh : results) {
                    this.setResultAggregators(aggregators, rh.result);
                }
            }
        }
        if (!isGroupBy && anyMatches && !includeResults && !aggregators.isEmpty()) {
            AggregateOnlyResult aggOnly = new AggregateOnlyResult(query);
            this.setResultAggregators(aggregators, aggOnly);
            output = Collections.singletonList(aggOnly);
        } else {
            output = new ArrayList<BaseResult>(results.size());
            for (ResultHolder rh : results) {
                output.add(rh.result);
            }
        }
        return new ResultsImpl(output, query.requestsKeys(), query.requestsValues(), !query.requestedAttributes().isEmpty(), anyMatches && !aggregators.isEmpty());
    }

    private void setResultAggregators(List<AggregatorInstance<?>> aggregators, BaseResult result) {
        ArrayList<Object> aggregateResults = new ArrayList<Object>();
        for (AggregatorInstance<?> aggregator : aggregators) {
            aggregateResults.add(aggregator.aggregateResult());
        }
        if (!aggregateResults.isEmpty()) {
            result.setAggregateResults(aggregateResults);
        }
    }

    private Map<String, Object> getAttributeValues(Set<Attribute<?>> attributes, Map<String, AttributeExtractor> extractors, Element element) {
        Map<String, Object> values;
        if (attributes.isEmpty()) {
            values = Collections.emptyMap();
        } else {
            values = new HashMap();
            for (Attribute<?> attribute : attributes) {
                String name = attribute.getAttributeName();
                values.put(name, BaseCriteria.getExtractor(name, extractors).attributeFor(element, name));
            }
        }
        return values;
    }

    private Map<String, AttributeExtractor> getCombinedExtractors(Map<String, AttributeExtractor> configExtractors, DynamicAttributesExtractor dynIndexer, Element element) {
        if (dynIndexer != null) {
            HashMap<String, AttributeExtractor> combinedExtractors = new HashMap<String, AttributeExtractor>();
            combinedExtractors.putAll(configExtractors);
            Map<String, ? extends Object> dynamic = DynamicSearchChecker.getSearchAttributes(element, configExtractors.keySet(), dynIndexer);
            for (final Map.Entry<String, ? extends Object> entry : dynamic.entrySet()) {
                AttributeExtractor old = combinedExtractors.put(entry.getKey(), new AttributeExtractor(){

                    @Override
                    public Object attributeFor(Element element, String attributeName) throws AttributeExtractorException {
                        if (!attributeName.equals(entry.getKey())) {
                            throw new AttributeExtractorException(String.format("Expected attribute name %s but got %s", entry.getKey(), attributeName));
                        }
                        return entry.getValue();
                    }
                });
                if (old == null) continue;
                throw new AttributeExtractorException(String.format("Attribute name %s already used by configured extractors", entry.getKey()));
            }
            return combinedExtractors;
        }
        return configExtractors;
    }

    private Object[] getSortAttributes(StoreQuery query, Map<String, AttributeExtractor> extractors, Element element) {
        Object[] sortAttributes;
        List<StoreQuery.Ordering> orderings = query.getOrdering();
        if (orderings.isEmpty()) {
            sortAttributes = EMPTY_OBJECT_ARRAY;
        } else {
            sortAttributes = new Object[orderings.size()];
            for (int i = 0; i < sortAttributes.length; ++i) {
                String name = orderings.get(i).getAttribute().getAttributeName();
                sortAttributes[i] = BaseCriteria.getExtractor(name, extractors).attributeFor(element, name);
            }
        }
        return sortAttributes;
    }

    @Override
    public void clear(String cacheName, int segmentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(String cacheName, int segmentId, Element element, byte[] key, Map<String, AttributeExtractor> extractors, DynamicAttributesExtractor dynamicIndexer) {
        SoftLockID sl;
        if (extractors.isEmpty() && dynamicIndexer == null) {
            return;
        }
        boolean isXa = element.getObjectValue() instanceof SoftLockID;
        if (isXa && (element = (sl = (SoftLockID)element.getObjectValue()).getOldElement()) == null) {
            return;
        }
        element = this.bruteForceSource.transformForIndexing(element);
        Map<String, ? extends Object> dynAttrs = DynamicSearchChecker.getSearchAttributes(element, extractors.keySet(), dynamicIndexer);
        HashSet attrs = new HashSet(dynAttrs.size());
        for (Map.Entry<String, ? extends Object> attr : dynAttrs.entrySet()) {
            if (!AttributeType.isSupportedType(attr.getValue())) {
                throw new CacheException(String.format("Unsupported attribute type specified %s for dynamically extracted attribute %s", attr.getClass().getName(), attr.getKey()));
            }
            attrs.add(new Attribute(attr.getKey()));
        }
        Searchable config = this.bruteForceSource.getSearchable();
        if (config == null) {
            return;
        }
        for (Map.Entry<String, AttributeExtractor> entry : extractors.entrySet()) {
            String schemaTypeName;
            String name = entry.getKey();
            SearchAttribute sa = config.getSearchAttributes().get(name);
            Class<?> c = ConfigurationHelper.getSearchAttributeType(sa, this.cache.getCacheConfiguration().getClassLoader());
            if (c == null) continue;
            AttributeExtractor extractor = entry.getValue();
            Object av = extractor.attributeFor(element, name);
            AttributeType schemaType = AttributeType.typeFor(c);
            AttributeType type = AttributeType.typeFor(name, av);
            String typeName = AttributeType.ENUM == type ? ((Enum)av).getDeclaringClass().getName() : type.name();
            if (typeName.equals(schemaTypeName = c.isEnum() ? c.getName() : schemaType.name())) continue;
            throw new SearchException(String.format("Expecting a %s value for attribute [%s] but was %s", schemaTypeName, name, typeName));
        }
        this.searchAttributes.addAll(attrs);
    }

    @Override
    public void remove(String cacheName, Object key, int segmentId, boolean isRemoval) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Attribute> getSearchAttributes(String cacheName) {
        return this.searchAttributes;
    }

    public void setBruteForceSource(BruteForceSource bruteForceSource) {
        this.bruteForceSource = bruteForceSource;
    }

    void addSearchAttributes(Set<Attribute<?>> attributeSet) {
        this.searchAttributes.addAll(attributeSet);
    }

    private static final class ResultHolder
    implements Comparable<ResultHolder> {
        private final BaseResult result;
        private final List<Object> aggregatorInputs;
        private final OrderComparator<BaseResult> comp;

        private ResultHolder(BaseResult res, List<Object> values, OrderComparator<BaseResult> cmp) {
            this.result = res;
            this.aggregatorInputs = values;
            this.comp = cmp;
        }

        @Override
        public int compareTo(ResultHolder other) {
            return this.comp.compare(this.result, other.result);
        }
    }
}

