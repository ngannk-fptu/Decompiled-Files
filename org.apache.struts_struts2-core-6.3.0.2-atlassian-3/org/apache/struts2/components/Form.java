/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptorUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import com.opensymphony.xwork2.validator.FieldValidator;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import com.opensymphony.xwork2.validator.Validator;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.opensymphony.xwork2.validator.validators.VisitorFieldValidator;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.components.UrlRenderer;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="form", tldTagClass="org.apache.struts2.views.jsp.ui.FormTag", description="Renders an input form", allowDynamicAttributes=true)
public class Form
extends ClosingUIBean {
    public static final String OPEN_TEMPLATE = "form";
    public static final String TEMPLATE = "form-close";
    private int sequence = 0;
    protected String onsubmit;
    protected String onreset;
    protected String action;
    protected String target;
    protected String enctype;
    protected String method;
    protected String namespace;
    protected String validate;
    protected String portletMode;
    protected String windowState;
    protected String acceptcharset;
    protected boolean includeContext = true;
    protected String focusElement;
    protected Configuration configuration;
    protected ObjectFactory objectFactory;
    protected UrlRenderer urlRenderer;
    protected ActionValidatorManager actionValidatorManager;

    public Form(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected boolean evaluateNameValue() {
        return false;
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Inject
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setUrlRenderer(UrlRenderer urlRenderer) {
        this.urlRenderer = urlRenderer;
    }

    @Inject
    public void setActionValidatorManager(ActionValidatorManager mgr) {
        this.actionValidatorManager = mgr;
    }

    @Override
    protected void evaluateExtraParams() {
        String id;
        super.evaluateExtraParams();
        if (this.validate != null) {
            this.addParameter("validate", this.findValue(this.validate, Boolean.class));
        }
        if (this.name == null && StringUtils.isNotEmpty((CharSequence)(id = (String)this.getParameters().get("id")))) {
            this.addParameter("name", id);
        }
        if (this.onsubmit != null) {
            this.addParameter("onsubmit", this.findString(this.onsubmit));
        }
        if (this.onreset != null) {
            this.addParameter("onreset", this.findString(this.onreset));
        }
        if (this.target != null) {
            this.addParameter("target", this.findString(this.target));
        }
        if (this.enctype != null) {
            this.addParameter("enctype", this.findString(this.enctype));
        }
        if (this.method != null) {
            this.addParameter("method", this.findString(this.method));
        }
        if (this.acceptcharset != null) {
            this.addParameter("acceptcharset", this.findString(this.acceptcharset));
        }
        if (!this.parameters.containsKey("tagNames")) {
            this.addParameter("tagNames", new ArrayList());
        }
        if (this.focusElement != null) {
            this.addParameter("focusElement", this.findString(this.focusElement));
        }
    }

    @Override
    protected void populateComponentHtmlId(Form form) {
        if (this.id != null) {
            super.populateComponentHtmlId(null);
        }
        this.urlRenderer.renderFormUrl(this);
    }

    protected void evaluateClientSideJsEnablement(String actionName, String namespace, String actionMethod) {
        Boolean validate = (Boolean)this.getParameters().get("validate");
        if (validate != null && validate.booleanValue()) {
            this.addParameter("performValidation", Boolean.FALSE);
            RuntimeConfiguration runtimeConfiguration = this.configuration.getRuntimeConfiguration();
            ActionConfig actionConfig = runtimeConfiguration.getActionConfig(namespace, actionName);
            if (actionConfig != null) {
                List<InterceptorMapping> interceptors = actionConfig.getInterceptors();
                for (InterceptorMapping interceptorMapping : interceptors) {
                    Set<String> includeMethods;
                    if (!ValidationInterceptor.class.isInstance(interceptorMapping.getInterceptor())) continue;
                    ValidationInterceptor validationInterceptor = (ValidationInterceptor)interceptorMapping.getInterceptor();
                    Set<String> excludeMethods = validationInterceptor.getExcludeMethodsSet();
                    if (MethodFilterInterceptorUtil.applyMethod(excludeMethods, includeMethods = validationInterceptor.getIncludeMethodsSet(), actionMethod)) {
                        this.addParameter("performValidation", Boolean.TRUE);
                    }
                    return;
                }
            }
        }
    }

    public List getValidators(String name) {
        Class actionClass = (Class)this.getParameters().get("actionClass");
        if (actionClass == null) {
            return Collections.EMPTY_LIST;
        }
        String formActionValue = this.findString(this.action);
        ActionMapping mapping = this.actionMapper.getMappingFromActionName(formActionValue);
        if (mapping == null) {
            mapping = this.actionMapper.getMappingFromActionName((String)this.getParameters().get("actionName"));
        }
        if (mapping == null) {
            return Collections.EMPTY_LIST;
        }
        String actionName = mapping.getName();
        String methodName = null;
        if (this.isValidateAnnotatedMethodOnly(actionName)) {
            methodName = mapping.getMethod();
        }
        List<Validator> actionValidators = this.actionValidatorManager.getValidators(actionClass, actionName, methodName);
        ArrayList<Validator> validators = new ArrayList<Validator>();
        this.findFieldValidators(name, actionClass, actionName, actionValidators, validators, "");
        return validators;
    }

    private boolean isValidateAnnotatedMethodOnly(String actionName) {
        String actionNamespace;
        RuntimeConfiguration runtimeConfiguration = this.configuration.getRuntimeConfiguration();
        ActionConfig actionConfig = runtimeConfiguration.getActionConfig(actionNamespace = this.getNamespace(this.stack), actionName);
        if (actionConfig != null) {
            List<InterceptorMapping> interceptors = actionConfig.getInterceptors();
            for (InterceptorMapping interceptorMapping : interceptors) {
                if (!ValidationInterceptor.class.isInstance(interceptorMapping.getInterceptor())) continue;
                ValidationInterceptor validationInterceptor = (ValidationInterceptor)interceptorMapping.getInterceptor();
                return validationInterceptor.isValidateAnnotatedMethodOnly();
            }
        }
        return false;
    }

    private void findFieldValidators(String name, Class actionClass, String actionName, List<Validator> validatorList, List<Validator> resultValidators, String prefix) {
        for (Validator validator : validatorList) {
            if (!(validator instanceof FieldValidator)) continue;
            FieldValidator fieldValidator = (FieldValidator)validator;
            if (validator instanceof VisitorFieldValidator) {
                VisitorFieldValidator vfValidator = (VisitorFieldValidator)fieldValidator;
                Class clazz = this.getVisitorReturnType(actionClass, vfValidator.getFieldName());
                if (clazz == null) continue;
                List<Validator> visitorValidators = this.actionValidatorManager.getValidators(clazz, actionName);
                String vPrefix = prefix + (vfValidator.isAppendPrefix() ? vfValidator.getFieldName() + "." : "");
                this.findFieldValidators(name, clazz, actionName, visitorValidators, resultValidators, vPrefix);
                continue;
            }
            if (!(prefix + fieldValidator.getFieldName()).equals(name)) continue;
            if (StringUtils.isNotBlank((CharSequence)prefix)) {
                FieldVisitorValidatorWrapper wrap = new FieldVisitorValidatorWrapper(fieldValidator, prefix);
                resultValidators.add(wrap);
                continue;
            }
            resultValidators.add(fieldValidator);
        }
    }

    protected Class getVisitorReturnType(Class actionClass, String visitorFieldName) {
        if (visitorFieldName == null) {
            return null;
        }
        String methodName = "get" + StringUtils.capitalize((String)visitorFieldName);
        try {
            Method method = actionClass.getMethod(methodName, new Class[0]);
            return method.getReturnType();
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    protected int getSequence() {
        return this.sequence++;
    }

    @StrutsTagAttribute(description="HTML onsubmit attribute")
    public void setOnsubmit(String onsubmit) {
        this.onsubmit = onsubmit;
    }

    @StrutsTagAttribute(description="HTML onreset attribute")
    public void setOnreset(String onreset) {
        this.onreset = onreset;
    }

    @StrutsTagAttribute(description="Set action name to submit to, without .action suffix", defaultValue="current action")
    public void setAction(String action) {
        this.action = action;
    }

    @StrutsTagAttribute(description="HTML form target attribute")
    public void setTarget(String target) {
        this.target = target;
    }

    @StrutsTagAttribute(description="HTML form enctype attribute")
    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }

    @StrutsTagAttribute(description="HTML form method attribute")
    public void setMethod(String method) {
        this.method = method;
    }

    @StrutsTagAttribute(description="Namespace for action to submit to", defaultValue="current namespace")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @StrutsTagAttribute(description="Whether client side/remote validation should be performed. Only useful with theme xhtml/ajax", type="Boolean", defaultValue="false")
    public void setValidate(String validate) {
        this.validate = validate;
    }

    @StrutsTagAttribute(description="The portlet mode to display after the form submit")
    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    @StrutsTagAttribute(description="The window state to display after the form submit")
    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    @StrutsTagAttribute(description="The accepted charsets for this form. The values may be comma or blank delimited.")
    public void setAcceptcharset(String acceptcharset) {
        this.acceptcharset = acceptcharset;
    }

    @StrutsTagAttribute(description="Id of element that will receive the focus when page loads.")
    public void setFocusElement(String focusElement) {
        this.focusElement = focusElement;
    }

    @StrutsTagAttribute(description="Whether actual context should be included in URL", type="Boolean", defaultValue="true")
    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
    }

    public static class FieldVisitorValidatorWrapper
    implements FieldValidator {
        private FieldValidator fieldValidator;
        private String namePrefix;

        public FieldVisitorValidatorWrapper(FieldValidator fv, String namePrefix) {
            this.fieldValidator = fv;
            this.namePrefix = namePrefix;
        }

        @Override
        public String getValidatorType() {
            return "field-visitor";
        }

        @Override
        public String getFieldName() {
            return this.namePrefix + this.fieldValidator.getFieldName();
        }

        public FieldValidator getFieldValidator() {
            return this.fieldValidator;
        }

        public void setFieldValidator(FieldValidator fieldValidator) {
            this.fieldValidator = fieldValidator;
        }

        @Override
        public String getDefaultMessage() {
            return this.fieldValidator.getDefaultMessage();
        }

        @Override
        public String getMessage(Object object) {
            return this.fieldValidator.getMessage(object);
        }

        @Override
        public String getMessageKey() {
            return this.fieldValidator.getMessageKey();
        }

        @Override
        public String[] getMessageParameters() {
            return this.fieldValidator.getMessageParameters();
        }

        @Override
        public ValidatorContext getValidatorContext() {
            return this.fieldValidator.getValidatorContext();
        }

        @Override
        public void setDefaultMessage(String message) {
            this.fieldValidator.setDefaultMessage(message);
        }

        @Override
        public void setFieldName(String fieldName) {
            this.fieldValidator.setFieldName(fieldName);
        }

        @Override
        public void setMessageKey(String key) {
            this.fieldValidator.setMessageKey(key);
        }

        @Override
        public void setMessageParameters(String[] messageParameters) {
            this.fieldValidator.setMessageParameters(messageParameters);
        }

        @Override
        public void setValidatorContext(ValidatorContext validatorContext) {
            this.fieldValidator.setValidatorContext(validatorContext);
        }

        @Override
        public void setValidatorType(String type) {
            this.fieldValidator.setValidatorType(type);
        }

        @Override
        public void setValueStack(ValueStack stack) {
            this.fieldValidator.setValueStack(stack);
        }

        @Override
        public void validate(Object object) throws ValidationException {
            this.fieldValidator.validate(object);
        }

        public String getNamePrefix() {
            return this.namePrefix;
        }

        public void setNamePrefix(String namePrefix) {
            this.namePrefix = namePrefix;
        }
    }
}

