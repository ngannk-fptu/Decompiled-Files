/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.api;

import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Options;
import com.atlassian.streams.api.common.Pair;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import java.net.URI;

public final class ActivityObjectTypes {
    public static final String STANDARD_IRI_BASE = "http://activitystrea.ms/schema/1.0/";
    public static final String ATLASSIAN_IRI_BASE = "http://streams.atlassian.com/syndication/types/";
    private static final TypeFactory standardTypes = ActivityObjectTypes.newTypeFactory("http://activitystrea.ms/schema/1.0/");

    public static Iterable<ActivityObjectType> getActivityObjectTypes(Iterable<StreamsEntry.ActivityObject> os) {
        return Options.catOptions(Iterables.transform(os, ActivityObjectTypes.getActivityObjectType()));
    }

    @Deprecated
    public static Function<StreamsEntry.ActivityObject, Option<ActivityObjectType>> getActivityObjectType() {
        return GetActivityObjectType.INSTANCE;
    }

    public Option<ActivityObjectType> activity2ObjectType(StreamsEntry.ActivityObject o) {
        return o.getActivityObjectType();
    }

    public static TypeFactory newTypeFactory(String baseIri) {
        return new TypeFactoryImpl(baseIri);
    }

    public static ActivityObjectType comment() {
        return standardTypes.newType("comment");
    }

    public static ActivityObjectType article() {
        return standardTypes.newType("article");
    }

    public static ActivityObjectType file() {
        return standardTypes.newType("file");
    }

    @Deprecated
    public static ActivityObjectType status() {
        return standardTypes.newType("status");
    }

    private static final class TypeFactoryImpl
    implements TypeFactory {
        private final LoadingCache<Pair<String, Option<ActivityObjectType>>, ActivityObjectType> objectTypes;

        public TypeFactoryImpl(String baseIri) {
            this.objectTypes = CacheBuilder.newBuilder().build(CacheLoader.from(TypeFactoryImpl.ObjectTypeFactory(baseIri)));
        }

        @Override
        public ActivityObjectType newType(String key) {
            return (ActivityObjectType)this.objectTypes.getUnchecked(Pair.pair(key, Option.none(ActivityObjectType.class)));
        }

        @Override
        public ActivityObjectType newType(String key, ActivityObjectType parent) {
            return (ActivityObjectType)this.objectTypes.getUnchecked(Pair.pair(key, Option.some(parent)));
        }

        private static Function<Pair<String, Option<ActivityObjectType>>, ActivityObjectType> ObjectTypeFactory(String baseIri) {
            return new ObjectTypeFactory(baseIri);
        }

        private static final class ActivityObjectTypeImpl
        implements ActivityObjectType {
            private final String key;
            private final URI iri;
            private final Option<ActivityObjectType> parent;

            public ActivityObjectTypeImpl(String key, URI iri, Option<ActivityObjectType> parent) {
                this.key = (String)Preconditions.checkNotNull((Object)key, (Object)"key");
                this.iri = (URI)Preconditions.checkNotNull((Object)iri, (Object)"iri");
                this.parent = (Option)Preconditions.checkNotNull(parent, (Object)"parent");
            }

            @Override
            public URI iri() {
                return this.iri;
            }

            @Override
            public String key() {
                return this.key;
            }

            @Override
            public Option<ActivityObjectType> parent() {
                return this.parent;
            }

            public String toString() {
                return this.key;
            }

            public int hashCode() {
                return this.iri.hashCode();
            }

            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null) {
                    return false;
                }
                if (!ActivityObjectType.class.isAssignableFrom(obj.getClass())) {
                    return false;
                }
                ActivityObjectType other = (ActivityObjectType)obj;
                return this.iri.equals(other.iri());
            }
        }

        private static final class ObjectTypeFactory
        implements Function<Pair<String, Option<ActivityObjectType>>, ActivityObjectType> {
            private final String baseIri;

            private ObjectTypeFactory(String baseIri) {
                this.baseIri = baseIri;
            }

            public ActivityObjectType apply(Pair<String, Option<ActivityObjectType>> keyParent) {
                return ObjectTypeFactory.newObjectType(keyParent.first(), URI.create(this.baseIri + keyParent.first()), keyParent.second());
            }

            public static ActivityObjectType newObjectType(String key, URI iri, Option<ActivityObjectType> parent) {
                return new ActivityObjectTypeImpl(key, iri, parent);
            }
        }
    }

    public static interface TypeFactory {
        public ActivityObjectType newType(String var1);

        public ActivityObjectType newType(String var1, ActivityObjectType var2);
    }

    @Deprecated
    private static enum GetActivityObjectType implements Function<StreamsEntry.ActivityObject, Option<ActivityObjectType>>
    {
        INSTANCE;


        public Option<ActivityObjectType> apply(StreamsEntry.ActivityObject o) {
            return o.getActivityObjectType();
        }
    }
}

