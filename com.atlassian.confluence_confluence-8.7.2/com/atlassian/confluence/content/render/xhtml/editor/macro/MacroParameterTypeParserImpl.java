/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.GenericLinkParser
 *  com.atlassian.renderer.links.UrlLink
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.editor.macro.ConfluenceContentMacroParameterParser;
import com.atlassian.confluence.content.render.xhtml.editor.macro.InvalidMacroParameterException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser;
import com.atlassian.confluence.content.render.xhtml.links.WebLink;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierCreationException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.ConfluenceLinkResolver;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.browser.beans.MacroParameterType;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.renderer.links.GenericLinkParser;
import com.atlassian.renderer.links.UrlLink;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroParameterTypeParserImpl
implements MacroParameterTypeParser {
    private static final Logger log = LoggerFactory.getLogger(MacroParameterTypeParserImpl.class);
    private final MacroMetadataManager macroMetadataManager;
    private final ConfluenceUserDao userDao;
    private final ConfluenceContentMacroParameterParser confluenceContentParser = new ConfluenceContentMacroParameterParser();

    public MacroParameterTypeParserImpl(MacroMetadataManager macroMetadataManager, ConfluenceUserDao userDao) {
        this.macroMetadataManager = macroMetadataManager;
        this.userDao = userDao;
    }

    @Override
    public Map<String, Object> parseMacroParameters(String macroName, Map<String, String> untypedParameters, ConversionContext conversionContext) throws InvalidMacroParameterException {
        ImmutableMap.Builder typedParameters = ImmutableMap.builder();
        Map<String, MacroParameter> parameters = this.macroMetadataManager.getParameters(macroName);
        for (Map.Entry<String, String> untypedEntry : untypedParameters.entrySet()) {
            String paramName = untypedEntry.getKey();
            Object paramTypedVal = this.parseMacroParameter(macroName, parameters, paramName, untypedEntry.getValue(), untypedParameters, conversionContext);
            typedParameters.put((Object)paramName, paramTypedVal);
        }
        return typedParameters.build();
    }

    @Override
    public Object parseMacroParameter(String macroName, String parameterName, String untypedValue, Map<String, String> untypedParameters, ConversionContext conversionContext) throws InvalidMacroParameterException {
        Map<String, MacroParameter> parameters = this.macroMetadataManager.getParameters(macroName);
        return this.parseMacroParameter(macroName, parameters, parameterName, untypedValue, untypedParameters, conversionContext);
    }

    private Object parseMacroParameter(String macroName, Map<String, MacroParameter> parameters, String parameterName, String untypedValue, Map<String, String> unTypedParameters, ConversionContext conversionContext) throws InvalidMacroParameterException {
        MacroParameter parameterMetadata = parameters.get(parameterName);
        if (parameterMetadata == null) {
            return untypedValue;
        }
        MacroParameterType parameterType = parameterMetadata.getType();
        try {
            if (parameterMetadata.isMultiple()) {
                String delimiter = parameterMetadata.getOptions().getProperty("delimiter");
                if (delimiter == null) {
                    delimiter = ",";
                }
                return this.parseMultipleMacroParameter(macroName, parameterType, parameterMetadata.getName(), untypedValue, unTypedParameters, conversionContext, delimiter);
            }
            return this.parseSingleMacroParameter(macroName, parameterType, parameterMetadata.getName(), untypedValue, unTypedParameters, conversionContext);
        }
        catch (MalformedURLException e) {
            throw new InvalidMacroParameterException(macroName, parameterName, untypedValue, parameterType, e);
        }
    }

    private Object parseMultipleMacroParameter(String macroName, MacroParameterType parameterType, String name, String untypedJoinedValue, Map<String, String> untypedParameters, ConversionContext conversionContext, String delimiter) throws MalformedURLException {
        Iterable<String> splitValues = this.splitValue(untypedJoinedValue, delimiter);
        if (parameterType == null) {
            return splitValues;
        }
        switch (parameterType) {
            case USERNAME: 
            case SPACE_KEY: 
            case CONFLUENCE_CONTENT: 
            case URL: 
            case ATTACHMENT: {
                try {
                    return ImmutableList.copyOf((Iterable)Iterables.transform(splitValues, untypedValue -> {
                        try {
                            return this.parseSingleMacroParameter(macroName, parameterType, name, (String)untypedValue, untypedParameters, conversionContext);
                        }
                        catch (MalformedURLException e) {
                            throw new UncheckedExecutionException((Throwable)e);
                        }
                    }));
                }
                catch (UncheckedExecutionException e) {
                    throw (MalformedURLException)e.getCause();
                }
            }
        }
        return untypedJoinedValue;
    }

    private Iterable<String> splitValue(String untypedJoinedValue, String delimiter) {
        List<String> splitValues = Arrays.asList(untypedJoinedValue.split(Pattern.quote(delimiter)));
        Predicate notBlankPredicate = StringUtils::isNotBlank;
        Iterable nonBlankValues = Iterables.filter(splitValues, (Predicate)notBlankPredicate);
        Function trimFunction = String::trim;
        return Iterables.transform((Iterable)nonBlankValues, (Function)trimFunction);
    }

    private Object parseSingleMacroParameter(String macroName, MacroParameterType parameterType, String name, String untypedValue, Map<String, String> untypedParameters, ConversionContext conversionContext) throws MalformedURLException {
        if (parameterType == null) {
            return untypedValue;
        }
        log.debug("Parsing single macro parameter: {}", (Object)parameterType);
        switch (parameterType) {
            case USERNAME: {
                return this.getUserResourceIdentifier(untypedValue);
            }
            case SPACE_KEY: {
                return new SpaceResourceIdentifier(untypedValue);
            }
            case CONFLUENCE_CONTENT: {
                return this.resolveConfluenceContentLink(untypedValue, conversionContext);
            }
            case URL: {
                return this.resolveUrl(untypedValue);
            }
            case ATTACHMENT: {
                AttachmentContainerResourceIdentifier attachmentContainerResourceIdentifier = null;
                if (conversionContext.getEntity() != null) {
                    ContentEntityObject attachmentContainer = conversionContext.getEntity();
                    attachmentContainerResourceIdentifier = this.createAttachmentContainerResourceIdentifier(attachmentContainer);
                }
                log.debug("Parsed single macro parameter for attachment with a container is : {}", attachmentContainerResourceIdentifier);
                return new AttachmentResourceIdentifier(attachmentContainerResourceIdentifier, untypedValue);
            }
            case FULL_ATTACHMENT: {
                log.debug("Resolving full attachment");
                return this.resolveFullAttachment(macroName, name, untypedValue, untypedParameters, conversionContext);
            }
        }
        return untypedValue;
    }

    private AttachmentContainerResourceIdentifier createAttachmentContainerResourceIdentifier(ContentEntityObject ceo) {
        AttachmentContainerResourceIdentifier attachmentContainerResourceIdentifier;
        if (ceo instanceof AbstractPage && ceo.isDraft()) {
            log.debug("Creating ContentEntityResourceIdentifier for page/shared draft");
            attachmentContainerResourceIdentifier = new ContentEntityResourceIdentifier(ceo.getId());
        } else if (ceo instanceof BlogPost) {
            log.debug("Creating BlogPostResourceIdentifier for blogpost");
            BlogPost blog = (BlogPost)ceo;
            attachmentContainerResourceIdentifier = new BlogPostResourceIdentifier(blog.getSpaceKey(), blog.getTitle(), blog.getPostingCalendarDate());
        } else if (ceo instanceof Page) {
            log.debug("Creating PageResourceIdentifier for page");
            attachmentContainerResourceIdentifier = new PageResourceIdentifier(((Page)ceo).getSpaceKey(), ceo.getTitle());
        } else if (ceo instanceof Draft) {
            log.debug("Creating DraftResourceIdentifier for legacy draft");
            attachmentContainerResourceIdentifier = new DraftResourceIdentifier(ceo.getId());
        } else {
            throw new ResourceIdentifierCreationException(ceo, "Resource not supported.");
        }
        return attachmentContainerResourceIdentifier;
    }

    private Link resolveConfluenceContentLink(String linkText, ConversionContext conversionContext) throws MalformedURLException {
        if (ConfluenceLinkResolver.isUrlLink(linkText)) {
            GenericLinkParser parser = new GenericLinkParser(linkText);
            parser.parseAsContentLink();
            UrlLink urlLink = new UrlLink(parser);
            String url = urlLink.getUnencodedUrl();
            return new DefaultLink(this.resolveUrl(url), new PlainTextLinkBody(urlLink.getLinkBody()), urlLink.getTitle(), null);
        }
        ResourceIdentifier destinationResourceIdentifier = this.confluenceContentParser.parse(linkText, conversionContext.getPageContext());
        return new DefaultLink(destinationResourceIdentifier, null);
    }

    private UrlResourceIdentifier resolveUrl(String url) throws MalformedURLException {
        if (WebLink.isValidURL(url)) {
            return new UrlResourceIdentifier(url);
        }
        String fixedUrl = HtmlUtil.reencodeURL(StringUtils.trim((String)url));
        if (WebLink.isValidURL(fixedUrl)) {
            return new UrlResourceIdentifier(fixedUrl);
        }
        log.error("URL '{}' was not valid, even after reencoding", (Object)url);
        throw new MalformedURLException(url);
    }

    private UserResourceIdentifier getUserResourceIdentifier(String username) {
        ConfluenceUser user = this.userDao.findByUsername(username);
        if (user != null) {
            return UserResourceIdentifier.createFromUsernameSource(user.getKey(), username);
        }
        return UserResourceIdentifier.createForNonExistentUser(username);
    }

    private AttachmentResourceIdentifier resolveFullAttachment(String macroName, String paramName, String paramValue, Map<String, String> untypedParameters, ConversionContext conversionContext) {
        AttachmentContainerResourceIdentifier attachmentContainerResourceIdentifier;
        ContentEntityObject entity = conversionContext.getEntity();
        Map<String, MacroParameter> parameters = this.macroMetadataManager.getParameters(macroName);
        String space = "";
        String pageName = "";
        String date = "";
        Collection derivedParams = Collections2.filter(parameters.values(), macroParameter -> macroParameter != null && macroParameter.getOptions() != null && paramName.equals(macroParameter.getOptions().get("derived")));
        for (MacroParameter derivedParam : derivedParams) {
            if (!paramName.equals(derivedParam.getOptions().get("derived"))) continue;
            String derivedParamName = derivedParam.getName();
            String untypedValue = untypedParameters.get(derivedParamName);
            switch (derivedParam.getType()) {
                case CONFLUENCE_CONTENT: {
                    pageName = untypedValue;
                    break;
                }
                case SPACE_KEY: {
                    space = untypedValue;
                    break;
                }
                case DATE: {
                    date = untypedValue;
                    break;
                }
            }
        }
        if (StringUtils.isEmpty((CharSequence)space) && StringUtils.isEmpty((CharSequence)pageName) && StringUtils.isEmpty((CharSequence)date)) {
            attachmentContainerResourceIdentifier = this.getAttachmentContainerRIFromCEO(entity);
        } else {
            if (StringUtils.isEmpty((CharSequence)space)) {
                space = this.getSpaceKey(entity);
            }
            attachmentContainerResourceIdentifier = StringUtils.isEmpty((CharSequence)date) ? new PageResourceIdentifier(space, pageName) : new BlogPostResourceIdentifier(space, pageName, BlogPost.getCalendarFromDatePath("/" + date + "/" + pageName));
        }
        return new AttachmentResourceIdentifier(attachmentContainerResourceIdentifier, paramValue);
    }

    private AttachmentContainerResourceIdentifier getAttachmentContainerRIFromCEO(ContentEntityObject ceo) {
        if (ceo.isDraft()) {
            if (DraftsTransitionHelper.isLegacyDraft(ceo)) {
                return new DraftResourceIdentifier(ceo.getId());
            }
            return new ContentEntityResourceIdentifier(ceo.getId());
        }
        if (ceo instanceof Page) {
            Page page = (Page)ceo;
            return new PageResourceIdentifier(page.getSpaceKey(), page.getTitle());
        }
        if (ceo instanceof BlogPost) {
            BlogPost blogPost = (BlogPost)ceo;
            return new BlogPostResourceIdentifier(blogPost.getSpaceKey(), blogPost.getTitle(), blogPost.getPostingCalendarDate());
        }
        return null;
    }

    private String getSpaceKey(ContentEntityObject ceo) {
        if (ceo instanceof Page) {
            return ((Page)ceo).getSpaceKey();
        }
        if (ceo instanceof BlogPost) {
            return ((BlogPost)ceo).getSpaceKey();
        }
        if (ceo instanceof Draft) {
            return ((Draft)ceo).getDraftSpaceKey();
        }
        return null;
    }
}

