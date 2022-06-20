package pavle.vukovic.memorygame;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Adapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Element> list;
    private final String DB_NAME = "GAMES";
    private PlayerDBHelper helper;
    private String username;
    String delete_url;
    HTTPHelper http_helper = new HTTPHelper();

    public Adapter(Context mContext, String user) {
        this.mContext = mContext;
        list = new ArrayList<>();
        helper = new PlayerDBHelper(mContext, DB_NAME, null, 1);
        username = user;
        delete_url = "http://192.168.85.223:3000/score/?username=" + username;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        Object o = null;
        try {
            o = list.get(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    public List<String> getBestByUserName(String username){
        List<String> o = null;

        for (Element el : list){
            if(el.getmText1_1().equals(username)){
                o = el.getResults();
                break;
            }
        }

        return o;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addElement(Element e) {
        list.add(e);
        notifyDataSetChanged();
    }

    public void removeElementByIndex(int i) {
        list.remove(i);
        notifyDataSetChanged();
    }

    public void removeElementByValue(Element e) {
        list.remove(e);
        notifyDataSetChanged();
    }

    public void deleteAll() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        Log.d("Adapter_TAG", "getView");
        if (view == null) {
            Log.d("Adapter_TAG", "getView - null");
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_element, null);
            viewHolder = new ViewHolder();

            viewHolder.textView1_1 = view.findViewById(R.id.username_text_view);
            viewHolder.textView1_2 = view.findViewById(R.id.best_text_view);
            viewHolder.textView1_3 = view.findViewById(R.id.best_value_text_view);
            viewHolder.textView2_1 = view.findViewById(R.id.email_text_view);
            viewHolder.textView2_2 = view.findViewById(R.id.worst_text_view);
            viewHolder.textView2_3 = view.findViewById(R.id.worst_value_text_view);
            viewHolder.button  = view.findViewById(R.id.remove_me_button);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Element element = (Element) getItem(i);

        viewHolder.textView1_1.setText(element.getmText1_1());
        viewHolder.textView1_2.setText(element.getmText1_2());
        viewHolder.textView1_3.setText(element.getmText1_3());
        viewHolder.textView2_1.setText(element.getmText2_1());
        viewHolder.textView2_2.setText(element.getmText2_2());
        viewHolder.textView2_3.setText(element.getmText2_3());

        if(viewHolder.textView1_1.getText().equals(username)){
            viewHolder.button.setBackgroundColor(mContext.getResources().getColor(R.color.tamno_zelena));
            viewHolder.button.setEnabled(true);
        }else{
            viewHolder.button.setBackgroundColor(mContext.getResources().getColor(R.color.siva));
            viewHolder.button.setEnabled(false);
        }

        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeElementByValue(element);
                helper.delete(element.getmText1_1());

                //salji zahtev na srv
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int ret = 0;

                        try {
                            ret = http_helper.httpDelete(delete_url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        return view;
    }

    static class ViewHolder {
        TextView textView1_1;
        TextView textView1_2;
        TextView textView1_3;
        TextView textView2_1;
        TextView textView2_2;
        TextView textView2_3;
        Button button;
    }
}
