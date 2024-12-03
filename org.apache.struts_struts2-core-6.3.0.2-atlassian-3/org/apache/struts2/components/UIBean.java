/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Form;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;
import org.apache.struts2.components.template.TemplateEngineManager;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.dispatcher.StaticContentLoader;
import org.apache.struts2.util.ComponentUtils;
import org.apache.struts2.util.TextProviderHelper;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.util.ContextUtil;

public abstract class UIBean
extends Component {
    private static final Logger LOG = LogManager.getLogger(UIBean.class);
    protected static final String ATTR_FIELD_VALUE = "fieldValue";
    protected static final String ATTR_NAME_VALUE = "nameValue";
    protected static final String ATTR_VALUE = "value";
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected String templateSuffix;
    protected String template;
    protected String templateDir;
    protected String theme;
    protected String key;
    protected String id;
    protected String cssClass;
    protected String cssStyle;
    protected String cssErrorClass;
    protected String cssErrorStyle;
    protected String disabled;
    protected String label;
    protected String labelPosition;
    protected String labelSeparator;
    protected String requiredPosition;
    protected String errorPosition;
    protected String name;
    protected String requiredLabel;
    protected String tabindex;
    protected String value;
    protected String title;
    protected String onclick;
    protected String ondblclick;
    protected String onmousedown;
    protected String onmouseup;
    protected String onmouseover;
    protected String onmousemove;
    protected String onmouseout;
    protected String onfocus;
    protected String onblur;
    protected String onkeypress;
    protected String onkeydown;
    protected String onkeyup;
    protected String onselect;
    protected String onchange;
    protected String accesskey;
    protected String tooltip;
    protected String tooltipConfig;
    protected String javascriptTooltip;
    protected String tooltipDelay;
    protected String tooltipCssClass;
    protected String tooltipIconPath;
    protected Map<String, Object> dynamicAttributes = new HashMap<String, Object>();
    protected String defaultTemplateDir;
    protected String defaultUITheme;
    protected String uiThemeExpansionToken;
    protected String uiStaticContentPath;
    protected TemplateEngineManager templateEngineManager;

    public UIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack);
        this.request = request;
        this.response = response;
        this.templateSuffix = ContextUtil.getTemplateSuffix(stack.getContext());
    }

    @Inject(value="struts.ui.templateDir")
    public void setDefaultTemplateDir(String dir) {
        this.defaultTemplateDir = dir;
    }

    @Inject(value="struts.ui.theme")
    public void setDefaultUITheme(String theme) {
        this.defaultUITheme = theme;
    }

    @Inject(value="struts.ui.theme.expansion.token")
    public void setUIThemeExpansionToken(String uiThemeExpansionToken) {
        this.uiThemeExpansionToken = uiThemeExpansionToken;
    }

    @Inject(value="struts.ui.staticContentPath")
    public void setStaticContentPath(String uiStaticContentPath) {
        this.uiStaticContentPath = StaticContentLoader.Validator.validateStaticContentPath(uiStaticContentPath);
    }

    @Inject
    public void setTemplateEngineManager(TemplateEngineManager mgr) {
        this.templateEngineManager = mgr;
    }

    @Override
    public boolean end(Writer writer, String body) {
        this.evaluateParams();
        try {
            super.end(writer, body, false);
            this.mergeTemplate(writer, this.buildTemplateName(this.template, this.getDefaultTemplate()));
        }
        catch (Exception e) {
            throw new StrutsException(e);
        }
        finally {
            this.popComponentStack();
        }
        return false;
    }

    protected abstract String getDefaultTemplate();

    protected Template buildTemplateName(String myTemplate, String myDefaultTemplate) {
        String templateName = myDefaultTemplate;
        if (myTemplate != null) {
            templateName = this.findString(myTemplate);
        }
        return new Template(this.getTemplateDir(), this.getTheme(), templateName);
    }

    protected void mergeTemplate(Writer writer, Template template) throws Exception {
        TemplateEngine engine = this.templateEngineManager.getTemplateEngine(template, this.templateSuffix);
        if (engine == null) {
            throw new ConfigurationException("Unable to find a TemplateEngine for template " + template);
        }
        LOG.debug("Rendering template {}", (Object)template);
        TemplateRenderingContext context = new TemplateRenderingContext(template, writer, this.getStack(), this.getParameters(), this);
        engine.renderTemplate(context);
    }

    public String getTemplateDir() {
        String result = null;
        if (this.templateDir != null) {
            result = this.findString(this.templateDir);
        }
        if (StringUtils.isBlank(result)) {
            result = this.defaultTemplateDir;
        }
        if (StringUtils.isBlank((CharSequence)result)) {
            result = "template";
        }
        return result;
    }

    public String getTheme() {
        Form form;
        String result = null;
        if (this.theme != null) {
            result = this.findString(this.theme);
        }
        if (StringUtils.isBlank(result) && (form = (Form)this.findAncestor(Form.class)) != null) {
            result = form.getTheme();
        }
        if (StringUtils.isBlank((CharSequence)result)) {
            result = this.defaultUITheme;
        }
        return result;
    }

    public void evaluateParams() {
        Map<String, Object> session;
        Object nonceValue;
        Object parsedValue;
        String gotTheme = this.getTheme();
        this.addParameter("templateDir", this.getTemplateDir());
        this.addParameter("theme", gotTheme);
        this.addParameter("template", this.template != null ? this.findString(this.template) : this.getDefaultTemplate());
        this.addParameter("dynamicAttributes", this.dynamicAttributes);
        this.addParameter("themeExpansionToken", this.uiThemeExpansionToken);
        this.addParameter("expandTheme", this.uiThemeExpansionToken + gotTheme);
        this.addParameter("staticContentPath", this.findString(this.uiStaticContentPath));
        String translatedName = null;
        String providedLabel = null;
        if (this.key != null) {
            if (this.name == null) {
                this.name = this.key;
            }
            if (this.label == null) {
                providedLabel = TextProviderHelper.getText(this.key, this.key, this.stack);
            }
        }
        if (this.name != null) {
            translatedName = this.findString(this.name);
            this.addParameter("name", translatedName);
        }
        if (this.label != null) {
            this.addParameter("label", this.findString(this.label));
        } else if (providedLabel != null) {
            this.addParameter("label", providedLabel);
        }
        if (this.labelSeparator != null) {
            this.addParameter("labelseparator", this.findString(this.labelSeparator));
        }
        if (this.labelPosition != null) {
            String labelPosition = this.findString(this.labelPosition);
            this.addParameter("labelPosition", labelPosition);
        }
        if (this.requiredPosition != null) {
            this.addParameter("requiredPosition", this.findString(this.requiredPosition));
        }
        if (this.errorPosition != null) {
            this.addParameter("errorposition", this.findString(this.errorPosition));
        }
        if (this.requiredLabel != null) {
            parsedValue = this.findValue(this.requiredLabel, Boolean.class);
            this.addParameter("required", parsedValue == null ? Boolean.valueOf(this.requiredLabel) : parsedValue);
        }
        if (this.disabled != null) {
            parsedValue = this.findValue(this.disabled, Boolean.class);
            this.addParameter("disabled", parsedValue == null ? Boolean.valueOf(this.disabled) : parsedValue);
        }
        if (this.tabindex != null) {
            this.addParameter("tabindex", this.findString(this.tabindex));
        }
        if (this.onclick != null) {
            this.addParameter("onclick", this.findString(this.onclick));
        }
        if (this.ondblclick != null) {
            this.addParameter("ondblclick", this.findString(this.ondblclick));
        }
        if (this.onmousedown != null) {
            this.addParameter("onmousedown", this.findString(this.onmousedown));
        }
        if (this.onmouseup != null) {
            this.addParameter("onmouseup", this.findString(this.onmouseup));
        }
        if (this.onmouseover != null) {
            this.addParameter("onmouseover", this.findString(this.onmouseover));
        }
        if (this.onmousemove != null) {
            this.addParameter("onmousemove", this.findString(this.onmousemove));
        }
        if (this.onmouseout != null) {
            this.addParameter("onmouseout", this.findString(this.onmouseout));
        }
        if (this.onfocus != null) {
            this.addParameter("onfocus", this.findString(this.onfocus));
        }
        if (this.onblur != null) {
            this.addParameter("onblur", this.findString(this.onblur));
        }
        if (this.onkeypress != null) {
            this.addParameter("onkeypress", this.findString(this.onkeypress));
        }
        if (this.onkeydown != null) {
            this.addParameter("onkeydown", this.findString(this.onkeydown));
        }
        if (this.onkeyup != null) {
            this.addParameter("onkeyup", this.findString(this.onkeyup));
        }
        if (this.onselect != null) {
            this.addParameter("onselect", this.findString(this.onselect));
        }
        if (this.onchange != null) {
            this.addParameter("onchange", this.findString(this.onchange));
        }
        if (this.accesskey != null) {
            this.addParameter("accesskey", this.findString(this.accesskey));
        }
        if (this.cssClass != null) {
            this.addParameter("cssClass", this.findString(this.cssClass));
        }
        if (this.cssStyle != null) {
            this.addParameter("cssStyle", this.findString(this.cssStyle));
        }
        if (this.cssErrorClass != null) {
            this.addParameter("cssErrorClass", this.findString(this.cssErrorClass));
        }
        if (this.cssErrorStyle != null) {
            this.addParameter("cssErrorStyle", this.findString(this.cssErrorStyle));
        }
        if (this.title != null) {
            this.addParameter("title", this.findString(this.title));
        }
        this.applyValueParameter(translatedName);
        Form form = (Form)this.findAncestor(Form.class);
        this.populateComponentHtmlId(form);
        if (form != null) {
            this.addParameter("form", form.getParameters());
            if (translatedName != null) {
                List tags = (List)form.getParameters().get("tagNames");
                tags.add(translatedName);
            }
        }
        if (this.tooltipConfig != null) {
            this.addParameter("tooltipConfig", this.findValue(this.tooltipConfig));
        }
        if (this.tooltip != null) {
            String tooltipDelayParam;
            String tooltipIcon;
            this.addParameter("tooltip", this.findString(this.tooltip));
            Map<String, String> tooltipConfigMap = this.getTooltipConfig(this);
            if (form != null) {
                form.addParameter("hasTooltip", Boolean.TRUE);
                Map<String, String> overallTooltipConfigMap = this.getTooltipConfig(form);
                overallTooltipConfigMap.putAll(tooltipConfigMap);
                for (Map.Entry<String, String> entry : overallTooltipConfigMap.entrySet()) {
                    this.addParameter(entry.getKey(), entry.getValue());
                }
            } else {
                LOG.warn("No ancestor Form found, javascript based tooltip will not work, however standard HTML tooltip using alt and title attribute will still work");
            }
            String jsTooltipEnabled = (String)this.getParameters().get("jsTooltipEnabled");
            if (jsTooltipEnabled != null) {
                this.javascriptTooltip = jsTooltipEnabled;
            }
            if ((tooltipIcon = (String)this.getParameters().get("tooltipIcon")) != null) {
                this.addParameter("tooltipIconPath", tooltipIcon);
            }
            if (this.tooltipIconPath != null) {
                this.addParameter("tooltipIconPath", this.findString(this.tooltipIconPath));
            }
            if ((tooltipDelayParam = (String)this.getParameters().get("tooltipDelay")) != null) {
                this.addParameter("tooltipDelay", tooltipDelayParam);
            }
            if (this.tooltipDelay != null) {
                this.addParameter("tooltipDelay", this.findString(this.tooltipDelay));
            }
            if (this.javascriptTooltip != null) {
                Object jsTooltips = this.findValue(this.javascriptTooltip, Boolean.class);
                this.addParameter("jsTooltipEnabled", jsTooltips == null ? this.javascriptTooltip : jsTooltips.toString());
                if (form != null) {
                    form.addParameter("hasTooltip", jsTooltips);
                }
                if (this.tooltipCssClass != null) {
                    this.addParameter("tooltipCssClass", this.findString(this.tooltipCssClass));
                }
            }
        }
        Object object = nonceValue = (session = this.stack.getActionContext().getSession()) != null ? session.get("nonce") : null;
        if (nonceValue != null) {
            this.addParameter("nonce", nonceValue.toString());
        }
        this.evaluateExtraParams();
    }

    protected void applyValueParameter(String translatedName) {
        if (this.parameters.containsKey(ATTR_VALUE)) {
            this.parameters.put(ATTR_NAME_VALUE, this.parameters.get(ATTR_VALUE));
        } else if (this.evaluateNameValue()) {
            Class<?> valueClazz = this.getValueClassType();
            if (valueClazz != null) {
                if (this.value != null) {
                    this.addParameter(ATTR_NAME_VALUE, this.findValue(this.value, valueClazz));
                } else if (translatedName != null) {
                    this.processTranslatedName(translatedName, expr -> this.findValue((String)expr, valueClazz));
                }
            } else if (this.value != null) {
                this.addParameter(ATTR_NAME_VALUE, this.findValue(this.value));
            } else if (translatedName != null) {
                this.processTranslatedName(translatedName, this::findValue);
            }
        }
    }

    private void processTranslatedName(String translatedName, Function<String, Object> evaluator) {
        boolean reevaluate;
        boolean evaluated = !translatedName.equals(this.name);
        boolean bl = reevaluate = !evaluated || this.isAcceptableExpression(translatedName);
        if (!reevaluate) {
            this.addParameter(ATTR_NAME_VALUE, translatedName);
        } else {
            String expr = this.completeExpression(translatedName);
            this.addParameter(ATTR_NAME_VALUE, evaluator.apply(expr));
        }
    }

    protected String escape(String name) {
        if (name != null) {
            return name.replaceAll("[^a-zA-Z0-9_]", "_");
        }
        return null;
    }

    protected String ensureAttributeSafelyNotEscaped(String val) {
        if (val != null) {
            return val.replaceAll("\"", "&#34;");
        }
        return null;
    }

    protected void evaluateExtraParams() {
    }

    protected boolean evaluateNameValue() {
        return true;
    }

    protected Class<?> getValueClassType() {
        return String.class;
    }

    public void addFormParameter(String key, Object value) {
        Form form = (Form)this.findAncestor(Form.class);
        if (form != null) {
            form.addParameter(key, value);
        }
    }

    protected void enableAncestorFormCustomOnsubmit() {
        Form form = (Form)this.findAncestor(Form.class);
        if (form != null) {
            form.addParameter("customOnsubmitEnabled", Boolean.TRUE);
        } else if (LOG.isWarnEnabled()) {
            LOG.warn("Cannot find an Ancestor form, custom onsubmit is NOT enabled");
        }
    }

    protected Map<String, String> getTooltipConfig(UIBean component) {
        Object tooltipConfigObj = component.getParameters().get("tooltipConfig");
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        if (tooltipConfigObj instanceof Map) {
            result = new LinkedHashMap((Map)tooltipConfigObj);
        } else if (tooltipConfigObj instanceof String) {
            String[] tooltipConfigArray;
            String tooltipConfigStr = (String)tooltipConfigObj;
            for (String aTooltipConfigArray : tooltipConfigArray = tooltipConfigStr.split("\\|")) {
                String[] configEntry = aTooltipConfigArray.trim().split("=");
                String configKey = configEntry[0].trim();
                if (configEntry.length > 1) {
                    String configValue = configEntry[1].trim();
                    result.put(configKey, configValue);
                    continue;
                }
                LOG.warn("component {} tooltip config param {} has no value defined, skipped", (Object)component, (Object)configKey);
            }
        }
        if (component.javascriptTooltip != null) {
            result.put("jsTooltipEnabled", component.javascriptTooltip);
        }
        if (component.tooltipIconPath != null) {
            result.put("tooltipIcon", component.tooltipIconPath);
        }
        if (component.tooltipDelay != null) {
            result.put("tooltipDelay", component.tooltipDelay);
        }
        return result;
    }

    protected void populateComponentHtmlId(Form form) {
        String tryId;
        if (this.id != null) {
            tryId = this.id;
        } else {
            String generatedId = this.escape(this.name != null ? this.findString(this.name) : null);
            if (null == generatedId) {
                LOG.debug("Cannot determine id attribute for [{}], consider defining id, name or key attribute!", (Object)this);
                tryId = null;
            } else {
                tryId = form != null ? form.getParameters().get("id") + "_" + generatedId : generatedId;
            }
        }
        if (tryId != null) {
            this.addParameter("id", tryId);
            this.addParameter("escapedId", this.escape(tryId));
        }
    }

    public String getId() {
        return this.id;
    }

    @StrutsTagAttribute(description="HTML id attribute")
    public void setId(String id) {
        this.id = id;
    }

    @StrutsTagAttribute(description="The template directory.")
    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    @StrutsTagAttribute(description="The theme (other than default) to use for rendering the element")
    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTemplate() {
        return this.template;
    }

    @StrutsTagAttribute(description="The template (other than default) to use for rendering the element")
    public void setTemplate(String template) {
        this.template = template;
    }

    @StrutsTagAttribute(description="The css class to use for element")
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    @StrutsTagAttribute(description="The css style definitions for element to use")
    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    @StrutsTagAttribute(description="The css style definitions for element to use - it's an alias of cssStyle attribute.")
    public void setStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    @StrutsTagAttribute(description="The css error class to use for element")
    public void setCssErrorClass(String cssErrorClass) {
        this.cssErrorClass = cssErrorClass;
    }

    @StrutsTagAttribute(description="The css error style definitions for element to use")
    public void setCssErrorStyle(String cssErrorStyle) {
        this.cssErrorStyle = cssErrorStyle;
    }

    @StrutsTagAttribute(description="Set the html title attribute on rendered html element")
    public void setTitle(String title) {
        this.title = title;
    }

    @StrutsTagAttribute(description="Set the html disabled attribute on rendered html element")
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    @StrutsTagAttribute(description="Label expression used for rendering an element specific label")
    public void setLabel(String label) {
        this.label = label;
    }

    @StrutsTagAttribute(description="String that will be appended to the label", defaultValue=":")
    public void setLabelSeparator(String labelseparator) {
        this.labelSeparator = labelseparator;
    }

    @StrutsTagAttribute(description="Define label position of form element (top/left)")
    public void setLabelPosition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

    @StrutsTagAttribute(description="Define required position of required form element (left|right)")
    public void setRequiredPosition(String requiredPosition) {
        this.requiredPosition = requiredPosition;
    }

    @StrutsTagAttribute(description="Define error position of form element (top|bottom)")
    public void setErrorPosition(String errorPosition) {
        this.errorPosition = errorPosition;
    }

    @StrutsTagAttribute(description="The name to set for element")
    public void setName(String name) {
        this.name = name;
    }

    @StrutsTagAttribute(description="If set to true, the rendered element will indicate that input is required", type="Boolean", defaultValue="false")
    public void setRequiredLabel(String requiredLabel) {
        this.requiredLabel = requiredLabel;
    }

    @StrutsTagAttribute(description="Set the html tabindex attribute on rendered html element")
    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }

    @StrutsTagAttribute(description="Preset the value of input element.")
    public void setValue(String value) {
        this.value = value;
    }

    @StrutsTagAttribute(description="Set the html onclick attribute on rendered html element")
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    @StrutsTagAttribute(description="Set the html ondblclick attribute on rendered html element")
    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }

    @StrutsTagAttribute(description="Set the html onmousedown attribute on rendered html element")
    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }

    @StrutsTagAttribute(description="Set the html onmouseup attribute on rendered html element")
    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }

    @StrutsTagAttribute(description="Set the html onmouseover attribute on rendered html element")
    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    @StrutsTagAttribute(description="Set the html onmousemove attribute on rendered html element")
    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }

    @StrutsTagAttribute(description="Set the html onmouseout attribute on rendered html element")
    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

    @StrutsTagAttribute(description="Set the html onfocus attribute on rendered html element")
    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    @StrutsTagAttribute(description=" Set the html onblur attribute on rendered html element")
    public void setOnblur(String onblur) {
        this.onblur = onblur;
    }

    @StrutsTagAttribute(description="Set the html onkeypress attribute on rendered html element")
    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    @StrutsTagAttribute(description="Set the html onkeydown attribute on rendered html element")
    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    @StrutsTagAttribute(description="Set the html onkeyup attribute on rendered html element")
    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    @StrutsTagAttribute(description="Set the html onselect attribute on rendered html element")
    public void setOnselect(String onselect) {
        this.onselect = onselect;
    }

    @StrutsTagAttribute(description="Set the html onchange attribute on rendered html element")
    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    @StrutsTagAttribute(description="Set the html accesskey attribute on rendered html element")
    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    @StrutsTagAttribute(description="Set the tooltip of this particular component")
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @StrutsTagAttribute(description="Deprecated. Use individual tooltip configuration attributes instead.")
    public void setTooltipConfig(String tooltipConfig) {
        this.tooltipConfig = tooltipConfig;
    }

    @StrutsTagAttribute(description="Set the key (name, value, label) for this particular component")
    public void setKey(String key) {
        this.key = key;
    }

    @StrutsTagAttribute(description="Use JavaScript to generate tooltips", type="Boolean", defaultValue="false")
    public void setJavascriptTooltip(String javascriptTooltip) {
        this.javascriptTooltip = javascriptTooltip;
    }

    @StrutsTagAttribute(description="CSS class applied to JavaScrip tooltips", defaultValue="StrutsTTClassic")
    public void setTooltipCssClass(String tooltipCssClass) {
        this.tooltipCssClass = tooltipCssClass;
    }

    @StrutsTagAttribute(description="Delay in milliseconds, before showing JavaScript tooltips ", defaultValue="Classic")
    public void setTooltipDelay(String tooltipDelay) {
        this.tooltipDelay = tooltipDelay;
    }

    @StrutsTagAttribute(description="Icon path used for image that will have the tooltip")
    public void setTooltipIconPath(String tooltipIconPath) {
        this.tooltipIconPath = tooltipIconPath;
    }

    public void setDynamicAttributes(Map<String, String> tagDynamicAttributes) {
        for (Map.Entry<String, String> entry : tagDynamicAttributes.entrySet()) {
            String attrName = entry.getKey();
            String attrValue = entry.getValue();
            if (this.isValidTagAttribute(attrName)) continue;
            if (ComponentUtils.containsExpression(attrValue) && !this.lazyEvaluation()) {
                String translated = TextParseUtil.translateVariables('%', attrValue, this.stack);
                this.dynamicAttributes.put(attrName, ObjectUtils.defaultIfNull((Object)translated, (Object)attrValue));
                continue;
            }
            this.dynamicAttributes.put(attrName, attrValue);
        }
    }

    @Override
    public void copyParams(Map<String, Object> params) {
        super.copyParams(params);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String entryKey = entry.getKey();
            if (this.isValidTagAttribute(entryKey) || entryKey.equals("dynamicAttributes")) continue;
            this.dynamicAttributes.put(entryKey, entry.getValue());
        }
    }

    protected boolean lazyEvaluation() {
        return false;
    }
}

