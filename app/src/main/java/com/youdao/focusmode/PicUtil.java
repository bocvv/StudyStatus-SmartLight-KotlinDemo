package com.youdao.focusmode;

public class PicUtil {


    static byte[] NV21MirrorFlip(byte[] nv21_data, int width, int height) {
        int i;
        int left, right;
        byte temp;
        int startPos = 0;

        // mirror Y
        for (i = 0; i < height; i++) {
            left = startPos;
            right = startPos + width - 1;
            while (left < right) {
                temp = nv21_data[left];
                nv21_data[left] = nv21_data[right];
                nv21_data[right] = temp;
                left++;
                right--;
            }
            startPos += width;
        }


        // mirror U and V
        int offset = width * height;
        startPos = 0;
        for (i = 0; i < height / 2; i++) {
            left = offset + startPos;
            right = offset + startPos + width - 2;
            while (left < right) {
                temp = nv21_data[left];
                nv21_data[left] = nv21_data[right];
                nv21_data[right] = temp;
                left++;
                right--;

                temp = nv21_data[left];
                nv21_data[left] = nv21_data[right];
                nv21_data[right] = temp;
                left++;
                right--;
            }
            startPos += width;
        }
        return nv21_data;
    }


    /**
     * 此处为顺时针旋转270
     *
     * @param data        旋转前的数据
     * @param imageWidth  旋转前数据的宽
     * @param imageHeight 旋转前数据的高
     * @return 旋转后的数据
     */
    public static byte[] rotateYUV420Degree270(byte[] data, int imageWidth, int imageHeight) {

        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];

        // Rotate the Y luma

        int i = 0;

        for (int x = imageWidth - 1; x >= 0; x--) {

            for (int y = 0; y < imageHeight; y++) {

                yuv[i] = data[y * imageWidth + x];
                i++;

            }
        }// Rotate the U and V color components
        i = imageWidth * imageHeight;

        for (int x = imageWidth - 1; x > 0; x = x - 2) {

            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i++;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i++;

            }

        }

        return yuv;

    }

}
