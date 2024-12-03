/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.util.IOTools
 */
package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.catalina.ssi.SSICommand;
import org.apache.catalina.ssi.SSIConditional;
import org.apache.catalina.ssi.SSIConfig;
import org.apache.catalina.ssi.SSIEcho;
import org.apache.catalina.ssi.SSIExec;
import org.apache.catalina.ssi.SSIExternalResolver;
import org.apache.catalina.ssi.SSIFlastmod;
import org.apache.catalina.ssi.SSIFsize;
import org.apache.catalina.ssi.SSIInclude;
import org.apache.catalina.ssi.SSIMediator;
import org.apache.catalina.ssi.SSIPrintenv;
import org.apache.catalina.ssi.SSISet;
import org.apache.catalina.ssi.SSIStopProcessingException;
import org.apache.catalina.util.IOTools;

public class SSIProcessor {
    protected static final String COMMAND_START = "<!--#";
    protected static final String COMMAND_END = "-->";
    protected final SSIExternalResolver ssiExternalResolver;
    protected final HashMap<String, SSICommand> commands = new HashMap();
    protected final int debug;
    protected final boolean allowExec;

    public SSIProcessor(SSIExternalResolver ssiExternalResolver, int debug, boolean allowExec) {
        this.ssiExternalResolver = ssiExternalResolver;
        this.debug = debug;
        this.allowExec = allowExec;
        this.addBuiltinCommands();
    }

    protected void addBuiltinCommands() {
        this.addCommand("config", new SSIConfig());
        this.addCommand("echo", new SSIEcho());
        if (this.allowExec) {
            this.addCommand("exec", new SSIExec());
        }
        this.addCommand("include", new SSIInclude());
        this.addCommand("flastmod", new SSIFlastmod());
        this.addCommand("fsize", new SSIFsize());
        this.addCommand("printenv", new SSIPrintenv());
        this.addCommand("set", new SSISet());
        SSIConditional ssiConditional = new SSIConditional();
        this.addCommand("if", ssiConditional);
        this.addCommand("elif", ssiConditional);
        this.addCommand("endif", ssiConditional);
        this.addCommand("else", ssiConditional);
    }

    public void addCommand(String name, SSICommand command) {
        this.commands.put(name, command);
    }

    public long process(Reader reader, long lastModifiedDate, PrintWriter writer) throws IOException {
        SSIMediator ssiMediator = new SSIMediator(this.ssiExternalResolver, lastModifiedDate);
        StringWriter stringWriter = new StringWriter();
        IOTools.flow((Reader)reader, (Writer)stringWriter);
        String fileContents = stringWriter.toString();
        stringWriter = null;
        int index = 0;
        boolean inside = false;
        StringBuilder command = new StringBuilder();
        try {
            while (index < fileContents.length()) {
                char c = fileContents.charAt(index);
                if (!inside) {
                    if (c == COMMAND_START.charAt(0) && this.charCmp(fileContents, index, COMMAND_START)) {
                        inside = true;
                        index += COMMAND_START.length();
                        command.setLength(0);
                        continue;
                    }
                    if (!ssiMediator.getConditionalState().processConditionalCommandsOnly) {
                        writer.write(c);
                    }
                    ++index;
                    continue;
                }
                if (c == COMMAND_END.charAt(0) && this.charCmp(fileContents, index, COMMAND_END)) {
                    long lmd;
                    inside = false;
                    index += COMMAND_END.length();
                    String strCmd = this.parseCmd(command);
                    if (this.debug > 0) {
                        this.ssiExternalResolver.log("SSIProcessor.process -- processing command: " + strCmd, null);
                    }
                    String[] paramNames = this.parseParamNames(command, strCmd.length());
                    String[] paramValues = this.parseParamValues(command, strCmd.length(), paramNames.length);
                    String configErrMsg = ssiMediator.getConfigErrMsg();
                    SSICommand ssiCommand = this.commands.get(strCmd.toLowerCase(Locale.ENGLISH));
                    String errorMessage = null;
                    if (ssiCommand == null) {
                        errorMessage = "Unknown command: " + strCmd;
                    } else if (paramValues == null) {
                        errorMessage = "Error parsing directive parameters.";
                    } else if (paramNames.length != paramValues.length) {
                        errorMessage = "Parameter names count does not match parameter values count on command: " + strCmd;
                    } else if ((!ssiMediator.getConditionalState().processConditionalCommandsOnly || ssiCommand instanceof SSIConditional) && (lmd = ssiCommand.process(ssiMediator, strCmd, paramNames, paramValues, writer)) > lastModifiedDate) {
                        lastModifiedDate = lmd;
                    }
                    if (errorMessage == null) continue;
                    this.ssiExternalResolver.log(errorMessage, null);
                    writer.write(configErrMsg);
                    continue;
                }
                command.append(c);
                ++index;
            }
        }
        catch (SSIStopProcessingException sSIStopProcessingException) {
            // empty catch block
        }
        return lastModifiedDate;
    }

    protected String[] parseParamNames(StringBuilder cmd, int start) {
        int bIdx = start;
        int i = 0;
        int quotes = 0;
        boolean inside = false;
        StringBuilder retBuf = new StringBuilder();
        while (bIdx < cmd.length()) {
            if (!inside) {
                while (bIdx < cmd.length() && this.isSpace(cmd.charAt(bIdx))) {
                    ++bIdx;
                }
                if (bIdx >= cmd.length()) break;
                inside = !inside;
                continue;
            }
            while (bIdx < cmd.length() && cmd.charAt(bIdx) != '=') {
                retBuf.append(cmd.charAt(bIdx));
                ++bIdx;
            }
            retBuf.append('=');
            inside = !inside;
            quotes = 0;
            boolean escaped = false;
            while (bIdx < cmd.length() && quotes != 2) {
                char c = cmd.charAt(bIdx);
                if (c == '\\' && !escaped) {
                    escaped = true;
                } else {
                    if (c == '\"' && !escaped) {
                        ++quotes;
                    }
                    escaped = false;
                }
                ++bIdx;
            }
        }
        StringTokenizer str = new StringTokenizer(retBuf.toString(), "=");
        String[] retString = new String[str.countTokens()];
        while (str.hasMoreTokens()) {
            retString[i++] = str.nextToken().trim();
        }
        return retString;
    }

    protected String[] parseParamValues(StringBuilder cmd, int start, int count) {
        int valIndex = 0;
        boolean inside = false;
        String[] vals = new String[count];
        StringBuilder sb = new StringBuilder();
        char endQuote = '\u0000';
        for (int bIdx = start; bIdx < cmd.length(); ++bIdx) {
            if (!inside) {
                while (bIdx < cmd.length() && !this.isQuote(cmd.charAt(bIdx))) {
                    ++bIdx;
                }
                if (bIdx >= cmd.length()) break;
                inside = !inside;
                endQuote = cmd.charAt(bIdx);
                continue;
            }
            boolean escaped = false;
            while (bIdx < cmd.length()) {
                char c = cmd.charAt(bIdx);
                if (c == '\\' && !escaped) {
                    escaped = true;
                } else {
                    if (c == endQuote && !escaped) break;
                    if (c == '$' && escaped) {
                        sb.append('\\');
                    }
                    escaped = false;
                    sb.append(c);
                }
                ++bIdx;
            }
            if (bIdx == cmd.length()) {
                return null;
            }
            vals[valIndex++] = sb.toString();
            sb.delete(0, sb.length());
            inside = !inside;
        }
        return vals;
    }

    private String parseCmd(StringBuilder cmd) {
        int firstLetter = -1;
        int lastLetter = -1;
        for (int i = 0; i < cmd.length(); ++i) {
            char c = cmd.charAt(i);
            if (Character.isLetter(c)) {
                if (firstLetter == -1) {
                    firstLetter = i;
                }
                lastLetter = i;
                continue;
            }
            if (!this.isSpace(c) || lastLetter > -1) break;
        }
        if (firstLetter == -1) {
            return "";
        }
        return cmd.substring(firstLetter, lastLetter + 1);
    }

    protected boolean charCmp(String buf, int index, String command) {
        return buf.regionMatches(index, command, 0, command.length());
    }

    protected boolean isSpace(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
    }

    protected boolean isQuote(char c) {
        return c == '\'' || c == '\"' || c == '`';
    }
}

