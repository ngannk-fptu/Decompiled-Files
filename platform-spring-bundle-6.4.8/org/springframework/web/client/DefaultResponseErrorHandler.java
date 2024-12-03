/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.UnknownHttpStatusCodeException;

public class DefaultResponseErrorHandler
implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        int rawStatusCode = response.getRawStatusCode();
        HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
        return statusCode != null ? this.hasError(statusCode) : this.hasError(rawStatusCode);
    }

    protected boolean hasError(HttpStatus statusCode) {
        return statusCode.isError();
    }

    protected boolean hasError(int unknownStatusCode) {
        HttpStatus.Series series = HttpStatus.Series.resolve(unknownStatusCode);
        return series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        if (statusCode == null) {
            byte[] body2 = this.getResponseBody(response);
            String message = this.getErrorMessage(response.getRawStatusCode(), response.getStatusText(), body2, this.getCharset(response));
            throw new UnknownHttpStatusCodeException(message, response.getRawStatusCode(), response.getStatusText(), response.getHeaders(), body2, this.getCharset(response));
        }
        this.handleError(response, statusCode);
    }

    private String getErrorMessage(int rawStatusCode, String statusText, @Nullable byte[] responseBody, @Nullable Charset charset) {
        String preface = rawStatusCode + " " + statusText + ": ";
        if (ObjectUtils.isEmpty((Object)responseBody)) {
            return preface + "[no body]";
        }
        charset = charset != null ? charset : StandardCharsets.UTF_8;
        String bodyText = new String(responseBody, charset);
        bodyText = LogFormatUtils.formatValue(bodyText, -1, true);
        return preface + bodyText;
    }

    protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
        String statusText = response.getStatusText();
        HttpHeaders headers = response.getHeaders();
        byte[] body2 = this.getResponseBody(response);
        Charset charset = this.getCharset(response);
        String message = this.getErrorMessage(statusCode.value(), statusText, body2, charset);
        switch (statusCode.series()) {
            case CLIENT_ERROR: {
                throw HttpClientErrorException.create(message, statusCode, statusText, headers, body2, charset);
            }
            case SERVER_ERROR: {
                throw HttpServerErrorException.create(message, statusCode, statusText, headers, body2, charset);
            }
        }
        throw new UnknownHttpStatusCodeException(message, statusCode.value(), statusText, headers, body2, charset);
    }

    @Deprecated
    protected HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        if (statusCode == null) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(), response.getHeaders(), this.getResponseBody(response), this.getCharset(response));
        }
        return statusCode;
    }

    protected byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        }
        catch (IOException iOException) {
            return new byte[0];
        }
    }

    @Nullable
    protected Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return contentType != null ? contentType.getCharset() : null;
    }
}

