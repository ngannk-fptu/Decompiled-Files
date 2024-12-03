/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.map.MappingJsonFactory
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.joda.time.DateTime
 *  org.joda.time.Duration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.request;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import com.atlassian.upm.impl.Locks;
import com.atlassian.upm.request.PluginRequest;
import com.atlassian.upm.request.PluginRequestFactory;
import com.atlassian.upm.request.PluginRequestStore;
import com.atlassian.upm.request.rest.representations.PluginRequestRepresentation;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import com.atlassian.upm.schedule.UpmScheduler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginSettingsPluginRequestStore
implements PluginRequestStore {
    public static final String KEY_PREFIX = PluginSettingsPluginRequestStore.class.getName() + ":requests:";
    private static final String REQUEST_KEY = "requests_v2";
    private static final Logger log = LoggerFactory.getLogger((String)PluginSettingsPluginRequestStore.class.getName());
    private final ClusterLock lock;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final PluginRequestFactory requestFactory;
    private final UpmLinkBuilder linkBuilder;
    private final UpmUriBuilder uriBuilder;
    private final UserManager userManager;
    private final UpmScheduler scheduler;
    private final ObjectMapper mapper;
    private static final Comparator<Collection<PluginRequest>> pluginRequestCountComparator = (reqs1, reqs2) -> {
        if (reqs1.size() != reqs2.size()) {
            return reqs2.size() - reqs1.size();
        }
        PluginRequest req1 = (PluginRequest)reqs1.stream().findFirst().get();
        PluginRequest req2 = (PluginRequest)reqs2.stream().findFirst().get();
        return req1.getPluginName().toLowerCase().compareTo(req2.getPluginName().toLowerCase());
    };
    public static Comparator<PluginRequestRepresentation> requestComparator = Comparator.naturalOrder();
    private static Function<PluginRequest, String> requestToPluginKey = PluginRequest::getPluginKey;

    public PluginSettingsPluginRequestStore(PluginSettingsFactory pluginSettingsFactory, PluginRequestFactory requestFactory, UpmLinkBuilder linkBuilder, UpmUriBuilder uriBuilder, UserManager userManager, UpmScheduler scheduler, ClusterLockService lockService) {
        this(pluginSettingsFactory, requestFactory, linkBuilder, uriBuilder, userManager, scheduler, lockService, new ObjectMapper((JsonFactory)new MappingJsonFactory()));
    }

    public PluginSettingsPluginRequestStore(PluginSettingsFactory pluginSettingsFactory, PluginRequestFactory requestFactory, UpmLinkBuilder linkBuilder, UpmUriBuilder uriBuilder, UserManager userManager, UpmScheduler scheduler, ClusterLockService lockService, ObjectMapper mapper) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.requestFactory = Objects.requireNonNull(requestFactory, "requestFactory");
        this.linkBuilder = Objects.requireNonNull(linkBuilder, "linkBuilder");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.lock = Locks.getLock(Objects.requireNonNull(lockService, "lockService"), this.getClass());
    }

    private PluginSettings getPluginSettings() {
        return new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), KEY_PREFIX);
    }

    @Override
    public List<PluginRequest> getRequests() {
        return Locks.readWithLock(this.lock, this::getRequestsWithoutLocking);
    }

    private List<PluginRequest> getRequestsWithoutLocking() {
        List<PluginRequestRepresentation> reps = this.transformEntries(this.getSavedEntriesAsStrings());
        return reps.stream().map(this::toPluginRequest).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public Map<String, Collection<PluginRequest>> getRequestsByPlugin(Integer maxResults, Integer startIndex) {
        return Locks.readWithLock(this.lock, () -> this.sortRequestsByAmountAndPlugin(this.mapRequestsToPlugin(this.getRequestsWithoutLocking()), maxResults, startIndex));
    }

    @Override
    public Map<String, Collection<PluginRequest>> getRequestsByPluginExcludingUser(Integer maxResults, Integer startIndex, UserKey userKey) {
        return Locks.readWithLock(this.lock, () -> this.sortRequestsByAmountAndPlugin(this.mapRequestsToPlugin(this.getRequestsNotByUsersPlugins(userKey)), maxResults, startIndex));
    }

    private Map<String, Collection<PluginRequest>> sortRequestsByAmountAndPlugin(Map<String, Collection<PluginRequest>> requestsByPlugin, Integer maxResults, Integer startIndex) {
        List requestedPluginKeys = requestsByPlugin.values().stream().sorted(pluginRequestCountComparator).map(pluginRequests -> ((PluginRequest)pluginRequests.stream().findFirst().get()).getPluginKey()).collect(Collectors.toList());
        int start = startIndex == null ? 0 : startIndex;
        int max = maxResults == null ? requestedPluginKeys.size() : Math.min(maxResults + start, requestedPluginKeys.size());
        HashMap<String, Collection<PluginRequest>> sortedPluginRequests = new HashMap<String, Collection<PluginRequest>>();
        for (String requestedPluginKey : requestedPluginKeys.subList(start, max)) {
            sortedPluginRequests.put(requestedPluginKey, requestsByPlugin.get(requestedPluginKey));
        }
        return Collections.unmodifiableMap(sortedPluginRequests);
    }

    private List<PluginRequest> getRequestsNotByUsersPlugins(UserKey userKey) {
        List<PluginRequestRepresentation> storedRequests = this.transformEntries(this.getSavedEntriesAsStrings());
        Map userRequests = userKey != null ? this.getRequestsByUserInternal(userKey, storedRequests) : Collections.emptyMap();
        return storedRequests.stream().filter(this.withAnyPluginKey(userRequests.keySet()).negate()).map(this::toPluginRequest).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public Map<String, PluginRequest> getRequestsByUser(UserKey userKey) {
        return Locks.readWithLock(this.lock, () -> {
            if (userKey == null) {
                return Collections.emptyMap();
            }
            List<PluginRequestRepresentation> storedRequests = this.transformEntries(this.getSavedEntriesAsStrings());
            return this.getRequestsByUserInternal(userKey, storedRequests);
        });
    }

    private Map<String, PluginRequest> getRequestsByUserInternal(UserKey userKey, List<PluginRequestRepresentation> storedRequests) {
        HashMap userPluginRequests = new HashMap();
        storedRequests.stream().filter(this.byUser(userKey)).map(this::toPluginRequest).filter(Objects::nonNull).forEach(request -> userPluginRequests.put(request.getPluginKey(), request));
        return Collections.unmodifiableMap(userPluginRequests);
    }

    @Override
    public List<PluginRequest> getRequests(String pluginKey) {
        return Locks.readWithLock(this.lock, () -> {
            List<PluginRequestRepresentation> storedRequests = this.transformEntries(this.getSavedEntriesAsStrings());
            return Collections.unmodifiableList(storedRequests.stream().filter(this.withPluginKey(pluginKey)).map(this::toPluginRequest).filter(Objects::nonNull).collect(Collectors.toList()));
        });
    }

    @Override
    public Option<PluginRequest> getRequest(String pluginKey, UserKey userKey) {
        return Locks.readWithLock(this.lock, () -> {
            for (PluginRequestRepresentation rep : this.transformEntries(this.getSavedEntriesAsStrings())) {
                if (!rep.getPluginKey().equals(pluginKey) || !rep.getUser().getUserKey().equals(userKey.getStringValue())) continue;
                return Option.option(this.toPluginRequest(rep));
            }
            return Option.none(PluginRequest.class);
        });
    }

    @Override
    public void addRequest(PluginRequest request) {
        Locks.writeWithLock(this.lock, () -> {
            try {
                List<PluginRequestRepresentation> requests = this.transformEntries(this.getSavedEntriesAsStrings());
                List requestsRep = requests.stream().filter(this.equalToRequest(request).negate()).collect(Collectors.toList());
                requestsRep.add(new PluginRequestRepresentation(request.getPluginKey(), request, this.linkBuilder, this.uriBuilder));
                this.saveRequests(Collections.unmodifiableList(requestsRep));
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to save PluginRequest", e);
            }
        });
    }

    @Override
    public void removeRequests(String pluginKey) {
        Locks.writeWithLock(this.lock, () -> {
            try {
                List<PluginRequestRepresentation> requests = this.transformEntries(this.getSavedEntriesAsStrings());
                List<PluginRequestRepresentation> requestsRep = requests.stream().filter(this.withPluginKey(pluginKey).negate()).collect(Collectors.toList());
                this.saveRequests(requestsRep);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to save PluginRequest", e);
            }
        });
    }

    @Override
    public void removeAllRequests() {
        Locks.writeWithLock(this.lock, () -> this.saveRequests(Collections.emptyList()));
    }

    private Iterable<String> getSavedEntriesAsStrings() {
        Object entries = this.getPluginSettings().get(REQUEST_KEY);
        if (entries == null) {
            return Collections.emptyList();
        }
        if (!(entries instanceof List)) {
            log.error("Invalid plugin request has been detected: " + entries);
            this.saveRequests(Collections.emptyList());
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList((List)entries));
    }

    private List<PluginRequestRepresentation> transformEntries(Iterable<String> stringEntries) {
        return StreamSupport.stream(stringEntries.spliterator(), false).map(from -> {
            try {
                return (PluginRequestRepresentation)this.mapper.readValue(from, PluginRequestRepresentation.class);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to parse PluginRequestRepresentation from JSON string: " + from, e);
            }
        }).sorted(requestComparator).collect(Collectors.toList());
    }

    private void saveRequests(Iterable<PluginRequestRepresentation> stringEntries) {
        List entries = StreamSupport.stream(stringEntries.spliterator(), false).map(from -> {
            try {
                return this.mapper.writeValueAsString(from);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to save PluginRequestRepresentation to JSON: " + from, e);
            }
        }).collect(Collectors.toList());
        this.getPluginSettings().put(REQUEST_KEY, entries);
    }

    private void removeRequestsByUser(UserKey userKey) {
        Locks.writeWithLock(this.lock, () -> {
            try {
                List<PluginRequestRepresentation> requests = this.transformEntries(this.getSavedEntriesAsStrings());
                List<PluginRequestRepresentation> requestsRep = requests.stream().filter(this.byUser(userKey).negate()).collect(Collectors.toList());
                this.saveRequests(requestsRep);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to save PluginRequest", e);
            }
        });
    }

    private PluginRequest toPluginRequest(PluginRequestRepresentation pluginRequestRepresentation) {
        UserKey userKey = new UserKey(pluginRequestRepresentation.getUser().getUserKey());
        if (this.userManager.getUserProfile(userKey) == null) {
            Runnable cleanupTask = () -> this.removeRequestsByUser(userKey);
            this.scheduler.triggerRunnable(cleanupTask, Duration.ZERO, "PluginSettingsPluginRequestStore cleanup task");
            return null;
        }
        return this.requestFactory.getPluginRequest(userKey, pluginRequestRepresentation.getPluginKey(), pluginRequestRepresentation.getPluginName(), new DateTime((Object)pluginRequestRepresentation.getTimestamp()), Option.option(pluginRequestRepresentation.getMessage()));
    }

    private Predicate<PluginRequestRepresentation> equalToRequest(PluginRequest pluginRequest) {
        return rep -> rep.getPluginKey().equals(pluginRequest.getPluginKey()) && rep.getUser().getUserKey().equals(pluginRequest.getUser().getUserKey().getStringValue());
    }

    private Predicate<PluginRequestRepresentation> withPluginKey(String pluginKey) {
        return rep -> rep.getPluginKey().equals(pluginKey);
    }

    private Predicate<PluginRequestRepresentation> withAnyPluginKey(Set<String> pluginKeys) {
        return rep -> pluginKeys.contains(rep.getPluginKey());
    }

    private Predicate<PluginRequestRepresentation> byUser(UserKey userKey) {
        return rep -> rep.getUser().getUserKey().equals(userKey.getStringValue());
    }

    private Map<String, Collection<PluginRequest>> mapRequestsToPlugin(List<PluginRequest> requests) {
        return requests.stream().collect(Collectors.toMap(requestToPluginKey, Collections::singletonList, (l1, l2) -> Stream.of(l1, l2).flatMap(Collection::stream).collect(Collectors.toList())));
    }
}

