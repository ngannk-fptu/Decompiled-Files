/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.extract.xml.slides;

import com.atlassian.confluence.plugins.conversion.extract.xml.SecureXmlUtils;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesSlide;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdListEntry;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMaster;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdListEntry;
import org.openxmlformats.schemas.presentationml.x2006.main.CmLstDocument;
import org.openxmlformats.schemas.presentationml.x2006.main.NotesDocument;
import org.openxmlformats.schemas.presentationml.x2006.main.PresentationDocument;
import org.openxmlformats.schemas.presentationml.x2006.main.SldDocument;
import org.openxmlformats.schemas.presentationml.x2006.main.SldMasterDocument;
import org.xml.sax.SAXException;

public class ExtendedXSLFSlideShow
extends XMLSlideShow {
    public static final String MAIN_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml";
    public static final String NOTES_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.notesSlide+xml";
    public static final String SLIDE_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.slide+xml";
    public static final String SLIDE_LAYOUT_RELATION_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout";
    public static final String NOTES_RELATION_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/notesSlide";
    public static final String COMMENT_RELATION_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/comments";
    private PresentationDocument presentationDoc = (PresentationDocument)PresentationDocument.Factory.parse(this.getCorePart().getInputStream(), SecureXmlUtils.createSecureXmlOptions());
    private List<PackagePart> embedds = new LinkedList<PackagePart>();
    private CTSlideIdListEntry[] slideArray;

    public ExtendedXSLFSlideShow(OPCPackage container) throws OpenXML4JException, IOException, XmlException, ParserConfigurationException, SAXException {
        super(container);
        for (CTSlideIdListEntry ctSlide : this.slideArray = this.getSlideReferences().getSldIdArray()) {
            PackagePart slidePart = this.getTargetPart(this.getCorePart().getRelationship(ctSlide.getId2()));
            for (PackageRelationship rel : slidePart.getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/oleObject")) {
                this.embedds.add(this.getTargetPart(rel));
            }
            for (PackageRelationship rel : slidePart.getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/package")) {
                this.embedds.add(this.getTargetPart(rel));
            }
        }
    }

    public CTSlideIdListEntry[] getSlideArray() {
        return this.slideArray;
    }

    public ExtendedXSLFSlideShow(String file) throws OpenXML4JException, IOException, XmlException, SAXException, ParserConfigurationException {
        this(ExtendedXSLFSlideShow.openPackage(file));
    }

    public CTPresentation getPresentation() {
        return this.presentationDoc.getPresentation();
    }

    public CTSlideIdList getSlideReferences() {
        return this.getPresentation().getSldIdLst();
    }

    public CTSlideMasterIdList getSlideMasterReferences() {
        return this.getPresentation().getSldMasterIdLst();
    }

    public PackagePart getSlideMasterPart(CTSlideMasterIdListEntry master) throws XmlException {
        try {
            return this.getTargetPart(this.getCorePart().getRelationship(master.getId2()));
        }
        catch (InvalidFormatException e) {
            throw new XmlException(e);
        }
    }

    public CTSlideMaster getSlideMaster(CTSlideMasterIdListEntry master) throws IOException, XmlException, SAXException, ParserConfigurationException {
        PackagePart masterPart = this.getSlideMasterPart(master);
        SldMasterDocument masterDoc = (SldMasterDocument)SldMasterDocument.Factory.parse(masterPart.getInputStream(), SecureXmlUtils.createSecureXmlOptions());
        return masterDoc.getSldMaster();
    }

    public PackagePart getSlidePart(CTSlideIdListEntry slide) throws XmlException {
        try {
            return this.getTargetPart(this.getCorePart().getRelationship(slide.getId2()));
        }
        catch (InvalidFormatException e) {
            throw new XmlException(e);
        }
    }

    public CTSlide getSlide(CTSlideIdListEntry slide) throws IOException, XmlException, SAXException, ParserConfigurationException {
        PackagePart slidePart = this.getSlidePart(slide);
        SldDocument slideDoc = (SldDocument)SldDocument.Factory.parse(slidePart.getInputStream(), SecureXmlUtils.createSecureXmlOptions());
        return slideDoc.getSld();
    }

    public PackagePart getNodesPart(CTSlideIdListEntry parentSlide) throws IOException, XmlException {
        PackageRelationshipCollection notes;
        PackagePart slidePart = this.getSlidePart(parentSlide);
        try {
            notes = slidePart.getRelationshipsByType(NOTES_RELATION_TYPE);
        }
        catch (InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
        if (notes.size() == 0) {
            return null;
        }
        if (notes.size() > 1) {
            throw new IllegalStateException("Expecting 0 or 1 notes for a slide, but found " + notes.size());
        }
        try {
            return this.getTargetPart(notes.getRelationship(0));
        }
        catch (InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
    }

    public CTNotesSlide getNotes(CTSlideIdListEntry slide) throws IOException, XmlException, SAXException, ParserConfigurationException {
        PackagePart notesPart = this.getNodesPart(slide);
        if (notesPart == null) {
            return null;
        }
        NotesDocument notesDoc = (NotesDocument)NotesDocument.Factory.parse(notesPart.getInputStream(), SecureXmlUtils.createSecureXmlOptions());
        return notesDoc.getNotes();
    }

    public CTCommentList getSlideComments(CTSlideIdListEntry slide) throws IOException, XmlException, SAXException, ParserConfigurationException {
        PackageRelationshipCollection commentRels;
        PackagePart slidePart = this.getSlidePart(slide);
        try {
            commentRels = slidePart.getRelationshipsByType(COMMENT_RELATION_TYPE);
        }
        catch (InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
        if (commentRels.size() == 0) {
            return null;
        }
        if (commentRels.size() > 1) {
            throw new IllegalStateException("Expecting 0 or 1 comments for a slide, but found " + commentRels.size());
        }
        try {
            PackagePart cPart = this.getTargetPart(commentRels.getRelationship(0));
            CmLstDocument commDoc = (CmLstDocument)CmLstDocument.Factory.parse(cPart.getInputStream(), SecureXmlUtils.createSecureXmlOptions());
            return commDoc.getCmLst();
        }
        catch (InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<PackagePart> getAllEmbeddedParts() {
        return this.embedds;
    }
}

