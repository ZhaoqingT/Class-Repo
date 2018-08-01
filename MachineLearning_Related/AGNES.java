package searchmethods;

import java.util.ArrayList;
import searchmethods.OtherTool.*;
/**
 * Created by zhaoqingteng on 12/6/16.
 */
public class AGNES {
    public class cluster{
        stateOverview lastcenter;
        stateOverview center;
        ArrayList<stateOverview> so;

        public cluster(stateOverview last, stateOverview cen, ArrayList<stateOverview> ar){
            lastcenter = last;
            center = cen;
            so = ar;
        }
        public cluster(){

        }
        public cluster(stateOverview s1, stateOverview s2){
            so = new ArrayList<>();
            so.add(s1);
            so.add(s2);
            lastcenter = new OtherTool().generate();
            center = new OtherTool().merge(s1, s2);
        }
        public cluster(stateOverview s){
            so = new ArrayList<>();
            so.add(s);
            lastcenter = s;
            center = s;
        }
    }

    public ArrayList<cluster> init(stateOverview[] so){
        ArrayList<cluster> res = new ArrayList<>();
        boolean[] visited = new boolean[so.length];
        for (int i = 0; i < so.length; i++){
            double mindis = Integer.MAX_VALUE;
            int min = -1;
            if (!visited[i]) {
                for (int j = i + 1; j < so.length; j++) {
                    if (!visited[j]) {
                        if (distance(so[i], so[j]) <= mindis && distance(so[i], so[j]) < 1) {
                            min = j;
                            mindis = distance(so[i], so[j]);
                        }
                    }
                }
            }
            if (min != -1) {
                cluster c = new cluster(so[i], so[min]);
                res.add(c);
                visited[i] = true;
                visited[min] = true;
            }
        }
        for (int i = 0; i < so.length; i++){
            if (!visited[i]){
                cluster c = new cluster(so[i]);
                res.add(c);
                visited[i] = true;
            }
        }
        return res;
    }

    public double distance(stateOverview s1, stateOverview s2){
        double dis = 0;
        dis += Math.pow((s1.Democrat - s2.Democrat), 2);
        dis += Math.pow((s1.population - s2.population), 2);
        dis += Math.pow((s1.population_change - s2.population_change), 2);
        dis += Math.pow((s1.age65plus - s2.age65plus), 2);
        dis += Math.pow((s1.black - s2.black), 2);
        dis += Math.pow((s1.hispanic - s2.hispanic), 2);
        dis += Math.pow((s1.edu_bachelors - s2.edu_bachelors), 2);
        dis += Math.pow((s1.income - s2.income), 2);
        dis += Math.pow((s1.poverty - s2.poverty), 2);
        dis += Math.pow((s1.density - s2.density), 2);
        dis = Math.sqrt(dis);
        return dis;
    }

    public double distance(cluster c1, cluster c2){
        return distance(c1.center, c2.center);
    }

    public cluster merge(cluster c1, cluster c2){
        cluster c = new cluster();
        c.so = new ArrayList<>();
        c.center = new OtherTool().merge(c1.center, c2.center);
        c.lastcenter = new OtherTool().merge(c1.lastcenter, c2.lastcenter);
        for (int i = 0; i < c1.so.size(); i++){
            c.so.add(c1.so.get(i));
        }
        for (int i = 0; i < c2.so.size(); i++){
            c.so.add(c2.so.get(i));
        }
        return c;
    }

    public ArrayList<cluster> merge(ArrayList<cluster> c){
        boolean[] visited = new boolean[c.size()];
        ArrayList<cluster> res = new ArrayList<>();
        for (int i = 0; i < c.size(); i++){
            int min = -1;
            double dis = Integer.MAX_VALUE;
            if (!visited[i]) {
                for (int j = i + 1; j < c.size(); j++) {
                    if (!visited[j]) {
                        if (distance(c.get(i), c.get(j)) <= dis && distance(c.get(i), c.get(j)) < 1) {
                            min = j;
                            dis = distance(c.get(i), c.get(j));
                        }
                    }
                }
            }
            if (min != -1) {
                res.add(merge(c.get(i), c.get(min)));
                visited[min] = true;
                visited[i] = true;
            }
        }
        for (int i = 0; i < c.size(); i++){
            if (!visited[i]){
                res.add(c.get(i));
            }
        }
        return res;
    }

    public ArrayList<cluster> methods(String path, int k){
        OtherTool.stateOverview[] so = new OtherTool().readFile(path);
        ArrayList<cluster> res = init(so);
        while(res.size() > k){
            res = merge(res);
        }
        return res;
    }
}
