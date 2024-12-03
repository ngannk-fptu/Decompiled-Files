/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.types.TAPAbstractType;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor;
import org.apache.poi.hwpf.usermodel.TableAutoformatLookSpecifier;
import org.apache.poi.hwpf.usermodel.TableCellDescriptor;

public final class TableProperties
extends TAPAbstractType
implements Duplicatable {
    public TableProperties() {
        this.setTlp(new TableAutoformatLookSpecifier());
        this.setShdTable(new ShadingDescriptor());
        this.setBrcBottom(new BorderCode());
        this.setBrcHorizontal(new BorderCode());
        this.setBrcLeft(new BorderCode());
        this.setBrcRight(new BorderCode());
        this.setBrcTop(new BorderCode());
        this.setBrcVertical(new BorderCode());
        this.setRgbrcInsideDefault_0(new BorderCode());
        this.setRgbrcInsideDefault_1(new BorderCode());
        this.setRgdxaCenter(new short[0]);
        this.setRgdxaCenterPrint(new short[0]);
        this.setRgshd(new ShadingDescriptor[0]);
        this.setRgtc(new TableCellDescriptor[0]);
    }

    public TableProperties(TableProperties other) {
        super(other);
    }

    public TableProperties(short columns) {
        this.setItcMac((short)columns);
        this.setRgshd(new ShadingDescriptor[columns]);
        for (int x = 0; x < columns; ++x) {
            this.getRgshd()[x] = new ShadingDescriptor();
        }
        TableCellDescriptor[] tableCellDescriptors = new TableCellDescriptor[columns];
        for (int x = 0; x < columns; ++x) {
            tableCellDescriptors[x] = new TableCellDescriptor();
        }
        this.setRgtc(tableCellDescriptors);
        this.setRgdxaCenter(new short[columns]);
        this.setRgdxaCenterPrint(new short[columns]);
    }

    @Override
    public TableProperties copy() {
        return new TableProperties(this);
    }
}

