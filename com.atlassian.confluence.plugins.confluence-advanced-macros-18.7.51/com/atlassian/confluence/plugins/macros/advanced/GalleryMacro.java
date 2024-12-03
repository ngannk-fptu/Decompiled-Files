/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.links.linktypes.PageLink
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.thumbnail.ThumbnailInfo
 *  com.atlassian.confluence.pages.thumbnail.ThumbnailManager
 *  com.atlassian.confluence.pages.thumbnail.Thumbnails
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.attachments.RendererAttachmentManager
 *  com.atlassian.confluence.renderer.embedded.ImagePathHelper
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.util.AttachmentComparator
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.links.LinkResolver
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.text.StrMatcher
 *  org.apache.commons.lang3.text.StrTokenizer
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.links.linktypes.PageLink;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.pages.thumbnail.Thumbnails;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.attachments.RendererAttachmentManager;
import com.atlassian.confluence.renderer.embedded.ImagePathHelper;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.AttachmentComparator;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrMatcher;
import org.apache.commons.lang3.text.StrTokenizer;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GalleryMacro
extends BaseMacro
implements Macro {
    private static final Logger log = LoggerFactory.getLogger(GalleryMacro.class);
    private static final int DEFAULT_COLUMNS = 4;
    private AttachmentManager attachmentManager;
    private ThumbnailManager thumbnailManager;
    private RendererAttachmentManager rendererAttachmentManager;
    private LinkResolver linkResolver;
    private ConfluenceActionSupport confluenceActionSupport;
    private static final String MACRO_NAME = "gallery";

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        try {
            return this.execute(parameters, body, (RenderContext)(conversionContext != null ? conversionContext.getPageContext() : null));
        }
        catch (MacroException e) {
            throw new MacroExecutionException((Throwable)e);
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setRendererAttachmentManager(RendererAttachmentManager rendererAttachmentManager) {
        this.rendererAttachmentManager = rendererAttachmentManager;
    }

    public void setThumbnailManager(ThumbnailManager thumbnailManager) {
        this.thumbnailManager = thumbnailManager;
    }

    public void setLinkResolver(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }

    public String getName() {
        return MACRO_NAME;
    }

    public String execute(Map params, String body, RenderContext renderContext) throws MacroException {
        Map typeSafeMacroParams = params;
        if (!this.isThumbnailSupported()) {
            return "<p><span class=\"error\">" + this.getConfluenceActionSupport().getText("gallery.error.thumbnails-not-supported") + "</span></p>";
        }
        Integer galleryId = this.nextGalleryId(renderContext);
        String title = (String)typeSafeMacroParams.get("title");
        Thumbnails thumbnails = this.findThumbnails(typeSafeMacroParams, renderContext);
        boolean slideshow = renderContext.getOutputType().equals("display");
        String template = this.getTemplate(null);
        VelocityContext contextMap = this.createVelocityContext(galleryId, title, thumbnails, slideshow);
        try {
            return this.getRenderedTemplateWithoutSwallowingErrors(template, contextMap);
        }
        catch (ResourceNotFoundException e) {
            return "<p><span class='error'>" + this.getConfluenceActionSupport().getText("gallery.error.unable-to-find-render-template", new String[]{StringEscapeUtils.escapeHtml4((String)((String)typeSafeMacroParams.get("theme")))}) + "</span></p>";
        }
        catch (Exception exception) {
            log.error("Error while trying to draw the image gallery", (Throwable)exception);
            return "<p><span class='error'>" + this.getConfluenceActionSupport().getText("gallery.error.unable-to-render", new String[]{StringEscapeUtils.escapeHtml4((String)exception.toString())}) + "</span></p>";
        }
    }

    protected boolean isThumbnailSupported() {
        return ThumbnailInfo.systemSupportsThumbnailing();
    }

    protected String getRenderedTemplateWithoutSwallowingErrors(String template, VelocityContext contextMap) throws Exception {
        return VelocityUtils.getRenderedTemplateWithoutSwallowingErrors((String)template, (Context)contextMap);
    }

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    private VelocityContext createVelocityContext(Integer galleryId, Object title, Thumbnails thumbnails, boolean slideshow) {
        VelocityContext contextMap = this.newVelocityContext();
        contextMap.put("galleryId", (Object)galleryId);
        contextMap.put("galleryTitle", title);
        contextMap.put("thumbnails", (Object)thumbnails);
        contextMap.put("slideshow", (Object)slideshow);
        return contextMap;
    }

    private Thumbnails findThumbnails(Map<String, String> params, RenderContext renderContext) {
        int columns = this.getColumns(params.get("columns"));
        String sort = params.get("sort");
        boolean reverseSort = BooleanUtils.toBoolean((String)params.get("reverse"));
        FilterParams filterParams = new FilterParams(this.splitList(params.get("include")), this.splitList(params.get("exclude")), this.splitList(params.get("includeLabel")), this.splitList(params.get("excludeLabel")));
        String[] pages = this.splitList(params.get("page"));
        List<Attachment> attachments = new ArrayList<Attachment>();
        if (renderContext instanceof PageContext) {
            PageContext context = (PageContext)renderContext;
            if (pages.length > 0) {
                for (String page : pages) {
                    Link link = this.linkResolver.createLink((RenderContext)context, page);
                    if (link == null || !(link instanceof PageLink)) continue;
                    ContentEntityObject content = ((PageLink)link).getDestinationContent();
                    this.addContentAttachmentsToList(content, attachments);
                }
            } else {
                ContentEntityObject content = context.getEntity();
                this.addContentAttachmentsToList(content, attachments);
            }
        }
        attachments = this.filterAttachments(filterParams, attachments);
        this.sortAttachments(sort, reverseSort, attachments);
        return this.createThumbnails(columns, attachments);
    }

    protected Thumbnails createThumbnails(int columns, List<Attachment> attachments) {
        return new Thumbnails(attachments, (ImagePathHelper)this.rendererAttachmentManager, columns, this.attachmentManager, this.thumbnailManager);
    }

    private String getTemplate(String theme) {
        if (StringUtils.isBlank((CharSequence)theme)) {
            theme = "default";
        }
        return "/com/atlassian/confluence/plugins/macros/advanced/gallery-" + theme + ".vm";
    }

    private int getColumns(String columns) {
        int colCount = 0;
        if (columns != null) {
            try {
                colCount = Integer.parseInt(columns);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return colCount == 0 ? 4 : colCount;
    }

    private String[] splitList(String commaDelimitedList) {
        return new StrTokenizer(commaDelimitedList, StrMatcher.commaMatcher(), StrMatcher.quoteMatcher()).setTrimmerMatcher(StrMatcher.trimMatcher()).getTokenArray();
    }

    private void addContentAttachmentsToList(ContentEntityObject content, List<Attachment> attachments) {
        if (content != null) {
            attachments.addAll(this.attachmentManager.getLatestVersionsOfAttachments(content));
        }
    }

    private List<Attachment> filterAttachments(FilterParams filterParams, List<Attachment> attachments) {
        Predicate filter = Predicates.alwaysTrue();
        Function attachmentToNameFn = input -> input.getFileName();
        if (!filterParams.include.isEmpty()) {
            filter = Predicates.and((Predicate)filter, (Predicate)Predicates.compose((Predicate)Predicates.in(filterParams.include), (Function)attachmentToNameFn));
        }
        if (!filterParams.exclude.isEmpty()) {
            filter = Predicates.and((Predicate)filter, (Predicate)Predicates.not((Predicate)Predicates.compose((Predicate)Predicates.in(filterParams.exclude), (Function)attachmentToNameFn)));
        }
        if (!filterParams.excludeLabels.isEmpty()) {
            filter = Predicates.and((Predicate)filter, (Predicate)Predicates.not(input -> {
                for (Label label : input.getLabels()) {
                    if (!filterParams.excludeLabels.contains(label.getName())) continue;
                    return true;
                }
                return false;
            }));
        }
        if (!filterParams.includeLabels.isEmpty()) {
            filter = Predicates.and((Predicate)filter, input -> {
                for (Label label : input.getLabels()) {
                    if (!filterParams.includeLabels.contains(label.getName())) continue;
                    return true;
                }
                return false;
            });
        }
        return Lists.newArrayList((Iterable)Iterables.filter(attachments, (Predicate)filter));
    }

    private void sortAttachments(String sort, boolean reverseSort, List attachments) {
        if (StringUtils.isBlank((CharSequence)sort)) {
            return;
        }
        GalleryAttachmentComparator comparator = new GalleryAttachmentComparator(sort, reverseSort);
        attachments.sort(comparator);
    }

    private Integer nextGalleryId(RenderContext renderContext) {
        Integer galleryId = (Integer)renderContext.getParam((Object)"nextGalleryId");
        if (galleryId == null) {
            galleryId = 0;
        }
        renderContext.addParam((Object)"nextGalleryId", (Object)(galleryId + 1));
        return galleryId;
    }

    public ConfluenceActionSupport getConfluenceActionSupport() {
        if (null == this.confluenceActionSupport) {
            this.confluenceActionSupport = new ConfluenceActionSupport();
            ContainerManager.autowireComponent((Object)this.confluenceActionSupport);
        }
        return this.confluenceActionSupport;
    }

    protected VelocityContext newVelocityContext() {
        return new VelocityContext(MacroUtils.defaultVelocityContext());
    }

    private class GalleryAttachmentComparator
    extends AttachmentComparator {
        private String sortBy;
        private boolean reverse;

        public GalleryAttachmentComparator(String sortBy, boolean reverse) {
            super(sortBy, reverse);
            this.sortBy = sortBy;
            this.reverse = reverse;
        }

        public int compare(Object o1, Object o2) {
            if (StringUtils.equals((CharSequence)this.sortBy, (CharSequence)"comment")) {
                Attachment anAttachment = (Attachment)o1;
                Attachment anotherAttachment = (Attachment)o2;
                int compareResult = Collator.getInstance().compare(StringUtils.defaultString((String)anAttachment.getVersionComment(), (String)""), StringUtils.defaultString((String)anotherAttachment.getVersionComment(), (String)""));
                return this.reverse ? -compareResult : compareResult;
            }
            return super.compare(o1, o2);
        }
    }

    private static class FilterParams {
        final Set<String> include;
        final Set<String> exclude;
        final Set<String> includeLabels;
        final Set<String> excludeLabels;

        FilterParams(String[] include, String[] exclude, String[] includeLabels, String[] excludeLabels) {
            this.include = ImmutableSet.copyOf((Object[])include);
            this.exclude = ImmutableSet.copyOf((Object[])exclude);
            this.includeLabels = ImmutableSet.copyOf((Object[])includeLabels);
            this.excludeLabels = ImmutableSet.copyOf((Object[])excludeLabels);
        }
    }
}

