/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.UnknownUser
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.Like
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.rest.serialization.graphql.GraphQLPagination
 *  com.atlassian.confluence.rest.serialization.graphql.GraphQLPaginationInfo
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.graphql.annotations.GraphQLExtensions
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.spi.GraphQLTypeContributor
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  graphql.schema.DataFetchingEnvironment
 *  graphql.schema.GraphQLFieldDefinition
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugins.like.graphql;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.UnknownUser;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.Like;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.plugins.like.graphql.LikeEntity;
import com.atlassian.confluence.rest.serialization.graphql.GraphQLPagination;
import com.atlassian.confluence.rest.serialization.graphql.GraphQLPaginationInfo;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.graphql.annotations.GraphQLExtensions;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.spi.GraphQLTypeContributor;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;

@GraphQLExtensions
public class LikesProvider
implements GraphQLTypeContributor {
    private static final int DEFAULT_ICON_HEIGHT = 48;
    private static final int DEFAULT_ICON_WIDTH = 48;
    private final LikeManager likeManager;
    private final PersonService personService;
    private final UserAccessor userAccessor;
    private final NetworkService networkService;
    private final ContentEntityManager contentEntityManager;

    public LikesProvider(LikeManager likeManager, PersonService personService, UserAccessor userAccessor, NetworkService networkService, ContentEntityManager contentEntityManager) {
        this.likeManager = likeManager;
        this.personService = personService;
        this.userAccessor = userAccessor;
        this.networkService = networkService;
        this.contentEntityManager = contentEntityManager;
    }

    public String contributeTypeName(String typeName, Type type, GraphQLTypeBuilderContext context) {
        return null;
    }

    public void contributeFields(String typeName, Type type, List<GraphQLFieldDefinition> fields, GraphQLTypeBuilderContext context) {
        if (!context.isCurrentType(Content.class)) {
            return;
        }
        fields.addAll(context.buildProviderGraphQLType("query", (Object)this).getFieldDefinitions());
    }

    @GraphQLName(value="likes")
    public LikesResponse likes(DataFetchingEnvironment env) {
        return this.likes(env, AuthenticatedUserThreadLocal.get());
    }

    LikesResponse likes(DataFetchingEnvironment env, ConfluenceUser currentUser) {
        ContentId id = (ContentId)((Map)env.getSource()).get("id");
        ContentEntityObject content = this.contentEntityManager.getById(id.asLong());
        Supplier followeeUsernames = Suppliers.memoize(() -> {
            if (currentUser == null) {
                return Collections.emptySet();
            }
            SimplePageRequest followeePageRequest = new SimplePageRequest(0, 0x7FFFFFFE);
            PageResponse followees = this.networkService.getFollowing(currentUser.getKey(), (PageRequest)followeePageRequest);
            return followees.getResults().stream().map(user -> Objects.requireNonNull(user).getUsername()).collect(Collectors.toSet());
        });
        return new LikesResponse(this.likeManager, this.personService, this.userAccessor, content, currentUser, (Supplier<Set<String>>)followeeUsernames);
    }

    public static class LikesResponse
    extends GraphQLPagination<LikeEntity> {
        private final LikeManager likeManager;
        private final PersonService personService;
        private final UserAccessor userAccessor;
        private final ContentEntityObject content;
        private final ConfluenceUser currentUser;
        private final Supplier<Set<String>> followeeUsernames;

        public LikesResponse(LikeManager likeManager, PersonService personService, UserAccessor userAccessor, ContentEntityObject content, ConfluenceUser currentUser, Supplier<Set<String>> followeeUsernames) {
            this.likeManager = likeManager;
            this.personService = personService;
            this.userAccessor = userAccessor;
            this.content = content;
            this.currentUser = currentUser;
            this.followeeUsernames = followeeUsernames;
        }

        @GraphQLName(value="currentUserLikes")
        public boolean currentUserLikes() {
            return this.currentUser != null && this.likeManager.hasLike(this.content, (com.atlassian.user.User)this.currentUser);
        }

        protected void load() {
            List nodes = this.likeManager.getLikes(this.content).stream().map(this::createLikeEntity).collect(Collectors.toList());
            this.setNodes(nodes);
            this.setCount(nodes.size());
            this.setEdges(LikesResponse.buildEdges(nodes, (node, index) -> index.toString()));
            this.setPageInfo(new GraphQLPaginationInfo(false));
        }

        private LikeEntity createLikeEntity(Like like) {
            return new LikeEntity(this.getUser(like), like.getCreatedDate(), this.followeeUsernames);
        }

        private @Nullable User getUser(Like like) {
            if (this.personService.validator().validateView().isAuthorized()) {
                return this.personService.find(new Expansion[0]).withUsername(like.getUsername()).fetch().orElse(null);
            }
            ConfluenceUser user = this.userAccessor.getUserByName(like.getUsername());
            if (user != null) {
                ProfilePictureInfo userProfilePicture = this.userAccessor.getUserProfilePicture((com.atlassian.user.User)user);
                return KnownUser.builder().profilePicture(new Icon(userProfilePicture.getUriReference(), 48, 48, userProfilePicture.isDefault())).userKey(user.getKey()).username(user.getName()).displayName(user.getFullName()).build();
            }
            return new UnknownUser(null, like.getUsername(), null);
        }
    }
}

