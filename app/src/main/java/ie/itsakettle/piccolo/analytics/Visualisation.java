package ie.itsakettle.piccolo.analytics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by wtr on 07/03/15.
 */
public class Visualisation {

    private Context c;

    public Visualisation(Context context)
    {
        this.c = context;
    }

    public Bitmap DailyGraph(Number[] minutes)
    {
        /* Get the notification manager */

        /*Now build the notification - first the graph   */
        /*Create the graph*/
        XYPlot plot = new XYPlot(c,"Intra Day");

        Number[] hour = {1, 2, 3, 4, 5, 6,7,8,9,10,11,12,13,14,15,16,17
                ,18,19,20,21,22,23,24};



        XYSeries series1 = new SimpleXYSeries(Arrays.asList(hour),
                Arrays.asList(minutes),"Series1");

        plot.getGraphWidget().setRangeOriginLinePaint(null);

        MyBarFormatter series1Format = new MyBarFormatter(Color.BLACK,Color.BLACK);
        plot.addSeries(series1, series1Format);
        MyBarRenderer renderer = ((MyBarRenderer) plot.getRenderer(MyBarRenderer.class));
        renderer.setBarWidthStyle(BarRenderer.BarWidthStyle.FIXED_WIDTH);
        renderer.setBarWidth(15);

        /*This gets rid of the yellow cursor line on the left hand side o fthe plot*/
        plot.getGraphWidget().setCursorPosition(-30, -1);

        plot.setRangeBoundaries(0, 60, BoundaryMode.FIXED);
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 20);
        plot.setDrawRangeOriginEnabled(false);



        /*plot formatting*/
        XYGraphWidget xy = plot.getGraphWidget();
        /*Set transparency*/
        xy.getBackgroundPaint().setColor(Color.TRANSPARENT);
        xy.getDomainGridLinePaint().setColor(Color.TRANSPARENT);
        plot.getBackgroundPaint().setColor(Color.TRANSPARENT);

        /*Get rid of labels*/
        xy.setDomainLabelPaint(null);
        xy.setRangeLabelPaint(null);
        xy.setDomainOriginLabelPaint(null);
        xy.setRangeOriginLabelPaint(null);

        /*Get rid of the y axis*/

        xy.setDomainOriginLinePaint(null);

        /*Make sure the grid has no background and also set some padding on left and right side*/
        xy.setGridBackgroundPaint(null);
        xy.setGridPaddingLeft(20);
        xy.setGridPaddingRight(20);
        xy.setMarginLeft(0);

        /*position the series in the plor*/
        xy.position(-0.5f, XLayoutStyle.RELATIVE_TO_RIGHT,
                -0.5f, YLayoutStyle.RELATIVE_TO_BOTTOM,
                AnchorPosition.CENTER);
        xy.setSize(new SizeMetrics(
                0, SizeLayoutType.FILL,
                0, SizeLayoutType.FILL));
        /*End plot formatting*/


        /*remove widgets*/
        LayoutManager l = plot.getLayoutManager();
        l.remove(plot.getDomainLabelWidget());
        l.remove(plot.getRangeLabelWidget());
        l.remove(plot.getLegendWidget());
        l.remove(plot.getTitleWidget());


        xy.setRangeLabelWidth(0);
        xy.setDomainLabelWidth(0);
        xy.setPadding(0, 0, 0, 0);
        xy.setMargins(0, 0, 0, 0);
        //xy.setGridPadding(0, 0, 0, 0); /*THIS ONE*/
        plot.setPlotMargins(0, 0, 0, 0);
        plot.setPlotPadding(0, 0, 0, 0);



        plot.layout(0, 0, 700, 100);
        plot.setDrawingCacheEnabled(true);
        plot.buildDrawingCache();
        Bitmap b = Bitmap.createBitmap(plot.getDrawingCache());
        return b;
    }

}


class MyBarFormatter extends BarFormatter {
    public MyBarFormatter(int fillColor, int borderColor) {
        super(fillColor, borderColor);
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return MyBarRenderer.class;
    }

    @Override
    public SeriesRenderer getRendererInstance(XYPlot plot) {
        return new MyBarRenderer(plot);
    }
}

class MyBarRenderer extends BarRenderer<MyBarFormatter> {

    public MyBarRenderer(XYPlot plot) {
        super(plot);
    }

    public MyBarFormatter getFormatter(int index, XYSeries series) {
        return getFormatter(series);
    }
}