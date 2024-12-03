/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.config;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface MeterFilter {
    public static MeterFilter commonTags(final Iterable<Tag> tags) {
        return new MeterFilter(){

            @Override
            public Meter.Id map(Meter.Id id) {
                return id.replaceTags(Tags.concat((Iterable<? extends Tag>)tags, id.getTagsAsIterable()));
            }
        };
    }

    public static MeterFilter renameTag(final String meterNamePrefix, final String fromTagKey, final String toTagKey) {
        return new MeterFilter(){

            @Override
            public Meter.Id map(Meter.Id id) {
                if (!id.getName().startsWith(meterNamePrefix)) {
                    return id;
                }
                ArrayList<Tag> tags = new ArrayList<Tag>();
                for (Tag tag : id.getTagsAsIterable()) {
                    if (tag.getKey().equals(fromTagKey)) {
                        tags.add(Tag.of(toTagKey, tag.getValue()));
                        continue;
                    }
                    tags.add(tag);
                }
                return id.replaceTags(tags);
            }
        };
    }

    public static MeterFilter ignoreTags(final String ... tagKeys) {
        return new MeterFilter(){

            @Override
            public Meter.Id map(Meter.Id id) {
                List<Tag> tags = StreamSupport.stream(id.getTagsAsIterable().spliterator(), false).filter(t -> {
                    for (String tagKey : tagKeys) {
                        if (!t.getKey().equals(tagKey)) continue;
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());
                return id.replaceTags(tags);
            }
        };
    }

    public static MeterFilter replaceTagValues(final String tagKey, final Function<String, String> replacement, final String ... exceptions) {
        return new MeterFilter(){

            @Override
            public Meter.Id map(Meter.Id id) {
                List<Tag> tags = StreamSupport.stream(id.getTagsAsIterable().spliterator(), false).map((? super T t) -> {
                    if (!t.getKey().equals(tagKey)) {
                        return t;
                    }
                    for (String exception : exceptions) {
                        if (!t.getValue().equals(exception)) continue;
                        return t;
                    }
                    return Tag.of(tagKey, (String)replacement.apply(t.getValue()));
                }).collect(Collectors.toList());
                return id.replaceTags(tags);
            }
        };
    }

    public static MeterFilter denyUnless(final Predicate<Meter.Id> iff) {
        return new MeterFilter(){

            @Override
            public MeterFilterReply accept(Meter.Id id) {
                return iff.test(id) ? MeterFilterReply.NEUTRAL : MeterFilterReply.DENY;
            }
        };
    }

    public static MeterFilter accept(final Predicate<Meter.Id> iff) {
        return new MeterFilter(){

            @Override
            public MeterFilterReply accept(Meter.Id id) {
                return iff.test(id) ? MeterFilterReply.ACCEPT : MeterFilterReply.NEUTRAL;
            }
        };
    }

    public static MeterFilter deny(final Predicate<Meter.Id> iff) {
        return new MeterFilter(){

            @Override
            public MeterFilterReply accept(Meter.Id id) {
                return iff.test(id) ? MeterFilterReply.DENY : MeterFilterReply.NEUTRAL;
            }
        };
    }

    public static MeterFilter accept() {
        return MeterFilter.accept(id -> true);
    }

    public static MeterFilter deny() {
        return MeterFilter.deny(id -> true);
    }

    public static MeterFilter maximumAllowableMetrics(final int maximumTimeSeries) {
        return new MeterFilter(){
            private final Set<Meter.Id> ids = ConcurrentHashMap.newKeySet();

            @Override
            public MeterFilterReply accept(Meter.Id id) {
                if (this.ids.size() > maximumTimeSeries) {
                    return MeterFilterReply.DENY;
                }
                this.ids.add(id);
                return this.ids.size() > maximumTimeSeries ? MeterFilterReply.DENY : MeterFilterReply.NEUTRAL;
            }
        };
    }

    public static MeterFilter maximumAllowableTags(final String meterNamePrefix, final String tagKey, final int maximumTagValues, final MeterFilter onMaxReached) {
        return new MeterFilter(){
            private final Set<String> observedTagValues = ConcurrentHashMap.newKeySet();

            @Override
            public MeterFilterReply accept(Meter.Id id) {
                String value = this.matchNameAndGetTagValue(id);
                if (value != null && !this.observedTagValues.contains(value)) {
                    if (this.observedTagValues.size() >= maximumTagValues) {
                        return onMaxReached.accept(id);
                    }
                    this.observedTagValues.add(value);
                }
                return MeterFilterReply.NEUTRAL;
            }

            @Nullable
            private String matchNameAndGetTagValue(Meter.Id id) {
                return id.getName().startsWith(meterNamePrefix) ? id.getTag(tagKey) : null;
            }

            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                String value = this.matchNameAndGetTagValue(id);
                if (value != null && !this.observedTagValues.contains(value) && this.observedTagValues.size() >= maximumTagValues) {
                    return onMaxReached.configure(id, config);
                }
                return config;
            }
        };
    }

    public static MeterFilter denyNameStartsWith(String prefix) {
        return MeterFilter.deny(id -> id.getName().startsWith(prefix));
    }

    public static MeterFilter acceptNameStartsWith(String prefix) {
        return MeterFilter.accept(id -> id.getName().startsWith(prefix));
    }

    public static MeterFilter maxExpected(final String prefix, final Duration max) {
        return new MeterFilter(){

            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                if (id.getType() == Meter.Type.TIMER && id.getName().startsWith(prefix)) {
                    return DistributionStatisticConfig.builder().maximumExpectedValue(Double.valueOf(max.toNanos())).build().merge(config);
                }
                return config;
            }
        };
    }

    @Deprecated
    public static MeterFilter maxExpected(String prefix, long max) {
        return MeterFilter.maxExpected(prefix, (double)max);
    }

    public static MeterFilter maxExpected(final String prefix, final double max) {
        return new MeterFilter(){

            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                if (id.getType() == Meter.Type.DISTRIBUTION_SUMMARY && id.getName().startsWith(prefix)) {
                    return DistributionStatisticConfig.builder().maximumExpectedValue(max).build().merge(config);
                }
                return config;
            }
        };
    }

    public static MeterFilter minExpected(final String prefix, final Duration min) {
        return new MeterFilter(){

            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                if (id.getType() == Meter.Type.TIMER && id.getName().startsWith(prefix)) {
                    return DistributionStatisticConfig.builder().minimumExpectedValue(Double.valueOf(min.toNanos())).build().merge(config);
                }
                return config;
            }
        };
    }

    @Deprecated
    public static MeterFilter minExpected(String prefix, long min) {
        return MeterFilter.minExpected(prefix, (double)min);
    }

    public static MeterFilter minExpected(final String prefix, final double min) {
        return new MeterFilter(){

            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                if (id.getType() == Meter.Type.DISTRIBUTION_SUMMARY && id.getName().startsWith(prefix)) {
                    return DistributionStatisticConfig.builder().minimumExpectedValue(min).build().merge(config);
                }
                return config;
            }
        };
    }

    default public MeterFilterReply accept(Meter.Id id) {
        return MeterFilterReply.NEUTRAL;
    }

    default public Meter.Id map(Meter.Id id) {
        return id;
    }

    @Nullable
    default public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
        return config;
    }
}

