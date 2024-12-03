/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.io.InputStreamUtils;
import com.mchange.io.OutputStreamUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SerializableUtils {
    public static final Object unmarshallObjectFromFile(File file) throws IOException, ClassNotFoundException {
        Object object;
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            object = objectInputStream.readObject();
        }
        catch (Throwable throwable) {
            InputStreamUtils.attemptClose(objectInputStream);
            throw throwable;
        }
        InputStreamUtils.attemptClose(objectInputStream);
        return object;
    }

    public static final void marshallObjectToFile(Object object, File file) throws IOException {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            objectOutputStream.writeObject(object);
        }
        catch (Throwable throwable) {
            OutputStreamUtils.attemptClose(objectOutputStream);
            throw throwable;
        }
        OutputStreamUtils.attemptClose(objectOutputStream);
    }

    private SerializableUtils() {
    }
}

