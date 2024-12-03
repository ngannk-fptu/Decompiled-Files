/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.mime.BoundaryLimitedInputStream;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.LineReader;
import org.bouncycastle.mime.MimeContext;
import org.bouncycastle.mime.MimeMultipartContext;
import org.bouncycastle.mime.MimeParser;
import org.bouncycastle.mime.MimeParserContext;
import org.bouncycastle.mime.MimeParserListener;
import org.bouncycastle.mime.encoding.Base64InputStream;
import org.bouncycastle.mime.encoding.QuotedPrintableInputStream;

public class BasicMimeParser
implements MimeParser {
    private final InputStream src;
    private final MimeParserContext parserContext;
    private final String defaultContentTransferEncoding;
    private Headers headers;
    private boolean isMultipart = false;
    private final String boundary;

    public BasicMimeParser(InputStream src) throws IOException {
        this(null, new Headers(src, "7bit"), src);
    }

    public BasicMimeParser(MimeParserContext parserContext, InputStream src) throws IOException {
        this(parserContext, new Headers(src, parserContext.getDefaultContentTransferEncoding()), src);
    }

    public BasicMimeParser(Headers headers, InputStream content) {
        this(null, headers, content);
    }

    public BasicMimeParser(MimeParserContext parserContext, Headers headers, InputStream src) {
        if (headers.isMultipart()) {
            this.isMultipart = true;
            this.boundary = headers.getBoundary();
        } else {
            this.boundary = null;
        }
        this.headers = headers;
        this.parserContext = parserContext;
        this.src = src;
        this.defaultContentTransferEncoding = parserContext != null ? parserContext.getDefaultContentTransferEncoding() : "7bit";
    }

    @Override
    public void parse(MimeParserListener listener) throws IOException {
        MimeContext context = listener.createContext(this.parserContext, this.headers);
        if (this.isMultipart) {
            String s;
            MimeMultipartContext mContext = (MimeMultipartContext)context;
            String startBoundary = "--" + this.boundary;
            boolean startFound = false;
            int partNo = 0;
            LineReader rd = new LineReader(this.src);
            while ((s = rd.readLine()) != null && !"--".equals(s)) {
                MimeContext partContext;
                Headers headers;
                InputStream inputStream;
                if (startFound) {
                    inputStream = new BoundaryLimitedInputStream(this.src, this.boundary);
                    headers = new Headers(inputStream, this.defaultContentTransferEncoding);
                    partContext = mContext.createContext(partNo++);
                    inputStream = partContext.applyContext(headers, inputStream);
                    listener.object(this.parserContext, headers, this.processStream(headers, inputStream));
                    if (inputStream.read() < 0) continue;
                    throw new IOException("MIME object not fully processed");
                }
                if (!startBoundary.equals(s)) continue;
                startFound = true;
                inputStream = new BoundaryLimitedInputStream(this.src, this.boundary);
                headers = new Headers(inputStream, this.defaultContentTransferEncoding);
                partContext = mContext.createContext(partNo++);
                inputStream = partContext.applyContext(headers, inputStream);
                listener.object(this.parserContext, headers, this.processStream(headers, inputStream));
                if (inputStream.read() < 0) continue;
                throw new IOException("MIME object not fully processed");
            }
        } else {
            InputStream inputStream = context.applyContext(this.headers, this.src);
            listener.object(this.parserContext, this.headers, this.processStream(this.headers, inputStream));
        }
    }

    public boolean isMultipart() {
        return this.isMultipart;
    }

    private InputStream processStream(Headers headers, InputStream inputStream) {
        if (headers.getContentTransferEncoding().equals("base64")) {
            return new Base64InputStream(inputStream);
        }
        if (headers.getContentTransferEncoding().equals("quoted-printable")) {
            return new QuotedPrintableInputStream(inputStream);
        }
        return inputStream;
    }
}

