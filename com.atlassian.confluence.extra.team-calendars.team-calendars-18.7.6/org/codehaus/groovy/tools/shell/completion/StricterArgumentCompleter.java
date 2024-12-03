/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.ArgumentCompleter
 *  jline.console.completer.ArgumentCompleter$ArgumentDelimiter
 *  jline.console.completer.ArgumentCompleter$ArgumentList
 *  jline.console.completer.Completer
 *  jline.internal.Preconditions
 */
package org.codehaus.groovy.tools.shell.completion;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.internal.Preconditions;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class StricterArgumentCompleter
extends ArgumentCompleter
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;

    public StricterArgumentCompleter(List<Completer> completers) {
        super(completers);
        MetaClass metaClass;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        if (this.isStrict()) {
            Preconditions.checkNotNull(candidates);
            ArgumentCompleter.ArgumentDelimiter delim = this.getDelimiter();
            ArgumentCompleter.ArgumentList list = delim.delimit((CharSequence)buffer, cursor);
            int argIndex = list.getCursorArgumentIndex();
            int i = 0;
            while (i < argIndex) {
                Completer sub = (Completer)ScriptBytecodeAdapter.castToType(this.getCompleters().get(i >= this.getCompleters().size() ? this.getCompleters().size() - 1 : i), Completer.class);
                Object[] args = list.getArguments();
                String arg = args == null || i >= args.length ? "" : ShortTypeHandling.castToString(BytecodeInterface8.objectArrayGet(args, i));
                LinkedList subCandidates = new LinkedList();
                if (ScriptBytecodeAdapter.compareEqual(sub.complete(arg, arg.length(), subCandidates), -1)) {
                    return -1;
                }
                boolean candidateMatches = false;
                CharSequence subCandidate = null;
                Iterator iterator = subCandidates.iterator();
                while (iterator.hasNext()) {
                    boolean bl;
                    subCandidate = (CharSequence)ScriptBytecodeAdapter.castToType(iterator.next(), CharSequence.class);
                    String trimmedCand = subCandidate.toString().trim();
                    if (!trimmedCand.equals(arg)) continue;
                    candidateMatches = bl = true;
                    break;
                }
                if (!candidateMatches) {
                    return -1;
                }
                int n = i;
                int cfr_ignored_0 = n + 1;
            }
        }
        return super.complete(buffer, cursor, candidates);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != StricterArgumentCompleter.class) {
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

