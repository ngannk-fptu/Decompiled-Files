/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang.StringEscapeUtils
 *  org.apache.commons.validator.Field
 *  org.apache.commons.validator.Form
 *  org.apache.commons.validator.ValidatorAction
 *  org.apache.commons.validator.ValidatorResources
 *  org.apache.commons.validator.Var
 *  org.apache.struts.config.ActionConfig
 *  org.apache.struts.config.ModuleConfig
 *  org.apache.struts.util.MessageResources
 *  org.apache.struts.util.ModuleUtils
 *  org.apache.struts.validator.Resources
 */
package org.apache.velocity.tools.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.Var;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.apache.struts.validator.Resources;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.apache.velocity.tools.struts.StrutsUtils;
import org.apache.velocity.tools.view.ViewContext;

@DefaultKey(value="validator")
@ValidScope(value={"request"})
public class ValidatorTool {
    protected ViewContext context;
    protected ServletContext app;
    protected HttpServletRequest request;
    protected HttpSession session;
    protected ValidatorResources resources;
    private static final String HTML_BEGIN_COMMENT = "\n<!-- Begin \n";
    private static final String HTML_END_COMMENT = "//End --> \n";
    private boolean xhtml = false;
    private boolean xmlMode = false;
    private boolean htmlComment = true;
    private boolean cdata = true;
    private String formName = null;
    private String methodName = null;
    private String src = null;
    private int page = 0;
    protected String jsFormName = null;
    private static final Comparator<ValidatorAction> actionComparator = new Comparator<ValidatorAction>(){

        @Override
        public int compare(ValidatorAction va1, ValidatorAction va2) {
            if (!(va1.getDepends() != null && va1.getDepends().length() != 0 || va2.getDepends() != null && va2.getDepends().length() != 0)) {
                return 0;
            }
            if (va1.getDepends() != null && va1.getDepends().length() > 0 && (va2.getDepends() == null || va2.getDepends().length() == 0)) {
                return 1;
            }
            if ((va1.getDepends() == null || va1.getDepends().length() == 0) && va2.getDepends() != null && va2.getDepends().length() > 0) {
                return -1;
            }
            return va1.getDependencyList().size() - va2.getDependencyList().size();
        }
    };

    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            this.context = (ViewContext)obj;
            this.request = this.context.getRequest();
            this.session = this.request.getSession(false);
            this.app = this.context.getServletContext();
        }
    }

    public void configure(Map<String, Object> params) {
        ActionConfig config;
        this.context = (ViewContext)params.get("velocityContext");
        this.request = (HttpServletRequest)params.get("request");
        this.session = this.request.getSession(false);
        this.app = (ServletContext)params.get("servletContext");
        Boolean b = (Boolean)params.get("XHTML");
        if (b != null) {
            this.xhtml = b;
        }
        if ((b = (Boolean)params.get("XMLMode")) != null) {
            this.xmlMode = b;
        }
        if ((config = (ActionConfig)this.request.getAttribute("org.apache.struts.action.mapping.instance")) != null) {
            this.formName = config.getAttribute();
        }
        ModuleConfig mconfig = ModuleUtils.getInstance().getModuleConfig(this.request, this.app);
        this.resources = (ValidatorResources)this.app.getAttribute("org.apache.commons.validator.VALIDATOR_RESOURCES" + mconfig.getPrefix());
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getMethod() {
        return this.methodName;
    }

    public void setMethod(String methodName) {
        this.methodName = methodName;
    }

    public boolean getHtmlComment() {
        return this.htmlComment;
    }

    public void setHtmlComment(boolean htmlComment) {
        this.htmlComment = htmlComment;
    }

    public String getSrc() {
        return this.src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public boolean getCdata() {
        return this.cdata;
    }

    public void setCdata(boolean cdata) {
        this.cdata = cdata;
    }

    public boolean getXhtml() {
        return this.xhtml;
    }

    public void setXhtml(boolean xhtml) {
        this.xhtml = xhtml;
    }

    public boolean getXMLMode() {
        return this.xmlMode;
    }

    public void setXMLMode(boolean xmlMode) {
        if (xmlMode) {
            this.setXhtml(true);
        }
        this.xmlMode = xmlMode;
    }

    public String getJavascript() throws Exception {
        return this.getJavascript(this.formName);
    }

    public String getJavascript(String formName) throws Exception {
        this.formName = formName;
        return this.getJavascript(formName, true);
    }

    public String getDynamicJavascript() throws Exception {
        return this.getDynamicJavascript(this.formName);
    }

    public String getStaticJavascript() throws Exception {
        StringBuilder results = new StringBuilder();
        results.append(this.getStartElement());
        if (this.htmlComment) {
            results.append(HTML_BEGIN_COMMENT);
        }
        results.append(this.getJavascriptStaticMethods(this.resources));
        results.append(this.getJavascriptEnd());
        return results.toString();
    }

    public String getDynamicJavascript(String formName) throws Exception {
        this.formName = formName;
        return this.getJavascript(formName, false);
    }

    protected String getJavascript(String formName, boolean getStatic) throws Exception {
        StringBuilder results = new StringBuilder();
        Locale locale = StrutsUtils.getLocale(this.request, this.session);
        Form form = this.resources.getForm(locale, formName);
        if (form != null) {
            results.append(this.getDynamicJavascript(this.resources, locale, form));
        }
        if (getStatic) {
            results.append(this.getJavascriptStaticMethods(this.resources));
        }
        if (form != null) {
            results.append(this.getJavascriptEnd());
        }
        return results.toString();
    }

    protected String getDynamicJavascript(ValidatorResources resources, Locale locale, Form form) {
        String formName;
        StringBuilder results = new StringBuilder();
        MessageResources messages = StrutsUtils.getMessageResources(this.request, this.app);
        List<ValidatorAction> actions = this.createActionList(resources, form);
        String methods = this.createMethods(actions);
        this.jsFormName = formName = form.getName();
        if (this.jsFormName.charAt(0) == '/') {
            String mappingName = StrutsUtils.getActionMappingName(this.jsFormName);
            ModuleConfig mconfig = ModuleUtils.getInstance().getModuleConfig(this.request, this.app);
            ActionConfig mapping = mconfig.findActionConfig(mappingName);
            if (mapping == null) {
                throw new NullPointerException("Cannot retrieve mapping for action " + mappingName);
            }
            this.jsFormName = mapping.getAttribute();
        }
        results.append(this.getJavascriptBegin(methods));
        for (ValidatorAction va : actions) {
            int jscriptVar = 0;
            String functionName = null;
            functionName = va.getJsFunctionName() != null && va.getJsFunctionName().length() > 0 ? va.getJsFunctionName() : va.getName();
            results.append("    function ");
            results.append(this.jsFormName);
            results.append("_");
            results.append(functionName);
            results.append(" () { \n");
            for (Field field : form.getFields()) {
                if (field.isIndexed() || field.getPage() != this.page || !field.isDependency(va.getName())) continue;
                String message = Resources.getMessage((ServletContext)this.app, (HttpServletRequest)this.request, (MessageResources)messages, (Locale)locale, (ValidatorAction)va, (Field)field);
                message = message != null ? message : "";
                results.append("     this.a");
                results.append(jscriptVar++);
                results.append(" = new Array(\"");
                results.append(field.getKey());
                results.append("\", \"");
                message = this.escapeJavascript(message);
                if (this.xmlMode && !this.cdata) {
                    message = StringEscapeUtils.escapeXml((String)message);
                }
                results.append(message);
                results.append("\", new Function (\"varName\", \"");
                Map vars = field.getVars();
                for (Map.Entry entry : vars.entrySet()) {
                    String varName = (String)entry.getKey();
                    Var var = (Var)entry.getValue();
                    String varValue = Resources.getVarValue((Var)var, (ServletContext)this.app, (HttpServletRequest)this.request, (boolean)false);
                    String jsType = var.getJsType();
                    if (varName.startsWith("field")) continue;
                    results.append("this.");
                    results.append(varName);
                    String escapedVarValue = this.escapeJavascript(varValue);
                    if (this.xmlMode && !this.cdata) {
                        escapedVarValue = StringEscapeUtils.escapeXml((String)escapedVarValue);
                    }
                    if ("int".equalsIgnoreCase(jsType)) {
                        results.append("=");
                        results.append(escapedVarValue);
                        results.append("; ");
                        continue;
                    }
                    if ("regexp".equalsIgnoreCase(jsType)) {
                        results.append("=/");
                        results.append(escapedVarValue);
                        results.append("/; ");
                        continue;
                    }
                    if ("string".equalsIgnoreCase(jsType)) {
                        results.append("='");
                        results.append(escapedVarValue);
                        results.append("'; ");
                        continue;
                    }
                    if ("mask".equalsIgnoreCase(varName)) {
                        results.append("=/");
                        results.append(escapedVarValue);
                        results.append("/; ");
                        continue;
                    }
                    results.append("='");
                    results.append(escapedVarValue);
                    results.append("'; ");
                }
                results.append(" return this[varName];\"));\n");
            }
            results.append("    } \n\n");
        }
        return results.toString();
    }

    protected String escapeJavascript(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length == 0) {
            return str;
        }
        StringBuilder out = new StringBuilder(length + 4);
        for (int i = 0; i < length; ++i) {
            char c = str.charAt(i);
            if (c == '\"' || c == '\'' || c == '\\' || c == '\n' || c == '\r') {
                out.append('\\');
            }
            out.append(c);
        }
        return out.toString();
    }

    protected String createMethods(List<ValidatorAction> actions) {
        String methodOperator = this.xmlMode && !this.cdata ? " &amp;&amp; " : " && ";
        StringBuilder methods = null;
        for (ValidatorAction va : actions) {
            if (methods == null) {
                methods = new StringBuilder(va.getMethod());
            } else {
                methods.append(methodOperator);
                methods.append(va.getMethod());
            }
            methods.append("(form)");
        }
        return methods.toString();
    }

    protected List<ValidatorAction> createActionList(ValidatorResources resources, Form form) {
        ArrayList<String> actionMethods = new ArrayList<String>();
        for (Field field : form.getFields()) {
            for (String dep : field.getDependencyList()) {
                if (dep == null || actionMethods.contains(dep)) continue;
                actionMethods.add(dep);
            }
        }
        ArrayList<ValidatorAction> actions = new ArrayList<ValidatorAction>();
        Iterator i = actionMethods.iterator();
        while (i.hasNext()) {
            String depends = (String)i.next();
            ValidatorAction va = resources.getValidatorAction(depends);
            if (va == null) {
                throw new NullPointerException("Depends string \"" + depends + "\" was not found in validator-rules.xml.");
            }
            String javascript = va.getJavascript();
            if (javascript != null && javascript.length() > 0) {
                actions.add(va);
                continue;
            }
            i.remove();
        }
        Collections.sort(actions, actionComparator);
        return actions;
    }

    protected String getJavascriptBegin(String methods) {
        StringBuilder sb = new StringBuilder();
        String name = this.jsFormName.replace('/', '_');
        name = this.jsFormName.substring(0, 1).toUpperCase() + this.jsFormName.substring(1, this.jsFormName.length());
        sb.append(this.getStartElement());
        if (this.xhtml && this.cdata) {
            sb.append("<![CDATA[\r\n");
        }
        if (!this.xmlMode && !this.xhtml && this.htmlComment) {
            sb.append(HTML_BEGIN_COMMENT);
        }
        sb.append("\n    var bCancel = false;\n\n");
        if (this.methodName == null || this.methodName.length() == 0) {
            sb.append("    function validate");
            sb.append(name);
        } else {
            sb.append("    function ");
            sb.append(this.methodName);
        }
        sb.append("(form) {\n");
        sb.append("      if (bCancel)\n");
        sb.append("          return true;\n");
        sb.append("      else\n");
        if (methods == null || methods.length() == 0) {
            sb.append("       return true;\n");
        } else {
            sb.append("      {\n        var formValidationResult;\n        formValidationResult = ");
            sb.append(methods);
            sb.append(";\n        return (formValidationResult == 1);\n      }\n");
        }
        sb.append("    }\n");
        return sb.toString();
    }

    protected String getJavascriptStaticMethods(ValidatorResources resources) {
        StringBuilder sb = new StringBuilder("\n\n");
        Collection actions = resources.getValidatorActions().values();
        for (ValidatorAction va : actions) {
            String javascript;
            if (va == null || (javascript = va.getJavascript()) == null || javascript.length() <= 0) continue;
            sb.append(javascript);
            sb.append("\n");
        }
        return sb.toString();
    }

    protected String getJavascriptEnd() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        if (!this.xmlMode && !this.xhtml && this.htmlComment) {
            sb.append(HTML_END_COMMENT);
        }
        if (this.xhtml && this.cdata) {
            sb.append("]]>\r\n");
        }
        sb.append("</script>\n\n");
        return sb.toString();
    }

    private String getStartElement() {
        StringBuilder start = new StringBuilder("<script type=\"text/javascript\"");
        if (!this.xhtml) {
            start.append(" language=\"Javascript1.1\"");
        }
        if (this.src != null) {
            start.append(" src=\"" + this.src + "\"");
        }
        start.append(">\n");
        return start.toString();
    }
}

