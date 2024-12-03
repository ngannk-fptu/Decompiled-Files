/*
 * Decompiled with CFR 0.152.
 */
package javax.security.auth.message;

import javax.security.auth.message.MessageInfo;

public class MessagePolicy {
    private final TargetPolicy[] targetPolicies;
    private final boolean mandatory;

    public MessagePolicy(TargetPolicy[] targetPolicies, boolean mandatory) {
        if (targetPolicies == null) {
            throw new IllegalArgumentException("targetPolicies is null");
        }
        this.targetPolicies = targetPolicies;
        this.mandatory = mandatory;
    }

    public boolean isMandatory() {
        return this.mandatory;
    }

    public TargetPolicy[] getTargetPolicies() {
        if (this.targetPolicies.length == 0) {
            return null;
        }
        return this.targetPolicies;
    }

    public static class TargetPolicy {
        private final Target[] targets;
        private final ProtectionPolicy protectionPolicy;

        public TargetPolicy(Target[] targets, ProtectionPolicy protectionPolicy) {
            if (protectionPolicy == null) {
                throw new IllegalArgumentException("protectionPolicy is null");
            }
            this.targets = targets;
            this.protectionPolicy = protectionPolicy;
        }

        public Target[] getTargets() {
            if (this.targets == null || this.targets.length == 0) {
                return null;
            }
            return this.targets;
        }

        public ProtectionPolicy getProtectionPolicy() {
            return this.protectionPolicy;
        }
    }

    public static interface Target {
        public Object get(MessageInfo var1);

        public void remove(MessageInfo var1);

        public void put(MessageInfo var1, Object var2);
    }

    public static interface ProtectionPolicy {
        public static final String AUTHENTICATE_SENDER = "#authenticateSender";
        public static final String AUTHENTICATE_CONTENT = "#authenticateContent";
        public static final String AUTHENTICATE_RECIPIENT = "#authenticateRecipient";

        public String getID();
    }
}

