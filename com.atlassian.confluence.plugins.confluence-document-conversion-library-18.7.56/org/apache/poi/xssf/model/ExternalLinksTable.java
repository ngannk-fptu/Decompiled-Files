/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalBook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalDefinedName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalLink;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalRow;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetData;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetDataSet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetNames;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.ExternalLinkDocument;

public class ExternalLinksTable
extends POIXMLDocumentPart {
    private CTExternalLink link;

    public ExternalLinksTable() {
        this.link = CTExternalLink.Factory.newInstance();
        this.link.addNewExternalBook();
    }

    public ExternalLinksTable(PackagePart part) throws IOException {
        super(part);
        try (InputStream stream = part.getInputStream();){
            this.readFrom(stream);
        }
    }

    public void readFrom(InputStream is) throws IOException {
        try {
            ExternalLinkDocument doc = (ExternalLinkDocument)ExternalLinkDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.link = doc.getExternalLink();
        }
        catch (XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }

    public void writeTo(OutputStream out) throws IOException {
        ExternalLinkDocument doc = ExternalLinkDocument.Factory.newInstance();
        doc.setExternalLink(this.link);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }

    @Override
    protected void commit() throws IOException {
        PackagePart part = this.getPackagePart();
        try (OutputStream out = part.getOutputStream();){
            this.writeTo(out);
        }
    }

    @Internal
    @Removal(version="6.0.0")
    public CTExternalLink getCTExternalLink() {
        return this.link;
    }

    public String getLinkedFileName() {
        String rId = this.link.getExternalBook().getId();
        PackageRelationship rel = this.getPackagePart().getRelationship(rId);
        if (rel != null && rel.getTargetMode() == TargetMode.EXTERNAL) {
            return rel.getTargetURI().toString();
        }
        return null;
    }

    public void setLinkedFileName(String target) {
        String rId = this.link.getExternalBook().getId();
        if (rId != null && !rId.isEmpty()) {
            this.getPackagePart().removeRelationship(rId);
        }
        PackageRelationship newRel = this.getPackagePart().addExternalRelationship(target, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/externalLinkPath");
        this.link.getExternalBook().setId(newRel.getId());
    }

    public List<String> getSheetNames() {
        CTExternalSheetName[] sheetNames = this.link.getExternalBook().getSheetNames().getSheetNameArray();
        ArrayList<String> names = new ArrayList<String>(sheetNames.length);
        for (CTExternalSheetName name : sheetNames) {
            names.add(name.getVal());
        }
        return names;
    }

    public List<Name> getDefinedNames() {
        CTExternalDefinedName[] extNames = this.link.getExternalBook().getDefinedNames().getDefinedNameArray();
        ArrayList<Name> names = new ArrayList<Name>(extNames.length);
        for (CTExternalDefinedName extName : extNames) {
            names.add(new ExternalName(extName));
        }
        return names;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cacheData(String sheetName, long rowR, String cellR, String v) {
        CTExternalBook externalBook = this.link.getExternalBook();
        Object object = externalBook.monitor();
        synchronized (object) {
            CTExternalSheetData sheetData = this.getSheetData(this.getSheetNameIndex(sheetName));
            CTExternalRow row = this.getRow(sheetData, rowR);
            CTExternalCell cell = this.getCell(row, cellR);
            cell.setV(v);
        }
    }

    private int getSheetNameIndex(String sheetName) {
        CTExternalBook externalBook = this.link.getExternalBook();
        CTExternalSheetNames sheetNames = externalBook.getSheetNames();
        if (sheetNames == null) {
            sheetNames = externalBook.addNewSheetNames();
        }
        int index = -1;
        for (int i = 0; i < sheetNames.sizeOfSheetNameArray(); ++i) {
            CTExternalSheetName ctExternalSheetName = sheetNames.getSheetNameArray(i);
            if (!ctExternalSheetName.getVal().equals(sheetName)) continue;
            index = i;
            break;
        }
        if (index == -1) {
            CTExternalSheetName ctExternalSheetName = sheetNames.addNewSheetName();
            ctExternalSheetName.setVal(sheetName);
            index = sheetNames.sizeOfSheetNameArray() - 1;
        }
        return index;
    }

    private CTExternalSheetData getSheetData(int sheetId) {
        CTExternalSheetDataSet sheetDataSet = this.link.getExternalBook().getSheetDataSet();
        if (sheetDataSet == null) {
            sheetDataSet = this.link.getExternalBook().addNewSheetDataSet();
        }
        CTExternalSheetData ctExternalSheetData = null;
        for (CTExternalSheetData item : sheetDataSet.getSheetDataArray()) {
            if (item.getSheetId() != (long)sheetId) continue;
            ctExternalSheetData = item;
            break;
        }
        if (ctExternalSheetData == null) {
            ctExternalSheetData = sheetDataSet.addNewSheetData();
            ctExternalSheetData.setSheetId(sheetId);
        }
        return ctExternalSheetData;
    }

    private CTExternalRow getRow(CTExternalSheetData sheetData, long rowR) {
        for (CTExternalRow ctExternalRow : sheetData.getRowArray()) {
            if (ctExternalRow.getR() != rowR) continue;
            return ctExternalRow;
        }
        CTExternalRow ctExternalRow = sheetData.addNewRow();
        ctExternalRow.setR(rowR);
        return ctExternalRow;
    }

    private CTExternalCell getCell(CTExternalRow row, String cellR) {
        for (CTExternalCell ctExternalCell : row.getCellArray()) {
            if (!ctExternalCell.getR().equals(cellR)) continue;
            return ctExternalCell;
        }
        CTExternalCell ctExternalCell = row.addNewCell();
        ctExternalCell.setR(cellR);
        return ctExternalCell;
    }

    protected class ExternalName
    implements Name {
        private final CTExternalDefinedName name;

        protected ExternalName(CTExternalDefinedName name) {
            this.name = name;
        }

        @Override
        public String getNameName() {
            return this.name.getName();
        }

        @Override
        public void setNameName(String name) {
            this.name.setName(name);
        }

        @Override
        public String getSheetName() {
            int sheetId = this.getSheetIndex();
            if (sheetId >= 0) {
                return ExternalLinksTable.this.getSheetNames().get(sheetId);
            }
            return null;
        }

        @Override
        public int getSheetIndex() {
            if (this.name.isSetSheetId()) {
                return (int)this.name.getSheetId();
            }
            return -1;
        }

        @Override
        public void setSheetIndex(int sheetId) {
            this.name.setSheetId(sheetId);
        }

        @Override
        public String getRefersToFormula() {
            return this.name.getRefersTo().substring(1);
        }

        @Override
        public void setRefersToFormula(String formulaText) {
            this.name.setRefersTo('=' + formulaText);
        }

        @Override
        public boolean isFunctionName() {
            return false;
        }

        @Override
        public boolean isDeleted() {
            return false;
        }

        @Override
        public boolean isHidden() {
            return false;
        }

        @Override
        public String getComment() {
            return null;
        }

        @Override
        public void setComment(String comment) {
            throw new IllegalStateException("Not Supported");
        }

        @Override
        public void setFunction(boolean value) {
            throw new IllegalStateException("Not Supported");
        }
    }
}

