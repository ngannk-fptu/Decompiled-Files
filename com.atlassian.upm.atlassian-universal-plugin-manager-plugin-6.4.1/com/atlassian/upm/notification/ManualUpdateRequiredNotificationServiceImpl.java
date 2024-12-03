/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserKey
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.MappingJsonFactory
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.joda.time.DateTime
 *  org.joda.time.Duration
 *  org.joda.time.ReadableDuration
 *  org.joda.time.ReadableInstant
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.notification;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.Pairs;
import com.atlassian.upm.UserSettings;
import com.atlassian.upm.UserSettingsStore;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import com.atlassian.upm.mail.EmailType;
import com.atlassian.upm.mail.ProductUserLists;
import com.atlassian.upm.mail.UpmMailSenderService;
import com.atlassian.upm.notification.ManualUpdateRequiredNotificationService;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualUpdateRequiredNotificationServiceImpl
implements ManualUpdateRequiredNotificationService {
    private static final int MAX_EMAILS = 3;
    private static final String KEY_PREFIX = ManualUpdateRequiredNotificationServiceImpl.class.getName();
    private static final String MANUAL_UPDATE_NOTIFICATIONS = "manual-update-notifications";
    private static final Logger log = LoggerFactory.getLogger(ManualUpdateRequiredNotificationServiceImpl.class);
    private final PluginRetriever pluginRetriever;
    private final UpmMailSenderService mailSenderService;
    private final UserSettingsStore userSettingsStore;
    private final ProductUserLists userLists;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ObjectMapper mapper;

    public ManualUpdateRequiredNotificationServiceImpl(PluginRetriever pluginRetriever, UpmMailSenderService mailSenderService, UserSettingsStore userSettingsStore, ProductUserLists userLists, PluginSettingsFactory pluginSettingsFactory) {
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.mailSenderService = Objects.requireNonNull(mailSenderService, "mailSenderService");
        this.userSettingsStore = Objects.requireNonNull(userSettingsStore, "userSettingsStore");
        this.userLists = Objects.requireNonNull(userLists, "userLists");
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.mapper = new ObjectMapper((JsonFactory)new MappingJsonFactory());
    }

    @Override
    public void sendFreeToPaidNotification(AvailableAddonWithVersion update) {
        this.sendManualUpdateEmail(update, EmailType.ADDON_UPDATE_FREE_TO_PAID, this.getPermittedUsers(EmailType.ADDON_UPDATE_FREE_TO_PAID), Collections.emptyMap());
    }

    @Override
    public void clearEmailRecords(String pluginKey) {
        this.saveEntries(this.getManualUpdateEmails().stream().filter(this.withPluginKey(pluginKey).negate()).collect(Collectors.toList()));
    }

    private Set<UserKey> getPermittedUsers(EmailType emailType) {
        switch (emailType) {
            case ADDON_UPDATE_FREE_TO_PAID: {
                return this.userLists.getSystemAdmins();
            }
        }
        return Collections.emptySet();
    }

    private void sendManualUpdateEmail(AvailableAddonWithVersion update, EmailType emailType, Set<UserKey> userKeys, Map<String, Object> context) {
        if (!this.mailSenderService.canSendEmail()) {
            return;
        }
        for (Plugin installedPlugin : this.pluginRetriever.getPlugin(update.getAddon().getKey())) {
            if (!this.shouldSendEmail(installedPlugin, emailType)) {
                return;
            }
            List recipients = userKeys.stream().filter(userKey -> !this.userSettingsStore.getBoolean((UserKey)userKey, UserSettings.DISABLE_EMAIL)).collect(Collectors.toList());
            for (UserKey userKey2 : recipients) {
                this.mailSenderService.sendUpmEmail(emailType, Pairs.ImmutablePair.pair(update.getAddon().getKey(), update.getAddon().getName()), Collections.singleton(userKey2), Collections.singletonList(update.getAddon().getName()), context);
            }
            if (recipients.isEmpty()) continue;
            this.recordEmail(installedPlugin, emailType);
        }
    }

    private boolean shouldSendEmail(Plugin plugin, EmailType emailType) {
        if (!plugin.isEnabled()) {
            return false;
        }
        List<ManualUpdateEmailRepresentation> emails = this.getEmailsFor(plugin, emailType);
        if (emails.size() >= 3) {
            return false;
        }
        if (emails.size() == 2) {
            return emails.stream().allMatch(this.olderThan(Duration.standardDays((long)3L)));
        }
        if (emails.size() == 1) {
            return emails.stream().allMatch(this.olderThan(Duration.standardDays((long)1L)));
        }
        return true;
    }

    private void recordEmail(Plugin plugin, EmailType emailType) {
        ArrayList<ManualUpdateEmailRepresentation> newList = new ArrayList<ManualUpdateEmailRepresentation>(this.getManualUpdateEmails());
        newList.add(new ManualUpdateEmailRepresentation(plugin.getKey(), plugin.getVersion(), emailType.toString(), new Date()));
        this.saveEntries(Collections.unmodifiableList(newList));
    }

    private List<ManualUpdateEmailRepresentation> getEmailsFor(Plugin plugin, EmailType emailType) {
        return this.getManualUpdateEmails().stream().filter(this.withPluginAndEmailType(plugin, emailType)).collect(Collectors.toList());
    }

    private List<ManualUpdateEmailRepresentation> getManualUpdateEmails() {
        Object entries = this.getPluginSettings().get(MANUAL_UPDATE_NOTIFICATIONS);
        if (entries == null) {
            return new ArrayList<ManualUpdateEmailRepresentation>();
        }
        if (!(entries instanceof List)) {
            log.error("Invalid emails storage has been detected: " + entries);
            this.saveEntries(Collections.emptyList());
            return new ArrayList<ManualUpdateEmailRepresentation>();
        }
        return ((List)entries).stream().map(this::toEmailRepresentation).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void saveEntries(List<ManualUpdateEmailRepresentation> manualUpdateEmails) {
        List transform = manualUpdateEmails.stream().map(this::fromEmailRepresentation).filter(Objects::nonNull).collect(Collectors.toList());
        this.getPluginSettings().put(MANUAL_UPDATE_NOTIFICATIONS, transform);
    }

    private PluginSettings getPluginSettings() {
        return new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), KEY_PREFIX);
    }

    private Predicate<ManualUpdateEmailRepresentation> withPluginAndEmailType(Plugin plugin, EmailType emailType) {
        return email -> email.getEmailType().equals(emailType.toString()) && email.getPluginKey().equals(plugin.getKey()) && email.getVersion().equals(plugin.getVersion());
    }

    private Predicate<ManualUpdateEmailRepresentation> withPluginKey(String pluginKey) {
        return email -> email.getPluginKey().equals(pluginKey);
    }

    private Predicate<? super ManualUpdateEmailRepresentation> olderThan(Duration duration) {
        return email -> new DateTime((Object)email.getDateSent()).isBefore((ReadableInstant)new DateTime().minus((ReadableDuration)duration));
    }

    private ManualUpdateEmailRepresentation toEmailRepresentation(String from) {
        try {
            return (ManualUpdateEmailRepresentation)this.mapper.readValue(from, ManualUpdateEmailRepresentation.class);
        }
        catch (IOException e) {
            log.warn("Failed to parse ManualUpdateEmailRepresentation from JSON string: " + from, (Throwable)e);
            return null;
        }
    }

    private String fromEmailRepresentation(ManualUpdateEmailRepresentation from) {
        try {
            return this.mapper.writeValueAsString((Object)from);
        }
        catch (IOException e) {
            log.warn("Failed to save ManualUpdateEmailRepresentation from JSON string: " + from, (Throwable)e);
            return null;
        }
    }

    public static final class ManualUpdateEmailRepresentation {
        @JsonProperty
        private final String pluginKey;
        @JsonProperty
        private final String version;
        @JsonProperty
        private final String emailType;
        @JsonProperty
        private final Date dateSent;

        @JsonCreator
        public ManualUpdateEmailRepresentation(@JsonProperty(value="pluginKey") String pluginKey, @JsonProperty(value="version") String version, @JsonProperty(value="emailType") String emailType, @JsonProperty(value="dateSent") Date dateSent) {
            this.pluginKey = pluginKey;
            this.version = version;
            this.emailType = emailType;
            this.dateSent = dateSent;
        }

        public String getPluginKey() {
            return this.pluginKey;
        }

        public String getVersion() {
            return this.version;
        }

        public String getEmailType() {
            return this.emailType;
        }

        public Date getDateSent() {
            return this.dateSent;
        }
    }
}

