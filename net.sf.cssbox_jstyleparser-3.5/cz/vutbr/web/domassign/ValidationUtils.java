/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.domassign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern AREA_REGEX = Pattern.compile("\\S+");

    public static String[] getAreas(String areasString) {
        ArrayList<String> areas = new ArrayList<String>();
        Matcher matcher = AREA_REGEX.matcher(areasString);
        while (matcher.find()) {
            areas.add(matcher.group());
        }
        return areas.toArray(new String[0]);
    }

    public static boolean containsRectangles(String[][] map) {
        boolean[][] boolMap;
        int height = map.length;
        if (height < 1) {
            return false;
        }
        int width = map[0].length;
        HashSet<String> knownAreas = new HashSet<String>();
        for (boolean[] column : boolMap = new boolean[height][width]) {
            Arrays.fill(column, false);
        }
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (boolMap[y][x]) continue;
                if (knownAreas.contains(map[y][x])) {
                    return false;
                }
                knownAreas.add(map[y][x]);
                int currWidth = ValidationUtils.getWidth(map, x, y);
                int currHeight = ValidationUtils.getHeight(map, x, y);
                if (ValidationUtils.isValidRectangle(map, x, y, currWidth, currHeight)) {
                    ValidationUtils.validateRectangle(boolMap, x, y, currWidth, currHeight);
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    private static boolean isValidRectangle(String[][] map, int x0, int y0, int width, int height) {
        String base = map[y0][x0];
        for (int x = x0 + 1; x < width; ++x) {
            for (int y = y0; y < height; ++y) {
                if (map[y][x].equals(base)) continue;
                return false;
            }
        }
        return true;
    }

    private static void validateRectangle(boolean[][] map, int x0, int y0, int width, int height) {
        for (int x = x0; x < x0 + width; ++x) {
            for (int y = y0; y < y0 + height; ++y) {
                map[y][x] = true;
            }
        }
    }

    private static int getWidth(String[][] map, int x, int y) {
        String base = map[y][x];
        int width = 1;
        while (++x < map[0].length && map[y][x].equals(base)) {
            ++width;
        }
        return width;
    }

    private static int getHeight(String[][] map, int x, int y) {
        String base = map[y][x];
        int height = 1;
        while (++y < map.length && map[y][x].equals(base)) {
            ++height;
        }
        return height;
    }
}

