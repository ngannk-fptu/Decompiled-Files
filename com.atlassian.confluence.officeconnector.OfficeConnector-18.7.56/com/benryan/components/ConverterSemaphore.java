/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.benryan.components;

import com.benryan.components.AutoCloseableSemaphore;
import org.springframework.stereotype.Component;

@Component
public class ConverterSemaphore
extends AutoCloseableSemaphore {
    private static final Integer PERMITS_SIZE = Integer.getInteger("officeconnector.converter.permits.size", Runtime.getRuntime().availableProcessors());

    public ConverterSemaphore() {
        super(PERMITS_SIZE);
    }
}

