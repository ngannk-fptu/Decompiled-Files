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

public class SSIEcho
implements SSICommand {
    private static final StringManager sm = StringManager.getManager(SSIEcho.class);
    protected static final String DEFAULT_ENCODING = "entity";
    protected static final String MISSING_VARIABLE_VALUE = "(none)";

    @Override
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues, PrintWriter writer) {
        String variableValue;
        String encoding = DEFAULT_ENCODING;
        String originalValue = null;
        String errorMessage = ssiMediator.getConfigErrMsg();
        for (int i = 0; i < paramNames.length; ++i) {
            String paramName = paramNames[i];
            String paramValue = paramValues[i];
            if (paramName.equalsIgnoreCase("var")) {
                originalValue = paramValue;
                continue;
            }
            if (paramName.equalsIgnoreCase("encoding")) {
                if (this.isValidEncoding(paramValue)) {
                    encoding = paramValue;
                    continue;
                }
                ssiMediator.log(sm.getString("ssiEcho.invalidEncoding", new Object[]{paramValue}));
                writer.write(ssiMediator.encode(errorMessage, DEFAULT_ENCODING));
                continue;
            }
            ssiMediator.log(sm.getString("ssiCommand.invalidAttribute", new Object[]{paramName}));
            writer.write(ssiMediator.encode(errorMessage, DEFAULT_ENCODING));
        }
        String string = variableValue = originalValue == null ? MISSING_VARIABLE_VALUE : ssiMediator.getVariableValue(originalValue, encoding);
        if (variableValue == null) {
            variableValue = MISSING_VARIABLE_VALUE;
        }
        writer.write(variableValue);
        return System.currentTimeMillis();
    }

    protected boolean isValidEncoding(String encoding) {
        return encoding.equalsIgnoreCase("url") || encoding.equalsIgnoreCase(DEFAULT_ENCODING) || encoding.equalsIgnoreCase("none");
    }
}

