/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.util.IOTools
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ssi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import org.apache.catalina.ssi.SSICommand;
import org.apache.catalina.ssi.SSIInclude;
import org.apache.catalina.ssi.SSIMediator;
import org.apache.catalina.util.IOTools;
import org.apache.tomcat.util.res.StringManager;

public class SSIExec
implements SSICommand {
    private static final StringManager sm = StringManager.getManager(SSIExec.class);
    protected final SSIInclude ssiInclude = new SSIInclude();
    protected static final int BUFFER_SIZE = 1024;

    @Override
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues, PrintWriter writer) {
        long lastModified = 0L;
        String configErrMsg = ssiMediator.getConfigErrMsg();
        String paramName = paramNames[0];
        String paramValue = paramValues[0];
        String substitutedValue = ssiMediator.substituteVariables(paramValue);
        if (paramName.equalsIgnoreCase("cgi")) {
            lastModified = this.ssiInclude.process(ssiMediator, "include", new String[]{"virtual"}, new String[]{substitutedValue}, writer);
        } else if (paramName.equalsIgnoreCase("cmd")) {
            boolean foundProgram = false;
            try {
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec(substitutedValue);
                foundProgram = true;
                char[] buf = new char[1024];
                try (BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                     BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));){
                    IOTools.flow((Reader)stdErrReader, (Writer)writer, (char[])buf);
                    IOTools.flow((Reader)stdOutReader, (Writer)writer, (char[])buf);
                }
                proc.waitFor();
                lastModified = System.currentTimeMillis();
            }
            catch (InterruptedException e) {
                ssiMediator.log(sm.getString("ssiExec.executeFailed", new Object[]{substitutedValue}), e);
                writer.write(configErrMsg);
            }
            catch (IOException e) {
                if (!foundProgram) {
                    // empty if block
                }
                ssiMediator.log(sm.getString("ssiExec.executeFailed", new Object[]{substitutedValue}), e);
            }
        }
        return lastModified;
    }
}

