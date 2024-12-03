/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.xwpf.usermodel.XWPFComment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFactory;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTComment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTComments;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CommentsDocument;

public class XWPFComments
extends POIXMLDocumentPart {
    XWPFDocument document;
    private final List<XWPFComment> comments = new ArrayList<XWPFComment>();
    private final List<XWPFPictureData> pictures = new ArrayList<XWPFPictureData>();
    private CTComments ctComments;

    public XWPFComments(POIXMLDocumentPart parent, PackagePart part) {
        super(parent, part);
        this.document = (XWPFDocument)this.getParent();
        if (this.document == null) {
            throw new NullPointerException();
        }
    }

    public XWPFComments() {
        this.ctComments = CTComments.Factory.newInstance();
    }

    @Override
    public void onDocumentRead() throws IOException {
        try (InputStream is = this.getPackagePart().getInputStream();){
            CommentsDocument doc = (CommentsDocument)CommentsDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.ctComments = doc.getComments();
            for (CTComment ctComment : this.ctComments.getCommentList()) {
                this.comments.add(new XWPFComment(ctComment, this));
            }
        }
        catch (XmlException e) {
            throw new POIXMLException("Unable to read comments", e);
        }
        for (POIXMLDocumentPart poixmlDocumentPart : this.getRelations()) {
            if (!(poixmlDocumentPart instanceof XWPFPictureData)) continue;
            XWPFPictureData xwpfPicData = (XWPFPictureData)poixmlDocumentPart;
            this.pictures.add(xwpfPicData);
            this.document.registerPackagePictureData(xwpfPicData);
        }
    }

    public String addPictureData(InputStream is, int format) throws InvalidFormatException, IOException {
        byte[] data = IOUtils.toByteArrayWithMaxLength(is, XWPFPictureData.getMaxImageSize());
        return this.addPictureData(data, format);
    }

    public String addPictureData(InputStream is, PictureType pictureType) throws InvalidFormatException, IOException {
        byte[] data = IOUtils.toByteArrayWithMaxLength(is, XWPFPictureData.getMaxImageSize());
        return this.addPictureData(data, pictureType);
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
            int idx = this.getXWPFDocument().getNextPicNameNumber(pictureType);
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

    @Override
    protected void commit() throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTComments.type.getName().getNamespaceURI(), "comments"));
        PackagePart part = this.getPackagePart();
        try (OutputStream out = part.getOutputStream();){
            this.ctComments.save(out, xmlOptions);
        }
    }

    public List<XWPFPictureData> getAllPictures() {
        return Collections.unmodifiableList(this.pictures);
    }

    public CTComments getCtComments() {
        return this.ctComments;
    }

    @Internal
    public void setCtComments(CTComments ctComments) {
        this.ctComments = ctComments;
    }

    public List<XWPFComment> getComments() {
        return this.comments;
    }

    public XWPFComment getComment(int pos) {
        if (pos >= 0 && pos < this.ctComments.sizeOfCommentArray()) {
            return this.getComments().get(pos);
        }
        return null;
    }

    public XWPFComment getCommentByID(String id) {
        for (XWPFComment comment : this.comments) {
            if (!comment.getId().equals(id)) continue;
            return comment;
        }
        return null;
    }

    public XWPFComment getComment(CTComment ctComment) {
        for (XWPFComment comment : this.comments) {
            if (comment.getCtComment() != ctComment) continue;
            return comment;
        }
        return null;
    }

    public XWPFComment createComment(BigInteger cid) {
        CTComment ctComment = this.ctComments.addNewComment();
        ctComment.setId(cid);
        XWPFComment comment = new XWPFComment(ctComment, this);
        this.comments.add(comment);
        return comment;
    }

    public boolean removeComment(int pos) {
        if (pos >= 0 && pos < this.ctComments.sizeOfCommentArray()) {
            this.comments.remove(pos);
            this.ctComments.removeComment(pos);
            return true;
        }
        return false;
    }

    public XWPFDocument getXWPFDocument() {
        if (null != this.document) {
            return this.document;
        }
        return (XWPFDocument)this.getParent();
    }

    public void setXWPFDocument(XWPFDocument document) {
        this.document = document;
    }
}

