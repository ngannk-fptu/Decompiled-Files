/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.CommandLineParser;

public class CommandLineParserImpl
implements CommandLineParser {
    String[] argv;
    String[] validSwitches;
    String[] reqSwitches;
    String[] argSwitches;
    char switch_char;

    public CommandLineParserImpl(String[] stringArray, String[] stringArray2, String[] stringArray3, String[] stringArray4, char c) {
        this.argv = stringArray;
        this.validSwitches = stringArray2 == null ? new String[]{} : stringArray2;
        this.reqSwitches = stringArray3 == null ? new String[]{} : stringArray3;
        this.argSwitches = stringArray4 == null ? new String[]{} : stringArray4;
        this.switch_char = c;
    }

    public CommandLineParserImpl(String[] stringArray, String[] stringArray2, String[] stringArray3, String[] stringArray4) {
        this(stringArray, stringArray2, stringArray3, stringArray4, '-');
    }

    @Override
    public boolean checkSwitch(String string) {
        for (int i = 0; i < this.argv.length; ++i) {
            if (this.argv[i].charAt(0) != this.switch_char || !this.argv[i].equals(this.switch_char + string)) continue;
            return true;
        }
        return false;
    }

    @Override
    public String findSwitchArg(String string) {
        for (int i = 0; i < this.argv.length - 1; ++i) {
            if (this.argv[i].charAt(0) != this.switch_char || !this.argv[i].equals(this.switch_char + string)) continue;
            return this.argv[i + 1].charAt(0) == this.switch_char ? null : this.argv[i + 1];
        }
        return null;
    }

    @Override
    public boolean checkArgv() {
        return this.checkValidSwitches() && this.checkRequiredSwitches() && this.checkSwitchArgSyntax();
    }

    boolean checkValidSwitches() {
        block0: for (int i = 0; i < this.argv.length; ++i) {
            if (this.argv[i].charAt(0) != this.switch_char) continue;
            for (int j = 0; j < this.validSwitches.length; ++j) {
                if (this.argv[i].equals(this.switch_char + this.validSwitches[j])) continue block0;
            }
            return false;
        }
        return true;
    }

    boolean checkRequiredSwitches() {
        int n = this.reqSwitches.length;
        while (--n >= 0) {
            if (this.checkSwitch(this.reqSwitches[n])) continue;
            return false;
        }
        return true;
    }

    boolean checkSwitchArgSyntax() {
        int n = this.argSwitches.length;
        while (--n >= 0) {
            String string;
            if (!this.checkSwitch(this.argSwitches[n]) || (string = this.findSwitchArg(this.argSwitches[n])) != null && string.charAt(0) != this.switch_char) continue;
            return false;
        }
        return true;
    }

    @Override
    public int findLastSwitched() {
        int n = this.argv.length;
        while (--n >= 0) {
            if (this.argv[n].charAt(0) != this.switch_char) continue;
            return n;
        }
        return -1;
    }

    @Override
    public String[] findUnswitchedArgs() {
        String[] stringArray = new String[this.argv.length];
        int n = 0;
        for (int i = 0; i < this.argv.length; ++i) {
            if (this.argv[i].charAt(0) == this.switch_char) {
                if (!CommandLineParserImpl.contains(this.argv[i].substring(1), this.argSwitches)) continue;
                ++i;
                continue;
            }
            stringArray[n++] = this.argv[i];
        }
        String[] stringArray2 = new String[n];
        System.arraycopy(stringArray, 0, stringArray2, 0, n);
        return stringArray2;
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

