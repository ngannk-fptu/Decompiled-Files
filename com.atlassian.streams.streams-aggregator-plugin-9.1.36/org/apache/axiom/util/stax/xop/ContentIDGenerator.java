/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.xop;

import org.apache.axiom.util.UIDGenerator;

public interface ContentIDGenerator {
    public static final ContentIDGenerator DEFAULT = new ContentIDGenerator(){

        public String generateContentID(String existingContentID) {
            if (existingContentID == null) {
                return UIDGenerator.generateContentId();
            }
            return existingContentID;
        }
    };

    public String generateContentID(String var1);
}

