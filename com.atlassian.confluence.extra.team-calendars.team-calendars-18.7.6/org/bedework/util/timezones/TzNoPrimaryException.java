/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones;

import org.bedework.util.timezones.TimezonesException;

public class TzNoPrimaryException
extends TimezonesException {
    public TzNoPrimaryException(String msg) {
        super(TimezonesException.noPrimary, msg);
    }
}

