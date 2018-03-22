package com.cloud.verify.web.rest.util;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
@Component
public class VerifyCodeUtil {

    //验证码长度
    private final int CODE_LENGTH = 4;
    //随机字符串范围
    private  String RAND_RANGE = "abcdefghjkmnpqrstuvwxyz"+
        "ABCDEFGHJKLMNPQRSTUVWXYZ" +"23456789";

    //可选字体
    private String[] FontFamily={"Times New Roman","宋体","黑体","Arial Unicode MS","Lucida Sans"};
    //选转标志，验证码旋转
    private boolean ROTATE_FLAG=true;
    //高度，设置旋转后，高度设为30，否则设置成25，效果好一些
    private int Height = 30;

    private Random rand = new Random();
    private  char[] CHARS = RAND_RANGE.toCharArray();


    /**
     * 生成随机字符串
     * @return 随机字符串
     */
    public String getRandString(){
        StringBuffer vcode = new StringBuffer();
        for (int i = 0; i < CODE_LENGTH; i++)
            vcode.append(CHARS[rand.nextInt(CHARS.length)]);
        return vcode.toString();
    }

    /**
     * 生成随机颜色
     * @param ll 产生颜色值下限(lower limit)
     * @param ul 产生颜色值上限(upper limit)
     * @return 生成的随机颜色对象
     */
    private Color getRandColor(int ll, int ul){
        if (ll > 255){
            ll = 255;
        }
        if (ll < 1){
            ll = 1;
        }
        if (ul > 255){
            ul = 255;
        }
        if (ul < 1){
            ul = 1;
        }
        if (ul == ll){
            ul = ll + 1;
        }
        int r = rand.nextInt(ul - ll) + ll;
        int g = rand.nextInt(ul - ll) + ll;
        int b = rand.nextInt(ul - ll) + ll;
        Color color = new Color(r,g,b);
        return color;
    }

    /**
     * 生成指定字符串的图像数据
     * @param verifyCode 即将被打印的随机字符串
     * @return 生成的图像数据
     * */
    public BufferedImage getImage(String verifyCode){
        int width = CODE_LENGTH * 18 + 20;
        BufferedImage image = new BufferedImage(width, Height, BufferedImage.TYPE_INT_RGB);

        //获取图形上下文
        Graphics graphics = image.getGraphics();
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setColor(getRandColor(100,255));
        g2.fillRect(0, 0, width, Height);
        //边框
        //g2.drawRect(0,0,CODE_LENGTH * 18 + 20,Height);
        //3条干扰线
        for(int i=0;i<3;i++){
            g2.setColor(new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));
            g2.drawLine(rand.nextInt(width),rand.nextInt(Height),
                rand.nextInt(width),rand.nextInt(Height));
        }

        int fontsize;
        int fontstyle = 0;
        double oldrot = 0;
        String vcode = getRandString();
        for (int i=0; i<CODE_LENGTH; i++){
            fontsize = Math.abs(rand.nextInt(24));
            // 18-24 fontsize
            if(fontsize < 12){
                fontsize += 12;
            }
            if(fontsize < 18){
                fontsize += 6;
            }
            fontstyle=rand.nextInt(6) ;
            g2.setFont(new Font(FontFamily[rand.nextInt(5)], fontstyle, fontsize));
            double rot = -0.25 + Math.abs(Math.toRadians(rand.nextInt(25)));

            //如果设置选装标志，则旋转文字
            if(ROTATE_FLAG){
                g2.rotate(-oldrot,10,15);
                oldrot = rot;
                g2.rotate(rot,15 * i + 10 , 15);
            }

            float stroke = Math.abs(rand.nextFloat()%30);
            g2.setStroke(new BasicStroke(stroke));
            String temp = vcode.substring(i, i+1);
            g2.setColor(new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));
            g2.setColor(getRandColor(1,100));
            g2.drawString(temp, 18 * i + 10, 20);
        }
        //图像生效
        g2.dispose();
        return image;
    }
}
