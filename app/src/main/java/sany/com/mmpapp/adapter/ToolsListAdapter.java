package sany.com.mmpapp.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import sany.com.mmpapp.R;

/**
 * Created by sunj7 on 16-12-20.
 */
public class ToolsListAdapter extends BaseAdapter {
    private Context ctx;
    private LayoutInflater inflater;
    private  String[] funcNamesArray;
    private TypedArray funcDrawablesArray;
    private  int[] tipsArray;
    public ToolsListAdapter(Context context,int[] tipsArr){
        this.ctx=context;
        this.inflater=(LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.funcDrawablesArray=ctx.getResources().obtainTypedArray(R.array.functionDrawableArray);
        this.funcNamesArray=ctx.getResources().getStringArray(R.array.fuctionNameArray);
        this.tipsArray=tipsArr;
    }
    @Override
    public int getCount() {
        return funcNamesArray.length;
    }

    @Override
    public Object getItem(int position) {
        return funcNamesArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layout=new RelativeLayout(ctx);
        inflater.inflate(R.layout.tools_item,layout,true);
        TextView tv_FuncName=(TextView)layout.findViewById(R.id.funcName);
        ImageView img_FuncDraw=(ImageView)layout.findViewById(R.id.funcImg);
        ImageView img_Tip=(ImageView)layout.findViewById(R.id.funcTipsImg);
        if(tipsArray[position]==1){
            img_Tip.setVisibility(View.VISIBLE);

        }else {
            img_Tip.setVisibility(View.GONE);
        }
        img_FuncDraw.setImageDrawable(funcDrawablesArray.getDrawable(position));
        tv_FuncName.setText(funcNamesArray[position]);
        return layout;
    }

}
