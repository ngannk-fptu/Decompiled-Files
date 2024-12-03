/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.cci.InteractionSpec
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.cci.object;

import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.InteractionSpec;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jca.cci.core.CciTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public abstract class EisOperation
implements InitializingBean {
    private CciTemplate cciTemplate = new CciTemplate();
    @Nullable
    private InteractionSpec interactionSpec;

    public void setCciTemplate(CciTemplate cciTemplate) {
        Assert.notNull((Object)cciTemplate, (String)"CciTemplate must not be null");
        this.cciTemplate = cciTemplate;
    }

    public CciTemplate getCciTemplate() {
        return this.cciTemplate;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.cciTemplate.setConnectionFactory(connectionFactory);
    }

    public void setInteractionSpec(@Nullable InteractionSpec interactionSpec) {
        this.interactionSpec = interactionSpec;
    }

    @Nullable
    public InteractionSpec getInteractionSpec() {
        return this.interactionSpec;
    }

    public void afterPropertiesSet() {
        this.cciTemplate.afterPropertiesSet();
        if (this.interactionSpec == null) {
            throw new IllegalArgumentException("InteractionSpec is required");
        }
    }
}

