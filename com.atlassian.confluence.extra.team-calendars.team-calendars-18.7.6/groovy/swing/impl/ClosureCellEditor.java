/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.impl;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.awt.Component;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ClosureCellEditor
extends AbstractCellEditor
implements TableCellEditor,
TreeCellEditor,
GroovyObject {
    private Map<String, Closure> callbacks;
    private Closure prepareEditor;
    private Closure editorValue;
    private List children;
    private boolean defaultEditor;
    private JTable table;
    private JTree tree;
    private Object value;
    private boolean selected;
    private boolean expanded;
    private boolean leaf;
    private int row;
    private int column;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ClosureCellEditor(Closure c, Map<String, Closure> callbacks) {
        MetaClass metaClass;
        List list;
        Map map;
        CallSite[] callSiteArray = ClosureCellEditor.$getCallSiteArray();
        this.callbacks = map = ScriptBytecodeAdapter.createMap(new Object[0]);
        this.children = list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Closure closure = c;
        this.editorValue = (Closure)ScriptBytecodeAdapter.castToType(closure, Closure.class);
        callSiteArray[0].call(this.callbacks, callbacks);
    }

    public ClosureCellEditor(Closure c) {
        CallSite[] callSiteArray = ClosureCellEditor.$getCallSiteArray();
        this(c, ScriptBytecodeAdapter.createMap(new Object[0]));
    }

    public ClosureCellEditor() {
        CallSite[] callSiteArray = ClosureCellEditor.$getCallSiteArray();
        this(null, ScriptBytecodeAdapter.createMap(new Object[0]));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Object object;
        CallSite[] callSiteArray = ClosureCellEditor.$getCallSiteArray();
        JTable jTable = table;
        this.table = (JTable)ScriptBytecodeAdapter.castToType(jTable, JTable.class);
        Object var8_8 = null;
        this.tree = (JTree)ScriptBytecodeAdapter.castToType(var8_8, JTree.class);
        this.value = object = value;
        boolean bl = isSelected;
        this.selected = DefaultTypeTransformation.booleanUnbox(bl);
        boolean bl2 = false;
        this.expanded = DefaultTypeTransformation.booleanUnbox(bl2);
        boolean bl3 = false;
        this.leaf = DefaultTypeTransformation.booleanUnbox(bl3);
        int n = row;
        this.row = DefaultTypeTransformation.intUnbox(n);
        int n2 = column;
        this.column = DefaultTypeTransformation.intUnbox(n2);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return (Component)ScriptBytecodeAdapter.castToType(callSiteArray[1].callCurrent(this), Component.class);
        }
        return this.prepare();
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        Object object;
        CallSite[] callSiteArray = ClosureCellEditor.$getCallSiteArray();
        Object var8_8 = null;
        this.table = (JTable)ScriptBytecodeAdapter.castToType(var8_8, JTable.class);
        JTree jTree = tree;
        this.tree = (JTree)ScriptBytecodeAdapter.castToType(jTree, JTree.class);
        this.value = object = value;
        boolean bl = isSelected;
        this.selected = DefaultTypeTransformation.booleanUnbox(bl);
        boolean bl2 = expanded;
        this.expanded = DefaultTypeTransformation.booleanUnbox(bl2);
        boolean bl3 = leaf;
        this.leaf = DefaultTypeTransformation.booleanUnbox(bl3);
        int n = row;
        this.row = DefaultTypeTransformation.intUnbox(n);
        Integer n2 = -1;
        this.column = DefaultTypeTransformation.intUnbox(n2);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return (Component)ScriptBytecodeAdapter.castToType(callSiteArray[2].callCurrent(this), Component.class);
        }
        return this.prepare();
    }

    private Component prepare() {
        CallSite[] callSiteArray = ClosureCellEditor.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(this.children)) || this.defaultEditor) {
            boolean bl;
            this.defaultEditor = bl = true;
            callSiteArray[4].call(this.children);
            if (DefaultTypeTransformation.booleanUnbox(this.table)) {
                TableCellEditor tce = (TableCellEditor)ScriptBytecodeAdapter.castToType(callSiteArray[5].call((Object)this.table, callSiteArray[6].call((Object)this.table, this.column)), TableCellEditor.class);
                callSiteArray[7].call((Object)this.children, callSiteArray[8].call((Object)tce, ArrayUtil.createArray(this.table, this.value, this.selected, this.row, this.column)));
            } else if (DefaultTypeTransformation.booleanUnbox(this.tree)) {
                TreeCellEditor tce = (TreeCellEditor)ScriptBytecodeAdapter.castToType(callSiteArray[9].callConstructor(DefaultCellEditor.class, callSiteArray[10].callConstructor(JTextField.class)), TreeCellEditor.class);
                callSiteArray[11].call((Object)this.children, callSiteArray[12].call((Object)tce, ArrayUtil.createArray(this.tree, this.value, this.selected, this.expanded, this.leaf, this.row)));
            }
        }
        Object o = callSiteArray[13].call(this.prepareEditor);
        if (o instanceof Component) {
            return (Component)ScriptBytecodeAdapter.castToType(o, Component.class);
        }
        return (Component)ScriptBytecodeAdapter.castToType(callSiteArray[14].call((Object)this.children, 0), Component.class);
    }

    @Override
    public Object getCellEditorValue() {
        CallSite[] callSiteArray = ClosureCellEditor.$getCallSiteArray();
        return callSiteArray[15].call(this.editorValue);
    }

    public void setEditorValue(Closure editorValue) {
        CallSite[] callSiteArray = ClosureCellEditor.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareNotEqual(editorValue, null)) {
            ClosureCellEditor closureCellEditor = this;
            ScriptBytecodeAdapter.setGroovyObjectProperty(closureCellEditor, ClosureCellEditor.class, editorValue, "delegate");
            Object object = callSiteArray[16].callGetProperty(Closure.class);
            ScriptBytecodeAdapter.setGroovyObjectProperty(object, ClosureCellEditor.class, editorValue, "resolveStrategy");
        }
        Closure closure = editorValue;
        this.editorValue = (Closure)ScriptBytecodeAdapter.castToType(closure, Closure.class);
    }

    public void setPrepareEditor(Closure prepareEditor) {
        CallSite[] callSiteArray = ClosureCellEditor.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareNotEqual(prepareEditor, null)) {
            ClosureCellEditor closureCellEditor = this;
            ScriptBytecodeAdapter.setGroovyObjectProperty(closureCellEditor, ClosureCellEditor.class, prepareEditor, "delegate");
            Object object = callSiteArray[17].callGetProperty(Closure.class);
            ScriptBytecodeAdapter.setGroovyObjectProperty(object, ClosureCellEditor.class, prepareEditor, "resolveStrategy");
        }
        Closure closure = prepareEditor;
        this.prepareEditor = (Closure)ScriptBytecodeAdapter.castToType(closure, Closure.class);
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        CallSite[] callSiteArray = ClosureCellEditor.$getCallSiteArray();
        Object calledMethod = callSiteArray[18].call(callSiteArray[19].callGetProperty(ClosureCellEditor.class), name, args);
        if (DefaultTypeTransformation.booleanUnbox(ScriptBytecodeAdapter.getProperty(ClosureCellEditor.class, this.callbacks, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})))) && ScriptBytecodeAdapter.getProperty(ClosureCellEditor.class, this.callbacks, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""}))) instanceof Closure) {
            return callSiteArray[20].call(ScriptBytecodeAdapter.getProperty(ClosureCellEditor.class, this.callbacks, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""}))), calledMethod, this, args);
        }
        return callSiteArray[21].callSafe(calledMethod, this, args);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ClosureCellEditor.class) {
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
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public Map<String, Closure> getCallbacks() {
        return this.callbacks;
    }

    public void setCallbacks(Map<String, Closure> map) {
        this.callbacks = map;
    }

    public Closure getPrepareEditor() {
        return this.prepareEditor;
    }

    public Closure getEditorValue() {
        return this.editorValue;
    }

    public List getChildren() {
        return this.children;
    }

    public void setChildren(List list) {
        this.children = list;
    }

    public boolean getDefaultEditor() {
        return this.defaultEditor;
    }

    public boolean isDefaultEditor() {
        return this.defaultEditor;
    }

    public void setDefaultEditor(boolean bl) {
        this.defaultEditor = bl;
    }

    public JTable getTable() {
        return this.table;
    }

    public void setTable(JTable jTable) {
        this.table = jTable;
    }

    public JTree getTree() {
        return this.tree;
    }

    public void setTree(JTree jTree) {
        this.tree = jTree;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object object) {
        this.value = object;
    }

    public boolean getSelected() {
        return this.selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean bl) {
        this.selected = bl;
    }

    public boolean getExpanded() {
        return this.expanded;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean bl) {
        this.expanded = bl;
    }

    public boolean getLeaf() {
        return this.leaf;
    }

    public boolean isLeaf() {
        return this.leaf;
    }

    public void setLeaf(boolean bl) {
        this.leaf = bl;
    }

    public int getRow() {
        return this.row;
    }

    public void setRow(int n) {
        this.row = n;
    }

    public int getColumn() {
        return this.column;
    }

    public void setColumn(int n) {
        this.column = n;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "putAll";
        stringArray[1] = "prepare";
        stringArray[2] = "prepare";
        stringArray[3] = "isEmpty";
        stringArray[4] = "clear";
        stringArray[5] = "getDefaultEditor";
        stringArray[6] = "getColumnClass";
        stringArray[7] = "add";
        stringArray[8] = "getTableCellEditorComponent";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "add";
        stringArray[12] = "getTreeCellEditorComponent";
        stringArray[13] = "call";
        stringArray[14] = "getAt";
        stringArray[15] = "call";
        stringArray[16] = "DELEGATE_FIRST";
        stringArray[17] = "DELEGATE_FIRST";
        stringArray[18] = "getMetaMethod";
        stringArray[19] = "metaClass";
        stringArray[20] = "call";
        stringArray[21] = "invoke";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[22];
        ClosureCellEditor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ClosureCellEditor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ClosureCellEditor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

