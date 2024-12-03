/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.text.ParseException;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.axis.utils.CLOption;
import org.apache.axis.utils.CLOptionDescriptor;
import org.apache.axis.utils.ParserControl;
import org.apache.axis.utils.Token;

public final class CLArgsParser {
    private static final int STATE_NORMAL = 0;
    private static final int STATE_REQUIRE_2ARGS = 1;
    private static final int STATE_REQUIRE_ARG = 2;
    private static final int STATE_OPTIONAL_ARG = 3;
    private static final int STATE_NO_OPTIONS = 4;
    private static final int STATE_OPTION_MODE = 5;
    private static final int TOKEN_SEPARATOR = 0;
    private static final int TOKEN_STRING = 1;
    private static final char[] ARG2_SEPARATORS = new char[]{'\u0000', '=', '-'};
    private static final char[] ARG_SEPARATORS = new char[]{'\u0000', '='};
    private static final char[] NULL_SEPARATORS = new char[]{'\u0000'};
    private final CLOptionDescriptor[] m_optionDescriptors;
    private final Vector m_options;
    private Hashtable m_optionIndex;
    private final ParserControl m_control;
    private String m_errorMessage;
    private String[] m_unparsedArgs = new String[0];
    private char ch;
    private String[] args;
    private boolean isLong;
    private int argIndex;
    private int stringIndex;
    private int stringLength;
    private static final int INVALID = Integer.MAX_VALUE;
    private int m_lastChar = Integer.MAX_VALUE;
    private int m_lastOptionId;
    private CLOption m_option;
    private int m_state = 0;

    public final String[] getUnparsedArgs() {
        return this.m_unparsedArgs;
    }

    public final Vector getArguments() {
        return this.m_options;
    }

    public final CLOption getArgumentById(int id) {
        return (CLOption)this.m_optionIndex.get(new Integer(id));
    }

    public final CLOption getArgumentByName(String name) {
        return (CLOption)this.m_optionIndex.get(name);
    }

    private final CLOptionDescriptor getDescriptorFor(int id) {
        for (int i = 0; i < this.m_optionDescriptors.length; ++i) {
            if (this.m_optionDescriptors[i].getId() != id) continue;
            return this.m_optionDescriptors[i];
        }
        return null;
    }

    private final CLOptionDescriptor getDescriptorFor(String name) {
        for (int i = 0; i < this.m_optionDescriptors.length; ++i) {
            if (!this.m_optionDescriptors[i].getName().equals(name)) continue;
            return this.m_optionDescriptors[i];
        }
        return null;
    }

    public final String getErrorString() {
        return this.m_errorMessage;
    }

    private final int getStateFor(CLOptionDescriptor descriptor) {
        int flags = descriptor.getFlags();
        if ((flags & 0x10) == 16) {
            return 1;
        }
        if ((flags & 2) == 2) {
            return 2;
        }
        if ((flags & 4) == 4) {
            return 3;
        }
        return 0;
    }

    public CLArgsParser(String[] args, CLOptionDescriptor[] optionDescriptors, ParserControl control) {
        this.m_optionDescriptors = optionDescriptors;
        this.m_control = control;
        this.m_options = new Vector();
        this.args = args;
        try {
            this.parse();
            this.checkIncompatibilities(this.m_options);
            this.buildOptionIndex();
        }
        catch (ParseException pe) {
            this.m_errorMessage = pe.getMessage();
        }
    }

    private final void checkIncompatibilities(Vector arguments) throws ParseException {
        int size = arguments.size();
        for (int i = 0; i < size; ++i) {
            CLOption option = (CLOption)arguments.elementAt(i);
            int id = option.getId();
            CLOptionDescriptor descriptor = this.getDescriptorFor(id);
            if (null == descriptor) continue;
            int[] incompatible = descriptor.getIncompatible();
            this.checkIncompatible(arguments, incompatible, i);
        }
    }

    private final void checkIncompatible(Vector arguments, int[] incompatible, int original) throws ParseException {
        int size = arguments.size();
        for (int i = 0; i < size; ++i) {
            if (original == i) continue;
            CLOption option = (CLOption)arguments.elementAt(i);
            int id = option.getId();
            for (int j = 0; j < incompatible.length; ++j) {
                if (id != incompatible[j]) continue;
                CLOption originalOption = (CLOption)arguments.elementAt(original);
                int originalId = originalOption.getId();
                String message = null;
                message = id == originalId ? "Duplicate options for " + this.describeDualOption(originalId) + " found." : "Incompatible options -" + this.describeDualOption(id) + " and " + this.describeDualOption(originalId) + " found.";
                throw new ParseException(message, 0);
            }
        }
    }

    private final String describeDualOption(int id) {
        String longOption;
        CLOptionDescriptor descriptor = this.getDescriptorFor(id);
        if (null == descriptor) {
            return "<parameter>";
        }
        StringBuffer sb = new StringBuffer();
        boolean hasCharOption = false;
        if (Character.isLetter((char)id)) {
            sb.append('-');
            sb.append((char)id);
            hasCharOption = true;
        }
        if (null != (longOption = descriptor.getName())) {
            if (hasCharOption) {
                sb.append('/');
            }
            sb.append("--");
            sb.append(longOption);
        }
        return sb.toString();
    }

    public CLArgsParser(String[] args, CLOptionDescriptor[] optionDescriptors) {
        this(args, optionDescriptors, null);
    }

    private final String[] subArray(String[] array, int index, int charIndex) {
        int remaining = array.length - index;
        String[] result = new String[remaining];
        if (remaining > 1) {
            System.arraycopy(array, index + 1, result, 1, remaining - 1);
        }
        result[0] = array[index].substring(charIndex - 1);
        return result;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final void parse() throws ParseException {
        if (0 == this.args.length) {
            return;
        }
        this.stringLength = this.args[this.argIndex].length();
        while (true) {
            this.ch = this.peekAtChar();
            if (this.argIndex >= this.args.length) break;
            if (null != this.m_control && this.m_control.isFinished(this.m_lastOptionId)) {
                this.m_unparsedArgs = this.subArray(this.args, this.argIndex, this.stringIndex);
                return;
            }
            if (5 == this.m_state) {
                if ('\u0000' == this.ch) {
                    this.getChar();
                    this.m_state = 0;
                    continue;
                }
                this.parseShortOption();
                continue;
            }
            if (0 == this.m_state) {
                this.parseNormal();
                continue;
            }
            if (4 == this.m_state) {
                this.addOption(new CLOption(this.args[this.argIndex++]));
                continue;
            }
            if (3 == this.m_state && '-' == this.ch) {
                this.m_state = 0;
                this.addOption(this.m_option);
                continue;
            }
            this.parseArguments();
        }
        if (this.m_option == null) return;
        if (3 == this.m_state) {
            this.m_options.addElement(this.m_option);
            return;
        } else {
            if (2 == this.m_state) {
                CLOptionDescriptor descriptor = this.getDescriptorFor(this.m_option.getId());
                String message = "Missing argument to option " + this.getOptionDescription(descriptor);
                throw new ParseException(message, 0);
            }
            if (1 != this.m_state) throw new ParseException("IllegalState " + this.m_state + ": " + this.m_option, 0);
            if (1 == this.m_option.getArgumentCount()) {
                this.m_option.addArgument("");
                this.m_options.addElement(this.m_option);
                return;
            } else {
                CLOptionDescriptor descriptor = this.getDescriptorFor(this.m_option.getId());
                String message = "Missing argument to option " + this.getOptionDescription(descriptor);
                throw new ParseException(message, 0);
            }
        }
    }

    private final String getOptionDescription(CLOptionDescriptor descriptor) {
        if (this.isLong) {
            return "--" + descriptor.getName();
        }
        return "-" + (char)descriptor.getId();
    }

    private final char peekAtChar() {
        if (Integer.MAX_VALUE == this.m_lastChar) {
            this.m_lastChar = this.readChar();
        }
        return (char)this.m_lastChar;
    }

    private final char getChar() {
        if (Integer.MAX_VALUE != this.m_lastChar) {
            char result = (char)this.m_lastChar;
            this.m_lastChar = Integer.MAX_VALUE;
            return result;
        }
        return this.readChar();
    }

    private final char readChar() {
        if (this.stringIndex >= this.stringLength) {
            ++this.argIndex;
            this.stringIndex = 0;
            this.stringLength = this.argIndex < this.args.length ? this.args[this.argIndex].length() : 0;
            return '\u0000';
        }
        if (this.argIndex >= this.args.length) {
            return '\u0000';
        }
        return this.args[this.argIndex].charAt(this.stringIndex++);
    }

    private final Token nextToken(char[] separators) {
        this.ch = this.getChar();
        if (this.isSeparator(this.ch, separators)) {
            this.ch = this.getChar();
            return new Token(0, null);
        }
        StringBuffer sb = new StringBuffer();
        do {
            sb.append(this.ch);
            this.ch = this.getChar();
        } while (!this.isSeparator(this.ch, separators));
        return new Token(1, sb.toString());
    }

    private final boolean isSeparator(char ch, char[] separators) {
        for (int i = 0; i < separators.length; ++i) {
            if (ch != separators[i]) continue;
            return true;
        }
        return false;
    }

    private final void addOption(CLOption option) {
        this.m_options.addElement(option);
        this.m_lastOptionId = option.getId();
        this.m_option = null;
    }

    private final void parseOption(CLOptionDescriptor descriptor, String optionString) throws ParseException {
        if (null == descriptor) {
            throw new ParseException("Unknown option " + optionString, 0);
        }
        this.m_state = this.getStateFor(descriptor);
        this.m_option = new CLOption(descriptor.getId());
        if (0 == this.m_state) {
            this.addOption(this.m_option);
        }
    }

    private final void parseShortOption() throws ParseException {
        this.ch = this.getChar();
        CLOptionDescriptor descriptor = this.getDescriptorFor(this.ch);
        this.isLong = false;
        this.parseOption(descriptor, "-" + this.ch);
        if (0 == this.m_state) {
            this.m_state = 5;
        }
    }

    private final void parseArguments() throws ParseException {
        if (2 == this.m_state) {
            if ('=' == this.ch || '\u0000' == this.ch) {
                this.getChar();
            }
            Token token = this.nextToken(NULL_SEPARATORS);
            this.m_option.addArgument(token.getValue());
            this.addOption(this.m_option);
            this.m_state = 0;
        } else if (3 == this.m_state) {
            if ('-' == this.ch || '\u0000' == this.ch) {
                this.getChar();
                this.addOption(this.m_option);
                this.m_state = 0;
                return;
            }
            if ('=' == this.ch) {
                this.getChar();
            }
            Token token = this.nextToken(NULL_SEPARATORS);
            this.m_option.addArgument(token.getValue());
            this.addOption(this.m_option);
            this.m_state = 0;
        } else if (1 == this.m_state) {
            if (0 == this.m_option.getArgumentCount()) {
                Token token = this.nextToken(ARG_SEPARATORS);
                if (0 == token.getType()) {
                    CLOptionDescriptor descriptor = this.getDescriptorFor(this.m_option.getId());
                    String message = "Unable to parse first argument for option " + this.getOptionDescription(descriptor);
                    throw new ParseException(message, 0);
                }
                this.m_option.addArgument(token.getValue());
            } else {
                StringBuffer sb = new StringBuffer();
                this.ch = this.getChar();
                if ('-' == this.ch) {
                    this.m_lastChar = this.ch;
                }
                while (!this.isSeparator(this.ch, ARG2_SEPARATORS)) {
                    sb.append(this.ch);
                    this.ch = this.getChar();
                }
                String argument = sb.toString();
                this.m_option.addArgument(argument);
                this.addOption(this.m_option);
                this.m_option = null;
                this.m_state = 0;
            }
        }
    }

    private final void parseNormal() throws ParseException {
        if ('-' != this.ch) {
            String argument = this.nextToken(NULL_SEPARATORS).getValue();
            this.addOption(new CLOption(argument));
            this.m_state = 0;
        } else {
            this.getChar();
            if ('\u0000' == this.peekAtChar()) {
                throw new ParseException("Malformed option -", 0);
            }
            this.ch = this.peekAtChar();
            if ('-' != this.ch) {
                this.parseShortOption();
            } else {
                this.getChar();
                if ('\u0000' == this.peekAtChar()) {
                    this.getChar();
                    this.m_state = 4;
                } else {
                    String optionName = this.nextToken(ARG_SEPARATORS).getValue();
                    CLOptionDescriptor descriptor = this.getDescriptorFor(optionName);
                    this.isLong = true;
                    this.parseOption(descriptor, "--" + optionName);
                }
            }
        }
    }

    private final void buildOptionIndex() {
        this.m_optionIndex = new Hashtable(this.m_options.size() * 2);
        for (int i = 0; i < this.m_options.size(); ++i) {
            CLOption option = (CLOption)this.m_options.get(i);
            CLOptionDescriptor optionDescriptor = this.getDescriptorFor(option.getId());
            this.m_optionIndex.put(new Integer(option.getId()), option);
            if (null == optionDescriptor) continue;
            this.m_optionIndex.put(optionDescriptor.getName(), option);
        }
    }
}

