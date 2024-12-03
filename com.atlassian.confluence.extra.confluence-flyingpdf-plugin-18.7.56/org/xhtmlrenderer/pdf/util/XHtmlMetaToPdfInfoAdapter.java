/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf.util;

import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.pdf.DefaultPDFCreationListener;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.util.XRLog;

public class XHtmlMetaToPdfInfoAdapter
extends DefaultPDFCreationListener {
    private static final String HTML_TAG_TITLE = "title";
    private static final String HTML_TAG_HEAD = "head";
    private static final String HTML_TAG_META = "meta";
    private static final String HTML_META_KEY_TITLE = "title";
    private static final String HTML_META_KEY_DC_TITLE = "DC.title";
    private static final String HTML_META_KEY_CREATOR = "creator";
    private static final String HTML_META_KEY_DC_CREATOR = "DC.creator";
    private static final String HTML_META_KEY_SUBJECT = "subject";
    private static final String HTML_META_KEY_DC_SUBJECT = "DC.subject";
    private static final String HTML_META_KEY_KEYWORDS = "keywords";
    private static final String HTML_META_ATTR_NAME = "name";
    private static final String HTML_META_ATTR_CONTENT = "content";
    private Map pdfInfoValues = new HashMap();

    public XHtmlMetaToPdfInfoAdapter(Document doc) {
        this.parseHtmlTags(doc);
    }

    @Override
    public void onClose(ITextRenderer renderer) {
        XRLog.render(Level.FINEST, "handling onClose event ...");
        this.addPdfMetaValuesToPdfDocument(renderer);
    }

    private void parseHtmlTags(Document doc) {
        XRLog.render(Level.FINEST, "parsing (X)HTML tags ...");
        this.parseHtmlTitleTag(doc);
        this.parseHtmlMetaTags(doc);
        if (XRLog.isLoggingEnabled()) {
            XRLog.render(Level.FINEST, "PDF info map = " + this.pdfInfoValues);
        }
    }

    private void parseHtmlTitleTag(Document doc) {
        NodeList headNodeList = doc.getDocumentElement().getElementsByTagName(HTML_TAG_HEAD);
        XRLog.render(Level.FINEST, "headNodeList=" + headNodeList);
        Element rootHeadNodeElement = (Element)headNodeList.item(0);
        NodeList titleNodeList = rootHeadNodeElement.getElementsByTagName("title");
        XRLog.render(Level.FINEST, "titleNodeList=" + titleNodeList);
        Element titleElement = (Element)titleNodeList.item(0);
        if (titleElement != null) {
            XRLog.render(Level.FINEST, "titleElement=" + titleElement);
            XRLog.render(Level.FINEST, "titleElement.name=" + titleElement.getTagName());
            XRLog.render(Level.FINEST, "titleElement.value=" + titleElement.getNodeValue());
            XRLog.render(Level.FINEST, "titleElement.content=" + titleElement.getTextContent());
            String titleContent = titleElement.getTextContent();
            PdfName pdfName = PdfName.TITLE;
            PdfString pdfString = new PdfString(titleContent);
            this.pdfInfoValues.put(pdfName, pdfString);
        }
    }

    private void parseHtmlMetaTags(Document doc) {
        NodeList headNodeList = doc.getDocumentElement().getElementsByTagName(HTML_TAG_HEAD);
        XRLog.render(Level.FINEST, "headNodeList=" + headNodeList);
        Element rootHeadNodeElement = (Element)headNodeList.item(0);
        NodeList metaNodeList = rootHeadNodeElement.getElementsByTagName(HTML_TAG_META);
        XRLog.render(Level.FINEST, "metaNodeList=" + metaNodeList);
        for (int inode = 0; inode < metaNodeList.getLength(); ++inode) {
            XRLog.render(Level.FINEST, "node " + inode + " = " + metaNodeList.item(inode).getNodeName());
            Element thisNode = (Element)metaNodeList.item(inode);
            XRLog.render(Level.FINEST, "node " + thisNode);
            String metaName = thisNode.getAttribute(HTML_META_ATTR_NAME);
            String metaContent = thisNode.getAttribute(HTML_META_ATTR_CONTENT);
            XRLog.render(Level.FINEST, "metaName=" + metaName + ", metaContent=" + metaContent);
            if (metaName.length() == 0 || metaContent.length() == 0) continue;
            PdfName pdfName = null;
            PdfString pdfString = null;
            if ("title".equalsIgnoreCase(metaName) || HTML_META_KEY_DC_TITLE.equalsIgnoreCase(metaName)) {
                pdfName = PdfName.TITLE;
                pdfString = new PdfString(metaContent, "UnicodeBig");
                this.pdfInfoValues.put(pdfName, pdfString);
                continue;
            }
            if (HTML_META_KEY_CREATOR.equalsIgnoreCase(metaName) || HTML_META_KEY_DC_CREATOR.equalsIgnoreCase(metaName)) {
                pdfName = PdfName.AUTHOR;
                pdfString = new PdfString(metaContent, "UnicodeBig");
                this.pdfInfoValues.put(pdfName, pdfString);
                continue;
            }
            if (HTML_META_KEY_SUBJECT.equalsIgnoreCase(metaName) || HTML_META_KEY_DC_SUBJECT.equalsIgnoreCase(metaName)) {
                pdfName = PdfName.SUBJECT;
                pdfString = new PdfString(metaContent, "UnicodeBig");
                this.pdfInfoValues.put(pdfName, pdfString);
                continue;
            }
            if (!HTML_META_KEY_KEYWORDS.equalsIgnoreCase(metaName)) continue;
            pdfName = PdfName.KEYWORDS;
            pdfString = new PdfString(metaContent, "UnicodeBig");
            this.pdfInfoValues.put(pdfName, pdfString);
        }
    }

    private void addPdfMetaValuesToPdfDocument(ITextRenderer renderer) {
        for (PdfName pdfName : this.pdfInfoValues.keySet()) {
            PdfString pdfString = (PdfString)this.pdfInfoValues.get(pdfName);
            XRLog.render(Level.FINEST, "pdfName=" + pdfName + ", pdfString=" + pdfString);
            renderer.getOutputDevice().getWriter().getInfo().put(pdfName, pdfString);
        }
        if (XRLog.isLoggingEnabled()) {
            XRLog.render(Level.FINEST, "added " + renderer.getOutputDevice().getWriter().getInfo().getKeys());
        }
    }
}

