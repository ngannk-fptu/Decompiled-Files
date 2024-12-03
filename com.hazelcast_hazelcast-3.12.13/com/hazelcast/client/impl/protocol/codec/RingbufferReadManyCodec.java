/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.RingbufferMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class RingbufferReadManyCodec {
    public static final RingbufferMessageType REQUEST_TYPE = RingbufferMessageType.RINGBUFFER_READMANY;
    public static final int RESPONSE_TYPE = 115;

    public static ClientMessage encodeRequest(String name, long startSequence, int minCount, int maxCount, Data filter) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, startSequence, minCount, maxCount, filter);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Ringbuffer.readMany");
        clientMessage.set(name);
        clientMessage.set(startSequence);
        clientMessage.set(minCount);
        clientMessage.set(maxCount);
        if (filter == null) {
            boolean filter_isNull = true;
            clientMessage.set(filter_isNull);
        } else {
            boolean filter_isNull = false;
            clientMessage.set(filter_isNull);
            clientMessage.set(filter);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        long startSequence = 0L;
        parameters.startSequence = startSequence = clientMessage.getLong();
        int minCount = 0;
        parameters.minCount = minCount = clientMessage.getInt();
        int maxCount = 0;
        parameters.maxCount = maxCount = clientMessage.getInt();
        Data filter = null;
        boolean filter_isNull = clientMessage.getBoolean();
        if (!filter_isNull) {
            parameters.filter = filter = clientMessage.getData();
        }
        return parameters;
    }

    public static ClientMessage encodeResponse(int readCount, Collection<Data> items) {
        int requiredDataSize = ResponseParameters.calculateDataSize(readCount, items);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(115);
        clientMessage.set(readCount);
        clientMessage.set(items.size());
        for (Data items_item : items) {
            clientMessage.set(items_item);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
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
        if (clientMessage.isComplete()) {
            return parameters;
        }
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
        parameters.itemSeqsExist = true;
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
        public boolean itemSeqsExist = false;
        public long[] itemSeqs;
        public boolean nextSeqExist = false;
        public long nextSeq;

        public static int calculateDataSize(int readCount, Collection<Data> items) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            dataSize += 4;
            for (Data items_item : items) {
                dataSize += ParameterUtil.calculateDataSize(items_item);
            }
            return dataSize;
        }

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
        public static final RingbufferMessageType TYPE = REQUEST_TYPE;
        public String name;
        public long startSequence;
        public int minCount;
        public int maxCount;
        public Data filter;

        public static int calculateDataSize(String name, long startSequence, int minCount, int maxCount, Data filter) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 8;
            dataSize += 4;
            dataSize += 4;
            ++dataSize;
            if (filter != null) {
                dataSize += ParameterUtil.calculateDataSize(filter);
            }
            return dataSize;
        }
    }
}

