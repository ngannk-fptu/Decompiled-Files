/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.util.EmptyStatement;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public final class AddressUtil {
    private static final int NUMBER_OF_ADDRESSES = 255;
    private static final int IPV4_LENGTH = 4;
    private static final int IPV6_LENGTH = 8;
    private static final int IPV6_MAX_THRESHOLD = 65535;
    private static final int IPV4_MAX_THRESHOLD = 255;
    private static final int HEXADECIMAL_RADIX = 16;
    private static final int DECIMAL_RADIX = 10;

    private AddressUtil() {
    }

    public static boolean matchAnyInterface(String address, Collection<String> interfaces) {
        if (interfaces == null || interfaces.size() == 0) {
            return false;
        }
        for (String interfaceMask : interfaces) {
            if (!AddressUtil.matchInterface(address, interfaceMask)) continue;
            return true;
        }
        return false;
    }

    public static boolean matchInterface(String address, String interfaceMask) {
        AddressMatcher mask;
        try {
            mask = AddressUtil.getAddressMatcher(interfaceMask);
        }
        catch (Exception e) {
            return false;
        }
        return mask.match(address);
    }

    public static boolean matchAnyDomain(String name, Collection<String> patterns) {
        if (patterns == null || patterns.size() == 0) {
            return false;
        }
        for (String pattern : patterns) {
            if (!AddressUtil.matchDomain(name, pattern)) continue;
            return true;
        }
        return false;
    }

    public static boolean matchDomain(String name, String pattern) {
        int index = pattern.indexOf(42);
        if (index == -1) {
            return name.equals(pattern);
        }
        String[] names = name.split("\\.");
        String[] patterns = pattern.split("\\.");
        if (patterns.length > names.length) {
            return false;
        }
        int nameIndexDiff = names.length - patterns.length;
        for (int i = patterns.length - 1; i > -1; --i) {
            if ("*".equals(patterns[i]) || patterns[i].equals(names[i + nameIndexDiff])) continue;
            return false;
        }
        return true;
    }

    public static AddressHolder getAddressHolder(String address) {
        return AddressUtil.getAddressHolder(address, -1);
    }

    public static AddressHolder getAddressHolder(String address, int defaultPort) {
        String host;
        int indexBracketStart = address.indexOf(91);
        int indexBracketEnd = address.indexOf(93, indexBracketStart);
        int indexColon = address.indexOf(58);
        int lastIndexColon = address.lastIndexOf(58);
        int port = defaultPort;
        String scopeId = null;
        if (indexColon > -1 && lastIndexColon > indexColon) {
            int indexPercent;
            if (indexBracketStart == 0 && indexBracketEnd > indexBracketStart) {
                host = address.substring(indexBracketStart + 1, indexBracketEnd);
                if (lastIndexColon == indexBracketEnd + 1) {
                    port = Integer.parseInt(address.substring(lastIndexColon + 1));
                }
            } else {
                host = address;
            }
            if ((indexPercent = host.indexOf(37)) != -1) {
                scopeId = host.substring(indexPercent + 1);
                host = host.substring(0, indexPercent);
            }
        } else if (indexColon > 0 && indexColon == lastIndexColon) {
            host = address.substring(0, indexColon);
            port = Integer.parseInt(address.substring(indexColon + 1));
        } else {
            host = address;
        }
        return new AddressHolder(host, port, scopeId);
    }

    public static boolean isIpAddress(String address) {
        try {
            AddressUtil.getAddressMatcher(address);
            return true;
        }
        catch (InvalidAddressException e) {
            return false;
        }
    }

    public static InetAddress fixScopeIdAndGetInetAddress(InetAddress inetAddress) throws SocketException {
        if (!(inetAddress instanceof Inet6Address)) {
            return inetAddress;
        }
        if (!inetAddress.isLinkLocalAddress() && !inetAddress.isSiteLocalAddress()) {
            return inetAddress;
        }
        Inet6Address inet6Address = (Inet6Address)inetAddress;
        if (inet6Address.getScopeId() > 0 || inet6Address.getScopedInterface() != null) {
            return inetAddress;
        }
        Inet6Address resultInetAddress = AddressUtil.findRealInet6Address(inet6Address);
        return resultInetAddress == null ? inetAddress : resultInetAddress;
    }

    private static Inet6Address findRealInet6Address(Inet6Address inet6Address) throws SocketException {
        Inet6Address resultInetAddress = null;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (!AddressUtil.isInet6Compatible(address, inet6Address)) continue;
                if (resultInetAddress != null) {
                    throw new IllegalArgumentException("This address " + inet6Address + " is bound to more than one network interface!");
                }
                resultInetAddress = (Inet6Address)address;
            }
        }
        return resultInetAddress;
    }

    private static boolean isInet6Compatible(InetAddress address, Inet6Address inet6Address) {
        if (!(address instanceof Inet6Address)) {
            return false;
        }
        return Arrays.equals(address.getAddress(), inet6Address.getAddress());
    }

    public static Inet6Address getInetAddressFor(Inet6Address inetAddress, String scope) throws UnknownHostException, SocketException {
        if (inetAddress.isLinkLocalAddress() || inetAddress.isSiteLocalAddress()) {
            char[] chars = scope.toCharArray();
            boolean numeric = true;
            for (char c : chars) {
                if (Character.isDigit(c)) continue;
                numeric = false;
                break;
            }
            if (numeric) {
                return Inet6Address.getByAddress(null, inetAddress.getAddress(), Integer.parseInt(scope));
            }
            return Inet6Address.getByAddress(null, inetAddress.getAddress(), NetworkInterface.getByName(scope));
        }
        return inetAddress;
    }

    public static Collection<Inet6Address> getPossibleInetAddressesFor(Inet6Address inet6Address) {
        if (!inet6Address.isSiteLocalAddress() && !inet6Address.isLinkLocalAddress() || inet6Address.getScopeId() > 0 || inet6Address.getScopedInterface() != null) {
            return Collections.singleton(inet6Address);
        }
        LinkedList<Inet6Address> possibleAddresses = new LinkedList<Inet6Address>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                AddressUtil.addPossibleAddress(inet6Address, possibleAddresses, ni);
            }
        }
        catch (IOException ignored) {
            EmptyStatement.ignore(ignored);
        }
        if (possibleAddresses.isEmpty()) {
            throw new IllegalArgumentException("Could not find a proper network interface to connect to " + inet6Address);
        }
        return possibleAddresses;
    }

    private static void addPossibleAddress(Inet6Address inet6Address, Deque<Inet6Address> possibleAddresses, NetworkInterface ni) throws UnknownHostException {
        Enumeration<InetAddress> addresses = ni.getInetAddresses();
        while (addresses.hasMoreElements()) {
            InetAddress address = addresses.nextElement();
            if (address instanceof Inet4Address || (!inet6Address.isLinkLocalAddress() || !address.isLinkLocalAddress()) && (!inet6Address.isSiteLocalAddress() || !address.isSiteLocalAddress())) continue;
            Inet6Address newAddress = Inet6Address.getByAddress(null, inet6Address.getAddress(), ((Inet6Address)address).getScopeId());
            possibleAddresses.addFirst(newAddress);
        }
    }

    public static Collection<String> getMatchingIpv4Addresses(AddressMatcher addressMatcher) {
        if (addressMatcher.isIPv6()) {
            throw new IllegalArgumentException("Cannot wildcard matching for IPv6: " + addressMatcher);
        }
        HashSet<String> addresses = new HashSet<String>();
        String first3 = addressMatcher.address[0] + '.' + addressMatcher.address[1] + '.' + addressMatcher.address[2];
        String lastPart = addressMatcher.address[3];
        if ("*".equals(lastPart)) {
            for (int j = 0; j <= 255; ++j) {
                addresses.add(first3 + '.' + j);
            }
        } else if (lastPart.indexOf(45) > 0) {
            int dashPos = lastPart.indexOf(45);
            int start = Integer.parseInt(lastPart.substring(0, dashPos));
            int end = Integer.parseInt(lastPart.substring(dashPos + 1));
            for (int j = start; j <= end; ++j) {
                addresses.add(first3 + '.' + j);
            }
        } else {
            addresses.add(addressMatcher.getAddress());
        }
        return addresses;
    }

    public static AddressMatcher getAddressMatcher(String address) {
        AddressMatcher matcher;
        int indexColon = address.indexOf(58);
        int lastIndexColon = address.lastIndexOf(58);
        int indexDot = address.indexOf(46);
        int lastIndexDot = address.lastIndexOf(46);
        if (indexColon > -1 && lastIndexColon > indexColon) {
            if (indexDot == -1) {
                matcher = new Ip6AddressMatcher();
                AddressUtil.parseIpv6(matcher, address);
            } else {
                if (indexDot >= lastIndexDot) {
                    throw new InvalidAddressException(address);
                }
                int lastIndexColon2 = address.lastIndexOf(58);
                String host2 = address.substring(lastIndexColon2 + 1);
                matcher = new Ip4AddressMatcher();
                AddressUtil.parseIpv4(matcher, host2);
            }
        } else if (indexDot > -1 && lastIndexDot > indexDot && indexColon == -1) {
            matcher = new Ip4AddressMatcher();
            AddressUtil.parseIpv4(matcher, address);
        } else {
            throw new InvalidAddressException(address);
        }
        return matcher;
    }

    public static Collection<Integer> getOutboundPorts(Collection<Integer> ports, Collection<String> portDefinitions) {
        if (ports == null) {
            ports = Collections.emptySet();
        }
        if (portDefinitions == null) {
            portDefinitions = Collections.emptySet();
        }
        if (portDefinitions.isEmpty() && ports.isEmpty()) {
            return Collections.emptySet();
        }
        if (portDefinitions.contains("*") || portDefinitions.contains("0")) {
            return Collections.emptySet();
        }
        HashSet<Integer> selectedPorts = new HashSet<Integer>(ports);
        AddressUtil.transformPortDefinitionsToPorts(portDefinitions, selectedPorts);
        if (selectedPorts.contains(0)) {
            return Collections.emptySet();
        }
        return selectedPorts;
    }

    private static void transformPortDefinitionsToPorts(Collection<String> portDefinitions, Set<Integer> ports) {
        for (String portDef : portDefinitions) {
            String[] portDefs;
            for (String def : portDefs = portDef.split("[,; ]")) {
                if ((def = def.trim()).isEmpty()) continue;
                int dashPos = def.indexOf(45);
                if (dashPos > 0) {
                    int start = Integer.parseInt(def.substring(0, dashPos));
                    int end = Integer.parseInt(def.substring(dashPos + 1));
                    for (int port = start; port <= end; ++port) {
                        ports.add(port);
                    }
                    continue;
                }
                ports.add(Integer.parseInt(def));
            }
        }
    }

    private static void parseIpv4(AddressMatcher matcher, String address) {
        String[] parts = address.split("\\.");
        if (parts.length != 4) {
            throw new InvalidAddressException(address);
        }
        for (String part : parts) {
            if (AddressUtil.isValidIpAddressPart(part, false)) continue;
            throw new InvalidAddressException(address);
        }
        matcher.setAddress(parts);
    }

    private static boolean isValidIpAddressPart(String part, boolean ipv6) {
        boolean isValid = true;
        if (part.length() == 1 && "*".equals(part)) {
            return true;
        }
        int rangeIndex = part.indexOf(45);
        if (rangeIndex > -1 && (rangeIndex != part.lastIndexOf(45) || rangeIndex == part.length() - 1)) {
            return false;
        }
        String[] subParts = rangeIndex > -1 ? part.split("\\-") : new String[]{part};
        try {
            for (String subPart : subParts) {
                int num;
                if (ipv6) {
                    num = Integer.parseInt(subPart, 16);
                    if (num <= 65535) continue;
                    isValid = false;
                } else {
                    num = Integer.parseInt(subPart);
                    if (num <= 255) continue;
                    isValid = false;
                }
                break;
            }
        }
        catch (NumberFormatException e) {
            isValid = false;
        }
        return isValid;
    }

    private static void parseIpv6(AddressMatcher matcher, String addrs) {
        Collection<String> ipString;
        String[] parts;
        String address = addrs;
        if (address.indexOf(37) > -1) {
            parts = address.split("\\%");
            address = parts[0];
        }
        if ((ipString = AddressUtil.parseIPV6parts(parts = address.split("((?<=:)|(?=:))"), address)).size() != 8) {
            throw new InvalidAddressException(address);
        }
        String[] addressParts = ipString.toArray(new String[0]);
        AddressUtil.checkIfAddressPartsAreValid(addressParts, address);
        matcher.setAddress(addressParts);
    }

    private static Collection<String> parseIPV6parts(String[] parts, String address) {
        LinkedList<String> ipString = new LinkedList<String>();
        int count = 0;
        int mark = -1;
        for (int i = 0; i < parts.length; ++i) {
            String nextPart;
            String part = parts[i];
            String string = nextPart = i < parts.length - 1 ? parts[i + 1] : null;
            if ("".equals(part)) continue;
            if (":".equals(part) && ":".equals(nextPart)) {
                if (mark != -1) {
                    throw new InvalidAddressException(address);
                }
                mark = count;
                continue;
            }
            if (":".equals(part)) continue;
            ++count;
            ipString.add(part);
        }
        if (mark > -1) {
            int remaining = 8 - count;
            for (int i = 0; i < remaining; ++i) {
                ipString.add(i + mark, "0");
            }
        }
        return ipString;
    }

    private static void checkIfAddressPartsAreValid(String[] addressParts, String address) {
        for (String part : addressParts) {
            if (AddressUtil.isValidIpAddressPart(part, true)) continue;
            throw new InvalidAddressException(address);
        }
    }

    public static class InvalidAddressException
    extends IllegalArgumentException {
        public InvalidAddressException(String message) {
            this(message, true);
        }

        public InvalidAddressException(String message, boolean prependText) {
            super((prependText ? "Illegal IP address format: " : "") + message);
        }
    }

    static class Ip6AddressMatcher
    extends AddressMatcher {
        public Ip6AddressMatcher() {
            super(new String[8]);
        }

        @Override
        public boolean isIPv4() {
            return false;
        }

        @Override
        public boolean isIPv6() {
            return true;
        }

        @Override
        public void setAddress(String[] ip) {
            System.arraycopy(ip, 0, this.address, 0, ip.length);
        }

        @Override
        public boolean match(AddressMatcher matcher) {
            if (matcher.isIPv4()) {
                return false;
            }
            Ip6AddressMatcher a = (Ip6AddressMatcher)matcher;
            String[] mask = this.address;
            String[] input = a.address;
            return this.match(mask, input, 16);
        }

        @Override
        public String getAddress() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.address.length; ++i) {
                sb.append(this.address[i]);
                if (i == this.address.length - 1) continue;
                sb.append(':');
            }
            return sb.toString();
        }
    }

    static class Ip4AddressMatcher
    extends AddressMatcher {
        public Ip4AddressMatcher() {
            super(new String[4]);
        }

        @Override
        public boolean isIPv4() {
            return true;
        }

        @Override
        public boolean isIPv6() {
            return false;
        }

        @Override
        public void setAddress(String[] ip) {
            System.arraycopy(ip, 0, this.address, 0, ip.length);
        }

        @Override
        public boolean match(AddressMatcher matcher) {
            if (matcher.isIPv6()) {
                return false;
            }
            String[] mask = this.address;
            String[] input = ((Ip4AddressMatcher)matcher).address;
            return this.match(mask, input, 10);
        }

        @Override
        public String getAddress() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.address.length; ++i) {
                sb.append(this.address[i]);
                if (i == this.address.length - 1) continue;
                sb.append('.');
            }
            return sb.toString();
        }
    }

    public static abstract class AddressMatcher {
        protected final String[] address;

        protected AddressMatcher(String[] address) {
            this.address = address;
        }

        public abstract boolean isIPv4();

        public abstract boolean isIPv6();

        public abstract void setAddress(String[] var1);

        protected final boolean match(String[] mask, String[] input, int radix) {
            if (input != null && mask != null) {
                for (int i = 0; i < mask.length; ++i) {
                    if (this.doMatch(mask[i], input[i], radix)) continue;
                    return false;
                }
                return true;
            }
            return false;
        }

        protected final boolean doMatch(String mask, String input, int radix) {
            int dashIndex = mask.indexOf(45);
            int ipa = Integer.parseInt(input, radix);
            if ("*".equals(mask)) {
                return true;
            }
            if (dashIndex != -1) {
                int start = Integer.parseInt(mask.substring(0, dashIndex).trim(), radix);
                int end = Integer.parseInt(mask.substring(dashIndex + 1).trim(), radix);
                if (ipa >= start && ipa <= end) {
                    return true;
                }
            } else {
                int x = Integer.parseInt(mask, radix);
                if (x == ipa) {
                    return true;
                }
            }
            return false;
        }

        public abstract String getAddress();

        public abstract boolean match(AddressMatcher var1);

        public boolean match(String address) {
            try {
                return this.match(AddressUtil.getAddressMatcher(address));
            }
            catch (Exception e) {
                return false;
            }
        }

        public String toString() {
            return this.getClass().getSimpleName() + '{' + this.getAddress() + '}';
        }
    }

    public static class AddressHolder {
        private final String address;
        private final String scopeId;
        private final int port;

        public AddressHolder(String address, int port, String scopeId) {
            this.address = address;
            this.scopeId = scopeId;
            this.port = port;
        }

        public String toString() {
            return "AddressHolder [" + this.address + "]:" + this.port;
        }

        public String getAddress() {
            return this.address;
        }

        public String getScopeId() {
            return this.scopeId;
        }

        public int getPort() {
            return this.port;
        }
    }
}

