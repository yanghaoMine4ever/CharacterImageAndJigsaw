package core.code;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaFx 工具类
 * @author zyh
 * @date 2018.3.25
 * @version v1.0
 */
public class FxUtil {

    public static void imageFileFilter(FileChooser fileChooser) {
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"));
    }


    /**
     * 根据颜色分布表 生成image的向量
     * @return 64个元素的数组，表示向量
     */
    public static long[] colorHistogram(Image image) {
        return colorHistogram(image, 0, 0, image.getWidth(), image.getHeight());
    }

    /**
     * 根据颜色分布表 生成image的向量
     * @return 64个元素的数组，表示向量
     */
    public static long[] colorHistogram(Image image, int x, int y, double width, double height) {
        long[] ch = new long[64];
        PixelReader reader = image.getPixelReader();
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = reader.getColor(x + j, y + i);
                int index = interval(color);
                ch[index] += 1;
            }
        }
        return ch;
    }

    /**
     * 计算两个向量余弦相似度
     * @param item1
     * @param item2
     * @return [-1 1]区间
     */
    public static double cosSimilar (long[] item1, long[] item2) {
        int len = item1.length;
        double top = 0, bottomLeft = 0, bottomRight = 0;
        for(int i = 0; i < len; i++) {
            top += (item1[i] * item2[i]);
            bottomLeft += (item1[i] * item1[i]);
            bottomRight += (item2[i] * item2[i]);
        }
        return top / (Math.sqrt(bottomLeft) * Math.sqrt(bottomRight));
    }

    /**
     * 计算两片图形rgb相似度，数值越小越接近
     * @param item1
     * @param item2
     * @return
     */
    public static double rgbSimilar(double[] item1, double[] item2) {
        return  Math.abs(item1[0] - item2[0]) + Math.abs(item1[1] - item2[1]) + Math.abs(item1[2] - item2[2]);
    }

    /**
     * 根据颜色直方图原理
     * 计算像素所在区间,共64个区间
     * @param color 像素颜色
     * @return 所在区间
     */
    public static int interval (Color color) {
        double red = color.getRed();
        double green = color.getGreen();
        double blue = color.getBlue();
        int b = (int)(blue * 255) / 64;
        int g = (int)(green * 255) / 64;
        int r = (int)(red * 255) / 64;
        return b + g * 4 + r * 16;
    }


    /**
     * 计算图片的像素（r, g, b)平均值
     * @param image 图片
     * @return 代表 r g b 三原色平均值的数组，length == 3
     */
    public static double[] rgbAvg (Image image) {
        return rgbAvg(image, 0, 0, image.getWidth(), image.getHeight());
    }

    /**
     * 计算图片指定区域内的像素（r, g, b)平均值
     * @param image 图片
     * @return 代表 r g b 三原色平均值的数组，length == 3
     */
    public static double[] rgbAvg(Image image, int x, int y, double width, double height) {
        double[] ra = new double[3];
        double r = 0, g = 0, b = 0;
        PixelReader reader = image.getPixelReader();
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = reader.getColor(x + j, y + i);
                r += (color.getRed() * 255);
                g += (color.getGreen() * 255);
                b += (color.getBlue() * 255);
            }
        }
        ra[0] = (r / (width * height));
        ra[1] = (g / (width * height));
        ra[2] = (b / (width * height));
        return ra;
    }

    public static double[] weightedAvg(Image image) {
        return weightedAvg(image, 0, 0, image.getWidth(), image.getHeight());
    }

    /**
     * 计算图片指定区域内的像素（r, g, b)加权均值
     * @param image 图片
     * @return 代表 r g b 三原色平均值的数组，length == 3
     */
    public static double[] weightedAvg (Image image, int x, int y, double width, double height) {
        double[] ra = new double[3];
        Map<Integer, Map<Double, Integer>> map = new HashMap<>();
        map.put(0, new HashMap<>());
        map.put(1, new HashMap<>());
        map.put(2, new HashMap<>());
        PixelReader reader = image.getPixelReader();
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = reader.getColor(x + j, y + i);
                double _r = color.getRed();
                double _g = color.getGreen();
                double _b = color.getBlue();
                if(map.get(0).get(_r) == null) {
                    map.get(0).put(_r, 1);
                } else {
                    map.get(0).put(_r, map.get(0).get(_r) + 1);
                }
                if(map.get(1).get(_g) == null) {
                    map.get(1).put(_g, 1);
                } else {
                    map.get(1).put(_g, map.get(1).get(_g) + 1);
                }
                if(map.get(2).get(_b) == null) {
                    map.get(2).put(_b, 1);
                } else {
                    map.get(2).put(_b, map.get(2).get(_b) + 1);
                }

            }
        }
        for(int i = 0; i < 3; i++) {
            double top = 0, bottom = 0;
            for(Map.Entry<Double, Integer> entry : map.get(i).entrySet()) {
                top += entry.getKey() * entry.getValue();
                bottom += entry.getValue();
            }
            ra[i] = top / bottom;
        }
        return ra;
    }

}
