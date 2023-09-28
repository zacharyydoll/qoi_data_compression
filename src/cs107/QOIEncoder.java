package cs107;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * "Quite Ok Image" Encoder
 * @apiNote Second task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.2
 * @since 1.0
 */
public final class QOIEncoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIEncoder() {
    }

    // ==================================================================================
    // ============================ QUITE OK IMAGE HEADER ===============================
    // ==================================================================================

    /**
     * Generate a "Quite Ok Image" header using the following parameters
     *
     * @param image (Helper.Image) - Image to use
     * @return (byte[]) - Corresponding "Quite Ok Image" Header
     * @throws AssertionError if the colorspace or the number of channels is corrupted or if the image is null.
     *                        (See the "Quite Ok Image" Specification or the handouts of the project for more information)
     */
    public static byte[] qoiHeader(Helper.Image image) {
        assert image != null;
        byte[] header = new byte[14];
        byte[] colorSpaces2 = ArrayUtils.fromInt(0);
        byte[] magic = QOISpecification.QOI_MAGIC;
        int[][] imagePixels = image.data();
        int height = imagePixels.length;
        int width = imagePixels[0].length;
        byte[] heightB = ArrayUtils.fromInt(height);
        byte[] widthB = ArrayUtils.fromInt(width);
        byte[] channels = {image.channels()};

        header[0] = magic[0];
        header[1] = magic[1];
        header[2] = magic[2];
        header[3] = magic[3];
        header[4] = widthB[0];
        header[5] = widthB[1];
        header[6] = widthB[2];
        header[7] = widthB[3];
        header[8] = heightB[0];
        header[9] = heightB[1];
        header[10] = heightB[2];
        header[11] = heightB[3];
        header[12] = channels[0];
        header[13] = colorSpaces2[3];

        //sorry, this is disgusting, but I thought it was the quickest method...

        return header;
    }

    // ==================================================================================
    // ============================ ATOMIC ENCODING METHODS =============================
    // ==================================================================================

    /**
     * Encode the given pixel using the QOI_OP_RGB schema
     *
     * @param pixel (byte[]) - The Pixel to encode
     * @return (byte[]) - Encoding of the pixel using the QOI_OP_RGB schema
     * @throws AssertionError if the pixel's length is not 4
     */
    public static byte[] qoiOpRGB(byte[] pixel) {
        assert pixel.length == 4;
        byte[] RGBValues = new byte[4];
        RGBValues[0] = QOISpecification.QOI_OP_RGB_TAG;
        byte[] RGB = ArrayUtils.extract(pixel, 0, 3);
        RGBValues[1] = pixel[0];
        RGBValues[2] = pixel[1];
        RGBValues[3] = pixel[2];
        return RGBValues;

        //storing the data of the pixel with the tag in front
    }

    /**
     * Encode the given pixel using the QOI_OP_RGBA schema
     *
     * @param pixel (byte[]) - The pixel to encode
     * @return (byte[]) Encoding of the pixel using the QOI_OP_RGBA schema
     * @throws AssertionError if the pixel's length is not 4
     */
    public static byte[] qoiOpRGBA(byte[] pixel) {

        assert pixel.length == 4;
        byte[] RGBAValues = new byte[5];
        RGBAValues[0] = QOISpecification.QOI_OP_RGBA_TAG;
        RGBAValues[1] = pixel[0];
        RGBAValues[2] = pixel[1];
        RGBAValues[3] = pixel[2];
        RGBAValues[4] = pixel[3];

        //storing the data of the pixel with the tag in front

        return RGBAValues;
    }

    /**
     * Encode the index using the QOI_OP_INDEX schema
     *
     * @param index (byte) - Index of the pixel
     * @return (byte[]) - Encoding of the index using the QOI_OP_INDEX schema
     * @throws AssertionError if the index is outside the range of all possible indices
     */
    public static byte[] qoiOpIndex(byte index) {
        return ArrayUtils.wrap(index);
    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_DIFF schema
     *
     * @param diff (byte[]) - The difference between 2 pixels
     * @return (byte[]) - Encoding of the given difference
     * @throws AssertionError if diff doesn't respect the constraints or diff's length is not 3
     *                        (See the handout for the constraints)
     */
    public static byte[] qoiOpDiff(byte[] diff) {
        assert diff != null;
        //assert diff.length == 3;
        assert diff[1] < 2 && diff[1] > -3;         //asserting that all of the channels are within the given differences
        assert diff[2] < 2 && diff[2] > -3;         //asserting that all of the channels are within the given differences
        assert diff[0] < 2 && diff[0] > -3;         //asserting that all of the channels are within the given differences
        byte tag = QOISpecification.QOI_OP_DIFF_TAG;

        byte[] arr = new byte[1];
        byte red = (byte) ((diff[0] + 2) * 16);     //diff is stored with an offset of 2
        byte green = (byte) ((diff[1] + 2) * 4);    //diff is stored with an offset of 2
        byte blue = (byte) (diff[2] + 2);           //diff is stored with an offset of 2
        arr[0] = (byte) (tag | red | green | blue);

        return arr;
    }


    /**
     * Encode the difference between 2 pixels using the QOI_OP_LUMA schema
     *
     * @param diff (byte[]) - The difference between 2 pixels
     * @return (byte[]) - Encoding of the given difference
     * @throws AssertionError if diff doesn't respect the constraints
     *                        or diff's length is not 3
     *                        (See the handout for the constraints)
     */
    public static byte[] qoiOpLuma(byte[] diff) {
        assert diff != null;
        byte tag = QOISpecification.QOI_OP_LUMA_TAG;
        byte green = (byte) (diff[1] + 32);             //add an offset of 32 for green
        byte RG = (byte) ((diff[0] - green + 8) * 16);  // add an offset of 8 (cf slide 44 moodle) 16 = 2 * 8
        byte BG = (byte) (diff[2] - green + 8);         //red and blue both have an offset of 8
        //byte tagOrGreen = (byte) (green | tag);
        //byte redOrBlue = (byte) (RG | BG);
        //arr = {(byte) (tag | green), (byte)(RG | BG)}; USE CONCAT!!
        byte[] arr = {(byte)(QOISpecification.QOI_OP_LUMA_TAG |(diff[1] + 32)), (byte)(((diff[0] - diff[1] + 8) * 16) | (diff[2] - diff[1]+ 8))};
        return arr;
    }

    /**
     * Encode the number of similar pixels using the QOI_OP_RUN schema
     *
     * @param count (byte) - Number of similar pixels
     * @return (byte[]) - Encoding of count
     * @throws AssertionError if count is not between 0 (exclusive) and 63 (exclusive)
     */
    public static byte[] qoiOpRun(byte count) {
        assert (0 < count && count < 63);   //can't be 0 because repeating a pixel zero times is useless
        byte offset = (byte) (count - 1);
        byte tag = QOISpecification.QOI_OP_RUN_TAG;
        byte b = (byte) (tag + offset);
        byte[] arr = ArrayUtils.wrap(b);
        return arr;
    }

    // ==================================================================================
    // ============================== GLOBAL ENCODING METHODS  ==========================
    // ==================================================================================

    /**
     * Encode the given image using the "Quite Ok Image" Protocol
     * (See handout for more information about the "Quite Ok Image" protocol)
     *
     * @param image (byte[][]) - Formatted image to encode
     * @return (byte[]) - "Quite Ok Image" representation of the image
     */
    public static byte[] encodeData(byte[][] image) {
        ArrayList<Byte> tab = new ArrayList<>();
        byte[][] hash = new byte[64][4]; //use QOISpecification.hash()
        int counter = 0;                 // for QOI_OP_RUN

        byte[] previous = QOISpecification.START_PIXEL;
        byte[] current;
        for (int i = 0; i < image.length; i++) {
            current = image[i];                               //setting the current to image[i], and previous for the very first pixel is START
            if (ArrayUtils.equals(current, previous)) {
                counter++;                                    //as long as the current is the same as the previous, memorize the amount of same consecutive pixels
                if (counter == 62 || i == image.length - 1) { //can't go over 62 -> if == 62 then qoiOpRUN
                    tab.add(qoiOpRun((byte) counter)[0]);
                    counter = 0;                              //resetting counter to zero
                }
            }
            else {
                if (counter >= 1) { //if counter != 0
                    tab.add(qoiOpRun((byte) counter)[0]);
                    counter = 0;
                }
                if (ArrayUtils.equals(current, hash[QOISpecification.hash(current)])) { //checking if the pixel is already in the hash
                    tab.add(qoiOpIndex(QOISpecification.hash(current))[0]);
                }
                else {
                    hash[QOISpecification.hash(current)] = current;     //add the current to the hash table at index hash
                    if (current[QOISpecification.a] == previous[QOISpecification.a]) {
                        byte r = QOISpecification.r;                    //using the channel tags to access the colors of the previous pixel
                        byte g = QOISpecification.g;                    //and get the values of the differences
                        byte b = QOISpecification.b;
                        byte rDiff = (byte) (current[r] - previous[r]); //getting the diff values for each channel
                        byte gDiff = (byte) (current[g] - previous[g]);
                        byte bDiff = (byte) (current[b] - previous[b]);
                        byte[] diff = new byte[]{rDiff, gDiff, bDiff};
                        byte RG = (byte) (diff[r] - diff[g]);
                        byte BG = (byte) (diff[b] - diff[g]);
                        if ((diff[r] > -3 && diff[r] < 2)               //if alpha is the same and color diffs are within conditions of Diff
                                && (diff[g] > -3 && diff[g] < 2)
                                && (diff[b] > -3 && diff[b] < 2)) {
                            tab.add(qoiOpDiff(diff)[0]);
                        } else if ((-33 < diff[g] && diff[g] < 32)      //if alpha is the same and color diffs are within conditions of Luma
                                && (-9 < RG && RG < 8)
                                && (-9 < BG && BG < 8)) {
                            tab.add(qoiOpLuma(diff)[0]);
                            tab.add(qoiOpLuma(diff)[1]);
                        } else {                                        //if alpha is the same but colors different
                            tab.add(qoiOpRGB(current)[0]);
                            tab.add(qoiOpRGB(current)[1]);
                            tab.add(qoiOpRGB(current)[2]);
                            tab.add(qoiOpRGB(current)[3]);
                        }
                    }
                    else {                                              //if nothing in common, including alpha channels
                        tab.add(qoiOpRGBA(current)[0]);                 // adding the values of RGBA manually at the right positions
                        tab.add(qoiOpRGBA(current)[1]);
                        tab.add(qoiOpRGBA(current)[2]);
                        tab.add(qoiOpRGBA(current)[3]);
                        tab.add(qoiOpRGBA(current)[4]);
                        }
                    }
                }
                previous = current;          //the previous becomes the current and the loop repeats itself, so that the current pixel always has previous
            }
        byte[] array = new byte[tab.size()]; //looping through the array and storing it's values in a byte array that I can return
        for (int j = 0; j < tab.size(); j++) {
            array[j] = tab.get(j);
        }
        return array;
    }



        /**
         * Creates the representation in memory of the "Quite Ok Image" file.
         * @apiNote THE FILE IS NOT CREATED YET, THIS IS JUST ITS REPRESENTATION.
         * TO CREATE THE FILE, YOU'LL NEED TO CALL Helper::write
         * @param image (Helper.Image) - Image to encode
         * @return (byte[]) - Binary representation of the "Quite Ok File" of the image
         * @throws AssertionError if the image is null
         */
        public static byte[] qoiFile (Helper.Image image) {
            assert image != null;
            byte[] header = qoiHeader(image);
            byte[] encodedData = encodeData(ArrayUtils.imageToChannels(image.data()));
            byte[] tag = QOISpecification.QOI_EOF;
            byte[] arr = ArrayUtils.concat(header, encodedData, tag); //putting the whole thing together
            return arr;
        }
    }
