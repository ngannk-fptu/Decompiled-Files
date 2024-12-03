/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.links.GenericLinkParser
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.links.LinkResolver
 *  com.atlassian.renderer.links.UnpermittedLink
 *  com.atlassian.renderer.links.UnresolvedLink
 *  com.atlassian.renderer.links.UrlLink
 *  com.atlassian.renderer.v2.macro.MacroManager
 *  com.atlassian.user.User
 *  com.opensymphony.util.UrlUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.AbstractAttachmentLink;
import com.atlassian.confluence.links.DraftAttachmentLink;
import com.atlassian.confluence.links.LinkParserHelper;
import com.atlassian.confluence.links.linktypes.AttachmentLink;
import com.atlassian.confluence.links.linktypes.BlogPostLink;
import com.atlassian.confluence.links.linktypes.ContentLink;
import com.atlassian.confluence.links.linktypes.IncludePageMacroLink;
import com.atlassian.confluence.links.linktypes.PageCreateLink;
import com.atlassian.confluence.links.linktypes.PageLink;
import com.atlassian.confluence.links.linktypes.ShortcutLink;
import com.atlassian.confluence.links.linktypes.SpaceLink;
import com.atlassian.confluence.links.linktypes.UserProfileLink;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.GenericLinkParser;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.links.UnpermittedLink;
import com.atlassian.renderer.links.UnresolvedLink;
import com.atlassian.renderer.links.UrlLink;
import com.atlassian.renderer.v2.macro.MacroManager;
import com.atlassian.user.User;
import com.opensymphony.util.UrlUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class ConfluenceLinkResolver
implements LinkResolver {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceLinkResolver.class);
    private PageManager pageManager;
    private SpaceManager spaceManager;
    private ConfluenceUserResolver confluenceUserResolver;
    private PermissionManager permissionManager;
    private AttachmentManager attachmentManager;
    private SettingsManager settingsManager;
    private ContentEntityManager contentEntityManager;
    private ShortcutLinksManager shortcutLinksManager;
    private PersonalInformationManager personalInformationManager;
    private MacroManager macroManager;
    private StorageFormatCleaner storageFormatCleaner;

    public List<String> extractLinkTextList(String pageContent) {
        LinkParserHelper helper = new LinkParserHelper(pageContent, this.macroManager, this.settingsManager);
        return helper.extractLinks();
    }

    public List<Link> extractLinks(RenderContext context, String pageContent) {
        if (!(context instanceof PageContext)) {
            throw new IllegalArgumentException("expecting a PageContext object, got " + context.getClass().getName());
        }
        ArrayList<Link> result = new ArrayList<Link>();
        for (String linkText : this.extractLinkTextList(pageContent)) {
            result.add(this.createLink(context, this.removeLinkBrackets(linkText)));
        }
        return result;
    }

    public String removeLinkBrackets(String linkText) {
        if (StringUtils.isNotEmpty((CharSequence)linkText) && linkText.startsWith("[") && linkText.endsWith("]")) {
            return linkText.substring(1, linkText.length() - 1);
        }
        return linkText;
    }

    public static String getLinkAsPlainText(String linkBody, String url) {
        if (StringUtils.isNotBlank((CharSequence)linkBody)) {
            return url.equals(linkBody) ? linkBody : linkBody + " (" + url + ")";
        }
        return url;
    }

    public Link createLink(RenderContext context, String linkText) {
        if (!(context instanceof PageContext)) {
            throw new IllegalArgumentException("expecting a PageContext object, got " + context.getClass().getName());
        }
        IncludePageMacroLink includePageMacroLink = new IncludePageMacroLink(linkText);
        if (includePageMacroLink.isValid()) {
            return includePageMacroLink;
        }
        PageContext pageContext = (PageContext)context;
        GenericLinkParser parser = new GenericLinkParser(linkText);
        if (StringUtils.isBlank((CharSequence)parser.getNotLinkBody())) {
            return new UnresolvedLink(linkText);
        }
        if (ConfluenceLinkResolver.isUrlLink(parser.getNotLinkBody())) {
            UrlLink link = new UrlLink(parser);
            if (this.storageFormatCleaner.isCleanUrlAttribute(parser.getNotLinkBody())) {
                return link;
            }
            return new UnresolvedLink(linkText, ConfluenceLinkResolver.getLinkAsPlainText(link.getLinkBody(), link.getUrl()));
        }
        try {
            parser.parseAsContentLink();
            if (parser.getContentId() > 0L) {
                return this.makeContentLink(parser);
            }
            if (StringUtils.isNotEmpty((CharSequence)parser.getShortcutName()) && this.shortcutLinksManager.hasShortcutLink(parser.getShortcutName())) {
                return this.makeShortcutLink(parser);
            }
            if (parser.getDestinationTitle().startsWith("~")) {
                return this.makeUserProfileLink(parser);
            }
            if (StringUtils.isNotEmpty((CharSequence)parser.getShortcutName())) {
                return new UnresolvedLink(parser.getOriginalLinkText(), StringUtils.isNotEmpty((CharSequence)parser.getLinkBody()) ? parser.getLinkBody() : parser.getOriginalLinkText());
            }
            if (!StringUtils.isNotEmpty((CharSequence)parser.getDestinationTitle()) && StringUtils.isNotEmpty((CharSequence)parser.getSpaceKey())) {
                return this.makeSpaceLink(parser);
            }
            if (this.isLinkToBlogPost(parser, pageContext)) {
                return this.makeBlogPostLink(parser, pageContext);
            }
            if (StringUtils.isNotEmpty((CharSequence)parser.getAttachmentName())) {
                return this.makeAttachmentLink(parser, pageContext);
            }
            if (this.isLinkToPage(parser)) {
                return this.makePageLink(parser, pageContext);
            }
        }
        catch (ParseException e) {
            log.info("Parse error while parsing link " + linkText, (Throwable)e);
        }
        return new UnresolvedLink(linkText);
    }

    private boolean isLinkToPage(GenericLinkParser parser) {
        return StringUtils.isNotEmpty((CharSequence)parser.getDestinationTitle()) || StringUtils.isNotEmpty((CharSequence)parser.getAnchor());
    }

    private boolean isLinkToBlogPost(GenericLinkParser parser, PageContext pageContext) {
        return parser.getDestinationTitle().indexOf("/") >= 0 || this.isAnchorToSameBlogPost(pageContext, parser);
    }

    private Link makeContentLink(GenericLinkParser parser) {
        ContentLink link = new ContentLink(parser, this.contentEntityManager);
        if (!link.hasDestination()) {
            return new UnresolvedLink(parser.getOriginalLinkText(), (Link)link);
        }
        if (!this.isUserPermittedToView(link.getDestinationContent())) {
            return new UnpermittedLink((Link)link);
        }
        return link;
    }

    private Link makeShortcutLink(GenericLinkParser parser) {
        ShortcutLink link = new ShortcutLink(parser, this.shortcutLinksManager);
        if (link.hasDestination()) {
            return link;
        }
        return new UnresolvedLink(parser.getOriginalLinkText(), (Link)link);
    }

    private Link makeSpaceLink(GenericLinkParser parser) {
        SpaceLink link = new SpaceLink(parser, this.spaceManager);
        if (!link.hasDestination()) {
            return new UnresolvedLink(parser.getOriginalLinkText(), (Link)link);
        }
        if (!this.isUserPermittedToViewSpace(link.getSpaceKey())) {
            return new UnpermittedLink((Link)link);
        }
        return link;
    }

    private Link makeUserProfileLink(GenericLinkParser parser) throws ParseException {
        UserProfileLink link = new UserProfileLink(parser, this.confluenceUserResolver, this.personalInformationManager);
        if (link.hasDestination()) {
            return link;
        }
        return new UnresolvedLink(parser.getOriginalLinkText(), (Link)link);
    }

    private Link makeBlogPostLink(GenericLinkParser parser, PageContext pageContext) throws ParseException {
        BlogPostLink blogPostLink = new BlogPostLink(parser, pageContext, this.pageManager);
        if (!blogPostLink.hasDestination()) {
            return new UnresolvedLink(parser.getOriginalLinkText(), (Link)blogPostLink);
        }
        if (!this.isUserPermittedToViewSpace(blogPostLink.getSpaceKey())) {
            return new UnpermittedLink((Link)blogPostLink);
        }
        return blogPostLink;
    }

    private Link makePageLink(GenericLinkParser parser, PageContext pageContext) throws ParseException {
        PageLink pageLink = new PageLink(parser, pageContext, this.pageManager);
        if (!pageLink.hasDestination()) {
            return this.makeCreateLink(pageLink, parser, pageContext);
        }
        if (this.isUserPermittedToViewSpace(pageLink.getSpaceKey())) {
            return pageLink;
        }
        return new UnpermittedLink((Link)pageLink);
    }

    private Link makeAttachmentLink(GenericLinkParser parser, PageContext pageContext) throws ParseException {
        AbstractAttachmentLink attachmentLink;
        ContentEntityObject entity = pageContext.getEntity();
        if (this.isLinkToPage(parser)) {
            PageLink pageLink = new PageLink(parser, pageContext, this.pageManager);
            attachmentLink = new AttachmentLink(parser, pageLink, this.attachmentManager);
        } else if (this.isLinkToBlogPost(parser, pageContext)) {
            BlogPostLink blogPostLink = new BlogPostLink(parser, pageContext, this.pageManager);
            attachmentLink = new AttachmentLink(parser, blogPostLink, this.attachmentManager);
        } else {
            if (entity instanceof Comment) {
                Comment comment = (Comment)entity;
                entity = comment.getContainer();
            }
            if (entity instanceof Draft) {
                attachmentLink = new DraftAttachmentLink(parser, (Draft)entity, this.attachmentManager);
            } else if (entity instanceof Page) {
                PageLink pageLink = new PageLink(parser, pageContext, this.pageManager);
                attachmentLink = new AttachmentLink(parser, pageLink, this.attachmentManager);
            } else if (entity instanceof BlogPost) {
                BlogPostLink blogPostLink = new BlogPostLink(parser, pageContext, this.pageManager);
                attachmentLink = new AttachmentLink(parser, blogPostLink, this.attachmentManager);
            } else {
                return new UnresolvedLink(parser.getOriginalLinkText());
            }
        }
        if (attachmentLink.getAttachment() == null) {
            return new UnresolvedLink(parser.getOriginalLinkText());
        }
        if (!this.isUserPermittedToView(attachmentLink.getAttachment())) {
            return new UnpermittedLink((Link)attachmentLink);
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, attachmentLink.getAttachment())) {
            return new UnpermittedLink((Link)attachmentLink);
        }
        return attachmentLink;
    }

    private Link makeCreateLink(PageLink pageLink, GenericLinkParser parser, PageContext pageContext) throws ParseException {
        if (this.spaceManager.getSpace(pageLink.getSpaceKey()) == null) {
            return new UnresolvedLink(pageLink.getOriginalLinkText(), (Link)pageLink);
        }
        if (this.isUserPermittedToCreatePage(pageLink.getSpaceKey())) {
            return new PageCreateLink(parser, pageContext);
        }
        return new UnpermittedLink((Link)new PageCreateLink(parser, pageContext));
    }

    private boolean isAnchorToSameBlogPost(PageContext pageContext, GenericLinkParser parser) {
        return pageContext.getPostingDay() != null && !StringUtils.isNotEmpty((CharSequence)parser.getDestinationTitle()) && parser.getOriginalLinkText().indexOf("#") != -1;
    }

    public static boolean isUrlLink(String textWithoutTitle) {
        if (textWithoutTitle.startsWith("mailto:") || textWithoutTitle.startsWith("file:") || textWithoutTitle.startsWith("//") || textWithoutTitle.startsWith("\\\\")) {
            return true;
        }
        String encodedText = textWithoutTitle.replaceAll("'", "");
        if (encodedText.startsWith("@")) {
            return false;
        }
        try {
            URL url = new URL(encodedText);
            if ("http".equals(url.getProtocol()) || "https".equals(url.getProtocol())) {
                return true;
            }
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
        return UrlUtils.verifyHierachicalURI((String)encodedText);
    }

    private boolean isUserPermittedToCreatePage(String spaceKey) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Space space = this.spaceManager.getSpace(spaceKey);
        return this.permissionManager.hasCreatePermission((User)user, (Object)space, Page.class);
    }

    private boolean isUserPermittedToViewSpace(String spaceKey) {
        return this.isUserPermittedToView(this.spaceManager.getSpace(spaceKey));
    }

    private boolean isUserPermittedToView(Object object) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.permissionManager.hasPermission((User)user, Permission.VIEW, object);
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setShortcutLinksManager(ShortcutLinksManager shortcutLinksManager) {
        this.shortcutLinksManager = shortcutLinksManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setConfluenceUserResolver(ConfluenceUserResolver confluenceUserResolver) {
        this.confluenceUserResolver = confluenceUserResolver;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setMacroManager(MacroManager macroManager) {
        this.macroManager = macroManager;
    }

    public void setPersonalInformationManager(PersonalInformationManager personalInformationManager) {
        this.personalInformationManager = personalInformationManager;
    }

    public void setStorageFormatCleaner(StorageFormatCleaner storageFormatCleaner) {
        this.storageFormatCleaner = storageFormatCleaner;
    }
}

