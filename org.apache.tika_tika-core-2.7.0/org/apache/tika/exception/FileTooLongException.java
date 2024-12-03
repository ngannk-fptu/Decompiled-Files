/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.exception;

import java.io.IOException;

public class FileTooLongException
extends IOException {
    public FileTooLongException(String msg) {
        super(msg);
    }

    public FileTooLongException(long length, long maxLength) {
        super(FileTooLongException.msg(length, maxLength));
    }

    private static String msg(long length, long maxLength) {
        return "File is " + length + " bytes, but " + maxLength + " is the maximum length allowed.  You can modify maxLength via the setter on the fetcher.";
    }
}

