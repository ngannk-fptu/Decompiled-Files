/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.MediaType
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public class SseEmitter
extends ResponseBodyEmitter {
    private static final MediaType TEXT_PLAIN = new MediaType("text", "plain", StandardCharsets.UTF_8);

    public SseEmitter() {
    }

    public SseEmitter(Long timeout) {
        super(timeout);
    }

    @Override
    protected void extendResponse(ServerHttpResponse outputMessage) {
        super.extendResponse(outputMessage);
        HttpHeaders headers = outputMessage.getHeaders();
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        }
    }

    @Override
    public void send(Object object) throws IOException {
        this.send(object, null);
    }

    @Override
    public void send(Object object, @Nullable MediaType mediaType) throws IOException {
        this.send(SseEmitter.event().data(object, mediaType));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void send(SseEventBuilder builder) throws IOException {
        Set<ResponseBodyEmitter.DataWithMediaType> dataToSend = builder.build();
        SseEmitter sseEmitter = this;
        synchronized (sseEmitter) {
            for (ResponseBodyEmitter.DataWithMediaType entry : dataToSend) {
                super.send(entry.getData(), entry.getMediaType());
            }
        }
    }

    @Override
    public String toString() {
        return "SseEmitter@" + ObjectUtils.getIdentityHexString((Object)this);
    }

    public static SseEventBuilder event() {
        return new SseEventBuilderImpl();
    }

    private static class SseEventBuilderImpl
    implements SseEventBuilder {
        private final Set<ResponseBodyEmitter.DataWithMediaType> dataToSend = new LinkedHashSet<ResponseBodyEmitter.DataWithMediaType>(4);
        @Nullable
        private StringBuilder sb;

        private SseEventBuilderImpl() {
        }

        @Override
        public SseEventBuilder id(String id) {
            this.append("id:").append(id).append('\n');
            return this;
        }

        @Override
        public SseEventBuilder name(String name) {
            this.append("event:").append(name).append('\n');
            return this;
        }

        @Override
        public SseEventBuilder reconnectTime(long reconnectTimeMillis) {
            this.append("retry:").append(String.valueOf(reconnectTimeMillis)).append('\n');
            return this;
        }

        @Override
        public SseEventBuilder comment(String comment) {
            this.append(':').append(comment).append('\n');
            return this;
        }

        @Override
        public SseEventBuilder data(Object object) {
            return this.data(object, null);
        }

        @Override
        public SseEventBuilder data(Object object, @Nullable MediaType mediaType) {
            this.append("data:");
            this.saveAppendedText();
            this.dataToSend.add(new ResponseBodyEmitter.DataWithMediaType(object, mediaType));
            this.append('\n');
            return this;
        }

        SseEventBuilderImpl append(String text) {
            if (this.sb == null) {
                this.sb = new StringBuilder();
            }
            this.sb.append(text);
            return this;
        }

        SseEventBuilderImpl append(char ch) {
            if (this.sb == null) {
                this.sb = new StringBuilder();
            }
            this.sb.append(ch);
            return this;
        }

        @Override
        public Set<ResponseBodyEmitter.DataWithMediaType> build() {
            if (!StringUtils.hasLength((CharSequence)this.sb) && this.dataToSend.isEmpty()) {
                return Collections.emptySet();
            }
            this.append('\n');
            this.saveAppendedText();
            return this.dataToSend;
        }

        private void saveAppendedText() {
            if (this.sb != null) {
                this.dataToSend.add(new ResponseBodyEmitter.DataWithMediaType(this.sb.toString(), TEXT_PLAIN));
                this.sb = null;
            }
        }
    }

    public static interface SseEventBuilder {
        public SseEventBuilder id(String var1);

        public SseEventBuilder name(String var1);

        public SseEventBuilder reconnectTime(long var1);

        public SseEventBuilder comment(String var1);

        public SseEventBuilder data(Object var1);

        public SseEventBuilder data(Object var1, @Nullable MediaType var2);

        public Set<ResponseBodyEmitter.DataWithMediaType> build();
    }
}

