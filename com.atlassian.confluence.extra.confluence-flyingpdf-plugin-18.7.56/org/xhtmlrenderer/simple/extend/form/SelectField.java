/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.FormField;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;
import org.xhtmlrenderer.util.GeneralUtil;

class SelectField
extends FormField {
    public SelectField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    @Override
    public JComponent create() {
        List optionList = this.createList();
        if (this.shouldRenderAsList()) {
            JList<Object> select = new JList<Object>(optionList.toArray());
            this.applyComponentStyle(select);
            select.setCellRenderer(new CellRenderer());
            select.addListSelectionListener(new HeadingItemListener());
            if (this.hasAttribute("multiple") && this.getAttribute("multiple").equalsIgnoreCase("true")) {
                select.setSelectionMode(2);
            } else {
                select.setSelectionMode(0);
            }
            int size = 0;
            if (this.hasAttribute("size")) {
                size = GeneralUtil.parseIntRelaxed(this.getAttribute("size"));
            }
            if (size == 0) {
                select.setVisibleRowCount(Math.min(select.getModel().getSize(), 20));
            } else {
                select.setVisibleRowCount(size);
            }
            return new JScrollPane(select);
        }
        JComboBox<Object> select = new JComboBox<Object>(optionList.toArray());
        this.applyComponentStyle(select);
        select.setEditable(false);
        select.setRenderer(new CellRenderer());
        select.addItemListener(new HeadingItemListener());
        return select;
    }

    @Override
    protected FormFieldState loadOriginalState() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        NodeList options = this.getElement().getElementsByTagName("option");
        for (int i = 0; i < options.getLength(); ++i) {
            Element option = (Element)options.item(i);
            if (!option.hasAttribute("selected") || !option.getAttribute("selected").equalsIgnoreCase("selected")) continue;
            list.add(new Integer(i));
        }
        return FormFieldState.fromList(list);
    }

    @Override
    protected void applyOriginalState() {
        if (this.shouldRenderAsList()) {
            JList select = (JList)((JScrollPane)this.getComponent()).getViewport().getView();
            select.setSelectedIndices(this.getOriginalState().getSelectedIndices());
        } else {
            JComboBox select = (JComboBox)this.getComponent();
            int[] indices = this.getOriginalState().getSelectedIndices();
            if (indices.length == 0) {
                select.setSelectedIndex(0);
            } else {
                select.setSelectedIndex(indices[indices.length - 1]);
            }
        }
    }

    @Override
    protected String[] getFieldValues() {
        if (this.shouldRenderAsList()) {
            JList select = (JList)((JScrollPane)this.getComponent()).getViewport().getView();
            Object[] selectedValues = select.getSelectedValues();
            String[] submitValues = new String[selectedValues.length];
            for (int i = 0; i < selectedValues.length; ++i) {
                NameValuePair pair = (NameValuePair)selectedValues[i];
                if (pair.getValue() == null) continue;
                submitValues[i] = pair.getValue();
            }
            return submitValues;
        }
        JComboBox select = (JComboBox)this.getComponent();
        NameValuePair selectedValue = (NameValuePair)select.getSelectedItem();
        if (selectedValue != null && selectedValue.getValue() != null) {
            return new String[]{selectedValue.getValue()};
        }
        return new String[0];
    }

    private List createList() {
        ArrayList list = new ArrayList();
        this.addChildren(list, this.getElement(), 0);
        return list;
    }

    private void addChildren(List list, Element e, int indent) {
        NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            if (!(children.item(i) instanceof Element)) continue;
            Element child = (Element)children.item(i);
            if ("option".equals(child.getNodeName())) {
                String optionText;
                String optionValue = optionText = XhtmlForm.collectText(child);
                if (child.hasAttribute("value")) {
                    optionValue = child.getAttribute("value");
                }
                list.add(new NameValuePair(optionText, optionValue, indent));
                continue;
            }
            if (!"optgroup".equals(child.getNodeName())) continue;
            String titleText = child.getAttribute("label");
            list.add(new NameValuePair(titleText, null, indent));
            this.addChildren(list, child, indent + 1);
        }
    }

    private boolean shouldRenderAsList() {
        int size;
        boolean result = false;
        if (this.hasAttribute("multiple") && this.getAttribute("multiple").equalsIgnoreCase("true")) {
            result = true;
        } else if (this.hasAttribute("size") && (size = GeneralUtil.parseIntRelaxed(this.getAttribute("size"))) > 0) {
            result = true;
        }
        return result;
    }

    private static class HeadingItemListener
    implements ItemListener,
    ListSelectionListener {
        private Object oldSelection = null;
        private int[] oldSelections = new int[0];

        private HeadingItemListener() {
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() != 1) {
                return;
            }
            if (!(e.getSource() instanceof JComboBox)) {
                return;
            }
            JComboBox combo = (JComboBox)e.getSource();
            if (((NameValuePair)e.getItem()).getValue() == null) {
                combo.setSelectedItem(this.oldSelection);
            } else {
                this.oldSelection = e.getItem();
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!(e.getSource() instanceof JList)) {
                return;
            }
            JList list = (JList)e.getSource();
            ListModel model = list.getModel();
            for (int i = e.getFirstIndex(); i <= e.getLastIndex(); ++i) {
                NameValuePair pair;
                if (!list.isSelectedIndex(i) || (pair = (NameValuePair)model.getElementAt(i)) == null || pair.getValue() != null) continue;
                if (list.getSelectedIndices().length == 1) {
                    list.setSelectedIndices(this.oldSelections);
                } else {
                    list.removeSelectionInterval(i, i);
                }
                return;
            }
            if (!e.getValueIsAdjusting()) {
                this.oldSelections = list.getSelectedIndices();
            }
        }
    }

    private static class CellRenderer
    extends DefaultListCellRenderer {
        private CellRenderer() {
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            NameValuePair pair = (NameValuePair)value;
            if (pair != null && pair.getValue() == null) {
                super.getListCellRendererComponent((JList<?>)list, value, index, false, false);
                Font fold = this.getFont();
                Font fnew = new Font(fold.getName(), 3, fold.getSize());
                this.setFont(fnew);
            } else {
                super.getListCellRendererComponent((JList<?>)list, value, index, isSelected, cellHasFocus);
            }
            return this;
        }
    }

    private static class NameValuePair {
        private String _name;
        private String _value;
        private int _indent;

        public NameValuePair(String name, String value, int indent) {
            this._name = name;
            this._value = value;
            this._indent = indent;
        }

        public String getName() {
            return this._name;
        }

        public String getValue() {
            return this._value;
        }

        public int getIndent() {
            return this._indent;
        }

        public String toString() {
            String txt = this.getName();
            for (int i = 0; i < this.getIndent(); ++i) {
                txt = "    " + txt;
            }
            return txt;
        }
    }
}

