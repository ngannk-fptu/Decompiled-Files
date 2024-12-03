/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.AssertionValidationProcessor
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.spi.PolicyAssertionValidator
 */
package com.sun.xml.ws.api.policy;

import com.sun.xml.ws.addressing.policy.AddressingPolicyValidator;
import com.sun.xml.ws.config.management.policy.ManagementPolicyValidator;
import com.sun.xml.ws.encoding.policy.EncodingPolicyValidator;
import com.sun.xml.ws.policy.AssertionValidationProcessor;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import java.util.Arrays;

public class ValidationProcessor
extends AssertionValidationProcessor {
    private static final PolicyAssertionValidator[] JAXWS_ASSERTION_VALIDATORS = new PolicyAssertionValidator[]{new AddressingPolicyValidator(), new EncodingPolicyValidator(), new ManagementPolicyValidator()};

    private ValidationProcessor() throws PolicyException {
        super(Arrays.asList(JAXWS_ASSERTION_VALIDATORS));
    }

    public static ValidationProcessor getInstance() throws PolicyException {
        return new ValidationProcessor();
    }
}

