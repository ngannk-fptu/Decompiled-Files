/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFactory;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;

public abstract class XWPFHeaderFooter
extends POIXMLDocumentPart
implements IBody {
    List<XWPFParagraph> paragraphs = new ArrayList<XWPFParagraph>();
    List<XWPFTable> tables = new ArrayList<XWPFTable>();
    List<XWPFPictureData> pictures = new ArrayList<XWPFPictureData>();
    List<IBodyElement> bodyElements = new ArrayList<IBodyElement>();
    CTHdrFtr headerFooter;
    XWPFDocument document;

    XWPFHeaderFooter(XWPFDocument doc, CTHdrFtr hdrFtr) {
        if (doc == null) {
            throw new NullPointerException();
        }
        this.document = doc;
        this.headerFooter = hdrFtr;
        this.readHdrFtr();
    }

    protected XWPFHeaderFooter() {
        this.headerFooter = CTHdrFtr.Factory.newInstance();
        this.readHdrFtr();
    }

    public XWPFHeaderFooter(POIXMLDocumentPart parent, PackagePart part) {
        super(parent, part);
        this.document = (XWPFDocument)this.getParent();
        if (this.document == null) {
            throw new NullPointerException();
        }
    }

    @Override
    protected void onDocumentRead() throws IOException {
        for (POIXMLDocumentPart poixmlDocumentPart : this.getRelations()) {
            if (!(poixmlDocumentPart instanceof XWPFPictureData)) continue;
            XWPFPictureData xwpfPicData = (XWPFPictureData)poixmlDocumentPart;
            this.pictures.add(xwpfPicData);
            this.document.registerPackagePictureData(xwpfPicData);
        }
    }

    @Internal
    public CTHdrFtr _getHdrFtr() {
        return this.headerFooter;
    }

    @Override
    public List<IBodyElement> getBodyElements() {
        return Collections.unmodifiableList(this.bodyElements);
    }

    @Override
    public List<XWPFParagraph> getParagraphs() {
        return Collections.unmodifiableList(this.paragraphs);
    }

    @Override
    public List<XWPFTable> getTables() throws ArrayIndexOutOfBoundsException {
        return Collections.unmodifiableList(this.tables);
    }

    public String getText() {
        String text;
        StringBuilder t = new StringBuilder(64);
        for (XWPFParagraph paragraph : this.paragraphs) {
            if (paragraph.isEmpty() || (text = paragraph.getText()) == null || text.length() <= 0) continue;
            t.append(text);
            t.append('\n');
        }
        for (XWPFTable table : this.tables) {
            text = table.getText();
            if (text == null || text.length() <= 0) continue;
            t.append(text);
            t.append('\n');
        }
        for (IBodyElement bodyElement : this.getBodyElements()) {
            if (!(bodyElement instanceof XWPFSDT)) continue;
            t.append(((XWPFSDT)bodyElement).getContent().getText()).append('\n');
        }
        return t.toString();
    }

    public void setHeaderFooter(CTHdrFtr headerFooter) {
        this.headerFooter = headerFooter;
        this.readHdrFtr();
    }

    @Override
    public XWPFTable getTable(CTTbl ctTable) {
        for (XWPFTable table : this.tables) {
            if (table == null) {
                return null;
            }
            if (!table.getCTTbl().equals(ctTable)) continue;
            return table;
        }
        return null;
    }

    @Override
    public XWPFParagraph getParagraph(CTP p) {
        for (XWPFParagraph paragraph : this.paragraphs) {
            if (!paragraph.getCTP().equals(p)) continue;
            return paragraph;
        }
        return null;
    }

    @Override
    public XWPFParagraph getParagraphArray(int pos) {
        if (pos >= 0 && pos < this.paragraphs.size()) {
            return this.paragraphs.get(pos);
        }
        return null;
    }

    public List<XWPFParagraph> getListParagraph() {
        return this.paragraphs;
    }

    public List<XWPFPictureData> getAllPictures() {
        return Collections.unmodifiableList(this.pictures);
    }

    public List<XWPFPictureData> getAllPackagePictures() {
        return this.document.getAllPackagePictures();
    }

    public String addPictureData(byte[] pictureData, int format) throws InvalidFormatException {
        return this.addPictureData(pictureData, PictureType.findByOoxmlId(format));
    }

    public String addPictureData(byte[] pictureData, PictureType pictureType) throws InvalidFormatException {
        if (pictureType == null) {
            throw new InvalidFormatException("pictureType is not supported");
        }
        XWPFPictureData xwpfPicData = this.document.findPackagePictureData(pictureData);
        POIXMLRelation relDesc = XWPFPictureData.RELATIONS[pictureType.ooxmlId];
        if (xwpfPicData == null) {
            int idx = this.document.getNextPicNameNumber(pictureType);
            xwpfPicData = (XWPFPictureData)this.createRelationship(relDesc, XWPFFactory.getInstance(), idx);
            PackagePart picDataPart = xwpfPicData.getPackagePart();
            try (OutputStream out = picDataPart.getOutputStream();){
                out.write(pictureData);
            }
            catch (IOException e) {
                throw new POIXMLException(e);
            }
            this.document.registerPackagePictureData(xwpfPicData);
            this.pictures.add(xwpfPicData);
            return this.getRelationId(xwpfPicData);
        }
        if (!this.getRelations().contains(xwpfPicData)) {
            POIXMLDocumentPart.RelationPart rp = this.addRelation(null, XWPFRelation.IMAGES, xwpfPicData);
            this.pictures.add(xwpfPicData);
            return rp.getRelationship().getId();
        }
        return this.getRelationId(xwpfPicData);
    }

    public String addPictureData(InputStream is, int format) throws InvalidFormatException, IOException {
        byte[] data = IOUtils.toByteArrayWithMaxLength(is, XWPFPictureData.getMaxImageSize());
        return this.addPictureData(data, format);
    }

    public String addPictureData(InputStream is, PictureType pictureType) throws InvalidFormatException, IOException {
        byte[] data = IOUtils.toByteArrayWithMaxLength(is, XWPFPictureData.getMaxImageSize());
        return this.addPictureData(data, pictureType);
    }

    public XWPFPictureData getPictureDataByID(String blipID) {
        POIXMLDocumentPart relatedPart = this.getRelationById(blipID);
        if (relatedPart instanceof XWPFPictureData) {
            return (XWPFPictureData)relatedPart;
        }
        return null;
    }

    public XWPFParagraph createParagraph() {
        XWPFParagraph paragraph = new XWPFParagraph(this.headerFooter.addNewP(), this);
        this.paragraphs.add(paragraph);
        this.bodyElements.add(paragraph);
        return paragraph;
    }

    public XWPFTable createTable(int rows, int cols) {
        XWPFTable table = new XWPFTable(this.headerFooter.addNewTbl(), this, rows, cols);
        this.tables.add(table);
        this.bodyElements.add(table);
        return table;
    }

    public void removeParagraph(XWPFParagraph paragraph) {
        if (this.paragraphs.contains(paragraph)) {
            CTP ctP = paragraph.getCTP();
            try (XmlCursor c = ctP.newCursor();){
                c.removeXml();
            }
            this.paragraphs.remove(paragraph);
            this.bodyElements.remove(paragraph);
        }
    }

    public void removeTable(XWPFTable table) {
        if (this.tables.contains(table)) {
            CTTbl ctTbl = table.getCTTbl();
            try (XmlCursor c = ctTbl.newCursor();){
                c.removeXml();
            }
            this.tables.remove(table);
            this.bodyElements.remove(table);
        }
    }

    public void clearHeaderFooter() {
        try (XmlCursor c = this.headerFooter.newCursor();){
            c.removeXmlContents();
        }
        this.paragraphs.clear();
        this.tables.clear();
        this.bodyElements.clear();
    }

    @Override
    public XWPFParagraph insertNewParagraph(XmlCursor cursor) {
        if (this.isCursorInHdrF(cursor)) {
            String uri = CTP.type.getName().getNamespaceURI();
            String localPart = "p";
            cursor.beginElement(localPart, uri);
            cursor.toParent();
            CTP p = (CTP)cursor.getObject();
            XWPFParagraph newP = new XWPFParagraph(p, this);
            XmlObject o = null;
            while (!(o instanceof CTP) && cursor.toPrevSibling()) {
                o = cursor.getObject();
            }
            if (!(o instanceof CTP) || o == p) {
                this.paragraphs.add(0, newP);
            } else {
                int pos = this.paragraphs.indexOf(this.getParagraph((CTP)o)) + 1;
                this.paragraphs.add(pos, newP);
            }
            int i = 0;
            try (XmlCursor p2 = p.newCursor();){
                cursor.toCursor(p2);
            }
            while (cursor.toPrevSibling()) {
                o = cursor.getObject();
                if (!(o instanceof CTP) && !(o instanceof CTTbl)) continue;
                ++i;
            }
            this.bodyElements.add(i, newP);
            var9_9 = null;
            try (XmlCursor p3 = p.newCursor();){
                cursor.toCursor(p3);
                cursor.toEndToken();
            }
            catch (Throwable throwable) {
                var9_9 = throwable;
                throw throwable;
            }
            return newP;
        }
        return null;
    }

    @Override
    public XWPFTable insertNewTbl(XmlCursor cursor) {
        if (this.isCursorInHdrF(cursor)) {
            String uri = CTTbl.type.getName().getNamespaceURI();
            String localPart = "tbl";
            cursor.beginElement(localPart, uri);
            cursor.toParent();
            CTTbl t = (CTTbl)cursor.getObject();
            XWPFTable newT = new XWPFTable(t, this);
            cursor.removeXmlContents();
            XmlObject o = null;
            while (!(o instanceof CTTbl) && cursor.toPrevSibling()) {
                o = cursor.getObject();
            }
            if (!(o instanceof CTTbl)) {
                this.tables.add(0, newT);
            } else {
                int pos = this.tables.indexOf(this.getTable((CTTbl)o)) + 1;
                this.tables.add(pos, newT);
            }
            int i = 0;
            try (XmlCursor cursor2 = t.newCursor();){
                while (cursor2.toPrevSibling()) {
                    o = cursor2.getObject();
                    if (!(o instanceof CTP) && !(o instanceof CTTbl)) continue;
                    ++i;
                }
            }
            this.bodyElements.add(i, newT);
            var9_9 = null;
            try (XmlCursor cursor3 = t.newCursor();){
                cursor.toCursor(cursor3);
                cursor.toEndToken();
            }
            catch (Throwable throwable) {
                var9_9 = throwable;
                throw throwable;
            }
            return newT;
        }
        return null;
    }

    private boolean isCursorInHdrF(XmlCursor cursor) {
        try (XmlCursor verify = cursor.newCursor();){
            verify.toParent();
            boolean bl = verify.getObject() == this.headerFooter;
            return bl;
        }
    }

    public POIXMLDocumentPart getOwner() {
        return this;
    }

    @Override
    public XWPFTable getTableArray(int pos) {
        if (pos >= 0 && pos < this.tables.size()) {
            return this.tables.get(pos);
        }
        return null;
    }

    @Override
    public void insertTable(int pos, XWPFTable table) {
        this.bodyElements.add(pos, table);
        int i = 0;
        for (CTTbl tbl : this.headerFooter.getTblArray()) {
            if (tbl == table.getCTTbl()) break;
            ++i;
        }
        this.tables.add(i, table);
    }

    public void readHdrFtr() {
        this.bodyElements = new ArrayList<IBodyElement>();
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        try (XmlCursor cursor = this.headerFooter.newCursor();){
            cursor.selectPath("./*");
            while (cursor.toNextSelection()) {
                XmlObject o = cursor.getObject();
                if (o instanceof CTP) {
                    XWPFParagraph p = new XWPFParagraph((CTP)o, this);
                    this.paragraphs.add(p);
                    this.bodyElements.add(p);
                }
                if (!(o instanceof CTTbl)) continue;
                XWPFTable t = new XWPFTable((CTTbl)o, this);
                this.tables.add(t);
                this.bodyElements.add(t);
            }
        }
    }

    @Override
    public XWPFTableCell getTableCell(CTTc cell) {
        CTRow row;
        XmlObject o;
        try (XmlCursor cursor = cell.newCursor();){
            cursor.toParent();
            o = cursor.getObject();
            if (!(o instanceof CTRow)) {
                XWPFTableCell xWPFTableCell = null;
                return xWPFTableCell;
            }
            row = (CTRow)o;
            cursor.toParent();
            o = cursor.getObject();
        }
        if (!(o instanceof CTTbl)) {
            return null;
        }
        CTTbl tbl = (CTTbl)o;
        XWPFTable table = this.getTable(tbl);
        if (table == null) {
            return null;
        }
        XWPFTableRow tableRow = table.getRow(row);
        return tableRow.getTableCell(cell);
    }

    @Override
    public XWPFDocument getXWPFDocument() {
        if (this.document != null) {
            return this.document;
        }
        return (XWPFDocument)this.getParent();
    }

    public void setXWPFDocument(XWPFDocument doc) {
        this.document = doc;
    }

    @Override
    public POIXMLDocumentPart getPart() {
        return this;
    }

    @Override
    protected void prepareForCommit() {
        if (this.bodyElements.isEmpty()) {
            this.createParagraph();
        }
        for (XWPFTable tbl : this.tables) {
            for (XWPFTableRow row : tbl.tableRows) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    if (!cell.getBodyElements().isEmpty()) continue;
                    cell.addParagraph();
                }
            }
        }
        super.prepareForCommit();
    }
}

