/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic;

import net.bytebuddy.description.method.MethodDescription;

public interface VisibilityBridgeStrategy {
    public boolean generateVisibilityBridge(MethodDescription var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Default implements VisibilityBridgeStrategy
    {
        ALWAYS{

            public boolean generateVisibilityBridge(MethodDescription methodDescription) {
                return true;
            }
        }
        ,
        ON_NON_GENERIC_METHOD{

            public boolean generateVisibilityBridge(MethodDescription methodDescription) {
                return !methodDescription.isGenerified();
            }
        }
        ,
        NEVER{

            public boolean generateVisibilityBridge(MethodDescription methodDescription) {
                return false;
            }
        };

    }
}

