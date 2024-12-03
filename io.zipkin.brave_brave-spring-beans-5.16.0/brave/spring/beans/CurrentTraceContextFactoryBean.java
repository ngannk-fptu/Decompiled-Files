/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.propagation.CurrentTraceContext
 *  brave.propagation.CurrentTraceContext$Builder
 *  brave.propagation.CurrentTraceContext$ScopeDecorator
 *  brave.propagation.CurrentTraceContextCustomizer
 *  brave.propagation.ThreadLocalCurrentTraceContext
 *  brave.propagation.ThreadLocalCurrentTraceContext$Builder
 *  org.springframework.beans.factory.FactoryBean
 */
package brave.spring.beans;

import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContextCustomizer;
import brave.propagation.ThreadLocalCurrentTraceContext;
import java.util.List;
import org.springframework.beans.factory.FactoryBean;

public class CurrentTraceContextFactoryBean
implements FactoryBean {
    List<CurrentTraceContextCustomizer> customizers;
    List<CurrentTraceContext.ScopeDecorator> scopeDecorators;

    public CurrentTraceContext getObject() {
        ThreadLocalCurrentTraceContext.Builder builder = ThreadLocalCurrentTraceContext.newBuilder();
        if (this.scopeDecorators != null) {
            for (CurrentTraceContext.ScopeDecorator scopeDecorator : this.scopeDecorators) {
                builder.addScopeDecorator(scopeDecorator);
            }
        }
        if (this.customizers != null) {
            for (CurrentTraceContextCustomizer customizer : this.customizers) {
                customizer.customize((CurrentTraceContext.Builder)builder);
            }
        }
        return builder.build();
    }

    public Class<? extends CurrentTraceContext> getObjectType() {
        return CurrentTraceContext.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setScopeDecorators(List<CurrentTraceContext.ScopeDecorator> scopeDecorators) {
        this.scopeDecorators = scopeDecorators;
    }

    public void setCustomizers(List<CurrentTraceContextCustomizer> customizers) {
        this.customizers = customizers;
    }
}

