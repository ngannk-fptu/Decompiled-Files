/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.baggage.BaggagePropagation
 *  brave.baggage.BaggagePropagation$FactoryBuilder
 *  brave.baggage.BaggagePropagationConfig
 *  brave.baggage.BaggagePropagationCustomizer
 *  brave.propagation.B3Propagation
 *  brave.propagation.Propagation$Factory
 *  org.springframework.beans.factory.FactoryBean
 */
package brave.spring.beans;

import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.baggage.BaggagePropagationCustomizer;
import brave.propagation.B3Propagation;
import brave.propagation.Propagation;
import java.util.List;
import org.springframework.beans.factory.FactoryBean;

public class BaggagePropagationFactoryBean
implements FactoryBean {
    Propagation.Factory propagationFactory = B3Propagation.FACTORY;
    List<BaggagePropagationConfig> configs;
    List<BaggagePropagationCustomizer> customizers;

    public Propagation.Factory getObject() {
        BaggagePropagation.FactoryBuilder builder = BaggagePropagation.newFactoryBuilder((Propagation.Factory)this.propagationFactory);
        if (this.configs != null) {
            builder.clear();
            for (BaggagePropagationConfig config : this.configs) {
                builder.add(config);
            }
        }
        if (this.customizers != null) {
            for (BaggagePropagationCustomizer customizer : this.customizers) {
                customizer.customize(builder);
            }
        }
        return builder.build();
    }

    public Class<? extends Propagation.Factory> getObjectType() {
        return Propagation.Factory.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setPropagationFactory(Propagation.Factory propagationFactory) {
        this.propagationFactory = propagationFactory;
    }

    public void setConfigs(List<BaggagePropagationConfig> configs) {
        this.configs = configs;
    }

    public void setCustomizers(List<BaggagePropagationCustomizer> customizers) {
        this.customizers = customizers;
    }
}

