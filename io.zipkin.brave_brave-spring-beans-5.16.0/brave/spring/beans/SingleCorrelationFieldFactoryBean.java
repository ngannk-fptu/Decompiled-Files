/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.baggage.BaggageField
 *  brave.baggage.CorrelationScopeConfig
 *  brave.baggage.CorrelationScopeConfig$SingleCorrelationField
 *  brave.baggage.CorrelationScopeConfig$SingleCorrelationField$Builder
 *  org.springframework.beans.factory.FactoryBean
 */
package brave.spring.beans;

import brave.baggage.BaggageField;
import brave.baggage.CorrelationScopeConfig;
import org.springframework.beans.factory.FactoryBean;

public class SingleCorrelationFieldFactoryBean
implements FactoryBean {
    BaggageField baggageField;
    String name;
    boolean dirty;
    boolean flushOnUpdate;

    public CorrelationScopeConfig.SingleCorrelationField getObject() {
        CorrelationScopeConfig.SingleCorrelationField.Builder builder = CorrelationScopeConfig.SingleCorrelationField.newBuilder((BaggageField)this.baggageField);
        if (this.name != null) {
            builder.name(this.name);
        }
        if (this.dirty) {
            builder.dirty();
        }
        if (this.flushOnUpdate) {
            builder.flushOnUpdate();
        }
        return builder.build();
    }

    public Class<? extends CorrelationScopeConfig> getObjectType() {
        return CorrelationScopeConfig.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setBaggageField(BaggageField baggageField) {
        this.baggageField = baggageField;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setFlushOnUpdate(boolean flushOnUpdate) {
        this.flushOnUpdate = flushOnUpdate;
    }
}

