/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.hercules;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LogScanHelper {
    File makeFile(String fileName) {
        return new File(fileName);
    }

    BufferedReader makeReader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}

