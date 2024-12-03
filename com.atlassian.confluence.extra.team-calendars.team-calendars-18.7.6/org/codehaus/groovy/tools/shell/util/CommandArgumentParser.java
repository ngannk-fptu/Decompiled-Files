/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class CommandArgumentParser
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;

    public CommandArgumentParser() {
        MetaClass metaClass;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static List<String> parseLine(String untrimmedLine, int numTokensToCollect) {
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = untrimmedLine;
            valueRecorder.record(string, 8);
            if (string != null) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert untrimmedLine != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        String line = untrimmedLine.trim();
        List tokens = ScriptBytecodeAdapter.createList(new Object[0]);
        String currentToken = "";
        boolean singleHyphenOpen = false;
        boolean doubleHyphenOpen = false;
        int index = 0;
        while (index < line.length() && !(tokens.size() == numTokensToCollect)) {
            String ch = ShortTypeHandling.castToString(Character.valueOf(line.charAt(index)));
            if (ScriptBytecodeAdapter.compareEqual(ch, Character.valueOf('\\')) && (singleHyphenOpen || doubleHyphenOpen)) {
                String string;
                String string2 = index == line.length() - 1 ? "\\" : Character.valueOf(line.charAt(index + 1));
                ch = ShortTypeHandling.castToString(string2);
                int n = index;
                index = n + 1;
                currentToken = string = StringGroovyMethods.plus(currentToken, (CharSequence)ch);
            } else if (ScriptBytecodeAdapter.compareEqual(ch, Character.valueOf('\"')) && !singleHyphenOpen) {
                if (doubleHyphenOpen) {
                    boolean bl;
                    String string;
                    tokens.add(currentToken);
                    currentToken = string = "";
                    doubleHyphenOpen = bl = false;
                } else {
                    boolean bl;
                    if (StringGroovyMethods.size(currentToken) > 0) {
                        String string;
                        tokens.add(currentToken);
                        currentToken = string = "";
                    }
                    doubleHyphenOpen = bl = true;
                }
            } else if (ScriptBytecodeAdapter.compareEqual(ch, Character.valueOf('\'')) && !doubleHyphenOpen) {
                if (singleHyphenOpen) {
                    boolean bl;
                    String string;
                    tokens.add(currentToken);
                    currentToken = string = "";
                    singleHyphenOpen = bl = false;
                } else {
                    boolean bl;
                    if (StringGroovyMethods.size(currentToken) > 0) {
                        String string;
                        tokens.add(currentToken);
                        currentToken = string = "";
                    }
                    singleHyphenOpen = bl = true;
                }
            } else if (ScriptBytecodeAdapter.compareEqual(ch, Character.valueOf(' ')) && !doubleHyphenOpen && !singleHyphenOpen) {
                if (StringGroovyMethods.size(currentToken) > 0) {
                    String string;
                    tokens.add(currentToken);
                    currentToken = string = "";
                }
            } else {
                String string;
                currentToken = string = StringGroovyMethods.plus(currentToken, (CharSequence)ch);
            }
            int n = index;
            int cfr_ignored_0 = n + 1;
        }
        if (index == line.length() && doubleHyphenOpen) {
            throw (Throwable)new IllegalArgumentException(StringGroovyMethods.plus((CharSequence)StringGroovyMethods.plus(StringGroovyMethods.plus("Missing closing \" in ", (CharSequence)line), (CharSequence)" -- "), (Object)tokens));
        }
        if (index == line.length() && singleHyphenOpen) {
            throw (Throwable)new IllegalArgumentException(StringGroovyMethods.plus((CharSequence)StringGroovyMethods.plus(StringGroovyMethods.plus("Missing closing ' in ", (CharSequence)line), (CharSequence)" -- "), (Object)tokens));
        }
        if (StringGroovyMethods.size(currentToken) > 0) {
            tokens.add(currentToken);
        }
        return tokens;
    }

    public static List<String> parseLine(String untrimmedLine) {
        return CommandArgumentParser.parseLine(untrimmedLine, -1);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CommandArgumentParser.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }
}

