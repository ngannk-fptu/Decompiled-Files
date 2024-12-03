/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.ClassicHttpRequest
 *  org.apache.hc.core5.http.ClassicHttpResponse
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HeaderElement
 *  org.apache.hc.core5.http.HttpEntity
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.config.Lookup
 *  org.apache.hc.core5.http.config.RegistryBuilder
 *  org.apache.hc.core5.http.message.BasicHeaderValueParser
 *  org.apache.hc.core5.http.message.MessageSupport
 *  org.apache.hc.core5.http.message.ParserCursor
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.impl.classic;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.BrotliDecompressingEntity;
import org.apache.hc.client5.http.entity.BrotliInputStreamFactory;
import org.apache.hc.client5.http.entity.DecompressingEntity;
import org.apache.hc.client5.http.entity.DeflateInputStreamFactory;
import org.apache.hc.client5.http.entity.GZIPInputStreamFactory;
import org.apache.hc.client5.http.entity.InputStreamFactory;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.message.BasicHeaderValueParser;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.http.message.ParserCursor;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.STATELESS)
@Internal
public final class ContentCompressionExec
implements ExecChainHandler {
    private final Header acceptEncoding;
    private final Lookup<InputStreamFactory> decoderRegistry;
    private final boolean ignoreUnknown;
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public ContentCompressionExec(List<String> acceptEncoding, Lookup<InputStreamFactory> decoderRegistry, boolean ignoreUnknown) {
        boolean brotliSupported = BrotliDecompressingEntity.isAvailable();
        String[] encoding = brotliSupported ? new String[]{"gzip", "x-gzip", "deflate", "br"} : new String[]{"gzip", "x-gzip", "deflate"};
        this.acceptEncoding = MessageSupport.format((String)"Accept-Encoding", (String[])(acceptEncoding != null ? acceptEncoding.toArray(EMPTY_STRING_ARRAY) : encoding));
        if (decoderRegistry != null) {
            this.decoderRegistry = decoderRegistry;
        } else {
            RegistryBuilder builder = RegistryBuilder.create().register("gzip", (Object)GZIPInputStreamFactory.getInstance()).register("x-gzip", (Object)GZIPInputStreamFactory.getInstance()).register("deflate", (Object)DeflateInputStreamFactory.getInstance());
            if (brotliSupported) {
                builder.register("br", (Object)BrotliInputStreamFactory.getInstance());
            }
            this.decoderRegistry = builder.build();
        }
        this.ignoreUnknown = ignoreUnknown;
    }

    public ContentCompressionExec(boolean ignoreUnknown) {
        this(null, null, ignoreUnknown);
    }

    public ContentCompressionExec() {
        this(null, null, true);
    }

    @Override
    public ClassicHttpResponse execute(ClassicHttpRequest request, ExecChain.Scope scope, ExecChain chain) throws IOException, HttpException {
        String contentEncoding;
        Args.notNull((Object)request, (String)"HTTP request");
        Args.notNull((Object)scope, (String)"Scope");
        HttpClientContext clientContext = scope.clientContext;
        RequestConfig requestConfig = clientContext.getRequestConfig();
        if (!request.containsHeader("Accept-Encoding") && requestConfig.isContentCompressionEnabled()) {
            request.addHeader(this.acceptEncoding);
        }
        ClassicHttpResponse response = chain.proceed(request, scope);
        HttpEntity entity = response.getEntity();
        if (requestConfig.isContentCompressionEnabled() && entity != null && entity.getContentLength() != 0L && (contentEncoding = entity.getContentEncoding()) != null) {
            HeaderElement[] codecs;
            ParserCursor cursor = new ParserCursor(0, contentEncoding.length());
            for (HeaderElement codec : codecs = BasicHeaderValueParser.INSTANCE.parseElements((CharSequence)contentEncoding, cursor)) {
                String codecname = codec.getName().toLowerCase(Locale.ROOT);
                InputStreamFactory decoderFactory = (InputStreamFactory)this.decoderRegistry.lookup(codecname);
                if (decoderFactory != null) {
                    response.setEntity((HttpEntity)new DecompressingEntity(response.getEntity(), decoderFactory));
                    response.removeHeaders("Content-Length");
                    response.removeHeaders("Content-Encoding");
                    response.removeHeaders("Content-MD5");
                    continue;
                }
                if ("identity".equals(codecname) || this.ignoreUnknown) continue;
                throw new HttpException("Unsupported Content-Encoding: " + codec.getName());
            }
        }
        return response;
    }
}

