/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.AbstractLocalizedTextProvider;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlobalLocalizedTextProvider
extends AbstractLocalizedTextProvider {
    private static final Logger LOG = LogManager.getLogger(GlobalLocalizedTextProvider.class);

    public GlobalLocalizedTextProvider() {
        this.addDefaultResourceBundle("com/opensymphony/xwork2/xwork-messages");
        this.addDefaultResourceBundle("org/apache/struts2/struts-messages");
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
        AbstractLocalizedTextProvider.GetDefaultMessageReturnArg result;
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
        if (this.unableToFindTextForKey(result = this.getDefaultMessageWithAlternateKey(aTextName, indexedTextName, locale, valueStack, args, defaultMessage)) && LOG.isDebugEnabled()) {
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

