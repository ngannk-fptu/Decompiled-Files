/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.cluster.impl.TcpIpJoiner;
import com.hazelcast.config.Config;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.instance.AddressPicker;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ServerSocketHelper;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.AddressUtil;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class DefaultAddressPicker
implements AddressPicker {
    static final String PREFER_IPV4_STACK = "java.net.preferIPv4Stack";
    static final String PREFER_IPV6_ADDRESSES = "java.net.preferIPv6Addresses";
    private final ILogger logger;
    private final HazelcastProperties hazelcastProperties;
    private final Config config;
    private final InterfacesConfig interfacesConfig;
    private final TcpIpConfig tcpIpConfig;
    private final String publicAddressConfig;
    private final EndpointQualifier endpointQualifier;
    private final boolean isReuseAddress;
    private final boolean isPortAutoIncrement;
    private final int port;
    private final int portCount;
    private HostnameResolver hostnameResolver = new InetAddressHostnameResolver();
    private Address publicAddress;
    private Address bindAddress;
    private ServerSocketChannel serverSocketChannel;

    DefaultAddressPicker(Config config, ILogger logger) {
        this(config, null, config.getNetworkConfig().getInterfaces(), config.getNetworkConfig().getJoin().getTcpIpConfig(), config.getNetworkConfig().isReuseAddress(), config.getNetworkConfig().isPortAutoIncrement(), config.getNetworkConfig().getPort(), config.getNetworkConfig().getPortCount(), config.getNetworkConfig().getPublicAddress(), logger);
    }

    DefaultAddressPicker(Config config, EndpointQualifier endpointQualifier, InterfacesConfig interfacesConfig, TcpIpConfig tcpIpConfig, boolean isReuseAddress, boolean isPortAutoIncrement, int port, int portCount, String publicAddressConfig, ILogger logger) {
        this.logger = logger;
        this.isReuseAddress = isReuseAddress;
        this.isPortAutoIncrement = isPortAutoIncrement;
        this.port = port;
        this.portCount = portCount;
        this.endpointQualifier = endpointQualifier;
        this.interfacesConfig = interfacesConfig;
        this.tcpIpConfig = tcpIpConfig;
        this.publicAddressConfig = publicAddressConfig;
        this.hazelcastProperties = new HazelcastProperties(config);
        this.config = config;
    }

    @Override
    public void pickAddress() throws Exception {
        if (this.publicAddress != null || this.bindAddress != null) {
            return;
        }
        try {
            AddressDefinition publicAddressDef = this.getPublicAddressByPortSearch();
            if (publicAddressDef != null) {
                this.publicAddress = DefaultAddressPicker.createAddress(publicAddressDef, publicAddressDef.port);
                this.logger.info("Using public address: " + this.publicAddress);
            } else {
                this.publicAddress = this.bindAddress;
                this.logger.finest("Using public address the same as the bind address: " + this.publicAddress);
            }
        }
        catch (Exception e) {
            ServerSocketChannel serverSocketChannel = this.getServerSocketChannel(this.endpointQualifier);
            if (serverSocketChannel != null) {
                serverSocketChannel.close();
            }
            this.logger.severe(e);
            throw e;
        }
    }

    private AddressDefinition getPublicAddressByPortSearch() throws IOException {
        boolean bindAny = this.hazelcastProperties.getBoolean(GroupProperty.SOCKET_SERVER_BIND_ANY);
        AddressDefinition bindAddressDef = this.pickAddressDef();
        EndpointConfig endpoint = this.config.getAdvancedNetworkConfig().isEnabled() ? this.config.getAdvancedNetworkConfig().getEndpointConfigs().get(this.endpointQualifier) : null;
        this.serverSocketChannel = ServerSocketHelper.createServerSocketChannel(this.logger, endpoint, bindAddressDef.inetAddress, bindAddressDef.port == 0 ? this.port : bindAddressDef.port, this.portCount, this.isPortAutoIncrement, this.isReuseAddress, bindAny);
        int port = this.serverSocketChannel.socket().getLocalPort();
        this.bindAddress = DefaultAddressPicker.createAddress(bindAddressDef, port);
        this.logger.info("Picked " + this.bindAddress + (this.endpointQualifier == null ? "" : ", for endpoint " + this.endpointQualifier) + ", using socket " + this.serverSocketChannel.socket() + ", bind any local is " + bindAny);
        return this.getPublicAddress(port);
    }

    private static Address createAddress(AddressDefinition addressDef, int port) throws UnknownHostException {
        if (addressDef.host == null) {
            return new Address(addressDef.inetAddress, port);
        }
        return new Address(addressDef.host, addressDef.inetAddress, port);
    }

    private AddressDefinition pickAddressDef() throws UnknownHostException, SocketException {
        AddressDefinition addressDef = this.getSystemConfiguredAddress();
        if (addressDef == null) {
            addressDef = this.pickInterfaceAddressDef();
        }
        if (addressDef != null) {
            addressDef.inetAddress = AddressUtil.fixScopeIdAndGetInetAddress(addressDef.inetAddress);
        }
        if (addressDef == null) {
            addressDef = DefaultAddressPicker.pickLoopbackAddress(null);
        }
        return addressDef;
    }

    private AddressDefinition pickInterfaceAddressDef() throws UnknownHostException, SocketException {
        AddressDefinition addressDef;
        List<InterfaceDefinition> interfaces = this.getInterfaces();
        if (interfaces.contains(new InterfaceDefinition("localhost", "127.0.0.1"))) {
            return DefaultAddressPicker.pickLoopbackAddress("localhost");
        }
        if (interfaces.contains(new InterfaceDefinition("127.0.0.1"))) {
            return DefaultAddressPicker.pickLoopbackAddress(null);
        }
        this.logger.info("Prefer IPv4 stack is " + this.preferIPv4Stack() + ", prefer IPv6 addresses is " + this.preferIPv6Addresses());
        if (interfaces.size() > 0 && (addressDef = this.pickMatchingAddress(interfaces)) != null) {
            return addressDef;
        }
        if (this.interfacesConfig.isEnabled()) {
            String msg = "Hazelcast CANNOT start on this node. No matching network interface found.\nInterface matching must be either disabled or updated in the hazelcast.xml config file.";
            this.logger.severe(msg);
            throw new RuntimeException(msg);
        }
        if (this.tcpIpConfig.isEnabled()) {
            this.logger.warning("Could not find a matching address to start with! Picking one of non-loopback addresses.");
        }
        return this.pickMatchingAddress(null);
    }

    private List<InterfaceDefinition> getInterfaces() {
        Map<String, String> addressDomainMap = this.createAddressToDomainMap(this.tcpIpConfig);
        ArrayList<InterfaceDefinition> interfaceDefs = new ArrayList<InterfaceDefinition>();
        if (this.interfacesConfig.isEnabled()) {
            Collection<String> configInterfaces = this.interfacesConfig.getInterfaces();
            for (String configInterface : configInterfaces) {
                if (!AddressUtil.isIpAddress(configInterface)) {
                    this.logger.warning("'" + configInterface + "' is not an IP address! Removing from interface list.");
                    continue;
                }
                DefaultAddressPicker.appendMatchingInterfaces(interfaceDefs, addressDomainMap, configInterface);
                interfaceDefs.add(new InterfaceDefinition(null, configInterface));
            }
            this.logger.info("Interfaces is enabled, trying to pick one address matching to one of: " + interfaceDefs);
        } else if (this.tcpIpConfig.isEnabled()) {
            for (Map.Entry<String, String> entry : addressDomainMap.entrySet()) {
                interfaceDefs.add(new InterfaceDefinition(entry.getValue(), entry.getKey()));
            }
            this.logger.info("Interfaces is disabled, trying to pick one address from TCP-IP config addresses: " + interfaceDefs);
        }
        return interfaceDefs;
    }

    private Map<String, String> createAddressToDomainMap(TcpIpConfig tcpIpConfig) {
        if (!tcpIpConfig.isEnabled()) {
            return Collections.emptyMap();
        }
        Collection<String> possibleAddresses = TcpIpJoiner.getConfigurationMembers(tcpIpConfig);
        Map<String, String> addressDomainMap = MapUtil.createLinkedHashMap(possibleAddresses.size());
        for (String possibleAddress : possibleAddresses) {
            String addressHolder = AddressUtil.getAddressHolder(possibleAddress).getAddress();
            if (AddressUtil.isIpAddress(addressHolder)) {
                if (addressDomainMap.containsKey(addressHolder)) continue;
                addressDomainMap.put(addressHolder, null);
                continue;
            }
            try {
                Collection<String> addresses = this.resolveDomainNames(addressHolder);
                for (String address : addresses) {
                    addressDomainMap.put(address, addressHolder);
                }
            }
            catch (UnknownHostException e) {
                this.logger.warning("Cannot resolve hostname: '" + addressHolder + "'");
            }
        }
        return addressDomainMap;
    }

    private static void appendMatchingInterfaces(Collection<InterfaceDefinition> interfaces, Map<String, String> address2DomainMap, String configInterface) {
        for (Map.Entry<String, String> entry : address2DomainMap.entrySet()) {
            String address = entry.getKey();
            if (!AddressUtil.matchInterface(address, configInterface)) continue;
            interfaces.add(new InterfaceDefinition(entry.getValue(), address));
        }
    }

    private Collection<String> resolveDomainNames(String domainName) throws UnknownHostException {
        Collection<String> addresses = this.hostnameResolver.resolve(domainName);
        this.logger.warning("You configured your member address as host name. Please be aware of that your dns can be spoofed. Make sure that your dns configurations are correct.");
        this.logger.info("Resolving domain name '" + domainName + "' to address(es): " + addresses);
        return addresses;
    }

    private AddressDefinition getSystemConfiguredAddress() throws UnknownHostException {
        String address = this.config.getProperty("hazelcast.local.localAddress");
        if (address != null) {
            if ("127.0.0.1".equals(address = address.trim()) || "localhost".equals(address)) {
                return DefaultAddressPicker.pickLoopbackAddress(address);
            }
            this.logger.info("Picking address configured by property 'hazelcast.local.localAddress'");
            return new AddressDefinition(address, InetAddress.getByName(address));
        }
        return null;
    }

    private AddressDefinition getPublicAddress(int port) throws UnknownHostException {
        String address = this.config.getProperty("hazelcast.local.publicAddress");
        if (address == null) {
            address = this.publicAddressConfig;
        }
        if (address != null) {
            if ("127.0.0.1".equals(address = address.trim()) || "localhost".equals(address)) {
                return DefaultAddressPicker.pickLoopbackAddress(address, port);
            }
            AddressUtil.AddressHolder holder = AddressUtil.getAddressHolder(address, port);
            return new AddressDefinition(holder.getAddress(), holder.getPort(), InetAddress.getByName(holder.getAddress()));
        }
        return null;
    }

    private static AddressDefinition pickLoopbackAddress(String host) throws UnknownHostException {
        return new AddressDefinition(host, InetAddress.getByName("127.0.0.1"));
    }

    private static AddressDefinition pickLoopbackAddress(String host, int defaultPort) throws UnknownHostException {
        InetAddress address = InetAddress.getByName("127.0.0.1");
        return new AddressDefinition(host, defaultPort, address);
    }

    AddressDefinition pickMatchingAddress(Collection<InterfaceDefinition> interfaces) throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        boolean preferIPv4Stack = this.preferIPv4Stack();
        boolean preferIPv6Addresses = this.preferIPv6Addresses();
        AddressDefinition matchingAddress = null;
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = networkInterfaces.nextElement();
            if (CollectionUtil.isEmpty(interfaces) && this.skipInterface(ni)) continue;
            Enumeration<InetAddress> e = ni.getInetAddresses();
            while (e.hasMoreElements()) {
                AddressDefinition address;
                InetAddress inetAddress = e.nextElement();
                if (preferIPv4Stack && inetAddress instanceof Inet6Address || (address = this.getMatchingAddress(interfaces, inetAddress)) == null) continue;
                matchingAddress = address;
                if (!(preferIPv6Addresses ? inetAddress instanceof Inet6Address : inetAddress instanceof Inet4Address)) continue;
                return matchingAddress;
            }
        }
        return matchingAddress;
    }

    private AddressDefinition getMatchingAddress(Collection<InterfaceDefinition> interfaces, InetAddress inetAddress) {
        if (CollectionUtil.isNotEmpty(interfaces)) {
            return this.match(inetAddress, interfaces);
        }
        if (!inetAddress.isLoopbackAddress()) {
            return new AddressDefinition(inetAddress);
        }
        return null;
    }

    private boolean skipInterface(NetworkInterface ni) throws SocketException {
        boolean skipInterface;
        boolean bl = skipInterface = !ni.isUp() || ni.isVirtual() || ni.isLoopback();
        if (skipInterface && this.logger.isFineEnabled()) {
            this.logger.fine("Skipping NetworkInterface '" + ni.getName() + "': isUp=" + ni.isUp() + ", isVirtual=" + ni.isVirtual() + ", isLoopback=" + ni.isLoopback());
        }
        return skipInterface;
    }

    private AddressDefinition match(InetAddress address, Collection<InterfaceDefinition> interfaces) {
        for (InterfaceDefinition inf : interfaces) {
            if (!AddressUtil.matchInterface(address.getHostAddress(), inf.address)) continue;
            return new AddressDefinition(inf.host, address);
        }
        return null;
    }

    private boolean preferIPv4Stack() {
        return Boolean.getBoolean(PREFER_IPV4_STACK) || this.hazelcastProperties.getBoolean(GroupProperty.PREFER_IPv4_STACK);
    }

    private boolean preferIPv6Addresses() {
        return !this.preferIPv4Stack() && Boolean.getBoolean(PREFER_IPV6_ADDRESSES);
    }

    @Override
    public Address getBindAddress() {
        return this.bindAddress;
    }

    @Override
    public Address getBindAddress(EndpointQualifier qualifier) {
        return this.bindAddress;
    }

    @Override
    public Address getPublicAddress() {
        return this.publicAddress;
    }

    @Override
    public Address getPublicAddress(EndpointQualifier qualifier) {
        return this.publicAddress;
    }

    @Override
    public ServerSocketChannel getServerSocketChannel() {
        return this.getServerSocketChannel(EndpointQualifier.MEMBER);
    }

    @Override
    public ServerSocketChannel getServerSocketChannel(EndpointQualifier qualifier) {
        return this.serverSocketChannel;
    }

    @Override
    public Map<EndpointQualifier, ServerSocketChannel> getServerSocketChannels() {
        return Collections.singletonMap(EndpointQualifier.MEMBER, this.serverSocketChannel);
    }

    @Override
    public Map<EndpointQualifier, Address> getPublicAddressMap() {
        return Collections.singletonMap(EndpointQualifier.MEMBER, this.publicAddress);
    }

    void setHostnameResolver(HostnameResolver hostnameResolver) {
        this.hostnameResolver = Preconditions.checkNotNull(hostnameResolver);
    }

    private static class InetAddressHostnameResolver
    implements HostnameResolver {
        private InetAddressHostnameResolver() {
        }

        @Override
        public Collection<String> resolve(String hostname) throws UnknownHostException {
            InetAddress[] inetAddresses = InetAddress.getAllByName(hostname);
            LinkedList<String> addresses = new LinkedList<String>();
            for (InetAddress inetAddress : inetAddresses) {
                addresses.add(inetAddress.getHostAddress());
            }
            return addresses;
        }
    }

    static interface HostnameResolver {
        public Collection<String> resolve(String var1) throws UnknownHostException;
    }

    static class AddressDefinition
    extends InterfaceDefinition {
        InetAddress inetAddress;
        int port;

        AddressDefinition(InetAddress inetAddress) {
            super(inetAddress.getHostAddress());
            this.inetAddress = inetAddress;
        }

        AddressDefinition(String host, InetAddress inetAddress) {
            super(host, inetAddress.getHostAddress());
            this.inetAddress = inetAddress;
        }

        AddressDefinition(String host, int port, InetAddress inetAddress) {
            super(host, inetAddress.getHostAddress());
            this.inetAddress = inetAddress;
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            AddressDefinition that = (AddressDefinition)o;
            if (this.port != that.port) {
                return false;
            }
            return !(this.inetAddress != null ? !this.inetAddress.equals(that.inetAddress) : that.inetAddress != null);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (this.inetAddress != null ? this.inetAddress.hashCode() : 0);
            result = 31 * result + this.port;
            return result;
        }
    }

    static class InterfaceDefinition {
        String host;
        String address;

        InterfaceDefinition(String address) {
            this.host = null;
            this.address = address;
        }

        InterfaceDefinition(String host, String address) {
            this.host = host;
            this.address = address;
        }

        public String toString() {
            return this.host != null ? this.host + "/" + this.address : this.address;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            InterfaceDefinition that = (InterfaceDefinition)o;
            if (this.address != null ? !this.address.equals(that.address) : that.address != null) {
                return false;
            }
            return !(this.host != null ? !this.host.equals(that.host) : that.host != null);
        }

        public int hashCode() {
            int result = this.host != null ? this.host.hashCode() : 0;
            result = 31 * result + (this.address != null ? this.address.hashCode() : 0);
            return result;
        }
    }
}

