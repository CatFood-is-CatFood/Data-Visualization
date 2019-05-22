import java.awt.*;
import java.util.ArrayList;

class Canvas {
    int x = 1100, y = 550;
    Color bgColor = Color.WHITE;
    Color color = Color.BLACK;
}

class Formats {
    double[] margins = {0.15, 0.15, 0.1, 0.05};  // NORTH, SOUTH, WEST, EAST
    boolean isBarFilled = true;
    boolean hasBarFrame = true;
    Color barFrameColor = Color.BLACK;
    boolean hasBorder = true;
    Color borderColor = Color.BLACK;
    Color rulerColor = Color.BLACK;
    Color rulerMarkColor = Color.BLACK;
    boolean hasUpRuler = true;
    Color keyColor = Color.BLACK;
    boolean hasHeader = true;
    Color headerColor = Color.BLACK;
    boolean hasFooter = true;
    boolean hasCreators = true;
    Color footerColor = Color.BLACK;
    boolean hasText = true;
    Color TextColor = Color.BLACK;
    boolean plotRuler_background = true;
    boolean numberformat = true;
    int Number = 1;
    Font plotKey_font = new Font("consolas", Font.PLAIN, 12);
    Font plotRuler_font = new Font("consolas", Font.PLAIN, 12);
    Font plotUpRuler_font = new Font("consolas", Font.PLAIN, 12);
    Font plotHeader_font = new Font("calibri", Font.PLAIN, 40);
    Font plotFooter_font = new Font("consolas", Font.BOLD, 16);
    Font plotText_font = new Font("calibri", Font.PLAIN, 40);
}

class HistogramData {
    String header = "";
    String footer = "";
    String Creators = "";
    double minValue = 0.0;
    DATA[] input = {};
    DATA[] graft = {};
    int askNumber = 10;
    String[] texts = {};
}

class DATA {
    String key;
    double[] values;
    Color barFillcolor = new Color(86, 126, 196);
    double[] index = {};

    public DATA(String key, double[] values, Color barFillcolor) {
        this.key = key;
        this.values = values;
        this.barFillcolor = barFillcolor;
    }

    public DATA(String key, double[] values, Color barFillcolor, double[] index) {
        this.key = key;
        this.values = values;
        this.barFillcolor = barFillcolor;
        this.index = index;
    }

    static DATA[] Create(DATA[] n) {
        DATA[] m = n;
        for (int i = 0; i < n.length; i++) {
            double a[] = new double[40 * (n[i].values.length - 1) + 1];
            double b[] = new double[40 * (n[i].index.length - 2) + 2];
            for (int j = 0; j < n[i].values.length - 1; j++) {
                a[40 * j] = n[i].values[j];
                b[40 * j] = n[i].index[j];
                for (int k = 1; k < 40; k++) {
                    a[40 * j + k] = n[i].values[j] + k / 40.0 * (n[i].values[j + 1] - n[i].values[j]);
                    b[40 * j + k] = n[i].index[j] + k / 40.0 * (n[i].index[j + 1] - n[i].index[j]);
                }
            }
            a[40 * (n[i].values.length - 1)] = n[i].values[n[i].values.length - 1];
            b[40 * (n[i].values.length - 1)] = n[i].index[n[i].values.length - 1];
            b[40 * (n[i].values.length - 1) + 1] = n[i].index[n[i].values.length - 1];
            n[i].values = a;
            n[i].index = b;
        }
        return m;
    }

    static DATA[] compareTo(DATA[] n, int times) {
        for (int i = 0; i < n.length; i++) {
            for (int j = n.length - 1; j > i; j--) {
                if (n[j].values[times] < n[j - 1].values[times]) {
                    DATA a = n[j];
                    n[j] = n[j - 1];
                    n[j - 1] = a;
                }
            }
        }
        return n;
    }

    static DATA[] Preparation(DATA[] n, int times, int askNumber) {
        ArrayList<DATA> a = new ArrayList<DATA>();
        for (int i = 0; i < n.length; i++) {
            if (n[i].values[times] != 0) {
                double[] b = {n[i].values[times]};
                DATA c = new DATA(n[i].key, b, n[i].barFillcolor, n[i].index);
                a.add(c);
            }
        }
        DATA[] d = new DATA[a.size()];
        for (int i = 0; i < a.size(); i++)
            d[i] = a.get(i);
        d = compareTo(d, 0);
        DATA[] D = new DATA[askNumber];
        for (int i = 0; i < askNumber; i++)
            D[askNumber - i - 1] = d[a.size() - i - 1];
        return D;
    }
}

public class HistogramA {
    Canvas c;
    Formats f;
    HistogramData d;
    double[] yValue;  // MIN, MAX
    double[] xValue;  // MIN, MAX
    double[] yScale;  // MIN, MAX
    double[] xScale;  // MIN, MAX
    int rulerGrade;
    double rulerStep;
    int index[][] = {};

    public HistogramA(Canvas c, Formats f, HistogramData d, int times) {
        this.c = c;
        this.f = f;
        this.d = d;
        yValue = new double[2];
        xValue = new double[2];
        yScale = new double[2];
        xScale = new double[2];
        setHistogramParameters(times);
    }

    public void draw(int times, String background) {
        int n = d.graft.length;
        setHistogramScale(n);
        plotBars(times, background);
        plotRuler();
        plotKeys(times);
        if (f.hasBorder) plotBorder();
        if (f.hasUpRuler) plotUpRuler();
        if (f.hasHeader) plotHeader();
        if (f.hasFooter) plotFooter();
    }

    private void setHistogramParameters(int times) {
        for (int i = 0; i < d.input.length; i++)
            d.input[i].index = new double[d.input[i].values.length + 1];
        for (int i = 0; i < d.input[0].values.length; i++) {
            DATA[] between = DATA.compareTo(d.input, i);
            for (int j = 0; j < d.input.length; j++) {
                for (int k = 0; k < d.input.length; k++) {
                    if (d.input[j].key.equals(between[k].key)) {
                        d.input[j].index[i] = k;
                        if (k < d.input.length - d.askNumber)
                            d.input[j].index[i] = d.input.length - d.askNumber - 1;
                    }
                }
            }
        }
        for (int i = 0; i < d.input.length; i++)
            d.input[i].index[d.input[i].values.length] = d.input[i].index[d.input[i].values.length - 1];
        d.input = DATA.Create(d.input);
        d.graft = DATA.Preparation(d.input, times, d.askNumber);
        double[] a = new double[d.graft.length];
        for (int i = 0; i < d.graft.length; i++)
            a[i] = d.graft[i].values[0];

        yValue[MIN] = -1;
        yValue[MAX] = a.length;

        xValue[MIN] = 0;

        double max = a[0];
        for (int i = 1; i < a.length; i++)
            if (max < a[i]) max = a[i];

        double span = max - xValue[MIN];
        double factor = 1.0;
        if (span >= 1)
            while (span >= 10) {
                span /= 10;
                factor *= 10;
            }
        else
            while (span < 1) {
                span *= 10;
                factor /= 10;
            }
        int nSpan = (int) Math.ceil(span);
        xValue[MAX] = xValue[MIN] + factor * nSpan;
        switch (nSpan) {
            case 1:
                rulerGrade = 5;
                rulerStep = factor / 5;
                break;
            case 2:
            case 3:
                rulerGrade = nSpan * 2;
                rulerStep = factor / 2;
                break;
            default:
                rulerGrade = nSpan;
                rulerStep = factor;
                break;
        }
    }

    private void setHistogramScale(int nBars) {
        double span = xValue[MAX] - xValue[MIN] + 1;
        double xSpacing = span / (1 - f.margins[NORTH] - f.margins[SOUTH]);
        xScale[MIN] = xValue[MIN] - f.margins[SOUTH] * xSpacing - 1;
        xScale[MAX] = xValue[MAX] + f.margins[NORTH] * xSpacing;
        StdDraw.setXscale(xScale[MIN], xScale[MAX]);

        double ySpacing = (nBars + 1) / (1 - f.margins[WEST] - f.margins[EAST]);
        yScale[MIN] = -f.margins[WEST] * ySpacing - 1;
        yScale[MAX] = nBars * 1.05 + f.margins[EAST] * ySpacing;
        StdDraw.setYscale(yScale[MIN], yScale[MAX]);
    }

    private void plotBars(int times, String background) {
        StdDraw.picture(.5 * (xScale[MIN] + xScale[MAX]), .5 * (yScale[MIN] + yScale[MAX]), background,
                (-xScale[MIN] + xScale[MAX]), (-yScale[MIN] + yScale[MAX]));
        double[] a = new double[d.graft.length];
        for (int i = 0; i < d.graft.length; i++) {
            a[i] = d.graft[i].values[0];
        }
        int n = a.length;
        //setHistogramScale(n);

        if (f.plotRuler_background) {
            StdDraw.setPenColor(f.rulerColor);
            final double y0 = yValue[MIN], y1 = yValue[MAX];
            for (int i = 0; i <= rulerGrade; i++) {
                double x = xValue[MIN] + i * rulerStep;
                if (x / (a[a.length - 1] - d.minValue) * xValue[MAX] <= xValue[MAX])
                    StdDraw.line(x / (a[a.length - 1] - d.minValue) * xValue[MAX], y0, x / (a[a.length - 1] - d.minValue) * xValue[MAX], y1);
            }
        }

        if (f.isBarFilled) {
            for (int i = 0; i < n; i++) {
                StdDraw.setPenColor(d.graft[i].barFillcolor);
                if ((a[i] - 0) > 0) {
                    if (d.graft[i].index[times] > d.graft.length - d.askNumber - 1 && a[i] - d.minValue > 0)
                        StdDraw.filledRectangle((a[i] - d.minValue) / 2 / (a[a.length - 1] - d.minValue) * xValue[MAX],
                                d.graft[i].index[times] - d.input.length + d.askNumber, (a[i] - d.minValue) / 2 / (a[a.length - 1] - d.minValue) * xValue[MAX], 0.25);
                    // (x, y, halfWidth, halfHeight)
                }
            }
        }

        if (f.hasBarFrame) {
            StdDraw.setPenColor(f.barFrameColor);
            for (int i = 0; i < n; i++) {
                if ((a[i] - 0) > 8) {
                    if (d.graft[i].index[times] > d.graft.length - d.askNumber - 1 && a[i] - d.minValue > 0)
                        StdDraw.rectangle((a[i] - d.minValue) / 2 / (a[a.length - 1] - d.minValue) * xValue[MAX],
                                d.graft[i].index[times] - d.input.length + d.askNumber, (a[i] - d.minValue) / 2 / (a[a.length - 1] - d.minValue) * xValue[MAX], 0.25);
                    // (x, y, halfWidth, halfHeight)
                }
            }
        }
        if (f.hasText) {
            StdDraw.setFont(f.plotText_font);
            StdDraw.setPenColor(f.TextColor);
            StdDraw.text(xValue[MAX] - rulerStep / 2, 0.5, d.texts[times / 40]);
        }
    }

    private void plotRuler() {
        StdDraw.setFont(f.plotRuler_font);
        StdDraw.setPenColor(f.rulerColor);
        final double y0 = yValue[MIN] - 0.05, y1 = yValue[MIN] + 0.05;
        String[] mark = new String[rulerGrade + 1];
        for (int i = 0; i <= rulerGrade; i++) {
            double x = xValue[MIN] + i * rulerStep;
            mark[i] = numberForRuler(x);
            if (x / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX] <= xValue[MAX])
                StdDraw.line(x / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX], y0, x / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX], y1);
        }
        int len = maxMarkLength(mark);
        final double y = yScale[MIN] + 0.7 * (yValue[MIN] - yScale[MIN]);
        StdDraw.setPenColor(f.rulerMarkColor);
        for (int i = 0; i <= rulerGrade; i++) {
            double xs = xValue[MIN] + i * rulerStep;
            if (xs / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX] <= xValue[MAX])
                StdDraw.text(xs / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX], y, String.format("%" + len + "s", mark[i]));
        }
    }

    private String numberForRuler(double x) {   // TO BE Customized
        x += d.minValue;
        if (f.numberformat)
            return String.format("%." + f.Number + "f", x);
        if (xValue[MAX] >= 5 && rulerStep > 10000) return String.format("%.1E", x);
        if (xValue[MAX] >= 5 && rulerStep > 1) return "" + (int) x;
        if (rulerStep > 0.1) return String.format("%.1f", x);
        if (rulerStep > 0.01) return String.format("%.2f", x);
        if (rulerStep > 0.001) return String.format("%.3f", x);
        if (rulerStep > 0.0001) return String.format("%.4f", x);
        if (rulerStep > 0.00001) return String.format("%.5f", x);
        return String.format("%g", x);
    }

    private int maxMarkLength(String[] sa) {
        int n = sa[0].length();
        for (String s : sa)
            if (n < s.length()) n = s.length();
        return n;
    }

    private void plotKeys(int times) {
        final double x = xValue[MIN] - 0.5 * rulerStep;
        for (int i = 0; i < d.graft.length; i++) {
            if (d.graft[i].key.length() >= 1) {
                double y = yValue[MIN] + 1 + i;
                if (d.graft[i].index[times] > d.graft.length - d.askNumber - 1) {
                    StdDraw.setPenColor(f.keyColor);
                    StdDraw.setFont(f.plotKey_font);
                    StdDraw.text(x+1, d.graft[i].index[times] - d.input.length + d.askNumber, d.graft[i].key);

                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(f.plotRuler_font);
                    if (d.graft[i].values[0] - d.minValue > 1.7)
                        StdDraw.text((d.graft[i].values[0] - d.minValue) / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX] - 0.3 * rulerStep,
                                d.graft[i].index[times] - d.input.length + d.askNumber, d.graft[i].key);

                    StdDraw.setPenColor(d.graft[i].barFillcolor);
                    StdDraw.text(d.graft[i].values[0] - d.minValue > 0 ? (d.graft[i].values[0] - d.minValue) / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX] + 0.3 * rulerStep : 0.3 * rulerStep,
                            d.graft[i].index[times] - d.input.length + d.askNumber, String.format("    %.3f", d.graft[i].values[0]));
                }
            }
        }
    }

    private void plotBorder() {
        double x = .5 * (yValue[MIN] + yValue[MAX]);
        double y = .5 * (xValue[MIN] + xValue[MAX]);
        double halfWidth = .5 * (yValue[MAX] - yValue[MIN]);
        double halfHeight = .5 * (xValue[MAX] - xValue[MIN]);
        StdDraw.setPenColor(f.borderColor);
        StdDraw.rectangle(y, x, halfHeight, halfWidth);
    }

    private void plotUpRuler() {
        StdDraw.setFont(f.plotUpRuler_font);
        StdDraw.setPenColor(f.rulerColor);
        final double y0 = yValue[MAX] - 0.05, y1 = yValue[MAX] + 0.05;
        String[] mark = new String[rulerGrade + 1];
        for (int i = 0; i <= rulerGrade; i++) {
            double x = xValue[MIN] + i * rulerStep;
            mark[i] = numberForRuler(x);
            if (x / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX] <= xValue[MAX])
                StdDraw.line(x / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX], y0, x / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX], y1);
        }
        int len = maxMarkLength(mark);
        final double y = yScale[MAX] + 1.2 * (yValue[MAX] - yScale[MAX]);
        StdDraw.setPenColor(f.rulerMarkColor);
        for (int i = 0; i <= rulerGrade; i++) {
            double xs = xValue[MIN] + i * rulerStep;
            if (xs / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX] <= xValue[MAX])
                StdDraw.text(xs / (d.graft[d.graft.length - 1].values[0] - d.minValue) * xValue[MAX], y, String.format("%" + len + "s", mark[i]));
        }
    }

    private void plotHeader() {
        StdDraw.setFont(f.plotHeader_font);
        double y = .5 * (yScale[MAX] + yScale[MAX] - 1.5);
        double x = .5 * (xScale[MIN] + xValue[MAX] + 6);
        StdDraw.setPenColor(f.headerColor);
        StdDraw.text(x, y, d.header);
    }

    private void plotFooter() {
        double y = .5 * (yScale[MIN] + yScale[MAX]);
        double x = .5 * (xValue[MIN] + xScale[MIN]);
        StdDraw.setPenColor(f.headerColor);
        StdDraw.setFont(f.plotHeader_font);
        StdDraw.text(0.75*xScale[MIN], y, d.Creators, 90);
        StdDraw.setPenColor(f.footerColor);
        StdDraw.setFont(f.plotFooter_font);
        StdDraw.text(0.6*xScale[MIN], y, d.footer, 90);
    }

    private final static int NORTH = 0;
    private final static int SOUTH = 1;
    private final static int WEST = 2;
    private final static int EAST = 3;
    private final static int MIN = 0;
    private final static int MAX = 1;
}
