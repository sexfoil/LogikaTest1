package com.example.android.logikatest1;


import android.support.v7.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.android.logikatest1.tools.Chart;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;



public class MainActivity extends AppCompatActivity {

    private static final int FIRST_VALUE_X = 1;
    private static final int LAST_VALUE_X = 5;
    private static final int STEP_VALUE_X = 1;
    private static final int SCALE_Y_MAX = 10;
    private static final int SCALE_Y_MIN = -SCALE_Y_MAX;
    private static final int SCALE_Y_RANGE = 10;

    private GraphView graph;
    private View mainView;
    private View currentView;

    private Chart chart;
    private int xPointIndex;
    private int graphStep;
    private int scaleY;
    private int[] pointsPosition;
    private double[] userData;

    private boolean setGraphStep;
    private boolean buildChart;

    private int offsetY = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide(); // hide ActionBar

        init();
    }


    public void init() {
        graph = findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(FIRST_VALUE_X - STEP_VALUE_X);
        graph.getViewport().setMaxX(LAST_VALUE_X + STEP_VALUE_X);
        graph.getViewport().setMinY(-10);
        graph.getViewport().setMaxY(10);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        mainView = findViewById(R.id.main);

        chart = new Chart(FIRST_VALUE_X, LAST_VALUE_X, STEP_VALUE_X);
        scaleY = (SCALE_Y_MAX - SCALE_Y_MIN) * SCALE_Y_RANGE;
        userData = new double[chart.getDataX().length];
        pointsPosition = new int[userData.length];

        setGraphStep = true;

        build_charts(graph);

        initListener();
        displayInfoData();
        setDataToGraphView();
    }


    private void initListener() {
        currentView = findViewById(R.id.graph);
        currentView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();

                xPointIndex = getPointIndex(X, mainView.getWidth());
                int screenHeight = mainView.getHeight();
                int screenZeroPoint = screenHeight / 2;

                if (setGraphStep) {
                    graphStep = screenHeight / scaleY;
                    for (int i = 0; i < pointsPosition.length; i++) {
                        pointsPosition[i] = screenZeroPoint;
                    }
                    setGraphStep = false;
                }
                //
                System.out.println("\n****** X = " + X + "\n");
                System.out.println("\n****** Y = " + Y + "\n");
                //
                if (xPointIndex >= 0 && (Math.abs(pointsPosition[xPointIndex] - Y) < 50)) {
                    int action = event.getAction() & MotionEvent.ACTION_MASK;

                    switch (action) {
                        case (MotionEvent.ACTION_DOWN):
                            offsetY = Y;
                            break;
                        case (MotionEvent.ACTION_MOVE):
                            if (Math.abs(Y - offsetY) > 3) {
                                double delta = Y - offsetY > 0 ? -0.1 : 0.1;
                                double tmp = Math.round((userData[xPointIndex] + delta) * 10);
                                userData[xPointIndex] = tmp / 10;
                                offsetY = Y; //
                                pointsPosition[xPointIndex] = Y;
                            }
                            break;
                        case (MotionEvent.ACTION_UP):
                            offsetY = 0;
                            pointsPosition[xPointIndex] = screenZeroPoint - (int)(((userData[xPointIndex] * 10) * graphStep));
                            //
                            //System.out.println("INDEX = " + xPointIndex);
                            //System.out.println("DATA = " + userData[xPointIndex]);
                            //System.out.println("STEP = " + graphStep);
                            //System.out.println("CHECK = " + (userData[xPointIndex]*10*graphStep));
                            //System.out.println("SUM = " + (screenZeroPoint - (int)(userData[xPointIndex] * 10 * graphStep)));
                            //System.out.println("POSITION = " + pointsPosition[xPointIndex]);
                            //
                            break;
                        default:
                            break;
                    }

                    displayInfoData();
                    setDataToGraphView();
                }
                return true;
            }
        });
    }


    public void setDataToGraphView() {
        if (graph != null) {
            graph.removeAllSeries();
        }

        if (buildChart) {
            chart.approximateData();

            int len = chart.getAllValuesX().length;

            DataPoint[] dataPointsOLS = new DataPoint[(len - 1) * 10 + 1];
            DataPoint[] dataPointsLagrange = new DataPoint[(len - 1) * 10 + 1];

            for (int x = 0; x < dataPointsOLS.length; x++) {
                double p = x / 10.0;
                dataPointsOLS[x] = new DataPoint(p, chart.getApproximation().getYbyOLSpow3(p));
                dataPointsLagrange[x] = new DataPoint(p, chart.getApproximation().getYbyLagrangePolynomial5Points(p));
            }

            LineGraphSeries<DataPoint> seriesOLS = new LineGraphSeries<>(dataPointsOLS);
            seriesOLS.setTitle("OLS pow3");
            seriesOLS.setColor(Color.RED);
            seriesOLS.setThickness(10);
            graph.addSeries(seriesOLS);

            LineGraphSeries<DataPoint> seriesLagrange = new LineGraphSeries<>(dataPointsLagrange);
            seriesLagrange.setTitle("Lagrange");
            seriesLagrange.setColor(Color.YELLOW);
            graph.addSeries(seriesLagrange);

            buildChart = false;
        }

        DataPoint[] dataPointsUser = new DataPoint[chart.getDataX().length];
        for (int i = 0; i < dataPointsUser.length; i++) {
            dataPointsUser[i] = new DataPoint(chart.getDataX()[i], chart.getDataY()[i]);
        }
        PointsGraphSeries<DataPoint> seriesUser = new PointsGraphSeries<>(dataPointsUser);
        seriesUser.setTitle("Set points");
        seriesUser.setColor(Color.BLUE);
        seriesUser.setSize(12);
        graph.addSeries(seriesUser);
    }


    public void build_charts(View view) {
        buildChart = true;
        chart.setDataY(userData);
        setDataToGraphView();
        //
        //View v = findViewById(R.id.main);
        //System.out.println("\n___________W: " + v.getWidth());
        //System.out.println("\n___________H: " + v.getHeight());
        //
    }


    public void reset_data(View view) {
        currentView.setOnTouchListener(null);
        setGraphStep = true;
        init();
    }


    private void displayInfoData() {
        TextView[] xSet = new TextView[5];
        xSet[0] = findViewById(R.id.x_1);
        xSet[1] = findViewById(R.id.x_2);
        xSet[2] = findViewById(R.id.x_3);
        xSet[3] = findViewById(R.id.x_4);
        xSet[4] = findViewById(R.id.x_5);

        for (int i = 0; i < 5; i++) {
            CharSequence text = "" + userData[i];
            xSet[i].setText(text);
        }
    }


    private int getPointIndex(int x, int width) {
        double a = (double) x / width;
        if (a > 0.21999 && a < 0.28001) {
            return 0;
        } else if (a > 0.36599 && a < 0.42601) {
            return 1;
        } else if (a > 0.51199 && a < 0.57201) {
            return 2;
        } else if (a > 0.65799 && a < 0.71801) {
            return 3;
        } else if (a > 0.80399 && a < 0.86401) {
            return 4;
        } else {
            return -1;
        }
    }


    public void exit(View view) {
        System.exit(0);
    }
}
