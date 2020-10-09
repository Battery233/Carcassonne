package edu.cmu.cs.cs214.hw4.gui;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Tool class for image processing.
 */
public class TileImages {
    //location of the tile image and the check symbol
    private static final String FILE_NAME = "src/main/resources/Carcassonne.png";
    private static final String ICON_FILE = "src/main/resources/available.png";

    public static BufferedImage rotateClockwise(BufferedImage src, int n) {
        int weight = src.getWidth();
        int height = src.getHeight();

        AffineTransform at = AffineTransform.getQuadrantRotateInstance(n, weight / 2.0, height / 2.0);
        AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

        BufferedImage dest = new BufferedImage(weight, height, src.getType());
        op.filter(src, dest);
        return dest;
    }

    /**
     * get the image for a specific type of tile.
     * @param id the id string
     * @return the tile image.
     */
    public static BufferedImage getImageById(String id) {
        char index = id.charAt(0);
        BufferedImage image;
        try {
            image = ImageIO.read(new File(FILE_NAME));
        } catch (IOException e) {
            System.out.println("Image load failed");
            return null;
        }
        return image.getSubimage(((index - 65) % 6) * 90, ((index - 65) / 6) * 90, 90, 90);
    }

    public static BufferedImage withCircle(BufferedImage src, Color color, int x, int y, int radius) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        Graphics2D g = (Graphics2D) dest.getGraphics();
        g.drawImage(src, 0, 0, null);
        g.setColor(color);
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        g.dispose();

        return dest;
    }

    /**
     * get the location available icon.
     * @return the icon image
     */
    public static BufferedImage availableIcon() {
        BufferedImage icon = null;
        try {
            icon = ImageIO.read(new File(ICON_FILE));
        } catch (IOException e) {
            System.out.println("Available sign not found!");
        }
        return icon;
    }

    /**
     * Test if the image loading is successful.
     * @return image loading status
     */
    public boolean imageLoadSuccessful() {
        try {
            ImageIO.read(new File(FILE_NAME));
            ImageIO.read(new File(ICON_FILE));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
