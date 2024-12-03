/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.jdbc.spi;

import java.util.Locale;
import org.hibernate.ConnectionAcquisitionMode;
import org.hibernate.ConnectionReleaseMode;

public enum PhysicalConnectionHandlingMode {
    IMMEDIATE_ACQUISITION_AND_HOLD(ConnectionAcquisitionMode.IMMEDIATELY, ConnectionReleaseMode.ON_CLOSE),
    DELAYED_ACQUISITION_AND_HOLD(ConnectionAcquisitionMode.AS_NEEDED, ConnectionReleaseMode.ON_CLOSE),
    DELAYED_ACQUISITION_AND_RELEASE_AFTER_STATEMENT(ConnectionAcquisitionMode.AS_NEEDED, ConnectionReleaseMode.AFTER_STATEMENT),
    DELAYED_ACQUISITION_AND_RELEASE_BEFORE_TRANSACTION_COMPLETION(ConnectionAcquisitionMode.AS_NEEDED, ConnectionReleaseMode.BEFORE_TRANSACTION_COMPLETION),
    DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION(ConnectionAcquisitionMode.AS_NEEDED, ConnectionReleaseMode.AFTER_TRANSACTION);

    private final ConnectionAcquisitionMode acquisitionMode;
    private final ConnectionReleaseMode releaseMode;

    private PhysicalConnectionHandlingMode(ConnectionAcquisitionMode acquisitionMode, ConnectionReleaseMode releaseMode) {
        this.acquisitionMode = acquisitionMode;
        this.releaseMode = releaseMode;
    }

    public ConnectionAcquisitionMode getAcquisitionMode() {
        return this.acquisitionMode;
    }

    public ConnectionReleaseMode getReleaseMode() {
        return this.releaseMode;
    }

    public static PhysicalConnectionHandlingMode interpret(Object setting) {
        if (setting == null) {
            return null;
        }
        if (setting instanceof PhysicalConnectionHandlingMode) {
            return (PhysicalConnectionHandlingMode)((Object)setting);
        }
        String value = setting.toString().trim();
        if (value.isEmpty()) {
            return null;
        }
        return PhysicalConnectionHandlingMode.valueOf(value.toUpperCase(Locale.ROOT));
    }

    public static PhysicalConnectionHandlingMode interpret(ConnectionAcquisitionMode acquisitionMode, ConnectionReleaseMode releaseMode) {
        if (acquisitionMode == ConnectionAcquisitionMode.IMMEDIATELY) {
            if (releaseMode != null && releaseMode != ConnectionReleaseMode.ON_CLOSE) {
                throw new IllegalArgumentException("Only ConnectionReleaseMode.ON_CLOSE can be used in combination with ConnectionAcquisitionMode.IMMEDIATELY; but ConnectionReleaseMode." + releaseMode.name() + " was specified.");
            }
            return IMMEDIATE_ACQUISITION_AND_HOLD;
        }
        switch (releaseMode) {
            case AFTER_STATEMENT: {
                return DELAYED_ACQUISITION_AND_RELEASE_AFTER_STATEMENT;
            }
            case BEFORE_TRANSACTION_COMPLETION: {
                return DELAYED_ACQUISITION_AND_RELEASE_BEFORE_TRANSACTION_COMPLETION;
            }
            case AFTER_TRANSACTION: {
                return DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION;
            }
        }
        return DELAYED_ACQUISITION_AND_HOLD;
    }
}

