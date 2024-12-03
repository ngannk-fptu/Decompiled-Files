/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.client;

import java.applet.Applet;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xalan.res.XSLMessages;

public class XSLTProcessorApplet
extends Applet {
    transient TransformerFactory m_tfactory = null;
    private String m_styleURL;
    private String m_documentURL;
    private final String PARAM_styleURL = "styleURL";
    private final String PARAM_documentURL = "documentURL";
    private String m_styleURLOfCached = null;
    private String m_documentURLOfCached = null;
    private URL m_codeBase = null;
    private String m_treeURL = null;
    private URL m_documentBase = null;
    private transient Thread m_callThread = null;
    private transient TrustedAgent m_trustedAgent = null;
    private transient Thread m_trustedWorker = null;
    private transient String m_htmlText = null;
    private transient String m_sourceText = null;
    private transient String m_nameOfIDAttrOfElemToModify = null;
    private transient String m_elemIdToModify = null;
    private transient String m_attrNameToSet = null;
    private transient String m_attrValueToSet = null;
    transient Hashtable m_parameters;
    private static final long serialVersionUID = 4618876841979251422L;

    @Override
    public String getAppletInfo() {
        return "Name: XSLTProcessorApplet\r\nAuthor: Scott Boag";
    }

    @Override
    public String[][] getParameterInfo() {
        String[][] info = new String[][]{{"styleURL", "String", "URL to an XSL stylesheet"}, {"documentURL", "String", "URL to an XML document"}};
        return info;
    }

    @Override
    public void init() {
        String param = this.getParameter("styleURL");
        this.m_parameters = new Hashtable();
        if (param != null) {
            this.setStyleURL(param);
        }
        if ((param = this.getParameter("documentURL")) != null) {
            this.setDocumentURL(param);
        }
        this.m_codeBase = this.getCodeBase();
        this.m_documentBase = this.getDocumentBase();
        this.resize(320, 240);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void start() {
        this.m_trustedAgent = new TrustedAgent();
        Thread currentThread = Thread.currentThread();
        this.m_trustedWorker = new Thread(currentThread.getThreadGroup(), this.m_trustedAgent);
        this.m_trustedWorker.start();
        try {
            this.m_tfactory = TransformerFactory.newInstance();
            this.showStatus("Causing Transformer and Parser to Load and JIT...");
            StringReader xmlbuf = new StringReader("<?xml version='1.0'?><foo/>");
            StringReader xslbuf = new StringReader("<?xml version='1.0'?><xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'><xsl:template match='foo'><out/></xsl:template></xsl:stylesheet>");
            PrintWriter pw = new PrintWriter(new StringWriter());
            TransformerFactory transformerFactory = this.m_tfactory;
            synchronized (transformerFactory) {
                Templates templates = this.m_tfactory.newTemplates(new StreamSource(xslbuf));
                Transformer transformer = templates.newTransformer();
                transformer.transform(new StreamSource(xmlbuf), new StreamResult(pw));
            }
            System.out.println("Primed the pump!");
            this.showStatus("Ready to go!");
        }
        catch (Exception e) {
            this.showStatus("Could not prime the pump!");
            System.out.println("Could not prime the pump!");
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
    }

    @Override
    public void stop() {
        if (null != this.m_trustedWorker) {
            this.m_trustedWorker.stop();
            this.m_trustedWorker = null;
        }
        this.m_styleURLOfCached = null;
        this.m_documentURLOfCached = null;
    }

    @Override
    public void destroy() {
        if (null != this.m_trustedWorker) {
            this.m_trustedWorker.stop();
            this.m_trustedWorker = null;
        }
        this.m_styleURLOfCached = null;
        this.m_documentURLOfCached = null;
    }

    public void setStyleURL(String urlString) {
        this.m_styleURL = urlString;
    }

    public void setDocumentURL(String urlString) {
        this.m_documentURL = urlString;
    }

    public void freeCache() {
        this.m_styleURLOfCached = null;
        this.m_documentURLOfCached = null;
    }

    public void setStyleSheetAttribute(String nameOfIDAttrOfElemToModify, String elemId, String attrName, String value) {
        this.m_nameOfIDAttrOfElemToModify = nameOfIDAttrOfElemToModify;
        this.m_elemIdToModify = elemId;
        this.m_attrNameToSet = attrName;
        this.m_attrValueToSet = value;
    }

    public void setStylesheetParam(String key, String expr) {
        this.m_parameters.put(key, expr);
    }

    public String escapeString(String s) {
        StringBuffer sb = new StringBuffer();
        int length = s.length();
        for (int i = 0; i < length; ++i) {
            char ch = s.charAt(i);
            if ('<' == ch) {
                sb.append("&lt;");
                continue;
            }
            if ('>' == ch) {
                sb.append("&gt;");
                continue;
            }
            if ('&' == ch) {
                sb.append("&amp;");
                continue;
            }
            if ('\ud800' <= ch && ch < '\udc00') {
                int next;
                if (i + 1 >= length) {
                    throw new RuntimeException(XSLMessages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[]{Integer.toHexString(ch)}));
                }
                if (56320 > (next = s.charAt(++i)) || next >= 57344) {
                    throw new RuntimeException(XSLMessages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[]{Integer.toHexString(ch) + " " + Integer.toHexString(next)}));
                }
                next = (ch - 55296 << 10) + next - 56320 + 65536;
                sb.append("&#x");
                sb.append(Integer.toHexString(next));
                sb.append(";");
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getHtmlText() {
        this.m_trustedAgent.m_getData = true;
        this.m_callThread = Thread.currentThread();
        try {
            Thread thread = this.m_callThread;
            synchronized (thread) {
                this.m_callThread.wait();
            }
        }
        catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
        return this.m_htmlText;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getTreeAsText(String treeURL) throws IOException {
        this.m_treeURL = treeURL;
        this.m_trustedAgent.m_getData = true;
        this.m_trustedAgent.m_getSource = true;
        this.m_callThread = Thread.currentThread();
        try {
            Thread thread = this.m_callThread;
            synchronized (thread) {
                this.m_callThread.wait();
            }
        }
        catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
        return this.m_sourceText;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getSource() throws TransformerException {
        StringWriter osw = new StringWriter();
        PrintWriter pw = new PrintWriter(osw, false);
        String text = "";
        try {
            URL docURL = new URL(this.m_documentBase, this.m_treeURL);
            TransformerFactory transformerFactory = this.m_tfactory;
            synchronized (transformerFactory) {
                Transformer transformer = this.m_tfactory.newTransformer();
                StreamSource source = new StreamSource(docURL.toString());
                StreamResult result = new StreamResult(pw);
                transformer.transform(source, result);
                text = osw.toString();
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        catch (Exception any_error) {
            any_error.printStackTrace();
        }
        return text;
    }

    public String getSourceTreeAsText() throws Exception {
        return this.getTreeAsText(this.m_documentURL);
    }

    public String getStyleTreeAsText() throws Exception {
        return this.getTreeAsText(this.m_styleURL);
    }

    public String getResultTreeAsText() throws Exception {
        return this.escapeString(this.getHtmlText());
    }

    public String transformToHtml(String doc, String style) {
        if (null != doc) {
            this.m_documentURL = doc;
        }
        if (null != style) {
            this.m_styleURL = style;
        }
        return this.getHtmlText();
    }

    public String transformToHtml(String doc) {
        if (null != doc) {
            this.m_documentURL = doc;
        }
        this.m_styleURL = null;
        return this.getHtmlText();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String processTransformation() throws TransformerException {
        String htmlData = null;
        this.showStatus("Waiting for Transformer and Parser to finish loading and JITing...");
        TransformerFactory transformerFactory = this.m_tfactory;
        synchronized (transformerFactory) {
            URL documentURL = null;
            URL styleURL = null;
            StringWriter osw = new StringWriter();
            PrintWriter pw = new PrintWriter(osw, false);
            StreamResult result = new StreamResult(pw);
            this.showStatus("Begin Transformation...");
            try {
                documentURL = new URL(this.m_codeBase, this.m_documentURL);
                StreamSource xmlSource = new StreamSource(documentURL.toString());
                styleURL = new URL(this.m_codeBase, this.m_styleURL);
                StreamSource xslSource = new StreamSource(styleURL.toString());
                Transformer transformer = this.m_tfactory.newTransformer(xslSource);
                for (Map.Entry entry : this.m_parameters.entrySet()) {
                    Object key = entry.getKey();
                    Object expression = entry.getValue();
                    transformer.setParameter((String)key, expression);
                }
                transformer.transform(xmlSource, result);
            }
            catch (TransformerConfigurationException tfe) {
                tfe.printStackTrace();
                throw new RuntimeException(tfe.getMessage());
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            this.showStatus("Transformation Done!");
            htmlData = osw.toString();
        }
        return htmlData;
    }

    private void readObject(ObjectInputStream inStream) throws IOException, ClassNotFoundException {
        inStream.defaultReadObject();
        this.m_tfactory = TransformerFactory.newInstance();
    }

    class TrustedAgent
    implements Runnable {
        public boolean m_getData = false;
        public boolean m_getSource = false;

        TrustedAgent() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            while (true) {
                Thread.yield();
                if (this.m_getData) {
                    try {
                        this.m_getData = false;
                        XSLTProcessorApplet.this.m_htmlText = null;
                        XSLTProcessorApplet.this.m_sourceText = null;
                        if (this.m_getSource) {
                            this.m_getSource = false;
                            XSLTProcessorApplet.this.m_sourceText = XSLTProcessorApplet.this.getSource();
                        }
                        XSLTProcessorApplet.this.m_htmlText = XSLTProcessorApplet.this.processTransformation();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        Thread e = XSLTProcessorApplet.this.m_callThread;
                        synchronized (e) {
                            XSLTProcessorApplet.this.m_callThread.notify();
                        }
                    }
                }
                try {
                    Thread.sleep(50L);
                    continue;
                }
                catch (InterruptedException ie) {
                    ie.printStackTrace();
                    continue;
                }
                break;
            }
        }
    }
}

