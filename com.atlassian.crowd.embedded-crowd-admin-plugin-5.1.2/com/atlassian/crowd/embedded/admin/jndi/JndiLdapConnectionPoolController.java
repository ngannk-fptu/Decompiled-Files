/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.ldap.LdapPoolType
 *  com.atlassian.crowd.embedded.api.ConnectionPoolProperties
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.DefaultConnectionPoolProperties
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.DirectoryQueries
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Controller
 *  org.springframework.ui.ModelMap
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.FieldError
 *  org.springframework.validation.ObjectError
 *  org.springframework.validation.Validator
 *  org.springframework.web.bind.WebDataBinder
 *  org.springframework.web.bind.annotation.InitBinder
 *  org.springframework.web.bind.annotation.ModelAttribute
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.servlet.ModelAndView
 */
package com.atlassian.crowd.embedded.admin.jndi;

import com.atlassian.crowd.directory.ldap.LdapPoolType;
import com.atlassian.crowd.embedded.admin.jndi.JndiLdapConnectionPoolPropertiesValidator;
import com.atlassian.crowd.embedded.admin.util.HtmlEncoder;
import com.atlassian.crowd.embedded.api.ConnectionPoolProperties;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.DefaultConnectionPoolProperties;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.DirectoryQueries;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value={"/configure/connection-pool/**"})
public class JndiLdapConnectionPoolController {
    public static final String CONNECTION_POOL_FORM_VIEW = "connection-pool-form";
    public static final String CONNECTION_POOL_PROPERTIES = "poolProperties";
    public static final String JNDI_DIRECTORIES = "jndiDirectories";
    @Autowired
    private CrowdDirectoryService crowdDirectoryService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private I18nResolver i18nResolver;
    @Autowired
    private HtmlEncoder htmlEncoder;
    @Autowired
    private DirectoryManager directoryManager;

    @InitBinder(value={"poolProperties"})
    protected void initConnectionPoolPropertiesValidator(WebDataBinder binder) {
        binder.setValidator((Validator)new JndiLdapConnectionPoolPropertiesValidator());
    }

    @ModelAttribute(value="systemPoolProperties")
    public ConnectionPoolProperties getSystemPoolProperties() {
        return this.crowdDirectoryService.getSystemConnectionPoolProperties();
    }

    @RequestMapping(method={RequestMethod.GET})
    public String initializeForm(ModelMap model) {
        model.addAttribute(CONNECTION_POOL_PROPERTIES, (Object)this.crowdDirectoryService.getStoredConnectionPoolProperties());
        model.addAttribute(JNDI_DIRECTORIES, this.getJndiDirectories());
        return CONNECTION_POOL_FORM_VIEW;
    }

    @RequestMapping(method={RequestMethod.POST})
    public ModelAndView saveSettings(@Valid @ModelAttribute(value="poolProperties") DefaultConnectionPoolProperties poolProperties, BindingResult errors) {
        Map model = errors.getModel();
        if (errors.hasErrors()) {
            this.revertInvalidValues(poolProperties, errors);
        } else {
            this.saveLdapProperties((ConnectionPoolProperties)poolProperties);
            model.put("saveSuccessful", true);
        }
        model.put(JNDI_DIRECTORIES, this.getJndiDirectories());
        return new ModelAndView(CONNECTION_POOL_FORM_VIEW, model);
    }

    private void saveLdapProperties(ConnectionPoolProperties poolProperties) {
        this.transactionTemplate.execute(() -> {
            this.crowdDirectoryService.setConnectionPoolProperties(poolProperties);
            return null;
        });
    }

    private void revertInvalidValues(DefaultConnectionPoolProperties properties, BindingResult errors) {
        ConnectionPoolProperties storedConfiguration = this.crowdDirectoryService.getStoredConnectionPoolProperties();
        errors.getFieldErrors().stream().map(FieldError::getField).forEach(errField -> {
            switch (errField) {
                case "initialSize": {
                    properties.setInitialSize(storedConfiguration.getInitialSize());
                    break;
                }
                case "preferredSize": {
                    properties.setPreferredSize(storedConfiguration.getPreferredSize());
                    break;
                }
                case "maximumSize": {
                    properties.setMaximumSize(storedConfiguration.getMaximumSize());
                    break;
                }
                case "timeoutInSec": {
                    properties.setTimeoutInSec(storedConfiguration.getTimeoutInSec());
                    break;
                }
                case "supportedProtocol": {
                    properties.setSupportedProtocol(storedConfiguration.getSupportedProtocol());
                    break;
                }
                case "supportedAuthentication": {
                    properties.setSupportedAuthentication(storedConfiguration.getSupportedAuthentication());
                    break;
                }
            }
        });
        errors.addError(new ObjectError(CONNECTION_POOL_PROPERTIES, this.i18nResolver.getText("embedded.crowd.connection.pool.save.fail")));
    }

    @ModelAttribute(value="htmlEncoder")
    public HtmlEncoder getHtmlEncoder() {
        return this.htmlEncoder;
    }

    private List<Directory> getJndiDirectories() {
        return this.directoryManager.searchDirectories(DirectoryQueries.allDirectories().withSearchRestriction((SearchRestriction)Combine.allOf((SearchRestriction[])new SearchRestriction[]{Restriction.on((Property)DirectoryTermKeys.TYPE).exactlyMatchingAny((Collection)ImmutableSet.of((Object)DirectoryType.CONNECTOR, (Object)DirectoryType.DELEGATING)), Restriction.on((Property)DirectoryTermKeys.ACTIVE).exactlyMatching((Object)true)}))).stream().filter(directory -> LdapPoolType.fromString((String)((String)directory.getAttributes().get("ldap.pool.type"))) == LdapPoolType.JNDI).collect(Collectors.toList());
    }
}

