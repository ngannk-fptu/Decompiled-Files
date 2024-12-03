/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.ProcessingPhase;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.ProcessingState;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EvaluationExpiryChecker {
    private static final Logger logger = LoggerFactory.getLogger(EvaluationExpiryChecker.class);
    private static final int DEFAULT_REQUEST_TIMEOUT = 60;
    private static final String REQUEST_TIMEOUT_SYSTEM_PROPERTY = "gatekeeper.request-timeout.seconds";
    private static final int MIN_REQUEST_TIMEOUT = 30;
    private static final int MAX_REQUEST_TIMEOUT = 900;
    private final long requestTimeoutMillis;

    public EvaluationExpiryChecker() {
        long requestTimeout = Integer.getInteger(REQUEST_TIMEOUT_SYSTEM_PROPERTY, 60).intValue();
        if (requestTimeout < 30L) {
            logger.warn("The value of {} is too small. Using {} as it is the minimum allowed value.", (Object)REQUEST_TIMEOUT_SYSTEM_PROPERTY, (Object)30);
            requestTimeout = 30L;
        }
        if (requestTimeout > 900L) {
            logger.warn("The value of {} is too big. Using {} as it is the maximum allowed value.", (Object)REQUEST_TIMEOUT_SYSTEM_PROPERTY, (Object)900);
            requestTimeout = 900L;
        }
        this.requestTimeoutMillis = requestTimeout * 1000L;
    }

    public void check(Evaluator evaluator) throws TimeoutException, InterruptedException {
        ProcessingState processingState = evaluator.getProcessingState();
        if (processingState.isExpired()) {
            processingState.setPhase(ProcessingPhase.TIMED_OUT);
            throw new TimeoutException();
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    public long getRequestTimeoutMillis() {
        return this.requestTimeoutMillis;
    }
}

