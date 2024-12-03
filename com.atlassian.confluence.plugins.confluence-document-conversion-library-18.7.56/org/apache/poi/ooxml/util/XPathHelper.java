/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ooxml.util;

import com.microsoft.schemas.compatibility.AlternateContentDocument;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.Internal;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

public final class XPathHelper {
    private static final Logger LOG = LogManager.getLogger(XPathHelper.class);
    private static final String OSGI_ERROR = "Schemas (*.xsb) for <CLASS> can't be loaded - usually this happens when OSGI loading is used and the thread context classloader has no reference to the xmlbeans classes - please either verify if the <XSB>.xsb is on the classpath or alternatively try to use the poi-ooxml-full-x.x.jar";
    private static final String MC_NS = "http://schemas.openxmlformats.org/markup-compatibility/2006";
    private static final String MAC_DML_NS = "http://schemas.microsoft.com/office/mac/drawingml/2008/main";
    private static final QName ALTERNATE_CONTENT_TAG = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "AlternateContent");
    static final XPathFactory xpathFactory = XPathFactory.newInstance();

    private XPathHelper() {
    }

    public static XPathFactory getFactory() {
        return xpathFactory;
    }

    private static void trySetFeature(XPathFactory xpf, String feature, boolean enabled) {
        try {
            xpf.setFeature(feature, enabled);
        }
        catch (Exception e) {
            LOG.atWarn().withThrowable(e).log("XPathFactory Feature ({}) unsupported", (Object)feature);
        }
        catch (AbstractMethodError ame) {
            LOG.atWarn().withThrowable(ame).log("Cannot set XPathFactory feature ({}) because outdated XML parser in classpath", (Object)feature);
        }
    }

    /*
     * Exception decompiling
     */
    @Internal
    public static <T extends XmlObject> T selectProperty(XmlObject startObject, Class<T> resultClass, XSLFShape.ReparseFactory<T> factory, QName[] ... path) throws XmlException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 33[SIMPLE_IF_TAKEN]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static XmlCursor selectProperty(XmlCursor cur, QName[][] path, int offset, boolean reparseAlternate, boolean isAlternate) throws XmlException {
        AlternateContentDocument.AlternateContent alterCont;
        XmlCursor innerCur;
        for (QName qn : path[offset]) {
            boolean found = cur.toChild(qn);
            while (found) {
                if (offset == path.length - 1) {
                    return cur;
                }
                cur.push();
                innerCur = XPathHelper.selectProperty(cur, path, offset + 1, reparseAlternate, false);
                if (innerCur != null) {
                    return innerCur;
                }
                cur.pop();
                found = cur.toNextSibling(qn);
            }
        }
        if (isAlternate || !cur.toChild(ALTERNATE_CONTENT_TAG)) {
            return null;
        }
        XmlObject xo = cur.getObject();
        if (xo instanceof AlternateContentDocument.AlternateContent) {
            alterCont = (AlternateContentDocument.AlternateContent)xo;
        } else {
            if (!reparseAlternate) {
                throw new XmlException(OSGI_ERROR.replace("<CLASS>", "AlternateContent").replace("<XSB>", "alternatecontentelement"));
            }
            try {
                AlternateContentDocument acd = (AlternateContentDocument)AlternateContentDocument.Factory.parse(cur.newXMLStreamReader());
                alterCont = acd.getAlternateContent();
            }
            catch (XmlException e) {
                throw new XmlException("unable to parse AlternateContent element", e);
            }
        }
        int choices = alterCont.sizeOfChoiceArray();
        for (int i = 0; i < choices; ++i) {
            AlternateContentDocument.AlternateContent.Choice choice = alterCont.getChoiceArray(i);
            innerCur = null;
            try (XmlCursor cCur = choice.newCursor();){
                String requiresNS = cCur.namespaceForPrefix(choice.getRequires());
                if (MAC_DML_NS.equalsIgnoreCase(requiresNS) || (innerCur = XPathHelper.selectProperty(cCur, path, offset, reparseAlternate, true)) == null || innerCur == cCur) continue;
                XmlCursor xmlCursor = innerCur;
                return xmlCursor;
            }
        }
        if (!alterCont.isSetFallback()) {
            return null;
        }
        XmlCursor fCur = alterCont.getFallback().newCursor();
        XmlCursor innerCur2 = null;
        try {
            XmlCursor xmlCursor = innerCur2 = XPathHelper.selectProperty(fCur, path, offset, reparseAlternate, true);
            return xmlCursor;
        }
        finally {
            if (innerCur2 != fCur) {
                fCur.close();
            }
        }
    }

    static {
        XPathHelper.trySetFeature(xpathFactory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
    }
}

