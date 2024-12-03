/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.tinymceplugin.placeholder;

import com.atlassian.confluence.tinymceplugin.placeholder.StyledString;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

public interface PlaceholderImageFactory {
    public BufferedImage getPlaceholderImage(StyledString var1, InputStream var2, Color var3);

    public BufferedImage getPlaceholderImage(StyledString var1, InputStream var2, Color var3, Color var4, boolean var5);

    public BufferedImage getPlaceholderImage(List<StyledString> var1, InputStream var2, Color var3);

    public BufferedImage getPlaceholderImage(List<StyledString> var1, InputStream var2, Color var3, Color var4, boolean var5);

    public BufferedImage getPlaceholderHeading(List<StyledString> var1, InputStream var2, Color var3);

    public BufferedImage getPlaceholderImage(InputStream var1, int var2);
}

