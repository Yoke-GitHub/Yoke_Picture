package com.yoke.yokepicturebackend.utils;

import java.awt.*;

/**
 * 工具类：计算颜色相似度
 */
public class ColorSimilarUtils {

    private ColorSimilarUtils() {
        // 工具类不需要实例化
    }

    /**
     * 计算两个颜色的相似度
     *
     * @param color1 第一个颜色
     * @param color2 第二个颜色
     * @return 相似度（0到1之间，1为完全相同）
     */
//    public static double calculateSimilarity(Color color1, Color color2) {
//        int r1 = color1.getRed();
//        int g1 = color1.getGreen();
//        int b1 = color1.getBlue();
//
//        int r2 = color2.getRed();
//        int g2 = color2.getGreen();
//        int b2 = color2.getBlue();
//
//        // 计算欧氏距离
//        double distance = Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
//
//        // 计算相似度
//        return 1 - distance / Math.sqrt(3 * Math.pow(255, 2));
//    }
//
//    /**
//     * 根据十六进制颜色代码计算相似度
//     *
//     * @param hexColor1 第一个颜色的十六进制代码（如 0xFF0000）
//     * @param hexColor2 第二个颜色的十六进制代码（如 0xFE0101）
//     * @return 相似度（0到1之间，1为完全相同）
//     */
//    public static double calculateSimilarity(String hexColor1, String hexColor2) {
//        Color color1 = Color.decode(hexColor1);
//        Color color2 = Color.decode(hexColor2);
//        return calculateSimilarity(color1, color2);
//    }
//
//    // 示例代码
//    public static void main(String[] args) {
//        // 测试颜色
//        Color color1 = Color.decode("0xFF0000");
//        Color color2 = Color.decode("0xFE0101");
//        double similarity = calculateSimilarity(color1, color2);
//
//        System.out.println("颜色相似度为：" + similarity);
//
//        // 测试十六进制方法
//        double hexSimilarity = calculateSimilarity("0xFF0000", "0xFE0101");
//        System.out.println("十六进制颜色相似度为：" + hexSimilarity);
//    }

    /**
     * 计算两个颜色之间的视觉相似度（基于CIE76标准）
     *
     * @param color1 RGB颜色值（格式0xRRGGBB）
     * @param color2 RGB颜色值（格式0xRRGGBB）
     * @return Delta E值，数值越小表示越相似
     */
    public static double calculateSimilarity(Color color1, Color color2) {

        int[] rgb1 = new int[]{color1.getRed(), color1.getGreen(), color1.getBlue()};
        int[] rgb2 = new int[]{color2.getRed(), color2.getGreen(), color2.getBlue()};
//        int[] rgb1 = extractRGBComponents(color1);
//        int[] rgb2 = extractRGBComponents(color2);

        double[] lab1 = rgbToLab(rgb1[0], rgb1[1], rgb1[2]);
        double[] lab2 = rgbToLab(rgb2[0], rgb2[1], rgb2[2]);

        return deltaE76(lab1, lab2);
    }

    // 提取RGB分量（忽略Alpha通道）
    private static int[] extractRGBComponents(int color) {
        return new int[]{
                (color >> 16) & 0xFF,
                (color >> 8) & 0xFF,
                color & 0xFF
        };
    }

    // RGB转Lab颜色空间
    private static double[] rgbToLab(int r, int g, int b) {
        double[] xyz = rgbToXyz(r, g, b);
        return xyzToLab(xyz[0], xyz[1], xyz[2]);
    }

    // RGB转XYZ颜色空间
    private static double[] rgbToXyz(int r8bit, int g8bit, int b8bit) {
        // 归一化并去除gamma校正
        double r = inverseGammaCorrection(r8bit / 255.0);
        double g = inverseGammaCorrection(g8bit / 255.0);
        double b = inverseGammaCorrection(b8bit / 255.0);

        // 使用sRGB转换矩阵
        double x = r * 0.4124564 + g * 0.3575761 + b * 0.1804375;
        double y = r * 0.2126729 + g * 0.7151522 + b * 0.0721750;
        double z = r * 0.0193339 + g * 0.1191920 + b * 0.9503041;

        // 转换为0-100范围
        return new double[]{x * 100, y * 100, z * 100};
    }

    // 逆gamma校正
    private static double inverseGammaCorrection(double component) {
        return (component <= 0.04045)
                ? component / 12.92
                : Math.pow((component + 0.055) / 1.055, 2.4);
    }

    // XYZ转Lab颜色空间（D65白点）
    private static double[] xyzToLab(double x, double y, double z) {
        final double Xn = 95.047;
        final double Yn = 100.000;
        final double Zn = 108.883;

        double xr = x / Xn;
        double yr = y / Yn;
        double zr = z / Zn;

        double fx = cubeRootOrLinear(xr);
        double fy = cubeRootOrLinear(yr);
        double fz = cubeRootOrLinear(zr);

        return new double[]{
                116 * fy - 16,    // L*
                500 * (fx - fy),  // a*
                200 * (fy - fz)   // b*
        };
    }

    // 立方根或线性变换
    private static double cubeRootOrLinear(double value) {
        final double epsilon = 0.008856; // (6/29)^3
        final double kappa = 903.3;     // (29/3)^3
        return (value > epsilon)
                ? Math.cbrt(value)
                : (kappa * value + 16) / 116;
    }

    // CIE76色差公式
    private static double deltaE76(double[] lab1, double[] lab2) {
        double dl = lab1[0] - lab2[0];
        double da = lab1[1] - lab2[1];
        double db = lab1[2] - lab2[2];
        return Math.sqrt(dl * dl + da * da + db * db);
    }

    /**
     * 快速RGB相似度判断（使用简单欧氏距离）
     *
     * @param threshold 相似度阈值（通常建议值：5-15）
     * @return 是否在阈值范围内
     */
    public static boolean isSimilarRGB(int color1, int color2, double threshold) {
        int[] c1 = extractRGBComponents(color1);
        int[] c2 = extractRGBComponents(color2);

        double distance = Math.sqrt(
                Math.pow(c1[0] - c2[0], 2) +
                        Math.pow(c1[1] - c2[1], 2) +
                        Math.pow(c1[2] - c2[2], 2)
        );
        return distance <= threshold;
    }

    // 示例代码
    public static void main(String[] args) {
        // 计算两个红色的相似度
        int red1 = 0xFF0000;
        int red2 = 0xFE0100;

// 使用Lab空间计算
        Color color1 = new Color(red1);
        Color color2 = new Color(red2);
        double deltaE = ColorSimilarUtils.calculateSimilarity(color1, color2);
        System.out.println("颜色相似度（ΔE）: " + deltaE);

// 使用RGB快速判断
        boolean isSimilar = ColorSimilarUtils.isSimilarRGB(red1, red2, 5.0);
        System.out.println("是否相似: " + isSimilar);
    }
}


