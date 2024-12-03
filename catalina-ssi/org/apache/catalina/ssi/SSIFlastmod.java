/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.util.Strftime
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;
import org.apache.catalina.ssi.SSICommand;
import org.apache.catalina.ssi.SSIMediator;
import org.apache.catalina.util.Strftime;
import org.apache.tomcat.util.res.StringManager;

public final class SSIFlastmod
implements SSICommand {
    private static final StringManager sm = StringManager.getManager(SSIFlastmod.class);

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
                    Date date = new Date(lastModified);
                    String configTimeFmt = ssiMediator.getConfigTimeFmt();
                    writer.write(this.formatDate(date, configTimeFmt));
                    continue;
                }
                ssiMediator.log(sm.getString("ssiCommand.invalidAttribute", new Object[]{paramName}));
                writer.write(configErrMsg);
                continue;
            }
            catch (IOException e) {
                ssiMediator.log(sm.getString("ssiFlastmod.noLastModified", new Object[]{substitutedValue}), e);
                writer.write(configErrMsg);
            }
        }
        return lastModified;
    }

    protected String formatDate(Date date, String configTimeFmt) {
        Strftime strftime = new Strftime(configTimeFmt, Locale.US);
        return strftime.format(date);
    }
}

