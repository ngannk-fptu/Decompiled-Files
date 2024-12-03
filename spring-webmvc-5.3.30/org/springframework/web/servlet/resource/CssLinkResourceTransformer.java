/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.FileCopyUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.GzipResourceResolver;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.ResourceTransformerSupport;
import org.springframework.web.servlet.resource.TransformedResource;

public class CssLinkResourceTransformer
extends ResourceTransformerSupport {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Log logger = LogFactory.getLog(CssLinkResourceTransformer.class);
    private final List<LinkParser> linkParsers = new ArrayList<LinkParser>(2);

    public CssLinkResourceTransformer() {
        this.linkParsers.add(new ImportStatementLinkParser());
        this.linkParsers.add(new UrlFunctionLinkParser());
    }

    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {
        String filename = (resource = transformerChain.transform(request, resource)).getFilename();
        if (!"css".equals(StringUtils.getFilenameExtension((String)filename)) || resource instanceof EncodedResourceResolver.EncodedResource || resource instanceof GzipResourceResolver.GzippedResource) {
            return resource;
        }
        byte[] bytes = FileCopyUtils.copyToByteArray((InputStream)resource.getInputStream());
        String content = new String(bytes, DEFAULT_CHARSET);
        TreeSet<ContentChunkInfo> links = new TreeSet<ContentChunkInfo>();
        for (LinkParser parser : this.linkParsers) {
            parser.parse(content, links);
        }
        if (links.isEmpty()) {
            return resource;
        }
        int index = 0;
        StringWriter writer = new StringWriter();
        for (ContentChunkInfo linkContentChunkInfo : links) {
            writer.write(content.substring(index, linkContentChunkInfo.getStart()));
            String link = content.substring(linkContentChunkInfo.getStart(), linkContentChunkInfo.getEnd());
            String newLink = null;
            if (!this.hasScheme(link)) {
                String absolutePath = this.toAbsolutePath(link, request);
                newLink = this.resolveUrlPath(absolutePath, request, resource, transformerChain);
            }
            writer.write(newLink != null ? newLink : link);
            index = linkContentChunkInfo.getEnd();
        }
        writer.write(content.substring(index));
        return new TransformedResource(resource, writer.toString().getBytes(DEFAULT_CHARSET));
    }

    private boolean hasScheme(String link) {
        int schemeIndex = link.indexOf(58);
        return schemeIndex > 0 && !link.substring(0, schemeIndex).contains("/") || link.indexOf("//") == 0;
    }

    private static class ContentChunkInfo
    implements Comparable<ContentChunkInfo> {
        private final int start;
        private final int end;

        ContentChunkInfo(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return this.start;
        }

        public int getEnd() {
            return this.end;
        }

        @Override
        public int compareTo(ContentChunkInfo other) {
            return Integer.compare(this.start, other.start);
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ContentChunkInfo)) {
                return false;
            }
            ContentChunkInfo otherCci = (ContentChunkInfo)other;
            return this.start == otherCci.start && this.end == otherCci.end;
        }

        public int hashCode() {
            return this.start * 31 + this.end;
        }
    }

    private static class UrlFunctionLinkParser
    extends AbstractLinkParser {
        private UrlFunctionLinkParser() {
        }

        @Override
        protected String getKeyword() {
            return "url(";
        }

        @Override
        protected int extractLink(int index, String content, SortedSet<ContentChunkInfo> linksToAdd) {
            return this.extractLink(index - 1, ")", content, linksToAdd);
        }
    }

    private static class ImportStatementLinkParser
    extends AbstractLinkParser {
        private ImportStatementLinkParser() {
        }

        @Override
        protected String getKeyword() {
            return "@import";
        }

        @Override
        protected int extractLink(int index, String content, SortedSet<ContentChunkInfo> linksToAdd) {
            if (!content.startsWith("url(", index) && logger.isTraceEnabled()) {
                logger.trace((Object)("Unexpected syntax for @import link at index " + index));
            }
            return index;
        }
    }

    protected static abstract class AbstractLinkParser
    implements LinkParser {
        protected AbstractLinkParser() {
        }

        protected abstract String getKeyword();

        @Override
        public void parse(String content, SortedSet<ContentChunkInfo> result) {
            int position = 0;
            while ((position = content.indexOf(this.getKeyword(), position)) != -1) {
                position += this.getKeyword().length();
                while (Character.isWhitespace(content.charAt(position))) {
                    ++position;
                }
                if (content.charAt(position) == '\'') {
                    position = this.extractLink(position, "'", content, result);
                    continue;
                }
                if (content.charAt(position) == '\"') {
                    position = this.extractLink(position, "\"", content, result);
                    continue;
                }
                position = this.extractLink(position, content, result);
            }
            return;
        }

        protected int extractLink(int index, String endKey, String content, SortedSet<ContentChunkInfo> linksToAdd) {
            int start = index + 1;
            int end = content.indexOf(endKey, start);
            linksToAdd.add(new ContentChunkInfo(start, end));
            return end + endKey.length();
        }

        protected abstract int extractLink(int var1, String var2, SortedSet<ContentChunkInfo> var3);
    }

    @FunctionalInterface
    protected static interface LinkParser {
        public void parse(String var1, SortedSet<ContentChunkInfo> var2);
    }
}

