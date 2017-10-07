package View.Components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

/**
 * Created by pamelaiupipeixinho on 07/10/17.
 * referece: @link http://java-swing-tips.blogspot.com.br/2015/08/change-indeterminate-jprogressbar.html
 */
public class StripedProgressBarUI extends BasicProgressBarUI {
    private final boolean dir;
    private final boolean slope;
    public StripedProgressBarUI(boolean dir, boolean slope) {
        super();
        this.dir = dir;
        this.slope = slope;
    }

    @Override protected int getBoxLength(int availableLength, int otherDimension) {
        return availableLength; //(int) Math.round(availableLength / 6d);
    }

    @Override public void paintIndeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D)) {
            return;
        }

        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth  = progressBar.getWidth() - b.right - b.left;
        int barRectHeight = progressBar.getHeight() - b.top - b.bottom;

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the striped box.
        boxRect = getBox(boxRect);
        if (boxRect != null) {
            int w = 10;
            int x = getAnimationIndex();
            GeneralPath p = new GeneralPath();
            if (dir) {
                p.moveTo(boxRect.x,           boxRect.y);
                p.lineTo(boxRect.x + w * .5f, boxRect.y + boxRect.height);
                p.lineTo(boxRect.x + w,       boxRect.y + boxRect.height);
                p.lineTo(boxRect.x + w * .5f, boxRect.y);
            } else {
                p.moveTo(boxRect.x,           boxRect.y + boxRect.height);
                p.lineTo(boxRect.x + w * .5f, boxRect.y + boxRect.height);
                p.lineTo(boxRect.x + w,       boxRect.y);
                p.lineTo(boxRect.x + w * .5f, boxRect.y);
            }
            p.closePath();
            g2.setColor(progressBar.getForeground());
            if (slope) {
                for (int i = boxRect.width + x; i > -w; i -= w) {
                    g2.fill(AffineTransform.getTranslateInstance(i, 0).createTransformedShape(p));
                }
            } else {
                for (int i = -x; i < boxRect.width; i += w) {
                    g2.fill(AffineTransform.getTranslateInstance(i, 0).createTransformedShape(p));
                }
            }
        }
    }
}