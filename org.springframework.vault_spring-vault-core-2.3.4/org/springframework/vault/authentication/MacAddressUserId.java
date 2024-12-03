/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.vault.authentication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.authentication.AppIdUserIdMechanism;
import org.springframework.vault.authentication.Sha256;

public class MacAddressUserId
implements AppIdUserIdMechanism {
    private final Log logger = LogFactory.getLog(MacAddressUserId.class);
    private final String networkInterfaceHint;

    public MacAddressUserId() {
        this("");
    }

    public MacAddressUserId(int networkInterfaceIndex) {
        Assert.isTrue((networkInterfaceIndex >= 0 ? 1 : 0) != 0, (String)"NetworkInterfaceIndex must be greater or equal to 0");
        this.networkInterfaceHint = "" + networkInterfaceIndex;
    }

    public MacAddressUserId(String networkInterfaceName) {
        Assert.notNull((Object)networkInterfaceName, (String)"NetworkInterfaceName must not be null");
        this.networkInterfaceHint = networkInterfaceName;
    }

    @Override
    public String createUserId() {
        try {
            Optional<Object> networkInterface = Optional.empty();
            ArrayList<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            if (StringUtils.hasText((String)this.networkInterfaceHint)) {
                try {
                    networkInterface = MacAddressUserId.getNetworkInterface(Integer.parseInt(this.networkInterfaceHint), interfaces);
                }
                catch (NumberFormatException e) {
                    networkInterface = MacAddressUserId.getNetworkInterface(this.networkInterfaceHint, interfaces);
                }
            }
            if (!networkInterface.isPresent()) {
                InetAddress localHost;
                if (StringUtils.hasText((String)this.networkInterfaceHint)) {
                    this.logger.warn((Object)String.format("Did not find a NetworkInterface applying hint %s", this.networkInterfaceHint));
                }
                if (!(networkInterface = Optional.ofNullable(NetworkInterface.getByInetAddress(localHost = InetAddress.getLocalHost()))).filter(MacAddressUserId::hasNetworkAddress).isPresent()) {
                    networkInterface = MacAddressUserId.getNetworkInterfaceWithHardwareAddress(interfaces);
                }
            }
            return networkInterface.map(MacAddressUserId::getRequiredNetworkAddress).map(Sha256::toHexString).map(Sha256::toSha256).orElseThrow(() -> new IllegalStateException("Cannot determine NetworkInterface"));
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Optional<NetworkInterface> getNetworkInterface(Number hint, List<NetworkInterface> interfaces) {
        if (interfaces.size() > hint.intValue() && hint.intValue() >= 0) {
            return Optional.of(interfaces.get(hint.intValue()));
        }
        return Optional.empty();
    }

    private static Optional<NetworkInterface> getNetworkInterface(String hint, List<NetworkInterface> interfaces) {
        return interfaces.stream().filter(anInterface -> MacAddressUserId.matchesHint(hint, anInterface)).findFirst();
    }

    private static boolean matchesHint(String hint, NetworkInterface networkInterface) {
        return hint.equals(networkInterface.getDisplayName()) || hint.equals(networkInterface.getName());
    }

    private static Optional<NetworkInterface> getNetworkInterfaceWithHardwareAddress(List<NetworkInterface> interfaces) {
        return interfaces.stream().filter(MacAddressUserId::hasNetworkAddress).sorted(Comparator.comparingInt(NetworkInterface::getIndex)).findFirst();
    }

    private static Optional<byte[]> getNetworkAddress(NetworkInterface it) {
        try {
            return Optional.ofNullable(it.getHardwareAddress());
        }
        catch (SocketException e) {
            throw new IllegalStateException(String.format("Cannot determine hardware address for %s", it.getName()));
        }
    }

    private static byte[] getRequiredNetworkAddress(NetworkInterface it) {
        return MacAddressUserId.getNetworkAddress(it).orElseThrow(() -> new IllegalStateException(String.format("Network interface %s has no hardware address", it.getName())));
    }

    private static boolean hasNetworkAddress(NetworkInterface it) {
        return MacAddressUserId.getNetworkAddress(it).isPresent();
    }
}

