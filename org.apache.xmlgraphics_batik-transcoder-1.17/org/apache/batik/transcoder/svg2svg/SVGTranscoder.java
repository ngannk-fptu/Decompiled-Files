/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.util.DOMUtilities
 */
package org.apache.batik.transcoder.svg2svg;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.transcoder.AbstractTranscoder;
import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.IntegerKey;
import org.apache.batik.transcoder.keys.StringKey;
import org.apache.batik.transcoder.svg2svg.PrettyPrinter;
import org.w3c.dom.Document;

public class SVGTranscoder
extends AbstractTranscoder {
    public static final ErrorHandler DEFAULT_ERROR_HANDLER = new ErrorHandler(){

        @Override
        public void error(TranscoderException ex) throws TranscoderException {
            throw ex;
        }

        @Override
        public void fatalError(TranscoderException ex) throws TranscoderException {
            throw ex;
        }

        @Override
        public void warning(TranscoderException ex) throws TranscoderException {
        }
    };
    public static final TranscodingHints.Key KEY_NEWLINE = new NewlineKey();
    public static final NewlineValue VALUE_NEWLINE_CR = new NewlineValue("\r");
    public static final NewlineValue VALUE_NEWLINE_CR_LF = new NewlineValue("\r\n");
    public static final NewlineValue VALUE_NEWLINE_LF = new NewlineValue("\n");
    public static final TranscodingHints.Key KEY_FORMAT = new BooleanKey();
    public static final Boolean VALUE_FORMAT_ON = Boolean.TRUE;
    public static final Boolean VALUE_FORMAT_OFF = Boolean.FALSE;
    public static final TranscodingHints.Key KEY_TABULATION_WIDTH = new IntegerKey();
    public static final TranscodingHints.Key KEY_DOCUMENT_WIDTH = new IntegerKey();
    public static final TranscodingHints.Key KEY_DOCTYPE = new DoctypeKey();
    public static final DoctypeValue VALUE_DOCTYPE_CHANGE = new DoctypeValue(0);
    public static final DoctypeValue VALUE_DOCTYPE_REMOVE = new DoctypeValue(1);
    public static final DoctypeValue VALUE_DOCTYPE_KEEP_UNCHANGED = new DoctypeValue(2);
    public static final TranscodingHints.Key KEY_PUBLIC_ID = new StringKey();
    public static final TranscodingHints.Key KEY_SYSTEM_ID = new StringKey();
    public static final TranscodingHints.Key KEY_XML_DECLARATION = new StringKey();

    public SVGTranscoder() {
        this.setErrorHandler(DEFAULT_ERROR_HANDLER);
    }

    @Override
    public void transcode(TranscoderInput input, TranscoderOutput output) throws TranscoderException {
        Reader r = input.getReader();
        Writer w = output.getWriter();
        if (r == null) {
            Document d = input.getDocument();
            if (d == null) {
                throw new RuntimeException("Reader or Document expected");
            }
            StringWriter sw = new StringWriter(1024);
            try {
                DOMUtilities.writeDocument((Document)d, (Writer)sw);
            }
            catch (IOException ioEx) {
                throw new RuntimeException("IO:" + ioEx.getMessage());
            }
            r = new StringReader(sw.toString());
        }
        if (w == null) {
            throw new RuntimeException("Writer expected");
        }
        this.prettyPrint(r, w);
    }

    protected void prettyPrint(Reader in, Writer out) throws TranscoderException {
        try {
            String s;
            DoctypeValue dtv;
            Integer i;
            Boolean b;
            PrettyPrinter pp = new PrettyPrinter();
            NewlineValue nlv = (NewlineValue)this.hints.get(KEY_NEWLINE);
            if (nlv != null) {
                pp.setNewline(nlv.getValue());
            }
            if ((b = (Boolean)this.hints.get(KEY_FORMAT)) != null) {
                pp.setFormat(b);
            }
            if ((i = (Integer)this.hints.get(KEY_TABULATION_WIDTH)) != null) {
                pp.setTabulationWidth(i);
            }
            if ((i = (Integer)this.hints.get(KEY_DOCUMENT_WIDTH)) != null) {
                pp.setDocumentWidth(i);
            }
            if ((dtv = (DoctypeValue)this.hints.get(KEY_DOCTYPE)) != null) {
                pp.setDoctypeOption(dtv.getValue());
            }
            if ((s = (String)this.hints.get(KEY_PUBLIC_ID)) != null) {
                pp.setPublicId(s);
            }
            if ((s = (String)this.hints.get(KEY_SYSTEM_ID)) != null) {
                pp.setSystemId(s);
            }
            if ((s = (String)this.hints.get(KEY_XML_DECLARATION)) != null) {
                pp.setXMLDeclaration(s);
            }
            pp.print(in, out);
            out.flush();
        }
        catch (IOException e) {
            this.getErrorHandler().fatalError(new TranscoderException(e.getMessage()));
        }
    }

    protected static class DoctypeValue {
        final int value;

        protected DoctypeValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    protected static class DoctypeKey
    extends TranscodingHints.Key {
        protected DoctypeKey() {
        }

        @Override
        public boolean isCompatibleValue(Object v) {
            return v instanceof DoctypeValue;
        }
    }

    protected static class NewlineValue {
        protected final String value;

        protected NewlineValue(String val) {
            this.value = val;
        }

        public String getValue() {
            return this.value;
        }
    }

    protected static class NewlineKey
    extends TranscodingHints.Key {
        protected NewlineKey() {
        }

        @Override
        public boolean isCompatibleValue(Object v) {
            return v instanceof NewlineValue;
        }
    }
}

