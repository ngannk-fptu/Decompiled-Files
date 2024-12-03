/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cms.OriginatorInfo
 */
package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInfoGenerator;

public class CMSAuthEnvelopedGenerator
extends CMSEnvelopedGenerator {
    final List recipientInfoGenerators = new ArrayList();
    protected CMSAttributeTableGenerator authAttrsGenerator = null;
    protected CMSAttributeTableGenerator unauthAttrsGenerator = null;
    protected OriginatorInfo originatorInfo;

    protected CMSAuthEnvelopedGenerator() {
    }

    public void setAuthenticatedAttributeGenerator(CMSAttributeTableGenerator protectedAttributeGenerator) {
        this.authAttrsGenerator = protectedAttributeGenerator;
    }

    public void setUnauthenticatedAttributeGenerator(CMSAttributeTableGenerator unauthenticatedAttributeGenerator) {
        this.unauthAttrsGenerator = unauthenticatedAttributeGenerator;
    }

    @Override
    public void setOriginatorInfo(OriginatorInformation originatorInfo) {
        this.originatorInfo = originatorInfo.toASN1Structure();
    }

    @Override
    public void addRecipientInfoGenerator(RecipientInfoGenerator recipientGenerator) {
        this.recipientInfoGenerators.add(recipientGenerator);
    }
}

