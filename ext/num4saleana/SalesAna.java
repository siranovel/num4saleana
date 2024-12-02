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
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
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
    public void crossAbcAna(List<Map<String, Object>> sales_info) {
        JFrame f = new CrossABCLayout(sales_info);
        // ウィンドウを表示
        f.setVisible(true);
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
    // パレットチャート
    //  ABC分析
    private class PaletChartPlot implements ChartPlot {
        private List<Map<String, Object>> createData(List<Map<String, Object>> sales_info) {
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
            List<Map<String, Object>> vals = createData(sales_info);
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
    // クロスABC分析
    private class CrossABCLayout extends  JFrame {
        public CrossABCLayout(List<Map<String, Object>> sales_info) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setTitle("クロスaBC分析");
            setSize(500, 500);
            setResizable(false);

            JPanel p = createPanel(sales_info);
            this.setContentPane(p);
        }
        private JPanel createPanel(List<Map<String, Object>> sales_info) {
            return new CrossABCPanel(sales_info);
        }
    }
    private class CrossABCPanel extends JPanel {
        private Map<String, Integer> rankIdx = new HashMap<String, Integer>();
        private StringBuilder[][] htmlABC = new StringBuilder[3][3];
        public CrossABCPanel(List<Map<String, Object>> sales_info) {
            rankIdx.put("A", Integer.valueOf(0));
            rankIdx.put("B", Integer.valueOf(1));
            rankIdx.put("C", Integer.valueOf(2));

            List<Map<String, Object>> data = calcSals(sales_info);
            this.setLayout(new BorderLayout());
            this.add(createLabel(), BorderLayout.NORTH);
            this.add(createTable(data));
        }
        private Component createLabel() {
            JPanel p = new JPanel();
            JLabel l = new JLabel("売上×粗利");
            l.setFont(new Font("Arial", Font.PLAIN, 30));
            l.setForeground(Color.BLUE);
            l.setHorizontalAlignment(JLabel.CENTER);
            p.add(l);
            return p;
        }
        private Component createTable(List<Map<String, Object>> sales_info) {
            List<Map<String, Object>> data = calcSals(sales_info);
            String[][] crossDt = createData(data);
            JPanel p = new JPanel();
            p.setLayout(new BorderLayout());
            p.add(new JLabel("売　上", JLabel.CENTER), BorderLayout.NORTH);
            p.add(new JLabel("<html><body>粗<br/>　<br/>利</body></html>"), BorderLayout.WEST);
            p.add(new GridPanel(crossDt));
            return p;
        }
        private List<Map<String, Object>> calcSals(List<Map<String, Object>> sales_info) {
            List<Map<String, Object>> data = sales_info;
            // sals:売上金額
            // margin:粗利額
            for(Map<String, Object> entry : sales_info) {
                entry.put("sals", (Long)entry.get("sale") *  (Long)entry.get("quantity"));
                entry.put("margin", (Long)entry.get("sale") -  (Long)entry.get("stock"));
            } 
            return data;          
        }
        private String[][] createData(List<Map<String, Object>> data) {
            String[][] crossDt = {
                {"A", "", "", ""},
                {"B", "", "", ""},
                {"C", "", "", ""},
            };
            Map<String, String> saleABC = createABC("sals", data);
            Map<String, String> marginABC = createABC("margin", data);

            for(String key : saleABC.keySet()) {
                int saleABCIdx = rankIdx.get(saleABC.get(key)).intValue();
                int marginABCIdx = rankIdx.get(marginABC.get(key)).intValue();

                if (htmlABC[marginABCIdx][saleABCIdx] == null) {
                    htmlABC[marginABCIdx][saleABCIdx] = new StringBuilder();
                    htmlABC[marginABCIdx][saleABCIdx].append("<html>");
                    htmlABC[marginABCIdx][saleABCIdx].append("<body>");
                }
                htmlABC[marginABCIdx][saleABCIdx].append(key);
                htmlABC[marginABCIdx][saleABCIdx].append("<br/>");
            }
            for(int j = 0; j < 3; j++) {
                for(int i = 0; i < 3; i++) {
                    if (htmlABC[j][i] != null) {
                        htmlABC[j][i].append("</body>");
                        htmlABC[j][i].append("</html>");
                        crossDt[j][i + 1] = htmlABC[j][i].toString();
                    }
                }
            }
            return crossDt;
        }
        private Map<String, String> createABC(String id, List<Map<String, Object>> data) {
            Map<String, String> saleABC = new HashMap<String, String>();
            Collections.sort(data, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> obj1, Map<String, Object> obj2) {
                    Long val1 = (Long)obj1.get(id);
                    Long val2 = (Long)obj2.get(id);

                    return val2.compareTo(val1);
                }
            });
            DescriptiveStatistics stat = new DescriptiveStatistics();
            for(Map<String, Object> entry : data) {
                stat.addValue((Long)entry.get(id));
            }
            double sum = stat.getSum();
            double rate = 0.0;

            for(Map<String, Object> entry : data) {
                String rank = "";

                rate += (Long)entry.get(id) / sum;
                if (rate < 0.7) {
                    rank = "A";
                } else if (rate < 0.9) {
                    rank = "B";
                } else {
                    rank = "C";
                }
                saleABC.put((String)entry.get("name"), rank);
            }            
            return saleABC;
        }        
        
        private class GridPanel extends JPanel {
            private GridBagLayout gbl = new GridBagLayout();
            private JLabel   lbltl= new JLabel();
            private JLabel[] lbltlw = new JLabel[3];
            private JLabel[] lbltlh = new JLabel[3];
            private JLabel[] lbls = new JLabel[3*3];

            public GridPanel(String[][] data) {
                setLayout(gbl);
                addLabel(lbltl     = new JLabel(" "),                0, 0, 1, 1);
                addLabel(lbltlw[0] = new JLabel("A", JLabel.CENTER), 1, 0, 1, 1);
                addLabel(lbltlw[1] = new JLabel("B", JLabel.CENTER), 2, 0, 1, 1);
                addLabel(lbltlw[2] = new JLabel("C", JLabel.CENTER), 3, 0, 1, 1);

                for(int j = 0; j < data.length; j++) {
                    addLabel(lbltlh[j] = new JLabel(data[j][0]), 0, j + 1, 1, 1);
                    for(int i = 0; i < data[j].length - 1; i++) {
                        addLabel(lbls[j * 3 + i] = new JLabel(data[j][i + 1]), i + 1, j + 1, 1, 1);
                    }
                }
                // ラベルサイズ変更
                chgLabelSize(lbltl, 20, 20);
                for(int i = 0; i < 3; i++) {
                    chgLabelSize(lbltlw[i], 150, 20);
                    chgLabelSize(lbltlh[i], 20, 125);
                }
                // 枠表示
               for(int i = 0; i < lbls.length; i++) {
                    lbls[i].setBorder(new LineBorder(Color.CYAN));
                }
            }
            private void chgLabelSize(JLabel lbl, int w, int h) {
                lbl.setPreferredSize(new Dimension(w, h));
            }
            private void addLabel(JLabel lbl, int x, int y, int w, int h) {
                GridBagConstraints gbc = new GridBagConstraints();

                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridx = x;
                gbc.gridy = y;
                gbc.gridwidth = w;
                gbc.gridheight = h;
                gbl.setConstraints(lbl, gbc);
                add(lbl);
            }
        }
    }
}
