/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.RenderedContentStore
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.components.AbstractRegexRendererComponent
 *  com.atlassian.renderer.v2.components.RendererComponent
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.LinkResolver;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.InvalidMigrationException;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.ConfluenceRendererConfiguration;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.RenderedContentStore;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRegexRendererComponent;
import com.atlassian.renderer.v2.components.RendererComponent;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class XhtmlCamelCaseLinkMigrationRendererComponent
extends AbstractRegexRendererComponent
implements RendererComponent {
    public static final Pattern LINK_CAMELCASE_PATTERN = Pattern.compile("(^|[^\\p{Alpha}!\\^])([\\p{Lu}][\\p{Alnum}]*[\\p{L}&&[^\\p{Lu}]][\\p{Alnum}]*[\\p{Lu}][\\p{Alnum}]+)", 32);
    private ConfluenceRendererConfiguration rendererConfiguration;
    private Marshaller<Link> linkMarshaller;
    private LinkResolver linkResolver;

    public XhtmlCamelCaseLinkMigrationRendererComponent(ConfluenceRendererConfiguration rendererConfiguration, Marshaller<Link> linkMarshaller, LinkResolver linkResolver) {
        this.rendererConfiguration = rendererConfiguration;
        this.linkMarshaller = linkMarshaller;
        this.linkResolver = linkResolver;
    }

    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderLinks() && this.rendererConfiguration.isAllowCamelCase();
    }

    public String render(String wiki, RenderContext renderContext) {
        if (StringUtils.isEmpty((CharSequence)wiki)) {
            return wiki;
        }
        return this.regexRender(wiki, renderContext, LINK_CAMELCASE_PATTERN);
    }

    public void appendSubstitution(StringBuffer stringBuffer, RenderContext context, Matcher matcher) {
        String linkText = matcher.group(2);
        if (linkText == null) {
            throw new InvalidMigrationException("CamelCase link text is null.");
        }
        PageContext pageContext = this.toPageContext(context);
        Link link = this.linkResolver.resolve(linkText, pageContext);
        if (link == null) {
            throw new InvalidMigrationException(MessageFormat.format("The link text \"{0}\" could not be resolved as a link", linkText));
        }
        String result = this.toXhtml(link, pageContext);
        String preLinkText = matcher.group(1);
        RenderedContentStore renderedContentStore = context.getRenderedContentStore();
        stringBuffer.append(preLinkText).append(renderedContentStore.addInline((Object)result));
    }

    private String toXhtml(Link link, PageContext context) {
        DefaultConversionContext conversionContext = new DefaultConversionContext(context);
        try {
            return Streamables.writeToString(this.linkMarshaller.marshal(link, conversionContext));
        }
        catch (XhtmlException e) {
            throw new RuntimeException(e);
        }
    }

    private PageContext toPageContext(RenderContext renderContext) {
        if (!(renderContext instanceof PageContext)) {
            throw new IllegalArgumentException("context must be an instance of PageContext.");
        }
        return (PageContext)renderContext;
    }
}

