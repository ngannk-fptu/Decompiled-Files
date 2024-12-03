/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLink
 *  com.atlassian.user.User
 *  com.google.common.collect.Maps
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.admin;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.admin.AdminTasklistManager;
import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import com.atlassian.confluence.admin.criteria.IgnorableAdminTaskCriteria;
import com.atlassian.confluence.admin.tasks.AdminTask;
import com.atlassian.confluence.admin.tasks.AdminTaskConfig;
import com.atlassian.confluence.admin.tasks.AdminTaskData;
import com.atlassian.confluence.admin.tasks.DefaultAdminTaskConfig;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.model.WebLink;
import com.atlassian.user.User;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class DefaultAdminTasklistManager
implements AdminTasklistManager {
    static final BandanaContext ADMIN_TASK_DATA_CONTEXT = new ConfluenceBandanaContext(AdminTaskData.class.getName());
    private final BandanaManager bandanaManager;
    private final Map<String, AdminTaskConfig> adminTaskConfigs;
    private final boolean autoCompleteTasks;
    private final WebInterfaceManager webInterfaceManager;
    private final SettingsManager settingsManager;

    public DefaultAdminTasklistManager(BandanaManager bandanaManager, List<AdminTaskConfig> coreTasksConfigs, boolean autoCompleteTasks, SettingsManager settingsManager, WebInterfaceManager webInterfaceManager) {
        this.bandanaManager = bandanaManager;
        Map<String, AdminTaskConfig> stringAdminTaskConfigMap = DefaultAdminTasklistManager.asMap(coreTasksConfigs);
        this.adminTaskConfigs = stringAdminTaskConfigMap;
        this.autoCompleteTasks = autoCompleteTasks;
        this.settingsManager = settingsManager;
        this.webInterfaceManager = webInterfaceManager;
    }

    private static Map<String, AdminTaskConfig> asMap(Iterable<AdminTaskConfig> tasks) {
        LinkedHashMap tmpAdminTaskConfigs = Maps.newLinkedHashMap();
        for (AdminTaskConfig config : tasks) {
            tmpAdminTaskConfigs.put(config.getKey(), config);
        }
        return Collections.unmodifiableMap(tmpAdminTaskConfigs);
    }

    @Override
    public List<AdminTask> getAllTasks() {
        ArrayList<AdminTask> result = new ArrayList<AdminTask>();
        for (Map.Entry<String, AdminTaskConfig> entry : this.adminTaskConfigs.entrySet()) {
            AdminTaskData data = this.getAdminTaskData(entry.getKey());
            AdminTask task = new AdminTask(entry.getValue(), data);
            task = this.autocomplete(task);
            result.add(task);
        }
        result.addAll(this.getTasksFromPlugins());
        return result;
    }

    private List<AdminTask> getTasksFromPlugins() {
        ArrayList<AdminTask> list = new ArrayList<AdminTask>();
        List adminTasks = this.webInterfaceManager.getDisplayableItems("system.admin.tasks/general", new DefaultWebInterfaceContext().toMap());
        for (WebItemModuleDescriptor adminTask : adminTasks) {
            AdminTaskData data = this.getAdminTaskData(adminTask.getKey());
            AdminTaskConfig config = this.from(adminTask, this.settingsManager);
            AdminTask task = new AdminTask(config, data);
            this.autocomplete(task);
            list.add(task);
        }
        return list;
    }

    private AdminTask autocomplete(AdminTask task) {
        if (this.autoCompleteTasks && task.getHasSuccessCriteria()) {
            if (!task.getIsCompleted() && task.getIsCriteriaMet()) {
                task = this.markTaskComplete(task.getKey());
            } else if (task.getIsCompleted() && !task.getIsCriteriaMet()) {
                task = this.markTaskIncomplete(task.getKey());
            }
        }
        return task;
    }

    @Override
    public AdminTask markTaskComplete(String key) {
        return this.markTaskComplete(key, null);
    }

    @Override
    public AdminTask markTaskComplete(String key, User user) {
        AdminTaskConfig config = this.getTask(key);
        String username = null;
        String fullName = null;
        String signedOffValue = null;
        if (config == null) {
            return null;
        }
        AdminConfigurationCriteria criteria = config.getAdminConfigurationCriteria();
        if (user != null) {
            username = user.getName();
            fullName = user.getFullName();
        }
        if (criteria != null && !criteria.hasLiveValue()) {
            signedOffValue = criteria.getValue();
        }
        AdminTaskData data = new AdminTaskData(new Date(), username, fullName, signedOffValue);
        this.bandanaManager.setValue(ADMIN_TASK_DATA_CONTEXT, key, (Object)data);
        return new AdminTask(config, data);
    }

    private AdminTaskConfig getTask(String key) {
        AdminTaskConfig coreTask = this.adminTaskConfigs.get(key);
        if (coreTask != null) {
            return coreTask;
        }
        for (AdminTask task : this.getTasksFromPlugins()) {
            if (!key.equals(task.getKey())) continue;
            return task.getAdminTaskConfig();
        }
        return null;
    }

    public AdminTask markTaskIncomplete(String key) {
        AdminTaskConfig config = this.adminTaskConfigs.get(key);
        AdminTaskData data = new AdminTaskData();
        this.bandanaManager.setValue(ADMIN_TASK_DATA_CONTEXT, key, (Object)data);
        return new AdminTask(config, data);
    }

    private AdminTaskData getAdminTaskData(String key) {
        AdminTaskData data = (AdminTaskData)this.bandanaManager.getValue(ADMIN_TASK_DATA_CONTEXT, key);
        if (data == null) {
            data = new AdminTaskData();
        }
        return data;
    }

    private AdminTaskConfig from(final WebItemModuleDescriptor webItem, SettingsManager settingsManager) {
        HttpServletRequest req = ServletContextThreadLocal.getRequest();
        final String labelKey = webItem.getWebLabel().getKey();
        String completeKey = webItem.getCompleteKey();
        IgnorableAdminTaskCriteria adminConfigurationCriteria = new IgnorableAdminTaskCriteria(completeKey, settingsManager);
        WebLink link = webItem.getLink();
        String renderedUrl = link != null ? link.getDisplayableUrl(req, new DefaultWebInterfaceContext().toMap()) : null;
        return new DefaultAdminTaskConfig(completeKey, adminConfigurationCriteria, renderedUrl){

            @Override
            public String getTitleKey() {
                return labelKey;
            }

            @Override
            public String getDescriptionKey() {
                String key = webItem.getDescriptionKey();
                if (StringUtils.isNotBlank((CharSequence)key)) {
                    return key;
                }
                return webItem.getDescription();
            }
        };
    }
}

