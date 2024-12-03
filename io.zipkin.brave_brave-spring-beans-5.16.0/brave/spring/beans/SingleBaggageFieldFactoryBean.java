/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.baggage.BaggageField
 *  brave.baggage.BaggagePropagationConfig
 *  brave.baggage.BaggagePropagationConfig$SingleBaggageField
 *  brave.baggage.BaggagePropagationConfig$SingleBaggageField$Builder
 *  org.springframework.beans.factory.FactoryBean
 */
package brave.spring.beans;

import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagationConfig;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.FactoryBean;

public class SingleBaggageFieldFactoryBean
implements FactoryBean {
    BaggageField field;
    List<String> keyNames = Collections.emptyList();

    public BaggagePropagationConfig.SingleBaggageField getObject() {
        BaggagePropagationConfig.SingleBaggageField.Builder builder = BaggagePropagationConfig.SingleBaggageField.newBuilder((BaggageField)this.field);
        if (this.keyNames != null) {
            for (String keyName : this.keyNames) {
                builder.addKeyName(keyName);
            }
        }
        return builder.build();
    }

    public Class<? extends BaggagePropagationConfig> getObjectType() {
        return BaggagePropagationConfig.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setField(BaggageField field) {
        this.field = field;
    }

    public void setKeyNames(List<String> keyNames) {
        if (keyNames == null) {
            return;
        }
        this.keyNames = keyNames;
    }
}

