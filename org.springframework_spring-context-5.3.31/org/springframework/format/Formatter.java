/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format;

import org.springframework.format.Parser;
import org.springframework.format.Printer;

public interface Formatter<T>
extends Printer<T>,
Parser<T> {
}

