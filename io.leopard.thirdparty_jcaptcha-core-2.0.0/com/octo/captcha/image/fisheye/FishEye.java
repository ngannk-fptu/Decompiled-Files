/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.image.fisheye;

import com.octo.captcha.image.ImageCaptcha;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.StringTokenizer;

public class FishEye
extends ImageCaptcha {
    private static final long serialVersionUID = 1L;
    private Point deformationCenter;
    private Integer tolerance;

    protected FishEye(String question, BufferedImage challenge, Point deformationCenter, Integer tolerance) {
        super(question, challenge);
        this.deformationCenter = deformationCenter;
        this.tolerance = tolerance;
    }

    public Boolean validateResponse(Object response) {
        if (response instanceof Point) {
            Point point = (Point)response;
            return this.validateResponse(point);
        }
        if (response instanceof String) {
            String s = (String)response;
            try {
                StringTokenizer token = new StringTokenizer(s, ",");
                Point point = new Point(Integer.parseInt(token.nextToken()), Integer.parseInt(token.nextToken()));
                return this.validateResponse(point);
            }
            catch (Throwable e) {
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }

    private Boolean validateResponse(Point point) {
        if (point.distance(this.deformationCenter) <= this.tolerance.doubleValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}

