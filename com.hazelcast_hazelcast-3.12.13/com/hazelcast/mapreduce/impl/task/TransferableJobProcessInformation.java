/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.JobProcessInformation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public class TransferableJobProcessInformation
implements JobProcessInformation,
Portable {
    private JobPartitionState[] partitionStates;
    private int processedRecords;

    public TransferableJobProcessInformation() {
    }

    public TransferableJobProcessInformation(JobPartitionState[] partitionStates, int processedRecords) {
        this.partitionStates = new JobPartitionState[partitionStates.length];
        System.arraycopy(partitionStates, 0, this.partitionStates, 0, partitionStates.length);
        this.processedRecords = processedRecords;
    }

    @Override
    @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="exposed since it is guarded by serialization cycle or by copy inside the constructor. This class is only used for transfer of the states and user can change it without breaking anything.")
    public JobPartitionState[] getPartitionStates() {
        return this.partitionStates;
    }

    @Override
    public int getProcessedRecords() {
        return this.processedRecords;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeInt("processedRecords", this.processedRecords);
        ObjectDataOutput out = writer.getRawDataOutput();
        out.writeObject(this.partitionStates);
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        this.processedRecords = reader.readInt("processedRecords");
        ObjectDataInput in = reader.getRawDataInput();
        this.partitionStates = (JobPartitionState[])in.readObject();
    }

    @Override
    public int getFactoryId() {
        return 1;
    }

    @Override
    public int getClassId() {
        return 2;
    }
}

