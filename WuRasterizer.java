import java.util.ArrayList;
import java.util.List;

class WuRasterizer implements LineRasterizer {

    @Override
    public Point[] rasterize(Point p1, Point p2) {
        List<Point> points = new ArrayList<>();
        float x1 = p1.x, y1 = p1.y;
        float x2 = p2.x, y2 = p2.y;

        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        if (steep) {
            float temp = x1; x1 = y1; y1 = temp;
            temp = x2; x2 = y2; y2 = temp;
        }
        if (x1 > x2) {
            float temp = x1; x1 = x2; x2 = temp;
            temp = y1; y1 = y2; y2 = temp;
        }

        float dx = x2 - x1;
        float dy = y2 - y1;
        float gradient = (dx == 0) ? 1.0f : dy / dx;

        int xend = Math.round(x1);
        float yend = y1 + gradient * (xend - x1);
        float xgap = 1.0f - fract(x1 + 0.5f);
        int xpxl1 = xend; 
        int ypxl1 = ipart(yend);

        if (steep) {
            addPoint(points, ypxl1, xpxl1, rfract(yend) * xgap);
            addPoint(points, ypxl1 + 1, xpxl1, fract(yend) * xgap);
        } else {
            addPoint(points, xpxl1, ypxl1, rfract(yend) * xgap);
            addPoint(points, xpxl1, ypxl1 + 1, fract(yend) * xgap);
        }
        float intery = yend + gradient;

        xend = Math.round(x2);
        yend = y2 + gradient * (xend - x2);
        xgap = fract(x2 + 0.5f);
        int xpxl2 = xend;
        int ypxl2 = ipart(yend);

        if (steep) {
            addPoint(points, ypxl2, xpxl2, rfract(yend) * xgap);
            addPoint(points, ypxl2 + 1, xpxl2, fract(yend) * xgap);
        } else {
            addPoint(points, xpxl2, ypxl2, rfract(yend) * xgap);
            addPoint(points, xpxl2, ypxl2 + 1, fract(yend) * xgap);
        }

        for (int x = xpxl1 + 1; x < xpxl2; x++) {
            if (steep) {
                addPoint(points, ipart(intery), x, rfract(intery));
                addPoint(points, ipart(intery) + 1, x, fract(intery));
            } else {
                addPoint(points, x, ipart(intery), rfract(intery));
                addPoint(points, x, ipart(intery) + 1, fract(intery));
            }
            intery += gradient;
        }

        return points.toArray(new Point[0]);
    }

    private void addPoint(List<Point> points, int x, int y, float intensity) {
        if (intensity > 0) {
            points.add(new Point(x, y, intensity));
        }
    }

    private int ipart(float x) { return (int) Math.floor(x); }
    private float fract(float x) { return x - (float) Math.floor(x); }
    private float rfract(float x) { return 1.0f - fract(x); }
}
