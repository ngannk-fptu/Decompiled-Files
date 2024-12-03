/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.res.StringManager;

public class TLSClientHelloExtractor {
    private static final Log log = LogFactory.getLog(TLSClientHelloExtractor.class);
    private static final StringManager sm = StringManager.getManager(TLSClientHelloExtractor.class);
    private final ExtractorResult result;
    private final List<Cipher> clientRequestedCiphers;
    private final List<String> clientRequestedCipherNames;
    private final String sniValue;
    private final List<String> clientRequestedApplicationProtocols;
    private final List<String> clientRequestedProtocols;
    private static final int TLS_RECORD_HEADER_LEN = 5;
    private static final int TLS_EXTENSION_SERVER_NAME = 0;
    private static final int TLS_EXTENSION_ALPN = 16;
    private static final int TLS_EXTENSION_SUPPORTED_VERSION = 43;
    public static byte[] USE_TLS_RESPONSE = "HTTP/1.1 400 \r\nContent-Type: text/plain;charset=UTF-8\r\nConnection: close\r\n\r\nBad Request\r\nThis combination of host and port requires TLS.\r\n".getBytes(StandardCharsets.UTF_8);

    public TLSClientHelloExtractor(ByteBuffer netInBuffer) throws IOException {
        int pos = netInBuffer.position();
        int limit = netInBuffer.limit();
        ExtractorResult result = ExtractorResult.NOT_PRESENT;
        ArrayList<Cipher> clientRequestedCiphers = new ArrayList<Cipher>();
        ArrayList<String> clientRequestedCipherNames = new ArrayList<String>();
        ArrayList<String> clientRequestedApplicationProtocols = new ArrayList<String>();
        ArrayList<String> clientRequestedProtocols = new ArrayList<String>();
        String sniValue = null;
        try {
            netInBuffer.flip();
            if (!TLSClientHelloExtractor.isAvailable(netInBuffer, 5)) {
                result = TLSClientHelloExtractor.handleIncompleteRead(netInBuffer);
                return;
            }
            if (!TLSClientHelloExtractor.isTLSHandshake(netInBuffer)) {
                if (TLSClientHelloExtractor.isHttp(netInBuffer)) {
                    result = ExtractorResult.NON_SECURE;
                }
                return;
            }
            if (!TLSClientHelloExtractor.isAllRecordAvailable(netInBuffer)) {
                result = TLSClientHelloExtractor.handleIncompleteRead(netInBuffer);
                return;
            }
            if (!TLSClientHelloExtractor.isClientHello(netInBuffer)) {
                return;
            }
            if (!TLSClientHelloExtractor.isAllClientHelloAvailable(netInBuffer)) {
                log.warn((Object)sm.getString("sniExtractor.clientHelloTooBig"));
                return;
            }
            String legacyVersion = TLSClientHelloExtractor.readProtocol(netInBuffer);
            TLSClientHelloExtractor.skipBytes(netInBuffer, 32);
            TLSClientHelloExtractor.skipBytes(netInBuffer, netInBuffer.get() & 0xFF);
            int cipherCount = netInBuffer.getChar() / 2;
            for (int i = 0; i < cipherCount; ++i) {
                char cipherId = netInBuffer.getChar();
                Cipher c = Cipher.valueOf(cipherId);
                if (c == null) {
                    clientRequestedCipherNames.add("Unknown(0x" + HexUtils.toHexString((char)cipherId) + ")");
                    continue;
                }
                clientRequestedCiphers.add(c);
                clientRequestedCipherNames.add(c.name());
            }
            TLSClientHelloExtractor.skipBytes(netInBuffer, netInBuffer.get() & 0xFF);
            if (!netInBuffer.hasRemaining()) {
                return;
            }
            TLSClientHelloExtractor.skipBytes(netInBuffer, 2);
            block17: while (netInBuffer.hasRemaining() && (sniValue == null || clientRequestedApplicationProtocols.isEmpty() || clientRequestedProtocols.isEmpty())) {
                char extensionType = netInBuffer.getChar();
                char extensionDataSize = netInBuffer.getChar();
                switch (extensionType) {
                    case '\u0000': {
                        sniValue = TLSClientHelloExtractor.readSniExtension(netInBuffer);
                        continue block17;
                    }
                    case '\u0010': {
                        TLSClientHelloExtractor.readAlpnExtension(netInBuffer, clientRequestedApplicationProtocols);
                        continue block17;
                    }
                    case '+': {
                        TLSClientHelloExtractor.readSupportedVersions(netInBuffer, clientRequestedProtocols);
                        continue block17;
                    }
                }
                TLSClientHelloExtractor.skipBytes(netInBuffer, extensionDataSize);
            }
            if (clientRequestedProtocols.isEmpty()) {
                clientRequestedProtocols.add(legacyVersion);
            }
            result = ExtractorResult.COMPLETE;
        }
        catch (IllegalArgumentException | BufferUnderflowException e) {
            throw new IOException(sm.getString("sniExtractor.clientHelloInvalid"), e);
        }
        finally {
            this.result = result;
            this.clientRequestedCiphers = clientRequestedCiphers;
            this.clientRequestedCipherNames = clientRequestedCipherNames;
            this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
            this.sniValue = sniValue;
            this.clientRequestedProtocols = clientRequestedProtocols;
            netInBuffer.limit(limit);
            netInBuffer.position(pos);
        }
    }

    public ExtractorResult getResult() {
        return this.result;
    }

    public String getSNIValue() {
        if (this.result == ExtractorResult.COMPLETE) {
            return this.sniValue;
        }
        throw new IllegalStateException(sm.getString("sniExtractor.tooEarly"));
    }

    public List<Cipher> getClientRequestedCiphers() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedCiphers;
        }
        throw new IllegalStateException(sm.getString("sniExtractor.tooEarly"));
    }

    public List<String> getClientRequestedCipherNames() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedCipherNames;
        }
        throw new IllegalStateException(sm.getString("sniExtractor.tooEarly"));
    }

    public List<String> getClientRequestedApplicationProtocols() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedApplicationProtocols;
        }
        throw new IllegalStateException(sm.getString("sniExtractor.tooEarly"));
    }

    public List<String> getClientRequestedProtocols() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedProtocols;
        }
        throw new IllegalStateException(sm.getString("sniExtractor.tooEarly"));
    }

    private static ExtractorResult handleIncompleteRead(ByteBuffer bb) {
        if (bb.limit() == bb.capacity()) {
            return ExtractorResult.UNDERFLOW;
        }
        return ExtractorResult.NEED_READ;
    }

    private static boolean isAvailable(ByteBuffer bb, int size) {
        if (bb.remaining() < size) {
            bb.position(bb.limit());
            return false;
        }
        return true;
    }

    private static boolean isTLSHandshake(ByteBuffer bb) {
        if (bb.get() != 22) {
            return false;
        }
        byte b2 = bb.get();
        byte b3 = bb.get();
        return b2 >= 3 && (b2 != 3 || b3 != 0);
    }

    private static boolean isHttp(ByteBuffer bb) {
        byte chr = 0;
        bb.position(0);
        do {
            if (bb.hasRemaining()) continue;
            return false;
        } while ((chr = bb.get()) == 13 || chr == 10);
        do {
            if (HttpParser.isToken(chr) && bb.hasRemaining()) continue;
            return false;
        } while ((chr = bb.get()) != 32 && chr != 9);
        while (chr == 32 || chr == 9) {
            if (!bb.hasRemaining()) {
                return false;
            }
            chr = bb.get();
        }
        while (chr != 32 && chr != 9) {
            if (HttpParser.isNotRequestTarget(chr) || !bb.hasRemaining()) {
                return false;
            }
            chr = bb.get();
        }
        while (chr == 32 || chr == 9) {
            if (!bb.hasRemaining()) {
                return false;
            }
            chr = bb.get();
        }
        do {
            if (HttpParser.isHttpProtocol(chr) && bb.hasRemaining()) continue;
            return false;
        } while ((chr = bb.get()) != 13 && chr != 10);
        return true;
    }

    private static boolean isAllRecordAvailable(ByteBuffer bb) {
        char size = bb.getChar();
        return TLSClientHelloExtractor.isAvailable(bb, size);
    }

    private static boolean isClientHello(ByteBuffer bb) {
        return bb.get() == 1;
    }

    private static boolean isAllClientHelloAvailable(ByteBuffer bb) {
        int size = ((bb.get() & 0xFF) << 16) + ((bb.get() & 0xFF) << 8) + (bb.get() & 0xFF);
        return TLSClientHelloExtractor.isAvailable(bb, size);
    }

    private static void skipBytes(ByteBuffer bb, int size) {
        bb.position(bb.position() + size);
    }

    private static String readProtocol(ByteBuffer bb) {
        char protocol = bb.getChar();
        switch (protocol) {
            case '\u0300': {
                return "SSLv3";
            }
            case '\u0301': {
                return "TLSv1.0";
            }
            case '\u0302': {
                return "TLSv1.1";
            }
            case '\u0303': {
                return "TLSv1.2";
            }
            case '\u0304': {
                return "TLSv1.3";
            }
        }
        return "Unknown(0x" + HexUtils.toHexString((char)protocol) + ")";
    }

    private static String readSniExtension(ByteBuffer bb) {
        TLSClientHelloExtractor.skipBytes(bb, 3);
        char serverNameSize = bb.getChar();
        byte[] serverNameBytes = new byte[serverNameSize];
        bb.get(serverNameBytes);
        return new String(serverNameBytes, StandardCharsets.UTF_8).toLowerCase(Locale.ENGLISH);
    }

    private static void readAlpnExtension(ByteBuffer bb, List<String> protocolNames) {
        char toRead = bb.getChar();
        byte[] inputBuffer = new byte[255];
        while (toRead > '\u0000') {
            int len = bb.get() & 0xFF;
            bb.get(inputBuffer, 0, len);
            protocolNames.add(new String(inputBuffer, 0, len, StandardCharsets.UTF_8));
            toRead = (char)(toRead - '\u0001');
            toRead = (char)(toRead - len);
        }
    }

    private static void readSupportedVersions(ByteBuffer bb, List<String> protocolNames) {
        int count = (bb.get() & 0xFF) / 2;
        for (int i = 0; i < count; ++i) {
            protocolNames.add(TLSClientHelloExtractor.readProtocol(bb));
        }
    }

    public static enum ExtractorResult {
        COMPLETE,
        NOT_PRESENT,
        UNDERFLOW,
        NEED_READ,
        NON_SECURE;

    }
}

