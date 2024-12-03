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
import org.apache.catalina.ssi.SSIStopProcessingException;
import org.apache.tomcat.util.res.StringManager;

public class SSISet
implements SSICommand {
    private static final StringManager sm = StringManager.getManager(SSISet.class);

    @Override
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues, PrintWriter writer) throws SSIStopProcessingException {
        long lastModified = 0L;
        String errorMessage = ssiMediator.getConfigErrMsg();
        String variableName = null;
        for (int i = 0; i < paramNames.length; ++i) {
            String paramName = paramNames[i];
            String paramValue = paramValues[i];
            if (paramName.equalsIgnoreCase("var")) {
                variableName = paramValue;
                continue;
            }
            if (paramName.equalsIgnoreCase("value")) {
                if (variableName != null) {
                    String substitutedValue = ssiMediator.substituteVariables(paramValue);
                    ssiMediator.setVariableValue(variableName, substitutedValue);
                    lastModified = System.currentTimeMillis();
                    continue;
                }
                ssiMediator.log(sm.getString("ssiSet.noVariable"));
                writer.write(errorMessage);
                throw new SSIStopProcessingException();
            }
            ssiMediator.log(sm.getString("ssiCommand.invalidAttribute", new Object[]{paramName}));
            writer.write(errorMessage);
            throw new SSIStopProcessingException();
        }
        return lastModified;
    }
}

