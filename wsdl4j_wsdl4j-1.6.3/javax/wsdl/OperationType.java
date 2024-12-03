/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class OperationType
implements Serializable {
    private final String id;
    private final int intId;
    private static int counter = 0;
    public static final long serialVersionUID = 1L;
    public static OperationType ONE_WAY = new OperationType("ONE_WAY");
    public static OperationType REQUEST_RESPONSE = new OperationType("REQUEST_RESPONSE");
    public static OperationType SOLICIT_RESPONSE = new OperationType("SOLICIT_RESPONSE");
    public static OperationType NOTIFICATION = new OperationType("NOTIFICATION");
    private static final OperationType[] INSTANCES = new OperationType[]{ONE_WAY, REQUEST_RESPONSE, SOLICIT_RESPONSE, NOTIFICATION};

    private OperationType(String id) {
        this.id = id;
        this.intId = counter++;
    }

    private String getId() {
        return this.id;
    }

    public boolean equals(OperationType operationType) {
        return operationType != null && this.id.equals(operationType.getId());
    }

    public String toString() {
        return this.id + "," + this.intId;
    }

    private Object readResolve() throws ObjectStreamException {
        return INSTANCES[this.intId];
    }
}

