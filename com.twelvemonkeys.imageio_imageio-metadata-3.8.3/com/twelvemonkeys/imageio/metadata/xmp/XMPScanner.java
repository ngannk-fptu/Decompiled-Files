/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.util.IIOUtil
 */
package com.twelvemonkeys.imageio.metadata.xmp;

import com.twelvemonkeys.imageio.util.IIOUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

public final class XMPScanner {
    private static final byte[] XMP_PACKET_BEGIN = new byte[]{60, 63, 120, 112, 97, 99, 107, 101, 116, 32, 98, 101, 103, 105, 110, 61};
    private static final byte[] XMP_PACKET_END = new byte[]{60, 63, 120, 112, 97, 99, 107, 101, 116, 32, 101, 110, 100, 61};

    public static Reader scanForXMPPacket(Object object) throws IOException {
        byte by;
        ImageInputStream imageInputStream = object instanceof ImageInputStream ? (ImageInputStream)object : ImageIO.createImageInputStream(object);
        long l = XMPScanner.scanForSequence(imageInputStream, XMP_PACKET_BEGIN);
        if (l >= 0L && ((by = imageInputStream.readByte()) == 39 || by == 34)) {
            Charset charset = null;
            byte[] byArray = new byte[4];
            imageInputStream.readFully(byArray);
            if (byArray[0] == -17 && byArray[1] == -69 && byArray[2] == -65 && byArray[3] == by || byArray[0] == by) {
                charset = StandardCharsets.UTF_8;
            } else if (byArray[0] == -2 && byArray[1] == -1 && byArray[2] == 0 && byArray[3] == by) {
                charset = StandardCharsets.UTF_16BE;
            } else if (byArray[0] == 0 && byArray[1] == -1 && byArray[2] == -2 && byArray[3] == by) {
                imageInputStream.skipBytes(1);
                charset = StandardCharsets.UTF_16LE;
            } else if (byArray[0] == 0 && byArray[1] == 0 && byArray[2] == -2 && byArray[3] == -1) {
                charset = Charset.forName("UTF-32BE");
            } else if (byArray[0] == 0 && byArray[1] == 0 && byArray[2] == 0 && byArray[3] == -1 && imageInputStream.read() == 254) {
                imageInputStream.skipBytes(2);
                charset = Charset.forName("UTF-32LE");
            }
            if (charset != null) {
                imageInputStream.mark();
                long l2 = XMPScanner.scanForSequence(imageInputStream, XMP_PACKET_END);
                imageInputStream.reset();
                long l3 = l2 - imageInputStream.getStreamPosition();
                InputStreamReader inputStreamReader = new InputStreamReader(IIOUtil.createStreamAdapter((ImageInputStream)imageInputStream, (long)l3), charset);
                while (((Reader)inputStreamReader).read() != 62) {
                }
                return inputStreamReader;
            }
        }
        return null;
    }

    private static long scanForSequence(ImageInputStream imageInputStream, byte[] byArray) throws IOException {
        int n;
        long l = -1L;
        int n2 = 0;
        int n3 = 0;
        while ((n = imageInputStream.read()) >= 0) {
            if (byArray[n2] == (byte)n) {
                if (l == -1L) {
                    l = imageInputStream.getStreamPosition() - 1L;
                }
                if (n3 == 1 || n3 == 3) {
                    imageInputStream.skipBytes(n3);
                }
                if (++n2 != byArray.length) continue;
                return l;
            }
            if (n2 == 1 && n == 0 && n3 < 3) {
                ++n3;
                continue;
            }
            if (n2 == 0) continue;
            n2 = 0;
            l = -1L;
            n3 = 0;
        }
        return -1L;
    }

    public static void main(String[] stringArray) throws IOException {
        Reader reader;
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(new File(stringArray[0]));
        while ((reader = XMPScanner.scanForXMPPacket(imageInputStream)) != null) {
            String string;
            BufferedReader bufferedReader = new BufferedReader(reader);
            while ((string = bufferedReader.readLine()) != null) {
                System.out.println(string);
            }
        }
        imageInputStream.close();
    }
}

