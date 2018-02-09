package com.lei.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanle on 2018/2/9.
 */

public class WuPanel extends View {

    private int mPanelWidth;
    private float mLineHeihgt;
    private static final int MAX_LINE = 10;
    private int MAX_IN_LINE = 5;

    private Paint mPaint = new Paint();
    private Bitmap mWhitePiece;//白棋
    private Bitmap mBlackPiece;//黑棋

    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;//棋子的高度为行高的3/4

    private boolean mIsWhite = true;//白棋下子
    private ArrayList<Point> mWhiteArray = new ArrayList<Point>();
    private ArrayList<Point> mBlackArray = new ArrayList<Point>();

    private boolean mIsGameOver;//游戏是否结束
    private boolean mIsWhiteWinner;//判断赢家
    private boolean print = true;

    public WuPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setBackgroundColor(0x44A0A0A0);//设置背景
        init();
    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.w);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);//拿到宽度
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);//拿到高度
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize,heightSize);

        if(widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize; //宽度由高度决定
        } else if(heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width,width);//宽高设置同样的值
    }

    @Override//宽高改变时进行回调
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeihgt = mPanelWidth * 1.0f / MAX_LINE;

        int pieceWidth = (int)(mLineHeihgt * ratioPieceOfLineHeight);//目标高度
        //绘制棋子尺寸
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth,false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mIsGameOver) {
            return false;
        }

        int action = event.getAction();
        if(action == MotionEvent.ACTION_UP) {
            int x = (int)event.getX();
            int y = (int)event.getY();
            Point point = getValidPoint(x, y);
            if(mWhiteArray.contains(point) || mBlackArray.contains(point)) {
                return false;
            }
            if(mIsWhite) {
                mWhiteArray.add(point);
            } else {
                mBlackArray.add(point);
            }

            invalidate();//请求重绘
            mIsWhite = !mIsWhite;
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int)(x / mLineHeihgt), (int)(y / mLineHeihgt));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);//绘制棋盘

        drawPieces(canvas);//绘制棋子

        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);
        if((whiteWin || blackWin) && print) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;

            String text = mIsWhiteWinner?"白胜":"黑胜";

            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();

            print = false;
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for(Point p : points) {
            int x = p.x;
            int y = p.y;

            boolean win = checkHorizontal(x, y, points);
            if(win){
                return true;
            }
             win = checkVetical(x, y, points);
            if(win){
                return true;
            }
            win = checkRightDiagonal(x, y, points);
            if(win){
                return true;
            }
            win = checkLeftDiagonal(x, y, points);
            if(win){
                return true;
            }


        }

        return false;
    }

    /**
     * 判断x,y位置的棋子，是否横向有相邻的五个一致
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        //左
        for(int i = 1 ; i < MAX_IN_LINE ; i ++) {
            if(points.contains(new Point(x - i, y))){
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_IN_LINE) {
            return true;
        }
        for(int i = 1 ; i < MAX_IN_LINE ; i ++) {
            if(points.contains(new Point(x + i, y))){
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_IN_LINE) {
            return true;
        }

        return false;
    }
    private boolean checkVetical(int x, int y, List<Point> points) {
        int count = 1;
        //上
        for(int i = 1 ; i < MAX_IN_LINE ; i ++) {
            if(points.contains(new Point(x, y - i))){
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_IN_LINE) {
            return true;
        }
        for(int i = 1 ; i < MAX_IN_LINE ; i ++) {
            if(points.contains(new Point(x , y + i))){
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_IN_LINE) {
            return true;
        }

        return false;
    }
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //左
        for(int i = 1 ; i < MAX_IN_LINE ; i ++) {
            if(points.contains(new Point(x - i, y - i))){
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_IN_LINE) {
            return true;
        }
        for(int i = 1 ; i < MAX_IN_LINE ; i ++) {
            if(points.contains(new Point(x + i, y + i))){
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_IN_LINE) {
            return true;
        }

        return false;
    }
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //左
        for(int i = 1 ; i < MAX_IN_LINE ; i ++) {
            if(points.contains(new Point(x - i, y + i))){
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_IN_LINE) {
            return true;
        }
        for(int i = 1 ; i < MAX_IN_LINE ; i ++) {
            if(points.contains(new Point(x + i, y - i))){
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_IN_LINE) {
            return true;
        }

        return false;
    }

    private void drawPieces(Canvas canvas) {//绘制棋子
        for(int i = 0 , n = mWhiteArray.size(); i < n ; i ++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece ,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeihgt,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeihgt,null);
        }
        for(int i = 0 , n = mBlackArray.size(); i < n ; i ++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece ,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeihgt,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeihgt,null);
        }
    }

    private void drawBoard(Canvas canvas) {//绘制棋盘
        int w = mPanelWidth;
        float lineHeight = mLineHeihgt;
        for(int i =  0; i < MAX_LINE ; i ++) {
            int startX = (int)(lineHeight / 2);
            int endX = (int)(w - lineHeight / 2);

            int y = (int)((0.5 + i) * lineHeight);

            canvas.drawLine(startX, y, endX, y, mPaint);//绘制横线
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }

    public void start() {//再来一局
        mBlackArray.clear();
        mWhiteArray.clear();
        mIsGameOver = false;
        mIsWhiteWinner = false;
        print = true;
        invalidate();
    }


    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_game_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            Bundle bundle = (Bundle)state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
