/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.plugin.web.renderer.RendererException
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.content.render.xhtml.view.excerpt;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.ExcerptConfig;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.plugin.web.renderer.RendererException;
import java.net.URI;
import java.util.List;
import javax.activation.DataSource;
import javax.xml.stream.XMLStreamException;

public interface Excerpter {
    public String createExcerpt(Content var1) throws Exception;

    public String createExcerpt(ContentEntityObject var1, String var2) throws Exception;

    public String createExcerpt(ContentEntityObject var1, String var2, ExcerptConfig var3) throws XMLStreamException, RendererException;

    public List<String> extractImageSrc(String var1, int var2) throws XMLStreamException;

    public List<URI> extractImageThumbnailUris(ContentEntityObject var1, int var2) throws XhtmlException;

    public List<DataSource> extractImageSrc(ContentEntityObject var1, int var2) throws XMLStreamException, XhtmlException;

    public List<DataSource> extractImageSrc(ContentEntityObject var1, int var2, boolean var3) throws XMLStreamException, XhtmlException;

    public String getExcerpt(ContentEntityObject var1);

    public String getExcerptSummary(ContentEntityObject var1);

    public String getText(String var1);
}

