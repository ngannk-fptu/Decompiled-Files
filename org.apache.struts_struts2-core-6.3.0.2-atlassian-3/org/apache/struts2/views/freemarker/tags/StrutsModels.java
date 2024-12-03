/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.views.freemarker.tags;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.views.freemarker.tags.ActionErrorModel;
import org.apache.struts2.views.freemarker.tags.ActionMessageModel;
import org.apache.struts2.views.freemarker.tags.ActionModel;
import org.apache.struts2.views.freemarker.tags.AnchorModel;
import org.apache.struts2.views.freemarker.tags.BeanModel;
import org.apache.struts2.views.freemarker.tags.CheckboxListModel;
import org.apache.struts2.views.freemarker.tags.CheckboxModel;
import org.apache.struts2.views.freemarker.tags.ComboBoxModel;
import org.apache.struts2.views.freemarker.tags.ComponentModel;
import org.apache.struts2.views.freemarker.tags.DateModel;
import org.apache.struts2.views.freemarker.tags.DoubleSelectModel;
import org.apache.struts2.views.freemarker.tags.ElseIfModel;
import org.apache.struts2.views.freemarker.tags.ElseModel;
import org.apache.struts2.views.freemarker.tags.FieldErrorModel;
import org.apache.struts2.views.freemarker.tags.FileModel;
import org.apache.struts2.views.freemarker.tags.FormModel;
import org.apache.struts2.views.freemarker.tags.HeadModel;
import org.apache.struts2.views.freemarker.tags.HiddenModel;
import org.apache.struts2.views.freemarker.tags.I18nModel;
import org.apache.struts2.views.freemarker.tags.IfModel;
import org.apache.struts2.views.freemarker.tags.IncludeModel;
import org.apache.struts2.views.freemarker.tags.InputTransferSelectModel;
import org.apache.struts2.views.freemarker.tags.IteratorModel;
import org.apache.struts2.views.freemarker.tags.LabelModel;
import org.apache.struts2.views.freemarker.tags.LinkModel;
import org.apache.struts2.views.freemarker.tags.OptGroupModel;
import org.apache.struts2.views.freemarker.tags.OptionTransferSelectModel;
import org.apache.struts2.views.freemarker.tags.ParamModel;
import org.apache.struts2.views.freemarker.tags.PasswordModel;
import org.apache.struts2.views.freemarker.tags.PropertyModel;
import org.apache.struts2.views.freemarker.tags.PushModel;
import org.apache.struts2.views.freemarker.tags.RadioModel;
import org.apache.struts2.views.freemarker.tags.ResetModel;
import org.apache.struts2.views.freemarker.tags.ScriptModel;
import org.apache.struts2.views.freemarker.tags.SelectModel;
import org.apache.struts2.views.freemarker.tags.SetModel;
import org.apache.struts2.views.freemarker.tags.SubmitModel;
import org.apache.struts2.views.freemarker.tags.TextAreaModel;
import org.apache.struts2.views.freemarker.tags.TextFieldModel;
import org.apache.struts2.views.freemarker.tags.TextModel;
import org.apache.struts2.views.freemarker.tags.TokenModel;
import org.apache.struts2.views.freemarker.tags.URLModel;
import org.apache.struts2.views.freemarker.tags.UpDownSelectModel;

public class StrutsModels {
    protected ValueStack stack;
    protected HttpServletRequest req;
    protected HttpServletResponse res;
    protected ActionModel action;
    protected BeanModel bean;
    protected CheckboxModel checkbox;
    protected CheckboxListModel checkboxlist;
    protected ComboBoxModel comboBox;
    protected ComponentModel component;
    protected DateModel date;
    protected DoubleSelectModel doubleselect;
    protected FileModel file;
    protected FormModel form;
    protected HeadModel head;
    protected HiddenModel hidden;
    protected AnchorModel a;
    protected I18nModel i18n;
    protected IncludeModel include;
    protected LabelModel label;
    protected PasswordModel password;
    protected PushModel push;
    protected ParamModel param;
    protected RadioModel radio;
    protected SelectModel select;
    protected SetModel set;
    protected SubmitModel submit;
    protected ResetModel reset;
    protected TextAreaModel textarea;
    protected TextModel text;
    protected TextFieldModel textfield;
    protected TokenModel token;
    protected URLModel url;
    protected PropertyModel property;
    protected IteratorModel iterator;
    protected ActionErrorModel actionerror;
    protected ActionMessageModel actionmessage;
    protected FieldErrorModel fielderror;
    protected OptionTransferSelectModel optiontransferselect;
    protected UpDownSelectModel updownselect;
    protected OptGroupModel optGroupModel;
    protected IfModel ifModel;
    protected ElseModel elseModel;
    protected ElseIfModel elseIfModel;
    protected InputTransferSelectModel inputtransferselect;
    protected ScriptModel script;
    protected LinkModel link;

    public StrutsModels(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        this.stack = stack;
        this.req = req;
        this.res = res;
    }

    public CheckboxListModel getCheckboxlist() {
        if (this.checkboxlist == null) {
            this.checkboxlist = new CheckboxListModel(this.stack, this.req, this.res);
        }
        return this.checkboxlist;
    }

    public CheckboxModel getCheckbox() {
        if (this.checkbox == null) {
            this.checkbox = new CheckboxModel(this.stack, this.req, this.res);
        }
        return this.checkbox;
    }

    public ComboBoxModel getCombobox() {
        if (this.comboBox == null) {
            this.comboBox = new ComboBoxModel(this.stack, this.req, this.res);
        }
        return this.comboBox;
    }

    public ComponentModel getComponent() {
        if (this.component == null) {
            this.component = new ComponentModel(this.stack, this.req, this.res);
        }
        return this.component;
    }

    public DoubleSelectModel getDoubleselect() {
        if (this.doubleselect == null) {
            this.doubleselect = new DoubleSelectModel(this.stack, this.req, this.res);
        }
        return this.doubleselect;
    }

    public FileModel getFile() {
        if (this.file == null) {
            this.file = new FileModel(this.stack, this.req, this.res);
        }
        return this.file;
    }

    public FormModel getForm() {
        if (this.form == null) {
            this.form = new FormModel(this.stack, this.req, this.res);
        }
        return this.form;
    }

    public HeadModel getHead() {
        if (this.head == null) {
            this.head = new HeadModel(this.stack, this.req, this.res);
        }
        return this.head;
    }

    public HiddenModel getHidden() {
        if (this.hidden == null) {
            this.hidden = new HiddenModel(this.stack, this.req, this.res);
        }
        return this.hidden;
    }

    public LabelModel getLabel() {
        if (this.label == null) {
            this.label = new LabelModel(this.stack, this.req, this.res);
        }
        return this.label;
    }

    public LinkModel getLink() {
        if (this.link == null) {
            this.link = new LinkModel(this.stack, this.req, this.res);
        }
        return this.link;
    }

    public PasswordModel getPassword() {
        if (this.password == null) {
            this.password = new PasswordModel(this.stack, this.req, this.res);
        }
        return this.password;
    }

    public RadioModel getRadio() {
        if (this.radio == null) {
            this.radio = new RadioModel(this.stack, this.req, this.res);
        }
        return this.radio;
    }

    public ScriptModel getScript() {
        if (this.script == null) {
            this.script = new ScriptModel(this.stack, this.req, this.res);
        }
        return this.script;
    }

    public SelectModel getSelect() {
        if (this.select == null) {
            this.select = new SelectModel(this.stack, this.req, this.res);
        }
        return this.select;
    }

    public SubmitModel getSubmit() {
        if (this.submit == null) {
            this.submit = new SubmitModel(this.stack, this.req, this.res);
        }
        return this.submit;
    }

    public ResetModel getReset() {
        if (this.reset == null) {
            this.reset = new ResetModel(this.stack, this.req, this.res);
        }
        return this.reset;
    }

    public TextAreaModel getTextarea() {
        if (this.textarea == null) {
            this.textarea = new TextAreaModel(this.stack, this.req, this.res);
        }
        return this.textarea;
    }

    public TextFieldModel getTextfield() {
        if (this.textfield == null) {
            this.textfield = new TextFieldModel(this.stack, this.req, this.res);
        }
        return this.textfield;
    }

    public DateModel getDate() {
        if (this.date == null) {
            this.date = new DateModel(this.stack, this.req, this.res);
        }
        return this.date;
    }

    public TokenModel getToken() {
        if (this.token == null) {
            this.token = new TokenModel(this.stack, this.req, this.res);
        }
        return this.token;
    }

    public URLModel getUrl() {
        if (this.url == null) {
            this.url = new URLModel(this.stack, this.req, this.res);
        }
        return this.url;
    }

    public IncludeModel getInclude() {
        if (this.include == null) {
            this.include = new IncludeModel(this.stack, this.req, this.res);
        }
        return this.include;
    }

    public ParamModel getParam() {
        if (this.param == null) {
            this.param = new ParamModel(this.stack, this.req, this.res);
        }
        return this.param;
    }

    public ActionModel getAction() {
        if (this.action == null) {
            this.action = new ActionModel(this.stack, this.req, this.res);
        }
        return this.action;
    }

    public AnchorModel getA() {
        if (this.a == null) {
            this.a = new AnchorModel(this.stack, this.req, this.res);
        }
        return this.a;
    }

    public AnchorModel getHref() {
        if (this.a == null) {
            this.a = new AnchorModel(this.stack, this.req, this.res);
        }
        return this.a;
    }

    public TextModel getText() {
        if (this.text == null) {
            this.text = new TextModel(this.stack, this.req, this.res);
        }
        return this.text;
    }

    public BeanModel getBean() {
        if (this.bean == null) {
            this.bean = new BeanModel(this.stack, this.req, this.res);
        }
        return this.bean;
    }

    public I18nModel getI18n() {
        if (this.i18n == null) {
            this.i18n = new I18nModel(this.stack, this.req, this.res);
        }
        return this.i18n;
    }

    public PushModel getPush() {
        if (this.push == null) {
            this.push = new PushModel(this.stack, this.req, this.res);
        }
        return this.push;
    }

    public SetModel getSet() {
        if (this.set == null) {
            this.set = new SetModel(this.stack, this.req, this.res);
        }
        return this.set;
    }

    public PropertyModel getProperty() {
        if (this.property == null) {
            this.property = new PropertyModel(this.stack, this.req, this.res);
        }
        return this.property;
    }

    public IteratorModel getIterator() {
        if (this.iterator == null) {
            this.iterator = new IteratorModel(this.stack, this.req, this.res);
        }
        return this.iterator;
    }

    public ActionErrorModel getActionerror() {
        if (this.actionerror == null) {
            this.actionerror = new ActionErrorModel(this.stack, this.req, this.res);
        }
        return this.actionerror;
    }

    public ActionMessageModel getActionmessage() {
        if (this.actionmessage == null) {
            this.actionmessage = new ActionMessageModel(this.stack, this.req, this.res);
        }
        return this.actionmessage;
    }

    public FieldErrorModel getFielderror() {
        if (this.fielderror == null) {
            this.fielderror = new FieldErrorModel(this.stack, this.req, this.res);
        }
        return this.fielderror;
    }

    public OptionTransferSelectModel getOptiontransferselect() {
        if (this.optiontransferselect == null) {
            this.optiontransferselect = new OptionTransferSelectModel(this.stack, this.req, this.res);
        }
        return this.optiontransferselect;
    }

    public UpDownSelectModel getUpdownselect() {
        if (this.updownselect == null) {
            this.updownselect = new UpDownSelectModel(this.stack, this.req, this.res);
        }
        return this.updownselect;
    }

    public OptGroupModel getOptgroup() {
        if (this.optGroupModel == null) {
            this.optGroupModel = new OptGroupModel(this.stack, this.req, this.res);
        }
        return this.optGroupModel;
    }

    public IfModel getIf() {
        if (this.ifModel == null) {
            this.ifModel = new IfModel(this.stack, this.req, this.res);
        }
        return this.ifModel;
    }

    public ElseModel getElse() {
        if (this.elseModel == null) {
            this.elseModel = new ElseModel(this.stack, this.req, this.res);
        }
        return this.elseModel;
    }

    public ElseIfModel getElseif() {
        if (this.elseIfModel == null) {
            this.elseIfModel = new ElseIfModel(this.stack, this.req, this.res);
        }
        return this.elseIfModel;
    }

    public InputTransferSelectModel getInputtransferselect() {
        if (this.inputtransferselect == null) {
            this.inputtransferselect = new InputTransferSelectModel(this.stack, this.req, this.res);
        }
        return this.inputtransferselect;
    }
}

