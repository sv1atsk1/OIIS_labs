package org.viachaslausviatski;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        try {

            BufferedImage originalImage = ImageIO.read(new File("src/main/java/org/viachaslausviatski/jason.jpg"));

            BufferedImage medianFiltered = applyMedianFilter(originalImage);

            BufferedImage equalizedImage = equalizeHistogram(medianFiltered);

            BufferedImage edgeDetectedImage = detectEdges(equalizedImage);

            ImageIO.write(medianFiltered, "png", new File("medianFiltered.png"));
            ImageIO.write(equalizedImage, "png", new File("equalizedImage.png"));
            ImageIO.write(edgeDetectedImage, "png", new File("edgeDetectedImage.png"));

            System.out.println("Обработка завершена успешно.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage applyMedianFilter(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int[] values = new int[9];
                int index = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        values[index++] = image.getRGB(x + j, y + i) & 0xFF;
                    }
                }

                java.util.Arrays.sort(values);

                int median = values[4];
                result.setRGB(x, y, new Color(median, median, median).getRGB());
            }
        }

        return result;
    }

    private static BufferedImage equalizeHistogram(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int totalPixels = width * height;

        int[] histogram = new int[256];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = image.getRGB(x, y) & 0xFF;
                histogram[pixelValue]++;
            }
        }

        int[] cumulativeHistogram = new int[256];
        cumulativeHistogram[0] = histogram[0];
        for (int i = 1; i < 256; i++) {
            cumulativeHistogram[i] = cumulativeHistogram[i - 1] + histogram[i];
        }

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = image.getRGB(x, y) & 0xFF;
                int equalizedValue = (cumulativeHistogram[pixelValue] * 255) / totalPixels;
                result.setRGB(x, y, new Color(equalizedValue, equalizedValue, equalizedValue).getRGB());
            }
        }

        return result;
    }

    private static BufferedImage detectEdges(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0, gy = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int pixelValue = image.getRGB(x + j, y + i) & 0xFF;
                        gx += sobelX[i + 1][j + 1] * pixelValue;
                        gy += sobelY[i + 1][j + 1] * pixelValue;
                    }
                }

                int gradient = (int) Math.sqrt(gx * gx + gy * gy);

                gradient = Math.min(255, Math.max(0, gradient));

                result.setRGB(x, y, new Color(gradient, gradient, gradient).getRGB());
            }
        }

        return result;
    }
}