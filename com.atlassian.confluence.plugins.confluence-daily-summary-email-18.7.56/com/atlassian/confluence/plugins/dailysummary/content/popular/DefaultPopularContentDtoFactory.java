/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.view.excerpt.ExcerptConfig
 *  com.atlassian.confluence.content.render.xhtml.view.excerpt.Excerpter
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.like.Like
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UnknownUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.activation.DataSource
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.dailysummary.content.popular;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.ExcerptConfig;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.Excerpter;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.like.Like;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.dailysummary.content.ImageDataSource;
import com.atlassian.confluence.plugins.dailysummary.content.popular.ImageHelper;
import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentDtoFactory;
import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentExcerptDto;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPopularContentDtoFactory
implements PopularContentDtoFactory {
    private static final Logger log = LoggerFactory.getLogger(DefaultPopularContentDtoFactory.class);
    private final Excerpter excerpter;
    private final LikeManager likeManager;
    private final NetworkService networkService;
    private final DataSourceFactory dataSourceFactory;
    private final UserManager userManager;
    private final List<String> excludedMacros;
    private final I18NBeanFactory i18NBeanFactory;
    private static final int CONTENT_IMG_LARGE_HEIGHT = 160;
    private static final int CONTENT_IMG_LARGE_MIN_WIDTH = 80;
    private static final int CONTENT_IMG_LARGE_MAX_WIDTH = 288;
    private static final int CONTENT_IMG_SMALL_HEIGHT = 48;
    private static final int AVATAR_IMG_SIZE = 24;
    private static final int AVATAR_IMG_SIZE_LARGE = 48;
    private static final float IMG_SIZE_THRESHOLD_RATIO = 0.66f;

    public DefaultPopularContentDtoFactory(@ComponentImport Excerpter excerpter, @ComponentImport LikeManager likeManager, @ComponentImport NetworkService networkService, @ComponentImport UserManager userManager, @ComponentImport DataSourceFactory dataSourceFactory, List<String> excludedMacros, @ComponentImport I18NBeanFactory i18NBeanFactory) {
        this.excerpter = excerpter;
        this.likeManager = likeManager;
        this.networkService = networkService;
        this.dataSourceFactory = dataSourceFactory;
        this.userManager = userManager;
        this.excludedMacros = ImmutableList.builder().addAll(excludedMacros).build();
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public PopularContentExcerptDto createExcerpt(ContentEntityObject content, ConfluenceUser user, Date date) {
        int MAX_EXCERPT_BLOCKS = 6;
        int MIN_EXCERPT_BLOCKS = 3;
        int MIN_CHAR_COUNT = 400;
        int MAX_CHAR_COUNT = 750;
        String excerpt = "";
        try {
            excerpt = this.excerpter.createExcerpt(content, "email", ExcerptConfig.builder().excludeHtmlElements((Iterable)Sets.newHashSet((Object[])new String[]{"img"})).minBlocks(3).maxBlocks(6).minCharCount(400).maxCharCount(750).excludedLastHtmlElement(Arrays.asList("h1", "h2", "h3", "h4", "h5", "h6")).macroDefinitionUpdater(this.getExcerptMacroDefinitionUpdater()).build());
        }
        catch (Exception ex) {
            log.warn("Error creating excerpt from : {} with exception : {}", (Object)content, (Object)ex.getMessage());
            log.debug("Exception creating excerpt from page :{}", (Object)content, (Object)ex);
        }
        if (StringUtils.isBlank((CharSequence)excerpt)) {
            log.debug("Excerpt was blank for {} with {} , {}", new Object[]{content, user, date});
            return null;
        }
        SimplePageRequest pageReq = new SimplePageRequest(0, 0x7FFFFFFE);
        PageResponse users = this.networkService.getFollowing(user.getKey(), (PageRequest)pageReq);
        Set userNetwork = StreamSupport.stream(users.spliterator(), false).map(arg_0 -> ((Function)com.atlassian.confluence.api.model.people.User.mapUserToUsername).apply(arg_0)).collect(Collectors.toSet());
        LinkedHashSet<String> networkParticipants = new LinkedHashSet<String>();
        for (Comment comment : content.getComments()) {
            ConfluenceUser creator = comment.getCreator();
            String creatorName = creator != null ? creator.getName() : null;
            if (!userNetwork.contains(creatorName) || user.getName().equals(creatorName)) continue;
            networkParticipants.add(creatorName);
        }
        for (Like like : this.likeManager.getLikes(content)) {
            if (!userNetwork.contains(like.getUsername()) || like.getUsername().equals(user.getName())) continue;
            networkParticipants.add(like.getUsername());
        }
        ConfluenceUser author = content.getCreator();
        Map<String, DataSource> emailImages = this.getImageDataSources(networkParticipants, content);
        PopularContentExcerptDto.Builder builder = new PopularContentExcerptDto.Builder(content, (User)author).excerptBody(excerpt).likeCount(this.likeManager.getLikes(content).size());
        builder.addNetworkParticipant(networkParticipants.stream().map(this::getUser).collect(Collectors.toList()));
        String authorName = author.getName();
        log.debug("Processing content #{} of author {} for user {}", new Object[]{content.getId(), authorName, user.getName()});
        ImageDataSource authorAvatar = this.getUserImage(authorName, 48);
        if (authorAvatar != null) {
            emailImages.put(authorName, authorAvatar);
        }
        return builder.addImageDataSource(emailImages).build();
    }

    private User getUser(String username) {
        try {
            return this.userManager.getUser(username);
        }
        catch (EntityException e) {
            return UnknownUser.unknownUser((String)username, (I18NBean)this.i18NBeanFactory.getI18NBean());
        }
    }

    private Map<String, DataSource> getImageDataSources(Set<String> users, ContentEntityObject ceo) {
        Map<String, DataSource> dataSourceMap = this.getUserImages(users);
        try {
            dataSourceMap.putAll(this.getContentImages(ceo));
        }
        catch (Exception ex) {
            log.warn("Could not extract images from content, will render summary email without content images for Content :{} with id: {}", (Object)ceo.getDisplayTitle(), (Object)ceo.getId());
            log.debug("Could not extract images - previous warning message stacktrace", (Throwable)ex);
        }
        return dataSourceMap;
    }

    private Map<String, DataSource> getContentImages(ContentEntityObject ceo) throws XMLStreamException, XhtmlException {
        ImageHelper imageHelper = new ImageHelper();
        List srcs = this.excerpter.extractImageSrc(ceo, 4);
        try {
            List<DataSource> cachedImageDatasources = this.getCachedContentImages(srcs);
            if (cachedImageDatasources != null) {
                return this.convertToContentImgs(cachedImageDatasources);
            }
        }
        catch (IOException io) {
            log.warn("Exception getting cached images: {}", (Object)io.getMessage());
            log.debug("Exception getting cached image, previous error stacktrace: ", (Throwable)io);
        }
        try {
            HashMap<BufferedImage, DataSource> imageDsMap = new HashMap<BufferedImage, DataSource>();
            int imageCount = 0;
            for (DataSource ds : srcs) {
                if (imageCount == 4) break;
                BufferedImage image = ImageIO.read(ds.getInputStream());
                imageDsMap.put(image, ds);
                ++imageCount;
            }
            TreeMap sortedImageDsMap = Maps.newTreeMap((arg0, arg1) -> {
                int arg1Area;
                if (arg0 == arg1) {
                    return 0;
                }
                int arg0Area = arg0.getHeight() * arg0.getWidth();
                if (arg0Area == (arg1Area = arg1.getHeight() * arg1.getWidth())) {
                    return srcs.indexOf(imageDsMap.get(arg0)) - srcs.indexOf(imageDsMap.get(arg1));
                }
                return arg1Area - arg0Area;
            });
            sortedImageDsMap.putAll(imageDsMap);
            imageCount = 0;
            HashMap<String, DataSource> contentImgMap = new HashMap<String, DataSource>();
            for (Map.Entry imageDsEntry : sortedImageDsMap.entrySet()) {
                BufferedImage image = imageCount == 0 ? this.getResizedImageIfAboveThreshold(imageHelper, (BufferedImage)imageDsEntry.getKey(), ((DataSource)imageDsEntry.getValue()).getName(), 160, 80, 288) : this.getResizedImageIfAboveThreshold(imageHelper, (BufferedImage)imageDsEntry.getKey(), ((DataSource)imageDsEntry.getValue()).getName(), 48, 48, 48);
                if (image == null) continue;
                contentImgMap.put("content-img" + imageCount, imageHelper.convertToDataSource(image, ((DataSource)imageDsEntry.getValue()).getName()));
                ++imageCount;
            }
            if (contentImgMap.size() == 2) {
                contentImgMap.remove("content-img1");
            }
            return contentImgMap;
        }
        catch (IOException ex) {
            log.error("Could not resize images for daily summary email {} for Content: {}", (Object)ex.getMessage(), (Object)ceo.getDisplayTitle());
            log.debug("Daily summary email exception: ", (Throwable)ex);
            return Collections.emptyMap();
        }
    }

    private BufferedImage getResizedImageIfAboveThreshold(ImageHelper imageHelper, BufferedImage toResize, String dsName, int height, int minWidth, int maxWidth) throws IOException {
        BufferedImage image = null;
        int heightThreshold = (int)((float)height * 0.66f);
        int widthThreshold = (int)((float)minWidth * 0.66f);
        if (toResize.getHeight() > heightThreshold && toResize.getWidth() > widthThreshold && (image = imageHelper.getCached(dsName, height, minWidth, maxWidth)) == null) {
            image = imageHelper.resizeAndCache(toResize, dsName, height, minWidth, maxWidth);
        }
        return image;
    }

    private Map<String, DataSource> convertToContentImgs(List<DataSource> dataSources) {
        HashMap<String, DataSource> contentImgMap = new HashMap<String, DataSource>();
        int imageCount = 0;
        for (DataSource ds : dataSources) {
            contentImgMap.put("content-img" + imageCount, ds);
            ++imageCount;
        }
        return contentImgMap;
    }

    private List<DataSource> getCachedContentImages(List<DataSource> srcs) throws IOException {
        ImageHelper imageHelper = new ImageHelper();
        LinkedHashMap<Object, BufferedImage> largeImages = new LinkedHashMap<Object, BufferedImage>();
        for (DataSource dataSource : srcs) {
            BufferedImage image = imageHelper.getCached(dataSource.getName(), 160, 80, 288);
            if (image == null) continue;
            largeImages.put(dataSource, image);
            break;
        }
        if (largeImages.isEmpty()) {
            return null;
        }
        LinkedHashMap<DataSource, BufferedImage> smallImages = new LinkedHashMap<DataSource, BufferedImage>();
        for (DataSource ds : srcs) {
            BufferedImage image = imageHelper.getCached(ds.getName(), 48, 48, 48);
            if (image == null) continue;
            smallImages.put(ds, image);
        }
        smallImages.remove(largeImages.keySet().iterator().next());
        largeImages.putAll(smallImages);
        if (smallImages.size() + 1 != srcs.size()) {
            return null;
        }
        ArrayList<DataSource> arrayList = new ArrayList<DataSource>();
        for (Map.Entry dsImageEntry : largeImages.entrySet()) {
            arrayList.add(imageHelper.convertToDataSource((RenderedImage)dsImageEntry.getValue(), ((DataSource)dsImageEntry.getKey()).getName()));
        }
        return arrayList;
    }

    private Map<String, DataSource> getUserImages(Set<String> userNames) {
        HashMap<String, DataSource> usernamesToDatasources = new HashMap<String, DataSource>();
        for (String user : userNames) {
            ImageDataSource ds = this.getUserImage(user, 24);
            if (ds == null) continue;
            usernamesToDatasources.put(user, ds);
        }
        return usernamesToDatasources;
    }

    private ImageDataSource getUserImage(String username, Integer avatarSize) {
        User user = this.getUser(username);
        DataSource avatarDs = this.dataSourceFactory.getAvatar(user);
        ImageHelper imageHelper = new ImageHelper();
        try {
            BufferedImage image = imageHelper.getCachedOrResize(avatarDs, avatarSize, avatarSize, avatarSize);
            return imageHelper.convertToDataSource(image, "px" + avatarSize + avatarDs.getName());
        }
        catch (IOException io) {
            log.warn("Exception: {} getting avatar for user in summary email : {}, for more information turn on INFO level logging for com.atlassian.confluence.plugins.dailysummary.content.popular", (Object)io.getMessage(), (Object)username);
            log.debug("Exception: ", (Throwable)io);
            return null;
        }
    }

    private MacroDefinitionUpdater getExcerptMacroDefinitionUpdater() {
        return macroDefinition -> {
            if (this.excludedMacros.contains(macroDefinition.getName())) {
                return null;
            }
            return macroDefinition;
        };
    }
}

