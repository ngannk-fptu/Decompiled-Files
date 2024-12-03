/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.parsers.SAXParser
 *  org.apache.xml.serialize.XMLSerializer
 */
package org.apache.xml.dtm.ref;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xml.dtm.ref.CoroutineManager;
import org.apache.xml.dtm.ref.IncrementalSAXSource;
import org.apache.xml.dtm.ref.IncrementalSAXSource_Filter;
import org.apache.xml.dtm.ref.ObjectFactory;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.utils.WrappedRuntimeException;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class IncrementalSAXSource_Xerces
implements IncrementalSAXSource {
    Method fParseSomeSetup = null;
    Method fParseSome = null;
    Object fPullParserConfig = null;
    Method fConfigSetInput = null;
    Method fConfigParse = null;
    Method fSetInputSource = null;
    Constructor fConfigInputSourceCtor = null;
    Method fConfigSetByteStream = null;
    Method fConfigSetCharStream = null;
    Method fConfigSetEncoding = null;
    Method fReset = null;
    SAXParser fIncrementalParser;
    private boolean fParseInProgress = false;
    private static final Object[] noparms = new Object[0];
    private static final Object[] parmsfalse = new Object[]{Boolean.FALSE};

    public IncrementalSAXSource_Xerces() throws NoSuchMethodException {
        try {
            Class xniConfigClass = ObjectFactory.findProviderClass("org.apache.xerces.xni.parser.XMLParserConfiguration", ObjectFactory.findClassLoader(), true);
            Class[] args1 = new Class[]{xniConfigClass};
            Constructor ctor = SAXParser.class.getConstructor(args1);
            Class xniStdConfigClass = ObjectFactory.findProviderClass("org.apache.xerces.parsers.StandardParserConfiguration", ObjectFactory.findClassLoader(), true);
            this.fPullParserConfig = xniStdConfigClass.newInstance();
            Object[] args2 = new Object[]{this.fPullParserConfig};
            this.fIncrementalParser = (SAXParser)ctor.newInstance(args2);
            Class fXniInputSourceClass = ObjectFactory.findProviderClass("org.apache.xerces.xni.parser.XMLInputSource", ObjectFactory.findClassLoader(), true);
            Class[] args3 = new Class[]{fXniInputSourceClass};
            this.fConfigSetInput = xniStdConfigClass.getMethod("setInputSource", args3);
            Class[] args4 = new Class[]{String.class, String.class, String.class};
            this.fConfigInputSourceCtor = fXniInputSourceClass.getConstructor(args4);
            Class[] args5 = new Class[]{InputStream.class};
            this.fConfigSetByteStream = fXniInputSourceClass.getMethod("setByteStream", args5);
            Class[] args6 = new Class[]{Reader.class};
            this.fConfigSetCharStream = fXniInputSourceClass.getMethod("setCharacterStream", args6);
            Class[] args7 = new Class[]{String.class};
            this.fConfigSetEncoding = fXniInputSourceClass.getMethod("setEncoding", args7);
            Class[] argsb = new Class[]{Boolean.TYPE};
            this.fConfigParse = xniStdConfigClass.getMethod("parse", argsb);
            Class[] noargs = new Class[]{};
            this.fReset = this.fIncrementalParser.getClass().getMethod("reset", noargs);
        }
        catch (Exception e) {
            IncrementalSAXSource_Xerces dummy = new IncrementalSAXSource_Xerces(new SAXParser());
            this.fParseSomeSetup = dummy.fParseSomeSetup;
            this.fParseSome = dummy.fParseSome;
            this.fIncrementalParser = dummy.fIncrementalParser;
        }
    }

    public IncrementalSAXSource_Xerces(SAXParser parser) throws NoSuchMethodException {
        this.fIncrementalParser = parser;
        Class<?> me = parser.getClass();
        Class[] parms = new Class[]{InputSource.class};
        this.fParseSomeSetup = me.getMethod("parseSomeSetup", parms);
        parms = new Class[]{};
        this.fParseSome = me.getMethod("parseSome", parms);
    }

    public static IncrementalSAXSource createIncrementalSAXSource() {
        try {
            return new IncrementalSAXSource_Xerces();
        }
        catch (NoSuchMethodException e) {
            IncrementalSAXSource_Filter iss = new IncrementalSAXSource_Filter();
            iss.setXMLReader((XMLReader)new SAXParser());
            return iss;
        }
    }

    public static IncrementalSAXSource createIncrementalSAXSource(SAXParser parser) {
        try {
            return new IncrementalSAXSource_Xerces(parser);
        }
        catch (NoSuchMethodException e) {
            IncrementalSAXSource_Filter iss = new IncrementalSAXSource_Filter();
            iss.setXMLReader((XMLReader)parser);
            return iss;
        }
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        this.fIncrementalParser.setContentHandler(handler);
    }

    @Override
    public void setLexicalHandler(LexicalHandler handler) {
        try {
            this.fIncrementalParser.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        }
        catch (SAXNotRecognizedException sAXNotRecognizedException) {
        }
        catch (SAXNotSupportedException sAXNotSupportedException) {
            // empty catch block
        }
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
        this.fIncrementalParser.setDTDHandler(handler);
    }

    @Override
    public void startParse(InputSource source) throws SAXException {
        if (this.fIncrementalParser == null) {
            throw new SAXException(XMLMessages.createXMLMessage("ER_STARTPARSE_NEEDS_SAXPARSER", null));
        }
        if (this.fParseInProgress) {
            throw new SAXException(XMLMessages.createXMLMessage("ER_STARTPARSE_WHILE_PARSING", null));
        }
        boolean ok = false;
        try {
            ok = this.parseSomeSetup(source);
        }
        catch (Exception ex) {
            throw new SAXException(ex);
        }
        if (!ok) {
            throw new SAXException(XMLMessages.createXMLMessage("ER_COULD_NOT_INIT_PARSER", null));
        }
    }

    @Override
    public Object deliverMoreNodes(boolean parsemore) {
        Serializable arg;
        if (!parsemore) {
            this.fParseInProgress = false;
            return Boolean.FALSE;
        }
        try {
            boolean keepgoing = this.parseSome();
            arg = keepgoing ? Boolean.TRUE : Boolean.FALSE;
        }
        catch (SAXException ex) {
            arg = ex;
        }
        catch (IOException ex) {
            arg = ex;
        }
        catch (Exception ex) {
            arg = new SAXException(ex);
        }
        return arg;
    }

    private boolean parseSomeSetup(InputSource source) throws SAXException, IOException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (this.fConfigSetInput != null) {
            Object[] parms1 = new Object[]{source.getPublicId(), source.getSystemId(), null};
            Object xmlsource = this.fConfigInputSourceCtor.newInstance(parms1);
            Object[] parmsa = new Object[]{source.getByteStream()};
            this.fConfigSetByteStream.invoke(xmlsource, parmsa);
            parmsa[0] = source.getCharacterStream();
            this.fConfigSetCharStream.invoke(xmlsource, parmsa);
            parmsa[0] = source.getEncoding();
            this.fConfigSetEncoding.invoke(xmlsource, parmsa);
            Object[] noparms = new Object[]{};
            this.fReset.invoke((Object)this.fIncrementalParser, noparms);
            parmsa[0] = xmlsource;
            this.fConfigSetInput.invoke(this.fPullParserConfig, parmsa);
            return this.parseSome();
        }
        Object[] parm = new Object[]{source};
        Object ret = this.fParseSomeSetup.invoke((Object)this.fIncrementalParser, parm);
        return (Boolean)ret;
    }

    private boolean parseSome() throws SAXException, IOException, IllegalAccessException, InvocationTargetException {
        if (this.fConfigSetInput != null) {
            Boolean ret = (Boolean)this.fConfigParse.invoke(this.fPullParserConfig, parmsfalse);
            return ret;
        }
        Object ret = this.fParseSome.invoke((Object)this.fIncrementalParser, noparms);
        return (Boolean)ret;
    }

    public static void main(String[] args) {
        System.out.println("Starting...");
        CoroutineManager co = new CoroutineManager();
        int appCoroutineID = co.co_joinCoroutineSet(-1);
        if (appCoroutineID == -1) {
            System.out.println("ERROR: Couldn't allocate coroutine number.\n");
            return;
        }
        IncrementalSAXSource parser = IncrementalSAXSource_Xerces.createIncrementalSAXSource();
        XMLSerializer trace = new XMLSerializer((OutputStream)System.out, null);
        parser.setContentHandler((ContentHandler)trace);
        parser.setLexicalHandler((LexicalHandler)trace);
        for (int arg = 0; arg < args.length; ++arg) {
            try {
                InputSource source = new InputSource(args[arg]);
                Object result = null;
                boolean more = true;
                parser.startParse(source);
                result = parser.deliverMoreNodes(more);
                while (result == Boolean.TRUE) {
                    System.out.println("\nSome parsing successful, trying more.\n");
                    if (arg + 1 < args.length && "!".equals(args[arg + 1])) {
                        ++arg;
                        more = false;
                    }
                    result = parser.deliverMoreNodes(more);
                }
                if (result instanceof Boolean && (Boolean)result == Boolean.FALSE) {
                    System.out.println("\nParser ended (EOF or on request).\n");
                    continue;
                }
                if (result == null) {
                    System.out.println("\nUNEXPECTED: Parser says shut down prematurely.\n");
                    continue;
                }
                if (!(result instanceof Exception)) continue;
                throw new WrappedRuntimeException((Exception)result);
            }
            catch (SAXException e) {
                e.printStackTrace();
            }
        }
    }
}

