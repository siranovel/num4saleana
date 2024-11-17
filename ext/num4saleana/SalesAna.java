import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.chart.ChartUtils;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class SalesAna {
    private static SalesAna sales = new SalesAna();
    public static SalesAna getInstance() {
        return sales;
    }
    public void abcAna(List<Map<String, Object>> sales_info) {


        ChartPlot plot = new PaletChartPlot();
        JFreeChart chart = plot.createChart("ABC分析", "", sales_info);

        plot.writeJPEG("abcana.jpeg", chart, 800, 500);
        
    }
    /*********************************/
    /* interface define              */
    /*********************************/
    private interface ChartPlot {
        JFreeChart createChart(String title, String dname, List<Map<String, Object>> vals);
        default void writeJPEG(String fname, JFreeChart chart, int width, int height) {
            File file = new File(fname);
            try {
                ChartUtils.saveChartAsJPEG(file, chart, width, height);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private interface CreatePlot {
        CategoryPlot createPlot(String dname, Map<String, Object> xi);
    }
    /*********************************/
    /* class define                  */
    /*********************************/
    private class PaletChartPlot implements ChartPlot {
        private List<Map<String, Object>> createDate(List<Map<String, Object>> sales_info) {
            List<Map<String, Object>> data = sales_info;
            Collections.sort(data, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> obj1, Map<String, Object> obj2) {
                    Long val1 = (Long)obj1.get("revenue");
                    Long val2 = (Long)obj2.get("revenue");

                    return val2.compareTo(val1);
                }
            });
            // 構成比計算
            DescriptiveStatistics stat = new DescriptiveStatistics();
            for(Map<String, Object> entry : data) {
                stat.addValue((Long)entry.get("revenue"));
            }
            double sum = stat.getSum();
            for(Map<String, Object> entry : data) {
                entry.put("ratio", (Long)entry.get("revenue") / sum);
            }            
            return data;
        }
        public JFreeChart createChart(String title, String dname, List<Map<String, Object>> sales_info) {
            CategoryPlot plot = createPlot(dname, sales_info);
            JFreeChart chart = new JFreeChart(title, plot);

            ChartUtils.applyCurrentTheme(chart);
            return chart;
        }
        private CategoryPlot createPlot(String dname, List<Map<String, Object>> sales_info) {
            List<Map<String, Object>> vals = createDate(sales_info);
            BarRenderer renderer0 = new BarRenderer();
            LineAndShapeRenderer renderer1 = new LineAndShapeRenderer(true, false);

            CategoryToolTipGenerator toolTipGenerator = new StandardCategoryToolTipGenerator();

            ItemLabelPosition position1 = new ItemLabelPosition(
                    ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
            renderer0.setDefaultPositiveItemLabelPosition(position1);
            ItemLabelPosition position2 = new ItemLabelPosition(
                    ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
            renderer0.setDefaultNegativeItemLabelPosition(position2);

            renderer0.setDefaultToolTipGenerator(toolTipGenerator);
            renderer1.setDefaultToolTipGenerator(toolTipGenerator);

            CategoryPlot plot = new CategoryPlot();
            plot.setOrientation(PlotOrientation.VERTICAL);
            plot.mapDatasetToRangeAxis(0,0);
            plot.mapDatasetToRangeAxis(1,1);
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

            /*--- 横軸 ---*/
            CategoryAxis domainAxis = new CategoryAxis("商品");
            plot.setDomainAxis(domainAxis);

            /*--- 縦軸 ---*/
            NumberAxis valueAxis0 = new NumberAxis("売上金額");

            plot.setRangeAxis(0, valueAxis0);
            plot.setRenderer(0, renderer0);
            plot.setDataset(0, createDataset0("売上", vals));

            NumberAxis valueAxis1 = new NumberAxis("構成比");
            plot.setRangeAxis(1, valueAxis1);
            plot.setRenderer(1, renderer1);
            plot.setDataset(1, createDataset1("売上累積構成比", vals));

            return plot;
            
        }
        private CategoryDataset createDataset0(String dname,List<Map<String, Object>> vals) {
            DefaultCategoryDataset data = new DefaultCategoryDataset();
            for(Map<String, Object> entry : vals) {
                data.addValue(
                    (Long)entry.get("revenue"), dname, (String)entry.get("name")
                );
            }
            return data;
        }
        private CategoryDataset createDataset1(String dname,List<Map<String, Object>> vals) {
            DefaultCategoryDataset data = new DefaultCategoryDataset();
            
            double ratios = 0.0;
            for(Map<String, Object> entry : vals) {
                ratios += (Double)entry.get("ratio");
                data.addValue(ratios, dname, (String)entry.get("name"));
            }
            return data;
        }
    }
}
