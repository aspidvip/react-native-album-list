package im.shimo.react.albums;

import android.content.ContentUris;
import android.database.Cursor;
import android.provider.MediaStore;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RNAlbumsModule extends ReactContextBaseJavaModule {

    public RNAlbumsModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNAlbumsModule";
    }


    @ReactMethod
    public void getImageList(ReadableMap options, Promise promise) {
        ArrayList<String> projection = new ArrayList<>();
        ArrayList<ReadableMap> columns = new ArrayList<>();

        setColumn("path", MediaStore.Images.Media.DATA, projection, columns);

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("title", MediaStore.Images.Media.TITLE);
        fieldMap.put("name", MediaStore.Images.Media.DISPLAY_NAME);
        fieldMap.put("size", MediaStore.Images.Media.SIZE);
        fieldMap.put("description", MediaStore.Images.Media.DESCRIPTION);
        fieldMap.put("orientation", MediaStore.Images.Media.ORIENTATION);
        fieldMap.put("type", MediaStore.Images.Media.MIME_TYPE);
        fieldMap.put("album", MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        Iterator<Map.Entry<String, String>> fieldIterator = fieldMap.entrySet().iterator();

        while (fieldIterator.hasNext()) {
            Map.Entry<String, String> pair = fieldIterator.next();

            if (shouldSetField(options, pair.getKey())) {
                setColumn(pair.getKey(), pair.getValue(), projection, columns);
            }

            fieldIterator.remove();
        }

        if (shouldSetField(options, "location")) {
            setColumn("latitude", MediaStore.Images.Media.LATITUDE, projection, columns);
            setColumn("longitude", MediaStore.Images.Media.LONGITUDE, projection, columns);
        }

        if (shouldSetField(options, "date")) {
            setColumn("added", MediaStore.Images.Media.DATE_ADDED, projection, columns);
            setColumn("modified", MediaStore.Images.Media.DATE_MODIFIED, projection, columns);
            setColumn("taken", MediaStore.Images.Media.DATE_TAKEN, projection, columns);
        }

        if (shouldSetField(options, "dimensions")) {
            setColumn("width", MediaStore.Images.Media.WIDTH, projection, columns);
            setColumn("height", MediaStore.Images.Media.HEIGHT, projection, columns);
        }


        Cursor cursor = getReactApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection.toArray(new String[projection.size()]),
                null,
                null,
                null
        );


        Map<String, Integer> columnIndexMap = new HashMap<>();
        WritableArray list = Arguments.createArray();

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            for (int i = 0; i < projection.size(); i++) {
                String field = projection.get(i);
                columnIndexMap.put(field, cursor.getColumnIndex(field));
            }

            do {
                Iterator<ReadableMap> columnIterator = columns.iterator();

                WritableMap image = Arguments.createMap();

                while (columnIterator.hasNext()) {
                    ReadableMap column = columnIterator.next();
                    setWritableMap(image, column.getString("name"), cursor.getString(columnIndexMap.get(column.getString("columnName"))));
                }

                list.pushMap(image);
            } while (cursor.moveToNext());
            cursor.close();
        }

        promise.resolve(list);
    }

    @ReactMethod
    public void getAlbumList(ReadableMap options, Promise promise) {
        // which image properties are we querying
        String[] PROJECTION_BUCKET = {
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns._ID
        };

        String BUCKET_ORDER_BY = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

        
        Cursor cursor = getReactApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                PROJECTION_BUCKET,
                null,
                null,
                BUCKET_ORDER_BY
        );

        HashMap<String, WritableMap> map = new HashMap();
        if (cursor != null && cursor.moveToFirst()) {
            String bucketName;
            String bucketId;
            String date;
            String id;

            int bucketNameColumn = cursor.getColumnIndex(
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
            int bucketIdColumn = cursor.getColumnIndex(
                    MediaStore.Images.ImageColumns.BUCKET_ID);
            int dateColumn = cursor.getColumnIndex(
                    MediaStore.Images.ImageColumns.DATE_TAKEN);
            int idColumn = cursor.getColumnIndex(
                    MediaStore.Images.ImageColumns._ID);
            do {
                bucketId = cursor.getString(bucketIdColumn);

                if (map.containsKey(bucketId) == false) {
                    // Get the field values
                    bucketName = cursor.getString(bucketNameColumn);
                    date = cursor.getString(dateColumn);
                    id = cursor.getString(idColumn);

                    WritableMap image = Arguments.createMap();
                    setWritableMap(image, "count", Integer.toString(getBucketImageCount(bucketId)));
                    setWritableMap(image, "date", date);
                    setWritableMap(image, "cover", ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.parseLong(id)).toString());
                    setWritableMap(image, "name", bucketName);

                    map.put(bucketId, image);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        WritableArray output = Arguments.createArray();
        for (WritableMap image : map.values()) {
            output.pushMap(image);
        }

        promise.resolve(output);
    }

    private boolean shouldSetField(ReadableMap options, String name) {
        return options.hasKey(name) && options.getBoolean(name);
    }

    private void setWritableMap(WritableMap map, String key, String value) {
        if (value == null) {
            map.putNull(key);
        } else {
            map.putString(key, value);
        }
    }

    private void setColumn(String name, String columnName, ArrayList<String> projection, ArrayList<ReadableMap> columns) {
        projection.add(columnName);
        WritableMap column = Arguments.createMap();
        column.putString("name", name);
        column.putString("columnName", columnName);
        columns.add(column);
    }

    private int getBucketImageCount(String bucketId) {
        Cursor cursor = getReactApplicationContext().getContentResolver().query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media.BUCKET_ID + "=?",
            new String[]{bucketId},
            null
        );

        return ((cursor == null) || (cursor.moveToFirst() == false)) ? 0 : cursor.getCount();
    }
}
