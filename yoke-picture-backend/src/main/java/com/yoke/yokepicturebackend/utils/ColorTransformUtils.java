package com.yoke.yokepicturebackend.utils;

/**
 * 颜色转换工具类
 */
public class ColorTransformUtils {

    private ColorTransformUtils() {
        // 工具类不需要实例化
    }
    /**
     * 将非标准的颜色值格式化为标准的6位十六进制颜色值
     * @param color 输入的颜色字符串，支持格式：080e0, 0x080e0, 0800e0, 0x0800e0
     * @return 标准化的6位十六进制颜色值，格式：0xRRGGBB
     */
    public static String getStandardColor(String color) {
        // 去除可能存在的0x前缀和空格
        String input = color.replace("0x", "").replace(" ", "").toLowerCase();
        int length = input.length();

        System.out.println("处理输入: " + color + " -> " + input + " (长度: " + length + ")");

        // 处理3位长度的情况（如abc -> 0xaabbcc）
        if (length == 3) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                char c = input.charAt(i);
                result.append(c).append(c);
            }
            return "0x" + result.toString();
        }

        // 处理5位长度的情况（如080e0 -> 0800e0）
        if (length == 5) {
            // 在第三位后面插入0：080e0 -> 0800e0
            return "0x" + input.substring(0, 3) + "0" + input.substring(3);
        }

        // 处理6位长度的情况，直接返回
        if (length == 6) {
            return "0x" + input;
        }

        // 处理7位长度的情况（带0x前缀的5位值）
        if (length == 7 && color.startsWith("0x")) {
            String content = color.substring(2);
            if (content.length() == 5) {
                return "0x" + content.substring(0, 3) + "0" + content.substring(3);
            }
        }

        // 对于其他不支持的长度，返回错误信息
        return "错误: 不支持的颜色格式 - " + color;
    }

}


