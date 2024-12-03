/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.gzipfilter.selector;

import com.atlassian.gzipfilter.selector.GzipCompatibilitySelector;
import com.atlassian.gzipfilter.selector.GzipCompatibilitySelectorFactory;
import com.atlassian.gzipfilter.selector.MimeTypeBasedSelector;
import com.atlassian.gzipfilter.selector.NoGzipCompatibilitySelector;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

public class UserAgentBasedGzipSelectorFactory
implements GzipCompatibilitySelectorFactory {
    public static final String COMPRESSABLE_MIME_TYPES_PARAM_NAME = "compressableMimeTypes";
    public static final String NO_COMPRESSION_USER_AGENTS_PARAM_NAME = "noCompressionUserAgents";
    public static final String USER_AGENT_HEADER = "User-Agent";
    private static final GzipCompatibilitySelector NO_GZIP_SELECTOR = new NoGzipCompatibilitySelector();
    public static final Set<String> DEFAULT_COMPRESSABLE_MIME_TYPES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("text/html", "text/xml", "text/plain", "text/javascript", "text/css", "application/javascript", "application/x-javascript", "application/xml", "application/xhtml", "application/xhtml+xml", "application/json")));
    private static final List<String> DEFAULT_NO_COMPRESSION_USER_AGENTS = Collections.unmodifiableList(Arrays.asList("MSIE 6"));
    private final Set<String> compressableMimeTypes;
    private final List<String> noCompressionUserAgents;

    public UserAgentBasedGzipSelectorFactory(FilterConfig filterConfig) {
        String compressableMimeTypesString = filterConfig.getInitParameter(COMPRESSABLE_MIME_TYPES_PARAM_NAME);
        this.compressableMimeTypes = compressableMimeTypesString != null ? Collections.unmodifiableSet(new HashSet<String>(this.split(compressableMimeTypesString))) : DEFAULT_COMPRESSABLE_MIME_TYPES;
        String noCompressionUserAgentsString = filterConfig.getInitParameter(NO_COMPRESSION_USER_AGENTS_PARAM_NAME);
        this.noCompressionUserAgents = noCompressionUserAgentsString != null ? Collections.unmodifiableList(this.split(noCompressionUserAgentsString)) : DEFAULT_NO_COMPRESSION_USER_AGENTS;
    }

    @Override
    public GzipCompatibilitySelector getSelector(FilterConfig filterConfig, HttpServletRequest request) {
        String userAgentHeader = request.getHeader(USER_AGENT_HEADER);
        if (userAgentHeader != null) {
            for (String noCompressionUserAgent : this.noCompressionUserAgents) {
                if (!userAgentHeader.contains(noCompressionUserAgent)) continue;
                return NO_GZIP_SELECTOR;
            }
        }
        return new MimeTypeBasedSelector(this.compressableMimeTypes);
    }

    private List<String> split(String string) {
        return Arrays.asList(string.split(","));
    }
}

