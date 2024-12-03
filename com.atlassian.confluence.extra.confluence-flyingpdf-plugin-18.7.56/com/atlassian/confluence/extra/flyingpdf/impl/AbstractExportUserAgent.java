/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.DecoderException
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.binary.StringUtils
 *  org.apache.commons.codec.net.URLCodec
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.impl.LightITextFSImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.net.URLCodec;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.ImageResource;

public abstract class AbstractExportUserAgent
extends ITextUserAgent {
    private static final String FILE_PREFIX = "file:";
    private static final String DATA_PREFIX = "data:";
    private static final Pattern RESOURCE_PATH_PATTERN = Pattern.compile("/s/(.*)/_/");
    private final String baseUrl;
    private final String cdnUrl;
    private final boolean bypassCdn = !Boolean.getBoolean("pdf.export.allow.cdn.access");

    public AbstractExportUserAgent(ITextOutputDevice device, String baseUrl, @Nullable String cdnUrl) {
        super(device);
        this.baseUrl = baseUrl;
        this.cdnUrl = cdnUrl;
    }

    @Override
    public ImageResource getImageResource(String uri) {
        try {
            return new ImageResource(uri, new LightITextFSImage(() -> this.resolveAndOpenStream(uri), this.getSharedContext().getDotsPerPixel(), this.baseUrl, uri));
        }
        catch (Exception e) {
            this.log(Level.SEVERE, "Can't get image resource for uri" + uri + ", error: " + e.getMessage());
            return new ImageResource(null, null);
        }
    }

    @Override
    protected InputStream resolveAndOpenStream(String uri) {
        String relativeUri;
        if (uri == null) {
            return null;
        }
        if (this.shrinkImageCacheBeforeFetching()) {
            this.shrinkImageCache();
        }
        String effectiveUri = uri;
        if (this.bypassCdn && org.apache.commons.lang3.StringUtils.isNotBlank((CharSequence)this.cdnUrl) && effectiveUri.startsWith(this.cdnUrl)) {
            effectiveUri = effectiveUri.replace(this.cdnUrl, this.baseUrl);
        }
        if ((relativeUri = effectiveUri).startsWith(FILE_PREFIX)) {
            relativeUri = relativeUri.substring(FILE_PREFIX.length());
        } else if (relativeUri.startsWith(this.baseUrl)) {
            relativeUri = relativeUri.substring(this.baseUrl.length());
        }
        Matcher matcher = RESOURCE_PATH_PATTERN.matcher(relativeUri);
        String decodedUri = relativeUri = matcher.replaceFirst("/");
        try {
            decodedUri = URLDecoder.decode(relativeUri, "UTF8");
        }
        catch (UnsupportedEncodingException e) {
            this.log(Level.SEVERE, "Can't decode uri" + effectiveUri + ", error: " + e.getMessage());
        }
        InputStream resource = this.fetchResourceFromConfluence(relativeUri, decodedUri);
        if (resource != null) {
            return resource;
        }
        if (effectiveUri.startsWith(DATA_PREFIX)) {
            return this.streamDataUrl(effectiveUri);
        }
        return super.resolveAndOpenStream(effectiveUri);
    }

    protected boolean shrinkImageCacheBeforeFetching() {
        return false;
    }

    protected abstract InputStream fetchResourceFromConfluence(String var1, String var2);

    private InputStream streamDataUrl(String dataUrl) {
        byte[] bytes;
        int dataIndex = dataUrl.indexOf(44);
        String data = dataUrl.substring(dataIndex + 1);
        if (dataUrl.substring(0, dataIndex).endsWith(";base64")) {
            bytes = Base64.decodeBase64((String)data);
        } else {
            try {
                bytes = URLCodec.decodeUrl((byte[])StringUtils.getBytesUsAscii((String)data));
            }
            catch (DecoderException e) {
                throw new IllegalArgumentException("Invalid data URL: \"" + dataUrl + "\".", e);
            }
        }
        return new ByteArrayInputStream(bytes);
    }

    protected void log(Level level, String message) {
    }
}

