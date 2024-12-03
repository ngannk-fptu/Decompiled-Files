/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.lang;

import java.util.Properties;

public final class Platform {
    final OperatingSystem os;
    final String version;
    final Architecture architecture;
    private static final Platform INSTANCE = new Platform();

    private Platform() {
        this(System.getProperties());
    }

    Platform(Properties properties) {
        this.os = Platform.normalizeOperatingSystem(properties.getProperty("os.name"));
        this.version = properties.getProperty("os.version");
        this.architecture = Platform.normalizeArchitecture(this.os, properties.getProperty("os.arch"));
    }

    static OperatingSystem normalizeOperatingSystem(String string) {
        String string2 = string;
        if (string2 == null) {
            throw new IllegalStateException("System property \"os.name\" == null");
        }
        if ((string2 = string2.toLowerCase()).startsWith("windows")) {
            return OperatingSystem.Windows;
        }
        if (string2.startsWith("linux")) {
            return OperatingSystem.Linux;
        }
        if (string2.startsWith("mac os") || string2.startsWith("darwin")) {
            return OperatingSystem.MacOS;
        }
        if (string2.startsWith("solaris") || string2.startsWith("sunos")) {
            return OperatingSystem.Solaris;
        }
        return OperatingSystem.Unknown;
    }

    static Architecture normalizeArchitecture(OperatingSystem operatingSystem, String string) {
        String string2 = string;
        if (string2 == null) {
            throw new IllegalStateException("System property \"os.arch\" == null");
        }
        string2 = string2.toLowerCase();
        if (operatingSystem == OperatingSystem.Windows && (string2.startsWith("x86") || string2.startsWith("i386"))) {
            return Architecture.X86;
        }
        if (operatingSystem == OperatingSystem.Linux) {
            if (string2.startsWith("x86") || string2.startsWith("i386")) {
                return Architecture.I386;
            }
            if (string2.startsWith("i686")) {
                return Architecture.I686;
            }
            if (string2.startsWith("power") || string2.startsWith("ppc")) {
                return Architecture.PPC;
            }
        } else if (operatingSystem == OperatingSystem.MacOS) {
            if (string2.startsWith("power") || string2.startsWith("ppc")) {
                return Architecture.PPC;
            }
            if (string2.startsWith("x86")) {
                return Architecture.X86;
            }
            if (string2.startsWith("i386")) {
                return Architecture.X86;
            }
        } else if (operatingSystem == OperatingSystem.Solaris) {
            if (string2.startsWith("sparc")) {
                return Architecture.SPARC;
            }
            if (string2.startsWith("x86")) {
                return Architecture.X86;
            }
        }
        return Architecture.Unknown;
    }

    public static Platform get() {
        return INSTANCE;
    }

    public OperatingSystem getOS() {
        return this.os;
    }

    public String getVersion() {
        return this.version;
    }

    public Architecture getArchitecture() {
        return this.architecture;
    }

    public static OperatingSystem os() {
        return Platform.INSTANCE.os;
    }

    public static String version() {
        return Platform.INSTANCE.version;
    }

    public static Architecture arch() {
        return Platform.INSTANCE.architecture;
    }

    public static enum OperatingSystem {
        Windows("Windows", "win"),
        Linux("Linux", "lnx"),
        Solaris("Solaris", "sun"),
        MacOS("Mac OS", "osx"),
        Unknown(System.getProperty("os.name"), null);

        final String id;
        final String name;

        private OperatingSystem(String string2, String string3) {
            this.name = string2;
            this.id = string3 != null ? string3 : string2.toLowerCase();
        }

        public String getName() {
            return this.name;
        }

        public String id() {
            return this.id;
        }

        public String toString() {
            return String.format("%s (%s)", this.id, this.name);
        }
    }

    public static enum Architecture {
        X86("x86"),
        I386("i386"),
        I686("i686"),
        PPC("ppc"),
        SPARC("sparc"),
        Unknown(System.getProperty("os.arch"));

        final String name;

        private Architecture(String string2) {
            this.name = string2;
        }

        public String toString() {
            return this.name;
        }
    }
}

