/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.search;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.search.RequiredSearch;
import io.micrometer.core.instrument.search.Search;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MeterNotFoundException
extends RuntimeException {
    private static final String OK = "OK:";
    private static final String NOT_OK = "FAIL:";

    private MeterNotFoundException(@Nullable String nameDetail, @Nullable String typeDetail, @Nullable List<String> tagDetail) {
        super("Unable to find a meter that matches all the requirements at once. Here's what was found:" + (nameDetail == null ? "" : "\n   " + nameDetail) + (typeDetail == null ? "" : "\n   " + typeDetail) + (tagDetail == null ? "" : "\n   " + tagDetail.stream().collect(Collectors.joining("\n   "))));
    }

    static MeterNotFoundException forSearch(RequiredSearch search, Class<? extends Meter> requiredType) {
        return new FromRequiredSearch(search, requiredType).build();
    }

    private static class FromRequiredSearch {
        private final RequiredSearch search;
        private final Class<? extends Meter> requiredMeterType;

        private FromRequiredSearch(RequiredSearch search, Class<? extends Meter> requiredMeterType) {
            this.search = search;
            this.requiredMeterType = requiredMeterType;
        }

        @Nullable
        private String nameDetail() {
            if (this.search.nameMatches == null) {
                return null;
            }
            Collection matchingName = Search.in(this.search.registry).name(this.search.nameMatches).meters().stream().map(m -> m.getId().getName()).distinct().sorted().collect(Collectors.toList());
            if (!matchingName.isEmpty()) {
                if (matchingName.size() == 1) {
                    return "OK: A meter with name '" + (String)matchingName.iterator().next() + "' was found.";
                }
                return "OK: Meters with names [" + matchingName.stream().map(name -> "'" + name + "'").collect(Collectors.joining(", ")) + "] were found.";
            }
            if (this.search.exactNameMatch != null) {
                return "FAIL: No meter with name '" + this.search.exactNameMatch + "' was found.";
            }
            return "FAIL: No meter that matches the name predicate was found.";
        }

        @Nullable
        private List<String> tagDetail() {
            String requiredTagDetail;
            if (this.search.requiredTagKeys.isEmpty() && this.search.requiredTags.isEmpty()) {
                return null;
            }
            ArrayList<String> details = new ArrayList<String>();
            for (String requiredKey : this.search.requiredTagKeys) {
                Collection matchingRequiredKey = Search.in(this.search.registry).name(this.search.nameMatches).tagKeys(requiredKey).meters().stream().filter(this.requiredMeterType::isInstance).map(m -> m.getId().getName()).distinct().sorted().collect(Collectors.toList());
                requiredTagDetail = "the required tag '" + requiredKey + "'.";
                if (matchingRequiredKey.isEmpty()) {
                    details.add("FAIL: No meters have " + requiredTagDetail);
                    continue;
                }
                if (matchingRequiredKey.size() == 1) {
                    details.add("OK: A meter with name '" + (String)matchingRequiredKey.iterator().next() + "' has " + requiredTagDetail);
                    continue;
                }
                details.add("OK: Meters with names [" + matchingRequiredKey.stream().map(name -> "'" + name + "'").collect(Collectors.joining(", ")) + "] have " + requiredTagDetail);
            }
            for (Tag requiredTag : this.search.requiredTags) {
                Collection matchingRequiredTag = Search.in(this.search.registry).name(this.search.nameMatches).tag(requiredTag.getKey(), requiredTag.getValue()).meters().stream().filter(this.requiredMeterType::isInstance).map(m -> m.getId().getName()).distinct().sorted().collect(Collectors.toList());
                requiredTagDetail = "a tag '" + requiredTag.getKey() + "' with value '" + requiredTag.getValue() + "'.";
                if (matchingRequiredTag.isEmpty()) {
                    Collection nonMatchingValues = Search.in(this.search.registry).name(this.search.nameMatches).tagKeys(requiredTag.getKey()).meters().stream().filter(this.requiredMeterType::isInstance).map(m -> m.getId().getTag(requiredTag.getKey())).distinct().sorted().collect(Collectors.toList());
                    if (nonMatchingValues.isEmpty()) {
                        details.add("FAIL: No meters have the required tag '" + requiredTag.getKey() + "'.");
                        continue;
                    }
                    if (nonMatchingValues.size() == 1) {
                        details.add("FAIL: No meters have " + requiredTagDetail + " The only value found was '" + (String)nonMatchingValues.iterator().next() + "'.");
                        continue;
                    }
                    details.add("FAIL: No meters have " + requiredTagDetail + " Tag values found were [" + nonMatchingValues.stream().map(v -> "'" + v + "'").collect(Collectors.joining(", ")) + "].");
                    continue;
                }
                if (matchingRequiredTag.size() == 1) {
                    details.add("OK: A meter with name '" + (String)matchingRequiredTag.iterator().next() + "' has " + requiredTagDetail);
                    continue;
                }
                details.add("OK: Meters with names [" + matchingRequiredTag.stream().map(name -> "'" + name + "'").collect(Collectors.joining(", ")) + "] have " + requiredTagDetail);
            }
            return details;
        }

        @Nullable
        private String typeDetail() {
            long count;
            Collection<Meter> matchesName;
            if (this.requiredMeterType.equals(Meter.class)) {
                return null;
            }
            if (this.search.nameMatches != null && !(matchesName = Search.in(this.search.registry).name(this.search.nameMatches).meters()).isEmpty()) {
                Collection nonMatchingTypes = matchesName.stream().filter(m -> !this.requiredMeterType.isInstance(m)).map(m -> this.meterTypeName(m.getClass())).distinct().sorted().collect(Collectors.toList());
                if (nonMatchingTypes.size() == 1) {
                    return "FAIL: Expected to find a " + this.meterTypeName(this.requiredMeterType) + ". The only type found was a " + (String)nonMatchingTypes.iterator().next() + ".";
                }
                if (nonMatchingTypes.size() > 1) {
                    return "FAIL: Expected to find a " + this.meterTypeName(this.requiredMeterType) + ". Types found were [" + nonMatchingTypes.stream().collect(Collectors.joining(", ")) + "].";
                }
            }
            if ((count = Search.in(this.search.registry).meters().stream().filter(this.requiredMeterType::isInstance).count()) == 0L) {
                return "FAIL: No meters with type " + this.meterTypeName(this.requiredMeterType) + " were found.";
            }
            if (count == 1L) {
                return "OK: A meter with type " + this.meterTypeName(this.requiredMeterType) + " was found.";
            }
            return "OK: Meters with type " + this.meterTypeName(this.requiredMeterType) + " were found.";
        }

        private String meterTypeName(Class<?> meterType) {
            if (Counter.class.isAssignableFrom(meterType)) {
                return "counter";
            }
            if (Gauge.class.isAssignableFrom(meterType)) {
                return "gauge";
            }
            if (LongTaskTimer.class.isAssignableFrom(meterType)) {
                return "long task timer";
            }
            if (Timer.class.isAssignableFrom(meterType)) {
                return "timer";
            }
            if (FunctionTimer.class.isAssignableFrom(meterType)) {
                return "function timer";
            }
            if (FunctionCounter.class.isAssignableFrom(meterType)) {
                return "function counter";
            }
            if (TimeGauge.class.isAssignableFrom(meterType)) {
                return "time gauge";
            }
            if (DistributionSummary.class.isAssignableFrom(meterType)) {
                return "distribution summary";
            }
            return meterType.getSimpleName();
        }

        private MeterNotFoundException build() {
            return new MeterNotFoundException(this.nameDetail(), this.typeDetail(), this.tagDetail());
        }
    }
}

