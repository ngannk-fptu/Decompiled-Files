/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.oxm.Unmarshaller
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.init;

import org.springframework.data.repository.init.AbstractRepositoryPopulatorFactoryBean;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.data.repository.init.UnmarshallingResourceReader;
import org.springframework.lang.Nullable;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.Assert;

public class UnmarshallerRepositoryPopulatorFactoryBean
extends AbstractRepositoryPopulatorFactoryBean {
    @Nullable
    private Unmarshaller unmarshaller;

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    @Override
    protected ResourceReader getResourceReader() {
        Unmarshaller unmarshaller = this.unmarshaller;
        if (unmarshaller == null) {
            throw new IllegalStateException("No Unmarshaller configured!");
        }
        return new UnmarshallingResourceReader(unmarshaller);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state((this.unmarshaller != null ? 1 : 0) != 0, (String)"No Unmarshaller configured!");
        super.afterPropertiesSet();
    }
}

