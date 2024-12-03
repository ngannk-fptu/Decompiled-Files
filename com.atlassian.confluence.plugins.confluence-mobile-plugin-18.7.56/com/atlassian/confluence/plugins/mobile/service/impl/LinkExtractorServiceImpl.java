/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.pages.TinyUrl
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.plugins.mobile.dto.LinkExtractorDto;
import com.atlassian.confluence.plugins.mobile.service.LinkExtractorService;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LinkExtractorServiceImpl
implements LinkExtractorService {
    private static final Pattern ACTION_PATH_PATTERN = Pattern.compile("^(/wiki)?/pages/viewpage\\.action$");
    private static final Pattern PAGE_PATH_PATTERN = Pattern.compile("^(/wiki)?/display/(.+)/(.+)$");
    private static final Pattern BLOGPOST_PATH_PATTERN = Pattern.compile("^(/wiki)?/display/(.+)/\\d{4}/\\d{2}/\\d{2}/(.+)$");
    private static final Pattern TINY_PATH_PATTERN = Pattern.compile("^(/wiki)?/x/(.+)$");
    private static final Pattern MOBILE_PATH_PATTERN = Pattern.compile("^content/view/(\\d+)");
    private static final String APP_PROTOCOL = "confluence://";
    private static final String PAGE_ID_PARAM = "pageId";
    private static final String SPACE_KEY_PARAM = "spaceKey";
    private static final String TITLE_PARAM = "title";
    private final ContentService contentService;
    private final SpaceService spaceService;

    @Autowired
    public LinkExtractorServiceImpl(@ComponentImport ContentService contentService, @ComponentImport SpaceService spaceService) {
        this.contentService = contentService;
        this.spaceService = spaceService;
    }

    @Override
    public LinkExtractorDto extractor(String link) {
        URL url = this.parseURL(link);
        if (this.isServerMatched(url.getHost())) {
            return this.buildLinkExtractorDto(url);
        }
        throw new BadRequestException("Server is not matched");
    }

    private boolean isServerMatched(String host) {
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        if (request == null) {
            return false;
        }
        return host.equals(request.getServerName());
    }

    private LinkExtractorDto buildLinkExtractorDto(URL url) {
        Matcher matcher = ACTION_PATH_PATTERN.matcher(url.getPath());
        if (matcher.matches()) {
            return this.extractFromActionPath(url);
        }
        matcher = BLOGPOST_PATH_PATTERN.matcher(url.getPath());
        if (matcher.matches()) {
            return this.extractFromDisplayPath(matcher, url, ContentType.BLOG_POST);
        }
        matcher = PAGE_PATH_PATTERN.matcher(url.getPath());
        if (matcher.matches()) {
            return this.extractFromDisplayPath(matcher, url, ContentType.PAGE);
        }
        matcher = TINY_PATH_PATTERN.matcher(url.getPath());
        if (matcher.matches()) {
            return this.extractFromTinyPath(matcher);
        }
        if (StringUtils.isNotBlank((CharSequence)url.getRef()) && (matcher = MOBILE_PATH_PATTERN.matcher(url.getRef())).matches()) {
            return this.extractFromMobileLink(matcher);
        }
        throw new BadRequestException("This URL is not supported");
    }

    private LinkExtractorDto extractFromActionPath(URL url) {
        Map<String, String> paramMap = this.parseQuery(url.getQuery());
        if (StringUtils.isNotBlank((CharSequence)paramMap.get(PAGE_ID_PARAM))) {
            return LinkExtractorDto.builder().pageId(Long.parseLong(paramMap.get(PAGE_ID_PARAM))).build();
        }
        if (StringUtils.isNotBlank((CharSequence)paramMap.get(SPACE_KEY_PARAM)) && StringUtils.isNotBlank((CharSequence)paramMap.get(TITLE_PARAM))) {
            Optional<Content> content = this.findContent(paramMap.get(SPACE_KEY_PARAM), paramMap.get(TITLE_PARAM), ContentType.PAGE);
            if (!content.isPresent()) {
                throw new NotFoundException("Cannot find content with title: " + paramMap.get(TITLE_PARAM));
            }
            return LinkExtractorDto.builder().pageId(content.get().getId().asLong()).build();
        }
        throw new BadRequestException("Missing pageId or (spaceKey and title) parameter.");
    }

    private LinkExtractorDto extractFromDisplayPath(Matcher matcher, URL url, ContentType type) {
        LinkExtractorDto.Builder builder = LinkExtractorDto.builder();
        Optional<Content> content = this.findContent(matcher.group(2), matcher.group(3), type);
        if (!content.isPresent()) {
            throw new NotFoundException("Cannot find content with title: " + matcher.group(3));
        }
        builder.pageId(content.get().getId().asLong());
        Map<String, String> paramMap = this.parseQuery(url.getQuery());
        if (paramMap.get("focusedCommentId") != null) {
            builder.commentId(Long.parseLong(paramMap.get("focusedCommentId")));
        }
        return builder.build();
    }

    private LinkExtractorDto extractFromTinyPath(Matcher matcher) {
        TinyUrl tinyUrl = new TinyUrl(matcher.group(2));
        return LinkExtractorDto.builder().pageId(tinyUrl.getPageId()).build();
    }

    private LinkExtractorDto extractFromMobileLink(Matcher matcher) {
        return LinkExtractorDto.builder().pageId(Long.parseLong(matcher.group(1))).build();
    }

    private URL parseURL(String link) {
        link = link.replace(APP_PROTOCOL, "http://");
        try {
            return new URL(link);
        }
        catch (MalformedURLException e) {
            throw new BadRequestException("Invalid URL");
        }
    }

    private Map<String, String> parseQuery(String query) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        if (StringUtils.isBlank((CharSequence)query)) {
            return paramMap;
        }
        try {
            String[] parameters;
            for (String parameter : parameters = URLDecoder.decode(query, "UTF-8").split("&")) {
                String[] data = parameter.split("=");
                if (data.length != 2) continue;
                paramMap.put(data[0], data[1]);
            }
            return paramMap;
        }
        catch (UnsupportedEncodingException e) {
            return paramMap;
        }
    }

    private Optional<Content> findContent(String spaceKey, String title, ContentType type) {
        Optional space = this.spaceService.find(new Expansion[0]).withKeys(new String[]{spaceKey}).fetch();
        if (!space.isPresent()) {
            throw new NotFoundException("No space with key : " + spaceKey);
        }
        return this.contentService.find(new Expansion[0]).withSpace(new Space[]{(Space)space.get()}).withType(new ContentType[]{type}).withTitle(title).fetch();
    }
}

