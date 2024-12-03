/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ExtendedHierarchicalStreamWriterHelper {
    public static void startNode(HierarchicalStreamWriter writer, String name, Class clazz) {
        if (writer instanceof ExtendedHierarchicalStreamWriter) {
            ((ExtendedHierarchicalStreamWriter)writer).startNode(name, clazz);
        } else {
            writer.startNode(name);
        }
    }
}

