/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.spring.container.ContainerContext
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.velocity.exception.MethodInvocationException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.status;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.cluster.ClusterInformation;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.plugin.persistence.PluginDataDao;
import com.atlassian.confluence.plugin.persistence.PluginDataWithoutBinary;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.exception.MethodInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemErrorInformationLogger {
    private static final boolean USAGE_INFO_ENABLED = Boolean.getBoolean("confluence.usage.info.enabled");
    private static final int REQUEST_PARAM_MAX_LENGTH = 2000;
    private static final int REQUEST_PARAM_MAX_COUNT = 10;
    private final UUID uniqueID;
    private final ServletContext servletContext;
    private final HttpServletRequest request;
    private final Throwable throwable;
    private static final Logger log = LoggerFactory.getLogger(SystemErrorInformationLogger.class);

    public SystemErrorInformationLogger(UUID uuid, ServletContext servletContext, HttpServletRequest request, Throwable throwable) {
        this.servletContext = servletContext;
        this.request = request;
        this.uniqueID = uuid;
        this.throwable = throwable;
    }

    public SystemErrorInformationLogger(UUID uuid, ServletContext servletContext, HttpServletRequest request) {
        this(uuid, servletContext, request, null);
    }

    public SystemErrorInformationLogger() {
        this(null, null, null);
    }

    public void writeToLog(boolean writeExtendedInfo) {
        log.info(this.toString(writeExtendedInfo));
    }

    public String logException() {
        if (this.throwable != null) {
            log.error("Unhandled exception " + this.uniqueID.toString() + ": " + this.throwable.getMessage(), this.throwable);
        }
        return this.uniqueID.toString();
    }

    public String toString(boolean writeExtendedInfo) {
        ContainerContext containerContext;
        StringBuilder output = new StringBuilder("\n");
        SystemInformationService sysInfoService = null;
        PluginDataDao pluginDataDao = null;
        ContainerManager containerManager = ContainerManager.getInstance();
        ContainerContext containerContext2 = containerContext = containerManager != null ? containerManager.getContainerContext() : null;
        if (containerContext != null) {
            try {
                sysInfoService = (SystemInformationService)containerContext.getComponent((Object)"systemInformationService");
                pluginDataDao = (PluginDataDao)containerContext.getComponent((Object)"pluginDataDao");
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (sysInfoService == null) {
            log.warn("No SystemInformationService could be retrieved from the Container.");
            output.append("Build Information: Can't retrieve build information - no SystemInformationService available.\n");
        } else {
            if (this.uniqueID != null) {
                output.append("Request Unique ID : ").append(this.uniqueID.toString()).append("\n");
            }
            if (writeExtendedInfo) {
                ConfluenceInfo confluenceInfo = sysInfoService.getConfluenceInfo();
                this.appendHeading(output, "Build Information");
                Map<String, String> buildStatsMap = GeneralUtil.convertBeanToMap(confluenceInfo);
                buildStatsMap.remove("enabledPlugins");
                buildStatsMap.remove("startTime");
                buildStatsMap.remove("globalSettings");
                this.writeMapToStringBuffer(buildStatsMap, output);
                this.appendHeading(output, "Server Information");
                if (this.servletContext != null) {
                    this.appendParameter(output, "Application Server: ", this.servletContext.getServerInfo());
                    output.append("Servlet Version: ").append(this.servletContext.getMajorVersion()).append(".").append(this.servletContext.getMinorVersion()).append("\n");
                }
                this.appendHeading(output, "Database Information");
                DatabaseInfo databaseInfo = sysInfoService.getDatabaseInfo();
                this.appendParameter(output, "Database Dialect: ", databaseInfo.getDialect());
                this.appendParameter(output, "Database URL: ", databaseInfo.getUrl());
                this.appendParameter(output, "Database Driver Name: ", databaseInfo.getDriverName());
                this.appendParameter(output, "Database Driver Version: ", databaseInfo.getDriverVersion());
                this.appendParameter(output, "Database Name: ", databaseInfo.getName());
                this.appendParameter(output, "Database Version: ", databaseInfo.getVersion());
                this.appendParameter(output, "Database Latency (ms): ", databaseInfo.getExampleLatency().toString());
                this.appendHeading(output, "System Information");
                Map<String, String> props = GeneralUtil.convertBeanToMap(sysInfoService.getSystemProperties());
                this.writeMapToStringBuffer(props, output);
                this.appendHeading(output, "Global Settings");
                Map<String, String> globalSettingsMap = GeneralUtil.convertBeanToMap(confluenceInfo.getGlobalSettings());
                globalSettingsMap.remove("defaultPersonalSpaceHomepageContent");
                globalSettingsMap.remove("siteWelcomeMessage");
                globalSettingsMap.remove("defaultSpaceHomepageContent");
                globalSettingsMap.remove("referrerSettings");
                globalSettingsMap.remove("captchaSettings");
                globalSettingsMap.remove("customHtmlSettings");
                globalSettingsMap.remove("colourSchemesSettings");
                globalSettingsMap.remove("confluenceHttpParameters");
                this.writeMapToStringBuffer(globalSettingsMap, output);
                if (USAGE_INFO_ENABLED) {
                    this.appendHeading(output, "Usage Info");
                    Map<String, String> usageInfoMap = GeneralUtil.convertBeanToMap(sysInfoService.getUsageInfo());
                    this.writeMapToStringBuffer(usageInfoMap, output);
                }
            }
            this.appendHeading(output, "JVM Stats");
            Map<String, String> jvmStats = GeneralUtil.convertBeanToMap(sysInfoService.getMemoryInfo());
            this.writeMapToStringBuffer(jvmStats, output);
        }
        AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
        if (bootstrapManager != null && bootstrapManager.getHibernateConfig().isHibernateSetup() && writeExtendedInfo) {
            this.appendHeading(output, "Cluster Information");
            try {
                ClusterManager clusterManager = (ClusterManager)ContainerManager.getComponent((String)"clusterManager");
                if (!clusterManager.isClustered()) {
                    output.append("Not clustered.\n");
                } else {
                    ClusterInformation clusterInformation = clusterManager.getClusterInformation();
                    this.appendParameter(output, "Name: ", clusterInformation.getName());
                    this.appendParameter(output, "Description: ", clusterInformation.getDescription());
                    output.append("Members: \n");
                    for (String member : clusterInformation.getMembers()) {
                        output.append(member);
                    }
                }
            }
            catch (Exception exception) {
                output.append("Couldn't report cluster information:").append(exception);
            }
        }
        if (writeExtendedInfo) {
            this.appendHeading(output, "Enabled Plugins");
            if (sysInfoService == null) {
                output.append("Can't retrieve plugin information - no SystemInformationService available.\n");
            } else {
                DateFormat format = DateFormat.getDateInstance(2, Locale.US);
                try {
                    List<Plugin> enabledPlugins = sysInfoService.getConfluenceInfo().getEnabledPlugins();
                    for (Plugin enabledPlugin : enabledPlugins) {
                        String lastModifiedStr;
                        String pluginVersion;
                        PluginInformation pluginInfo = enabledPlugin.getPluginInformation();
                        String pluginName = enabledPlugin.getName();
                        String pluginKey = enabledPlugin.getKey();
                        if (pluginInfo == null) {
                            pluginVersion = "N/A";
                        } else {
                            pluginVersion = pluginInfo.getVersion();
                            if (pluginVersion == null) {
                                pluginVersion = "N/A";
                            }
                        }
                        String string = lastModifiedStr = pluginDataDao == null ? "unknown" : "bundled";
                        if (pluginDataDao != null && pluginDataDao.pluginDataExists(pluginKey)) {
                            Date lastModified;
                            PluginDataWithoutBinary pluginData = pluginDataDao.getPluginDataWithoutBinary(pluginKey);
                            Date date = lastModified = pluginData != null ? pluginData.getLastModificationDate() : null;
                            if (lastModified != null) {
                                lastModifiedStr = format.format(lastModified);
                            }
                        }
                        output.append(pluginName).append(" (").append(pluginKey).append(", Version: ").append(pluginVersion).append(", Installed: ").append(lastModifiedStr).append(")\n");
                    }
                }
                catch (Exception exception) {
                    output.append("Couldn't report plugins:").append(exception);
                }
            }
        }
        if (this.request != null) {
            this.appendHeading(output, "Request Information");
            this.appendParameter(output, "URL: ", this.request.getRequestURL());
            this.appendParameter(output, "Scheme: ", this.request.getScheme());
            this.appendParameter(output, "Server: ", this.request.getServerName());
            this.appendParameter(output, "Port: ", this.request.getServerPort());
            this.appendParameter(output, "URI: ", this.request.getRequestURI());
            this.appendParameter(output, "Context Path: ", this.request.getContextPath());
            this.appendParameter(output, "Servlet Path: ", this.request.getServletPath());
            this.appendParameter(output, "Path Info: ", this.request.getPathInfo());
            this.appendParameter(output, "Query String: ", this.request.getQueryString());
            this.appendHeading(output, "Attributes");
            Enumeration attributeNames = this.request.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = (String)attributeNames.nextElement();
                this.appendParameter(output, name + ": ", this.request.getAttribute(name));
            }
            this.appendParameters(output, this.request, writeExtendedInfo);
            Throwable throwable = (Throwable)this.request.getAttribute("javax.servlet.error.exception");
            StringBuilder causedByTotal = new StringBuilder();
            while (throwable != null) {
                String at = throwable.getStackTrace().length > 0 ? throwable.getStackTrace()[0].toString() : "Unknown location";
                causedByTotal.append("caused by: ").append(throwable).append("\n").append("at ").append(at).append("\n");
                if (throwable instanceof InvocationTargetException) {
                    throwable = ((InvocationTargetException)throwable).getTargetException();
                    continue;
                }
                if (throwable instanceof MethodInvocationException) {
                    throwable = ((MethodInvocationException)throwable).getWrappedThrowable();
                    continue;
                }
                if (throwable instanceof ServletException) {
                    throwable = ((ServletException)throwable).getRootCause();
                    continue;
                }
                throwable = throwable.getCause();
            }
            output.append((CharSequence)causedByTotal);
        }
        return output.toString();
    }

    void appendParameters(StringBuilder output, HttpServletRequest request, boolean writeExtendedInfo) {
        this.appendHeading(output, "Parameters");
        Pattern passwords = Pattern.compile(".*pass.*", 2);
        Enumeration paramNames = request.getParameterNames();
        int count = 0;
        while (paramNames.hasMoreElements() && count < 10) {
            String paramName = (String)paramNames.nextElement();
            if (passwords.matcher(paramName).matches()) continue;
            ++count;
            String[] paramValues = request.getParameterValues(paramName);
            String truncatedName = this.truncateParamData(paramName, writeExtendedInfo);
            output.append(HtmlUtil.htmlEncode(truncatedName)).append(" : ");
            for (int i = 0; i < paramValues.length; ++i) {
                String paramValue = paramValues[i];
                String truncatedValue = this.truncateParamData(paramValue, writeExtendedInfo);
                output.append(HtmlUtil.htmlEncode(truncatedValue));
                if (i >= paramValues.length - 1) continue;
                output.append(", ");
            }
            output.append("\n");
        }
    }

    private String truncateParamData(String param, boolean writeExtendedInfo) {
        int offset = Math.min(param.length(), 2000);
        return writeExtendedInfo ? param : param.substring(0, offset);
    }

    private void appendParameter(StringBuilder output, String desc, Object value) {
        output.append(desc).append(value).append("\n");
    }

    private void appendParameter(StringBuilder output, String desc, int value) {
        output.append(desc).append(value).append("\n");
    }

    private void appendParameter(StringBuilder output, String paramDescription, String param) {
        output.append(paramDescription).append(param).append("\n");
    }

    private void writeMapToStringBuffer(Map<String, String> buildStatsMap, StringBuilder output) {
        buildStatsMap.forEach((key, value) -> this.appendParameter(output, key + " = ", (String)value));
    }

    private void appendHeading(StringBuilder output, String heading) {
        output.append("--------------------------").append('\n');
        output.append(heading).append("\n");
        output.append("--------------------------").append('\n');
    }
}

