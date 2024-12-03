/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.MetadataInitializer;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.recordstore.RecordStoreMutationObserver;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Metadata;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public class JsonMetadataRecordStoreMutationObserver
implements RecordStoreMutationObserver<Record> {
    private InternalSerializationService serializationService;
    private MetadataInitializer metadataInitializer;

    public JsonMetadataRecordStoreMutationObserver(InternalSerializationService serializationService, MetadataInitializer metadataInitializer) {
        this.serializationService = serializationService;
        this.metadataInitializer = metadataInitializer;
    }

    @Override
    public void onClear() {
    }

    @Override
    public void onPutRecord(Data key, Record record) {
        this.onPutInternal(record);
    }

    @Override
    public void onReplicationPutRecord(Data key, Record record) {
        this.onPutInternal(record);
    }

    @Override
    public void onUpdateRecord(Data key, Record record, Object newValue) {
        this.updateValueMetadataIfNeccessary(record, newValue);
    }

    @Override
    public void onRemoveRecord(Data key, Record record) {
    }

    @Override
    public void onEvictRecord(Data key, Record record) {
    }

    @Override
    public void onLoadRecord(Data key, Record record) {
        this.onPutInternal(record);
    }

    @Override
    public void onDestroy(boolean internal) {
    }

    @Override
    public void onReset() {
    }

    protected Metadata getMetadata(Record record) {
        return record.getMetadata();
    }

    protected void setMetadata(Record record, Metadata metadata) {
        record.setMetadata(metadata);
    }

    protected void removeMetadata(Record record) {
        record.setMetadata(null);
    }

    private void onPutInternal(Record record) {
        Metadata metadata = this.initializeMetadata(record.getKey(), record.getValue());
        if (metadata != null) {
            this.setMetadata(record, metadata);
        }
    }

    @SuppressFBWarnings(value={"NP_LOAD_OF_KNOWN_NULL_VALUE"})
    private void updateValueMetadataIfNeccessary(Record record, Object updateValue) {
        Object valueMetadata = null;
        try {
            valueMetadata = record.getValue() instanceof Data ? this.metadataInitializer.createFromData((Data)this.serializationService.toData(updateValue)) : this.metadataInitializer.createFromObject(this.serializationService.toObject(updateValue));
        }
        catch (IOException e) {
            EmptyStatement.ignore(e);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        if (valueMetadata != null) {
            Metadata existing = this.getMetadata(record);
            if (existing == null) {
                existing = new Metadata();
                this.setMetadata(record, existing);
            }
            existing.setValueMetadata(valueMetadata);
        } else {
            Metadata existing = this.getMetadata(record);
            if (existing != null) {
                if (existing.getKeyMetadata() == null) {
                    this.removeMetadata(record);
                } else {
                    existing.setValueMetadata(valueMetadata);
                }
            }
        }
    }

    private Metadata initializeMetadata(Data key, Object value) {
        try {
            Object keyMetadata = this.metadataInitializer.createFromData(key);
            Object valueMetadata = value instanceof Data ? this.metadataInitializer.createFromData((Data)value) : this.metadataInitializer.createFromObject(value);
            if (keyMetadata != null || valueMetadata != null) {
                Metadata metadata = new Metadata();
                metadata.setKeyMetadata(keyMetadata);
                metadata.setValueMetadata(valueMetadata);
                return metadata;
            }
            return null;
        }
        catch (IOException e) {
            return null;
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }
}

