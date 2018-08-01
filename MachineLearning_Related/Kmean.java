package searchmethods;

/**
 * Created by zhaoqingteng on 12/5/16.
 */

import searchmethods.OtherTool.*;

import java.util.ArrayList;
import java.util.Random;

public class Kmean {

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
    }

    public double getlast(cluster c){
        return distance(c.lastcenter, c.center);
    }

    public cluster pro(cluster c){
        if (c.so.size() == 0){
            return c;
        }
        c.lastcenter = new OtherTool().copy(c.center);
        double dem = 0;
        double pop = 0;
        double poc = 0;
        double age = 0;
        double bla = 0;
        double his = 0;
        double edu = 0;
        double inc = 0;
        double pov = 0;
        double den = 0;
        for (int i = 0; i < c.so.size(); i++){
            dem = dem + c.so.get(i).Democrat;
            pop = pop + c.so.get(i).population;
            poc = poc + c.so.get(i).population_change;
            age = age + c.so.get(i).age65plus;
            bla = bla + c.so.get(i).black;
            his = his + c.so.get(i).hispanic;
            edu = edu + c.so.get(i).edu_bachelors;
            inc = inc + c.so.get(i).income;
            pov = pov + c.so.get(i).poverty;
            den = den + c.so.get(i).density;
        }
        dem = dem / c.so.size();
        pop = pop / c.so.size();
        poc = poc / c.so.size();
        age = age / c.so.size();
        bla = bla / c.so.size();
        his = his / c.so.size();
        edu = edu / c.so.size();
        inc = inc / c.so.size();
        pov = pov / c.so.size();
        den = den / c.so.size();
        if (dem > 0.3)
            c.center.Democrat = 1;
        else
            c.center.Democrat = 0;
        c.center.population = pop;
        c.center.population_change = poc;
        c.center.age65plus = age;
        c.center.black = bla;
        c.center.hispanic = his;
        c.center.edu_bachelors = edu;
        c.center.income = inc;
        c.center.poverty = pov;
        c.center.density = den;

        return c;
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


    public cluster init(stateOverview[] so){

        cluster c = new cluster();
        c.so = new ArrayList<>();
        c.center = new OtherTool().generate();
        c.lastcenter = new OtherTool().generate();
        Random r = new Random();
        int len = so.length;
        for (int i = 0; i < len / 100; i++){
            int num = r.nextInt(so.length);
            c.so.add(so[num]);
        }

        cluster res = pro(c);
        return res;
    }

    public ArrayList<cluster> method(String path, int k){
        stateOverview[] so = new OtherTool().readFile(path);
        ArrayList<cluster> c = new ArrayList<>();
        boolean change = true;
        for (int i = 0; i < k; i++){
            c.add(init(so));
        }
        int time = 0;
        while (change){
            for (int i = 0; i < so.length; i++){
                double dis1 = distance(so[i], c.get(0).center);
                double dis2 = distance(so[i], c.get(1).center);
                if (dis1 <= dis2){
                    if (!c.get(0).so.contains(so[i]))
                        c.get(0).so.add(so[i]);
                    if (c.get(1).so.contains(so[i]))
                        c.get(1).so.remove(so[i]);
                }else {
                    if (!c.get(1).so.contains(so[i]))
                        c.get(1).so.add(so[i]);
                    if (c.get(0).so.contains(so[i]))
                        c.get(0).so.remove(so[i]);
                }
            }
            pro(c.get(0));
            pro(c.get(1));
            if (getlast(c.get(0)) <= 0.001 || getlast(c.get(1)) <= 0.001)
                change = false;
        }
        return c;
    }


}
