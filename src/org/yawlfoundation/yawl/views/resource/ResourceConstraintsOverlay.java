package org.yawlfoundation.yawl.views.resource;

import org.imgscalr.Scalr;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLTask;
import org.yawlfoundation.yawl.editor.ui.net.NetGraph;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.OntologyQueryException;
import org.yawlfoundation.yawl.views.ontology.Triple;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 28/10/2016
 */
public class ResourceConstraintsOverlay {

    private static final String ICON_PATH = "/org/yawlfoundation/yawl/views/resource/icon/";
    private static final String FAM_TASK_ICON_NAME = "famTask.png";
    private static final String FOUR_EYES_ICON_NAME = "fourEyes.png";
    private NetGraph _graph;
    private Map<String, YAWLAtomicTask> _taskMap;            // [taskID, task]
    private Set<Triple> _familiarTaskSet;
    private Set<Triple> _fourEyesTaskSet;
    private Icon _famTaskIcon;
    private Icon _fourEyesIcon;


    public ResourceConstraintsOverlay(NetGraph graph, Map<String, YAWLAtomicTask> taskMap) {
        _graph = graph;
        _taskMap = taskMap;
        _familiarTaskSet = getFamiliarTasks(_graph);
        _fourEyesTaskSet = getFourEyesTasks(_graph);
    }


    public void paint(Graphics g) {
        if (hasConstraints()) overlayConstraints((Graphics2D) g, _graph.getScale());
    }


    private boolean hasConstraints() {
        return !(_familiarTaskSet.isEmpty() && _fourEyesTaskSet.isEmpty());
    }


    private void overlayConstraints(Graphics2D g, double scale) {
        Graphics2D gCopy = (Graphics2D) g.create();
        gCopy.setStroke(new BasicStroke(3 * (float) scale, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 0, new float[] { 5 * (float) scale }, 0));

        gCopy.setColor(Color.GREEN.darker());
        overlayConstraints(gCopy, _familiarTaskSet, scale, FAM_TASK_ICON_NAME);

        gCopy.setColor(Color.RED.darker());
        overlayConstraints(gCopy, _fourEyesTaskSet, scale, FOUR_EYES_ICON_NAME);

        gCopy.dispose();
    }


    private Rectangle2D getTaskBounds(String taskID) {
        YAWLTask task = (YAWLTask) _taskMap.get(taskID);
        return task != null ? task.getBounds() : null;
    }


    private Point2D[] getPathPoints(Rectangle2D T1, Rectangle2D T2, double scale) {
        Point2D.Double p1, p2, pb;
        if (T1.getMaxX() <= T2.getX()) {
            if (T2.getY() <= T1.getMaxY()) {
                // b, a
                p1 = new Point2D.Double(T1.getMaxX(), T1.getY());
                p2 = new Point2D.Double(T2.getX(), T2.getY());
                pb = new Point2D.Double((p1.getX() + p2.getX()) / 2,
                        (p1.getY() + p2.getY()) / 2 - (Math.abs(p2.getX() - p1.getX()) / 4));
            }
            else {
                // d, c
                p1 = new Point2D.Double(T1.getMaxX(), T1.getMaxY());
                p2 = new Point2D.Double(T2.getX(), T2.getMaxY());
                pb = new Point2D.Double((p1.getX() + p2.getX()) / 2,
                        (p1.getY() + p2.getY()) / 2 + (Math.abs(p2.getX() - p1.getX()) / 4));
            }
        }
        else {
            if (T2.getY() <= T1.getMaxY()) {
                // a, c
                p1 = new Point2D.Double(T1.getX(), T1.getY());
                p2 = new Point2D.Double(T2.getX(), T2.getMaxY());
            }
            else {
                // c, a
                p1 = new Point2D.Double(T1.getX(), T1.getMaxY());
                p2 = new Point2D.Double(T2.getX(), T2.getY());
            }
            pb = new Point2D.Double(
                    (p1.getX() + p2.getX()) / 2 - (Math.abs(p2.getY() - p1.getY()) / 4),
                    (p1.getY() + p2.getY()) / 2);
        }

        scalePoint(p1, scale);
        scalePoint(p2, scale);
        scalePoint(pb, scale);

        return new Point2D[] { p1, pb, p2 };
    }


    private Path2D getConstraintPath(Point2D[] points) {
        Path2D path = new Path2D.Double();
        path.moveTo(points[0].getX(), points[0].getY());
        path.curveTo(points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY());
        return path;
    }


    private void scalePoint(Point2D.Double point, double scale) {
        point.setLocation(point.getX() * scale, point.getY() * scale);
    }


    private Set<Triple> getFamiliarTasks(NetGraph graph) {
        return getConstraint(graph, "isFamiliarTaskOf");
    }


    private Set<Triple> getFourEyesTasks(NetGraph graph) {
        return getConstraint(graph, "isFourEyesTaskOf");
    }


    private Set<Triple> getConstraint(NetGraph graph, String predicate) {
        String netID = graph.getNetModel().getName() + "!";
        try {
            Set<Triple> queryResult =
                    OntologyHandler.query(predicate).getTriples();
            for (Triple triple : queryResult) {
                triple.removeSubjectPrefix(netID);
                triple.removeObjectPrefix(netID);
            }
            return queryResult;
        }
        catch (OntologyQueryException vqe) {
            return Collections.emptySet();
        }
    }


    private void overlayConstraints(Graphics2D g, Set<Triple> triples, double scale,
                                    String iconName) {
        for (Triple triple : triples) {
            Rectangle2D T1 = getTaskBounds(triple.getSubject());
            Rectangle2D T2 = getTaskBounds(triple.getObject());
            if (! (T1 == null || T2 == null)) {
                Point2D[] pathPoints = getPathPoints(T1, T2, scale);
                Path2D path = getConstraintPath(pathPoints);
                g.draw(path);

                boolean horizontal = isHorizontal(pathPoints);
                Point2D position = getIconPosition(path, horizontal);
                paintIcon(g, position, iconName, horizontal, scale);
            }
        }
    }


    private void paintIcon(Graphics2D g, Point2D location, String iconName,
                           boolean isHorizontal, double scale) {
        if (location != null) {
            Icon icon = getIcon(iconName, scale);
            if (icon != null) {
                int midIcon = icon.getIconHeight() / 2;
                int x = (int) location.getX();
                int y = (int) location.getY();
                if (isHorizontal) {
                    y -= midIcon;
                }
                else {
                    x -= midIcon;
                }
                icon.paintIcon(null, g, x, y);
            }
        }
    }


    private Icon getIcon(String iconName, double scale) {
        Icon icon = null;
        if (iconName.equals(FAM_TASK_ICON_NAME)) {
            icon = getFamTaskIcon();
        }
        if (iconName.equals(FOUR_EYES_ICON_NAME)) {
            icon = getFourEyesIcon();
        }
        if (scale != 1.0) {
            icon = resizeIcon(icon, scale);
        }
        return icon;
    }


    private boolean isHorizontal(Point2D[] pathPoints) {
        Point2D start = pathPoints[0];
        Point2D end = pathPoints[pathPoints.length - 1];
        double diffX = Math.abs(start.getX() - end.getX());
        double diffY = Math.abs(start.getY() - end.getY());
        return diffX >= diffY;
    }


    private Icon resizeIcon(Icon icon, double scale) {
        BufferedImage image = toBufferedImage(((ImageIcon) icon).getImage());
        image = Scalr.resize(image, (int) (16 * scale));
        return image != null ? new ImageIcon(image) : icon;
    }


    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bImg = new BufferedImage(img.getWidth(null),
                img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bImg.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bImg;
    }



    private Icon getFamTaskIcon() {
        if (_famTaskIcon == null) {
            _famTaskIcon = loadIcon(FAM_TASK_ICON_NAME);
        }
        return _famTaskIcon;
    }


    private Icon getFourEyesIcon() {
        if (_fourEyesIcon == null) {
            _fourEyesIcon = loadIcon(FOUR_EYES_ICON_NAME);
        }
        return _fourEyesIcon;
    }


    private Icon loadIcon(String iconFileName) {
        try {
            String path = ICON_PATH + iconFileName;
            InputStream is = this.getClass().getResourceAsStream(path);
            BufferedImage image = ImageIO.read(is);
            if (image != null) {
                return new ImageIcon(image);
            }
        }
        catch (IOException ignore) {

        }
        return null;
    }


    // since the curved path has not yet appeared on the canvas, here it is reproduced
    // in a BufferedImage, so that the precise y-coord mid-line can be found
    private Point2D getIconPosition(Path2D path, boolean isHorizontal) {
        try {
            // create the Image
            Rectangle rect = path.getBounds();
            int width = rect.x + rect.width;
            int height = rect.y + rect.height;
            BufferedImage bi = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);

            // ... and draw the path on it
            Graphics2D g = bi.createGraphics();
            g.setBackground(Color.white);
            g.setColor(Color.GREEN.darker());
            int pathRGB = g.getColor().getRGB();
            g.setStroke(new BasicStroke(3));
            g.draw(path);
            g.dispose();

            if (isHorizontal) {

                // starting at min-y, test each y at midline-x for the curve color
                int midX = rect.x + (rect.width / 2);
                for (int y = 0; y < rect.y + rect.height; y++) {
                    if (y < 0) continue;
                    int pixelRGB = bi.getRGB(midX, y);

                    // if the current pixel is same color as curve, return this point
                    if (pathRGB == pixelRGB) {
                        return new Point2D.Double(midX, y);
                    }
                }
            }
            else {
                int midY = rect.y + (rect.height / 2);
                 for (int x = 0; x < rect.x + rect.width; x++) {
                     if (x < 0) continue;
                     int pixelRGB = bi.getRGB(x, midY);

                     // if the current pixel is same color as curve, return this point
                     if (pathRGB == pixelRGB) {
                         return new Point2D.Double(x, midY);
                     }
                 }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
