/*
 * Decompiled with CFR 0.152.
 */
package oshi.driver.linux;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.Util;

@ThreadSafe
public final class Sysfs {
    private Sysfs() {
    }

    public static String querySystemVendor() {
        String sysVendor = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/sys_vendor").trim();
        if (!sysVendor.isEmpty()) {
            return sysVendor;
        }
        return null;
    }

    public static String queryProductModel() {
        String productName = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_name").trim();
        String productVersion = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_version").trim();
        if (productName.isEmpty()) {
            if (!productVersion.isEmpty()) {
                return productVersion;
            }
        } else {
            if (!productVersion.isEmpty() && !"None".equals(productVersion)) {
                return productName + " (version: " + productVersion + ")";
            }
            return productName;
        }
        return null;
    }

    public static String queryProductSerial() {
        String serial = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_serial");
        if (!serial.isEmpty() && !"None".equals(serial)) {
            return serial;
        }
        return Sysfs.queryBoardSerial();
    }

    public static String queryUUID() {
        String uuid = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_uuid");
        if (!uuid.isEmpty() && !"None".equals(uuid)) {
            return uuid;
        }
        return null;
    }

    public static String queryBoardVendor() {
        String boardVendor = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_vendor").trim();
        if (!boardVendor.isEmpty()) {
            return boardVendor;
        }
        return null;
    }

    public static String queryBoardModel() {
        String boardName = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_name").trim();
        if (!boardName.isEmpty()) {
            return boardName;
        }
        return null;
    }

    public static String queryBoardVersion() {
        String boardVersion = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_version").trim();
        if (!boardVersion.isEmpty()) {
            return boardVersion;
        }
        return null;
    }

    public static String queryBoardSerial() {
        String boardSerial = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_serial").trim();
        if (!boardSerial.isEmpty()) {
            return boardSerial;
        }
        return null;
    }

    public static String queryBiosVendor() {
        String biosVendor = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_vendor").trim();
        if (biosVendor.isEmpty()) {
            return biosVendor;
        }
        return null;
    }

    public static String queryBiosDescription() {
        String modalias = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/modalias").trim();
        if (!modalias.isEmpty()) {
            return modalias;
        }
        return null;
    }

    public static String queryBiosVersion(String biosRevision) {
        String biosVersion = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_version").trim();
        if (!biosVersion.isEmpty()) {
            return biosVersion + (Util.isBlank(biosRevision) ? "" : " (revision " + biosRevision + ")");
        }
        return null;
    }

    public static String queryBiosReleaseDate() {
        String biosDate = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_date").trim();
        if (!biosDate.isEmpty()) {
            return ParseUtil.parseMmDdYyyyToYyyyMmDD(biosDate);
        }
        return null;
    }
}

