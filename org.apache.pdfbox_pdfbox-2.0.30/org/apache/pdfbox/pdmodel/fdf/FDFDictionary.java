/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDSimpleFileSpecification;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationCaret;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationCircle;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationFileAttachment;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationFreeText;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationHighlight;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationInk;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationLine;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationLink;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationPolygon;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationPolyline;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationSound;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationSquare;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationSquiggly;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationStamp;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationStrikeOut;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationText;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationUnderline;
import org.apache.pdfbox.pdmodel.fdf.FDFField;
import org.apache.pdfbox.pdmodel.fdf.FDFJavaScript;
import org.apache.pdfbox.pdmodel.fdf.FDFPage;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FDFDictionary
implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(FDFDictionary.class);
    private COSDictionary fdf;

    public FDFDictionary() {
        this.fdf = new COSDictionary();
    }

    public FDFDictionary(COSDictionary fdfDictionary) {
        this.fdf = fdfDictionary;
    }

    public FDFDictionary(Element fdfXML) {
        this();
        NodeList nodeList = fdfXML.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (!(node instanceof Element)) continue;
            Element child = (Element)node;
            if (child.getTagName().equals("f")) {
                PDSimpleFileSpecification fs = new PDSimpleFileSpecification();
                fs.setFile(child.getAttribute("href"));
                this.setFile(fs);
                continue;
            }
            if (child.getTagName().equals("ids")) {
                COSArray ids = new COSArray();
                String original = child.getAttribute("original");
                String modified = child.getAttribute("modified");
                try {
                    ids.add(COSString.parseHex(original));
                }
                catch (IOException e) {
                    LOG.warn((Object)("Error parsing ID entry for attribute 'original' [" + original + "]. ID entry ignored."), (Throwable)e);
                }
                try {
                    ids.add(COSString.parseHex(modified));
                }
                catch (IOException e) {
                    LOG.warn((Object)("Error parsing ID entry for attribute 'modified' [" + modified + "]. ID entry ignored."), (Throwable)e);
                }
                this.setID(ids);
                continue;
            }
            if (child.getTagName().equals("fields")) {
                NodeList fields = child.getChildNodes();
                ArrayList<FDFField> fieldList = new ArrayList<FDFField>();
                for (int f = 0; f < fields.getLength(); ++f) {
                    Node currentNode = fields.item(f);
                    if (!(currentNode instanceof Element) || !((Element)currentNode).getTagName().equals("field")) continue;
                    try {
                        fieldList.add(new FDFField((Element)fields.item(f)));
                        continue;
                    }
                    catch (IOException e) {
                        LOG.warn((Object)("Error parsing field entry [" + currentNode.getNodeValue() + "]. Field ignored."), (Throwable)e);
                    }
                }
                this.setFields(fieldList);
                continue;
            }
            if (!child.getTagName().equals("annots")) continue;
            NodeList annots = child.getChildNodes();
            ArrayList<FDFAnnotation> annotList = new ArrayList<FDFAnnotation>();
            for (int j = 0; j < annots.getLength(); ++j) {
                Node annotNode = annots.item(j);
                if (!(annotNode instanceof Element)) continue;
                Element annot = (Element)annotNode;
                String annotationName = annot.getNodeName();
                try {
                    if (annotationName.equals("text")) {
                        annotList.add(new FDFAnnotationText(annot));
                        continue;
                    }
                    if (annotationName.equals("caret")) {
                        annotList.add(new FDFAnnotationCaret(annot));
                        continue;
                    }
                    if (annotationName.equals("freetext")) {
                        annotList.add(new FDFAnnotationFreeText(annot));
                        continue;
                    }
                    if (annotationName.equals("fileattachment")) {
                        annotList.add(new FDFAnnotationFileAttachment(annot));
                        continue;
                    }
                    if (annotationName.equals("highlight")) {
                        annotList.add(new FDFAnnotationHighlight(annot));
                        continue;
                    }
                    if (annotationName.equals("ink")) {
                        annotList.add(new FDFAnnotationInk(annot));
                        continue;
                    }
                    if (annotationName.equals("line")) {
                        annotList.add(new FDFAnnotationLine(annot));
                        continue;
                    }
                    if (annotationName.equals("link")) {
                        annotList.add(new FDFAnnotationLink(annot));
                        continue;
                    }
                    if (annotationName.equals("circle")) {
                        annotList.add(new FDFAnnotationCircle(annot));
                        continue;
                    }
                    if (annotationName.equals("square")) {
                        annotList.add(new FDFAnnotationSquare(annot));
                        continue;
                    }
                    if (annotationName.equals("polygon")) {
                        annotList.add(new FDFAnnotationPolygon(annot));
                        continue;
                    }
                    if (annotationName.equals("polyline")) {
                        annotList.add(new FDFAnnotationPolyline(annot));
                        continue;
                    }
                    if (annotationName.equals("sound")) {
                        annotList.add(new FDFAnnotationSound(annot));
                        continue;
                    }
                    if (annotationName.equals("squiggly")) {
                        annotList.add(new FDFAnnotationSquiggly(annot));
                        continue;
                    }
                    if (annotationName.equals("stamp")) {
                        annotList.add(new FDFAnnotationStamp(annot));
                        continue;
                    }
                    if (annotationName.equals("strikeout")) {
                        annotList.add(new FDFAnnotationStrikeOut(annot));
                        continue;
                    }
                    if (annotationName.equals("underline")) {
                        annotList.add(new FDFAnnotationUnderline(annot));
                        continue;
                    }
                    LOG.warn((Object)("Unknown or unsupported annotation type '" + annotationName + "'"));
                    continue;
                }
                catch (IOException e) {
                    LOG.warn((Object)("Error parsing annotation information [" + annot.getNodeValue() + "]. Annotation ignored"), (Throwable)e);
                }
            }
            this.setAnnotations(annotList);
        }
    }

    public void writeXML(Writer output) throws IOException {
        List<FDFField> fields;
        COSArray ids;
        PDFileSpecification fs = this.getFile();
        if (fs != null) {
            output.write("<f href=\"" + fs.getFile() + "\" />\n");
        }
        if ((ids = this.getID()) != null) {
            COSString original = (COSString)ids.getObject(0);
            COSString modified = (COSString)ids.getObject(1);
            output.write("<ids original=\"" + original.toHexString() + "\" ");
            output.write("modified=\"" + modified.toHexString() + "\" />\n");
        }
        if ((fields = this.getFields()) != null && fields.size() > 0) {
            output.write("<fields>\n");
            for (FDFField field : fields) {
                field.writeXML(output);
            }
            output.write("</fields>\n");
        }
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.fdf;
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.fdf.getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        this.fdf.setItem(COSName.F, (COSObjectable)fs);
    }

    public COSArray getID() {
        return (COSArray)this.fdf.getDictionaryObject(COSName.ID);
    }

    public void setID(COSArray id) {
        this.fdf.setItem(COSName.ID, (COSBase)id);
    }

    public List<FDFField> getFields() {
        COSArrayList retval = null;
        COSArray fieldArray = (COSArray)this.fdf.getDictionaryObject(COSName.FIELDS);
        if (fieldArray != null) {
            ArrayList<FDFField> fields = new ArrayList<FDFField>();
            for (int i = 0; i < fieldArray.size(); ++i) {
                fields.add(new FDFField((COSDictionary)fieldArray.getObject(i)));
            }
            retval = new COSArrayList(fields, fieldArray);
        }
        return retval;
    }

    public void setFields(List<FDFField> fields) {
        this.fdf.setItem(COSName.FIELDS, (COSBase)COSArrayList.converterToCOSArray(fields));
    }

    public String getStatus() {
        return this.fdf.getString(COSName.STATUS);
    }

    public void setStatus(String status) {
        this.fdf.setString(COSName.STATUS, status);
    }

    public List<FDFPage> getPages() {
        COSArrayList retval = null;
        COSArray pageArray = (COSArray)this.fdf.getDictionaryObject(COSName.PAGES);
        if (pageArray != null) {
            ArrayList<FDFPage> pages = new ArrayList<FDFPage>();
            for (int i = 0; i < pageArray.size(); ++i) {
                pages.add(new FDFPage((COSDictionary)pageArray.get(i)));
            }
            retval = new COSArrayList(pages, pageArray);
        }
        return retval;
    }

    public void setPages(List<FDFPage> pages) {
        this.fdf.setItem(COSName.PAGES, (COSBase)COSArrayList.converterToCOSArray(pages));
    }

    public String getEncoding() {
        String encoding = this.fdf.getNameAsString(COSName.ENCODING);
        if (encoding == null) {
            encoding = "PDFDocEncoding";
        }
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.fdf.setName(COSName.ENCODING, encoding);
    }

    public List<FDFAnnotation> getAnnotations() throws IOException {
        COSArrayList retval = null;
        COSArray annotArray = (COSArray)this.fdf.getDictionaryObject(COSName.ANNOTS);
        if (annotArray != null) {
            ArrayList<FDFAnnotation> annots = new ArrayList<FDFAnnotation>();
            for (int i = 0; i < annotArray.size(); ++i) {
                annots.add(FDFAnnotation.create((COSDictionary)annotArray.getObject(i)));
            }
            retval = new COSArrayList(annots, annotArray);
        }
        return retval;
    }

    public void setAnnotations(List<FDFAnnotation> annots) {
        this.fdf.setItem(COSName.ANNOTS, (COSBase)COSArrayList.converterToCOSArray(annots));
    }

    public COSStream getDifferences() {
        return (COSStream)this.fdf.getDictionaryObject(COSName.DIFFERENCES);
    }

    public void setDifferences(COSStream diff) {
        this.fdf.setItem(COSName.DIFFERENCES, (COSBase)diff);
    }

    public String getTarget() {
        return this.fdf.getString(COSName.TARGET);
    }

    public void setTarget(String target) {
        this.fdf.setString(COSName.TARGET, target);
    }

    public List<PDFileSpecification> getEmbeddedFDFs() throws IOException {
        COSArrayList retval = null;
        COSArray embeddedArray = (COSArray)this.fdf.getDictionaryObject(COSName.EMBEDDED_FDFS);
        if (embeddedArray != null) {
            ArrayList<PDFileSpecification> embedded = new ArrayList<PDFileSpecification>();
            for (int i = 0; i < embeddedArray.size(); ++i) {
                embedded.add(PDFileSpecification.createFS(embeddedArray.get(i)));
            }
            retval = new COSArrayList(embedded, embeddedArray);
        }
        return retval;
    }

    public void setEmbeddedFDFs(List<PDFileSpecification> embedded) {
        this.fdf.setItem(COSName.EMBEDDED_FDFS, (COSBase)COSArrayList.converterToCOSArray(embedded));
    }

    public FDFJavaScript getJavaScript() {
        FDFJavaScript fs = null;
        COSDictionary dic = (COSDictionary)this.fdf.getDictionaryObject(COSName.JAVA_SCRIPT);
        if (dic != null) {
            fs = new FDFJavaScript(dic);
        }
        return fs;
    }

    public void setJavaScript(FDFJavaScript js) {
        this.fdf.setItem(COSName.JAVA_SCRIPT, (COSObjectable)js);
    }
}

