/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.AsciiReader;
import com.ctc.wstx.io.BaseReader;
import com.ctc.wstx.io.CharsetNames;
import com.ctc.wstx.io.ISOLatinReader;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.InputSourceFactory;
import com.ctc.wstx.io.ReaderBootstrapper;
import com.ctc.wstx.io.StreamBootstrapper;
import com.ctc.wstx.io.SystemId;
import com.ctc.wstx.io.UTF32Reader;
import com.ctc.wstx.io.UTF8Reader;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.util.URLUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public final class DefaultInputResolver {
    private DefaultInputResolver() {
    }

    public static WstxInputSource resolveEntity(WstxInputSource parent, URL pathCtxt, String entityName, String publicId, String systemId, XMLResolver customResolver, ReaderConfig cfg, int xmlVersion) throws IOException, XMLStreamException {
        Object source;
        if (pathCtxt == null && (pathCtxt = parent.getSource()) == null) {
            pathCtxt = URLUtil.urlFromCurrentDir();
        }
        if (customResolver != null && (source = customResolver.resolveEntity(publicId, systemId, pathCtxt.toExternalForm(), entityName)) != null) {
            return DefaultInputResolver.sourceFrom(parent, cfg, entityName, xmlVersion, source);
        }
        if (systemId == null) {
            throw new XMLStreamException("Can not resolve " + (entityName == null ? "[External DTD subset]" : "entity '" + entityName + "'") + " without a system id (public id '" + publicId + "')");
        }
        URL url = URLUtil.urlFromSystemId(systemId, pathCtxt);
        return DefaultInputResolver.sourceFromURL(parent, cfg, entityName, xmlVersion, url, publicId);
    }

    public static WstxInputSource resolveEntityUsing(WstxInputSource refCtxt, String entityName, String publicId, String systemId, XMLResolver resolver, ReaderConfig cfg, int xmlVersion) throws IOException, XMLStreamException {
        Object source;
        URL ctxt;
        URL uRL = ctxt = refCtxt == null ? null : refCtxt.getSource();
        if (ctxt == null) {
            ctxt = URLUtil.urlFromCurrentDir();
        }
        return (source = resolver.resolveEntity(publicId, systemId, ctxt.toExternalForm(), entityName)) == null ? null : DefaultInputResolver.sourceFrom(refCtxt, cfg, entityName, xmlVersion, source);
    }

    protected static WstxInputSource sourceFrom(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, Object o) throws IllegalArgumentException, IOException, XMLStreamException {
        if (o instanceof Source) {
            if (o instanceof StreamSource) {
                return DefaultInputResolver.sourceFromSS(parent, cfg, refName, xmlVersion, (StreamSource)o);
            }
            throw new IllegalArgumentException("Can not use other Source objects than StreamSource: got " + o.getClass());
        }
        if (o instanceof URL) {
            return DefaultInputResolver.sourceFromURL(parent, cfg, refName, xmlVersion, (URL)o, null);
        }
        if (o instanceof InputStream) {
            return DefaultInputResolver.sourceFromIS(parent, cfg, refName, xmlVersion, (InputStream)o, null, null);
        }
        if (o instanceof Reader) {
            return DefaultInputResolver.sourceFromR(parent, cfg, refName, xmlVersion, (Reader)o, null, null);
        }
        if (o instanceof String) {
            return DefaultInputResolver.sourceFromString(parent, cfg, refName, xmlVersion, (String)o);
        }
        if (o instanceof File) {
            URL u = URLUtil.toURL((File)o);
            return DefaultInputResolver.sourceFromURL(parent, cfg, refName, xmlVersion, u, null);
        }
        throw new IllegalArgumentException("Unrecognized input argument type for sourceFrom(): " + o.getClass());
    }

    public static Reader constructOptimizedReader(ReaderConfig cfg, InputStream in, boolean isXml11, String encoding) throws XMLStreamException {
        BaseReader r;
        int inputBufLen = cfg.getInputBufferLength();
        String normEnc = CharsetNames.normalize(encoding);
        boolean recycleBuffer = true;
        if (normEnc == "UTF-8") {
            r = new UTF8Reader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, recycleBuffer);
        } else if (normEnc == "ISO-8859-1") {
            r = new ISOLatinReader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, recycleBuffer);
        } else if (normEnc == "US-ASCII") {
            r = new AsciiReader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, recycleBuffer);
        } else if (normEnc.startsWith("UTF-32")) {
            boolean isBE = normEnc == "UTF-32BE";
            r = new UTF32Reader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, recycleBuffer, isBE);
        } else {
            try {
                return new InputStreamReader(in, encoding);
            }
            catch (UnsupportedEncodingException ex) {
                throw new XMLStreamException("[unsupported encoding]: " + ex);
            }
        }
        if (isXml11) {
            r.setXmlCompliancy(272);
        }
        return r;
    }

    private static WstxInputSource sourceFromSS(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, StreamSource ssrc) throws IOException, XMLStreamException {
        InputBootstrapper bs;
        Reader r = ssrc.getReader();
        String pubId = ssrc.getPublicId();
        String sysId0 = ssrc.getSystemId();
        URL ctxt = parent == null ? null : parent.getSource();
        URL url = sysId0 == null || sysId0.length() == 0 ? null : URLUtil.urlFromSystemId(sysId0, ctxt);
        SystemId systemId = SystemId.construct(sysId0, url == null ? ctxt : url);
        if (r == null) {
            InputStream in = ssrc.getInputStream();
            if (in == null) {
                if (url == null) {
                    throw new IllegalArgumentException("Can not create Stax reader for a StreamSource -- neither reader, input stream nor system id was set.");
                }
                in = URLUtil.inputStreamFromURL(url);
            }
            bs = StreamBootstrapper.getInstance(pubId, systemId, in);
        } else {
            bs = ReaderBootstrapper.getInstance(pubId, systemId, r, null);
        }
        Reader r2 = ((InputBootstrapper)bs).bootstrapInput(cfg, false, xmlVersion);
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, bs, pubId, systemId, xmlVersion, r2);
    }

    private static WstxInputSource sourceFromURL(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, URL url, String pubId) throws IOException, XMLStreamException {
        InputStream in = URLUtil.inputStreamFromURL(url);
        SystemId sysId = SystemId.construct(url);
        StreamBootstrapper bs = StreamBootstrapper.getInstance(pubId, sysId, in);
        Reader r = bs.bootstrapInput(cfg, false, xmlVersion);
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, bs, pubId, sysId, xmlVersion, r);
    }

    public static WstxInputSource sourceFromString(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, String refContent) throws IOException, XMLStreamException {
        return DefaultInputResolver.sourceFromR(parent, cfg, refName, xmlVersion, new StringReader(refContent), null, refName);
    }

    private static WstxInputSource sourceFromIS(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, InputStream is, String pubId, String sysId) throws IOException, XMLStreamException {
        StreamBootstrapper bs = StreamBootstrapper.getInstance(pubId, SystemId.construct(sysId), is);
        Reader r = bs.bootstrapInput(cfg, false, xmlVersion);
        URL ctxt = parent.getSource();
        if (sysId != null && sysId.length() > 0) {
            ctxt = URLUtil.urlFromSystemId(sysId, ctxt);
        }
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, bs, pubId, SystemId.construct(sysId, ctxt), xmlVersion, r);
    }

    private static WstxInputSource sourceFromR(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, Reader r, String pubId, String sysId) throws IOException, XMLStreamException {
        URL ctxt;
        ReaderBootstrapper rbs = ReaderBootstrapper.getInstance(pubId, SystemId.construct(sysId), r, null);
        Reader r2 = rbs.bootstrapInput(cfg, false, xmlVersion);
        URL uRL = ctxt = parent == null ? null : parent.getSource();
        if (sysId != null && sysId.length() > 0) {
            ctxt = URLUtil.urlFromSystemId(sysId, ctxt);
        }
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, rbs, pubId, SystemId.construct(sysId, ctxt), xmlVersion, r2);
    }
}

