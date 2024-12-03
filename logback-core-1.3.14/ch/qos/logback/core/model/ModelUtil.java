/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.model;

import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Properties;

public class ModelUtil {
    public static void resetForReuse(Model model) {
        if (model == null) {
            return;
        }
        model.resetForReuse();
    }

    public static void setProperty(ModelInterpretationContext mic, String key, String value, ActionUtil.Scope scope) {
        switch (scope) {
            case LOCAL: {
                mic.addSubstitutionProperty(key, value);
                break;
            }
            case CONTEXT: {
                mic.getContext().putProperty(key, value);
                break;
            }
            case SYSTEM: {
                OptionHelper.setSystemProperty(mic, key, value);
            }
        }
    }

    public static void setProperties(ModelInterpretationContext ic, Properties props, ActionUtil.Scope scope) {
        switch (scope) {
            case LOCAL: {
                ic.addSubstitutionProperties(props);
                break;
            }
            case CONTEXT: {
                ContextUtil cu = new ContextUtil(ic.getContext());
                cu.addProperties(props);
                break;
            }
            case SYSTEM: {
                OptionHelper.setSystemProperties(ic, props);
            }
        }
    }
}

