/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.cmdline.getopt;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.apache.xalan.xsltc.cmdline.getopt.IllegalArgumentException;
import org.apache.xalan.xsltc.cmdline.getopt.MissingOptArgException;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;

public class GetOpt {
    private Option theCurrentOption = null;
    private ListIterator theOptionsIterator;
    private List theOptions = new ArrayList();
    private List theCmdArgs = null;
    private OptionMatcher theOptionMatcher = null;

    public GetOpt(String[] args, String optString) {
        String token;
        int i;
        int currOptIndex = 0;
        this.theCmdArgs = new ArrayList();
        this.theOptionMatcher = new OptionMatcher(optString);
        for (i = 0; i < args.length; ++i) {
            token = args[i];
            int tokenLength = token.length();
            if (token.equals("--")) {
                currOptIndex = i + 1;
                break;
            }
            if (token.startsWith("-") && tokenLength == 2) {
                this.theOptions.add(new Option(token.charAt(1)));
                continue;
            }
            if (token.startsWith("-") && tokenLength > 2) {
                for (int j = 1; j < tokenLength; ++j) {
                    this.theOptions.add(new Option(token.charAt(j)));
                }
                continue;
            }
            if (token.startsWith("-")) continue;
            if (this.theOptions.size() == 0) {
                currOptIndex = i;
                break;
            }
            int indexoflast = 0;
            indexoflast = this.theOptions.size() - 1;
            Option op = (Option)this.theOptions.get(indexoflast);
            char opLetter = op.getArgLetter();
            if (!op.hasArg() && this.theOptionMatcher.hasArg(opLetter)) {
                op.setArg(token);
                continue;
            }
            currOptIndex = i;
            break;
        }
        this.theOptionsIterator = this.theOptions.listIterator();
        for (i = currOptIndex; i < args.length; ++i) {
            token = args[i];
            this.theCmdArgs.add(token);
        }
    }

    public void printOptions() {
        ListIterator it = this.theOptions.listIterator();
        while (it.hasNext()) {
            Option opt = (Option)it.next();
            System.out.print("OPT =" + opt.getArgLetter());
            String arg = opt.getArgument();
            if (arg != null) {
                System.out.print(" " + arg);
            }
            System.out.println();
        }
    }

    public int getNextOption() throws IllegalArgumentException, MissingOptArgException {
        int retval = -1;
        if (this.theOptionsIterator.hasNext()) {
            this.theCurrentOption = (Option)this.theOptionsIterator.next();
            char c = this.theCurrentOption.getArgLetter();
            boolean shouldHaveArg = this.theOptionMatcher.hasArg(c);
            String arg = this.theCurrentOption.getArgument();
            if (!this.theOptionMatcher.match(c)) {
                ErrorMsg msg = new ErrorMsg("ILLEGAL_CMDLINE_OPTION_ERR", new Character(c));
                throw new IllegalArgumentException(msg.toString());
            }
            if (shouldHaveArg && arg == null) {
                ErrorMsg msg = new ErrorMsg("CMDLINE_OPT_MISSING_ARG_ERR", new Character(c));
                throw new MissingOptArgException(msg.toString());
            }
            retval = c;
        }
        return retval;
    }

    public String getOptionArg() {
        String retval = null;
        String tmp = this.theCurrentOption.getArgument();
        char c = this.theCurrentOption.getArgLetter();
        if (this.theOptionMatcher.hasArg(c)) {
            retval = tmp;
        }
        return retval;
    }

    public String[] getCmdArgs() {
        String[] retval = new String[this.theCmdArgs.size()];
        int i = 0;
        ListIterator it = this.theCmdArgs.listIterator();
        while (it.hasNext()) {
            retval[i++] = (String)it.next();
        }
        return retval;
    }

    static class OptionMatcher {
        private String theOptString = null;

        public OptionMatcher(String optString) {
            this.theOptString = optString;
        }

        public boolean match(char c) {
            boolean retval = false;
            if (this.theOptString.indexOf(c) != -1) {
                retval = true;
            }
            return retval;
        }

        public boolean hasArg(char c) {
            boolean retval = false;
            int index = this.theOptString.indexOf(c) + 1;
            if (index == this.theOptString.length()) {
                retval = false;
            } else if (this.theOptString.charAt(index) == ':') {
                retval = true;
            }
            return retval;
        }
    }

    static class Option {
        private char theArgLetter;
        private String theArgument = null;

        public Option(char argLetter) {
            this.theArgLetter = argLetter;
        }

        public void setArg(String arg) {
            this.theArgument = arg;
        }

        public boolean hasArg() {
            return this.theArgument != null;
        }

        public char getArgLetter() {
            return this.theArgLetter;
        }

        public String getArgument() {
            return this.theArgument;
        }
    }
}

