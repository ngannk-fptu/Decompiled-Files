/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.util.Set;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.transform.tailrec.VariableReplacedListener;

public class UsedVariableTracker
implements VariableReplacedListener,
GroovyObject {
    private final Set<String> usedVariableNames;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public UsedVariableTracker() {
        MetaClass metaClass;
        Set set;
        this.usedVariableNames = set = (Set)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[0]), Set.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void variableReplaced(VariableExpression oldVar, VariableExpression newVar) {
        this.usedVariableNames.add(newVar.getName());
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != UsedVariableTracker.class) {
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

    public final Set<String> getUsedVariableNames() {
        return this.usedVariableNames;
    }
}

