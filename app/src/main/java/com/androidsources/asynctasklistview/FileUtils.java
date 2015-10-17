package com.androidsources.asynctasklistview;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Gowtham Chandrasekar on 17-10-2015.
 */

public class FileUtils {

    public static void close(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(OutputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}