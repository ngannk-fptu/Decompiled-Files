/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastException
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.nio.Address
 *  com.hazelcast.spi.discovery.DiscoveryNode
 *  com.hazelcast.spi.discovery.SimpleDiscoveryNode
 */
package com.hazelcast.kubernetes;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategy;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

final class DnsEndpointResolver
extends HazelcastKubernetesDiscoveryStrategy.EndpointResolver {
    private final String serviceDns;
    private final int port;
    private final DirContext dirContext;

    DnsEndpointResolver(ILogger logger, String serviceDns, int port, DirContext dirContext) {
        super(logger);
        this.serviceDns = serviceDns;
        this.port = port;
        this.dirContext = dirContext;
    }

    DnsEndpointResolver(ILogger logger, String serviceDns, int port, int serviceDnsTimeout) {
        this(logger, serviceDns, port, DnsEndpointResolver.createDirContext(serviceDnsTimeout));
    }

    private static DirContext createDirContext(int serviceDnsTimeout) {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns:");
        env.put("com.sun.jndi.dns.timeout.initial", String.valueOf((long)serviceDnsTimeout * 1000L));
        try {
            return new InitialDirContext(env);
        }
        catch (NamingException e) {
            throw new HazelcastException("Error while initializing DirContext", (Throwable)e);
        }
    }

    @Override
    List<DiscoveryNode> resolve() {
        try {
            return this.lookup();
        }
        catch (NameNotFoundException e) {
            this.logger.warning(String.format("DNS lookup for serviceDns '%s' failed: name not found", this.serviceDns));
            return Collections.emptyList();
        }
        catch (Exception e) {
            this.logger.warning(String.format("DNS lookup for serviceDns '%s' failed", this.serviceDns), (Throwable)e);
            return Collections.emptyList();
        }
    }

    private List<DiscoveryNode> lookup() throws NamingException, UnknownHostException {
        HashSet<String> addresses = new HashSet<String>();
        Attributes attributes = this.dirContext.getAttributes(this.serviceDns, new String[]{"SRV"});
        Attribute srvAttribute = attributes.get("srv");
        if (srvAttribute != null) {
            NamingEnumeration<?> servers = srvAttribute.getAll();
            while (servers.hasMore()) {
                String server = (String)servers.next();
                String serverHost = DnsEndpointResolver.extractHost(server);
                InetAddress address = InetAddress.getByName(serverHost);
                if (!addresses.add(address.getHostAddress()) || !this.logger.isFinestEnabled()) continue;
                this.logger.finest("Found node service with address: " + address);
            }
        }
        if (addresses.size() == 0) {
            this.logger.warning("Could not find any service for serviceDns '" + this.serviceDns + "'");
            return Collections.emptyList();
        }
        ArrayList<DiscoveryNode> result = new ArrayList<DiscoveryNode>();
        for (String address : addresses) {
            result.add((DiscoveryNode)new SimpleDiscoveryNode(new Address(address, DnsEndpointResolver.getHazelcastPort(this.port))));
        }
        return result;
    }

    private static String extractHost(String server) {
        String host = server.split(" ")[3];
        return host.replaceAll("\\\\.$", "");
    }

    private static int getHazelcastPort(int port) {
        if (port > 0) {
            return port;
        }
        return 5701;
    }
}

