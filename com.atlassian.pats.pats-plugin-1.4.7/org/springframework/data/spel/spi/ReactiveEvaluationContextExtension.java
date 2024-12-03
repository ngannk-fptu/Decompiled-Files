/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.spel.spi;

import org.springframework.data.spel.spi.EvaluationContextExtension;
import org.springframework.data.spel.spi.ExtensionIdAware;
import reactor.core.publisher.Mono;

public interface ReactiveEvaluationContextExtension
extends ExtensionIdAware {
    public Mono<? extends EvaluationContextExtension> getExtension();
}

