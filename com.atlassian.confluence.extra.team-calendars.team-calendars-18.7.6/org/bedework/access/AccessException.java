/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

public class AccessException
extends Exception {
    private static final String badACEMsg = "org.bedework.cmt.access.badace";
    private static final String badACLMsg = "org.bedework.cmt.access.badacl";
    private static final String badACLLengthMsg = "org.bedework.cmt.access.badacllength";
    private static final String badACLRewindMsg = "org.bedework.cmt.access.badaclrewinf";
    private static final String badXmlACLMsg = "org.bedework.cmt.access.badxmlacl";

    public AccessException(String s) {
        super(s);
    }

    public AccessException(String s, String extra) {
        super(s + " " + extra);
    }

    public AccessException(Throwable t) {
        super(t);
    }

    public static AccessException badACE(String extra) {
        return new AccessException(badACEMsg, extra);
    }

    public static AccessException badACL(String extra) {
        return new AccessException(badACLMsg, extra);
    }

    public static AccessException badACLRewind() {
        return new AccessException(badACLRewindMsg);
    }

    public static AccessException badACLLength() {
        return new AccessException(badACLLengthMsg);
    }

    public static AccessException badXmlACL(String extra) {
        return new AccessException(badXmlACLMsg, extra);
    }
}

