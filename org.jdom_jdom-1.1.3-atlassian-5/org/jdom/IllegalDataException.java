/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

public class IllegalDataException
extends IllegalArgumentException {
    private static final String CVS_ID = "@(#) $RCSfile: IllegalDataException.java,v $ $Revision: 1.14 $ $Date: 2007/11/10 05:28:59 $ $Name:  $";

    IllegalDataException(String data, String construct, String reason) {
        super(new StringBuffer().append("The data \"").append(data).append("\" is not legal for a JDOM ").append(construct).append(": ").append(reason).append(".").toString());
    }

    IllegalDataException(String data, String construct) {
        super(new StringBuffer().append("The data \"").append(data).append("\" is not legal for a JDOM ").append(construct).append(".").toString());
    }

    public IllegalDataException(String reason) {
        super(reason);
    }
}

