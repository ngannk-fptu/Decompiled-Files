/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.ssi;

import java.io.PrintWriter;
import java.util.Collection;
import org.apache.catalina.ssi.SSICommand;
import org.apache.catalina.ssi.SSIMediator;

public class SSIPrintenv
implements SSICommand {
    @Override
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues, PrintWriter writer) {
        long lastModified = 0L;
        if (paramNames.length > 0) {
            String errorMessage = ssiMediator.getConfigErrMsg();
            writer.write(errorMessage);
        } else {
            Collection<String> variableNames = ssiMediator.getVariableNames();
            for (String variableName : variableNames) {
                String variableValue = ssiMediator.getVariableValue(variableName, "entity");
                if (variableValue == null) {
                    variableValue = "(none)";
                }
                writer.write(variableName);
                writer.write(61);
                writer.write(variableValue);
                writer.write(10);
                lastModified = System.currentTimeMillis();
            }
        }
        return lastModified;
    }
}

