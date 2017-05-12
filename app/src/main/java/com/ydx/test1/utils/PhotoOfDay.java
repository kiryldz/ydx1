package com.ydx.test1.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Xml;
import com.ydx.test1.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PhotoOfDay extends AsyncTask<Void , Void ,Void>{

    private String imageUrl;
    private Activity mContext;
    private Bitmap bitmapOfDay;

    public PhotoOfDay(Activity context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = df.format(new Date());
        imageUrl = getImageUrl("http://api-fotki.yandex.ru/api/podhistory/poddate;"
                + gmtTime + "/");
        bitmapOfDay = loadBitmap(imageUrl);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (imageUrl != null) {
            mContext.findViewById(R.id.mainLayout).setBackground(
                    new BitmapDrawable(mContext.getResources(), bitmapOfDay));
        }
    }

    private String getImageUrl(String stringUrl) {
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = connection.getInputStream();
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(stream, null);
                return processEntry(parser);
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;

}

    private String processEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String prefix = parser.getPrefix();
            if ("f".equals(prefix) && "img".equals(name)) {
                for (int i = 0; i<parser.getAttributeCount(); i++) {
                    if ("height".equals(parser.getAttributeName(i))) {
                        if (700 >= Integer.valueOf(parser.getAttributeValue(i))
                                && 500 <= Integer.valueOf(parser.getAttributeValue(i)) ) {
                            return parser.getAttributeValue(i+1);
                        }
                    }
                }
            }
        }
        return null;
    }

    private Bitmap loadBitmap(String srcUrl) {
        try {
            URL url = new URL(srcUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream is = urlConnection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte [] bitmap = buffer.toByteArray();
            return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
