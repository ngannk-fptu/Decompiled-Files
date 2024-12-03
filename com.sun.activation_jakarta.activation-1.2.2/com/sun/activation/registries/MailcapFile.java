/*
 * Decompiled with CFR 0.152.
 */
package com.sun.activation.registries;

import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MailcapParseException;
import com.sun.activation.registries.MailcapTokenizer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MailcapFile {
    private Map type_hash = new HashMap();
    private Map fallback_hash = new HashMap();
    private Map native_commands = new HashMap();
    private static boolean addReverse = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MailcapFile(String new_fname) throws IOException {
        if (LogSupport.isLoggable()) {
            LogSupport.log("new MailcapFile: file " + new_fname);
        }
        FileReader reader = null;
        try {
            reader = new FileReader(new_fname);
            this.parse(new BufferedReader(reader));
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    public MailcapFile(InputStream is) throws IOException {
        if (LogSupport.isLoggable()) {
            LogSupport.log("new MailcapFile: InputStream");
        }
        this.parse(new BufferedReader(new InputStreamReader(is, "iso-8859-1")));
    }

    public MailcapFile() {
        if (LogSupport.isLoggable()) {
            LogSupport.log("new MailcapFile: default");
        }
    }

    public Map getMailcapList(String mime_type) {
        String type;
        Map search_result = null;
        Map wildcard_result = null;
        search_result = (Map)this.type_hash.get(mime_type);
        int separator = mime_type.indexOf(47);
        String subtype = mime_type.substring(separator + 1);
        if (!subtype.equals("*") && (wildcard_result = (Map)this.type_hash.get(type = mime_type.substring(0, separator + 1) + "*")) != null) {
            search_result = search_result != null ? this.mergeResults(search_result, wildcard_result) : wildcard_result;
        }
        return search_result;
    }

    public Map getMailcapFallbackList(String mime_type) {
        String type;
        Map search_result = null;
        Map wildcard_result = null;
        search_result = (Map)this.fallback_hash.get(mime_type);
        int separator = mime_type.indexOf(47);
        String subtype = mime_type.substring(separator + 1);
        if (!subtype.equals("*") && (wildcard_result = (Map)this.fallback_hash.get(type = mime_type.substring(0, separator + 1) + "*")) != null) {
            search_result = search_result != null ? this.mergeResults(search_result, wildcard_result) : wildcard_result;
        }
        return search_result;
    }

    public String[] getMimeTypes() {
        HashSet types = new HashSet(this.type_hash.keySet());
        types.addAll(this.fallback_hash.keySet());
        types.addAll(this.native_commands.keySet());
        String[] mts = new String[types.size()];
        mts = types.toArray(mts);
        return mts;
    }

    public String[] getNativeCommands(String mime_type) {
        String[] cmds = null;
        List v = (List)this.native_commands.get(mime_type.toLowerCase(Locale.ENGLISH));
        if (v != null) {
            cmds = new String[v.size()];
            cmds = v.toArray(cmds);
        }
        return cmds;
    }

    private Map mergeResults(Map first, Map second) {
        Iterator verb_enum = second.keySet().iterator();
        HashMap clonedHash = new HashMap(first);
        while (verb_enum.hasNext()) {
            String verb = (String)verb_enum.next();
            ArrayList cmdVector = (ArrayList)clonedHash.get(verb);
            if (cmdVector == null) {
                clonedHash.put(verb, second.get(verb));
                continue;
            }
            List oldV = (List)second.get(verb);
            cmdVector = new ArrayList(cmdVector);
            cmdVector.addAll(oldV);
            clonedHash.put(verb, cmdVector);
        }
        return clonedHash;
    }

    public void appendToMailcap(String mail_cap) {
        if (LogSupport.isLoggable()) {
            LogSupport.log("appendToMailcap: " + mail_cap);
        }
        try {
            this.parse(new StringReader(mail_cap));
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private void parse(Reader reader) throws IOException {
        BufferedReader buf_reader = new BufferedReader(reader);
        String line = null;
        String continued = null;
        while ((line = buf_reader.readLine()) != null) {
            line = line.trim();
            try {
                if (line.charAt(0) == '#') continue;
                if (line.charAt(line.length() - 1) == '\\') {
                    if (continued != null) {
                        continued = continued + line.substring(0, line.length() - 1);
                        continue;
                    }
                    continued = line.substring(0, line.length() - 1);
                    continue;
                }
                if (continued != null) {
                    continued = continued + line;
                    try {
                        this.parseLine(continued);
                    }
                    catch (MailcapParseException mailcapParseException) {
                        // empty catch block
                    }
                    continued = null;
                    continue;
                }
                try {
                    this.parseLine(line);
                }
                catch (MailcapParseException mailcapParseException) {
                }
            }
            catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {}
        }
    }

    protected void parseLine(String mailcapEntry) throws MailcapParseException, IOException {
        int currentToken;
        MailcapTokenizer tokenizer = new MailcapTokenizer(mailcapEntry);
        tokenizer.setIsAutoquoting(false);
        if (LogSupport.isLoggable()) {
            LogSupport.log("parse: " + mailcapEntry);
        }
        if ((currentToken = tokenizer.nextToken()) != 2) {
            MailcapFile.reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
        }
        String primaryType = tokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
        String subType = "*";
        currentToken = tokenizer.nextToken();
        if (currentToken != 47 && currentToken != 59) {
            MailcapFile.reportParseError(47, 59, currentToken, tokenizer.getCurrentTokenValue());
        }
        if (currentToken == 47) {
            currentToken = tokenizer.nextToken();
            if (currentToken != 2) {
                MailcapFile.reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
            }
            subType = tokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
            currentToken = tokenizer.nextToken();
        }
        String mimeType = primaryType + "/" + subType;
        if (LogSupport.isLoggable()) {
            LogSupport.log("  Type: " + mimeType);
        }
        LinkedHashMap<String, ArrayList<String>> commands = new LinkedHashMap<String, ArrayList<String>>();
        if (currentToken != 59) {
            MailcapFile.reportParseError(59, currentToken, tokenizer.getCurrentTokenValue());
        }
        tokenizer.setIsAutoquoting(true);
        currentToken = tokenizer.nextToken();
        tokenizer.setIsAutoquoting(false);
        if (currentToken != 2 && currentToken != 59) {
            MailcapFile.reportParseError(2, 59, currentToken, tokenizer.getCurrentTokenValue());
        }
        if (currentToken == 2) {
            ArrayList<String> v = (ArrayList<String>)this.native_commands.get(mimeType);
            if (v == null) {
                v = new ArrayList<String>();
                v.add(mailcapEntry);
                this.native_commands.put(mimeType, v);
            } else {
                v.add(mailcapEntry);
            }
        }
        if (currentToken != 59) {
            currentToken = tokenizer.nextToken();
        }
        if (currentToken == 59) {
            boolean isFallback = false;
            do {
                if ((currentToken = tokenizer.nextToken()) != 2) {
                    MailcapFile.reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
                }
                String paramName = tokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
                currentToken = tokenizer.nextToken();
                if (currentToken != 61 && currentToken != 59 && currentToken != 5) {
                    MailcapFile.reportParseError(61, 59, 5, currentToken, tokenizer.getCurrentTokenValue());
                }
                if (currentToken != 61) continue;
                tokenizer.setIsAutoquoting(true);
                currentToken = tokenizer.nextToken();
                tokenizer.setIsAutoquoting(false);
                if (currentToken != 2) {
                    MailcapFile.reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
                }
                String paramValue = tokenizer.getCurrentTokenValue();
                if (paramName.startsWith("x-java-")) {
                    String commandName = paramName.substring(7);
                    if (commandName.equals("fallback-entry") && paramValue.equalsIgnoreCase("true")) {
                        isFallback = true;
                    } else {
                        ArrayList<String> classes;
                        if (LogSupport.isLoggable()) {
                            LogSupport.log("    Command: " + commandName + ", Class: " + paramValue);
                        }
                        if ((classes = (ArrayList<String>)commands.get(commandName)) == null) {
                            classes = new ArrayList<String>();
                            commands.put(commandName, classes);
                        }
                        if (addReverse) {
                            classes.add(0, paramValue);
                        } else {
                            classes.add(paramValue);
                        }
                    }
                }
                currentToken = tokenizer.nextToken();
            } while (currentToken == 59);
            Map masterHash = isFallback ? this.fallback_hash : this.type_hash;
            Map curcommands = (Map)masterHash.get(mimeType);
            if (curcommands == null) {
                masterHash.put(mimeType, commands);
            } else {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("Merging commands for type " + mimeType);
                }
                for (String cmdName : curcommands.keySet()) {
                    List ccv = (List)curcommands.get(cmdName);
                    List cv = (List)commands.get(cmdName);
                    if (cv == null) continue;
                    for (String clazz : cv) {
                        if (ccv.contains(clazz)) continue;
                        if (addReverse) {
                            ccv.add(0, clazz);
                            continue;
                        }
                        ccv.add(clazz);
                    }
                }
                for (String cmdName : commands.keySet()) {
                    if (curcommands.containsKey(cmdName)) continue;
                    List cv = (List)commands.get(cmdName);
                    curcommands.put(cmdName, cv);
                }
            }
        } else if (currentToken != 5) {
            MailcapFile.reportParseError(5, 59, currentToken, tokenizer.getCurrentTokenValue());
        }
    }

    protected static void reportParseError(int expectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + " token.");
    }

    protected static void reportParseError(int expectedToken, int otherExpectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + " or a " + MailcapTokenizer.nameForToken(otherExpectedToken) + " token.");
    }

    protected static void reportParseError(int expectedToken, int otherExpectedToken, int anotherExpectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
        if (LogSupport.isLoggable()) {
            LogSupport.log("PARSE ERROR: Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + ", a " + MailcapTokenizer.nameForToken(otherExpectedToken) + ", or a " + MailcapTokenizer.nameForToken(anotherExpectedToken) + " token.");
        }
        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + ", a " + MailcapTokenizer.nameForToken(otherExpectedToken) + ", or a " + MailcapTokenizer.nameForToken(anotherExpectedToken) + " token.");
    }

    static {
        try {
            addReverse = Boolean.getBoolean("javax.activation.addreverse");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

