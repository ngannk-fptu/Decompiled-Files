/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package com.atlassian.plugin.servlet.cache.model;

import com.atlassian.plugin.servlet.cache.model.CacheableRequest;
import com.atlassian.plugin.servlet.cache.model.CacheableResponseStream;
import com.atlassian.plugin.servlet.cache.model.ETagToken;
import com.atlassian.plugin.servlet.util.function.FailableConsumer;
import com.atlassian.plugin.servlet.util.function.FailableSupplier;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CacheableResponse
extends HttpServletResponseWrapper
implements Closeable {
    private CacheableResponseStream outputStream;
    private PrintWriter writer;

    public CacheableResponse(@Nonnull HttpServletResponse response) {
        super(response);
    }

    @Override
    public final void close() throws IOException {
        if (Objects.nonNull(this.writer)) {
            this.writer.close();
        }
        if (Objects.nonNull((Object)this.outputStream)) {
            this.outputStream.close();
        }
    }

    public final void flushBuffer() {
        try {
            if (Objects.nonNull(this.writer)) {
                this.writer.flush();
            }
            if (Objects.nonNull((Object)this.outputStream)) {
                this.outputStream.flush();
            }
        }
        catch (IOException exception) {
            throw new IllegalStateException("Error while flushing data.");
        }
    }

    public final void flushResponse() {
        this.getContentBody().ifPresent(FailableConsumer.wrapper(contentBody -> {
            HttpServletResponse response = (HttpServletResponse)this.getResponse();
            response.setContentLength(((byte[])contentBody).length);
            response.getOutputStream().write(contentBody);
            response.getOutputStream().flush();
        }));
    }

    public final Optional<byte[]> getContentBody() {
        this.flushBuffer();
        return Optional.ofNullable(this.outputStream).map(CacheableResponseStream::getCopy);
    }

    @Nonnull
    public final ServletOutputStream getOutputStream() {
        this.outputStream = Optional.ofNullable(this.outputStream).orElseGet(CacheableResponseStream::new);
        return this.outputStream;
    }

    @Nonnull
    public final PrintWriter getWriter() {
        this.writer = Optional.ofNullable(this.writer).orElseGet(FailableSupplier.wrapper(() -> {
            String characterEncoding = this.getResponse().getCharacterEncoding();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)this.getOutputStream(), characterEncoding);
            return new PrintWriter(outputStreamWriter, true);
        }));
        return this.writer;
    }

    public final boolean isCacheable(@Nonnull CacheableRequest request) {
        Objects.requireNonNull(request, "The request is mandatory to verify if the current response is cacheable.");
        return request.isCacheable(this);
    }

    public final void setETagHeader() {
        this.toETagToken().map(ETagToken::getDoubleQuotedValue).ifPresent(token -> {
            HttpServletResponse response = (HttpServletResponse)this.getResponse();
            response.setHeader("ETag", token);
        });
    }

    @Nonnull
    public Optional<ETagToken> toETagToken() {
        return this.getContentBody().map(ETagToken::new);
    }
}

