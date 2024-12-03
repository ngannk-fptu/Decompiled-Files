/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.util.Internal;

@Internal
public final class OfficeArtContent {
    protected static final Logger LOG = LogManager.getLogger(OfficeArtContent.class);
    private final EscherContainerRecord drawingGroupData = new EscherContainerRecord();
    private EscherContainerRecord mainDocumentDgContainer;
    private EscherContainerRecord headerDocumentDgContainer;

    public OfficeArtContent(byte[] data, int offset, int size) {
        this.fillEscherRecords(data, offset, size);
    }

    private void fillEscherRecords(byte[] data, int offset, int size) {
        if (size == 0) {
            return;
        }
        DefaultEscherRecordFactory recordFactory = new DefaultEscherRecordFactory();
        int pos = offset;
        pos += this.drawingGroupData.fillFields(data, pos, recordFactory);
        if (this.drawingGroupData.getRecordId() == EscherRecordTypes.DGG_CONTAINER.typeID) {
            LOG.atDebug().log("Invalid record-id for filling Escher records: " + this.drawingGroupData.getRecordId());
        }
        block4: while (pos < offset + size) {
            byte dgglbl = data[pos];
            if (dgglbl != 0 && dgglbl != 1) {
                throw new IllegalArgumentException("Invalid dgglbl when filling Escher records: " + dgglbl);
            }
            ++pos;
            EscherContainerRecord dgContainer = new EscherContainerRecord();
            pos += dgContainer.fillFields(data, pos, recordFactory);
            if (dgContainer.getRecordId() != EscherRecordTypes.DG_CONTAINER.typeID) {
                throw new IllegalArgumentException("Did have an invalid record-type: " + dgContainer.getRecordId() + " when filling Escher records");
            }
            switch (dgglbl) {
                case 0: {
                    this.mainDocumentDgContainer = dgContainer;
                    continue block4;
                }
                case 1: {
                    this.headerDocumentDgContainer = dgContainer;
                    continue block4;
                }
            }
            LogManager.getLogger(OfficeArtContent.class).atWarn().log("dgglbl {} for OfficeArtWordDrawing is out of bounds [0, 1]", (Object)Unbox.box(dgglbl));
        }
        if (pos != offset + size) {
            throw new IllegalStateException("Did not read all data when filling Escher records: pos: " + pos + ", offset: " + offset + ", size: " + size);
        }
    }

    private List<? extends EscherContainerRecord> getDgContainers() {
        ArrayList<EscherContainerRecord> dgContainers = new ArrayList<EscherContainerRecord>(2);
        if (this.mainDocumentDgContainer != null) {
            dgContainers.add(this.mainDocumentDgContainer);
        }
        if (this.headerDocumentDgContainer != null) {
            dgContainers.add(this.headerDocumentDgContainer);
        }
        return dgContainers;
    }

    public EscherContainerRecord getBStoreContainer() {
        return (EscherContainerRecord)this.drawingGroupData.getChildById(EscherRecordTypes.BSTORE_CONTAINER.typeID);
    }

    public List<? extends EscherContainerRecord> getSpgrContainers() {
        ArrayList<EscherContainerRecord> spgrContainers = new ArrayList<EscherContainerRecord>(1);
        for (EscherContainerRecord escherContainerRecord : this.getDgContainers()) {
            for (EscherRecord escherRecord : escherContainerRecord) {
                if (escherRecord.getRecordId() != -4093) continue;
                spgrContainers.add((EscherContainerRecord)escherRecord);
            }
        }
        return spgrContainers;
    }

    public List<? extends EscherContainerRecord> getSpContainers() {
        ArrayList<EscherContainerRecord> spContainers = new ArrayList<EscherContainerRecord>(1);
        for (EscherContainerRecord escherContainerRecord : this.getSpgrContainers()) {
            for (EscherRecord escherRecord : escherContainerRecord) {
                if (escherRecord.getRecordId() != -4092) continue;
                spContainers.add((EscherContainerRecord)escherRecord);
            }
        }
        return spContainers;
    }

    public String toString() {
        return "OfficeArtContent{drawingGroupData=" + this.drawingGroupData + ", mainDocumentDgContainer=" + this.mainDocumentDgContainer + ", headerDocumentDgContainer=" + this.headerDocumentDgContainer + '}';
    }
}

