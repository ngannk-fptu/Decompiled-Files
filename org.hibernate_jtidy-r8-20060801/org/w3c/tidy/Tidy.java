/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.tidy.Clean;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.DOMDocumentImpl;
import org.w3c.tidy.DOMNodeImpl;
import org.w3c.tidy.Lexer;
import org.w3c.tidy.Node;
import org.w3c.tidy.Out;
import org.w3c.tidy.OutFactory;
import org.w3c.tidy.PPrint;
import org.w3c.tidy.ParsePropertyImpl;
import org.w3c.tidy.ParserImpl;
import org.w3c.tidy.Report;
import org.w3c.tidy.StreamIn;
import org.w3c.tidy.StreamInFactory;
import org.w3c.tidy.TagTable;
import org.w3c.tidy.TidyMessageListener;
import org.w3c.tidy.TidyUtils;

public class Tidy
implements Serializable {
    static final long serialVersionUID = -2794371560623987718L;
    private static final Map CMDLINE_ALIAS = new HashMap();
    private PrintWriter errout;
    private PrintWriter stderr;
    private Configuration configuration;
    private String inputStreamName = "InputStream";
    private int parseErrors;
    private int parseWarnings;
    private Report report = new Report();

    public Tidy() {
        this.configuration = new Configuration(this.report);
        TagTable tt = new TagTable();
        tt.setConfiguration(this.configuration);
        this.configuration.tt = tt;
        this.configuration.errfile = null;
        this.errout = this.stderr = new PrintWriter(System.err, true);
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public PrintWriter getStderr() {
        return this.stderr;
    }

    public int getParseErrors() {
        return this.parseErrors;
    }

    public int getParseWarnings() {
        return this.parseWarnings;
    }

    public void setInputStreamName(String name) {
        if (name != null) {
            this.inputStreamName = name;
        }
    }

    public String getInputStreamName() {
        return this.inputStreamName;
    }

    public PrintWriter getErrout() {
        return this.errout;
    }

    public void setErrout(PrintWriter out) {
        this.errout = out;
    }

    public void setConfigurationFromFile(String filename) {
        this.configuration.parseFile(filename);
    }

    public void setConfigurationFromProps(Properties props) {
        this.configuration.addProps(props);
    }

    public static Document createEmptyDocument() {
        Node document = new Node(0, new byte[0], 0, 0);
        Node node = new Node(5, new byte[0], 0, 0, "html", new TagTable());
        if (document != null && node != null) {
            document.insertNodeAtStart(node);
            return (Document)document.getAdapter();
        }
        return null;
    }

    public Node parse(InputStream in, OutputStream out) {
        StreamIn streamIn = StreamInFactory.getStreamIn(this.configuration, in);
        Out o = null;
        if (out != null) {
            o = OutFactory.getOut(this.configuration, out);
        }
        return this.parse(streamIn, o);
    }

    public Node parse(Reader in, OutputStream out) {
        StreamIn streamIn = StreamInFactory.getStreamIn(this.configuration, in);
        Out o = null;
        if (out != null) {
            o = OutFactory.getOut(this.configuration, out);
        }
        return this.parse(streamIn, o);
    }

    public Node parse(Reader in, Writer out) {
        StreamIn streamIn = StreamInFactory.getStreamIn(this.configuration, in);
        Out o = null;
        if (out != null) {
            o = OutFactory.getOut(this.configuration, out);
        }
        return this.parse(streamIn, o);
    }

    public Node parse(InputStream in, Writer out) {
        StreamIn streamIn = StreamInFactory.getStreamIn(this.configuration, in);
        Out o = null;
        if (out != null) {
            o = OutFactory.getOut(this.configuration, out);
        }
        return this.parse(streamIn, o);
    }

    public Document parseDOM(InputStream in, OutputStream out) {
        Node document = this.parse(in, out);
        if (document != null) {
            return (Document)document.getAdapter();
        }
        return null;
    }

    public void pprint(Document doc, OutputStream out) {
        if (!(doc instanceof DOMDocumentImpl)) {
            return;
        }
        this.pprint(((DOMDocumentImpl)doc).adaptee, out);
    }

    public void pprint(org.w3c.dom.Node node, OutputStream out) {
        if (!(node instanceof DOMNodeImpl)) {
            return;
        }
        this.pprint(((DOMNodeImpl)node).adaptee, out);
    }

    private Node parse(StreamIn streamIn, Out o) {
        Node doctype;
        Node document = null;
        if (this.errout == null) {
            return null;
        }
        this.configuration.adjust();
        this.parseErrors = 0;
        this.parseWarnings = 0;
        Lexer lexer = new Lexer(streamIn, this.configuration, this.report);
        lexer.errout = this.errout;
        streamIn.setLexer(lexer);
        this.report.setFilename(this.inputStreamName);
        if (!this.configuration.quiet) {
            this.report.helloMessage(this.errout);
        }
        if (this.configuration.xmlTags) {
            document = ParserImpl.parseXMLDocument(lexer);
            if (!document.checkNodeIntegrity()) {
                if (!this.configuration.quiet) {
                    this.report.badTree(this.errout);
                }
                return null;
            }
        } else {
            lexer.warnings = 0;
            document = ParserImpl.parseDocument(lexer);
            if (!document.checkNodeIntegrity()) {
                if (!this.configuration.quiet) {
                    this.report.badTree(this.errout);
                }
                return null;
            }
            Clean cleaner = new Clean(this.configuration.tt);
            cleaner.nestedEmphasis(document);
            cleaner.list2BQ(document);
            cleaner.bQ2Div(document);
            if (this.configuration.logicalEmphasis) {
                cleaner.emFromI(document);
            }
            if (this.configuration.word2000 && cleaner.isWord2000(document)) {
                cleaner.dropSections(lexer, document);
                cleaner.cleanWord2000(lexer, document);
            }
            if (this.configuration.makeClean || this.configuration.dropFontTags) {
                cleaner.cleanTree(lexer, document);
            }
            if (!document.checkNodeIntegrity()) {
                this.report.badTree(this.errout);
                return null;
            }
            doctype = document.findDocType();
            if (doctype != null) {
                doctype = (Node)doctype.clone();
            }
            if (document.content != null) {
                if (this.configuration.xHTML) {
                    lexer.setXHTMLDocType(document);
                } else {
                    lexer.fixDocType(document);
                }
                if (this.configuration.tidyMark) {
                    lexer.addGenerator(document);
                }
            }
            if (this.configuration.xmlOut && this.configuration.xmlPi) {
                lexer.fixXmlDecl(document);
            }
            if (!this.configuration.quiet && document.content != null) {
                this.report.reportVersion(this.errout, lexer, this.inputStreamName, doctype);
            }
        }
        if (!this.configuration.quiet) {
            this.parseWarnings = lexer.warnings;
            this.parseErrors = lexer.errors;
            this.report.reportNumWarnings(this.errout, lexer);
        }
        if (!this.configuration.quiet && lexer.errors > 0 && !this.configuration.forceOutput) {
            this.report.needsAuthorIntervention(this.errout);
        }
        if (!this.configuration.onlyErrors && (lexer.errors == 0 || this.configuration.forceOutput)) {
            if (this.configuration.burstSlides) {
                Node body = null;
                doctype = document.findDocType();
                if (doctype != null) {
                    Node.discardElement(doctype);
                }
                lexer.versions = (short)(lexer.versions | 8);
                if (this.configuration.xHTML) {
                    lexer.setXHTMLDocType(document);
                } else {
                    lexer.fixDocType(document);
                }
                body = document.findBody(this.configuration.tt);
                if (body != null) {
                    PPrint pprint = new PPrint(this.configuration);
                    if (!this.configuration.quiet) {
                        this.report.reportNumberOfSlides(this.errout, pprint.countSlides(body));
                    }
                    pprint.createSlides(lexer, document);
                } else if (!this.configuration.quiet) {
                    this.report.missingBody(this.errout);
                }
            } else if (o != null) {
                PPrint pprint = new PPrint(this.configuration);
                if (document.findDocType() == null) {
                    this.configuration.numEntities = true;
                }
                if (this.configuration.bodyOnly) {
                    pprint.printBody(o, lexer, document, this.configuration.xmlOut);
                } else if (this.configuration.xmlOut && !this.configuration.xHTML) {
                    pprint.printXMLTree(o, (short)0, 0, lexer, document);
                } else {
                    pprint.printTree(o, (short)0, 0, lexer, document);
                }
                pprint.flushLine(o, 0);
                o.flush();
            }
        }
        if (!this.configuration.quiet) {
            this.report.errorSummary(lexer);
        }
        return document;
    }

    private Node parse(InputStream in, String file, OutputStream out) throws FileNotFoundException, IOException {
        Out o = null;
        boolean inputStreamOpen = false;
        boolean outputStreamOpen = false;
        if (file != null) {
            in = new FileInputStream(file);
            inputStreamOpen = true;
            this.inputStreamName = file;
        } else if (in == null) {
            in = System.in;
            this.inputStreamName = "stdin";
        }
        StreamIn streamIn = StreamInFactory.getStreamIn(this.configuration, in);
        if (this.configuration.writeback && file != null) {
            out = new FileOutputStream(file);
            outputStreamOpen = true;
        }
        if (out != null) {
            o = OutFactory.getOut(this.configuration, out);
        }
        Node node = this.parse(streamIn, o);
        if (inputStreamOpen) {
            try {
                in.close();
            }
            catch (IOException e) {
                // empty catch block
            }
        }
        if (outputStreamOpen) {
            try {
                out.close();
            }
            catch (IOException e) {
                // empty catch block
            }
        }
        return node;
    }

    private void pprint(Node node, OutputStream out) {
        if (out != null) {
            Out o = OutFactory.getOut(this.configuration, out);
            Lexer lexer = new Lexer(null, this.configuration, this.report);
            PPrint pprint = new PPrint(this.configuration);
            if (this.configuration.xmlTags) {
                pprint.printXMLTree(o, (short)0, 0, lexer, node);
            } else {
                pprint.printTree(o, (short)0, 0, lexer, node);
            }
            pprint.flushLine(o, 0);
            try {
                out.flush();
            }
            catch (IOException e) {
                // empty catch block
            }
        }
    }

    public static void main(String[] argv) {
        Tidy tidy = new Tidy();
        int returnCode = tidy.mainExec(argv);
        System.exit(returnCode);
    }

    protected int mainExec(String[] argv) {
        int argCount = argv.length;
        int argIndex = 0;
        Properties properties = new Properties();
        while (argCount > 0) {
            String errorfile;
            if (argv[argIndex].startsWith("-")) {
                String alias;
                String argName = argv[argIndex].toLowerCase();
                while (argName.length() > 0 && argName.charAt(0) == '-') {
                    argName = argName.substring(1);
                }
                if (argName.equals("help") || argName.equals("h") || argName.equals("?")) {
                    this.report.helpText(new PrintWriter(System.out, true));
                    return 0;
                }
                if (argName.equals("help-config")) {
                    this.configuration.printConfigOptions(new PrintWriter(System.out, true), false);
                    return 0;
                }
                if (argName.equals("show-config")) {
                    this.configuration.adjust();
                    this.configuration.printConfigOptions(this.errout, true);
                    return 0;
                }
                if (argName.equals("version") || argName.equals("v")) {
                    this.report.showVersion(this.errout);
                    return 0;
                }
                String argValue = null;
                if (argCount > 2 && !argv[argIndex + 1].startsWith("-")) {
                    argValue = argv[argIndex + 1];
                    --argCount;
                    ++argIndex;
                }
                if ((alias = (String)CMDLINE_ALIAS.get(argName)) != null) {
                    argName = alias;
                }
                if (Configuration.isKnownOption(argName)) {
                    properties.setProperty(argName, argValue == null ? "" : argName);
                } else if (argName.equals("config")) {
                    if (argValue != null) {
                        this.configuration.parseFile(argValue);
                    }
                } else if (TidyUtils.isCharEncodingSupported(argName)) {
                    properties.setProperty("char-encoding", argName);
                } else {
                    block18: for (int i = 0; i < argName.length(); ++i) {
                        switch (argName.charAt(i)) {
                            case 'i': {
                                this.configuration.indentContent = true;
                                this.configuration.smartIndent = true;
                                continue block18;
                            }
                            case 'o': {
                                this.configuration.hideEndTags = true;
                                continue block18;
                            }
                            case 'u': {
                                this.configuration.upperCaseTags = true;
                                continue block18;
                            }
                            case 'c': {
                                this.configuration.makeClean = true;
                                continue block18;
                            }
                            case 'b': {
                                this.configuration.makeBare = true;
                                continue block18;
                            }
                            case 'n': {
                                this.configuration.numEntities = true;
                                continue block18;
                            }
                            case 'm': {
                                this.configuration.writeback = true;
                                continue block18;
                            }
                            case 'e': {
                                this.configuration.onlyErrors = true;
                                continue block18;
                            }
                            case 'q': {
                                this.configuration.quiet = true;
                                continue block18;
                            }
                            default: {
                                this.report.unknownOption(this.errout, argName.charAt(i));
                            }
                        }
                    }
                }
                --argCount;
                ++argIndex;
                continue;
            }
            this.configuration.addProps(properties);
            this.configuration.adjust();
            if (this.configuration.errfile != null && !this.configuration.errfile.equals(errorfile = "stderr")) {
                if (this.errout != this.stderr) {
                    this.errout.close();
                }
                try {
                    this.setErrout(new PrintWriter(new FileWriter(this.configuration.errfile), true));
                    errorfile = this.configuration.errfile;
                }
                catch (IOException e) {
                    errorfile = "stderr";
                    this.setErrout(this.stderr);
                }
            }
            String file = argCount > 0 ? argv[argIndex] : "stdin";
            try {
                this.parse(null, file, System.out);
            }
            catch (FileNotFoundException fnfe) {
                this.report.unknownFile(this.errout, file);
            }
            catch (IOException ioe) {
                this.report.unknownFile(this.errout, file);
            }
            ++argIndex;
            if (--argCount > 0) continue;
        }
        if (this.parseErrors + this.parseWarnings > 0 && !this.configuration.quiet) {
            this.report.generalInfo(this.errout);
        }
        if (this.errout != this.stderr) {
            this.errout.close();
        }
        if (this.parseErrors > 0) {
            return 2;
        }
        if (this.parseWarnings > 0) {
            return 1;
        }
        return 0;
    }

    public void setMessageListener(TidyMessageListener listener) {
        this.report.addMessageListener(listener);
    }

    public void setSpaces(int spaces) {
        this.configuration.spaces = spaces;
    }

    public int getSpaces() {
        return this.configuration.spaces;
    }

    public void setWraplen(int wraplen) {
        this.configuration.wraplen = wraplen;
    }

    public int getWraplen() {
        return this.configuration.wraplen;
    }

    public void setTabsize(int tabsize) {
        this.configuration.tabsize = tabsize;
    }

    public int getTabsize() {
        return this.configuration.tabsize;
    }

    public void setErrfile(String errfile) {
        this.configuration.errfile = errfile;
    }

    public String getErrfile() {
        return this.configuration.errfile;
    }

    public void setWriteback(boolean writeback) {
        this.configuration.writeback = writeback;
    }

    public boolean getWriteback() {
        return this.configuration.writeback;
    }

    public void setOnlyErrors(boolean onlyErrors) {
        this.configuration.onlyErrors = onlyErrors;
    }

    public boolean getOnlyErrors() {
        return this.configuration.onlyErrors;
    }

    public void setShowWarnings(boolean showWarnings) {
        this.configuration.showWarnings = showWarnings;
    }

    public boolean getShowWarnings() {
        return this.configuration.showWarnings;
    }

    public void setQuiet(boolean quiet) {
        this.configuration.quiet = quiet;
    }

    public boolean getQuiet() {
        return this.configuration.quiet;
    }

    public void setIndentContent(boolean indentContent) {
        this.configuration.indentContent = indentContent;
    }

    public boolean getIndentContent() {
        return this.configuration.indentContent;
    }

    public void setSmartIndent(boolean smartIndent) {
        this.configuration.smartIndent = smartIndent;
    }

    public boolean getSmartIndent() {
        return this.configuration.smartIndent;
    }

    public void setHideEndTags(boolean hideEndTags) {
        this.configuration.hideEndTags = hideEndTags;
    }

    public boolean getHideEndTags() {
        return this.configuration.hideEndTags;
    }

    public void setXmlTags(boolean xmlTags) {
        this.configuration.xmlTags = xmlTags;
    }

    public boolean getXmlTags() {
        return this.configuration.xmlTags;
    }

    public void setXmlOut(boolean xmlOut) {
        this.configuration.xmlOut = xmlOut;
    }

    public boolean getXmlOut() {
        return this.configuration.xmlOut;
    }

    public void setXHTML(boolean xhtml) {
        this.configuration.xHTML = xhtml;
    }

    public boolean getXHTML() {
        return this.configuration.xHTML;
    }

    public void setUpperCaseTags(boolean upperCaseTags) {
        this.configuration.upperCaseTags = upperCaseTags;
    }

    public boolean getUpperCaseTags() {
        return this.configuration.upperCaseTags;
    }

    public void setUpperCaseAttrs(boolean upperCaseAttrs) {
        this.configuration.upperCaseAttrs = upperCaseAttrs;
    }

    public boolean getUpperCaseAttrs() {
        return this.configuration.upperCaseAttrs;
    }

    public void setMakeClean(boolean makeClean) {
        this.configuration.makeClean = makeClean;
    }

    public boolean getMakeClean() {
        return this.configuration.makeClean;
    }

    public void setMakeBare(boolean makeBare) {
        this.configuration.makeBare = makeBare;
    }

    public boolean getMakeBare() {
        return this.configuration.makeBare;
    }

    public void setBreakBeforeBR(boolean breakBeforeBR) {
        this.configuration.breakBeforeBR = breakBeforeBR;
    }

    public boolean getBreakBeforeBR() {
        return this.configuration.breakBeforeBR;
    }

    public void setBurstSlides(boolean burstSlides) {
        this.configuration.burstSlides = burstSlides;
    }

    public boolean getBurstSlides() {
        return this.configuration.burstSlides;
    }

    public void setNumEntities(boolean numEntities) {
        this.configuration.numEntities = numEntities;
    }

    public boolean getNumEntities() {
        return this.configuration.numEntities;
    }

    public void setQuoteMarks(boolean quoteMarks) {
        this.configuration.quoteMarks = quoteMarks;
    }

    public boolean getQuoteMarks() {
        return this.configuration.quoteMarks;
    }

    public void setQuoteNbsp(boolean quoteNbsp) {
        this.configuration.quoteNbsp = quoteNbsp;
    }

    public boolean getQuoteNbsp() {
        return this.configuration.quoteNbsp;
    }

    public void setQuoteAmpersand(boolean quoteAmpersand) {
        this.configuration.quoteAmpersand = quoteAmpersand;
    }

    public boolean getQuoteAmpersand() {
        return this.configuration.quoteAmpersand;
    }

    public void setWrapAttVals(boolean wrapAttVals) {
        this.configuration.wrapAttVals = wrapAttVals;
    }

    public boolean getWrapAttVals() {
        return this.configuration.wrapAttVals;
    }

    public void setWrapScriptlets(boolean wrapScriptlets) {
        this.configuration.wrapScriptlets = wrapScriptlets;
    }

    public boolean getWrapScriptlets() {
        return this.configuration.wrapScriptlets;
    }

    public void setWrapSection(boolean wrapSection) {
        this.configuration.wrapSection = wrapSection;
    }

    public boolean getWrapSection() {
        return this.configuration.wrapSection;
    }

    public void setAltText(String altText) {
        this.configuration.altText = altText;
    }

    public String getAltText() {
        return this.configuration.altText;
    }

    public void setXmlPi(boolean xmlPi) {
        this.configuration.xmlPi = xmlPi;
    }

    public boolean getXmlPi() {
        return this.configuration.xmlPi;
    }

    public void setDropFontTags(boolean dropFontTags) {
        this.configuration.dropFontTags = dropFontTags;
    }

    public boolean getDropFontTags() {
        return this.configuration.dropFontTags;
    }

    public void setDropProprietaryAttributes(boolean dropProprietaryAttributes) {
        this.configuration.dropProprietaryAttributes = dropProprietaryAttributes;
    }

    public boolean getDropProprietaryAttributes() {
        return this.configuration.dropProprietaryAttributes;
    }

    public void setDropEmptyParas(boolean dropEmptyParas) {
        this.configuration.dropEmptyParas = dropEmptyParas;
    }

    public boolean getDropEmptyParas() {
        return this.configuration.dropEmptyParas;
    }

    public void setFixComments(boolean fixComments) {
        this.configuration.fixComments = fixComments;
    }

    public boolean getFixComments() {
        return this.configuration.fixComments;
    }

    public void setWrapAsp(boolean wrapAsp) {
        this.configuration.wrapAsp = wrapAsp;
    }

    public boolean getWrapAsp() {
        return this.configuration.wrapAsp;
    }

    public void setWrapJste(boolean wrapJste) {
        this.configuration.wrapJste = wrapJste;
    }

    public boolean getWrapJste() {
        return this.configuration.wrapJste;
    }

    public void setWrapPhp(boolean wrapPhp) {
        this.configuration.wrapPhp = wrapPhp;
    }

    public boolean getWrapPhp() {
        return this.configuration.wrapPhp;
    }

    public void setFixBackslash(boolean fixBackslash) {
        this.configuration.fixBackslash = fixBackslash;
    }

    public boolean getFixBackslash() {
        return this.configuration.fixBackslash;
    }

    public void setIndentAttributes(boolean indentAttributes) {
        this.configuration.indentAttributes = indentAttributes;
    }

    public boolean getIndentAttributes() {
        return this.configuration.indentAttributes;
    }

    public void setDocType(String doctype) {
        if (doctype != null) {
            this.configuration.docTypeStr = (String)ParsePropertyImpl.DOCTYPE.parse(doctype, "doctype", this.configuration);
        }
    }

    public String getDocType() {
        String result = null;
        switch (this.configuration.docTypeMode) {
            case 0: {
                result = "omit";
                break;
            }
            case 1: {
                result = "auto";
                break;
            }
            case 2: {
                result = "strict";
                break;
            }
            case 3: {
                result = "loose";
                break;
            }
            case 4: {
                result = this.configuration.docTypeStr;
            }
        }
        return result;
    }

    public void setLogicalEmphasis(boolean logicalEmphasis) {
        this.configuration.logicalEmphasis = logicalEmphasis;
    }

    public boolean getLogicalEmphasis() {
        return this.configuration.logicalEmphasis;
    }

    public void setXmlPIs(boolean xmlPIs) {
        this.configuration.xmlPIs = xmlPIs;
    }

    public boolean getXmlPIs() {
        return this.configuration.xmlPIs;
    }

    public void setEncloseText(boolean encloseText) {
        this.configuration.encloseBodyText = encloseText;
    }

    public boolean getEncloseText() {
        return this.configuration.encloseBodyText;
    }

    public void setEncloseBlockText(boolean encloseBlockText) {
        this.configuration.encloseBlockText = encloseBlockText;
    }

    public boolean getEncloseBlockText() {
        return this.configuration.encloseBlockText;
    }

    public void setWord2000(boolean word2000) {
        this.configuration.word2000 = word2000;
    }

    public boolean getWord2000() {
        return this.configuration.word2000;
    }

    public void setTidyMark(boolean tidyMark) {
        this.configuration.tidyMark = tidyMark;
    }

    public boolean getTidyMark() {
        return this.configuration.tidyMark;
    }

    public void setXmlSpace(boolean xmlSpace) {
        this.configuration.xmlSpace = xmlSpace;
    }

    public boolean getXmlSpace() {
        return this.configuration.xmlSpace;
    }

    public void setEmacs(boolean emacs) {
        this.configuration.emacs = emacs;
    }

    public boolean getEmacs() {
        return this.configuration.emacs;
    }

    public void setLiteralAttribs(boolean literalAttribs) {
        this.configuration.literalAttribs = literalAttribs;
    }

    public boolean getLiteralAttribs() {
        return this.configuration.literalAttribs;
    }

    public void setPrintBodyOnly(boolean bodyOnly) {
        this.configuration.bodyOnly = bodyOnly;
    }

    public boolean getPrintBodyOnly() {
        return this.configuration.bodyOnly;
    }

    public void setFixUri(boolean fixUri) {
        this.configuration.fixUri = fixUri;
    }

    public boolean getFixUri() {
        return this.configuration.fixUri;
    }

    public void setLowerLiterals(boolean lowerLiterals) {
        this.configuration.lowerLiterals = lowerLiterals;
    }

    public boolean getLowerLiterals() {
        return this.configuration.lowerLiterals;
    }

    public void setHideComments(boolean hideComments) {
        this.configuration.hideComments = hideComments;
    }

    public boolean getHideComments() {
        return this.configuration.hideComments;
    }

    public void setIndentCdata(boolean indentCdata) {
        this.configuration.indentCdata = indentCdata;
    }

    public boolean getIndentCdata() {
        return this.configuration.indentCdata;
    }

    public void setForceOutput(boolean forceOutput) {
        this.configuration.forceOutput = forceOutput;
    }

    public boolean getForceOutput() {
        return this.configuration.forceOutput;
    }

    public void setShowErrors(int showErrors) {
        this.configuration.showErrors = showErrors;
    }

    public int getShowErrors() {
        return this.configuration.showErrors;
    }

    public void setAsciiChars(boolean asciiChars) {
        this.configuration.asciiChars = asciiChars;
    }

    public boolean getAsciiChars() {
        return this.configuration.asciiChars;
    }

    public void setJoinClasses(boolean joinClasses) {
        this.configuration.joinClasses = joinClasses;
    }

    public boolean getJoinClasses() {
        return this.configuration.joinClasses;
    }

    public void setJoinStyles(boolean joinStyles) {
        this.configuration.joinStyles = joinStyles;
    }

    public boolean getJoinStyles() {
        return this.configuration.joinStyles;
    }

    public void setTrimEmptyElements(boolean trimEmpty) {
        this.configuration.trimEmpty = trimEmpty;
    }

    public boolean getTrimEmptyElements() {
        return this.configuration.trimEmpty;
    }

    public void setReplaceColor(boolean replaceColor) {
        this.configuration.replaceColor = replaceColor;
    }

    public boolean getReplaceColor() {
        return this.configuration.replaceColor;
    }

    public void setEscapeCdata(boolean escapeCdata) {
        this.configuration.escapeCdata = escapeCdata;
    }

    public boolean getEscapeCdata() {
        return this.configuration.escapeCdata;
    }

    public void setRepeatedAttributes(int repeatedAttributes) {
        this.configuration.duplicateAttrs = repeatedAttributes;
    }

    public int getRepeatedAttributes() {
        return this.configuration.duplicateAttrs;
    }

    public void setKeepFileTimes(boolean keepFileTimes) {
        this.configuration.keepFileTimes = keepFileTimes;
    }

    public boolean getKeepFileTimes() {
        return this.configuration.keepFileTimes;
    }

    public void setRawOut(boolean rawOut) {
        this.configuration.rawOut = rawOut;
    }

    public boolean getRawOut() {
        return this.configuration.rawOut;
    }

    public void setInputEncoding(String encoding) {
        this.configuration.setInCharEncodingName(encoding);
    }

    public String getInputEncoding() {
        return this.configuration.getInCharEncodingName();
    }

    public void setOutputEncoding(String encoding) {
        this.configuration.setOutCharEncodingName(encoding);
    }

    public String getOutputEncoding() {
        return this.configuration.getOutCharEncodingName();
    }

    static {
        CMDLINE_ALIAS.put("xml", "input-xml");
        CMDLINE_ALIAS.put("xml", "output-xhtml");
        CMDLINE_ALIAS.put("asxml", "output-xhtml");
        CMDLINE_ALIAS.put("ashtml", "output-html");
        CMDLINE_ALIAS.put("omit", "hide-endtags");
        CMDLINE_ALIAS.put("upper", "uppercase-tags");
        CMDLINE_ALIAS.put("raw", "output-raw");
        CMDLINE_ALIAS.put("numeric", "numeric-entities");
        CMDLINE_ALIAS.put("change", "write-back");
        CMDLINE_ALIAS.put("update", "write-back");
        CMDLINE_ALIAS.put("modify", "write-back");
        CMDLINE_ALIAS.put("errors", "only-errors");
        CMDLINE_ALIAS.put("slides", "split");
        CMDLINE_ALIAS.put("lang", "language");
        CMDLINE_ALIAS.put("w", "wrap");
        CMDLINE_ALIAS.put("file", "error-file");
        CMDLINE_ALIAS.put("f", "error-file");
    }
}

