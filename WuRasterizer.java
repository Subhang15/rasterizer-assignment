import java.util.ArrayList;
import java.util.List;

class WuRasterizer implements LineRasterizer {
    @Override
    public Point[] rasterize(Point p1, Point p2) {
        List<Point> points = new ArrayList<>();
        int x1 = p1.x;
        int y1 = p1.y;
        int x2 = p2.x;
        int y2 = p2.y;

        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);
        if (steep) {
            int temp = x1;
            x1 = y1;
            y1 = temp;
            temp = x2;
            x2 = y2;
            y2 = temp;
        }
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
            temp = y1;
            y1 = y2;
            y2 = temp;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;
        float gradient = (dx == 0) ? 1.0f : (float) dy / dx;

        // Integer endpoints: intensity is 1.0 at (x1, y1) and (x2, y2)
        if (steep) {
            points.add(new Point(y1, x1, 1.0f));
            points.add(new Point(y2, x2, 1.0f));
        } else {
            points.add(new Point(x1, y1, 1.0f));
            points.add(new Point(x2, y2, 1.0f));
        }

        float intery = y1 + gradient;
        int xpxl1 = x1;
        int xpxl2 = x2;

        // Main loop
        for (int x = xpxl1 + 1; x < xpxl2; x++) {
            int py = (int) Math.floor(intery);
            float f = intery - py;
            if (steep) {
                points.add(new Point(py, x, 1.0f - f));
                points.add(new Point(py + 1, x, f));
            } else {
                points.add(new Point(x, py, 1.0f - f));
                points.add(new Point(x, py + 1, f));
            }
            intery += gradient;
        }

        return points.toArray(new Point[0]);
    }
}
