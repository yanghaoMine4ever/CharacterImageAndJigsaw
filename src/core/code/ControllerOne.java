package core.code;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControllerOne {
    @FXML
    public FlowPane picView;
    public ToggleGroup ggg;

    String[] arr={"M","N","H","Q","$","O","C","?","7",">","!",":","–",";","."};

    Set<Image> set = new HashSet(); //要转换的图片


    /**
     * 选择图片
     */
    public void choosePic() throws IOException {
        Window stage = picView.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择图片");
        FxUtil.imageFileFilter(fileChooser);
        List<File> pics = fileChooser.showOpenMultipleDialog(stage);
        addPicView(pics);
    }

    public void toTxts() throws Exception {
        if(set.size() <= 0) {
            return;
        }
        RadioButton button = (RadioButton) ggg.getSelectedToggle();
        String type = button.getText();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存到");
        File file = fileChooser.showSaveDialog(picView.getScene().getWindow());
        if(file == null) {
            return;
        }
        FileOutputStream fos = new FileOutputStream(file);
        Writer writer = new OutputStreamWriter(fos);
        if(type.equals("html")) {
            writer.write("<!DOCTYPE html>" +
                    "<html lang=\"en\">" +
                    "<head>" +
                    "    <meta charset=\"UTF-8\">" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">" +
                    "    <title>字符画</title>" +
                    "</head>" +
                    "<style>" +
                    ".a {font-size: 8px; font-family: Courier; background: #fff;white-space:nowrap; " +
//                    "-webkit-transform-origin-x: 0; -webkit-transform: scale(0.66);} " +
                    " .b {font-size: 8px;}" +
                    "</style><body class='a b'>\n");
        }
        for(Image image : set) {
            toTxt(image, writer, type);
            writer.write("</br>\n");
        }
        if(type.equals("html")) {
            writer.write("</body></html>");
        }
        set.clear();
        picView.getChildren().clear();
        fos.close();
    }

    private void toTxt(Image image, Writer writer, String type) throws Exception {
        double width = image.getWidth();
        double height = image.getHeight();
        PixelReader reader = image.getPixelReader();
        for(int j = 0; j < height; j += 12) {
            int h = j;
            for (int i = 0; i < width; i += 6) {
                int w = i;
                double gray = 0;
                for(int y = 0; y < 12; y++) {
                    for (int x = 0; x < 6; x++) {
                        Color color = reader.getColor(x + w >= width ? (int)width - 1 : x + w, y + h >= height ? (int)height - 1 : y + h);
                        gray += getGray(color);
                    }
                }
                gray /= 6 * 12;
                writer.write(toText(gray));
            }
            if(type.equals("html")) {
                writer.write("</br>");
            }
            writer.write("\n");
        }
        writer.flush();
    }

    private String toText(double g) {
        int index = (int)((g / 255) * (arr.length - 1));
        return arr[index];
    }

    private void addPicView(List<File> fileList) throws IOException {
        if(fileList == null || fileList.size() == 0) {
            return;
        }
        for (File file: fileList) {
            InputStream is = new FileInputStream(file);
            Image image = new Image(is);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            picView.getChildren().add(imageView);
            set.add(image);
            is.close();
        }
    }

    private double getGray(Color color) {
        return 0.299 * 255 * color.getRed() + 0.587 * 255 * color.getGreen() + 0.114 * 255 * color.getBlue();
    }

}
