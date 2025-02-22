/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aws;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PortRange {
    private static final Pattern PORT_NUMBER_REGEX = Pattern.compile("^(\\d+)$");
    private static final Pattern PORT_RANGE_REGEX = Pattern.compile("^(\\d+)-(\\d+)$");
    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;
    private final int fromPort;
    private final int toPort;

    PortRange(String spec) {
        Matcher portNumberMatcher = PORT_NUMBER_REGEX.matcher(spec);
        Matcher portRangeMatcher = PORT_RANGE_REGEX.matcher(spec);
        if (portNumberMatcher.find()) {
            int port;
            this.fromPort = port = Integer.parseInt(spec);
            this.toPort = port;
        } else if (portRangeMatcher.find()) {
            this.fromPort = Integer.parseInt(portRangeMatcher.group(1));
            this.toPort = Integer.parseInt(portRangeMatcher.group(2));
        } else {
            throw new IllegalArgumentException(String.format("Invalid port range specification: %s", spec));
        }
        this.validatePorts();
    }

    private void validatePorts() {
        if (this.fromPort < 0 || this.fromPort > 65535) {
            throw new IllegalArgumentException(String.format("Specified port (%s) outside of port range (%s-%s)", this.fromPort, 0, 65535));
        }
        if (this.toPort < 0 || this.toPort > 65535) {
            throw new IllegalArgumentException(String.format("Specified port (%s) outside of port range (%s-%s)", this.toPort, 0, 65535));
        }
        if (this.fromPort > this.toPort) {
            throw new IllegalArgumentException(String.format("Port %s is greater than %s", this.fromPort, this.toPort));
        }
    }

    int getFromPort() {
        return this.fromPort;
    }

    int getToPort() {
        return this.toPort;
    }
}

