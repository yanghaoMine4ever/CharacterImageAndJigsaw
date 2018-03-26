package core.code;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.*;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

public class ControllerTwo {

    public FlowPane littlePics;
    public ImageView bigPic;
    public Label tip;
    private Image image; //大图
    private HashMap<Image, long[]> set = new HashMap<>(); //小图s
    private HashMap<Image, double[]> imageSet = new HashMap<>();
    private final int WIDTH = 10;
    private final int HEIGHT = 10;

    private boolean isRunning = false;

    public void chooseBigPic() throws Exception {
        Window stage = bigPic.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择图片");
        FxUtil.imageFileFilter(fileChooser);
        File pic = fileChooser.showOpenDialog(stage);
        if (pic == null) {
            return;
        }
        InputStream is = new FileInputStream(pic);
        image = new Image(is);
        bigPic.setImage(image);
        is.close();
    }

    public void chooseLittlePics() throws Exception {
        Window stage = bigPic.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择图片");
        FxUtil.imageFileFilter(fileChooser);
        List<File> fileList = fileChooser.showOpenMultipleDialog(stage);
        if (fileList == null || fileList.size() <= 0) {
            return;
        }
        for (File file : fileList) {
            InputStream is = new FileInputStream(file);
            Image image = new Image(is);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(30);
            imageView.setFitHeight(30);
            littlePics.getChildren().add(imageView);
            set.put(image, null);
            imageSet.put(image, null);
            is.close();
        }
    }

    public void start() throws IOException {
        if (!isRunning) {
            method2();
        }
    }

    private void method2() throws IOException {
        isRunning = true;
        Set<Map.Entry<Image, double[]>> entrySet = imageSet.entrySet();
        for (Map.Entry<Image, double[]> entry : entrySet) {
            Image image = entry.getKey();
            entry.setValue(FxUtil.weightedAvg(image));
        }
        double width = image.getWidth();
        double height = image.getHeight();
        Canvas canvas = new Canvas(width, height);
        GraphicsContext context = canvas.getGraphicsContext2D();
        for (int i = 0; i < height; i += HEIGHT) {
            for (int j = 0; j < width; j += WIDTH) {
                double w = j + WIDTH >= width ? width - j : WIDTH;
                double h = i + HEIGHT >= height ? height - i : HEIGHT;
                double[] ra = FxUtil.weightedAvg(image, j, i, w, h);
                Image _image = _min(ra);
                context.drawImage(_image, j, i, w, h);
            }
        }
        FileChooser fileChooser = new FileChooser();
        FxUtil.imageFileFilter(fileChooser);
        fileChooser.setTitle("保存到");
        fileChooser.setInitialFileName(System.currentTimeMillis() + ".png");
        File file = fileChooser.showSaveDialog(bigPic.getScene().getWindow());
        if(file == null) {return;}
        Image outImage = canvas.snapshot(null, null);
        ImageIO.write(SwingFXUtils.fromFXImage(outImage, null), "png", file);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "制作成功！");
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.showAndWait();
        isRunning = false;
    }

    private void method1() throws IOException {
        Set<Map.Entry<Image, long[]>> entrySet = set.entrySet();
        for (Map.Entry<Image, long[]> entry : entrySet) {
            Image image = entry.getKey();
            entry.setValue(FxUtil.colorHistogram(image));
        }
        double width = image.getWidth();
        double height = image.getHeight();
        Canvas canvas = new Canvas(width, height);
        GraphicsContext context = canvas.getGraphicsContext2D();
        for (int i = 0; i < height; i += HEIGHT) {
            for (int j = 0; j < width; j += WIDTH) {
                double w = j + WIDTH >= width ? width - j : WIDTH;
                double h = i + HEIGHT >= height ? height - i : HEIGHT;
                long[] xl = FxUtil.colorHistogram(image, j, i, w, h);
                Image _image = min(xl);
                context.drawImage(_image, j, i, w, h);
            }
        }
        Image outImage = canvas.snapshot(null, null);
        OutputStream os = new FileOutputStream("D:/" + System.currentTimeMillis() + ".png");
        ImageIO.write(SwingFXUtils.fromFXImage(outImage, null), "png", os);
        os.close();
    }

    private Image _min(double[] ra) {
        Set<Map.Entry<Image, double[]>> entrySet = imageSet.entrySet();
        double min = 0;
        Image minImage = null;
        for (Map.Entry<Image, double[]> entry : entrySet) {
            Image image = entry.getKey();
            double[] xl2 = entry.getValue();
            double d = FxUtil.rgbSimilar(ra, xl2);
            if (min == 0 || d < min) {
                min = d;
                minImage = image;
            }
        }
        return minImage;
    }

    private Image min(long[] xl) {
        Set<Map.Entry<Image, long[]>> entrySet = set.entrySet();
        double min = 2;
        Image minImage = null;
        for (Map.Entry<Image, long[]> entry : entrySet) {
            Image image = entry.getKey();
            long[] xl2 = entry.getValue();
            double d = FxUtil.cosSimilar(xl, xl2);
            if ((1 - d) < min) {
                min = 1 - d;
                minImage = image;
            }
        }
        return minImage;
    }
}
