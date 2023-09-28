package cs107;

import java.util.Arrays;

import static cs107.Helper.Image;
import static cs107.Helper.generateImage;

/**
 * "Quite Ok Image" Decoder
 * @apiNote Third task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.2
 * @since 1.0
 */
public final class QOIDecoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIDecoder(){}

    // ==================================================================================
    // =========================== QUITE OK IMAGE HEADER ================================
    // ==================================================================================

    /**
     * Extract useful information from the "Quite Ok Image" header
     * @param header (byte[]) - A "Quite Ok Image" header
     * @return (int[]) - Array such as its content is {width, height, channels, color space}
     * @throws AssertionError See handouts section 6.1
     */
    public static int[] decodeHeader(byte[] header){
        /*byte[] magic = QOISpecification.QOI_MAGIC;
        for (int i = 0; i < 4; i++)
            assert header[i] == magic[i];
        assert header.length == QOISpecification.HEADER_SIZE;
        assert (header[12] == QOISpecification.RGB) || (header[12] == QOISpecification.RGBA);
        assert (header[13] == QOISpecification.ALL) || (header[13] == QOISpecification.sRGB);
        byte[] b = ArrayUtils.extract(header, 4, 10);
        byte[][] partition = ArrayUtils.partition(b, 4, 4, 1, 1);
        int[] arr = new int[4];
        //partition[2] = new byte[]{partition[2][0], 0, 0, 0};
        //partition[3] = new byte[]{partition[3][0], 0, 0, 0};
        partition[2] = ArrayUtils.fromInt(3);
        partition[3] = ArrayUtils.fromInt(0);
        for (int i = 0; i < partition.length; i++) {
            arr[i] = partition[i][3];
        }
        return arr;*/
        assert header!=null;
        assert header.length==QOISpecification.HEADER_SIZE;
        assert header[12] == QOISpecification.RGB || header[12] ==QOISpecification.RGBA;
        assert header[13] == QOISpecification.ALL || header[13] ==QOISpecification.sRGB;
        int n = ArrayUtils.toInt(ArrayUtils.extract(header, 4, 4));
        int n2 = ArrayUtils.toInt(ArrayUtils.extract(header, 8, 4));
        int[] output = {n, n2, (int) header[12], (int) header[13]};
        return output;
    }

    // ==================================================================================
    // =========================== ATOMIC DECODING METHODS ==============================
    // ==================================================================================

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param input (byte[]) - Stream of bytes to read from
     * @param alpha (byte) - Alpha component of the pixel
     * @param position (int) - Index in the buffer
     * @param idx (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.1
     */
    public static int decodeQoiOpRGB(byte[][] buffer, byte[] input, byte alpha, int position, int idx) {
        assert buffer != null;
        assert input != null;
        assert position < buffer[0].length * buffer.length; //making sure pos != 0 && the position is inferior to buffer length
        assert idx < input.length;                          //making sure idx is not negative, and not superior to the length of the input

        byte[] RGB = ArrayUtils.extract(input, idx, 3);
        //assert RGB[0] != 0 || RGB[1] != 0 || RGB[2] != 0;   //making sure that the pixel is not empty of color
        byte[] a = ArrayUtils.wrap(alpha);
        byte[] pixel = ArrayUtils.concat(RGB, a);
        buffer[position] = pixel;    //storing the pixel in the buffer at the given position.
        return QOISpecification.RGB; //return value if RGB regardless (i.e 3 as there are three color channels)
    }

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param input (byte[]) - Stream of bytes to read from
     * @param position (int) - Index in the buffer
     * @param idx (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.2
     */
    public static int decodeQoiOpRGBA(byte[][] buffer, byte[] input, int position, int idx){
        assert buffer != null;
        assert input != null;
        assert (position >= 0 && position < buffer.length); //making sure pos != 0 && the position is inferior to buffer length
        assert (idx >= 0 && idx < input.length);            //making sure idx is not negative, and not superior to the length of the input

        byte[] RGBA = ArrayUtils.extract(input, idx, 4);
        assert (RGBA[0] != 0 || RGBA[1] != 0 || RGBA[2] != 0) && RGBA[3] != 0; //not empty of color and alpha != 0.
        buffer[position] = RGBA;                                               //storing the pixel in the buffer at the given position.
        return QOISpecification.RGBA;   //return value is RGBA regardless, because the amount of occupied bytes is always 4, as there are four channels
    }

    /**
     * Create a new pixel following the "QOI_OP_DIFF" schema.
     * @param previousPixel (byte[]) - The previous pixel
     * @param chunk (byte) - A "QOI_OP_DIFF" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.4
     */
    public static byte[] decodeQoiOpDiff(byte[] previousPixel, byte chunk){
        //assert chunk == QOISpecification.QOI_OP_DIFF_TAG;

        assert previousPixel != null;
        assert previousPixel.length == 4;

        byte previousR = previousPixel[QOISpecification.r];
        byte previousG = previousPixel[QOISpecification.g];
        byte previousB = previousPixel[QOISpecification.b];
        byte dr =  (byte) ((previousR) + ((chunk >> 4) & 0b11)-2);// -2 because variations are stocked with an offset of 2 (p.20)
        byte dg = (byte) ((previousG) + ((chunk >> 2) & 0b11)-2); //finding the different channel values of the new pixel by
        byte db = (byte) ((previousB) + (chunk & 0b11)-2);        //adding the values of the differences to the values of the previous pixel for each channel.
        byte a = previousPixel[QOISpecification.a];
        //shift 4 for red because red there is green and blue (2 x 2 bits) to the left of it, and the tag in front of it
        // &0b11 because we only want to keep the values of red (two bits), same for green and blue.
        byte[] arr = {dr, dg, db, a};
        return arr;
    }

    /**
     * Create a new pixel following the "QOI_OP_LUMA" schema
     * @param previousPixel (byte[]) - The previous pixel
     * @param data (byte[]) - A "QOI_OP_LUMA" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.5
     */
    public static byte[] decodeQoiOpLuma(byte[] previousPixel, byte[] data){
        assert previousPixel != null;
        assert previousPixel.length == 4;

        //For the first byte : //data[0] >>>6 extracting LUMA_TAG (2 bits)
        // data[0]&0b00_11_11_11 getting the values of dg (6 bits) 00 at first because first two are the tag.
        //For the second byte : extracts the dr-dg value from data, and the db-dg value (each 4 bits long).

        byte[] firstByte = {(byte)(data[0]>>>6&0b11),(byte)(data[0]&0b00_11_11_11)}; //tag and dg
        byte[] secondByte ={(byte)((data[1]>>>4&0b11_11)),(byte)((data[1]&0b11_11))};//(dr - dg) and (db - dg)
        byte[][] luma = {firstByte, secondByte};

        //note: I know I could've used concat, it was just easier to read.

        byte[] output = {
                (byte)(previousPixel[QOISpecification.r] + luma[1][0] + luma[0][1]-40), //(dr - dg) +  dg = dr -> by adding dr to the r value of the previous pixel we get the new value of r
                (byte)(previousPixel[QOISpecification.g] + luma[0][1] - 32),            //(dg - dg) + dg = green value of new pixel
                (byte)(previousPixel[QOISpecification.b] + luma[1][1]+ luma[0][1]-40),  //same as for red, 40 = 32 + 8
                previousPixel[QOISpecification.a]                                       //alpha is the same for both pixels
        };
        return output;
    }


    /**
     * Store the given pixel in the buffer multiple times
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param pixel (byte[]) - The pixel to store
     * @param chunk (byte) - a QOI_OP_RUN data chunk
     * @param position (int) - Index in buffer to start writing from
     * @return (int) - number of written pixels in buffer
     * @throws AssertionError See handouts section 6.2.6
     */
    public static int decodeQoiOpRun(byte[][] buffer, byte[] pixel, byte chunk, int position){
        assert buffer != null;      //Run records the amount of consecutive repetitions of a same pixel.
        assert pixel != null;
        assert pixel.length == 4;   //the tag for RUN
        int counter = chunk + 65;   // 63 and 64 impossible because they are the QOI_RGB and QOI_RGBA tags, so max is 65
        for (int i = position; i < (position + counter); i++) //starts at pos and ends at pos + amount of consecutive pixels
            buffer[i] = pixel;                                //filling the buffer with the pixel that needs to be reproduced over a length of position + counter
        return counter -1;          //number of pixels in the buffer minus 1.
    }

    // ==================================================================================
    // ========================= GLOBAL DECODING METHODS ================================
    // ==================================================================================

    /**
     * Decode the given data using the "Quite Ok Image" Protocol
     * @param data (byte[]) - Data to decode
     * @param width (int) - The width of the expected output
     * @param height (int) - The height of the expected output
     * @return (byte[][]) - Decoded "Quite Ok Image"
     * @throws AssertionError See handouts section 6.3
     */
    public static byte[][] decodeData(byte[] data, int width, int height) {
        assert data != null;
        assert width <= data.length;
        assert height <= data.length;
        byte[] previous = QOISpecification.START_PIXEL;
        byte[][] hash = new byte[64][4];
        byte[][] array = new byte[width * height][4];

        for (int idx = 0, i = 0; idx < data.length; i++) { //idx iterates across the encoded bytes, and is incremented by the amount of bytes consumed at each step.
            byte b = data[idx];                            //i is the amount of pixels for each run, which is where to place them in the returned array.
            if ((b >>> 6 & 0b11) == (QOISpecification.QOI_OP_RUN_TAG >>> 6 & 0b11) //checking if the tag of the byte is RUN
            && b != QOISpecification.QOI_OP_RGB_TAG                                //making sure to not confuse it with RGB or RGBA, that are also 0b11
            && b != QOISpecification.QOI_OP_RGBA_TAG) {
                i += decodeQoiOpRun(array, previous, b, idx);  //if the tag is RUN, update the progression index i.
                idx++;                                         //adding 1 to idx so it's in first position of next bloc.
            }
            else if ((b >>> 6 & 0b11) == (QOISpecification.QOI_OP_INDEX_TAG >> 0b11)) { //checking if the tag is the same as QOI_INDEX (0) -> no need >>>
                array[i] = hash[b & 0b11_11_11];               //add the value the byte of hash at index HASH to the return array (p.32)
                idx++;                                                                  // increment index to access next byte.
            }
            else if ((b >>> 6 & 0b11) == (QOISpecification.QOI_OP_DIFF_TAG >>> 6 & 0b11)) { //check if the tag is the same as QOI_DIFF
                array[i] = decodeQoiOpDiff(previous, b);
                byte hashIdx = QOISpecification.hash(array[i]);
                hash[hashIdx] = array[i];
                idx++;
            }
            else if ((b >>> 6 & 0b11) == (QOISpecification.QOI_OP_LUMA_TAG >>> 6 & 0b11)) {
                byte[] luma = new byte[2];      //luma has 2 bytes
                luma[0] = b;
                luma[1] = data[idx + 1];        //needs to be idx+1 because we need the next byte, as luma is defined by two bytes
                array[i] = decodeQoiOpLuma(previous, luma);
                byte hashIdx = QOISpecification.hash(array[i]);
                hash[hashIdx] = array[i];
                idx += 2;                  //adding two instead of 1, because luma has two bytes
            }
            else if ((b == QOISpecification.QOI_OP_RGB_TAG)) {
                int RGB = decodeQoiOpRGB(array, data, previous[QOISpecification.a], i, idx+1) + 1; //we want the next bytes, so idx+1
                idx += RGB;                                                                            //decodeQoiRGB returns the amount of consumed bytes -> add one to get a total of 4 bytes (1 pixel)
                byte hashIdx = QOISpecification.hash(array[i]);
                hash[hashIdx] = array[i];
            }
            else {
                int RGBA = decodeQoiOpRGBA(array, data, i, idx+1) + 1; //we want the next byte, so idx+1
                idx += RGBA;                                               //decodeQoiRGBA returns the amount of consumed bytes -> add one to get 4 bytes in total (and move on to the next PIXEL, instead of just next byte)
                byte hashIdx = QOISpecification.hash(array[i]);
                hash[hashIdx] = array[i];
            }
            previous = array[i]; //the previous becomes the current and the loop repeats, like in EncodeData.
        }
        byte[][] expected = { {0,0,0,-1}, {0,0,0,-1}, {0,0,0,-1}, {0,-1,0,-1},{-18,-20,-18,-1},{0,0,0,-1}, {100,100,100,-1}, {90,90,90,90}};
        for (int i = 0; i < array.length; i++) {
            System.out.println(Arrays.toString(array[i]) + " || " + Arrays.toString(expected[i]));
        }
        return array;
    }


    /**
     * Decode a file using the "Quite Ok Image" Protocol
     * @param content (byte[]) - Content of the file to decode
     * @return (Image) - Decoded image
     * @throws AssertionError if content is null
     */
    public static Image decodeQoiFile(byte[] content){
        assert content!=null;
        byte[] header = ArrayUtils.extract(content, 0, QOISpecification.HEADER_SIZE);
        byte[] blocks = ArrayUtils.extract(content, QOISpecification.HEADER_SIZE ,content.length - QOISpecification.HEADER_SIZE - QOISpecification.QOI_EOF.length);
        int[] decodedHeader = decodeHeader(header);
        byte[][] decodedData = decodeData(blocks, decodedHeader[0], decodedHeader[1]);
        int[][] channels2image = ArrayUtils.channelsToImage(decodedData, decodedHeader[1], decodedHeader[0]);
        Image image = generateImage(channels2image, (byte) decodedHeader[2], (byte) decodedHeader[3]);
        return image;
    }
}

