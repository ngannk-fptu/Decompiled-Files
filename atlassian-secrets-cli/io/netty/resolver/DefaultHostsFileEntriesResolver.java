/*
 * Decompiled with CFR 0.152.
 */
package io.netty.resolver;

import io.netty.resolver.HostsFileEntriesProvider;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class DefaultHostsFileEntriesResolver
implements HostsFileEntriesResolver {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultHostsFileEntriesResolver.class);
    private static final long DEFAULT_REFRESH_INTERVAL = SystemPropertyUtil.getLong("io.netty.hostsFileRefreshInterval", 0L);
    private final long refreshInterval;
    private final AtomicLong lastRefresh = new AtomicLong(System.nanoTime());
    private final HostsFileEntriesProvider.Parser hostsFileParser;
    private volatile Map<String, List<InetAddress>> inet4Entries;
    private volatile Map<String, List<InetAddress>> inet6Entries;

    public DefaultHostsFileEntriesResolver() {
        this(HostsFileEntriesProvider.parser(), DEFAULT_REFRESH_INTERVAL);
    }

    DefaultHostsFileEntriesResolver(HostsFileEntriesProvider.Parser hostsFileParser, long refreshInterval) {
        this.hostsFileParser = hostsFileParser;
        this.refreshInterval = ObjectUtil.checkPositiveOrZero(refreshInterval, "refreshInterval");
        HostsFileEntriesProvider entries = DefaultHostsFileEntriesResolver.parseEntries(hostsFileParser);
        this.inet4Entries = entries.ipv4Entries();
        this.inet6Entries = entries.ipv6Entries();
    }

    @Override
    public InetAddress address(String inetHost, ResolvedAddressTypes resolvedAddressTypes) {
        return DefaultHostsFileEntriesResolver.firstAddress(this.addresses(inetHost, resolvedAddressTypes));
    }

    public List<InetAddress> addresses(String inetHost, ResolvedAddressTypes resolvedAddressTypes) {
        String normalized = this.normalize(inetHost);
        this.ensureHostsFileEntriesAreFresh();
        switch (resolvedAddressTypes) {
            case IPV4_ONLY: {
                return this.inet4Entries.get(normalized);
            }
            case IPV6_ONLY: {
                return this.inet6Entries.get(normalized);
            }
            case IPV4_PREFERRED: {
                List<InetAddress> allInet4Addresses = this.inet4Entries.get(normalized);
                return allInet4Addresses != null ? DefaultHostsFileEntriesResolver.allAddresses(allInet4Addresses, this.inet6Entries.get(normalized)) : this.inet6Entries.get(normalized);
            }
            case IPV6_PREFERRED: {
                List<InetAddress> allInet6Addresses = this.inet6Entries.get(normalized);
                return allInet6Addresses != null ? DefaultHostsFileEntriesResolver.allAddresses(allInet6Addresses, this.inet4Entries.get(normalized)) : this.inet4Entries.get(normalized);
            }
        }
        throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + (Object)((Object)resolvedAddressTypes));
    }

    private void ensureHostsFileEntriesAreFresh() {
        long interval = this.refreshInterval;
        if (interval == 0L) {
            return;
        }
        long last = this.lastRefresh.get();
        long currentTime = System.nanoTime();
        if (currentTime - last > interval && this.lastRefresh.compareAndSet(last, currentTime)) {
            HostsFileEntriesProvider entries = DefaultHostsFileEntriesResolver.parseEntries(this.hostsFileParser);
            this.inet4Entries = entries.ipv4Entries();
            this.inet6Entries = entries.ipv6Entries();
        }
    }

    String normalize(String inetHost) {
        return inetHost.toLowerCase(Locale.ENGLISH);
    }

    private static List<InetAddress> allAddresses(List<InetAddress> a, List<InetAddress> b) {
        ArrayList<InetAddress> result = new ArrayList<InetAddress>(a.size() + (b == null ? 0 : b.size()));
        result.addAll(a);
        if (b != null) {
            result.addAll(b);
        }
        return result;
    }

    private static InetAddress firstAddress(List<InetAddress> addresses) {
        return addresses != null && !addresses.isEmpty() ? addresses.get(0) : null;
    }

    private static HostsFileEntriesProvider parseEntries(HostsFileEntriesProvider.Parser parser) {
        if (PlatformDependent.isWindows()) {
            return parser.parseSilently(Charset.defaultCharset(), CharsetUtil.UTF_16, CharsetUtil.UTF_8);
        }
        return parser.parseSilently();
    }

    static {
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.hostsFileRefreshInterval: {}", (Object)DEFAULT_REFRESH_INTERVAL);
        }
    }
}

