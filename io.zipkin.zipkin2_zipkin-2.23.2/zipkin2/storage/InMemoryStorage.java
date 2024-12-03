/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import zipkin2.Call;
import zipkin2.Callback;
import zipkin2.DependencyLink;
import zipkin2.Span;
import zipkin2.internal.DependencyLinker;
import zipkin2.storage.AutocompleteTags;
import zipkin2.storage.QueryRequest;
import zipkin2.storage.ServiceAndSpanNames;
import zipkin2.storage.SpanConsumer;
import zipkin2.storage.SpanStore;
import zipkin2.storage.StorageComponent;
import zipkin2.storage.Traces;

public final class InMemoryStorage
extends StorageComponent
implements SpanStore,
SpanConsumer,
AutocompleteTags,
ServiceAndSpanNames,
Traces {
    private final SortedMultimap<TraceIdTimestamp, Span> spansByTraceIdTimestamp = new SortedMultimap<TraceIdTimestamp, Span>(TIMESTAMP_DESCENDING){

        @Override
        Collection<Span> valueContainer() {
            return new LinkedHashSet<Span>();
        }
    };
    private final SortedMultimap<String, TraceIdTimestamp> traceIdToTraceIdTimestamps = new SortedMultimap<String, TraceIdTimestamp>(STRING_COMPARATOR){

        @Override
        Collection<TraceIdTimestamp> valueContainer() {
            return new LinkedHashSet<TraceIdTimestamp>();
        }
    };
    private final ServiceNameToTraceIds serviceToTraceIds = new ServiceNameToTraceIds();
    private final SortedMultimap<String, String> serviceToSpanNames = new SortedMultimap<String, String>(STRING_COMPARATOR){

        @Override
        Collection<String> valueContainer() {
            return new LinkedHashSet<String>();
        }
    };
    private final SortedMultimap<String, String> serviceToRemoteServiceNames = new SortedMultimap<String, String>(STRING_COMPARATOR){

        @Override
        Collection<String> valueContainer() {
            return new LinkedHashSet<String>();
        }
    };
    private final SortedMultimap<String, String> autocompleteTags = new SortedMultimap<String, String>(STRING_COMPARATOR){

        @Override
        Collection<String> valueContainer() {
            return new LinkedHashSet<String>();
        }
    };
    final boolean strictTraceId;
    final boolean searchEnabled;
    final int maxSpanCount;
    final Call<List<String>> autocompleteKeysCall;
    final Set<String> autocompleteKeys;
    final AtomicInteger acceptedSpanCount = new AtomicInteger();
    static final Comparator<String> STRING_COMPARATOR = new Comparator<String>(){

        @Override
        public int compare(String left, String right) {
            if (left == null) {
                return -1;
            }
            return left.compareTo(right);
        }

        public String toString() {
            return "String::compareTo";
        }
    };
    static final Comparator<TraceIdTimestamp> TIMESTAMP_DESCENDING = new Comparator<TraceIdTimestamp>(){

        @Override
        public int compare(TraceIdTimestamp left, TraceIdTimestamp right) {
            int result;
            long x = left.timestamp;
            long y = right.timestamp;
            int n = x < y ? -1 : (result = x == y ? 0 : 1);
            if (result != 0) {
                return -result;
            }
            return right.lowTraceId.compareTo(left.lowTraceId);
        }

        public String toString() {
            return "TimestampDescending{}";
        }
    };

    public static Builder newBuilder() {
        return new Builder();
    }

    InMemoryStorage(Builder builder) {
        this.strictTraceId = builder.strictTraceId;
        this.searchEnabled = builder.searchEnabled;
        this.maxSpanCount = builder.maxSpanCount;
        this.autocompleteKeysCall = Call.create(builder.autocompleteKeys);
        this.autocompleteKeys = new LinkedHashSet<String>(builder.autocompleteKeys);
    }

    public int acceptedSpanCount() {
        return this.acceptedSpanCount.get();
    }

    public synchronized void clear() {
        this.acceptedSpanCount.set(0);
        this.traceIdToTraceIdTimestamps.clear();
        this.spansByTraceIdTimestamp.clear();
        this.serviceToTraceIds.clear();
        this.serviceToRemoteServiceNames.clear();
        this.serviceToSpanNames.clear();
        this.autocompleteTags.clear();
    }

    @Override
    public Call<Void> accept(List<Span> spans) {
        return new StoreSpansCall(spans);
    }

    synchronized void doAccept(List<Span> spans) {
        int delta = spans.size();
        this.acceptedSpanCount.addAndGet(delta);
        int spansToRecover = this.spansByTraceIdTimestamp.size() + delta - this.maxSpanCount;
        this.evictToRecoverSpans(spansToRecover);
        for (Span span : spans) {
            long timestamp = span.timestampAsLong() / 1000L;
            String lowTraceId = InMemoryStorage.lowTraceId(span.traceId());
            TraceIdTimestamp traceIdTimeStamp = new TraceIdTimestamp(lowTraceId, timestamp);
            this.spansByTraceIdTimestamp.put(traceIdTimeStamp, span);
            this.traceIdToTraceIdTimestamps.put(lowTraceId, traceIdTimeStamp);
            if (!this.searchEnabled) continue;
            String serviceName = span.localServiceName();
            if (serviceName != null) {
                String spanName;
                this.serviceToTraceIds.put(serviceName, lowTraceId);
                String remoteServiceName = span.remoteServiceName();
                if (remoteServiceName != null) {
                    this.serviceToRemoteServiceNames.put(serviceName, remoteServiceName);
                }
                if ((spanName = span.name()) != null) {
                    this.serviceToSpanNames.put(serviceName, spanName);
                }
            }
            for (Map.Entry<String, String> tag : span.tags().entrySet()) {
                if (!this.autocompleteKeys.contains(tag.getKey())) continue;
                this.autocompleteTags.put(tag.getKey(), tag.getValue());
            }
        }
    }

    int evictToRecoverSpans(int spansToRecover) {
        int spansEvicted = 0;
        while (spansToRecover > 0) {
            int spansInOldestTrace = this.deleteOldestTrace();
            spansToRecover -= spansInOldestTrace;
            spansEvicted += spansInOldestTrace;
        }
        return spansEvicted;
    }

    private int deleteOldestTrace() {
        int spansEvicted = 0;
        String lowTraceId = ((TraceIdTimestamp)this.spansByTraceIdTimestamp.delegate.lastKey()).lowTraceId;
        Collection<TraceIdTimestamp> traceIdTimeStamps = this.traceIdToTraceIdTimestamps.remove(lowTraceId);
        for (TraceIdTimestamp traceIdTimeStamp : traceIdTimeStamps) {
            Collection<Span> spans = this.spansByTraceIdTimestamp.remove(traceIdTimeStamp);
            spansEvicted += spans.size();
        }
        if (this.searchEnabled) {
            for (String orphanedService : this.serviceToTraceIds.removeServiceIfTraceId(lowTraceId)) {
                this.serviceToRemoteServiceNames.remove(orphanedService);
                this.serviceToSpanNames.remove(orphanedService);
            }
        }
        return spansEvicted;
    }

    @Override
    public Call<List<List<Span>>> getTraces(QueryRequest request) {
        return this.getTraces(request, this.strictTraceId);
    }

    synchronized Call<List<List<Span>>> getTraces(QueryRequest request, boolean strictTraceId) {
        Set<String> lowTraceIdsInRange = this.traceIdsDescendingByTimestamp(request);
        if (lowTraceIdsInRange.isEmpty()) {
            return Call.emptyList();
        }
        ArrayList<List<Span>> result = new ArrayList<List<Span>>();
        Iterator<String> lowTraceId = lowTraceIdsInRange.iterator();
        while (lowTraceId.hasNext() && result.size() < request.limit()) {
            List<Span> next = this.spansByTraceId(lowTraceId.next());
            if (!request.test(next)) continue;
            if (!strictTraceId) {
                result.add(next);
                continue;
            }
            for (List<Span> strictTrace : InMemoryStorage.strictByTraceId(next)) {
                if (!request.test(strictTrace)) continue;
                result.add(strictTrace);
            }
        }
        return Call.create(result);
    }

    static Collection<List<Span>> strictByTraceId(List<Span> next) {
        LinkedHashMap groupedByTraceId = new LinkedHashMap();
        for (Span span : next) {
            String traceId = span.traceId();
            if (!groupedByTraceId.containsKey(traceId)) {
                groupedByTraceId.put(traceId, new ArrayList());
            }
            ((List)groupedByTraceId.get(traceId)).add(span);
        }
        return groupedByTraceId.values();
    }

    public synchronized List<List<Span>> getTraces() {
        ArrayList<List<Span>> result = new ArrayList<List<Span>>();
        for (String lowTraceId : this.traceIdToTraceIdTimestamps.keySet()) {
            List<Span> sameTraceId = this.spansByTraceId(lowTraceId);
            if (this.strictTraceId) {
                result.addAll(InMemoryStorage.strictByTraceId(sameTraceId));
                continue;
            }
            result.add(sameTraceId);
        }
        return result;
    }

    public synchronized List<DependencyLink> getDependencies() {
        return this.getDependencyLinks(this.traceIdToTraceIdTimestamps.keySet());
    }

    Set<String> traceIdsDescendingByTimestamp(QueryRequest request) {
        Collection<TraceIdTimestamp> traceIdTimestamps;
        if (!this.searchEnabled) {
            return Collections.emptySet();
        }
        Collection<TraceIdTimestamp> collection = traceIdTimestamps = request.serviceName() != null ? this.traceIdTimestampsByServiceName(request.serviceName()) : this.spansByTraceIdTimestamp.keySet();
        if (traceIdTimestamps == null || traceIdTimestamps.isEmpty()) {
            return Collections.emptySet();
        }
        return InMemoryStorage.lowTraceIdsInRange(traceIdTimestamps, request.endTs, request.lookback);
    }

    static Set<String> lowTraceIdsInRange(Collection<TraceIdTimestamp> descendingByTimestamp, long endTs, long lookback) {
        long beginTs = endTs - lookback;
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (TraceIdTimestamp traceIdTimestamp : descendingByTimestamp) {
            if (traceIdTimestamp.timestamp < beginTs || traceIdTimestamp.timestamp > endTs) continue;
            result.add(traceIdTimestamp.lowTraceId);
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public synchronized Call<List<Span>> getTrace(String traceId) {
        List<Span> spans = this.spansByTraceId(InMemoryStorage.lowTraceId(traceId = Span.normalizeTraceId(traceId)));
        if (spans.isEmpty()) {
            return Call.emptyList();
        }
        if (!this.strictTraceId) {
            return Call.create(spans);
        }
        ArrayList<Span> filtered = new ArrayList<Span>(spans);
        Iterator iterator = filtered.iterator();
        while (iterator.hasNext()) {
            if (((Span)iterator.next()).traceId().equals(traceId)) continue;
            iterator.remove();
        }
        return Call.create(filtered);
    }

    @Override
    public synchronized Call<List<List<Span>>> getTraces(Iterable<String> traceIds) {
        LinkedHashSet<String> normalized = new LinkedHashSet<String>();
        for (String string : traceIds) {
            normalized.add(Span.normalizeTraceId(string));
        }
        LinkedHashSet<String> lower64Bit = new LinkedHashSet<String>();
        for (String traceId : normalized) {
            lower64Bit.add(InMemoryStorage.lowTraceId(traceId));
        }
        ArrayList<List<Span>> arrayList = new ArrayList<List<Span>>();
        for (String lowTraceId : lower64Bit) {
            List<Span> sameTraceId = this.spansByTraceId(lowTraceId);
            if (this.strictTraceId) {
                for (List<Span> trace : InMemoryStorage.strictByTraceId(sameTraceId)) {
                    if (!normalized.contains(trace.get(0).traceId())) continue;
                    arrayList.add(trace);
                }
                continue;
            }
            arrayList.add(sameTraceId);
        }
        return Call.create(arrayList);
    }

    @Override
    public synchronized Call<List<String>> getServiceNames() {
        if (!this.searchEnabled) {
            return Call.emptyList();
        }
        return Call.create(new ArrayList(this.serviceToTraceIds.keySet()));
    }

    @Override
    public synchronized Call<List<String>> getRemoteServiceNames(String service) {
        if (service.isEmpty() || !this.searchEnabled) {
            return Call.emptyList();
        }
        service = service.toLowerCase(Locale.ROOT);
        return Call.create(new ArrayList<String>(this.serviceToRemoteServiceNames.get(service)));
    }

    @Override
    public synchronized Call<List<String>> getSpanNames(String service) {
        if (service.isEmpty() || !this.searchEnabled) {
            return Call.emptyList();
        }
        service = service.toLowerCase(Locale.ROOT);
        return Call.create(new ArrayList<String>(this.serviceToSpanNames.get(service)));
    }

    @Override
    public synchronized Call<List<DependencyLink>> getDependencies(long endTs, long lookback) {
        if (endTs <= 0L) {
            throw new IllegalArgumentException("endTs <= 0");
        }
        if (lookback <= 0L) {
            throw new IllegalArgumentException("lookback <= 0");
        }
        Set<String> lowTraceIdsInRange = InMemoryStorage.lowTraceIdsInRange(this.spansByTraceIdTimestamp.keySet(), endTs, lookback);
        List<DependencyLink> links = this.getDependencyLinks(lowTraceIdsInRange);
        return Call.create(links);
    }

    List<DependencyLink> getDependencyLinks(Set<String> lowTraceIdsInRange) {
        if (lowTraceIdsInRange.isEmpty()) {
            return Collections.emptyList();
        }
        DependencyLinker linksBuilder = new DependencyLinker();
        for (String lowTraceId : lowTraceIdsInRange) {
            linksBuilder.putTrace(this.spansByTraceId(lowTraceId));
        }
        return linksBuilder.link();
    }

    @Override
    public synchronized Call<List<String>> getKeys() {
        if (!this.searchEnabled) {
            return Call.emptyList();
        }
        return this.autocompleteKeysCall.clone();
    }

    @Override
    public synchronized Call<List<String>> getValues(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key was empty");
        }
        if (!this.searchEnabled) {
            return Call.emptyList();
        }
        return Call.create(new ArrayList<String>(this.autocompleteTags.get(key)));
    }

    List<Span> spansByTraceId(String lowTraceId) {
        ArrayList<Span> sameTraceId = new ArrayList<Span>();
        for (TraceIdTimestamp traceIdTimestamp : this.traceIdToTraceIdTimestamps.get(lowTraceId)) {
            sameTraceId.addAll(this.spansByTraceIdTimestamp.get(traceIdTimestamp));
        }
        return sameTraceId;
    }

    Collection<TraceIdTimestamp> traceIdTimestampsByServiceName(String serviceName) {
        ArrayList<TraceIdTimestamp> traceIdTimestamps = new ArrayList<TraceIdTimestamp>();
        for (String lowTraceId : this.serviceToTraceIds.get(serviceName)) {
            traceIdTimestamps.addAll(this.traceIdToTraceIdTimestamps.get(lowTraceId));
        }
        Collections.sort(traceIdTimestamps, TIMESTAMP_DESCENDING);
        return traceIdTimestamps;
    }

    static String lowTraceId(String traceId) {
        return traceId.length() == 32 ? traceId.substring(16) : traceId;
    }

    @Override
    public InMemoryStorage traces() {
        return this;
    }

    @Override
    public InMemoryStorage spanStore() {
        return this;
    }

    @Override
    public InMemoryStorage autocompleteTags() {
        return this;
    }

    @Override
    public InMemoryStorage serviceAndSpanNames() {
        return this;
    }

    @Override
    public SpanConsumer spanConsumer() {
        return this;
    }

    @Override
    public void close() {
    }

    public String toString() {
        return "InMemoryStorage{}";
    }

    static final class TraceIdTimestamp {
        final String lowTraceId;
        final long timestamp;

        TraceIdTimestamp(String lowTraceId, long timestamp) {
            this.lowTraceId = lowTraceId;
            this.timestamp = timestamp;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof TraceIdTimestamp)) {
                return false;
            }
            TraceIdTimestamp that = (TraceIdTimestamp)o;
            return this.lowTraceId.equals(that.lowTraceId) && this.timestamp == that.timestamp;
        }

        public int hashCode() {
            int h$ = 1;
            h$ *= 1000003;
            h$ ^= this.lowTraceId.hashCode();
            h$ *= 1000003;
            return h$ ^= (int)(this.timestamp >>> 32 ^ this.timestamp);
        }
    }

    static abstract class SortedMultimap<K, V> {
        final SortedMap<K, Collection<V>> delegate;
        int size = 0;

        SortedMultimap(Comparator<K> comparator) {
            this.delegate = new TreeMap<K, Collection<V>>(comparator);
        }

        abstract Collection<V> valueContainer();

        Set<K> keySet() {
            return this.delegate.keySet();
        }

        int size() {
            return this.size;
        }

        void put(K key, V value) {
            Collection<V> valueContainer = (Collection<V>)this.delegate.get(key);
            if (valueContainer == null) {
                valueContainer = this.valueContainer();
                this.delegate.put(key, valueContainer);
            }
            if (valueContainer.add(value)) {
                ++this.size;
            }
        }

        Collection<V> remove(K key) {
            Collection value = (Collection)this.delegate.remove(key);
            if (value != null) {
                this.size -= value.size();
            }
            return value;
        }

        void clear() {
            this.delegate.clear();
            this.size = 0;
        }

        Collection<V> get(K key) {
            Set result = (Set)this.delegate.get(key);
            return result != null ? result : Collections.emptySet();
        }
    }

    static final class ServiceNameToTraceIds
    extends SortedMultimap<String, String> {
        ServiceNameToTraceIds() {
            super(STRING_COMPARATOR);
        }

        @Override
        Set<String> valueContainer() {
            return new LinkedHashSet<String>();
        }

        Set<String> removeServiceIfTraceId(String lowTraceId) {
            LinkedHashSet<String> result = new LinkedHashSet<String>();
            for (Map.Entry entry : this.delegate.entrySet()) {
                Collection lowTraceIds = (Collection)entry.getValue();
                if (!lowTraceIds.remove(lowTraceId) || !lowTraceIds.isEmpty()) continue;
                result.add((String)entry.getKey());
            }
            this.delegate.keySet().removeAll(result);
            return result;
        }
    }

    final class StoreSpansCall
    extends Call.Base<Void> {
        final List<Span> spans;

        StoreSpansCall(List<Span> spans) {
            this.spans = spans;
        }

        @Override
        protected Void doExecute() {
            InMemoryStorage.this.doAccept(this.spans);
            return null;
        }

        @Override
        protected void doEnqueue(Callback<Void> callback) {
            try {
                callback.onSuccess(this.doExecute());
            }
            catch (Throwable t) {
                StoreSpansCall.propagateIfFatal(t);
                callback.onError(t);
            }
        }

        @Override
        public Call<Void> clone() {
            return new StoreSpansCall(this.spans);
        }

        public String toString() {
            return "StoreSpansCall{" + this.spans + "}";
        }
    }

    public static final class Builder
    extends StorageComponent.Builder {
        boolean strictTraceId = true;
        boolean searchEnabled = true;
        int maxSpanCount = 500000;
        List<String> autocompleteKeys = Collections.emptyList();

        @Override
        public Builder strictTraceId(boolean strictTraceId) {
            this.strictTraceId = strictTraceId;
            return this;
        }

        @Override
        public Builder searchEnabled(boolean searchEnabled) {
            this.searchEnabled = searchEnabled;
            return this;
        }

        @Override
        public Builder autocompleteKeys(List<String> autocompleteKeys) {
            if (autocompleteKeys == null) {
                throw new NullPointerException("autocompleteKeys == null");
            }
            this.autocompleteKeys = autocompleteKeys;
            return this;
        }

        public Builder maxSpanCount(int maxSpanCount) {
            if (maxSpanCount <= 0) {
                throw new IllegalArgumentException("maxSpanCount <= 0");
            }
            this.maxSpanCount = maxSpanCount;
            return this;
        }

        @Override
        public InMemoryStorage build() {
            return new InMemoryStorage(this);
        }
    }
}

