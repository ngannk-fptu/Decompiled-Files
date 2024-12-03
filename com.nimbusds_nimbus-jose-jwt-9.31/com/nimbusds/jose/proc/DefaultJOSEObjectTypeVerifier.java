/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.SecurityContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class DefaultJOSEObjectTypeVerifier<C extends SecurityContext>
implements JOSEObjectTypeVerifier<C> {
    private final Set<JOSEObjectType> allowedTypes;
    public static final DefaultJOSEObjectTypeVerifier JOSE = new DefaultJOSEObjectTypeVerifier(JOSEObjectType.JOSE, null);
    public static final DefaultJOSEObjectTypeVerifier JWT = new DefaultJOSEObjectTypeVerifier(JOSEObjectType.JWT, null);

    public DefaultJOSEObjectTypeVerifier() {
        this.allowedTypes = Collections.singleton(null);
    }

    public DefaultJOSEObjectTypeVerifier(Set<JOSEObjectType> allowedTypes) {
        if (allowedTypes == null || allowedTypes.isEmpty()) {
            throw new IllegalArgumentException("The allowed types must not be null or empty");
        }
        this.allowedTypes = allowedTypes;
    }

    public DefaultJOSEObjectTypeVerifier(JOSEObjectType ... allowedTypes) {
        if (allowedTypes == null || allowedTypes.length == 0) {
            throw new IllegalArgumentException("The allowed types must not be null or empty");
        }
        this.allowedTypes = new HashSet<JOSEObjectType>(Arrays.asList(allowedTypes));
    }

    public Set<JOSEObjectType> getAllowedTypes() {
        return this.allowedTypes;
    }

    @Override
    public void verify(JOSEObjectType type, C context) throws BadJOSEException {
        if (type == null && !this.allowedTypes.contains(null)) {
            throw new BadJOSEException("Required JOSE header typ (type) parameter is missing");
        }
        if (!this.allowedTypes.contains(type)) {
            throw new BadJOSEException("JOSE header typ (type) " + type + " not allowed");
        }
    }
}

