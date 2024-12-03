/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.cluster.AWSClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterAlreadyExistsException;
import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterInformation;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.DefaultClusterSetupValidator;
import com.atlassian.confluence.cluster.KubernetesClusterJoinConfig;
import com.atlassian.confluence.cluster.MulticastClusterJoinConfig;
import com.atlassian.confluence.cluster.TCPIPClusterJoinConfig;
import com.atlassian.confluence.cluster.shareddata.SharedDataManager;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.SharedConfigurationMap;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.confluence.util.ClusterUtils;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.validation.MessageHolder;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import com.google.common.base.Preconditions;
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class SetupClusterAction
extends AbstractSetupAction {
    private static final Logger log = LoggerFactory.getLogger(SetupClusterAction.class);
    private static final String SETUP_CLUSTER = "cluster";
    private static final String NEW_CLUSTER = "newCluster";
    private static final String SKIP_CLUSTER = "skipCluster";
    private static final String JOIN_CLUSTER = "joinCluster";
    private static final String GENERATE_ADDRESS = "auto";
    private static final String AWS_SECRET_KEY_AUTH = "secretKey";
    private static final String AWS_IAM_ROLE_AUTH = "iamRole";
    private String setupClusterType;
    private String clusterName;
    private String clusterAddressString;
    private String clusterPeersString;
    private String awsAuthMethod;
    private String accessKey;
    private String secretKey;
    private String iamRole;
    private String region;
    private String hostHeader;
    private String securityGroupName;
    private String tagKey;
    private String tagValue;
    private String clusterHome;
    private ClusterManager clusterManager;
    private SharedDataManager clusterSharedDataManager;
    private String networkInterface;
    private String generateClusterAddress;
    private String generateClusterAddressSubmitted;
    private String joinMethod;
    private ClusterConfigurationHelperInternal clusterConfigurationHelper;

    @Override
    public void validate() {
        super.validate();
        String setupClusterType = this.getSetupClusterType();
        if (StringUtils.isEmpty((CharSequence)setupClusterType)) {
            this.addActionError(this.getText("error.no.cluster.setup.type"));
        } else if (setupClusterType.equalsIgnoreCase(SKIP_CLUSTER)) {
            return;
        }
        MessageHolder holder = this.getMessageHolder();
        DefaultClusterSetupValidator clusterSetupValidator = new DefaultClusterSetupValidator(holder);
        clusterSetupValidator.validateClusterName(this.clusterName).validateClusterHome(this.clusterHome).validateNetworkInterface(this.networkInterface).validateClusterJoinMethod(this.joinMethod);
        if (ClusterJoinConfig.ClusterJoinType.MULTICAST.getText().equals(this.joinMethod)) {
            clusterSetupValidator.validateMulticastClusterJoinConfig(this.isGenerateAddress(), this.clusterAddressString);
        } else if (ClusterJoinConfig.ClusterJoinType.TCP_IP.getText().equals(this.joinMethod)) {
            clusterSetupValidator.validateTCPIPClusterJoinConfig(this.clusterPeersString);
        } else if (ClusterJoinConfig.ClusterJoinType.AWS.getText().equals(this.joinMethod)) {
            clusterSetupValidator.validateAWSJoinConfig(this.accessKey, this.secretKey, this.iamRole, this.region, this.hostHeader, this.securityGroupName, this.tagKey, this.tagValue);
        } else if (ClusterJoinConfig.ClusterJoinType.KUBERNETES.getText().equals(this.joinMethod)) {
            clusterSetupValidator.validateKubernetesJoinConfig();
        } else {
            throw new IllegalStateException("Missing validation for join method " + this.joinMethod);
        }
    }

    @Override
    public String doDefault() throws Exception {
        Optional<ClusterJoinConfig> clusterJoinConfigOptional = this.clusterConfigurationHelper.joinConfig();
        if (clusterJoinConfigOptional.isPresent()) {
            ClusterJoinConfig clusterJoinConfig = clusterJoinConfigOptional.get();
            this.setClusterName((String)this.getBootstrapStatusProvider().getProperty("confluence.cluster.name"));
            this.setClusterHome((String)this.getBootstrapStatusProvider().getProperty("shared-home"));
            this.setJoinMethod((String)this.getBootstrapStatusProvider().getProperty("confluence.cluster.join.type"));
            this.setSetupClusterType(NEW_CLUSTER);
            if (clusterJoinConfig instanceof AWSClusterJoinConfig) {
                AWSClusterJoinConfig awsClusterJoinConfig = (AWSClusterJoinConfig)clusterJoinConfig;
                this.setAccessKey(awsClusterJoinConfig.getAccessKey().orElse(""));
                this.setSecretKey(awsClusterJoinConfig.getSecretKey().orElse(""));
                this.setIamRole(awsClusterJoinConfig.getIamRole().orElse(""));
                this.setHostHeader(awsClusterJoinConfig.getHostHeader().orElse(""));
                this.setTagKey(awsClusterJoinConfig.getTagKey().orElse(""));
                this.setTagValue(awsClusterJoinConfig.getTagValue().orElse(""));
                return "skipToNextStep";
            }
            if (clusterJoinConfig instanceof KubernetesClusterJoinConfig) {
                return "skipToNextStep";
            }
            return super.doDefault();
        }
        return super.doDefault();
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        ClusterJoinConfig clusterJoinConfig;
        if (this.hasErrors() || this.messageHolder.hasErrors()) {
            return "error";
        }
        if (this.setupClusterType.equalsIgnoreCase(SKIP_CLUSTER)) {
            return "setupdb";
        }
        File clusterHomeDir = new File(this.clusterHome);
        Optional<ClusterJoinConfig> clusterJoinConfigOptional = this.clusterConfigurationHelper.joinConfig();
        if (ClusterJoinConfig.ClusterJoinType.MULTICAST.getText().equals(this.joinMethod)) {
            InetAddress clusterAddress = this.isGenerateAddress() ? ClusterUtils.hashNameToMulticastAddress(this.clusterName) : ClusterUtils.addressFromIpString(this.clusterAddressString);
            clusterJoinConfig = (ClusterJoinConfig)MulticastClusterJoinConfig.createForAddress(clusterAddress).right().get();
        } else if (ClusterJoinConfig.ClusterJoinType.TCP_IP.getText().equals(this.joinMethod)) {
            clusterJoinConfig = (ClusterJoinConfig)TCPIPClusterJoinConfig.createForPeers(this.clusterPeersString).right().get();
        } else if (ClusterJoinConfig.ClusterJoinType.AWS.getText().equals(this.joinMethod)) {
            clusterJoinConfigOptional.ifPresent(reConfigClusterJoinConfig -> {
                AWSClusterJoinConfig awsClusterJoinConfig = (AWSClusterJoinConfig)reConfigClusterJoinConfig;
                this.setAccessKey(awsClusterJoinConfig.getAccessKey().orElseGet(() -> this.accessKey));
                this.setSecretKey(awsClusterJoinConfig.getSecretKey().orElseGet(() -> this.secretKey));
                this.setIamRole(awsClusterJoinConfig.getIamRole().orElseGet(() -> this.iamRole));
                this.setRegion(awsClusterJoinConfig.getRegion().orElseGet(() -> this.region));
                this.setHostHeader(awsClusterJoinConfig.getHostHeader().orElseGet(() -> this.hostHeader));
                this.setSecurityGroupName(awsClusterJoinConfig.getSecurityGroupName().orElseGet(() -> this.securityGroupName));
                this.setTagKey(awsClusterJoinConfig.getTagKey().orElseGet(() -> this.tagKey));
                this.setTagValue(awsClusterJoinConfig.getTagValue().orElseGet(() -> this.tagValue));
            });
            clusterJoinConfig = clusterJoinConfigOptional.orElseGet(() -> (ClusterJoinConfig)AWSClusterJoinConfig.createForKeys(this.accessKey, this.secretKey, this.iamRole, this.region, this.hostHeader, this.securityGroupName, this.tagKey, this.tagValue).right().get());
        } else if (ClusterJoinConfig.ClusterJoinType.KUBERNETES.getText().equals(this.joinMethod)) {
            clusterJoinConfig = new KubernetesClusterJoinConfig();
        } else {
            this.addActionError("invalid.cluster.join.method", this.joinMethod);
            log.error("The cluster join method: " + this.joinMethod + " is not valid");
            return "error";
        }
        String setupType = this.getSetupClusterType();
        if (NEW_CLUSTER.equals(setupType)) {
            try {
                if (!this.createNamedCluster(this.clusterName, clusterHomeDir, clusterJoinConfig, this.networkInterface)) {
                    return "error";
                }
                this.getSetupPersister().progessSetupStep();
                if ("standalone.to.cluster".equals(this.getSetupPersister().getSetupType())) {
                    this.performEarlyStartup();
                    this.bootstrapConfigurer().setProperty("hibernate.setup", "true");
                    this.performLateStartup();
                    return SETUP_CLUSTER;
                }
                return "setupdb";
            }
            catch (RuntimeException ex) {
                log.error("Could not setup cluster: ", (Throwable)ex);
                return "error";
            }
        }
        return "error";
    }

    public void setSetupClusterType(String setupClusterType) {
        this.setupClusterType = setupClusterType;
    }

    public String getSetupClusterType() {
        if (this.setupClusterType == null) {
            if (StringUtils.isNotEmpty((CharSequence)ServletActionContext.getRequest().getParameter(NEW_CLUSTER))) {
                if (ServletActionContext.getRequest().getParameter(NEW_CLUSTER).equalsIgnoreCase(SKIP_CLUSTER)) {
                    this.getSetupPersister().setSetupType("custom");
                    this.getSetupPersister().synchSetupStackWithConfigRecord("setupdbchoice-start");
                    this.setupClusterType = SKIP_CLUSTER;
                } else {
                    this.setupClusterType = NEW_CLUSTER;
                }
            } else if (StringUtils.isNotEmpty((CharSequence)ServletActionContext.getRequest().getParameter(JOIN_CLUSTER))) {
                this.setupClusterType = JOIN_CLUSTER;
            }
        }
        return this.setupClusterType;
    }

    private boolean createNamedCluster(String name, File clusterHome, ClusterJoinConfig joinConfig, String networkInterface) {
        try {
            this.getClusterConfigurationHelper().createCluster(name, clusterHome, networkInterface, joinConfig);
            return true;
        }
        catch (ClusterAlreadyExistsException ex) {
            String clusterName = ex.getClusterName();
            log.error("Cluster {} already exists with connection details: {}", (Object)clusterName, (Object)joinConfig);
            this.addActionError("cluster.already.exists", clusterName, joinConfig);
            return false;
        }
        catch (ClusterException ex) {
            this.addActionError(HtmlUtil.htmlEncode(ex.getMessage()));
            log.error("There was a problem creating the cluster with name '" + name + "'", (Throwable)ex);
            return false;
        }
    }

    @Deprecated
    public boolean isThisNodeClustered() {
        return this.getClusterManager().isClustered();
    }

    @Deprecated
    public SharedConfigurationMap getSharedConfig() {
        return SharedConfigurationMap.getPublished(this.clusterSharedDataManager);
    }

    public ClusterInformation getClusterInformation() {
        return this.clusterManager.getClusterInformation();
    }

    public String getClusterName() {
        return this.clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = (String)Preconditions.checkNotNull((Object)clusterName);
    }

    public String getClusterHome() {
        return this.clusterHome;
    }

    public void setClusterHome(String clusterHome) {
        this.clusterHome = (String)Preconditions.checkNotNull((Object)clusterHome);
    }

    public String getNetworkInterface() {
        return this.networkInterface;
    }

    public void setNetworkInterface(String networkInterface) {
        this.networkInterface = (String)Preconditions.checkNotNull((Object)networkInterface);
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = (ClusterManager)Preconditions.checkNotNull((Object)clusterManager);
    }

    public ClusterManager getClusterManager() {
        if (this.clusterManager == null) {
            this.setClusterManager((ClusterManager)BootstrapUtils.getBootstrapContext().getBean("clusterManager"));
        }
        return this.clusterManager;
    }

    public ClusterConfigurationHelperInternal getClusterConfigurationHelper() {
        if (this.clusterConfigurationHelper == null) {
            this.setClusterConfigurationHelper((ClusterConfigurationHelperInternal)BootstrapUtils.getBootstrapContext().getBean("clusterConfigurationHelper"));
        }
        return this.clusterConfigurationHelper;
    }

    public void setClusterConfigurationHelper(ClusterConfigurationHelperInternal clusterConfigurationHelper) {
        this.clusterConfigurationHelper = (ClusterConfigurationHelperInternal)Preconditions.checkNotNull((Object)clusterConfigurationHelper);
    }

    public List getClusterableInterfaces() {
        List<NetworkInterface> networkInterfaces = this.getClusterConfigurationHelper().getClusterableInterfaces();
        ArrayList<ClusterableInterface> clusterableInterfaces = new ArrayList<ClusterableInterface>(networkInterfaces.size());
        for (int i = 0; i < networkInterfaces.size(); ++i) {
            clusterableInterfaces.add(new ClusterableInterface(networkInterfaces.get(i), this.networkInterface, i));
        }
        return clusterableInterfaces;
    }

    public String getClusterAddressString() {
        return this.clusterAddressString;
    }

    public void setClusterAddressString(String clusterAddressString) {
        this.clusterAddressString = (String)Preconditions.checkNotNull((Object)clusterAddressString);
    }

    public String getGenerateClusterAddress() {
        return this.generateClusterAddress;
    }

    public void setGenerateClusterAddress(String generateClusterAddress) {
        this.generateClusterAddress = generateClusterAddress;
    }

    public String getGenerateClusterAddressSubmitted() {
        return this.generateClusterAddressSubmitted;
    }

    public void setGenerateClusterAddressSubmitted(String generateClusterAddressSubmitted) {
        this.generateClusterAddressSubmitted = generateClusterAddressSubmitted;
    }

    public String getClusterPeersString() {
        return this.clusterPeersString;
    }

    public void setClusterPeersString(String clusterPeersString) {
        this.clusterPeersString = clusterPeersString;
    }

    public void setClusterSharedDataManager(SharedDataManager clusterSharedDataManager) {
        this.clusterSharedDataManager = clusterSharedDataManager;
    }

    public String getJoinMethod() {
        if (this.joinMethod == null) {
            return ClusterJoinConfig.ClusterJoinType.MULTICAST.getText();
        }
        return this.joinMethod;
    }

    public boolean isMulticast() {
        return ClusterJoinConfig.ClusterJoinType.MULTICAST.getText().equals(this.getJoinMethod());
    }

    public boolean isTcpIp() {
        return ClusterJoinConfig.ClusterJoinType.TCP_IP.getText().equals(this.getJoinMethod());
    }

    public boolean isAws() {
        return ClusterJoinConfig.ClusterJoinType.AWS.getText().equals(this.getJoinMethod());
    }

    public boolean isGenerateAddress() {
        if (this.generateClusterAddressSubmitted == null) {
            return true;
        }
        return GENERATE_ADDRESS.equals(this.getGenerateClusterAddress());
    }

    public boolean isServerToDataCenterMigration() {
        return this.getSetupPersister().getSetupType().equalsIgnoreCase("standalone.to.cluster");
    }

    public void setJoinMethod(String joinMethod) {
        this.joinMethod = joinMethod;
    }

    public String getAwsAuthMethod() {
        if (this.awsAuthMethod == null) {
            return AWS_IAM_ROLE_AUTH;
        }
        return this.awsAuthMethod;
    }

    public void setAwsAuthMethod(String awsAuthMethod) {
        this.awsAuthMethod = awsAuthMethod;
    }

    public boolean isAwsSecretKeyAuth() {
        return AWS_SECRET_KEY_AUTH.equals(this.getAwsAuthMethod());
    }

    public boolean isAwsIamRoleAuth() {
        return AWS_IAM_ROLE_AUTH.equals(this.getAwsAuthMethod());
    }

    public String getAccessKey() {
        return this.accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getIamRole() {
        return this.iamRole;
    }

    public void setIamRole(String iamRole) {
        this.iamRole = iamRole;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getHostHeader() {
        return this.hostHeader;
    }

    public void setHostHeader(String hostHeader) {
        this.hostHeader = hostHeader;
    }

    public String getSecurityGroupName() {
        return this.securityGroupName;
    }

    public void setSecurityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
    }

    public String getTagKey() {
        return this.tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagValue() {
        return this.tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public static class ClusterableInterface {
        private final List<InetAddress> ipv4Addresses = new ArrayList<InetAddress>();
        private final List<InetAddress> ipv6Addresses = new ArrayList<InetAddress>();
        private final NetworkInterface networkInterface;
        private final boolean isCurrent;

        public ClusterableInterface(NetworkInterface networkInterface, @Nullable String currentChoice, int position) {
            this.networkInterface = networkInterface;
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress instanceof Inet4Address) {
                    this.ipv4Addresses.add(inetAddress);
                    continue;
                }
                this.ipv6Addresses.add(inetAddress);
            }
            this.isCurrent = currentChoice == null && position == 0 || networkInterface.getName().equals(currentChoice);
        }

        public List<InetAddress> getIpv4Addresses() {
            return this.ipv4Addresses;
        }

        public List<InetAddress> getIpv6Addresses() {
            return this.ipv6Addresses;
        }

        public String getName() {
            return this.networkInterface.getName();
        }

        public boolean isCurrent() {
            return this.isCurrent;
        }
    }
}

