/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.completion;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.Node;
import groovy.util.NodeList;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class NavigablePropertiesCompleter
implements GroovyObject {
    private static final Pattern NO_CONTROL_CHARS_PATTERN;
    private static final Pattern INVALID_CHAR_FOR_IDENTIFIER_PATTERN;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public NavigablePropertiesCompleter() {
        MetaClass metaClass;
        CallSite[] callSiteArray = NavigablePropertiesCompleter.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public void addCompletions(Object instance, String prefix, Set<CharSequence> candidates) {
        CallSite[] callSiteArray = NavigablePropertiesCompleter.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(instance, null)) {
            return;
        }
        callSiteArray[0].callCurrent(this, instance, prefix, candidates);
    }

    public void addIndirectObjectMembers(Object instance, String prefix, Set<CharSequence> candidates) {
        CallSite[] callSiteArray = NavigablePropertiesCompleter.$getCallSiteArray();
        if (instance instanceof Map) {
            Map map = (Map)ScriptBytecodeAdapter.castToType(instance, Map.class);
            callSiteArray[1].callStatic(NavigablePropertiesCompleter.class, map, prefix, candidates);
        }
        if (instance instanceof Node) {
            Node node = (Node)ScriptBytecodeAdapter.castToType(instance, Node.class);
            callSiteArray[2].callCurrent(this, node, prefix, candidates);
        }
        if (instance instanceof NodeList) {
            NodeList nodeList = (NodeList)ScriptBytecodeAdapter.castToType(instance, NodeList.class);
            callSiteArray[3].callCurrent(this, nodeList, prefix, candidates);
        }
    }

    public static void addMapProperties(Map instance, String prefix, Set<CharSequence> candidates) {
        CallSite[] callSiteArray = NavigablePropertiesCompleter.$getCallSiteArray();
        String key = null;
        public class _addMapProperties_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _addMapProperties_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _addMapProperties_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _addMapProperties_closure1.$getCallSiteArray();
                return it instanceof String;
            }

            public Object doCall() {
                CallSite[] callSiteArray = _addMapProperties_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _addMapProperties_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[]{};
                return new CallSiteArray(_addMapProperties_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _addMapProperties_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[4].call(callSiteArray[5].call(callSiteArray[6].call(instance), new _addMapProperties_closure1(NavigablePropertiesCompleter.class, NavigablePropertiesCompleter.class))), Iterator.class);
        while (iterator.hasNext()) {
            key = ShortTypeHandling.castToString(iterator.next());
            if (!(DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call((Object)key, NO_CONTROL_CHARS_PATTERN)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[8].call((Object)key, prefix)))) continue;
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[9].call((Object)key, INVALID_CHAR_FOR_IDENTIFIER_PATTERN)) || DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call((Object)key, "$"))) {
                Object object = callSiteArray[11].call(callSiteArray[12].call(key, "\\", "\\\\"), "'", "\\'");
                key = ShortTypeHandling.castToString(object);
                Object object2 = callSiteArray[13].call(callSiteArray[14].call((Object)"'", key), "'");
                key = ShortTypeHandling.castToString(object2);
            }
            callSiteArray[15].call(candidates, key);
        }
    }

    public void addNodeListEntries(NodeList instance, String prefix, Set<CharSequence> candidates) {
        CallSite[] callSiteArray = NavigablePropertiesCompleter.$getCallSiteArray();
        Object member = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[16].call(instance), Iterator.class);
        while (iterator.hasNext()) {
            member = iterator.next();
            callSiteArray[17].callCurrent(this, member, prefix, candidates);
        }
    }

    public void addNodeChildren(Node instance, String prefix, Set<CharSequence> candidates) {
        CallSite[] callSiteArray = NavigablePropertiesCompleter.$getCallSiteArray();
        Object child = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[18].call(callSiteArray[19].call(instance)), Iterator.class);
        while (iterator.hasNext()) {
            child = iterator.next();
            String member = "";
            if (child instanceof String) {
                String string;
                member = string = ShortTypeHandling.castToString(child);
            } else if (child instanceof Node) {
                Object object = callSiteArray[20].call((Node)ScriptBytecodeAdapter.castToType(child, Node.class));
                member = ShortTypeHandling.castToString(object);
            } else {
                if (!(child instanceof NodeList)) continue;
                Object node = null;
                Iterator iterator2 = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[21].call((NodeList)ScriptBytecodeAdapter.castToType(child, NodeList.class)), Iterator.class);
                while (iterator2.hasNext()) {
                    node = iterator2.next();
                    callSiteArray[22].callCurrent(this, ScriptBytecodeAdapter.createPojoWrapper((Node)ScriptBytecodeAdapter.castToType(node, Node.class), Node.class), prefix, candidates);
                }
            }
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call((Object)member, prefix))) continue;
            callSiteArray[24].call(candidates, member);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != NavigablePropertiesCompleter.class) {
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

    static {
        Object object = ScriptBytecodeAdapter.bitwiseNegate("^[^\\p{Cntrl}]+$");
        NO_CONTROL_CHARS_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object, Pattern.class);
        Object object2 = ScriptBytecodeAdapter.bitwiseNegate("[ @#%^&\u00a7()+\\-={}\\[\\]~`\u00b4<>,.\"'/!?:;|\\\\]");
        INVALID_CHAR_FOR_IDENTIFIER_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object2, Pattern.class);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "addIndirectObjectMembers";
        stringArray[1] = "addMapProperties";
        stringArray[2] = "addNodeChildren";
        stringArray[3] = "addNodeListEntries";
        stringArray[4] = "iterator";
        stringArray[5] = "findAll";
        stringArray[6] = "keySet";
        stringArray[7] = "matches";
        stringArray[8] = "startsWith";
        stringArray[9] = "find";
        stringArray[10] = "endsWith";
        stringArray[11] = "replace";
        stringArray[12] = "replace";
        stringArray[13] = "plus";
        stringArray[14] = "plus";
        stringArray[15] = "add";
        stringArray[16] = "iterator";
        stringArray[17] = "addIndirectObjectMembers";
        stringArray[18] = "iterator";
        stringArray[19] = "children";
        stringArray[20] = "name";
        stringArray[21] = "iterator";
        stringArray[22] = "addNodeChildren";
        stringArray[23] = "startsWith";
        stringArray[24] = "add";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[25];
        NavigablePropertiesCompleter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(NavigablePropertiesCompleter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = NavigablePropertiesCompleter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

