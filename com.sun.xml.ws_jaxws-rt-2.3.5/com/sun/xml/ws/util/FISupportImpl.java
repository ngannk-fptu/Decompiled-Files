/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.fastinfoset.stax.StAXDocumentParser
 *  org.jvnet.fastinfoset.FastInfosetSource
 */
package com.sun.xml.ws.util;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.resources.StreamingMessages;
import com.sun.xml.ws.util.FastInfosetUtil;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import org.jvnet.fastinfoset.FastInfosetSource;

class FISupportImpl
implements FastInfosetUtil.FISupport {
    private static final Logger LOG = Logger.getLogger(FISupportImpl.class.getName());
    private MethodHandle codec;
    private MethodHandle streamCodec;

    FISupportImpl() {
    }

    @Override
    public boolean isFastInfosetSource(Source o) {
        return o instanceof FastInfosetSource;
    }

    @Override
    public XMLStreamReader createFIStreamReader(Source source) {
        StAXDocumentParser stAXDocumentParser = new StAXDocumentParser();
        stAXDocumentParser.setInputStream(((FastInfosetSource)source).getInputStream());
        stAXDocumentParser.setStringInterning(true);
        return stAXDocumentParser;
    }

    @Override
    public Codec getFICodec() {
        try {
            return this.getCodecHandle().invoke();
        }
        catch (Throwable t) {
            LOG.fine(StreamingMessages.FASTINFOSET_EXCEPTION());
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.log(Level.FINEST, t.getMessage(), t);
            }
            return null;
        }
    }

    @Override
    public Codec getFICodec(StreamSOAPCodec soapCodec, SOAPVersion version) {
        try {
            return this.getStreamCodecHandle().invoke(soapCodec, version);
        }
        catch (Throwable t) {
            LOG.fine(StreamingMessages.FASTINFOSET_EXCEPTION());
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.log(Level.FINEST, t.getMessage(), t);
            }
            return null;
        }
    }

    private MethodHandle getCodecHandle() throws ReflectiveOperationException {
        if (this.codec == null) {
            Class<?> c = Class.forName("com.sun.xml.ws.encoding.fastinfoset.FastInfosetCodec");
            Method m = c.getMethod("create", new Class[0]);
            this.codec = MethodHandles.publicLookup().unreflect(m);
        }
        return this.codec;
    }

    private MethodHandle getStreamCodecHandle() throws ReflectiveOperationException {
        if (this.streamCodec == null) {
            Class<?> c = Class.forName("com.sun.xml.ws.encoding.fastinfoset.FastInfosetStreamSOAPCodec");
            Method m = c.getMethod("create", StreamSOAPCodec.class, SOAPVersion.class);
            this.streamCodec = MethodHandles.publicLookup().unreflect(m);
        }
        return this.streamCodec;
    }
}

