/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.spi.ServiceProvider
 */
package com.ctc.wstx.stax;

import aQute.bnd.annotation.spi.ServiceProvider;
import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.OutputConfigFlags;
import com.ctc.wstx.dom.WstxDOMWrappingWriter;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.CharsetNames;
import com.ctc.wstx.io.UTF8Writer;
import com.ctc.wstx.sw.AsciiXmlWriter;
import com.ctc.wstx.sw.BufferingXmlWriter;
import com.ctc.wstx.sw.ISOLatin1XmlWriter;
import com.ctc.wstx.sw.NonNsStreamWriter;
import com.ctc.wstx.sw.RepairingNsStreamWriter;
import com.ctc.wstx.sw.SimpleNsStreamWriter;
import com.ctc.wstx.sw.XmlWriter;
import com.ctc.wstx.util.URLUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.io.Stax2Result;
import org.codehaus.stax2.ri.Stax2EventWriterImpl;
import org.codehaus.stax2.ri.Stax2WriterAdapter;

@ServiceProvider(value=XMLOutputFactory.class)
public class WstxOutputFactory
extends XMLOutputFactory2
implements OutputConfigFlags {
    protected final WriterConfig mConfig = WriterConfig.createFullDefaults();

    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream out) throws XMLStreamException {
        return this.createXMLEventWriter(out, null);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream out, String enc) throws XMLStreamException {
        if (out == null) {
            throw new IllegalArgumentException("Null OutputStream is not a valid argument");
        }
        return new Stax2EventWriterImpl(this.createSW(out, null, enc, false));
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return new Stax2EventWriterImpl(this.createSW(result));
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Writer w) throws XMLStreamException {
        if (w == null) {
            throw new IllegalArgumentException("Null Writer is not a valid argument");
        }
        return new Stax2EventWriterImpl(this.createSW(null, w, null, false));
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream out) throws XMLStreamException {
        return this.createXMLStreamWriter(out, null);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream out, String enc) throws XMLStreamException {
        if (out == null) {
            throw new IllegalArgumentException("Null OutputStream is not a valid argument");
        }
        return this.createSW(out, null, enc, false);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return this.createSW(result);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Writer w) throws XMLStreamException {
        if (w == null) {
            throw new IllegalArgumentException("Null Writer is not a valid argument");
        }
        return this.createSW(null, w, null, false);
    }

    @Override
    public Object getProperty(String name) {
        return this.mConfig.getProperty(name);
    }

    @Override
    public boolean isPropertySupported(String name) {
        return this.mConfig.isPropertySupported(name);
    }

    @Override
    public void setProperty(String name, Object value) {
        this.mConfig.setProperty(name, value);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Writer w, String enc) throws XMLStreamException {
        return new Stax2EventWriterImpl(this.createSW(null, w, enc, false));
    }

    @Override
    public XMLEventWriter createXMLEventWriter(XMLStreamWriter sw) throws XMLStreamException {
        XMLStreamWriter2 sw2 = Stax2WriterAdapter.wrapIfNecessary(sw);
        return new Stax2EventWriterImpl(sw2);
    }

    @Override
    public XMLStreamWriter2 createXMLStreamWriter(Writer w, String enc) throws XMLStreamException {
        return this.createSW(null, w, enc, false);
    }

    @Override
    public void configureForXmlConformance() {
        this.mConfig.configureForXmlConformance();
    }

    @Override
    public void configureForRobustness() {
        this.mConfig.configureForRobustness();
    }

    @Override
    public void configureForSpeed() {
        this.mConfig.configureForSpeed();
    }

    public WriterConfig getConfig() {
        return this.mConfig;
    }

    private XMLStreamWriter2 createSW(OutputStream out, Writer w, String enc, boolean requireAutoClose) throws XMLStreamException {
        XmlWriter xw;
        boolean autoCloseOutput;
        WriterConfig cfg = this.mConfig.createNonShared();
        boolean bl = autoCloseOutput = requireAutoClose || this.mConfig.willAutoCloseOutput();
        if (w == null) {
            if (enc == null) {
                enc = "UTF-8";
            } else if (enc != "UTF-8" && enc != "ISO-8859-1" && enc != "US-ASCII") {
                enc = CharsetNames.normalize(enc);
            }
            try {
                if (enc == "UTF-8") {
                    w = new UTF8Writer(cfg, out, autoCloseOutput);
                    xw = new BufferingXmlWriter(w, cfg, enc, autoCloseOutput, out, 16);
                }
                if (enc == "ISO-8859-1") {
                    xw = new ISOLatin1XmlWriter(out, cfg, autoCloseOutput);
                }
                if (enc == "US-ASCII") {
                    xw = new AsciiXmlWriter(out, cfg, autoCloseOutput);
                }
                w = new OutputStreamWriter(out, enc);
                xw = new BufferingXmlWriter(w, cfg, enc, autoCloseOutput, out, -1);
            }
            catch (IOException ex) {
                throw new XMLStreamException(ex);
            }
        } else {
            if (enc == null) {
                enc = CharsetNames.findEncodingFor(w);
            }
            try {
                xw = new BufferingXmlWriter(w, cfg, enc, autoCloseOutput, null, -1);
            }
            catch (IOException ex) {
                throw new XMLStreamException(ex);
            }
        }
        return this.createSW(enc, cfg, xw);
    }

    protected XMLStreamWriter2 createSW(String enc, WriterConfig cfg, XmlWriter xw) {
        if (cfg.willSupportNamespaces()) {
            if (cfg.automaticNamespacesEnabled()) {
                return new RepairingNsStreamWriter(xw, enc, cfg);
            }
            return new SimpleNsStreamWriter(xw, enc, cfg);
        }
        return new NonNsStreamWriter(xw, enc, cfg);
    }

    private XMLStreamWriter2 createSW(Result res) throws XMLStreamException {
        boolean requireAutoClose;
        Result sr;
        OutputStream out = null;
        Writer w = null;
        String encoding = null;
        String sysId = null;
        if (res instanceof Stax2Result) {
            sr = (Stax2Result)res;
            try {
                out = ((Stax2Result)sr).constructOutputStream();
                if (out == null) {
                    w = ((Stax2Result)sr).constructWriter();
                }
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
            requireAutoClose = true;
        } else if (res instanceof StreamResult) {
            sr = (StreamResult)res;
            out = ((StreamResult)sr).getOutputStream();
            sysId = ((StreamResult)sr).getSystemId();
            if (out == null) {
                w = ((StreamResult)sr).getWriter();
            }
            requireAutoClose = false;
        } else if (res instanceof SAXResult) {
            sr = (SAXResult)res;
            sysId = ((SAXResult)sr).getSystemId();
            if (sysId == null || sysId.length() == 0) {
                throw new XMLStreamException("Can not create a stream writer for a SAXResult that does not have System Id (support for using SAX input source not implemented)");
            }
            requireAutoClose = true;
        } else {
            if (res instanceof DOMResult) {
                return WstxDOMWrappingWriter.createFrom(this.mConfig.createNonShared(), (DOMResult)res);
            }
            throw new IllegalArgumentException("Can not instantiate a writer for XML result type " + res.getClass() + " (unrecognized type)");
        }
        if (out != null) {
            return this.createSW(out, null, encoding, requireAutoClose);
        }
        if (w != null) {
            return this.createSW(null, w, encoding, requireAutoClose);
        }
        if (sysId != null && sysId.length() > 0) {
            requireAutoClose = true;
            try {
                out = URLUtil.outputStreamFromURL(URLUtil.urlFromSystemId(sysId));
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
            return this.createSW(out, null, encoding, requireAutoClose);
        }
        throw new XMLStreamException("Can not create Stax writer for passed-in Result -- neither writer, output stream or system id was accessible");
    }
}

