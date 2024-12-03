/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.IMapEvent;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.impl.InterceptorRegistry;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.TargetAware;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PostJoinMapOperation
extends Operation
implements IdentifiedDataSerializable,
Versioned,
TargetAware {
    private List<InterceptorInfo> interceptorInfoList = new LinkedList<InterceptorInfo>();
    private List<AccumulatorInfo> infoList;

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    public void addMapInterceptors(MapContainer mapContainer) {
        InterceptorRegistry interceptorRegistry = mapContainer.getInterceptorRegistry();
        List<MapInterceptor> interceptorList = interceptorRegistry.getInterceptors();
        Map<String, MapInterceptor> interceptorMap = interceptorRegistry.getId2InterceptorMap();
        Map<MapInterceptor, String> revMap = MapUtil.createHashMap(interceptorMap.size());
        for (Map.Entry<String, MapInterceptor> entry : interceptorMap.entrySet()) {
            revMap.put(entry.getValue(), entry.getKey());
        }
        InterceptorInfo interceptorInfo = new InterceptorInfo(mapContainer.getName());
        for (MapInterceptor interceptor : interceptorList) {
            interceptorInfo.addInterceptor((String)revMap.get(interceptor), interceptor);
        }
        this.interceptorInfoList.add(interceptorInfo);
    }

    @Override
    public void run() throws Exception {
        MapService mapService = (MapService)this.getService();
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        for (InterceptorInfo interceptorInfo : this.interceptorInfoList) {
            MapContainer mapContainer = mapServiceContext.getMapContainer(interceptorInfo.mapName);
            InterceptorRegistry interceptorRegistry = mapContainer.getInterceptorRegistry();
            Map<String, MapInterceptor> interceptorMap = interceptorRegistry.getId2InterceptorMap();
            List entryList = interceptorInfo.interceptors;
            for (Map.Entry entry : entryList) {
                if (interceptorMap.containsKey(entry.getKey())) continue;
                interceptorRegistry.register((String)entry.getKey(), (MapInterceptor)entry.getValue());
            }
        }
        this.createQueryCaches();
    }

    private void createQueryCaches() {
        MapService mapService = (MapService)this.getService();
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        QueryCacheContext queryCacheContext = mapServiceContext.getQueryCacheContext();
        PublisherContext publisherContext = queryCacheContext.getPublisherContext();
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        for (AccumulatorInfo info : this.infoList) {
            this.addAccumulatorInfo(queryCacheContext, info);
            PublisherRegistry publisherRegistry = mapPublisherRegistry.getOrCreate(info.getMapName());
            publisherRegistry.getOrCreate(info.getCacheId());
            mapServiceContext.addLocalListenerAdapter(new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                }
            }, info.getMapName());
        }
    }

    private void addAccumulatorInfo(QueryCacheContext context, AccumulatorInfo info) {
        PublisherContext publisherContext = context.getPublisherContext();
        AccumulatorInfoSupplier infoSupplier = publisherContext.getAccumulatorInfoSupplier();
        infoSupplier.putIfAbsent(info.getMapName(), info.getCacheId(), info);
    }

    public void setInfoList(List<AccumulatorInfo> infoList) {
        this.infoList = infoList;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.interceptorInfoList.size());
        for (InterceptorInfo interceptorInfo : this.interceptorInfoList) {
            interceptorInfo.writeData(out);
        }
        int size = this.infoList.size();
        out.writeInt(size);
        for (AccumulatorInfo info : this.infoList) {
            out.writeObject(info);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int interceptorsCount = in.readInt();
        for (int i = 0; i < interceptorsCount; ++i) {
            InterceptorInfo info = new InterceptorInfo();
            info.readData(in);
            this.interceptorInfoList.add(info);
        }
        int accumulatorsCount = in.readInt();
        if (accumulatorsCount < 1) {
            this.infoList = Collections.emptyList();
            return;
        }
        this.infoList = new ArrayList<AccumulatorInfo>(accumulatorsCount);
        for (int i = 0; i < accumulatorsCount; ++i) {
            AccumulatorInfo info = (AccumulatorInfo)in.readObject();
            this.infoList.add(info);
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 97;
    }

    @Override
    public void setTarget(Address address) {
    }

    public static class InterceptorInfo
    implements IdentifiedDataSerializable {
        private String mapName;
        private final List<Map.Entry<String, MapInterceptor>> interceptors = new LinkedList<Map.Entry<String, MapInterceptor>>();

        InterceptorInfo(String mapName) {
            this.mapName = mapName;
        }

        public InterceptorInfo() {
        }

        void addInterceptor(String id, MapInterceptor interceptor) {
            this.interceptors.add(new AbstractMap.SimpleImmutableEntry<String, MapInterceptor>(id, interceptor));
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeUTF(this.mapName);
            out.writeInt(this.interceptors.size());
            for (Map.Entry<String, MapInterceptor> entry : this.interceptors) {
                out.writeUTF(entry.getKey());
                out.writeObject(entry.getValue());
            }
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            this.mapName = in.readUTF();
            int size = in.readInt();
            for (int i = 0; i < size; ++i) {
                String id = in.readUTF();
                MapInterceptor interceptor = (MapInterceptor)in.readObject();
                this.interceptors.add(new AbstractMap.SimpleImmutableEntry<String, MapInterceptor>(id, interceptor));
            }
        }

        @Override
        public int getFactoryId() {
            return MapDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 100;
        }
    }
}

