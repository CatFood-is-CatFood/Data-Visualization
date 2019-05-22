import java.applet.AudioClip;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.json.*;
import javax.swing.*;

public class HistogramATest {
    public static void main(String[] args) {
        URL url = null;
        try {
            url = new URL("file:Summer.wav");
        } catch (MalformedURLException e) {
        }
        AudioClip music = JApplet.newAudioClip(url);
        music.play();
        //music.loop();
        String background = "background.PNG";

        HistogramA h = createHistogramAFrom(args[0], 0);
        StdDraw.setCanvasSize(h.c.x, h.c.y);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(h.c.bgColor);
        h.draw(0, background);
        StdDraw.show();
        StdDraw.pause(20);
        for (int i = 1; i < h.d.input[0].values.length; i++) {
            h = createHistogramAFrom(args[0], i);
            StdDraw.clear(h.c.bgColor);
            h.draw(i, background);
            StdDraw.show();
            StdDraw.pause(20);
            //if (i % 40 == 0)
            //    StdDraw.pause(150);
        }
        StdDraw.pause(150);

        music.stop();
    }

    private static HistogramA createHistogramAFrom(String fileName, int i) {
        HistogramA h = null;
        try (
                InputStream is = new FileInputStream(new File(fileName));
                JsonReader rdr = Json.createReader(is)
        ) {
            JsonObject obj = rdr.readObject().getJsonObject("histograma");
            Canvas canvas = getCanvasFrom(obj.getJsonObject("canvas"));
            Formats fmts = getFormatsFrom(obj.getJsonObject("formats"));
            HistogramData data = getDataFrom(obj.getJsonObject("data"));
            h = new HistogramA(canvas, fmts, data, i);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        ;
        return h;
    }

    private static Canvas getCanvasFrom(JsonObject obj) {
        Canvas canvas = new Canvas();

        JsonArray szArray = obj.getJsonArray("size");
        if (szArray != null) {  // otherwise, use the default size
            int[] size = toIntArray(szArray);
            canvas.x = size[0];
            canvas.y = size[1];
        }

        JsonArray bgcArray = obj.getJsonArray("bgcolor");
        if (bgcArray != null)  // otherwise, use the default bgColor
            canvas.bgColor = getColorFrom(bgcArray);

        JsonArray cArray = obj.getJsonArray("color");
        if (cArray != null)    // otherwise, use the default color
            canvas.color = getColorFrom(cArray);

        return canvas;
    }

    private static int[] toIntArray(JsonArray jsa) {
        int[] a = new int[jsa.size()];
        for (int i = 0; i < jsa.size(); i++)
            a[i] = jsa.getInt(i);
        return a;
    }

    private static int[][] to2DIntArray(JsonArray jsa) {
        int[][] a = new int[jsa.size()][3];
        int i = 0;
        for (JsonArray n : jsa.getValuesAs(JsonArray.class))
            a[i++] = toIntArray(n);
        return a;
    }

    private static double[] toDoubleArray(JsonArray jsa) {
        double[] a = new double[jsa.size()];
        for (int i = 0; i < jsa.size(); i++)
            a[i] = jsa.getJsonNumber(i).doubleValue();
        return a;
    }

    private static double[][] to2DDoubleArray(JsonArray jsa) {
        double[][] d = new double[jsa.size()][];
        int i = 0;
        for (JsonArray n : jsa.getValuesAs(JsonArray.class))
            d[i++] = toDoubleArray(n);
        return d;
    }

    private static String[] toStringArray(JsonArray jsa) {
        String[] s = new String[jsa.size()];
        for (int i = 0; i < jsa.size(); i++)
            s[i] = jsa.getString(i);
        return s;
    }

    private static Color getColorFrom(JsonArray jsa) {
        int[] c = toIntArray(jsa);
        return new Color(c[0], c[1], c[2]);
    }

    private static Font getFontFrom(JsonArray jsa) {
        String[] c = toStringArray(jsa);
        if (c[1].equals("Font.PLAIN"))
            return new Font(c[0], Font.PLAIN, Integer.parseInt(c[2]));
        else if (c[1].equals("Font.BOLD"))
            return new Font(c[0], Font.BOLD, Integer.parseInt(c[2]));
        else
            return new Font(c[0], Font.ITALIC, Integer.parseInt(c[2]));
    }

    private static Formats getFormatsFrom(JsonObject obj) {
        Formats fmts = new Formats();
        JsonArray margins = obj.getJsonArray("margins");
        if (margins != null)
            fmts.margins = toDoubleArray(margins);

        JsonValue isBarFilled = obj.get("isbarfilled");
        if (isBarFilled != null)
            fmts.isBarFilled = isBarFilled == JsonValue.TRUE;

        JsonValue hasBarFrame = obj.get("hasbarframe");
        if (hasBarFrame != null)
            fmts.hasBarFrame = hasBarFrame == JsonValue.TRUE;

        JsonArray barFrameColor = obj.getJsonArray("barframecolor");
        if (barFrameColor != null)
            fmts.barFrameColor = getColorFrom(barFrameColor);

        JsonValue hasBorder = obj.get("hasborder");
        if (hasBorder != null)
            fmts.hasBorder = hasBorder == JsonValue.TRUE;

        JsonArray borderColor = obj.getJsonArray("bordercolor");
        if (borderColor != null)
            fmts.borderColor = getColorFrom(borderColor);

        JsonArray rulerColor = obj.getJsonArray("rulercolor");
        if (rulerColor != null)
            fmts.rulerColor = getColorFrom(rulerColor);

        JsonArray rulerMarkColor = obj.getJsonArray("rulermarkcolor");
        if (rulerMarkColor != null)
            fmts.rulerMarkColor = getColorFrom(rulerMarkColor);

        JsonValue hasUpRuler = obj.get("hasupruler");
        if (hasUpRuler != null)
            fmts.hasUpRuler = hasUpRuler == JsonValue.TRUE;

        JsonArray keyColor = obj.getJsonArray("keycolor");
        if (keyColor != null)
            fmts.keyColor = getColorFrom(keyColor);

        JsonValue hasHeader = obj.get("hasheader");
        if (hasHeader != null)
            fmts.hasHeader = hasHeader == JsonValue.TRUE;

        JsonArray headerColor = obj.getJsonArray("headercolor");
        if (headerColor != null)
            fmts.headerColor = getColorFrom(headerColor);

        JsonValue hasCreators = obj.get("hasCreators");
        if (hasCreators != null)
            fmts.hasCreators = hasCreators == JsonValue.TRUE;

        JsonValue hasFooter = obj.get("hasfooter");
        if (hasFooter != null)
            fmts.hasFooter = hasFooter == JsonValue.TRUE;

        JsonArray footerColor = obj.getJsonArray("footercolor");
        if (footerColor != null)
            fmts.footerColor = getColorFrom(footerColor);

        JsonValue hasText = obj.get("hasText");
        if (hasText != null)
            fmts.hasText = hasText == JsonValue.TRUE;

        JsonArray TextColor = obj.getJsonArray("barFillcolor");
        if (TextColor != null)
            fmts.TextColor = getColorFrom(TextColor);

        JsonArray plotKey_font = obj.getJsonArray("plotKey_font");
        if (plotKey_font != null)
            fmts.plotKey_font = getFontFrom(plotKey_font);

        JsonArray plotRuler_font = obj.getJsonArray("plotRuler_font");
        if (plotRuler_font != null)
            fmts.plotRuler_font = getFontFrom(plotRuler_font);

        JsonArray plotUpRuler_font = obj.getJsonArray("plotUpRuler_font");
        if (plotUpRuler_font != null)
            fmts.plotUpRuler_font = getFontFrom(plotUpRuler_font);

        JsonArray plotHeader_font = obj.getJsonArray("plotHeader_font");
        if (plotHeader_font != null)
            fmts.plotHeader_font = getFontFrom(plotHeader_font);

        JsonArray plotFooter_font = obj.getJsonArray("plotFooter_font");
        if (plotFooter_font != null)
            fmts.plotFooter_font = getFontFrom(plotFooter_font);

        JsonArray plotText_font = obj.getJsonArray("plotText_font");
        if (plotText_font != null)
            fmts.plotText_font = getFontFrom(plotText_font);

        JsonValue numberformat = obj.get("numberformat");
        if (numberformat != null)
            fmts.numberformat = numberformat == JsonValue.TRUE;

        JsonNumber Number = obj.getJsonNumber("Number");
        if (Number != null)
            fmts.Number = Number.intValue();
        return fmts;
    }

    private static HistogramData getDataFrom(JsonObject obj) {
        HistogramData data = new HistogramData();
        data.header = obj.getString("header", "");
        data.footer = obj.getString("footer", "");
        data.Creators = obj.getString("Creators", "");

        JsonNumber minValue = obj.getJsonNumber("minvalue");
        if (minValue != null)
            data.minValue = minValue.doubleValue();

        JsonNumber askNumber = obj.getJsonNumber("askNumber");
        if (askNumber != null)
            data.askNumber = askNumber.intValue();

        double[][] values = to2DDoubleArray(obj.getJsonArray("values"));
        String[] keys = toStringArray(obj.getJsonArray("keys"));
        data.input = new DATA[keys.length];

        int[][] Allcolor = to2DIntArray(obj.getJsonArray("barfillcolor"));
        Color[] barFillcolor = new Color[Allcolor.length];
        for (int i = 0; i < Allcolor.length; i++)
            barFillcolor[i] = new Color(Allcolor[i][0], Allcolor[i][1], Allcolor[i][2]);

        for (int i = 0; i < data.input.length; i++) {
            data.input[i] = new DATA(keys[i], values[i], barFillcolor[i]);
        }

        JsonArray texts = obj.getJsonArray("texts");
        if (texts != null)
            data.texts = toStringArray(texts);

        return data;
    }
}
