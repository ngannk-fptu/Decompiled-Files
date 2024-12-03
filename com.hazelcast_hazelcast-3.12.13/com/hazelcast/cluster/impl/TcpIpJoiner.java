/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cluster.impl;

import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigAccessor;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Member;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.AbstractJoiner;
import com.hazelcast.internal.cluster.impl.SplitBrainJoinMessage;
import com.hazelcast.internal.cluster.impl.operations.JoinMastershipClaimOp;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.AddressUtil;
import com.hazelcast.util.Clock;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.FutureUtil;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class TcpIpJoiner
extends AbstractJoiner {
    private static final long JOIN_RETRY_WAIT_TIME = 1000L;
    private static final int MASTERSHIP_CLAIM_TIMEOUT = 10;
    private final int maxPortTryCount;
    private volatile boolean claimingMastership;
    private final JoinConfig joinConfig;
    private final long previouslyJoinedMemberAddressRetentionDuration;
    private final ConcurrentMap<Address, Long> knownMemberAddresses = new ConcurrentHashMap<Address, Long>();

    public TcpIpJoiner(Node node) {
        super(node);
        int tryCount = node.getProperties().getInteger(GroupProperty.TCP_JOIN_PORT_TRY_COUNT);
        if (tryCount <= 0) {
            throw new IllegalArgumentException(String.format("%s should be greater than zero! Current value: %d", GroupProperty.TCP_JOIN_PORT_TRY_COUNT, tryCount));
        }
        this.maxPortTryCount = tryCount;
        this.joinConfig = ConfigAccessor.getActiveMemberNetworkConfig(this.config).getJoin();
        this.previouslyJoinedMemberAddressRetentionDuration = node.getProperties().getMillis(GroupProperty.TCP_PREVIOUSLY_JOINED_MEMBER_ADDRESS_RETENTION_DURATION);
    }

    public boolean isClaimingMastership() {
        return this.claimingMastership;
    }

    private int getConnTimeoutSeconds() {
        return this.joinConfig.getTcpIpConfig().getConnectionTimeoutSeconds();
    }

    @Override
    public void doJoin() {
        Address targetAddress = this.getTargetAddress();
        if (targetAddress != null) {
            long maxJoinMergeTargetMillis = this.node.getProperties().getMillis(GroupProperty.MAX_JOIN_MERGE_TARGET_SECONDS);
            this.joinViaTargetMember(targetAddress, maxJoinMergeTargetMillis);
            if (!this.clusterService.isJoined()) {
                this.joinViaPossibleMembers();
            }
        } else if (this.joinConfig.getTcpIpConfig().getRequiredMember() != null) {
            Address requiredMember = this.getRequiredMemberAddress();
            long maxJoinMillis = this.getMaxJoinMillis();
            this.joinViaTargetMember(requiredMember, maxJoinMillis);
        } else {
            this.joinViaPossibleMembers();
        }
    }

    private void joinViaTargetMember(Address targetAddress, long maxJoinMillis) {
        try {
            if (targetAddress == null) {
                throw new IllegalArgumentException("Invalid target address: NULL");
            }
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Joining over target member " + targetAddress);
            }
            if (targetAddress.equals(this.node.getThisAddress()) || this.isLocalAddress(targetAddress)) {
                this.clusterJoinManager.setThisMemberAsMaster();
                return;
            }
            long joinStartTime = Clock.currentTimeMillis();
            while (this.shouldRetry() && Clock.currentTimeMillis() - joinStartTime < maxJoinMillis) {
                Object connection = this.node.getEndpointManager(EndpointQualifier.MEMBER).getOrConnect(targetAddress);
                if (connection == null) {
                    Thread.sleep(1000L);
                    continue;
                }
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Sending joinRequest " + targetAddress);
                }
                this.clusterJoinManager.sendJoinRequest(targetAddress, true);
                Thread.sleep(1000L);
            }
        }
        catch (Exception e) {
            this.logger.warning(e);
        }
    }

    private void joinViaPossibleMembers() {
        try {
            Collection<Address> possibleAddresses = this.getPossibleAddressesForInitialJoin();
            long maxJoinMillis = this.getMaxJoinMillis();
            long startTime = Clock.currentTimeMillis();
            while (this.shouldRetry() && Clock.currentTimeMillis() - startTime < maxJoinMillis) {
                this.tryJoinAddresses(possibleAddresses);
                if (this.clusterService.isJoined()) {
                    return;
                }
                if (this.isAllBlacklisted(possibleAddresses)) {
                    this.logger.fine("This node will assume master role since none of the possible members accepted join request.");
                    this.clusterJoinManager.setThisMemberAsMaster();
                    return;
                }
                if (this.tryClaimMastership(possibleAddresses)) {
                    return;
                }
                this.clusterService.setMasterAddressToJoin(null);
            }
        }
        catch (Throwable t) {
            this.logger.severe(t);
        }
    }

    private boolean tryClaimMastership(Collection<Address> addresses) {
        boolean consensus = false;
        if (this.isThisNodeMasterCandidate(addresses)) {
            consensus = this.claimMastership(addresses);
            if (consensus) {
                if (this.logger.isFineEnabled()) {
                    HashSet<Address> votingEndpoints = new HashSet<Address>(addresses);
                    votingEndpoints.removeAll(this.blacklistedAddresses.keySet());
                    this.logger.fine("Setting myself as master after consensus! Voting endpoints: " + votingEndpoints);
                }
                this.clusterJoinManager.setThisMemberAsMaster();
            } else if (this.logger.isFineEnabled()) {
                HashSet<Address> votingEndpoints = new HashSet<Address>(addresses);
                votingEndpoints.removeAll(this.blacklistedAddresses.keySet());
                this.logger.fine("My claim to be master is rejected! Voting endpoints: " + votingEndpoints);
            }
        } else if (this.logger.isFineEnabled()) {
            this.logger.fine("Cannot claim myself as master! Will try to connect a possible master...");
        }
        this.claimingMastership = false;
        return consensus;
    }

    protected Collection<Address> getPossibleAddressesForInitialJoin() {
        return this.getPossibleAddresses();
    }

    private boolean claimMastership(Collection<Address> possibleAddresses) {
        if (this.logger.isFineEnabled()) {
            HashSet<Address> votingEndpoints = new HashSet<Address>(possibleAddresses);
            votingEndpoints.removeAll(this.blacklistedAddresses.keySet());
            this.logger.fine("Claiming myself as master node! Asking to endpoints: " + votingEndpoints);
        }
        this.claimingMastership = true;
        InternalOperationService operationService = this.node.getNodeEngine().getOperationService();
        LinkedList futures = new LinkedList();
        for (Address address : possibleAddresses) {
            if (this.isBlacklisted(address)) continue;
            InternalCompletableFuture future = operationService.createInvocationBuilder("hz:core:clusterService", (Operation)new JoinMastershipClaimOp(), address).setTryCount(1).invoke();
            futures.add(future);
        }
        try {
            Collection<Boolean> responses = FutureUtil.returnWithDeadline(futures, 10L, TimeUnit.SECONDS, FutureUtil.RETHROW_EVERYTHING);
            for (Boolean response : responses) {
                if (response.booleanValue()) continue;
                return false;
            }
            return true;
        }
        catch (Exception e) {
            this.logger.fine(e);
            return false;
        }
    }

    private boolean isThisNodeMasterCandidate(Collection<Address> addresses) {
        int thisHashCode = this.node.getThisAddress().hashCode();
        for (Address address : addresses) {
            if (this.isBlacklisted(address) || this.node.getEndpointManager(EndpointQualifier.MEMBER).getConnection(address) == null || thisHashCode <= address.hashCode()) continue;
            return false;
        }
        return true;
    }

    private void tryJoinAddresses(Collection<Address> addresses) throws InterruptedException {
        long connectionTimeoutMillis = TimeUnit.SECONDS.toMillis(this.getConnTimeoutSeconds());
        long start = Clock.currentTimeMillis();
        while (!this.clusterService.isJoined() && Clock.currentTimeMillis() - start < connectionTimeoutMillis) {
            Address masterAddress = this.clusterService.getMasterAddress();
            if (this.isAllBlacklisted(addresses) && masterAddress == null) {
                return;
            }
            if (masterAddress != null) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Sending join request to " + masterAddress);
                }
                this.clusterJoinManager.sendJoinRequest(masterAddress, true);
            } else {
                this.sendMasterQuestion(addresses);
            }
            if (this.clusterService.isJoined()) continue;
            Thread.sleep(1000L);
        }
    }

    private boolean isAllBlacklisted(Collection<Address> possibleAddresses) {
        return this.blacklistedAddresses.keySet().containsAll(possibleAddresses);
    }

    private void sendMasterQuestion(Collection<Address> addresses) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("NOT sending master question to blacklisted endpoints: " + this.blacklistedAddresses);
        }
        for (Address address : addresses) {
            if (this.isBlacklisted(address)) continue;
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Sending master question to " + address);
            }
            this.clusterJoinManager.sendMasterQuestion(address);
        }
    }

    private Address getRequiredMemberAddress() {
        block8: {
            TcpIpConfig tcpIpConfig = this.joinConfig.getTcpIpConfig();
            String host = tcpIpConfig.getRequiredMember();
            try {
                AddressUtil.AddressHolder addressHolder = AddressUtil.getAddressHolder(host, ConfigAccessor.getActiveMemberNetworkConfig(this.config).getPort());
                if (AddressUtil.isIpAddress(addressHolder.getAddress())) {
                    return new Address(addressHolder.getAddress(), addressHolder.getPort());
                }
                InterfacesConfig interfaces = ConfigAccessor.getActiveMemberNetworkConfig(this.config).getInterfaces();
                if (interfaces.isEnabled()) {
                    InetAddress[] inetAddresses = InetAddress.getAllByName(addressHolder.getAddress());
                    if (inetAddresses.length > 1) {
                        for (InetAddress inetAddress : inetAddresses) {
                            if (!AddressUtil.matchAnyInterface(inetAddress.getHostAddress(), interfaces.getInterfaces())) continue;
                            return new Address(inetAddress, addressHolder.getPort());
                        }
                    } else if (AddressUtil.matchAnyInterface(inetAddresses[0].getHostAddress(), interfaces.getInterfaces())) {
                        return new Address(addressHolder.getAddress(), addressHolder.getPort());
                    }
                    break block8;
                }
                return new Address(addressHolder.getAddress(), addressHolder.getPort());
            }
            catch (Exception e) {
                this.logger.warning(e);
            }
        }
        return null;
    }

    protected Collection<Address> getPossibleAddresses() {
        Collection<String> possibleMembers = this.getMembers();
        HashSet<Address> possibleAddresses = new HashSet<Address>();
        NetworkConfig networkConfig = ConfigAccessor.getActiveMemberNetworkConfig(this.config);
        for (String possibleMember : possibleMembers) {
            AddressUtil.AddressHolder addressHolder = AddressUtil.getAddressHolder(possibleMember);
            try {
                boolean portIsDefined = addressHolder.getPort() != -1 || !networkConfig.isPortAutoIncrement();
                int count = portIsDefined ? 1 : this.maxPortTryCount;
                int port = addressHolder.getPort() != -1 ? addressHolder.getPort() : networkConfig.getPort();
                AddressUtil.AddressMatcher addressMatcher = null;
                try {
                    addressMatcher = AddressUtil.getAddressMatcher(addressHolder.getAddress());
                }
                catch (AddressUtil.InvalidAddressException ignore) {
                    EmptyStatement.ignore(ignore);
                }
                if (addressMatcher != null) {
                    Collection<String> matchedAddresses = addressMatcher.isIPv4() ? AddressUtil.getMatchingIpv4Addresses(addressMatcher) : Collections.singleton(addressHolder.getAddress());
                    for (String matchedAddress : matchedAddresses) {
                        this.addPossibleAddresses(possibleAddresses, null, InetAddress.getByName(matchedAddress), port, count);
                    }
                    continue;
                }
                String host = addressHolder.getAddress();
                InterfacesConfig interfaces = networkConfig.getInterfaces();
                if (interfaces.isEnabled()) {
                    InetAddress[] inetAddresses;
                    for (InetAddress inetAddress : inetAddresses = InetAddress.getAllByName(host)) {
                        if (!AddressUtil.matchAnyInterface(inetAddress.getHostAddress(), interfaces.getInterfaces())) continue;
                        this.addPossibleAddresses(possibleAddresses, host, inetAddress, port, count);
                    }
                    continue;
                }
                this.addPossibleAddresses(possibleAddresses, host, null, port, count);
            }
            catch (UnknownHostException e) {
                this.logger.warning("Cannot resolve hostname '" + addressHolder.getAddress() + "'. Please make sure host is valid and reachable.");
                if (!this.logger.isFineEnabled()) continue;
                this.logger.fine("Error during resolving possible target!", e);
            }
        }
        this.cleanupKnownMemberAddresses();
        possibleAddresses.addAll(this.knownMemberAddresses.keySet());
        possibleAddresses.remove(this.node.getThisAddress());
        return possibleAddresses;
    }

    private void addPossibleAddresses(Set<Address> possibleAddresses, String host, InetAddress inetAddress, int port, int count) throws UnknownHostException {
        for (int i = 0; i < count; ++i) {
            int currentPort = port + i;
            Address address = host != null && inetAddress != null ? new Address(host, inetAddress, currentPort) : (host != null ? new Address(host, currentPort) : new Address(inetAddress, currentPort));
            if (this.isLocalAddress(address)) continue;
            possibleAddresses.add(address);
        }
    }

    private boolean isLocalAddress(Address address) throws UnknownHostException {
        Address thisAddress = this.node.getThisAddress();
        boolean local = thisAddress.getInetSocketAddress().equals(address.getInetSocketAddress());
        if (this.logger.isFineEnabled()) {
            this.logger.fine(address + " is local? " + local);
        }
        return local;
    }

    protected Collection<String> getMembers() {
        return TcpIpJoiner.getConfigurationMembers(this.config);
    }

    public static Collection<String> getConfigurationMembers(Config config) {
        return TcpIpJoiner.getConfigurationMembers(ConfigAccessor.getActiveMemberNetworkConfig(config).getJoin().getTcpIpConfig());
    }

    public static Collection<String> getConfigurationMembers(TcpIpConfig tcpIpConfig) {
        List<String> configMembers = tcpIpConfig.getMembers();
        HashSet<String> possibleMembers = new HashSet<String>();
        for (String member : configMembers) {
            String[] members = member.split("[,; ]");
            Collections.addAll(possibleMembers, members);
        }
        return possibleMembers;
    }

    public void onMemberAdded(Member member) {
        if (!member.localMember()) {
            this.knownMemberAddresses.put(member.getAddress(), Long.MAX_VALUE);
        }
    }

    public void onMemberRemoved(Member member) {
        if (!member.localMember()) {
            this.knownMemberAddresses.put(member.getAddress(), Clock.currentTimeMillis());
        }
    }

    @Override
    public void searchForOtherClusters() {
        Collection<Address> possibleAddresses;
        try {
            possibleAddresses = this.getPossibleAddresses();
        }
        catch (Throwable e) {
            this.logger.severe(e);
            return;
        }
        possibleAddresses.remove(this.node.getThisAddress());
        possibleAddresses.removeAll(this.node.getClusterService().getMemberAddresses());
        if (possibleAddresses.isEmpty()) {
            return;
        }
        SplitBrainJoinMessage request = this.node.createSplitBrainJoinMessage();
        for (Address address : possibleAddresses) {
            SplitBrainJoinMessage.SplitBrainMergeCheckResult result = this.sendSplitBrainJoinMessageAndCheckResponse(address, request);
            if (result != SplitBrainJoinMessage.SplitBrainMergeCheckResult.LOCAL_NODE_SHOULD_MERGE) continue;
            this.logger.warning(this.node.getThisAddress() + " is merging [tcp/ip] to " + address);
            this.setTargetAddress(address);
            this.startClusterMerge(address, request.getMemberListVersion());
            return;
        }
    }

    private void cleanupKnownMemberAddresses() {
        long currentTime = Clock.currentTimeMillis();
        Iterator iterator = this.knownMemberAddresses.values().iterator();
        while (iterator.hasNext()) {
            Long memberLeftTime = (Long)iterator.next();
            if (currentTime - memberLeftTime < this.previouslyJoinedMemberAddressRetentionDuration) continue;
            iterator.remove();
        }
    }

    public ConcurrentMap<Address, Long> getKnownMemberAddresses() {
        return this.knownMemberAddresses;
    }

    @Override
    public String getType() {
        return "tcp-ip";
    }
}

