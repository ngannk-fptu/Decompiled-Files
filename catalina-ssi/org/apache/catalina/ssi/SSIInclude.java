/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.PrintWriter;
import org.apache.catalina.ssi.SSICommand;
import org.apache.catalina.ssi.SSIMediator;
import org.apache.tomcat.util.res.StringManager;

public final class SSIInclude
implements SSICommand {
    private static final StringManager sm = StringManager.getManager(SSIInclude.class);

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
                    String text = ssiMediator.getFileText(substitutedValue, virtual);
                    writer.write(text);
                    continue;
                }
                ssiMediator.log(sm.getString("ssiCommand.invalidAttribute", new Object[]{paramName}));
                writer.write(configErrMsg);
                continue;
            }
            catch (IOException e) {
                ssiMediator.log(sm.getString("ssiInclude.includeFailed", new Object[]{substitutedValue}), e);
                writer.write(configErrMsg);
            }
        }
        return lastModified;
    }
}

