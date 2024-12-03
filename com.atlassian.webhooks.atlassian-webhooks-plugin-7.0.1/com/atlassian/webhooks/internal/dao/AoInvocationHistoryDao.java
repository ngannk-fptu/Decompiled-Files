/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.webhooks.history.DetailedInvocation
 *  com.atlassian.webhooks.history.DetailedInvocationError
 *  com.atlassian.webhooks.history.DetailedInvocationRequest
 *  com.atlassian.webhooks.history.DetailedInvocationResponse
 *  com.atlassian.webhooks.history.DetailedInvocationResult
 *  com.atlassian.webhooks.history.InvocationCounts
 *  com.atlassian.webhooks.history.InvocationOutcome
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  net.java.ao.EntityStreamCallback
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.internal.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.webhooks.history.DetailedInvocation;
import com.atlassian.webhooks.history.DetailedInvocationError;
import com.atlassian.webhooks.history.DetailedInvocationRequest;
import com.atlassian.webhooks.history.DetailedInvocationResponse;
import com.atlassian.webhooks.history.DetailedInvocationResult;
import com.atlassian.webhooks.history.InvocationCounts;
import com.atlassian.webhooks.history.InvocationOutcome;
import com.atlassian.webhooks.internal.dao.InvocationHistoryDao;
import com.atlassian.webhooks.internal.dao.ao.AoDailyInvocationCounts;
import com.atlassian.webhooks.internal.dao.ao.AoHistoricalInvocation;
import com.atlassian.webhooks.internal.history.SimpleInvocationCounts;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AoInvocationHistoryDao
implements InvocationHistoryDao {
    @VisibleForTesting
    static final int DEFAULT_MAX_STRING_LENGTH = 255;
    private static final long DAY_AS_MS = TimeUnit.DAYS.toMillis(1L);
    private static final String COUNT_COLS = String.join((CharSequence)",", "ID", "EVENT_ID", "ERRORS", "FAILURES", "SUCCESSES", "WEBHOOK_ID");
    private static final String SIMPLE_INVOCATION_COLS = String.join((CharSequence)",", "ID", "EVENT_ID", "FINISH", "OUTCOME", "REQUEST_ID", "REQUEST_METHOD", "REQUEST_URL", "RESULT_DESCRIPTION", "START", "WEBHOOK_ID");
    private static final Logger log = LoggerFactory.getLogger(AoInvocationHistoryDao.class);
    private final ActiveObjects ao;
    private final Clock clock;

    public AoInvocationHistoryDao(@Nonnull ActiveObjects ao) {
        this(ao, Clock.systemDefaultZone());
    }

    AoInvocationHistoryDao(ActiveObjects ao, Clock clock) {
        this.ao = ao;
        this.clock = clock;
    }

    @Override
    public void addCounts(int webhookId, @Nonnull String eventId, @Nonnull Date date, int errors, int failures, int successes) {
        Objects.requireNonNull(eventId, "eventId");
        Objects.requireNonNull(date, "date");
        long daysSinceEpoch = date.getTime() / DAY_AS_MS;
        this.updateCount(webhookId, eventId, daysSinceEpoch, errors, failures, successes);
    }

    @Override
    @Nonnull
    public Map<String, String> decodeHeaders(String id, String headersString) {
        if (StringUtils.isBlank((CharSequence)headersString)) {
            return Collections.emptyMap();
        }
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(headersString));
            return Maps.fromProperties((Properties)properties);
        }
        catch (IOException e) {
            log.debug("Failed to parse headers for invocation {}", (Object)id, (Object)e);
            return Collections.emptyMap();
        }
    }

    @Override
    public int deleteDailyCountsOlderThan(int days) {
        long daysSinceEpoch = this.clock.millis() / DAY_AS_MS;
        long oldestToKeep = daysSinceEpoch - (long)days;
        return this.ao.deleteWithSQL(AoDailyInvocationCounts.class, "DAY_SINCE_EPOCH < ?", new Object[]{oldestToKeep});
    }

    @Override
    public void deleteForWebhook(int webhookId) {
        this.ao.deleteWithSQL(AoDailyInvocationCounts.class, "WEBHOOK_ID = ?", new Object[]{webhookId});
    }

    @Override
    @Nonnull
    public InvocationCounts getCounts(int webhookId, String eventId, int days) {
        AoDailyInvocationCounts[] counts;
        Object[] params;
        long daysSinceEpoch = this.clock.millis() / DAY_AS_MS;
        long firstDay = daysSinceEpoch - (long)days;
        String whereClause = "WEBHOOK_ID = ? AND DAY_SINCE_EPOCH >= ?";
        if (eventId == null) {
            params = new Object[]{webhookId, firstDay};
        } else {
            whereClause = whereClause + " AND EVENT_ID = ?";
            params = new Object[]{webhookId, firstDay, AoInvocationHistoryDao.lower(eventId)};
        }
        int errors = 0;
        int failures = 0;
        int successes = 0;
        for (AoDailyInvocationCounts count : counts = (AoDailyInvocationCounts[])this.ao.find(AoDailyInvocationCounts.class, Query.select().where(whereClause, params))) {
            errors += count.getErrors();
            failures += count.getFailures();
            successes += count.getSuccesses();
        }
        return new SimpleInvocationCounts(Duration.of(days, ChronoUnit.DAYS), errors, failures, successes);
    }

    @Override
    @Nonnull
    public Map<String, InvocationCounts> getCountsByEvent(int webhookId, @Nonnull Collection<String> eventIds, int days) {
        long daysSinceEpoch = this.clock.millis() / DAY_AS_MS;
        long firstDay = daysSinceEpoch - (long)days;
        StringBuilder queryBuilder = new StringBuilder("WEBHOOK_ID").append(" = ? AND ").append("DAY_SINCE_EPOCH").append(" >= ?");
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(webhookId);
        params.add(firstDay);
        AoInvocationHistoryDao.addEventClause(queryBuilder, params, eventIds);
        CountsByEventCallback callback = new CountsByEventCallback(Duration.of(days, ChronoUnit.DAYS), eventIds);
        this.ao.stream(AoDailyInvocationCounts.class, Query.select((String)COUNT_COLS).where(queryBuilder.toString(), params.toArray()).order("EVENT_ID ASC"), (EntityStreamCallback)callback);
        callback.onDone();
        return callback.getResult();
    }

    @Override
    @Nonnull
    public Map<Integer, InvocationCounts> getCountsByWebhook(@Nonnull Collection<Integer> webhookIds, int days) {
        long daysSinceEpoch = this.clock.millis() / DAY_AS_MS;
        long firstDay = daysSinceEpoch - (long)days;
        String query = "DAY_SINCE_EPOCH >= ? AND WEBHOOK_ID" + AoInvocationHistoryDao.inClauseWithPlaceholders(webhookIds);
        ArrayList<Number> params = new ArrayList<Number>();
        params.add(firstDay);
        params.addAll(webhookIds);
        CountsByWebhookCallback callback = new CountsByWebhookCallback(Duration.of(days, ChronoUnit.DAYS), webhookIds);
        this.ao.stream(AoDailyInvocationCounts.class, Query.select((String)COUNT_COLS).where(query, params.toArray()).order("WEBHOOK_ID ASC"), (EntityStreamCallback)callback);
        callback.onDone();
        return callback.getResult();
    }

    @Override
    public AoHistoricalInvocation getLatestInvocation(int webhookId, String eventId, Collection<InvocationOutcome> outcomes) {
        ArrayList<Object> arguments = new ArrayList<Object>();
        StringBuilder queryBuilder = new StringBuilder("WEBHOOK_ID = ?");
        arguments.add(webhookId);
        AoInvocationHistoryDao.addOutcomeClause(queryBuilder, arguments, outcomes);
        AoInvocationHistoryDao.addEventClause(queryBuilder, arguments, eventId);
        AoHistoricalInvocation[] result = (AoHistoricalInvocation[])this.ao.find(AoHistoricalInvocation.class, Query.select().where(queryBuilder.toString(), arguments.toArray()).order("FINISH DESC").limit(1));
        return result == null || result.length == 0 ? null : result[0];
    }

    @Override
    @Nonnull
    public List<AoHistoricalInvocation> getLatestInvocations(int webhookId, String eventId, Collection<InvocationOutcome> outcomes) {
        ArrayList<Object> arguments = new ArrayList<Object>();
        StringBuilder queryBuilder = new StringBuilder("WEBHOOK_ID = ?");
        arguments.add(webhookId);
        AoInvocationHistoryDao.addOutcomeClause(queryBuilder, arguments, outcomes);
        AoInvocationHistoryDao.addEventClause(queryBuilder, arguments, eventId);
        Object[] result = (AoHistoricalInvocation[])this.ao.find(AoHistoricalInvocation.class, Query.select((String)SIMPLE_INVOCATION_COLS).where(queryBuilder.toString(), arguments.toArray()).limit(InvocationOutcome.values().length));
        return result == null ? Collections.emptyList() : ImmutableList.copyOf((Object[])result);
    }

    @Override
    @Nonnull
    public Multimap<String, AoHistoricalInvocation> getLatestInvocationsByEvent(int webhookId, @Nonnull Collection<String> eventIds) {
        ArrayList<Object> arguments = new ArrayList<Object>();
        StringBuilder queryBuilder = new StringBuilder("WEBHOOK_ID = ?");
        arguments.add(webhookId);
        AoInvocationHistoryDao.addEventClause(queryBuilder, arguments, eventIds);
        InvocationByEventCallback callback = new InvocationByEventCallback(eventIds);
        this.ao.stream(AoHistoricalInvocation.class, Query.select((String)SIMPLE_INVOCATION_COLS).where(queryBuilder.toString(), arguments.toArray()), (EntityStreamCallback)callback);
        return callback.getResult();
    }

    @Override
    @Nonnull
    public Multimap<Integer, AoHistoricalInvocation> getLatestInvocationsByWebhook(@Nonnull Collection<Integer> ids) {
        InvocationByWebhookCallback callback = new InvocationByWebhookCallback();
        this.ao.stream(AoHistoricalInvocation.class, Query.select((String)SIMPLE_INVOCATION_COLS).where("WEBHOOK_ID" + AoInvocationHistoryDao.inClauseWithPlaceholders(ids), ids.toArray()), (EntityStreamCallback)callback);
        return callback.getResult();
    }

    @Override
    public void saveInvocation(int webhookId, @Nonnull DetailedInvocation invocation) {
        InvocationOutcome outcome = invocation.getResult().getOutcome();
        DetailedInvocationRequest request = invocation.getRequest();
        DetailedInvocationResult result = invocation.getResult();
        String id = AoInvocationHistoryDao.invocationPk(webhookId, invocation.getEvent().getId(), outcome);
        AoHistoricalInvocation current = (AoHistoricalInvocation)this.ao.get(AoHistoricalInvocation.class, (Object)id);
        if (current != null && current.getFinish() > invocation.getFinish().toEpochMilli()) {
            return;
        }
        ImmutableMap.Builder builder = ImmutableMap.builder().put((Object)"ID", (Object)id).put((Object)"EVENT_ID", (Object)AoInvocationHistoryDao.lower(invocation.getEvent().getId())).put((Object)"FINISH", (Object)invocation.getFinish().toEpochMilli()).put((Object)"OUTCOME", (Object)outcome).put((Object)"REQUEST_ID", (Object)invocation.getId()).put((Object)"REQUEST_METHOD", (Object)request.getMethod().name()).put((Object)"REQUEST_URL", (Object)AoInvocationHistoryDao.sanitise(request.getUrl())).put((Object)"RESULT_DESCRIPTION", (Object)AoInvocationHistoryDao.sanitise(result.getDescription())).put((Object)"START", (Object)invocation.getStart().toEpochMilli()).put((Object)"WEBHOOK_ID", (Object)webhookId);
        AoInvocationHistoryDao.maybePut((ImmutableMap.Builder<String, Object>)builder, "REQUEST_BODY", request.getBody().orElse(null));
        AoInvocationHistoryDao.maybePut((ImmutableMap.Builder<String, Object>)builder, "REQUEST_HEADERS", AoInvocationHistoryDao.encodeHeaders(request.getHeaders()));
        if (result instanceof DetailedInvocationError) {
            AoInvocationHistoryDao.maybePut((ImmutableMap.Builder<String, Object>)builder, "ERROR_CONTENT", ((DetailedInvocationError)result).getContent());
        } else if (result instanceof DetailedInvocationResponse) {
            DetailedInvocationResponse response = (DetailedInvocationResponse)result;
            AoInvocationHistoryDao.maybePut((ImmutableMap.Builder<String, Object>)builder, "RESPONSE_BODY", response.getBody().orElse(null));
            AoInvocationHistoryDao.maybePut((ImmutableMap.Builder<String, Object>)builder, "RESPONSE_HEADERS", AoInvocationHistoryDao.encodeHeaders(response.getHeaders()));
            builder.put((Object)"STATUS_CODE", (Object)response.getStatusCode());
        }
        if (current != null) {
            this.ao.delete(new RawEntity[]{current});
        }
        this.ao.create(AoHistoricalInvocation.class, (Map)builder.build());
    }

    private static void addEventClause(StringBuilder queryBuilder, List<Object> arguments, String eventId) {
        if (StringUtils.isBlank((CharSequence)eventId)) {
            return;
        }
        queryBuilder.append(" AND ").append("EVENT_ID").append(" = ?");
        arguments.add(AoInvocationHistoryDao.lower(eventId));
    }

    private static void addEventClause(StringBuilder queryBuilder, List<Object> arguments, Collection<String> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return;
        }
        queryBuilder.append(" AND ").append("EVENT_ID").append(AoInvocationHistoryDao.inClauseWithPlaceholders(eventIds));
        eventIds.forEach(eventId -> arguments.add(AoInvocationHistoryDao.lower(eventId)));
    }

    private static void addOutcomeClause(StringBuilder queryBuilder, List<Object> arguments, Collection<InvocationOutcome> outcomes) {
        if (outcomes == null || outcomes.isEmpty()) {
            return;
        }
        queryBuilder.append(" AND ").append("OUTCOME").append(AoInvocationHistoryDao.inClauseWithPlaceholders(outcomes));
        arguments.addAll(outcomes);
    }

    private static String countsPk(int webhookId, String eventId, long daysSinceEpoch) {
        return webhookId + "." + AoInvocationHistoryDao.lower(eventId) + "." + daysSinceEpoch;
    }

    private static String encodeHeaders(Map<String, String> headers) {
        Properties properties = new Properties();
        properties.putAll(headers);
        StringWriter writer = new StringWriter();
        try {
            int eol;
            properties.store(writer, null);
            String encoded = writer.toString();
            if (encoded.startsWith("#") && (eol = encoded.indexOf(10)) > 0 && eol + 1 < encoded.length()) {
                encoded = encoded.substring(eol + 1);
            }
            return encoded;
        }
        catch (IOException e) {
            return null;
        }
    }

    private static String inClauseWithPlaceholders(Collection<?> items) {
        return " IN (" + items.stream().map(item -> "?").collect(Collectors.joining(", ")) + ")";
    }

    private static String invocationPk(int webhookId, String eventId, InvocationOutcome outcome) {
        return webhookId + "." + AoInvocationHistoryDao.lower(eventId) + "." + outcome.name().substring(0, 1);
    }

    private static String lower(String value) {
        return StringUtils.lowerCase((String)value, (Locale)Locale.ROOT);
    }

    private static void maybePut(ImmutableMap.Builder<String, Object> builder, String key, String value) {
        String trimmed = StringUtils.trimToNull((String)value);
        if (trimmed != null) {
            builder.put((Object)key, (Object)value);
        }
    }

    private static String sanitise(String details) {
        if (StringUtils.length((CharSequence)details) > 255) {
            log.trace("Truncating to {} chars: {}", (Object)255, (Object)details);
            return StringUtils.substring((String)details, (int)0, (int)255);
        }
        return details;
    }

    private void updateCount(int webhookId, String eventId, long daysSinceEpoch, int errors, int failures, int successes) {
        String dailyCountId = AoInvocationHistoryDao.countsPk(webhookId, eventId, daysSinceEpoch);
        this.ao.executeInTransaction(() -> {
            int updatedErrors = errors;
            int updatedFailures = failures;
            int updatedSuccesses = successes;
            AoDailyInvocationCounts current = (AoDailyInvocationCounts)this.ao.get(AoDailyInvocationCounts.class, (Object)dailyCountId);
            if (current != null) {
                updatedErrors += current.getErrors();
                updatedFailures += current.getFailures();
                updatedSuccesses += current.getSuccesses();
                this.ao.delete(new RawEntity[]{current});
            }
            this.ao.create(AoDailyInvocationCounts.class, (Map)ImmutableMap.builder().put((Object)"ID", (Object)dailyCountId).put((Object)"DAY_SINCE_EPOCH", (Object)daysSinceEpoch).put((Object)"ERRORS", (Object)updatedErrors).put((Object)"EVENT_ID", (Object)AoInvocationHistoryDao.lower(eventId)).put((Object)"FAILURES", (Object)updatedFailures).put((Object)"SUCCESSES", (Object)updatedSuccesses).put((Object)"WEBHOOK_ID", (Object)webhookId).build());
            return null;
        });
    }

    private static class InvocationByWebhookCallback
    implements EntityStreamCallback<AoHistoricalInvocation, String> {
        private final ListMultimap<Integer, AoHistoricalInvocation> result = ArrayListMultimap.create();

        private InvocationByWebhookCallback() {
        }

        public void onRowRead(AoHistoricalInvocation invocation) {
            this.result.put((Object)invocation.getWebhookId(), (Object)invocation);
        }

        Multimap<Integer, AoHistoricalInvocation> getResult() {
            return this.result;
        }
    }

    private static class InvocationByEventCallback
    implements EntityStreamCallback<AoHistoricalInvocation, String> {
        private final Map<String, String> lowerToRequestedEventId;
        private final ListMultimap<String, AoHistoricalInvocation> result;

        private InvocationByEventCallback(Collection<String> eventIds) {
            this.lowerToRequestedEventId = eventIds.stream().collect(Collectors.toMap(x$0 -> AoInvocationHistoryDao.lower(x$0), Function.identity()));
            this.result = ArrayListMultimap.create();
        }

        public void onRowRead(AoHistoricalInvocation invocation) {
            this.result.put((Object)this.lowerToRequestedEventId.get(invocation.getEventId()), (Object)invocation);
        }

        Multimap<String, AoHistoricalInvocation> getResult() {
            return this.result;
        }
    }

    private static class CountsByWebhookCallback
    implements EntityStreamCallback<AoDailyInvocationCounts, String> {
        private final Duration duration;
        private final Collection<Integer> ids;
        private final Map<Integer, InvocationCounts> result;
        private int errors;
        private int failures;
        private int successes;
        private Integer currentId;

        private CountsByWebhookCallback(Duration duration, Collection<Integer> webhookIds) {
            this.duration = duration;
            this.ids = webhookIds;
            this.result = new HashMap<Integer, InvocationCounts>(webhookIds.size());
        }

        public void onRowRead(AoDailyInvocationCounts count) {
            Integer webhookId = count.getWebhookId();
            if (!webhookId.equals(this.currentId)) {
                this.popCurrentItem();
                this.successes = 0;
                this.failures = 0;
                this.errors = 0;
                this.currentId = webhookId;
            }
            this.errors += count.getErrors();
            this.failures += count.getFailures();
            this.successes += count.getSuccesses();
        }

        void onDone() {
            this.popCurrentItem();
            SimpleInvocationCounts empty = new SimpleInvocationCounts(this.duration, 0, 0, 0);
            this.ids.forEach(webhookId -> this.result.putIfAbsent((Integer)webhookId, empty));
        }

        Map<Integer, InvocationCounts> getResult() {
            return this.result;
        }

        private void popCurrentItem() {
            if (this.currentId != null) {
                this.result.put(this.currentId, new SimpleInvocationCounts(this.duration, this.errors, this.failures, this.successes));
            }
            this.currentId = null;
        }
    }

    private static class CountsByEventCallback
    implements EntityStreamCallback<AoDailyInvocationCounts, String> {
        private final Duration duration;
        private final Map<String, String> lowerToRequestedEventId;
        private final Map<String, InvocationCounts> result;
        private int errors;
        private int failures;
        private int successes;
        private String currentEvent;

        private CountsByEventCallback(Duration duration, Collection<String> eventIds) {
            this.duration = duration;
            this.lowerToRequestedEventId = eventIds.stream().collect(Collectors.toMap(x$0 -> AoInvocationHistoryDao.lower(x$0), Function.identity()));
            this.result = new HashMap<String, InvocationCounts>(eventIds.size());
        }

        public void onRowRead(AoDailyInvocationCounts count) {
            String eventId = count.getEventId();
            if (!eventId.equals(this.currentEvent)) {
                this.popCurrentEvent();
                this.successes = 0;
                this.failures = 0;
                this.errors = 0;
                this.currentEvent = eventId;
            }
            this.errors += count.getErrors();
            this.failures += count.getFailures();
            this.successes += count.getSuccesses();
        }

        void onDone() {
            this.popCurrentEvent();
            SimpleInvocationCounts empty = new SimpleInvocationCounts(this.duration, 0, 0, 0);
            this.lowerToRequestedEventId.values().forEach(eventId -> this.result.putIfAbsent((String)eventId, empty));
        }

        Map<String, InvocationCounts> getResult() {
            return this.result;
        }

        private void popCurrentEvent() {
            if (this.currentEvent != null) {
                this.result.put(this.lowerToRequestedEventId.get(this.currentEvent), new SimpleInvocationCounts(this.duration, this.errors, this.failures, this.successes));
            }
            this.currentEvent = null;
        }
    }
}

