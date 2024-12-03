/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.lang.Reference;
import groovy.swing.LookAndFeelHelper;
import groovy.swing.factory.ActionFactory;
import groovy.swing.factory.BevelBorderFactory;
import groovy.swing.factory.BindFactory;
import groovy.swing.factory.BindGroupFactory;
import groovy.swing.factory.BindProxyFactory;
import groovy.swing.factory.BoxFactory;
import groovy.swing.factory.BoxLayoutFactory;
import groovy.swing.factory.ButtonGroupFactory;
import groovy.swing.factory.CellEditorFactory;
import groovy.swing.factory.CellEditorGetValueFactory;
import groovy.swing.factory.CellEditorPrepareFactory;
import groovy.swing.factory.ClosureColumnFactory;
import groovy.swing.factory.CollectionFactory;
import groovy.swing.factory.ColumnFactory;
import groovy.swing.factory.ColumnModelFactory;
import groovy.swing.factory.ComboBoxFactory;
import groovy.swing.factory.ComponentFactory;
import groovy.swing.factory.CompoundBorderFactory;
import groovy.swing.factory.DialogFactory;
import groovy.swing.factory.EmptyBorderFactory;
import groovy.swing.factory.EtchedBorderFactory;
import groovy.swing.factory.FormattedTextFactory;
import groovy.swing.factory.FrameFactory;
import groovy.swing.factory.GlueFactory;
import groovy.swing.factory.GridBagFactory;
import groovy.swing.factory.HBoxFactory;
import groovy.swing.factory.HGlueFactory;
import groovy.swing.factory.HStrutFactory;
import groovy.swing.factory.ImageIconFactory;
import groovy.swing.factory.InternalFrameFactory;
import groovy.swing.factory.LayoutFactory;
import groovy.swing.factory.LineBorderFactory;
import groovy.swing.factory.ListFactory;
import groovy.swing.factory.MapFactory;
import groovy.swing.factory.MatteBorderFactory;
import groovy.swing.factory.PropertyColumnFactory;
import groovy.swing.factory.RendererFactory;
import groovy.swing.factory.RendererUpdateFactory;
import groovy.swing.factory.RichActionWidgetFactory;
import groovy.swing.factory.RigidAreaFactory;
import groovy.swing.factory.ScrollPaneFactory;
import groovy.swing.factory.SeparatorFactory;
import groovy.swing.factory.SplitPaneFactory;
import groovy.swing.factory.TDFactory;
import groovy.swing.factory.TRFactory;
import groovy.swing.factory.TabbedPaneFactory;
import groovy.swing.factory.TableFactory;
import groovy.swing.factory.TableLayoutFactory;
import groovy.swing.factory.TableModelFactory;
import groovy.swing.factory.TextArgWidgetFactory;
import groovy.swing.factory.TitledBorderFactory;
import groovy.swing.factory.VBoxFactory;
import groovy.swing.factory.VGlueFactory;
import groovy.swing.factory.VStrutFactory;
import groovy.swing.factory.WidgetFactory;
import groovy.swing.factory.WindowFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.OverlayLayout;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableColumn;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class SwingBuilder
extends FactoryBuilderSupport {
    private static final Logger LOG;
    private static boolean headless;
    private static final String DELEGATE_PROPERTY_OBJECT_ID = "_delegateProperty:id";
    private static final String DEFAULT_DELEGATE_PROPERTY_OBJECT_ID = "id";
    private static final Random random;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public SwingBuilder(boolean init) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        super(init);
        Object object = callSiteArray[0].call(GraphicsEnvironment.class);
        headless = DefaultTypeTransformation.booleanUnbox(object);
        Object object2 = callSiteArray[1].callConstructor(LinkedList.class);
        ScriptBytecodeAdapter.setGroovyObjectProperty(object2, SwingBuilder.class, this, "containingWindows");
        String string = DEFAULT_DELEGATE_PROPERTY_OBJECT_ID;
        callSiteArray[2].call(this, DELEGATE_PROPERTY_OBJECT_ID, string);
    }

    public SwingBuilder() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        this(true);
    }

    public Object registerSupportNodes() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[3].callCurrent(this, "action", callSiteArray[4].callConstructor(ActionFactory.class));
        callSiteArray[5].callCurrent(this, "actions", callSiteArray[6].callConstructor(CollectionFactory.class));
        callSiteArray[7].callCurrent(this, "map", callSiteArray[8].callConstructor(MapFactory.class));
        callSiteArray[9].callCurrent(this, "imageIcon", callSiteArray[10].callConstructor(ImageIconFactory.class));
        callSiteArray[11].callCurrent(this, "buttonGroup", callSiteArray[12].callConstructor(ButtonGroupFactory.class));
        callSiteArray[13].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.getMethodPointer(ButtonGroupFactory.class, "buttonGroupAttributeDelegate"));
        callSiteArray[14].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.getMethodPointer(SwingBuilder.class, "objectIDAttributeDelegate"));
        callSiteArray[15].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.getMethodPointer(SwingBuilder.class, "clientPropertyAttributeDelegate"));
        callSiteArray[16].callCurrent(this, "noparent", callSiteArray[17].callConstructor(CollectionFactory.class));
        callSiteArray[18].callCurrent(this, "keyStrokeAction", ScriptBytecodeAdapter.getMethodPointer(this, "createKeyStrokeAction"));
        return callSiteArray[19].callCurrent(this, "shortcut", ScriptBytecodeAdapter.getMethodPointer(this, "shortcut"));
    }

    public Object registerBinding() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        BindFactory bindFactory = (BindFactory)ScriptBytecodeAdapter.castToType(callSiteArray[20].callConstructor(BindFactory.class), BindFactory.class);
        callSiteArray[21].callCurrent(this, "bind", bindFactory);
        callSiteArray[22].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.getMethodPointer(bindFactory, "bindingAttributeDelegate"));
        callSiteArray[23].callCurrent(this, "bindProxy", callSiteArray[24].callConstructor(BindProxyFactory.class));
        return callSiteArray[25].callCurrent(this, "bindGroup", callSiteArray[26].callConstructor(BindGroupFactory.class));
    }

    public Object registerPassThruNodes() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[27].callCurrent(this, "widget", callSiteArray[28].callConstructor(WidgetFactory.class, Component.class, true));
        callSiteArray[29].callCurrent(this, "container", callSiteArray[30].callConstructor(WidgetFactory.class, Component.class, false));
        return callSiteArray[31].callCurrent(this, "bean", callSiteArray[32].callConstructor(WidgetFactory.class, Object.class, true));
    }

    public Object registerWindows() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[33].callCurrent(this, "dialog", callSiteArray[34].callConstructor(DialogFactory.class));
        callSiteArray[35].callCurrent(this, "fileChooser", JFileChooser.class);
        callSiteArray[36].callCurrent(this, "frame", callSiteArray[37].callConstructor(FrameFactory.class));
        callSiteArray[38].callCurrent(this, "optionPane", JOptionPane.class);
        return callSiteArray[39].callCurrent(this, "window", callSiteArray[40].callConstructor(WindowFactory.class));
    }

    public Object registerActionButtonWidgets() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[41].callCurrent(this, "button", callSiteArray[42].callConstructor(RichActionWidgetFactory.class, JButton.class));
        callSiteArray[43].callCurrent(this, "checkBox", callSiteArray[44].callConstructor(RichActionWidgetFactory.class, JCheckBox.class));
        callSiteArray[45].callCurrent(this, "checkBoxMenuItem", callSiteArray[46].callConstructor(RichActionWidgetFactory.class, JCheckBoxMenuItem.class));
        callSiteArray[47].callCurrent(this, "menuItem", callSiteArray[48].callConstructor(RichActionWidgetFactory.class, JMenuItem.class));
        callSiteArray[49].callCurrent(this, "radioButton", callSiteArray[50].callConstructor(RichActionWidgetFactory.class, JRadioButton.class));
        callSiteArray[51].callCurrent(this, "radioButtonMenuItem", callSiteArray[52].callConstructor(RichActionWidgetFactory.class, JRadioButtonMenuItem.class));
        return callSiteArray[53].callCurrent(this, "toggleButton", callSiteArray[54].callConstructor(RichActionWidgetFactory.class, JToggleButton.class));
    }

    public Object registerTextWidgets() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[55].callCurrent(this, "editorPane", callSiteArray[56].callConstructor(TextArgWidgetFactory.class, JEditorPane.class));
        callSiteArray[57].callCurrent(this, "label", callSiteArray[58].callConstructor(TextArgWidgetFactory.class, JLabel.class));
        callSiteArray[59].callCurrent(this, "passwordField", callSiteArray[60].callConstructor(TextArgWidgetFactory.class, JPasswordField.class));
        callSiteArray[61].callCurrent(this, "textArea", callSiteArray[62].callConstructor(TextArgWidgetFactory.class, JTextArea.class));
        callSiteArray[63].callCurrent(this, "textField", callSiteArray[64].callConstructor(TextArgWidgetFactory.class, JTextField.class));
        callSiteArray[65].callCurrent(this, "formattedTextField", callSiteArray[66].callConstructor(FormattedTextFactory.class));
        return callSiteArray[67].callCurrent(this, "textPane", callSiteArray[68].callConstructor(TextArgWidgetFactory.class, JTextPane.class));
    }

    public Object registerMDIWidgets() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[69].callCurrent(this, "desktopPane", JDesktopPane.class);
        return callSiteArray[70].callCurrent(this, "internalFrame", callSiteArray[71].callConstructor(InternalFrameFactory.class));
    }

    public Object registerBasicWidgets() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[72].callCurrent(this, "colorChooser", JColorChooser.class);
        callSiteArray[73].callCurrent(this, "comboBox", callSiteArray[74].callConstructor(ComboBoxFactory.class));
        callSiteArray[75].callCurrent(this, "list", callSiteArray[76].callConstructor(ListFactory.class));
        callSiteArray[77].callCurrent(this, "progressBar", JProgressBar.class);
        callSiteArray[78].callCurrent(this, "separator", callSiteArray[79].callConstructor(SeparatorFactory.class));
        callSiteArray[80].callCurrent(this, "scrollBar", JScrollBar.class);
        callSiteArray[81].callCurrent(this, "slider", JSlider.class);
        callSiteArray[82].callCurrent(this, "spinner", JSpinner.class);
        return callSiteArray[83].callCurrent(this, "tree", JTree.class);
    }

    public Object registerMenuWidgets() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[84].callCurrent(this, "menu", JMenu.class);
        callSiteArray[85].callCurrent(this, "menuBar", JMenuBar.class);
        return callSiteArray[86].callCurrent(this, "popupMenu", JPopupMenu.class);
    }

    public Object registerContainers() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[87].callCurrent(this, "panel", JPanel.class);
        callSiteArray[88].callCurrent(this, "scrollPane", callSiteArray[89].callConstructor(ScrollPaneFactory.class));
        callSiteArray[90].callCurrent(this, "splitPane", callSiteArray[91].callConstructor(SplitPaneFactory.class));
        callSiteArray[92].callCurrent(this, "tabbedPane", callSiteArray[93].callConstructor(TabbedPaneFactory.class, JTabbedPane.class));
        callSiteArray[94].callCurrent(this, "toolBar", JToolBar.class);
        callSiteArray[95].callCurrent(this, "viewport", JViewport.class);
        return callSiteArray[96].callCurrent(this, "layeredPane", JLayeredPane.class);
    }

    public Object registerDataModels() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[97].callCurrent(this, "boundedRangeModel", DefaultBoundedRangeModel.class);
        callSiteArray[98].callCurrent(this, "spinnerDateModel", SpinnerDateModel.class);
        callSiteArray[99].callCurrent(this, "spinnerListModel", SpinnerListModel.class);
        return callSiteArray[100].callCurrent(this, "spinnerNumberModel", SpinnerNumberModel.class);
    }

    public Object registerTableComponents() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[101].callCurrent(this, "table", callSiteArray[102].callConstructor(TableFactory.class));
        callSiteArray[103].callCurrent(this, "tableColumn", TableColumn.class);
        callSiteArray[104].callCurrent(this, "tableModel", callSiteArray[105].callConstructor(TableModelFactory.class));
        callSiteArray[106].callCurrent(this, "propertyColumn", callSiteArray[107].callConstructor(PropertyColumnFactory.class));
        callSiteArray[108].callCurrent(this, "closureColumn", callSiteArray[109].callConstructor(ClosureColumnFactory.class));
        callSiteArray[110].callCurrent(this, "columnModel", callSiteArray[111].callConstructor(ColumnModelFactory.class));
        return callSiteArray[112].callCurrent(this, "column", callSiteArray[113].callConstructor(ColumnFactory.class));
    }

    public Object registerBasicLayouts() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[114].callCurrent(this, "borderLayout", callSiteArray[115].callConstructor(LayoutFactory.class, BorderLayout.class));
        callSiteArray[116].callCurrent(this, "cardLayout", callSiteArray[117].callConstructor(LayoutFactory.class, CardLayout.class));
        callSiteArray[118].callCurrent(this, "flowLayout", callSiteArray[119].callConstructor(LayoutFactory.class, FlowLayout.class));
        callSiteArray[120].callCurrent(this, "gridLayout", callSiteArray[121].callConstructor(LayoutFactory.class, GridLayout.class));
        callSiteArray[122].callCurrent(this, "overlayLayout", callSiteArray[123].callConstructor(LayoutFactory.class, OverlayLayout.class));
        callSiteArray[124].callCurrent(this, "springLayout", callSiteArray[125].callConstructor(LayoutFactory.class, SpringLayout.class));
        callSiteArray[126].callCurrent(this, "gridBagLayout", callSiteArray[127].callConstructor(GridBagFactory.class));
        callSiteArray[128].callCurrent(this, "gridBagConstraints", GridBagConstraints.class);
        callSiteArray[129].callCurrent(this, "gbc", GridBagConstraints.class);
        callSiteArray[130].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.getMethodPointer(GridBagFactory.class, "processGridBagConstraintsAttributes"));
        return callSiteArray[131].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.getMethodPointer(LayoutFactory.class, "constraintsAttributeDelegate"));
    }

    public Object registerBoxLayout() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[132].callCurrent(this, "boxLayout", callSiteArray[133].callConstructor(BoxLayoutFactory.class));
        callSiteArray[134].callCurrent(this, "box", callSiteArray[135].callConstructor(BoxFactory.class));
        callSiteArray[136].callCurrent(this, "hbox", callSiteArray[137].callConstructor(HBoxFactory.class));
        callSiteArray[138].callCurrent(this, "hglue", callSiteArray[139].callConstructor(HGlueFactory.class));
        callSiteArray[140].callCurrent(this, "hstrut", callSiteArray[141].callConstructor(HStrutFactory.class));
        callSiteArray[142].callCurrent(this, "vbox", callSiteArray[143].callConstructor(VBoxFactory.class));
        callSiteArray[144].callCurrent(this, "vglue", callSiteArray[145].callConstructor(VGlueFactory.class));
        callSiteArray[146].callCurrent(this, "vstrut", callSiteArray[147].callConstructor(VStrutFactory.class));
        callSiteArray[148].callCurrent(this, "glue", callSiteArray[149].callConstructor(GlueFactory.class));
        return callSiteArray[150].callCurrent(this, "rigidArea", callSiteArray[151].callConstructor(RigidAreaFactory.class));
    }

    public Object registerTableLayout() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[152].callCurrent(this, "tableLayout", callSiteArray[153].callConstructor(TableLayoutFactory.class));
        callSiteArray[154].callCurrent(this, "tr", callSiteArray[155].callConstructor(TRFactory.class));
        return callSiteArray[156].callCurrent(this, "td", callSiteArray[157].callConstructor(TDFactory.class));
    }

    public Object registerBorders() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[158].callCurrent(this, "lineBorder", callSiteArray[159].callConstructor(LineBorderFactory.class));
        callSiteArray[160].callCurrent(this, "loweredBevelBorder", callSiteArray[161].callConstructor(BevelBorderFactory.class, callSiteArray[162].callGetProperty(BevelBorder.class)));
        callSiteArray[163].callCurrent(this, "raisedBevelBorder", callSiteArray[164].callConstructor(BevelBorderFactory.class, callSiteArray[165].callGetProperty(BevelBorder.class)));
        callSiteArray[166].callCurrent(this, "etchedBorder", callSiteArray[167].callConstructor(EtchedBorderFactory.class, callSiteArray[168].callGetProperty(EtchedBorder.class)));
        callSiteArray[169].callCurrent(this, "loweredEtchedBorder", callSiteArray[170].callConstructor(EtchedBorderFactory.class, callSiteArray[171].callGetProperty(EtchedBorder.class)));
        callSiteArray[172].callCurrent(this, "raisedEtchedBorder", callSiteArray[173].callConstructor(EtchedBorderFactory.class, callSiteArray[174].callGetProperty(EtchedBorder.class)));
        callSiteArray[175].callCurrent(this, "titledBorder", callSiteArray[176].callConstructor(TitledBorderFactory.class));
        callSiteArray[177].callCurrent(this, "emptyBorder", callSiteArray[178].callConstructor(EmptyBorderFactory.class));
        callSiteArray[179].callCurrent(this, "compoundBorder", callSiteArray[180].callConstructor(CompoundBorderFactory.class));
        return callSiteArray[181].callCurrent(this, "matteBorder", callSiteArray[182].callConstructor(MatteBorderFactory.class));
    }

    public Object registerRenderers() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        RendererFactory renderFactory = (RendererFactory)ScriptBytecodeAdapter.castToType(callSiteArray[183].callConstructor(RendererFactory.class), RendererFactory.class);
        callSiteArray[184].callCurrent(this, "tableCellRenderer", renderFactory);
        callSiteArray[185].callCurrent(this, "listCellRenderer", renderFactory);
        callSiteArray[186].callCurrent(this, "onRender", callSiteArray[187].callConstructor(RendererUpdateFactory.class));
        callSiteArray[188].callCurrent(this, "cellRenderer", renderFactory);
        return callSiteArray[189].callCurrent(this, "headerRenderer", renderFactory);
    }

    public Object registerEditors() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[190].callCurrent(this, "cellEditor", callSiteArray[191].callConstructor(CellEditorFactory.class));
        callSiteArray[192].callCurrent(this, "editorValue", callSiteArray[193].callConstructor(CellEditorGetValueFactory.class));
        return callSiteArray[194].callCurrent(this, "prepareEditor", callSiteArray[195].callConstructor(CellEditorPrepareFactory.class));
    }

    public Object registerThreading() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[196].callCurrent(this, "edt", ScriptBytecodeAdapter.getMethodPointer(this, "edt"));
        callSiteArray[197].callCurrent(this, "doOutside", ScriptBytecodeAdapter.getMethodPointer(this, "doOutside"));
        return callSiteArray[198].callCurrent(this, "doLater", ScriptBytecodeAdapter.getMethodPointer(this, "doLater"));
    }

    @Override
    public void registerBeanFactory(String nodeName, String groupName, Class klass) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[199].call(LayoutManager.class, klass))) {
            callSiteArray[200].callCurrent(this, nodeName, groupName, callSiteArray[201].callConstructor(LayoutFactory.class, klass));
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[202].call(JScrollPane.class, klass))) {
            callSiteArray[203].callCurrent(this, nodeName, groupName, callSiteArray[204].callConstructor(ScrollPaneFactory.class, klass));
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[205].call(JTable.class, klass))) {
            callSiteArray[206].callCurrent(this, nodeName, groupName, callSiteArray[207].callConstructor(TableFactory.class, klass));
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[208].call(JComponent.class, klass)) || DefaultTypeTransformation.booleanUnbox(callSiteArray[209].call(JApplet.class, klass)) || DefaultTypeTransformation.booleanUnbox(callSiteArray[210].call(JDialog.class, klass)) || DefaultTypeTransformation.booleanUnbox(callSiteArray[211].call(JFrame.class, klass)) || DefaultTypeTransformation.booleanUnbox(callSiteArray[212].call(JWindow.class, klass))) {
            callSiteArray[213].callCurrent(this, nodeName, groupName, callSiteArray[214].callConstructor(ComponentFactory.class, klass));
        } else {
            ScriptBytecodeAdapter.invokeMethodOnSuperN(FactoryBuilderSupport.class, this, "registerBeanFactory", new Object[]{nodeName, groupName, klass});
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SwingBuilder edt(@DelegatesTo(value=SwingBuilder.class) Closure c) {
        Reference<Closure> c2 = new Reference<Closure>(c);
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[215].call((Object)c2.get(), this);
        if (headless || DefaultTypeTransformation.booleanUnbox(callSiteArray[216].call(SwingUtilities.class))) {
            callSiteArray[217].call((Object)c2.get(), this);
            return this;
        }
        Reference<Map> continuationData = new Reference<Map>((Map)ScriptBytecodeAdapter.castToType(callSiteArray[218].callCurrent(this), Map.class));
        try {
            try {
                if (!(c2.get() instanceof MethodClosure)) {
                    Object object = callSiteArray[219].call((Object)c2.get(), ScriptBytecodeAdapter.createList(new Object[]{this}));
                    c2.set((Closure)ScriptBytecodeAdapter.castToType(object, Closure.class));
                }
                public class _edt_closure1
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference continuationData;
                    private /* synthetic */ Reference c;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _edt_closure1(Object _outerInstance, Object _thisObject, Reference continuationData, Reference c) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _edt_closure1.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.continuationData = reference2 = continuationData;
                        this.c = reference = c;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _edt_closure1.$getCallSiteArray();
                        callSiteArray[0].callCurrent((GroovyObject)this, this.continuationData.get());
                        callSiteArray[1].call(this.c.get());
                        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                            Object object = callSiteArray[2].callCurrent(this);
                            this.continuationData.set((Map)ScriptBytecodeAdapter.castToType(object, Map.class));
                            return object;
                        }
                        Map map = this.getContinuationData();
                        this.continuationData.set(map);
                        return map;
                    }

                    public Map getContinuationData() {
                        CallSite[] callSiteArray = _edt_closure1.$getCallSiteArray();
                        return (Map)ScriptBytecodeAdapter.castToType(this.continuationData.get(), Map.class);
                    }

                    public Closure getC() {
                        CallSite[] callSiteArray = _edt_closure1.$getCallSiteArray();
                        return (Closure)ScriptBytecodeAdapter.castToType(this.c.get(), Closure.class);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _edt_closure1.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _edt_closure1.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "restoreFromContinuationData";
                        stringArray[1] = "call";
                        stringArray[2] = "getContinuationData";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[3];
                        _edt_closure1.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_edt_closure1.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _edt_closure1.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[220].call(SwingUtilities.class, new _edt_closure1(this, this, continuationData, c2));
            }
            catch (InterruptedException e) {
                throw (Throwable)callSiteArray[221].callConstructor(GroovyRuntimeException.class, "interrupted swing interaction", e);
            }
            catch (InvocationTargetException e) {
                throw (Throwable)callSiteArray[222].callConstructor(GroovyRuntimeException.class, "exception in event dispatch thread", callSiteArray[223].call(e));
            }
        }
        catch (Throwable throwable) {
            callSiteArray[225].callCurrent((GroovyObject)this, continuationData.get());
            throw throwable;
        }
        callSiteArray[224].callCurrent((GroovyObject)this, continuationData.get());
        return this;
    }

    public SwingBuilder doLater(@DelegatesTo(value=SwingBuilder.class) Closure c) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[226].call((Object)c, this);
        if (headless) {
            callSiteArray[227].call(c);
        } else {
            if (!(c instanceof MethodClosure)) {
                Object object = callSiteArray[228].call((Object)c, ScriptBytecodeAdapter.createList(new Object[]{this}));
                c = (Closure)ScriptBytecodeAdapter.castToType(object, Closure.class);
            }
            callSiteArray[229].call(SwingUtilities.class, c);
        }
        return this;
    }

    public SwingBuilder doOutside(@DelegatesTo(value=SwingBuilder.class) Closure c) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[230].call((Object)c, this);
        if (!(c instanceof MethodClosure)) {
            Object object = callSiteArray[231].call((Object)c, ScriptBytecodeAdapter.createList(new Object[]{this}));
            c = (Closure)ScriptBytecodeAdapter.castToType(object, Closure.class);
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[232].call(SwingUtilities.class))) {
            callSiteArray[233].call(callSiteArray[234].callConstructor(Thread.class, c));
        } else {
            callSiteArray[235].call(c);
        }
        return this;
    }

    public static SwingBuilder edtBuilder(@DelegatesTo(value=SwingBuilder.class) Closure c) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        SwingBuilder builder = (SwingBuilder)ScriptBytecodeAdapter.castToType(callSiteArray[236].callConstructor(SwingBuilder.class), SwingBuilder.class);
        return (SwingBuilder)ScriptBytecodeAdapter.castToType(callSiteArray[237].call((Object)builder, c), SwingBuilder.class);
    }

    @Deprecated
    public static SwingBuilder $static_methodMissing(String method, Object args) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(method, "build") && ScriptBytecodeAdapter.compareEqual(callSiteArray[238].callGetProperty(args), 1) && callSiteArray[239].call(args, 0) instanceof Closure) {
                return (SwingBuilder)ScriptBytecodeAdapter.castToType(callSiteArray[240].callStatic(SwingBuilder.class, callSiteArray[241].call(args, 0)), SwingBuilder.class);
            }
            throw (Throwable)callSiteArray[242].callConstructor(MissingMethodException.class, method, SwingBuilder.class, args, true);
        }
        if (ScriptBytecodeAdapter.compareEqual(method, "build") && ScriptBytecodeAdapter.compareEqual(callSiteArray[243].callGetProperty(args), 1) && callSiteArray[244].call(args, 0) instanceof Closure) {
            return (SwingBuilder)ScriptBytecodeAdapter.castToType(callSiteArray[245].callStatic(SwingBuilder.class, callSiteArray[246].call(args, 0)), SwingBuilder.class);
        }
        throw (Throwable)callSiteArray[247].callConstructor(MissingMethodException.class, method, SwingBuilder.class, args, true);
    }

    public Object build(@DelegatesTo(value=SwingBuilder.class) Closure c) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        callSiteArray[248].call((Object)c, this);
        return callSiteArray[249].call(c);
    }

    public KeyStroke shortcut(Object key, Object modifier) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return (KeyStroke)ScriptBytecodeAdapter.castToType(callSiteArray[250].call(KeyStroke.class, key, callSiteArray[251].call(callSiteArray[252].call(callSiteArray[253].call(Toolkit.class)), modifier)), KeyStroke.class);
    }

    public KeyStroke shortcut(String key, Object modifier) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        KeyStroke ks = (KeyStroke)ScriptBytecodeAdapter.castToType(callSiteArray[254].call(KeyStroke.class, key), KeyStroke.class);
        if (ScriptBytecodeAdapter.compareEqual(ks, null)) {
            return (KeyStroke)ScriptBytecodeAdapter.castToType(null, KeyStroke.class);
        }
        return (KeyStroke)ScriptBytecodeAdapter.castToType(callSiteArray[255].call(KeyStroke.class, callSiteArray[256].call(ks), callSiteArray[257].call(callSiteArray[258].call(callSiteArray[259].call(ks), modifier), callSiteArray[260].call(callSiteArray[261].call(Toolkit.class)))), KeyStroke.class);
    }

    public static LookAndFeel lookAndFeel(Object laf, Closure initCode) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return (LookAndFeel)ScriptBytecodeAdapter.castToType(callSiteArray[262].callStatic(SwingBuilder.class, ScriptBytecodeAdapter.createMap(new Object[0]), laf, initCode), LookAndFeel.class);
    }

    public static LookAndFeel lookAndFeel(Map attributes, Object laf, Closure initCode) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return (LookAndFeel)ScriptBytecodeAdapter.castToType(callSiteArray[263].call(callSiteArray[264].callGetProperty(LookAndFeelHelper.class), laf, attributes, initCode), LookAndFeel.class);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static LookAndFeel lookAndFeel(Object ... lafs) {
        CallSite[] callSiteArray;
        block10: {
            block11: {
                block9: {
                    callSiteArray = SwingBuilder.$getCallSiteArray();
                    if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) break block9;
                    if (!ScriptBytecodeAdapter.compareEqual(callSiteArray[268].callGetProperty(lafs), 1)) break block10;
                    break block11;
                }
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[265].callGetProperty(lafs), 1)) {
                    callSiteArray[266].callStatic(SwingBuilder.class, ScriptBytecodeAdapter.createMap(new Object[0]), callSiteArray[267].call((Object)lafs, 0), ScriptBytecodeAdapter.createGroovyObjectWrapper((Closure)ScriptBytecodeAdapter.asType(null, Closure.class), Closure.class));
                }
                break block10;
            }
            callSiteArray[269].callStatic(SwingBuilder.class, ScriptBytecodeAdapter.createMap(new Object[0]), BytecodeInterface8.objectArrayGet(lafs, 0), ScriptBytecodeAdapter.createGroovyObjectWrapper((Closure)ScriptBytecodeAdapter.asType(null, Closure.class), Closure.class));
        }
        Object laf = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[270].call(lafs), Iterator.class);
        while (true) {
            if (!iterator.hasNext()) {
                callSiteArray[274].call((Object)LOG, new GStringImpl(new Object[]{lafs}, new String[]{"All Look and Feel options failed: ", ""}));
                return (LookAndFeel)ScriptBytecodeAdapter.castToType(null, LookAndFeel.class);
            }
            laf = iterator.next();
            try {
                if (!(laf instanceof ArrayList)) return (LookAndFeel)ScriptBytecodeAdapter.castToType(callSiteArray[272].callStatic((Class)SwingBuilder.class, (Object)laf), LookAndFeel.class);
                return (LookAndFeel)ScriptBytecodeAdapter.castToType(callSiteArray[271].callStatic(SwingBuilder.class, ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{laf}, new int[]{0})), LookAndFeel.class);
            }
            catch (Throwable t) {
                callSiteArray[273].call((Object)LOG, new GStringImpl(new Object[]{laf, t}, new String[]{"Could not instantiate Look and Feel ", " because of ", ". Attempting next option."}));
                continue;
            }
            break;
        }
        catch (Throwable throwable) {
            throw throwable;
        }
    }

    private static LookAndFeel _laf(List s) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return (LookAndFeel)ScriptBytecodeAdapter.castToType(callSiteArray[275].callStatic(SwingBuilder.class, ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{s}, new int[]{0})), LookAndFeel.class);
    }

    private static LookAndFeel _laf(String s, Map m) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return (LookAndFeel)ScriptBytecodeAdapter.castToType(callSiteArray[276].callStatic(SwingBuilder.class, m, s, ScriptBytecodeAdapter.createGroovyObjectWrapper((Closure)ScriptBytecodeAdapter.asType(null, Closure.class), Closure.class)), LookAndFeel.class);
    }

    private static LookAndFeel _laf(LookAndFeel laf, Map m) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return (LookAndFeel)ScriptBytecodeAdapter.castToType(callSiteArray[277].callStatic(SwingBuilder.class, m, laf, ScriptBytecodeAdapter.createGroovyObjectWrapper((Closure)ScriptBytecodeAdapter.asType(null, Closure.class), Closure.class)), LookAndFeel.class);
    }

    private static LookAndFeel _laf(String s) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return (LookAndFeel)ScriptBytecodeAdapter.castToType(callSiteArray[278].callStatic(SwingBuilder.class, ScriptBytecodeAdapter.createMap(new Object[0]), s, ScriptBytecodeAdapter.createGroovyObjectWrapper((Closure)ScriptBytecodeAdapter.asType(null, Closure.class), Closure.class)), LookAndFeel.class);
    }

    private static LookAndFeel _laf(LookAndFeel laf) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return (LookAndFeel)ScriptBytecodeAdapter.castToType(callSiteArray[279].callStatic(SwingBuilder.class, ScriptBytecodeAdapter.createMap(new Object[0]), laf, ScriptBytecodeAdapter.createGroovyObjectWrapper((Closure)ScriptBytecodeAdapter.asType(null, Closure.class), Closure.class)), LookAndFeel.class);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Object objectIDAttributeDelegate(Object builder, Object node, Object attributes) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        Object object = callSiteArray[280].call(builder, DELEGATE_PROPERTY_OBJECT_ID);
        Object idAttr = DefaultTypeTransformation.booleanUnbox(object) ? object : DEFAULT_DELEGATE_PROPERTY_OBJECT_ID;
        Object theID = callSiteArray[281].call(attributes, idAttr);
        if (!DefaultTypeTransformation.booleanUnbox(theID)) {
            return null;
        }
        callSiteArray[282].call(builder, theID, node);
        if (!DefaultTypeTransformation.booleanUnbox(node)) {
            return null;
        }
        try {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[283].callGetProperty(node))) return null;
            boolean bl = true;
            if (!bl) return null;
            Object object2 = theID;
            ScriptBytecodeAdapter.setProperty(object2, null, node, "name");
            return object2;
        }
        catch (MissingPropertyException mpe) {
            Object var11_11 = null;
            return var11_11;
        }
    }

    /*
     * WARNING - void declaration
     */
    public static Object clientPropertyAttributeDelegate(Object builder, Object node, Object attributes) {
        void var2_2;
        Reference<Object> node2 = new Reference<Object>(node);
        Reference<void> attributes2 = new Reference<void>(var2_2);
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        Object clientPropertyMap = callSiteArray[284].call((Object)attributes2.get(), "clientProperties");
        public class _clientPropertyAttributeDelegate_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference node;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _clientPropertyAttributeDelegate_closure2(Object _outerInstance, Object _thisObject, Reference node) {
                Reference reference;
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.node = reference = node;
            }

            public Object doCall(Object key, Object value) {
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure2.$getCallSiteArray();
                return callSiteArray[0].call(this.node.get(), key, value);
            }

            public Object call(Object key, Object value) {
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure2.$getCallSiteArray();
                return callSiteArray[1].callCurrent(this, key, value);
            }

            public Object getNode() {
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure2.$getCallSiteArray();
                return this.node.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _clientPropertyAttributeDelegate_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "putClientProperty";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _clientPropertyAttributeDelegate_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_clientPropertyAttributeDelegate_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _clientPropertyAttributeDelegate_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[285].call(clientPropertyMap, new _clientPropertyAttributeDelegate_closure2(SwingBuilder.class, SwingBuilder.class, node2));
        public class _clientPropertyAttributeDelegate_closure3
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _clientPropertyAttributeDelegate_closure3(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure3.$getCallSiteArray();
                return ScriptBytecodeAdapter.findRegex(callSiteArray[0].callGetProperty(it), "clientProperty(\\w)");
            }

            public Object doCall() {
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure3.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _clientPropertyAttributeDelegate_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "key";
                return new CallSiteArray(_clientPropertyAttributeDelegate_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _clientPropertyAttributeDelegate_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class _clientPropertyAttributeDelegate_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference attributes;
            private /* synthetic */ Reference node;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _clientPropertyAttributeDelegate_closure4(Object _outerInstance, Object _thisObject, Reference attributes, Reference node) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.attributes = reference2 = attributes;
                this.node = reference = node;
            }

            public Object doCall(Object key, Object value) {
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure4.$getCallSiteArray();
                callSiteArray[0].call(this.attributes.get(), key);
                return callSiteArray[1].call(this.node.get(), callSiteArray[2].call(key, "clientProperty"), value);
            }

            public Object call(Object key, Object value) {
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure4.$getCallSiteArray();
                return callSiteArray[3].callCurrent(this, key, value);
            }

            public Object getAttributes() {
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure4.$getCallSiteArray();
                return this.attributes.get();
            }

            public Object getNode() {
                CallSite[] callSiteArray = _clientPropertyAttributeDelegate_closure4.$getCallSiteArray();
                return this.node.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _clientPropertyAttributeDelegate_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "remove";
                stringArray[1] = "putClientProperty";
                stringArray[2] = "minus";
                stringArray[3] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _clientPropertyAttributeDelegate_closure4.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_clientPropertyAttributeDelegate_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _clientPropertyAttributeDelegate_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[286].call(callSiteArray[287].call((Object)attributes2.get(), new _clientPropertyAttributeDelegate_closure3(SwingBuilder.class, SwingBuilder.class)), new _clientPropertyAttributeDelegate_closure4(SwingBuilder.class, SwingBuilder.class, attributes2, node2));
    }

    public void createKeyStrokeAction(Map attributes, JComponent component) {
        Object object;
        Reference<JComponent> component2 = new Reference<JComponent>(component);
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        Object object2 = callSiteArray[288].callCurrent(this, attributes, component2.get());
        component2.set((JComponent)ScriptBytecodeAdapter.castToType(object2, JComponent.class));
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[289].call((Object)attributes, "keyStroke"))) {
            throw (Throwable)callSiteArray[290].callConstructor(RuntimeException.class, "You must define a value for keyStroke:");
        }
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[291].call((Object)attributes, "action"))) {
            throw (Throwable)callSiteArray[292].callConstructor(RuntimeException.class, "You must define a value for action:");
        }
        Object object3 = callSiteArray[293].call((Object)attributes, "condition");
        Reference<Object> condition = new Reference<Object>(DefaultTypeTransformation.booleanUnbox(object3) ? object3 : callSiteArray[294].callGetProperty(JComponent.class));
        if (condition.get() instanceof GString) {
            String string = (String)ScriptBytecodeAdapter.asType(condition.get(), String.class);
            condition.set(string);
        }
        if (condition.get() instanceof String) {
            Object object4 = callSiteArray[295].call(callSiteArray[296].call(condition.get()), " ", "_");
            condition.set(object4);
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[297].call(condition.get(), "WHEN_"))) {
                Object object5 = callSiteArray[298].call((Object)"WHEN_", condition.get());
                condition.set(object5);
            }
        }
        if (!(ScriptBytecodeAdapter.isCase(object = condition.get(), callSiteArray[299].callGetProperty(JComponent.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[300].callGetProperty(JComponent.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[301].callGetProperty(JComponent.class)))) {
            if (ScriptBytecodeAdapter.isCase(object, "WHEN_FOCUSED")) {
                Object object6 = callSiteArray[302].callGetProperty(JComponent.class);
                condition.set(object6);
            } else if (ScriptBytecodeAdapter.isCase(object, "WHEN_ANCESTOR_OF_FOCUSED_COMPONENT")) {
                Object object7 = callSiteArray[303].callGetProperty(JComponent.class);
                condition.set(object7);
            } else if (ScriptBytecodeAdapter.isCase(object, "WHEN_IN_FOCUSED_WINDOW")) {
                Object object8 = callSiteArray[304].callGetProperty(JComponent.class);
                condition.set(object8);
            } else {
                Object object9 = callSiteArray[305].callGetProperty(JComponent.class);
                condition.set(object9);
            }
        }
        Reference<Object> actionKey = new Reference<Object>(callSiteArray[306].call((Object)attributes, "actionKey"));
        if (!DefaultTypeTransformation.booleanUnbox(actionKey.get())) {
            Object object10 = callSiteArray[307].call((Object)"Action", callSiteArray[308].call(Math.class, callSiteArray[309].call(random)));
            actionKey.set(object10);
        }
        Object keyStroke = callSiteArray[310].call((Object)attributes, "keyStroke");
        Object action = callSiteArray[311].call((Object)attributes, "action");
        if (keyStroke instanceof GString) {
            String string = (String)ScriptBytecodeAdapter.asType(keyStroke, String.class);
            keyStroke = string;
        }
        if (keyStroke instanceof String || keyStroke instanceof Number) {
            List list = ScriptBytecodeAdapter.createList(new Object[]{keyStroke});
            keyStroke = list;
        }
        public class _createKeyStrokeAction_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference component;
            private /* synthetic */ Reference condition;
            private /* synthetic */ Reference actionKey;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _createKeyStrokeAction_closure5(Object _outerInstance, Object _thisObject, Reference component, Reference condition, Reference actionKey) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                CallSite[] callSiteArray = _createKeyStrokeAction_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.component = reference3 = component;
                this.condition = reference2 = condition;
                this.actionKey = reference = actionKey;
            }

            public Object doCall(Object ks) {
                CallSite[] callSiteArray = _createKeyStrokeAction_closure5.$getCallSiteArray();
                Object object = ks;
                if (ScriptBytecodeAdapter.isCase(object, KeyStroke.class)) {
                    return callSiteArray[0].call(callSiteArray[1].call(this.component.get(), this.condition.get()), ks, this.actionKey.get());
                }
                if (ScriptBytecodeAdapter.isCase(object, String.class)) {
                    return callSiteArray[2].call(callSiteArray[3].call(this.component.get(), this.condition.get()), callSiteArray[4].call(KeyStroke.class, ks), this.actionKey.get());
                }
                if (ScriptBytecodeAdapter.isCase(object, Number.class)) {
                    return callSiteArray[5].call(callSiteArray[6].call(this.component.get(), this.condition.get()), callSiteArray[7].call(KeyStroke.class, callSiteArray[8].call(ks)), this.actionKey.get());
                }
                throw (Throwable)callSiteArray[9].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{ks}, new String[]{"Cannot apply ", " as a KeyStroke value."}));
            }

            public JComponent getComponent() {
                CallSite[] callSiteArray = _createKeyStrokeAction_closure5.$getCallSiteArray();
                return (JComponent)ScriptBytecodeAdapter.castToType(this.component.get(), JComponent.class);
            }

            public Object getCondition() {
                CallSite[] callSiteArray = _createKeyStrokeAction_closure5.$getCallSiteArray();
                return this.condition.get();
            }

            public Object getActionKey() {
                CallSite[] callSiteArray = _createKeyStrokeAction_closure5.$getCallSiteArray();
                return this.actionKey.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _createKeyStrokeAction_closure5.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "put";
                stringArray[1] = "getInputMap";
                stringArray[2] = "put";
                stringArray[3] = "getInputMap";
                stringArray[4] = "getKeyStroke";
                stringArray[5] = "put";
                stringArray[6] = "getInputMap";
                stringArray[7] = "getKeyStroke";
                stringArray[8] = "intValue";
                stringArray[9] = "<$constructor$>";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[10];
                _createKeyStrokeAction_closure5.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_createKeyStrokeAction_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _createKeyStrokeAction_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[312].call(keyStroke, new _createKeyStrokeAction_closure5(this, this, component2, condition, actionKey));
        callSiteArray[313].call(callSiteArray[314].callGetProperty(component2.get()), actionKey.get(), action);
    }

    private Object findTargetComponent(Map attributes, JComponent component) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(component)) {
            return component;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[315].call((Object)attributes, "component"))) {
            Object c = callSiteArray[316].call((Object)attributes, "component");
            if (!(c instanceof JComponent)) {
                throw (Throwable)callSiteArray[317].callConstructor(RuntimeException.class, "The property component: is not of type JComponent.");
            }
            return c;
        }
        Object c = callSiteArray[318].callCurrent(this);
        if (c instanceof JComponent) {
            return c;
        }
        throw (Throwable)callSiteArray[319].callConstructor(RuntimeException.class, "You must define one of the following: a value of type JComponent, a component: attribute or nest this node inside another one that produces a JComponent.");
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != SwingBuilder.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public KeyStroke shortcut(Object key) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return this.shortcut(key, (Object)0);
    }

    public KeyStroke shortcut(String key) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return this.shortcut(key, (Object)0);
    }

    public static LookAndFeel lookAndFeel(Map attributes, Object laf) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return SwingBuilder.lookAndFeel(attributes, laf, null);
    }

    public static LookAndFeel lookAndFeel(Map attributes) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return SwingBuilder.lookAndFeel(attributes, null, null);
    }

    public static LookAndFeel lookAndFeel() {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        return SwingBuilder.lookAndFeel(ScriptBytecodeAdapter.createMap(new Object[0]), null, null);
    }

    public void createKeyStrokeAction(Map attributes) {
        CallSite[] callSiteArray = SwingBuilder.$getCallSiteArray();
        this.createKeyStrokeAction(attributes, null);
    }

    static {
        boolean bl;
        headless = bl = false;
        Object object = SwingBuilder.$getCallSiteArray()[320].call(Logger.class, SwingBuilder.$getCallSiteArray()[321].callGetProperty(SwingBuilder.class));
        LOG = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
        Object object2 = SwingBuilder.$getCallSiteArray()[322].callConstructor(Random.class);
        random = (Random)ScriptBytecodeAdapter.castToType(object2, Random.class);
    }

    public static String getDELEGATE_PROPERTY_OBJECT_ID() {
        return DELEGATE_PROPERTY_OBJECT_ID;
    }

    public static String getDEFAULT_DELEGATE_PROPERTY_OBJECT_ID() {
        return DEFAULT_DELEGATE_PROPERTY_OBJECT_ID;
    }

    public /* synthetic */ void super$4$registerBeanFactory(String string, Class clazz) {
        super.registerBeanFactory(string, clazz);
    }

    public /* synthetic */ void super$4$registerBeanFactory(String string, String string2, Class clazz) {
        super.registerBeanFactory(string, string2, clazz);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "isHeadless";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "putAt";
        stringArray[3] = "registerFactory";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "registerFactory";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "registerFactory";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "registerFactory";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "registerFactory";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "addAttributeDelegate";
        stringArray[14] = "addAttributeDelegate";
        stringArray[15] = "addAttributeDelegate";
        stringArray[16] = "registerFactory";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "registerExplicitMethod";
        stringArray[19] = "registerExplicitMethod";
        stringArray[20] = "<$constructor$>";
        stringArray[21] = "registerFactory";
        stringArray[22] = "addAttributeDelegate";
        stringArray[23] = "registerFactory";
        stringArray[24] = "<$constructor$>";
        stringArray[25] = "registerFactory";
        stringArray[26] = "<$constructor$>";
        stringArray[27] = "registerFactory";
        stringArray[28] = "<$constructor$>";
        stringArray[29] = "registerFactory";
        stringArray[30] = "<$constructor$>";
        stringArray[31] = "registerFactory";
        stringArray[32] = "<$constructor$>";
        stringArray[33] = "registerFactory";
        stringArray[34] = "<$constructor$>";
        stringArray[35] = "registerBeanFactory";
        stringArray[36] = "registerFactory";
        stringArray[37] = "<$constructor$>";
        stringArray[38] = "registerBeanFactory";
        stringArray[39] = "registerFactory";
        stringArray[40] = "<$constructor$>";
        stringArray[41] = "registerFactory";
        stringArray[42] = "<$constructor$>";
        stringArray[43] = "registerFactory";
        stringArray[44] = "<$constructor$>";
        stringArray[45] = "registerFactory";
        stringArray[46] = "<$constructor$>";
        stringArray[47] = "registerFactory";
        stringArray[48] = "<$constructor$>";
        stringArray[49] = "registerFactory";
        stringArray[50] = "<$constructor$>";
        stringArray[51] = "registerFactory";
        stringArray[52] = "<$constructor$>";
        stringArray[53] = "registerFactory";
        stringArray[54] = "<$constructor$>";
        stringArray[55] = "registerFactory";
        stringArray[56] = "<$constructor$>";
        stringArray[57] = "registerFactory";
        stringArray[58] = "<$constructor$>";
        stringArray[59] = "registerFactory";
        stringArray[60] = "<$constructor$>";
        stringArray[61] = "registerFactory";
        stringArray[62] = "<$constructor$>";
        stringArray[63] = "registerFactory";
        stringArray[64] = "<$constructor$>";
        stringArray[65] = "registerFactory";
        stringArray[66] = "<$constructor$>";
        stringArray[67] = "registerFactory";
        stringArray[68] = "<$constructor$>";
        stringArray[69] = "registerBeanFactory";
        stringArray[70] = "registerFactory";
        stringArray[71] = "<$constructor$>";
        stringArray[72] = "registerBeanFactory";
        stringArray[73] = "registerFactory";
        stringArray[74] = "<$constructor$>";
        stringArray[75] = "registerFactory";
        stringArray[76] = "<$constructor$>";
        stringArray[77] = "registerBeanFactory";
        stringArray[78] = "registerFactory";
        stringArray[79] = "<$constructor$>";
        stringArray[80] = "registerBeanFactory";
        stringArray[81] = "registerBeanFactory";
        stringArray[82] = "registerBeanFactory";
        stringArray[83] = "registerBeanFactory";
        stringArray[84] = "registerBeanFactory";
        stringArray[85] = "registerBeanFactory";
        stringArray[86] = "registerBeanFactory";
        stringArray[87] = "registerBeanFactory";
        stringArray[88] = "registerFactory";
        stringArray[89] = "<$constructor$>";
        stringArray[90] = "registerFactory";
        stringArray[91] = "<$constructor$>";
        stringArray[92] = "registerFactory";
        stringArray[93] = "<$constructor$>";
        stringArray[94] = "registerBeanFactory";
        stringArray[95] = "registerBeanFactory";
        stringArray[96] = "registerBeanFactory";
        stringArray[97] = "registerBeanFactory";
        stringArray[98] = "registerBeanFactory";
        stringArray[99] = "registerBeanFactory";
        stringArray[100] = "registerBeanFactory";
        stringArray[101] = "registerFactory";
        stringArray[102] = "<$constructor$>";
        stringArray[103] = "registerBeanFactory";
        stringArray[104] = "registerFactory";
        stringArray[105] = "<$constructor$>";
        stringArray[106] = "registerFactory";
        stringArray[107] = "<$constructor$>";
        stringArray[108] = "registerFactory";
        stringArray[109] = "<$constructor$>";
        stringArray[110] = "registerFactory";
        stringArray[111] = "<$constructor$>";
        stringArray[112] = "registerFactory";
        stringArray[113] = "<$constructor$>";
        stringArray[114] = "registerFactory";
        stringArray[115] = "<$constructor$>";
        stringArray[116] = "registerFactory";
        stringArray[117] = "<$constructor$>";
        stringArray[118] = "registerFactory";
        stringArray[119] = "<$constructor$>";
        stringArray[120] = "registerFactory";
        stringArray[121] = "<$constructor$>";
        stringArray[122] = "registerFactory";
        stringArray[123] = "<$constructor$>";
        stringArray[124] = "registerFactory";
        stringArray[125] = "<$constructor$>";
        stringArray[126] = "registerFactory";
        stringArray[127] = "<$constructor$>";
        stringArray[128] = "registerBeanFactory";
        stringArray[129] = "registerBeanFactory";
        stringArray[130] = "addAttributeDelegate";
        stringArray[131] = "addAttributeDelegate";
        stringArray[132] = "registerFactory";
        stringArray[133] = "<$constructor$>";
        stringArray[134] = "registerFactory";
        stringArray[135] = "<$constructor$>";
        stringArray[136] = "registerFactory";
        stringArray[137] = "<$constructor$>";
        stringArray[138] = "registerFactory";
        stringArray[139] = "<$constructor$>";
        stringArray[140] = "registerFactory";
        stringArray[141] = "<$constructor$>";
        stringArray[142] = "registerFactory";
        stringArray[143] = "<$constructor$>";
        stringArray[144] = "registerFactory";
        stringArray[145] = "<$constructor$>";
        stringArray[146] = "registerFactory";
        stringArray[147] = "<$constructor$>";
        stringArray[148] = "registerFactory";
        stringArray[149] = "<$constructor$>";
        stringArray[150] = "registerFactory";
        stringArray[151] = "<$constructor$>";
        stringArray[152] = "registerFactory";
        stringArray[153] = "<$constructor$>";
        stringArray[154] = "registerFactory";
        stringArray[155] = "<$constructor$>";
        stringArray[156] = "registerFactory";
        stringArray[157] = "<$constructor$>";
        stringArray[158] = "registerFactory";
        stringArray[159] = "<$constructor$>";
        stringArray[160] = "registerFactory";
        stringArray[161] = "<$constructor$>";
        stringArray[162] = "LOWERED";
        stringArray[163] = "registerFactory";
        stringArray[164] = "<$constructor$>";
        stringArray[165] = "RAISED";
        stringArray[166] = "registerFactory";
        stringArray[167] = "<$constructor$>";
        stringArray[168] = "LOWERED";
        stringArray[169] = "registerFactory";
        stringArray[170] = "<$constructor$>";
        stringArray[171] = "LOWERED";
        stringArray[172] = "registerFactory";
        stringArray[173] = "<$constructor$>";
        stringArray[174] = "RAISED";
        stringArray[175] = "registerFactory";
        stringArray[176] = "<$constructor$>";
        stringArray[177] = "registerFactory";
        stringArray[178] = "<$constructor$>";
        stringArray[179] = "registerFactory";
        stringArray[180] = "<$constructor$>";
        stringArray[181] = "registerFactory";
        stringArray[182] = "<$constructor$>";
        stringArray[183] = "<$constructor$>";
        stringArray[184] = "registerFactory";
        stringArray[185] = "registerFactory";
        stringArray[186] = "registerFactory";
        stringArray[187] = "<$constructor$>";
        stringArray[188] = "registerFactory";
        stringArray[189] = "registerFactory";
        stringArray[190] = "registerFactory";
        stringArray[191] = "<$constructor$>";
        stringArray[192] = "registerFactory";
        stringArray[193] = "<$constructor$>";
        stringArray[194] = "registerFactory";
        stringArray[195] = "<$constructor$>";
        stringArray[196] = "registerExplicitMethod";
        stringArray[197] = "registerExplicitMethod";
        stringArray[198] = "registerExplicitMethod";
        stringArray[199] = "isAssignableFrom";
        stringArray[200] = "registerFactory";
        stringArray[201] = "<$constructor$>";
        stringArray[202] = "isAssignableFrom";
        stringArray[203] = "registerFactory";
        stringArray[204] = "<$constructor$>";
        stringArray[205] = "isAssignableFrom";
        stringArray[206] = "registerFactory";
        stringArray[207] = "<$constructor$>";
        stringArray[208] = "isAssignableFrom";
        stringArray[209] = "isAssignableFrom";
        stringArray[210] = "isAssignableFrom";
        stringArray[211] = "isAssignableFrom";
        stringArray[212] = "isAssignableFrom";
        stringArray[213] = "registerFactory";
        stringArray[214] = "<$constructor$>";
        stringArray[215] = "setDelegate";
        stringArray[216] = "isEventDispatchThread";
        stringArray[217] = "call";
        stringArray[218] = "getContinuationData";
        stringArray[219] = "curry";
        stringArray[220] = "invokeAndWait";
        stringArray[221] = "<$constructor$>";
        stringArray[222] = "<$constructor$>";
        stringArray[223] = "getTargetException";
        stringArray[224] = "restoreFromContinuationData";
        stringArray[225] = "restoreFromContinuationData";
        stringArray[226] = "setDelegate";
        stringArray[227] = "call";
        stringArray[228] = "curry";
        stringArray[229] = "invokeLater";
        stringArray[230] = "setDelegate";
        stringArray[231] = "curry";
        stringArray[232] = "isEventDispatchThread";
        stringArray[233] = "start";
        stringArray[234] = "<$constructor$>";
        stringArray[235] = "call";
        stringArray[236] = "<$constructor$>";
        stringArray[237] = "edt";
        stringArray[238] = "length";
        stringArray[239] = "getAt";
        stringArray[240] = "edtBuilder";
        stringArray[241] = "getAt";
        stringArray[242] = "<$constructor$>";
        stringArray[243] = "length";
        stringArray[244] = "getAt";
        stringArray[245] = "edtBuilder";
        stringArray[246] = "getAt";
        stringArray[247] = "<$constructor$>";
        stringArray[248] = "setDelegate";
        stringArray[249] = "call";
        stringArray[250] = "getKeyStroke";
        stringArray[251] = "or";
        stringArray[252] = "getMenuShortcutKeyMask";
        stringArray[253] = "getDefaultToolkit";
        stringArray[254] = "getKeyStroke";
        stringArray[255] = "getKeyStroke";
        stringArray[256] = "getKeyCode";
        stringArray[257] = "or";
        stringArray[258] = "or";
        stringArray[259] = "getModifiers";
        stringArray[260] = "getMenuShortcutKeyMask";
        stringArray[261] = "getDefaultToolkit";
        stringArray[262] = "lookAndFeel";
        stringArray[263] = "lookAndFeel";
        stringArray[264] = "instance";
        stringArray[265] = "length";
        stringArray[266] = "lookAndFeel";
        stringArray[267] = "getAt";
        stringArray[268] = "length";
        stringArray[269] = "lookAndFeel";
        stringArray[270] = "iterator";
        stringArray[271] = "_laf";
        stringArray[272] = "_laf";
        stringArray[273] = "fine";
        stringArray[274] = "warning";
        stringArray[275] = "_laf";
        stringArray[276] = "lookAndFeel";
        stringArray[277] = "lookAndFeel";
        stringArray[278] = "lookAndFeel";
        stringArray[279] = "lookAndFeel";
        stringArray[280] = "getAt";
        stringArray[281] = "remove";
        stringArray[282] = "setVariable";
        stringArray[283] = "name";
        stringArray[284] = "remove";
        stringArray[285] = "each";
        stringArray[286] = "each";
        stringArray[287] = "findAll";
        stringArray[288] = "findTargetComponent";
        stringArray[289] = "containsKey";
        stringArray[290] = "<$constructor$>";
        stringArray[291] = "containsKey";
        stringArray[292] = "<$constructor$>";
        stringArray[293] = "remove";
        stringArray[294] = "WHEN_FOCUSED";
        stringArray[295] = "replaceAll";
        stringArray[296] = "toUpperCase";
        stringArray[297] = "startsWith";
        stringArray[298] = "plus";
        stringArray[299] = "WHEN_FOCUSED";
        stringArray[300] = "WHEN_ANCESTOR_OF_FOCUSED_COMPONENT";
        stringArray[301] = "WHEN_IN_FOCUSED_WINDOW";
        stringArray[302] = "WHEN_FOCUSED";
        stringArray[303] = "WHEN_ANCESTOR_OF_FOCUSED_COMPONENT";
        stringArray[304] = "WHEN_IN_FOCUSED_WINDOW";
        stringArray[305] = "WHEN_FOCUSED";
        stringArray[306] = "remove";
        stringArray[307] = "plus";
        stringArray[308] = "abs";
        stringArray[309] = "nextLong";
        stringArray[310] = "remove";
        stringArray[311] = "remove";
        stringArray[312] = "each";
        stringArray[313] = "put";
        stringArray[314] = "actionMap";
        stringArray[315] = "containsKey";
        stringArray[316] = "remove";
        stringArray[317] = "<$constructor$>";
        stringArray[318] = "getCurrent";
        stringArray[319] = "<$constructor$>";
        stringArray[320] = "getLogger";
        stringArray[321] = "name";
        stringArray[322] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[323];
        SwingBuilder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(SwingBuilder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = SwingBuilder.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

