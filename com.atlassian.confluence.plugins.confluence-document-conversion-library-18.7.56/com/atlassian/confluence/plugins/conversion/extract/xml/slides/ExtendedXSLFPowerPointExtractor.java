/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.extract.xml.slides;

import com.atlassian.confluence.plugins.conversion.extract.xml.slides.ExtendedXSLFSlideShow;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.extractor.XSLFExtractor;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTable;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesSlide;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdListEntry;
import org.xml.sax.SAXException;

public class ExtendedXSLFPowerPointExtractor
extends XSLFExtractor {
    private static final String DRAWINGML_NAMESPACE = "http://schemas.openxmlformats.org/drawingml/2006/main";
    private ExtendedXSLFSlideShow slideshow;
    private boolean slidesByDefault = true;
    private boolean notesByDefault = false;

    public ExtendedXSLFPowerPointExtractor(ExtendedXSLFSlideShow slideshow) {
        super(slideshow);
        this.slideshow = slideshow;
    }

    public ExtendedXSLFPowerPointExtractor(OPCPackage container) throws XmlException, OpenXML4JException, IOException, SAXException, ParserConfigurationException {
        this(new ExtendedXSLFSlideShow(container));
    }

    @Override
    public void setSlidesByDefault(boolean slidesByDefault) {
        this.slidesByDefault = slidesByDefault;
    }

    @Override
    public void setNotesByDefault(boolean notesByDefault) {
        this.notesByDefault = notesByDefault;
    }

    @Override
    public String getText() {
        try {
            return this.getText(this.slidesByDefault, this.notesByDefault);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (XmlException e) {
            throw new RuntimeException(e);
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public String getText(boolean slideText, boolean notesText) throws IOException, XmlException, SAXException, ParserConfigurationException {
        StringBuilder text = new StringBuilder();
        CTSlideIdListEntry[] slideArray = this.slideshow.getSlideArray();
        for (int i = 0; i < slideArray.length; ++i) {
            CTSlideIdListEntry slideId = slideArray[i];
            CTSlide rawSlide = this.slideshow.getSlide(slideArray[i]);
            try {
                CTNotesSlide notes = this.slideshow.getNotes(slideId);
                CTCommentList comments = this.slideshow.getSlideComments(slideId);
                if (slideText) {
                    this.extractText(rawSlide.getCSld().getSpTree(), text);
                    if (comments != null) {
                        for (CTComment comment : comments.getCmArray()) {
                            text.append(comment.getText()).append("\n");
                        }
                    }
                }
                if (!notesText || notes == null) continue;
                this.extractText(notes.getCSld().getSpTree(), text);
                continue;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return text.toString();
    }

    private void extractText(CTGroupShape gs, StringBuilder text) {
        CTGraphicalObjectFrame[] frames;
        CTShape[] shapes = gs.getSpArray();
        for (int i = 0; i < shapes.length; ++i) {
            CTTextBody textBody = shapes[i].getTxBody();
            if (textBody == null) continue;
            CTTextParagraph[] paras = textBody.getPArray();
            for (int j = 0; j < paras.length; ++j) {
                XmlCursor c = paras[j].newCursor();
                c.selectPath("./*");
                while (c.toNextSelection()) {
                    XmlObject o = c.getObject();
                    if (o instanceof CTRegularTextRun) {
                        CTRegularTextRun txrun = (CTRegularTextRun)o;
                        text.append(txrun.getT());
                        continue;
                    }
                    if (!(o instanceof CTTextLineBreak)) continue;
                    text.append('\n');
                }
                text.append("\n");
            }
        }
        for (CTGraphicalObjectFrame frame : frames = gs.getGraphicFrameArray()) {
            CTGraphicalObjectData graphicData = frame.getGraphic().getGraphicData();
            XmlCursor c = graphicData.newCursor();
            c.selectPath("./*");
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                if (!(o instanceof CTTable)) continue;
                this.extractText((CTTable)o, text);
            }
        }
    }

    private void extractText(CTTable gs, StringBuilder text) {
        XmlObject[] textParts;
        String declaration = "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main';";
        for (XmlObject textPart : textParts = gs.selectPath(declaration + "$this//a:t")) {
            if (!(textPart instanceof XmlAnySimpleType)) continue;
            text.append(((XmlAnySimpleType)textPart).getStringValue());
        }
    }
}

