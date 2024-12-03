/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.TextParser;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.util.Map;

public interface WithLazyParams {

    public static class LazyParamInjector {
        protected OgnlUtil ognlUtil;
        protected TextParser textParser;
        protected ReflectionProvider reflectionProvider;
        private final TextParseUtil.ParsedValueEvaluator valueEvaluator;

        public LazyParamInjector(final ValueStack valueStack) {
            this.valueEvaluator = new TextParseUtil.ParsedValueEvaluator(){

                @Override
                public Object evaluate(String parsedValue) {
                    return valueStack.findValue(parsedValue);
                }
            };
        }

        @Inject
        public void setTextParser(TextParser textParser) {
            this.textParser = textParser;
        }

        @Inject
        public void setReflectionProvider(ReflectionProvider reflectionProvider) {
            this.reflectionProvider = reflectionProvider;
        }

        @Inject
        public void setOgnlUtil(OgnlUtil ognlUtil) {
            this.ognlUtil = ognlUtil;
        }

        public Interceptor injectParams(Interceptor interceptor, Map<String, String> params, ActionContext invocationContext) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                Object paramValue = this.textParser.evaluate(new char[]{'$'}, entry.getValue(), this.valueEvaluator, 1);
                this.ognlUtil.setProperty(entry.getKey(), paramValue, interceptor, invocationContext.getContextMap());
            }
            return interceptor;
        }
    }
}

