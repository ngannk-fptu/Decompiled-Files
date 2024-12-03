/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xinclude;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Map;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.io.ASCIIReader;
import org.apache.xerces.impl.io.Latin1Reader;
import org.apache.xerces.impl.io.UTF16Reader;
import org.apache.xerces.impl.io.UTF8Reader;
import org.apache.xerces.util.EncodingMap;
import org.apache.xerces.util.HTTPInputSource;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xinclude.XIncludeHandler;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.parser.XMLInputSource;

public class XIncludeTextReader {
    private Reader fReader;
    private final XIncludeHandler fHandler;
    private XMLInputSource fSource;
    private XMLErrorReporter fErrorReporter;
    private XMLString fTempString = new XMLString();

    public XIncludeTextReader(XMLInputSource xMLInputSource, XIncludeHandler xIncludeHandler, int n) throws IOException {
        this.fHandler = xIncludeHandler;
        this.fSource = xMLInputSource;
        this.fTempString = new XMLString(new char[n + 1], 0, 0);
    }

    public void setErrorReporter(XMLErrorReporter xMLErrorReporter) {
        this.fErrorReporter = xMLErrorReporter;
    }

    protected Reader getReader(XMLInputSource xMLInputSource) throws IOException {
        Object object;
        Object object2;
        String string;
        if (xMLInputSource.getCharacterStream() != null) {
            return xMLInputSource.getCharacterStream();
        }
        InputStream inputStream = null;
        String string2 = xMLInputSource.getEncoding();
        if (string2 == null) {
            string2 = "UTF-8";
        }
        if (xMLInputSource.getByteStream() != null) {
            inputStream = xMLInputSource.getByteStream();
            if (!(inputStream instanceof BufferedInputStream)) {
                inputStream = new BufferedInputStream(inputStream, this.fTempString.ch.length);
            }
        } else {
            Object object3;
            Object object4;
            string = XMLEntityManager.expandSystemId(xMLInputSource.getSystemId(), xMLInputSource.getBaseSystemId(), false);
            object2 = new URL(string);
            object = ((URL)object2).openConnection();
            if (object instanceof HttpURLConnection && xMLInputSource instanceof HTTPInputSource) {
                object4 = (HttpURLConnection)object;
                HTTPInputSource hTTPInputSource = (HTTPInputSource)xMLInputSource;
                object3 = hTTPInputSource.getHTTPRequestProperties();
                while (object3.hasNext()) {
                    Map.Entry entry = (Map.Entry)object3.next();
                    ((URLConnection)object4).setRequestProperty((String)entry.getKey(), (String)entry.getValue());
                }
                boolean bl = hTTPInputSource.getFollowHTTPRedirects();
                if (!bl) {
                    ((HttpURLConnection)object4).setInstanceFollowRedirects(bl);
                }
            }
            inputStream = new BufferedInputStream(((URLConnection)object).getInputStream());
            object4 = ((URLConnection)object).getContentType();
            int n = object4 != null ? ((String)object4).indexOf(59) : -1;
            String string3 = null;
            if (n != -1) {
                object3 = ((String)object4).substring(0, n).trim();
                string3 = ((String)object4).substring(n + 1).trim();
                if (string3.startsWith("charset=")) {
                    if ((string3 = string3.substring(8).trim()).charAt(0) == '\"' && string3.charAt(string3.length() - 1) == '\"' || string3.charAt(0) == '\'' && string3.charAt(string3.length() - 1) == '\'') {
                        string3 = string3.substring(1, string3.length() - 1);
                    }
                } else {
                    string3 = null;
                }
            } else {
                object3 = object4 != null ? ((String)object4).trim() : "";
            }
            String string4 = null;
            if (((String)object3).equals("text/xml")) {
                string4 = string3 != null ? string3 : "US-ASCII";
            } else if (((String)object3).equals("application/xml")) {
                string4 = string3 != null ? string3 : this.getEncodingName(inputStream);
            } else if (((String)object3).endsWith("+xml")) {
                string4 = this.getEncodingName(inputStream);
            }
            if (string4 != null) {
                string2 = string4;
            }
        }
        string2 = string2.toUpperCase(Locale.ENGLISH);
        if ((string2 = this.consumeBOM(inputStream, string2)).equals("UTF-8")) {
            return this.createUTF8Reader(inputStream);
        }
        if (string2.equals("UTF-16BE")) {
            return this.createUTF16Reader(inputStream, true);
        }
        if (string2.equals("UTF-16LE")) {
            return this.createUTF16Reader(inputStream, false);
        }
        string = EncodingMap.getIANA2JavaMapping(string2);
        if (string == null) {
            object2 = this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210");
            object = this.fErrorReporter.getLocale();
            throw new IOException(object2.formatMessage((Locale)object, "EncodingDeclInvalid", new Object[]{string2}));
        }
        if (string.equals("ASCII")) {
            return this.createASCIIReader(inputStream);
        }
        if (string.equals("ISO8859_1")) {
            return this.createLatin1Reader(inputStream);
        }
        return new InputStreamReader(inputStream, string);
    }

    private Reader createUTF8Reader(InputStream inputStream) {
        return new UTF8Reader(inputStream, this.fTempString.ch.length, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }

    private Reader createUTF16Reader(InputStream inputStream, boolean bl) {
        return new UTF16Reader(inputStream, this.fTempString.ch.length << 1, bl, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }

    private Reader createASCIIReader(InputStream inputStream) {
        return new ASCIIReader(inputStream, this.fTempString.ch.length, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }

    private Reader createLatin1Reader(InputStream inputStream) {
        return new Latin1Reader(inputStream, this.fTempString.ch.length);
    }

    protected String getEncodingName(InputStream inputStream) throws IOException {
        byte[] byArray = new byte[4];
        String string = null;
        inputStream.mark(4);
        int n = inputStream.read(byArray, 0, 4);
        inputStream.reset();
        if (n == 4) {
            string = this.getEncodingName(byArray);
        }
        return string;
    }

    protected String consumeBOM(InputStream inputStream, String string) throws IOException {
        byte[] byArray = new byte[3];
        int n = 0;
        inputStream.mark(3);
        if (string.equals("UTF-8")) {
            n = inputStream.read(byArray, 0, 3);
            if (n == 3) {
                int n2 = byArray[0] & 0xFF;
                int n3 = byArray[1] & 0xFF;
                int n4 = byArray[2] & 0xFF;
                if (n2 != 239 || n3 != 187 || n4 != 191) {
                    inputStream.reset();
                }
            } else {
                inputStream.reset();
            }
        } else if (string.startsWith("UTF-16")) {
            n = inputStream.read(byArray, 0, 2);
            if (n == 2) {
                int n5 = byArray[0] & 0xFF;
                int n6 = byArray[1] & 0xFF;
                if (n5 == 254 && n6 == 255) {
                    return "UTF-16BE";
                }
                if (n5 == 255 && n6 == 254) {
                    return "UTF-16LE";
                }
            }
            inputStream.reset();
        }
        return string;
    }

    protected String getEncodingName(byte[] byArray) {
        int n = byArray[0] & 0xFF;
        int n2 = byArray[1] & 0xFF;
        if (n == 254 && n2 == 255) {
            return "UTF-16BE";
        }
        if (n == 255 && n2 == 254) {
            return "UTF-16LE";
        }
        int n3 = byArray[2] & 0xFF;
        if (n == 239 && n2 == 187 && n3 == 191) {
            return "UTF-8";
        }
        int n4 = byArray[3] & 0xFF;
        if (n == 0 && n2 == 0 && n3 == 0 && n4 == 60) {
            return "ISO-10646-UCS-4";
        }
        if (n == 60 && n2 == 0 && n3 == 0 && n4 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (n == 0 && n2 == 0 && n3 == 60 && n4 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (n == 0 && n2 == 60 && n3 == 0 && n4 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (n == 0 && n2 == 60 && n3 == 0 && n4 == 63) {
            return "UTF-16BE";
        }
        if (n == 60 && n2 == 0 && n3 == 63 && n4 == 0) {
            return "UTF-16LE";
        }
        if (n == 76 && n2 == 111 && n3 == 167 && n4 == 148) {
            return "CP037";
        }
        return null;
    }

    public void parse() throws IOException {
        this.fReader = this.getReader(this.fSource);
        this.fSource = null;
        int n = this.fReader.read(this.fTempString.ch, 0, this.fTempString.ch.length - 1);
        this.fHandler.fHasIncludeReportedContent = true;
        while (n != -1) {
            for (int i = 0; i < n; ++i) {
                char c = this.fTempString.ch[i];
                if (this.isValid(c)) continue;
                if (XMLChar.isHighSurrogate(c)) {
                    int n2;
                    if (++i < n) {
                        n2 = this.fTempString.ch[i];
                    } else {
                        n2 = this.fReader.read();
                        if (n2 != -1) {
                            this.fTempString.ch[n++] = (char)n2;
                        }
                    }
                    if (XMLChar.isLowSurrogate(n2)) {
                        int n3 = XMLChar.supplemental(c, (char)n2);
                        if (this.isValid(n3)) continue;
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[]{Integer.toString(n3, 16)}, (short)2);
                        continue;
                    }
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[]{Integer.toString(n2, 16)}, (short)2);
                    continue;
                }
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[]{Integer.toString(c, 16)}, (short)2);
            }
            if (this.fHandler != null && n > 0) {
                this.fTempString.offset = 0;
                this.fTempString.length = n;
                this.fHandler.characters(this.fTempString, this.fHandler.modifyAugmentations(null, true));
            }
            n = this.fReader.read(this.fTempString.ch, 0, this.fTempString.ch.length - 1);
        }
    }

    public void setInputSource(XMLInputSource xMLInputSource) {
        this.fSource = xMLInputSource;
    }

    public void close() throws IOException {
        if (this.fReader != null) {
            this.fReader.close();
            this.fReader = null;
        }
    }

    protected boolean isValid(int n) {
        return XMLChar.isValid(n);
    }

    protected void setBufferSize(int n) {
        if (this.fTempString.ch.length != ++n) {
            this.fTempString.ch = new char[n];
        }
    }
}

