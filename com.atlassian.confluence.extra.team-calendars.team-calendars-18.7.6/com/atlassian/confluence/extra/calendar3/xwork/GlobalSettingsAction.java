/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.setup.settings.ConfluenceFlavour
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskId
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskManager
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.confluence.extra.calendar3.xwork;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.PrivateCalendarUrlManager;
import com.atlassian.confluence.extra.calendar3.license.DCAwareLicenseVerifier;
import com.atlassian.confluence.extra.calendar3.license.LicenseAccessor;
import com.atlassian.confluence.extra.calendar3.upgrade.task.PasswordEncryptionTask;
import com.atlassian.confluence.extra.calendar3.util.EncryptKeyHolder;
import com.atlassian.confluence.setup.settings.ConfluenceFlavour;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.upm.license.compatibility.PluginLicenseManagerAccessor;
import com.atlassian.user.User;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@ReadOnlyAccessAllowed
@WebSudoRequired
public class GlobalSettingsAction
extends ConfluenceActionSupport {
    private static final String DEFAULT_START_OF_WEEK = "default";
    private PrivateCalendarUrlManager privateCalendarUrlManager;
    private PluginLicenseManagerAccessor pluginLicenseManagerAccessor;
    private CalendarSettingsManager calendarSettingsManager;
    private boolean subscriptionsReset;
    private boolean settingsUpdated;
    private boolean disablePrivateUrls;
    private boolean confluenceCloud;
    private long expireTime;
    private boolean siteAdminsEnabled;
    private boolean displayWeekNumber;
    private boolean excludeSubscriptionsFromContent;
    private String startOfWeek;
    private String displayTimeFormat;
    private String longRunTaskId;
    private LongRunningTaskManager longRunningTaskManager;
    private EncryptKeyHolder keyHolder;
    private ActiveObjectsServiceWrapper activeObjectsServiceWrapper;
    private TransactionTemplate transactionTemplate;
    private LicenseAccessor licenseAccessor;

    public LicenseAccessor getLicenseAccessor() {
        return this.licenseAccessor;
    }

    public void setLicenseAccessor(LicenseAccessor licenseAccessor) {
        this.licenseAccessor = licenseAccessor;
    }

    public TransactionTemplate getTransactionTemplate() {
        return this.transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public String getLongRunTaskId() {
        return this.longRunTaskId;
    }

    public void setLongRunTaskId(String longRunTaskId) {
        this.longRunTaskId = longRunTaskId;
    }

    public ActiveObjectsServiceWrapper getActiveObjectsServiceWrapper() {
        return this.activeObjectsServiceWrapper;
    }

    public void setActiveObjectsServiceWrapper(ActiveObjectsServiceWrapper activeObjectsServiceWrapper) {
        this.activeObjectsServiceWrapper = activeObjectsServiceWrapper;
    }

    public EncryptKeyHolder getKeyHolder() {
        return this.keyHolder;
    }

    public void setKeyHolder(EncryptKeyHolder keyHolder) {
        this.keyHolder = keyHolder;
    }

    public LongRunningTaskManager getLongRunningTaskManager() {
        return this.longRunningTaskManager;
    }

    public void setLongRunningTaskManager(LongRunningTaskManager longRunningTaskManager) {
        this.longRunningTaskManager = longRunningTaskManager;
    }

    public void setPluginLicenseManagerAccessor(PluginLicenseManagerAccessor pluginLicenseManagerAccessor) {
        this.pluginLicenseManagerAccessor = pluginLicenseManagerAccessor;
    }

    public void setPrivateCalendarUrlManager(PrivateCalendarUrlManager privateCalendarUrlManager) {
        this.privateCalendarUrlManager = privateCalendarUrlManager;
    }

    public void setCalendarSettingsManager(CalendarSettingsManager calendarSettingsManager) {
        this.calendarSettingsManager = calendarSettingsManager;
    }

    public boolean isSubscriptionsReset() {
        return this.subscriptionsReset;
    }

    public void setSubscriptionsReset(boolean subscriptionsReset) {
        this.subscriptionsReset = subscriptionsReset;
    }

    public boolean isSettingsUpdated() {
        return this.settingsUpdated;
    }

    public void setSettingsUpdated(boolean settingsUpdated) {
        this.settingsUpdated = settingsUpdated;
    }

    public boolean isUsingConfluenceDCLicense() {
        if (!(this.licenseAccessor instanceof DCAwareLicenseVerifier)) {
            return false;
        }
        DCAwareLicenseVerifier dcAwareLicenseVerifier = (DCAwareLicenseVerifier)((Object)this.licenseAccessor);
        return dcAwareLicenseVerifier.isUsingConfluenceDCLicense();
    }

    public boolean isConfluenceCloud() {
        this.confluenceCloud = false;
        if (ConfluenceFlavour.selected() != ConfluenceFlavour.VANILLA) {
            return true;
        }
        return this.confluenceCloud;
    }

    public long getCacheExpireTime() {
        return this.calendarSettingsManager.getCacheExpireTime();
    }

    public void setCacheExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isSiteAdminsEnabled() {
        return this.calendarSettingsManager.areSiteAdminsEnabled();
    }

    public boolean isDisplayWeekNumber() {
        return this.calendarSettingsManager.getDisplayWeekNumber();
    }

    public String encryptingExternalCalendarPassword() {
        LongRunningTaskId longRunningTaskId = this.getLongRunningTaskManager().startLongRunningTask((User)AuthenticatedUserThreadLocal.get(), (LongRunningTask)new PasswordEncryptionTask(this.transactionTemplate, this.getKeyHolder(), this.getActiveObjectsServiceWrapper().getActiveObjects()));
        this.setLongRunTaskId(longRunningTaskId.toString());
        return "success";
    }

    public boolean isProcessing() {
        return StringUtils.isNotEmpty((CharSequence)this.longRunTaskId);
    }

    public String resetAllSubscriptions() throws Exception {
        this.privateCalendarUrlManager.resetAllPrivateUrls();
        return "success";
    }

    public void setDisablePrivateUrls(boolean disablePrivateUrls) {
        this.disablePrivateUrls = disablePrivateUrls;
    }

    public void setExcludeSubscriptionsFromContent(boolean excludeSubscriptionsFromContent) {
        this.excludeSubscriptionsFromContent = excludeSubscriptionsFromContent;
    }

    public void setSiteAdminsEnabled(boolean siteAdminsEnabled) {
        this.siteAdminsEnabled = siteAdminsEnabled;
    }

    public void setDisplayWeekNumber(boolean displayWeekNumber) {
        this.displayWeekNumber = displayWeekNumber;
    }

    public void setDisplayTimeFormat(String displayTimeFormat) {
        this.displayTimeFormat = displayTimeFormat;
    }

    public String setGlobalSettings() {
        this.calendarSettingsManager.setCacheExpireTime(this.expireTime);
        this.calendarSettingsManager.setEnablePrivateUrls(!this.disablePrivateUrls);
        this.calendarSettingsManager.setDisplayTimeFormat(this.displayTimeFormat);
        this.calendarSettingsManager.setEnableSiteAdmins(this.siteAdminsEnabled);
        this.calendarSettingsManager.setDisplayWeekNumber(this.displayWeekNumber);
        this.calendarSettingsManager.setExcludeSubscriptionsFromContent(this.excludeSubscriptionsFromContent);
        this.storeStartOfWeek();
        return "success";
    }

    public boolean isPrivateUrlsDisabled() {
        return !this.calendarSettingsManager.arePrivateUrlsEnabled();
    }

    public boolean isExcludeSubscriptionsFromContent() {
        return this.calendarSettingsManager.isExcludeSubscriptionsFromContent();
    }

    public String getTimeFormat() {
        return this.calendarSettingsManager.getDisplayTimeFormat();
    }

    public boolean shouldDisplayLicenseTab() {
        return this.pluginLicenseManagerAccessor == null || !this.pluginLicenseManagerAccessor.isUpmPluginLicenseManagerResolved();
    }

    public boolean shouldDisplayUpm2Link() {
        return this.pluginLicenseManagerAccessor != null && this.pluginLicenseManagerAccessor.isUpmPluginLicenseManagerResolved();
    }

    public void setStartOfWeek(String startOfWeek) {
        this.startOfWeek = startOfWeek;
    }

    public String getStartOfWeek() {
        Integer startDay = this.calendarSettingsManager.getStartDayOfWeek();
        if (startDay == null) {
            return DEFAULT_START_OF_WEEK;
        }
        return startDay.toString();
    }

    public String storeStartOfWeek() {
        if (this.startOfWeek.equals(DEFAULT_START_OF_WEEK)) {
            this.calendarSettingsManager.setStartDayOfWeek(null);
        } else {
            this.calendarSettingsManager.setStartDayOfWeek(Integer.valueOf(this.startOfWeek));
        }
        return "success";
    }

    public Map<String, String> getDaysOfTheWeek() {
        return this.getDaysOfTheWeekLocalized();
    }

    private Map<String, String> getDaysOfTheWeekLocalized() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern((String)"EEEE").withLocale(this.getLocale());
        LinkedHashMap<String, String> dateTimes = new LinkedHashMap<String, String>();
        dateTimes.put(Integer.toString(7), this.getDayString(dateTimeFormatter, 7));
        dateTimes.put(Integer.toString(1), this.getDayString(dateTimeFormatter, 1));
        dateTimes.put(Integer.toString(2), this.getDayString(dateTimeFormatter, 2));
        dateTimes.put(Integer.toString(3), this.getDayString(dateTimeFormatter, 3));
        dateTimes.put(Integer.toString(4), this.getDayString(dateTimeFormatter, 4));
        dateTimes.put(Integer.toString(5), this.getDayString(dateTimeFormatter, 5));
        dateTimes.put(Integer.toString(6), this.getDayString(dateTimeFormatter, 6));
        return dateTimes;
    }

    private String getDayString(DateTimeFormatter dateTimeFormatter, int day) {
        return dateTimeFormatter.print((ReadableInstant)new DateTime(DateTimeZone.UTC).withDayOfWeek(day));
    }
}

