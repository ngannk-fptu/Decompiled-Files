/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.image;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.image.ImageIO;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.commons.logging.Log;

public class ImageIOFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$image$ImageIOFactory == null ? (class$org$apache$axis$components$image$ImageIOFactory = ImageIOFactory.class$("org.apache.axis.components.image.ImageIOFactory")) : class$org$apache$axis$components$image$ImageIOFactory).getName());
    static /* synthetic */ Class class$org$apache$axis$components$image$ImageIOFactory;
    static /* synthetic */ Class class$org$apache$axis$components$image$ImageIO;

    public static ImageIO getImageIO() {
        ImageIO imageIO = (ImageIO)AxisProperties.newInstance(class$org$apache$axis$components$image$ImageIO == null ? (class$org$apache$axis$components$image$ImageIO = ImageIOFactory.class$("org.apache.axis.components.image.ImageIO")) : class$org$apache$axis$components$image$ImageIO);
        if (imageIO == null) {
            try {
                Class cls = ClassUtils.forName("org.apache.axis.components.image.JDK13IO");
                imageIO = (ImageIO)cls.newInstance();
            }
            catch (Exception e) {
                log.debug((Object)"ImageIOFactory: No matching ImageIO found", (Throwable)e);
            }
        }
        log.debug((Object)("axis.ImageIO: " + imageIO.getClass().getName()));
        return imageIO;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        AxisProperties.setClassOverrideProperty(class$org$apache$axis$components$image$ImageIO == null ? (class$org$apache$axis$components$image$ImageIO = ImageIOFactory.class$("org.apache.axis.components.image.ImageIO")) : class$org$apache$axis$components$image$ImageIO, "axis.ImageIO");
        AxisProperties.setClassDefaults(class$org$apache$axis$components$image$ImageIO == null ? (class$org$apache$axis$components$image$ImageIO = ImageIOFactory.class$("org.apache.axis.components.image.ImageIO")) : class$org$apache$axis$components$image$ImageIO, new String[]{"org.apache.axis.components.image.MerlinIO", "org.apache.axis.components.image.JimiIO", "org.apache.axis.components.image.JDK13IO"});
    }
}

