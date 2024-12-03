/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.encoding;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.apache.axis.components.encoding.DefaultXMLEncoder;
import org.apache.axis.components.encoding.UTF16Encoder;
import org.apache.axis.components.encoding.UTF8Encoder;
import org.apache.axis.components.encoding.XMLEncoder;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
import org.apache.commons.logging.Log;

public class XMLEncoderFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$encoding$XMLEncoderFactory == null ? (class$org$apache$axis$components$encoding$XMLEncoderFactory = XMLEncoderFactory.class$("org.apache.axis.components.encoding.XMLEncoderFactory")) : class$org$apache$axis$components$encoding$XMLEncoderFactory).getName());
    public static final String ENCODING_UTF_8 = "UTF-8";
    public static final String ENCODING_UTF_16 = "UTF-16";
    public static final String DEFAULT_ENCODING = "UTF-8";
    private static Map encoderMap = new HashMap();
    private static final String PLUGABLE_PROVIDER_FILENAME = "org.apache.axis.components.encoding.XMLEncoder";
    static /* synthetic */ Class class$org$apache$axis$components$encoding$XMLEncoderFactory;
    static /* synthetic */ Class class$org$apache$axis$components$encoding$XMLEncoder;

    public static XMLEncoder getDefaultEncoder() {
        try {
            return XMLEncoderFactory.getEncoder("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(Messages.getMessage("unsupportedDefaultEncoding00", "UTF-8"));
        }
    }

    public static XMLEncoder getEncoder(String encoding) throws UnsupportedEncodingException {
        XMLEncoder encoder = (XMLEncoder)encoderMap.get(encoding);
        if (encoder == null) {
            encoder = new DefaultXMLEncoder(encoding);
            encoderMap.put(encoding, encoder);
        }
        return encoder;
    }

    private static void loadPluggableEncoders() {
        ClassLoader clzLoader = (class$org$apache$axis$components$encoding$XMLEncoder == null ? (class$org$apache$axis$components$encoding$XMLEncoder = XMLEncoderFactory.class$(PLUGABLE_PROVIDER_FILENAME)) : class$org$apache$axis$components$encoding$XMLEncoder).getClassLoader();
        ClassLoaders loaders = new ClassLoaders();
        loaders.put(clzLoader);
        DiscoverServiceNames dsn = new DiscoverServiceNames(loaders);
        ResourceNameIterator iter = dsn.findResourceNames(PLUGABLE_PROVIDER_FILENAME);
        while (iter.hasNext()) {
            String className = iter.nextResourceName();
            try {
                Object o = Class.forName(className).newInstance();
                if (!(o instanceof XMLEncoder)) continue;
                XMLEncoder encoder = (XMLEncoder)o;
                encoderMap.put(encoder.getEncoding(), encoder);
                encoderMap.put(encoder.getEncoding().toLowerCase(), encoder);
            }
            catch (Exception e) {
                String msg = e + JavaUtils.LS + JavaUtils.stackToString(e);
                log.info((Object)Messages.getMessage("exception01", msg));
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        encoderMap.put("UTF-8", new UTF8Encoder());
        encoderMap.put(ENCODING_UTF_16, new UTF16Encoder());
        encoderMap.put("UTF-8".toLowerCase(), new UTF8Encoder());
        encoderMap.put(ENCODING_UTF_16.toLowerCase(), new UTF16Encoder());
        try {
            XMLEncoderFactory.loadPluggableEncoders();
        }
        catch (Throwable t) {
            String msg = t + JavaUtils.LS + JavaUtils.stackToString(t);
            log.info((Object)Messages.getMessage("exception01", msg));
        }
    }
}

