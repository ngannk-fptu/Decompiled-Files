/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import java.net.URI;

public class GeneralException
extends Exception {
    private static final long serialVersionUID = -1641787397301043615L;
    private final ErrorObject error;
    private final ClientID clientID;
    private final URI redirectURI;
    private final ResponseMode responseMode;
    private final State state;

    public GeneralException(String message) {
        this(message, null, null, null, null, null, null);
    }

    public GeneralException(String message, Throwable cause) {
        this(message, null, null, null, null, null, cause);
    }

    public GeneralException(ErrorObject error) {
        this(error.getDescription(), error, null, null, null, null, null);
    }

    public GeneralException(String message, ErrorObject error) {
        this(message, error, null, null, null, null, null);
    }

    public GeneralException(String message, ErrorObject error, Throwable cause) {
        this(message, error, null, null, null, null, cause);
    }

    public GeneralException(String message, ErrorObject error, ClientID clientID, URI redirectURI, ResponseMode responseMode, State state) {
        this(message, error, clientID, redirectURI, responseMode, state, null);
    }

    public GeneralException(String message, ErrorObject error, ClientID clientID, URI redirectURI, ResponseMode responseMode, State state, Throwable cause) {
        super(message, cause);
        this.error = error;
        this.clientID = clientID;
        this.redirectURI = redirectURI;
        this.responseMode = responseMode;
        this.state = state;
    }

    public ErrorObject getErrorObject() {
        return this.error;
    }

    public ClientID getClientID() {
        return this.clientID;
    }

    public URI getRedirectionURI() {
        return this.redirectURI;
    }

    public ResponseMode getResponseMode() {
        return this.responseMode;
    }

    public State getState() {
        return this.state;
    }
}

