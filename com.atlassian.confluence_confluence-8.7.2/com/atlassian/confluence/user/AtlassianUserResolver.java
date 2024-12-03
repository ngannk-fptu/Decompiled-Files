/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.people.Anonymous
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.atlassianuser.EmbeddedCrowdUser
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.google.common.base.Functions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.people.Anonymous;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.user.AtlassianUserQueryHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.DefaultUserAccessor;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.atlassianuser.EmbeddedCrowdUser;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
final class AtlassianUserResolver
implements ConfluenceUserResolver {
    private static final Logger log = LoggerFactory.getLogger(AtlassianUserResolver.class);
    private final ConfluenceUserDao confluenceUserDao;
    private final UserManager userManager;
    private final AtlassianUserQueryHelper userQueryHelper;
    private final CrowdService crowdService;

    AtlassianUserResolver(ConfluenceUserDao confluenceUserDao, UserManager userManager, AtlassianUserQueryHelper userQueryHelper, CrowdService crowdService) {
        this.confluenceUserDao = Objects.requireNonNull(confluenceUserDao);
        this.userManager = Objects.requireNonNull(userManager);
        this.userQueryHelper = Objects.requireNonNull(userQueryHelper);
        this.crowdService = Objects.requireNonNull(crowdService);
    }

    @Override
    @Nullable
    public ConfluenceUser getUserByKey(UserKey key) {
        return this.confluenceUserDao.findByKey(key);
    }

    @Override
    @Nullable
    public ConfluenceUser getUserByName(String name) {
        if (StringUtils.isEmpty((CharSequence)name)) {
            return null;
        }
        User user = null;
        try {
            user = this.userManager.getUser(name);
        }
        catch (EntityException e) {
            try {
                user = this.userManager.getUser(name.toLowerCase());
            }
            catch (EntityException e1) {
                log.error("Error in getUser():" + e1.getMessage(), (Throwable)e1);
            }
        }
        return (ConfluenceUser)user;
    }

    @Override
    public List<ConfluenceUser> getUsersByUserKeys(List<UserKey> userKeys) {
        ArrayList<ConfluenceUser> confluenceUsers = new ArrayList<ConfluenceUser>();
        if (userKeys == null || userKeys.isEmpty()) {
            return confluenceUsers;
        }
        List partitions = Lists.partition(userKeys, (int)DefaultUserAccessor.BULK_FETCH_USERS_BATCH_SIZE);
        try {
            for (List partition : partitions) {
                Map usersByLowerName = this.confluenceUserDao.findByKeys((Set<UserKey>)ImmutableSet.copyOf((Collection)partition)).values().stream().filter(Optional::isPresent).map(Optional::get).filter(user -> user.getLowerName() != null).collect(Collectors.toMap(ConfluenceUser::getLowerName, Function.identity(), (u1, u2) -> u1));
                if (usersByLowerName.isEmpty()) continue;
                Collection<User> crowdUsers = this.userQueryHelper.findUsersByName(usersByLowerName.keySet());
                confluenceUsers.addAll(crowdUsers.stream().map(crowdUser -> {
                    ConfluenceUser matchingConfUser = (ConfluenceUser)usersByLowerName.get(IdentifierUtils.toLowerCase((String)crowdUser.getName()));
                    if (matchingConfUser instanceof ConfluenceUserImpl) {
                        ((ConfluenceUserImpl)matchingConfUser).setBackingUser((User)crowdUser);
                    }
                    return matchingConfUser;
                }).filter(Objects::nonNull).collect(Collectors.toList()));
            }
        }
        catch (EntityException e) {
            log.error(e.getMessage(), (Throwable)e);
        }
        return confluenceUsers;
    }

    @Override
    public PageResponse<ConfluenceUser> getUsers(LimitedRequest limitedRequest) {
        Iterable crowdUsers = this.crowdService.search((Query)QueryBuilder.queryFor(com.atlassian.crowd.embedded.api.User.class, (EntityDescriptor)EntityDescriptor.user()).startingAt(limitedRequest.getStart()).returningAtMost(limitedRequest.getLimit() + 1));
        Map lowerNameToCrowdUser = StreamSupport.stream(crowdUsers.spliterator(), false).filter(Objects::nonNull).collect(Collectors.toMap(crowdUser -> crowdUser.getName().toLowerCase(), Functions.identity()));
        ArrayList<String> lowerCrowdNames = new ArrayList<String>(lowerNameToCrowdUser.keySet());
        List partitionedLowerCrowdNames = Lists.partition(lowerCrowdNames, (int)DefaultUserAccessor.BULK_FETCH_USERS_BATCH_SIZE);
        ArrayList<ConfluenceUser> confluenceUsers = new ArrayList<ConfluenceUser>();
        for (List partition : partitionedLowerCrowdNames) {
            List<ConfluenceUser> partitionOfConfluenceUser = this.confluenceUserDao.findConfluenceUsersByLowerNames(partition);
            confluenceUsers.addAll(partitionOfConfluenceUser);
        }
        for (ConfluenceUser confluenceUser : confluenceUsers) {
            com.atlassian.crowd.embedded.api.User crowdUser2 = (com.atlassian.crowd.embedded.api.User)lowerNameToCrowdUser.get(confluenceUser.getLowerName());
            EmbeddedCrowdUser embeddedCrowdUser = new EmbeddedCrowdUser(crowdUser2);
            ((ConfluenceUserImpl)confluenceUser).setBackingUser((User)embeddedCrowdUser);
        }
        return PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, confluenceUsers, null);
    }

    @Override
    @Nullable
    public ConfluenceUser getExistingUserByKey(UserKey key) {
        return Optional.ofNullable(this.getUserByKey(key)).filter(this::validExistingUser).orElse(null);
    }

    private boolean validExistingUser(@Nullable ConfluenceUser user) {
        return user != null && user.getLowerName() != null && !this.confluenceUserDao.isDeletedUser(user);
    }

    @Override
    @Nullable
    public ConfluenceUser getExistingUserByPerson(Person person) {
        if (person instanceof Anonymous) {
            return null;
        }
        if (person.optionalUsername().isPresent()) {
            return this.getUserByName((String)person.optionalUsername().get());
        }
        if (person.optionalUserKey().isPresent()) {
            return this.getExistingUserByKey((UserKey)person.optionalUserKey().get());
        }
        throw new IllegalArgumentException("Person without username or user key: " + person);
    }

    @Override
    public Optional<ConfluenceUser> getExistingByApiUser(com.atlassian.confluence.api.model.people.User user) {
        if (!user.optionalUsername().isPresent() && !user.optionalUserKey().isPresent()) {
            throw new IllegalArgumentException("One of userkey or username must be defined on " + user);
        }
        ConfluenceUser loadedUser = null;
        if (user.optionalUserKey().isPresent()) {
            loadedUser = this.getUserByKey((UserKey)user.optionalUserKey().get());
        }
        if (loadedUser == null && user.optionalUsername().isPresent()) {
            loadedUser = this.getUserByName((String)user.optionalUsername().get());
        }
        return Optional.ofNullable(loadedUser);
    }
}

