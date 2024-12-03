/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.ddf.EscherTertiaryOptRecord;
import org.apache.poi.hwpf.model.FSPA;
import org.apache.poi.hwpf.model.FSPATable;
import org.apache.poi.hwpf.model.OfficeArtContent;
import org.apache.poi.hwpf.usermodel.OfficeDrawing;
import org.apache.poi.hwpf.usermodel.OfficeDrawings;

public class OfficeDrawingsImpl
implements OfficeDrawings {
    private final OfficeArtContent officeArtContent;
    private final FSPATable _fspaTable;
    private final byte[] _mainStream;

    public OfficeDrawingsImpl(FSPATable fspaTable, OfficeArtContent officeArtContent, byte[] mainStream) {
        this._fspaTable = fspaTable;
        this.officeArtContent = officeArtContent;
        this._mainStream = mainStream;
    }

    private EscherBlipRecord getBitmapRecord(int bitmapIndex) {
        EscherContainerRecord bContainer = this.officeArtContent.getBStoreContainer();
        if (bContainer == null) {
            return null;
        }
        if (bContainer.getChildCount() < bitmapIndex) {
            return null;
        }
        EscherRecord imageRecord = bContainer.getChild(bitmapIndex - 1);
        if (imageRecord instanceof EscherBlipRecord) {
            return (EscherBlipRecord)imageRecord;
        }
        if (imageRecord instanceof EscherBSERecord) {
            DefaultEscherRecordFactory recordFactory;
            EscherRecord record;
            EscherBSERecord bseRecord = (EscherBSERecord)imageRecord;
            EscherBlipRecord blip = bseRecord.getBlipRecord();
            if (blip != null) {
                return blip;
            }
            if (bseRecord.getOffset() > 0 && (record = (recordFactory = new DefaultEscherRecordFactory()).createRecord(this._mainStream, bseRecord.getOffset())) instanceof EscherBlipRecord) {
                record.fillFields(this._mainStream, bseRecord.getOffset(), recordFactory);
                return (EscherBlipRecord)record;
            }
        }
        return null;
    }

    private EscherContainerRecord getEscherShapeRecordContainer(int shapeId) {
        for (EscherContainerRecord escherContainerRecord : this.officeArtContent.getSpContainers()) {
            EscherSpRecord escherSpRecord = (EscherSpRecord)escherContainerRecord.getChildById((short)-4086);
            if (escherSpRecord == null || escherSpRecord.getShapeId() != shapeId) continue;
            return escherContainerRecord;
        }
        return null;
    }

    private OfficeDrawing getOfficeDrawing(final FSPA fspa) {
        return new OfficeDrawing(){

            @Override
            public OfficeDrawing.HorizontalPositioning getHorizontalPositioning() {
                int value = this.getTertiaryPropertyValue(EscherPropertyTypes.GROUPSHAPE__POSH);
                switch (value) {
                    case 0: {
                        return OfficeDrawing.HorizontalPositioning.ABSOLUTE;
                    }
                    case 1: {
                        return OfficeDrawing.HorizontalPositioning.LEFT;
                    }
                    case 2: {
                        return OfficeDrawing.HorizontalPositioning.CENTER;
                    }
                    case 3: {
                        return OfficeDrawing.HorizontalPositioning.RIGHT;
                    }
                    case 4: {
                        return OfficeDrawing.HorizontalPositioning.INSIDE;
                    }
                    case 5: {
                        return OfficeDrawing.HorizontalPositioning.OUTSIDE;
                    }
                }
                return OfficeDrawing.HorizontalPositioning.ABSOLUTE;
            }

            @Override
            public OfficeDrawing.HorizontalRelativeElement getHorizontalRelative() {
                int value = this.getTertiaryPropertyValue(EscherPropertyTypes.GROUPSHAPE__POSRELH);
                switch (value) {
                    case 1: {
                        return OfficeDrawing.HorizontalRelativeElement.MARGIN;
                    }
                    case 2: {
                        return OfficeDrawing.HorizontalRelativeElement.PAGE;
                    }
                    case 3: {
                        return OfficeDrawing.HorizontalRelativeElement.TEXT;
                    }
                    case 4: {
                        return OfficeDrawing.HorizontalRelativeElement.CHAR;
                    }
                }
                return OfficeDrawing.HorizontalRelativeElement.TEXT;
            }

            @Override
            public EscherContainerRecord getOfficeArtSpContainer() {
                return OfficeDrawingsImpl.this.getEscherShapeRecordContainer(this.getShapeId());
            }

            @Override
            public byte[] getPictureData() {
                EscherContainerRecord shapeDescription = OfficeDrawingsImpl.this.getEscherShapeRecordContainer(this.getShapeId());
                if (shapeDescription == null) {
                    return null;
                }
                EscherOptRecord escherOptRecord = (EscherOptRecord)shapeDescription.getChildById(EscherOptRecord.RECORD_ID);
                if (escherOptRecord == null) {
                    return null;
                }
                EscherSimpleProperty escherProperty = (EscherSimpleProperty)escherOptRecord.lookup(EscherPropertyTypes.BLIP__BLIPTODISPLAY);
                if (escherProperty == null) {
                    return null;
                }
                int bitmapIndex = escherProperty.getPropertyValue();
                EscherBlipRecord escherBlipRecord = OfficeDrawingsImpl.this.getBitmapRecord(bitmapIndex);
                if (escherBlipRecord == null) {
                    return null;
                }
                return escherBlipRecord.getPicturedata();
            }

            @Override
            public int getRectangleBottom() {
                return fspa.getYaBottom();
            }

            @Override
            public int getRectangleLeft() {
                return fspa.getXaLeft();
            }

            @Override
            public int getRectangleRight() {
                return fspa.getXaRight();
            }

            @Override
            public int getRectangleTop() {
                return fspa.getYaTop();
            }

            @Override
            public int getShapeId() {
                return fspa.getSpid();
            }

            private int getTertiaryPropertyValue(EscherPropertyTypes type) {
                EscherContainerRecord shapeDescription = OfficeDrawingsImpl.this.getEscherShapeRecordContainer(this.getShapeId());
                if (shapeDescription == null) {
                    return -1;
                }
                EscherTertiaryOptRecord escherTertiaryOptRecord = (EscherTertiaryOptRecord)shapeDescription.getChildById(EscherTertiaryOptRecord.RECORD_ID);
                if (escherTertiaryOptRecord == null) {
                    return -1;
                }
                EscherSimpleProperty escherProperty = (EscherSimpleProperty)escherTertiaryOptRecord.lookup(type);
                return escherProperty == null ? -1 : escherProperty.getPropertyValue();
            }

            @Override
            public OfficeDrawing.VerticalPositioning getVerticalPositioning() {
                int value = this.getTertiaryPropertyValue(EscherPropertyTypes.GROUPSHAPE__POSV);
                switch (value) {
                    case 0: {
                        return OfficeDrawing.VerticalPositioning.ABSOLUTE;
                    }
                    case 1: {
                        return OfficeDrawing.VerticalPositioning.TOP;
                    }
                    case 2: {
                        return OfficeDrawing.VerticalPositioning.CENTER;
                    }
                    case 3: {
                        return OfficeDrawing.VerticalPositioning.BOTTOM;
                    }
                    case 4: {
                        return OfficeDrawing.VerticalPositioning.INSIDE;
                    }
                    case 5: {
                        return OfficeDrawing.VerticalPositioning.OUTSIDE;
                    }
                }
                return OfficeDrawing.VerticalPositioning.ABSOLUTE;
            }

            @Override
            public OfficeDrawing.VerticalRelativeElement getVerticalRelativeElement() {
                int value = this.getTertiaryPropertyValue(EscherPropertyTypes.GROUPSHAPE__POSV);
                switch (value) {
                    case 1: {
                        return OfficeDrawing.VerticalRelativeElement.MARGIN;
                    }
                    case 2: {
                        return OfficeDrawing.VerticalRelativeElement.PAGE;
                    }
                    case 3: {
                        return OfficeDrawing.VerticalRelativeElement.TEXT;
                    }
                    case 4: {
                        return OfficeDrawing.VerticalRelativeElement.LINE;
                    }
                }
                return OfficeDrawing.VerticalRelativeElement.TEXT;
            }

            public String toString() {
                return "OfficeDrawingImpl: " + fspa;
            }
        };
    }

    @Override
    public OfficeDrawing getOfficeDrawingAt(int characterPosition) {
        FSPA fspa = this._fspaTable.getFspaFromCp(characterPosition);
        if (fspa == null) {
            return null;
        }
        return this.getOfficeDrawing(fspa);
    }

    @Override
    public Collection<OfficeDrawing> getOfficeDrawings() {
        ArrayList<OfficeDrawing> result = new ArrayList<OfficeDrawing>();
        for (FSPA fspa : this._fspaTable.getShapes()) {
            result.add(this.getOfficeDrawing(fspa));
        }
        return Collections.unmodifiableList(result);
    }
}

