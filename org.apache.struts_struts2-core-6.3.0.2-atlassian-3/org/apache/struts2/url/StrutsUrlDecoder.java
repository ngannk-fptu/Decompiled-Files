/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.message.ParameterizedMessage
 */
package org.apache.struts2.url;

import com.opensymphony.xwork2.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.url.UrlDecoder;

public class StrutsUrlDecoder
implements UrlDecoder {
    private static final Logger LOG = LogManager.getLogger(StrutsUrlDecoder.class);
    private static final Collection<Charset> AVAILABLE_CHARSETS = Charset.availableCharsets().values();
    private String encoding = "UTF-8";

    @Inject(value="struts.i18n.encoding", required=false)
    public void setEncoding(String encoding) {
        LOG.debug("Using default encoding: {}", (Object)encoding);
        if (StringUtils.isNotEmpty((CharSequence)encoding)) {
            this.encoding = encoding;
        }
    }

    @Override
    public String decode(String input, String encoding, boolean isQueryString) {
        if (input == null) {
            return null;
        }
        byte[] bytes = null;
        try {
            bytes = input.getBytes(this.getCharset(encoding));
        }
        catch (UnsupportedEncodingException uee) {
            LOG.debug((Message)new ParameterizedMessage("Unable to URL decode the specified input since the encoding: {} is not supported.", (Object)encoding), (Throwable)uee);
        }
        return this.internalDecode(bytes, encoding, isQueryString);
    }

    @Override
    public String decode(String input, boolean isQueryString) {
        return this.decode(input, this.encoding, isQueryString);
    }

    @Override
    public String decode(String input) {
        return this.decode(input, false);
    }

    private String internalDecode(byte[] bytes, String encoding, boolean isQuery) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        int ix = 0;
        int ox = 0;
        while (ix < len) {
            int b;
            if ((b = bytes[ix++]) == 43 && isQuery) {
                b = 32;
            } else if (b == 37) {
                if (ix + 2 > len) {
                    throw new IllegalArgumentException("The % character must be followed by two hexadecimal digits");
                }
                b = (byte)((this.convertHexDigit(bytes[ix++]) << 4 & 0xFF) + this.convertHexDigit(bytes[ix++]) & 0xFF & 0xFF);
            }
            bytes[ox++] = b;
        }
        if (encoding != null) {
            try {
                return new String(bytes, 0, ox, this.getCharset(encoding));
            }
            catch (UnsupportedEncodingException uee) {
                LOG.debug((Message)new ParameterizedMessage("Unable to URL decode the specified input since the encoding: {} is not supported.", (Object)encoding), (Throwable)uee);
                return null;
            }
        }
        return new String(bytes, 0, ox);
    }

    private byte convertHexDigit(byte b) {
        if (b >= 48 && b <= 57) {
            return (byte)(b - 48);
        }
        if (b >= 97 && b <= 102) {
            return (byte)(b - 97 + 10);
        }
        if (b >= 65 && b <= 70) {
            return (byte)(b - 65 + 10);
        }
        throw new IllegalArgumentException((char)b + " is not a hexadecimal digit");
    }

    private Charset getCharset(String encoding) throws UnsupportedEncodingException {
        for (Charset charset : AVAILABLE_CHARSETS) {
            if (!encoding.equalsIgnoreCase(charset.name())) continue;
            return charset;
        }
        throw new UnsupportedEncodingException("The character encoding " + encoding + " is not supported");
    }
}

