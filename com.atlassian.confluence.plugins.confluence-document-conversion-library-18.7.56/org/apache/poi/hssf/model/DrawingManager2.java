/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ddf.EscherDgRecord;
import org.apache.poi.ddf.EscherDggRecord;

public class DrawingManager2 {
    private final EscherDggRecord dgg;
    private final List<EscherDgRecord> drawingGroups = new ArrayList<EscherDgRecord>();

    public DrawingManager2(EscherDggRecord dgg) {
        this.dgg = dgg;
    }

    public void clearDrawingGroups() {
        this.drawingGroups.clear();
    }

    public EscherDgRecord createDgRecord() {
        EscherDgRecord dg = new EscherDgRecord();
        dg.setRecordId(EscherDgRecord.RECORD_ID);
        short dgId = this.findNewDrawingGroupId();
        dg.setOptions((short)(dgId << 4));
        dg.setNumShapes(0);
        dg.setLastMSOSPID(-1);
        this.drawingGroups.add(dg);
        this.dgg.addCluster(dgId, 0);
        this.dgg.setDrawingsSaved(this.dgg.getDrawingsSaved() + 1);
        return dg;
    }

    public int allocateShapeId(EscherDgRecord dg) {
        return this.dgg.allocateShapeId(dg, true);
    }

    public short findNewDrawingGroupId() {
        return this.dgg.findNewDrawingGroupId();
    }

    public EscherDggRecord getDgg() {
        return this.dgg;
    }

    public void incrementDrawingsSaved() {
        this.dgg.setDrawingsSaved(this.dgg.getDrawingsSaved() + 1);
    }
}

