package com.example.mcpaper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.Nullable;

public final class FileUtils {

    private FileUtils() {
    }

    @Nullable
    public static String resolveFileName(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }

        String name = null;
        try {
            int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cursor.moveToFirst() && index >= 0) {
                name = cursor.getString(index);
            }
        } finally {
            cursor.close();
        }
        return name;
    }
}
