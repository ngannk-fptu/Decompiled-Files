/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ssi;

import java.io.PrintWriter;
import org.apache.catalina.ssi.SSICommand;
import org.apache.catalina.ssi.SSIMediator;
import org.apache.tomcat.util.res.StringManager;

public final class SSIConfig
implements SSICommand {
    private static final StringManager sm = StringManager.getManager(SSIConfig.class);

    @Override
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues, PrintWriter writer) {
        for (int i = 0; i < paramNames.length; ++i) {
            String paramName = paramNames[i];
            String paramValue = paramValues[i];
            String substitutedValue = ssiMediator.substituteVariables(paramValue);
            if (paramName.equalsIgnoreCase("errmsg")) {
                ssiMediator.setConfigErrMsg(substitutedValue);
                continue;
            }
            if (paramName.equalsIgnoreCase("sizefmt")) {
                ssiMediator.setConfigSizeFmt(substitutedValue);
                continue;
            }
            if (paramName.equalsIgnoreCase("timefmt")) {
                ssiMediator.setConfigTimeFmt(substitutedValue);
                continue;
            }
            ssiMediator.log(sm.getString("ssiCommand.invalidAttribute", new Object[]{paramName}));
            String configErrMsg = ssiMediator.getConfigErrMsg();
            writer.write(configErrMsg);
        }
        return 0L;
    }
}

