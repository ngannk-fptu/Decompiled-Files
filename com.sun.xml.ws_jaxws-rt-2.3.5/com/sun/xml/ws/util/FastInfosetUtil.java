/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.resources.StreamingMessages;
import com.sun.xml.ws.streaming.XMLReaderException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

public class FastInfosetUtil {
    private static final FISupport fi;
    private static final Logger LOG;

    private FastInfosetUtil() {
    }

    public static Codec getFICodec(StreamSOAPCodec soapCodec, SOAPVersion version) {
        return fi.getFICodec(soapCodec, version);
    }

    public static Codec getFICodec() {
        return fi.getFICodec();
    }

    public static boolean isFastInfosetSource(Source o) {
        return fi.isFastInfosetSource(o);
    }

    public static XMLStreamReader createFIStreamReader(Source source) {
        return fi.createFIStreamReader(source);
    }

    static {
        FISupport s;
        block6: {
            LOG = Logger.getLogger(FastInfosetUtil.class.getName());
            s = null;
            try {
                if (Class.forName("com.sun.xml.ws.encoding.fastinfoset.FastInfosetCodec") != null) {
                    s = (FISupport)Class.forName("com.sun.xml.ws.util.FISupportImpl").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    LOG.config(StreamingMessages.FASTINFOSET_ENABLED());
                }
            }
            catch (ClassNotFoundException | NoClassDefFoundError t) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.log(Level.FINEST, t.getMessage(), t);
                }
            }
            catch (ReflectiveOperationException | SecurityException t) {
                if (!LOG.isLoggable(Level.FINER)) break block6;
                LOG.log(Level.FINER, t.getMessage(), t);
            }
        }
        if (s == null) {
            LOG.config(StreamingMessages.FASTINFOSET_NO_IMPLEMENTATION());
            s = new FISupport(){

                @Override
                public boolean isFastInfosetSource(Source o) {
                    return false;
                }

                @Override
                public XMLStreamReader createFIStreamReader(Source source) {
                    throw new XMLReaderException("fastinfoset.noImplementation", new Object[0]);
                }

                @Override
                public Codec getFICodec(StreamSOAPCodec soapCodec, SOAPVersion version) {
                    return null;
                }

                @Override
                public Codec getFICodec() {
                    return null;
                }
            };
        }
        fi = s;
    }

    static interface FISupport {
        public boolean isFastInfosetSource(Source var1);

        public XMLStreamReader createFIStreamReader(Source var1);

        public Codec getFICodec(StreamSOAPCodec var1, SOAPVersion var2);

        public Codec getFICodec();
    }
}

