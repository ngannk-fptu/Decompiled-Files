/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.ImmutableDirectoryGroup
 *  com.atlassian.crowd.model.group.ImmutableDirectoryGroup$Builder
 *  com.atlassian.crowd.model.group.ImmutableDirectoryGroupWithAttributes
 *  com.atlassian.crowd.model.group.ImmutableGroup
 *  com.atlassian.crowd.model.group.ImmutableGroup$Builder
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.model.user.ImmutableTimestampedUser
 *  com.atlassian.crowd.model.user.ImmutableTimestampedUser$Builder
 *  com.atlassian.crowd.model.user.ImmutableTimestampedUserWithAttributes
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.ImmutableUser$Builder
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.crowd.search.hibernate;

import com.atlassian.crowd.model.InternalEntityAttribute;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.ImmutableDirectoryGroup;
import com.atlassian.crowd.model.group.ImmutableDirectoryGroupWithAttributes;
import com.atlassian.crowd.model.group.ImmutableGroup;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.user.ImmutableTimestampedUser;
import com.atlassian.crowd.model.user.ImmutableTimestampedUserWithAttributes;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.hibernate.CustomDataFetcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomDataFetchers {
    private static final String DIRECTORY_ID = "directory.id";
    private static final String GROUP_TYPE = "type";
    private static final String ACTIVE = "active";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String DISPLAY_NAME = "displayName";
    private static final String EMAIL = "emailAddress";
    private static final String DESCRIPTION = "description";
    private static final String EXTERNAL_ID = "externalId";
    private static final String CREATED_DATE = "createdDate";
    private static final String UPDATED_DATE = "updatedDate";
    private static final String LOCAL = "local";
    private static final CustomDataFetcher<?> NO_OP = new CustomDataFetcher(){

        @Override
        public List<String> attributes(String alias) {
            return ImmutableList.of((Object)alias);
        }

        public Function<Object[], ?> getTransformer(int start) {
            return values -> values[start];
        }
    };
    private static final ImmutableMap<Class<? extends User>, CustomDataFetcher<?>> USER_PRODUCERS = ImmutableMap.of(ImmutableTimestampedUser.class, CustomDataFetchers.crateImmutableTimestampedUserProducer(), ImmutableUser.class, CustomDataFetchers.createImmutableUserProducer(), ImmutableTimestampedUserWithAttributes.class, CustomDataFetchers.createImmutableTimestampedUserWithAttributesProducer());
    private static final ImmutableMap<Class<? extends Group>, CustomDataFetcher<?>> GROUP_PRODUCERS = ImmutableMap.of(ImmutableDirectoryGroup.class, CustomDataFetchers.createImmutableDirectoryGroupProducer(), ImmutableGroup.class, CustomDataFetchers.createImmutableGroupProducer(), ImmutableDirectoryGroupWithAttributes.class, CustomDataFetchers.createImmutableDirectoryGroupWithAttributesProducer());

    private CustomDataFetchers() {
    }

    public static <T> CustomDataFetcher<T> entityProducer(Class<T> returnType) {
        return Stream.of(USER_PRODUCERS, GROUP_PRODUCERS).flatMap(e -> e.entrySet().stream()).filter(entry -> returnType.isAssignableFrom((Class)entry.getKey())).findFirst().map(entry -> (CustomDataFetcher)entry.getValue()).orElse(NO_OP);
    }

    private static CustomDataFetcher<ImmutableTimestampedUserWithAttributes> createImmutableTimestampedUserWithAttributesProducer() {
        return new CustomDataFetcher<ImmutableTimestampedUserWithAttributes>(){

            @Override
            public List<String> attributes(String alias) {
                return ImmutableList.of((Object)alias);
            }

            @Override
            public Function<Object[], ImmutableTimestampedUserWithAttributes> getTransformer(int start) {
                IdentityHashMap cache = new IdentityHashMap();
                return values -> cache.computeIfAbsent((InternalUser)((Object)((Object)values[start])), this::transform);
            }

            private ImmutableTimestampedUserWithAttributes transform(InternalUser user) {
                return ImmutableTimestampedUserWithAttributes.builder((TimestampedUser)user, InternalEntityAttribute.toMap(user.getAttributes())).build();
            }
        };
    }

    private static CustomDataFetcher<ImmutableDirectoryGroupWithAttributes> createImmutableDirectoryGroupWithAttributesProducer() {
        return new CustomDataFetcher<ImmutableDirectoryGroupWithAttributes>(){

            @Override
            public List<String> attributes(String alias) {
                return ImmutableList.of((Object)alias);
            }

            @Override
            public Function<Object[], ImmutableDirectoryGroupWithAttributes> getTransformer(int start) {
                IdentityHashMap cache = new IdentityHashMap();
                return values -> cache.computeIfAbsent((InternalGroup)((Object)((Object)values[start])), this::transform);
            }

            private ImmutableDirectoryGroupWithAttributes transform(InternalGroup group) {
                return ImmutableDirectoryGroupWithAttributes.builder((InternalDirectoryGroup)group, InternalEntityAttribute.toMap(group.getAttributes())).build();
            }
        };
    }

    private static CustomDataFetcher<ImmutableDirectoryGroup> createImmutableDirectoryGroupProducer() {
        SetterBuilder<ImmutableDirectoryGroup.Builder> setters = new SetterBuilder<ImmutableDirectoryGroup.Builder>();
        setters.put(DIRECTORY_ID, (rec$, x$0) -> ((ImmutableDirectoryGroup.Builder)rec$).setDirectoryId(x$0));
        setters.put(GROUP_TYPE, (rec$, x$0) -> ((ImmutableDirectoryGroup.Builder)rec$).setType(x$0));
        setters.put(ACTIVE, (rec$, x$0) -> ((ImmutableDirectoryGroup.Builder)rec$).setActive(x$0));
        setters.put(DESCRIPTION, (rec$, x$0) -> ((ImmutableDirectoryGroup.Builder)rec$).setDescription(x$0));
        setters.put(EXTERNAL_ID, (rec$, x$0) -> ((ImmutableDirectoryGroup.Builder)rec$).setExternalId(x$0));
        setters.put(LOCAL, ImmutableDirectoryGroup.Builder::setLocal);
        setters.put(CREATED_DATE, ImmutableDirectoryGroup.Builder::setCreatedDate);
        setters.put(UPDATED_DATE, ImmutableDirectoryGroup.Builder::setUpdatedDate);
        return CustomDataFetchers.create(ImmutableDirectoryGroup::builder, ImmutableDirectoryGroup.Builder::build, setters);
    }

    private static CustomDataFetcher<ImmutableGroup> createImmutableGroupProducer() {
        SetterBuilder<ImmutableGroup.Builder> setters = new SetterBuilder<ImmutableGroup.Builder>();
        setters.put(DIRECTORY_ID, ImmutableGroup.Builder::setDirectoryId);
        setters.put(GROUP_TYPE, ImmutableGroup.Builder::setType);
        setters.put(ACTIVE, ImmutableGroup.Builder::setActive);
        setters.put(DESCRIPTION, ImmutableGroup.Builder::setDescription);
        setters.put(EXTERNAL_ID, ImmutableGroup.Builder::setExternalId);
        return CustomDataFetchers.create(ImmutableGroup::builder, ImmutableGroup.Builder::build, setters);
    }

    private static CustomDataFetcher<ImmutableTimestampedUser> crateImmutableTimestampedUserProducer() {
        SetterBuilder<ImmutableTimestampedUser.Builder> setters = new SetterBuilder<ImmutableTimestampedUser.Builder>();
        setters.put(DIRECTORY_ID, (rec$, x$0) -> ((ImmutableTimestampedUser.Builder)rec$).directoryId(x$0));
        setters.put(DISPLAY_NAME, (rec$, x$0) -> ((ImmutableTimestampedUser.Builder)rec$).displayName(x$0));
        setters.put(EMAIL, (rec$, x$0) -> ((ImmutableTimestampedUser.Builder)rec$).emailAddress(x$0));
        setters.put(ACTIVE, (rec$, x$0) -> ((ImmutableTimestampedUser.Builder)rec$).active(x$0));
        setters.put(FIRST_NAME, (rec$, x$0) -> ((ImmutableTimestampedUser.Builder)rec$).firstName(x$0));
        setters.put(LAST_NAME, (rec$, x$0) -> ((ImmutableTimestampedUser.Builder)rec$).lastName(x$0));
        setters.put(EXTERNAL_ID, (rec$, x$0) -> ((ImmutableTimestampedUser.Builder)rec$).externalId(x$0));
        setters.put(CREATED_DATE, ImmutableTimestampedUser.Builder::createdDate);
        setters.put(UPDATED_DATE, ImmutableTimestampedUser.Builder::updatedDate);
        return CustomDataFetchers.create(ImmutableTimestampedUser::builder, ImmutableTimestampedUser.Builder::build, setters);
    }

    private static CustomDataFetcher<ImmutableUser> createImmutableUserProducer() {
        SetterBuilder<ImmutableUser.Builder> setters = new SetterBuilder<ImmutableUser.Builder>();
        setters.put(DIRECTORY_ID, (rec$, x$0) -> ((ImmutableUser.Builder)rec$).directoryId(x$0));
        setters.put(DISPLAY_NAME, (rec$, x$0) -> ((ImmutableUser.Builder)rec$).displayName(x$0));
        setters.put(EMAIL, (rec$, x$0) -> ((ImmutableUser.Builder)rec$).emailAddress(x$0));
        setters.put(ACTIVE, (rec$, x$0) -> ((ImmutableUser.Builder)rec$).active(x$0));
        setters.put(FIRST_NAME, (rec$, x$0) -> ((ImmutableUser.Builder)rec$).firstName(x$0));
        setters.put(LAST_NAME, (rec$, x$0) -> ((ImmutableUser.Builder)rec$).lastName(x$0));
        setters.put(EXTERNAL_ID, (rec$, x$0) -> ((ImmutableUser.Builder)rec$).externalId(x$0));
        return CustomDataFetchers.create(ImmutableUser::builder, ImmutableUser.Builder::build, setters);
    }

    private static <T, Q> CustomDataFetcher<Q> create(Function<String, T> fromNameBuilder, Function<T, Q> buildSupplier, SetterBuilder<T> setterBuilder) {
        ImmutableMap immutableSetters = ImmutableMap.copyOf(setterBuilder.setters);
        ImmutableList attributes = ImmutableList.builder().add((Object)"id").add((Object)"name").addAll(immutableSetters.keySet()).build();
        return new CustomDataFetcher<Q>((List)attributes, fromNameBuilder, (Map)immutableSetters, buildSupplier){
            final /* synthetic */ List val$attributes;
            final /* synthetic */ Function val$fromNameBuilder;
            final /* synthetic */ Map val$immutableSetters;
            final /* synthetic */ Function val$buildSupplier;
            {
                this.val$attributes = list;
                this.val$fromNameBuilder = function;
                this.val$immutableSetters = map;
                this.val$buildSupplier = function2;
            }

            @Override
            public List<String> attributes(String alias) {
                return this.val$attributes.stream().map(att -> alias + "." + att).collect(Collectors.toList());
            }

            @Override
            public Function<Object[], Q> getTransformer(int start) {
                HashMap cache = new HashMap();
                return values -> cache.computeIfAbsent(values[start], id -> this.transform((Object[])values, start + 1));
            }

            private Q transform(Object[] values, int start) {
                int idx = start;
                Object builder = this.val$fromNameBuilder.apply((String)values[idx++]);
                for (BiConsumer setter : this.val$immutableSetters.values()) {
                    setter.accept(builder, values[idx++]);
                }
                return this.val$buildSupplier.apply(builder);
            }
        };
    }

    private static class SetterBuilder<T> {
        Map<String, BiConsumer<T, Object>> setters = new LinkedHashMap<String, BiConsumer<T, Object>>();

        private SetterBuilder() {
        }

        <Q> void put(String property, BiConsumer<T, Q> consumer) {
            this.setters.put(property, (entity, value) -> consumer.accept(entity, value));
        }
    }
}

