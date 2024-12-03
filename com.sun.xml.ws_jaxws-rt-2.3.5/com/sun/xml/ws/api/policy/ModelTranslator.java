/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.privateutil.PolicyLogger
 *  com.sun.xml.ws.policy.sourcemodel.PolicyModelTranslator
 *  com.sun.xml.ws.policy.spi.PolicyAssertionCreator
 */
package com.sun.xml.ws.api.policy;

import com.sun.xml.ws.config.management.policy.ManagementAssertionCreator;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.sourcemodel.PolicyModelTranslator;
import com.sun.xml.ws.policy.spi.PolicyAssertionCreator;
import com.sun.xml.ws.resources.ManagementMessages;
import java.util.Arrays;

public class ModelTranslator
extends PolicyModelTranslator {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ModelTranslator.class);
    private static final PolicyAssertionCreator[] JAXWS_ASSERTION_CREATORS = new PolicyAssertionCreator[]{new ManagementAssertionCreator()};
    private static final ModelTranslator translator;
    private static final PolicyException creationException;

    private ModelTranslator() throws PolicyException {
        super(Arrays.asList(JAXWS_ASSERTION_CREATORS));
    }

    public static ModelTranslator getTranslator() throws PolicyException {
        if (creationException != null) {
            throw (PolicyException)LOGGER.logSevereException((Throwable)creationException);
        }
        return translator;
    }

    static {
        ModelTranslator tempTranslator = null;
        PolicyException tempException = null;
        try {
            tempTranslator = new ModelTranslator();
        }
        catch (PolicyException e) {
            tempException = e;
            LOGGER.warning(ManagementMessages.WSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION(), (Throwable)e);
        }
        finally {
            translator = tempTranslator;
            creationException = tempException;
        }
    }
}

