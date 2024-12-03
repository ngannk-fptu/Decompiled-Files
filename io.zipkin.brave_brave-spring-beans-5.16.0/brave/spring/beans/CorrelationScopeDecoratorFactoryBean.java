/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.baggage.CorrelationScopeConfig
 *  brave.baggage.CorrelationScopeCustomizer
 *  brave.baggage.CorrelationScopeDecorator$Builder
 *  brave.propagation.CurrentTraceContext$ScopeDecorator
 *  org.springframework.beans.factory.FactoryBean
 */
package brave.spring.beans;

import brave.baggage.CorrelationScopeConfig;
import brave.baggage.CorrelationScopeCustomizer;
import brave.baggage.CorrelationScopeDecorator;
import brave.propagation.CurrentTraceContext;
import java.util.List;
import org.springframework.beans.factory.FactoryBean;

public class CorrelationScopeDecoratorFactoryBean
implements FactoryBean {
    CorrelationScopeDecorator.Builder builder;
    List<CorrelationScopeConfig> configs;
    List<CorrelationScopeCustomizer> customizers;

    public CurrentTraceContext.ScopeDecorator getObject() {
        if (this.builder == null) {
            throw new NullPointerException("builder == null");
        }
        if (this.configs != null) {
            this.builder.clear();
            for (CorrelationScopeConfig config : this.configs) {
                this.builder.add(config);
            }
        }
        if (this.customizers != null) {
            for (CorrelationScopeCustomizer customizer : this.customizers) {
                customizer.customize(this.builder);
            }
        }
        return this.builder.build();
    }

    public Class<? extends CurrentTraceContext.ScopeDecorator> getObjectType() {
        return CurrentTraceContext.ScopeDecorator.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setBuilder(CorrelationScopeDecorator.Builder builder) {
        this.builder = builder;
    }

    public void setConfigs(List<CorrelationScopeConfig> configs) {
        this.configs = configs;
    }

    public void setCustomizers(List<CorrelationScopeCustomizer> customizers) {
        this.customizers = customizers;
    }
}

