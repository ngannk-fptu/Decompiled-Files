/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.Entity
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.cache.CacheLoader
 *  javax.annotation.Nonnull
 *  org.apache.lucene.search.FieldComparatorSource
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.SortField
 *  org.apache.lucene.search.SortField$Type
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.search.v2.mappers.TransformingStringFieldComparatorSource;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.UserAttributeSort;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.Entity;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.CacheLoader;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

@Internal
class UserAttributeSortMapper
implements LuceneSortMapper<UserAttributeSort> {
    private final LocaleManager localeManager;
    private final UserAccessor userAccessor;
    private final CacheLoader<String, ConfluenceUser> getUserByKey = new CacheLoader<String, ConfluenceUser>(){

        public ConfluenceUser load(@Nonnull String userKey) {
            Preconditions.checkNotNull((Object)userKey);
            ConfluenceUser user = UserAttributeSortMapper.this.userAccessor.getUserByKey(new UserKey(userKey));
            if (user == null) {
                throw new IllegalArgumentException("No such user " + userKey);
            }
            return user;
        }
    };

    public UserAttributeSortMapper(LocaleManager localeManager, UserAccessor userAccessor) {
        this.localeManager = localeManager;
        this.userAccessor = userAccessor;
    }

    @Override
    public Sort convertToLuceneSort(UserAttributeSort userSort) {
        boolean reverse = SearchSort.Order.DESCENDING.equals((Object)userSort.getOrder());
        if (userSort.getAttribute().equals((Object)UserAttributeSort.UserAttribute.USERKEY)) {
            return new Sort(new SortField(userSort.getFieldName(), SortField.Type.STRING, reverse));
        }
        return new Sort(new SortField(userSort.getFieldName(), this.getFieldComparator(userSort), reverse));
    }

    private FieldComparatorSource getFieldComparator(UserAttributeSort userSort) {
        Function<ConfluenceUser, String> transformerFunction = this.getStringValueFunction(userSort.getAttribute());
        return new TransformingStringFieldComparatorSource<ConfluenceUser>(this.localeManager.getLocale(AuthenticatedUserThreadLocal.get()), this.getUserByKey, transformerFunction);
    }

    private Function<ConfluenceUser, String> getStringValueFunction(UserAttributeSort.UserAttribute attribute) {
        switch (attribute) {
            case FULLNAME: {
                return User::getFullName;
            }
            case EMAIL: {
                return input -> Strings.nullToEmpty((String)input.getEmail());
            }
            case USERNAME: {
                return Entity::getName;
            }
        }
        throw new UnsupportedOperationException("Sorting not implemented for " + attribute);
    }
}

