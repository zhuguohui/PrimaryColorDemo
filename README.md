# PrimaryColorDemo
可以动态设置图片的主题色
# github地址
[PrimaryColorDemo](https://github.com/zhuguohui/PrimaryColorDemo/tree/master)
# 效果
## 原始图片
就是一张普通的png图片
![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/275127b55a864e53b0d2a879ba7ce5d4~tplv-k3u1fbpfcp-watermark.image?)
## 根据选择的主题色动态渲染。


![图片主题色3M.gif](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f624232b2e5d4613b1b0b12df18d9f27~tplv-k3u1fbpfcp-watermark.image?)


# 思考

最近在思考怎么实现动态的设置图片的主题色。不是那种渲染透明iocn。而是把图片的明暗关系保留。而改变其中的主题色。终于花了半天的时间研究出来了。和大家共享。

# 实现

思路很简单
1. 将图片从RGB色彩空间转化为YUV色彩空间
2. 使用ColorMatirx的setRotate(0, hue0);方法。将图片沿着Y轴旋转hue0的角度（角度从0到360度）
3. 在家图片从YUV色彩空间转化RGB色彩空间

# 原理

## YUV 空间

通俗的讲。YUV只是颜色的一种表达形式。Y中保存了图片的明暗度。UV保存了图片的色度信息。
下图所示，第一张是原图，依次是YUV分量。

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/09e6ae7ba32c45aeb6f4c64c42cad0a5~tplv-k3u1fbpfcp-watermark.image?)

## setRotate方法

ColorMatirx的setRotate方法可以将颜色沿着一个轴旋转，沿着的这个轴的信息就不会变化。

/**
 
* 用于色调的旋转运算
 
* axis=0 表示色调围绕红色进行旋转
 
* axis=1 表示色调围绕绿色进行旋转
 
* axis=2 表示色调围绕蓝色进行旋转
 
*/
 
public void setRotate(int axis, float degrees)


![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cba99321cd04422cade31f39566a2ca2~tplv-k3u1fbpfcp-watermark.image?)

1）围绕红色轴旋转

我们可以根据三原色来建立一个三维**向量**坐标系，当围绕红色旋转时，我们将红色虚化为一个点，绿色为横坐标，蓝色为纵坐标，旋转θ°。


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/48d92256cfcf4b65a4efcba63b3ae39b~tplv-k3u1fbpfcp-watermark.image?)

## 偷梁换柱

如果我们将颜色从RGB转换为YUV 那么这个时候调用setRotate(0,degrees)那么颜色就会以Y信息为轴，进行旋转degrees的角度。
由于没有改变Y信息，所以图片的明暗度不会发生变化。
代码如下

```java
  ColorMatrix cm = new ColorMatrix();
        ColorMatrix tmp = new ColorMatrix();

        cm.setRGB2YUV();
        tmp.setRotate(0, hue0);
        cm.postConcat(tmp);
        tmp.setYUV2RGB();
        cm.postConcat(tmp);


        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bitmap,0,0,paint);
```
## 难点计算这个转动角度

其实也很简单。我们只需获取原来图片中的主题色


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6e0430c1438c4b0cb1245471603ab733~tplv-k3u1fbpfcp-watermark.image?)

使用拾色器就可以搞定

然后根据目标色计算角度。

因为只转动UV信息

所以将原图中的UV信息组成一个二维向量，目标色的UV信息组成一个二维向量。

计算公式如下。


![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/795850c0adf34aff92b371cddcaf4918~tplv-k3u1fbpfcp-watermark.image?)

还需要注意一点
就是上面计算出来的角度是最小角度。
还要根据两个向量之间的叉积，来判断向量之间的位置关系。

**叉乘公式**

  
两个向量的叉乘，又叫向量积、外积、叉积，叉乘的运算结果是一个向量而不是一个标量。并且两个向量的叉积与这两个向量组成的坐标平面垂直。

一、二维向量叉乘公式：**a（x1，y1），b（x2，y2），则a×b=（x1y2-x2y1）**

根据叉乘结果的正负，可以判断两个向量的位置关系

****判断**点P在**向量**AB的左侧还是右侧，则可根据**向量**ABxAP 的叉乘结果r 来**判断**，根据右手定则：**

1.  若r > 0，则点P在**向量**AB的左侧；
1.  若r = 0，则点P在**向量**AB上；
1.  若r < 0，则点P在**向量**AB的右侧。


# 代码实现

注意，主题色写死在代码里面。要扩展需要自行改动。

最后的代码如下：

```java
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

}
```
