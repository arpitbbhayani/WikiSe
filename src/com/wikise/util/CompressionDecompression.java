package com.wikise.util;

import java.nio.MappedByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by Arpit Bhayani on 6/2/14.
 */
public class CompressionDecompression {

    public byte[] compress ( String line ) {

        boolean shift = true;
        byte b = 0;

        byte byteArray[] = new byte[((line.length() + 1) / 2)];

        int k = 0;

        for ( int i = 0 ; i < line.length() ; i++ ) {

            char currentChar = line.charAt(i);

            if ( currentChar == '$' ) {
                b = 15;
            }
            else if ( currentChar == ':' ) {
                b = 14;
            }
            else {
                    /* 0-9 */
                b = (byte) (b | ((int)currentChar - (int)'0')+1);
            }

            if ( shift ) {
                b = (byte) (b << 4);
                shift = false;
                //compressed.append((char)b);
                byteArray[k++] = b;
                b = 0;
            }
            else {
                //compressed.setCharAt(compressed.length() - 1, (char) b);
                byteArray[k-1] = (byte) (byteArray[k-1] | b);
                shift = true;
                b = 0;
            }

        }

        return byteArray;
    }


    public String decompress( MappedByteBuffer compressedBytes ) {

        StringBuilder decompressed = new StringBuilder();
        for ( int i = 0 ; i < compressedBytes.capacity() ; i++ ) {

            byte b = (byte) compressedBytes.get(i);

            int v = (b >>> 4 & 0x0000000f);
            if ( v == 15 ) {
                decompressed.append('$');
            }
            else if ( v == 14 ) {
                decompressed.append(':');
            }
            else if ( v == 0 ) {
                break;
            }
            else {
                decompressed.append((char) ((int)v - 1 + (int)'0'));
            }

            v = (b & 0x0000000f);
            if ( v == 15 ) {
                decompressed.append('$');
            }
            else if ( v == 14 ) {
                decompressed.append(':');
            }
            else if ( v == 0 ) {
                break;
            }
            else {
                decompressed.append((char) ((int)v - 1 + (int)'0'));
            }

        }

        return new String(decompressed);
    }

    public String decompress( byte[] compressedBytes ) {

        StringBuilder decompressed = new StringBuilder();
        for ( int i = 0 ; i < compressedBytes.length ; i++ ) {

            byte b = (byte) compressedBytes[i];

            int v = (b >>> 4 & 0x0000000f);
            if ( v == 15 ) {
                decompressed.append('$');
            }
            else if ( v == 14 ) {
                decompressed.append(':');
            }
            else if ( v == 0 ) {
                break;
            }
            else {
                decompressed.append((char) ((int)v - 1 + (int)'0'));
            }

            v = (b & 0x0000000f);
            if ( v == 15 ) {
                decompressed.append('$');
            }
            else if ( v == 14 ) {
                decompressed.append(':');
            }
            else if ( v == 0 ) {
                break;
            }
            else {
                decompressed.append((char) ((int)v - 1 + (int)'0'));
            }

        }

        return new String(decompressed);
    }

}
