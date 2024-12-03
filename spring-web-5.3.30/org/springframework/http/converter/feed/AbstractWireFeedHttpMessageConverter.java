/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.WireFeed
 *  com.rometools.rome.io.FeedException
 *  com.rometools.rome.io.WireFeedInput
 *  com.rometools.rome.io.WireFeedOutput
 *  org.springframework.util.StreamUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.http.converter.feed;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedInput;
import com.rometools.rome.io.WireFeedOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractWireFeedHttpMessageConverter<T extends WireFeed>
extends AbstractHttpMessageConverter<T> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected AbstractWireFeedHttpMessageConverter(MediaType supportedMediaType) {
        super(supportedMediaType);
    }

    @Override
    protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        WireFeedInput feedInput = new WireFeedInput();
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = contentType != null && contentType.getCharset() != null ? contentType.getCharset() : DEFAULT_CHARSET;
        try {
            InputStream inputStream = StreamUtils.nonClosing((InputStream)inputMessage.getBody());
            InputStreamReader reader = new InputStreamReader(inputStream, charset);
            return (T)feedInput.build((Reader)reader);
        }
        catch (FeedException ex) {
            throw new HttpMessageNotReadableException("Could not read WireFeed: " + ex.getMessage(), ex, inputMessage);
        }
    }

    @Override
    protected void writeInternal(T wireFeed, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Charset charset = StringUtils.hasLength((String)wireFeed.getEncoding()) ? Charset.forName(wireFeed.getEncoding()) : DEFAULT_CHARSET;
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType != null) {
            contentType = new MediaType(contentType, charset);
            outputMessage.getHeaders().setContentType(contentType);
        }
        WireFeedOutput feedOutput = new WireFeedOutput();
        try {
            OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), charset);
            feedOutput.output(wireFeed, (Writer)writer);
        }
        catch (FeedException ex) {
            throw new HttpMessageNotWritableException("Could not write WireFeed: " + ex.getMessage(), ex);
        }
    }
}

