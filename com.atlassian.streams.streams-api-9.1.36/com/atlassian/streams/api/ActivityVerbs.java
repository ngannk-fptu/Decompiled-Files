/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 */
package com.atlassian.streams.api;

import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Pair;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.net.URI;

public class ActivityVerbs {
    public static final String STANDARD_IRI_BASE = "http://activitystrea.ms/schema/1.0/";
    public static final String ATLASSIAN_IRI_BASE = "http://streams.atlassian.com/syndication/verbs/";
    private static final VerbFactory standardVerbs = ActivityVerbs.newVerbFactory("http://activitystrea.ms/schema/1.0/");

    @Deprecated
    public static Function<ActivityVerb, String> verbsToKeys() {
        return VerbsToKeys.INSTANCE;
    }

    public static String verb2Key(ActivityVerb verb) {
        return verb.key();
    }

    public static VerbFactory newVerbFactory(String baseIri) {
        return new VerbFactoryImpl(baseIri);
    }

    public static ActivityVerb post() {
        return standardVerbs.newVerb("post");
    }

    public static ActivityVerb update() {
        return standardVerbs.newVerb("update");
    }

    public static ActivityVerb like() {
        return standardVerbs.newVerb("like");
    }

    private static final class VerbFactoryImpl
    implements com.atlassian.streams.api.ActivityVerbs$VerbFactory {
        private final LoadingCache<Pair<String, Option<ActivityVerb>>, ActivityVerb> verbs;

        public VerbFactoryImpl(String baseIri) {
            this.verbs = CacheBuilder.newBuilder().build(CacheLoader.from(VerbFactoryImpl.verbFactory(baseIri)));
        }

        @Override
        public ActivityVerb newVerb(String key) {
            return (ActivityVerb)this.verbs.getUnchecked(Pair.pair(key, Option.none(ActivityVerb.class)));
        }

        @Override
        public ActivityVerb newVerb(String key, ActivityVerb parent) {
            return (ActivityVerb)this.verbs.getUnchecked(Pair.pair(key, Option.some(parent)));
        }

        private static Function<Pair<String, Option<ActivityVerb>>, ActivityVerb> verbFactory(String baseIri) {
            return new VerbFactory(baseIri);
        }

        private static final class ActivityVerbTypeImpl
        implements ActivityVerb {
            private final String key;
            private final URI iri;
            private final Option<ActivityVerb> parent;

            public ActivityVerbTypeImpl(String key, URI iri, Option<ActivityVerb> parent) {
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
            public Option<ActivityVerb> parent() {
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
                if (!ActivityVerb.class.isAssignableFrom(obj.getClass())) {
                    return false;
                }
                ActivityVerb other = (ActivityVerb)obj;
                return this.iri.equals(other.iri());
            }
        }

        private static final class VerbFactory
        implements Function<Pair<String, Option<ActivityVerb>>, ActivityVerb> {
            private final String baseIri;

            private VerbFactory(String baseIri) {
                this.baseIri = baseIri;
            }

            public ActivityVerb apply(Pair<String, Option<ActivityVerb>> keyParent) {
                return VerbFactory.newVerb(keyParent.first(), URI.create(this.baseIri + keyParent.first()), keyParent.second());
            }

            public static ActivityVerb newVerb(String key, URI iri, Option<ActivityVerb> parent) {
                return new ActivityVerbTypeImpl(key, iri, parent);
            }
        }
    }

    public static interface VerbFactory {
        public ActivityVerb newVerb(String var1);

        public ActivityVerb newVerb(String var1, ActivityVerb var2);
    }

    @Deprecated
    private static enum VerbsToKeys implements Function<ActivityVerb, String>
    {
        INSTANCE;


        public String apply(ActivityVerb verb) {
            return verb.key();
        }
    }
}

