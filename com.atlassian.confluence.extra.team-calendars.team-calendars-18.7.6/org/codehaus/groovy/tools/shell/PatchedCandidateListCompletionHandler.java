/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.ConsoleReader
 *  jline.console.CursorBuffer
 *  jline.console.completer.CandidateListCompletionHandler
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.IOException;
import java.util.List;
import jline.console.ConsoleReader;
import jline.console.CursorBuffer;
import jline.console.completer.CandidateListCompletionHandler;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.util.JAnsiHelper;

public class PatchedCandidateListCompletionHandler
extends CandidateListCompletionHandler
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;

    public PatchedCandidateListCompletionHandler() {
        MetaClass metaClass;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public boolean complete(ConsoleReader reader, List<CharSequence> candidates, int pos) throws IOException {
        CursorBuffer buf = reader.getCursorBuffer();
        public class _complete_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _complete_closure1(Object _outerInstance, Object _thisObject) {
                super(_outerInstance, _thisObject);
            }

            public Object doCall(CharSequence candidate) {
                return JAnsiHelper.stripAnsi(candidate);
            }

            public Object call(CharSequence candidate) {
                return this.doCall(candidate);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _complete_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        List<CharSequence> deAnsifiedcandidates = DefaultGroovyMethods.collect(candidates, new _complete_closure1(this, this));
        if (candidates.size() == 1) {
            CharSequence value = (CharSequence)ScriptBytecodeAdapter.castToType(deAnsifiedcandidates.get(0), CharSequence.class);
            if (value.equals(buf.toString())) {
                return false;
            }
            CandidateListCompletionHandler.setBuffer((ConsoleReader)reader, (CharSequence)value, (int)pos);
            return true;
        }
        if (candidates.size() > 1) {
            String value = this.getUnambiguousCompletions(deAnsifiedcandidates);
            CandidateListCompletionHandler.setBuffer((ConsoleReader)reader, (CharSequence)value, (int)pos);
        }
        CandidateListCompletionHandler.printCandidates((ConsoleReader)reader, candidates);
        reader.drawLine();
        return true;
    }

    private String getUnambiguousCompletions(List<CharSequence> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return ShortTypeHandling.castToString(null);
        }
        Object[] strings = (String[])ScriptBytecodeAdapter.castToType(candidates.toArray((Object[])ScriptBytecodeAdapter.castToType(new String[candidates.size()], Object[].class)), String[].class);
        String first = ShortTypeHandling.castToString(BytecodeInterface8.objectArrayGet(strings, 0));
        StringBuilder candidate = new StringBuilder();
        int i = 0;
        while (i < first.length() && this.startsWith(first.substring(0, i + 1), (String[])strings)) {
            candidate.append(first.charAt(i));
            int n = i;
            int cfr_ignored_0 = n + 1;
        }
        return candidate.toString();
    }

    private boolean startsWith(String starts, String ... candidates) {
        String candidate2 = null;
        String[] stringArray = candidates;
        if (candidates != null) {
            for (String candidate2 : stringArray) {
                if (!(!candidate2.startsWith(starts))) continue;
                return false;
            }
        }
        return true;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != PatchedCandidateListCompletionHandler.class) {
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

