/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.DatatypeConverter
 *  javax.xml.bind.JAXBContext
 */
package com.amazonaws.util;

import com.amazonaws.log.InternalLogApi;
import com.amazonaws.log.InternalLogFactory;
import com.amazonaws.util.Base64Codec;
import com.amazonaws.util.CodecUtils;
import java.util.HashMap;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;

public final class Base64
extends Enum<Base64> {
    private static final InternalLogApi LOG;
    private static final Base64Codec codec;
    private static final boolean isJaxbAvailable;
    private static final /* synthetic */ Base64[] $VALUES;

    public static Base64[] values() {
        return (Base64[])$VALUES.clone();
    }

    public static Base64 valueOf(String name) {
        return Enum.valueOf(Base64.class, name);
    }

    public static String encodeAsString(byte ... bytes) {
        if (bytes == null) {
            return null;
        }
        if (isJaxbAvailable) {
            try {
                return DatatypeConverter.printBase64Binary((byte[])bytes);
            }
            catch (NullPointerException ex) {
                LOG.debug("Recovering from JAXB bug: https://netbeans.org/bugzilla/show_bug.cgi?id=224923", ex);
            }
        }
        return bytes.length == 0 ? "" : CodecUtils.toStringDirect(codec.encode(bytes));
    }

    public static byte[] encode(byte[] bytes) {
        return bytes == null || bytes.length == 0 ? bytes : codec.encode(bytes);
    }

    public static byte[] decode(String b64) {
        if (b64 == null) {
            return null;
        }
        if (b64.length() == 0) {
            return new byte[0];
        }
        byte[] buf = new byte[b64.length()];
        int len = CodecUtils.sanitize(b64, buf);
        return codec.decode(buf, len);
    }

    public static byte[] decode(byte[] b64) {
        return b64 == null || b64.length == 0 ? b64 : codec.decode(b64, b64.length);
    }

    static {
        boolean available;
        $VALUES = new Base64[0];
        LOG = InternalLogFactory.getLog(Base64.class);
        codec = new Base64Codec();
        try {
            Class.forName("javax.xml.bind.DatatypeConverter");
            available = true;
        }
        catch (Exception e) {
            available = false;
        }
        if (available) {
            HashMap<String, String> inconsistentJaxbImpls = new HashMap<String, String>();
            inconsistentJaxbImpls.put("org.apache.ws.jaxme.impl.JAXBContextImpl", "Apache JaxMe");
            try {
                String className = JAXBContext.newInstance((Class[])new Class[0]).getClass().getName();
                if (inconsistentJaxbImpls.containsKey(className)) {
                    LOG.warn("A JAXB implementation known to produce base64 encodings that are inconsistent with the reference implementation has been detected. The results of the encodeAsString() method may be incorrect. Implementation: " + (String)inconsistentJaxbImpls.get(className));
                }
            }
            catch (UnsupportedOperationException ignored) {
                available = false;
            }
            catch (Exception exception) {
            }
            catch (NoClassDefFoundError noClassDefFoundError) {}
        } else {
            LOG.warn("JAXB is unavailable. Will fallback to SDK implementation which may be less performant.If you are using Java 9+, you will need to include javax.xml.bind:jaxb-api as a dependency.");
        }
        isJaxbAvailable = available;
    }
}

