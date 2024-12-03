/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import java.net.URI;

public class ParseException
extends GeneralException {
    private static final long serialVersionUID = 5717029305138222869L;

    public ParseException(String message) {
        this(message, null, null, null, null, null);
    }

    public ParseException(String message, Throwable cause) {
        this(message, null, null, null, null, null, cause);
    }

    public ParseException(String message, ErrorObject error) {
        this(message, error, null, null, null, null);
    }

    public ParseException(String message, ErrorObject error, Throwable cause) {
        this(message, error, null, null, null, null, cause);
    }

    public ParseException(String message, ErrorObject error, ClientID clientID, URI redirectURI, ResponseMode responseMode, State state) {
        this(message, error, clientID, redirectURI, responseMode, state, null);
    }

    public ParseException(String message, ErrorObject error, ClientID clientID, URI redirectURI, ResponseMode responseMode, State state, Throwable cause) {
        super(message, error, clientID, redirectURI, responseMode, state, cause);
    }
}

