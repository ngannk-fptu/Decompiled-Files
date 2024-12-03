/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ERSDataGroup;
import org.bouncycastle.tsp.ers.ERSFileData;

public class ERSDirectoryDataGroup
extends ERSDataGroup {
    public ERSDirectoryDataGroup(File dataDirectory) throws FileNotFoundException {
        super(ERSDirectoryDataGroup.buildGroup(dataDirectory));
    }

    private static List<ERSData> buildGroup(File dataDirectory) throws FileNotFoundException {
        if (dataDirectory.isDirectory()) {
            File[] files = dataDirectory.listFiles();
            ArrayList<ERSData> dataObjects = new ArrayList<ERSData>(files.length);
            for (int i = 0; i != files.length; ++i) {
                if (files[i].isDirectory()) {
                    if (files[i].listFiles().length == 0) continue;
                    dataObjects.add(new ERSDirectoryDataGroup(files[i]));
                    continue;
                }
                dataObjects.add(new ERSFileData(files[i]));
            }
            return dataObjects;
        }
        throw new IllegalArgumentException("file reference does not refer to directory");
    }

    public List<ERSFileData> getFiles() {
        ArrayList<ERSFileData> files = new ArrayList<ERSFileData>();
        for (int i = 0; i != this.dataObjects.size(); ++i) {
            if (!(this.dataObjects.get(i) instanceof ERSFileData)) continue;
            files.add((ERSFileData)this.dataObjects.get(i));
        }
        return files;
    }

    public List<ERSDirectoryDataGroup> getSubdirectories() {
        ArrayList<ERSDirectoryDataGroup> subdirectories = new ArrayList<ERSDirectoryDataGroup>();
        for (int i = 0; i != this.dataObjects.size(); ++i) {
            if (!(this.dataObjects.get(i) instanceof ERSDirectoryDataGroup)) continue;
            subdirectories.add((ERSDirectoryDataGroup)this.dataObjects.get(i));
        }
        return subdirectories;
    }
}

