package com.gongw.mobile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;


/**
 * Created by gongw on 2018/7/11.
 */
public class RecyclerViewDivider extends RecyclerView.ItemDecoration{
    private int mOrientation = LinearLayoutManager.VERTICAL ;
    private int mItemSize = 1 ;
    private Context context;
    private int color = 0x88c7c7cc;

    private Paint mPaint ;

    private boolean footDivider = false;

    private int marginLeft = 0;
    private int marginRight = 0;
    private int marginTop  = 0;
    private int marginBottom = 0;
    /**
     * 构造方法传入布局方向，不可不传
     * @param context
     * @param orientation
     */
    public RecyclerViewDivider(Context context, int orientation) {
        this.mOrientation = orientation;
        if(orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL){
            throw new IllegalArgumentException("请传入正确的参数") ;
        }

        this.context = context;
        initDivider();
    }

    private void initDivider(){
        mItemSize = (int) TypedValue.applyDimension(mItemSize, TypedValue.COMPLEX_UNIT_DIP,context.getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG) ;
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setMargin(int marginTop,int marginLeft,int marginBottom,int marginRight){
        this.marginTop = marginTop;
        this.marginLeft = marginLeft;
        this.marginBottom = marginBottom;
        this.marginRight = marginRight;
    }

    public void setmItemSize(int itemSize){
        this.mItemSize = itemSize;
    }

    public void setColor(int color){
        mPaint.setColor(context.getResources().getColor(color));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == LinearLayoutManager.VERTICAL){
            drawVertical(c,parent) ;
        }else {
            drawHorizontal(c,parent) ;
        }
    }


    private void drawVertical(Canvas canvas, RecyclerView parent){
        final int left = parent.getPaddingLeft() + marginLeft;
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight()  - marginRight;
        final int childSize = footDivider ? parent.getChildCount() : parent.getChildCount()-1  ;
        for(int i = 0 ; i < childSize ; i ++){
            final View child = parent.getChildAt( i ) ;
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin ;
            final int bottom = top + mItemSize ;
            canvas.drawRect(left,top,right,bottom,mPaint);
        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent){
        final int top = parent.getPaddingTop()+ marginTop ;
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom() - marginBottom;
        final int childSize = footDivider ? parent.getChildCount() : parent.getChildCount()-1 ;
        for(int i = 0 ; i < childSize ; i ++){
            final View child = parent.getChildAt( i ) ;
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin ;
            final int right = left + mItemSize ;
            canvas.drawRect(left,top,right,bottom,mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == LinearLayoutManager.VERTICAL){
            outRect.set(0,0,0,mItemSize);
        }else {
            outRect.set(0,0,mItemSize,0);
        }
    }

    public void setFootDivider(boolean footDivider) {
        this.footDivider = footDivider;
    }
}
