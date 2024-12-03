/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.Resolution
 *  aQute.bnd.annotation.spi.ServiceProvider
 *  org.codehaus.stax2.XMLEventReader2
 *  org.codehaus.stax2.XMLInputFactory2
 *  org.codehaus.stax2.XMLStreamReader2
 *  org.codehaus.stax2.io.Stax2ByteArraySource
 *  org.codehaus.stax2.io.Stax2Source
 *  org.codehaus.stax2.ri.Stax2FilteredStreamReader
 *  org.codehaus.stax2.ri.Stax2ReaderAdapter
 *  org.codehaus.stax2.ri.evt.Stax2EventReaderAdapter
 *  org.codehaus.stax2.ri.evt.Stax2FilteredEventReader
 */
package com.ctc.wstx.stax;

import aQute.bnd.annotation.Resolution;
import aQute.bnd.annotation.spi.ServiceProvider;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.InputConfigFlags;
import com.ctc.wstx.dom.WstxDOMWrappingReader;
import com.ctc.wstx.dtd.DTDId;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.evt.DefaultEventAllocator;
import com.ctc.wstx.evt.WstxEventReader;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.BranchingReaderSource;
import com.ctc.wstx.io.DefaultInputResolver;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.InputSourceFactory;
import com.ctc.wstx.io.ReaderBootstrapper;
import com.ctc.wstx.io.StreamBootstrapper;
import com.ctc.wstx.io.SystemId;
import com.ctc.wstx.sr.ReaderCreator;
import com.ctc.wstx.sr.ValidatingStreamReader;
import com.ctc.wstx.util.DefaultXmlSymbolTable;
import com.ctc.wstx.util.SimpleCache;
import com.ctc.wstx.util.SymbolTable;
import com.ctc.wstx.util.URLUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.stream.EventFilter;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.io.Stax2ByteArraySource;
import org.codehaus.stax2.io.Stax2Source;
import org.codehaus.stax2.ri.Stax2FilteredStreamReader;
import org.codehaus.stax2.ri.Stax2ReaderAdapter;
import org.codehaus.stax2.ri.evt.Stax2EventReaderAdapter;
import org.codehaus.stax2.ri.evt.Stax2FilteredEventReader;
import org.xml.sax.InputSource;

@ServiceProvider(value=XMLInputFactory.class, resolution=Resolution.OPTIONAL)
public class WstxInputFactory
extends XMLInputFactory2
implements ReaderCreator,
InputConfigFlags {
    static final int MAX_SYMBOL_TABLE_SIZE = 12000;
    static final int MAX_SYMBOL_TABLE_GENERATIONS = 500;
    protected final ReaderConfig mConfig;
    protected XMLEventAllocator mAllocator = null;
    protected SimpleCache<DTDId, DTDSubset> mDTDCache = null;
    static final SymbolTable mRootSymbols = DefaultXmlSymbolTable.getInstance();
    private SymbolTable mSymbols = mRootSymbols;

    public WstxInputFactory() {
        this.mConfig = ReaderConfig.createFullDefaults();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addSymbol(String symbol) {
        SymbolTable symbolTable = this.mSymbols;
        synchronized (symbolTable) {
            this.mSymbols.findSymbol(symbol);
        }
    }

    @Override
    public synchronized DTDSubset findCachedDTD(DTDId id) {
        return this.mDTDCache == null ? null : this.mDTDCache.find(id);
    }

    @Override
    public synchronized void updateSymbolTable(SymbolTable t) {
        SymbolTable curr = this.mSymbols;
        if (t.isDirectChildOf(curr)) {
            if (t.size() > 12000 || t.version() > 500) {
                this.mSymbols = mRootSymbols;
            } else {
                this.mSymbols.mergeChild(t);
            }
        }
    }

    @Override
    public synchronized void addCachedDTD(DTDId id, DTDSubset extSubset) {
        if (this.mDTDCache == null) {
            this.mDTDCache = new SimpleCache(this.mConfig.getDtdCacheSize());
        }
        this.mDTDCache.add(id, extSubset);
    }

    public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) {
        return new Stax2FilteredEventReader(Stax2EventReaderAdapter.wrapIfNecessary((XMLEventReader)reader), filter);
    }

    public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
        Stax2FilteredStreamReader fr = new Stax2FilteredStreamReader(reader, filter);
        if (!filter.accept((XMLStreamReader)fr)) {
            fr.next();
        }
        return fr;
    }

    public XMLEventReader createXMLEventReader(InputStream in) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(null, in, null, true, false));
    }

    public XMLEventReader createXMLEventReader(InputStream in, String enc) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(null, in, enc, true, false));
    }

    public XMLEventReader createXMLEventReader(Reader r) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(null, r, true, false));
    }

    public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(source, true));
    }

    public XMLEventReader createXMLEventReader(String systemId, InputStream in) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(SystemId.construct(systemId), in, null, true, false));
    }

    public XMLEventReader createXMLEventReader(String systemId, Reader r) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(SystemId.construct(systemId), r, true, false));
    }

    public XMLEventReader createXMLEventReader(XMLStreamReader sr) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), Stax2ReaderAdapter.wrapIfNecessary((XMLStreamReader)sr));
    }

    public XMLStreamReader createXMLStreamReader(InputStream in) throws XMLStreamException {
        return this.createSR(null, in, null, false, false);
    }

    public XMLStreamReader createXMLStreamReader(InputStream in, String enc) throws XMLStreamException {
        return this.createSR(null, in, enc, false, false);
    }

    public XMLStreamReader createXMLStreamReader(Reader r) throws XMLStreamException {
        return this.createSR(null, r, false, false);
    }

    public XMLStreamReader createXMLStreamReader(Source src) throws XMLStreamException {
        return this.createSR(src, false);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, InputStream in) throws XMLStreamException {
        return this.createSR(SystemId.construct(systemId), in, null, false, false);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, Reader r) throws XMLStreamException {
        return this.createSR(SystemId.construct(systemId), r, false, false);
    }

    public Object getProperty(String name) {
        Object ob = this.mConfig.getProperty(name);
        if (ob == null && name.equals("javax.xml.stream.allocator")) {
            return this.getEventAllocator();
        }
        return ob;
    }

    public void setProperty(String propName, Object value) {
        if (!this.mConfig.setProperty(propName, value) && "javax.xml.stream.allocator".equals(propName)) {
            this.setEventAllocator((XMLEventAllocator)value);
        }
    }

    public XMLEventAllocator getEventAllocator() {
        return this.mAllocator;
    }

    public XMLReporter getXMLReporter() {
        return this.mConfig.getXMLReporter();
    }

    public XMLResolver getXMLResolver() {
        return this.mConfig.getXMLResolver();
    }

    public boolean isPropertySupported(String name) {
        return this.mConfig.isPropertySupported(name);
    }

    public void setEventAllocator(XMLEventAllocator allocator) {
        this.mAllocator = allocator;
    }

    public void setXMLReporter(XMLReporter r) {
        this.mConfig.setXMLReporter(r);
    }

    public void setXMLResolver(XMLResolver r) {
        this.mConfig.setXMLResolver(r);
    }

    public XMLEventReader2 createXMLEventReader(URL src) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(this.createPrivateConfig(), src, true, true));
    }

    public XMLEventReader2 createXMLEventReader(File f) throws XMLStreamException {
        return new WstxEventReader(this.createEventAllocator(), this.createSR(f, true, true));
    }

    public XMLStreamReader2 createXMLStreamReader(URL src) throws XMLStreamException {
        return this.createSR(this.createPrivateConfig(), src, false, true);
    }

    public XMLStreamReader2 createXMLStreamReader(File f) throws XMLStreamException {
        return this.createSR(f, false, true);
    }

    public void configureForXmlConformance() {
        this.mConfig.configureForXmlConformance();
    }

    public void configureForConvenience() {
        this.mConfig.configureForConvenience();
    }

    public void configureForSpeed() {
        this.mConfig.configureForSpeed();
    }

    public void configureForLowMemUsage() {
        this.mConfig.configureForLowMemUsage();
    }

    public void configureForRoundTripping() {
        this.mConfig.configureForRoundTripping();
    }

    public ReaderConfig getConfig() {
        return this.mConfig;
    }

    private XMLStreamReader2 doCreateSR(ReaderConfig cfg, SystemId systemId, InputBootstrapper bs, boolean forER, boolean autoCloseInput) throws XMLStreamException {
        Reader r;
        if (!autoCloseInput) {
            autoCloseInput = cfg.willAutoCloseInput();
        }
        try {
            r = bs.bootstrapInput(cfg, true, 0);
            if (bs.declaredXml11()) {
                cfg.enableXml11(true);
            }
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
        BranchingReaderSource input = InputSourceFactory.constructDocumentSource(cfg, bs, null, systemId, r, autoCloseInput);
        return ValidatingStreamReader.createValidatingStreamReader(input, this, cfg, bs, forER);
    }

    public XMLStreamReader2 createSR(ReaderConfig cfg, String systemId, InputBootstrapper bs, boolean forER, boolean autoCloseInput) throws XMLStreamException {
        URL src = cfg.getBaseURL();
        if (src == null && systemId != null && systemId.length() > 0) {
            try {
                src = URLUtil.urlFromSystemId(systemId);
            }
            catch (IOException ie) {
                throw new WstxIOException(ie);
            }
        }
        return this.doCreateSR(cfg, SystemId.construct(systemId, src), bs, forER, autoCloseInput);
    }

    public XMLStreamReader2 createSR(ReaderConfig cfg, SystemId systemId, InputBootstrapper bs, boolean forER, boolean autoCloseInput) throws XMLStreamException {
        return this.doCreateSR(cfg, systemId, bs, forER, autoCloseInput);
    }

    protected XMLStreamReader2 createSR(SystemId systemId, InputStream in, String enc, boolean forER, boolean autoCloseInput) throws XMLStreamException {
        if (in == null) {
            throw new IllegalArgumentException("Null InputStream is not a valid argument");
        }
        ReaderConfig cfg = this.createPrivateConfig();
        if (enc == null || enc.length() == 0) {
            return this.createSR(cfg, systemId, (InputBootstrapper)StreamBootstrapper.getInstance(null, systemId, in), forER, autoCloseInput);
        }
        Reader r = DefaultInputResolver.constructOptimizedReader(cfg, in, false, enc);
        return this.createSR(cfg, systemId, (InputBootstrapper)ReaderBootstrapper.getInstance(null, systemId, r, enc), forER, autoCloseInput);
    }

    protected XMLStreamReader2 createSR(ReaderConfig cfg, URL src, boolean forER, boolean autoCloseInput) throws XMLStreamException {
        SystemId systemId = SystemId.construct(src);
        try {
            return this.createSR(cfg, systemId, URLUtil.inputStreamFromURL(src), forER, autoCloseInput);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    private XMLStreamReader2 createSR(ReaderConfig cfg, SystemId systemId, InputStream in, boolean forER, boolean autoCloseInput) throws XMLStreamException {
        return this.doCreateSR(cfg, systemId, StreamBootstrapper.getInstance(null, systemId, in), forER, autoCloseInput);
    }

    protected XMLStreamReader2 createSR(SystemId systemId, Reader r, boolean forER, boolean autoCloseInput) throws XMLStreamException {
        return this.createSR(this.createPrivateConfig(), systemId, (InputBootstrapper)ReaderBootstrapper.getInstance(null, systemId, r, null), forER, autoCloseInput);
    }

    protected XMLStreamReader2 createSR(File f, boolean forER, boolean autoCloseInput) throws XMLStreamException {
        ReaderConfig cfg = this.createPrivateConfig();
        try {
            URL base;
            if (!f.isAbsolute() && (base = cfg.getBaseURL()) != null) {
                URL src = new URL(base, f.getPath());
                return this.createSR(cfg, SystemId.construct(src), URLUtil.inputStreamFromURL(src), forER, autoCloseInput);
            }
            SystemId systemId = SystemId.construct(URLUtil.toURL(f));
            return this.createSR(cfg, systemId, new FileInputStream(f), forER, autoCloseInput);
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }

    protected XMLStreamReader2 createSR(Source src, boolean forER) throws XMLStreamException {
        boolean autoCloseInput;
        Object ss;
        ReaderConfig cfg = this.createPrivateConfig();
        Reader r = null;
        InputStream in = null;
        String pubId = null;
        String sysId = null;
        String encoding = null;
        InputBootstrapper bs = null;
        if (src instanceof Stax2Source) {
            ss = (Stax2Source)src;
            sysId = ss.getSystemId();
            pubId = ss.getPublicId();
            encoding = ss.getEncoding();
            try {
                if (src instanceof Stax2ByteArraySource) {
                    Stax2ByteArraySource bas = (Stax2ByteArraySource)src;
                    bs = StreamBootstrapper.getInstance(pubId, SystemId.construct(sysId), bas.getBuffer(), bas.getBufferStart(), bas.getBufferEnd());
                } else {
                    in = ss.constructInputStream();
                    if (in == null) {
                        r = ss.constructReader();
                    }
                }
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
            autoCloseInput = true;
        } else if (src instanceof StreamSource) {
            ss = (StreamSource)src;
            sysId = ((StreamSource)ss).getSystemId();
            pubId = ((StreamSource)ss).getPublicId();
            in = ((StreamSource)ss).getInputStream();
            if (in == null) {
                r = ((StreamSource)ss).getReader();
            }
            autoCloseInput = cfg.willAutoCloseInput();
        } else if (src instanceof SAXSource) {
            ss = (SAXSource)src;
            sysId = ((SAXSource)ss).getSystemId();
            InputSource isrc = ((SAXSource)ss).getInputSource();
            if (isrc != null) {
                encoding = isrc.getEncoding();
                in = isrc.getByteStream();
                if (in == null) {
                    r = isrc.getCharacterStream();
                }
            }
            autoCloseInput = cfg.willAutoCloseInput();
        } else {
            if (src instanceof DOMSource) {
                DOMSource domSrc = (DOMSource)src;
                return WstxDOMWrappingReader.createFrom(domSrc, cfg);
            }
            throw new IllegalArgumentException("Can not instantiate Stax reader for XML source type " + src.getClass() + " (unrecognized type)");
        }
        if (bs == null) {
            if (r != null) {
                bs = ReaderBootstrapper.getInstance(pubId, SystemId.construct(sysId), r, encoding);
            } else if (in != null) {
                bs = StreamBootstrapper.getInstance(pubId, SystemId.construct(sysId), in);
            } else {
                if (sysId != null && sysId.length() > 0) {
                    autoCloseInput = true;
                    try {
                        return this.createSR(cfg, URLUtil.urlFromSystemId(sysId), forER, autoCloseInput);
                    }
                    catch (IOException ioe) {
                        throw new WstxIOException(ioe);
                    }
                }
                throw new XMLStreamException("Can not create Stax reader for the Source passed -- neither reader, input stream nor system id was accessible; can not use other types of sources (like embedded SAX streams)");
            }
        }
        return this.createSR(cfg, sysId, bs, forER, autoCloseInput);
    }

    protected XMLEventAllocator createEventAllocator() {
        if (this.mAllocator != null) {
            return this.mAllocator.newInstance();
        }
        return this.mConfig.willPreserveLocation() ? DefaultEventAllocator.getDefaultInstance() : DefaultEventAllocator.getFastInstance();
    }

    public ReaderConfig createPrivateConfig() {
        return this.mConfig.createNonShared(this.mSymbols.makeChild());
    }

    static {
        mRootSymbols.setInternStrings(true);
    }
}

