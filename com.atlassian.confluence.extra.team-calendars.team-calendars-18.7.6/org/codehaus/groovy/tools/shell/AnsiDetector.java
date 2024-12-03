/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.TerminalFactory
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.util.concurrent.Callable;
import jline.TerminalFactory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;

public class AnsiDetector
implements Callable<Boolean>,
GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;

    public AnsiDetector() {
        MetaClass metaClass;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Boolean call() throws Exception {
        return TerminalFactory.create().isAnsiSupported();
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AnsiDetector.class) {
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

