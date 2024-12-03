/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.atlassian.streams.api.ActivityObjectTypes
 *  com.atlassian.streams.api.ActivityRequest
 *  com.atlassian.streams.api.ActivityVerb
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsFilterType$Operator
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.common.Fold
 *  com.atlassian.streams.api.common.Function2
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.api.common.Pairs
 *  com.atlassian.streams.api.common.Predicates
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.ActivityObjectTypes;
import com.atlassian.streams.api.ActivityRequest;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Fold;
import com.atlassian.streams.api.common.Function2;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.api.common.Pairs;
import com.atlassian.streams.api.common.Predicates;
import com.atlassian.streams.spi.StandardStreamsFilterOption;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;

public final class Filters {
    private static Function<Long, Date> toDate = new Function<Long, Date>(){

        public Date apply(Long l) {
            return new Date(l);
        }
    };

    @Deprecated
    public static com.google.common.base.Predicate<String> isAndNot(Iterable<Pair<StreamsFilterType.Operator, Iterable<String>>> filters) {
        return Filters.isAndNot(filters, Filters.isIn(), Filters.notIn());
    }

    @Deprecated
    public static com.google.common.base.Predicate<String> isAndNot(Iterable<Pair<StreamsFilterType.Operator, Iterable<String>>> filters, Function<Iterable<String>, com.google.common.base.Predicate<String>> is, Function<Iterable<String>, com.google.common.base.Predicate<String>> not) {
        com.google.common.base.Predicate alwaysTrue = com.google.common.base.Predicates.alwaysTrue();
        if (filters == null) {
            return alwaysTrue;
        }
        return (com.google.common.base.Predicate)Fold.foldl(filters, (Object)alwaysTrue, (Function2)new IsAndNot(is, not));
    }

    public static Predicate<String> isAndNot(Iterable<Pair<StreamsFilterType.Operator, Iterable<String>>> filters, java.util.function.Function<Iterable<String>, com.google.common.base.Predicate<String>> is, java.util.function.Function<Iterable<String>, com.google.common.base.Predicate<String>> not) {
        com.google.common.base.Predicate alwaysTrue = com.google.common.base.Predicates.alwaysTrue();
        if (filters == null) {
            return alwaysTrue;
        }
        return (Predicate)Fold.foldl(filters, (Object)alwaysTrue, (Function2)new IsAndNot(is, not));
    }

    @Deprecated
    public static Function<Iterable<String>, com.google.common.base.Predicate<String>> isIn() {
        return IsIn.INSTANCE;
    }

    @Deprecated
    public static Function<Iterable<String>, com.google.common.base.Predicate<String>> notIn() {
        return NotIn.INSTANCE;
    }

    @Deprecated
    public static com.google.common.base.Predicate<String> containsAndDoesNotContain(Iterable<Pair<StreamsFilterType.Operator, Iterable<String>>> filters) {
        return Filters.containsAndDoesNotContain(filters, Filters.isContaining(), Filters.isNotContaining());
    }

    @Deprecated
    public static com.google.common.base.Predicate<String> containsAndDoesNotContain(Iterable<Pair<StreamsFilterType.Operator, Iterable<String>>> filters, Function<Iterable<String>, com.google.common.base.Predicate<String>> contains, Function<Iterable<String>, com.google.common.base.Predicate<String>> doesNotContain) {
        com.google.common.base.Predicate alwaysTrue = com.google.common.base.Predicates.alwaysTrue();
        if (filters == null) {
            return alwaysTrue;
        }
        return (com.google.common.base.Predicate)Fold.foldl(filters, (Object)alwaysTrue, (Function2)new ContainsAndDoesNotContain(contains, doesNotContain));
    }

    @Deprecated
    public static Function<Iterable<String>, com.google.common.base.Predicate<String>> isContaining() {
        return IsContaining.INSTANCE;
    }

    @Deprecated
    public static Function<Iterable<String>, com.google.common.base.Predicate<String>> isNotContaining() {
        return IsNotContaining.INSTANCE;
    }

    @Deprecated
    public static Function<Iterable<String>, com.google.common.base.Predicate<String>> caseInsensitive(Function<Iterable<String>, com.google.common.base.Predicate<String>> f) {
        return new AsCaseInsensitive(f);
    }

    @Deprecated
    public static com.google.common.base.Predicate<Iterable<String>> anyInUsers(ActivityRequest request) {
        if (Iterables.isEmpty(Filters.getIsValues(request.getStandardFilters().get((Object)StandardStreamsFilterOption.USER.getKey())))) {
            return com.google.common.base.Predicates.alwaysTrue();
        }
        return new AnyInUsers(request);
    }

    @Deprecated
    public static com.google.common.base.Predicate<String> inUsers(ActivityRequest request) {
        return Filters.isAndNot(request.getStandardFilters().get((Object)StandardStreamsFilterOption.USER.getKey()));
    }

    @Deprecated
    public static com.google.common.base.Predicate<Iterable<String>> notInUsers(ActivityRequest request) {
        return new NotInUsers(request);
    }

    @Deprecated
    public static com.google.common.base.Predicate<StreamsEntry> entryAuthors(com.google.common.base.Predicate<Iterable<String>> authorPredicate) {
        return new EntryAuthors(authorPredicate);
    }

    private static Function<UserProfile, String> getUsername() {
        return GetUsername.INSTANCE;
    }

    @Deprecated
    public static com.google.common.base.Predicate<String> inProjectKeys(ActivityRequest request) {
        return Filters.isAndNot(request.getStandardFilters().get((Object)"key"));
    }

    @Deprecated
    public static com.google.common.base.Predicate<String> inIssueKeys(ActivityRequest request) {
        return Filters.isAndNot(request.getStandardFilters().get((Object)StandardStreamsFilterOption.ISSUE_KEY.getKey()));
    }

    @Deprecated
    public static com.google.common.base.Predicate<String> inIssueKeys(ActivityRequest request, Function<Iterable<String>, com.google.common.base.Predicate<String>> is, Function<Iterable<String>, com.google.common.base.Predicate<String>> not) {
        return Filters.isAndNot((Iterable<Pair<StreamsFilterType.Operator, Iterable<String>>>)request.getStandardFilters().get((Object)StandardStreamsFilterOption.ISSUE_KEY.getKey()), is, not);
    }

    @Deprecated
    public static com.google.common.base.Predicate<Pair<ActivityObjectType, ActivityVerb>> inActivities(ActivityRequest request) {
        if (!Filters.providerFilterExists(request, "activity")) {
            return com.google.common.base.Predicates.alwaysTrue();
        }
        return new InActivities(request.getProviderFilters().get((Object)"activity"));
    }

    @Deprecated
    public static com.google.common.base.Predicate<Option<Pair<ActivityObjectType, ActivityVerb>>> inOptionActivities(ActivityRequest request) {
        if (!Filters.providerFilterExists(request, "activity")) {
            return com.google.common.base.Predicates.alwaysTrue();
        }
        return new InOptionActivities(request.getProviderFilters().get((Object)"activity"));
    }

    @Deprecated
    public static com.google.common.base.Predicate<StreamsEntry> entriesInActivities(ActivityRequest request) {
        if (!Filters.providerFilterExists(request, "activity")) {
            return com.google.common.base.Predicates.alwaysTrue();
        }
        return new EntriesInActivities(request.getProviderFilters().get((Object)"activity"));
    }

    private static boolean providerFilterExists(ActivityRequest request, String filterKey) {
        return Filters.filterExists((Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>>)request.getProviderFilters(), filterKey);
    }

    private static boolean filterExists(Multimap<String, Pair<StreamsFilterType.Operator, Iterable<String>>> filters, String filterKey) {
        return filters.get((Object)filterKey) != null && !filters.get((Object)filterKey).isEmpty();
    }

    public static Iterable<String> getAuthors(ActivityRequest request) {
        return Filters.getIsValues(request.getStandardFilters().get((Object)StandardStreamsFilterOption.USER.getKey()));
    }

    public static Iterable<String> getProjectKeys(ActivityRequest request) {
        return Filters.getIsValues(request.getStandardFilters().get((Object)"key"));
    }

    public static Iterable<String> getNotProjectKeys(ActivityRequest request) {
        return Filters.getNotValues(request.getStandardFilters().get((Object)"key"));
    }

    public static Iterable<String> getIssueKeys(ActivityRequest request) {
        return Filters.getIsValues(request.getStandardFilters().get((Object)StandardStreamsFilterOption.ISSUE_KEY.getKey()));
    }

    public static Iterable<String> getNotIssueKeys(ActivityRequest request) {
        return Filters.getNotValues(request.getStandardFilters().get((Object)StandardStreamsFilterOption.ISSUE_KEY.getKey()));
    }

    public static Option<Date> getMinDate(ActivityRequest request) {
        Collection filters = request.getStandardFilters().get((Object)StandardStreamsFilterOption.UPDATE_DATE.getKey());
        Option<Long> minDate = Filters.parseLongSafely(Filters.getFirstValue(StreamsFilterType.Operator.AFTER, filters));
        if (!minDate.isDefined() && Iterables.size((Iterable)filters) > 0) {
            return Filters.getDateRange(filters).map(Pairs.first());
        }
        return minDate.map(toDate);
    }

    public static Iterable<ActivityObjectType> getRequestedActivityObjectTypes(ActivityRequest request, Iterable<Pair<ActivityObjectType, ActivityVerb>> activities) {
        return Pairs.firsts((Iterable)Iterables.filter(activities, Filters.inActivities(request)));
    }

    @Deprecated
    public static Function<ActivityRequest, Iterable<Pair<ActivityObjectType, ActivityVerb>>> inSupportedActivities(final Iterable<Pair<ActivityObjectType, ActivityVerb>> supported) {
        return new Function<ActivityRequest, Iterable<Pair<ActivityObjectType, ActivityVerb>>>(){

            public Iterable<Pair<ActivityObjectType, ActivityVerb>> apply(ActivityRequest activityRequest) {
                return Iterables.filter((Iterable)supported, Filters.inActivities(activityRequest));
            }
        };
    }

    public static Option<Date> getMaxDate(ActivityRequest request) {
        Collection filters = request.getStandardFilters().get((Object)StandardStreamsFilterOption.UPDATE_DATE.getKey());
        Option<Long> maxDate = Filters.parseLongSafely(Filters.getFirstValue(StreamsFilterType.Operator.BEFORE, filters));
        if (!maxDate.isDefined() && Iterables.size((Iterable)filters) > 0) {
            return Filters.getDateRange(filters).map(Pairs.second());
        }
        return maxDate.map(toDate);
    }

    private static Option<Pair<Date, Date>> getDateRange(Collection<Pair<StreamsFilterType.Operator, Iterable<String>>> filters) {
        Pair firstFilter = (Pair)Iterables.get(filters, (int)0);
        Iterable dates = Iterables.transform((Iterable)((Iterable)firstFilter.second()), Filters.toDate());
        if (Iterables.size((Iterable)dates) < 2) {
            return Option.none();
        }
        Iterable ranges = Pairs.mkPairs((Iterable)dates);
        return Option.some((Object)Iterables.get((Iterable)ranges, (int)0));
    }

    private static Option<Long> parseLongSafely(Option<String> minDate) {
        Iterator iterator = minDate.iterator();
        if (iterator.hasNext()) {
            String min = (String)iterator.next();
            return Option.some((Object)Long.parseLong(min));
        }
        return Option.none();
    }

    @Deprecated
    public static com.google.common.base.Predicate<Date> inDateRange(ActivityRequest request) {
        com.google.common.base.Predicate alwaysTrue = com.google.common.base.Predicates.alwaysTrue();
        return (com.google.common.base.Predicate)Fold.foldl((Iterable)request.getStandardFilters().get((Object)StandardStreamsFilterOption.UPDATE_DATE.getKey()), (Object)alwaysTrue, (Function2)ContainsDate.INSTANCE);
    }

    public static Set<String> getIsValues(Iterable<Pair<StreamsFilterType.Operator, Iterable<String>>> filters) {
        Pair<Set<String>, Set<String>> isAndNotValues = Filters.getIsAndNotValues(filters);
        return Sets.difference((Set)((Set)isAndNotValues.first()), (Set)((Set)isAndNotValues.second()));
    }

    public static Set<String> getNotValues(Iterable<Pair<StreamsFilterType.Operator, Iterable<String>>> filters) {
        Pair<Set<String>, Set<String>> isAndNotValues = Filters.getIsAndNotValues(filters);
        return (Set)isAndNotValues.second();
    }

    public static Pair<Set<String>, Set<String>> getIsAndNotValues(Iterable<Pair<StreamsFilterType.Operator, Iterable<String>>> filters) {
        Pair emptySets = Pair.pair((Object)ImmutableSet.of(), (Object)ImmutableSet.of());
        return (Pair)Fold.foldl(filters, (Object)emptySets, Filters.extractIsAndNotValues());
    }

    private static Function2<Pair<StreamsFilterType.Operator, Iterable<String>>, Pair<Set<String>, Set<String>>, Pair<Set<String>, Set<String>>> extractIsAndNotValues() {
        return ExtractIsAndNotValues.INSTANCE;
    }

    @Deprecated
    public static Function<String, Date> toDate() {
        return ToDate.INSTANCE;
    }

    public static Date toDate(String date) {
        return new Date(Long.parseLong(date));
    }

    public static Iterable<String> getAllValues(StreamsFilterType.Operator op, Collection<Pair<StreamsFilterType.Operator, Iterable<String>>> filters) {
        return ((ImmutableList.Builder)Fold.foldl(filters, (Object)ImmutableList.builder(), (Function2)new BuildAllValuesWithOp(op))).build();
    }

    public static Option<String> getFirstValue(StreamsFilterType.Operator op, Iterable<Pair<StreamsFilterType.Operator, Iterable<String>>> filters) {
        try {
            Iterable values = (Iterable)((Pair)Iterables.find(filters, Filters.withOp(op))).second();
            if (!Iterables.isEmpty((Iterable)values)) {
                return Option.some((Object)Iterables.get((Iterable)values, (int)0));
            }
        }
        catch (NoSuchElementException noSuchElementException) {
            // empty catch block
        }
        return Option.none();
    }

    private static com.google.common.base.Predicate<Pair<StreamsFilterType.Operator, Iterable<String>>> withOp(StreamsFilterType.Operator op) {
        return new WithOperator(op);
    }

    private static final class WithOperator
    implements com.google.common.base.Predicate<Pair<StreamsFilterType.Operator, Iterable<String>>> {
        private final StreamsFilterType.Operator op;

        public WithOperator(StreamsFilterType.Operator op) {
            this.op = op;
        }

        public boolean apply(Pair<StreamsFilterType.Operator, Iterable<String>> input) {
            return ((StreamsFilterType.Operator)input.first()).equals((Object)this.op);
        }
    }

    public static final class BuildAllValuesWithOp
    implements Function2<Pair<StreamsFilterType.Operator, Iterable<String>>, ImmutableList.Builder<String>, ImmutableList.Builder<String>> {
        private final StreamsFilterType.Operator op;

        public BuildAllValuesWithOp(StreamsFilterType.Operator op) {
            this.op = op;
        }

        public ImmutableList.Builder<String> apply(Pair<StreamsFilterType.Operator, Iterable<String>> filter, ImmutableList.Builder<String> builder) {
            if (((StreamsFilterType.Operator)filter.first()).equals((Object)this.op)) {
                builder.addAll((Iterable)filter.second());
            }
            return builder;
        }
    }

    @Deprecated
    private static enum ToDate implements Function<String, Date>
    {
        INSTANCE;


        public Date apply(String date) {
            return new Date(Long.parseLong(date));
        }
    }

    private static enum ExtractIsAndNotValues implements Function2<Pair<StreamsFilterType.Operator, Iterable<String>>, Pair<Set<String>, Set<String>>, Pair<Set<String>, Set<String>>>
    {
        INSTANCE;


        public Pair<Set<String>, Set<String>> apply(Pair<StreamsFilterType.Operator, Iterable<String>> current, Pair<Set<String>, Set<String>> intermediate) {
            switch ((StreamsFilterType.Operator)current.first()) {
                case IS: {
                    return Pair.pair((Object)Sets.union((Set)((Set)intermediate.first()), (Set)ImmutableSet.copyOf((Iterable)((Iterable)current.second()))), (Object)intermediate.second());
                }
                case NOT: {
                    return Pair.pair((Object)intermediate.first(), (Object)Sets.union((Set)((Set)intermediate.second()), (Set)ImmutableSet.copyOf((Iterable)((Iterable)current.second()))));
                }
            }
            return intermediate;
        }
    }

    private static enum ToBetweenPredicate implements Function<Pair<Date, Date>, com.google.common.base.Predicate<Date>>
    {
        INSTANCE;


        public com.google.common.base.Predicate<Date> apply(Pair<Date, Date> range) {
            return new BetweenPredicate(range);
        }

        private static final class BetweenPredicate
        implements com.google.common.base.Predicate<Date> {
            private final Pair<Date, Date> range;

            public BetweenPredicate(Pair<Date, Date> range) {
                this.range = range;
            }

            public boolean apply(Date input) {
                return input.after((Date)this.range.first()) && input.before((Date)this.range.second());
            }
        }
    }

    private static enum ToAfterPredicate implements Function<Date, com.google.common.base.Predicate<Date>>
    {
        INSTANCE;


        public com.google.common.base.Predicate<Date> apply(Date date) {
            return new AfterPredicate(date);
        }

        private static final class AfterPredicate
        implements com.google.common.base.Predicate<Date> {
            private final Date date;

            public AfterPredicate(Date date) {
                this.date = date;
            }

            public boolean apply(Date input) {
                return input.after(this.date);
            }
        }
    }

    private static enum ToBeforePredicate implements Function<Date, com.google.common.base.Predicate<Date>>
    {
        INSTANCE;


        public com.google.common.base.Predicate<Date> apply(Date date) {
            return new BeforePredicate(date);
        }

        private static final class BeforePredicate
        implements com.google.common.base.Predicate<Date> {
            private final Date date;

            public BeforePredicate(Date date) {
                this.date = date;
            }

            public boolean apply(Date input) {
                return input.before(this.date);
            }
        }
    }

    @Deprecated
    private static enum ContainsDate implements Function2<Pair<StreamsFilterType.Operator, Iterable<String>>, com.google.common.base.Predicate<Date>, com.google.common.base.Predicate<Date>>
    {
        INSTANCE;


        public com.google.common.base.Predicate<Date> apply(Pair<StreamsFilterType.Operator, Iterable<String>> filter, com.google.common.base.Predicate<Date> predicate) {
            Iterable dates = Iterables.transform((Iterable)((Iterable)filter.second()), Filters.toDate());
            switch ((StreamsFilterType.Operator)filter.first()) {
                case BEFORE: {
                    return com.google.common.base.Predicates.and(predicate, (com.google.common.base.Predicate)com.google.common.base.Predicates.or((Iterable)Iterables.transform((Iterable)dates, (Function)ToBeforePredicate.INSTANCE)));
                }
                case AFTER: {
                    return com.google.common.base.Predicates.and(predicate, (com.google.common.base.Predicate)com.google.common.base.Predicates.or((Iterable)Iterables.transform((Iterable)dates, (Function)ToAfterPredicate.INSTANCE)));
                }
                case BETWEEN: {
                    Iterable ranges = Pairs.mkPairs((Iterable)dates);
                    return com.google.common.base.Predicates.and(predicate, (com.google.common.base.Predicate)com.google.common.base.Predicates.or((Iterable)Iterables.transform((Iterable)ranges, (Function)ToBetweenPredicate.INSTANCE)));
                }
            }
            return predicate;
        }
    }

    private static final class EntriesInActivities
    implements com.google.common.base.Predicate<StreamsEntry> {
        private final com.google.common.base.Predicate<Pair<ActivityObjectType, ActivityVerb>> inActivities;

        public EntriesInActivities(Collection<Pair<StreamsFilterType.Operator, Iterable<String>>> activities) {
            this.inActivities = new InActivities(activities);
        }

        public boolean apply(StreamsEntry entry) {
            return Iterables.any((Iterable)Iterables.transform((Iterable)ActivityObjectTypes.getActivityObjectTypes((Iterable)entry.getActivityObjects()), (Function)Pairs.pairWith((Object)entry.getVerb())), this.inActivities);
        }
    }

    private static final class InOptionActivities
    implements com.google.common.base.Predicate<Option<Pair<ActivityObjectType, ActivityVerb>>> {
        private final com.google.common.base.Predicate<String> inActivities;

        public InOptionActivities(Collection<Pair<StreamsFilterType.Operator, Iterable<String>>> activities) {
            this.inActivities = Filters.isAndNot(activities);
        }

        public boolean apply(Option<Pair<ActivityObjectType, ActivityVerb>> activity) {
            Iterator iterator = activity.iterator();
            if (iterator.hasNext()) {
                Pair activityPair = (Pair)iterator.next();
                return this.inActivities.apply((Object)(((ActivityObjectType)activityPair.first()).key() + ":" + ((ActivityVerb)activityPair.second()).key()));
            }
            return false;
        }

        public String toString() {
            return String.format("inOptionActivities(%s)", this.inActivities);
        }
    }

    private static final class InActivities
    implements com.google.common.base.Predicate<Pair<ActivityObjectType, ActivityVerb>> {
        private final com.google.common.base.Predicate<String> inActivities;

        public InActivities(Collection<Pair<StreamsFilterType.Operator, Iterable<String>>> activities) {
            this.inActivities = Filters.isAndNot(activities);
        }

        public boolean apply(Pair<ActivityObjectType, ActivityVerb> activity) {
            return this.inActivities.apply((Object)(((ActivityObjectType)activity.first()).key() + ":" + ((ActivityVerb)activity.second()).key()));
        }
    }

    private static enum GetUsername implements Function<UserProfile, String>
    {
        INSTANCE;


        public String apply(UserProfile profile) {
            return profile.getUsername();
        }
    }

    private static final class EntryAuthors
    implements com.google.common.base.Predicate<StreamsEntry> {
        private final com.google.common.base.Predicate<Iterable<String>> authorPredicate;

        public EntryAuthors(com.google.common.base.Predicate<Iterable<String>> authorPredicate) {
            this.authorPredicate = authorPredicate;
        }

        public boolean apply(StreamsEntry entry) {
            return this.authorPredicate.apply((Object)Iterables.transform((Iterable)entry.getAuthors(), (Function)Filters.getUsername()));
        }
    }

    private static final class NotInUsers
    implements com.google.common.base.Predicate<Iterable<String>> {
        private final Iterable<String> nottedUsers;

        public NotInUsers(ActivityRequest request) {
            this.nottedUsers = Filters.getAllValues(StreamsFilterType.Operator.NOT, request.getStandardFilters().get((Object)StandardStreamsFilterOption.USER.getKey()));
        }

        public boolean apply(Iterable<String> users) {
            return Sets.intersection((Set)ImmutableSet.copyOf(this.nottedUsers), (Set)ImmutableSet.copyOf(users)).isEmpty();
        }
    }

    private static final class AnyInUsers
    implements com.google.common.base.Predicate<Iterable<String>> {
        private final com.google.common.base.Predicate<String> inUsers;

        public AnyInUsers(ActivityRequest request) {
            this.inUsers = Filters.inUsers(request);
        }

        public boolean apply(Iterable<String> users) {
            return Iterables.any(users, this.inUsers);
        }
    }

    private static enum ToUpperCase implements Function<String, String>
    {
        INSTANCE;


        public String apply(String x) {
            return x.toUpperCase();
        }
    }

    @Deprecated
    private static final class AsCaseInsensitive
    implements Function<Iterable<String>, com.google.common.base.Predicate<String>> {
        private final Function<Iterable<String>, com.google.common.base.Predicate<String>> f;

        private AsCaseInsensitive(Function<Iterable<String>, com.google.common.base.Predicate<String>> f) {
            this.f = f;
        }

        public com.google.common.base.Predicate<String> apply(Iterable<String> xs) {
            return new CaseInsensitive(xs);
        }

        private final class CaseInsensitive
        implements com.google.common.base.Predicate<String> {
            private final com.google.common.base.Predicate<String> p;

            private CaseInsensitive(Iterable<String> xs) {
                this.p = (com.google.common.base.Predicate)AsCaseInsensitive.this.f.apply((Object)Iterables.transform(xs, (Function)ToUpperCase.INSTANCE));
            }

            public boolean apply(String x) {
                return this.p.apply((Object)x.toUpperCase());
            }
        }
    }

    @Deprecated
    private static final class ContainsAndDoesNotContain
    implements Function2<Pair<StreamsFilterType.Operator, Iterable<String>>, com.google.common.base.Predicate<String>, com.google.common.base.Predicate<String>> {
        private final Function<Iterable<String>, com.google.common.base.Predicate<String>> contains;
        private final Function<Iterable<String>, com.google.common.base.Predicate<String>> doesNotContain;

        ContainsAndDoesNotContain(Function<Iterable<String>, com.google.common.base.Predicate<String>> contains, Function<Iterable<String>, com.google.common.base.Predicate<String>> doesNotContain) {
            this.contains = contains;
            this.doesNotContain = doesNotContain;
        }

        public com.google.common.base.Predicate<String> apply(Pair<StreamsFilterType.Operator, Iterable<String>> filter, com.google.common.base.Predicate<String> predicate) {
            switch ((StreamsFilterType.Operator)filter.first()) {
                case CONTAINS: {
                    return com.google.common.base.Predicates.and(predicate, (com.google.common.base.Predicate)((com.google.common.base.Predicate)this.contains.apply(filter.second())));
                }
                case DOES_NOT_CONTAIN: {
                    return com.google.common.base.Predicates.and(predicate, (com.google.common.base.Predicate)((com.google.common.base.Predicate)this.doesNotContain.apply((Object)ImmutableSet.copyOf((Iterable)((Iterable)filter.second())))));
                }
            }
            return predicate;
        }
    }

    @Deprecated
    private static enum IsNotContaining implements Function<Iterable<String>, com.google.common.base.Predicate<String>>
    {
        INSTANCE;


        public com.google.common.base.Predicate<String> apply(Iterable<String> xs) {
            return com.google.common.base.Predicates.not(IsContaining.INSTANCE.apply(xs));
        }
    }

    @Deprecated
    private static enum IsContaining implements Function<Iterable<String>, com.google.common.base.Predicate<String>>
    {
        INSTANCE;


        public com.google.common.base.Predicate<String> apply(Iterable<String> xs) {
            return new ContainsPredicate(xs);
        }

        public String toString() {
            return "isContaining()";
        }

        private final class ContainsPredicate
        implements com.google.common.base.Predicate<String> {
            private final Iterable<String> xs;

            private ContainsPredicate(Iterable<String> xs) {
                this.xs = xs;
            }

            public boolean apply(String input) {
                if (input != null) {
                    for (String x : this.xs) {
                        if (!input.toLowerCase().contains(x.toLowerCase())) continue;
                        return true;
                    }
                }
                return false;
            }

            public String toString() {
                return "isContaining(" + Iterables.toString(this.xs) + ")";
            }
        }
    }

    private static final class IsAndNot
    implements Function2<Pair<StreamsFilterType.Operator, Iterable<String>>, com.google.common.base.Predicate<String>, com.google.common.base.Predicate<String>> {
        private final java.util.function.Function<Iterable<String>, com.google.common.base.Predicate<String>> is;
        private final java.util.function.Function<Iterable<String>, com.google.common.base.Predicate<String>> not;

        @Deprecated
        IsAndNot(Function<Iterable<String>, com.google.common.base.Predicate<String>> is, Function<Iterable<String>, com.google.common.base.Predicate<String>> not) {
            this.is = is;
            this.not = not;
        }

        IsAndNot(java.util.function.Function<Iterable<String>, com.google.common.base.Predicate<String>> is, java.util.function.Function<Iterable<String>, com.google.common.base.Predicate<String>> not) {
            this.is = is;
            this.not = not;
        }

        public com.google.common.base.Predicate<String> apply(Pair<StreamsFilterType.Operator, Iterable<String>> filter, com.google.common.base.Predicate<String> predicate) {
            switch ((StreamsFilterType.Operator)filter.first()) {
                case IS: {
                    return com.google.common.base.Predicates.and(predicate, this.is.apply((Iterable<String>)filter.second()));
                }
                case NOT: {
                    return com.google.common.base.Predicates.and(predicate, this.not.apply((Iterable<String>)ImmutableSet.copyOf((Iterable)((Iterable)filter.second()))));
                }
            }
            return predicate;
        }
    }

    @Deprecated
    private static enum NotIn implements Function<Iterable<String>, com.google.common.base.Predicate<String>>
    {
        INSTANCE;


        public com.google.common.base.Predicate<String> apply(Iterable<String> xs) {
            return com.google.common.base.Predicates.not(IsIn.INSTANCE.apply(xs));
        }
    }

    @Deprecated
    private static enum IsIn implements Function<Iterable<String>, com.google.common.base.Predicate<String>>
    {
        INSTANCE;


        public com.google.common.base.Predicate<String> apply(Iterable<String> xs) {
            return Predicates.contains(xs);
        }
    }
}

