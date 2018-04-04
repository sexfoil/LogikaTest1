package com.example.android.logikatest1.tools;

public class Chart {
    private int firstValue;
    private int lastValue;
    private int step;
    private int chartSize;
    private int[] dataX;
    private double[] dataY;
    private Approximation approximation;

    private int[] allValuesX;
    private double[] valuesYbyOLSpow3;
    private double[] valuesYbyLagrangePolynomial;

    public Chart(int firstValue, int lastValue, int step) {
        this.firstValue = firstValue;
        this.lastValue = lastValue;
        this.step = step;
        approximation = new Approximation();
        initDataX();
        dataY = new double[dataX.length];
        valuesYbyOLSpow3 = new double[chartSize];
        valuesYbyLagrangePolynomial = new double[chartSize];
    }

    /**
     *  Initialize of provided data
     */
    private void initDataX() {
        dataX = new int[(lastValue - firstValue) / step + 1];
        chartSize = dataX.length + 2;
        allValuesX = new int[chartSize];
        allValuesX[0] = firstValue - step;
        for (int i = 0; i < dataX.length; i++) {
            dataX[i] = firstValue + i * step;
            allValuesX[i + 1] = dataX[i];
        }
        allValuesX[allValuesX.length - 1] = lastValue + step;
    }

    /**
     *  Makes approximation of provided function by OLS pow3 and Lagrange Polynomial methods
     *  for 5 points of user data and 2 extremum points
     */
    public void approximateData() {

        // first method of task
        approximation.ordinaryLeastSquaresPow3(dataX, dataY);
        for (int i = 0; i < allValuesX.length; i++) {
            valuesYbyOLSpow3[i] = approximation.getYbyOLSpow3(allValuesX[i]);
        }

        // second method of task
        approximation.setDataToLagrangePolynomial(dataX, dataY);
        for (int i = 0; i < allValuesX.length; i++) {
            valuesYbyLagrangePolynomial[i] = approximation.getYbyLagrangePolynomial5Points(allValuesX[i]);
        }
    }

    public Approximation getApproximation() {
        return approximation;
    }

    public int[] getDataX() {
        return dataX;
    }

    public double[] getDataY() {
        return dataY;
    }

    public int[] getAllValuesX() {
        return allValuesX;
    }

    public double[] getValuesYbyOLSpow3() {
        return valuesYbyOLSpow3;
    }

    public double[] getValuesYbyLagrangePolynomial() {
        return valuesYbyLagrangePolynomial;
    }

    public void setDataY(double[] dataY) {
        this.dataY = dataY;
    }
}
