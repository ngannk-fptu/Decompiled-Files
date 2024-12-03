/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.rendering;

public enum ImageType {
    BINARY{

        @Override
        int toBufferedImageType() {
            return 12;
        }
    }
    ,
    GRAY{

        @Override
        int toBufferedImageType() {
            return 10;
        }
    }
    ,
    RGB{

        @Override
        int toBufferedImageType() {
            return 1;
        }
    }
    ,
    ARGB{

        @Override
        int toBufferedImageType() {
            return 2;
        }
    }
    ,
    BGR{

        @Override
        int toBufferedImageType() {
            return 5;
        }
    };


    abstract int toBufferedImageType();
}

