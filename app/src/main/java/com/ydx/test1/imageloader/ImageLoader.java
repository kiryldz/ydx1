package com.ydx.test1.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class ImageLoader {

    private List<String> mImageUrls = new ArrayList<>();

    @Nullable
    String getImageUrl(int index) {
        final List<String> imageUrls = getImageUrls();
        if (!imageUrls.isEmpty() && index < mImageUrls.size()) {
            return imageUrls.get(index % mImageUrls.size());
        } else {
            return null;
        }
    }

    @NonNull
    private List<String> getImageUrls() {
        try {
            final Calendar calendar = Calendar.getInstance();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"
                    , java.util.Locale.getDefault());
            final String formattedDate = dateFormat.format(calendar.getTime());
            final String stringUrl = "http://api-fotki.yandex.ru/api/podhistory/poddate;"
                    + formattedDate
                    + "T12:00:00Z/?limit=100";
            final URL url = new URL(stringUrl);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final InputStream stream = connection.getInputStream();
                final XmlPullParser parser = Xml.newPullParser();
                parser.setInput(stream, null);
                String imgUrl;
                while ((imgUrl = processEntry(parser)) != null) {
                    mImageUrls.add(imgUrl);
                }
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return mImageUrls;
    }

    @Nullable
    private String processEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.START_TAG
                    && "img".equals(parser.getName())) {
                for (int i = 1; i < parser.getAttributeCount(); i++) {
                    if ("size".equals(parser.getAttributeName(i))) {
                        if ("XXXL".equals(parser.getAttributeValue(i))) {
                            return parser.getAttributeValue(i-1);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    Bitmap loadBitmap(final String srcUrl) {
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
            byte[] bytes = buffer.toByteArray();
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
