/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.NamespaceContext
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XMLLocator
 *  org.apache.xerces.xni.XMLResourceIdentifier
 *  org.apache.xerces.xni.XMLString
 *  org.apache.xerces.xni.XNIException
 *  org.apache.xerces.xni.parser.XMLDocumentFilter
 *  org.apache.xerces.xni.parser.XMLInputSource
 */
package org.cyberneko.html.filters;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.HTMLElements;
import org.cyberneko.html.HTMLEntities;
import org.cyberneko.html.filters.DefaultFilter;
import org.cyberneko.html.filters.Identity;
import org.cyberneko.html.filters.Purifier;

public class Writer
extends DefaultFilter {
    public static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    public static final String NOTIFY_HTML_BUILTIN_REFS = "http://cyberneko.org/html/features/scanner/notify-builtin-refs";
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String FILTERS = "http://cyberneko.org/html/properties/filters";
    protected String fEncoding;
    protected PrintWriter fPrinter;
    protected boolean fSeenRootElement;
    protected boolean fSeenHttpEquiv;
    protected int fElementDepth;
    protected boolean fNormalize;
    protected boolean fPrintChars;

    public Writer() {
        try {
            this.fEncoding = "UTF-8";
            this.fPrinter = new PrintWriter(new OutputStreamWriter((OutputStream)System.out, this.fEncoding));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Writer(OutputStream outputStream, String encoding) throws UnsupportedEncodingException {
        this(new OutputStreamWriter(outputStream, encoding), encoding);
    }

    public Writer(java.io.Writer writer, String encoding) {
        this.fEncoding = encoding;
        this.fPrinter = writer instanceof PrintWriter ? (PrintWriter)writer : new PrintWriter(writer);
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException {
        this.fSeenRootElement = false;
        this.fSeenHttpEquiv = false;
        this.fElementDepth = 0;
        this.fNormalize = true;
        this.fPrintChars = true;
        super.startDocument(locator, encoding, nscontext, augs);
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, Augmentations augs) throws XNIException {
        this.startDocument(locator, encoding, null, augs);
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        if (this.fSeenRootElement && this.fElementDepth <= 0) {
            this.fPrinter.println();
        }
        this.fPrinter.print("<!--");
        this.printCharacters(text, false);
        this.fPrinter.print("-->");
        if (!this.fSeenRootElement) {
            this.fPrinter.println();
        }
        this.fPrinter.flush();
    }

    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        this.fSeenRootElement = true;
        ++this.fElementDepth;
        this.fNormalize = !HTMLElements.getElement(element.rawname).isSpecial();
        this.printStartElement(element, attributes);
        super.startElement(element, attributes, augs);
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        this.fSeenRootElement = true;
        this.printStartElement(element, attributes);
        super.emptyElement(element, attributes, augs);
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.fPrintChars) {
            this.printCharacters(text, this.fNormalize);
        }
        super.characters(text, augs);
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        --this.fElementDepth;
        this.fNormalize = true;
        this.printEndElement(element);
        super.endElement(element, augs);
    }

    @Override
    public void startGeneralEntity(String name, XMLResourceIdentifier id, String encoding, Augmentations augs) throws XNIException {
        this.fPrintChars = false;
        if (name.startsWith("#")) {
            try {
                boolean hex = name.startsWith("#x");
                int offset = hex ? 2 : 1;
                int base = hex ? 16 : 10;
                int value = Integer.parseInt(name.substring(offset), base);
                String entity = HTMLEntities.get(value);
                if (entity != null) {
                    name = entity;
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        this.printEntity(name);
        super.startGeneralEntity(name, id, encoding, augs);
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        this.fPrintChars = true;
        super.endGeneralEntity(name, augs);
    }

    protected void printAttributeValue(String text) {
        int length = text.length();
        for (int j = 0; j < length; ++j) {
            char c = text.charAt(j);
            if (c == '\"') {
                this.fPrinter.print("&quot;");
                continue;
            }
            this.fPrinter.print(c);
        }
        this.fPrinter.flush();
    }

    protected void printCharacters(XMLString text, boolean normalize) {
        if (normalize) {
            for (int i = 0; i < text.length; ++i) {
                char c = text.ch[text.offset + i];
                if (c != '\n') {
                    String entity = HTMLEntities.get(c);
                    if (entity != null) {
                        this.printEntity(entity);
                        continue;
                    }
                    this.fPrinter.print(c);
                    continue;
                }
                this.fPrinter.println();
            }
        } else {
            for (int i = 0; i < text.length; ++i) {
                char c = text.ch[text.offset + i];
                this.fPrinter.print(c);
            }
        }
        this.fPrinter.flush();
    }

    protected void printStartElement(QName element, XMLAttributes attributes) {
        int contentIndex = -1;
        String originalContent = null;
        if (element.rawname.toLowerCase().equals("meta")) {
            String httpEquiv = null;
            int length = attributes.getLength();
            for (int i = 0; i < length; ++i) {
                String aname = attributes.getQName(i).toLowerCase();
                if (aname.equals("http-equiv")) {
                    httpEquiv = attributes.getValue(i);
                    continue;
                }
                if (!aname.equals("content")) continue;
                contentIndex = i;
            }
            if (httpEquiv != null && httpEquiv.toLowerCase().equals("content-type")) {
                this.fSeenHttpEquiv = true;
                String content = null;
                if (contentIndex != -1) {
                    originalContent = attributes.getValue(contentIndex);
                    content = originalContent.toLowerCase();
                }
                if (content != null) {
                    int charsetIndex = content.indexOf("charset=");
                    content = charsetIndex != -1 ? content.substring(0, charsetIndex + 8) : content + ";charset=";
                    content = content + this.fEncoding;
                    attributes.setValue(contentIndex, content);
                }
            }
        }
        this.fPrinter.print('<');
        this.fPrinter.print(element.rawname);
        int attrCount = attributes != null ? attributes.getLength() : 0;
        for (int i = 0; i < attrCount; ++i) {
            String aname = attributes.getQName(i);
            String avalue = attributes.getValue(i);
            this.fPrinter.print(' ');
            this.fPrinter.print(aname);
            this.fPrinter.print("=\"");
            this.printAttributeValue(avalue);
            this.fPrinter.print('\"');
        }
        this.fPrinter.print('>');
        this.fPrinter.flush();
        if (contentIndex != -1 && originalContent != null) {
            attributes.setValue(contentIndex, originalContent);
        }
    }

    protected void printEndElement(QName element) {
        this.fPrinter.print("</");
        this.fPrinter.print(element.rawname);
        this.fPrinter.print('>');
        this.fPrinter.flush();
    }

    protected void printEntity(String name) {
        this.fPrinter.print('&');
        this.fPrinter.print(name);
        this.fPrinter.print(';');
        this.fPrinter.flush();
    }

    public static void main(String[] argv) throws Exception {
        if (argv.length == 0) {
            Writer.printUsage();
            System.exit(1);
        }
        HTMLConfiguration parser = new HTMLConfiguration();
        parser.setFeature(NOTIFY_CHAR_REFS, true);
        parser.setFeature(NOTIFY_HTML_BUILTIN_REFS, true);
        String iencoding = null;
        String oencoding = "Windows-1252";
        boolean identity = false;
        boolean purify = false;
        for (int i = 0; i < argv.length; ++i) {
            String arg = argv[i];
            if (arg.equals("-ie")) {
                iencoding = argv[++i];
                continue;
            }
            if (arg.equals("-e") || arg.equals("-oe")) {
                oencoding = argv[++i];
                continue;
            }
            if (arg.equals("-i")) {
                identity = true;
                continue;
            }
            if (arg.equals("-p")) {
                purify = true;
                continue;
            }
            if (arg.equals("-h")) {
                Writer.printUsage();
                System.exit(1);
            }
            Vector<DefaultFilter> filtersVector = new Vector<DefaultFilter>(2);
            if (identity) {
                filtersVector.addElement(new Identity());
            } else if (purify) {
                filtersVector.addElement(new Purifier());
            }
            filtersVector.addElement(new Writer(System.out, oencoding));
            Object[] filters = new XMLDocumentFilter[filtersVector.size()];
            filtersVector.copyInto(filters);
            parser.setProperty(FILTERS, filters);
            XMLInputSource source = new XMLInputSource(null, arg, null);
            source.setEncoding(iencoding);
            parser.parse(source);
        }
    }

    private static void printUsage() {
        System.err.println("usage: java " + Writer.class.getName() + " (options) file ...");
        System.err.println();
        System.err.println("options:");
        System.err.println("  -ie name  Specify IANA name of input encoding.");
        System.err.println("  -oe name  Specify IANA name of output encoding.");
        System.err.println("  -i        Perform identity transform.");
        System.err.println("  -p        Purify output to ensure XML well-formedness.");
        System.err.println("  -h        Display help screen.");
        System.err.println();
        System.err.println("notes:");
        System.err.println("  The -i and -p options are mutually exclusive.");
        System.err.println("  The -e option has been replaced with -oe.");
    }
}

