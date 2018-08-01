package searchmethods;

/**
 * Created by zhaoqingteng on 12/2/16.
 */

import com.sun.org.apache.regexp.internal.RE;
import searchmethods.OtherTool.*;

import java.text.DecimalFormat;

public class perceptron {

    double theta = 0;

    public double method(String path, double lr1, double lr2){
        OtherTool.stateOverview[] trainData = new OtherTool().readFile("votes-train.csv");
//        for (int i = 0; i < trainData.length; i++) {
//            System.out.println(trainData[i].population);
//        }
        double[] w = new double[9];
        w = train_data(trainData, lr1, lr2);
        OtherTool.stateOverview[] testData = new OtherTool().readFile(path);
        double right = 0;
        double wrong = 0;
        for (int i = 0; i < testData.length; i++){
            double res = CalcOut(theta, w, testData[i]);
//            System.out.println(res);
            if(res == testData[i].Democrat)
                right++;
            else
                wrong++;
        }
        return right / (right + wrong);
    }

    public double[] train_data(stateOverview[] trainData, double lr1, double lr2){
        double[] w = new double[10];
        w[0] = randomNumber(0, 0.5);
        w[1] = randomNumber(0, 0.5);
        w[2] = randomNumber(0, 0.5);
        w[3] = randomNumber(0, 0.5);
        w[4] = randomNumber(0, 0.5);
        w[5] = randomNumber(0, 0.5);
        w[6] = randomNumber(0, 0.5);
        w[7] = randomNumber(0, 0.5);
        w[8] = randomNumber(0, 0.5);
        w[9] = randomNumber(0, 0.5);
        for (int i = 0; i < 1000; i++){
            double localError = 0;
            double globalError = 0;
            for (int j = 0; j < trainData.length * 2 / 3; j++){
                double res = CalcOut1(theta, w, trainData[j]);
                if (!Double.isNaN(res)) {
                    localError = trainData[j].Democrat - res;
                    w[0] += lr1 * localError * trainData[j].population;
                    w[1] += lr1 * localError * trainData[j].population_change;
                    w[2] += lr1 * localError * trainData[j].age65plus;
                    w[3] += lr1 * localError * trainData[j].black;
                    w[4] += lr1 * localError * trainData[j].hispanic;
                    w[5] += lr1 * localError * trainData[j].edu_bachelors;
                    w[6] += lr1 * localError * trainData[j].income;
                    w[7] += lr1 * localError * trainData[j].poverty;
                    w[8] += lr1 * localError * trainData[j].density;
                    w[9] += lr1 * localError;
                    globalError += (localError * localError);
                    if (globalError == 0) {
                        break;
                    }
                }
            }
            for (int j = trainData.length * 2 / 3; j < trainData.length; j++){
                double res = CalcOut1(theta, w, trainData[j]);
                if (!Double.isNaN(res)) {
                    localError = res - trainData[j].Democrat;
                    w[0] += lr2 * localError * trainData[j].population;
                    w[1] += lr2 * localError * trainData[j].population_change;
                    w[2] += lr2 * localError * trainData[j].age65plus;
                    w[3] += lr2 * localError * trainData[j].black;
                    w[4] += lr2 * localError * trainData[j].hispanic;
                    w[5] += lr2 * localError * trainData[j].edu_bachelors;
                    w[6] += lr2 * localError * trainData[j].income;
                    w[7] += lr2 * localError * trainData[j].poverty;
                    w[8] += lr2 * localError * trainData[j].density;
                    w[9] += lr2 * localError;
                    globalError += (localError * localError);
                    if (globalError == 0) {
                        break;
                    }
                }
            }
        }
        return w;
    }

    public static double randomNumber(double min , double max) {
        DecimalFormat df = new DecimalFormat("#.####");
        double d = min + Math.random() * (max - min);
        String s = df.format(d);
        double x = Double.parseDouble(s);
        return x;
    }

    public double CalcOut(double thresh, double[] w, stateOverview so){
        double res = w[0] * so.population + w[1] * so.population_change + w[2] * so.age65plus
                + w[3] * so.black + w[4] * so.hispanic + w[5] * so.edu_bachelors
                + w[6] * so.income + w[7] * so.poverty + w[8] * so.density + w[9];
        return (res > thresh ? 1: 0);
    }
    public double CalcOut1(double thresh, double[] w, stateOverview so){
        double res = w[0] * so.population + w[1] * so.population_change + w[2] * so.age65plus
                + w[3] * so.black + w[4] * so.hispanic + w[5] * so.edu_bachelors
                + w[6] * so.income + w[7] * so.poverty + w[8] * so.density + w[9];
        return res;
    }
}
