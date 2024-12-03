/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cmdline;

import com.mchange.v2.cmdline.BadCommandLineException;
import com.mchange.v2.cmdline.MissingSwitchException;
import com.mchange.v2.cmdline.ParsedCommandLine;
import com.mchange.v2.cmdline.UnexpectedSwitchArgumentException;
import com.mchange.v2.cmdline.UnexpectedSwitchException;
import java.util.HashMap;
import java.util.LinkedList;

class ParsedCommandLineImpl
implements ParsedCommandLine {
    String[] argv;
    String switchPrefix;
    String[] unswitchedArgs;
    HashMap foundSwitches = new HashMap();

    ParsedCommandLineImpl(String[] stringArray, String string, String[] stringArray2, String[] stringArray3, String[] stringArray4) throws BadCommandLineException {
        int n;
        this.argv = stringArray;
        this.switchPrefix = string;
        LinkedList<String> linkedList = new LinkedList<String>();
        int n2 = string.length();
        for (n = 0; n < stringArray.length; ++n) {
            if (stringArray[n].startsWith(string)) {
                String string2 = stringArray[n].substring(n2);
                String string3 = null;
                int n3 = string2.indexOf(61);
                if (n3 >= 0) {
                    string3 = string2.substring(n3 + 1);
                    string2 = string2.substring(0, n3);
                } else if (ParsedCommandLineImpl.contains(string2, stringArray4) && n < stringArray.length - 1 && !stringArray[n + 1].startsWith(string)) {
                    string3 = stringArray[++n];
                }
                if (stringArray2 != null && !ParsedCommandLineImpl.contains(string2, stringArray2)) {
                    throw new UnexpectedSwitchException("Unexpected Switch: " + string2, string2);
                }
                if (stringArray4 != null && string3 != null && !ParsedCommandLineImpl.contains(string2, stringArray4)) {
                    throw new UnexpectedSwitchArgumentException("Switch \"" + string2 + "\" should not have an argument. Argument \"" + string3 + "\" found.", string2, string3);
                }
                this.foundSwitches.put(string2, string3);
                continue;
            }
            linkedList.add(stringArray[n]);
        }
        if (stringArray3 != null) {
            for (n = 0; n < stringArray3.length; ++n) {
                if (this.foundSwitches.containsKey(stringArray3[n])) continue;
                throw new MissingSwitchException("Required switch \"" + stringArray3[n] + "\" not found.", stringArray3[n]);
            }
        }
        this.unswitchedArgs = new String[linkedList.size()];
        linkedList.toArray(this.unswitchedArgs);
    }

    @Override
    public String getSwitchPrefix() {
        return this.switchPrefix;
    }

    @Override
    public String[] getRawArgs() {
        return (String[])this.argv.clone();
    }

    @Override
    public boolean includesSwitch(String string) {
        return this.foundSwitches.containsKey(string);
    }

    @Override
    public String getSwitchArg(String string) {
        return (String)this.foundSwitches.get(string);
    }

    @Override
    public String[] getUnswitchedArgs() {
        return (String[])this.unswitchedArgs.clone();
    }

    private static boolean contains(String string, String[] stringArray) {
        int n = stringArray.length;
        while (--n >= 0) {
            if (!stringArray[n].equals(string)) continue;
            return true;
        }
        return false;
    }
}

