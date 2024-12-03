/*
 * Decompiled with CFR 0.152.
 */
package oshi.hardware.platform.linux;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@ThreadSafe
final class LinuxSensors
extends AbstractSensors {
    private static final String TEMP = "temp";
    private static final String FAN = "fan";
    private static final String VOLTAGE = "in";
    private static final String[] SENSORS = new String[]{"temp", "fan", "in"};
    private static final String HWMON = "hwmon";
    private static final String HWMON_PATH = "/sys/class/hwmon/hwmon";
    private static final String THERMAL_ZONE = "thermal_zone";
    private static final String THERMAL_ZONE_PATH = "/sys/class/thermal/thermal_zone";
    private static final boolean IS_PI = LinuxSensors.queryCpuTemperatureFromVcGenCmd() > 0.0;
    private final Map<String, String> sensorsMap = new HashMap<String, String>();

    LinuxSensors() {
        if (!IS_PI) {
            this.populateSensorsMapFromHwmon();
            if (!this.sensorsMap.containsKey(TEMP)) {
                this.populateSensorsMapFromThermalZone();
            }
        }
    }

    private void populateSensorsMapFromHwmon() {
        String[] stringArray = SENSORS;
        int n = stringArray.length;
        for (int i = 0; i < n; ++i) {
            String sensor;
            String sensorPrefix = sensor = stringArray[i];
            this.getSensorFilesFromPath(HWMON_PATH, sensor, f -> {
                try {
                    return f.getName().startsWith(sensorPrefix) && f.getName().endsWith("_input") && FileUtil.getIntFromFile(f.getCanonicalPath()) > 0;
                }
                catch (IOException e) {
                    return false;
                }
            });
        }
    }

    private void populateSensorsMapFromThermalZone() {
        this.getSensorFilesFromPath(THERMAL_ZONE_PATH, TEMP, f -> f.getName().equals(TEMP));
    }

    private void getSensorFilesFromPath(String sensorPath, String sensor, FileFilter sensorFileFilter) {
        int i = 0;
        while (Paths.get(sensorPath + i, new String[0]).toFile().isDirectory()) {
            String path = sensorPath + i;
            File dir = new File(path);
            File[] matchingFiles = dir.listFiles(sensorFileFilter);
            if (matchingFiles != null && matchingFiles.length > 0) {
                this.sensorsMap.put(sensor, String.format("%s/%s", path, sensor));
            }
            ++i;
        }
    }

    @Override
    public double queryCpuTemperature() {
        if (IS_PI) {
            return LinuxSensors.queryCpuTemperatureFromVcGenCmd();
        }
        String tempStr = this.sensorsMap.get(TEMP);
        if (tempStr != null) {
            long millidegrees = 0L;
            if (tempStr.contains(HWMON)) {
                millidegrees = FileUtil.getLongFromFile(String.format("%s1_input", tempStr));
                if (millidegrees > 0L) {
                    return (double)millidegrees / 1000.0;
                }
                long sum = 0L;
                int count = 0;
                for (int i = 2; i <= 6; ++i) {
                    millidegrees = FileUtil.getLongFromFile(String.format("%s%d_input", tempStr, i));
                    if (millidegrees <= 0L) continue;
                    sum += millidegrees;
                    ++count;
                }
                if (count > 0) {
                    return (double)sum / ((double)count * 1000.0);
                }
            } else if (tempStr.contains(THERMAL_ZONE) && (millidegrees = FileUtil.getLongFromFile(tempStr)) > 0L) {
                return (double)millidegrees / 1000.0;
            }
        }
        return 0.0;
    }

    private static double queryCpuTemperatureFromVcGenCmd() {
        String tempStr = ExecutingCommand.getFirstAnswer("vcgencmd measure_temp");
        if (tempStr.startsWith("temp=")) {
            return ParseUtil.parseDoubleOrDefault(tempStr.replaceAll("[^\\d|\\.]+", ""), 0.0);
        }
        return 0.0;
    }

    @Override
    public int[] queryFanSpeeds() {
        String fanStr;
        if (!IS_PI && (fanStr = this.sensorsMap.get(FAN)) != null) {
            String fanPath;
            ArrayList<Integer> speeds = new ArrayList<Integer>();
            int fan = 1;
            while (new File(fanPath = String.format("%s%d_input", fanStr, fan)).exists()) {
                speeds.add(FileUtil.getIntFromFile(fanPath));
                ++fan;
            }
            int[] fanSpeeds = new int[speeds.size()];
            for (int i = 0; i < speeds.size(); ++i) {
                fanSpeeds[i] = (Integer)speeds.get(i);
            }
            return fanSpeeds;
        }
        return new int[0];
    }

    @Override
    public double queryCpuVoltage() {
        if (IS_PI) {
            return LinuxSensors.queryCpuVoltageFromVcGenCmd();
        }
        String voltageStr = this.sensorsMap.get(VOLTAGE);
        if (voltageStr != null) {
            return (double)FileUtil.getIntFromFile(String.format("%s1_input", voltageStr)) / 1000.0;
        }
        return 0.0;
    }

    private static double queryCpuVoltageFromVcGenCmd() {
        String voltageStr = ExecutingCommand.getFirstAnswer("vcgencmd measure_volts core");
        if (voltageStr.startsWith("volt=")) {
            return ParseUtil.parseDoubleOrDefault(voltageStr.replaceAll("[^\\d|\\.]+", ""), 0.0);
        }
        return 0.0;
    }
}

