/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.profile.internal;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Scanner;

public abstract class AbstractProfilesConfigFileScanner {
    protected abstract void onEmptyOrCommentLine(String var1, String var2);

    protected abstract void onProfileStartingLine(String var1, String var2);

    protected abstract void onProfileEndingLine(String var1);

    protected abstract void onEndOfFile();

    protected abstract void onProfileProperty(String var1, String var2, String var3, boolean var4, String var5);

    protected boolean isSupportedProperty(String propertyName) {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void run(Scanner scanner) {
        String currentProfileName = null;
        try {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                boolean atNewProfileStartingLine;
                ++lineNumber;
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    this.onEmptyOrCommentLine(currentProfileName, line);
                    continue;
                }
                String newProfileName = AbstractProfilesConfigFileScanner.parseProfileName(line);
                boolean bl = atNewProfileStartingLine = newProfileName != null;
                if (atNewProfileStartingLine) {
                    if (currentProfileName != null) {
                        this.onProfileEndingLine(currentProfileName);
                    }
                    this.onProfileStartingLine(newProfileName, line);
                    currentProfileName = newProfileName;
                    continue;
                }
                Map.Entry<String, String> property = AbstractProfilesConfigFileScanner.parsePropertyLine(line, lineNumber);
                if (currentProfileName == null) {
                    throw new IllegalArgumentException("Property is defined without a preceding profile name on line " + lineNumber);
                }
                this.onProfileProperty(currentProfileName, property.getKey(), property.getValue(), this.isSupportedProperty(property.getKey()), line);
            }
            if (currentProfileName != null) {
                this.onProfileEndingLine(currentProfileName);
            }
            this.onEndOfFile();
        }
        finally {
            scanner.close();
        }
    }

    private static String parseProfileName(String trimmedLine) {
        if (trimmedLine.startsWith("[") && trimmedLine.endsWith("]")) {
            String profileName = trimmedLine.substring(1, trimmedLine.length() - 1);
            return profileName.trim();
        }
        return null;
    }

    private static Map.Entry<String, String> parsePropertyLine(String propertyLine, int lineNumber) {
        String[] pair = propertyLine.split("=", 2);
        if (pair.length != 2) {
            throw new IllegalArgumentException("Invalid property format: no '=' character is found on line " + lineNumber);
        }
        String propertyKey = pair[0].trim();
        String propertyValue = pair[1].trim();
        return new AbstractMap.SimpleImmutableEntry<String, String>(propertyKey, propertyValue);
    }
}

