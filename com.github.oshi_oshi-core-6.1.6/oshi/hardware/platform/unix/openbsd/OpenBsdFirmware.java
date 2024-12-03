/*
 * Decompiled with CFR 0.152.
 */
package oshi.hardware.platform.unix.openbsd;

import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.tuples.Triplet;

@Immutable
public class OpenBsdFirmware
extends AbstractFirmware {
    private final Supplier<Triplet<String, String, String>> manufVersRelease = Memoizer.memoize(OpenBsdFirmware::readDmesg);

    @Override
    public String getManufacturer() {
        return this.manufVersRelease.get().getA();
    }

    @Override
    public String getVersion() {
        return this.manufVersRelease.get().getB();
    }

    @Override
    public String getReleaseDate() {
        return this.manufVersRelease.get().getC();
    }

    private static Triplet<String, String, String> readDmesg() {
        String version = null;
        String vendor = null;
        String releaseDate = "";
        List<String> dmesg = ExecutingCommand.runNative("dmesg");
        for (String line : dmesg) {
            if (!line.startsWith("bios0: vendor")) continue;
            version = ParseUtil.getStringBetween(line, '\"');
            releaseDate = ParseUtil.parseMmDdYyyyToYyyyMmDD(ParseUtil.parseLastString(line));
            vendor = line.split("vendor")[1].trim();
        }
        return new Triplet<String, String, String>(Util.isBlank(vendor) ? "unknown" : vendor, Util.isBlank(version) ? "unknown" : version, Util.isBlank(releaseDate) ? "unknown" : releaseDate);
    }
}

