package searchmethods;

/**
 * Created by zhaoqingteng on 12/3/16.
 */
import searchmethods.OtherTool.*;

import java.util.ArrayList;

public class DecisionTree {

    OtherTool.stateOverview[] trainData_temp = new OtherTool().readFile("votes-train.csv");

    ArrayList<int[]> trainData = dataPro(trainData_temp);

    public class Node{
        int attribute;
        ArrayList<Node> child;
        boolean finish;
        int level;
        int res;
        Node(){}
        Node(int att, int result){
            attribute = att;
            res = result;
            finish = true;
        }
    }

    public double method(String path){
        boolean[] visited = {true,true,true,true,true,true,true,true,true,true};
        Node root = buildTree(trainData, 0, visited);
        ArrayList<int[]> testData = dataPro(new OtherTool().readFile(path));
        int size = testData.size();
        double right = 0;
        double wrong = 0;
        for (int i = 0; i < size; i++){
            int pre = predict(root, testData.get(i));
            if (pre == testData.get(i)[0])
                right++;
            else
                wrong++;
        }
        return right / (right + wrong);
    }

    public int predict(Node root, int[] data){
        if (root == null){
            return -1;
        }
        if (root.finish == true){
            return root.res;
        }
        int att = root.attribute;
        Node next = root.child.get(data[att] - 1);
        int res = predict(next, data);
        return res;
    }

    public int CalcuLevel1(ArrayList<int[]> so, boolean[] visited){
        int pos = 0;
        int neg = 0;
        int col = so.get(0).length;
        int row = so.size();
        for (int i = 0; i < row; i++){
            if (so.get(i)[0] == 0)
                neg++;
            else
                pos++;
        }
        double info = CalcuLevel4(pos, neg);
        double[] infopar = new double[col];
        for (int i = 1; i < col; i++){
            infopar[i] = info - CalcuLevel2(so, i);
        }
        double res = 0;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < col; i++){
            if (visited[i]){
                if (infopar[i] >= res) {
                    res = infopar[i];
                    max = i;
                }
            }
        }
        return max;
    }

    public double CalcuLevel2(ArrayList<int[]> so, int col){
        int total = so.size();
        int num1 = 0;
        int num2 = 0;
        int num3 = 0;
        int num4 = 0;
        double res = 0;
        ArrayList<int[]> da1 = new ArrayList<int[]>();
        ArrayList<int[]> da2 = new ArrayList<int[]>();
        ArrayList<int[]> da3 = new ArrayList<int[]>();
        ArrayList<int[]> da4 = new ArrayList<int[]>();
        for (int i = 0; i < total; i++){
            if (so.get(i)[col] == 1){
                num1++;
                da1.add(so.get(i));
            }
            if (so.get(i)[col] == 2){
                num2++;
                da2.add(so.get(i));
            }
            if (so.get(i)[col] == 3){
                num3++;
                da3.add(so.get(i));
            }
            if (so.get(i)[col] == 4){
                num4++;
                da4.add(so.get(i));
            }
        }
        res += (CalcuLevel3(da1) * num1 / total);
        res += (CalcuLevel3(da2) * num2 / total);
        res += (CalcuLevel3(da3) * num3 / total);
        res += (CalcuLevel3(da4) * num4 / total);
        return res;
    }

    public double CalcuLevel3(ArrayList<int[]> so){
        double res;
        int pos = 0;
        int neg = 0;
        for (int i = 0; i < so.size(); i++){
            if (so.get(i)[0] == 0){
                neg++;
            }else
            pos++;
        }
        res = CalcuLevel4(pos,neg);
        return res;
    }

    public double CalcuLevel4(int a, int b){
        if (a == 0 || b == 0){
            return 0;
        }
        double res1 = (Math.log(a) - Math.log(a + b)) / Math.log(2);
        double res2 = (Math.log(b) - Math.log(a + b)) / Math.log(2);
        res1 = res1 * (-a) / (a + b);
        res2 = res2 * (-b) / (a + b);
        return res1 + res2;
    }

    public int ResDecide(ArrayList<int[]> so){
        int size = so.size();
        int pos = 0;
        int neg = 0;
        for (int i = 0; i < size; i++){
            if (so.get(i)[0] == 0)
                neg++;
            else
                pos++;
        }
        return neg >= pos ? 0 : 1;
    }

    public Node buildTree(ArrayList<int[]> so, int level, boolean[] visited){
        int row = so.size();
        int col = so.get(0).length;
        int test = so.get(0)[0];
        boolean judge = true;
        Node root = new Node();
        root.level = level;
        if (level == col - 2){
            root.finish = true;
            root.res = ResDecide(so);
            return root;
        }
        for (int i = 0; i < row; i++){
            if (test != so.get(i)[0]) {
                judge = false;
                break;
            }
        }
        if (judge == true){
            root.finish = true;
            root.res = ResDecide(so);
            return root;
        }
        int att = CalcuLevel1(so, visited);
        visited[att] = false;
        root.attribute = att;
        root.child = new ArrayList<Node>();
        ArrayList<int[]> da1 = new ArrayList<int[]>();
        ArrayList<int[]> da2 = new ArrayList<int[]>();
        ArrayList<int[]> da3 = new ArrayList<int[]>();
        ArrayList<int[]> da4 = new ArrayList<int[]>();
        for (int i = 0; i < so.size(); i++){
            if (so.get(i)[att] == 1)
                da1.add(so.get(i));
            else if (so.get(i)[att] == 2)
                da2.add(so.get(i));
            else if (so.get(i)[att] == 3)
                da3.add(so.get(i));
            else
                da4.add(so.get(i));
        }
        if (!da1.isEmpty()) {
            boolean[] nextvisit1 = new boolean[visited.length];
            for (int i = 0; i < nextvisit1.length; i++){
                nextvisit1[i] = visited[i];
            }
            root.child.add(buildTree(da1, level + 1, nextvisit1));
        }else {
            int res = ResDecide(so);
            root.child.add(new Node(-1, res));
        }
        if (!da2.isEmpty()) {
            boolean[] nextvisit2 = new boolean[visited.length];
            for (int i = 0; i < nextvisit2.length; i++){
                nextvisit2[i] = visited[i];
            }
            root.child.add(buildTree(da2, level + 1, nextvisit2));
        }else {
            int res = ResDecide(so);
            root.child.add(new Node(-1, res));
        }
        if (!da3.isEmpty()) {
            boolean[] nextvisit3 = new boolean[visited.length];
            for (int i = 0; i < nextvisit3.length; i++){
                nextvisit3[i] = visited[i];
            }
            root.child.add(buildTree(da3, level + 1, nextvisit3));
        }else {
            int res = ResDecide(so);
            root.child.add(new Node(-1, res));
        }
        if (!da4.isEmpty()) {
            boolean[] nextvisit4 = new boolean[visited.length];
            for (int i = 0; i < nextvisit4.length; i++){
                nextvisit4[i] = visited[i];
            }
            root.child.add(buildTree(da4, level + 1, nextvisit4));
        }else {
            int res = ResDecide(so);
            root.child.add(new Node(-1, res));
        }
        return root;
    }

    public ArrayList<int[]> dataPro(stateOverview[] s){
        ArrayList<int[]> list = new ArrayList<int[]>();
        for (int i = 0; i < s.length; i++){
            int[] new_data = new int[10];
            new_data[0] = s[i].Democrat;
            if (s[i].population >= 0.007) {
                s[i].population = 4;
            }else if (s[i].population >= 0.0027 && s[i].population < 0.007) {
                s[i].population = 3;
            }else if (s[i].population >= 0.0012 && s[i].population < 0.0027) {
                s[i].population = 2;
            }else {
                s[i].population = 1;
            }
            new_data[1] = (int)s[i].population;
            if (s[i].population_change >= 0.43) {
                s[i].population_change = 4;
            }else if (s[i].population_change >= 0.375 && s[i].population_change < 0.43) {
                s[i].population_change = 3;
            }else if (s[i].population_change >= 0.335 && s[i].population_change < 0.375) {
                s[i].population_change = 2;
            }else {
                s[i].population_change = 1;
            }
            new_data[2] = (int)s[i].population_change;
            if (s[i].age65plus >= 0.3) {
                s[i].age65plus = 4;
            }else if (s[i].age65plus >= 0.245 && s[i].age65plus < 0.3) {
                s[i].age65plus = 3;
            }else if (s[i].age65plus >= 0.19 && s[i].age65plus < 0.245) {
                s[i].age65plus = 2;
            }else {
                s[i].age65plus = 1;
            }
            new_data[3] = (int)s[i].age65plus;
            if (s[i].black >= 0.13) {
                s[i].black = 4;
            }else if (s[i].black >= 0.03 && s[i].black < 0.13) {
                s[i].black = 3;
            }else if (s[i].black >= 0.009 && s[i].black < 0.03) {
                s[i].black = 2;
            }else {
                s[i].black = 1;
            }
            new_data[4] = (int)s[i].black;
            if (s[i].hispanic >= 0.1) {
                s[i].hispanic = 4;
            }else if (s[i].hispanic >= 0.04 && s[i].hispanic < 0.1) {
                s[i].hispanic = 3;
            }else if (s[i].hispanic >= 0.019 && s[i].hispanic < 0.04) {
                s[i].hispanic = 2;
            }else {
                s[i].hispanic = 1;
            }
            new_data[5] = (int)s[i].hispanic;
            if (s[i].edu_bachelors >= 0.26) {
                s[i].edu_bachelors = 4;
            }else if (s[i].edu_bachelors >= 0.18 && s[i].edu_bachelors < 0.26) {
                s[i].edu_bachelors = 3;
            }else if (s[i].edu_bachelors >= 0.12 && s[i].edu_bachelors < 0.18) {
                s[i].edu_bachelors = 2;
            }else {
                s[i].edu_bachelors = 1;
            }
            new_data[6] = (int)s[i].edu_bachelors;
            if (s[i].income >= 0.3) {
                s[i].income = 4;
            }else if (s[i].income >= 0.235 && s[i].income < 0.3) {
                s[i].income = 3;
            }else if (s[i].income >= 0.18 && s[i].income < 0.235) {
                s[i].income = 2;
            }else {
                s[i].income = 1;
            }
            new_data[7] = (int)s[i].income;
            if (s[i].poverty >= 0.37) {
                s[i].poverty = 4;
            }else if (s[i].poverty >= 0.29 && s[i].poverty < 0.37) {
                s[i].poverty = 3;
            }else if (s[i].poverty >= 0.215 && s[i].poverty < 0.29) {
                s[i].poverty = 2;
            }else {
                s[i].poverty = 1;
            }
            new_data[8] = (int)s[i].poverty;
            if (s[i].density >= 0.0055) {
                s[i].density = 4;
            }else if (s[i].density >= 0.0022 && s[i].density < 0.0055) {
                s[i].density = 3;
            }else if (s[i].density >= 0.0009 && s[i].density < 0.0022) {
                s[i].density = 2;
            }else {
                s[i].density = 1;
            }
            new_data[9] = (int)s[i].density;
            list.add(new_data);
        }
        return list;
    }
}
