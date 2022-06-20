package pavle.vukovic.memorygame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Element {
    private String mText1_1;
    private String mText1_2;
    private String mText1_3;
    private String mText2_1;
    private String mText2_2;
    private String mText2_3;
    private Boolean mButton;
    private List<String> results;

    public Element(String username, String email, List<String> points){
        this.mText1_1 = username;
        this.mText1_2 = "Best";
        this.mText2_1 = email;
        this.mText2_2 = "Worst";
        this.mButton  = false;
        this.results = points;
        sort(results);
        this.mText1_3 = best(results);
        this.mText2_3 = worst(results);
    }

    public String getmText1_1() {
        return mText1_1;
    }

    public void setmText1_1(String mText1_1) {
        this.mText1_1 = mText1_1;
    }

    public String getmText1_2() {
        return mText1_2;
    }

    public void setmText1_2(String mText1_2) {
        this.mText1_2 = mText1_2;
    }

    public String getmText1_3() {
        return mText1_3;
    }

    public void setmText1_3(String mText1_3) {
        this.mText1_3 = mText1_3;
    }

    public String getmText2_1() {
        return mText2_1;
    }

    public void setmText2_1(String mText2_1) {
        this.mText2_1 = mText2_1;
    }

    public String getmText2_2() {
        return mText2_2;
    }

    public void setmText2_2(String mText2_2) {
        this.mText2_2 = mText2_2;
    }

    public String getmText2_3() {
        return mText2_3;
    }

    public void setmText2_3(String mText2_3) {
        this.mText2_3 = mText2_3;
    }

    public Boolean getmButton() {
        return mButton;
    }

    public void setmButton(Boolean mButton) {
        this.mButton = mButton;
    }

    public void sort(List<String> list){
        List<Integer> list_int = new ArrayList<Integer>();

        for (String str : list){
            list_int.add(Integer.parseInt(str));
        }

        Collections.sort(list_int, Collections.reverseOrder());

        list.clear();

        for (Integer integer : list_int){
            list.add(Integer.toString(integer));
        }
    }

    public String best(List<String> list){
        return list.get(0);
    }

    public String worst(List<String> list){
        return list.get(list.size() - 1);
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }
}
