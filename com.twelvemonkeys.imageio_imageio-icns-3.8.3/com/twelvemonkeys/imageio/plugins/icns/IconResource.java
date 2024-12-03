/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.icns;

import com.twelvemonkeys.imageio.plugins.icns.ICNSUtil;
import com.twelvemonkeys.lang.Validate;
import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

final class IconResource {
    final long start;
    final int type;
    final int length;

    private IconResource(long l, int n, int n2) {
        this.validate(n, n2);
        this.start = l;
        this.type = n;
        this.length = n2;
    }

    static IconResource read(ImageInputStream imageInputStream) throws IOException {
        return IconResource.read(imageInputStream.getStreamPosition(), imageInputStream);
    }

    static IconResource read(long l, ImageInputStream imageInputStream) throws IOException {
        return new IconResource(l, imageInputStream.readInt(), imageInputStream.readInt());
    }

    private void validate(int n, int n2) {
        switch (n) {
            case 1229147982: {
                this.validateLengthForType(n, n2, 128);
                break;
            }
            case 1229147683: {
                this.validateLengthForType(n, n2, 256);
                break;
            }
            case 1768123683: {
                this.validateLengthForType(n, n2, 48);
                break;
            }
            case 1768123700: {
                this.validateLengthForType(n, n2, 96);
                break;
            }
            case 1768123704: {
                this.validateLengthForType(n, n2, 192);
                break;
            }
            case 1768125219: {
                this.validateLengthForType(n, n2, 64);
                break;
            }
            case 1768125236: {
                this.validateLengthForType(n, n2, 128);
                break;
            }
            case 1768125240: 
            case 1933077867: {
                this.validateLengthForType(n, n2, 256);
                break;
            }
            case 1768123444: {
                this.validateLengthForType(n, n2, 512);
                break;
            }
            case 1768123448: 
            case 1815637355: {
                this.validateLengthForType(n, n2, 1024);
                break;
            }
            case 1768122403: {
                this.validateLengthForType(n, n2, 576);
                break;
            }
            case 1768122420: {
                this.validateLengthForType(n, n2, 1152);
                break;
            }
            case 1748528491: 
            case 1768122424: {
                this.validateLengthForType(n, n2, 2304);
                break;
            }
            case 1949855083: {
                this.validateLengthForType(n, n2, 16384);
                break;
            }
            case 1768108087: 
            case 1768108088: 
            case 1768108089: 
            case 1768108336: 
            case 1768108337: 
            case 1768108338: 
            case 1768108339: 
            case 1768108340: 
            case 1768124468: 
            case 1768124469: 
            case 1768124470: 
            case 1768436530: 
            case 1768698674: 
            case 1769157426: 
            case 1769222962: {
                if (n2 > 8) break;
                throw new IllegalArgumentException(String.format("Wrong combination of icon type '%s' and length: %d", ICNSUtil.intToStr(n), n2));
            }
            case 1768123990: {
                this.validateLengthForType(n, n2, 4);
                break;
            }
            default: {
                if (n2 > 8) break;
                throw new IllegalStateException(String.format("Unknown icon type: '%s' length: %d", ICNSUtil.intToStr(n), n2));
            }
        }
    }

    private void validateLengthForType(int n, int n2, int n3) {
        Validate.isTrue((n2 == n3 + 8 ? 1 : 0) != 0, (String)String.format("Wrong combination of icon type '%s' and length: %d (expected: %d)", ICNSUtil.intToStr(n), n2 - 8, n3));
    }

    Dimension size() {
        switch (this.type) {
            case 1229147683: 
            case 1229147982: {
                return new Dimension(32, 32);
            }
            case 1768123683: 
            case 1768123700: 
            case 1768123704: {
                return new Dimension(16, 12);
            }
            case 1768124468: 
            case 1768125219: 
            case 1768125236: 
            case 1768125240: 
            case 1769157426: 
            case 1933077867: {
                return new Dimension(16, 16);
            }
            case 1768108337: 
            case 1768123444: 
            case 1768123448: 
            case 1768124469: 
            case 1768698674: 
            case 1815637355: {
                return new Dimension(32, 32);
            }
            case 1748528491: 
            case 1768122403: 
            case 1768122420: 
            case 1768122424: 
            case 1768436530: {
                return new Dimension(48, 48);
            }
            case 1768108338: 
            case 1768124470: {
                return new Dimension(64, 64);
            }
            case 1768108087: 
            case 1769222962: 
            case 1949855083: {
                return new Dimension(128, 128);
            }
            case 1768108088: 
            case 1768108339: {
                return new Dimension(256, 256);
            }
            case 1768108089: 
            case 1768108340: {
                return new Dimension(512, 512);
            }
            case 1768108336: {
                return new Dimension(1024, 1024);
            }
        }
        throw new IllegalStateException(String.format("Unknown icon type: '%s'", ICNSUtil.intToStr(this.type)));
    }

    int depth() {
        switch (this.type) {
            case 1229147683: 
            case 1229147982: 
            case 1768122403: 
            case 1768123683: 
            case 1768125219: {
                return 1;
            }
            case 1768122420: 
            case 1768123444: 
            case 1768123700: 
            case 1768125236: {
                return 4;
            }
            case 1748528491: 
            case 1768122424: 
            case 1768123448: 
            case 1768123704: 
            case 1768125240: 
            case 1815637355: 
            case 1933077867: 
            case 1949855083: {
                return 8;
            }
            case 1768108087: 
            case 1768108088: 
            case 1768108089: 
            case 1768108336: 
            case 1768108337: 
            case 1768108338: 
            case 1768108339: 
            case 1768108340: 
            case 1768124468: 
            case 1768124469: 
            case 1768124470: 
            case 1768436530: 
            case 1768698674: 
            case 1769157426: 
            case 1769222962: {
                return 32;
            }
        }
        throw new IllegalStateException(String.format("Unknown icon type: '%s'", ICNSUtil.intToStr(this.type)));
    }

    boolean isUnknownType() {
        switch (this.type) {
            case 1229147683: 
            case 1229147982: 
            case 1748528491: 
            case 1768108087: 
            case 1768108088: 
            case 1768108089: 
            case 1768108336: 
            case 1768108337: 
            case 1768108338: 
            case 1768108339: 
            case 1768108340: 
            case 1768122403: 
            case 1768122420: 
            case 1768122424: 
            case 1768123444: 
            case 1768123448: 
            case 1768123683: 
            case 1768123700: 
            case 1768123704: 
            case 1768124468: 
            case 1768124469: 
            case 1768124470: 
            case 1768125219: 
            case 1768125236: 
            case 1768125240: 
            case 1768436530: 
            case 1768698674: 
            case 1769157426: 
            case 1769222962: 
            case 1815637355: 
            case 1933077867: 
            case 1949855083: {
                return false;
            }
        }
        return true;
    }

    boolean hasMask() {
        switch (this.type) {
            case 1229147683: 
            case 1768122403: 
            case 1768123683: 
            case 1768125219: {
                return true;
            }
        }
        return false;
    }

    boolean isMaskType() {
        switch (this.type) {
            case 1748528491: 
            case 1815637355: 
            case 1933077867: 
            case 1949855083: {
                return true;
            }
        }
        return false;
    }

    boolean isCompressed() {
        switch (this.type) {
            case 1768436530: 
            case 1768698674: 
            case 1769157426: 
            case 1769222962: {
                Dimension dimension = this.size();
                if (this.length == dimension.width * dimension.height * this.depth() / 8 + 8) break;
                return true;
            }
        }
        return false;
    }

    boolean isForeignFormat() {
        switch (this.type) {
            case 1768108087: 
            case 1768108088: 
            case 1768108089: 
            case 1768108336: 
            case 1768108337: 
            case 1768108338: 
            case 1768108339: 
            case 1768108340: 
            case 1768124468: 
            case 1768124469: 
            case 1768124470: {
                return true;
            }
        }
        return false;
    }

    boolean isTOC() {
        return this.type == 1414480672;
    }

    public int hashCode() {
        return (int)this.start ^ this.type;
    }

    public boolean equals(Object object) {
        return object == this || object != null && object.getClass() == this.getClass() && this.isEqual((IconResource)object);
    }

    private boolean isEqual(IconResource iconResource) {
        return this.start == iconResource.start && this.type == iconResource.type && this.length == iconResource.length;
    }

    public String toString() {
        return String.format("%s['%s' start: %d, length: %d%s]", this.getClass().getSimpleName(), ICNSUtil.intToStr(this.type), this.start, this.length, this.isCompressed() ? " (compressed)" : "");
    }

    static int typeFromImage(RenderedImage renderedImage, String string) {
        int n;
        int n2 = renderedImage.getWidth();
        if (n2 == (n = renderedImage.getHeight())) {
            switch (string) {
                case "JPEG2000": 
                case "PNG": {
                    return IconResource.typeFromWidthForeign(n2);
                }
                case "None": 
                case "RLE": {
                    return IconResource.typeFromWidthNative(n2);
                }
            }
            throw new IllegalArgumentException("Unsupported compression for ICNS: " + string);
        }
        throw new IllegalArgumentException(String.format("Unsupported dimensions for ICNS, only square icons supported: %dx%d", n2, n));
    }

    private static int typeFromWidthNative(int n) {
        switch (n) {
            case 16: {
                return 1769157426;
            }
            case 32: {
                return 1768698674;
            }
            case 48: {
                return 1768436530;
            }
            case 128: {
                return 1769222962;
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported dimensions for ICNS, only 16, 32, 48 and 128 supported: %dx%d", n, n));
    }

    private static int typeFromWidthForeign(int n) {
        switch (n) {
            case 16: {
                return 1768124468;
            }
            case 32: {
                return 1768124469;
            }
            case 64: {
                return 1768124470;
            }
            case 128: {
                return 1768108087;
            }
            case 256: {
                return 1768108088;
            }
            case 512: {
                return 1768108089;
            }
            case 1024: {
                return 1768108336;
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported dimensions for ICNS, only multiples of 2 from 16 to 1024 supported: %dx%d", n, n));
    }
}

