/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.UnsupportedEncodingException;
import org.apache.xml.serialize.EncodingInfo;
import org.apache.xml.serialize.Encodings;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;

public class OutputFormat {
    private String _method;
    private String _version;
    private int _indent = 0;
    private String _encoding = "UTF-8";
    private EncodingInfo _encodingInfo = null;
    private boolean _allowJavaNames = false;
    private String _mediaType;
    private String _doctypeSystem;
    private String _doctypePublic;
    private boolean _omitXmlDeclaration = false;
    private boolean _omitDoctype = false;
    private boolean _omitComments = false;
    private boolean _standalone = false;
    private String[] _cdataElements;
    private String[] _nonEscapingElements;
    private String _lineSeparator = "\n";
    private int _lineWidth = 72;
    private boolean _preserve = false;
    private boolean _preserveEmptyAttributes = false;

    public OutputFormat() {
    }

    public OutputFormat(String string, String string2, boolean bl) {
        this.setMethod(string);
        this.setEncoding(string2);
        this.setIndenting(bl);
    }

    public OutputFormat(Document document) {
        this.setMethod(OutputFormat.whichMethod(document));
        this.setDoctype(OutputFormat.whichDoctypePublic(document), OutputFormat.whichDoctypeSystem(document));
        this.setMediaType(OutputFormat.whichMediaType(this.getMethod()));
    }

    public OutputFormat(Document document, String string, boolean bl) {
        this(document);
        this.setEncoding(string);
        this.setIndenting(bl);
    }

    public String getMethod() {
        return this._method;
    }

    public void setMethod(String string) {
        this._method = string;
    }

    public String getVersion() {
        return this._version;
    }

    public void setVersion(String string) {
        this._version = string;
    }

    public int getIndent() {
        return this._indent;
    }

    public boolean getIndenting() {
        return this._indent > 0;
    }

    public void setIndent(int n) {
        this._indent = n < 0 ? 0 : n;
    }

    public void setIndenting(boolean bl) {
        if (bl) {
            this._indent = 4;
            this._lineWidth = 72;
        } else {
            this._indent = 0;
            this._lineWidth = 0;
        }
    }

    public String getEncoding() {
        return this._encoding;
    }

    public void setEncoding(String string) {
        this._encoding = string;
        this._encodingInfo = null;
    }

    public void setEncoding(EncodingInfo encodingInfo) {
        this._encoding = encodingInfo.getIANAName();
        this._encodingInfo = encodingInfo;
    }

    public EncodingInfo getEncodingInfo() throws UnsupportedEncodingException {
        if (this._encodingInfo == null) {
            this._encodingInfo = Encodings.getEncodingInfo(this._encoding, this._allowJavaNames);
        }
        return this._encodingInfo;
    }

    public void setAllowJavaNames(boolean bl) {
        this._allowJavaNames = bl;
    }

    public boolean setAllowJavaNames() {
        return this._allowJavaNames;
    }

    public String getMediaType() {
        return this._mediaType;
    }

    public void setMediaType(String string) {
        this._mediaType = string;
    }

    public void setDoctype(String string, String string2) {
        this._doctypePublic = string;
        this._doctypeSystem = string2;
    }

    public String getDoctypePublic() {
        return this._doctypePublic;
    }

    public String getDoctypeSystem() {
        return this._doctypeSystem;
    }

    public boolean getOmitComments() {
        return this._omitComments;
    }

    public void setOmitComments(boolean bl) {
        this._omitComments = bl;
    }

    public boolean getOmitDocumentType() {
        return this._omitDoctype;
    }

    public void setOmitDocumentType(boolean bl) {
        this._omitDoctype = bl;
    }

    public boolean getOmitXMLDeclaration() {
        return this._omitXmlDeclaration;
    }

    public void setOmitXMLDeclaration(boolean bl) {
        this._omitXmlDeclaration = bl;
    }

    public boolean getStandalone() {
        return this._standalone;
    }

    public void setStandalone(boolean bl) {
        this._standalone = bl;
    }

    public String[] getCDataElements() {
        return this._cdataElements;
    }

    public boolean isCDataElement(String string) {
        if (this._cdataElements == null) {
            return false;
        }
        for (int i = 0; i < this._cdataElements.length; ++i) {
            if (!this._cdataElements[i].equals(string)) continue;
            return true;
        }
        return false;
    }

    public void setCDataElements(String[] stringArray) {
        this._cdataElements = stringArray;
    }

    public String[] getNonEscapingElements() {
        return this._nonEscapingElements;
    }

    public boolean isNonEscapingElement(String string) {
        if (this._nonEscapingElements == null) {
            return false;
        }
        for (int i = 0; i < this._nonEscapingElements.length; ++i) {
            if (!this._nonEscapingElements[i].equals(string)) continue;
            return true;
        }
        return false;
    }

    public void setNonEscapingElements(String[] stringArray) {
        this._nonEscapingElements = stringArray;
    }

    public String getLineSeparator() {
        return this._lineSeparator;
    }

    public void setLineSeparator(String string) {
        this._lineSeparator = string == null ? "\n" : string;
    }

    public boolean getPreserveSpace() {
        return this._preserve;
    }

    public void setPreserveSpace(boolean bl) {
        this._preserve = bl;
    }

    public int getLineWidth() {
        return this._lineWidth;
    }

    public void setLineWidth(int n) {
        this._lineWidth = n <= 0 ? 0 : n;
    }

    public boolean getPreserveEmptyAttributes() {
        return this._preserveEmptyAttributes;
    }

    public void setPreserveEmptyAttributes(boolean bl) {
        this._preserveEmptyAttributes = bl;
    }

    public char getLastPrintable() {
        if (this.getEncoding() != null && this.getEncoding().equalsIgnoreCase("ASCII")) {
            return '\u00ff';
        }
        return '\uffff';
    }

    public static String whichMethod(Document document) {
        if (document instanceof HTMLDocument) {
            return "html";
        }
        for (Node node = document.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 1) {
                if (node.getNodeName().equalsIgnoreCase("html")) {
                    return "html";
                }
                if (node.getNodeName().equalsIgnoreCase("root")) {
                    return "fop";
                }
                return "xml";
            }
            if (node.getNodeType() != 3) continue;
            String string = node.getNodeValue();
            for (int i = 0; i < string.length(); ++i) {
                if (string.charAt(i) == ' ' || string.charAt(i) == '\n' || string.charAt(i) == '\t' || string.charAt(i) == '\r') continue;
                return "xml";
            }
        }
        return "xml";
    }

    public static String whichDoctypePublic(Document document) {
        DocumentType documentType = document.getDoctype();
        if (documentType != null) {
            try {
                return documentType.getPublicId();
            }
            catch (Error error) {
                // empty catch block
            }
        }
        if (document instanceof HTMLDocument) {
            return "-//W3C//DTD XHTML 1.0 Strict//EN";
        }
        return null;
    }

    public static String whichDoctypeSystem(Document document) {
        DocumentType documentType = document.getDoctype();
        if (documentType != null) {
            try {
                return documentType.getSystemId();
            }
            catch (Error error) {
                // empty catch block
            }
        }
        if (document instanceof HTMLDocument) {
            return "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
        }
        return null;
    }

    public static String whichMediaType(String string) {
        if (string.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        if (string.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (string.equalsIgnoreCase("xhtml")) {
            return "text/html";
        }
        if (string.equalsIgnoreCase("text")) {
            return "text/plain";
        }
        if (string.equalsIgnoreCase("fop")) {
            return "application/pdf";
        }
        return null;
    }

    public static class Defaults {
        public static final int Indent = 4;
        public static final String Encoding = "UTF-8";
        public static final int LineWidth = 72;
    }

    public static class DTD {
        public static final String HTMLPublicId = "-//W3C//DTD HTML 4.01//EN";
        public static final String HTMLSystemId = "http://www.w3.org/TR/html4/strict.dtd";
        public static final String XHTMLPublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
        public static final String XHTMLSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
    }
}

