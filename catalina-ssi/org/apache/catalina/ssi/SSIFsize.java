/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import org.apache.catalina.ssi.SSICommand;
import org.apache.catalina.ssi.SSIMediator;
import org.apache.tomcat.util.res.StringManager;

public final class SSIFsize
implements SSICommand {
    private static final StringManager sm = StringManager.getManager(SSIFsize.class);
    static final int ONE_KIBIBYTE = 1024;
    static final int ONE_MEBIBYTE = 0x100000;

    @Override
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues, PrintWriter writer) {
        long lastModified = 0L;
        String configErrMsg = ssiMediator.getConfigErrMsg();
        for (int i = 0; i < paramNames.length; ++i) {
            String paramName = paramNames[i];
            String paramValue = paramValues[i];
            String substitutedValue = ssiMediator.substituteVariables(paramValue);
            try {
                if (paramName.equalsIgnoreCase("file") || paramName.equalsIgnoreCase("virtual")) {
                    boolean virtual = paramName.equalsIgnoreCase("virtual");
                    lastModified = ssiMediator.getFileLastModified(substitutedValue, virtual);
                    long size = ssiMediator.getFileSize(substitutedValue, virtual);
                    String configSizeFmt = ssiMediator.getConfigSizeFmt();
                    writer.write(this.formatSize(size, configSizeFmt));
                    continue;
                }
                ssiMediator.log(sm.getString("ssiCommand.invalidAttribute", new Object[]{paramName}));
                writer.write(configErrMsg);
                continue;
            }
            catch (IOException e) {
                ssiMediator.log(sm.getString("ssiFsize.noSize", new Object[]{substitutedValue}), e);
                writer.write(configErrMsg);
            }
        }
        return lastModified;
    }

    public String repeat(char aChar, int numChars) {
        if (numChars < 0) {
            throw new IllegalArgumentException(sm.getString("ssiFsize.invalidNumChars"));
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < numChars; ++i) {
            buf.append(aChar);
        }
        return buf.toString();
    }

    public String padLeft(String str, int maxChars) {
        String result = str;
        int charsToAdd = maxChars - str.length();
        if (charsToAdd > 0) {
            result = this.repeat(' ', charsToAdd) + str;
        }
        return result;
    }

    protected String formatSize(long size, String format) {
        String retString = "";
        if (format.equalsIgnoreCase("bytes")) {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            retString = decimalFormat.format(size);
        } else {
            if (size < 0L) {
                retString = "-";
            } else if (size == 0L) {
                retString = "0k";
            } else if (size < 1024L) {
                retString = "1k";
            } else if (size < 0x100000L) {
                retString = Long.toString((size + 512L) / 1024L);
                retString = retString + "k";
            } else if (size < 0x6300000L) {
                DecimalFormat decimalFormat = new DecimalFormat("0.0M");
                retString = decimalFormat.format((double)size / 1048576.0);
            } else {
                retString = Long.toString((size + 541696L) / 0x100000L);
                retString = retString + "M";
            }
            retString = this.padLeft(retString, 5);
        }
        return retString;
    }
}

