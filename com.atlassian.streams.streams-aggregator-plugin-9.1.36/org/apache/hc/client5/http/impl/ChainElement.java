/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl;

public enum ChainElement {
    REDIRECT,
    COMPRESS,
    BACK_OFF,
    RETRY,
    CACHING,
    PROTOCOL,
    CONNECT,
    MAIN_TRANSPORT;

}

