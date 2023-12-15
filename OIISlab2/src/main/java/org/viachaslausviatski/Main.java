package org.viachaslausviatski;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    private static final double[][] Anaglyphs0 = {{0.299, 0.587, 0.114}, {0, 0, 0}, {0, 0, 0}};
    private static final double[][] Anaglyphs1 = {{0, 0, 0}, {0, 0, 0}, {0.299, 0.587, 0.114}};
    private static final double[][] Anaglyphs2 = {{0, 0, 0}, {0.299, 0.587, 0.114}, {0.299, 0.587, 0.114}};
    private static final double[][] Anaglyphs3 = {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}};
    private static final double[][] Anaglyphs4 = {{0, 0, 0}, {0, 1, 0}, {0, 0, 1}};
    private static final double[][] Anaglyphs5 = {{0, 0.7, 0.3}, {0, 0, 0}, {0, 0, 0}};

    public static void main(String[] args) {
        BufferedImage[] imgList = openFiles();
        allAnaglyphs(imgList);
    }
    private static BufferedImage[] openFiles() {
        BufferedImage[] imgList = new BufferedImage[3];
        try {
            imgList[0] = ImageIO.read(new File("src/main/java/org/viachaslausviatski/Picture10.png"));
            imgList[1] = ImageIO.read(new File("src/main/java/org/viachaslausviatski/Picture11.png"));
            imgList[2] = ImageIO.read(new File("src/main/java/org/viachaslausviatski/Picture12.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgList;
    }

    private static double[] matrixMultiplication(double[][] matrixFrom, double[] matrixTo) {
        double[] resultingMatrix = new double[3];
        for (int row = 0; row < 3; row++) {
            double tempResult = 0;
            for (int column = 0; column < 3; column++) {
                tempResult += matrixFrom[row][column] * matrixTo[column];
            }
            resultingMatrix[row] = tempResult;
        }
        return resultingMatrix;
    }

    private static double[] matrixAddition(double[] matrixFrom, double[] matrixTo) {
        double[] resultingMatrix = matrixTo.clone();
        for (int i = 0; i < matrixFrom.length; i++) {
            resultingMatrix[i] += matrixFrom[i];
        }
        return resultingMatrix;
    }

    private static BufferedImage anaglyphs(BufferedImage imgTo, BufferedImage imgFrom, double[][] anaglyphTo, double[][] anaglyphFrom) {
        int width = imgTo.getWidth();
        int height = imgTo.getHeight();
        BufferedImage processedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                // Check the coordinates before accessing pixels
                if (column < imgTo.getWidth() && row < imgTo.getHeight() && column < imgFrom.getWidth() && row < imgFrom.getHeight()) {
                    double[] resultArray = matrixAddition(
                            matrixMultiplication(anaglyphTo, getPixelColor(imgTo, column, row)),
                            matrixMultiplication(anaglyphFrom, getPixelColor(imgFrom, column, row))
                    );
                    setPixelColor(processedImage, column, row, resultArray);
                } else {
                    // Handle out-of-bounds case, such as skipping or filling with a default color
                }
            }
        }
        return processedImage;
    }

    private static double[] getPixelColor(BufferedImage image, int x, int y) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Check if coordinates are within bounds
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException("Coordinate out of bounds! x: " + x + ", y: " + y +
                    ", image width: " + width + ", image height: " + height);
        }

        try {
            int rgb = image.getRGB(x, y);
            double[] color = new double[3];
            color[0] = ((rgb >> 16) & 0xFF) / 255.0;
            color[1] = ((rgb >> 8) & 0xFF) / 255.0;
            color[2] = (rgb & 0xFF) / 255.0;
            return color;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Error getting pixel color at x: " + x + ", y: " + y, e);
        }
    }

    private static void setPixelColor(BufferedImage image, int x, int y, double[] color) {
        int r = (int) (color[0] * 255);
        int g = (int) (color[1] * 255);
        int b = (int) (color[2] * 255);
        int rgb = (r << 16) | (g << 8) | b;
        image.setRGB(x, y, rgb);
    }

    private static void allAnaglyphs(BufferedImage[] imgList) {
        BufferedImage halfAn = anaglyphs(imgList[0], imgList[1], Anaglyphs0, Anaglyphs4);
        BufferedImage colorAn = anaglyphs(imgList[0], imgList[1], Anaglyphs3, Anaglyphs4);
        BufferedImage optimizedAn = anaglyphs(imgList[0], imgList[1], Anaglyphs5, Anaglyphs4);

        try {
            ImageIO.write(halfAn, "png", new File("Result10.png"));
            ImageIO.write(colorAn, "png", new File("Result11.png"));
            ImageIO.write(optimizedAn, "png", new File("Result12.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}