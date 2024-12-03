/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.ParameterAction;
import org.apache.struts2.util.PrefixTrie;

public class DefaultActionMapper
implements ActionMapper {
    private static final Logger LOG = LogManager.getLogger(DefaultActionMapper.class);
    protected static final String METHOD_PREFIX = "method:";
    protected static final String ACTION_PREFIX = "action:";
    protected boolean allowDynamicMethodCalls = false;
    protected boolean allowSlashesInActionNames = false;
    protected boolean alwaysSelectFullNamespace = false;
    protected PrefixTrie prefixTrie;
    protected Pattern allowedNamespaceNames = Pattern.compile("[a-zA-Z0-9._/\\-]*");
    protected String defaultNamespaceName = "/";
    protected Pattern allowedActionNames = Pattern.compile("[a-zA-Z0-9._!/\\-]*");
    protected String defaultActionName = "index";
    protected Pattern allowedMethodNames = Pattern.compile("[a-zA-Z_]*[0-9]*");
    protected String defaultMethodName = "execute";
    private boolean allowActionPrefix = false;
    protected List<String> extensions = new ArrayList<String>(){
        {
            this.add("action");
            this.add("");
        }
    };
    protected Container container;

    public DefaultActionMapper() {
        this.prefixTrie = new PrefixTrie(){
            {
                this.put(DefaultActionMapper.METHOD_PREFIX, (key, mapping) -> {
                    if (DefaultActionMapper.this.allowDynamicMethodCalls) {
                        mapping.setMethod(DefaultActionMapper.this.cleanupMethodName(key.substring(DefaultActionMapper.METHOD_PREFIX.length())));
                    }
                });
                this.put(DefaultActionMapper.ACTION_PREFIX, (key, mapping) -> {
                    if (DefaultActionMapper.this.allowActionPrefix) {
                        int bang;
                        String name = key.substring(DefaultActionMapper.ACTION_PREFIX.length());
                        if (DefaultActionMapper.this.allowDynamicMethodCalls && (bang = name.indexOf(33)) != -1) {
                            String method = DefaultActionMapper.this.cleanupMethodName(name.substring(bang + 1));
                            mapping.setMethod(method);
                            name = name.substring(0, bang);
                        }
                        String actionName = DefaultActionMapper.this.cleanupActionName(name);
                        if (DefaultActionMapper.this.allowSlashesInActionNames && actionName.startsWith("/")) {
                            actionName = actionName.substring(1);
                        }
                        if (!DefaultActionMapper.this.allowSlashesInActionNames && actionName.lastIndexOf(47) != -1) {
                            actionName = actionName.substring(actionName.lastIndexOf(47) + 1);
                        }
                        mapping.setName(actionName);
                    }
                });
            }
        };
    }

    protected void addParameterAction(String prefix, ParameterAction parameterAction) {
        this.prefixTrie.put(prefix, parameterAction);
    }

    @Inject(value="struts.enable.DynamicMethodInvocation")
    public void setAllowDynamicMethodCalls(String enableDynamicMethodCalls) {
        this.allowDynamicMethodCalls = BooleanUtils.toBoolean((String)enableDynamicMethodCalls);
    }

    @Inject(value="struts.enable.SlashesInActionNames")
    public void setSlashesInActionNames(String enableSlashesInActionNames) {
        this.allowSlashesInActionNames = BooleanUtils.toBoolean((String)enableSlashesInActionNames);
    }

    @Inject(value="struts.mapper.alwaysSelectFullNamespace")
    public void setAlwaysSelectFullNamespace(String alwaysSelectFullNamespace) {
        this.alwaysSelectFullNamespace = BooleanUtils.toBoolean((String)alwaysSelectFullNamespace);
    }

    @Inject(value="struts.allowed.namespace.names", required=false)
    public void setAllowedNamespaceNames(String allowedNamespaceNames) {
        this.allowedNamespaceNames = Pattern.compile(allowedNamespaceNames);
    }

    @Inject(value="struts.default.namespace.name", required=false)
    public void setDefaultNamespaceName(String defaultNamespaceName) {
        this.defaultNamespaceName = defaultNamespaceName;
    }

    @Inject(value="struts.allowed.action.names", required=false)
    public void setAllowedActionNames(String allowedActionNames) {
        this.allowedActionNames = Pattern.compile(allowedActionNames);
    }

    @Inject(value="struts.default.action.name", required=false)
    public void setDefaultActionName(String defaultActionName) {
        this.defaultActionName = defaultActionName;
    }

    @Inject(value="struts.allowed.method.names", required=false)
    public void setAllowedMethodNames(String allowedMethodNames) {
        this.allowedMethodNames = Pattern.compile(allowedMethodNames);
    }

    @Inject(value="struts.default.method.name", required=false)
    public void setDefaultMethodName(String defaultMethodName) {
        this.defaultMethodName = defaultMethodName;
    }

    @Inject(value="struts.mapper.action.prefix.enabled")
    public void setAllowActionPrefix(String allowActionPrefix) {
        this.allowActionPrefix = BooleanUtils.toBoolean((String)allowActionPrefix);
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(value="struts.action.extension")
    public void setExtensions(String extensions) {
        if (StringUtils.isNotEmpty((CharSequence)extensions)) {
            ArrayList<String> list = new ArrayList<String>();
            String[] tokens = extensions.split(",");
            Collections.addAll(list, tokens);
            if (extensions.endsWith(",")) {
                list.add("");
            }
            this.extensions = Collections.unmodifiableList(list);
        } else {
            this.extensions = null;
        }
    }

    @Override
    public ActionMapping getMappingFromActionName(String actionName) {
        ActionMapping mapping = new ActionMapping();
        mapping.setName(actionName);
        return this.parseActionName(mapping);
    }

    public boolean isSlashesInActionNames() {
        return this.allowSlashesInActionNames;
    }

    @Override
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        ActionMapping mapping = new ActionMapping();
        String uri = RequestUtils.getUri(request);
        int indexOfSemicolon = uri.indexOf(59);
        uri = indexOfSemicolon > -1 ? uri.substring(0, indexOfSemicolon) : uri;
        if ((uri = this.dropExtension(uri, mapping)) == null) {
            return null;
        }
        this.parseNameAndNamespace(uri, mapping, configManager);
        this.handleSpecialParameters(request, mapping);
        this.extractMethodName(mapping, configManager);
        return this.parseActionName(mapping);
    }

    protected ActionMapping parseActionName(ActionMapping mapping) {
        String name;
        int exclamation;
        if (mapping.getName() == null) {
            return null;
        }
        if (this.allowDynamicMethodCalls && (exclamation = (name = mapping.getName()).lastIndexOf(33)) != -1) {
            mapping.setName(name.substring(0, exclamation));
            mapping.setMethod(name.substring(exclamation + 1));
        }
        return mapping;
    }

    public void handleSpecialParameters(HttpServletRequest request, ActionMapping mapping) {
        HashSet<String> uniqueParameters = new HashSet<String>();
        Map parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {
            ParameterAction parameterAction;
            if (key.endsWith(".x") || key.endsWith(".y")) {
                key = key.substring(0, key.length() - 2);
            }
            if (uniqueParameters.contains(key) || (parameterAction = (ParameterAction)this.prefixTrie.get(key)) == null) continue;
            parameterAction.execute(key, mapping);
            uniqueParameters.add(key);
            break;
        }
    }

    protected void parseNameAndNamespace(String uri, ActionMapping mapping, ConfigurationManager configManager) {
        int pos;
        String actionName;
        String actionNamespace;
        int lastSlash = uri.lastIndexOf(47);
        if (lastSlash == -1) {
            actionNamespace = "";
            actionName = uri;
        } else if (lastSlash == 0) {
            actionNamespace = "/";
            actionName = uri.substring(lastSlash + 1);
        } else if (this.alwaysSelectFullNamespace) {
            actionNamespace = uri.substring(0, lastSlash);
            actionName = uri.substring(lastSlash + 1);
        } else {
            Configuration config = configManager.getConfiguration();
            String prefix = uri.substring(0, lastSlash);
            actionNamespace = "";
            boolean rootAvailable = false;
            for (PackageConfig cfg : config.getPackageConfigs().values()) {
                String ns = cfg.getNamespace();
                if (ns != null && prefix.startsWith(ns) && (prefix.length() == ns.length() || prefix.charAt(ns.length()) == '/') && ns.length() > actionNamespace.length()) {
                    actionNamespace = ns;
                }
                if (!"/".equals(ns)) continue;
                rootAvailable = true;
            }
            actionName = uri.substring(actionNamespace.length() + 1);
            if (rootAvailable && "".equals(actionNamespace)) {
                actionNamespace = "/";
            }
        }
        if (!this.allowSlashesInActionNames && (pos = actionName.lastIndexOf(47)) > -1 && pos < actionName.length() - 1) {
            actionName = actionName.substring(pos + 1);
        }
        mapping.setNamespace(this.cleanupNamespaceName(actionNamespace));
        mapping.setName(this.cleanupActionName(actionName));
    }

    protected String cleanupNamespaceName(String rawNamespace) {
        if (this.allowedNamespaceNames.matcher(rawNamespace).matches()) {
            return rawNamespace;
        }
        LOG.warn("{} did not match allowed namespace names {} - default namespace {} will be used!", (Object)rawNamespace, (Object)this.allowedNamespaceNames, (Object)this.defaultNamespaceName);
        return this.defaultNamespaceName;
    }

    protected String cleanupActionName(String rawActionName) {
        if (this.allowedActionNames.matcher(rawActionName).matches()) {
            return rawActionName;
        }
        LOG.warn("{} did not match allowed action names {} - default action {} will be used!", (Object)rawActionName, (Object)this.allowedActionNames, (Object)this.defaultActionName);
        return this.defaultActionName;
    }

    protected String cleanupMethodName(String rawMethodName) {
        if (this.allowedMethodNames.matcher(rawMethodName).matches()) {
            return rawMethodName;
        }
        LOG.warn("{} did not match allowed method names {} - default method {} will be used!", (Object)rawMethodName, (Object)this.allowedMethodNames, (Object)this.defaultMethodName);
        return this.defaultMethodName;
    }

    protected void extractMethodName(ActionMapping mapping, ConfigurationManager configurationManager) {
        if (mapping.getMethod() != null && this.allowDynamicMethodCalls) {
            LOG.debug("DMI is enabled and method has been already mapped based on bang operator");
            return;
        }
        String methodName = null;
        for (PackageConfig cfg : configurationManager.getConfiguration().getPackageConfigs().values()) {
            if (!cfg.getNamespace().equals(mapping.getNamespace())) continue;
            ActionConfig actionCfg = cfg.getActionConfigs().get(mapping.getName());
            if (actionCfg != null) {
                methodName = actionCfg.getMethodName();
                LOG.trace("Using method: {} for action mapping: {}", (Object)methodName, (Object)mapping);
                break;
            }
            LOG.debug("No action config for action mapping: {}", (Object)mapping);
            break;
        }
        mapping.setMethod(methodName);
    }

    protected String dropExtension(String name, ActionMapping mapping) {
        if (this.extensions == null) {
            return name;
        }
        for (String ext : this.extensions) {
            if ("".equals(ext)) {
                int index = name.lastIndexOf(46);
                if (index != -1 && name.indexOf(47, index) < 0) continue;
                return name;
            }
            String extension = "." + ext;
            if (!name.endsWith(extension)) continue;
            name = name.substring(0, name.length() - extension.length());
            mapping.setExtension(ext);
            return name;
        }
        return null;
    }

    protected String getDefaultExtension() {
        if (this.extensions == null) {
            return null;
        }
        return this.extensions.get(0);
    }

    @Override
    public String getUriFromActionMapping(ActionMapping mapping) {
        StringBuilder uri = new StringBuilder();
        this.handleNamespace(mapping, uri);
        this.handleName(mapping, uri);
        this.handleDynamicMethod(mapping, uri);
        this.handleExtension(mapping, uri);
        this.handleParams(mapping, uri);
        return uri.toString();
    }

    protected void handleNamespace(ActionMapping mapping, StringBuilder uri) {
        if (mapping.getNamespace() != null) {
            uri.append(mapping.getNamespace());
            if (!"/".equals(mapping.getNamespace())) {
                uri.append("/");
            }
        }
    }

    protected void handleName(ActionMapping mapping, StringBuilder uri) {
        String name = mapping.getName();
        if (name.indexOf(63) != -1) {
            name = name.substring(0, name.indexOf(63));
        }
        uri.append(name);
    }

    protected void handleDynamicMethod(ActionMapping mapping, StringBuilder uri) {
        if (!this.allowDynamicMethodCalls) {
            LOG.debug("DMI is disabled, ignoring appending !method to the URI");
            return;
        }
        if (StringUtils.isNotEmpty((CharSequence)mapping.getMethod())) {
            String name = mapping.getName();
            if (!name.contains("!")) {
                uri.append("!").append(mapping.getMethod());
            } else if (name.endsWith("!")) {
                uri.append(mapping.getMethod());
            }
        }
    }

    protected void handleExtension(ActionMapping mapping, StringBuilder uri) {
        String extension = this.lookupExtension(mapping.getExtension());
        if (extension != null && (extension.length() == 0 || uri.indexOf('.' + extension) == -1) && extension.length() > 0) {
            uri.append(".").append(extension);
        }
    }

    protected String lookupExtension(String extension) {
        if (extension == null) {
            ActionMapping orig;
            ActionContext context = ActionContext.getContext();
            if (context != null && (orig = context.getActionMapping()) != null) {
                extension = orig.getExtension();
            }
            if (extension == null) {
                extension = this.getDefaultExtension();
            }
        }
        return extension;
    }

    protected void handleParams(ActionMapping mapping, StringBuilder uri) {
        String name = mapping.getName();
        String params = "";
        if (name.indexOf(63) != -1) {
            params = name.substring(name.indexOf(63));
        }
        if (params.length() > 0) {
            uri.append(params);
        }
    }
}

