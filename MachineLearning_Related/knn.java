/**
 * Created by zhaoqingteng on 11/30/16.
 */

package searchmethods;

import searchmethods.OtherTool.*;
import java.util.Comparator;
import java.util.PriorityQueue;


public class knn {

    private static class Node {
        stateOverview so;
        double dis;

        public Node(stateOverview s, double f) {
            so = s;
            dis = f;
        }
    }

    private class CComparator<T> implements Comparator<T>{

        @Override
        public int compare(T t, T t1) {
            Node a = (Node) t;
            Node b = (Node) t1;
            if(a.dis > b.dis)
                return -1;
            else
                return 1;
        }
    }

    OtherTool.stateOverview[] trainData = new OtherTool().readFile("votes-train.csv");

    public float method(String path, int k, int[] a){
        OtherTool.stateOverview[] testData = new OtherTool().readFile(path);
        float right = 0;
        float wrong = 0;
        for (int i = 0; i < testData.length; i++){
            int res = decide(testData[i], k, a);
            if (res == testData[i].Democrat)
                right++;
            else
                wrong++;
        }
        return right / (right + wrong);
    }

    public int decide(stateOverview testData, int k, int[] a){
        PriorityQueue<Node> q = new PriorityQueue<>(new CComparator<>());
        for (int i = 0; i < trainData.length; i++){
            double dis = getDistance(testData, i, a);
            q.offer(new Node(trainData[i], dis));
        }
        int dem = 0;
        for (int i = 0; i < trainData.length - k; i++){
            q.remove();
        }
        for (int i = 0; i < k; i++){
            Node cur = q.poll();
            if(cur.so.Democrat == 0)
                dem++;
            else
                dem--;
        }
        if(dem > 0)
            return 0;
        else
            return 1;
    }

    public double getDistance(stateOverview testData, int i, int[] a){
        return a[0] * Math.abs(testData.population - trainData[i].population)
                + a[1] * Math.abs(testData.population_change - trainData[i].population_change)
                + a[2] * Math.abs(testData.age65plus - trainData[i].age65plus)
                + a[3] * Math.abs(testData.black - trainData[i].black)
                + a[4] * Math.abs(testData.hispanic - trainData[i].hispanic)
                + a[5] * Math.abs(testData.edu_bachelors - trainData[i].edu_bachelors)
                + a[6] * Math.abs(testData.income - trainData[i].income)
                + a[7] * Math.abs(testData.poverty - trainData[i].poverty)
                + a[8] * Math.abs(testData.density - trainData[i].density);
    }
}
