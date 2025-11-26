package com.example.berenang10;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * Utility class to generate a QR Code Bitmap from a string.
 * This class requires the 'com.google.zxing:core' library dependency.
 */
public class QRCodeGenerator {

    /**
     * Generates a QR Code Bitmap.
     * @param content The string content to encode.
     * @param size The desired width and height of the bitmap (in pixels).
     * @return The generated Bitmap.
     * @throws WriterException if the content cannot be encoded.
     */
    public static Bitmap generateQRCodeBitmap(String content, int size) throws WriterException {
        // Use MultiFormatWriter to encode the content into a BitMatrix
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    size,
                    size
            );
        } catch (IllegalArgumentException illegalArgumentException) {
            // Content can't be encoded
            throw new WriterException("Content cannot be encoded into QR Code.");
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        // Convert the BitMatrix to an array of colors (pixels)
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                // Set pixel color based on the BitMatrix cell (true=black, false=white)
                pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        // Create the final Bitmap from the pixel array
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, size, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}