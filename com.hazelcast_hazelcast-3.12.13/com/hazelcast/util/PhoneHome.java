/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.core.ClientType;
import com.hazelcast.instance.BuildInfo;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.JetBuildInfo;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterConnectionFactory;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.JsonUtil;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PhoneHome {
    private static final int TIMEOUT = 1000;
    private static final int A_INTERVAL = 5;
    private static final int B_INTERVAL = 10;
    private static final int C_INTERVAL = 20;
    private static final int D_INTERVAL = 40;
    private static final int E_INTERVAL = 60;
    private static final int F_INTERVAL = 100;
    private static final int G_INTERVAL = 150;
    private static final int H_INTERVAL = 300;
    private static final int J_INTERVAL = 600;
    private static final String BASE_PHONE_HOME_URL = "http://phonehome.hazelcast.com/ping";
    private static final int CONNECTION_TIMEOUT_MILLIS = 3000;
    private static final String FALSE = "false";
    volatile ScheduledFuture<?> phoneHomeFuture;
    private final ILogger logger;
    private final BuildInfo buildInfo = BuildInfoProvider.getBuildInfo();

    public PhoneHome(Node hazelcastNode) {
        this.logger = hazelcastNode.getLogger(PhoneHome.class);
    }

    public void check(final Node hazelcastNode) {
        if (!hazelcastNode.getProperties().getBoolean(GroupProperty.VERSION_CHECK_ENABLED)) {
            this.logger.warning(GroupProperty.VERSION_CHECK_ENABLED.getName() + " property is deprecated. Please use " + GroupProperty.PHONE_HOME_ENABLED.getName() + " instead to disable phone home.");
            return;
        }
        if (!hazelcastNode.getProperties().getBoolean(GroupProperty.PHONE_HOME_ENABLED)) {
            return;
        }
        if (FALSE.equals(System.getenv("HZ_PHONE_HOME_ENABLED"))) {
            return;
        }
        try {
            this.phoneHomeFuture = hazelcastNode.nodeEngine.getExecutionService().scheduleWithRepetition("PhoneHome", new Runnable(){

                @Override
                public void run() {
                    PhoneHome.this.phoneHome(hazelcastNode);
                }
            }, 0L, 1L, TimeUnit.DAYS);
        }
        catch (RejectedExecutionException e) {
            this.logger.warning("Could not schedule phone home task! Most probably Hazelcast failed to start.");
        }
    }

    public void shutdown() {
        if (this.phoneHomeFuture != null) {
            this.phoneHomeFuture.cancel(true);
        }
    }

    public String convertToLetter(int size) {
        String letter = size < 5 ? "A" : (size < 10 ? "B" : (size < 20 ? "C" : (size < 40 ? "D" : (size < 60 ? "E" : (size < 100 ? "F" : (size < 150 ? "G" : (size < 300 ? "H" : (size < 600 ? "J" : "I"))))))));
        return letter;
    }

    public Map<String, String> phoneHome(Node hazelcastNode) {
        PhoneHomeParameterCreator parameterCreator = this.createParameters(hazelcastNode);
        String urlStr = BASE_PHONE_HOME_URL + parameterCreator.build();
        this.fetchWebService(urlStr);
        return parameterCreator.getParameters();
    }

    public PhoneHomeParameterCreator createParameters(Node hazelcastNode) {
        ClusterServiceImpl clusterService = hazelcastNode.getClusterService();
        int clusterSize = clusterService.getMembers().size();
        Long clusterUpTime = clusterService.getClusterClock().getClusterUpTime();
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        JetBuildInfo jetBuildInfo = hazelcastNode.getBuildInfo().getJetBuildInfo();
        PhoneHomeParameterCreator parameterCreator = new PhoneHomeParameterCreator().addParam("version", this.buildInfo.getVersion()).addParam("m", hazelcastNode.getThisUuid()).addParam("p", this.getDownloadId()).addParam("c", clusterService.getClusterId()).addParam("crsz", this.convertToLetter(clusterSize)).addParam("cssz", this.convertToLetter(hazelcastNode.clientEngine.getClientEndpointCount())).addParam("cuptm", Long.toString(clusterUpTime)).addParam("nuptm", Long.toString(runtimeMxBean.getUptime())).addParam("jvmn", runtimeMxBean.getVmName()).addParam("jvmv", System.getProperty("java.version")).addParam("jetv", jetBuildInfo == null ? "" : jetBuildInfo.getVersion());
        this.addClientInfo(hazelcastNode, parameterCreator);
        this.addOSInfo(parameterCreator);
        boolean isManagementCenterConfigEnabled = hazelcastNode.config.getManagementCenterConfig().isEnabled();
        if (isManagementCenterConfigEnabled) {
            this.addManCenterInfo(hazelcastNode, clusterSize, parameterCreator);
        } else {
            parameterCreator.addParam("mclicense", "MC_NOT_CONFIGURED");
            parameterCreator.addParam("mcver", "MC_NOT_CONFIGURED");
        }
        return parameterCreator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getDownloadId() {
        InputStream is;
        String downloadId;
        block4: {
            downloadId = "source";
            is = null;
            try {
                is = this.getClass().getClassLoader().getResourceAsStream("hazelcast-download.properties");
                if (is == null) break block4;
                Properties properties = new Properties();
                properties.load(is);
                downloadId = properties.getProperty("hazelcastDownloadId");
            }
            catch (IOException ignored) {
                try {
                    EmptyStatement.ignore(ignored);
                }
                catch (Throwable throwable) {
                    IOUtil.closeResource(is);
                    throw throwable;
                }
                IOUtil.closeResource(is);
            }
        }
        IOUtil.closeResource(is);
        return downloadId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fetchWebService(String urlStr) {
        BufferedInputStream in = null;
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            in = new BufferedInputStream(conn.getInputStream());
            IOUtil.closeResource(in);
        }
        catch (Exception ignored) {
            EmptyStatement.ignore(ignored);
        }
        finally {
            IOUtil.closeResource(in);
        }
    }

    private void addOSInfo(PhoneHomeParameterCreator parameterCreator) {
        OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        try {
            parameterCreator.addParam("osn", osMxBean.getName()).addParam("osa", osMxBean.getArch()).addParam("osv", osMxBean.getVersion());
        }
        catch (SecurityException e) {
            parameterCreator.addParam("osn", "N/A").addParam("osa", "N/A").addParam("osv", "N/A");
        }
    }

    private void addClientInfo(Node hazelcastNode, PhoneHomeParameterCreator parameterCreator) {
        Map<ClientType, Integer> clusterClientStats = hazelcastNode.clientEngine.getConnectedClientStats();
        parameterCreator.addParam("ccpp", Integer.toString(clusterClientStats.get((Object)ClientType.CPP))).addParam("cdn", Integer.toString(clusterClientStats.get((Object)ClientType.CSHARP))).addParam("cjv", Integer.toString(clusterClientStats.get((Object)ClientType.JAVA))).addParam("cnjs", Integer.toString(clusterClientStats.get((Object)ClientType.NODEJS))).addParam("cpy", Integer.toString(clusterClientStats.get((Object)ClientType.PYTHON))).addParam("cgo", Integer.toString(clusterClientStats.get((Object)ClientType.GO)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addManCenterInfo(Node hazelcastNode, int clusterSize, PhoneHomeParameterCreator parameterCreator) {
        String license;
        String version;
        int responseCode;
        InputStream inputStream = null;
        InputStreamReader reader = null;
        try {
            HttpURLConnection connection;
            ManagementCenterConfig managementCenterConfig = hazelcastNode.config.getManagementCenterConfig();
            String manCenterURL = managementCenterConfig.getUrl();
            manCenterURL = manCenterURL.endsWith("/") ? manCenterURL : manCenterURL + '/';
            URL manCenterPhoneHomeURL = new URL(manCenterURL + "phoneHome.do");
            ManagementCenterConnectionFactory connectionFactory = hazelcastNode.getNodeExtension().getManagementCenterConnectionFactory();
            if (connectionFactory != null) {
                connectionFactory.init(managementCenterConfig.getMutualAuthConfig());
                connection = (HttpURLConnection)connectionFactory.openConnection(manCenterPhoneHomeURL);
            } else {
                connection = (HttpURLConnection)manCenterPhoneHomeURL.openConnection();
            }
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            inputStream = connection.getInputStream();
            responseCode = connection.getResponseCode();
            reader = new InputStreamReader(inputStream, "UTF-8");
            JsonObject mcPhoneHomeInfoJson = Json.parse(reader).asObject();
            version = JsonUtil.getString(mcPhoneHomeInfoJson, "mcVersion");
            license = JsonUtil.getString(mcPhoneHomeInfoJson, "mcLicense", null);
            IOUtil.closeResource(reader);
        }
        catch (Exception ignored) {
            EmptyStatement.ignore(ignored);
            parameterCreator.addParam("mclicense", "MC_NOT_AVAILABLE");
            parameterCreator.addParam("mcver", "MC_NOT_AVAILABLE");
            return;
        }
        finally {
            IOUtil.closeResource(reader);
            IOUtil.closeResource(inputStream);
        }
        IOUtil.closeResource(inputStream);
        if (responseCode == 200) {
            if (license == null) {
                this.checkClusterSizeAndSetLicense(clusterSize, parameterCreator);
            } else {
                parameterCreator.addParam("mclicense", license);
            }
            parameterCreator.addParam("mcver", version);
        } else {
            parameterCreator.addParam("mclicense", "MC_CONN_ERR_" + responseCode);
            parameterCreator.addParam("mcver", "MC_CONN_ERR_" + responseCode);
        }
    }

    private void checkClusterSizeAndSetLicense(int clusterSize, PhoneHomeParameterCreator parameterCreator) {
        if (clusterSize <= 2) {
            parameterCreator.addParam("mclicense", "MC_LICENSE_NOT_REQUIRED");
        } else {
            parameterCreator.addParam("mclicense", "MC_LICENSE_REQUIRED_BUT_NOT_SET");
        }
    }

    public static class PhoneHomeParameterCreator {
        private final StringBuilder builder;
        private final Map<String, String> parameters = new HashMap<String, String>();
        private boolean hasParameterBefore;

        public PhoneHomeParameterCreator() {
            this.builder = new StringBuilder();
            this.builder.append("?");
        }

        Map<String, String> getParameters() {
            return this.parameters;
        }

        public PhoneHomeParameterCreator addParam(String key, String value) {
            if (this.hasParameterBefore) {
                this.builder.append("&");
            } else {
                this.hasParameterBefore = true;
            }
            try {
                this.builder.append(key).append("=").append(URLEncoder.encode(value, "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                throw ExceptionUtil.rethrow(e);
            }
            this.parameters.put(key, value);
            return this;
        }

        String build() {
            return this.builder.toString();
        }
    }
}

