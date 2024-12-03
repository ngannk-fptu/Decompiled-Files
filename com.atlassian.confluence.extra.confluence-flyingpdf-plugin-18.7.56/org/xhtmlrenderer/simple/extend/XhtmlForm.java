/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.DefaultFormSubmissionListener;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.simple.extend.form.FormField;
import org.xhtmlrenderer.simple.extend.form.FormFieldFactory;
import org.xhtmlrenderer.util.XRLog;

public class XhtmlForm {
    private static final String FS_DEFAULT_GROUP = "__fs_default_group_";
    private static int _defaultGroupCount = 1;
    private UserAgentCallback _userAgentCallback;
    private Map _componentCache;
    private Map _buttonGroups;
    private Element _parentFormElement;
    private FormSubmissionListener _formSubmissionListener;

    public XhtmlForm(UserAgentCallback uac, Element e, FormSubmissionListener fsListener) {
        this._userAgentCallback = uac;
        this._buttonGroups = new HashMap();
        this._componentCache = new LinkedHashMap();
        this._parentFormElement = e;
        this._formSubmissionListener = fsListener;
    }

    public XhtmlForm(UserAgentCallback uac, Element e) {
        this(uac, e, new DefaultFormSubmissionListener());
    }

    public UserAgentCallback getUserAgentCallback() {
        return this._userAgentCallback;
    }

    public void addButtonToGroup(String groupName, AbstractButton button) {
        ButtonGroupWrapper group;
        if (groupName == null) {
            groupName = XhtmlForm.createNewDefaultGroupName();
        }
        if ((group = (ButtonGroupWrapper)this._buttonGroups.get(groupName)) == null) {
            group = new ButtonGroupWrapper();
            this._buttonGroups.put(groupName, group);
        }
        group.add(button);
    }

    private static String createNewDefaultGroupName() {
        return FS_DEFAULT_GROUP + ++_defaultGroupCount;
    }

    private static boolean isFormField(Element e) {
        String nodeName = e.getNodeName();
        return nodeName.equals("input") || nodeName.equals("select") || nodeName.equals("textarea");
    }

    public FormField addComponent(Element e, LayoutContext context, BlockBox box) {
        FormField field = null;
        if (this._componentCache.containsKey(e)) {
            field = (FormField)this._componentCache.get(e);
        } else {
            if (!XhtmlForm.isFormField(e)) {
                return null;
            }
            field = FormFieldFactory.create(this, context, box);
            if (field == null) {
                XRLog.layout("Unknown field type: " + e.getNodeName());
                return null;
            }
            this._componentCache.put(e, field);
        }
        return field;
    }

    public void reset() {
        Iterator buttonGroups = this._buttonGroups.values().iterator();
        while (buttonGroups.hasNext()) {
            ((ButtonGroupWrapper)buttonGroups.next()).clearSelection();
        }
        Iterator fields = this._componentCache.values().iterator();
        while (fields.hasNext()) {
            ((FormField)fields.next()).reset();
        }
    }

    public void submit(JComponent source) {
        if (this._parentFormElement == null) {
            return;
        }
        StringBuffer data = new StringBuffer();
        String action = this._parentFormElement.getAttribute("action");
        data.append(action).append("?");
        Iterator fields = this._componentCache.entrySet().iterator();
        boolean first = true;
        while (fields.hasNext()) {
            Map.Entry entry = fields.next();
            FormField field = (FormField)entry.getValue();
            if (!field.includeInSubmission(source)) continue;
            String[] dataStrings = field.getFormDataStrings();
            for (int i = 0; i < dataStrings.length; ++i) {
                if (!first) {
                    data.append('&');
                }
                data.append(dataStrings[i]);
                first = false;
            }
        }
        if (this._formSubmissionListener != null) {
            this._formSubmissionListener.submit(data.toString());
        }
    }

    public static String collectText(Element e) {
        StringBuffer result = new StringBuffer();
        Node node = e.getFirstChild();
        if (node != null) {
            do {
                short nodeType;
                if ((nodeType = node.getNodeType()) != 3 && nodeType != 4) continue;
                Text text = (Text)node;
                result.append(text.getData());
            } while ((node = node.getNextSibling()) != null);
        }
        return result.toString().trim();
    }

    private static class ButtonGroupWrapper {
        private ButtonGroup _group = new ButtonGroup();
        private AbstractButton _dummy = new JRadioButton();

        public ButtonGroupWrapper() {
            this._group.add(this._dummy);
        }

        public void add(AbstractButton b) {
            this._group.add(b);
        }

        public void clearSelection() {
            this._group.setSelected(this._dummy.getModel(), true);
        }
    }
}

