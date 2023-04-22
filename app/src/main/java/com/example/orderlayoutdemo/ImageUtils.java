package com.example.orderlayoutdemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

/**
 * <pre>
 * Created by zhuguohui
 * Date: 2023/4/21
 * Time: 17:24
 * Desc:
 * </pre>
 */
public class ImageUtils {
    public static Bitmap handleImageEffect(Bitmap bitmap,int targetColor){
        //由于不能直接在原图上修改，所以创建一个图片，设定宽度高度与原图相同。为32位ARGB图片
        Bitmap currentBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //创建一个和原图相同大小的画布
        Canvas canvas = new Canvas(currentBitmap);
        //创建笔刷并设置抗锯齿
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        float hue0=getHub(Color.parseColor("#61BCF8"),targetColor);

        ColorMatrix cm = new ColorMatrix();
        ColorMatrix tmp = new ColorMatrix();

        cm.setRGB2YUV();
        tmp.setRotate(0, hue0);
        cm.postConcat(tmp);
        tmp.setYUV2RGB();
        cm.postConcat(tmp);


        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bitmap,0,0,paint);
        return currentBitmap;
    }



    private static int[] convertRGB2YUV(int color) {
        ColorMatrix cm = new ColorMatrix();
        cm.setRGB2YUV();
        final float[] yuvArray = cm.getArray();

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int[] result = new int[3];
        result[0] = floatToByte(yuvArray[0] * r + yuvArray[1] * g + yuvArray[2] * b);
        result[1] = floatToByte(yuvArray[5] * r + yuvArray[6] * g + yuvArray[7] * b) ;
        result[2] = floatToByte(yuvArray[10] * r + yuvArray[11] * g + yuvArray[12] * b) ;
        return result;
    }

    private static int floatToByte(float x) {
        int n = java.lang.Math.round(x);
        return n;
    }

    public static float getHub(int fromColor,int targetColor){

        int[] yuv1 = convertRGB2YUV(fromColor);
        int[] yuv2 = convertRGB2YUV(targetColor);
        //计算两个颜色的uv分量组成的向量之间的夹角
        return getDegreeBetweenVectors(new int[]{yuv1[1], yuv1[2]}, new int[]{yuv2[1], yuv2[2]});
    }

    /**
     * 计算向量1，顺时针旋转多少度可以得到向量2
     * 返回的度数为0到360度
     * @param vs1
     * @param vs2
     * @return
     */
    private static float getDegreeBetweenVectors(int[] vs1,int[] vs2 ){
        double cosDegree=0;
        //向量的内积
        int nj = vs1[0] * vs2[0] + vs1[1] * vs2[1];
        //叉积
        int cj=vs1[0]*vs2[1]-vs1[1]*vs2[0];
        double bl = Math.sqrt(vs1[0] * vs1[0] + vs1[1] * vs1[1]) *Math.sqrt(vs2[0] * vs2[0] + vs2[1] * vs2[1]);

        cosDegree= (nj/bl);
        double degree = Math.acos(cosDegree) / Math.PI * 180;
        //叉积大于0，表示向量2 在向量1的左边

        degree = (float) (cj > 0 ? 360- degree : degree);

        return (float) degree;
    }


}
