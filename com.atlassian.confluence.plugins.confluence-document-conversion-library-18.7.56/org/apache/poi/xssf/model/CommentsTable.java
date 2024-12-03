/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.model;

import com.microsoft.schemas.vml.CTShape;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;
import org.apache.poi.xssf.model.Comments;
import org.apache.poi.xssf.usermodel.OoxmlSheetExtensions;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFVMLDrawing;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCommentList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComments;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CommentsDocument;

@Internal
public class CommentsTable
extends POIXMLDocumentPart
implements Comments {
    public static final String DEFAULT_AUTHOR = "";
    public static final int DEFAULT_AUTHOR_ID = 0;
    private Sheet sheet;
    private XSSFVMLDrawing vmlDrawing;
    private CTComments comments;
    private Map<CellAddress, CTComment> commentRefs;

    public CommentsTable() {
        this.comments = CTComments.Factory.newInstance();
        this.comments.addNewCommentList();
        this.comments.addNewAuthors().addAuthor(DEFAULT_AUTHOR);
    }

    public CommentsTable(PackagePart part) throws IOException {
        super(part);
        try (InputStream stream = part.getInputStream();){
            this.readFrom(stream);
        }
    }

    public void readFrom(InputStream is) throws IOException {
        try {
            CommentsDocument doc = (CommentsDocument)CommentsDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.comments = doc.getComments();
        }
        catch (XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }

    public void writeTo(OutputStream out) throws IOException {
        CommentsDocument doc = CommentsDocument.Factory.newInstance();
        doc.setComments(this.comments);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }

    @Override
    @Internal
    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    protected void commit() throws IOException {
        PackagePart part = this.getPackagePart();
        try (OutputStream out = part.getOutputStream();){
            this.writeTo(out);
        }
    }

    @Deprecated
    @Removal(version="6.0.0")
    public void referenceUpdated(CellAddress oldReference, CTComment comment) {
        if (this.commentRefs != null) {
            this.commentRefs.remove(oldReference);
            this.commentRefs.put(new CellAddress(comment.getRef()), comment);
        }
    }

    @Override
    public void referenceUpdated(CellAddress oldReference, XSSFComment comment) {
        if (this.commentRefs != null) {
            this.commentRefs.remove(oldReference);
            this.commentRefs.put(comment.getAddress(), comment.getCTComment());
        }
    }

    @Override
    public void commentUpdated(XSSFComment comment) {
    }

    @Override
    public int getNumberOfComments() {
        return this.comments.getCommentList().sizeOfCommentArray();
    }

    @Override
    public int getNumberOfAuthors() {
        return this.comments.getAuthors().sizeOfAuthorArray();
    }

    @Override
    public String getAuthor(long authorId) {
        return this.comments.getAuthors().getAuthorArray(Math.toIntExact(authorId));
    }

    @Override
    public int findAuthor(String author) {
        String[] authorArray = this.comments.getAuthors().getAuthorArray();
        for (int i = 0; i < authorArray.length; ++i) {
            if (!authorArray[i].equals(author)) continue;
            return i;
        }
        return this.addNewAuthor(author);
    }

    @Override
    public XSSFComment findCellComment(CellAddress cellAddress) {
        CTComment ctComment = this.getCTComment(cellAddress);
        if (ctComment == null) {
            return null;
        }
        XSSFVMLDrawing vml = this.getVMLDrawing(this.sheet, false);
        return new XSSFComment(this, ctComment, vml == null ? null : vml.findCommentShape(cellAddress.getRow(), cellAddress.getColumn()));
    }

    @Internal
    CTComment getCTComment(CellAddress cellRef) {
        this.prepareCTCommentCache();
        return this.commentRefs.get(cellRef);
    }

    @Override
    public Iterator<CellAddress> getCellAddresses() {
        this.prepareCTCommentCache();
        return this.commentRefs.keySet().iterator();
    }

    @Override
    public XSSFComment createNewComment(ClientAnchor clientAnchor) {
        CellAddress ref;
        CTShape vmlShape;
        XSSFVMLDrawing vml = this.getVMLDrawing(this.sheet, true);
        CTShape cTShape = vmlShape = vml == null ? null : vml.newCommentShape();
        if (vmlShape != null && clientAnchor instanceof XSSFClientAnchor && ((XSSFClientAnchor)clientAnchor).isSet()) {
            int dx1Pixels = clientAnchor.getDx1() / 9525;
            int dy1Pixels = clientAnchor.getDy1() / 9525;
            int dx2Pixels = clientAnchor.getDx2() / 9525;
            int dy2Pixels = clientAnchor.getDy2() / 9525;
            String position = clientAnchor.getCol1() + ", " + dx1Pixels + ", " + clientAnchor.getRow1() + ", " + dy1Pixels + ", " + clientAnchor.getCol2() + ", " + dx2Pixels + ", " + clientAnchor.getRow2() + ", " + dy2Pixels;
            vmlShape.getClientDataArray(0).setAnchorArray(0, position);
        }
        if (this.findCellComment(ref = new CellAddress(clientAnchor.getRow1(), clientAnchor.getCol1())) != null) {
            throw new IllegalArgumentException("Multiple cell comments in one cell are not allowed, cell: " + ref);
        }
        return new XSSFComment(this, this.newComment(ref), vmlShape);
    }

    @Internal
    public CTComment newComment(CellAddress ref) {
        CTComment ct = this.comments.getCommentList().addNewComment();
        ct.setRef(ref.formatAsString());
        ct.setAuthorId(0L);
        if (this.commentRefs != null) {
            this.commentRefs.put(ref, ct);
        }
        return ct;
    }

    @Override
    public boolean removeComment(CellAddress cellRef) {
        String stringRef = cellRef.formatAsString();
        CTCommentList lst = this.comments.getCommentList();
        if (lst != null) {
            CTComment[] commentArray = lst.getCommentArray();
            for (int i = 0; i < commentArray.length; ++i) {
                CTComment comment = commentArray[i];
                if (!stringRef.equals(comment.getRef())) continue;
                lst.removeComment(i);
                if (this.commentRefs != null) {
                    this.commentRefs.remove(cellRef);
                }
                return true;
            }
        }
        return false;
    }

    @Internal
    public CTComments getCTComments() {
        return this.comments;
    }

    private void prepareCTCommentCache() {
        if (this.commentRefs == null) {
            this.commentRefs = new HashMap<CellAddress, CTComment>();
            for (CTComment comment : this.comments.getCommentList().getCommentArray()) {
                this.commentRefs.put(new CellAddress(comment.getRef()), comment);
            }
        }
    }

    private int addNewAuthor(String author) {
        int index = this.comments.getAuthors().sizeOfAuthorArray();
        this.comments.getAuthors().insertAuthor(index, author);
        return index;
    }

    private XSSFVMLDrawing getVMLDrawing(Sheet sheet, boolean autocreate) {
        if (this.vmlDrawing == null && sheet instanceof OoxmlSheetExtensions) {
            this.vmlDrawing = ((OoxmlSheetExtensions)((Object)sheet)).getVMLDrawing(autocreate);
        }
        return this.vmlDrawing;
    }
}

