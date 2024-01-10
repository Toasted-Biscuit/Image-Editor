import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.*;

public class ImageEditorPanel extends JPanel {

    Color[][] pixels;
    
    public ImageEditorPanel() {
        BufferedImage imageIn = null;
        try {
            // the image should be in the main project folder, not in \src or \bin
            imageIn = ImageIO.read(new File("Aurora_Borealis.png"));
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        pixels = makeColorArray(imageIn);
        setPreferredSize(new Dimension(pixels[0].length, pixels.length));
        setBackground(Color.BLACK);
    }

    public void paintComponent(Graphics g) {
        // paints the array pixels onto the screen
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                g.setColor(pixels[row][col]);
                g.fillRect(col, row, 1, 1);
            }
        }
    }

    public void run() {
        // call your image-processing methods here OR call them from keyboard event
        // handling methods
        // write image-processing methods as pure functions - for example: pixels =
        // flip(pixels);

        // pixels = flipHoriz(pixels);
        // pixels = flipVert(pixels);
        // pixels = grayScale(pixels);
        pixels = scramble(pixels);
        repaint();
    }

    public Color[][] flipHoriz(Color[][] oldPic) {
        Color[][] flipped = new Color[oldPic.length][oldPic[0].length];
        for (int r = 0; r < oldPic.length; r++) {
            for (int c = 0; c < oldPic[r].length; c++) {
                flipped[r][oldPic[r].length - 1 - c] = oldPic[r][c];
            }
        }
        return flipped;
    }

    public Color[][] flipVert(Color[][] oldPic) {
        Color[][] flipped = new Color[oldPic.length][oldPic[0].length];
        for (int r = 0; r < oldPic.length; r++) {
            for (int c = 0; c < oldPic[r].length; c++) {
                flipped[oldPic.length - 1 - r][c] = oldPic[r][c];
            }
        }
        return flipped;
    }

    public Color[][] grayScale(Color[][] oldPic) {
        Color[][] grayScale = new Color[oldPic.length][oldPic[0].length];
        for (int r = 0; r < oldPic.length; r++) {
            for (int c = 0; c < oldPic[0].length; c++) {
                Color col = oldPic[r][c];
                double red = col.getRed() * 0.2126;
                double green = col.getGreen() * 0.7152;
                double blue = col.getBlue() * 0.0722;
                int gray = (int) (red + green + blue);
                grayScale[r][c] = new Color (gray,gray,gray);
            } 
        }
        return grayScale;
    }

    /**
     * Recieves a 2D array of colors and takes those values and randomly places them
     * on a 2D array of the same size. This scrambles the image.
     * @param oldPic the color array to be scrambled
     * @return the scrambled array
     */
    public Color[][] scramble(Color[][] oldPic) {
        // Makes array for new img and 2 booleans to check if a pixel has been used
        Color[][] scrambled = new Color[oldPic.length][oldPic[0].length];
        boolean[][] oldPixelCheck = new boolean[oldPic.length][oldPic[0].length];
        boolean[][] scrambledPixelCheck = new boolean[oldPic.length][oldPic[0].length];
        // variables
        int totalPixels = oldPic.length * oldPic[0].length;
        int currentPixel = 0;
        int progress = 25;
        // logic
        while (currentPixel < totalPixels) {
            int oldR = getRandom(0, oldPic.length - 1);
            int oldC = getRandom(0, oldPic[0].length - 1);
            int newR = getRandom(0, oldPic.length - 1);
            int newC = getRandom(0, oldPic[0].length - 1);
            while (oldPixelCheck[oldR][oldC]) {  // Checks if a pixel in that spot has been used, if so, try again
                oldR = getRandom(0, oldPic.length - 1);
                oldC = getRandom(0, oldPic[0].length - 1);
            }
            while (scrambledPixelCheck[newR][newC]) {  // Checks if a pixel in that spot has been used, if so, try again
                newR = getRandom(0, oldPic.length - 1);
                newC = getRandom(0, oldPic[0].length - 1);
            }
            // Puts color from random spot on old img to random spot on new img
            scrambled[newR][newC] = oldPic[oldR][oldC];
            oldPixelCheck[oldR][oldC] = true;
            scrambledPixelCheck[newR][newC] = true;
            currentPixel++;
            // Progress meter
            if (currentPixel == (int) (totalPixels * progress * 0.01)) {
                System.out.println(progress + "% done scrambling");
                progress += 25;
            }
        }
        return scrambled;
    }

    public int getRandom(int min, int max) {
        int range = (max - min) + 1;
        return (int) ((Math.random() * range) + min);
    }

    public Color[][] makeColorArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[height][width];
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color c = new Color(image.getRGB(col, row), true);
                result[row][col] = c;
            }
        }
        // System.out.println("Loaded image: width: " +width + " height: " + height);
        return result;
    }
}