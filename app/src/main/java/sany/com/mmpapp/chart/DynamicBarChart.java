package sany.com.mmpapp.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sunj7 on 17-1-6.
 */
public class DynamicBarChart {
    public AbstractDemoChart abstractDemoChart;
    public XYMultipleSeriesRenderer renderer;
    public XYMultipleSeriesDataset dataset;
    public GraphicalView chartView;
    private Context context;
    private int[] colors;
    private String[] titles;
    private List<double[]> values;
    private String topTile;
    private String bottonTile;

    public DynamicBarChart(Context context,double[] times,String[] tablesOfX,String topTitleStr,String bottomTitleStr ) {
        super();
        // TODO Auto-generated constructor stub
        this.context = context;
        abstractDemoChart = new AbstractDemoChart();
        this.topTile=topTitleStr;
        this.bottonTile=bottomTitleStr;
        init(times,tablesOfX,topTile,bottonTile);
    }

    private void init(double[] times,String[] tablesOfX,String str1,String str2) {
        titles = new String[] { "" };
        values = new ArrayList<double[]>();
        values.add(times);
        colors = new int[] { 0xFF1E90FF };
        renderer = abstractDemoChart.buildBarRenderer(colors);
        renderer.setXLabels(0);
       // renderer.setXLabels(tablesOfX.length);
       // renderer.setYLabels(10);
        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setPanEnabled(false, false);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setShowGrid(true);
        // renderer.setZoomEnabled(false);
        renderer.setZoomRate(1.1f);
        renderer.setZoomEnabled(false, false);
        for(int i=0;i<tablesOfX.length;i++){
            renderer.addXTextLabel(i+1,tablesOfX[i]);
        }
        renderer.setBarSpacing(0.1f);
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
        renderer.getSeriesRendererAt(0).setChartValuesTextSize(25);
        renderer.setDisplayValues(true);
        if(times.length==1){
            renderer.setBarWidth(300);

        }
        List<Double> getMax=new ArrayList<Double>();
        for(int i=0;i<values.size();i++){
            for(double d:values.get(i)){
                getMax.add(d);
            }
        }

        double yMax= Collections.max(getMax);
        yMax=yMax+yMax/10;
        double xMax=tablesOfX.length+0.5;
        abstractDemoChart.setChartSettings(renderer, str1, str2, "趟次", 0.5, xMax, 0, yMax,0xFF1E90FF, 0xFF1E90FF);
        dataset = abstractDemoChart.buildBarDataset(titles, values);
        chartView = ChartFactory.getBarChartView(context, dataset, renderer, Type.DEFAULT);
    }

    public void setView() {
        dataset = abstractDemoChart.buildBarDataset(titles, values);
        chartView = ChartFactory.getBarChartView(context, dataset, renderer, Type.DEFAULT);
    }

    public void setValues(List<double[]> values) {
        this.values = values;
    }

    public GraphicalView getChartView() {
        return chartView;
    }
}
