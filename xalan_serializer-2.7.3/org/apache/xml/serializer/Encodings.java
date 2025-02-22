/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.xml.serializer.EncodingInfo;
import org.apache.xml.serializer.ObjectFactory;
import org.apache.xml.serializer.SecuritySupport;
import org.apache.xml.serializer.SerializerBase;
import org.apache.xml.serializer.utils.WrappedRuntimeException;

public final class Encodings {
    private static final String ENCODINGS_FILE = SerializerBase.PKG_PATH + "/Encodings.properties";
    static final String DEFAULT_MIME_ENCODING = "UTF-8";
    private static final Hashtable _encodingTableKeyJava = new Hashtable();
    private static final Hashtable _encodingTableKeyMime = new Hashtable();
    private static final EncodingInfo[] _encodings = Encodings.loadEncodingInfo();

    static Writer getWriter(OutputStream output, String encoding) throws UnsupportedEncodingException {
        for (int i = 0; i < _encodings.length; ++i) {
            if (!Encodings._encodings[i].name.equalsIgnoreCase(encoding)) continue;
            try {
                String javaName = Encodings._encodings[i].javaName;
                OutputStreamWriter osw = new OutputStreamWriter(output, javaName);
                return osw;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                continue;
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
        }
        try {
            return new OutputStreamWriter(output, encoding);
        }
        catch (IllegalArgumentException iae) {
            throw new UnsupportedEncodingException(encoding);
        }
    }

    static EncodingInfo getEncodingInfo(String encoding) {
        String normalizedEncoding = Encodings.toUpperCaseFast(encoding);
        EncodingInfo ei = (EncodingInfo)_encodingTableKeyJava.get(normalizedEncoding);
        if (ei == null) {
            ei = (EncodingInfo)_encodingTableKeyMime.get(normalizedEncoding);
        }
        if (ei == null) {
            ei = new EncodingInfo(null, null, '\u0000');
        }
        return ei;
    }

    public static boolean isRecognizedEncoding(String encoding) {
        String normalizedEncoding = encoding.toUpperCase();
        EncodingInfo ei = (EncodingInfo)_encodingTableKeyJava.get(normalizedEncoding);
        if (ei == null) {
            ei = (EncodingInfo)_encodingTableKeyMime.get(normalizedEncoding);
        }
        return ei != null;
    }

    private static String toUpperCaseFast(String s) {
        boolean different = false;
        int mx = s.length();
        char[] chars = new char[mx];
        for (int i = 0; i < mx; ++i) {
            char ch = s.charAt(i);
            if ('a' <= ch && ch <= 'z') {
                ch = (char)(ch + -32);
                different = true;
            }
            chars[i] = ch;
        }
        String upper = different ? String.valueOf(chars) : s;
        return upper;
    }

    static String getMimeEncoding(String encoding) {
        block5: {
            if (null == encoding) {
                try {
                    encoding = System.getProperty("file.encoding", "UTF8");
                    if (null != encoding) {
                        String jencoding = encoding.equalsIgnoreCase("Cp1252") || encoding.equalsIgnoreCase("ISO8859_1") || encoding.equalsIgnoreCase("8859_1") || encoding.equalsIgnoreCase("UTF8") ? DEFAULT_MIME_ENCODING : Encodings.convertJava2MimeEncoding(encoding);
                        encoding = null != jencoding ? jencoding : DEFAULT_MIME_ENCODING;
                        break block5;
                    }
                    encoding = DEFAULT_MIME_ENCODING;
                }
                catch (SecurityException se) {
                    encoding = DEFAULT_MIME_ENCODING;
                }
            } else {
                encoding = Encodings.convertJava2MimeEncoding(encoding);
            }
        }
        return encoding;
    }

    private static String convertJava2MimeEncoding(String encoding) {
        EncodingInfo enc = (EncodingInfo)_encodingTableKeyJava.get(Encodings.toUpperCaseFast(encoding));
        if (null != enc) {
            return enc.name;
        }
        return encoding;
    }

    public static String convertMime2JavaEncoding(String encoding) {
        for (int i = 0; i < _encodings.length; ++i) {
            if (!Encodings._encodings[i].name.equalsIgnoreCase(encoding)) continue;
            return Encodings._encodings[i].javaName;
        }
        return encoding;
    }

    private static EncodingInfo[] loadEncodingInfo() {
        try {
            InputStream is = SecuritySupport.getResourceAsStream(ObjectFactory.findClassLoader(), ENCODINGS_FILE);
            Properties props = new Properties();
            if (is != null) {
                props.load(is);
                is.close();
            }
            int totalEntries = props.size();
            ArrayList<EncodingInfo> encodingInfo_list = new ArrayList<EncodingInfo>();
            Enumeration<Object> keys = props.keys();
            for (int i = 0; i < totalEntries; ++i) {
                char highChar;
                String mimeName;
                String javaName = (String)keys.nextElement();
                String val = props.getProperty(javaName);
                int len = Encodings.lengthOfMimeNames(val);
                if (len == 0) {
                    mimeName = javaName;
                    highChar = '\u0000';
                    continue;
                }
                try {
                    String highVal = val.substring(len).trim();
                    highChar = (char)Integer.decode(highVal).intValue();
                }
                catch (NumberFormatException e) {
                    highChar = '\u0000';
                }
                String mimeNames = val.substring(0, len);
                StringTokenizer st = new StringTokenizer(mimeNames, ",");
                boolean first = true;
                while (st.hasMoreTokens()) {
                    mimeName = st.nextToken();
                    EncodingInfo ei = new EncodingInfo(mimeName, javaName, highChar);
                    encodingInfo_list.add(ei);
                    _encodingTableKeyMime.put(mimeName.toUpperCase(), ei);
                    if (first) {
                        _encodingTableKeyJava.put(javaName.toUpperCase(), ei);
                    }
                    first = false;
                }
            }
            EncodingInfo[] ret_ei = new EncodingInfo[encodingInfo_list.size()];
            encodingInfo_list.toArray(ret_ei);
            return ret_ei;
        }
        catch (MalformedURLException mue) {
            throw new WrappedRuntimeException(mue);
        }
        catch (IOException ioe) {
            throw new WrappedRuntimeException(ioe);
        }
    }

    private static int lengthOfMimeNames(String val) {
        int len = val.indexOf(32);
        if (len < 0) {
            len = val.length();
        }
        return len;
    }

    static boolean isHighUTF16Surrogate(char ch) {
        return '\ud800' <= ch && ch <= '\udbff';
    }

    static boolean isLowUTF16Surrogate(char ch) {
        return '\udc00' <= ch && ch <= '\udfff';
    }

    static int toCodePoint(char highSurrogate, char lowSurrogate) {
        int codePoint = (highSurrogate - 55296 << 10) + (lowSurrogate - 56320) + 65536;
        return codePoint;
    }

    static int toCodePoint(char ch) {
        char codePoint = ch;
        return codePoint;
    }

    public static char getHighChar(String encoding) {
        String normalizedEncoding = Encodings.toUpperCaseFast(encoding);
        EncodingInfo ei = (EncodingInfo)_encodingTableKeyJava.get(normalizedEncoding);
        if (ei == null) {
            ei = (EncodingInfo)_encodingTableKeyMime.get(normalizedEncoding);
        }
        char highCodePoint = ei != null ? ei.getHighChar() : (char)'\u0000';
        return highCodePoint;
    }
}

