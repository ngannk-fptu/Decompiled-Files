/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.JobPartitionStateCodec;
import com.hazelcast.client.impl.protocol.codec.MapReduceMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.mapreduce.JobPartitionState;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class MapReduceJobProcessInformationCodec {
    public static final MapReduceMessageType REQUEST_TYPE = MapReduceMessageType.MAPREDUCE_JOBPROCESSINFORMATION;
    public static final int RESPONSE_TYPE = 112;

    public static ClientMessage encodeRequest(String name, String jobId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, jobId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("MapReduce.jobProcessInformation");
        clientMessage.set(name);
        clientMessage.set(jobId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        String jobId = null;
        parameters.jobId = jobId = clientMessage.getStringUtf8();
        return parameters;
    }

    public static ClientMessage encodeResponse(Collection<JobPartitionState> jobPartitionStates, int processRecords) {
        int requiredDataSize = ResponseParameters.calculateDataSize(jobPartitionStates, processRecords);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(112);
        clientMessage.set(jobPartitionStates.size());
        for (JobPartitionState jobPartitionStates_item : jobPartitionStates) {
            JobPartitionStateCodec.encode(jobPartitionStates_item, clientMessage);
        }
        clientMessage.set(processRecords);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        ArrayList<JobPartitionState> jobPartitionStates = null;
        int jobPartitionStates_size = clientMessage.getInt();
        jobPartitionStates = new ArrayList<JobPartitionState>(jobPartitionStates_size);
        for (int jobPartitionStates_index = 0; jobPartitionStates_index < jobPartitionStates_size; ++jobPartitionStates_index) {
            JobPartitionState jobPartitionStates_item = JobPartitionStateCodec.decode(clientMessage);
            jobPartitionStates.add(jobPartitionStates_item);
        }
        parameters.jobPartitionStates = jobPartitionStates;
        int processRecords = 0;
        parameters.processRecords = processRecords = clientMessage.getInt();
        return parameters;
    }

    public static class ResponseParameters {
        public List<JobPartitionState> jobPartitionStates;
        public int processRecords;

        public static int calculateDataSize(Collection<JobPartitionState> jobPartitionStates, int processRecords) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (JobPartitionState jobPartitionStates_item : jobPartitionStates) {
                dataSize += JobPartitionStateCodec.calculateDataSize(jobPartitionStates_item);
            }
            return dataSize += 4;
        }
    }

    public static class RequestParameters {
        public static final MapReduceMessageType TYPE = REQUEST_TYPE;
        public String name;
        public String jobId;

        public static int calculateDataSize(String name, String jobId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            return dataSize += ParameterUtil.calculateDataSize(jobId);
        }
    }
}

