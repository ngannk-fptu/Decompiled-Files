/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BodyType;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.ICell;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

public class XWPFTableCell
implements IBody,
ICell {
    private static final EnumMap<XWPFVertAlign, STVerticalJc.Enum> alignMap = new EnumMap(XWPFVertAlign.class);
    private static final HashMap<Integer, XWPFVertAlign> stVertAlignTypeMap;
    private final CTTc ctTc;
    protected List<XWPFParagraph> paragraphs;
    protected List<XWPFTable> tables;
    protected List<IBodyElement> bodyElements;
    protected IBody part;
    private final XWPFTableRow tableRow;

    public XWPFTableCell(CTTc cell, XWPFTableRow tableRow, IBody part) {
        this.ctTc = cell;
        this.part = part;
        this.tableRow = tableRow;
        this.bodyElements = new ArrayList<IBodyElement>();
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        try (XmlCursor cursor = this.ctTc.newCursor();){
            cursor.selectPath("./*");
            while (cursor.toNextSelection()) {
                XWPFSDT c;
                XmlObject o = cursor.getObject();
                if (o instanceof CTP) {
                    XWPFParagraph p = new XWPFParagraph((CTP)o, this);
                    this.paragraphs.add(p);
                    this.bodyElements.add(p);
                }
                if (o instanceof CTTbl) {
                    XWPFTable t = new XWPFTable((CTTbl)o, this);
                    this.tables.add(t);
                    this.bodyElements.add(t);
                }
                if (o instanceof CTSdtBlock) {
                    c = new XWPFSDT((CTSdtBlock)o, (IBody)this);
                    this.bodyElements.add(c);
                }
                if (!(o instanceof CTSdtRun)) continue;
                c = new XWPFSDT((CTSdtRun)o, (IBody)this);
                this.bodyElements.add(c);
            }
        }
    }

    @Internal
    public CTTc getCTTc() {
        return this.ctTc;
    }

    @Override
    public List<IBodyElement> getBodyElements() {
        return Collections.unmodifiableList(this.bodyElements);
    }

    public void setParagraph(XWPFParagraph p) {
        if (this.ctTc.sizeOfPArray() == 0) {
            this.ctTc.addNewP();
        }
        this.ctTc.setPArray(0, p.getCTP());
    }

    @Override
    public List<XWPFParagraph> getParagraphs() {
        return Collections.unmodifiableList(this.paragraphs);
    }

    public XWPFParagraph addParagraph() {
        XWPFParagraph p = new XWPFParagraph(this.ctTc.addNewP(), this);
        this.addParagraph(p);
        return p;
    }

    public void addParagraph(XWPFParagraph p) {
        this.paragraphs.add(p);
        this.bodyElements.add(p);
    }

    public void removeParagraph(int pos) {
        XWPFParagraph removedParagraph = this.paragraphs.get(pos);
        this.paragraphs.remove(pos);
        this.ctTc.removeP(pos);
        this.bodyElements.remove(removedParagraph);
    }

    @Override
    public XWPFParagraph getParagraph(CTP p) {
        for (XWPFParagraph paragraph : this.paragraphs) {
            if (!p.equals(paragraph.getCTP())) continue;
            return paragraph;
        }
        return null;
    }

    public XWPFTableRow getTableRow() {
        return this.tableRow;
    }

    public String getColor() {
        CTShd ctshd;
        String color = null;
        CTTcPr tcpr = this.ctTc.getTcPr();
        if (tcpr != null && (ctshd = tcpr.getShd()) != null) {
            color = ctshd.xgetFill().getStringValue();
        }
        return color;
    }

    public void setColor(String rgbStr) {
        CTTcPr tcpr = this.getTcPr();
        CTShd ctshd = tcpr.isSetShd() ? tcpr.getShd() : tcpr.addNewShd();
        ctshd.setColor("auto");
        ctshd.setVal(STShd.CLEAR);
        ctshd.setFill(rgbStr);
    }

    public XWPFVertAlign getVerticalAlignment() {
        CTVerticalJc va;
        XWPFVertAlign vAlign = null;
        CTTcPr tcpr = this.ctTc.getTcPr();
        if (tcpr != null && (va = tcpr.getVAlign()) != null) {
            vAlign = stVertAlignTypeMap.get(va.getVal().intValue());
        }
        return vAlign;
    }

    public void setVerticalAlignment(XWPFVertAlign vAlign) {
        CTTcPr tcpr = this.getTcPr();
        CTVerticalJc va = tcpr.addNewVAlign();
        va.setVal(alignMap.get((Object)vAlign));
    }

    @Override
    public XWPFParagraph insertNewParagraph(XmlCursor cursor) {
        if (!this.isCursorInTableCell(cursor)) {
            return null;
        }
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
        }
        catch (Throwable throwable) {
            var9_9 = throwable;
            throw throwable;
        }
        cursor.toEndToken();
        return newP;
    }

    @Override
    public XWPFTable insertNewTbl(XmlCursor cursor) {
        if (this.isCursorInTableCell(cursor)) {
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

    private boolean isCursorInTableCell(XmlCursor cursor) {
        try (XmlCursor verify = cursor.newCursor();){
            verify.toParent();
            boolean bl = verify.getObject() == this.ctTc;
            return bl;
        }
    }

    @Override
    public XWPFParagraph getParagraphArray(int pos) {
        if (pos >= 0 && pos < this.paragraphs.size()) {
            return this.paragraphs.get(pos);
        }
        return null;
    }

    @Override
    public POIXMLDocumentPart getPart() {
        return this.tableRow.getTable().getPart();
    }

    @Override
    public BodyType getPartType() {
        return BodyType.TABLECELL;
    }

    @Override
    public XWPFTable getTable(CTTbl ctTable) {
        for (int i = 0; i < this.tables.size(); ++i) {
            if (this.getTables().get(i).getCTTbl() != ctTable) continue;
            return this.getTables().get(i);
        }
        return null;
    }

    @Override
    public XWPFTable getTableArray(int pos) {
        if (pos >= 0 && pos < this.tables.size()) {
            return this.tables.get(pos);
        }
        return null;
    }

    @Override
    public List<XWPFTable> getTables() {
        return Collections.unmodifiableList(this.tables);
    }

    @Override
    public void insertTable(int pos, XWPFTable table) {
        this.bodyElements.add(pos, table);
        int i = 0;
        for (CTTbl tbl : this.ctTc.getTblList()) {
            if (tbl == table.getCTTbl()) break;
            ++i;
        }
        this.tables.add(i, table);
    }

    public void removeTable(int pos) {
        XWPFTable removedTable = this.tables.get(pos);
        this.tables.remove(pos);
        this.ctTc.removeTbl(pos);
        this.bodyElements.remove(removedTable);
    }

    public String getText() {
        StringBuilder text = new StringBuilder();
        for (XWPFParagraph p : this.paragraphs) {
            text.append(p.getText());
        }
        return text.toString();
    }

    public void setText(String text) {
        XWPFParagraph par = this.paragraphs.isEmpty() ? this.addParagraph() : this.paragraphs.get(0);
        par.createRun().setText(text);
    }

    public String getTextRecursively() {
        StringBuilder text = new StringBuilder(64);
        for (int i = 0; i < this.bodyElements.size(); ++i) {
            boolean isLast = i == this.bodyElements.size() - 1;
            this.appendBodyElementText(text, this.bodyElements.get(i), isLast);
        }
        return text.toString();
    }

    private void appendBodyElementText(StringBuilder text, IBodyElement e, boolean isLast) {
        if (e instanceof XWPFParagraph) {
            text.append(((XWPFParagraph)e).getText());
            if (!isLast) {
                text.append('\t');
            }
        } else if (e instanceof XWPFTable) {
            XWPFTable eTable = (XWPFTable)e;
            for (XWPFTableRow row : eTable.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    List<IBodyElement> localBodyElements = cell.getBodyElements();
                    for (int i = 0; i < localBodyElements.size(); ++i) {
                        boolean localIsLast = i == localBodyElements.size() - 1;
                        this.appendBodyElementText(text, localBodyElements.get(i), localIsLast);
                    }
                }
            }
            if (!isLast) {
                text.append('\n');
            }
        } else if (e instanceof XWPFSDT) {
            text.append(((XWPFSDT)e).getContent().getText());
            if (!isLast) {
                text.append('\t');
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
        XWPFTableRow tr = table.getRow(row);
        if (tr == null) {
            return null;
        }
        return tr.getTableCell(cell);
    }

    @Override
    public XWPFDocument getXWPFDocument() {
        return this.part.getXWPFDocument();
    }

    public double getWidthDecimal() {
        return XWPFTable.getWidthDecimal(this.getTcWidth());
    }

    public TableWidthType getWidthType() {
        return XWPFTable.getWidthType(this.getTcWidth());
    }

    public void setWidth(String widthValue) {
        XWPFTable.setWidthValue(widthValue, this.getTcWidth());
    }

    private CTTblWidth getTcWidth() {
        CTTcPr tcPr = this.getTcPr();
        return tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
    }

    protected CTTcPr getTcPr() {
        return this.ctTc.isSetTcPr() ? this.ctTc.getTcPr() : this.ctTc.addNewTcPr();
    }

    public void setWidthType(TableWidthType widthType) {
        XWPFTable.setWidthType(widthType, this.getTcWidth());
    }

    public int getWidth() {
        return (int)Units.toDXA(POIXMLUnits.parseLength(this.getTcWidth().xgetW()));
    }

    static {
        alignMap.put(XWPFVertAlign.TOP, STVerticalJc.TOP);
        alignMap.put(XWPFVertAlign.CENTER, STVerticalJc.CENTER);
        alignMap.put(XWPFVertAlign.BOTH, STVerticalJc.BOTH);
        alignMap.put(XWPFVertAlign.BOTTOM, STVerticalJc.BOTTOM);
        stVertAlignTypeMap = new HashMap();
        stVertAlignTypeMap.put(1, XWPFVertAlign.TOP);
        stVertAlignTypeMap.put(2, XWPFVertAlign.CENTER);
        stVertAlignTypeMap.put(3, XWPFVertAlign.BOTH);
        stVertAlignTypeMap.put(4, XWPFVertAlign.BOTTOM);
    }

    public static enum XWPFVertAlign {
        TOP,
        CENTER,
        BOTH,
        BOTTOM;

    }
}

