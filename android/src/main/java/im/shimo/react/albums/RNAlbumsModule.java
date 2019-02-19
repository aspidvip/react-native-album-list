package im.shimo.react.albums;

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
                MediaStore.Images.ImageColumns.DATA,
                "count(" +  MediaStore.Images.ImageColumns.BUCKET_ID + ") as count"
        };

        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
        String BUCKET_ORDER_BY = "MAX(" + MediaStore.Images.ImageColumns.DATE_TAKEN + ") DESC";


        Cursor cursor = getReactApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                PROJECTION_BUCKET,
                BUCKET_GROUP_BY,
                null,
                BUCKET_ORDER_BY
        );

        WritableArray list = Arguments.createArray();
        if (cursor != null && cursor.moveToFirst()) {
            String bucket;
            String date;
            String data;
            String count;
            int bucketColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int dateColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);
            int dataColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.DATA);
            int countColumn = cursor.getColumnIndex("count");
            do {
                // Get the field values
                bucket = cursor.getString(bucketColumn);
                date = cursor.getString(dateColumn);
                data = cursor.getString(dataColumn);
                count = cursor.getString(countColumn);


                WritableMap image = Arguments.createMap();
                setWritableMap(image, "count", count);
                setWritableMap(image, "date", date);
                setWritableMap(image, "cover", "file://" + data);
                setWritableMap(image, "name", bucket);

                list.pushMap(image);
            } while (cursor.moveToNext());

            cursor.close();
        }

        promise.resolve(list);
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
}
