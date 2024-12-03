/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.Timers
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.logging.admin;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeExecution;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.NoSuchClusterNodeException;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.logging.admin.LoggingConfigEntry;
import com.atlassian.confluence.impl.logging.admin.LoggingConfigService;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.user.User;
import com.atlassian.util.profiling.Timers;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class ConfigureLoggingAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(ConfigureLoggingAction.class);
    private static final Supplier<LoggingConfigService> loggingConfigServiceRef = new LazyComponentReference("loggingConfigService");
    private ClusterManager clusterManager;
    private String nodeId;
    private ClusterNodeInformation currentNode;
    private State state;
    private SaveLoggersTask saveLoggerLevelsTask = new SaveLoggersTask();
    private AddLoggerTask addLoggerTask = new AddLoggerTask();
    private ChangeProfilingTask changeProfilingTask = new ChangeProfilingTask();
    private DeleteLoggerTask deleteLoggerTask = new DeleteLoggerTask();
    private ChangeProfileTask changeProfileTask = new ChangeProfileTask();
    private static final String LOGLEVEL_DEFAULT = "loglevel.production";

    public ConfigureLoggingAction() {
        Long minFrameTime = Long.getLong("atlassian.profile.mintime", 1L);
        Timers.getConfiguration().setMinFrameTime(minFrameTime.longValue(), TimeUnit.MILLISECONDS);
    }

    private static LoggingConfigService loggingConfigService() {
        return loggingConfigServiceRef.get();
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        try {
            this.state = this.execute(new CollectState());
            return "success";
        }
        catch (NoSuchClusterNodeException e) {
            log.warn("Invalid nodeId " + this.nodeId);
            return "invalidNodeId";
        }
    }

    public String changeProfiling() throws Exception {
        this.execute(this.changeProfilingTask);
        return "success";
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setToDeleteName(String toDeleteName) {
        this.deleteLoggerTask.loggerName = toDeleteName;
    }

    public void setProfilingOn(boolean buttonValue) {
        this.changeProfilingTask.on = buttonValue;
    }

    public List<String> getLevelTypes() {
        ArrayList<String> levels = new ArrayList<String>();
        levels.add("ALL");
        levels.add("TRACE");
        levels.add("DEBUG");
        levels.add("INFO");
        levels.add("WARN");
        levels.add("ERROR");
        levels.add("FATAL");
        return levels;
    }

    public String delete() throws Exception {
        if (this.deleteLoggerTask.loggerName.equals("root")) {
            this.addActionError("You cannot delete the root logger");
            return "error";
        }
        this.execute(this.deleteLoggerTask);
        return "success";
    }

    public String save() throws Exception {
        this.execute(this.saveLoggerLevelsTask);
        return "success";
    }

    public String add() throws Exception {
        if (StringUtils.isBlank((CharSequence)this.addLoggerTask.className)) {
            this.addActionError("Please specify a valid name for the logger");
            return "error";
        }
        this.execute(this.addLoggerTask);
        return "success";
    }

    public List getEntries() {
        return this.state.entries;
    }

    public void setClassNames(String[] classNames) {
        this.saveLoggerLevelsTask.classNames = classNames;
    }

    public void setLevelNames(String[] levelNames) {
        this.saveLoggerLevelsTask.levelNames = levelNames;
    }

    public void setExtraClassName(String extraClassName) {
        this.addLoggerTask.className = extraClassName;
    }

    public void setExtraLevelName(String extraLevelName) {
        this.addLoggerTask.levelName = extraLevelName;
    }

    public String turnOnHibernateLogging() throws Exception {
        this.execute(new ToggleHibernateLoggingTask(true));
        return "success";
    }

    public String turnOffHibernateLogging() throws Exception {
        this.execute(new ToggleHibernateLoggingTask(false));
        return "success";
    }

    public boolean isHibernateLoggingEnabled() {
        return this.state.hibernateLoggingEnabled;
    }

    public void setProfileName(String profileName) {
        this.changeProfileTask.profileName = profileName;
    }

    public String changeProfile() {
        try {
            this.execute(this.changeProfileTask);
        }
        catch (Exception e) {
            this.getActionErrors().add("Unable to load properties for profile : " + this.changeProfileTask.profileName);
            return "error";
        }
        return "success";
    }

    private <T> T execute(Callable<T> task) throws Exception {
        ClusterNodeExecution<T> execution = this.clusterManager.submitToNode(StringUtils.trimToNull((String)this.nodeId), task, "cluster-manager-executor");
        this.currentNode = execution.getClusterNode();
        return execution.getCompletionStage().toCompletableFuture().get();
    }

    public boolean isProfilingEnabled() {
        return this.state.profilingEnabled;
    }

    public boolean isDiagnosticEnabled() {
        return this.state.diagnosticEnabled;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public ClusterNodeInformation getCurrentNode() {
        return this.currentNode;
    }

    public List<ClusterNodeInformation> getOtherNodes() throws Exception {
        if (this.state == null) {
            this.state = this.execute(new CollectState());
        }
        return this.clusterManager.getAllNodesInformation().stream().filter(n -> !Objects.equals(n, this.currentNode)).collect(Collectors.toList());
    }

    public String nodeUri(String url, String nodeId) {
        UriBuilder builder = UriBuilder.fromUri((String)url);
        if (!StringUtils.isBlank((CharSequence)nodeId)) {
            builder.queryParam("nodeId", new Object[]{nodeId});
        }
        return builder.build(new Object[0]).toString();
    }

    public String nodeUri(String url) {
        return this.nodeUri(url, this.nodeId);
    }

    private static class State
    implements Serializable {
        final List<LoggingConfigEntry> entries;
        final boolean diagnosticEnabled;
        final boolean profilingEnabled;
        final boolean hibernateLoggingEnabled;

        public State(List<LoggingConfigEntry> entries, boolean diagnosticEnabled, boolean profilingEnabled, boolean hibernateLoggingEnabled) {
            this.entries = entries;
            this.diagnosticEnabled = diagnosticEnabled;
            this.profilingEnabled = profilingEnabled;
            this.hibernateLoggingEnabled = hibernateLoggingEnabled;
        }
    }

    private static class ToggleHibernateLoggingTask
    implements Callable<Void>,
    Serializable {
        final boolean on;

        public ToggleHibernateLoggingTask(boolean on) {
            this.on = on;
        }

        @Override
        public Void call() throws Exception {
            if (this.on) {
                ConfigureLoggingAction.loggingConfigService().turnOnHibernateLogging();
                log.info("SQL logging enabled");
            } else {
                ConfigureLoggingAction.loggingConfigService().turnOffHibernateLogging();
                log.info("SQL logging disabled");
            }
            return null;
        }
    }

    private static class ChangeProfileTask
    implements Callable<Void>,
    Serializable {
        String profileName;

        private ChangeProfileTask() {
        }

        @Override
        public Void call() throws Exception {
            InputStream propStream = ClassLoaderUtils.getResourceAsStream((String)this.getPropertiesResource(this.profileName), ConfigureLoggingAction.class);
            if (propStream == null) {
                throw new IllegalArgumentException("Invalid profile " + this.profileName);
            }
            ConfigureLoggingAction.loggingConfigService().reconfigure(propStream);
            return null;
        }

        private String getPropertiesResource(String profileName) {
            if (this.getText(ConfigureLoggingAction.LOGLEVEL_DEFAULT).equals(profileName) || StringUtils.isEmpty((CharSequence)profileName)) {
                return "log4j.properties";
            }
            log.info("Switching to {} level logging", (Object)profileName);
            return "log4j-" + profileName.toLowerCase() + ".properties";
        }

        private String getText(String text) {
            I18NBeanFactory i18NBeanFactory = (I18NBeanFactory)ContainerManager.getInstance().getContainerContext().getComponent((Object)"i18NBeanFactory");
            return i18NBeanFactory.getI18NBean().getText(text);
        }
    }

    private static class ChangeProfilingTask
    implements Callable<Void>,
    Serializable {
        boolean on;

        private ChangeProfilingTask() {
        }

        @Override
        public Void call() throws Exception {
            Timers.getConfiguration().setEnabled(this.on);
            return null;
        }
    }

    private static class DeleteLoggerTask
    implements Callable<Void>,
    Serializable {
        String loggerName;

        private DeleteLoggerTask() {
        }

        @Override
        public Void call() throws Exception {
            ConfigureLoggingAction.loggingConfigService().resetLoggerLevel(this.loggerName);
            return null;
        }
    }

    public static class AddLoggerTask
    implements Callable<Void>,
    Serializable {
        String className;
        String levelName;

        @Override
        public Void call() throws Exception {
            ConfigureLoggingAction.loggingConfigService().setLevelForLogger(this.className, this.levelName);
            log.debug("New logger [ " + this.className + " ] saved");
            return null;
        }
    }

    private static class SaveLoggersTask
    implements Callable<Void>,
    Serializable {
        String[] classNames;
        String[] levelNames;

        private SaveLoggersTask() {
        }

        @Override
        public Void call() throws Exception {
            for (int i = 0; i < this.classNames.length; ++i) {
                ConfigureLoggingAction.loggingConfigService().setLevelForLogger(this.classNames[i], this.levelNames[i]);
            }
            log.debug("New log configuration saved");
            return null;
        }
    }

    private static class CollectState
    implements Callable<State>,
    Serializable {
        private CollectState() {
        }

        @Override
        public State call() throws Exception {
            return new State(ConfigureLoggingAction.loggingConfigService().getLoggerConfig(), ConfigureLoggingAction.loggingConfigService().isDiagnosticEnabled(), Timers.getConfiguration().isEnabled(), ConfigureLoggingAction.loggingConfigService().isHibernateLoggingEnabled());
        }
    }
}

