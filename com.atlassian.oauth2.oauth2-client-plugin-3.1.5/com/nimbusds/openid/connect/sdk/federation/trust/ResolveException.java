/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.trust;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.GeneralException;
import java.util.Collections;
import java.util.List;

public class ResolveException
extends GeneralException {
    private static final long serialVersionUID = 1039304462191728890L;
    private List<Throwable> causes;

    public ResolveException(String message) {
        super(message);
    }

    public ResolveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResolveException(String message, List<Throwable> causes) {
        super(message);
        this.causes = causes;
    }

    public ResolveException(String message, ErrorObject errorObject) {
        super(message, errorObject);
    }

    public List<Throwable> getCauses() {
        if (this.causes != null) {
            return this.causes;
        }
        if (this.getCause() != null) {
            return Collections.singletonList(this.getCause());
        }
        return Collections.emptyList();
    }
}

