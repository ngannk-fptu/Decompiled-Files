/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.template.etsi102941;

import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.its.template.etsi102941.basetypes.EtsiTs102941BaseTypes;

public class EtsiTs102941TypesCaManagement {
    public static final OERDefinition.Builder CaCertificateRequest = OERDefinition.seq(EtsiTs102941BaseTypes.PublicKeys.label("publicKeys"), EtsiTs102941BaseTypes.CertificateSubjectAttributes.label("requestedSubjectAttributes")).typeName("CaCertificateRequest");
}

