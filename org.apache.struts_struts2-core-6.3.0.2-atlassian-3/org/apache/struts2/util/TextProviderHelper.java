/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.Collections;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextProviderHelper {
    private static final Logger LOG = LogManager.getLogger(TextProviderHelper.class);

    public static String getText(String key, String defaultMessage, List<Object> args, ValueStack stack) {
        String msg = null;
        TextProvider tp = null;
        for (Object o : stack.getRoot()) {
            if (!(o instanceof TextProvider)) continue;
            tp = (TextProvider)o;
            msg = tp.getText(key, null, args, stack);
            break;
        }
        if (msg == null) {
            msg = defaultMessage;
            msg = StringEscapeUtils.escapeHtml4((String)msg);
            msg = StringEscapeUtils.escapeEcmaScript((String)msg);
            LOG.debug("Message for key '{}' is null, returns escaped default message [{}]", (Object)key, (Object)msg);
            if (LOG.isWarnEnabled()) {
                if (tp != null) {
                    LOG.warn("The first TextProvider in the ValueStack ({}) could not locate the message resource with key '{}'", (Object)tp.getClass().getName(), (Object)key);
                } else {
                    LOG.warn("Could not locate the message resource '{}' as there is no TextProvider in the ValueStack.", (Object)key);
                }
            }
        }
        return msg;
    }

    public static String getText(String key, String defaultMessage, ValueStack stack) {
        return TextProviderHelper.getText(key, defaultMessage, Collections.emptyList(), stack);
    }
}

