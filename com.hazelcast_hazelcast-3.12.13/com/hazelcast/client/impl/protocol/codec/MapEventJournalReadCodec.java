/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class MapEventJournalReadCodec {
    public static final MapMessageType REQUEST_TYPE = MapMessageType.MAP_EVENTJOURNALREAD;
    public static final int RESPONSE_TYPE = 115;

    public static ClientMessage encodeRequest(String name, long startSequence, int minSize, int maxSize, Data predicate, Data projection) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, startSequence, minSize, maxSize, predicate, projection);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Map.eventJournalRead");
        clientMessage.set(name);
        clientMessage.set(startSequence);
        clientMessage.set(minSize);
        clientMessage.set(maxSize);
        if (predicate == null) {
            boolean predicate_isNull = true;
            clientMessage.set(predicate_isNull);
        } else {
            boolean predicate_isNull = false;
            clientMessage.set(predicate_isNull);
            clientMessage.set(predicate);
        }
        if (projection == null) {
            boolean projection_isNull = true;
            clientMessage.set(projection_isNull);
        } else {
            boolean projection_isNull = false;
            clientMessage.set(projection_isNull);
            clientMessage.set(projection);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        long startSequence = 0L;
        parameters.startSequence = startSequence = clientMessage.getLong();
        int minSize = 0;
        parameters.minSize = minSize = clientMessage.getInt();
        int maxSize = 0;
        parameters.maxSize = maxSize = clientMessage.getInt();
        Data predicate = null;
        boolean predicate_isNull = clientMessage.getBoolean();
        if (!predicate_isNull) {
            parameters.predicate = predicate = clientMessage.getData();
        }
        Data projection = null;
        boolean projection_isNull = clientMessage.getBoolean();
        if (!projection_isNull) {
            parameters.projection = projection = clientMessage.getData();
        }
        return parameters;
    }

    public static ClientMessage encodeResponse(int readCount, Collection<Data> items, long[] itemSeqs) {
        int requiredDataSize = ResponseParameters.calculateDataSize(readCount, items, itemSeqs);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(115);
        clientMessage.set(readCount);
        clientMessage.set(items.size());
        for (Data items_item : items) {
            clientMessage.set(items_item);
        }
        if (itemSeqs == null) {
            boolean itemSeqs_isNull = true;
            clientMessage.set(itemSeqs_isNull);
        } else {
            boolean itemSeqs_isNull = false;
            clientMessage.set(itemSeqs_isNull);
            clientMessage.set(itemSeqs.length);
            for (long itemSeqs_item : itemSeqs) {
                clientMessage.set(itemSeqs_item);
            }
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeResponse(int readCount, Collection<Data> items, long[] itemSeqs, long nextSeq) {
        int requiredDataSize = ResponseParameters.calculateDataSize(readCount, items, itemSeqs, nextSeq);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(115);
        clientMessage.set(readCount);
        clientMessage.set(items.size());
        for (Data items_item : items) {
            clientMessage.set(items_item);
        }
        if (itemSeqs == null) {
            boolean itemSeqs_isNull = true;
            clientMessage.set(itemSeqs_isNull);
        } else {
            boolean itemSeqs_isNull = false;
            clientMessage.set(itemSeqs_isNull);
            clientMessage.set(itemSeqs.length);
            for (long itemSeqs_item : itemSeqs) {
                clientMessage.set(itemSeqs_item);
            }
        }
        clientMessage.set(nextSeq);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        int readCount = 0;
        parameters.readCount = readCount = clientMessage.getInt();
        ArrayList<Data> items = null;
        int items_size = clientMessage.getInt();
        items = new ArrayList<Data>(items_size);
        for (int items_index = 0; items_index < items_size; ++items_index) {
            Data items_item = clientMessage.getData();
            items.add(items_item);
        }
        parameters.items = items;
        long[] itemSeqs = null;
        boolean itemSeqs_isNull = clientMessage.getBoolean();
        if (!itemSeqs_isNull) {
            int itemSeqs_size = clientMessage.getInt();
            itemSeqs = new long[itemSeqs_size];
            for (int itemSeqs_index = 0; itemSeqs_index < itemSeqs_size; ++itemSeqs_index) {
                long itemSeqs_item;
                itemSeqs[itemSeqs_index] = itemSeqs_item = clientMessage.getLong();
            }
            parameters.itemSeqs = itemSeqs;
        }
        if (clientMessage.isComplete()) {
            return parameters;
        }
        long nextSeq = 0L;
        parameters.nextSeq = nextSeq = clientMessage.getLong();
        parameters.nextSeqExist = true;
        return parameters;
    }

    public static class ResponseParameters {
        public int readCount;
        public List<Data> items;
        public long[] itemSeqs;
        public boolean nextSeqExist = false;
        public long nextSeq;

        public static int calculateDataSize(int readCount, Collection<Data> items, long[] itemSeqs) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            dataSize += 4;
            for (Data items_item : items) {
                dataSize += ParameterUtil.calculateDataSize(items_item);
            }
            ++dataSize;
            if (itemSeqs != null) {
                dataSize += 4;
                for (Object itemSeqs_item : (Object)itemSeqs) {
                    dataSize += 8;
                }
            }
            return dataSize;
        }

        public static int calculateDataSize(int readCount, Collection<Data> items, long[] itemSeqs, long nextSeq) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            dataSize += 4;
            for (Data items_item : items) {
                dataSize += ParameterUtil.calculateDataSize(items_item);
            }
            ++dataSize;
            if (itemSeqs != null) {
                dataSize += 4;
                for (Object itemSeqs_item : (Object)itemSeqs) {
                    dataSize += 8;
                }
            }
            return dataSize += 8;
        }
    }

    public static class RequestParameters {
        public static final MapMessageType TYPE = REQUEST_TYPE;
        public String name;
        public long startSequence;
        public int minSize;
        public int maxSize;
        public Data predicate;
        public Data projection;

        public static int calculateDataSize(String name, long startSequence, int minSize, int maxSize, Data predicate, Data projection) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 8;
            dataSize += 4;
            dataSize += 4;
            ++dataSize;
            if (predicate != null) {
                dataSize += ParameterUtil.calculateDataSize(predicate);
            }
            ++dataSize;
            if (projection != null) {
                dataSize += ParameterUtil.calculateDataSize(projection);
            }
            return dataSize;
        }
    }
}

