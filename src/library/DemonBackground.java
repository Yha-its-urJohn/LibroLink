package library;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;
import javax.swing.*;

/** Dashboard background: checkered haori pattern, falling sakura petals, rising embers and a sword-slash streak. */
public class DemonBackground extends JPanel {
    private static final int PETALS = 34, EMBERS = 40;
    private final Random rnd = new Random();
    private final float[] px = new float[PETALS], py = new float[PETALS], ps = new float[PETALS],
            pv = new float[PETALS], pr = new float[PETALS], pw = new float[PETALS];
    private final float[] ex = new float[EMBERS], ey = new float[EMBERS], er = new float[EMBERS], ev = new float[EMBERS];
    private final Timer timer;
    private int frames = 0;

    public DemonBackground() {
        for (int i = 0; i < PETALS; i++) {
            px[i] = rnd.nextFloat();
            py[i] = rnd.nextFloat();
            ps[i] = 6 + rnd.nextFloat() * 7;
            pv[i] = 0.0006f + rnd.nextFloat() * 0.0012f;
            pr[i] = rnd.nextFloat() * 6;
            pw[i] = rnd.nextFloat() * 6;
        }
        for (int i = 0; i < EMBERS; i++) {
            ex[i] = rnd.nextFloat();
            ey[i] = rnd.nextFloat();
            er[i] = 1 + rnd.nextFloat() * 2.5f;
            ev[i] = 0.0008f + rnd.nextFloat() * 0.002f;
        }
        timer = new Timer(33, e -> { step(); repaint(); });
        timer.start();
    }

    private void step() {
        frames++;
        for (int i = 0; i < PETALS; i++) {
            py[i] += pv[i];
            px[i] += Math.sin(frames * 0.03 + pw[i]) * 0.0007;
            pr[i] += 0.03f;
            if (py[i] > 1.05f) { py[i] = -0.05f; px[i] = rnd.nextFloat(); }
        }
        for (int i = 0; i < EMBERS; i++) {
            ey[i] -= ev[i];
            if (ey[i] < -0.05f) { ey[i] = 1.05f; ex[i] = rnd.nextFloat(); }
        }
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g.setPaint(new GradientPaint(0, 0, new Color(10, 14, 20), 0, h, new Color(20, 40, 42)));
        g.fillRect(0, 0, w, h);

        // slowly drifting checkered pattern
        int s = 56, off = (int) ((frames * 0.3) % (2 * s));
        g.setColor(new Color(40, 90, 70, 28));
        for (int gx = -2 * s; gx < w + 2 * s; gx += s)
            for (int gy = -2 * s; gy < h + 2 * s; gy += s)
                if (Math.floorMod(gx / s + gy / s, 2) == 0) g.fillRect(gx + off, gy + off, s, s);

        // rising embers
        for (int i = 0; i < EMBERS; i++) {
            int a = Math.max(0, Math.min(255, (int) (220 * ey[i])));
            int r = (int) Math.max(1, er[i]);
            g.setColor(new Color(255, 150, 60, a));
            g.fillOval((int) (ex[i] * w) - r, (int) (ey[i] * h) - r, r * 2, r * 2);
        }

        // falling sakura petals
        for (int i = 0; i < PETALS; i++) {
            AffineTransform old = g.getTransform();
            g.translate(px[i] * w, py[i] * h);
            g.rotate(pr[i]);
            int sz = (int) ps[i];
            g.setColor(new Color(255, 183, 197, 200));
            g.fillOval(-sz / 2, -sz / 4, sz, Math.max(2, sz / 2));
            g.setTransform(old);
        }

        // sword-slash streak every ~12 seconds
        int m = frames % 360;
        if (m < 40) {
            float t = m / 40f;
            int x0 = (int) (-0.3f * w + 1.6f * w * t);
            g.setStroke(new BasicStroke(3f));
            g.setColor(new Color(190, 240, 255, (int) ((1 - t) * 200)));
            g.drawLine(x0, 0, x0 - (int) (h * 0.6f), h);
        }
        g.dispose();
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }
}
