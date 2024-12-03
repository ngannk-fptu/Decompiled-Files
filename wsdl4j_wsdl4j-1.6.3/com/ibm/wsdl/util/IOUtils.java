/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;

public class IOUtils {
    static boolean debug = false;

    public static String getStringFromReader(Reader reader) throws IOException {
        String tempLine;
        BufferedReader bufIn = new BufferedReader(reader);
        StringWriter swOut = new StringWriter();
        PrintWriter pwOut = new PrintWriter(swOut);
        while ((tempLine = bufIn.readLine()) != null) {
            pwOut.println(tempLine);
        }
        pwOut.flush();
        return swOut.toString();
    }
}

