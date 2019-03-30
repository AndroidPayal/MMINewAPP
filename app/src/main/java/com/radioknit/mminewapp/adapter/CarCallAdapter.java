package com.radioknit.mminewapp.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radioknit.mminewapp.Const;
import com.radioknit.mminewapp.R;
import com.radioknit.mminewapp.Utils;
import com.radioknit.mminewapp.activity.CarCallActivity;

import static com.radioknit.mminewapp.activity.CarCallActivity.showState;
import static com.radioknit.mminewapp.activity.CarCallActivity.showStateDown;
import static com.radioknit.mminewapp.activity.CarCallActivity.showStateUp;


/**
 * Created by nishant on 24/4/17.
 */

public class CarCallAdapter extends BaseAdapter {

    private static final String TAG = "CarCallIndicator";

    private Context mContext;
    protected ViewHolder mViewHolder;
    private View view;
    private LayoutInflater layoutInflater;
    private String cop1Calls;
    private String cop2Calls;
    private int callIndex;
    private CarCallIndicatorSignalListner mCarCallIndicatorSignalListner;
    private String strUpDnCalls;
    private int floorNo;
    public static TextView textViewStateAll[]=new TextView[16];
    public  static ImageView imageViewUpAll[] = new ImageView[16];
    public static ImageView imageViewDownAll[] = new ImageView[16];
    private Drawable drawableSel = null, drawableNotSel = null;


    public CarCallAdapter(Context context, String cop1Calls , String cop2Calls , CarCallIndicatorSignalListner carCallIndicatorSignalListner){
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        this.cop1Calls = cop1Calls;
        this.cop2Calls = cop2Calls;
        mCarCallIndicatorSignalListner = carCallIndicatorSignalListner;

    }

    public CarCallAdapter(Context context, String strUpDnCalls , int floorNO, CarCallIndicatorSignalListner carCallIndicatorSignalListner){
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        this.strUpDnCalls = strUpDnCalls;
        this.floorNo = floorNO;
        mCarCallIndicatorSignalListner = carCallIndicatorSignalListner;

    }

    class ViewHolder {

        TextView txtFloorNumber;
        ImageView imgUp;
        ImageView imgDown;
        LinearLayout llItem_call;
    }

    @Override
    public int getCount() {
        return Const.NO_OF_FLOORS;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

       final ViewHolder viewHolder ;

        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_calles, null);
            viewHolder = new ViewHolder();
            viewHolder.txtFloorNumber = (TextView) convertView.findViewById(R.id.tv_call_item_floor_no);
            viewHolder.imgUp = (ImageView) convertView.findViewById(R.id.img_calls_up_indicator);
            viewHolder.imgDown = (ImageView) convertView.findViewById(R.id.img_calls_dn_indicator);
            viewHolder.llItem_call = (LinearLayout) convertView.findViewById(R.id.llItems_calls);
//            if((15 - position) == 1)
//
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        final ViewHolder holder = (ViewHolder) view.getTag();
//        drawable = ContextCompat.getDrawable(mContext, R.drawable.circular_text_view_selected);
        drawableSel = mContext.getResources().getDrawable(R.drawable.circular_text_view_selected);
        drawableNotSel=mContext.getResources().getDrawable(R.drawable.circular_text_view);
//        drawable = mContext.getDrawable(R.drawable.circular_text_view_selected);
        viewHolder.txtFloorNumber.setText(""+(15-(position)));
        textViewStateAll[position]= viewHolder.txtFloorNumber;
        imageViewDownAll[position]= viewHolder.imgDown;
        imageViewUpAll[position]= viewHolder.imgUp;


        if(showState[position]==0){
            textViewStateAll[position].setBackground(drawableNotSel);
        }
        else if(showState[position]==1){
            textViewStateAll[position].setBackground(drawableSel);
        }

        if(showStateUp[position]==0){
            imageViewUpAll[position].setImageResource(R.drawable.up_arraow);
        }
        else if(showStateUp[position]==1){
            imageViewUpAll[position].setImageResource(R.drawable.up_green);
        }

        if(showStateDown[position]==0){
            imageViewDownAll[position].setImageResource(R.drawable.down_arr);
        }
        else if(showStateDown[position]==1){
            imageViewDownAll[position].setImageResource(R.drawable.down_green);
        }

        //for holding the position on selected item
        // for maintaining the car call state.

//        if((15-callIndex) == position){
//            Log.e(TAG, "Floor hilight = "+ (15- callIndex));
//            holder.txtFloorNumber.setBackgroundColor(mContext.getResources().getColor(R.color.red));
//        }
        if(Utils.isObjNotNull(cop1Calls)) {
            if (Utils.isStringNotNull(cop1Calls)) {
                if (cop1Calls.charAt(0) == '1') {
                    callIndex = 8;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }

                if (cop1Calls.charAt(1) == '1') {
                    callIndex = 7;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop1Calls.charAt(2) == '1') {
                    callIndex = 6;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop1Calls.charAt(3) == '1') {
                    callIndex = 5;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop1Calls.charAt(4) == '1') {
                    callIndex = 4;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop1Calls.charAt(5) == '1') {
                    callIndex = 3;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop1Calls.charAt(6) == '1') {
                    callIndex = 2;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop1Calls.charAt(7) == '1') {
                    callIndex = 1;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
            }
        }

        if(Utils.isObjNotNull(cop2Calls)) {
            if (Utils.isStringNotNull(cop2Calls)) {
                if (cop2Calls.charAt(0) == '1') {
                    callIndex = 16;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop2Calls.charAt(1) == '1') {
                    callIndex = 15;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop2Calls.charAt(2) == '1') {
                    callIndex = 14;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop2Calls.charAt(3) == '1') {
                    callIndex = 13;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop2Calls.charAt(4) == '1') {
                    callIndex = 12;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop2Calls.charAt(5) == '1') {
                    callIndex = 11;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop2Calls.charAt(6) == '1') {
                    callIndex = 10;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
                if (cop2Calls.charAt(7) == '1') {
                    callIndex = 9;
                    if ((Const.NO_OF_FLOORS - callIndex) == position) {
                        viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    }
                }
            }
        }

        viewHolder.txtFloorNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*viewHolder.txtFloorNumber.setBackground(drawableNotSel);
                    mCarCallIndicatorSignalListner.sendCarCallIndicatorSignal(position);*/
//                    viewHolder.txtFloorNumber.setTag(position);

                }
            });

        viewHolder.imgUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCarCallIndicatorSignalListner.sendUpCallIndicatorSignal(position);
                }
            });

        viewHolder.imgDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCarCallIndicatorSignalListner.sendDnCallIndicatorSignal(position);
                }
            });

            showUpDnCalls(viewHolder,strUpDnCalls, position);

        return convertView;
    }

    private void showUpDnCalls(ViewHolder holder,String strUpDnCalls, int position) {

        if(Utils.isObjNotNull(strUpDnCalls)) {
            if (strUpDnCalls.charAt(7) == '1') {
                if (floorNo == position) {
                    holder.imgDown.setImageResource(R.drawable.down_black_arraw);
                }
            }
            if (strUpDnCalls.charAt(6) == '1') {
                if (floorNo == position) {
                    holder.imgUp.setImageResource(R.drawable.up_black_arrow);
                }
            }
        }
    }

    public interface  CarCallIndicatorSignalListner{
          void sendCarCallIndicatorSignal(int position);
          void sendUpCallIndicatorSignal(int position);
          void sendDnCallIndicatorSignal(int position);
    }


    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

}
