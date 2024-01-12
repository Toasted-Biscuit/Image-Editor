import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class ImageEditorPanel extends JPanel implements KeyListener{

    Color[][] pixels;
    Color[][] reset;
    
    public ImageEditorPanel() {
        BufferedImage imageIn = null;
        try {
            JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(fc);
            imageIn = ImageIO.read(fc.getSelectedFile());
            addKeyListener(this);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        pixels = makeColorArray(imageIn);
        reset = makeColorArray(imageIn);
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
     
    public Color[][] flipHoriz(Color[][] oldPic) {
        Color[][] editPic = new Color[oldPic.length][oldPic[0].length];
        for (int r = 0; r < oldPic.length; r++) {
            for (int c = 0; c < oldPic[r].length; c++) {
                editPic[r][oldPic[r].length - 1 - c] = oldPic[r][c];
            }
        }
        return editPic;
    }

    public Color[][] flipVert(Color[][] oldPic) {
        Color[][] editPic = new Color[oldPic.length][oldPic[0].length];
        for (int r = 0; r < oldPic.length; r++) {
            for (int c = 0; c < oldPic[r].length; c++) {
                editPic[oldPic.length - 1 - r][c] = oldPic[r][c];
            }
        }
        return editPic;
    }

    public Color[][] grayScale(Color[][] oldPic) {
        Color[][] editPic = new Color[oldPic.length][oldPic[0].length];
        for (int r = 0; r < oldPic.length; r++) {
            for (int c = 0; c < oldPic[0].length; c++) {
                Color col = oldPic[r][c];
                double red = col.getRed() * 0.2126;
                double green = col.getGreen() * 0.7152;
                double blue = col.getBlue() * 0.0722;
                int gray = (int) (red + green + blue);
                editPic[r][c] = new Color (gray,gray,gray);
            } 
        }
        return editPic;
    }

    public Color[][] contrast(Color[][] oldPic) {
        Color[][] editPic = new Color[oldPic.length][oldPic[0].length];
        final double CONTRAST_MULT = 0.1;
        for (int r = 0; r < oldPic.length; r++) {
            for (int c = 0; c < oldPic[0].length; c++) {
                int red = oldPic[r][c].getRed();
                int green = oldPic[r][c].getGreen();
                int blue = oldPic[r][c].getBlue();
                // Logic decides if value should be brighter or darker
                if (red > 127) {
                    red = (int) (red * (1 + CONTRAST_MULT));
                    if (red > 255) {
                        red = 255;
                    }
                } else {
                    red = (int) (red * (1 - CONTRAST_MULT));
                    if (red < 0) {
                        red = 0;
                    }
                }
                if (green > 127) {
                    green = (int) (green * (1 + CONTRAST_MULT));
                    if (green > 255) {
                        green = 255;
                    }
                } else {
                    green = (int) (green * (1 - CONTRAST_MULT));
                    if (green < 0) {
                        green = 0;
                    }
                }
                if (blue > 127) {
                    blue = (int) (blue * (1 + CONTRAST_MULT));
                    if (blue > 255) {
                        blue = 255;
                    }
                } else {
                    blue = (int) (blue * (1 - CONTRAST_MULT));
                    if (blue < 0) {
                        blue = 0;
                    }
                }
                Color contrast = new Color(red, green, blue);
                editPic[r][c] = contrast;
            }
        }
        return editPic;
    }

    public Color[][] posterize(Color[][] oldPic) {
        Color[][] editPic = new Color[oldPic.length][oldPic[0].length];
        // Color Palette
        final Color DARK_BLUE = new Color(0, 21, 36);
        final Color TURQUOISE = new Color(21, 97, 109);
        final Color WHITE = new Color(255, 236, 209);
        final Color ORANGE = new Color(255, 125, 0);
        for (int r = 0; r < oldPic.length; r++) {
            for (int c = 0; c < oldPic[0].length; c++) {
                int red = oldPic[r][c].getRed();
                int green = oldPic[r][c].getGreen();
                int blue = oldPic[r][c].getBlue();
                // Finds the distance between pixel's color and the set colors
                double distDarkBlue = Math.sqrt(Math.pow((DARK_BLUE.getRed() - red), 2) +
                                               Math.pow((DARK_BLUE.getGreen() - green), 2) +
                                               Math.pow((DARK_BLUE.getBlue() - blue), 2));
                double distTurquoise = Math.sqrt(Math.pow((TURQUOISE.getRed() - red), 2) +
                                                Math.pow((TURQUOISE.getGreen() - green), 2) +
                                                Math.pow((TURQUOISE.getBlue() - blue), 2));
                double distWhite = Math.sqrt(Math.pow((WHITE.getRed() - red), 2) +
                                               Math.pow((WHITE.getGreen() - green), 2) +
                                               Math.pow((WHITE.getBlue() - blue), 2));
                double distOrange = Math.sqrt(Math.pow((ORANGE.getRed() - red), 2) +
                                             Math.pow((ORANGE.getGreen() - green), 2) +
                                             Math.pow((ORANGE.getBlue() - blue), 2));
                // Each statement checks if it is the lowest, if so, set the pixel to that color
                if (distDarkBlue < distTurquoise && distDarkBlue < distWhite && distDarkBlue < distOrange) {
                    editPic[r][c] = DARK_BLUE;
                } else if (distTurquoise < distDarkBlue && distTurquoise < distWhite && distTurquoise < distOrange) {
                    editPic[r][c] = TURQUOISE;
                } else if (distWhite < distDarkBlue && distWhite < distTurquoise && distWhite < distOrange) {
                    editPic[r][c] = WHITE;
                } else if (distOrange < distDarkBlue && distOrange < distTurquoise && distOrange < distWhite) {
                    editPic[r][c] = ORANGE;
                // If the distances are the same between colors, set it to the white color
                } else if (distDarkBlue == distTurquoise || distDarkBlue == distWhite || distDarkBlue == distOrange ||
                           distTurquoise == distWhite || distTurquoise == distOrange || distWhite == distOrange) {
                    editPic[r][c] = WHITE;
                }
            }
        }
        return editPic;
    }

    public Color[][] blur(Color[][] oldPic) {
        Color[][] editPic = new Color[oldPic.length][oldPic[0].length];
        // These values determine how many pixels you check in each direction
        // A value of 3 checks 3 pixels before and after the main pixel
        // Making one value higher than the other can cause a smear effect
        final int CHECK_HIEGHT = 5;
        final int CHECK_WIDTH = 5;
        for (int r = 0; r < editPic.length; r++) {
            for (int c = 0; c < editPic[r].length; c++) {
                int redSum = 0;
                int greenSum = 0;
                int blueSum = 0;
                int totalPixels = 0;
                // Checks each pixel around a pixel
                // If it checks offscreen it doesn't get counted
                for (int h = CHECK_HIEGHT * -1; h < CHECK_HIEGHT; h++) {
                    for (int w = CHECK_WIDTH * -1; w < CHECK_WIDTH; w++) {
                        if (r + h >= 0 && r + h < oldPic.length && c + w >= 0 && c + w < oldPic[r].length) {
                            redSum += oldPic[r + h][c + w].getRed();
                            greenSum += oldPic[r + h][c + w].getGreen();
                            blueSum += oldPic[r + h][c + w].getBlue();
                            totalPixels++;
                        }
                    }
                }
                Color average = new Color(redSum / totalPixels, greenSum / totalPixels, blueSum / totalPixels);
                editPic[r][c] = average;
            }
        }
        return editPic;
    }

    /**
     * Recieves a 2D array of colors and takes those values and randomly places them
     * on a 2D array of the same size. This scrambles the image.
     * @param oldPic the color array to be scrambled
     * @return the scrambled array
     */
    public Color[][] scramble(Color[][] oldPic) {
        // Makes array for new img and 2 booleans to check if a pixel has been used
        Color[][] editPic = new Color[oldPic.length][oldPic[0].length];
        boolean[][] oldPixelCheck = new boolean[oldPic.length][oldPic[0].length];
        boolean[][] editPixelCheck = new boolean[oldPic.length][oldPic[0].length];
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
            while (editPixelCheck[newR][newC]) {  // Checks if a pixel in that spot has been used, if so, try again
                newR = getRandom(0, oldPic.length - 1);
                newC = getRandom(0, oldPic[0].length - 1);
            }
            // Puts color from random spot on old img to random spot on new img
            editPic[newR][newC] = oldPic[oldR][oldC];
            oldPixelCheck[oldR][oldC] = true;
            editPixelCheck[newR][newC] = true;
            currentPixel++;
            // Progress meter
            if (currentPixel == (int) (totalPixels * progress * 0.01)) {
                System.out.println(progress + "% done scrambling");
                progress += 25;
            }
        }
        return editPic;
    }

    public int getRandom(int min, int max) {
        int range = (max - min) + 1;
        return (int) ((Math.random() * range) + min);
    }

    public void saveImg(Color[][] pic, String location) {
        // Creates an image using the color array
        BufferedImage img = new BufferedImage(pic[0].length, pic.length, 1);
        for (int r = 0; r < pic.length; r++) {
            for (int c = 0; c < pic[0].length; c++) {
                img.setRGB(c,r,pic[r][c].getRGB());
            }
        }
        // Saves the image to the specified location
        try {
            ImageIO.write(img, "png", new File(location + ".png"));
            System.out.println("Saved Image at " + location + ".png");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
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

    @Override
    public void keyTyped(KeyEvent e) {
        // Flip Horizontally
        if (e.getKeyChar() == 'h') {
            pixels = flipHoriz(pixels);
        }
        // Flip Vertially
        if (e.getKeyChar() == 'v') {
            pixels = flipVert(pixels);
        }
        // Grayscale
        if (e.getKeyChar() == 'g') {
            pixels = grayScale(pixels);
        }
        // Blur
        if (e.getKeyChar() == 'b') {
            pixels = blur(pixels);
        }
        // Scramble
        if (e.getKeyChar() == 'x') {
            pixels = scramble(pixels);
        }
        // Contrast
        if (e.getKeyChar() == 'c') {
            pixels = contrast(pixels);
        }
        // Posterize
        if (e.getKeyChar() == 'p') {
            pixels = posterize(pixels);
        }
        // Save Image
        if (e.getKeyChar() == 's') {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Where would you like to save the file?");
            fc.showSaveDialog(fc);
            saveImg(pixels, fc.getSelectedFile().toString());
        }
        // Reset Image
        if (e.getKeyChar() == 'r') {
            pixels = reset;
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
}
