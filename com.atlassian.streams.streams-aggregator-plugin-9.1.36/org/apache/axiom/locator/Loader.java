/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.locator;

abstract class Loader {
    Loader() {
    }

    abstract Class load(String var1) throws ClassNotFoundException;
}

