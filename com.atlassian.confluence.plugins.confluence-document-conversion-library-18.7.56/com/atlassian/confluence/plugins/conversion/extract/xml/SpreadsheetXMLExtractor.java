/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.search.v2.extractor.util.AbstractLengthLimitedStringBuilder
 *  com.atlassian.confluence.search.v2.extractor.util.LimitReachedException
 *  com.atlassian.plugins.conversion.extract.xml.AbstractXMLExtractor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.conversion.extract.xml;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.v2.extractor.util.AbstractLengthLimitedStringBuilder;
import com.atlassian.confluence.search.v2.extractor.util.LimitReachedException;
import com.atlassian.plugins.conversion.extract.xml.AbstractXMLExtractor;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.SAXParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SpreadsheetXMLExtractor
extends AbstractXMLExtractor {
    private static final Logger log = LoggerFactory.getLogger(SpreadsheetXMLExtractor.class);

    public static String extractText(InputStream inputStream, int maxSize, Attachment attachment) throws IOException {
        return SpreadsheetXMLExtractor.extractText(inputStream, maxSize);
    }

    /*
     * Exception decompiling
     */
    public static String extractText(InputStream inputStream, int maxSize) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
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

    private static void parseSharedStrings(SAXParser parser, ZipEntry ze, final ZipInputStream zin, ArrayList<String> strings) throws IOException {
        log.debug("parsing shared strings [ {} ]", (Object)ze.getName());
        BufferedInputStream in = new BufferedInputStream((InputStream)zin){

            @Override
            public void close() throws IOException {
                zin.closeEntry();
            }
        };
        try {
            SharedStringsHandler sharedStringsHandler = new SharedStringsHandler(strings);
            parser.parse((InputStream)in, (DefaultHandler)sharedStringsHandler);
        }
        catch (LimitReachedException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOException("Error parsing shared strings: " + e.getMessage(), e);
        }
    }

    private static void parseEntry(SAXParser parser, ZipEntry ze, final ZipInputStream zin, AbstractLengthLimitedStringBuilder outputBuffer, List<String> strings) throws IOException {
        if (!ze.getName().endsWith(".xml")) {
            log.debug("skipping [ {} ]", (Object)ze.getName());
            return;
        }
        log.debug("parsing [ {} ]", (Object)ze.getName());
        try {
            BufferedInputStream in = new BufferedInputStream((InputStream)zin){

                @Override
                public void close() throws IOException {
                    zin.closeEntry();
                }
            };
            parser.parse((InputStream)in, (DefaultHandler)new PartHandler(outputBuffer, strings));
        }
        catch (Exception e) {
            throw new IOException("Error parsing spreadsheet: " + e.getMessage(), e);
        }
    }

    private static class SharedStringsHandler
    extends DefaultHandler {
        private boolean capturingString = false;
        private boolean capturingStringPart = false;
        private final ArrayList<String> strings;
        private final StringBuilder buffer;
        private int uniqueCount;

        SharedStringsHandler(ArrayList<String> strings) {
            this.strings = strings;
            this.buffer = new StringBuilder(524288);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("si")) {
                this.capturingString = true;
            }
            if (this.capturingString && qName.equals("t")) {
                this.capturingStringPart = true;
            } else if (qName.equals("t")) {
                log.error("expected <si> before <t>");
            }
            if (qName.equals("sst")) {
                this.uniqueCount = Integer.parseInt(attributes.getValue("uniqueCount"));
                this.strings.ensureCapacity(this.uniqueCount);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (this.capturingStringPart) {
                this.buffer.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals("t")) {
                this.capturingStringPart = false;
            }
            if (qName.equals("si")) {
                this.strings.add(this.buffer.toString());
                this.buffer.setLength(0);
                this.capturingString = false;
            }
        }

        @Override
        public void endDocument() {
            if (this.strings.size() != this.uniqueCount) {
                log.error("expected [ {} ] entries but read [ {} ]", (Object)this.uniqueCount, (Object)this.strings.size());
            } else {
                log.debug("read [ {} ] shared strings", (Object)this.strings.size());
            }
        }
    }

    private static class PartHandler
    extends DefaultHandler {
        private final AbstractLengthLimitedStringBuilder outputBuffer;
        private final List<String> strings;
        private String capturingElement = null;
        private CapturingType type = null;
        private final StringBuilder buffer = new StringBuilder(128);

        PartHandler(AbstractLengthLimitedStringBuilder outputBuffer, List<String> strings) {
            this.outputBuffer = outputBuffer;
            this.strings = strings;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("c") || qName.equals("si")) {
                this.type = "s".equals(attributes.getValue("t")) ? CapturingType.INDEXED : CapturingType.LITERAL;
                log.debug("capturing [ {} ]", (Object)qName);
                this.capturingElement = qName;
            } else if (qName.equals("sheet")) {
                this.appendToOutput(attributes.getValue("name"));
            } else {
                log.debug("skipping [ {} ]", (Object)qName);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (this.capturingElement != null) {
                this.buffer.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals(this.capturingElement)) {
                this.capturingElement = null;
                switch (this.type) {
                    case INDEXED: {
                        int index = Integer.parseInt(this.buffer.toString());
                        this.appendToOutput(this.strings.get(index));
                        break;
                    }
                    case LITERAL: {
                        this.appendToOutput(this.buffer.toString());
                    }
                }
                this.type = null;
                this.buffer.setLength(0);
            }
        }

        private void appendToOutput(String s) {
            this.outputBuffer.append(' ').append(s);
        }
    }

    private static enum CapturingType {
        INDEXED,
        LITERAL;

    }
}

