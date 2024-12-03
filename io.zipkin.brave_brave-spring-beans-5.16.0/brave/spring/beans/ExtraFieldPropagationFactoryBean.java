/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.propagation.B3Propagation
 *  brave.propagation.ExtraFieldCustomizer
 *  brave.propagation.ExtraFieldPropagation
 *  brave.propagation.ExtraFieldPropagation$FactoryBuilder
 *  brave.propagation.Propagation$Factory
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.FactoryBean
 */
package brave.spring.beans;

import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldCustomizer;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.Propagation;
import brave.spring.beans.BaggagePropagationFactoryBean;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

@Deprecated
public class ExtraFieldPropagationFactoryBean
implements FactoryBean {
    static final Log logger = LogFactory.getLog(ExtraFieldPropagationFactoryBean.class);
    Propagation.Factory propagationFactory = B3Propagation.FACTORY;
    List<String> fields;
    List<ExtraFieldCustomizer> customizers;

    public Propagation.Factory getObject() {
        logger.warn((Object)("The factory '" + this.getClass().getName() + "' will be removed in a future release.\nUse '" + BaggagePropagationFactoryBean.class.getName() + "' instead"));
        ExtraFieldPropagation.FactoryBuilder builder = ExtraFieldPropagation.newFactoryBuilder((Propagation.Factory)this.propagationFactory);
        if (this.fields != null) {
            for (String field : this.fields) {
                builder.addField(field);
            }
        }
        if (this.customizers != null) {
            for (ExtraFieldCustomizer customizer : this.customizers) {
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

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public void setCustomizers(List<ExtraFieldCustomizer> customizers) {
        this.customizers = customizers;
    }
}

