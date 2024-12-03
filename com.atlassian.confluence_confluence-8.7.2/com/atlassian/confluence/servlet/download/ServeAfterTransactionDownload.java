/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadException
 *  com.atlassian.plugin.servlet.DownloadStrategy
 *  com.google.common.io.ByteStreams
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.servlet.download;

import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadStrategy;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class ServeAfterTransactionDownload
implements DownloadStrategy {
    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public static String getDecodedPathInfo(HttpServletRequest httpServletRequest) {
        return HtmlUtil.urlDecode(httpServletRequest.getPathInfo());
    }

    @Nullable
    private InputStream getStreamInTransaction(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        TransactionTemplate tt = new TransactionTemplate(this.transactionManager);
        StreamResult result = (StreamResult)tt.execute(this.getStreamResultCallback(httpServletRequest, httpServletResponse));
        return Objects.requireNonNull(result).getStream();
    }

    public final void serveFile(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws DownloadException {
        try (InputStream is = this.getStreamInTransaction(httpServletRequest, httpServletResponse);){
            if (is == null) {
                return;
            }
            this.streamResponse(is, (OutputStream)httpServletResponse.getOutputStream());
        }
        catch (IOException e) {
            throw new DownloadException((Exception)e);
        }
    }

    protected void streamResponse(InputStream fromStream, OutputStream toStream) throws IOException {
        ByteStreams.copy((InputStream)fromStream, (OutputStream)toStream);
        toStream.flush();
    }

    protected TransactionCallback<StreamResult> getStreamResultCallback(HttpServletRequest request, HttpServletResponse response) {
        return new StreamResultCallback(request, response);
    }

    @Nullable
    protected abstract InputStream getStreamForDownload(HttpServletRequest var1, HttpServletResponse var2) throws IOException;

    protected class StreamResultCallback
    implements TransactionCallback<StreamResult> {
        private final HttpServletRequest request;
        private final HttpServletResponse response;

        public StreamResultCallback(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }

        public StreamResult doInTransaction(@NonNull TransactionStatus transactionStatus) {
            InputStream is = null;
            IOException exception = null;
            try {
                is = ServeAfterTransactionDownload.this.getStreamForDownload(this.request, this.response);
            }
            catch (IOException ex) {
                exception = ex;
            }
            return new StreamResult(is, exception);
        }
    }

    private static class StreamResult {
        private final IOException exception;
        private final InputStream stream;

        public StreamResult(InputStream stream, IOException exception) {
            this.exception = exception;
            this.stream = stream;
        }

        @Nullable
        public InputStream getStream() throws IOException {
            if (this.exception != null) {
                throw new IOException("An exception was encountered while getting a stream", this.exception);
            }
            return this.stream;
        }
    }
}

