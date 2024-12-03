/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.like.Like
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugins.edgeindex.EdgeQueryParameter
 *  com.atlassian.confluence.plugins.edgeindex.PopularContentQueries
 *  com.atlassian.confluence.plugins.edgeindex.ScoreConfig
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper
 *  com.atlassian.confluence.userstatus.FavouriteManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.dailysummary.content.popular;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.like.Like;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.dailysummary.content.popular.ContentStat;
import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentDtoFactory;
import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentExcerptDto;
import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentManager;
import com.atlassian.confluence.plugins.edgeindex.EdgeQueryParameter;
import com.atlassian.confluence.plugins.edgeindex.PopularContentQueries;
import com.atlassian.confluence.plugins.edgeindex.ScoreConfig;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.userstatus.FavouriteManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultPopularContentManager
implements PopularContentManager {
    private static final int NETWORK_COMMENT_SCORE = Integer.getInteger("dailysummary.popularcontent.score.networkcomment", 135);
    private static final int NON_NETWORK_COMMENT_SCORE = Integer.getInteger("dailysummary.popularcontent.score.nonnetworkcomment", 100);
    private static final int NON_NETWORK_LIKE_SCORE = Integer.getInteger("dailysummary.popularcontent.score.nonnetworklike", 50);
    private static final int NETWORK_LIKE_SCORE = Integer.getInteger("dailysummary.popularcontent.score.networklike", 67);
    private static final int CANDIDATE_SET_SIZE = Integer.getInteger("dailysummary.popularcontent.candidatesetsize", 30);
    private static final double NETWORK_CREATOR_BOOST = 1.2;
    private static final double FAV_SPACE_CONTENT_BOOST = 1.5;
    private static final double USER_IS_PARTICIPANT_PENALTY = 0.5;
    private static final Logger log = LoggerFactory.getLogger(PopularContentManager.class);
    private final NetworkService networkService;
    private final PopularContentQueries popularContentQueries;
    private final FavouriteManager favouriteManager;
    private final LikeManager likeManager;
    private final PopularContentDtoFactory popularContentDtoFactory;

    public DefaultPopularContentManager(@ComponentImport NetworkService networkService, @ComponentImport FavouriteManager favouritesManager, @ComponentImport LikeManager likeManager, PopularContentDtoFactory popularContentDtoFactory, @ComponentImport PopularContentQueries contentQueries) {
        this.networkService = networkService;
        this.popularContentQueries = contentQueries;
        this.favouriteManager = favouritesManager;
        this.likeManager = likeManager;
        this.popularContentDtoFactory = popularContentDtoFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<PopularContentExcerptDto> getPopularContent(ConfluenceUser user, Date date, @Nullable Space space, int maxResults, int numDays) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (currentUser != null && user != null && !currentUser.equals(user)) {
            throw new IllegalArgumentException(String.format("Current authenticated user (%s) is not null, and not equal to user param %s", currentUser, user));
        }
        if (user == null) {
            user = currentUser;
        }
        log.debug("Getting popular content for {} on {}", (Object)user, (Object)date);
        try {
            AuthenticatedUserThreadLocal.set((ConfluenceUser)user);
            ScoreConfig scoreConfig = new ScoreConfig();
            scoreConfig.setTimeDecayBase(1.0f);
            EdgeQueryParameter queryParameter = EdgeQueryParameter.builder().since((long)numDays, TimeUnit.DAYS).withScoreConfig(scoreConfig).withEdgeTypes((List)ImmutableList.of((Object)"page.create", (Object)"blogpost.create", (Object)"comment.create", (Object)"like.create")).build();
            List popularContents = this.popularContentQueries.getMostPopular(CANDIDATE_SET_SIZE, queryParameter);
            Calendar cal = Calendar.getInstance();
            if (date == null) {
                date = new Date();
            }
            cal.setTime(date);
            cal.add(5, numDays * -1);
            List<ContentStat> contentStats = this.sortByRelevance(popularContents, user, cal.getTime());
            List contents = contentStats.stream().map(ContentStat::getContent).collect(Collectors.toList());
            ImmutableList pages = space == null ? ImmutableList.builder().addAll(contents).build() : ImmutableList.builder().addAll((Iterable)contents.stream().filter(contentEntityObject -> {
                if (!(contentEntityObject instanceof SpaceContentEntityObject)) {
                    return false;
                }
                SpaceContentEntityObject spaceContentEntityObject = (SpaceContentEntityObject)contentEntityObject;
                return space.getKey().equals(spaceContentEntityObject.getSpaceKey());
            }).collect(Collectors.toList())).build();
            ArrayList<PopularContentExcerptDto> contentExcerpts = new ArrayList<PopularContentExcerptDto>();
            for (ContentEntityObject page : pages) {
                PopularContentExcerptDto contentDto = this.popularContentDtoFactory.createExcerpt(page, user, date);
                if (contentDto == null) continue;
                if (contentDto.getLikeCount() > 0 || contentDto.getCommentCount() > 0) {
                    contentExcerpts.add(contentDto);
                }
                if (contentExcerpts.size() != maxResults) continue;
                ArrayList<PopularContentExcerptDto> arrayList = contentExcerpts;
                return arrayList;
            }
            ArrayList<PopularContentExcerptDto> arrayList = contentExcerpts;
            return arrayList;
        }
        catch (Exception ex) {
            log.error("Daily summary email could not generate popular content for User : " + user, (Throwable)ex);
            ArrayList<PopularContentExcerptDto> arrayList = new ArrayList<PopularContentExcerptDto>();
            return arrayList;
        }
        finally {
            AuthenticatedUserThreadLocal.set((ConfluenceUser)currentUser);
        }
    }

    @Override
    @Deprecated
    public List<PopularContentExcerptDto> getPopularContent(User user, Date date, @Nullable Space space, int maxResults, int numDays) {
        return this.getPopularContent(FindUserHelper.getUser((User)user), date, space, maxResults, numDays);
    }

    protected List<ContentStat> sortByRelevance(List<ContentEntityObject> ceos, ConfluenceUser user, Date since) {
        SimplePageRequest pageReq = new SimplePageRequest(0, 0x7FFFFFFE);
        PageResponse following = this.networkService.getFollowing(user.getKey(), (PageRequest)pageReq);
        Set followingUserNames = StreamSupport.stream(following.spliterator(), false).map(input -> input.getUsername()).collect(Collectors.toSet());
        List rankedStats = ceos.stream().map(ceo -> new ContentStat((ContentEntityObject)ceo, this.score((ContentEntityObject)ceo, (User)user, followingUserNames, since))).sorted(ContentStat.comparator).collect(Collectors.toList());
        return Lists.reverse(rankedStats.stream().filter(input -> input.getCount() >= 0).collect(Collectors.toList()));
    }

    protected int score(ContentEntityObject content, User user, Set<String> followingSet, Date since) {
        String creatorName;
        int followingComments = 0;
        ConfluenceUser creator = content.getCreator();
        String string = creatorName = creator != null ? creator.getName() : null;
        if (creatorName == null || creatorName.equals(user.getName())) {
            return -1;
        }
        boolean creatorInNetwork = followingSet.contains(creatorName);
        boolean contentInFavSpaces = false;
        boolean userIsParticipant = false;
        if (content instanceof Spaced) {
            contentInFavSpaces = this.favouriteManager.isUserFavourite(user, ((Spaced)content).getSpace());
        }
        int commentCount = 0;
        for (Comment comment : content.getComments()) {
            String commentCreatorName;
            if (!comment.getCreationDate().after(since)) continue;
            ConfluenceUser commentCreator = comment.getCreator();
            String string2 = commentCreatorName = commentCreator != null ? commentCreator.getName() : null;
            if (followingSet.contains(commentCreatorName)) {
                ++followingComments;
            } else {
                ++commentCount;
            }
            if (userIsParticipant || !user.getName().equals(commentCreatorName)) continue;
            userIsParticipant = true;
        }
        int likeCount = 0;
        int networkLikeCount = 0;
        for (Like like : this.likeManager.getLikes(content)) {
            if (!like.getCreatedDate().after(since)) continue;
            if (followingSet.contains(like.getUsername())) {
                ++networkLikeCount;
            } else {
                ++likeCount;
            }
            if (userIsParticipant || !user.getName().equals(like.getUsername())) continue;
            userIsParticipant = true;
        }
        return (int)((double)(commentCount * NON_NETWORK_COMMENT_SCORE + followingComments * NETWORK_COMMENT_SCORE + likeCount * NON_NETWORK_LIKE_SCORE + networkLikeCount * NETWORK_LIKE_SCORE) * (creatorInNetwork ? 1.2 : 1.0) * (contentInFavSpaces ? 1.5 : 1.0) * (userIsParticipant ? 0.5 : 1.0));
    }
}

