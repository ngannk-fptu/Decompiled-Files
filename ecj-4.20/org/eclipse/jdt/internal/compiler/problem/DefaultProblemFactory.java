/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.problem;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.internal.compiler.util.HashtableOfInt;
import org.eclipse.jdt.internal.compiler.util.Util;

public class DefaultProblemFactory
implements IProblemFactory {
    public HashtableOfInt messageTemplates;
    private Locale locale;
    private static HashtableOfInt DEFAULT_LOCALE_TEMPLATES;
    private static final char[] DOUBLE_QUOTES;
    private static final char[] SINGLE_QUOTE;
    private static final char[] FIRST_ARGUMENT;

    static {
        DOUBLE_QUOTES = "''".toCharArray();
        SINGLE_QUOTE = "'".toCharArray();
        FIRST_ARGUMENT = "{0}".toCharArray();
    }

    public DefaultProblemFactory() {
        this(Locale.getDefault());
    }

    public DefaultProblemFactory(Locale loc) {
        this.setLocale(loc);
    }

    @Override
    public CategorizedProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments, String[] messageArguments, int severity, int startPosition, int endPosition, int lineNumber, int columnNumber) {
        return new DefaultProblem(originatingFileName, this.getLocalizedMessage(problemId, messageArguments), problemId, problemArguments, severity, startPosition, endPosition, lineNumber, columnNumber);
    }

    @Override
    public CategorizedProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments, int elaborationId, String[] messageArguments, int severity, int startPosition, int endPosition, int lineNumber, int columnNumber) {
        return new DefaultProblem(originatingFileName, this.getLocalizedMessage(problemId, elaborationId, messageArguments), problemId, problemArguments, severity, startPosition, endPosition, lineNumber, columnNumber);
    }

    private static final int keyFromID(int id) {
        return id + 1;
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        if (locale == this.locale) {
            return;
        }
        this.locale = locale;
        if (Locale.getDefault().equals(locale)) {
            if (DEFAULT_LOCALE_TEMPLATES == null) {
                DEFAULT_LOCALE_TEMPLATES = DefaultProblemFactory.loadMessageTemplates(locale);
            }
            this.messageTemplates = DEFAULT_LOCALE_TEMPLATES;
        } else {
            this.messageTemplates = DefaultProblemFactory.loadMessageTemplates(locale);
        }
    }

    @Override
    public final String getLocalizedMessage(int id, String[] problemArguments) {
        return this.getLocalizedMessage(id, 0, problemArguments);
    }

    @Override
    public final String getLocalizedMessage(int id, int elaborationId, String[] problemArguments) {
        StringBuffer output;
        block12: {
            String rawMessage = (String)this.messageTemplates.get(DefaultProblemFactory.keyFromID(id & 0x1FFFFF));
            if (rawMessage == null) {
                return "Unable to retrieve the error message for problem id: " + (id & 0x1FFFFF) + ". Check compiler resources.";
            }
            char[] message = rawMessage.toCharArray();
            if (elaborationId != 0) {
                String elaboration = (String)this.messageTemplates.get(DefaultProblemFactory.keyFromID(elaborationId));
                if (elaboration == null) {
                    return "Unable to retrieve the error message elaboration for elaboration id: " + elaborationId + ". Check compiler resources.";
                }
                message = CharOperation.replace(message, FIRST_ARGUMENT, elaboration.toCharArray());
            }
            message = CharOperation.replace(message, DOUBLE_QUOTES, SINGLE_QUOTE);
            if (problemArguments == null) {
                return new String(message);
            }
            int length = message.length;
            int start = 0;
            int end = length;
            output = null;
            if ((id & Integer.MIN_VALUE) != 0) {
                output = new StringBuffer(10 + length + problemArguments.length * 20);
                output.append((String)this.messageTemplates.get(DefaultProblemFactory.keyFromID(514)));
            }
            while ((end = CharOperation.indexOf('{', message, start)) > -1) {
                if (output == null) {
                    output = new StringBuffer(length + problemArguments.length * 20);
                }
                output.append(message, start, end - start);
                start = CharOperation.indexOf('}', message, end + 1);
                if (start > -1) {
                    try {
                        output.append(problemArguments[CharOperation.parseInt(message, end + 1, start - end - 1)]);
                    }
                    catch (NumberFormatException numberFormatException) {
                        output.append(message, end + 1, start - end);
                    }
                    catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                        return "Cannot bind message for problem (id: " + (id & 0x1FFFFF) + ") \"" + new String(message) + "\" with arguments: {" + Util.toString(problemArguments) + "}";
                    }
                    ++start;
                    continue;
                }
                output.append(message, end, length);
                break block12;
            }
            if (output == null) {
                return new String(message);
            }
            output.append(message, start, length - start);
        }
        return output.toString();
    }

    public final String localizedMessage(CategorizedProblem problem) {
        return this.getLocalizedMessage(problem.getID(), problem.getArguments());
    }

    public static HashtableOfInt loadMessageTemplates(Locale loc) {
        ResourceBundle bundle = null;
        String bundleName = "org.eclipse.jdt.internal.compiler.problem.messages";
        try {
            bundle = ResourceBundle.getBundle(bundleName, loc);
        }
        catch (MissingResourceException e) {
            System.out.println("Missing resource : " + bundleName.replace('.', '/') + ".properties for locale " + loc);
            throw e;
        }
        HashtableOfInt templates = new HashtableOfInt(700);
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            try {
                int messageID = Integer.parseInt(key);
                templates.put(DefaultProblemFactory.keyFromID(messageID), bundle.getString(key));
            }
            catch (NumberFormatException | MissingResourceException runtimeException) {}
        }
        return templates;
    }
}

