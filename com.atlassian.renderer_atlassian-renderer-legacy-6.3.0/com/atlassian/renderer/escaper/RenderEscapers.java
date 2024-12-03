/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.renderer.escaper;

import com.atlassian.renderer.escaper.RenderEscaper;
import com.opensymphony.util.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderEscapers {
    public static final RenderEscaper LINK_TEXT_RENDERER_ESCAPER = new LinkTextEscaper();
    public static final RenderEscaper LINK_RENDERER_ESCAPER = new LinkRenderEscaper();
    public static final RenderEscaper NONE_RENDERER_ESCAPER = new NoneRenderEscaper();
    public static final RenderEscaper ATTRIBUTE_RENDERER_ESCAPER = new AttributeEscaper();

    private static class NoneRenderEscaper
    implements RenderEscaper {
        private NoneRenderEscaper() {
        }

        @Override
        public String escape(String data, String characterEncoding) {
            return data;
        }
    }

    private static class LinkTextEscaper
    implements RenderEscaper {
        private LinkTextEscaper() {
        }

        @Override
        public String escape(String data, String characterEncoding) {
            return data;
        }
    }

    private static class AttributeEscaper
    implements RenderEscaper {
        private AttributeEscaper() {
        }

        @Override
        public String escape(String data, String characterEncoding) {
            return TextUtils.htmlEncode((String)data);
        }
    }

    private static class LinkRenderEscaper
    implements RenderEscaper {
        private static final Logger log = LoggerFactory.getLogger(LinkRenderEscaper.class);
        private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

        private LinkRenderEscaper() {
        }

        @Override
        public String escape(String data, String characterEncoding) {
            if (StringUtils.isEmpty((String)characterEncoding)) {
                characterEncoding = DEFAULT_CHARACTER_ENCODING;
            }
            try {
                return URLEncoder.encode(data, characterEncoding);
            }
            catch (UnsupportedEncodingException e) {
                log.error("", (Throwable)e);
                return data;
            }
        }
    }
}

