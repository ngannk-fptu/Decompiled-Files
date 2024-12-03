/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.util.AbstractLocalizedTextProvider;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProviderFactory;
import java.beans.PropertyDescriptor;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrutsLocalizedTextProvider
extends AbstractLocalizedTextProvider {
    private static final Logger LOG = LogManager.getLogger(StrutsLocalizedTextProvider.class);

    @Deprecated
    public static void clearDefaultResourceBundles() {
    }

    public StrutsLocalizedTextProvider() {
        this.addDefaultResourceBundle("com/opensymphony/xwork2/xwork-messages");
        this.addDefaultResourceBundle("org/apache/struts2/struts-messages");
    }

    @Deprecated
    public static Locale localeFromString(String localeStr, Locale defaultLocale) {
        if (localeStr == null || localeStr.trim().length() == 0 || "_".equals(localeStr)) {
            if (defaultLocale != null) {
                return defaultLocale;
            }
            return Locale.getDefault();
        }
        int index = localeStr.indexOf(95);
        if (index < 0) {
            return new Locale(localeStr);
        }
        String language = localeStr.substring(0, index);
        if (index == localeStr.length()) {
            return new Locale(language);
        }
        if ((index = (localeStr = localeStr.substring(index + 1)).indexOf(95)) < 0) {
            return new Locale(language, localeStr);
        }
        String country = localeStr.substring(0, index);
        if (index == localeStr.length()) {
            return new Locale(language, country);
        }
        localeStr = localeStr.substring(index + 1);
        return new Locale(language, country, localeStr);
    }

    @Override
    public String findText(Class aClass, String aTextName, Locale locale) {
        return this.findText(aClass, aTextName, locale, aTextName, new Object[0]);
    }

    @Override
    public String findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args) {
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        return this.findText(aClass, aTextName, locale, defaultMessage, args, valueStack);
    }

    @Override
    public String findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args, ValueStack valueStack) {
        Object model;
        Object action;
        ActionContext context;
        ActionInvocation actionInvocation;
        String msg;
        String indexedTextName = null;
        if (aTextName == null) {
            LOG.warn("Trying to find text with null key!");
            aTextName = "";
        }
        if (aTextName.contains("[")) {
            int i = -1;
            indexedTextName = aTextName;
            while ((i = indexedTextName.indexOf(91, i + 1)) != -1) {
                int j = indexedTextName.indexOf(93, i);
                String a = indexedTextName.substring(0, i);
                String b = indexedTextName.substring(j);
                indexedTextName = a + "[*" + b;
            }
        }
        boolean performedInitialDefaultBundlesMessageLookup = false;
        AbstractLocalizedTextProvider.GetDefaultMessageReturnArg result = null;
        if (this.searchDefaultBundlesFirst) {
            result = this.getDefaultMessageWithAlternateKey(aTextName, indexedTextName, locale, valueStack, args, defaultMessage);
            performedInitialDefaultBundlesMessageLookup = true;
            if (!this.unableToFindTextForKey(result)) {
                return result.message;
            }
        }
        if ((msg = this.findMessage(aClass, aTextName, indexedTextName, locale, args, null, valueStack)) != null) {
            return msg;
        }
        if (ModelDriven.class.isAssignableFrom(aClass) && (actionInvocation = (context = ActionContext.getContext()).getActionInvocation()) != null && (action = actionInvocation.getAction()) instanceof ModelDriven && (model = ((ModelDriven)action).getModel()) != null && (msg = this.findMessage(model.getClass(), aTextName, indexedTextName, locale, args, null, valueStack)) != null) {
            return msg;
        }
        for (Class clazz = aClass; clazz != null && !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
            String basePackageName = clazz.getName();
            while (basePackageName.lastIndexOf(46) != -1) {
                String packageName = (basePackageName = basePackageName.substring(0, basePackageName.lastIndexOf(46))) + ".package";
                msg = this.getMessage(packageName, locale, aTextName, valueStack, args);
                if (msg != null) {
                    return msg;
                }
                if (indexedTextName == null || (msg = this.getMessage(packageName, locale, indexedTextName, valueStack, args)) == null) continue;
                return msg;
            }
        }
        int idx = aTextName.indexOf(46);
        if (idx != -1) {
            String newKey = null;
            String prop = null;
            if (aTextName.startsWith("invalid.fieldvalue.")) {
                idx = aTextName.indexOf(46, "invalid.fieldvalue.".length());
                if (idx != -1) {
                    prop = aTextName.substring("invalid.fieldvalue.".length(), idx);
                    newKey = "invalid.fieldvalue." + aTextName.substring(idx + 1);
                }
            } else {
                prop = aTextName.substring(0, idx);
                newKey = aTextName.substring(idx + 1);
            }
            if (prop != null) {
                Object obj = valueStack.findValue(prop);
                try {
                    Class<?> clazz;
                    PropertyDescriptor propertyDescriptor;
                    Object actionObj = ReflectionProviderFactory.getInstance().getRealTarget(prop, valueStack.getContext(), valueStack.getRoot());
                    if (actionObj != null && (propertyDescriptor = ReflectionProviderFactory.getInstance().getPropertyDescriptor(actionObj.getClass(), prop)) != null && (clazz = propertyDescriptor.getPropertyType()) != null) {
                        if (obj != null) {
                            valueStack.push(obj);
                        }
                        msg = this.findText(clazz, newKey, locale, null, args);
                        if (obj != null) {
                            valueStack.pop();
                        }
                        if (msg != null) {
                            return msg;
                        }
                    }
                }
                catch (Exception e) {
                    LOG.debug("unable to find property {}", (Object)prop, (Object)e);
                }
            }
        }
        if (!performedInitialDefaultBundlesMessageLookup) {
            result = this.getDefaultMessageWithAlternateKey(aTextName, indexedTextName, locale, valueStack, args, defaultMessage);
        }
        if (this.unableToFindTextForKey(result) && LOG.isDebugEnabled()) {
            String warn = "Unable to find text for key '" + aTextName + "' ";
            if (indexedTextName != null) {
                warn = warn + " or indexed key '" + indexedTextName + "' ";
            }
            warn = warn + "in class '" + aClass.getName() + "' and locale '" + locale + "'";
            LOG.debug(warn);
        }
        return result != null ? result.message : null;
    }

    @Override
    public String findText(ResourceBundle bundle, String aTextName, Locale locale) {
        return this.findText(bundle, aTextName, locale, aTextName, new Object[0]);
    }

    @Override
    public String findText(ResourceBundle bundle, String aTextName, Locale locale, String defaultMessage, Object[] args) {
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        return this.findText(bundle, aTextName, locale, defaultMessage, args, valueStack);
    }
}

