/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ServiceLoader;

public class AssertionValidationProcessor {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AssertionValidationProcessor.class);
    private final Collection<PolicyAssertionValidator> validators = new LinkedList<PolicyAssertionValidator>();

    private AssertionValidationProcessor() throws PolicyException {
        this(null);
    }

    protected AssertionValidationProcessor(Collection<PolicyAssertionValidator> policyValidators) throws PolicyException {
        for (PolicyAssertionValidator validator : ServiceLoader.load(PolicyAssertionValidator.class)) {
            this.validators.add(validator);
        }
        if (policyValidators != null) {
            for (PolicyAssertionValidator validator : policyValidators) {
                this.validators.add(validator);
            }
        }
        if (this.validators.size() == 0) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0076_NO_SERVICE_PROVIDERS_FOUND(PolicyAssertionValidator.class.getName())));
        }
    }

    public static AssertionValidationProcessor getInstance() throws PolicyException {
        return new AssertionValidationProcessor();
    }

    public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion assertion) throws PolicyException {
        PolicyAssertionValidator validator;
        PolicyAssertionValidator.Fitness assertionFitness = PolicyAssertionValidator.Fitness.UNKNOWN;
        Iterator<PolicyAssertionValidator> iterator = this.validators.iterator();
        while (iterator.hasNext() && (assertionFitness = assertionFitness.combine((validator = iterator.next()).validateClientSide(assertion))) != PolicyAssertionValidator.Fitness.SUPPORTED) {
        }
        return assertionFitness;
    }

    public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion assertion) throws PolicyException {
        PolicyAssertionValidator validator;
        PolicyAssertionValidator.Fitness assertionFitness = PolicyAssertionValidator.Fitness.UNKNOWN;
        Iterator<PolicyAssertionValidator> iterator = this.validators.iterator();
        while (iterator.hasNext() && (assertionFitness = assertionFitness.combine((validator = iterator.next()).validateServerSide(assertion))) != PolicyAssertionValidator.Fitness.SUPPORTED) {
        }
        return assertionFitness;
    }
}

