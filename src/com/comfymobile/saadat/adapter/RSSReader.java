package com.comfymobile.saadat.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.activity.MenuActivity;
import com.comfymobile.saadat.activity.SourceListActivity;
import com.comfymobile.saadat.database.LocalDatabase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Author Grinch
 * Date: 12.07.13
 * Time: 21:18
 */
public class RSSReader extends AsyncTask<String,Void,Void> {

    public static final String TAG_CHANNEL = "channel";
    public static final String TAG_ITEM = "item";

    public static final String CHANNEL_TITLE = "title";
    public static final String CHANNEL_DESC = "description";
    public static final String CHANNEL_LINK = "link";
    public static final String ITEM_TXT = "txt";
    public static final String ITEM_TITLE = "title";
    public static final String ITEM_DESC = "description";
    public static final String ITEM_DATE = "pubDate";
    public static final String ITEM_URL = "link";

    LocalDatabase database;
    Context context;
    boolean fromLoading;
    ProgressDialog dialog;

    public RSSReader(Context context,boolean fromLoading){
        this.context = context;
        this.fromLoading = fromLoading;
        database = LocalDatabase.getInstance(context);
    }

    private  String getStringFromElement(Element element){
       if (element != null && element.getChildNodes() != null && element.getChildNodes().getLength() > 0)
           return  element.getFirstChild().getNodeValue();
       return  "";
    }

    @Override
    protected void onPreExecute() {


        if (!fromLoading){
            dialog = ProgressDialog.show(context,
                    context.getString(R.string.rss_dialog_title),
                    context.getString(R.string.rss_dialog_message));
            dialog.setCanceledOnTouchOutside(false);
        } else {
            Intent intent = new Intent(context, MenuActivity.class);
            context.startActivity(intent);
            View layout = View.inflate(context,R.layout.loading,null);
            TextView loadText = (TextView) layout.findViewById(R.id.loadText);
            loadText.setText("Загрузка - Новости...");
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            boolean isFirstConnection = true;
            for (int j = 0 ; j < params.length ; j++){
                URL url = new URL(params[j]);
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                if (connect.getResponseCode() == HttpURLConnection.HTTP_OK){
                    if (isFirstConnection){
                        database.clearNewsSource();
                        database.clearNews();
                        isFirstConnection = false;
                    }
                    InputStream is = connect.getInputStream();
                    DocumentBuilderFactory dbf = DocumentBuilderFactory
                            .newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document document = db.parse(is);
                    Element element = document.getDocumentElement();

                    NodeList channelList = element.getElementsByTagName(TAG_CHANNEL);


                    for (int i=0; i < channelList.getLength(); i++){
                        Element channel = (Element) channelList.item(i);


                        Element eTitle = (Element) channel.getElementsByTagName(CHANNEL_TITLE).item(0);
                        Element eDescription = (Element) channel.getElementsByTagName(CHANNEL_DESC).item(0);
                        Element eLink = (Element) channel.getElementsByTagName(CHANNEL_LINK).item(0);



                        String title = getStringFromElement(eTitle);
                        String description = getStringFromElement(eDescription);
                        String link = getStringFromElement(eLink);

                        database.updateNewsSource(j,title,description,link);

                        NodeList itemList = channel.getElementsByTagName(TAG_ITEM);
                        for (int k  =0; k < itemList.getLength(); k++){
                            Element item = (Element) itemList.item(k);

                            Element iTitle = (Element) item.getElementsByTagName(ITEM_TITLE).item(0);
                            Element iDescription = (Element) item.getElementsByTagName(ITEM_DESC).item(0);
                            if (iDescription == null)
                                iDescription = (Element) item.getElementsByTagName(ITEM_TXT).item(0);
                            Element iDate = (Element) item.getElementsByTagName(ITEM_DATE).item(0);
                            Element iLink = (Element) item.getElementsByTagName(ITEM_URL).item(0);


                            String ititle = getStringFromElement(iTitle);
                            String idescription = getStringFromElement(iDescription);
                            String ipubDate = getStringFromElement(iDate);
                            String ilink = getStringFromElement(iLink);

                            DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
                            Date nDate;
                            try {
                                nDate = formatter.parse(ipubDate);
                                SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM",Locale.getDefault());
                                ipubDate = newFormat.format(nDate);
                            } catch (ParseException e) {

                            }

                            database.updateNews(ititle, idescription.replaceAll("\\<[^>]*>",""), ipubDate, j,ilink);

                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (fromLoading){
            Intent intent = new Intent(context, MenuActivity.class);
            context.startActivity(intent);
            View layout = View.inflate(context,R.layout.loading,null);
            TextView loadText = (TextView) layout.findViewById(R.id.loadText);
            loadText.setText("Загрузка - Завершено");
            ((Activity) context).finish();
        }else {
            if (dialog != null){
                Intent intent = new Intent(context, SourceListActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
                dialog.dismiss();
            }
        }
    }
}
