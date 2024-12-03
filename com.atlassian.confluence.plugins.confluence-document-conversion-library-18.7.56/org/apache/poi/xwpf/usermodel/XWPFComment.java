/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xwpf.usermodel.BodyType;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFComments;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTComment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;

public class XWPFComment
implements IBody {
    protected CTComment ctComment;
    protected XWPFComments comments;
    protected XWPFDocument document;
    private List<XWPFParagraph> paragraphs = new ArrayList<XWPFParagraph>();
    private List<XWPFTable> tables = new ArrayList<XWPFTable>();
    private List<IBodyElement> bodyElements = new ArrayList<IBodyElement>();

    public XWPFComment(CTComment ctComment, XWPFComments comments) {
        this.comments = comments;
        this.ctComment = ctComment;
        this.document = comments.getXWPFDocument();
        this.init();
    }

    protected void init() {
        try (XmlCursor cursor = this.ctComment.newCursor();){
            cursor.selectPath("./*");
            while (cursor.toNextSelection()) {
                XmlObject o = cursor.getObject();
                if (o instanceof CTP) {
                    XWPFParagraph p = new XWPFParagraph((CTP)o, this);
                    this.bodyElements.add(p);
                    this.paragraphs.add(p);
                    continue;
                }
                if (o instanceof CTTbl) {
                    XWPFTable t = new XWPFTable((CTTbl)o, this);
                    this.bodyElements.add(t);
                    this.tables.add(t);
                    continue;
                }
                if (!(o instanceof CTSdtBlock)) continue;
                XWPFSDT c = new XWPFSDT((CTSdtBlock)o, (IBody)this);
                this.bodyElements.add(c);
            }
        }
    }

    @Override
    public POIXMLDocumentPart getPart() {
        return this.comments;
    }

    @Override
    public BodyType getPartType() {
        return BodyType.COMMENT;
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
    public List<XWPFTable> getTables() {
        return Collections.unmodifiableList(this.tables);
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
    public XWPFParagraph getParagraphArray(int pos) {
        if (pos >= 0 && pos < this.paragraphs.size()) {
            return this.paragraphs.get(pos);
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
    public XWPFParagraph insertNewParagraph(XmlCursor cursor) {
        if (this.isCursorInCmt(cursor)) {
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
            p2 = p.newCursor();
            var9_9 = null;
            try {
                cursor.toCursor(p2);
                cursor.toEndToken();
            }
            catch (Throwable throwable) {
                var9_9 = throwable;
                throw throwable;
            }
            finally {
                if (p2 != null) {
                    if (var9_9 != null) {
                        try {
                            p2.close();
                        }
                        catch (Throwable throwable) {
                            var9_9.addSuppressed(throwable);
                        }
                    } else {
                        p2.close();
                    }
                }
            }
            return newP;
        }
        return null;
    }

    private boolean isCursorInCmt(XmlCursor cursor) {
        try (XmlCursor verify = cursor.newCursor();){
            verify.toParent();
            boolean bl = verify.getObject() == this.ctComment;
            return bl;
        }
    }

    @Override
    public XWPFTable insertNewTbl(XmlCursor cursor) {
        if (this.isCursorInCmt(cursor)) {
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
            cursor2 = t.newCursor();
            var9_9 = null;
            try {
                cursor.toCursor(cursor2);
                cursor.toEndToken();
            }
            catch (Throwable throwable) {
                var9_9 = throwable;
                throw throwable;
            }
            finally {
                if (cursor2 != null) {
                    if (var9_9 != null) {
                        try {
                            cursor2.close();
                        }
                        catch (Throwable throwable) {
                            var9_9.addSuppressed(throwable);
                        }
                    } else {
                        cursor2.close();
                    }
                }
            }
            return newT;
        }
        return null;
    }

    @Override
    public void insertTable(int pos, XWPFTable table) {
        this.bodyElements.add(pos, table);
        int i = 0;
        for (CTTbl tbl : this.ctComment.getTblList()) {
            if (tbl == table.getCTTbl()) break;
            ++i;
        }
        this.tables.add(i, table);
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
        return this.document;
    }

    public String getText() {
        StringBuilder text = new StringBuilder();
        for (XWPFParagraph p : this.paragraphs) {
            if (text.length() > 0) {
                text.append("\n");
            }
            text.append(p.getText());
        }
        return text.toString();
    }

    public XWPFParagraph createParagraph() {
        XWPFParagraph paragraph = new XWPFParagraph(this.ctComment.addNewP(), this);
        this.paragraphs.add(paragraph);
        this.bodyElements.add(paragraph);
        return paragraph;
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

    public XWPFTable createTable(int rows, int cols) {
        XWPFTable table = new XWPFTable(this.ctComment.addNewTbl(), this, rows, cols);
        this.tables.add(table);
        this.bodyElements.add(table);
        return table;
    }

    public CTComment getCtComment() {
        return this.ctComment;
    }

    public XWPFComments getComments() {
        return this.comments;
    }

    public String getId() {
        return this.ctComment.getId().toString();
    }

    public String getAuthor() {
        return this.ctComment.getAuthor();
    }

    public void setAuthor(String author) {
        this.ctComment.setAuthor(author);
    }

    public String getInitials() {
        return this.ctComment.getInitials();
    }

    public void setInitials(String initials) {
        this.ctComment.setInitials(initials);
    }

    public Calendar getDate() {
        return this.ctComment.getDate();
    }

    public void setDate(Calendar date) {
        this.ctComment.setDate(date);
    }
}

