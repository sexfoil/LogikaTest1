package com.example.android.logikatest1.tools;

import java.util.ArrayList;

public class Approximation {
    // OLS data for ols3p[3]X^3 + ols3p[2]X^2 + ols3p[1]X + ols3p[0] = Y
    private double[] ols3p = new double[4];
    private double[][] gauss;

    // LAGRANGE data
    private double[] lagrX;
    private double[] lagrY;
    private double[] lagrange5p;

    /* LAGRANGE START:*/
    public void setDataToLagrangePolynomial(int[] dataX, double[] dataY) {
        lagrX = new double[dataX.length];
        lagrY = new double[dataY.length];
        for (int i = 0; i < dataX.length; i++) {
            lagrX[i] = dataX[i];
            lagrY[i] = dataY[i];
        }
        lagrange5p = get5PointsLagrangePolynomialFormula();
    }

    /**
     * Method to evaluated factors A, B, C, D and E
     * for the equation like f(x) = Ax^4 + Bx^3 + Cx^2 + Dx + E
     * with 5 known points of f(x)
     *
     * @return double[] Array of factors [A, B, C, D, E]
     */
    public double[] get5PointsLagrangePolynomialFormula() {
        double[] lagrange = new double[5];
        for (int i = 0; i < 5; i++) {
            double[] l = new double[5];
            ArrayList<Double> point = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                if (j != i) {
                    point.add(lagrX[j]);
                }
            }
            double a = point.get(0);
            double b = point.get(1);
            double c = point.get(2);
            double d = point.get(3);
            l[0] = 1;
            l[1] = a + b + c + d;
            l[2] = a * b + a * c + a * d + b * c + b * d + c * d;
            l[3] = a * b * c + a * b * d + a * c * d + b * c * d;
            l[4] = a * b * c * d;
            double denominator = 1;
            for (int p = 0; p < point.size(); p++) {
                denominator *= lagrX[i] - point.get(p);
            }

            for (int f = 0; f < l.length; f++) {
                lagrange[f] += (l[f] / denominator) * lagrY[i];
            }
        }
        return lagrange;
    }

    public double getYbyLagrangePolynomial5Points(double x) {
        return lagrange5p[0] * x * x * x * x - lagrange5p[1] * x * x * x + lagrange5p[2] * x * x - lagrange5p[3] * x + lagrange5p[4];
    }
    /* :END LAGRANGE*/


    /* OLS START: */
    public void ordinaryLeastSquaresPow3(int[] x, double[] y) {
        double sumX = 0;
        double sumX2 = 0;
        double sumX3 = 0;
        double sumX4 = 0;
        double sumX5 = 0;
        double sumX6 = 0;
        double sumY = 0;
        double sumYX = 0;
        double sumYX2 = 0;
        double sumYX3 = 0;
        int n = x.length;
        gauss = new double[4][5];
        for (int i = 0; i < x.length; i++) {
            sumX += x[i];
            sumX2 += Math.pow(x[i], 2);
            sumX3 += Math.pow(x[i], 3);
            sumX4 += Math.pow(x[i], 4);
            sumX5 += Math.pow(x[i], 5);
            sumX6 += Math.pow(x[i], 6);
            sumY += y[i];
            sumYX += y[i] * x[i];
            sumYX2 += y[i] * Math.pow(x[i], 2);
            sumYX3 += y[i] * Math.pow(x[i], 3);
        }

        gauss[0][0] = n;
        gauss[0][1] = gauss[1][0] = sumX;
        gauss[0][2] = gauss[1][1] = gauss[2][0] = sumX2;
        gauss[0][3] = gauss[1][2] = gauss[2][1] = gauss[3][0] = sumX3;
        gauss[1][3] = gauss[2][2] = gauss[3][1] = sumX4;
        gauss[2][3] = gauss[3][2] = sumX5;
        gauss[3][3] = sumX6;
        gauss[0][4] = sumY;
        gauss[1][4] = sumYX;
        gauss[2][4] = sumYX2;
        gauss[3][4] = sumYX3;

        gaussMethod();
    }

    private void gaussMethod() {
        int lastIndex = gauss[0].length - 1;
        int curRow = 0;
        while (curRow < gauss.length) {
            for (int row = curRow; row < gauss.length; row++) {
                boolean isNotZero = setOneInCurrentFirstElement(gauss[row], curRow);
                if (isNotZero && row != curRow) {
                    deductRow(gauss[curRow], gauss[row]);
                }
            }
            curRow++;
        }

        curRow--;
        ols3p[curRow] = gauss[curRow][lastIndex];
        while (--curRow >= 0) {
            double sumA = 0;
            for (int i = curRow + 1; i < lastIndex; i++) {
                sumA += gauss[curRow][i] * ols3p[i];
            }
            ols3p[curRow] = gauss[curRow][lastIndex] - sumA;
        }
    }

    /**
     * Method to set 1 in current first element in the row
     * by dividing all the elements in the row in current first element
     *
     * @param row   current row to set 1 in current first element
     * @param index of current first element in the row
     * @return true if current first element not zero
     */
    private boolean setOneInCurrentFirstElement(double[] row, int index) {
        double d = row[0 + index];
        if (d != 0) {
            for (int i = 0 + index; i < row.length; i++) {
                row[i] /= d;
            }
            return true;
        }
        return false;
    }

    private void deductRow(double[] upRow, double[] downRow) {
        for (int i = 0; i < downRow.length; i++) {
            downRow[i] -= upRow[i];
        }
    }

    public double getYbyOLSpow3(double x) {
        return ols3p[3] * x * x * x + ols3p[2] * x * x + ols3p[1] * x + ols3p[0];
    }
    /* :END OLS */
}
