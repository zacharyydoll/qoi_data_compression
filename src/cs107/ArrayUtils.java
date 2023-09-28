package cs107;

import java.util.ArrayList;

/**
 * Utility class to manipulate arrays.
 * @apiNote First Task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.2
 * @since 1.0
 */
public final class ArrayUtils {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private ArrayUtils() {
    }

    // ==================================================================================
    // =========================== ARRAY EQUALITY METHODS ===============================
    // ==================================================================================

    /**
     * Check if the content of both arrays is the same
     *
     * @param a1 (byte[]) - First array
     * @param a2 (byte[]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[] a1, byte[] a2) {
        boolean equals = true;
        if (a1.length == a2.length) {
            for (int i = 0; i < a1.length; i++) {
                if (a1[i] != a2[i])
                    equals = false;
            }
        } else if (a1.length == 0 || a2.length == 0)
            return Helper.fail("Not Implemented");

        else {
            equals = false;
        }
        return equals;
    }

    /**
     * Check if the content of both arrays is the same
     *
     * @param a1 (byte[][]) - First array
     * @param a2 (byte[][]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[][] a1, byte[][] a2) {
        boolean equals = true;
        if (a1.length == a2.length) {
            for (int i = 0; i < a1.length; i++) {
                if (a1[i] != a2[i])
                    equals = false;
            }
        } else if (a1.length == 0 || a2.length == 0)
            return Helper.fail("Not Implemented");

        else {
            equals = false;
        }
        return equals;
    }

    // ==================================================================================
    // ============================ ARRAY WRAPPING METHODS ==============================
    // ==================================================================================

    /**
     * Wrap the given value in an array
     *
     * @param value (byte) - value to wrap
     * @return (byte[]) - array with one element (value)
     */
    public static byte[] wrap(byte value) {
        byte[] wrapped_value = {value};
        return wrapped_value;
    }

    // ==================================================================================
    // ========================== INTEGER MANIPULATION METHODS ==========================
    // ==================================================================================

    /**
     * Create an Integer using the given array. The input needs to be considered
     * as "Big Endian"
     * (See handout for the definition of "Big Endian")
     *
     * @param bytes (byte[]) - Array of 4 bytes
     * @return (int) - Integer representation of the array
     * @throws AssertionError if the input is null or the input's length is different from 4
     */
    public static int toInt(byte[] bytes) {
        assert bytes != null;
        assert bytes.length == 4;
        int n = 0;
        for (int i = 0; i < 4; i++) {
            int shiftPos = 8 * (4 - (i + 1)); //shift the first byte by 24 bits, second by 16, 3rd by 8, 4th by 0
            n += (bytes[i] << shiftPos); //& 0xFF; //for each passage, add to n the element of the byte array that is now shifted to correct pos
        }
        return n;
    }

    /**
     * Separate the Integer (word) to 4 bytes. The Memory layout of this integer is "Big Endian"
     * (See handout for the definition of "Big Endian")
     *
     * @param value (int) - The integer
     * @return (byte[]) - Big Endian representation of the integer
     */
    public static byte[] fromInt(int value) {
        byte[] convertedInt = new byte[4];
        for (int i = 0; i < 4; i++) {
            int shiftPos = 8 * (4 - (i + 1));
            convertedInt[i] = (byte) (value >> shiftPos);
        }
        return convertedInt;
    }

    // ==================================================================================
    // ========================== ARRAY CONCATENATION METHODS ===========================
    // ==================================================================================

    /**
     * Concatenate a given sequence of bytes and stores them in an array
     *
     * @param bytes (byte ...) - Sequence of bytes to store in the array
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     */
    public static byte[] concat(byte... bytes) {
        assert bytes != null;
        byte[] array = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            array[i] = bytes[i];
        }
        if (bytes.length == 0)
            return Helper.fail("Not Implemented");
        return array;
    }

    /**
     * Concatenate a given sequence of arrays into one array
     *
     * @param tabs (byte[] ...) - Sequence of arrays
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     *                        or one of the inner arrays of input is null.
     */
    public static byte[] concat(byte[]... tabs) {
        int n = 0;
        assert tabs != null;
        byte[] array;
        for (int i = 0; i < tabs.length; i++) {
            n += tabs[i].length;
            assert tabs[i] != null;
        }
        array = new byte[n];
        int x = 0;
        for (int i = 0; i < tabs.length; i++) {
            byte[] arr = tabs[i];
            assert arr != null;
            for (int j = 0; j < arr.length; j++) {
                array[x] = arr[j];
                x++;
            }
        }
        return array;
    }


    // ==================================================================================
    // =========================== ARRAY EXTRACTION METHODS =============================
    // ==================================================================================

    /**
     * Extract an array from another array
     *
     * @param input  (byte[]) - Array to extract from
     * @param start  (int) - Index in the input array to start the extract from
     * @param length (int) - The number of bytes to extract
     * @return (byte[]) - The extracted array
     * @throws AssertionError if the input is null or start and length are invalid.
     *                        start + length should also be smaller than the input's length
     */
    public static byte[] extract(byte[] input, int start, int length) {
        assert input != null;
        assert start >= 0;
        assert length > 0;
        byte[] newTab = new byte[length];
        for (int i = 0; i < length; i++) {
            newTab[i] = input[start + i];
        }
        return newTab;
    }


    /**
     * Create a partition of the input array.
     * (See handout for more information on how this method works)
     *
     * @param input (byte[]) - The original array
     * @param sizes (int ...) - Sizes of the partitions
     * @return (byte[][]) - Array of input's partitions.
     * The order of the partition is the same as the order in sizes
     * @throws AssertionError if one of the parameters is null
     *                        or the sum of the elements in sizes is different from the input's length
     */
    public static byte[][] partition(byte[] input, int... sizes) {
        assert input != null;
        assert sizes != null;
        //assert sizes.length == input.length;
        int index = 0;
        byte[][] partitionedTab = new byte[sizes.length][];
        for (int i = 0; i < sizes.length; i++) {
            byte[] split = new byte[sizes[i]];
            int endIndex = sizes[i];
            for (int j = 0; j < endIndex; j++) {
                split[j] = input[index];
                index++;
            }
            partitionedTab[i] = split;
        }
        return partitionedTab;
    }


    // ==================================================================================
    // ============================== ARRAY FORMATTING METHODS ==========================
    // ==================================================================================

    /**
     * Format a 2-dim integer array
     * where each dimension is a direction in the image to
     * a 2-dim byte array where the first dimension is the pixel
     * and the second dimension is the channel.
     * See handouts for more information on the format.
     *
     * @param input (int[][]) - image data
     * @return (byte [][]) - formatted image data
     * @throws AssertionError if the input is null
     *                        or one of the inner arrays of input is null
     */
    public static byte[][] imageToChannels(int[][] input) {
        assert input != null;
        final int h = input.length;
        final int w = input[0].length;
        byte[][] image2Channel = new byte[h * w][4];
        for (int i = 0; i < input.length; i++)
            assert input[i] != null;

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int p = input[i][j];
                byte[] p2Byte = fromInt(p);
                /*final int r = QOISpecification.r;
                final int g = QOISpecification.g;
                final int b = QOISpecification.b;
                p2Byte[0] = (byte) ((p >> 24) & 0xFF);
                p2Byte[1] = (byte) ((p >> 16) & 0xFF);
                p2Byte[2] = (byte) ((p >> 8) & 0xFF);
                p2Byte[3] = (byte) (p & 0xFF);*/
                image2Channel[i * w + j] = new byte[]{p2Byte[1], p2Byte[2], p2Byte[3], p2Byte[0]};
            }
        }
        return image2Channel;
    }


    /**
     * Format a 2-dim byte array where the first dimension is the pixel
     * and the second is the channel to a 2-dim int array where the first
     * dimension is the height and the second is the width
     *
     * @param input  (byte[][]) : linear representation of the image
     * @param height (int) - Height of the resulting image
     * @param width  (int) - Width of the resulting image
     * @return (int[][]) - the image data
     * @throws AssertionError if the input is null
     *                        or one of the inner arrays of input is null
     *                        or input's length differs from width * height
     *                        or height is invalid
     *                        or width is invalid
     */
    public static int[][] channelsToImage(byte[][] input, int height, int width) {
        assert input != null;
        for (int i = 0; i < input.length; i++)
            assert input[i] != null;
        assert input.length == width * height;
        assert height > 0;
        assert width > 0;
        int[][] channels2Image = new int[height][width];

        int temp = 0;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                byte[] pixel = input[temp];
                byte[] reordered = new byte[]{pixel[3], pixel[0], pixel[1], pixel[2]}; //rearranging to ARGB
                int n = ArrayUtils.toInt(reordered);
                channels2Image[i][j] = n;
                temp++;
            }
        }
        return channels2Image;
    }
}
