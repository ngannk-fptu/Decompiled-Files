/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.jexl2.Expression
 *  org.apache.commons.jexl2.JexlContext
 *  org.apache.commons.jexl2.JexlEngine
 *  org.apache.commons.jexl2.MapContext
 *  org.apache.commons.lang3.ClassUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.text.StringSubstitutor
 *  org.apache.commons.text.lookup.StringLookup
 */
package org.apache.commons.configuration2.interpol;

import java.util.ArrayList;
import java.util.Objects;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;

public class ExprLookup
implements Lookup {
    private static final String CLASS = "Class:";
    private static final String DEFAULT_PREFIX = "$[";
    private static final String DEFAULT_SUFFIX = "]";
    private ConfigurationInterpolator interpolator;
    private StringSubstitutor substitutor;
    private ConfigurationLogger logger;
    private final JexlEngine engine = new JexlEngine();
    private Variables variables;
    private String prefixMatcher = "$[";
    private String suffixMatcher = "]";

    public ExprLookup() {
    }

    public ExprLookup(Variables list) {
        this.setVariables(list);
    }

    public ExprLookup(Variables list, String prefix, String suffix) {
        this(list);
        this.setVariablePrefixMatcher(prefix);
        this.setVariableSuffixMatcher(suffix);
    }

    public void setVariablePrefixMatcher(String prefix) {
        this.prefixMatcher = prefix;
    }

    public void setVariableSuffixMatcher(String suffix) {
        this.suffixMatcher = suffix;
    }

    public void setVariables(Variables list) {
        this.variables = new Variables(list);
    }

    public Variables getVariables() {
        return new Variables(this.variables);
    }

    public ConfigurationLogger getLogger() {
        return this.logger;
    }

    public void setLogger(ConfigurationLogger logger) {
        this.logger = logger;
    }

    public ConfigurationInterpolator getInterpolator() {
        return this.interpolator;
    }

    public void setInterpolator(ConfigurationInterpolator interpolator) {
        this.interpolator = interpolator;
        this.installSubstitutor(interpolator);
    }

    @Override
    public String lookup(String var) {
        String result;
        block3: {
            if (this.substitutor == null) {
                return var;
            }
            result = this.substitutor.replace(var);
            try {
                Expression exp = this.engine.createExpression(result);
                Object exprResult = exp.evaluate(this.createContext());
                result = exprResult != null ? String.valueOf(exprResult) : null;
            }
            catch (Exception e) {
                ConfigurationLogger l = this.getLogger();
                if (l == null) break block3;
                l.debug("Error encountered evaluating " + result + ": " + e);
            }
        }
        return result;
    }

    private void installSubstitutor(ConfigurationInterpolator ip) {
        if (ip == null) {
            this.substitutor = null;
        } else {
            StringLookup variableResolver = key -> Objects.toString(ip.resolve(key), null);
            this.substitutor = new StringSubstitutor(variableResolver, this.prefixMatcher, this.suffixMatcher, '$');
        }
    }

    private JexlContext createContext() {
        MapContext ctx = new MapContext();
        this.initializeContext((JexlContext)ctx);
        return ctx;
    }

    private void initializeContext(JexlContext ctx) {
        this.variables.forEach(var -> ctx.set(var.getName(), var.getValue()));
    }

    public static class Variable {
        private String key;
        private Object value;

        public Variable() {
        }

        public Variable(String name, Object value) {
            this.setName(name);
            this.setValue(value);
        }

        public String getName() {
            return this.key;
        }

        public void setName(String name) {
            this.key = name;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) throws ConfigurationRuntimeException {
            try {
                if (!(value instanceof String)) {
                    this.value = value;
                    return;
                }
                String val = (String)value;
                String name = StringUtils.removeStartIgnoreCase((String)val, (String)ExprLookup.CLASS);
                Class clazz = ClassUtils.getClass((String)name);
                this.value = name.length() == val.length() ? clazz.newInstance() : clazz;
            }
            catch (Exception e) {
                throw new ConfigurationRuntimeException("Unable to create " + value, e);
            }
        }
    }

    public static class Variables
    extends ArrayList<Variable> {
        private static final long serialVersionUID = 20111205L;

        public Variables() {
        }

        public Variables(Variables vars) {
            super(vars);
        }

        public Variable getVariable() {
            return !this.isEmpty() ? (Variable)this.get(this.size() - 1) : null;
        }
    }
}

