/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Platform
 */
package oshi;

import com.sun.jna.Platform;
import java.util.function.Supplier;
import oshi.PlatformEnum;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.platform.linux.LinuxHardwareAbstractionLayer;
import oshi.hardware.platform.mac.MacHardwareAbstractionLayer;
import oshi.hardware.platform.unix.aix.AixHardwareAbstractionLayer;
import oshi.hardware.platform.unix.freebsd.FreeBsdHardwareAbstractionLayer;
import oshi.hardware.platform.unix.openbsd.OpenBsdHardwareAbstractionLayer;
import oshi.hardware.platform.unix.solaris.SolarisHardwareAbstractionLayer;
import oshi.hardware.platform.windows.WindowsHardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.software.os.mac.MacOperatingSystem;
import oshi.software.os.unix.aix.AixOperatingSystem;
import oshi.software.os.unix.freebsd.FreeBsdOperatingSystem;
import oshi.software.os.unix.openbsd.OpenBsdOperatingSystem;
import oshi.software.os.unix.solaris.SolarisOperatingSystem;
import oshi.software.os.windows.WindowsOperatingSystem;
import oshi.util.Memoizer;

public class SystemInfo {
    private static final PlatformEnum CURRENT_PLATFORM = PlatformEnum.getValue(Platform.getOSType());
    private static final String NOT_SUPPORTED = "Operating system not supported: ";
    private final Supplier<OperatingSystem> os = Memoizer.memoize(SystemInfo::createOperatingSystem);
    private final Supplier<HardwareAbstractionLayer> hardware = Memoizer.memoize(SystemInfo::createHardware);

    public static PlatformEnum getCurrentPlatform() {
        return CURRENT_PLATFORM;
    }

    public OperatingSystem getOperatingSystem() {
        return this.os.get();
    }

    private static OperatingSystem createOperatingSystem() {
        switch (CURRENT_PLATFORM) {
            case WINDOWS: {
                return new WindowsOperatingSystem();
            }
            case LINUX: {
                return new LinuxOperatingSystem();
            }
            case MACOS: {
                return new MacOperatingSystem();
            }
            case SOLARIS: {
                return new SolarisOperatingSystem();
            }
            case FREEBSD: {
                return new FreeBsdOperatingSystem();
            }
            case AIX: {
                return new AixOperatingSystem();
            }
            case OPENBSD: {
                return new OpenBsdOperatingSystem();
            }
        }
        throw new UnsupportedOperationException(NOT_SUPPORTED + CURRENT_PLATFORM.getName());
    }

    public HardwareAbstractionLayer getHardware() {
        return this.hardware.get();
    }

    private static HardwareAbstractionLayer createHardware() {
        switch (CURRENT_PLATFORM) {
            case WINDOWS: {
                return new WindowsHardwareAbstractionLayer();
            }
            case LINUX: {
                return new LinuxHardwareAbstractionLayer();
            }
            case MACOS: {
                return new MacHardwareAbstractionLayer();
            }
            case SOLARIS: {
                return new SolarisHardwareAbstractionLayer();
            }
            case FREEBSD: {
                return new FreeBsdHardwareAbstractionLayer();
            }
            case AIX: {
                return new AixHardwareAbstractionLayer();
            }
            case OPENBSD: {
                return new OpenBsdHardwareAbstractionLayer();
            }
        }
        throw new UnsupportedOperationException(NOT_SUPPORTED + CURRENT_PLATFORM.getName());
    }
}

