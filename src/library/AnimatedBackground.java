package library;

import java.awt.*;
import java.util.Random;
import javax.swing.*;

/** Animated background that slowly fades between Shadow (violet) and Flame (fire) on its own. */
public class AnimatedBackground extends JPanel {
    private static final int N = 70;
    private final Random rnd = new Random();
    private final float[] x = new float[N], y = new float[N], r = new float[N], v = new float[N];
    private final Timer timer;
    private float mix = 0, target = 0, pulse = 0;   // mix: 0 = Shadow, 1 = Flame
    private int frames = 0;

    public AnimatedBackground() {
        for (int i = 0; i < N; i++) spawn(i, true);
        timer = new Timer(33, e -> { step(); repaint(); });
        timer.start();
    }

    private void spawn(int i, boolean anywhere) {
        x[i] = rnd.nextFloat();
        y[i] = anywhere ? rnd.nextFloat() : 1.05f;
        r[i] = 2 + rnd.nextFloat() * 5;
        v[i] = 0.0008f + rnd.nextFloat() * 0.0025f;
    }

    private void step() {
        pulse += 0.03f;
        frames++;
        if (frames % 300 == 0) target = 1 - target;                       // switch mood every ~10 seconds
        mix += Math.signum(target - mix) * Math.min(0.01f, Math.abs(target - mix));   // smooth fade
        for (int i = 0; i < N; i++) {
            y[i] -= v[i] * (1f + 0.6f * mix);
            x[i] += Math.sin(pulse + r[i]) * 0.0006f;
            if (y[i] < -0.05f) spawn(i, false);
        }
    }

    private Color blend(Color a, Color b, int alpha) {
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * mix),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * mix),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * mix),
                alpha);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g.setPaint(new GradientPaint(0, 0, blend(new Color(8, 10, 32), new Color(30, 6, 8), 255),
                0, h, blend(new Color(58, 18, 110), new Color(120, 20, 10), 255)));
        g.fillRect(0, 0, w, h);

        float glow = 0.55f + 0.25f * (float) Math.sin(pulse);
        Color gc = new Color(140, 90, 255), gf = new Color(255, 90, 30);
        g.setPaint(new RadialGradientPaint(w / 2f, h * 1.05f, Math.max(1, w * 0.6f), new float[]{0f, 1f},
                new Color[]{blend(gc, gf, (int) (110 * glow)), blend(gc, gf, 0)}));
        g.fillRect(0, 0, w, h);

        Color pc = new Color(170, 140, 255), pf = new Color(255, 150, 60);
        for (int i = 0; i < N; i++) {
            int a = Math.max(0, Math.min(255, (int) (240 * y[i])));
            int px = (int) (x[i] * w), py = (int) (y[i] * h);
            int big = (int) (r[i] * 2.2f), small = (int) Math.max(2, r[i] * 0.6f);
            g.setColor(blend(pc, pf, a / 5));
            g.fillOval(px - big, py - big, big * 2, big * 2);
            g.setColor(blend(pc, pf, a));
            g.fillOval(px - small, py - small, small * 2, small * 2);
        }
        g.dispose();
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }
}
